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

import asnGenerated.ContinueWithArgumentArg;
import asnGenerated.ContinueWithArgumentArgExtension;
import asnGenerated.LegID;
import asnGenerated.LegOrCallSegment;
import asnGenerated.LegType;

import com.camel.util.Util;

/**
 * This clss have methods to encode Continue operation
 * and ContinueWithArg operation.
 * @author nkumar
 *
 */
public class SasCapContinue {

	//Instance of logger
	private static Logger logger = Logger.getLogger(SasCapContinue.class);
	
	/**
	 * This method will encode continue operation.
	 * @param source
	 * @param cpb
	 * @param msgs
	 * @param invokeId
	 * @throws Exception
	 */
	public static void encodeContinue(Object source, SasCapCallProcessBuffer cpb, SasCapMsgsToSend msgs, int invokeId) throws Exception{
		logger.info(Util.toString(cpb.dlgId) + "::: encodeContinue:Enter");
		byte[] continueOpCode =  { CAPOpcode.CONTINUE };
		Operation continueOp = new Operation(Operation.OPERATIONTYPE_LOCAL, continueOpCode);
		InvokeReqEvent ireContinue = new InvokeReqEvent(source, cpb.dlgId, continueOp);			
		ireContinue.setClassType(CAPOpcode.CLASS_FOUR);
		ireContinue.setInvokeId(invokeId);
		//ireContinue.setLastInvokeEvent(true);
		List<ComponentReqEvent> list = msgs.getCompReqEvents() ;
		list.add(ireContinue);
		msgs.setCompReqEvents(list);
		logger.info(Util.toString(cpb.dlgId) + "::: encodeContinue:Enter");
	}
	
	/**
	 * This method will encode continue operation.
	 * @param source
	 * @param cpb
	 * @param msgs
	 * @param invokeId
	 * @throws Exception
	 */
	public static void encodeActivityTest(Object source, SasCapCallProcessBuffer cpb, SasCapMsgsToSend msgs, int invokeId) throws Exception{
		logger.info(Util.toString(cpb.dlgId) + "::: encodeActivityTest:Enter");
		byte[] activityOpCode =  { CAPOpcode.ACTIVITY_TEST };
		Operation activityOp = new Operation(Operation.OPERATIONTYPE_LOCAL, activityOpCode);
		InvokeReqEvent ireContinue = new InvokeReqEvent(source, cpb.dlgId, activityOp);			
		ireContinue.setClassType(CAPOpcode.CLASS_THREE);
		ireContinue.setInvokeId(invokeId);
		//ireContinue.setLastInvokeEvent(true);
		List<ComponentReqEvent> list = msgs.getCompReqEvents() ;
		list.add(ireContinue);
		msgs.setCompReqEvents(list);
		logger.info(Util.toString(cpb.dlgId) + "::: encodeActivityTest:Enter");
	}

	/**
	 * This method will encode continueWithArg operation.
	 * @param source
	 * @param cpb
	 * @param msgs
	 * @param invokeId
	 * @throws Exception
	 */
	public static void encodeContinueWithArg(Object source, SasCapCallProcessBuffer cpb, SasCapMsgsToSend msgs, int invokeId) throws Exception{
		logger.info(Util.toString(cpb.dlgId) + "::: encodeContinueWithArg:Enter");
		ContinueWithArgumentArg continueWithArgumentArg = new ContinueWithArgumentArg() ;
		ContinueWithArgumentArgExtension conArgExtension = new ContinueWithArgumentArgExtension() ;
		LegOrCallSegment legOrCallSegment = new LegOrCallSegment();
		if(cpb.isCalSegmentIDForContinueWithArgPresent()){
			 legOrCallSegment.selectCallSegmentID(cpb.calSegmentIDForContinueWithArg) ;
			 logger.debug(Util.toString(cpb.dlgId) + "::: encodeContinueWithArg:CallSegmentId:" + cpb.calSegmentIDForContinueWithArg.getValue());
		}else if(cpb.islegIdForContinueWithArgPresent()){
			LegType legType = new LegType();
			logger.debug(Util.toString(cpb.dlgId) + "::: encodeContinueWithArg:legIdForContinueWithArg:" + cpb.legIdForContinueWithArg);
			if(cpb.legIdForContinueWithArg.equalsIgnoreCase("01")){
				byte[] leg = {0x01};
				legType.setValue(leg);
			}else if(cpb.legIdForContinueWithArg.equalsIgnoreCase("02")){
				byte[] leg = {0x02};
				legType.setValue(leg);
			}
			LegID legID = new LegID();
			legID.selectSendingSideID(legType);
			legOrCallSegment.selectLegID(legID);
		}
		conArgExtension.setLegOrCallSegment(legOrCallSegment);
		continueWithArgumentArg.setContinueWithArgumentArgExtension(conArgExtension);
		IEncoder<ContinueWithArgumentArg> encoder;
		try {
			encoder = CoderFactory.getInstance().newEncoder("BER");
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			logger.info(Util.toString(cpb.dlgId) + "::: encodeContinueWithArg:encoding the ContinueWithArgumentArg");
			encoder.encode(continueWithArgumentArg, outputStream);
			logger.info(Util.toString(cpb.dlgId) + "::: encodeContinueWithArg:successfully encoded the ContinueWithArgumentArg");
			byte[] encodedData = outputStream.toByteArray();
			
			if(logger.isDebugEnabled()){
			logger.debug(Util.toString(cpb.dlgId) + "::: encodeContinueWithArg:Encoded ContinueWithArgumentArg: " + Util.formatBytes(encodedData));
			logger.debug(Util.toString(cpb.dlgId) + "::: encodeContinueWithArg:length of encoded ContinueWithArgumentArg: " + encodedData.length);
			}
		byte[] continueOpCode =  { CAPOpcode.CONTINUE_ARG };
		Operation continueOp = new Operation(Operation.OPERATIONTYPE_LOCAL, continueOpCode);
		InvokeReqEvent ireContinue = new InvokeReqEvent(source, cpb.dlgId, continueOp);		
		ireContinue.setParameters(new Parameters(Parameters.PARAMETERTYPE_SEQUENCE, encodedData));
		ireContinue.setClassType(CAPOpcode.CLASS_TWO);
		ireContinue.setInvokeId(invokeId);
		List<ComponentReqEvent> list = msgs.getCompReqEvents() ;
		list.add(ireContinue);
		msgs.setCompReqEvents(list);
		}catch(Exception e){
			logger.error(Util.toString(cpb.dlgId) + "::: encodeContinueWithArg:Exception:" , e);
			throw e ;
		}
		logger.info(Util.toString(cpb.dlgId) + "::: encodeContinueWithArg:Enter");
	}
}
