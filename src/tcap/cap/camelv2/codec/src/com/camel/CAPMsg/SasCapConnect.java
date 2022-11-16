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

import asnGenerated.CalledPartyNumber;
import asnGenerated.ConnectArg;
import asnGenerated.DestinationRoutingAddress;
import asnGenerated.OriginalCalledPartyID;

import com.camel.dataTypes.CalledPartyNum;
import com.camel.dataTypes.PartyId;
import com.camel.util.Util;

/**
 * This class have methods to set componentRequestEvent of  
 * SasCapMsgsToSend for connectArg.
 * @author nkumar
 *
 */
public class SasCapConnect {

	//Instance of logger
	private static Logger logger = Logger.getLogger(SasCapConnect.class);
	
	/**
	 * This function will set InvokeRequestEvent object in componentRequestEvent of  
	 * SasCapMsgsToSend  for connect operation.
	 * @param cpb
	 * @param msgs
	 * @throws Exception
	 */
	public static void encodeConnect(Object source,SasCapCallProcessBuffer cpb, SasCapMsgsToSend msgs, int invokeId) throws Exception {
		logger.info(Util.toString(cpb.dlgId) + "::: encodeConnect:Enter");
		ConnectArg connect = new ConnectArg();
		
		ArrayList<CalledPartyNumber> destList = new ArrayList<CalledPartyNumber>();
		for(int k = 0; k < cpb.destRoutingAdd.size() ; k++){
			CalledPartyNum cldParty = cpb.destRoutingAdd.get(k);
			logger.debug(Util.toString(cpb.dlgId) + "::: encodeConnect:CalledPartyNum: " + cldParty);
			byte[] data = CalledPartyNum.encodeCaldParty(cldParty.getAddrSignal(), cldParty.getNatureOfAdrs(),cldParty.getNumPlan(),cldParty.getIntNtwrkNum());
			destList.add(new CalledPartyNumber(data));
		}
		connect.setDestinationRoutingAddress(new DestinationRoutingAddress(destList));
		logger.info(Util.toString(cpb.dlgId) + "::: encodeConnect:All destinationRoutingAdrs added in the list");
		if(cpb.isOrignalCaldPartyIdPresent()){
			PartyId orignalParty = cpb.orignalCaldPartyId ;
			logger.debug(Util.toString(cpb.dlgId) + "::: encodeConnect:PartyId: " + orignalParty);
			byte[] data = PartyId.encodePartyId(orignalParty.getAddrSignal(), orignalParty.getNatureOfAdrs(), orignalParty.getNumPlan(), orignalParty.getAdrsPresntRestd());
			connect.setOriginalCalledPartyID(new OriginalCalledPartyID(data));
		}
		IEncoder<ConnectArg> encoder = CoderFactory.getInstance().newEncoder("BER");
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		logger.info(Util.toString(cpb.dlgId) + "::: encodeConnect:Encoding the connectArg");
		encoder.encode(connect, outputStream);
		byte[] encodedConnect = outputStream.toByteArray();
		
		
		if(logger.isDebugEnabled()){
		logger.debug(Util.toString(cpb.dlgId) + "::: encodeConnect:Encoded ConnectArg: " + Util.formatBytes(encodedConnect));
		logger.debug(Util.toString(cpb.dlgId) + "::: encodeConnect:length of encoded ConnectArg: " + encodedConnect.length);
		}
		
		byte[] connectOpCode =  { CAPOpcode.CONNECT };
		Operation connectOp = new Operation(Operation.OPERATIONTYPE_LOCAL, connectOpCode);
		InvokeReqEvent ireConnect = new InvokeReqEvent(source, cpb.dlgId, connectOp);
		ireConnect.setInvokeId(invokeId);
		ireConnect.setParameters(new Parameters(Parameters.PARAMETERTYPE_SEQUENCE, encodedConnect));
		ireConnect.setClassType(CAPOpcode.CONNECT_CLASS);
		//ireConnect.setLastInvokeEvent(true);
		List<ComponentReqEvent> list = msgs.getCompReqEvents() ;
		list.add(ireConnect);
		msgs.setCompReqEvents(list);
		logger.info(Util.toString(cpb.dlgId) + "::: encodeConnect:Exit");
	}
}
