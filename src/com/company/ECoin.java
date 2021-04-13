package com.company;

import com.company.Model.Wallet;
import com.company.NetworkHandlers.PeerClient;
import com.company.NetworkHandlers.PeerServer;
import com.company.NetworkHandlers.UI;
import com.company.ServiceData.BlockData;
import com.company.ServiceData.WalletData;
import javafx.application.Application;
import javafx.stage.Stage;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.sql.*;

public class ECoin extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        new UI().start(primaryStage);
        new PeerClient(6000).start();
        new PeerServer(6000).start();
    }

    @Override
    public void init() throws NoSuchAlgorithmException, SQLException, InvalidKeySpecException {
        try {
//                        This will create the db tables with columns for the Blockchain.
            Connection connection = DriverManager.getConnection("jdbc:sqlite:C:\\Users\\spiro\\IdeaProjects\\e-coin\\db\\ecointest2.db");
            Statement stmt = connection.createStatement();
            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS BLOCKCHAIN ( " +
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
            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS TRANSACTIONS ( " +
                    " ID INTEGER NOT NULL UNIQUE, " +
                    " \"FROM\" BLOB, " +
                    " \"TO\" BLOB, " +
                    " LEDGER_ID INTEGER, " +
                    " VALUE INTEGER, " +
                    " SIGNATURE BLOB, " +
                    " CREATED_ON TEXT, " +
                    " PRIMARY KEY(ID AUTOINCREMENT) " +
                    ")"
            );

            //This creates your wallet if there is none and give you a KeyPair.
            //We will create it in separate db for better security and ease of portability.
            Connection walletConnection = DriverManager.getConnection("jdbc:sqlite:C:\\Users\\spiro\\IdeaProjects\\e-coin\\db\\wallet.db");
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
                PreparedStatement pstmt = walletConnection.prepareStatement("INSERT INTO WALLET(PRIVATE_KEY, PUBLIC_KEY) " +
                        " VALUES (?,?) ");
                pstmt.setBytes(1, prvBlob);
                pstmt.setBytes(2, pubBlob);
                pstmt.executeUpdate();
            }
            resultSet.close();
            walletStatment.close();
            walletConnection.close();
            stmt.close();
            connection.close();
        } catch (SQLException | NoSuchAlgorithmException e) {
            System.out.println("db failed: " + e.getMessage());
        }
//                                ContactData.getInstance().loadContacts();

        WalletData.getInstance().loadWallet();
        BlockData.getInstance().loadBlockChain();
    }

//        @Override
//        public void stop() throws Exception {
//                ContactData.getInstance().saveContacts();
//        }
}

