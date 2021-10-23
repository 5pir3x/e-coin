package com.company.NetworkHandlers;

import com.company.Model.Block;
import com.company.ServiceData.BlockData;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class PeerClient extends Thread {


    private Queue<Integer> queue = new ConcurrentLinkedQueue<>();


    public PeerClient(Integer port) {
        this.queue.add(port);
        this.queue.add(6000);
    }

    @Override
    public void run() {

        while (true) {
            try (Socket socket = new Socket("127.0.0.1", queue.peek())) {
                System.out.println("Sending blockchain object on port: " + queue.peek());
                queue.add(queue.poll());
                socket.setSoTimeout(5000);

                ObjectOutputStream objectOutput = new ObjectOutputStream(socket.getOutputStream());
                ObjectInputStream objectInput = new ObjectInputStream(socket.getInputStream());

                LinkedList<Block> blockChain = BlockData.getInstance().getCurrentBlockChain();
                objectOutput.writeObject(blockChain);

                BlockData.getInstance().getBlockchainConsensus((LinkedList<Block>) objectInput.readObject());
                Thread.sleep(2000);
                if (BlockData.getInstance().isExit()) {
                    System.out.println("SSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSS");
                }
            } catch (SocketTimeoutException e) {
                System.out.println("The socket timed out");
                queue.add(queue.poll());
            } catch (IOException e) {
                System.out.println("Client Error: " + e.getMessage() + " -- Error on port: "+ queue.peek());
                queue.add(queue.poll());
            } catch (InterruptedException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }
}
