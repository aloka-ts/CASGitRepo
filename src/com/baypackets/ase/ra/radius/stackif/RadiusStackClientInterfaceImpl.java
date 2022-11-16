package com.baypackets.ase.ra.radius.stackif;

import org.apache.log4j.Logger;
import org.tinyradius.packet.AccountingRequest;

import com.baypackets.ase.ra.radius.RadiusRequest;
import com.baypackets.ase.ra.radius.RadiusResourceAdaptor;
import com.baypackets.ase.ra.radius.RadiusResourceException;
import com.baypackets.ase.ra.radius.RadiusResourceFactory;
import com.baypackets.ase.ra.radius.RadiusResponse;
import com.baypackets.ase.ra.radius.RadiusStackClientInterface;
import com.baypackets.ase.ra.radius.utils.Constants;
import com.baypackets.ase.spi.measurement.MeasurementCounter;
import com.baypackets.ase.spi.measurement.MeasurementManager;
import com.baypackets.ase.spi.resource.ResourceContext;

public class RadiusStackClientInterfaceImpl implements RadiusStackClientInterface,Constants{

	private static Logger logger = Logger.getLogger(RadiusStackClientInterfaceImpl.class);
	private RadiusResourceFactory raFactory;
	private RadiusResourceAdaptor ra;
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
	
	private MeasurementCounter radReqSendErrorCnt;
	
	//private Map requests;
	
	public RadiusStackClientInterfaceImpl(RadiusResourceAdaptor ra)
	{
		this.ra = ra;
		//this.requests = new Hashtable(64*1024); // initialize with 64 K entries 
	}
	
	
	@Override
	public void init(ResourceContext context) throws RadiusResourceException {
		this.raFactory=(RadiusResourceFactory) context.getResourceFactory();
		logger.debug("Initialize measurement counters.");		
		MeasurementManager measurementMgr = context.getMeasurementManager();			
		this.radAccessReqCnt = measurementMgr.getMeasurementCounter(RADIUS_ACCESS_REQUEST_COUNTER_OUT);
		this.radAccountingReqCnt = measurementMgr.getMeasurementCounter(RADIUS_ACCOUNTING_REQUEST_COUNTER_OUT);
		this.radAccountingOnReqCnt = measurementMgr.getMeasurementCounter(RADIUS_ACCOUNTING_ON_REQUEST_COUNTER_OUT);
		this.radAccountingOffReqCnt = measurementMgr.getMeasurementCounter(RADIUS_ACCOUNTING_OFF_REQUEST_COUNTER_OUT);
		this.radAccountingStartReqCnt = measurementMgr.getMeasurementCounter(RADIUS_ACCOUNTING_START_REQUEST_COUNTER_OUT);
		this.radAccountingUpdateReqCnt = measurementMgr.getMeasurementCounter(RADIUS_ACCOUNTING_UPDATE_REQUEST_COUNTER_OUT);
		this.radAccountingStopReqCnt = measurementMgr.getMeasurementCounter(RADIUS_ACCOUNTING_STOP_REQUEST_COUNTER_OUT);
		this.radAccessAcceptResCnt = measurementMgr.getMeasurementCounter(RADIUS_ACCESS_ACCEPT_COUNTER_IN);
		this.radAccessRejectResCnt = measurementMgr.getMeasurementCounter(RADIUS_ACCESS_REJECT_COUNTER_IN);
		this.radAccessChallangeResCnt = measurementMgr.getMeasurementCounter(RADIUS_ACCESS_CHALLANGE_COUNTER_IN);
		this.radAccountingResCnt=measurementMgr.getMeasurementCounter(RADIUS_ACCOUNTING_RESPONSE_COUNTER_IN);
		this.radReqSendErrorCnt=measurementMgr.getMeasurementCounter(RADIUS_REQUEST_SEND_COUNTER_ERROR);
	}

	@Override
	public void init(String clientPropertyFile) throws RadiusResourceException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void start() throws RadiusResourceException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void stop() throws RadiusResourceException {
		// TODO Auto-generated method stub
		
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


	@Override
	public void handleRequest(RadiusRequest request)
			throws RadiusResourceException {
		if (request == null) {
			logger.error("handleResponse(): null response.");
			return;
		}
			if (request instanceof RadiusAccountingRequestImpl) {
				this.radAccountingReqCnt.increment();
				this.incrementAccoutingRequestStatusType(((RadiusAccountingRequestImpl)request).getAcctStatusType());
			}
			else if(request instanceof RadiusAccessRequestImpl) {
				this.radAccessReqCnt.increment();
			}
			else{
					logger.error("Wrong/Unkown type response received.");
					throw new RadiusResourceException("Wrong/Unkown response type.");

				}
		}


	@Override
	public void handleResponse(RadiusResponse response)
			throws RadiusResourceException {
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
	}
	
	
//	public void addRequestToMap(int handle, RadiusRequest request) {
//		logger.debug("request map size before adding is :[ "+ this.requests.size()+"]"); 
//			this.requests.put(new Integer(handle) , request);
//		logger.debug("request map size after adding is :[ "+ this.requests.size()+"]"); 
//	}

}
