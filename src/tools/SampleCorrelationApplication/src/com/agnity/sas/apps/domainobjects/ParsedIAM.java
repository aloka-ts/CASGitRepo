package com.agnity.sas.apps.domainobjects;

import com.genband.isup.datatypes.FwCallIndicators;
import com.genband.isup.datatypes.NatOfConnIndicators;
import com.genband.isup.enumdata.TransmissionMedReqEnum;

public class ParsedIAM {

	private NatOfConnIndicators noi;
	private FwCallIndicators fw;
	private String callingPartyNum;
	private TransmissionMedReqEnum tmr;
	private String calledpartyNum;

	public ParsedIAM(NatOfConnIndicators noi, FwCallIndicators fw,
			String callingPartyNum, TransmissionMedReqEnum tmr,
			String calledpartyNum) {
		this.setNoi(noi);
		this.setFw(fw);
		this.setTmr(tmr);
		this.setCalledpartyNum(calledpartyNum);
		this.setCallingPartyNum(callingPartyNum);


	}

	/**
	 * @param noi the noi to set
	 */
	public void setNoi(NatOfConnIndicators noi) {
		this.noi = noi;
	}

	/**
	 * @return the noi
	 */
	public NatOfConnIndicators getNoi() {
		return noi;
	}

	/**
	 * @param fw the fw to set
	 */
	public void setFw(FwCallIndicators fw) {
		this.fw = fw;
	}

	/**
	 * @return the fw
	 */
	public FwCallIndicators getFw() {
		return fw;
	}

	/**
	 * @param callingPartyNum the callingPartyNum to set
	 */
	public void setCallingPartyNum(String callingPartyNum) {
		this.callingPartyNum = callingPartyNum;
	}

	/**
	 * @return the callingPartyNum
	 */
	public String getCallingPartyNum() {
		return callingPartyNum;
	}

	/**
	 * @param tmr the tmr to set
	 */
	public void setTmr(TransmissionMedReqEnum tmr) {
		this.tmr = tmr;
	}

	/**
	 * @return the tmr
	 */
	public TransmissionMedReqEnum getTmr() {
		return tmr;
	}

	/**
	 * @param calledpartyNum the calledpartyNum to set
	 */
	public void setCalledpartyNum(String calledpartyNum) {
		this.calledpartyNum = calledpartyNum;
	}

	/**
	 * @return the calledpartyNum
	 */
	public String getCalledpartyNum() {
		return calledpartyNum;
	}

}
