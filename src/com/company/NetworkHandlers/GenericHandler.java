package com.company.NetworkHandlers;

import com.company.Model.Block;
import com.company.Model.PeerContextData;
import com.company.Model.PeerInfoIO;
import com.company.ServiceData.BlockData;

import java.util.LinkedList;
import java.util.TreeSet;

public class GenericHandler {

    public static TreeSet<PeerInfoIO> getValidPeers() {
        return PeerContextData.getInstance().getValidPeers();
    }
    public static void setValidPeers(TreeSet<PeerInfoIO> validPeers) {
        PeerContextData.getInstance().setValidPeers(validPeers);
    }

    public static LinkedList<Block> getLatestBlockChain() {
        return BlockData.getInstance().getCurrentBlockChain();
    }
//    public static void  setLatestBlockChain(LinkedList<Block> latestBlockChain) {
//        PeerContextData.setCurrentBlockChain(latestBlockChain);
//    }

}
