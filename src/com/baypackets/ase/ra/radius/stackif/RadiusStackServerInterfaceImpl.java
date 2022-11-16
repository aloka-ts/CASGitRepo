package com.baypackets.ase.ra.radius.stackif;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.util.Properties;

import javax.servlet.sip.SipApplicationSession;

import org.apache.log4j.Logger;
import org.tinyradius.packet.AccessRequest;
import org.tinyradius.packet.AccountingRequest;
import org.tinyradius.packet.RadiusPacket;

import com.baypackets.ase.ra.radius.RadiusRequest;
import com.baypackets.ase.ra.radius.RadiusResourceAdaptor;
import com.baypackets.ase.ra.radius.RadiusResourceEvent;
import com.baypackets.ase.ra.radius.RadiusResourceException;
import com.baypackets.ase.ra.radius.RadiusResourceFactory;
import com.baypackets.ase.ra.radius.RadiusResponse;
import com.baypackets.ase.ra.radius.RadiusStackServerInterface;
import com.baypackets.ase.ra.radius.impl.RadiusSession;
import com.baypackets.ase.ra.radius.utils.Constants;
import com.baypackets.ase.resource.ResourceException;
import com.baypackets.ase.spi.measurement.MeasurementCounter;
import com.baypackets.ase.spi.measurement.MeasurementManager;
import com.baypackets.ase.spi.resource.ResourceContext;

public class RadiusStackServerInterfaceImpl implements RadiusStackServerInterface, Constants{
	
	//private int handleCount;
	private AseRadiusServer radServer;
	private RadiusResourceAdaptor ra;
	//private Map outgoingRequests;
	private RadiusResourceFactory raFactory;
	private String serverPropertyFile;
	
	private MeasurementCounter radAccessReqCnt;         //Accounting Request Counter Event Based Charging
	private MeasurementCounter radAccountingReqCnt;       //Accounting Request Counter Session Based Charging
	
	private MeasurementCounter radAccountingOnReqCnt;
	private MeasurementCounter radAccountingOffReqCnt;
	private MeasurementCounter radAccountingStartReqCnt;
	private MeasurementCounter radAccountingUpdateReqCnt;
	private MeasurementCounter radAccountingStopReqCnt;
	
	private MeasurementCounter radAccessAcceptResCnt;
	private MeasurementCounter radAccessRejectResCnt;
	private MeasurementCounter radAccessChallangeResCnt;
	
	private MeasurementCounter radAccountingResCnt;
	private MeasurementCounter radResSendErrorCnt;
	private MeasurementCounter radReqFailedToTriggerCnt;

		
	public RadiusStackServerInterfaceImpl(RadiusResourceAdaptor ra)
	{
		//handleCount = 0;
		this.ra = ra;
		//outgoingRequests = new Hashtable(0x10000);
	}

	private static Logger logger = Logger.getLogger(RadiusStackServerInterface.class);
	
	@Override
	public void init(ResourceContext context) throws RadiusResourceException {
		if(logger.isDebugEnabled())
			logger.debug("Inside init()....");		
		raFactory= (RadiusResourceFactory)context.getResourceFactory();
		if(logger.isDebugEnabled())
			logger.debug("Initialize measurement counters.");		
		MeasurementManager measurementMgr = context.getMeasurementManager();	
		
		this.radAccessReqCnt = measurementMgr.getMeasurementCounter(RADIUS_ACCESS_REQUEST_COUNTER_IN);
		this.radAccountingReqCnt = measurementMgr.getMeasurementCounter(RADIUS_ACCOUNTING_REQUEST_COUNTER_IN);
		this.radAccountingOnReqCnt = measurementMgr.getMeasurementCounter(RADIUS_ACCOUNTING_ON_REQUEST_COUNTER_IN);
		this.radAccountingOffReqCnt = measurementMgr.getMeasurementCounter(RADIUS_ACCOUNTING_OFF_REQUEST_COUNTER_IN);
		this.radAccountingStartReqCnt = measurementMgr.getMeasurementCounter(RADIUS_ACCOUNTING_START_REQUEST_COUNTER_IN);
		this.radAccountingUpdateReqCnt = measurementMgr.getMeasurementCounter(RADIUS_ACCOUNTING_UPDATE_REQUEST_COUNTER_IN);
		this.radAccountingStopReqCnt = measurementMgr.getMeasurementCounter(RADIUS_ACCOUNTING_STOP_REQUEST_COUNTER_IN);
		this.radAccessAcceptResCnt = measurementMgr.getMeasurementCounter(RADIUS_ACCESS_ACCEPT_COUNTER_OUT);
		this.radAccessRejectResCnt = measurementMgr.getMeasurementCounter(RADIUS_ACCESS_REJECT_COUNTER_OUT);
		this.radAccessChallangeResCnt = measurementMgr.getMeasurementCounter(RADIUS_ACCESS_CHALLANGE_COUNTER_OUT);
		this.radAccountingResCnt=measurementMgr.getMeasurementCounter(RADIUS_ACCOUNTING_RESPONSE_COUNTER_OUT);
		this.radResSendErrorCnt=measurementMgr.getMeasurementCounter(RADIUS_RESPONSE_SEND_COUNTER_ERROR);
		this.radReqFailedToTriggerCnt=measurementMgr.getMeasurementCounter(RADIUS_REQUEST_FAILED_TO_TRIGGER_AN_APPLICATION);
		serverPropertyFile = (new StringBuilder()).append(context.getConfigProperty("ase.home")).append(File.separator).append("conf").append(File.separator).append("radiusServer.properties").toString();
		init(serverPropertyFile);
	}

	@Override
	public void init(String configPropertyFileName) throws RadiusResourceException {
		Properties properties = new Properties();
		try {
			properties.load(new FileInputStream(configPropertyFileName));
		} catch(FileNotFoundException e){
			logger.error("FileNotFoundException occured while loading the properties file " + e);
			throw new RadiusResourceException(e.getMessage(),e.getCause());
		} catch (IOException e) {
			logger.error("IOException occured while loading the properties file" + e);
			throw new RadiusResourceException(e.getMessage(),e.getCause());
		}

		String sessionTimeout=	properties.getProperty(PROP_RADIUS_SESSION_TIMEOUT);
		String serverIP=properties.getProperty(PROP_RADIUS_SERVER_IP);
		String authPort=properties.getProperty(PROP_RADIUS_AUTH_PORT);
		String accountingPort=properties.getProperty(PROP_RADIUS_ACCOUNTING_PORT);
		String socketTimeOut=properties.getProperty(PROP_RADIUS_SOCKET_TIMEOUT);
		String sharedSecret=properties.getProperty(PROP_RADIUS_SHARED_SECRET);	
		String idleTimeout=properties.getProperty(PROP_RADIUS_IDLE_TIMEOUT);
		String threadPoolSize=properties.getProperty(PROP_RADIUS_SERVER_MAX_THREADS);
		String duplicateInterval=properties.getProperty(PROP_RADIUS_DUPLICATE_INTERVAL);
		int authPortValue=1812,accountingPortValue=1813,socketTimeOutValue=10000,threadPoolSizeValue=10;
		long duplicateIntervalVal=30000;
		if(authPort!=null && authPort.trim().length()!=0){
			try{
				authPortValue=Integer.parseInt(authPort);
			}catch (Exception e) {
				logger.error("Incorrect value given for property"+PROP_RADIUS_AUTH_PORT);
				authPortValue=1812;
			}
		}
		if(accountingPort!=null && accountingPort.trim().length()!=0){
			try{
				accountingPortValue=Integer.parseInt(accountingPort);
			}catch (Exception e) {
				logger.error("Incorrect value given for property"+PROP_RADIUS_ACCOUNTING_PORT);
				authPortValue=1813;
			}
		}
		if(socketTimeOut!=null && socketTimeOut.trim().length()!=0){
			try{
				socketTimeOutValue=Integer.parseInt(socketTimeOut);
			}catch (Exception e) {
				logger.error("Incorrect value given for property"+PROP_RADIUS_SOCKET_TIMEOUT);
				socketTimeOutValue=10000;
			}
		}
		if(threadPoolSize!=null && threadPoolSize.trim().length()!=0){
			try{
				threadPoolSizeValue=Integer.parseInt(threadPoolSize);
			}catch (Exception e) {
				logger.error("Incorrect value given for property"+PROP_RADIUS_SOCKET_TIMEOUT);
				threadPoolSizeValue=10;
			}
		}
		if(duplicateInterval!=null && duplicateInterval.trim().length()!=0){
			try{
				duplicateIntervalVal=Long.parseLong(duplicateInterval);
			}catch (Exception e) {
				logger.error("Incorrect value given for property"+PROP_RADIUS_DUPLICATE_INTERVAL);
				threadPoolSizeValue=10;
			}
		}

		this.radServer=new AseRadiusServer(this,sessionTimeout, idleTimeout, serverIP, authPortValue, accountingPortValue, socketTimeOutValue, sharedSecret,threadPoolSizeValue,duplicateIntervalVal);
	}
	
	
//	private synchronized int getNextHandle()
//	{
//		return handleCount++;
//	}
	

	@Override
	public void start() throws RadiusResourceException {
		if(logger.isDebugEnabled())
			logger.debug("Inside start() method of RadiusStackServerInterfaceImpl.....");
		this.radServer.start(true, true);
	}

	@Override
	public void stop() throws RadiusResourceException {
		if(logger.isDebugEnabled())
			logger.debug("Inside stop() method of RadiusStackServerInterfaceImpl.....");
		this.radServer.stop();
		}

	@Override
	public void handleRequest(RadiusRequest request)
			throws RadiusResourceException {
		logger.debug("handleRequest(RadiusRequest) called ");
	}

	@Override
	public void handleResponse(RadiusResponse response)
	throws RadiusResourceException {
		boolean sentSuccessfully = true;
		if (response == null) {
			logger.error("handleResponse(): null response.");
			return;
		}
			if (response instanceof RadiusAbstractResponse) {
				int responseType =response.getType();			
				switch (responseType){
				case ACCESS_ACCEPT:
					this.radAccessAcceptResCnt.increment();
					break;
				case ACCESS_REJECT:
					this.radAccessRejectResCnt.increment();
					break;
				case ACCESS_CHALLENGE:
					this.radAccessChallangeResCnt.increment();
					break;
				case ACCOUNTING_RESPONSE:
					this.radAccountingResCnt.increment();
					break;
				default:
					logger.error("Wrong/Unkown type response received.");
					throw new RadiusResourceException("Wrong/Unkown response type.");

				}
			}
			try {
				this.radServer.sendResponse(response);
				RadiusRequest request=(RadiusRequest) response.getRequest();
				RadiusSession radiusSession= (RadiusSession) ((RadiusAbstractRequest)request).getProtocolSession();
				radiusSession.removeRequest(request);
				SipApplicationSession appSesion= radiusSession.getApplicationSession();
				if(appSesion!=null)
					appSesion.invalidate();
			if(logger.isDebugEnabled())
				logger.debug("RadiusResponse sent successfully");			
		}catch (IOException e) {
			logger.error("IoException in sending Radius response " ,e);
			sentSuccessfully=false;
		} catch (Exception e) {
			logger.error("handleResponse() failed: " ,e);
			sentSuccessfully=false;
		}
		if(!sentSuccessfully) {
			this.radResSendErrorCnt.increment();
			RadiusResourceEvent resourceEvent = new RadiusResourceEvent(response, 
					RadiusResourceEvent.RESPONSE_FAIL_EVENT, response.getApplicationSession());
			try {
				this.ra.deliverEvent(resourceEvent);
			} catch (ResourceException e) {
				logger.error("Exception in delivering RoResourceEvent :: ",e);
				throw new RadiusResourceException(e);
			}

		}


	}
	
	public void handleIncomingRadiusRequest(RadiusPacket request, InetSocketAddress localAddress, InetSocketAddress remoteAddress,DatagramSocket dgs){
		if(logger.isDebugEnabled())
				logger.debug("Inside handleIncomingRadiusRequest().......");
			RadiusRequest req = null;		
			if (localAddress.getPort()== radServer.getAuthPort() && request instanceof AccessRequest){
				req=new RadiusAccessRequestImpl((AccessRequest)request);
				((RadiusAccessRequestImpl)req).setDatagramSocket(dgs);
				((RadiusAccessRequestImpl)req).setRemoteAddress(remoteAddress);
				this.radAccessReqCnt.increment();
			}
			else if (localAddress.getPort()== radServer.getAcctPort()&& request instanceof AccountingRequest){
				req=new RadiusAccountingRequestImpl((AccountingRequest)request);
				((RadiusAccountingRequestImpl)req).setDatagramSocket(dgs);
				((RadiusAccountingRequestImpl)req).setRemoteAddress(remoteAddress);
				this.radAccountingReqCnt.increment();
				try {
					this.incrementAccoutingRequestStatusType(((RadiusAccountingRequestImpl)req).getAcctStatusType());
				} catch (RadiusResourceException e) {
					logger.error("Exception in getAcctStatusType ......",e);
				}
			}
				else
					logger.error("unknown Radius packet type: "+request.getPacketType());		
		
			if(req!=null){
				try {
					ra.deliverRequest(req);
				} catch (ResourceException e) {
					logger.error("handleIncomingRadiusRequest() failed......",e);
				}
			}
	}
	public void incrementFailedToTriggerCounter(){
		radReqFailedToTriggerCnt.increment();
	}
	private void incrementAccoutingRequestStatusType(int acctStatusType) {
		switch (acctStatusType) {
		case AccountingRequest.ACCT_STATUS_TYPE_ACCOUNTING_ON:
			this.radAccountingOnReqCnt.increment();
			break;
		case AccountingRequest.ACCT_STATUS_TYPE_ACCOUNTING_OFF:
			this.radAccountingOffReqCnt.increment();
			break;
		case AccountingRequest.ACCT_STATUS_TYPE_START:
			this.radAccountingStartReqCnt.increment();
			break;
		case AccountingRequest.ACCT_STATUS_TYPE_INTERIM_UPDATE:
			this.radAccountingUpdateReqCnt.increment();
			break;
		case AccountingRequest.ACCT_STATUS_TYPE_STOP:
			this.radAccountingStopReqCnt.increment();
			break;
		default:
			break;
		}
	}
}
