package com.company.NetworkHandlers;

import com.company.Model.Wallet;
import com.company.Model.WalletDataNetwork;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.security.NoSuchAlgorithmException;

public class Client extends Thread {

    private Integer port;

    public Client(Integer port) {
        this.port = port;

    }

    @Override
    public void run() {
        while (true) {

        try (Socket socket = new Socket("127.0.0.1", port)) {

            socket.setSoTimeout(5000);

            ObjectOutputStream objectOutput = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream objectInput = new ObjectInputStream(socket.getInputStream());

            System.out.println("Enter string to be echoed: ");

            Wallet wallet = new Wallet(2048,100);
            WalletDataNetwork wd = new WalletDataNetwork(wallet.getKeyPair(),wallet.getWalletAddress(),wallet.getKeyPair().hashCode());
            objectOutput.writeObject(wd);
            Thread.sleep(2000);

        } catch(SocketTimeoutException e) {
            System.out.println("The socket timed out");
        } catch (IOException e) {
            System.out.println("Client Error: " + e.getMessage());
        } catch (NoSuchAlgorithmException | InterruptedException e) {
            e.printStackTrace();
        }
        }
    }
}
