package com.company.Model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;

public class Block implements Serializable {

    private byte[] prevHash;
    private byte[] currHash;
    private String timeStamp;
    private byte[] minedBy;
    private Integer ledgerId = 0;
    private Integer miningPoints = 0;
    private Double luck = 0.0;

    private ArrayList<Transaction> transactionLedger = new ArrayList<>();

    //This constructor is used when we retrieve it from the db
    public Block(byte[] prevHash, byte[] currHash, String timeStamp, byte[] minedBy,Integer ledgerId,
                 Integer miningPoints, Double luck, ArrayList<Transaction> transactionLedger) {
        this.prevHash = prevHash;
        this.currHash = currHash;
        this.timeStamp = timeStamp;
        this.minedBy = minedBy;
        this.ledgerId = ledgerId;
        this.transactionLedger = transactionLedger;
        this.miningPoints = miningPoints;
        this.luck = luck;
    }
    //This constructor is used when we initiate it after retrieve.
    public Block(LinkedList<Block> currentBlockChain) {
        Block lastBlock = currentBlockChain.getLast();
        prevHash = lastBlock.getCurrHash();
        ledgerId = lastBlock.getLedgerId() + 1;
        luck = Math.random() * 1000000;
    }
    //This constructor is used only for creating the first block in the blockchain.
    public Block() {
        prevHash = new byte[]{0};
    }

    public byte[] getPrevHash() { return prevHash; }
    public byte[] getCurrHash() { return currHash; }

    public void setPrevHash(byte[] prevHash) { this.prevHash = prevHash; }
    public void setCurrHash(byte[] currHash) { this.currHash = currHash; }

    public ArrayList<Transaction> getTransactionLedger() { return transactionLedger; }
    public void setTransactionLedger(ArrayList<Transaction> transactionLedger) {
        this.transactionLedger = transactionLedger;
    }

    public String getTimeStamp() { return timeStamp; }
    public void setMinedBy(byte[] minedBy) { this.minedBy = minedBy; }

    public void setTimeStamp(String timeStamp) { this.timeStamp = timeStamp; }

    public byte[] getMinedBy() { return minedBy; }

    public Integer getMiningPoints() { return miningPoints; }
    public void setMiningPoints(Integer miningPoints) { this.miningPoints = miningPoints; }
    public Double getLuck() { return luck; }
    public void setLuck(Double luck) { this.luck = luck; }

    public Integer getLedgerId() { return ledgerId; }
    public void setLedgerId(Integer ledgerId) { this.ledgerId = ledgerId; }

    @Override
    public String toString() {
        return "Block{" +
                "prevHash=" + Arrays.toString(prevHash) +
                ", currHash=" + Arrays.toString(currHash) +
                ", timeStamp='" + timeStamp + '\'' +
                ", minedBy=" + Arrays.toString(minedBy) +
                ", ledgerId=" + ledgerId +
                ", miningPoints=" + miningPoints +
                ", luck=" + luck +
                '}';
    }
}
