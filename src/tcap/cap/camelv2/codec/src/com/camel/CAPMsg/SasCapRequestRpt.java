package com.camel.CAPMsg;

import jain.protocol.ss7.tcap.ComponentReqEvent;
import jain.protocol.ss7.tcap.component.InvokeReqEvent;
import jain.protocol.ss7.tcap.component.Operation;
import jain.protocol.ss7.tcap.component.Parameters;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.bn.CoderFactory;
import org.bn.IEncoder;

import asnGenerated.BCSMEvent;
import asnGenerated.EventTypeBCSM;
import asnGenerated.LegID;
import asnGenerated.LegType;
import asnGenerated.MonitorMode;
import asnGenerated.RequestReportBCSMEventArg;
import asnGenerated.EventTypeBCSM.EnumType;

import com.camel.util.Util;

/**
 * This class have methods to set componentRequestEvent of  
 * SasCapMsgsToSend for RequestReportBcsm.
 * @author nkumar
 *
 */
public class SasCapRequestRpt {

	//Instance of logger
	private static Logger logger = Logger.getLogger(SasCapRequestRpt.class);
	
	/**
	 * This function will set InvokeRequestEvent object in componentRequestEvent of  
	 * SasCapMsgsToSend  for RequestReportBcsm.
	 * @param cpb
	 * @param msgs
	 * @throws Exception
	 */
	public static void setComponentReqEvent(Object source, SasCapCallProcessBuffer cpb, SasCapMsgsToSend msgs ,int invokeId) throws Exception {
		logger.info(Util.toString(cpb.dlgId) + "::: setComponentReqEvent:Enter");
		RequestReportBCSMEventArg requestReport = new RequestReportBCSMEventArg() ;
		logger.info(Util.toString(cpb.dlgId) + "::: setComponentReqEvent:Setting bcsm events");
		if(cpb.isBcsmEventListPresent())
			requestReport.setBcsmEvents(cpb.bcsmEventList);
		else 
			requestReport.setBcsmEvents(prepareBcsmEventList());
		try {
		IEncoder<RequestReportBCSMEventArg> encoder = CoderFactory.getInstance().newEncoder("BER");
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		logger.info(Util.toString(cpb.dlgId) + "::: setComponentReqEvent:Encoding the requestReport");
		encoder.encode(requestReport, outputStream);
		byte[] encodedRequestReport = outputStream.toByteArray();
		if(logger.isDebugEnabled()){
		logger.debug(Util.toString(cpb.dlgId) + "::: setComponentReqEvent:Encoded encodedRequestReport: " + Util.formatBytes(encodedRequestReport));
		logger.debug(Util.toString(cpb.dlgId) + "::: setComponentReqEvent:length of encoded encodedRequestReport: " + encodedRequestReport.length);
		}
		byte[] requestReportOpCode =  { CAPOpcode.REQUEST_REPORT };
		Operation requestOp = new Operation(Operation.OPERATIONTYPE_LOCAL, requestReportOpCode);
		//TODO
		InvokeReqEvent ire = new InvokeReqEvent(source, cpb.dlgId, requestOp);
		ire.setInvokeId(invokeId);
		ire.setParameters(new Parameters(Parameters.PARAMETERTYPE_SEQUENCE, encodedRequestReport));
		ire.setClassType(CAPOpcode.RRBCSM_CLASS);
		List<ComponentReqEvent> list = msgs.getCompReqEvents() ;
		list.add(ire);
		msgs.setCompReqEvents(list);
		} catch(Exception e){
			logger.error(Util.toString(cpb.dlgId) + "::: setComponentReqEvent:Exception" , e);
			throw e ;
		}
		logger.info(Util.toString(cpb.dlgId) + "::: setComponentReqEvent:Exit");
	}
	
	private static List<BCSMEvent> prepareBcsmEventList(){
		ArrayList<BCSMEvent> bcsmEventList = new ArrayList<BCSMEvent>();
		
		BCSMEvent obj1 = new BCSMEvent();
		
		EventTypeBCSM event = new EventTypeBCSM();
		event.setValue(EnumType.oAbandon);
		
		obj1.setEventTypeBCSM(event);
		
		MonitorMode md1 = new MonitorMode();
		md1.setValue(MonitorMode.EnumType.interrupted);
		
		
		obj1.setMonitorMode(md1);
		
		LegID legID1 = new LegID();
		byte[] legtype = {0x01 } ;
		legID1.selectSendingSideID(new LegType(legtype));
		
		obj1.setLegID(legID1);
		
		bcsmEventList.add(obj1);
		
		EventTypeBCSM event2 = new EventTypeBCSM();
		BCSMEvent obj2 = new BCSMEvent();
		event2.setValue(EnumType.oAnswer);
		
		obj2.setEventTypeBCSM(event2);
		
		MonitorMode md2 = new MonitorMode();
		md2.setValue(MonitorMode.EnumType.notifyAndContinue);
		
		
		obj2.setMonitorMode(md2);
		LegID legID2 = new LegID();
		byte[] legtype2 = {0x02 } ;
		legID2.selectSendingSideID(new LegType(legtype2));
		
		obj2.setLegID(legID2);
		
		bcsmEventList.add(obj2);
		
		EventTypeBCSM event3 = new EventTypeBCSM();
		BCSMEvent obj3 = new BCSMEvent();
		event3.setValue(EnumType.oNoAnswer);
		
		
		obj3.setEventTypeBCSM(event3);
		obj3.setMonitorMode(md1);
		obj3.setLegID(legID2);
		
		bcsmEventList.add(obj3);
		
		EventTypeBCSM event4 = new EventTypeBCSM();
		BCSMEvent obj4 = new BCSMEvent();
		event4.setValue(EnumType.oCalledPartyBusy);
		
		obj4.setEventTypeBCSM(event4);
		obj4.setMonitorMode(md1);
		obj4.setLegID(legID2);
		
		bcsmEventList.add(obj4);
		
		EventTypeBCSM event5 = new EventTypeBCSM();
		BCSMEvent obj5 = new BCSMEvent();
		event5.setValue(EnumType.oDisconnect);
		
		obj5.setEventTypeBCSM(event5);
		obj5.setMonitorMode(md1);
		obj5.setLegID(legID2);
		
		bcsmEventList.add(obj5);
		
		EventTypeBCSM event6 = new EventTypeBCSM();
		BCSMEvent obj6 = new BCSMEvent();
		event6.setValue(EnumType.oDisconnect);
		
		obj6.setEventTypeBCSM(event6);
		obj6.setMonitorMode(md1);
		obj6.setLegID(legID1);
		
		bcsmEventList.add(obj6);
		
		return bcsmEventList ;
	}
}
