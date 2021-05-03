package com.company.NetworkDataModel;

import com.company.Model.Block;

import java.io.Serializable;
import java.util.LinkedList;

public class BlockChainNetworkData implements Serializable {
    private LinkedList<Block> currentBlockChain = new LinkedList<>();

    public BlockChainNetworkData(LinkedList<Block> currentBlockChain) {
         this.currentBlockChain = currentBlockChain;
    }

    public LinkedList<Block> getCurrentBlockChain() { return currentBlockChain; }
    public void setCurrentBlockChain(LinkedList<Block> currentBlockChain) { this.currentBlockChain = currentBlockChain; }
}
