package com.company.Model;

import java.io.Serializable;
import java.util.Base64;

public class TransactionFX  implements Serializable {

    private String from;
    private String to;
    private Integer value;
    private Integer ledgerId;
    private String signature;
    private String timestamp;
    private Base64.Encoder encoder = Base64.getEncoder();

    public TransactionFX(Transaction transaction) {
        this.from = encoder.encodeToString(transaction.getFrom());
        this.to = encoder.encodeToString(transaction.getTo());
        this.value = transaction.getValue();
        this.ledgerId = transaction.getLedgerId();
        this.timestamp = transaction.getTimeStamp();
        this.signature = encoder.encodeToString(transaction.getSignature());
    }

    public Integer getLedgerId() { return ledgerId; }
    public String getFrom() { return from; }
    public String getTo() { return to; }
    public Integer getValue() { return value; }
    public String getSignature() { return signature; }
    public String getTimestamp() { return timestamp; }
    public Base64.Encoder getEncoder() { return encoder; }
}
