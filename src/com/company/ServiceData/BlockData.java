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
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedList;

public class BlockData {

    private ObservableList<TransactionFX> newBlockTransactionsFX;
    private ObservableList<Transaction> newBlockTransactions;
    private LinkedList<Block> currentBlockChain = new LinkedList<>();
    private Block latestBlock;
    private String walletBalance;
    private boolean exit = false;
    private int miningPoints;
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

    public static BlockData getInstance() {
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
        walletBalance = getBalance(currentBlockChain, newBlockTransactions, WalletData.getInstance().getWallet().getPublicKey()).toString();
        return walletBalance;
    }

    public void verifyBlockChain(LinkedList<Block> currentBlockChain) throws GeneralSecurityException {
        for (Block block : currentBlockChain) {
            if (!block.isVerified(signing)) {
                throw new GeneralSecurityException("Block validation failed");
            }
            ArrayList<Transaction> transactions = block.getTransactionLedger();
//            transactions.sort(transactionComparator);
            for (Transaction transaction : transactions) {
                if (!transaction.isVerified(transaction, signing)) {
                    throw new GeneralSecurityException("Transaction validation failed");
                }
            }
        }
    }

    public void addTransaction(Transaction transaction, boolean blockReward) throws GeneralSecurityException {
        try {
            if (getBalance(currentBlockChain, newBlockTransactions, new DSAPublicKeyImpl(transaction.getFrom())) < transaction.getValue() && !blockReward) {
                throw new GeneralSecurityException("Not enough funds by sender to record transaction");
            } else {
                newBlockTransactions.add(transaction);
                newBlockTransactions.sort(transactionComparator);
                Connection connection = DriverManager.getConnection("jdbc:sqlite:C:\\Users\\spiro\\IdeaProjects\\e-coin\\db\\blockchain.db");

                PreparedStatement pstmt;
                pstmt = connection.prepareStatement("INSERT INTO TRANSACTIONS(\"FROM\", \"TO\", LEDGER_ID, VALUE, SIGNATURE, CREATED_ON) " +
                        " VALUES (?,?,?,?,?,?) ");
                pstmt.setBytes(1, transaction.getFrom());
                pstmt.setBytes(2, transaction.getTo());
                pstmt.setInt(3, transaction.getLedgerId());
                pstmt.setInt(4, transaction.getValue());
                pstmt.setBytes(5, transaction.getSignature());
                pstmt.setString(6, transaction.getTimeStamp());
                pstmt.executeUpdate();

                pstmt.close();
                connection.close();
            }
        } catch (SQLException e) {
            System.out.println("Problem with DB: " + e.getMessage());
            e.printStackTrace();
        }

    }

    private Integer getBalance(LinkedList<Block> blockChain, ObservableList<Transaction> currentLedger, PublicKey walletAddress) {
        Integer balance = 0;
        for (Block block : blockChain) {
            for (Transaction transaction : block.getTransactionLedger()) {
                if (Arrays.equals(transaction.getFrom(), walletAddress.getEncoded())) {
                    balance -= transaction.getValue();
                }
                if (Arrays.equals(transaction.getTo(), walletAddress.getEncoded())) {
                    balance += transaction.getValue();
                }
            }
        }
        for (Transaction transaction : currentLedger) {
            if (Arrays.equals(transaction.getFrom(), walletAddress.getEncoded())) {
                balance -= transaction.getValue();
            }
        }
        return balance;
    }

    public void loadBlockChain() {
        try {
            Connection connection = DriverManager.getConnection("jdbc:sqlite:C:\\Users\\spiro\\IdeaProjects\\e-coin\\db\\blockchain.db");
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
            Transaction transaction = new Transaction(new Wallet(), WalletData.getInstance().getWallet().getPublicKey().getEncoded(),
                    100, latestBlock.getLedgerId() + 1, signing);
            newBlockTransactions.clear();
            newBlockTransactions.addAll(transaction);
            newBlockTransactions.sort(transactionComparator);
            verifyBlockChain(currentBlockChain);
            resultSet.close();
            stmt.close();
            connection.close();
        } catch (SQLException | NoSuchAlgorithmException e) {
            System.out.println("Problem with DB: " + e.getMessage());
            e.printStackTrace();
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        }
    }

    private ArrayList<Transaction> loadTransactionLedger(Integer ledgerID) throws SQLException {
        ArrayList<Transaction> transactions = new ArrayList<>();
        try {
            Connection connection = DriverManager.getConnection("jdbc:sqlite:C:\\Users\\spiro\\IdeaProjects\\e-coin\\db\\blockchain.db");
            PreparedStatement stmt = connection.prepareStatement(" SELECT  * FROM TRANSACTIONS WHERE LEDGER_ID = ?");

            stmt.setInt(1, ledgerID);
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
                Transaction transaction = new Transaction(new Wallet(), WalletData.getInstance().getWallet().getPublicKey().getEncoded(), 100, ledgerID + 1, signing);
                transactions.add(transaction);
            }
            resultSet.close();
            stmt.close();
            connection.close();
        } catch (SQLException | NoSuchAlgorithmException | InvalidKeyException | SignatureException e) {
            e.printStackTrace();
        }
        return transactions;
    }

    public void mineBlock() {
        try {
            finalizeBlock(WalletData.getInstance().getWallet(), new Wallet());
            addBlock(latestBlock);
        } catch (SQLException | GeneralSecurityException e) {
            System.out.println("Problem with DB: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void finalizeBlock(Wallet minersWallet, Wallet blockRewardWallet) throws GeneralSecurityException, SQLException {
        latestBlock = new Block(BlockData.getInstance().currentBlockChain);
        latestBlock.setTransactionLedger(new ArrayList<>(newBlockTransactions));
        latestBlock.setCurrHash(latestBlock.getPrevHash());
        boolean rewardTransaction = true;
        for (Transaction trans : latestBlock.getTransactionLedger()) {
            if (latestBlock.getLedgerId() == 1) {
                continue;
            }
            if (trans.isVerified(trans, signing) && getBalance(currentBlockChain, FXCollections.observableArrayList(latestBlock.getTransactionLedger()), new DSAPublicKeyImpl(trans.getFrom())) >= 0 || rewardTransaction) {
                latestBlock.setCurrHash((Arrays.toString(latestBlock.getPrevHash()) + Arrays.toString(trans.getSignature())).getBytes());
                rewardTransaction = false;
            } else {
                throw new GeneralSecurityException("Block transactions validation failed");
            }
        }
        latestBlock.setTimeStamp(LocalDateTime.now().toString());
        latestBlock.setMinedBy(minersWallet.getPublicKey().getEncoded());
        latestBlock.setMiningPoints(miningPoints);
        signing.initSign(minersWallet.getPrivateKey());
        signing.update(latestBlock.toString().getBytes());
        latestBlock.setCurrHash(signing.sign());
        currentBlockChain.add(latestBlock);
        miningPoints = 0;
        //Reward transaction
        addTransaction(latestBlock.getTransactionLedger().get(0), true);
        Transaction transaction = new Transaction(blockRewardWallet, minersWallet.getPublicKey().getEncoded(), 100, latestBlock.getLedgerId() + 1, signing);
        newBlockTransactions.clear();
        newBlockTransactions.add(transaction);
    }

    private void addBlock(Block block) {
        try {
            Connection connection = DriverManager.getConnection("jdbc:sqlite:C:\\Users\\spiro\\IdeaProjects\\e-coin\\db\\blockchain.db");
            PreparedStatement pstmt;
            pstmt = connection.prepareStatement("INSERT INTO BLOCKCHAIN(PREVIOUS_HASH, CURRENT_HASH , LEDGER_ID, CREATED_ON, CREATED_BY,MINING_POINTS,LUCK ) " +
                    " VALUES (?,?,?,?,?,?,?) ");
            pstmt.setBytes(1, block.getPrevHash());
            pstmt.setBytes(2, block.getCurrHash());
            pstmt.setInt(3, block.getLedgerId());
            pstmt.setString(4, block.getTimeStamp());
            pstmt.setBytes(5, block.getMinedBy());
            pstmt.setInt(6, block.getMiningPoints());
            pstmt.setDouble(7, block.getLuck());
            pstmt.executeUpdate();
            pstmt.close();
            connection.close();
        } catch (SQLException e) {
            System.out.println("Problem with DB: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void addReceivedBlockChainToDB(LinkedList<Block> receivedBC) {
        try {
            Connection connection = DriverManager.getConnection("jdbc:sqlite:C:\\Users\\spiro\\IdeaProjects\\e-coin\\db\\blockchain.db");
            Statement clearDBStatement = connection.createStatement();
            clearDBStatement.executeUpdate(" DELETE FROM BLOCKCHAIN ");
            clearDBStatement.executeUpdate(" DELETE FROM TRANSACTIONS ");
            clearDBStatement.close();
            connection.close();
            for (Block block : receivedBC) {
                addBlock(block);
                boolean rewardTransaction = true;
                for (Transaction transaction : block.getTransactionLedger()) {
                    addTransaction(transaction, rewardTransaction);
                    rewardTransaction = false;
                }
            }
        } catch (SQLException | GeneralSecurityException e) {
            System.out.println("Problem with DB: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public LinkedList<Block> getBlockchainConsensus(LinkedList<Block> recievedBC) {
        try {
            //--------------todo: Implement consensus here and return the winning blockchain in objectOutput. -------------------------------
            //Verify the validity of the received blockchain.
            verifyBlockChain(recievedBC);
            //Check if we have received an identical blockchain.
            if (!Arrays.equals(recievedBC.getLast().getCurrHash(), getCurrentBlockChain().getLast().getCurrHash())) {
                //Check how old the blockchains are.
                long lastMinedLocalBlock = LocalDateTime.parse(getCurrentBlockChain().getLast().getTimeStamp()).toEpochSecond(ZoneOffset.UTC);
                long lastMinedRcvdBlock = LocalDateTime.parse(recievedBC.getLast().getTimeStamp()).toEpochSecond(ZoneOffset.UTC);
                //if both are old just do nothing
                if ((lastMinedLocalBlock + 65) < LocalDateTime.now().toEpochSecond(ZoneOffset.UTC) && (lastMinedRcvdBlock + 65) < LocalDateTime.now().toEpochSecond(ZoneOffset.UTC)) {
                    System.out.println("both are old check other peers");
                    //If your blockchain is old but the received one is new use the received one
                } else if ((lastMinedLocalBlock + 65) < LocalDateTime.now().toEpochSecond(ZoneOffset.UTC) && (lastMinedRcvdBlock + 65) >= LocalDateTime.now().toEpochSecond(ZoneOffset.UTC)) {
                    //we reset the mining points since we weren't contributing until now.
                    setMiningPoints(0);
                    setCurrentBlockChain(recievedBC);
                    addReceivedBlockChainToDB(recievedBC);
                    System.out.println("received blockchain won!, local BC was old");
                    //If received one is old but local is new send ours to them
                } else if ((lastMinedLocalBlock + 65) > LocalDateTime.now().toEpochSecond(ZoneOffset.UTC) && (lastMinedRcvdBlock + 65) < LocalDateTime.now().toEpochSecond(ZoneOffset.UTC)) {
                    //we reset the mining points since we weren't contributing until now.
                    return getCurrentBlockChain();
                } else {
                    //Compare timestamps to see which one is older
                    long initRcvBlockTime = LocalDateTime.parse(recievedBC.getFirst().getTimeStamp()).toEpochSecond(ZoneOffset.UTC);
                    long initLocalBlockTIme = LocalDateTime.parse(getCurrentBlockChain().getFirst().getTimeStamp()).toEpochSecond(ZoneOffset.UTC);
                    if (initRcvBlockTime < initLocalBlockTIme) {
                        //we reset the mining points since we weren't contributing until now.
                        setMiningPoints(0);
                        setCurrentBlockChain(recievedBC);
                        addReceivedBlockChainToDB(recievedBC);
                        System.out.println("PeerClient blockchain won!, PeerServer's BC was old");
                    } else if (initLocalBlockTIme < initRcvBlockTime) {
                        return getCurrentBlockChain();
                    } else {
                        //check if both blockchains have the same prevHashes to confirm they are both
                        //contending to mine the last block
                        if (recievedBC.equals(getCurrentBlockChain())) {
                            //if they are the same compare the mining points and luck in case of equal mining points
                            //of last block to see who wins
                            //transfer all transactions to the winning block and add them in DB.
                            if (recievedBC.getLast().getMiningPoints() > getCurrentBlockChain().getLast().getMiningPoints() ||
                                    recievedBC.getLast().getMiningPoints().equals(getCurrentBlockChain().getLast().getMiningPoints()) &&
                                            recievedBC.getLast().getLuck() > getCurrentBlockChain().getLast().getLuck()) {
                                //remove the reward transaction from the losing block and transfer the transactions to the winning block
                                getCurrentBlockChain().getLast().getTransactionLedger().remove(0);
                                for (Transaction transaction : getCurrentBlockChain().getLast().getTransactionLedger()) {
                                    if (!recievedBC.getLast().getTransactionLedger().contains(transaction)) {
                                        recievedBC.getLast().getTransactionLedger().add(transaction);
                                    }
                                }
                                recievedBC.getLast().getTransactionLedger().sort(transactionComparator);
                                //we are returning the mining points since our local block lost.
                                setMiningPoints(BlockData.getInstance().getMiningPoints() + getCurrentBlockChain().getLast().getMiningPoints());
                                setCurrentBlockChain(recievedBC);
                                addReceivedBlockChainToDB(recievedBC);
                                System.out.println("Received blockchain won!");
                            } else {
                                //remove the reward transaction from the losing block and transfer the transactions to our winning block
                                recievedBC.getLast().getTransactionLedger().remove(0);
                                for (Transaction transaction : recievedBC.getLast().getTransactionLedger()) {
                                    if (!getCurrentBlockChain().getLast().getTransactionLedger().contains(transaction)) {
                                        getCurrentBlockChain().getLast().getTransactionLedger().add(transaction);
                                        addTransaction(transaction, false);
                                    }
                                }
                                getCurrentBlockChain().getLast().getTransactionLedger().sort(transactionComparator);
                                return getCurrentBlockChain();
                            }
                        } else {
                            System.out.println("blockchains mismatch, this shouldn't happen");
                        }
                    }
                }
                //if only the transaction ledgers are different then combine them.
            } else if (!recievedBC.getLast().getTransactionLedger().equals(getCurrentBlockChain().getLast().getTransactionLedger())) {
                for (Transaction transaction : recievedBC.getLast().getTransactionLedger()) {
                    if (!getCurrentBlockChain().getLast().getTransactionLedger().contains(transaction)) {
                        getCurrentBlockChain().getLast().getTransactionLedger().add(transaction);
                        addTransaction(transaction, false);
                    }
                }
                getCurrentBlockChain().getLast().getTransactionLedger().sort(transactionComparator);
                for (Transaction transaction : getCurrentBlockChain().getLast().getTransactionLedger()) {
                    if (!recievedBC.getLast().getTransactionLedger().contains(transaction)) {
                        recievedBC.getLast().getTransactionLedger().add(transaction);
                    }
                }
                recievedBC.getLast().getTransactionLedger().sort(transactionComparator);
                System.out.println("Transaction ledgers updated");
                return recievedBC;
            } else {
                System.out.println("blockchains are identical");
            }
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        }
        return recievedBC;
    }

    public LinkedList<Block> getCurrentBlockChain() {
        return currentBlockChain;
    }

    public void setCurrentBlockChain(LinkedList<Block> currentBlockChain) {
        this.currentBlockChain = currentBlockChain;
    }

    public int getMiningPoints() {
        return miningPoints;
    }

    public void setMiningPoints(int miningPoints) {
        this.miningPoints = miningPoints;
    }

    Comparator<Transaction> transactionComparator = Comparator.comparing(Transaction::getTimeStamp);

    public boolean isExit() {
        return exit;
    }

    public void setExit(boolean exit) {
        this.exit = exit;
    }
}
