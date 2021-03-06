package com.company;

import com.company.Model.Wallet;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.security.NoSuchAlgorithmException;
import java.util.Scanner;


public class Main {

    public static void main(String[] args) {
        try (Socket socket = new Socket("127.0.0.1", 6000)) {

            socket.setSoTimeout(5000);
//            BufferedReader echoes = new BufferedReader(
//                    new InputStreamReader(socket.getInputStream()));
//            PrintWriter stringToEcho = new PrintWriter(socket.getOutputStream(), true);

            ObjectOutputStream objectOutput = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream objectInput = new ObjectInputStream(socket.getInputStream());

            Scanner scanner = new Scanner(System.in);
            String echoString;
            String response;

//            do {
            System.out.println("Enter string to be echoed: ");
//                echoString = scanner.nextLine();
            Wallet wallet = new Wallet();
//            WalletDataNetwork wd = new WalletDataNetwork(wallet.getKeyPair(),wallet.getWalletAddress(),wallet.getKeyPair().hashCode());
            objectOutput.writeObject(wallet);

//                if (!echoString.equals("exit")) {
//                    response = echoes.readLine();
//                    System.out.println(response);
//                }
//            } while (!echoString.equals("exit"));
        } catch(SocketTimeoutException e) {
            System.out.println("The socket timed out");
        } catch (IOException e) {
            System.out.println("Client Error: " + e.getMessage());

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }
}
