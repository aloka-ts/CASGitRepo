package com.genband.jain.protocol.ss7.tcap;

import java.util.ArrayList;
import java.util.Date;
import java.util.StringTokenizer;

import org.apache.log4j.Logger;

public class INGateway {

	private static Logger logger = Logger.getLogger(INGateway.class);
	private static final String DELIM = ",";
	private static final String SEPARATOR =":";
	private static final Short DEFAULT_PORT = 5060; 
	
	public static final short STATUS_UP = 1;
	public static final short STATUS_DOWN = 2;
	
	private String id;
	private String host;
	private short port;
	private short status;
	private long lastUpdateTime;
	
	public String getId() {
		return id;
	}
	public String getHost() {
		return host;
	}
	public short getPort() {
		return port;
	}
	public short getStatus() {
		return status;
	}
	public long getLastUpdateTime() {
		return lastUpdateTime;
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
	
	void setStatus(short status) {
		this.status = status;
	}
	
	void setLastUpdateTime(long lastUpdateTime) {
		this.lastUpdateTime = lastUpdateTime;
	}
	
	public String toString(){
		StringBuffer buffer = new StringBuffer();
		buffer.append("INGateway [Host=");
		buffer.append(host);
		buffer.append(",port=");
		buffer.append(port);
		buffer.append(",status=");
		buffer.append(status==STATUS_UP ? "UP" : 
						status == STATUS_DOWN ? "DOWN" : "UNKNOWN");
		buffer.append(",LastUpdateTime=");
		buffer.append(new Date(lastUpdateTime));
		buffer.append("]");
		return buffer.toString();
	}

	public static ArrayList<INGateway> parseAll(String config){
		ArrayList<INGateway> gws = new ArrayList<INGateway>();
		config = (config == null) ?"" :config.trim();
		StringTokenizer tokenizer = new StringTokenizer(config, DELIM);
		for(;tokenizer.hasMoreTokens();){
			INGateway gw = parse(tokenizer.nextToken());
			gws.add(gw);
		}
		return  gws;
	}
	
	public static INGateway parse(String config){
		INGateway gw = null;
		config = (config == null) ?"" :config.trim();
		StringTokenizer tokenizer = new StringTokenizer(config, SEPARATOR);
		String host = tokenizer.hasMoreTokens() ? tokenizer.nextToken() : "";
		if(host.trim().equals(""))
			return gw;
		
		String strPort = tokenizer.hasMoreTokens() ? tokenizer.nextToken() : "";
		short port = -1;
		try{
			port = Short.parseShort(strPort);
		}catch(NumberFormatException nfe){}
		port = (port != -1) ? port : DEFAULT_PORT;
		
		gw = new INGateway();
		gw.setId(host+SEPARATOR+port);
		gw.setHost(host);
		gw.setPort(port);
		gw.setLastUpdateTime(System.currentTimeMillis());
		return gw;
	}

}
