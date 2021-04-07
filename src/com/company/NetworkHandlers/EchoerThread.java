package com.company.NetworkHandlers;


import com.company.Model.WalletDataNetwork;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;


public class EchoerThread extends Thread {
    private Socket socket;

    public EchoerThread(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {

            try {

                ObjectOutputStream objectOutput = new ObjectOutputStream(socket.getOutputStream());
                ObjectInputStream objectInput = new ObjectInputStream(socket.getInputStream());

                WalletDataNetwork walletDataNetwork = (WalletDataNetwork) objectInput.readObject();

                System.out.println("Wallet PublicKey = " + walletDataNetwork.getWalletAddress() + " balance= " + walletDataNetwork.getBalance());

            } catch (IOException | ClassNotFoundException ex) {
                ex.printStackTrace();
            }
        }

}
