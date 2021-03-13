package com.company.Model;

import java.io.Serializable;

public class PeerInfo implements Serializable, Comparable {

    private String iP;
    private Integer portNo;

    public PeerInfo(String iP, Integer portNo) {
        this.iP = iP;
        this.portNo = portNo;
    }

    public String getiP() { return iP; }
    public void setiP(String iP) { this.iP = iP; }

    public Integer getPortNo() { return portNo; }
    public void setPortNo(Integer portNo) { this.portNo = portNo; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PeerInfo)) return false;

        PeerInfo peerInfo = (PeerInfo) o;

        if (!iP.equals(peerInfo.iP)) return false;
        return portNo.equals(peerInfo.portNo);
    }

    @Override
    public int hashCode() {
        int result = iP.hashCode();
        result = 31 * result + portNo.hashCode();
        return result;
    }

    //We just need to know when the IPs are the same.
    @Override
    public int compareTo(Object o) {
        if (((PeerInfo) o).getiP().equals(this.iP) ) {
            return 0;
        } else {
            return 1;
        }
    }
}
