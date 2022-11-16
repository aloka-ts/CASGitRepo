package com.baypackets.ase.ra.smpp.stackif;

import java.util.regex.Pattern;

public interface SmppAppRules {



    public int getTon();

    public void setTon(int ton);

    public void setNpi(int npi);

    public void setRange(String range);

    public int getNpi();

    public String getRange();


}
