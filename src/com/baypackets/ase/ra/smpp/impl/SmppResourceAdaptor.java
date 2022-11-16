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
//      File:   SmppResourceAdaptor.java
//
//      Desc:   This interface defines an SMPP request. This is the base class for all
//              the SMPP request and all the SMPP request must extend this. This class
//              contains set methods to set various field of an SMPP request and get
//              methods to get various fields of an SMPP request. This interface describes
//              in details all the API available to application.
//
//      Author                          Date            Description
//      ----------------------------------------------------------------------
//      Prashant Kumar              23/01/08        Initial Creation
//
/***********************************************************************************/

package com.baypackets.ase.ra.smpp.impl;

import com.baypackets.ase.resource.ResourceException;
import com.baypackets.ase.ra.smpp.SmppRequest;
import com.baypackets.ase.ra.smpp.SmppResponse;
import com.baypackets.ase.ra.smpp.SmppResourceEvent;

public interface SmppResourceAdaptor {

	/**
	 *	This method is used to deliver any <code>SmppResponse</code> coming from the
	 *	SMSC to application.Underlying stack calls handleEvent(ServerPDUEvent) on 
	 *	StackListener class which is a callback listener registered with stack for 
	 *	a <code>SmscSession</code>.This listener calls this method which inturns 
	 *	deliver the response to application.
	 *
 	 * 	@param resp - <code>SmppResponse</code> object to be delivered to application.
	 *
	 *	@throws ResourceException -If problem in delivering response to application.
	 */
	public void deliverResponse(SmppResponse resp) throws ResourceException;

	/**
	 *	This method is used to deliver any <code>SmppRequest</code> coming from the
	 *	SMSC to application.Underlying stack calls handleEvent(ServerPDUEvent) on 
	 *	StackListener class which is a callback listener registered with stack for 
	 *	a <code>SmscSession</code>.This listener calls this method which inturns 
	 *	deliver the request to application.
	 *
 	 * 	@param resp - <code>SmppRequest</code> object to be delivered to application.
	 *
	 *	@throws ResourceException -If problem in delivering request to application.
	 */
	public void deliverRequest(SmppRequest req) throws ResourceException;

	/**
	 *	This method is used to deliver any <code>SmppResourceEvent</code> to the 
	 *	application.This can be used by SMPP RA or StackListener to notify 
	 *	application about any event.
	 *
 	 * 	@param event - <code>SmppResourceEvent</code> object to be delivered to application.
	 *
	 *	@throws ResourceException -If problem in delivering event to application.
	 */
	public void deliverEvent(SmppResourceEvent event) throws ResourceException;
}

