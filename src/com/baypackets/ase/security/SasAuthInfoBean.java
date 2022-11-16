package com.baypackets.ase.security;

public class SasAuthInfoBean {
	//Bug ID : 5638
	private String userName;
	private String password;
	private int statusCode;
	private String realm;
	
	public SasAuthInfoBean(int statusCode,String realm, String userName, String password ) {
		this.statusCode = statusCode;
		this.userName = userName;
		this.password = password;
		this.realm = realm;
	}
	
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public int getStatusCode() {
		return statusCode;
	}
	public void setStatusCode(int statusCode) {
		this.statusCode = statusCode;
	}
	public String getRealm() {
		return realm;
	}
	public void setRealm(String realm) {
		this.realm = realm;
	}
		
}