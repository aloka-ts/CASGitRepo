package com.genband.isup.operations;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;

import org.apache.log4j.Logger;

import com.genband.isup.messagetypes.ACMMessage;
import com.genband.isup.messagetypes.ANMMessage;
import com.genband.isup.messagetypes.CHGMessage;
import com.genband.isup.messagetypes.CPGMessage;
import com.genband.isup.messagetypes.IAMMessage;
import com.genband.isup.messagetypes.RELMessage;
import com.genband.isup.messagetypes.RLCMessage;
import com.genband.isup.messagetypes.SUSRESMessage;

/**
 * This class contains methods for encoding of ISUP messages
 * @author vgoel
 *
 */

public class ISUPEncoder
{	
	private static Logger logger = Logger.getLogger(ISUPEncoder.class);

	/**
	 * This method will encode IAM message based on the protocol set by codec library user in IAM. 
	 * By default ITUT shall be consider. 
	 * @param iam
	 * @return byte[]
	 */
	public static byte[] encodeIAM(IAMMessage iam)
	{
		logger.info("encodeIAM:Enter, protocol" + iam.getProtocol());

		int fixLength           = 6; 
		int initialOffsetLength = 2;

		int varLength = 0;
		varLength += iam.getCalledPartyNumberBytes().length ;
		int totalVarLength = varLength + 1;		//length of variable part array, 1 for oplength

		// If the IAM is for ANSI then consider variable length for User Service Info 
		int varLength1 = 0;
		if (iam.getProtocol() == ISUPConstants.ISUP_ANSI) {
			varLength1 = iam.getUserServiceInfoBytes().length;
			totalVarLength += varLength1 + 1;

			fixLength           = 5; // TMR will not be encoded. so reducing by 1 byte
			initialOffsetLength = 3; // Ist Mandatory, 2nd Mandatory and 3rd Optional Param
		}


		boolean isOptParamsExist = false;
		int optLength = 0;
		LinkedList<Byte> optList = new LinkedList<Byte>();
		if(iam.getCallingPartyNumberBytes() != null){
			isOptParamsExist = true;
			optList.add((byte)(ISUPConstants.CODE_CALLING_PARTY_NUM));
			optList.add((byte)(iam.getCallingPartyNumberBytes().length));
			for(int i=0; i<iam.getCallingPartyNumberBytes().length; i++)
				optList.add(iam.getCallingPartyNumberBytes()[i]);
		}

		// Parameters which are allowed in ITU-T mode only. 
		if (iam.getProtocol() == ISUPConstants.ISUP_ITUT) {
			if(iam.getCorrelationIdBytes() != null){
				isOptParamsExist = true;
				optList.add((byte)(ISUPConstants.CODE_CORRELATION_ID));
				optList.add((byte)(iam.getCorrelationIdBytes().length));
				for(int i=0; i<iam.getCorrelationIdBytes().length; i++)
					optList.add(iam.getCorrelationIdBytes()[i]);
			}

			if(iam.getScfIdBytes() != null){
				isOptParamsExist = true;
				optList.add((byte)(ISUPConstants.CODE_SCF_ID));
				optList.add((byte)(iam.getScfIdBytes().length));
				for(int i=0; i<iam.getScfIdBytes().length; i++)
					optList.add(iam.getScfIdBytes()[i]);
			}
			if(iam.getCalledINNumberBytes() != null){
				isOptParamsExist = true;
				optList.add((byte)(ISUPConstants.CODE_CALLED_IN_NUMBER));
				optList.add((byte)(iam.getCalledINNumberBytes().length));
				for(int i=0; i<iam.getCalledINNumberBytes().length; i++)
					optList.add(iam.getCalledINNumberBytes()[i]);
			}

			if(iam.getAdditionalPartyCategoryBytes() != null){
				isOptParamsExist = true;
				optList.add((byte)(ISUPConstants.CODE_ADDITIONAL_PARTY_CAT));
				optList.add((byte)(iam.getAdditionalPartyCategoryBytes().length));
				for(int i=0; i<iam.getAdditionalPartyCategoryBytes().length; i++)
					optList.add(iam.getAdditionalPartyCategoryBytes()[i]);
			}
			if(iam.getCarrierInfoTransferBytes() != null){
				isOptParamsExist = true;
				optList.add((byte)(ISUPConstants.CODE_CARRIER_INFO_TRFR));
				optList.add((byte)(iam.getCarrierInfoTransferBytes().length));
				for(int i=0; i<iam.getCarrierInfoTransferBytes().length; i++)
					optList.add(iam.getCarrierInfoTransferBytes()[i]);
			}
			if(iam.getDpcInfoBytes() != null){
				isOptParamsExist = true;
				optList.add((byte)(ISUPConstants.CODE_DPC_INFO));
				optList.add((byte)(iam.getDpcInfoBytes().length));
				for(int i=0; i<iam.getDpcInfoBytes().length; i++)
					optList.add(iam.getDpcInfoBytes()[i]);
			}
			if(iam.getChargeAreaInformationBytes() != null){
				isOptParamsExist = true;
				optList.add((byte)(ISUPConstants.CODE_CHARGE_AREA_INFO));
				optList.add((byte)(iam.getChargeAreaInformationBytes().length));
				for(int i=0; i<iam.getChargeAreaInformationBytes().length; i++)
					optList.add(iam.getChargeAreaInformationBytes()[i]);
			}
			if(iam.getServiceActivationBytes() != null){
				isOptParamsExist = true;
				optList.add((byte)(ISUPConstants.CODE_SERVICE_ACTIVATION));
				optList.add((byte)(iam.getServiceActivationBytes().length));
				for(int i=0; i<iam.getServiceActivationBytes().length; i++)
					optList.add(iam.getServiceActivationBytes()[i]);
			}
		}

		// Parameters allowed in both ANSI and ITUT mode. 
		if(iam.getGenericNumberBytes() != null){
			isOptParamsExist = true;
			optList.add((byte)(ISUPConstants.CODE_GENERIC_NUMBER));
			optList.add((byte)(iam.getGenericNumberBytes().length));
			for(int i=0; i<iam.getGenericNumberBytes().length; i++)
				optList.add(iam.getGenericNumberBytes()[i]);
		}
		if(iam.getAccessTransportBytes() != null){
			isOptParamsExist = true;
			optList.add((byte)(ISUPConstants.CODE_ACCESS_TRANSPORT));
			optList.add((byte)(iam.getAccessTransportBytes().length));
			for(int i=0; i<iam.getAccessTransportBytes().length; i++)
				optList.add(iam.getAccessTransportBytes()[i]);
		}

		// Parameters allowed in ANSI only
		if(iam.getProtocol() == ISUPConstants.ISUP_ANSI) {
			if(iam.getJurisdictionInfoByte() != null) {
				optList.add((byte)(ISUPConstants.CODE_JURISDICTION_INFO));
				optList.add((byte)(iam.getJurisdictionInfoByte().length));
				for(int i=0; i<iam.getJurisdictionInfoByte().length; i++)
					optList.add(iam.getJurisdictionInfoByte()[i]);
			}
			if(iam.getChargeNumberBytes() != null){
				isOptParamsExist = true;
				optList.add((byte)(ISUPConstants.CODE_CHARGE_NUM));
				optList.add((byte)(iam.getChargeNumberBytes().length));
				for(int i=0; i<iam.getChargeNumberBytes().length; i++)
					optList.add(iam.getChargeNumberBytes()[i]);
			}
		}

		if(iam.getContractorNumberBytes() != null){
			isOptParamsExist = true;
			optList.add((byte)(ISUPConstants.CODE_CONTRACTOR_NUMBER));
			optList.add((byte)(iam.getContractorNumberBytes().length));
			for(int i=0; i<iam.getContractorNumberBytes().length; i++)
				optList.add(iam.getContractorNumberBytes()[i]);
		}
		if(iam.getRedirectingNumberBytes() != null){
			isOptParamsExist = true;
			optList.add((byte)(ISUPConstants.CODE_REDIRECTING_NUMBER));
			optList.add((byte)(iam.getRedirectingNumberBytes().length));
			for(int i=0; i<iam.getRedirectingNumberBytes().length; i++)
				optList.add(iam.getRedirectingNumberBytes()[i]);
		}
		if(iam.getOriginalCalledNumberBytes() != null){
			isOptParamsExist = true;
			optList.add((byte)(ISUPConstants.CODE_ORIGINAL_CALLED_NUMBER));
			optList.add((byte)(iam.getOriginalCalledNumberBytes().length));
			for(int i=0; i<iam.getOriginalCalledNumberBytes().length; i++)
				optList.add(iam.getOriginalCalledNumberBytes()[i]);
		}
		if(iam.getRedirectionInfoBytes() != null){
			isOptParamsExist = true;
			optList.add((byte)(ISUPConstants.CODE_REDIRECTION_INFO));
			optList.add((byte)(iam.getRedirectionInfoBytes().length));
			for(int i=0; i<iam.getRedirectionInfoBytes().length; i++)
				optList.add(iam.getRedirectionInfoBytes()[i]);
		}
		
		if(iam.getUserServiceInfoBytes() != null){
			isOptParamsExist = true;
			optList.add((byte)(ISUPConstants.CODE_USER_SERVICE_INFO));
			optList.add((byte)(iam.getUserServiceInfoBytes().length));
			for(int i=0; i<iam.getUserServiceInfoBytes().length; i++)
				optList.add(iam.getUserServiceInfoBytes()[i]);
		}
		
		//for unknown opt params
		if(iam.getOtherOptParams() != null) {
			isOptParamsExist = true;
			Iterator<Map.Entry<Integer,byte[]>> it = iam.getOtherOptParams().entrySet().iterator();
			while(it.hasNext()) {
				Map.Entry<Integer, byte[]> param = it.next();
				optList.add((byte)(param.getKey().intValue()));
				optList.add((byte)param.getValue().length);
				for(int i=0; i<param.getValue().length; i++)
					optList.add(param.getValue()[i]);
			}			
		}
		optLength += optList.size();

		int totalOptLength  = 0;
		int optParamsOffset = 0;

		if(isOptParamsExist){
			totalOptLength = optLength + 1;		     //2 for opcode and oplength, 1 for end of opt params param
			optParamsOffset = totalVarLength + 1;	 //offset value of opt params
		} 

		int optParamsIndex = fixLength + initialOffsetLength + totalVarLength;	//array index value for starting of opt params (starts from 0)

		//creating byte[]
		int totalLength = fixLength + initialOffsetLength + totalVarLength + totalOptLength;
		logger.info("encodeIAM:total length: " + totalLength);

		int index = 0;
		byte[] buffer = new byte[totalLength];		
		buffer[index++] = iam.getMessageTypeBytes()[0];
		buffer[index++] = iam.getNatureOfConnIndicatorsBytes()[0];
		buffer[index++] = iam.getForwardCallIndicatorsBytes()[0];
		buffer[index++] = iam.getForwardCallIndicatorsBytes()[1];
		buffer[index++] = iam.getCallingPartyCategoryBytes()[0];

		if (iam.getProtocol() == ISUPConstants.ISUP_ITUT) {
			buffer[index++] = iam.getTmrBytes()[0];
			buffer[index++] = 0x2;									//offset for mandatory var param
			buffer[index++] = (byte)(optParamsOffset);			    //offset for start of opt params
			buffer[index++] = (byte)(varLength);					//length of mandatory var param

			for(int i=0,j=index; i<varLength; i++,j++){	            //putting var params
				buffer[j] = iam.getCalledPartyNumberBytes()[i];
			}
		} else {
			// ANSI
			buffer[index++] = 0x3;                                  //offset for 1st mandatory var param
			buffer[index++] = (byte) (varLength1 + 3);              //offset for 2nd mandatory var param
			buffer[index++] = (byte)(optParamsOffset);			    //offset for opt params
			buffer[index++] = (byte)(varLength1);                   // Length of 1st mandatory param

			for(int i=0,j=index; i<varLength1; i++,j++){			//putting User Service Info (Ist mandatory param)
				buffer[j] = iam.getUserServiceInfoBytes()[i];
			}
			index += varLength1;

			buffer[index++] = (byte) (varLength);                   //length of 2nd mandatory var param

			for( int i=0,j=index; i<varLength; i++,j++){	        //putting called party number (IInd mandatory param)
				buffer[j] = iam.getCalledPartyNumberBytes()[i];
			}
		}

		Object[] optBytes = optList.toArray();
		for(int i=0,j=optParamsIndex; i<optBytes.length; i++,j++){	//putting opt and other opt data
			buffer[j] = (Byte)(optBytes[i]);
		}

		if(isOptParamsExist)				
			buffer[totalLength-1] = 0x00;				//putting end of opt params param

		logger.info("encodeIAM:Exit");
		return buffer;
	}

	/**
	 * This method will encode ACM message
	 * @param acm
	 * @return byte[]
	 */
	public static byte[] encodeACM(ACMMessage acm)
	{
		logger.info("encodeACM:Enter");

		int fixLength = 3;
		int totalVarLength = 0;		//length of variable part array is 0

		boolean isOptParamsExist = false;
		int optLength = 0;
		LinkedList<Byte> optList = new LinkedList<Byte>();
		if(acm.getCauseIndicatorsBytes() != null){
			isOptParamsExist = true;
			optList.add((byte)(ISUPConstants.CODE_CAUSE_IND));
			optList.add((byte)(acm.getCauseIndicatorsBytes().length));
			for(int i=0; i<acm.getCauseIndicatorsBytes().length; i++)
				optList.add(acm.getCauseIndicatorsBytes()[i]);
		}
		if(acm.getOptBwCallIndicatorsBytes() != null){
			isOptParamsExist = true;
			optList.add((byte)(ISUPConstants.CODE_OPT_BW_CALL_IND));
			optList.add((byte)(acm.getOptBwCallIndicatorsBytes().length));
			for(int i=0; i<acm.getOptBwCallIndicatorsBytes().length; i++)
				optList.add(acm.getOptBwCallIndicatorsBytes()[i]);
		}
		if(acm.getCarrierInfoTrfrBytes() != null){
			isOptParamsExist = true;
			optList.add((byte)(ISUPConstants.CODE_CARRIER_INFO_TRFR));
			optList.add((byte)(acm.getCarrierInfoTrfrBytes().length));
			for(int i=0; i<acm.getCarrierInfoTrfrBytes().length; i++)
				optList.add(acm.getCarrierInfoTrfrBytes()[i]);
		}
		if(acm.getChargeAreaInfoBytes() != null){
			isOptParamsExist = true;
			optList.add((byte)(ISUPConstants.CODE_CHARGE_AREA_INFO));
			optList.add((byte)(acm.getChargeAreaInfoBytes().length));
			for(int i=0; i<acm.getChargeAreaInfoBytes().length; i++)
				optList.add(acm.getChargeAreaInfoBytes()[i]);
		}		//add here for other opt params

		//for unknown opt params
		if(acm.getOtherOptParams() != null) {
			isOptParamsExist = true;
			Iterator<Map.Entry<Integer,byte[]>> it = acm.getOtherOptParams().entrySet().iterator();
			while(it.hasNext()) {
				Map.Entry<Integer, byte[]> param = it.next();
				optList.add((byte)(param.getKey().intValue()));
				optList.add((byte)param.getValue().length);
				for(int i=0; i<param.getValue().length; i++)
					optList.add(param.getValue()[i]);
			}			
		}
		optLength += optList.size();

		int totalOptLength = 0;
		int optParamsOffset = 0;
		if(isOptParamsExist){
			totalOptLength = optLength + 1;		//2 for opcode and oplength, 1 for end of opt params param
			optParamsOffset = totalVarLength + 1;					//offset value of opt params
		} 
		//1 is being added for including the optional parameter offset
		int optParamsIndex = fixLength + 1 + totalVarLength;						//array index value for starting of opt params (starts from 0)

		//creating byte[]
		int totalLength = fixLength+1+totalVarLength+totalOptLength;		//1 is there in place of 2 as varoffset is not there in acm
		logger.info("encodeACM:total length: " + totalLength);
		byte[] buffer = new byte[totalLength];		
		buffer[0] = acm.getMessageTypeBytes()[0];
		buffer[1] = acm.getBackwardCallIndicatorsBytes()[0];
		buffer[2] = acm.getBackwardCallIndicatorsBytes()[1];

		//Commented as there is no variable parameters in ACM
		//buffer[3] = 0x00;											//offset for var params
		buffer[3] = (byte)(optParamsOffset);						//offset for opt params

		Object[] optBytes = optList.toArray();
		for(int i=0,j=optParamsIndex; i<optBytes.length; i++,j++){	////putting opt and other opt data
			buffer[j] = (Byte)(optBytes[i]);
		}

		if(isOptParamsExist)				
			buffer[totalLength-1] = 0x00;				//putting end of opt params param

		logger.info("encodeACM:Exit");
		return buffer;
	}


	/**
	 * This method will encode ANM message
	 * @param anm
	 * @return byte[]
	 */
	public static byte[] encodeANM(ANMMessage anm)
	{
		logger.info("encodeANM:Enter");

		int fixLength = 1;
		int totalVarLength = 0;		//length of variable part array is 0

		boolean isOptParamsExist = false;
		int optLength = 0;
		LinkedList<Byte> optList = new LinkedList<Byte>();
		if(anm.getBackwardCallIndicatorsBytes() != null){
			isOptParamsExist = true;
			optList.add((byte)(ISUPConstants.CODE_BW_CALL_IND));
			optList.add((byte)(anm.getBackwardCallIndicatorsBytes().length));
			for(int i=0; i<anm.getBackwardCallIndicatorsBytes().length; i++)
				optList.add(anm.getBackwardCallIndicatorsBytes()[i]);
		}		//add here for other opt params

		//for unknown opt params
		if(anm.getOtherOptParams() != null) {
			isOptParamsExist = true;
			Iterator<Map.Entry<Integer,byte[]>> it = anm.getOtherOptParams().entrySet().iterator();
			while(it.hasNext()) {
				Map.Entry<Integer, byte[]> param = it.next();
				optList.add((byte)(param.getKey().intValue()));
				optList.add((byte)param.getValue().length);
				for(int i=0; i<param.getValue().length; i++)
					optList.add(param.getValue()[i]);
			}			
		}
		optLength += optList.size();

		int totalOptLength = 0;
		int optParamsOffset = 0;
		if(isOptParamsExist){
			totalOptLength = optLength + 1;		//1 for end of opt params param
			optParamsOffset = totalVarLength + 1;					//offset value of opt params
		} 
		//previously 2 is added as it is counting for variable as well, since there is no
		//variable hence 1 is added
		int optParamsIndex = fixLength + 1 + totalVarLength;						//array index value for starting of opt params (starts from 0)

		//creating byte[]
		//2 was added before for varoffseet and optoffset since there is not var offset, 1 is being added
		int totalLength = fixLength+1+totalVarLength+totalOptLength;		//2 for varoffset and optoffset
		logger.info("encodeANM:total length: " + totalLength);
		byte[] buffer = new byte[totalLength];		
		buffer[0] = anm.getMessageTypeBytes()[0];
		//There are no variable parameters in the ANMMessage this commenting this part 
		//buffer[1] = 0x00;											//offset for var params
		buffer[1] = (byte)(optParamsOffset);						//offset for opt params

		Object[] optBytes = optList.toArray();
		for(int i=0,j=optParamsIndex; i<optBytes.length; i++,j++){	//putting opt and other opt data
			buffer[j] = (Byte)(optBytes[i]);
		}

		if(isOptParamsExist)				
			buffer[totalLength-1] = 0x00;				//putting end of opt params param

		logger.info("encodeANM:Exit");
		return buffer;
	}


	/**
	 * This method will encode CPG message
	 * @param cpg
	 * @return byte[]
	 */
	public static byte[] encodeCPG(CPGMessage cpg)
	{
		logger.info("encodeCPG:Enter");

		int fixLength = 2;
		int totalVarLength = 0;		//length of variable part array is 0

		boolean isOptParamsExist = false;
		int optLength = 0;
		LinkedList<Byte> optList = new LinkedList<Byte>();
		//add here for other opt params

		if(cpg.getBwCallIndicatorsBytes() != null){
			isOptParamsExist = true;
			optList.add((byte)(ISUPConstants.CODE_BW_CALL_IND));
			optList.add((byte)(cpg.getBwCallIndicatorsBytes()).length);
			for(int i=0; i<cpg.getBwCallIndicatorsBytes().length; i++)
				optList.add(cpg.getBwCallIndicatorsBytes()[i]);
		}
		if(cpg.getCarrierInfoTransferBytes() != null){
			isOptParamsExist = true;
			optList.add((byte)(ISUPConstants.CODE_CARRIER_INFO_TRFR));
			optList.add((byte)(cpg.getCarrierInfoTransferBytes().length));
			for(int i=0; i<cpg.getCarrierInfoTransferBytes().length; i++)
				optList.add(cpg.getCarrierInfoTransferBytes()[i]);
		}
		if(cpg.getChargeAreaInfoBytes() != null){
			isOptParamsExist = true;
			optList.add((byte)(ISUPConstants.CODE_CHARGE_AREA_INFO));
			optList.add((byte)(cpg.getChargeAreaInfoBytes().length));
			for(int i=0; i<cpg.getChargeAreaInfoBytes().length; i++)
				optList.add(cpg.getChargeAreaInfoBytes()[i]);
		}
		if(cpg.getCauseIndicatorBytes() != null){
			isOptParamsExist = true;
			optList.add((byte)(ISUPConstants.CODE_CAUSE_IND));
			optList.add((byte)(cpg.getCauseIndicatorBytes().length));
			for(int i=0; i<cpg.getCauseIndicatorBytes().length; i++)
				optList.add(cpg.getCauseIndicatorBytes()[i]);
		}
		//for unknown opt params
		if(cpg.getOtherOptParams() != null) {
			isOptParamsExist = true;
			Iterator<Map.Entry<Integer,byte[]>> it = cpg.getOtherOptParams().entrySet().iterator();
			while(it.hasNext()) {
				Map.Entry<Integer, byte[]> param = it.next();
				optList.add((byte)(param.getKey().intValue()));
				optList.add((byte)param.getValue().length);
				for(int i=0; i<param.getValue().length; i++)
					optList.add(param.getValue()[i]);
			}			
		}
		optLength += optList.size();

		int totalOptLength = 0;
		int optParamsOffset = 0;
		if(isOptParamsExist){
			totalOptLength = optLength + 1;		//2 for opcode and oplength, 1 for end of opt params param
			optParamsOffset = totalVarLength + 1;					//offset value of opt params
		} 
		//Since there is no variable param to CPG, making 2 to 1
		int optParamsIndex = fixLength + 1 + totalVarLength;						//array index value for starting of opt params (starts from 0)

		//creating byte[]
		//Since there is no variable param to CPG, making 2 to 1
		int totalLength = fixLength+1+totalVarLength+totalOptLength;		//2 for varoffset and optoffset
		logger.info("encodeCPG:total length: " + totalLength);
		byte[] buffer = new byte[totalLength];		
		buffer[0] = cpg.getMessageTypeBytes()[0];
		buffer[1] = cpg.getEventInfoBytes()[0];

		//Since there is no variable parameters hence removing this
		//buffer[2] = 0x00;											//offset for var params
		buffer[2] = (byte)(optParamsOffset);						//offset for opt params

		Object[] optBytes = optList.toArray();
		for(int i=0,j=optParamsIndex; i<optBytes.length; i++,j++){	////putting opt and other opt data
			buffer[j] = (Byte)(optBytes[i]);
		}

		if(isOptParamsExist)				
			buffer[totalLength-1] = 0x00;				//putting end of opt params param

		logger.info("encodeCPG:Exit");
		return buffer;
	}


	/**
	 * This method will encode RLC message
	 * @param rlc
	 * @return byte[]
	 */
	public static byte[] encodeRLC(RLCMessage rlc)
	{
		logger.info("encodeRLC:Enter");

		int fixLength = 1;
		int totalVarLength = 0;		//length of variable part array is 0

		boolean isOptParamsExist = false;
		int optLength = 0;
		LinkedList<Byte> optList = new LinkedList<Byte>();
		//add here for other opt params

		//for unknown opt params
		if(rlc.getOtherOptParams() != null) {
			isOptParamsExist = true;
			Iterator<Map.Entry<Integer,byte[]>> it = rlc.getOtherOptParams().entrySet().iterator();
			while(it.hasNext()) {
				Map.Entry<Integer, byte[]> param = it.next();
				optList.add((byte)(param.getKey().intValue()));
				optList.add((byte)param.getValue().length);
				for(int i=0; i<param.getValue().length; i++)
					optList.add(param.getValue()[i]);
			}			
		}
		optLength += optList.size();

		int totalOptLength = 0;
		int optParamsOffset = 0;
		if(isOptParamsExist){
			totalOptLength = optLength + 1;		//2 for opcode and oplength, 1 for end of opt params param
			optParamsOffset = totalVarLength + 1;					//offset value of opt params
		} 
		//Since there is no variable parameters 2 is being replaced by 1 
		int optParamsIndex = fixLength + 1 + totalVarLength;						//array index value for starting of opt params (starts from 0)

		//creating byte[]
		//Since there is no variable parameters 2 is being replaced by 1
		int totalLength = fixLength+1+totalVarLength+totalOptLength;		//2 for varoffset and optoffset
		logger.info("encodeRLC:total length: " + totalLength);
		byte[] buffer = new byte[totalLength];		
		buffer[0] = rlc.getMessageTypeBytes()[0];

		//buffer[1] = 0x00;											//offset for var params
		buffer[1] = (byte)(optParamsOffset);						//offset for opt params

		Object[] optBytes = optList.toArray();
		for(int i=0,j=optParamsIndex; i<optBytes.length; i++,j++){	////putting opt and other opt data
			buffer[j] = (Byte)(optBytes[i]);
		}

		if(isOptParamsExist)				
			buffer[totalLength-1] = 0x00;				//putting end of opt params param

		logger.info("encodeRLC:Exit");
		return buffer;
	}


	/**
	 * This method will encode REL message
	 * @param rel
	 * @return byte[]
	 */
	public static byte[] encodeREL(RELMessage rel)
	{
		logger.info("encodeREL:Enter");

		int fixLength = 1;

		int varLength = 0;
		if (rel.getCauseBytes() != null) {
			varLength += rel.getCauseBytes().length;
		}
		int totalVarLength = varLength + 1;		//length of variable part array, 1 for oplength

		boolean isOptParamsExist = false;
		int optLength = 0;
		LinkedList<Byte> optList = new LinkedList<Byte>();
		//add here for other opt params

		//for unknown opt params
		if(rel.getOtherOptParams() != null) {
			isOptParamsExist = true;
			Iterator<Map.Entry<Integer,byte[]>> it = rel.getOtherOptParams().entrySet().iterator();
			while(it.hasNext()) {
				Map.Entry<Integer, byte[]> param = it.next();
				optList.add((byte)(param.getKey().intValue()));
				optList.add((byte)param.getValue().length);
				for(int i=0; i<param.getValue().length; i++)
					optList.add(param.getValue()[i]);
			}			
		}
		optLength += optList.size();

		int totalOptLength = 0;
		int optParamsOffset = 0;
		if(isOptParamsExist){
			totalOptLength = optLength + 1;		//2 for opcode and oplength, 1 for end of opt params param
			optParamsOffset = totalVarLength + 1;					//offset value of opt params
		} 
		int optParamsIndex = fixLength + 2 + totalVarLength;						//array index value for starting of opt params (starts from 0)

		//creating byte[]
		int totalLength = fixLength+2+totalVarLength+totalOptLength;		//2 for varoffset and optoffset
		logger.info("encodeREL:total length: " + totalLength);
		byte[] buffer = new byte[totalLength];		
		buffer[0] = rel.getMessageTypeBytes()[0];

		buffer[1] = 0x2;											//offset for var params
		buffer[2] = (byte)(optParamsOffset);						//offset for opt params

		buffer[3] = (byte)(varLength);								//length of var param
		for(int i=0,j=4; i<varLength; i++,j++){						//putting var params
			buffer[j] = rel.getCauseBytes()[i];
		}

		Object[] optBytes = optList.toArray();
		for(int i=0,j=optParamsIndex; i<optBytes.length; i++,j++){	////putting opt and other opt data
			buffer[j] = (Byte)(optBytes[i]);
		}

		if(isOptParamsExist)				
			buffer[totalLength-1] = 0x00;				//putting end of opt params param

		logger.info("encodeREL:Exit");
		return buffer;
	}


	/**
	 * This method will encode SUS message
	 * @param sus
	 * @return byte[]
	 */
	public static byte[] encodeSUS(SUSRESMessage sus)
	{
		logger.info("encodeSUS:Enter");

		int fixLength = 2;

		int totalVarLength = 0;		//length of variable part is 0

		boolean isOptParamsExist = false;
		int optLength = 0;
		LinkedList<Byte> optList = new LinkedList<Byte>();
		//add here for other opt params

		//for unknown opt params
		if(sus.getOtherOptParams() != null) {
			isOptParamsExist = true;
			Iterator<Map.Entry<Integer,byte[]>> it = sus.getOtherOptParams().entrySet().iterator();
			while(it.hasNext()) {
				Map.Entry<Integer, byte[]> param = it.next();
				optList.add((byte)(param.getKey().intValue()));
				optList.add((byte)param.getValue().length);
				for(int i=0; i<param.getValue().length; i++)
					optList.add(param.getValue()[i]);
			}			
		}
		optLength += optList.size();

		int totalOptLength = 0;
		int optParamsOffset = 0;
		if(isOptParamsExist){
			totalOptLength = optLength + 1;		//2 for opcode and oplength, 1 for end of opt params param
			optParamsOffset = totalVarLength + 1;					//offset value of opt params
		} 
		int optParamsIndex = fixLength + 2 + totalVarLength;						//array index value for starting of opt params (starts from 0)

		//creating byte[]
		int totalLength = fixLength+2+totalVarLength+totalOptLength;		//2 for varoffset and optoffset
		logger.info("encodeSUS:total length: " + totalLength);
		byte[] buffer = new byte[totalLength];		
		buffer[0] = sus.getMessageTypeBytes()[0];
		buffer[1] = sus.getSuspendResumeIndBytes()[0];

		buffer[2] = 0x0;											//offset for var params
		buffer[3] = (byte)(optParamsOffset);						//offset for opt params

		Object[] optBytes = optList.toArray();
		for(int i=0,j=optParamsIndex; i<optBytes.length; i++,j++){	////putting opt and other opt data
			buffer[j] = (Byte)(optBytes[i]);
		}

		if(isOptParamsExist)				
			buffer[totalLength-1] = 0x00;				//putting end of opt params param

		logger.info("encodeSUS:Exit");
		return buffer;
	}


	/**
	 * This method will encode RES message
	 * @param res
	 * @return byte[]
	 */
	public static byte[] encodeRES(SUSRESMessage res)
	{
		logger.info("encodeRES:Enter");

		int fixLength = 2;

		int totalVarLength = 0;		//length of variable part is 0

		boolean isOptParamsExist = false;
		int optLength = 0;
		LinkedList<Byte> optList = new LinkedList<Byte>();
		//add here for other opt params

		//for unknown opt params
		if(res.getOtherOptParams() != null) {
			isOptParamsExist = true;
			Iterator<Map.Entry<Integer,byte[]>> it = res.getOtherOptParams().entrySet().iterator();
			while(it.hasNext()) {
				Map.Entry<Integer, byte[]> param = it.next();
				optList.add((byte)(param.getKey().intValue()));
				optList.add((byte)param.getValue().length);
				for(int i=0; i<param.getValue().length; i++)
					optList.add(param.getValue()[i]);
			}			
		}
		optLength += optList.size();

		int totalOptLength = 0;
		int optParamsOffset = 0;
		if(isOptParamsExist){
			totalOptLength = optLength + 1;		//2 for opcode and oplength, 1 for end of opt params param
			optParamsOffset = totalVarLength + 1;					//offset value of opt params
		} 
		int optParamsIndex = fixLength + 2 + totalVarLength;						//array index value for starting of opt params (starts from 0)

		//creating byte[]
		int totalLength = fixLength+2+totalVarLength+totalOptLength;		//2 for varoffset and optoffset
		logger.info("encodeRES:total length: " + totalLength);
		byte[] buffer = new byte[totalLength];		
		buffer[0] = res.getMessageTypeBytes()[0];
		buffer[1] = res.getSuspendResumeIndBytes()[0];

		buffer[2] = 0x0;											//offset for var params
		buffer[3] = (byte)(optParamsOffset);						//offset for opt params

		Object[] optBytes = optList.toArray();
		for(int i=0,j=optParamsIndex; i<optBytes.length; i++,j++){	////putting opt and other opt data
			buffer[j] = (Byte)(optBytes[i]);
		}

		if(isOptParamsExist)				
			buffer[totalLength-1] = 0x00;				//putting end of opt params param

		logger.info("encodeRES:Exit");
		return buffer;
	}


	/**
	 * This method will encode CHG message
	 * @param chg
	 * @return byte[]
	 */
	public static byte[] encodeCHG(CHGMessage chg)
	{
		logger.info("encodeCHG:Enter");

		int fixLength = 2;

		int varLength = 0;
		varLength += chg.getChargingInfoBytes().length ;
		int totalVarLength = varLength + 1;		//length of variable part array, 1 for oplength

		boolean isOptParamsExist = false;
		int optLength = 0;
		LinkedList<Byte> optList = new LinkedList<Byte>();
		//add here for other opt params

		//for unknown opt params
		if(chg.getOtherOptParams() != null) {
			isOptParamsExist = true;
			Iterator<Map.Entry<Integer,byte[]>> it = chg.getOtherOptParams().entrySet().iterator();
			while(it.hasNext()) {
				Map.Entry<Integer, byte[]> param = it.next();
				optList.add((byte)(param.getKey().intValue()));
				optList.add((byte)param.getValue().length);
				for(int i=0; i<param.getValue().length; i++)
					optList.add(param.getValue()[i]);
			}			
		}
		optLength += optList.size();

		int totalOptLength = 0;
		int optParamsOffset = 0;
		if(isOptParamsExist){
			totalOptLength = optLength + 1;		//2 for opcode and oplength, 1 for end of opt params param
			optParamsOffset = totalVarLength + 1;					//offset value of opt params
		} 
		int optParamsIndex = fixLength + 2 + totalVarLength;						//array index value for starting of opt params (starts from 0)

		//creating byte[]
		int totalLength = fixLength+2+totalVarLength+totalOptLength;		//2 for varoffset and optoffset
		logger.info("encodeCHG:total length: " + totalLength);
		byte[] buffer = new byte[totalLength];		
		buffer[0] = chg.getMessageTypeBytes()[0];
		buffer[1] = chg.getChargingInfoCategoryBytes()[0];

		buffer[2] = 0x2;											//offset for var params
		buffer[3] = (byte)(optParamsOffset);						//offset for opt params

		buffer[4] = (byte)(varLength);								//length of var param
		for(int i=0,j=5; i<varLength; i++,j++){						//putting var params
			buffer[j] = chg.getChargingInfoBytes()[i];
		}

		Object[] optBytes = optList.toArray();
		for(int i=0,j=optParamsIndex; i<optBytes.length; i++,j++){	////putting opt and other opt data
			buffer[j] = (Byte)(optBytes[i]);
		}

		if(isOptParamsExist)				
			buffer[totalLength-1] = 0x00;				//putting end of opt params param

		logger.info("encodeCHG:Exit");
		return buffer;
	}

}
