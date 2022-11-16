/**
 * Filename:	CreditControlAnswerImpl.java
 * Created On:	16-Oct-2006
 */

package com.baypackets.ase.ra.ro.stackif;

import java.util.List;
import java.util.LinkedList;
import java.util.Iterator;
import java.util.Map;

import org.apache.log4j.Logger;

import com.condor.apncommon.DiameterBaseInfo;
import com.condor.ro.roclient.response.*;

import com.baypackets.ase.ra.ro.util.UnmodifiableIterator;
import com.baypackets.ase.ra.ro.impl.RoSession;
import com.baypackets.ase.resource.Request;
import com.baypackets.ase.ra.ro.*;

public class CreditControlAnswerImpl
	extends AbstractCreditControlMessage
	implements CreditControlAnswer {

	private static Logger logger = Logger.getLogger(CreditControlAnswerImpl.class);

	private long _resultCode = -1;
	private CreditControlRequestImpl _request;
	private short _sessFailover = -1;
	private List _mulServCC;
	private short _ccFailureHandling = -1;
	private List _redHostList;
	private short _redHostUsage = -1;
	private long _redMaxCacheTime = -1;
	private Map _failedAVPs;

	public CreditControlAnswerImpl(	DiameterBaseInfo stackObj,
									RoSession session,
									CreditControlRequestImpl req) {
		super(session, stackObj);

		this._request = req;

		if(stackObj instanceof RoFirstInterogationRes) {
			RoFirstInterogationRes resp = (RoFirstInterogationRes)stackObj;

			// Copy Result Code
			this._resultCode = resp.getResultCode();

			// Copy CC Failure Handling
			if(resp.getIsCCFailHdl()) {
				this._ccFailureHandling = (short)resp.getCCFailHdl();
			}

			// Copy Multiple Services Credit Control
			int num = (new Byte(resp.getNoOfMulServicesCC())).intValue();
			if(num > 0) {
				this._mulServCC = new LinkedList();
				for(int idx = 0; idx < num; ++idx) {
					this._mulServCC.add(
						new CCAMultipleServicesCreditControlImpl(resp.getAAACCAMulServices(idx)));
				}
			}

			// Note :- Condor stack does not support other fields
		} else if(stackObj instanceof RoUpdateInterogationRes) {
			RoUpdateInterogationRes resp = (RoUpdateInterogationRes)stackObj;

			// Copy Result Code
			this._resultCode = resp.getResultCode();

			// Copy Multiple Services Credit Control
			int num = (new Byte(resp.getNoOfMulServicesCC())).intValue();
			if(num > 0) {
				this._mulServCC = new LinkedList();
				for(int idx = 0; idx < num; ++idx) {
					this._mulServCC.add(
						new CCAMultipleServicesCreditControlImpl(resp.getAAACCAMulServices(idx)));
				}
			}

			// Note :- Condor stack does not support other fields
		} else if(stackObj instanceof RoFinalInterogationRes) {
			RoFinalInterogationRes resp = (RoFinalInterogationRes)stackObj;

			// Copy Result Code
			this._resultCode = resp.getResultCode();

			// Note :- Condor stack does not support other fields
		} else if(stackObj instanceof RoDirectDebitingResponse) {
			RoDirectDebitingResponse resp = (RoDirectDebitingResponse)stackObj;

			// Copy Result Code
			this._resultCode = resp.getResultCode();

			// Copy Multiple Services Credit Control
			int num = (new Byte(resp.getNoOfMulServicesCC())).intValue();
			if(num > 0) {
				this._mulServCC = new LinkedList();
				for(int idx = 0; idx < num; ++idx) {
					this._mulServCC.add(
						new CCAMultipleServicesCreditControlImpl(resp.getAAACCAMulServices(idx)));
				}
			}

			// Note :- Condor stack does not support other fields
		} else if(stackObj instanceof RoRefundServiceResponse) {
			RoRefundServiceResponse resp = (RoRefundServiceResponse)stackObj;

			// Copy Result Code
			this._resultCode = resp.getResultCode();

			// Note :- Condor stack does not support other fields
		} else if(stackObj instanceof RoBalanceCheckResponse) {
			RoBalanceCheckResponse resp = (RoBalanceCheckResponse)stackObj;

			// Copy Result Code
			this._resultCode = resp.getResultCode();

			// Note :- Condor stack does not support other fields
		} else if(stackObj instanceof RoServicePriceEnqResponse) {
			RoServicePriceEnqResponse resp = (RoServicePriceEnqResponse)stackObj;

			// Copy Result Code
			this._resultCode = resp.getResultCode();

			// Note :- Condor stack does not support other fields
		}
	}

	public long getResultCode() {
		return this._resultCode;
	}

	public Request getRequest() {
		return this._request;
	}

	public short getCCSessionFailover() {
		return this._sessFailover;
	}

	public Iterator getMultipleServicesCreditControlList() {
		if(this._mulServCC != null) {
			return new UnmodifiableIterator(this._mulServCC.iterator());
		} else {
			return null;
		}
	}

	public short getCreditControlFailureHandling() {
		return this._ccFailureHandling;
	}

	public Iterator getRedirectHostList() {
		if(this._redHostList != null) {
			return new UnmodifiableIterator(this._redHostList.iterator());
		} else {
			return null;
		}
	}

	public short getRedirectHostUsage() {
		return this._redHostUsage;
	}

	public long getRedirectMaxCacheTime() {
		return this._redMaxCacheTime;
	}

	public int[] getFailedAVPCodes() {
		if(this._failedAVPs != null) {
			int[] codeList = new int[this._failedAVPs.keySet().size()];

			Iterator iter = this._failedAVPs.keySet().iterator();
			int i = 0;
			while(iter.hasNext()) {
				codeList[i++] = ((Integer)iter.next()).intValue();
			}

			return codeList;
		} else {
			return null;
		}
	}

	public Object getFailedAVP(int code) {
		if(this._failedAVPs != null) {
			return this._failedAVPs.get(new Integer(code));
		} else {
			return null;
		}
	}
}
