package com.genband.m5.maps.common;

public class SS_Constants {

	public enum UserStatus {
		ACTIVE, INACTIVE, UNKNOWN
    }
 
    public enum ContactType {
    	EMAIL, PAGER, PHONE, MOBILE, FAX, AIM, YAHOO, GTALK, SKYPE
    }

    public enum ReturnMessage {
    	SUCCESS, FAILURE, PROVERROR
    }
    
    public enum OperationType {
    	CREATE, MODIFY, VIEW, LIST, DELETE, CREATE_LIST, MODIFY_LIST, DELETE_LIST
    }
    
    public static final String PAGEFLOW_NEXT = "NEXT";
    public static final String PAGEFLOW_PREVIOUS = "PREVIOUS";
    public static final String PAGEFLOW_FIRST = "FIRST";
    public static final String PAGEFLOW_LAST = "LAST";
}
