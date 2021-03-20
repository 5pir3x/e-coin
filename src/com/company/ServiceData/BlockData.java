package com.company.ServiceData;

import com.company.Model.Block;
import com.company.Model.Transaction;
import com.company.Model.TransactionFX;
import com.company.Model.Wallet;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import sun.security.provider.DSAPublicKeyImpl;

import java.security.*;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedList;

public class BlockData  {

    private ObservableList<TransactionFX> newBlockTransactionsFX;
    private ObservableList<Transaction> newBlockTransactions;
    private LinkedList<Block> currentBlockChain = new LinkedList<>();
    private Block latestBlock;
    //todo: Make this wallet create itself for each new Block.

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
        newBlockTransactions.sort(transactionComparator);
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
            ArrayList<Transaction> transactions = block.getTransactionLedger();
//            transactions.sort(transactionComparator);
            for (Transaction transaction : transactions) {
                if (!transaction.isVerified(transaction, new DSAPublicKeyImpl(transaction.getFrom()))) {
                    throw new GeneralSecurityException("Blockchain validation failed");
                }
            }
        }
    }

    public void addTransaction(Transaction transaction,boolean blockReward) throws GeneralSecurityException {
        try {
            if (getBalance(currentBlockChain, newBlockTransactions,transaction,new DSAPublicKeyImpl(transaction.getFrom())) < 0 && !blockReward) {
                throw new GeneralSecurityException("Not enough funds to record transaction");
            } else {
                newBlockTransactions.add(transaction);
                newBlockTransactions.sort(transactionComparator);
                Connection connection = DriverManager.getConnection("jdbc:sqlite:C:\\Users\\spiro\\IdeaProjects\\e-coin\\db\\ecointest2.db");

                PreparedStatement pstmt;
                pstmt = connection.prepareStatement("INSERT INTO TRANSACTIONS(\"FROM\", \"TO\", LEDGER_ID, VALUE, SIGNATURE, CREATED_ON) " +
                    " VALUES (?,?,?,?,?,?) ");
                pstmt.setBytes(1,transaction.getFrom());
                pstmt.setBytes(2,transaction.getTo());
                pstmt.setInt(3,transaction.getLedgerId());
                pstmt.setInt(4,transaction.getValue());
                pstmt.setBytes(5,transaction.getSignature());
                pstmt.setString(6,transaction.getTimeStamp());
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
        newBlockTransactions.add(transaction);
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
            }
//            else if (Arrays.equals(transaction.getTo(), walletAddress.getEncoded())) {
//                balance += transaction.getValue();
//            }
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
                        resultSet.getInt("MINING_POINTS"),
                        resultSet.getDouble("LUCK"),
                        transactionLedger
                ));
            }

            latestBlock = currentBlockChain.getLast();
            Transaction transaction = new Transaction(new Wallet(2048,100),WalletData.getInstance().getWallet().getPublicKey().getEncoded(),100,latestBlock.getLedgerId() + 1);
//            transactions.add(transaction);
            newBlockTransactions.addAll(transaction);
            newBlockTransactions.sort(transactionComparator);
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
                        resultSet.getInt("LEDGER_ID"),
                        resultSet.getString("CREATED_ON")
                ));
            }
            if (transactions.isEmpty()) {
                    Transaction transaction = new Transaction(new Wallet(2048,100),WalletData.getInstance().getWallet().getPublicKey().getEncoded(),100,ledgerID + 1);
                    transactions.add(transaction);
            }
            resultSet.close();
            stmt.close();
            connection.close();
        } catch (SQLException | NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (SignatureException e) {
            e.printStackTrace();
        }
        return transactions;
    }

    //todo:Outstanding for impl
    public void mineBlock() {

        try {
            finalizeBlock(WalletData.getInstance().getWallet(), new Wallet(2048,100));

            Connection connection = DriverManager.getConnection("jdbc:sqlite:C:\\Users\\spiro\\IdeaProjects\\e-coin\\db\\ecointest2.db");
            PreparedStatement pstmt;
            pstmt = connection.prepareStatement("INSERT INTO BLOCKCHAIN(PREVIOUS_HASH, CURRENT_HASH , LEDGER_ID, CREATED_ON, CREATED_BY,MINING_POINTS,LUCK ) " +
                    " VALUES (?,?,?,?,?,?,?) ");
            pstmt.setBytes(1,latestBlock.getPrevHash());
            pstmt.setBytes(2,latestBlock.getCurrHash());
            pstmt.setInt(3,latestBlock.getLedgerId());
            pstmt.setString(4,latestBlock.getTimeStamp());
            pstmt.setBytes(5,WalletData.getInstance().getWallet().getPublicKey().getEncoded());
            pstmt.setInt(6,latestBlock.getMiningPoints());
            pstmt.setDouble(7,latestBlock.getLuck());
            pstmt.executeUpdate();
            pstmt.close();
            connection.close();
        } catch (SQLException | GeneralSecurityException e) {
            System.out.println("Problem with DB: " + e.getMessage());
            e.printStackTrace();
        }

    }

    public void finalizeBlock(Wallet minersWallet,Wallet blockRewardWallet) throws GeneralSecurityException, SQLException {

        latestBlock = new Block(BlockData.getInstance().currentBlockChain);
        latestBlock.setTransactionLedger(new ArrayList<>(newBlockTransactions));
        latestBlock.setCurrHash(latestBlock.getPrevHash());
        boolean rewardTransaction = true;
        for (Transaction trans : latestBlock.getTransactionLedger()) {
            //todo:reenable these checks when add trans is ready
            if (trans.isVerified(trans, new DSAPublicKeyImpl(trans.getFrom())) && getBalance(currentBlockChain,FXCollections.observableArrayList(latestBlock.getTransactionLedger()),new DSAPublicKeyImpl(trans.getFrom()) ) >= 0 || rewardTransaction) {
                latestBlock.setCurrHash((Arrays.toString(latestBlock.getPrevHash()) + Arrays.toString(trans.getSignature())).getBytes());
                rewardTransaction = false;
            } else {
                throw new GeneralSecurityException("Block transactions validation failed");
            }
        }
        signing.initSign(minersWallet.getPrivateKey());
        signing.update(latestBlock.getCurrHash());
        latestBlock.setCurrHash(signing.sign());
        latestBlock.setTimeStamp(LocalDateTime.now().toString());
        latestBlock.setMinedBy(minersWallet.getPublicKey().getEncoded());
        currentBlockChain.add(latestBlock);
        //Reward transaction
        addTransaction(latestBlock.getTransactionLedger().get(0),true);
        Transaction transaction = new Transaction(blockRewardWallet,minersWallet.getPublicKey().getEncoded(),100,latestBlock.getLedgerId() + 1);
//        Connection connection = DriverManager.getConnection("jdbc:sqlite:C:\\Users\\spiro\\IdeaProjects\\e-coin\\db\\ecointest2.db");
//        try {
//            PreparedStatement pstmt = connection.prepareStatement(" INSERT INTO TRANSACTIONS(\"FROM\", \"TO\", LEDGER_ID, VALUE, SIGNATURE, CREATED_ON) " +
//                    " VALUES (?,?,?,?,?,?) ");
//            pstmt.setBytes(1, transaction.getFrom());
//            pstmt.setBytes(2, transaction.getTo());
//            pstmt.setInt(3, transaction.getLedgerId());
//            pstmt.setInt(4,100);
//            pstmt.setBytes(5,transaction.getSignature());
//            pstmt.setString(6,transaction.getTimeStamp());
//            pstmt.executeUpdate();
//            newBlockTransactions.clear();
//            newBlockTransactions.add(transaction);
//            pstmt.close();
//            connection.close();
//        } catch (SQLException e) {
//            System.out.println("Problem with DB: " + e.getMessage());
//            e.printStackTrace();
//        }

        newBlockTransactions.clear();
        newBlockTransactions.add(transaction);
//        addBlockRewardTransaction(transaction);

    }
    public LinkedList<Block> getCurrentBlockChain() { return currentBlockChain; }
    public void setCurrentBlockChain(LinkedList<Block> currentBlockChain) { this.currentBlockChain = currentBlockChain; }

    public Block getLatestBlock() { return latestBlock; }
    public void setLatestBlock(Block latestBlock) { this.latestBlock = latestBlock; }
    Comparator<Transaction> transactionComparator = new Comparator<Transaction>() {
        @Override
        public int compare(Transaction t1, Transaction t2) {
            return t1.getTimeStamp().compareTo(t2.getTimeStamp());
        }

    };
}
