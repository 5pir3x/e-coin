package com.company.NetworkHandlers;

import com.company.NetworkDataModel.BlockChainNetworkData;
import com.company.ServiceData.BlockData;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

public class PeerClient extends Thread {

    private Integer port;

    public PeerClient(Integer port) {
        this.port = port;

    }

    @Override
    public void run() {
//        todo:Retrieve the initial list of peers here instead from the constructor.
        while (true) {
            try (Socket socket = new Socket("127.0.0.1", port)) {

                socket.setSoTimeout(5000);

                ObjectOutputStream objectOutput = new ObjectOutputStream(socket.getOutputStream());
                ObjectInputStream objectInput = new ObjectInputStream(socket.getInputStream());

                System.out.println("Enter string to be echoed: ");

//                Wallet wallet = new Wallet(2048, 100);
//                WalletDataNetwork wd = new WalletDataNetwork(wallet.getKeyPair(), wallet.getWalletAddress(), wallet.getKeyPair().hashCode());
                BlockChainNetworkData blockChain = new BlockChainNetworkData(BlockData.getInstance().getCurrentBlockChain());
//                if()
                System.out.println(blockChain.getCurrentBlockChain().getLast().getTimeStamp());
//                Instant instant = LocalDateTime.parse(blockChain.getCurrentBlockChain().getLast().getTimeStamp()).toInstant(ZoneOffset.UTC);
//                System.out.println(instant.getEpochSecond());
                long lastMinedBlock = LocalDateTime.parse(blockChain.getCurrentBlockChain().getLast().getTimeStamp()).toEpochSecond(ZoneOffset.UTC);
                if ((lastMinedBlock + 60) < LocalDateTime.now().toEpochSecond(ZoneOffset.UTC)) {
                    System.out.println("BlockChain is too old for mining! Update it from peers");
                } else {
                    System.out.println("BlockChain is current mining will commence in " + ((lastMinedBlock + 60) - LocalDateTime.now().toEpochSecond(ZoneOffset.UTC) ) + " seconds");
                };
                System.out.println(LocalDateTime.parse(blockChain.getCurrentBlockChain().getLast().getTimeStamp()).toEpochSecond(ZoneOffset.UTC));

//                BlockNetworkData bdn = new BlockNetworkData(block.getPrevHash(),block.getCurrHash(),block.getTimeStamp(),block.getMinedBy(),block.getLedgerId(),block.getMiningPoints(),block.getLuck(),block.getTransactionLedger());
                objectOutput.writeObject(blockChain);
                System.out.println(((String) objectInput.readObject()));
                Thread.sleep(2000);

            } catch (SocketTimeoutException e) {
                System.out.println("The socket timed out");
            } catch (IOException e) {
                System.out.println("Client Error: " + e.getMessage());
            } catch (InterruptedException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

}
