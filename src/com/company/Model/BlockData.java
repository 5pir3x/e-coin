package com.company.Model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import sun.security.provider.DSAPublicKeyImpl;

import java.security.GeneralSecurityException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.Signature;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;

public class BlockData  {


    private ObservableList<Block> currentBlockChainFX;
    private LinkedList<Block> currentBlockChain = new LinkedList<>();
    private Block latestBlock;
    private Wallet blockRewardWallet  = new Wallet(2048,100);
    //helper class.
    private Signature signing = Signature.getInstance("SHA256withDSA");


    //singleton class
    private static BlockData instance;
    static {
        try {
            instance = new BlockData();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    public BlockData() throws NoSuchAlgorithmException {
        currentBlockChainFX = FXCollections.observableArrayList();
    }

    public static BlockData getInstance(){
        return instance;
    }

    private void verifyBlockChain (LinkedList<Block> currentBlockChain) throws GeneralSecurityException {
        for (Block block : currentBlockChain) {
            for (Transaction transaction : block.getTransactionLedger()) {
                if (!transaction.isVerified(transaction, new DSAPublicKeyImpl(transaction.getFrom()))) {
                    throw new GeneralSecurityException("Blockchain validation failed");
                }
            }
        }
    }

    public void addTransaction(Transaction transaction) throws GeneralSecurityException {
        latestBlock.getTransactionLedger().add(transaction);
        if (getBalance(currentBlockChain,latestBlock.getTransactionLedger(),new DSAPublicKeyImpl(transaction.getFrom())) < 0) {
            latestBlock.getTransactionLedger().remove(transaction);
            throw new GeneralSecurityException("Not enough funds to record transaction");
        }
    }
    private void addBlockRewardTransaction(Transaction transaction) throws GeneralSecurityException {
        latestBlock.getTransactionLedger().add(transaction);
    }

    public LinkedList<Block> finalizeBlock(Wallet minersWallet) throws GeneralSecurityException {

        for (Transaction transaction : latestBlock.getTransactionLedger()) {
            //todo:reenable these checks when add transaction is ready
//            if (transaction.isVerified(transaction, new DSAPublicKeyImpl(transaction.getFrom())) && getBalance(currentBlockChain,latestBlock.getTransactionLedger(),new DSAPublicKeyImpl(transaction.getFrom())) >= 0) {
                latestBlock.setCurrHash((Arrays.toString(latestBlock.getPrevHash()) + Arrays.toString(transaction.getSignature())).getBytes());
//            } else {
//                throw new GeneralSecurityException("Block transactions validation failed");
//            }
        }
        //Reward transaction
        Transaction transaction = new Transaction(blockRewardWallet.getPublicKey().getEncoded(),minersWallet.getPublicKey().getEncoded(),100,blockRewardWallet.getPrivateKey());
        addBlockRewardTransaction(transaction);
        latestBlock.setCurrHash((Arrays.toString(latestBlock.getCurrHash()) + Arrays.toString(transaction.getSignature())).getBytes());
        signing.initSign(minersWallet.getPrivateKey());
        signing.update(latestBlock.getCurrHash());
        latestBlock.setCurrHash( signing.sign());
        latestBlock.setTimeStamp(LocalDateTime.now().toString());
        latestBlock.setMinedBy(minersWallet.getPublicKey().getEncoded());
        currentBlockChain.add(latestBlock);
        return currentBlockChain;
    }
    public Integer getBalance(LinkedList<Block> blockChain, PublicKey walletAddress) {
        Integer balance = 0;
        for (Block block : blockChain) {
            for ( Transaction transaction : block.getTransactionLedger()) {
                if (Arrays.equals(transaction.getFrom(), walletAddress.getEncoded())) {
                    balance -= transaction.getValue();
                } else if (Arrays.equals(transaction.getTo(), walletAddress.getEncoded())) {
                    balance += transaction.getValue();
                }
            }
        }
        return balance;
    }

    public Integer getBalance(LinkedList<Block> blockChain, ArrayList<Transaction> currentLedger, PublicKey walletAddress) {
        Integer balance = 0;
        for (Block block : blockChain) {
            for (Transaction transaction : block.getTransactionLedger()) {
                if (Arrays.equals(transaction.getFrom(), walletAddress.getEncoded())) {
                    balance -= transaction.getValue();
                } else if (Arrays.equals(transaction.getTo(), walletAddress.getEncoded())) {
                    balance += transaction.getValue();
                }
            }
        }
        for ( Transaction transaction : currentLedger) {
            if (Arrays.equals(transaction.getFrom(), walletAddress.getEncoded())) {
                balance -= transaction.getValue();
            } else if (Arrays.equals(transaction.getTo(), walletAddress.getEncoded())) {
                balance += transaction.getValue();
            }
        }

        return balance;
    }
    public ObservableList<Transaction> getTransactionLedger() {
        return FXCollections.observableArrayList(currentBlockChainFX.get(currentBlockChainFX.size()-1).getTransactionLedger());
    }

    public void loadBlockChain() {
        try {
            Connection connection = DriverManager.getConnection("jdbc:sqlite:C:\\Users\\spiro\\IdeaProjects\\e-coin\\db\\ecointest2.db");
            Statement stmt = connection.createStatement();
            ResultSet resultSet = stmt.executeQuery(" SELECT  * FROM BLOCKCHAIN ");
            while (resultSet.next()) {
                ArrayList<Transaction> transactionLedger = loadTransactionLedger(resultSet.getInt("LEDGER_ID"));
                currentBlockChainFX.add(new Block(
                        resultSet.getBytes("PREVIOUS_HASH"),
                        resultSet.getBytes("CURRENT_HASH"),
                        resultSet.getString("CREATED_ON"),
                        resultSet.getBytes("CREATED_BY"),
                        transactionLedger
                ));
            }
            currentBlockChain.addAll(currentBlockChainFX);

            latestBlock = new Block(currentBlockChain);

            verifyBlockChain(currentBlockChain);
            resultSet.close();
            stmt.close();
            connection.close();
        }
        catch (SQLException | NoSuchAlgorithmException e) {
            System.out.println("Problem with DB: " + e.getMessage());
            e.printStackTrace();
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        }
    }
    private ArrayList<Transaction> loadTransactionLedger(Integer ledgerID) throws SQLException {
        ArrayList<Transaction> transactions = new ArrayList<>();
        try {
            Connection connection = DriverManager.getConnection("jdbc:sqlite:C:\\Users\\spiro\\IdeaProjects\\e-coin\\db\\ecointest2.db");
            PreparedStatement stmt = connection.prepareStatement(" SELECT  * FROM TRANSACTIONS WHERE LEDGER_ID = ?");

            stmt.setInt(1,ledgerID);
            ResultSet resultSet = stmt.executeQuery();
            while (resultSet.next()) {
                transactions.add(new Transaction(
                        resultSet.getBytes("FROM"),
                        resultSet.getBytes("TO"),
                        resultSet.getInt("VALUE"),
                        resultSet.getBytes("SIGNATURE"),
                        resultSet.getInt("LEDGER_ID")
                ));
            }
            resultSet.close();
            stmt.close();
            connection.close();
        } catch (SQLException | NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return transactions;
    }

    //todo:Outstanding for impl
    public void mineBlock() {

        try {
            Connection connection = DriverManager.getConnection("jdbc:sqlite:C:\\Users\\spiro\\IdeaProjects\\e-coin\\db\\ecointest2.db");
            Statement stmt = connection.createStatement();

            latestBlock.setTransactionLedger(loadTransactionLedger(latestBlock.getLedgerId()));
            finalizeBlock(WalletData.getInstance().getWallet());
            PreparedStatement pstmt;

             pstmt = connection.prepareStatement("INSERT INTO BLOCKCHAIN(PREVIOUS_HASH, CURRENT_HASH , LEDGER_ID, CREATED_ON, CREATED_BY) " +
                    " VALUES (?,?,?,?,?) ");
            pstmt.setBytes(1,latestBlock.getPrevHash());
            pstmt.setBytes(2,latestBlock.getCurrHash());
            pstmt.setInt(3,latestBlock.getLedgerId());
            pstmt.setString(4,latestBlock.getTimeStamp());
            pstmt.setBytes(5,WalletData.getInstance().getWallet().getPublicKey().getEncoded());
            pstmt.executeUpdate();
            stmt.close();
            connection.close();
            latestBlock = new Block(currentBlockChain);
        }
        catch (SQLException | GeneralSecurityException e) {
            System.out.println("Problem with DB: " + e.getMessage());
            e.printStackTrace();
        }

    }

    public LinkedList<Block> getCurrentBlockChain() { return currentBlockChain; }
    public void setCurrentBlockChain(LinkedList<Block> currentBlockChain) { this.currentBlockChain = currentBlockChain; }

    public Block getLatestBlock() { return latestBlock; }
    public void setLatestBlock(Block latestBlock) { this.latestBlock = latestBlock; }
}
