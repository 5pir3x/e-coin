package com.company.NetworkHandlers;

import com.company.ServiceData.BlockData;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

public class MiningThread extends Thread {


    @Override
    public void run() {
        while (true) {
            long lastMinedBlock = LocalDateTime.parse(BlockData.getInstance().getCurrentBlockChain().getLast().getTimeStamp()).toEpochSecond(ZoneOffset.UTC);
            if ((lastMinedBlock + 65) < LocalDateTime.now().toEpochSecond(ZoneOffset.UTC)) {
                System.out.println("BlockChain is too old for mining! Update it from peers");
            } else if ( ((lastMinedBlock + 6) - LocalDateTime.now().toEpochSecond(ZoneOffset.UTC)) > 0 ) {
                System.out.println("BlockChain is current mining will commence in " + ((lastMinedBlock + 60) - LocalDateTime.now().toEpochSecond(ZoneOffset.UTC) ) + " seconds");
            } else {
                System.out.println("MINING NEW BLOCK");
                    BlockData.getInstance().mineBlock();
                    BlockData.getInstance().getWalletBallanceFX();
                    System.out.println(BlockData.getInstance().getWalletBallanceFX());
            }
            System.out.println(LocalDateTime.parse(BlockData.getInstance().getCurrentBlockChain().getLast().getTimeStamp()).toEpochSecond(ZoneOffset.UTC));
            try {
                Thread.sleep(2000);
                BlockData.getInstance().setMiningPoints(BlockData.getInstance().getMiningPoints() + 2);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
