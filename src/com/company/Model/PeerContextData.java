package com.company.Model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.Serializable;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.TreeSet;

public class PeerContextData implements Serializable {

    private String name;
    private Integer port;
    private static TreeSet<PeerInfoIO> validPeers = new TreeSet<>();
    private ArrayList<PeerInfoIO> connectedPeers = new ArrayList<>();
//    private static LinkedList<Block> currentBlockChain = new LinkedList<>();
    private ObservableList<Transaction> newBlockTransactions;
    private LinkedList<Block> currentBlockChain = new LinkedList<>();

    //singleton class
    private static PeerContextData instance;
    static {
        try {
            instance = new PeerContextData();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }
    public static PeerContextData getInstance(){
        return instance;
    }
    public PeerContextData() throws NoSuchAlgorithmException {
        newBlockTransactions = FXCollections.observableArrayList();
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Integer getPort() { return port; }
    public void setPort(Integer port) { this.port = port; }

    public static TreeSet<PeerInfoIO> getValidPeers() { return validPeers; }
    public static void setValidPeers(TreeSet<PeerInfoIO> validPeers) {
        PeerContextData.validPeers = validPeers;
    }

    public ArrayList<PeerInfoIO> getConnectedPeers() { return connectedPeers; }
    public void setConnectedPeers(ArrayList<PeerInfoIO> connectedPeers) {
        this.connectedPeers = connectedPeers;
    }

    public ObservableList<Transaction> getNewBlockTransactions() { return newBlockTransactions; }
    public void setNewBlockTransactions(ObservableList<Transaction> newBlockTransactions) { this.newBlockTransactions = newBlockTransactions; }

//    public static LinkedList<Block> getCurrentBlockChain() {
//        return currentBlockChain;
//    }
//
//    public static void setCurrentBlockChain(LinkedList<Block> currentBlockChain) {
//        this.currentBlockChain = currentBlockChain;
//    }
}
