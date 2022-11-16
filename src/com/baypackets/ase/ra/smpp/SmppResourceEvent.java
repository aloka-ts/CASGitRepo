/*******************************************************************************
 *   Copyright (c) Agnity, Inc. All rights reserved.
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


/***********************************************************************************
//
//      File:   SmppResourceEvent.java
//
//      Desc: 	This class defines SMPP RA specific events.All Events are 
//				constructed with a reference to the object, the "source",
//				that is logically deemed to be the object upon which the 
//				Event in question initially occurred upon. 
//				This event is independent of application session as
//				SmscSession does not belong to any application session.
//				So in any SmppResourceEvent applcation session is set to
//				'null'.
//
//      Author                          Date            Description
//      ----------------------------------------------------------------------
//      Prashant Kumar              01/02/08        Initial Creation
//
/***********************************************************************************/

package com.baypackets.ase.ra.smpp;

import javax.servlet.sip.SipApplicationSession;

import com.baypackets.ase.resource.ResourceEvent;
import com.baypackets.ase.resource.Message;

public class SmppResourceEvent extends ResourceEvent {
	
	/**
	 *	This resource is fired whenever any SMSC goes down or connection
	 *	with that SMSC is broken.
	 */
	 public static final String SMSC_DOWN_EVENT="SMSC_DOWN_EVENT";

	/**
	 *	This resource is fired whenever any SMSC comes up or connection
	 *	with that SMSC is established.
	 */
	 public static final String SMSC_UP_EVENT="SMSC_UP_EVENT";

	////////////////////////////// Implementation ///////////////////////////////////
	private Smsc data;

	/**
	 *	This constructor creats a new object of SmppResourceEvent with 
	 *	source,type and application session as parameter.
	 *
	 *	@param source -reference to the object upon which the 
	 *					Event in question initially occurred upon. 
	 *	@param type -type of Event.
	 *	@param appSession -<code>SipApplicationSession</code> in which 
	 *						this event occured.
	 */
	public SmppResourceEvent(Object source,
							String type,
							Smsc data,
							SipApplicationSession appSession) {
		super(source, type, appSession);
	}

	/**
	 *	This method associates a data with a SmppResourceEvent.
	 *
	 *	@param errorCode -Error Code to be associated with SmppResourceEvent.
	 */
	public void setData(Smsc eventData) {
		this.data = eventData;
	}

	/**
	 *	This method returns the data associated with this SmppResourceEvent.
	 *
	 *	@return <code>Object</code> -Data associated with this SmppResourceEvent.
	 */
	public Smsc getData() {
		return this.data;
	}
	
	/**
	 *	This method returns the SmppResourceEvent in String form.
	 *
	 *	@return String -String representation of this SmppResourceEvent.
	 */
	public String toString() {
		return new String("SmppResourceEvent:- Type : " + this.getType() +
						",Source :" +this.source +
						", App Session : " + this.getApplicationSession() +
						", Data : " + this.data);
	}
}

