package com.company.Model;

import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.sql.*;

public class WalletData {

    private Wallet wallet;
    //singleton class
    private static WalletData instance;
    static {
        instance = new WalletData();
    }
    public static WalletData getInstance(){
        return instance;
    }
    public void loadWallet() throws SQLException, NoSuchAlgorithmException, InvalidKeySpecException {
        //This creates your wallet if there is none
        //We will create it in separate db for better security and ease of portability.
        Connection walletConnection = DriverManager.getConnection("jdbc:sqlite:C:\\Users\\spiro\\IdeaProjects\\e-coin\\db\\wallet.db");
        Statement walletStatment = walletConnection.createStatement();
        ResultSet resultSet;
        resultSet = walletStatment.executeQuery(" SELECT * FROM WALLET ");
        KeyFactory keyFactory = KeyFactory.getInstance("DSA");
        PublicKey pub2 = null;
        PrivateKey prv2 = null;
        while (resultSet.next()) {
            pub2 = keyFactory.generatePublic (
                    new X509EncodedKeySpec(resultSet.getBytes("PUBLIC_KEY")));
            prv2 = keyFactory.generatePrivate(
                    new PKCS8EncodedKeySpec(resultSet.getBytes("PRIVATE_KEY")));
        }
        this.wallet = new Wallet(pub2,prv2);
    }
}
