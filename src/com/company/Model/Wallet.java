package com.company.Model;

import java.io.Serializable;
import java.security.*;

public class Wallet implements Serializable {

    private KeyPair keyPair;

    //Constructors for generating new KeyPair
    public Wallet() throws NoSuchAlgorithmException {
        this(2048, KeyPairGenerator.getInstance("DSA"));
    }
    public Wallet(Integer keySize, KeyPairGenerator keyPairGen) {
       keyPairGen.initialize(keySize);
       this.keyPair = keyPairGen.generateKeyPair();
    }

    //Constructor for importing Keys only
    public Wallet(PublicKey publicKey, PrivateKey privateKey) {
        this.keyPair = new KeyPair(publicKey,privateKey);
    }

    public KeyPair getKeyPair() { return keyPair; }

    public PublicKey getPublicKey() { return keyPair.getPublic(); }
    public PrivateKey getPrivateKey() { return keyPair.getPrivate(); }
}