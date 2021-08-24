package com.company.NetworkHandlers;

import com.company.Controller.MainWindowController;
import com.company.NetworkDataModel.BlockChainNetworkData;
import com.company.ServiceData.BlockData;
import javafx.application.Platform;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

public class MiningThread extends Thread {


    @Override
    public void run() {
        while (true) {
            BlockChainNetworkData blockChain = new BlockChainNetworkData(BlockData.getInstance().getCurrentBlockChain());
            long lastMinedBlock = LocalDateTime.parse(blockChain.getCurrentBlockChain().getLast().getTimeStamp()).toEpochSecond(ZoneOffset.UTC);
            if ((lastMinedBlock + 65) < LocalDateTime.now().toEpochSecond(ZoneOffset.UTC)) {
                System.out.println("BlockChain is too old for mining! Update it from peers");
            } else if ( ((lastMinedBlock + 6) - LocalDateTime.now().toEpochSecond(ZoneOffset.UTC)) > 0 ) {
                System.out.println("BlockChain is current mining will commence in " + ((lastMinedBlock + 60) - LocalDateTime.now().toEpochSecond(ZoneOffset.UTC) ) + " seconds");
            } else {
                System.out.println("MINING NEW BLOCK");
                Platform.runLater(() -> {
                    BlockData.getInstance().mineBlock();
                    BlockData.getInstance().getWalletBallanceFX();
                    MainWindowController.getInstance().setTableview(BlockData.getInstance().getTransactionLedgerFX(),BlockData.getInstance().getWalletBallanceFX());
//                    MainWindowController.getInstance().setTableview(BlockData.getInstance().getTransactionLedgerFX(),BlockData.getInstance().getWalletBallanceFX());
                    System.out.println(BlockData.getInstance().getWalletBallanceFX());
//                    MainWindowController.getInstance().setTableview(BlockData.getInstance().getTransactionLedgerFX());
                });

//                MainWindowController.getInstance().setTableview(BlockData.getInstance().getTransactionLedgerFX());
            }
            System.out.println(LocalDateTime.parse(blockChain.getCurrentBlockChain().getLast().getTimeStamp()).toEpochSecond(ZoneOffset.UTC));
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
