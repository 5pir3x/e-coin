package com.company.NetworkDataModel;

import com.company.Model.Transaction;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;

public class BlockNetworkData implements Serializable {
    private byte[] prevHash;
    private byte[] currHash;
    private String timeStamp;
    private byte[] minedBy;
    private Integer ledgerId = 0;
    private Integer miningPoints = 0;
    private Double luck;
    private ArrayList<TransactionNetworkData> transactionLedger = new ArrayList<>();

    //This constructor is used when we retrieve it from the db
    public BlockNetworkData(byte[] prevHash, byte[] currHash, String timeStamp, byte[] minedBy, Integer ledgerId, Integer miningPoints, Double luck, ArrayList<Transaction> transactionLedger) {
        this.prevHash = prevHash;
        this.currHash = currHash;
        this.timeStamp = timeStamp;
        this.minedBy = minedBy;
        this.ledgerId = ledgerId;
        this.miningPoints = miningPoints;
        this.luck = luck;
        for (Transaction tr : transactionLedger) {
            TransactionNetworkData tdn = new TransactionNetworkData(tr);
            this.transactionLedger.add(tdn);
        }
    }
    //This constructor is used when we initiate it after retrieve.
//    public Block(LinkedList<Block> currentBlockChain) throws GeneralSecurityException {
//        Block lastBlock = currentBlockChain.getLast();
//        prevHash = lastBlock.getCurrHash();
//        ledgerId = lastBlock.getLedgerId() + 1;
//        luck = Math.random() * 1000000;
//    }

    public byte[] getPrevHash() { return prevHash; }
    public byte[] getCurrHash() { return currHash; }

    public void setPrevHash(byte[] prevHash) { this.prevHash = prevHash; }
    public void setCurrHash(byte[] currHash) { this.currHash = currHash; }

    public ArrayList<TransactionNetworkData> getTransactionLedger() { return transactionLedger; }
    public void setTransactionLedger(ArrayList<TransactionNetworkData> transactionLedger) { this.transactionLedger = transactionLedger; }

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
//                ", transactionLedger=" + transactionLedger.toString() +
                ", timeStamp=" + timeStamp.toString() +
                ", minedBy=" + Arrays.toString(minedBy) +
                '}';
    }
}
