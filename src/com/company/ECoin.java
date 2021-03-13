package com.company;

import com.company.Model.BlockChainData;
import com.company.Model.Wallet;
import com.company.Model.WalletData;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.sql.*;

public class ECoin extends Application{

public static void main(String[]args){ launch(args); }

@Override
public void start(Stage primaryStage) throws IOException, SQLException {
        Parent root = FXMLLoader.load(getClass().getResource("View/MainWindow.fxml"));
        primaryStage.setTitle("E-Coin");
        primaryStage.setScene(new Scene(root, 600, 400));
        primaryStage.show();

        }

        @Override
        public void init() throws NoSuchAlgorithmException, SQLException, InvalidKeySpecException {
                try {
                        Connection connection = DriverManager.getConnection("jdbc:sqlite:C:\\Users\\spiro\\IdeaProjects\\e-coin\\db\\ecointest2.db");
                        Statement stmt = connection.createStatement();
                        stmt.executeUpdate("CREATE TABLE IF NOT EXISTS BLOCKCHAIN ( " +
                                " ID INTEGER NOT NULL UNIQUE, " +
                                " PREVIOUS_HASH TEXT UNIQUE, " +
                                " CURRENT_HASH TEXT UNIQUE, " +
                                " LEDGER_ID INTEGER NOT NULL UNIQUE, " +
                                " CREATED_ON  TEXT, " +
                                " CREATED_BY  TEXT, " +
                                " PRIMARY KEY( ID AUTOINCREMENT) " +
                                ")"
                        );
                        stmt.executeUpdate("CREATE TABLE IF NOT EXISTS TRANSACTIONS ( " +
                                " ID INTEGER NOT NULL UNIQUE, " +
                                " \"FROM\" TEXT, " +
                                " \"TO\" TEXT, " +
                                " LEDGER_ID INTEGER, " +
                                " VALUE INTEGER, " +
                                " SIGNATURE TEXT, " +
                                " PRIMARY KEY(ID AUTOINCREMENT) " +
                                ")"
                        );

                        //This creates your wallet if there is none
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
                                PreparedStatement pstmt = connection.prepareStatement("INSERT INTO WALLET(PRIVATE_KEY, PUBLIC_KEY) " +
                                        " VALUES (?,?) ");
                                pstmt.setBytes(1,prvBlob);
                                pstmt.setBytes(2,pubBlob);
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
                //                ContactData.getInstance().loadContacts();
                BlockChainData.getInstance().loadBlockChain();
                WalletData.getInstance().loadWallet();
        }

//        @Override
//        public void stop() throws Exception {
//                ContactData.getInstance().saveContacts();
//        }
}

