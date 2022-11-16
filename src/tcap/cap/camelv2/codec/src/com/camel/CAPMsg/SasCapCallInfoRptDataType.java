package com.camel.CAPMsg;

import java.util.List;

/**
 * This class have parameters for Call Information Report 
 * operation.
 * @author nkumar
 *
 */
public class SasCapCallInfoRptDataType {

	/** This field contains legId.The legId will be either "01" or "02" */
	String legId ;
	
	/** This filed contains list of SasCapReqInfoDataType */
	List<SasCapReqInfoDataType> reqInfoList ;

	public String getLegId() {
		return legId;
	}

	public void setLegId(String legId) {
		this.legId = legId;
	}

	public List<SasCapReqInfoDataType> getReqInfoList() {
		return reqInfoList;
	}

	public void setReqInfoList(List<SasCapReqInfoDataType> reqInfoList) {
		this.reqInfoList = reqInfoList;
	}
	public boolean isLegIdPresent(){
		return legId != null ;
	}
	
}
