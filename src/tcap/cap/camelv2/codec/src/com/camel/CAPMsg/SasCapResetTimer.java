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

import asnGenerated.Integer4;
import asnGenerated.ResetTimerArg;
import asnGenerated.TimerID;
import asnGenerated.TimerValue;
import asnGenerated.TimerID.EnumType;

import com.camel.util.Util;

/**
 * This class have methods to encode ResetTimer operation
 * using the SasCapCallProcessBuffer.
 * @author nkumar
 *
 */
public class SasCapResetTimer {

	//Instance of logger
	private static Logger logger = Logger.getLogger(SasCapResetTimer.class);

	
	/**
	 * This method will encode RT and set the request in msgs.
	 * @param source
	 * @param cpb
	 * @param msgs
	 * @param invokeId
	 * @throws Exception
	 */
	public static void encodeRT(Object source, SasCapCallProcessBuffer cpb, SasCapMsgsToSend msgs, int invokeId) throws Exception{
		logger.info(Util.toString(cpb.dlgId) + "::: encodeRT:Enter");
		ResetTimerArg resetTimerArg = new ResetTimerArg() ;
		TimerID timerId = new TimerID();
		timerId.setValue(EnumType.tssf);
		logger.debug(Util.toString(cpb.dlgId) + "::: encodeRT:Timer Id enum type: " + timerId.getValue());
		resetTimerArg.setTimerID(timerId);
		TimerValue timerValue = new TimerValue();
		timerValue.setValue(new Integer4(cpb.resetTimerDuration));
		logger.debug(Util.toString(cpb.dlgId) + "::: encodeRT:Timer value: " + timerValue.getValue());
		resetTimerArg.setTimervalue(timerValue);
		
		IEncoder<ResetTimerArg> encoder;
		try {
			encoder = CoderFactory.getInstance().newEncoder("BER");
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			logger.info(Util.toString(cpb.dlgId) + "::: encodeRT:encoding the ResetTimerArg");
			encoder.encode(resetTimerArg, outputStream);
			logger.info(Util.toString(cpb.dlgId) + "::: encodeRT:successfully encoded the ResetTimerArg");
			byte[] encodedData = outputStream.toByteArray();
			
			if(logger.isDebugEnabled()){
			logger.debug(Util.toString(cpb.dlgId) + "::: encodeRT:Encoded ResetTimerArg: " + Util.formatBytes(encodedData));
			logger.debug(Util.toString(cpb.dlgId) + "::: encodeRT:length of encoded ResetTimerArg: " + encodedData.length);
			}
			
			byte[] resetTimerOpCode =  { CAPOpcode.RESET_TIMER };
			Operation rtOp = new Operation(Operation.OPERATIONTYPE_LOCAL, resetTimerOpCode);
			InvokeReqEvent ire = new InvokeReqEvent(source, cpb.dlgId, rtOp);
			ire.setInvokeId(invokeId);
			//ire.setLastInvokeEvent(true);
			ire.setParameters(new Parameters(Parameters.PARAMETERTYPE_SEQUENCE, encodedData));
			ire.setClassType(CAPOpcode.CLASS_TWO);
			List<ComponentReqEvent> list = msgs.getCompReqEvents() ;
			list.add(ire);
			msgs.setCompReqEvents(list);
		}catch(Exception e){
			logger.error(Util.toString(cpb.dlgId) + "::: encodeRT:Exception:" , e);
			throw e ;
		}
		
		logger.info(Util.toString(cpb.dlgId) + "::: encodeRT:Enter");
	}
}
