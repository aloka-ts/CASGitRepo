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
//      File:   SmppSessionFactory.java
//
//      Desc:   This class is a factory to create new SmppSession.
//
//      Author                          Date            Description
//      ----------------------------------------------------------------------
//      Prashant Kumar              01/02/08        Initial Creation
//
/***********************************************************************************/

package com.baypackets.ase.ra.smpp.impl;

import org.apache.log4j.Logger;

import com.baypackets.ase.resource.ResourceException;
import com.baypackets.ase.spi.container.SasProtocolSession;
import com.baypackets.ase.spi.resource.ResourceContext;
import com.baypackets.ase.spi.resource.SessionFactory;
import com.baypackets.ase.util.UIDGenerator;

public class SmppSessionFactory implements SessionFactory {
	
	private static Logger logger = Logger.getLogger(SmppSessionFactory.class);

	private ResourceContext context;

	/**
	 *	This method initializes Session factory.
	 *
	 *	@param context -<code>ResourceContext</code> of the SMPP RA.
	 *
	 *	@throws ResourceException -If problem in initializing the session 
	 *								factory.
	 */
	public void init(ResourceContext context) throws ResourceException {
		this.context = context;

	}

	/**
	 *	This method creates a new <code>SmppSession</code> object and return it.
	 *
	 *	@returns <code>SasProtocolSession</code> -newly creates session object.
	 *
	 *	@throws ResourceException -Incase there is any problem in creating new 
	 *								<code>SmppSession</code>.
	 */
	public SasProtocolSession createSession() throws ResourceException {
		//String adaptorClassName=context.getAdaptorClassName();
		
		String protocolName = this.context.getProtocol();
		String id=protocolName+"_"+UIDGenerator.getInstance().get128BitUuid();
		logger.debug("Inside  SmppSessionFactory createSession .........id is " +id);
		return new SmppSession(id);
	}
}
