package com.baypackets.ase.ra.ro.impl;

import java.util.List;

import javax.servlet.sip.SipApplicationSession;
import javax.servlet.sip.URI;
import javax.servlet.sip.SipURI;

import org.apache.log4j.Logger;

import com.baypackets.ase.resource.Request;
import com.baypackets.ase.resource.ResourceException;
import com.baypackets.ase.resource.ResourceSession;

import com.baypackets.ase.spi.resource.MessageFactory;
import com.baypackets.ase.spi.resource.SessionFactory;
import com.baypackets.ase.spi.container.SasApplicationSession;
import com.baypackets.ase.spi.container.SasMessage;
import com.baypackets.ase.spi.container.SasProtocolSession;
import com.baypackets.ase.spi.resource.ResourceContext;

import com.baypackets.ase.ra.ro.*;
import com.baypackets.ase.ra.ro.stackif.*;

public class RoResourceFactoryImpl implements RoResourceFactory, Constants {
	
	private static Logger logger = Logger.getLogger(RoResourceFactoryImpl.class);
	private ResourceContext context;
	private MessageFactory msgFactory;
	private SessionFactory sessionFactory;
	
	public void init(ResourceContext context) {
		logger.debug("init() is called.");

		this.context = context;
		if (context != null) {
			logger.debug("init(): get message factory and session factory from context.");
			this.msgFactory = context.getMessageFactory();
			if (this.msgFactory == null) {
				logger.error("init(): null message factory.");
			}
			this.sessionFactory = (SessionFactory)context.getSessionFactory();
			if (this.sessionFactory== null) {
				logger.error("init(): null session factory.");
			}
		} else {
			logger.error("init(): null context received");
		}
	}

	public ResourceSession createSession(SipApplicationSession appSession) throws ResourceException {
		if(appSession == null) {
			throw new IllegalArgumentException("Application Session cannot be NULL ");
		}
		SasProtocolSession session = sessionFactory.createSession();
		((SasApplicationSession)appSession).addProtocolSession(session);
		
		return (ResourceSession)session;
	}
	
	public RoRequest createRequest(SipApplicationSession appSession, int type) throws ResourceException {
		SasProtocolSession session = null;
		if (appSession != null) {
			session = sessionFactory.createSession();
			((SasApplicationSession)appSession).addProtocolSession(session);
		}

		logger.info("Creating Request");
		SasMessage message = msgFactory.createRequest(session, type);
		message.setInitial(true);
		return (RoRequest)message;
	}

	public Request createRequest(int type) throws ResourceException {
		return this.createRequest(null, type);
	}

	public CCRMultipleServicesCreditControl createCCRMultipleServicesCreditControl() {
		return new CCRMultipleServicesCreditControlImpl();
	}

	public RequestedServiceUnit createRequestedServiceUnit(	long ccTime,
															long totalOctets,
															long inputOctets,
															long outputOctets,
															long servUnits) {
		return new RequestedServiceUnitImpl(	ccTime,
												totalOctets,
												inputOctets,
												outputOctets,
												servUnits);
	}

	public UsedServiceUnit createUsedServiceUnit(	short reportReason,
													short tariffChangeUsage,
													long ccTime,
													long totalOctets,
													long inputOctets,
													long outputOctets,
													long servUnits) {
		return new UsedServiceUnitImpl(	reportReason,
										tariffChangeUsage,
										ccTime,
										totalOctets,
										inputOctets,
										outputOctets,
										servUnits);
	}

	public SubscriptionId createSubscriptionId(short subsIdType, String subsIdData) {
		return new SubscriptionIdImpl(subsIdType, subsIdData);
	}

	public ServiceInformation createServiceInformation(IMSInformation imsInfo) {
		return new ServiceInformationImpl(imsInfo);
	}

	public IMSInformation createIMSInformation() {
		return new IMSInformationImpl();
	}

	public TimeStamps createTimeStamps(String reqTimestamp, String resTimestamp) {
		return new TimeStampsImpl(reqTimestamp, resTimestamp);
	}

	public InterOperatorIdentifier createInterOperatorIdentifier(	String origIOI,
																	String termIOI) {
		return new InterOperatorIdentifierImpl(origIOI, termIOI);
	}

	public TrunkGroupId createTrunkGroupId(String incomingTGId, String outgoingTGId) {
		return new TrunkGroupIdImpl(incomingTGId, outgoingTGId);
	}

	public EventType createEventType(String sipMethod, String event, int expires) {
		return new EventTypeImpl(sipMethod, event, expires);
	}

	public MessageBody createMessageBody(	String msgType,
											String msgLength,
											String msgDisposition,
											short originator) {
		return new MessageBodyImpl(msgType, msgLength, msgDisposition, originator);
	}

	public ApplicationServerInformation createApplicationServerInformation(
												String appServer,
												List appProvidedCdPtyAddresses) {
		return new ApplicationServerInformationImpl(appServer, appProvidedCdPtyAddresses);
	}

	public SDPMediaComponent createSDPMediaComponent(	String sdpMediaName,
														short mediaInitFlag,
														String qos,
														String gprsChargingId,
														List sdpMediaDescs) {
		return new SDPMediaComponentImpl(	sdpMediaName,
											mediaInitFlag,
											qos,
											gprsChargingId,
											sdpMediaDescs);
	}

	public ServerCapabilities createServerCapabilities() {
		return new ServerCapabilitiesImpl();
	}

	public RedirectServer createRedirectServer(short addrType, String address) {
		return new RedirectServerImpl(addrType, address);
	}
}
