package com.company.Model;

import java.io.Serializable;
import java.security.*;
import java.util.ArrayList;
import java.util.Arrays;

public class Wallet implements Serializable {

    private KeyPair keyPair;
    private PublicKey walletAddress;
    private Integer balance = 0;

    KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance("DSA");
    //Constructors for generating new KeyPair
    public Wallet() throws NoSuchAlgorithmException {
        this(2048);
    }
    public Wallet(Integer keySize) throws NoSuchAlgorithmException {
       keyPairGen.initialize(keySize);
       this.keyPair = keyPairGen.generateKeyPair();
       this.walletAddress = keyPair.getPublic();

    }
    //Constructor for Block wallet.
    public Wallet(Integer keySize,Integer blockBalance) throws NoSuchAlgorithmException {
     this(keySize);
        balance = 100;
    }
    //Constructor for importing Keys only
    public Wallet(PublicKey publicKey, PrivateKey privateKey) throws NoSuchAlgorithmException {
        this.keyPair = new KeyPair(publicKey,privateKey);
        this.walletAddress = keyPair.getPublic();
    }
    //Constructor for importing Keys and calculating balance from blockchain
//    public Wallet(PublicKey publicKey, PrivateKey privateKey,LinkedList<Block> blockChain) throws NoSuchAlgorithmException {
//        this(publicKey,privateKey);
//        setBalance(getBalance(blockChain));
//    }

    public KeyPair getKeyPair() { return keyPair; }

    public PublicKey getPublicKey() { return keyPair.getPublic(); }
    public PrivateKey getPrivateKey() { return keyPair.getPrivate(); }

    public Integer getBalance() { return balance; }
    public void setBalance(Integer balance) { this.balance = balance; }

    public Integer getBalance(Integer balance, ArrayList<Transaction> currentLedger) {
        for ( Transaction transaction : currentLedger) {
            if (Arrays.equals(transaction.getFrom(), keyPair.getPublic().getEncoded())) {
                balance -= transaction.getValue();
            } else if (Arrays.equals(transaction.getTo(), keyPair.getPublic().getEncoded())) {
                balance += transaction.getValue();
            }
        }

        return balance;
    }
//    public Integer adjustFunds(Integer amount) {
//       return this.balance = balance + amount;
//    }
    public PublicKey getWalletAddress() { return walletAddress; }
}
