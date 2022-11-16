package com.camel.CAPMsg;

/**
 * This class have parameters for iNbandInfo used in 
 * PC and PA.
 * @author nkumar
 *
 */
public class SasCapInbandInfoDataType {

	/** This is the enum of type SasCapMsgIdEnum.Default value is ELEMENTRY_MSG_ID.
	 *  If set the value  ELEMENTRY_MSG_ID then set the elementryMsgId parameter of CPB.
	 *  If set the value  ELEMENTRY_MSG_ID_LIST then set the elementryMsgIdList parameter of CPB.
	 *  If set the value  VARIABLEMSG then set the variableMsg parameter of CPB.
	 */
	public SasCapMsgIdEnum msgId = SasCapMsgIdEnum.ELEMENTRY_MSG_ID;
	
	/** The numberOfRepetitions used in iNbandInfo. It is optional field */
	public Integer numberOfRepetitions ;
	
	/** The duration used in iNbandInfo. It is an optional field */
	public Integer duration ;
	
	/** The interval used in iNbandInfo. It is an optional field */
	public Integer interval ;
	
	public boolean isNumberOfRepetitionsPresent(){
		return numberOfRepetitions != null ;
	}
	
	public boolean isDurationPresent(){
		return duration != null ;
	}
	
	public boolean isIntervalPresent(){
		return interval != null ;
	}
	
	public boolean isMsgIdPresent(){
		return msgId != null ;
	}
}
