package com.baypackets.ase.ra.smpp.stackif;

public class Rule {

	public String Range = null;
	public String AppName = null;
	public Integer ton;
	public Integer npi;

	

	public String getRange() {
		return Range;
	}



	public void setRange(String range) {
		Range = range;
	}



	public String getAppName() {
		return AppName;
	}



	public void setAppName(String appName) {
		AppName = appName;
	}



	public Integer getTon() {
		return ton;
	}



	public void setTon(int ton) {
		this.ton = Integer.valueOf(ton);
	}



	public Integer getNpi() {
		return npi;
	}



	public void setNpi(int npi) {
		this.npi = Integer.valueOf(npi);
	}



	@Override
	public String toString() {
		return "Rule [Range=" + Range + ", AppName=" + AppName + ", ton=" + ton + ", npi=" + npi + "]";
	}
}
