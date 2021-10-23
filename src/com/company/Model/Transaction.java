package com.company.Model;

import sun.security.provider.DSAPublicKeyImpl;

import java.io.Serializable;
import java.security.InvalidKeyException;
import java.security.Signature;
import java.security.SignatureException;
import java.time.LocalDateTime;
import java.util.Arrays;

public class Transaction implements Serializable {

   private byte[] from;
   private byte[] to;
   private Integer value;
   private String timeStamp;
   private byte[] signature;
   private Integer ledgerId;


   //Constructor for loading with existing signature
   public Transaction(byte[] from, byte[] to, Integer value, byte[] signature, Integer ledgerId,
                      String timeStamp) {
      this.from = from;
      this.to = to;
      this.value = value;
      this.signature = signature;
      this.ledgerId = ledgerId;
      this.timeStamp = timeStamp;
   }
   //Constructor for creating a new transaction and signing it.
   public Transaction (Wallet fromWallet, byte[] toAddress, Integer value, Integer ledgerId,
                       Signature signing) throws InvalidKeyException, SignatureException {
      this.from = fromWallet.getPublicKey().getEncoded();
      this.to = toAddress;
      this.value = value;
      this.ledgerId = ledgerId;
      this.timeStamp = LocalDateTime.now().toString();
      signing.initSign(fromWallet.getPrivateKey());
      String sr = this.toString();
      signing.update(sr.getBytes());
      this.signature = signing.sign();
   }

   public Boolean isVerified(Transaction transaction, Signature signing)
           throws InvalidKeyException, SignatureException {
      signing.initVerify(new DSAPublicKeyImpl(transaction.getFrom()));
      signing.update(transaction.toString().getBytes());
      return signing.verify(signature);
   }

   @Override
   public String toString() {
      return "Transaction{" +
              "from=" + Arrays.toString(from) +
              ", to=" + Arrays.toString(to) +
              ", value=" + value +
              ", timeStamp= " + timeStamp +
              ", ledgerId=" + ledgerId +
              '}';
   }

   public byte[] getFrom() { return from; }
   public void setFrom(byte[] from) { this.from = from; }

   public byte[] getTo() { return to; }
   public void setTo(byte[] to) { this.to = to; }

   public Integer getValue() { return value; }
   public void setValue(Integer value) { this.value = value; }
   public byte[] getSignature() { return signature; }

   public Integer getLedgerId() { return ledgerId; }
   public void setLedgerId(Integer ledgerId) { this.ledgerId = ledgerId; }

   public String getTimeStamp() {
      return timeStamp;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (!(o instanceof Transaction)) return false;
      Transaction that = (Transaction) o;
      return Arrays.equals(getSignature(), that.getSignature());
   }

   @Override
   public int hashCode() {
      return Arrays.hashCode(getSignature());
   }

}
