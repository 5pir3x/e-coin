package com.company.NetworkHandlers;

import com.company.NetworkDataModel.BlockChainNetworkData;
import com.company.ServiceData.BlockData;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

public class MiningThread extends Thread {


    @Override
    public void run() {
        while (true) {
            BlockChainNetworkData blockChain = new BlockChainNetworkData(BlockData.getInstance().getCurrentBlockChain());
            long lastMinedBlock = LocalDateTime.parse(blockChain.getCurrentBlockChain().getLast().getTimeStamp()).toEpochSecond(ZoneOffset.UTC);
            if ((lastMinedBlock + 60) < LocalDateTime.now().toEpochSecond(ZoneOffset.UTC)) {
                System.out.println("BlockChain is too old for mining! Update it from peers");
            } else {
                System.out.println("BlockChain is current mining will commence in " + ((lastMinedBlock + 60) - LocalDateTime.now().toEpochSecond(ZoneOffset.UTC) ) + " seconds");
            };
            System.out.println(LocalDateTime.parse(blockChain.getCurrentBlockChain().getLast().getTimeStamp()).toEpochSecond(ZoneOffset.UTC));
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
