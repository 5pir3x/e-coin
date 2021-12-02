package com.company;

import com.company.Model.Block;
import com.company.Model.Transaction;
import com.company.Model.Wallet;
import com.company.ServiceData.BlockchainData;
import com.company.ServiceData.WalletData;
import com.company.Threads.MiningThread;
import com.company.Threads.PeerClient;
import com.company.Threads.PeerServer;
import com.company.Threads.UI;
import javafx.application.Application;
import javafx.stage.Stage;

import java.security.*;
import java.sql.*;
import java.time.LocalDateTime;

public class ECoin extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        new UI().start(primaryStage);
        new PeerClient().start();
        new PeerServer(6000).start();
        new MiningThread().start();
    }

    @Override
    public void init() {
        try {
            //This creates your wallet if there is none and gives you a KeyPair.
            //We will create it in separate db for better security and ease of portability.
            Connection walletConnection = DriverManager
                    .getConnection("jdbc:sqlite:C:\\Users\\spiro\\IdeaProjects\\e-coin\\db\\wallet.db");
            Statement walletStatment = walletConnection.createStatement();
            walletStatment.executeUpdate("CREATE TABLE IF NOT EXISTS WALLET ( " +
                    " PRIVATE_KEY BLOB NOT NULL UNIQUE, " +
                    " PUBLIC_KEY BLOB NOT NULL UNIQUE, " +
                    " PRIMARY KEY (PRIVATE_KEY, PUBLIC_KEY)" +
                    ") "
            );
            ResultSet resultSet = walletStatment.executeQuery(" SELECT * FROM WALLET ");
            if (!resultSet.next()) {
                Wallet newWallet = new Wallet();
                byte[] pubBlob = newWallet.getPublicKey().getEncoded();
                byte[] prvBlob = newWallet.getPrivateKey().getEncoded();
                PreparedStatement pstmt = walletConnection
                        .prepareStatement("INSERT INTO WALLET(PRIVATE_KEY, PUBLIC_KEY) " +
                        " VALUES (?,?) ");
                pstmt.setBytes(1, prvBlob);
                pstmt.setBytes(2, pubBlob);
                pstmt.executeUpdate();
            }
            resultSet.close();
            walletStatment.close();
            walletConnection.close();
            WalletData.getInstance().loadWallet();

//          This will create the db tables with columns for the Blockchain.
            Connection blockchainConnection = DriverManager
                    .getConnection("jdbc:sqlite:C:\\Users\\spiro\\IdeaProjects\\e-coin\\db\\blockchain.db");
            Statement blockchainStmt = blockchainConnection.createStatement();
            blockchainStmt.executeUpdate("CREATE TABLE IF NOT EXISTS BLOCKCHAIN ( " +
                    " ID INTEGER NOT NULL UNIQUE, " +
                    " PREVIOUS_HASH BLOB UNIQUE, " +
                    " CURRENT_HASH BLOB UNIQUE, " +
                    " LEDGER_ID INTEGER NOT NULL UNIQUE, " +
                    " CREATED_ON  TEXT, " +
                    " CREATED_BY  BLOB, " +
                    " MINING_POINTS  TEXT, " +
                    " LUCK  NUMERIC, " +
                    " PRIMARY KEY( ID AUTOINCREMENT) " +
                    ")"
            );
            ResultSet resultSetBlockchain = blockchainStmt.executeQuery(" SELECT * FROM BLOCKCHAIN ");
            Transaction initBlockRewardTransaction = null;
            if (!resultSetBlockchain.next()) {
                Block firstBlock = new Block();
                firstBlock.setMinedBy(WalletData.getInstance().getWallet().getPublicKey().getEncoded());
                firstBlock.setTimeStamp(LocalDateTime.now().toString());
                //helper class.
                Signature signing = Signature.getInstance("SHA256withDSA");
                signing.initSign(WalletData.getInstance().getWallet().getPrivateKey());
                signing.update(firstBlock.toString().getBytes());
                firstBlock.setCurrHash(signing.sign());
                PreparedStatement pstmt = blockchainConnection
                        .prepareStatement("INSERT INTO BLOCKCHAIN(PREVIOUS_HASH, CURRENT_HASH , LEDGER_ID," +
                                " CREATED_ON, CREATED_BY,MINING_POINTS,LUCK ) " +
                        " VALUES (?,?,?,?,?,?,?) ");
                pstmt.setBytes(1, firstBlock.getPrevHash());
                pstmt.setBytes(2, firstBlock.getCurrHash());
                pstmt.setInt(3, firstBlock.getLedgerId());
                pstmt.setString(4, firstBlock.getTimeStamp());
                pstmt.setBytes(5, WalletData.getInstance().getWallet().getPublicKey().getEncoded());
                pstmt.setInt(6, firstBlock.getMiningPoints());
                pstmt.setDouble(7, firstBlock.getLuck());
                pstmt.executeUpdate();
                Signature transSignature = Signature.getInstance("SHA256withDSA");
                initBlockRewardTransaction = new Transaction(WalletData.getInstance().getWallet(),WalletData.getInstance().getWallet().getPublicKey().getEncoded(),100,1,transSignature);
            }
            resultSetBlockchain.close();

            blockchainStmt.executeUpdate("CREATE TABLE IF NOT EXISTS TRANSACTIONS ( " +
                    " ID INTEGER NOT NULL UNIQUE, " +
                    " \"FROM\" BLOB, " +
                    " \"TO\" BLOB, " +
                    " LEDGER_ID INTEGER, " +
                    " VALUE INTEGER, " +
                    " SIGNATURE BLOB UNIQUE, " +
                    " CREATED_ON TEXT, " +
                    " PRIMARY KEY(ID AUTOINCREMENT) " +
                    ")"
            );
            if (initBlockRewardTransaction != null) {
                BlockchainData.getInstance().addTransaction(initBlockRewardTransaction,true);
                BlockchainData.getInstance().addTransactionState(initBlockRewardTransaction);
            }
            blockchainStmt.close();
            blockchainConnection.close();
        } catch (SQLException | NoSuchAlgorithmException | InvalidKeyException | SignatureException e) {
            System.out.println("db failed: " + e.getMessage());
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        }
        BlockchainData.getInstance().loadBlockChain();
    }
}

