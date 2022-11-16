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
//      File:   SmppMessageFactory.java
//
//		Desc:	This interface defines Resource adaptor message factory interface for SMPP. It 
//				provides various API to be used by application to create various 
//				smpp messages.
//
//      Author                          Date            Description
//      ----------------------------------------------------------------------
//      Prashant Kumar				24/01/08        Initial Creation
//
/***********************************************************************************/

package com.baypackets.ase.ra.smpp.stackif;

import com.baypackets.ase.resource.ResourceException;
import com.baypackets.ase.spi.resource.ResourceContext;
import com.baypackets.ase.spi.resource.MessageFactory;
import com.baypackets.ase.spi.container.SasMessage;
import com.baypackets.ase.spi.container.SasProtocolSession;

import com.baypackets.ase.ra.smpp.Address;
import com.baypackets.ase.ra.smpp.WrongMultipleDestException;
import com.baypackets.ase.ra.smpp.SmppRequest;

public interface SmppMessageFactory extends MessageFactory {
	
	public SmppRequest createRequest(SasProtocolSession session,Address sourceAddr, Address destAddr)
			throws ResourceException;

	public SmppRequest createRequest(SasProtocolSession session,Address sourceAddr, Address[] destAddr)
			throws ResourceException,WrongMultipleDestException;

	public SmppRequest createDataRequest(SasProtocolSession session,Address sourceAddr, Address destAddr)
			throws ResourceException;

}
