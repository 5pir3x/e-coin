package com.company.Model;

import java.io.Serializable;
import java.security.*;
import java.util.Arrays;

public class Transaction implements Serializable {

   //my public key - my wallet address
   private byte[] from;
   //your public key - your wallet address
   private byte[] to;
   //value to be transferred
   private Integer value;
   //encrypted with my private key
   private byte[] signature;
   //helper class.
   private Signature signing = Signature.getInstance("SHA256withDSA");



   public Transaction(byte[] from, byte[] to, Integer value, PrivateKey privateKey) throws NoSuchAlgorithmException, InvalidKeyException, SignatureException {
      this.from = from;
      this.to = to;
      this.value = value;
      this.signing.initSign(privateKey);
//      String sr = ( Arrays.toString(from) + " " + Arrays.toString(to) + " " +  value.toString());
      String sr = this.toString();
      signing.update(sr.getBytes());
      signature = signing.sign();
   }
   public Transaction (Wallet fromWallet, byte[] toAddress, Integer value) throws InvalidKeyException, SignatureException, NoSuchAlgorithmException {
      this.from = fromWallet.getPublicKey().getEncoded();
      this.to = toAddress;
      this.value = value;
      this.signing.initSign(fromWallet.getPrivateKey());
      String sr = this.toString();
      signing.update(sr.getBytes());
      signature = signing.sign();
   }


   public Boolean isVerified(Transaction transaction, PublicKey publicKey) throws InvalidKeyException, SignatureException {
      signing.initVerify(publicKey);
      signing.update(transaction.toString().getBytes());
      return signing.verify(signature);
   }

   @Override
   public String toString() {
      return "Transaction{" +
              "from=" + Arrays.toString(from) +
              ", to=" + Arrays.toString(to) +
              ", value=" + value +
              '}';
   }

   public byte[] getFrom() { return from; }
   public void setFrom(byte[] from) { this.from = from; }

   public byte[] getTo() { return to; }
   public void setTo(byte[] to) { this.to = to; }

   public Integer getValue() { return value; }
   public void setValue(Integer value) { this.value = value; }

   public byte[] getSignature() { return signature; }


}
