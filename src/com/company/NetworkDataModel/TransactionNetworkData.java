package com.company.NetworkDataModel;

import com.company.Model.Transaction;

import java.io.Serializable;

public class TransactionNetworkData implements Serializable {

    //my public key - my wallet address
    private byte[] from;
    //your public key - your wallet address
    private byte[] to;
    //value to be transferred
    private Integer value;
    private String timeStamp;
    //encrypted with my private key
    private byte[] signature;
    private Integer ledgerId;

    public TransactionNetworkData(Transaction transaction) {
        this.from = transaction.getFrom();
        this.to = transaction.getTo();
        this.value = transaction.getValue();
        this.ledgerId = transaction.getLedgerId();
        this.timeStamp = transaction.getTimeStamp();
        this.signature = transaction.getSignature();
    }

    public byte[] getFrom() { return from; }
    public byte[] getTo() { return to; }
    public Integer getValue() { return value; }
    public String getTimeStamp() { return timeStamp; }
    public byte[] getSignature() { return signature; }
    public Integer getLedgerId() { return ledgerId; }
}
