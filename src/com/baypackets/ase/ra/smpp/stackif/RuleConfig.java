package com.baypackets.ase.ra.smpp.stackif;

import java.util.*;

import com.baypackets.ase.ra.smpp.server.receiver.util.Table;

public class RuleConfig {

	private String ipAddress;
	private Integer portNumber;
	private String disableClient;
	private Integer commsTimeout;
	private Integer receiverTimeout;
	private String sendEnquireLink;
	
	private Table users;

	public Table getUsers() {
		return users;
	}

	public void setUsers(Table users) {
		this.users = users;
	}

	public Integer getReceiverTimeout() {
		return receiverTimeout;
	}

	public void setReceiverTimeout(Integer receiverTimeout) {
		this.receiverTimeout = receiverTimeout;
	}

	public Integer getCommsTimeout() {
		return commsTimeout;
	}

	public void setCommsTimeout(Integer commsTimeout) {
		this.commsTimeout = commsTimeout;
	}

	public String getIpAddress() {
		return ipAddress;
	}

	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}

	public Integer getPortNumber() {
		return portNumber;
	}

	public void setPortNumber(Integer portNumber) {
		this.portNumber = portNumber;
	}

	public List<Rule> rules;

	public RuleConfig() {

	}

	public List<Rule> getRules() {
		return rules;
	}

	public void setRules(List<Rule> rules) {
		this.rules = rules;
	}

	public String getSendEnquireLink() {
		return sendEnquireLink;
	}

	public void setSendEnquireLink(String sendEnquireLink) {
		this.sendEnquireLink = sendEnquireLink;
	}

	public String getDisableClient() {
		return disableClient;
	}

	public void setDisableClient(String disableClient) {
		this.disableClient = disableClient;
	}

	@Override
	public String toString() {
		return "RuleConfig [ipAddress=" + ipAddress + ", portNumber=" + portNumber + ", disableClient=" + disableClient
				+ ", commsTimeout=" + commsTimeout + ", receiverTimeout=" + receiverTimeout + ", sendEnquireLink="
				+ sendEnquireLink + ", users=" + users + ", rules=" + rules + "]";
	}

}
