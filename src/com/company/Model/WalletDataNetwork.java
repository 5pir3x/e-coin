package com.company.Model;

import java.io.Serializable;
import java.security.KeyPair;
import java.security.PublicKey;

public class WalletDataNetwork implements Serializable {

    private KeyPair keyPair;
    private PublicKey walletAddress;
    private Integer balance = 0;

    public WalletDataNetwork(KeyPair keyPair, PublicKey walletAddress, Integer balance) {
        this.keyPair = keyPair;
        this.walletAddress = walletAddress;
        this.balance = balance;
    }

    public KeyPair getKeyPair() { return keyPair; }
    public void setKeyPair(KeyPair keyPair) { this.keyPair = keyPair; }

    public PublicKey getWalletAddress() { return walletAddress; }
    public void setWalletAddress(PublicKey walletAddress) { this.walletAddress = walletAddress; }

    public Integer getBalance() { return balance; }
    public void setBalance(Integer balance) { this.balance = balance; }
}
