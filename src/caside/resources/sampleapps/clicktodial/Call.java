package com.baypackets.clicktodial.util;
 
import java.io.Serializable;
import java.util.Date;

/*******************************************************************************
 *   Copyright (c) 2014 Agnity, Inc. All rights reserved.
 *   
 *   This is proprietary source code of Agnity, Inc. 
 *   
 *   Agnity, Inc. retains all intellectual property rights associated 
 *   with this source code. Use is subject to license terms.
 *   
 *   This source code contains trade secrets owned by Agnity, Inc.
 *   Confidentiality of this computer program must be maintained at 
 *   all times, unless explicitly authorized by Agnity, Inc.
 *******************************************************************************/
public class Call
implements Serializable
{
	//The answer is 42!
	private static final long serialVersionUID = 42L;
	
	private String callID;
	private Date callStartTime;
	private Date callEndTime;
 
	public String getCallID()
	{
		return this.callID;
	}
 
	public void setCallID(String callID)
	{
		this.callID = callID;
	}
 
	public Date getCallStartTime()
	{
		return this.callStartTime;
   }
 
	public void setCallStartTime(Date callStartTime)
	{
		this.callStartTime = callStartTime;
	}
 
	public Date getCallEndTime()
	{
		return this.callEndTime;
	}
 
	public void setCallEndTime(Date callEndTime)
	{
		this.callEndTime = callEndTime;
	}
}

 