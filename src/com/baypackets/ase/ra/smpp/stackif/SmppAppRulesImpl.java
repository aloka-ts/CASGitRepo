package com.baypackets.ase.ra.smpp.stackif;

import java.util.regex.Pattern;

public class SmppAppRulesImpl {

    private int ton;
    private int npi;
    private String range;
    private org.smpp.pdu.AddressRange stackObj;
    private Pattern pattern;



    public SmppAppRulesImpl(String range){
        this.range = range;
    }

    public SmppAppRulesImpl(int ton, int npi, String range){

        this.ton=ton;
        this.npi=npi;
        this.range=range;

    }


    public void setTon(int ton) {

    }

    public void setNpi(int npi) {

    }


    public void setRange(String range) {

    }


    public int getTon() {
        return 0;
    }


    public int getNpi() {
        return 0;
    }


    public String getRange() {
        return null;
    }
}
