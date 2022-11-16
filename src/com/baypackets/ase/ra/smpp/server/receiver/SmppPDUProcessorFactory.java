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
import org.smpp.debug.FileLog;

import com.baypackets.ase.ra.smpp.impl.SmppResourceAdaptorImpl;
import com.baypackets.ase.ra.smpp.impl.SmppResourceFactoryImpl;
import com.baypackets.ase.ra.smpp.server.receiver.util.Table;

/**
 * Class <code>SimulatorPDUProcessorFactory</code> creates new instances of
 * a <code>SimulatorPDUProcessor</code>. It's passed to <code>SMSCListener</code>
 * which uses it to create new PDU processors whenewer new connection
 * from client is requested. The PDU processor is passed to
 * instance of <code>SMSCSession</code> which uses the processor to handle
 * client requests and responses.
 * 
 * @see PDUProcessorFactory
 * @see PDUProcessorGroup
 * @see SmppPDUProcessor
 * @author Bahul Malik
 */
public class SmppPDUProcessorFactory implements PDUProcessorFactory {
	private static Logger logger = Logger.getLogger(SmppPDUProcessorFactory.class);
	private PDUProcessorGroup procGroup;
	private ShortMessageStore messageStore;
	private DeliveryInfoSender deliveryInfoSender;
	private Table users;
	private SmppResourceFactoryImpl smppResourceFactory ;
    private  SmppResourceAdaptorImpl smppResourceAdaptorImpl;
    private String sendEnquireLink;
	/**
	 * If the information about processing has to be printed
	 * to the standard output.
	 */
	private boolean displayInfo = false;

	/**
	 * Constructs processor factory with given processor group, 
	 * message store for storing of the messages and a table of
	 * users for authentication. The message store and users parameters are
	 * passed to generated instancies of <code>SimulatorPDUProcessor</code>.
	 * @param procGroup the group the newly generated PDU processors will belong to
	 * @param messageStore the store for messages received from the client
	 * @param users the list of users used for authenticating of the client
	 * @param smppResourceAdaptorImpl 
	 */
	public SmppPDUProcessorFactory(
		PDUProcessorGroup procGroup,
		ShortMessageStore messageStore,
		DeliveryInfoSender deliveryInfoSender,
		Table users,SmppResourceFactoryImpl smppResourceFactory,
		SmppResourceAdaptorImpl smppResourceAdaptorImpl,
		String sendEnquireLink) {
		this.procGroup = procGroup;
		this.messageStore = messageStore;
		this.deliveryInfoSender = deliveryInfoSender;
		this.users = users;
		this.smppResourceFactory=smppResourceFactory;
		this.smppResourceAdaptorImpl=smppResourceAdaptorImpl;
		this.sendEnquireLink=sendEnquireLink;
	}


	/**
	 * Creates a new instance of <code>SimulatorPDUProcessor</code> with
	 * parameters provided in construction of th factory.
	 *
	 * @param session the sessin the PDU processor will work for
	 * @return newly created <code>SimulatorPDUProcessor</code>
	 */
	public PDUProcessor createPDUProcessor(SMSCSession session) {
		SmppPDUProcessor simPDUProcessor = new SmppPDUProcessor(session, messageStore, users,smppResourceFactory,smppResourceAdaptorImpl,sendEnquireLink);
		simPDUProcessor.setDisplayInfo(getDisplayInfo());
		simPDUProcessor.setGroup(procGroup);
		simPDUProcessor.setDeliveryInfoSender(deliveryInfoSender);
		display("new connection accepted");
		return simPDUProcessor;
	}

	/**
	 * Sets if the info about processing has to be printed on
	 * the standard output.
	 */
	public void setDisplayInfo(boolean on) {
		displayInfo = on;
	}

	/**
	 * Returns status of printing of processing info on the standard output.
	 */
	public boolean getDisplayInfo() {
		return displayInfo;
	}

	private void display(String info) {
		logger.debug(info);
		if (getDisplayInfo()) {
			logger.debug(FileLog.getLineTimeStamp() + " [sys] " + info);
		}
	}
}
