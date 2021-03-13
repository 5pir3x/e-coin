package com.company.Model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.TreeSet;

public class PeerContextData implements Serializable {

    private String name;
    private Integer port;
    private static TreeSet<PeerInfoIO> validPeers = new TreeSet<>();
    private ArrayList<PeerInfoIO> connectedPeers = new ArrayList<>();
    private static LinkedList<Block> currentBlockChain = new LinkedList<>();

    private static PeerContextData instance = new PeerContextData(); //for singleton class
    public static PeerContextData getInstance(){
        return instance;
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

    public static LinkedList<Block> getCurrentBlockChain() { return currentBlockChain; }
    public static void setCurrentBlockChain(LinkedList<Block> currentBlockChain) {
        PeerContextData.currentBlockChain = currentBlockChain;
    }
}
