package com.company.Model;

import java.io.Serializable;
import java.security.GeneralSecurityException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;

public class Block implements Serializable {


    private byte[] prevHash;
    private byte[] currHash;
    private String timeStamp;
    private byte[] minedBy;
    private Integer ledgerId = 0;
    private ArrayList<Transaction> transactionLedger = new ArrayList<>();

    //This constructor is used when we retrieve it from the db
    public Block(byte[] prevHash, byte[] currHash, String timeStamp, byte[] minedBy,Integer ledgerId, ArrayList<Transaction> transactionLedger) throws NoSuchAlgorithmException {
        this.prevHash = prevHash;
        this.currHash = currHash;
        this.timeStamp = timeStamp;
        this.minedBy = minedBy;
        this.ledgerId = ledgerId;
        this.transactionLedger = transactionLedger;
    }
    //This constructor is used when we initiate it after retrieve.
    public Block(LinkedList<Block> currentBlockChain) throws GeneralSecurityException {
        Block lastBlock = currentBlockChain.getLast();
        prevHash = lastBlock.getCurrHash();
        ledgerId = lastBlock.getLedgerId() + 1;
    }

    public byte[] getPrevHash() { return prevHash; }
    public byte[] getCurrHash() { return currHash; }

    public void setPrevHash(byte[] prevHash) { this.prevHash = prevHash; }
    public void setCurrHash(byte[] currHash) { this.currHash = currHash; }

    public ArrayList<Transaction> getTransactionLedger() { return transactionLedger; }
    public void setTransactionLedger(ArrayList<Transaction> transactionLedger) { this.transactionLedger = transactionLedger; }

    public String getTimeStamp() { return timeStamp; }
    public void setMinedBy(byte[] minedBy) { this.minedBy = minedBy; }

    public void setTimeStamp(String timeStamp) { this.timeStamp = timeStamp; }

    public byte[] getMinedBy() { return minedBy; }


    public Integer getLedgerId() { return ledgerId; }
    public void setLedgerId(Integer ledgerId) { this.ledgerId = ledgerId; }

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
