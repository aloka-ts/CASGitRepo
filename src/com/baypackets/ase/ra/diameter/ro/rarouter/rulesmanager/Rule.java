package com.baypackets.ase.ra.diameter.ro.rarouter.rulesmanager;

import java.io.Serializable;
import java.util.ArrayList;

import com.baypackets.ase.ra.diameter.ro.RoRequest;


public abstract class Rule implements Serializable {
	
	 protected String ruleName;
	 protected String appName;
	 
	 public void setName (String ruleName){
		 this.ruleName = ruleName;
	 }

	 public String getName () {
		 return this.ruleName;
	 }

	 public void setAppName (String appName) {
		 this.appName = appName;
	 }

	 public String getAppName () {
		 return this.appName;
	 }

	 public abstract boolean evaluate(String[] input , ArrayList list) ;

	 public abstract String[] getInputData(RoRequest request) ; 
	
}
