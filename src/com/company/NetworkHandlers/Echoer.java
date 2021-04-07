package com.company.NetworkHandlers;


import java.io.IOException;
import java.net.ServerSocket;


public class Echoer extends Thread {

    private ServerSocket serverSocket;
    public Echoer(ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
    }

    @Override
    public void run() {
        while (true) {
            try {
                new EchoerThread(serverSocket.accept()).start();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }
}
