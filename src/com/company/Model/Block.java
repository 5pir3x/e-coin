package com.company.Model;

import sun.security.provider.DSAPublicKeyImpl;

import java.io.Serializable;
import java.security.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;

public class Block implements Serializable {

    private LinkedList<Block> currentBlockChain = new LinkedList<>();
    private byte[] prevHash;
    private byte[] currHash;
    private ArrayList<Transaction> transactionLedger = new ArrayList<>();
    private LocalDateTime timeStamp;
    private byte[] minedBy;
    private Wallet blockRewardWallet  = new Wallet(2048,100);
    //helper class.
    private Signature signing = Signature.getInstance("SHA256withDSA");

    public Block(byte[] prevHash) throws NoSuchAlgorithmException {
        this.prevHash = prevHash;
    }
    public Block(Block previousBlock) throws NoSuchAlgorithmException {
        this.prevHash = previousBlock.currHash;
    }
    public Block(LinkedList<Block> currentBlockChain) throws GeneralSecurityException {
        //Re-Validate the Blockchain before assigning it.
        verifyBlockChain(currentBlockChain);
        this.currentBlockChain = currentBlockChain;
        this.prevHash = currentBlockChain.getLast().getCurrHash();
    }

    public void addTransaction(Transaction transaction) {
        transactionLedger.add(transaction);
    }

    public LinkedList<Block> finalizeBlock(Wallet minersWallet) throws GeneralSecurityException {
        currHash = prevHash;
        for (Transaction transaction : transactionLedger) {
            if (transaction.isVerified(transaction, new DSAPublicKeyImpl(transaction.getFrom())) && getBalance(currentBlockChain,transactionLedger,new DSAPublicKeyImpl(transaction.getFrom())) >= 0) {
                this.currHash = (Arrays.toString(currHash) + Arrays.toString(transaction.getSignature())).getBytes();
            } else {
                throw new GeneralSecurityException("Block transactions validation failed");
            }
        }
        //Reward transaction
        Transaction transaction = new Transaction(blockRewardWallet.getPublicKey().getEncoded(),minersWallet.getPublicKey().getEncoded(),100,blockRewardWallet.getPrivateKey());
        addTransaction(transaction);
        this.currHash = (Arrays.toString(currHash) + Arrays.toString(transaction.getSignature())).getBytes();
        signing.initSign(minersWallet.getPrivateKey());
        signing.update(currHash);
        currHash = signing.sign();
        this.timeStamp = LocalDateTime.now();
        minedBy = minersWallet.getPublicKey().getEncoded();
        currentBlockChain.add(this);
        return currentBlockChain;
    }

    public LinkedList<Block> getCurrentBlockChain() { return currentBlockChain; }

    public byte[] getPrevHash() { return prevHash; }
    public byte[] getCurrHash() { return currHash; }

    public ArrayList<Transaction> getTransactionLedger() { return transactionLedger; }

    public LocalDateTime getTimeStamp() { return timeStamp; }

    public byte[] getMinedBy() { return minedBy; }

    public void verifyBlockChain (LinkedList<Block> currentBlockChain) throws GeneralSecurityException {
        for (Block block : currentBlockChain) {
            for (Transaction transaction : block.getTransactionLedger()) {
                if (!transaction.isVerified(transaction, new DSAPublicKeyImpl(transaction.getFrom()))) {
                    throw new GeneralSecurityException("Blockchain validation failed");
                }
            }
        }
    }
    public Integer getBalance(LinkedList<Block> blockChain, PublicKey walletAddress) {
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

    public Integer getBalance(LinkedList<Block> blockChain, ArrayList<Transaction> currentLedger, PublicKey walletAddress) {
        Integer balance = 0;
        for (Block block : blockChain) {
            for ( Transaction transaction : block.getTransactionLedger()) {
                if (Arrays.equals(transaction.getFrom(), walletAddress.getEncoded())) {
                    balance -= transaction.getValue();
                } else if (Arrays.equals(transaction.getTo(), walletAddress.getEncoded())) {
                    balance += transaction.getValue();
                }
            }
            for ( Transaction transaction : currentLedger) {
                if (Arrays.equals(transaction.getFrom(), walletAddress.getEncoded())) {
                    balance -= transaction.getValue();
                } else if (Arrays.equals(transaction.getTo(), walletAddress.getEncoded())) {
                    balance += transaction.getValue();
                }
            }
        }
        return balance;
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Block)) return false;

        Block block = (Block) o;

        // Probably incorrect - comparing Object[] arrays with Arrays.equals
        if (!Arrays.equals(prevHash, block.prevHash)) return false;
        // Probably incorrect - comparing Object[] arrays with Arrays.equals
        return Arrays.equals(currHash, block.currHash);
    }

    @Override
    public int hashCode() {
        int result = Arrays.hashCode(prevHash);
        result = 31 * result + Arrays.hashCode(currHash);
        return result;
    }

    @Override
    public String toString() {
        return "Block{" +
                "prevHash=" + Arrays.toString(prevHash) +
                ", currHash=" + Arrays.toString(currHash) +
                ", transactionLedger=" + transactionLedger.toString() +
                ", timeStamp=" + timeStamp.toString() +
                ", minedBy=" + Arrays.toString(minedBy) +
                '}';
    }
}
