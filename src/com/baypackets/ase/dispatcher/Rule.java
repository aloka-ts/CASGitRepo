package com.baypackets.ase.dispatcher;
import java.util.ArrayList;
import java.io.Serializable;

import com.baypackets.ase.spi.container.SasMessage;

public abstract class Rule implements Serializable {
	
	 private static final long serialVersionUID = 3090242548897014L;
	 protected String ruleName;
	 protected String appName;
	 protected String servletName;
	 
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

	 public void setServletName (String servletName){
		 this.servletName = servletName;
	 }

	 public String getServletName () {
		 return this.servletName;
	 }
	
	 public abstract boolean evaluate(String[] input , ArrayList list) ;

	 public abstract String[] getInputData(SasMessage message); 
	
	 public abstract ArrayList getInputParameterData (SasMessage message);
}
