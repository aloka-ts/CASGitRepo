/**
 * Filename:	AbstractCreditControlMessage.java
 * Created On:	04-Oct-2006
 */
package com.baypackets.ase.ra.ro.stackif;

import org.apache.log4j.Logger;

import com.baypackets.ase.ra.ro.*;
import com.baypackets.ase.ra.ro.impl.RoSession;
import com.baypackets.ase.dispatcher.Destination;

import com.condor.ro.roclient.response.*;
import com.condor.apncommon.DiameterBaseInfo;

abstract class AbstractCreditControlMessage
	extends AbstractRoMessage
	implements CreditControlMessage {

	private static final Logger logger = Logger.getLogger(AbstractCreditControlMessage.class);

	private CustomAVPMapImpl _avpMap;

	private short _ccReqType = -1;
	private String _reqMethod;
	private ServiceInformationImpl _servInfo;
	private long _authAppId = -1;
	private Destination m_destination=  null;

	AbstractCreditControlMessage(RoSession session, DiameterBaseInfo stackObj) {
		super(session, stackObj, false);

		if(stackObj instanceof RoFirstInterogationRes) {
			this._ccReqType = Constants.CCRT_INITIAL_REQUEST;
			this._reqMethod = Constants.METHOD_FIRST_INTERROGATION;

			RoFirstInterogationRes resp = (RoFirstInterogationRes)stackObj;
			com.condor.diaCommon.AAACustomAvpInfo caInfo = resp.getCustomAvpInfo();
			if(caInfo != null) {
				this._avpMap = CustomAVPCopier.readAvpInfo(caInfo);
			}

			if(resp.getIsServiceInfoPresent()) {
				this._servInfo = new ServiceInformationImpl(resp.getServiceInfo());
			}
		} else if(stackObj instanceof RoUpdateInterogationRes) {
			this._ccReqType = Constants.CCRT_UPDATE_REQUEST;
			this._reqMethod = Constants.METHOD_INTERMEDIATE_INTERROGATION;

			RoUpdateInterogationRes resp = (RoUpdateInterogationRes)stackObj;
			com.condor.diaCommon.AAACustomAvpInfo caInfo = resp.getCustomAvpInfo();
			if(caInfo != null) {
				this._avpMap = CustomAVPCopier.readAvpInfo(caInfo);
			}

			if(resp.getServiceInfo() != null) {
				this._servInfo = new ServiceInformationImpl(resp.getServiceInfo());
			}
		} else if(stackObj instanceof RoFinalInterogationRes) {
			this._ccReqType = Constants.CCRT_TERMINATION_REQUEST;
			this._reqMethod = Constants.METHOD_FINAL_INTERROGATION;

			RoFinalInterogationRes resp = (RoFinalInterogationRes)stackObj;
			com.condor.diaCommon.AAACustomAvpInfo caInfo = resp.getCustomAvpInfo();
			if(caInfo != null) {
				this._avpMap = CustomAVPCopier.readAvpInfo(caInfo);
			}

			if(resp.getServiceInfo() != null) {
				this._servInfo = new ServiceInformationImpl(resp.getServiceInfo());
			}
		} else if(stackObj instanceof RoDirectDebitingResponse) {
			this._ccReqType = Constants.CCRT_EVENT_REQUEST;
			this._reqMethod = Constants.METHOD_DIRECT_DEBITING;

			RoDirectDebitingResponse resp = (RoDirectDebitingResponse)stackObj;
			com.condor.diaCommon.AAACustomAvpInfo caInfo = resp.getCustomAvpInfo();
			if(caInfo != null) {
				this._avpMap = CustomAVPCopier.readAvpInfo(caInfo);
			}

			if(resp.getIsServiceInfoPresent()) {
				this._servInfo = new ServiceInformationImpl(resp.getServiceInfo());
			}

			this._authAppId = resp.getAuthApplicationId();
		} else if(stackObj instanceof RoRefundServiceResponse) {
			this._ccReqType = Constants.CCRT_EVENT_REQUEST;
			this._reqMethod = Constants.METHOD_REFUND_ACCOUNT;

			RoRefundServiceResponse resp = (RoRefundServiceResponse)stackObj;
			com.condor.diaCommon.AAACustomAvpInfo caInfo = resp.getCustomAvpInfo();
			if(caInfo != null) {
				this._avpMap = CustomAVPCopier.readAvpInfo(caInfo);
			}

			if(resp.getServiceInfo() != null) {
				this._servInfo = new ServiceInformationImpl(resp.getServiceInfo());
			}
		} else if(stackObj instanceof RoBalanceCheckResponse) {
			this._ccReqType = Constants.CCRT_EVENT_REQUEST;
			this._reqMethod = Constants.METHOD_CHECK_BALANCE;

			RoBalanceCheckResponse resp = (RoBalanceCheckResponse)stackObj;
			com.condor.diaCommon.AAACustomAvpInfo caInfo = resp.getCustomAvpInfo();
			if(caInfo != null) {
				this._avpMap = CustomAVPCopier.readAvpInfo(caInfo);
			}

			if(resp.getServiceInfo() != null) {
				this._servInfo = new ServiceInformationImpl(resp.getServiceInfo());
			}
		} else if(stackObj instanceof RoServicePriceEnqResponse) {
			this._ccReqType = Constants.CCRT_EVENT_REQUEST;
			this._reqMethod = Constants.METHOD_PRICE_ENQUERY;

			RoServicePriceEnqResponse resp = (RoServicePriceEnqResponse)stackObj;
			com.condor.diaCommon.AAACustomAvpInfo caInfo = resp.getCustomAvpInfo();
			if(caInfo != null) {
				this._avpMap = CustomAVPCopier.readAvpInfo(caInfo);
			}

			if(resp.getServiceInfo() != null) {
				this._servInfo = new ServiceInformationImpl(resp.getServiceInfo());
			}
		}
	}

	AbstractCreditControlMessage(RoSession session, int reqType, boolean readonly) {
		super(session, null, readonly);

		switch(reqType) {
			case Constants.RO_FIRST_INTERROGATION:
				this._ccReqType = Constants.CCRT_INITIAL_REQUEST;
				this._reqMethod = Constants.METHOD_FIRST_INTERROGATION;
			break;

			case Constants.RO_INTERMEDIATE_INTERROGATION:
				this._ccReqType = Constants.CCRT_UPDATE_REQUEST;
				this._reqMethod = Constants.METHOD_INTERMEDIATE_INTERROGATION;
			break;

			case Constants.RO_FINAL_INTERROGATION:
				this._ccReqType = Constants.CCRT_TERMINATION_REQUEST;
				this._reqMethod = Constants.METHOD_FINAL_INTERROGATION;
			break;

			case Constants.RO_DIRECT_DEBITING:
				this._ccReqType = Constants.CCRT_EVENT_REQUEST;
				this._reqMethod = Constants.METHOD_DIRECT_DEBITING;
			break;

			case Constants.RO_REFUND_ACCOUNT:
				this._ccReqType = Constants.CCRT_EVENT_REQUEST;
				this._reqMethod = Constants.METHOD_REFUND_ACCOUNT;
			break;

			case Constants.RO_CHECK_BALANCE:
				this._ccReqType = Constants.CCRT_EVENT_REQUEST;
				this._reqMethod = Constants.METHOD_CHECK_BALANCE;
			break;

			case Constants.RO_PRICE_ENQUERY:
				this._ccReqType = Constants.CCRT_EVENT_REQUEST;
				this._reqMethod = Constants.METHOD_PRICE_ENQUERY;
			break;

			default:
				throw new IllegalArgumentException("Invalid req type");
		}
	}

	public long getAuthApplicationId() {
		return this._authAppId;
	}

	public short getCCRequestType() {
		return this._ccReqType;
	}

	public long getCCRequestNumber() {
		logger.error("Not supported by stack");
		return -1;
	}

	public ServiceInformation getServiceInformation() {
		return this._servInfo;
	}

	public void setServiceInformation(ServiceInformation servInfo) {
		if(this._readonly) {
			throw new IllegalStateException("Cannot modify this message");
		}

		this._servInfo = (ServiceInformationImpl)servInfo;
	}

	public int[] getCustomAVPCodes() {
		if(this._avpMap != null) {
			return this._avpMap.getCustomAVPCodes();
		} else {
			return null;
		}
	}

	public CustomAVP getCustomAVP(int code) {
		if(this._avpMap != null) {
			return this._avpMap.getCustomAVP(code);
		} else {
			return null;
		}
	}

	public CustomAVP setCustomAVP(int code, CustomAVP value) {
		if(this._readonly) {
			throw new IllegalStateException("Cannot modify this message");
		}

		if(this._avpMap == null) {
			this._avpMap = new CustomAVPMapImpl();
		}

		return this._avpMap.setCustomAVP(code, value);
	}

	public CustomAVP removeCustomAVP(int code) {
		if(this._readonly) {
			throw new IllegalStateException("Cannot modify this message");
		}

		if(this._avpMap != null) {
			return this._avpMap.removeCustomAVP(code);
		} else {
			return null;
		}
	}

	public CustomAVPMapImpl getCustomAVPMap() {
		return this._avpMap;
	}

	public String getMethod() {
		return this._reqMethod;
	}

	public void setDestination(Object destination)
	{
		if(m_destination==null)
			m_destination= new Destination();
			this.m_destination = (Destination)destination;
	}

	public Object getDestination()
	{
		return this.m_destination;
	}

}
