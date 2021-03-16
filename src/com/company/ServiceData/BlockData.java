package com.company.ServiceData;

import com.company.Model.Block;
import com.company.Model.Transaction;
import com.company.Model.TransactionFX;
import com.company.Model.Wallet;
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

    private ObservableList<TransactionFX> newBlockTransactionsFX;
    private ObservableList<Transaction> newBlockTransactions;
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
        newBlockTransactions = FXCollections.observableArrayList();
        newBlockTransactionsFX = FXCollections.observableArrayList();
    }

    public static BlockData getInstance(){
        return instance;
    }

    public ObservableList<TransactionFX> getTransactionLedgerFX() {
        newBlockTransactionsFX.clear();
        for (Transaction transaction : newBlockTransactions) {
            newBlockTransactionsFX.add(new TransactionFX(transaction));
        }
        return FXCollections.observableArrayList(newBlockTransactionsFX);
    }
    public String getWalletBallanceFX() {
      return getBalance(currentBlockChain, newBlockTransactions, WalletData.getInstance().getWallet().getPublicKey()).toString();
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
        try {
            if (getBalance(currentBlockChain, newBlockTransactions,transaction,new DSAPublicKeyImpl(transaction.getFrom())) < 0) {
                throw new GeneralSecurityException("Not enough funds to record transaction");
            } else {
                newBlockTransactions.add(transaction);
                Connection connection = DriverManager.getConnection("jdbc:sqlite:C:\\Users\\spiro\\IdeaProjects\\e-coin\\db\\ecointest2.db");

                PreparedStatement pstmt;
                pstmt = connection.prepareStatement("INSERT INTO TRANSACTIONS(\"FROM\", \"TO\", LEDGER_ID, VALUE, SIGNATURE) " +
                    " VALUES (?,?,?,?,?) ");
                pstmt.setBytes(1,transaction.getFrom());
                pstmt.setBytes(2,transaction.getTo());
                pstmt.setInt(3,transaction.getLedgerId());
                pstmt.setInt(4,transaction.getValue());
                pstmt.setBytes(5,transaction.getSignature());
                pstmt.executeUpdate();

                pstmt.close();
                connection.close();
            }
        } catch (SQLException e) {
            System.out.println("Problem with DB: " + e.getMessage());
            e.printStackTrace();
        }

    }
    private void addBlockRewardTransaction(Transaction transaction) throws GeneralSecurityException {
        latestBlock.getTransactionLedger().add(transaction);
    }


    public Integer getBalance(LinkedList<Block> blockChain, ObservableList<Transaction> currentLedger, PublicKey walletAddress) {
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

    public Integer getBalance(LinkedList<Block> blockChain, ObservableList<Transaction> currentLedger,Transaction currentTransaction, PublicKey walletAddress) {
        Integer balance = -currentTransaction.getValue();
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

    public void loadBlockChain() {
        try {
            Connection connection = DriverManager.getConnection("jdbc:sqlite:C:\\Users\\spiro\\IdeaProjects\\e-coin\\db\\ecointest2.db");
            Statement stmt = connection.createStatement();
            ResultSet resultSet = stmt.executeQuery(" SELECT * FROM BLOCKCHAIN ");
            ArrayList<Transaction> transactionLedger = new ArrayList<>();
            while (resultSet.next()) {
                transactionLedger = loadTransactionLedger(resultSet.getInt("LEDGER_ID"));
                currentBlockChain.add(new Block(
                        resultSet.getBytes("PREVIOUS_HASH"),
                        resultSet.getBytes("CURRENT_HASH"),
                        resultSet.getString("CREATED_ON"),
                        resultSet.getBytes("CREATED_BY"),
                        resultSet.getInt("LEDGER_ID"),
                        transactionLedger
                ));
            }

            latestBlock = currentBlockChain.getLast();
            newBlockTransactions.addAll(loadTransactionLedger(latestBlock.getLedgerId() + 1));
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
            newBlockTransactions.clear();
//            newBlockTransactionsFX.addAll(latestBlock.getTransactionLedger());
            pstmt.close();
            connection.close();
        } catch (SQLException | GeneralSecurityException e) {
            System.out.println("Problem with DB: " + e.getMessage());
            e.printStackTrace();
        }

    }

    public LinkedList<Block> finalizeBlock(Wallet minersWallet) throws GeneralSecurityException, SQLException {
        //Reward transaction
        Transaction transaction = new Transaction(blockRewardWallet,minersWallet.getPublicKey().getEncoded(),100,latestBlock.getLedgerId());
        Connection connection = DriverManager.getConnection("jdbc:sqlite:C:\\Users\\spiro\\IdeaProjects\\e-coin\\db\\ecointest2.db");
        try {
            PreparedStatement pstmt = connection.prepareStatement(" INSERT INTO TRANSACTIONS(\"FROM\", \"TO\", LEDGER_ID, VALUE, SIGNATURE) " +
                    " VALUES (?,?,?,?,?) ");
            pstmt.setBytes(1, transaction.getFrom());
            pstmt.setBytes(2, transaction.getTo());
            pstmt.setInt(3, transaction.getLedgerId());
            pstmt.setInt(4,100);
            pstmt.setBytes(5,transaction.getSignature());
            pstmt.executeUpdate();
            pstmt.close();
            connection.close();
        } catch (SQLException e) {
            System.out.println("Problem with DB: " + e.getMessage());
            e.printStackTrace();
        }
        addBlockRewardTransaction(transaction);
        latestBlock = new Block(BlockData.getInstance().currentBlockChain);
        latestBlock.setTransactionLedger(loadTransactionLedger(latestBlock.getLedgerId()));
        latestBlock.setCurrHash(latestBlock.getPrevHash());
        for (Transaction trans : latestBlock.getTransactionLedger()) {
            //todo:reenable these checks when add trans is ready
//            if (trans.isVerified(trans, new DSAPublicKeyImpl(trans.getFrom())) && getBalance(currentBlockChain,latestBlock.getTransactionLedger(),new DSAPublicKeyImpl(trans.getFrom())) >= 0) {
            latestBlock.setCurrHash((Arrays.toString(latestBlock.getPrevHash()) + Arrays.toString(trans.getSignature())).getBytes());
//            } else {
//                throw new GeneralSecurityException("Block transactions validation failed");
//            }
        }
        latestBlock.setCurrHash((Arrays.toString(latestBlock.getCurrHash()) + Arrays.toString(transaction.getSignature())).getBytes());
        signing.initSign(minersWallet.getPrivateKey());
        signing.update(latestBlock.getCurrHash());
        latestBlock.setCurrHash( signing.sign());
        latestBlock.setTimeStamp(LocalDateTime.now().toString());
        latestBlock.setMinedBy(minersWallet.getPublicKey().getEncoded());
        currentBlockChain.add(latestBlock);
        return currentBlockChain;
    }
    public LinkedList<Block> getCurrentBlockChain() { return currentBlockChain; }
    public void setCurrentBlockChain(LinkedList<Block> currentBlockChain) { this.currentBlockChain = currentBlockChain; }

    public Block getLatestBlock() { return latestBlock; }
    public void setLatestBlock(Block latestBlock) { this.latestBlock = latestBlock; }
}
