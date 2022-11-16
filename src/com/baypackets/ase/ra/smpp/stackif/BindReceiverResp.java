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
//      File:   BindReceiverResp.java
//
//		Desc:	To connect to an SMSC as receiver, RA sends BindReceiver request to
//				an SMSC. On response on to the BindReceiver request SMSC sends 
//				BindReceiverResp which is accepted by stack and passed to SMPP RA.
//				Basically this class is a wrapper over underlying stack's BindReceiverResp
//				class. All of the set/get operations are preformed on this underlying
//				stack object.
//
//      Author                          Date            Description
//      ----------------------------------------------------------------------
//      Prashant Kumar				24/01/08        Initial Creation
//
/***********************************************************************************/
package com.baypackets.ase.ra.smpp.stackif;

import org.apache.log4j.Logger;

public class BindReceiverResp extends AbstractBindResponse {


	private static Logger logger =Logger.getLogger(BindReceiverResp.class);

	public BindReceiverResp(org.smpp.pdu.BindResponse stackObj) {
		if(logger.isDebugEnabled()) {
			logger.debug("Inside BindReceiverResp()");
		}
		setStackObject(stackObj);
	}

}
