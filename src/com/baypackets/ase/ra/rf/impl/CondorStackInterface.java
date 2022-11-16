package com.baypackets.ase.ra.rf.impl;

import java.util.Map;
import java.util.Hashtable;
import java.util.ArrayList;
import java.util.TimerTask;
import java.util.Calendar;
import java.io.File;

import javax.servlet.sip.SipURI;

import org.apache.log4j.Logger;

import com.baypackets.ase.ra.rf.*;
import com.baypackets.ase.resource.ResourceEvent;
import com.baypackets.ase.resource.ResourceException;
import com.baypackets.ase.spi.resource.ResourceContext;
import com.baypackets.ase.spi.measurement.MeasurementManager;
import com.baypackets.ase.spi.measurement.MeasurementCounter;
import com.baypackets.ase.spi.util.TimerService;
import com.baypackets.ase.util.AseAlarmService;
import com.baypackets.bayprocessor.slee.common.ConfigRepository;
import com.baypackets.ase.common.Registry;

import com.condor.chargingcommon.ImsInfo ;

import com.condor.rf.rfClient.*;
import com.condor.rf.rfMessages.*;
import com.condor.rf.rfInterfaces.rfInterface;
import com.condor.diaCommon.UnExpectedStateInfo;
import com.condor.chargingcommon.*;

/**
*	@author Neeraj Kumar Jadaun
*/

public class CondorStackInterface implements RfStackInterface, rfInterface, Constants 
{
	private static Logger logger = Logger.getLogger(CondorStackInterface.class);
	private static RfResourceFactory raFactory;
	private static TimerService timerService;
	//private static int timeout = 30;		//Configurable by the User TODO
	private AseAlarmService alarmService;

	private RfResourceAdaptor ra;
	private rfClientAPI rfClient;

	private int handleCount = 0;
	private Hashtable requests;
	private String configFile;			//Condor Stack initializes by reading this file
	
	// Following is used for generating log messages only
	private static String[] methods = new String[4]; // Total request types = 4

	private boolean sendRequest = true;
	private MeasurementCounter acrEventCnt;		//Accounting Request Counter Event Based Charging
	private MeasurementCounter acrSessionCnt;	//Accounting Request Counter Session Based Charging

	private MeasurementCounter acaEvent1xxxCnt;	//Accounting Answer Counter Event Based Charging
	private MeasurementCounter acaEvent2xxxCnt;
	private MeasurementCounter acaEvent3xxxCnt;
	private MeasurementCounter acaEvent4xxxCnt;
	private MeasurementCounter acaEvent5xxxCnt;		

	private MeasurementCounter acaSession1xxxCnt;	//Accounting Answer Counter Session Based Charging
	private MeasurementCounter acaSession2xxxCnt;
	private MeasurementCounter acaSession3xxxCnt;
	private MeasurementCounter acaSession4xxxCnt;
	private MeasurementCounter acaSession5xxxCnt;
	
	private final int EVENT = 1;		//EVENT Based charging
	private final int SESSION = 2;		//Session Based Charging
	
	public CondorStackInterface(RfResourceAdaptor ra) 
	{
		if(logger.isInfoEnabled())
			logger.info("In the Constructor of CondorStackInterface(RfResourceAdaptor )");
		this.ra = ra;		
		this.rfClient = new rfClientAPI();		
		this.requests = new Hashtable(1024) ;
	}

	public void init(ResourceContext context) throws RfResourceException 
	{
		if(logger.isDebugEnabled())
			logger.debug("Entering init(ResourceContext context) with resource context  :"+context);
		this.alarmService = (AseAlarmService)Registry.lookup(com.baypackets.ase.util.Constants.NAME_ALARM_SERVICE);
		try
		{
		CondorStackInterface.raFactory = (RfResourceFactory)context.getResourceFactory();
		CondorStackInterface.timerService = (TimerService)context.getTimerService();
		this.configFile = (String)context.getConfigProperty("ase.home") + File.separator +"conf" + File.separator + "rfClient.cfg";
		if(logger.isDebugEnabled())
			logger.debug("Use [" + this.configFile + "]");
		
		// Following is used only for logging messages
		methods[0] = new String("rfClient.rfAcctEventDataTrigger() ");
		methods[1] = new String("rfClient.rfClient.rfStartAcctSession() ");
		methods[2] = new String("rfClient.rfClient.rfSendInterAcctData() ");
		methods[3] = new String("rfClient.rfClient.rfStopAcctSession() ");
		}
		catch(Exception ee)
		{
			logger.error(ee.toString(), ee);
		}
		if(logger.isDebugEnabled())
			logger.debug("Initialize measurement counters.");
		MeasurementManager measurementMgr = context.getMeasurementManager();

		this.acrEventCnt = measurementMgr.getMeasurementCounter(Constants.ACR_EVENT_COUNTER);
		this.acrSessionCnt = measurementMgr.getMeasurementCounter(Constants.ACR_SESSION_COUNTER);

		this.acaEvent1xxxCnt = measurementMgr.getMeasurementCounter(Constants.ACA_EVENT_1xxx_COUNTER);
		this.acaEvent2xxxCnt = measurementMgr.getMeasurementCounter(Constants.ACA_EVENT_2xxx_COUNTER);
		this.acaEvent3xxxCnt = measurementMgr.getMeasurementCounter(Constants.ACA_EVENT_3xxx_COUNTER);
		this.acaEvent4xxxCnt = measurementMgr.getMeasurementCounter(Constants.ACA_EVENT_4xxx_COUNTER);
		this.acaEvent5xxxCnt = measurementMgr.getMeasurementCounter(Constants.ACA_EVENT_5xxx_COUNTER);

		this.acaSession1xxxCnt = measurementMgr.getMeasurementCounter(Constants.ACA_SESSION_1xxx_COUNTER);
		this.acaSession2xxxCnt = measurementMgr.getMeasurementCounter(Constants.ACA_SESSION_2xxx_COUNTER);
		this.acaSession3xxxCnt = measurementMgr.getMeasurementCounter(Constants.ACA_SESSION_3xxx_COUNTER);
		this.acaSession4xxxCnt = measurementMgr.getMeasurementCounter(Constants.ACA_SESSION_4xxx_COUNTER);
		this.acaSession5xxxCnt = measurementMgr.getMeasurementCounter(Constants.ACA_SESSION_5xxx_COUNTER);
		if(logger.isDebugEnabled())
			logger.debug("Leaving init(ResourceContext context)." );

	}
	// This method is for testing only
	public void init(String cfgFile) throws RfResourceException
	{
		try
		{
			CondorStackInterface.raFactory = RfResourceAdaptorFactory.getResourceFactory();
			this.configFile = cfgFile;
			if(logger.isDebugEnabled())
				logger.debug("Use [" + this.configFile + "]");
		}
                catch(Exception ex)
                {
					logger.error("RfResourceFactory.init() failed.");
					logger.error(ex.getMessage(), ex);
					throw new RfResourceException("RfResourceFactory.init() failed.");
				}
	}

	public void start() throws RfResourceException 
	{
		if(logger.isDebugEnabled())
			logger.debug("start(): initialize Condor RF stack.");
		int noOfTrial = 1;

		for(int i=1;i<=noOfTrial;i++)
		{
			if(logger.isDebugEnabled())
				logger.debug("Trying to start "+i+" time");
			int errorCode = this.rfClient.initializeRFInterface(this.configFile);
			if(logger.isDebugEnabled())
				logger.debug("The value of the errorcode "+errorCode);
			if(errorCode == rfErrorCodes.RF_SUCCESS)
			{
				if(logger.isDebugEnabled()) {
					logger.debug("start(): Condor statck is initialized.");
					logger.debug("start(): Register callback methods...");
				}
				this.rfClient.rfRegisterAppAPIs(this);
				if(logger.isDebugEnabled())
					logger.debug("start(): Registration Of Call Back methods are done");
				return;
			}
			else{
					String alarmMsg ="rfClient.initializeRFInterface() failed with error code [" + errorCode + "]";
					logger.error(alarmMsg);
					try
					{
						this.alarmService.sendAlarm(
							com.baypackets.ase.util.Constants.ALARM_RF_STACK_INITIALIZATION_FAILED,
								 alarmMsg );
					}
					catch(Exception e)
					{
						logger.error(e.toString(),e);
						logger.error("Unable to send alarm exception");
					}
					throw new RfResourceException(alarmMsg);
            }
		} // for loop ends

	}

	public void stop() throws RfResourceException 
	{
		if(logger.isInfoEnabled())
			logger.info("stop(): closing Diameter stack...");
		this.rfClient.rfCloseDiameterStack(true);
		if(logger.isInfoEnabled())
			logger.info("stop(): Diameter stack is closed");

	}

	public void handleRequest(RfRequest request) throws RfResourceException 
	{		
		if(logger.isInfoEnabled())
			logger.info("handleRequest() method is called "+request);

		if (request == null) 
		{
			logger.error("handleRequest(): null request.");
			return;
		}
		
		rfHandle rfhandle = new rfHandle();
		// Create and send Condor request
		int errorCode = 0;
		int handle =-1;
		String reqStr = null;
		SimpleRequestWrapper wrapper = null;
		Integer key = null;
		try 
		{

			//Destination Info to be sent to Rf Stack
		
			String destRealm = request.getDestRealm();
			String destHost = request.getDestHost();
			destInfo info = null;
			if((destHost!=null)||(destRealm!=null))
			{
				info = new destInfo();
				info.setDestHost(destHost);	
				info.setDestRealm(destRealm);

			}
		
			if(request instanceof RfAccountingRequest )
			{

				/*if(((RfAccountingRequest)request).getInterimByStack())
				{
					if(logger.isDebugEnabled())
						logger.debug("Interim request is called by RfStack");
					
					synchronized((Object)request)
					{
						if(logger.isDebugEnabled())
							logger.debug("Interim request before synchronized");
						((Object)request).notify();
						if(logger.isDebugEnabled())
							logger.debug("Interim request after synchronized");
					}
					if(logger.isDebugEnabled())
						logger.debug("Interim request ");
					return;
				}*/			

				//Depending on the Accounting Record type the request is differentiated 
				switch (((RfAccountingRequest)request).getAccntRecordType()) 
				{
					case EVENT_RECORD:
						// EVENT BASED Charging
						if(logger.isInfoEnabled())
							logger.info("Event Based Charging ");
						//rfAccntData data = ((RfAccountingRequest)request).getStackObject();
						handle = this.getNextHandle();
						((RfSession)((RfMessage)request).getSession()).setHandle(handle);
						if(logger.isDebugEnabled())
							logger.debug("The application handle in Event based offline charging =>"+handle);
						reqStr = methods[0];
						// creating a wrapper class of this request to store it into map for
						// further use(response processing)
						wrapper = new SimpleRequestWrapper(rfhandle, request);
						key = new Integer(handle);
						this.requests.put(key, wrapper);
						// invoking stack method to send event request
						errorCode = this.rfClient.rfAcctEventDataTrigger(
													((RfAccountingRequest)request).getStackObject(), 
													info,
													handle);
						break;
									
					case START_RECORD:
					 	//Session Based Charging
						if(logger.isInfoEnabled())
							logger.info("Session based charging: start record");
						rfhandle = new rfHandle();
						handle = this.getNextHandle();
						((RfSession)((RfMessage)request).getSession()).setHandle(handle);
						if(logger.isDebugEnabled())
							logger.debug("The handle in session based offline charging  ==:>"+handle);
						reqStr = methods[1];
						//rfAccntData data = ((RfAccountingRequest)request).getStackObject();
						// creating a wrapper class of this request to store it into map for
						//  further use(response and interim and stop reqeusts processing)
						wrapper = new SimpleRequestWrapper(rfhandle, request);
						key = new Integer(handle);
						this.requests.put(key, wrapper);

						// invoking stack method to send start accounting session request
						errorCode = this.rfClient.rfStartAccntSession(
													((RfAccountingRequest)request).getStackObject(),
													info,
													handle, 
													rfhandle);
						break;

					case INTERIM_RECORD:
						if(logger.isInfoEnabled())
							logger.info("Session based charging: interim record");
						if(((RfSession)((RfMessage)request).getSession()).getRfSessionState()!=ACTIVE)
						{
							if(logger.isDebugEnabled())
								logger.debug("Session is not in active mode: ");
							return ;
						}

						//rfAccntData data = ((RfAccountingRequest)request).getStackObject();
						handle = ((RfSession)((RfMessage)request).getSession()).getHandle();
						if(logger.isDebugEnabled())
							logger.debug("handle for Interim Request in Session Based offline charging == :   "+handle);
						reqStr = methods[2];
						key = new Integer(handle);
						wrapper = (SimpleRequestWrapper)this.requests.get(key);
						if(wrapper==null)
						{
							logger.error("Requests not found for interim requests for handle:"+handle);
							throw new RfResourceException("Requests not found for interim request ");
						}
					
						rfhandle = wrapper.getRfHandle();
						errorCode = this.rfClient.rfSendInterAcctData(
													((RfAccountingRequest)request).getStackObject(),
													rfhandle);
						break;
						
					case STOP_RECORD:	
						if(logger.isInfoEnabled())
							logger.info("Session based charging: stop record");
					
						//rfAccntData data = ((RfAccountingRequest)request).getStackObject();
						handle = ((RfSession)((RfMessage)request).getSession()).getHandle();
						if(logger.isDebugEnabled())
							logger.debug("Stop Accounting request in session based offline charging    "+handle);
						reqStr = methods[3];
						key = new Integer(handle);
						// get corrosponding request from map
						wrapper = (SimpleRequestWrapper)this.requests.get(key);
						if(wrapper==null)
						{
							logger.error("Requests not found for stop reqeust for handle"+ handle);
							throw new RfResourceException("Requests not found for stop reqeust"); 
						}
						rfhandle = (rfHandle)wrapper.getRfHandle();
						errorCode = this.rfClient.rfStopAcctSession(
													((RfAccountingRequest)request).getStackObject(), 
													rfhandle);
						break;
				
					default:	
						if(logger.isInfoEnabled())
							logger.info("neighter event nor session bassed offline request");
						throw new RfResourceException("AccntRecordType is not set in request");

				} // switch ends 
			
				if (errorCode == rfErrorCodes.RF_SUCCESS) 		//Success
				{		if(logger.isInfoEnabled())
							logger.info(reqStr + "successfully called");
						((RfSession)((RfMessage)request).getSession()).setRfSessionState(ACTIVE);
						this.acrEventCnt.increment();
				}
				else
				{
					logger.error(reqStr + "call failed with error code: [" +errorCode + "]");
					this.requests.remove(key);
					if(errorCode ==rfErrorCodes.RF_FAIL)
					{
						logger.error(reqStr + " failure cause [error in processing the message]");
						ra.deliverEvent(new ResourceEvent(	request, 
															RfResourceEvent.REQUEST_FAIL_EVENT, 
															((RfAccountingRequest)request).getApplicationSession()));
					}
					else if(errorCode == rfErrorCodes.RF_NULL_PTR) {
						logger.error(reqStr + " failure cause [null  pointer]");
						ra.deliverEvent(new ResourceEvent(	request, 
															RfResourceEvent.REQUEST_FAIL_EVENT, 
															((RfAccountingRequest)request).getApplicationSession()));
					}
					else if(errorCode == rfErrorCodes.RF_NULL_SESSION)
					{
						logger.error(reqStr + " failure cause [null session object]");
						ra.deliverEvent(new ResourceEvent(	request, 
															RfResourceEvent.REQUEST_FAIL_EVENT, 
															((RfAccountingRequest)request).getApplicationSession()));
					}	
					throw new RfResourceException("Request could not be sent");
					
				}
			}
		}
		catch (Exception ex) 
		{
			logger.error("handleRequest() failed: " + ex);
			logger.error(ex.getMessage(), ex);
			throw new RfResourceException(ex);
		}

	}

	public void handleResponse(RfResponse response) throws RfResourceException 
	{
		if(logger.isDebugEnabled())
			logger.debug("Entering handleResponse()");
		if (response == null) 
		{
			logger.error("handleResponse(): null response.");
			return;
		}
		if(logger.isDebugEnabled())
			logger.debug("Leaving handleResponse()");
			return;
	}


	public int rfAcctEventResponse(rfAccntResponse rfaccntResponse, int handle)
	{
		if(logger.isDebugEnabled())
			logger.debug("rfAcctEventResponse(): got response for request[" + handle + "]");
		this.updateResponseCounter(EVENT, rfaccntResponse.getResultCode());
		try 
		{
			if(logger.isDebugEnabled())
				logger.debug("Result code received: "+rfaccntResponse.getResultCode());
			if(rfaccntResponse.getResultCode() > 2999 ) {
				if(logger.isDebugEnabled())
					logger.debug("error response received ");
			}
			// find request from map for this response
			SimpleRequestWrapper wrap = (SimpleRequestWrapper)this.requests.get(new Integer(handle));
			if(wrap==null)
			{
				if(logger.isDebugEnabled())
					logger.debug("Requests not found"+ handle);
				return 0;
			}
				
			RfRequest request = wrap.getRfRequest();
			// creating RfResponse from stack response to send to the application
			RfResponse toResponse = (RfAccountingResponse)((RfAccountingRequest)request).createResponse(rfaccntResponse, EVENT);
			this.ra.deliverResponse(toResponse);
			((RfSession)((RfMessage)request).getSession()).setRfSessionState(INACTIVE);
			this.requests.remove(new Integer(handle));
			return 0;
		} 
		catch (Exception ex) 
		{
			logger.error("rfAcctEventResponse() failed: " + ex);
			logger.error(ex.getMessage(), ex);
			return 1;
		}
	}

	public int rfStartAcctResponse(rfAccntResponse acctResponse, int handle)
	{
		if(logger.isDebugEnabled())
			logger.debug("rfStartAcctResponse(): got response for request[" + handle + "]");
        this.updateResponseCounter(SESSION, acctResponse.getResultCode());

		if(logger.isDebugEnabled()) {	
			logger.debug(" : acctResponse.getIsAcctInteIntvalPresent() "+acctResponse.getIsAcctInteIntvalPresent());
			logger.debug(": acctResponse.getAcctInterimInterval() "+acctResponse.getAcctInterimInterval());
			logger.debug("Result code received: "+acctResponse.getResultCode());
		}
		try
		{
			SimpleRequestWrapper wrap;
			// find request for this response from map
			if(acctResponse.getResultCode() > 2999 ) {
				if(logger.isDebugEnabled())
					logger.debug("error response received removing from map");
				wrap = (SimpleRequestWrapper)this.requests.remove(new Integer(handle));
			}
			else {
				wrap = (SimpleRequestWrapper)this.requests.get(new Integer(handle));
			}
			
			if(wrap==null)
			{
				logger.error("Start: Requests not found"+ handle);
				return 0;
			}
			RfRequest request = wrap.getRfRequest();
			// create RfResponse from stack response object to send to application 
			RfResponse toResponse = (RfAccountingResponse)((RfAccountingRequest)request).createResponse(acctResponse, SESSION);

			this.ra.deliverResponse(toResponse);
			return 0;
		}
		catch (Exception ex)
		{
			logger.error("rfStartAcctResponse() failed: " + ex);
			logger.error(ex.getMessage(), ex);
			return 1;
		}
	}
	public int rfInterimAcctResponse(rfAccntResponse acctResponse, int handle)
	{
		if(logger.isDebugEnabled())
			logger.debug("rfInterimAcctResponse(): got response for request[" + handle + "]");
                
		this.updateResponseCounter(SESSION, acctResponse.getResultCode());
		if(logger.isDebugEnabled())
			logger.debug("Result code received: "+acctResponse.getResultCode());
		RfRequest request = null;
		SimpleRequestWrapper wrap;	
		// find request corrosponding to this response from map
			if(acctResponse.getResultCode() > 2999 ) {
				if(logger.isDebugEnabled())
					logger.debug("error response received removing from map");
				wrap = (SimpleRequestWrapper)this.requests.remove(new Integer(handle));
			}
			else {
				wrap = (SimpleRequestWrapper)this.requests.get(new Integer(handle));
			}
		if(wrap==null)
		{
			logger.error("interim:Requests not found "+ handle);
			return 0;
		}
		try
		{
			request = wrap.getRfRequest();
			// creating RfResponse from stack response to send to the application
			RfResponse toResponse = (RfAccountingResponse)((RfAccountingRequest)request).createResponse(acctResponse, SESSION);
			toResponse.setAccntRecordType(INTERIM_RECORD);
			this.ra.deliverResponse(toResponse);
			return 0;
		}
		catch (Exception ex)
		{
			logger.error("rfInterimAcctResponse() failed: " + ex);
			logger.error(ex.getMessage(), ex);
			return 1;
		}
	}

	public int rfStopAcctResponse(rfAccntResponse acctResponse, int handle)
	{
		if(logger.isDebugEnabled())
			logger.debug("rfStopAcctResponse(): got response for request[" + handle + "]");
		this.updateResponseCounter(SESSION, acctResponse.getResultCode());
		if(logger.isDebugEnabled())
			logger.debug("Result code received: "+acctResponse.getResultCode());
		if(acctResponse.getResultCode() > 2999 ) {
			if(logger.isDebugEnabled())
				logger.debug("error response received ");
		}
		//find request for this response from map
		SimpleRequestWrapper wrap = (SimpleRequestWrapper)this.requests.get(new Integer(handle));
		
		if(wrap==null)
		{
			logger.error("Stop:Requests not found"+ handle);
			return 0;
		}
		RfRequest request = wrap.getRfRequest();

		try
		{
			// create RfResponse from stack response object to send to application
			RfResponse toResponse = (RfAccountingResponse)((RfAccountingRequest)request).createResponse(acctResponse, SESSION);
			this.ra.deliverResponse(toResponse);

			((RfSession)((RfMessage)request).getSession()).setRfSessionState(INACTIVE);
			this.requests.remove(new Integer(handle));
			return 1;
		}
		catch (Exception ex)
		{
			logger.error("rfStopAcctResponse() failed: " + ex);
			logger.error(ex.getMessage(), ex);
			return 0;
		}
	}
	public int rfTxTimerExpired(int appHandle)
	{
		try
		{
			if(logger.isDebugEnabled())
				logger.debug("Time out occurred");
			RfRequest request = ((SimpleRequestWrapper)this.requests.remove(new Integer(appHandle))).getRfRequest();
			ra.deliverEvent(new ResourceEvent(request, RfResourceEvent.INTERIM_TIMEOUT_EVENT, ((RfAccountingRequest)request).getApplicationSession()));
			((RfSession)((RfMessage)request).getSession()).setRfSessionState(INACTIVE);
			return 0;
		}
		catch(Exception e)
		{
			logger.error("Time out occurred", e);
			return 1;
		}
	}

	public int rfDelAcctRecordFromNonVolatileStorage(int hopbyhopID)
	{
		if(logger.isDebugEnabled())
			logger.debug("Entering rfDelAcctRecordFromNonVolatileStorage(int)");
		return 0;
	}

	public int rfStoreAcctRecordInNonVolatileStorage(rfAccntDataStorage accntDataStorage, int hopbyhopID)
	{
		if(logger.isDebugEnabled())
			logger.debug("Entering rfStoreAcctRecordInNonVolatileStorage() ");
		return 0;
	}


	public int rfInterimDataReq(rfAccntData ptrData, int handle)
	{
		if(logger.isDebugEnabled())
			logger.debug("rfInterimDataReq(): got request[" + handle + "]");
		return 1;
		/*
		RfRequest request = null;
		RfAccountingRequest req = null;
		RfSession session = null;
		try
		{
			SimpleRequestWrapper wrap = (SimpleRequestWrapper)this.requests.get(new Integer(handle));
			if(wrap==null)
			{
				logger.error("Requests not found "+ handle);
				return 1;
			}
			request = wrap.getRfRequest();

			session = (RfSession)request.getSession();

			req = (RfAccountingRequest)session.createMessage(SESSION);
		}
		catch(Exception eee)
		{
			if(logger.isDebugEnabled())
				logger.debug("rfInterimDataReq ERROR ", eee);
		}

		try
		{
			req.setAccntRecordType(INTERIM_RECORD);
			this.ra.deliverRequest(req);
			
			//req.setInterimByStack(true);
			synchronized((Object)req)
			{
				((Object)req).wait();
			} 
			if(logger.isDebugEnabled())
				logger.debug("HELLO INDIA  After ");
			//copyRequestSession(ptrData, (RfAccountingRequest)request, -1);
			//rfAccntData data = ((RfAccountingRequest)request).getStackObject();
			//checkRequestEvent(ptrData , -1 );
	
			req.setAccntRecordType(INTERIM_RECORD);

			return 0;

		} 
		catch (Exception ex) 
		{
			logger.error("rfInterimDataReq() failed: " + ex);
			logger.error(ex.getMessage(), ex);
			return 1;
		}
		*/
	}
	

	public int rfNotifyDfnDown(short dfnId) 
	{
		if(logger.isDebugEnabled())
			logger.debug("rfNotifyDfnDown() is called");
		String alarmMsg="DFN with dfnId "+dfnId+": is down";
		try
		{
			this.alarmService.sendAlarm(com.baypackets.ase.util.Constants.ALARM_DFN_DOWN, alarmMsg );
		}
		catch(Exception e)
		{
			logger.error(e.toString(),e);
			logger.error("Unable to send alarm exception");
		}

		try
		{
			ra.deliverEvent(new ResourceEvent(null, RfResourceEvent.RF_NOTIFY_DFN_DOWN, null));
			return 0;
		}
		catch(ResourceException ee)
		{
			logger.error(ee.toString(),ee);
			return 1;
		}
		
	}

	public int rfNotifyDfnUP(short dfnId)
	{
		if(logger.isDebugEnabled())
			logger.debug("Local dfn is up"+dfnId);
		return 0;
	}

	public int rfNotifyPeerDown(java.lang.String peerName) 
	{
		if(logger.isDebugEnabled())
			logger.debug("rfNotifyPeerDown() is called for peer name"+peerName);
		try
		{
			ra.deliverEvent(new ResourceEvent(null, RfResourceEvent.RF_NOTIFY_PEER_DOWN, null));
			return 0;
		}
		catch(ResourceException e)
		{
			logger.error(e.toString(),e);
			return 1;
		}
	}

	public int rfNotifyPeerUp(java.lang.String peerName) 
	{
		if(logger.isDebugEnabled())
			logger.debug("rfNotifyPeerUp() is called for peer name"+peerName);
		try
		{
			ra.deliverEvent(new ResourceEvent(null, RfResourceEvent.RF_NOTIFY_PEER_UP, null));
			return 0;
		}
		catch(ResourceException e)
		{
			logger.error(e.toString(), e);
			return 1;
		}
	}
	public int rfNotifyDisconnectPeerRequest(int cause) 
	{
		if(logger.isDebugEnabled())
			logger.debug("rfNotifyDisconnectPeerRequest() is called");
		try
		{
			ra.deliverEvent(new ResourceEvent(null, RfResourceEvent.RF_NOTIFY_DISCONNECT_PEER_REQUEST,null));
		}
		catch(ResourceException ee)
		{
			logger.error(ee.toString(),ee);
			return 1;	
		}
		if(logger.isDebugEnabled())
			logger.debug("Peer diameter node (CDF) wants to disconnect with the local DFN");
		return 0;
	}

	public int rfDisconnectPeerResponse(int resultCode) 
	{
		if(logger.isDebugEnabled())
			logger.debug("rfDisconnectPeerResponse() is called");
		try
		{
			ra.deliverEvent(new ResourceEvent(null, RfResourceEvent.RF_DISCONNECT_PEER_RESPONSE,null));
			return 0;
		}
		catch(ResourceException e)
		{
			logger.error(e.toString(), e);
		}
		return 1;
	}


	public int rfNotifyFailOver()  
	{
		if(logger.isDebugEnabled())
			logger.debug("rfNotifyFailOver() is called");

		String alarmMsg="Fail over notification is received from DFN";
		try
		{
			this.alarmService.sendAlarm(com.baypackets.ase.util.Constants.ALARM_FAIL_OVER_DFN, alarmMsg );
		}
		catch(Exception e)
		{
			logger.error(e.toString(),e);
			logger.error("Unable to send alarm exception");
		}
		try
		{
			ra.deliverEvent(new ResourceEvent(null, RfResourceEvent.RF_NOTIFY_FAILOVER, null));
			return 0;
		}
		catch(ResourceException ee)
		{
			logger.error(ee.toString(),ee);
			return 1;
		}
	}

	public synchronized void setDontSendRequest()
	{
		this.sendRequest = false;
	}
	
	public boolean getDontSendRequest()
	{
		return this.sendRequest;
	}


	public int rfNotifyUnexpectedMsgEvent(com.condor.diaCommon.UnExpectedStateInfo stateInfo, int handle) 
	{
		if(logger.isDebugEnabled()) {
			logger.debug("rfNotifyUnexpectedMsgEvent(): receive error msg[" + handle + "]");
			logger.debug("Stack expects [" + stateInfo.getExpectedMsg() + "]");
			logger.debug("Stack received [" + stateInfo.getReceivedMsg() + "]");
		}

		try
		{
			ra.deliverEvent(new ResourceEvent(null, RfResourceEvent.RF_NOTIFY_UNEXPECTED_MESSAGE, null));
			return 0;
		}
		catch(ResourceException e)
		{
			logger.error(e.toString(), e);
			return 1;
		}
	}

	//Added to notify about error in the stack
	public int rfNotifyErrorEvent(ErrorInfo info)
	{
		if(logger.isDebugEnabled())
			logger.debug("Entering rfNotifyErrorEvent()");
		try
		{
			ra.deliverEvent(new ResourceEvent(null, RfResourceEvent.REQUEST_FAIL_EVENT, null));
			return 0;
		}
		catch(Exception e)
		{
			if(logger.isDebugEnabled())
				logger.debug("Exception occurred while delivering Event "+info.getMsgType());
			return 1;
		}
	}
	
	// mathod to update response counter
	private void updateResponseCounter(int type, int resultCode) 
	{
		switch (type) 
		{
			case EVENT:
				if (resultCode >= 1000 && resultCode < 2000) 
				{
					this.acaEvent1xxxCnt.increment();
				} 
				else if (resultCode >= 2000 && resultCode < 3000) 
				{
					this.acaEvent2xxxCnt.increment();
				}
				else if (resultCode >= 3000 && resultCode < 4000) 
				{
					this.acaEvent3xxxCnt.increment();
				}
				else if (resultCode >= 4000 && resultCode < 5000) 
				{
					this.acaEvent4xxxCnt.increment();
				}
				else if (resultCode >= 5000 && resultCode < 6000) 
				{
					this.acaEvent5xxxCnt.increment();
				}
				break;
			case SESSION:
				if (resultCode >= 1000 && resultCode < 2000) 
				{
					this.acaSession1xxxCnt.increment();
				} 
				else if (resultCode >= 2000 && resultCode < 3000) 
				{
					this.acaSession2xxxCnt.increment();
				}
				else if (resultCode >= 3000 && resultCode < 4000) 
				{
					this.acaSession3xxxCnt.increment();
				} 
				else if (resultCode >= 4000 && resultCode < 5000) 
				{
					this.acaSession4xxxCnt.increment();
				} 
				else if (resultCode >= 5000 && resultCode < 6000) 
				{
					this.acaSession5xxxCnt.increment();
				}
				break;
		}
	}
	
	// this class is a wrapper for RfRequest and rfHandle.
	private class SimpleRequestWrapper
	{
		private rfHandle handle = null;
		private RfRequest request = null;

		public SimpleRequestWrapper(rfHandle handle, RfRequest request)
		{
			this.handle = handle;
			this.request = request;
		}

		public rfHandle getRfHandle()
		{
			return this.handle;
		}

		public RfRequest getRfRequest()
		{
			return this.request;
		}
	}
	
	// this method returns a new integer every time it is being called.
	private synchronized int getNextHandle() {
		return this.handleCount++;
	}
	
	public void addRequestToMap(int handle , RfRequest request) {
		this.requests.put(new Integer(handle) , request);
	}
}
