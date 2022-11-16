package com.genband.isup.operations;

import java.util.LinkedList;
import java.util.Map;

import javax.xml.bind.DatatypeConverter;

import org.apache.log4j.Logger;

import com.genband.isup.messagetypes.ACMMessage;
import com.genband.isup.messagetypes.ANMMessage;
import com.genband.isup.messagetypes.CHGMessage;
import com.genband.isup.messagetypes.CPGMessage;
import com.genband.isup.messagetypes.IAMMessage;
import com.genband.isup.messagetypes.RELMessage;
import com.genband.isup.messagetypes.RLCMessage;
import com.genband.isup.messagetypes.SUSRESMessage;
import com.genband.isup.util.Util;


/**
 * This class contains methods for decoding and encoding of 
 * ISUP operations (i.e. IAM, ACM etc.). 
 * @author vgoel
 *
 */

public class ISUPOperationsCoding
{
	private static Logger logger = Logger.getLogger(ISUPOperationsCoding.class);

	public static byte[] hexStringToByteArray(String s) {    
		int len = s.length();    
		byte[] data = new byte[len / 2];     
		for (int i = 0; i < len; i += 2) {        
			data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)  + Character.digit(s.charAt(i+1), 16));    
		}    
		return data;
	}


	static public void warmup() {

		try {
			logger.error("ISUPOperationsCoding...starting warmup.");
			String bufferIAM = "010020000a03020907031010021802500a070311100219035065059e020000616604430fa4bf6f0703101002180250fd0481310005f10803fb05fe0300023100";
			String bufferACM = "06001400";
			String bufferCPG = "2c010111021434f10803fc05fe03003200fd048032420029010200";
			String bufferANM = "09011102121400";
			String bufferREL = "0c0200028490";
			String bufferRLC = "1000";

			LinkedList<byte[]> opBufferLinkList =  new LinkedList<byte[]>();

			opBufferLinkList.add(hexStringToByteArray(bufferIAM));
			opBufferLinkList.add(hexStringToByteArray(bufferACM));
			opBufferLinkList.add(hexStringToByteArray(bufferCPG));
			opBufferLinkList.add(hexStringToByteArray(bufferANM));
			opBufferLinkList.add(hexStringToByteArray(bufferREL));
			opBufferLinkList.add(hexStringToByteArray(bufferRLC));

			LinkedList<String> opCodesLinkList =  new LinkedList<String>();

			opCodesLinkList.add(ISUPConstants.OP_CODE_IAM);
			opCodesLinkList.add(ISUPConstants.OP_CODE_ACM);
			opCodesLinkList.add(ISUPConstants.OP_CODE_CPG);
			opCodesLinkList.add(ISUPConstants.OP_CODE_ANM);
			opCodesLinkList.add(ISUPConstants.OP_CODE_REL);
			opCodesLinkList.add(ISUPConstants.OP_CODE_RLC);

			//decoding
			LinkedList<Object> objLinkList = decodeOperations(opBufferLinkList,opCodesLinkList,ISUPConstants.ISUP_ITUT);

			//encoding
			LinkedList<byte []> byteArrayLinkList = encodeOperations(objLinkList,opCodesLinkList);
			logger.error("ISUPOperationsCoding...starting warmup.");
		} catch (Exception e) {
			logger.error("ISUPOperationsCoding...inside warmup.." + e.getMessage());
			logger.error("ISUPOperationsCoding...inside warmup..." + e);
		}
	}
	
	/**
	 * This method will decode the ISUP operations and will return the 
	 * list of objects as per sequence of input operation codes.
	 * This method expects that input byte[] to contain "Message Type" also.
	 * User would need to provide Protocol (ITUT or ANSI) for which byte need to 
	 * be processed. 
	 * 
	 * Support for optional params decoding is not yet present.
	 *   
	 * @param opBuffer
	 * @param opCodes
	 * @return LinkedList<Object>
	 * @throws Exception
	 */
	public static LinkedList<Object> decodeOperations(LinkedList<byte[]> opBuffer, LinkedList<String> opCodes)
	{
		 return decodeOperations(opBuffer,opCodes,ISUPConstants.ISUP_ITUT);
	}
	/**
	 * This method will decode the ISUP operations and will return the 
	 * list of objects as per sequence of input operation codes.
	 * This method expects that input byte[] to contain "Message Type" also.
	 * User would need to provide Protocol (ITUT or ANSI) for which byte need to 
	 * be processed. 
	 * 
	 * Support for optional params decoding is not yet present.
	 *   
	 * @param opBuffer
	 * @param opCodes
	 * @param Protocol
	 * @return LinkedList<Object>
	 * @throws Exception
	 */

	public static LinkedList<Object> decodeOperations(LinkedList<byte[]> opBuffer, LinkedList<String> opCodes, int Protocol)
	{
		logger.info("decodeOperations:Enter");

		if (Protocol < ISUPConstants.ISUP_ITUT || Protocol > ISUPConstants.ISUP_ANSI)
			Protocol = ISUPConstants.ISUP_ITUT;

		if(logger.isDebugEnabled()){
			logger.debug("decodeOperations:Input ---> opCodes:" + opCodes + " Protoocl: " + Protocol);
			for(byte[] singleOpBuffer : opBuffer)
				logger.debug("decodeOperations:Input ---> opBuffer:" + Util.formatBytes(singleOpBuffer));
		}	
		LinkedList<Object> outList = new LinkedList<Object>();	

		for(int i=0; i<opCodes.size(); i++)
		{
			byte[] singleOpBuffer = opBuffer.get(i); 
			String singleOpCode = opCodes.get(i);

			if(singleOpCode.equals(ISUPConstants.OP_CODE_IAM))
			{				
				logger.info("decodeOperations:decoding IAM");
				IAMMessage iam = new IAMMessage();
				iam.setProtocol(Protocol);

				LinkedList<byte[]> fixParamsList = ISUPDecoder.decodeFixedParams(singleOpBuffer, ISUPConstants.MAND_FIXED_PARAMS_OFFSET[Protocol][ISUPConstants.OP_CODE_INT_IAM]);
				iam.setMessageType(fixParamsList.get(0));		//message type will be of one byte only
				iam.setNatureOfConnIndicators(fixParamsList.get(1));
				iam.setForwardCallIndicators(fixParamsList.get(2));
				iam.setCallingPartyCategory(fixParamsList.get(3));

				if (Protocol == ISUPConstants.ISUP_ITUT)
					iam.setTmr(fixParamsList.get(4));

				LinkedList<byte[]> varParamsList = ISUPDecoder.decodeVariableParams(singleOpBuffer, ISUPConstants.MAND_VAR_PARAMS_OFFSET[Protocol][ISUPConstants.OP_CODE_INT_IAM]);	

				if (Protocol == ISUPConstants.ISUP_ITUT)
					iam.setCalledPartyNumber(varParamsList.get(0));
				else {
					iam.setUserServiceInfoByte(varParamsList.get(0));
					iam.setCalledPartyNumber(varParamsList.get(1));
				}

				//ISUPDecoder.decodeIAMOptParams(singleOpBuffer, ISUPConstants.OPT_PARAMS_OFFSET[ISUPConstants.OP_CODE_INT_IAM], iam);
				Map<Integer, byte[]> optMap = ISUPDecoder.decodeOptParams(singleOpBuffer, ISUPConstants.OPT_PARAMS_OFFSET[ISUPConstants.OP_CODE_INT_IAM]);
				iam.setParams(optMap);

				outList.add(iam);
				if(logger.isDebugEnabled())
					logger.info("decodeOperations:decoding IAM: " + iam);				
			}			
			else if(singleOpCode.equals(ISUPConstants.OP_CODE_ACM))
			{
				logger.info("decodeOperations:decoding ACM");
				ACMMessage acm = new ACMMessage();
				acm.setProtocol(Protocol);
				
				LinkedList<byte[]> fixParamsList = ISUPDecoder.decodeFixedParams(singleOpBuffer, ISUPConstants.MAND_FIXED_PARAMS_OFFSET[Protocol][ISUPConstants.OP_CODE_INT_ACM]);
				acm.setMessageType(fixParamsList.get(0));		//message type will be of one byte only
				acm.setBackwardCallIndicators(fixParamsList.get(1));		

				Map<Integer, byte[]> optMap = ISUPDecoder.decodeOptParams(singleOpBuffer, ISUPConstants.OPT_PARAMS_OFFSET[ISUPConstants.OP_CODE_INT_ACM]);
				acm.setParams(optMap);

				outList.add(acm);
				if(logger.isDebugEnabled())
					logger.info("decodeOperations:decoding ACM: " + acm);	
			}
			else if(singleOpCode.equals(ISUPConstants.OP_CODE_ANM))
			{
				logger.info("decodeOperations:decoding ANM");
				ANMMessage anm = new ANMMessage();		
				anm.setProtocol(Protocol);
				
				LinkedList<byte[]> fixParamsList = ISUPDecoder.decodeFixedParams(singleOpBuffer, ISUPConstants.MAND_FIXED_PARAMS_OFFSET[Protocol][ISUPConstants.OP_CODE_INT_ANM]);
				anm.setMessageType(fixParamsList.get(0));		//message type will be of one byte only																		

				Map<Integer, byte[]> optMap = ISUPDecoder.decodeOptParams(singleOpBuffer, ISUPConstants.OPT_PARAMS_OFFSET[ISUPConstants.OP_CODE_INT_ANM]);
				anm.setParams(optMap);

				outList.add(anm);
				if(logger.isDebugEnabled())
					logger.info("decodeOperations:decoding ANM: " + anm);	
			}
			else if(singleOpCode.equals(ISUPConstants.OP_CODE_CPG))
			{
				logger.info("decodeOperations:decoding CPG");
				CPGMessage cpg = new CPGMessage();		
				cpg.setProtocol(Protocol);
				
				LinkedList<byte[]> fixParamsList = ISUPDecoder.decodeFixedParams(singleOpBuffer, ISUPConstants.MAND_FIXED_PARAMS_OFFSET[Protocol][ISUPConstants.OP_CODE_INT_CPG]);
				cpg.setMessageType(fixParamsList.get(0));		//message type will be of one byte only		
				cpg.setEvenntInfo(fixParamsList.get(1));

				Map<Integer, byte[]> optMap = ISUPDecoder.decodeOptParams(singleOpBuffer, ISUPConstants.OPT_PARAMS_OFFSET[ISUPConstants.OP_CODE_INT_CPG]);
				cpg.setParams(optMap);

				outList.add(cpg);
				if(logger.isDebugEnabled())
					logger.info("decodeOperations:decoding CPG: " + cpg);
			}
			else if(singleOpCode.equals(ISUPConstants.OP_CODE_RLC))
			{
				logger.info("decodeOperations:decoding RLC");
				RLCMessage rlc = new RLCMessage();	
				rlc.setProtocol(Protocol);
				
				LinkedList<byte[]> fixParamsList = ISUPDecoder.decodeFixedParams(singleOpBuffer, ISUPConstants.MAND_FIXED_PARAMS_OFFSET[Protocol][ISUPConstants.OP_CODE_INT_RLC]);
				rlc.setMessageType(fixParamsList.get(0));		//message type will be of one byte only		

				Map<Integer, byte[]> optMap = ISUPDecoder.decodeOptParams(singleOpBuffer, ISUPConstants.OPT_PARAMS_OFFSET[ISUPConstants.OP_CODE_INT_RLC]);
				rlc.setParams(optMap);

				outList.add(rlc);
				if(logger.isDebugEnabled())
					logger.info("decodeOperations:decoding RLC: " + rlc);
			}
			else if(singleOpCode.equals(ISUPConstants.OP_CODE_REL))
			{
				logger.info("decodeOperations:decoding REL");
				RELMessage rel = new RELMessage();		
				rel.setProtocol(Protocol);
				
				LinkedList<byte[]> fixParamsList = ISUPDecoder.decodeFixedParams(singleOpBuffer, ISUPConstants.MAND_FIXED_PARAMS_OFFSET[Protocol][ISUPConstants.OP_CODE_INT_REL]);
				rel.setMessageType(fixParamsList.get(0));		//message type will be of one byte only		

				LinkedList<byte[]> varParamsList = ISUPDecoder.decodeVariableParams(singleOpBuffer, ISUPConstants.MAND_VAR_PARAMS_OFFSET[Protocol][ISUPConstants.OP_CODE_INT_REL]);				
				rel.setCause(varParamsList.get(0));

				Map<Integer, byte[]> optMap = ISUPDecoder.decodeOptParams(singleOpBuffer, ISUPConstants.OPT_PARAMS_OFFSET[ISUPConstants.OP_CODE_INT_REL]);
				rel.setParams(optMap);

				outList.add(rel);
				if(logger.isDebugEnabled())
					logger.info("decodeOperations:decoding REL: " + rel);
			}
			else if(singleOpCode.equals(ISUPConstants.OP_CODE_SUS))
			{
				logger.info("decodeOperations:decoding SUS");
				SUSRESMessage sus = new SUSRESMessage();	
				sus.setProtocol(Protocol);
				
				LinkedList<byte[]> fixParamsList = ISUPDecoder.decodeFixedParams(singleOpBuffer, ISUPConstants.MAND_FIXED_PARAMS_OFFSET[Protocol][ISUPConstants.OP_CODE_INT_SUS]);
				sus.setMessageType(fixParamsList.get(0));		//message type will be of one byte only		
				sus.setSuspendResumeInd(fixParamsList.get(1));

				Map<Integer, byte[]> optMap = ISUPDecoder.decodeOptParams(singleOpBuffer, ISUPConstants.OPT_PARAMS_OFFSET[ISUPConstants.OP_CODE_INT_SUS]);
				sus.setParams(optMap);

				outList.add(sus);
				if(logger.isDebugEnabled())
					logger.info("decodeOperations:decoding SUS: " + sus);
			}
			else if(singleOpCode.equals(ISUPConstants.OP_CODE_RES))
			{
				logger.info("decodeOperations:decoding RES");
				SUSRESMessage res = new SUSRESMessage();		
				res.setProtocol(Protocol);
				
				LinkedList<byte[]> fixParamsList = ISUPDecoder.decodeFixedParams(singleOpBuffer, ISUPConstants.MAND_FIXED_PARAMS_OFFSET[Protocol][ISUPConstants.OP_CODE_INT_RES]);
				res.setMessageType(fixParamsList.get(0));		//message type will be of one byte only		
				res.setSuspendResumeInd(fixParamsList.get(1));

				Map<Integer, byte[]> optMap = ISUPDecoder.decodeOptParams(singleOpBuffer, ISUPConstants.OPT_PARAMS_OFFSET[ISUPConstants.OP_CODE_INT_RES]);
				res.setParams(optMap);

				outList.add(res);
				if(logger.isDebugEnabled())
					logger.info("decodeOperations:decoding RES: " + res);
			}
			else if(singleOpCode.equals(ISUPConstants.OP_CODE_CHG))
			{
				logger.info("decodeOperations:decoding CHG");
				CHGMessage chg = new CHGMessage();
				chg.setProtocol(Protocol);
				
				LinkedList<byte[]> fixParamsList = ISUPDecoder.decodeFixedParams(singleOpBuffer, ISUPConstants.MAND_FIXED_PARAMS_OFFSET[Protocol][ISUPConstants.OP_CODE_INT_CHG]);
				chg.setMessageType(fixParamsList.get(0));		//message type will be of one byte only		
				chg.setChargingInfoCategory(fixParamsList.get(1));

				LinkedList<byte[]> varParamsList = ISUPDecoder.decodeVariableParams(singleOpBuffer, ISUPConstants.MAND_VAR_PARAMS_OFFSET[Protocol][ISUPConstants.OP_CODE_INT_CHG]);				
				chg.setChargingInfo(varParamsList.get(0));

				Map<Integer, byte[]> optMap = ISUPDecoder.decodeOptParams(singleOpBuffer, ISUPConstants.OPT_PARAMS_OFFSET[ISUPConstants.OP_CODE_INT_CHG]);
				chg.setParams(optMap);

				outList.add(chg);
				if(logger.isDebugEnabled())
					logger.info("decodeOperations:decoding CHG: " + chg);
			}
		}

		logger.info("decodeOperations:Exit");
		return outList;
	}

	/**
	 * This method will encode the ISUP operations and will return the list of encoded byte[]. 
	 * Operation codes are needed as input to get to know the type of incoming object.
	 * Output byte[] will contain "Message Type" also.
	 * 
	 * It shall be developer responsibility to set protocol in eac ISUP message,. 
	 * 
	 * Support for optional params decoding is not yet present.
	 * 
	 * @param opObjects
	 * @param opCodes
	 * @return LinkedList<byte[]>
	 * @throws Exception
	 */
	public static LinkedList<byte[]> encodeOperations(LinkedList<Object> opObjects, LinkedList<String> opCodes) throws Exception
	{
		logger.info("encodeOperations:Enter");
		if(logger.isDebugEnabled()){
			logger.debug("encodeOperations:Input ---> opCodes:" + opCodes +  ", opObjects:" + opObjects);
		}	
		LinkedList<byte[]> outList = new LinkedList<byte[]>();	

		for(int i=0; i<opCodes.size(); i++)
		{
			Object singleOpObj = opObjects.get(i); 
			String singleOpCode = opCodes.get(i);

			if(singleOpCode.equals(ISUPConstants.OP_CODE_IAM))
			{
				logger.info("encodeOperations:encoding IAM");

				IAMMessage iam = (IAMMessage)singleOpObj;
				byte[] buffer = ISUPEncoder.encodeIAM(iam);
				outList.add(buffer);

				if(logger.isDebugEnabled())
					logger.info("encodeOperations:encoding IAM: " + Util.formatBytes(buffer));				
			}			
			else if(singleOpCode.equals(ISUPConstants.OP_CODE_ACM))
			{
				logger.info("encodeOperations:encoding ACM");

				ACMMessage acm = (ACMMessage)singleOpObj;
				byte[] buffer = ISUPEncoder.encodeACM(acm);
				outList.add(buffer);

				if(logger.isDebugEnabled())
					logger.info("encodeOperations:encoding ACM: " + Util.formatBytes(buffer));
			}
			else if(singleOpCode.equals(ISUPConstants.OP_CODE_ANM))
			{
				logger.info("encodeOperations:encoding ANM");

				ANMMessage anm = (ANMMessage)singleOpObj;
				byte[] buffer = ISUPEncoder.encodeANM(anm);
				outList.add(buffer);

				if(logger.isDebugEnabled())
					logger.info("encodeOperations:encoding ANM: " + Util.formatBytes(buffer));
			}
			else if(singleOpCode.equals(ISUPConstants.OP_CODE_CPG))
			{
				logger.info("encodeOperations:encoding CPG");

				CPGMessage cpg = (CPGMessage)singleOpObj;
				byte[] buffer = ISUPEncoder.encodeCPG(cpg);
				outList.add(buffer);

				if(logger.isDebugEnabled())
					logger.info("encodeOperations:encoding CPG: " + Util.formatBytes(buffer));
			}
			else if(singleOpCode.equals(ISUPConstants.OP_CODE_RLC))
			{
				logger.info("encodeOperations:encoding RLC");

				RLCMessage rlc = (RLCMessage)singleOpObj;
				byte[] buffer = ISUPEncoder.encodeRLC(rlc);
				outList.add(buffer);

				if(logger.isDebugEnabled())
					logger.info("encodeOperations:encoding RLC: " + Util.formatBytes(buffer));
			}
			else if(singleOpCode.equals(ISUPConstants.OP_CODE_REL))
			{
				logger.info("encodeOperations:encoding REL");

				RELMessage rel = (RELMessage)singleOpObj;
				byte[] buffer = ISUPEncoder.encodeREL(rel);
				outList.add(buffer);

				if(logger.isDebugEnabled())
					logger.info("encodeOperations:encoding REL: " + Util.formatBytes(buffer));
			}
			else if(singleOpCode.equals(ISUPConstants.OP_CODE_SUS))
			{
				logger.info("encodeOperations:encoding SUS");

				SUSRESMessage sus = (SUSRESMessage)singleOpObj;
				byte[] buffer = ISUPEncoder.encodeSUS(sus);
				outList.add(buffer);

				if(logger.isDebugEnabled())
					logger.info("encodeOperations:encoding SUS: " + Util.formatBytes(buffer));
			}
			else if(singleOpCode.equals(ISUPConstants.OP_CODE_RES))
			{
				logger.info("encodeOperations:encoding RES");

				SUSRESMessage res = (SUSRESMessage)singleOpObj;
				byte[] buffer = ISUPEncoder.encodeRES(res);
				outList.add(buffer);

				if(logger.isDebugEnabled())
					logger.info("encodeOperations:encoding RES: " + Util.formatBytes(buffer));
			}
			else if(singleOpCode.equals(ISUPConstants.OP_CODE_CHG))
			{
				logger.info("encodeOperations:encoding CHG");

				CHGMessage chg = (CHGMessage)singleOpObj;
				byte[] buffer = ISUPEncoder.encodeCHG(chg);
				outList.add(buffer);

				if(logger.isDebugEnabled())
					logger.info("encodeOperations:encoding CHG: " + Util.formatBytes(buffer));
			}
		}

		logger.info("encodeOperations:Exit");
		return outList;		
	}
}
