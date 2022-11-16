package com.genband.jain.protocol.ss7.tcap;

import java.util.ArrayList;
import java.util.Date;
import java.util.StringTokenizer;

import org.apache.log4j.Logger;

public class Sas {
	
	private static Logger logger = Logger.getLogger(Sas.class);
	private static final String DELIM = ",";
	private static final String SEPARATOR =":";
	private static final Short DEFAULT_PORT = 5060; 
	
	public static final short STATUS_UP = 1;
	public static final short STATUS_DOWN = 2;
	
	private String id;
	private String host;
	private short port;
	
	
	public String getId() {
		return id;
	}
	public String getHost() {
		return host;
	}
	public short getPort() {
		return port;
	}
	
	
	void setId(String id) {
		this.id = id;
	}

	void setHost(String host) {
		this.host = host;
	}
	
	void setPort(short port) {
		this.port = port;
	}
	
	
	
	public String toString(){
		StringBuffer buffer = new StringBuffer();
		buffer.append("Sas [Host=");
		buffer.append(host);
		buffer.append(",port=");
		buffer.append(port);				
		buffer.append("]");
		return buffer.toString();
	}
	
	public static ArrayList<Sas> parseAll(String config){
		ArrayList<Sas> sasList = new ArrayList<Sas>();
		config = (config == null) ?"" :config.trim();
		StringTokenizer tokenizer = new StringTokenizer(config, DELIM);
		for(;tokenizer.hasMoreTokens();){
			Sas sas = parse(tokenizer.nextToken());
			sasList.add(sas);
		}
		return  sasList;
	}
	
	public static Sas parse(String config){
		Sas sas = null;
		config = (config == null) ?"" :config.trim();
		StringTokenizer tokenizer = new StringTokenizer(config, SEPARATOR);
		String host = tokenizer.hasMoreTokens() ? tokenizer.nextToken() : "";
		if(host.trim().equals(""))
			return sas;
		
		String strPort = tokenizer.hasMoreTokens() ? tokenizer.nextToken() : "";
		short port = -1;
		try{
			port = Short.parseShort(strPort);
		}catch(NumberFormatException nfe){}
		port = (port != -1) ? port : DEFAULT_PORT;
		
		sas = new Sas();
		sas.setId(host+SEPARATOR+port);
		sas.setHost(host);
		sas.setPort(port);
		
		return sas;
	}
}
