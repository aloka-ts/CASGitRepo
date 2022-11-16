/*
 * ReplicationEvent.java
 *
 * Created on Nov 4, 2004
 */
package com.baypackets.ase.spi.replication;

import java.util.EventObject;
import java.util.Collection;

import com.baypackets.ase.util.AseStrings;


/**
 *
 */
public class ReplicationEvent extends EventObject 
{
	
	public static final String INVITE_TRAN_COMPLETE		= "SIP_INVITE_TRAN_COMPLETE";
	public static final String BYE_TRAN_COMPLETE		= "SIP_BYE_TRAN_COMPLETE";
	public static final String INFO_TRAN_COMPLETE		= "SIP_INFO_TRAN_COMPLETE";
	public static final String OPTIONS_TRAN_COMPLETE	= "SIP_OPTIONS_TRAN_COMPLETE";
	public static final String REFER_TRAN_COMPLETE		= "SIP_REFER_TRAN_COMPLETE";
	public static final String NOTIFY_TRAN_COMPLETE		= "SIP_NOTIFY_TRAN_COMPLETE";
	public static final String SUBSCRIBE_TRAN_COMPLETE		= "SIP_SUBSCRIBE_TRAN_COMPLETE";
	public static final String MESSAGE_TRAN_COMPLETE	= "SIP_MESSAGE_TRAN_COMPLETE";
	public static final String PUBLISH_TRAN_COMPLETE	= "SIP_PUBLISH_TRAN_COMPLETE";
	public static final String REGISTER_TRAN_COMPLETE	= "SIP_REGISTER_TRAN_COMPLETE";
	public static final String CLEAN_UP					= "CLEAN_UP";
	public static final String BULK_REPLICATION			= "BULK_REPLICATION";
	public static final String TIMER_CREATION			= "TIMER_CREATION";
	public static final String TIMER_EXPIRY				= "TIMER_EXPIRY";
	public static final String TIMER_CANCELLATION		= "TIMER_CANCELLATION";
	public static final String RESOURCE_REQUEST_SENT	= "RESOURCE_REQUEST_SENT";
	public static final String RESOURCE_RESPONSE_SENT	= "RESOURCE_RESPONSE_SENT";
	public static final String REPLICABLE_CHANGED		= "REPLICABLE_CHANGED";
	private static final String INVITE_REDIRECTION_RESP = "INVITE_REDIRECTION_RESP";
	
    //Added to provide FT handling in case of provisional reliable responses
	public static final String INVITE_TRAN_REL_PROV_RESP = "SIP_INVITE_TRAN_REL_PROV_RESP";

	// Bug 13141
	public static final int TYPE_REGULAR  = 0;
	public static final int TYPE_TIMER	  = 1;
	public static final int TYPE_REINVITE = 2;
	public final static String RES_180			= "180";
	public final static String RES_183			= "183";
	
	public final static String RES_300			= "300";
	public final static String RES_301			= "301";
	public final static String RES_302			= "302";
	public final static String RES_305			= "305";
	public final static String RES_380			= "380";

	
	private String eventId;
	private Collection appNames;
	private String replicationContextId;
	// General attribute to be used optionally while firing an event
	private Object attribute;
	
	//Bug 13141
	private boolean isReinviteTranComplete; 

	/**
	* @param obj
	*/
	public ReplicationEvent(Object source, String eventId) 
	{
		super(source);
		
		isReinviteTranComplete = false;
		
		if(eventId.equals(AseStrings.INVITE)) 
		{
			this.eventId = INVITE_TRAN_COMPLETE;
		} 
		else if (eventId.equals(AseStrings.BYE)) 
		{
			this.eventId = BYE_TRAN_COMPLETE;
		} 
		else if (eventId.equals(AseStrings.INFO)) 
		{
			this.eventId = INFO_TRAN_COMPLETE;
		} 
		else if (eventId.equals(AseStrings.OPTIONS)) 
		{
			this.eventId = OPTIONS_TRAN_COMPLETE;
		} 
		else if (eventId.equals(AseStrings.REFER)) 
		{
			this.eventId = REFER_TRAN_COMPLETE;
		} 
		else if (eventId.equals(AseStrings.NOTIFY)) 
		{
			this.eventId = NOTIFY_TRAN_COMPLETE;
		} 
		else if (eventId.equals(AseStrings.MESSAGE)) 
		{
			this.eventId = MESSAGE_TRAN_COMPLETE;
		} 
		else if (eventId.equals(AseStrings.PUBLISH)) 
		{
			this.eventId = PUBLISH_TRAN_COMPLETE;
		} 
		else if (eventId.equals(AseStrings.REGISTER)) 
		{
			this.eventId = REGISTER_TRAN_COMPLETE;
		} 
		else if (eventId.equals(AseStrings.SUBSCRIBE)) 
		{
			this.eventId = SUBSCRIBE_TRAN_COMPLETE;
		}
		else if (eventId.equals(AseStrings.RESOURCE))
		{
			this.eventId = RESOURCE_REQUEST_SENT;
		} 
		else if (eventId.equals(AseStrings.RESOURCE_REQ))
		{
			this.eventId = RESOURCE_REQUEST_SENT;
		} 
		else if (eventId.equals(AseStrings.RESOURCE_RES))
		{
			this.eventId = RESOURCE_RESPONSE_SENT;
		} 
		else if (eventId.equals(RES_180) || eventId.equals(RES_183))
		{
			this.eventId = INVITE_TRAN_REL_PROV_RESP;
		} 
		else if (eventId.equals(RES_300) || eventId.equals(RES_301)||eventId.equals(RES_302)||eventId.equals(RES_305)||eventId.equals(RES_380))
		{
			this.eventId = INVITE_REDIRECTION_RESP;
		} 
		else 
		{
			this.eventId = eventId;
		}
	}

	public boolean isReinviteTranComplete() {
		return isReinviteTranComplete;
	}

	public void setReinviteTranComplete(boolean isReinviteTranComplete) {
		this.isReinviteTranComplete = isReinviteTranComplete;
	}
	
	/**
	*
	*/
	public String getEventId() 
	{
		return eventId;
	}
	
	/**
	*
	*/
	public String getReplicationContextId() 
	{
		return replicationContextId;
	}
    
	/**
	* Returns the names of the applications for which replication is to be
	* performed.
	*/
	public Collection getAppNames() 
	{
		return appNames;
	}
    
	/**
	* 
	*/
	public void setAppNames(Collection appNames) 
	{
		this.appNames = appNames;
	}
    
	public void setReplicationContextId(String id) 
	{
		replicationContextId = id;
	}

	public void setAttribute(Object attr) 
	{
		this.attribute = attr;
	}

	public Object getAttribute() 
	{
		return this.attribute;
	}
}
