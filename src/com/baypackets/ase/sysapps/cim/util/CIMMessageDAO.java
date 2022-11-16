package com.baypackets.ase.sysapps.cim.util;


public class CIMMessageDAO {
	
	public enum Type{
		SENDER,RECEIVER
	}

	public CIMMessageDAO() {
		super();
		// TODO Auto-generated constructor stub
	}

	public CIMMessageDAO(Type objectType,String id, String user, String phoneNumber,
			String activityType, String message, String network) {
		super();
		this.objectType=objectType;
		this.id = id;
		this.user = user;
		this.phoneNumber = phoneNumber;
		this.activityType = activityType;
		this.message = message;
		this.network = network;
	}
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getUser() {
		return user;
	}
	public void setUser(String user) {
		this.user = user;
	}
	public String getPhoneNumber() {
		return phoneNumber;
	}
	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}
	public String getActivityType() {
		return activityType;
	}
	public void setActivityType(String type) {
		this.activityType = type;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public String getNetwork() {
		return network;
	}
	public void setNetwork(String network) {
		this.network = network;
	}
	
	public Type getObjectType() {
		return objectType;
	}

	private Type objectType;
	private String id; 
	private String user;
	private String phoneNumber;
	private String activityType; 
	private String message;
	private String network;
	
}
