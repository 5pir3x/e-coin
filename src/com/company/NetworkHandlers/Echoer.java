package com.company.NetworkHandlers;


import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;


public class Echoer extends Thread {
    private Socket socket;
    private ServerSocket serverSocket;
    public Echoer(Socket socket, ServerSocket serverSocket) {
        this.socket = socket;
        this.serverSocket = serverSocket;
    }

    @Override
    public void run() {
        while (true) {

            try {
                new EchoerThread(serverSocket.accept()).start();
////            BufferedReader input = new BufferedReader(
////                    new InputStreamReader(socket.getInputStream()));
////            PrintWriter output = new PrintWriter(socket.getOutputStream(), true);
//                ObjectOutputStream objectOutput = new ObjectOutputStream(socket.getOutputStream());
//                ObjectInputStream objectInput = new ObjectInputStream(socket.getInputStream());
//
//                WalletDataNetwork walletDataNetwork = (WalletDataNetwork) objectInput.readObject();
////            wallet = (WalletData) objectInput.readObject();
////                System.out.println("Received client input: " + echoString);
//                System.out.println("Wallet PublicKey = " + walletDataNetwork.getWalletAddress() + " balance= " + walletDataNetwork.getBalance());
////                if(echoString.equals("exit")) {
////                    break;
////                }
////                Thread.sleep(3000);
////                output.println("Object recieved");
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }
}
