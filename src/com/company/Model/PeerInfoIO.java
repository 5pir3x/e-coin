package com.company.Model;

import java.io.Serializable;

public class PeerInfoIO extends PeerInfo implements Serializable {

    private Integer inputPort;
    private Integer outputPort;

    public PeerInfoIO(String iP, Integer portNo, Integer inputPort, Integer outputPort) {
        super(iP, portNo);
        this.inputPort = inputPort;
        this.outputPort = outputPort;
    }


    public Integer getInputPort() { return inputPort; }
    public void setInputPort(Integer inputPort) { this.inputPort = inputPort; }

    public Integer getOutputPort() { return outputPort; }
    public void setOutputPort(Integer outputPort) { this.outputPort = outputPort; }
}
