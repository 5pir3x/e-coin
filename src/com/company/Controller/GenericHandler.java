package com.company.Controller;

import com.company.Model.PeerContextData;
import com.company.Model.PeerInfoIO;

import java.util.TreeSet;

public class GenericHandler {

    public static TreeSet<PeerInfoIO> getValidPeers() {
        return PeerContextData.getValidPeers();
    }
    public static void setValidPeers(TreeSet<PeerInfoIO> validPeers) {
        PeerContextData.setValidPeers(validPeers);
    }

//    public static LinkedList<Block> getLatestBlockChain() {
//        return PeerContextData.getCurrentBlockChain();
//    }
//    public static void  setLatestBlockChain(LinkedList<Block> latestBlockChain) {
//        PeerContextData.setCurrentBlockChain(latestBlockChain);
//    }

}
