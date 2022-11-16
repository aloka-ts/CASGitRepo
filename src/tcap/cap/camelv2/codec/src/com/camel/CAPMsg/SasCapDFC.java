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

import asnGenerated.DisconnectForwardConnectionWithArgumentArg;

import com.camel.util.Util;

/**
 * This class have methods to encode DFC operation
 * and DFCWithArg operation.
 * @author nkumar
 *
 */
public class SasCapDFC {

	//Instance of logger
	private static Logger logger = Logger.getLogger(SasCapDFC.class);
	
	/**
	 * This method will encode DFC operation.
	 * @param source
	 * @param cpb
	 * @param msgs
	 * @param invokeId
	 * @throws Exception
	 */
	public static void encodeDFC(Object source, SasCapCallProcessBuffer cpb, SasCapMsgsToSend msgs, int invokeId) throws Exception{
		logger.info(Util.toString(cpb.dlgId) + "::: encodeDFC:Enter");
		byte[] dfcOpCode =  { CAPOpcode.DISCONNECT_FORWARD_CONNECTION };
		Operation dfcOp = new Operation(Operation.OPERATIONTYPE_LOCAL, dfcOpCode);
		InvokeReqEvent ire = new InvokeReqEvent(source, cpb.dlgId, dfcOp);			
		ire.setClassType(CAPOpcode.CLASS_TWO);
		ire.setInvokeId(invokeId);
		List<ComponentReqEvent> list = msgs.getCompReqEvents() ;
		list.add(ire);
		msgs.setCompReqEvents(list);
		logger.info(Util.toString(cpb.dlgId) + "::: encodeDFC:Exit");
	}
	
	/**
	 * This method will encode DFCWithArg operation.
	 * @param source
	 * @param cpb
	 * @param msgs
	 * @param invokeId
	 * @throws Exception
	 */
	public static void encodeDFCWithArg(Object source, SasCapCallProcessBuffer cpb, SasCapMsgsToSend msgs, int invokeId) throws Exception{
		logger.info(Util.toString(cpb.dlgId) + "::: encodeDFCWithArg:Enter");
		DisconnectForwardConnectionWithArgumentArg diArg = new DisconnectForwardConnectionWithArgumentArg();
		if(cpb.isCalSegmentIDForDFCPresent()){
			diArg.setCallSegmentID(cpb.calSegmentIDForDFCWithArg);
		}
		IEncoder<DisconnectForwardConnectionWithArgumentArg> encoder;
		try {
			encoder = CoderFactory.getInstance().newEncoder("BER");
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			logger.info(Util.toString(cpb.dlgId) + "::: encodeDFCWithArg:encoding the DisconnectForwardConnectionWithArgumentArg");
			encoder.encode(diArg, outputStream);
			logger.info(Util.toString(cpb.dlgId) + "::: encodeDFCWithArg:successfully encoded the DisconnectForwardConnectionWithArgumentArg");
			byte[] encodedData = outputStream.toByteArray();
			
			if(logger.isDebugEnabled()){
			logger.debug(Util.toString(cpb.dlgId) + "::: encodeDFCWithArg:Encoded DisconnectForwardConnectionWithArgumentArg: " + Util.formatBytes(encodedData));
			logger.debug(Util.toString(cpb.dlgId) + "::: encodeDFCWithArg:length of encoded DisconnectForwardConnectionWithArgumentArg: " + encodedData.length);
			}
		byte[] dfcOpCode =  { CAPOpcode.DISCONNECT_FORWARD_CONNECTION_ARG };
		Operation dfcOp = new Operation(Operation.OPERATIONTYPE_LOCAL, dfcOpCode);
		InvokeReqEvent ire = new InvokeReqEvent(source, cpb.dlgId, dfcOp);			
		ire.setClassType(CAPOpcode.CLASS_TWO);
		ire.setInvokeId(invokeId);
		ire.setParameters(new Parameters(Parameters.PARAMETERTYPE_SEQUENCE, encodedData));
		List<ComponentReqEvent> list = msgs.getCompReqEvents() ;
		list.add(ire);
		msgs.setCompReqEvents(list);
		}catch(Exception e){
			logger.error(Util.toString(cpb.dlgId) + "::: encodeDFCWithArg:Exception:" , e);
			throw e ;
		}
		logger.info(Util.toString(cpb.dlgId) + "::: encodeDFCWithArg:Exit");
	}
}
