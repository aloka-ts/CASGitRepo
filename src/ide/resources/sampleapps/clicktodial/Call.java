/*
 * Call.java
 *
 * Created on September 16, 2004, 12:29 PM
 */
package com.baypackets.clicktodial.util;

import java.util.Date;


/**
 * Encapsulates call state information.
 */
public class Call implements java.io.Serializable {
    
    private String callID;
    private Date callStartTime;
    private Date callEndTime;
    
    /**
     *
     */
    public String getCallID() {
        return callID;
    }
    
    /**
     *
     */
    public void setCallID(String callID) {
        this.callID = callID;
    }
    
    /**
     *
     */
    public Date getCallStartTime() {
        return callStartTime;
    }
    
    /**
     *
     */
    public void setCallStartTime(Date callStartTime) {
        this.callStartTime = callStartTime;
    }
    
    /**
     *
     */
    public Date getCallEndTime() {
        return callEndTime;
    }
    
    /**
     *
     */
    public void setCallEndTime(Date callEndTime) {
        this.callEndTime = callEndTime;
    }
    
}
