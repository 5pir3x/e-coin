package com.company.NetworkDataModel;

import com.company.Model.Block;

import java.io.Serializable;
import java.util.LinkedList;

public class BlockChainNetworkData implements Serializable {
    private LinkedList<BlockNetworkData> currentBlockChain = new LinkedList<>();

    public BlockChainNetworkData(LinkedList<Block> currentBlockChain) {
        for (Block block : currentBlockChain) {
            BlockNetworkData bdn =  new BlockNetworkData(block.getPrevHash(),block.getCurrHash(),block.getTimeStamp(),block.getMinedBy(),block.getLedgerId(),block.getMiningPoints(),block.getLuck(),block.getTransactionLedger());
            this.currentBlockChain.add(bdn);
        }
    }

    public LinkedList<BlockNetworkData> getCurrentBlockChain() { return currentBlockChain; }
    public void setCurrentBlockChain(LinkedList<BlockNetworkData> currentBlockChain) { this.currentBlockChain = currentBlockChain; }
}
