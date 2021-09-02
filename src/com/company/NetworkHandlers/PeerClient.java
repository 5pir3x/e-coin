package com.company.NetworkHandlers;

import com.company.NetworkDataModel.BlockChainNetworkData;
import com.company.ServiceData.BlockData;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketTimeoutException;

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
                BlockChainNetworkData blockChain = new BlockChainNetworkData(BlockData.getInstance().getCurrentBlockChain());

                objectOutput.writeObject(blockChain);
                System.out.println(((String) objectInput.readObject()));
                Thread.sleep(2000);
                if (BlockData.getInstance().isExit()) {
                    System.out.println("SSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSS");
                }
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
