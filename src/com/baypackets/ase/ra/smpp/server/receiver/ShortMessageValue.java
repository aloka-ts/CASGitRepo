/*
 * Copyright (c) 1996-2001
 * Logica Mobile Networks Limited
 * All rights reserved.
 *
 * This software is distributed under Logica Open Source License Version 1.0
 * ("Licence Agreement"). You shall use it and distribute only in accordance
 * with the terms of the License Agreement.
 *
 */
package com.baypackets.ase.ra.smpp.server.receiver;

import org.apache.log4j.Logger;
import org.smpp.pdu.SubmitSM;
import org.smpp.util.DataCodingCharsetHandler;
import java.io.UnsupportedEncodingException;

/**
 * Class for storing a subset of attributes of messages to a message store.
 *
 * @author Bahul Malik
 *
 */
class ShortMessageValue {
	private static Logger logger = Logger.getLogger(ShortMessageValue.class);
	String systemId;
	String serviceType;
	String sourceAddr;
	String destinationAddr;
	String shortMessage;

	/**
	 * Constructor for building the object from <code>SubmitSM</code>
	 * PDU.
	 *
	 * @param systemId system id of the client
	 * @param submit the PDU send from the client
	 */
	ShortMessageValue(String systemId, SubmitSM submit) throws UnsupportedEncodingException {
		this.systemId = systemId;
		serviceType = submit.getServiceType();
		sourceAddr = submit.getSourceAddr().getAddress();
		destinationAddr = submit.getDestAddr().getAddress();
        logger.debug("Encoding value "+ submit.getDataCoding());
		logger.debug("Sending messags to network" +submit);
		shortMessage = submit.getShortMessage();
	}
}
