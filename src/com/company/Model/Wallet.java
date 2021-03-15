package com.company.Model;

import java.io.Serializable;
import java.security.*;

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

    public KeyPair getKeyPair() { return keyPair; }

    public PublicKey getPublicKey() { return keyPair.getPublic(); }
    public PrivateKey getPrivateKey() { return keyPair.getPrivate(); }


    public PublicKey getWalletAddress() { return walletAddress; }
}
