package com.baypackets.ase.spi.radius;

import java.util.HashMap;
import java.util.Map;

public class RadiusSession {

    private String username;
    private String password;
    private boolean isAuthenticated;
    public Map<String, String> vendorAttributeMap = new HashMap<String, String>();

    public int maxConcurrentSessionAllowed;


    public static final int ACCESS = 0;
    public static final int ACCOUNTING_START = 1;
    public static final int ACCOUNTING_STOP = 2;

    public int requestType;


    public String getUsername() {
	return username;
    }

    public void setUsername(String username) {
	this.username = username;
    }

    public String getPassword() {
	return password;
    }

    public void setPassword(String password) {
	this.password = password;
    }



    public boolean isAuthenticated() {
	return isAuthenticated;
    }


    public void setAuthenticated(boolean isAuthenticated) {
	this.isAuthenticated = isAuthenticated;
    }

    public int getMaxConcurrentSessionAllowed() {
	return maxConcurrentSessionAllowed;
    }

    public void setMaxConcurrentSessionAllowed(int maxConcurrentSessionAllowed) {
	this.maxConcurrentSessionAllowed = maxConcurrentSessionAllowed;
    }

    public void setVendorAttribute(String name, String value) {
	this.vendorAttributeMap.put(name, value);
    }

    public String getVendorAttribute(String name) {
	return this.vendorAttributeMap.get(name);
    }

    public Map<String, String> getVendorAttributes() {
	return this.vendorAttributeMap;
    }

    public int getRequestType() {
	return requestType;
    }

    public void setRequestType(int requestType) {
	this.requestType = requestType;
    }

    @Override
    public String toString() {
	return "RadiusSession [isAuthenticated=" + isAuthenticated
		+ ", maxConcurrentSessionAllowed="
		+ maxConcurrentSessionAllowed + ", password=" + password
		+ ", requestType=" + requestType + ", username=" + username
		+ ", vendorAttributeMap=" + vendorAttributeMap + "]";
    }

}