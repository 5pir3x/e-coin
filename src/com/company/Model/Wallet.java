package com.company.Model;

import java.io.Serializable;
import java.security.*;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.LinkedList;

public class Wallet implements Serializable {

    private KeyPair keyPair;
    private PublicKey walletAddress;

    KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance("DSA");

    public Wallet(Integer keySize) throws NoSuchAlgorithmException {
       keyPairGen.initialize(keySize);
       this.keyPair = keyPairGen.generateKeyPair();
       this.walletAddress = keyPair.getPublic();
    }
    public Wallet(Integer keySize,Integer blockBalance) throws NoSuchAlgorithmException {
     this(keySize);
        Integer balance = 100;
    }
    public Wallet(PublicKey publicKey, PrivateKey privateKey) throws NoSuchAlgorithmException {
        this.keyPair = new KeyPair(publicKey,privateKey);
    }

    public KeyPair getKeyPair() { return keyPair; }

    public PublicKey getPublicKey() { return keyPair.getPublic(); }
    public PrivateKey getPrivateKey() { return keyPair.getPrivate(); }

    public Integer getBalance(LinkedList<Block> blockChain) {
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
//    public Integer adjustFunds(Integer amount) {
//       return this.balance = balance + amount;
//    }
    public PublicKey getWalletAddress() { return walletAddress; }
}
