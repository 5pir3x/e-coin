package com.company.ServiceData;

import com.company.Model.Block;
import com.company.Model.Transaction;
import com.company.Model.Wallet;

import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.sql.*;
import java.util.Arrays;
import java.util.LinkedList;

public class WalletData {

    private Wallet wallet;
    //singleton class
    private static WalletData instance;
    static {
        instance = new WalletData();
    }
    //todo:Make sure this displays correct balance.
    public String getWalletBalanceFX(LinkedList<Block> blockChain) {
        return getBalance(blockChain).toString();
    }
    public Integer getBalance(LinkedList<Block> blockChain) {
        Integer balance = 0;
        for (Block block : blockChain) {
            for ( Transaction transaction : block.getTransactionLedger()) {
                if (Arrays.equals(transaction.getFrom(), wallet.getPublicKey().getEncoded())) {
                    balance -= transaction.getValue();
                } else if (Arrays.equals(transaction.getTo(), wallet.getPublicKey().getEncoded())) {
                    balance += transaction.getValue();
                }
            }
        }
        return balance;
    }
    public static WalletData getInstance(){
        return instance;
    }
    //This will load your wallet from the database.
    public void loadWallet() throws SQLException, NoSuchAlgorithmException, InvalidKeySpecException {
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
    public Wallet getWallet() { return wallet; }
}
