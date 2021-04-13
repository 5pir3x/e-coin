package com.company.NetworkHandlers;


import com.company.NetworkDataModel.BlockChainNetworkData;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;


public class PeerRequestThread extends Thread {
    private Socket socket;

    public PeerRequestThread(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try {

            ObjectOutputStream objectOutput = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream objectInput = new ObjectInputStream(socket.getInputStream());

//                WalletDataNetwork walletDataNetwork = (WalletDataNetwork) objectInput.readObject();
                BlockChainNetworkData bdn = (BlockChainNetworkData) objectInput.readObject();
                System.out.println("LedgerId = " + bdn.getCurrentBlockChain().getLast().getLedgerId() + " balance= " + bdn.getCurrentBlockChain().getLast().getTransactionLedger().get(0).getValue() + " Size= " + bdn.getCurrentBlockChain().getLast().getTransactionLedger().size());
            objectOutput.writeObject("123456");
        } catch (IOException | ClassNotFoundException ex) {
            ex.printStackTrace();
        }
    }

}
