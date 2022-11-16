package com.camel.CAPMsg;

import jain.protocol.ss7.tcap.ComponentReqEvent;
import jain.protocol.ss7.tcap.component.InvokeReqEvent;
import jain.protocol.ss7.tcap.component.Operation;
import jain.protocol.ss7.tcap.component.Parameters;

import java.io.ByteArrayOutputStream;
import java.util.List;

import org.apache.log4j.Logger;
import org.bn.CoderFactory;
import org.bn.IEncoder;

import asnGenerated.AssistingSSPIPRoutingAddress;
import asnGenerated.BothwayThroughConnectionInd;
import asnGenerated.CorrelationID;
import asnGenerated.Digits;
import asnGenerated.EstablishTemporaryConnectionArg;
import asnGenerated.ServiceInteractionIndicatorsTwo;
import asnGenerated.BothwayThroughConnectionInd.EnumType;

import com.camel.dataTypes.GenericDigitsDataType;
import com.camel.dataTypes.GenericNumDataType;
import com.camel.exceptions.InvalidInputException;
import com.camel.util.Util;

/**
 * This class have methods to process the CAP operation
 * ETC.
 * @author nkumar
 *
 */
public class SasCapETC {

	
	//Instance of logger
	private static Logger logger = Logger.getLogger(SasCapETC.class);
	
	/**
	 * This function will encode the ETC argument and
	 * set the invokeReqEvent in the msgs.
	 * @param source
	 * @param cpb
	 * @param msgs
	 * @param invokeId
	 * @throws Exception
	 */
	public static void encodeETC(Object source, SasCapCallProcessBuffer cpb, SasCapMsgsToSend msgs, int invokeId) throws Exception{
		logger.info(Util.toString(cpb.dlgId) + "::: encodeETC:Enter");
		if(! cpb.isAssistSSPIPRoutingAdrsPresent()){
			throw new InvalidInputException("assistSSPIPRoutingAdrs parameter of CPB is null");
		}
		EstablishTemporaryConnectionArg etcArg = new EstablishTemporaryConnectionArg();
		if(logger.isDebugEnabled()){
			logger.debug(Util.toString(cpb.dlgId) + "::: encodeETC:assistSSPIPRoutingAdrs:" + cpb.assistSSPIPRoutingAdrs);
		}
		byte[] encodedDigits = GenericNumDataType.encodeGenericNum(cpb.assistSSPIPRoutingAdrs.getNumQualifier(), cpb.assistSSPIPRoutingAdrs.getAddrSignal(),
												cpb.assistSSPIPRoutingAdrs.getNatureOfAdrs(), cpb.assistSSPIPRoutingAdrs.getNumPlan(), cpb.assistSSPIPRoutingAdrs.getAdrsPresntRestd(), 
												cpb.assistSSPIPRoutingAdrs.getScreening(), cpb.assistSSPIPRoutingAdrs.getNumIncomplte());
		
		AssistingSSPIPRoutingAddress ipAdrs = new AssistingSSPIPRoutingAddress();
		Digits digits = new Digits();
		digits.setValue(encodedDigits);
		ipAdrs.setValue(digits);
		etcArg.setAssistingSSPIPRoutingAddress(ipAdrs);
		
		if(cpb.isCorrelationIdPresent()){
			if(logger.isDebugEnabled()){
				logger.debug(Util.toString(cpb.dlgId) + "::: encodeETC:correlationId:" + cpb.correlationId);
			}
			byte[] encodedCorId = GenericDigitsDataType.encodeGenericDigits(cpb.correlationId.getEncodingSchemeEnum(), cpb.correlationId.getDigits());
			CorrelationID correlationID = new CorrelationID();
			Digits digits2 = new Digits();
			digits2.setValue(encodedCorId);
			correlationID.setValue(digits2);
			etcArg.setCorrelationID(correlationID);
		}
		BothwayThroughConnectionInd pathReq = new BothwayThroughConnectionInd();
		if(cpb.bothwayPathRequired){
			pathReq.setValue(EnumType.bothwayPathRequired);
		}else {
			pathReq.setValue(EnumType.bothwayPathNotRequired);
		}
		if(logger.isDebugEnabled()){
			logger.debug(Util.toString(cpb.dlgId) + "::: encodeETC:bothwayPathRequired:" + cpb.bothwayPathRequired);
		}
		ServiceInteractionIndicatorsTwo serviceInd = new ServiceInteractionIndicatorsTwo();
		serviceInd.setBothwayThroughConnectionInd(pathReq);
		etcArg.setServiceInteractionIndicatorsTwo(serviceInd);
		//TODO scfID
		
		IEncoder<EstablishTemporaryConnectionArg> encoder;
		try {
			encoder = CoderFactory.getInstance().newEncoder("BER");
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			logger.info(Util.toString(cpb.dlgId) + "::: encodeETC:encoding the EstablishTemporaryConnectionArg");
			encoder.encode(etcArg, outputStream);
			logger.info(Util.toString(cpb.dlgId) + "::: encodeETC:successfully encoded the EstablishTemporaryConnectionArg");
			byte[] encodedData = outputStream.toByteArray();
			
			if(logger.isDebugEnabled()){
			logger.debug(Util.toString(cpb.dlgId) + "::: encodeETC:Encoded EstablishTemporaryConnectionArg: " + Util.formatBytes(encodedData));
			logger.debug(Util.toString(cpb.dlgId) + "::: encodeETC:length of encoded EstablishTemporaryConnectionArg: " + encodedData.length);
			}
			
			byte[] etcOpCode =  { CAPOpcode.ESTABLISH_TEMP_CONNECTION };
			Operation etcOp = new Operation(Operation.OPERATIONTYPE_LOCAL, etcOpCode);
			InvokeReqEvent ire = new InvokeReqEvent(source, cpb.dlgId, etcOp);
			ire.setInvokeId(invokeId);
			ire.setParameters(new Parameters(Parameters.PARAMETERTYPE_SEQUENCE, encodedData));
			ire.setClassType(CAPOpcode.CLASS_TWO);
			List<ComponentReqEvent> list = msgs.getCompReqEvents() ;
			list.add(ire);
			msgs.setCompReqEvents(list);
		}catch(Exception e){
			throw e ;
		}
		
		logger.info(Util.toString(cpb.dlgId) + "::: encodeETC:Exit");
		
	}
}
