package com.company;

import com.company.Model.Block;
import com.company.Model.Transaction;
import com.company.Model.Wallet;

import java.security.GeneralSecurityException;
import java.util.LinkedList;


public class Main {

    public static void main(String[] args) throws GeneralSecurityException {

        Wallet wallet1 = new Wallet(2048);
        Wallet wallet2 = new Wallet(2048);
        Block firstBlock = new Block(new byte[0]);

        LinkedList<Block> blockChain = new LinkedList<>();
        System.out.println(wallet1.getBalance(firstBlock.getCurrentBlockChain()));
//        Transaction transaction1 = new Transaction(wallet1,wallet2.getPublicKey().getEncoded(),100);
//        firstBlock.addTransaction(transaction1);
        Block secondBlock = new Block(firstBlock.finalizeBlock(wallet1));
        Transaction transaction1 = new Transaction(wallet1,wallet2.getPublicKey().getEncoded(),50);
        secondBlock.addTransaction(transaction1);
        System.out.println(wallet1.getBalance(secondBlock.getCurrentBlockChain()));
        Block thirdBlock = new Block(secondBlock.finalizeBlock(wallet1));
        System.out.println(wallet1.getBalance(secondBlock.getCurrentBlockChain()));
        thirdBlock.finalizeBlock(wallet1);
        for (Block b : thirdBlock.getCurrentBlockChain()) {
            System.out.println(b.toString());
        }
    }
}
