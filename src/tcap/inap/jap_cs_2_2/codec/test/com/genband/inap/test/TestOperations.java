package com.genband.inap.test;

import jain.protocol.ss7.tcap.component.ErrorIndEvent;
import jain.protocol.ss7.tcap.component.InvokeIndEvent;
import jain.protocol.ss7.tcap.component.Operation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;

import junit.framework.TestCase;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import com.genband.inap.asngenerated.CalledPartyNumber;
import com.genband.inap.asngenerated.CarrierCode;
import com.genband.inap.asngenerated.ConnectArg;
import com.genband.inap.asngenerated.CriticalityType;
import com.genband.inap.asngenerated.DestinationRoutingAddress;
import com.genband.inap.asngenerated.ExtensionField;
import com.genband.inap.asngenerated.InitialDPExtension;
import com.genband.inap.asngenerated.RestartNotificationAcknowledgementArg;
import com.genband.inap.asngenerated.RestartNotificationArg;
import com.genband.inap.asngenerated.RestartedNodeID;
import com.genband.inap.asngenerated.ServiceInteractionIndicatorsTwo;
import com.genband.inap.asngenerated.TtcContractorNumber;
import com.genband.inap.asngenerated.CriticalityType.EnumType;
import com.genband.inap.datatypes.CalledPartyNum;
import com.genband.inap.datatypes.CarrierIdentificationCode;
import com.genband.inap.datatypes.ScfId;
import com.genband.inap.datatypes.TtcContractorNum;
import com.genband.inap.enumdata.GTIndicatorEnum;
import com.genband.inap.enumdata.IntNwNumEnum;
import com.genband.inap.enumdata.NatureOfAddEnum;
import com.genband.inap.enumdata.NumPlanEnum;
import com.genband.inap.enumdata.RoutingIndicatorEnum;
import com.genband.inap.enumdata.SPCIndicatorEnum;
import com.genband.inap.enumdata.SSNIndicatorEnum;
import com.genband.inap.exceptions.InvalidInputException;
import com.genband.inap.operations.InapOpCodes;
import com.genband.inap.operations.InapOperationsCoding;
import com.genband.inap.util.Util;

public class TestOperations extends TestCase
{
	private static Logger logger = Logger.getLogger(TestOperations.class);
	
	protected void setUp() throws Exception {
		super.setUp();
		 PropertyConfigurator.configure("config" + System.getProperty("file.separator") + "log4j.properties");
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}
	
	public void testEncodeDecodeOperations() throws Exception
	{				
		byte[] b = null;
		try {
			b = CalledPartyNum.encodeCaldParty("1234", NatureOfAddEnum.NATIONAL_NO, NumPlanEnum.TELEX_NP, IntNwNumEnum.ROUTING_ALLWD);					
		} catch (InvalidInputException e) {
			assertFalse(true);
			e.printStackTrace();
		}
		
		ConnectArg con = new ConnectArg();
		CalledPartyNumber cpNum = new CalledPartyNumber();
		cpNum.setValue(b);
		Collection<CalledPartyNumber> coll = new ArrayList<CalledPartyNumber>();
		coll.add(cpNum);
		DestinationRoutingAddress destAdd = new DestinationRoutingAddress();
		if (logger.isDebugEnabled()) {
			logger.debug("Encode Connect: Setting CalledPartyNum in DestinationRoutingAddress");
		}
		destAdd.setValue(coll);
		con.setDestinationRoutingAddress(destAdd);		
		
		ServiceInteractionIndicatorsTwo sii2 = new ServiceInteractionIndicatorsTwo();
		sii2.setAllowedCdINNoPresentaionInd(false);
		sii2.setCalledINNumberOverriding(false);
		if (logger.isDebugEnabled()) {
			logger.debug("Encode Connect: Setting ServiceInteractionIndicatorsTwo");
		}
		con.setServiceInteractionIndicatorsTwo(sii2);
		
		Collection<ExtensionField> coll1 = new LinkedList<ExtensionField>();
		ExtensionField ext = new ExtensionField();
		ext.setType(new Long(255));
		CriticalityType crit = new CriticalityType();
		crit.setValue(EnumType.ignore);
		ext.setCriticality(crit);
		if (logger.isDebugEnabled()) {
			logger.debug("Encode Connect: Connect Extension Type: " + ext.getType() + ", Crit type: " + ext.getCriticality().getValue());
		}
		/*byte[] extVal = {(byte)0xa1, (byte)0x0e, (byte)0x30, (byte)0x0c, (byte)0xa1, (byte)0x0a, (byte)0x80, (byte)0x08, (byte)0x03, (byte)0xfa,
				(byte)0x05, (byte)0xfe, (byte)0x03, (byte)0x00, (byte)0x02, (byte)0x31};*/
		byte[] ttcCont = TtcContractorNum.encodeTtcContractorNum("1234", NatureOfAddEnum.NATIONAL_NO, NumPlanEnum.ISDN_NP);
		TtcContractorNumber ttcCN = new TtcContractorNumber();
		ttcCN.setValue(ttcCont);
		InitialDPExtension idpExt = new InitialDPExtension();
		idpExt.setTtcContractorNumber(ttcCN);
		byte[] extVal = InapOperationsCoding.encodeIdpExt(idpExt);
		if (logger.isDebugEnabled()) {
			logger.debug("Encode Connect: Encoded Connect Extension Value: " + Util.formatBytes(extVal));
		}
		ext.setValue(extVal);
		coll1.add(ext);
		con.setExtensions(coll1);
		
		
		LinkedList<byte[]> encode = null;
		try {
			LinkedList<Object> objLL = new LinkedList<Object>();
			LinkedList<String> opCode = new LinkedList<String>();
			objLL.add(con);
			opCode.add(InapOpCodes.CONNECT);
			encode = InapOperationsCoding.encodeOperations(objLL, opCode);					
		} catch (Exception e) {
			assertFalse(true);
			e.printStackTrace();
		}
		if (logger.isDebugEnabled()) {
			logger.debug("Encoded Connect: " + Util.formatBytes(encode.get(0)));
		}
		assertNotNull(encode);	
		
		InvokeIndEvent iie = new InvokeIndEvent(new Object());		
		byte[] opCode =  { (byte)0x14 };
		Operation op = new Operation(Operation.OPERATIONTYPE_LOCAL, opCode);
		iie.setOperation(op);
		
		ConnectArg con1 = null;
		try{
			con1 = (ConnectArg)InapOperationsCoding.decodeOperation(encode.get(0), iie);
		}
		catch(Exception e){
			e.printStackTrace();
			assertFalse(true);
		}
		
		/*Object object = null;
		//System.out.println(encode.get(0));
		try {
			object = InapOperationsCoding.decodeOperationsForOpCode(encode.get(0), InapOpCodes.CONNECT_DEC);					
		} catch (Exception e) {
			assertFalse(true);
			e.printStackTrace();
		}	*/		
		
		LinkedList<CalledPartyNumber> ll = (LinkedList<CalledPartyNumber>)con1.getDestinationRoutingAddress().getValue();
		byte[] cpByte = ll.get(0).getValue();
		CalledPartyNum cpNumDecoded = null;
		try {
			cpNumDecoded = CalledPartyNum.decodeCaldParty(cpByte);
		} catch (InvalidInputException e) {
			assertFalse(true);
			e.printStackTrace();
		}
		assertEquals(IntNwNumEnum.ROUTING_ALLWD, cpNumDecoded.getIntNtwrkNum());
		assertEquals("1234", cpNumDecoded.getAddrSignal());
		assertEquals(NatureOfAddEnum.NATIONAL_NO, cpNumDecoded.getNatureOfAdrs());
		assertEquals(NumPlanEnum.TELEX_NP, cpNumDecoded.getNumPlan());
		
		
		//test error ind event
		ErrorIndEvent errInd = new ErrorIndEvent(new Object());
		errInd.setErrorCode(new byte[]{(byte)0x01});
		errInd.setErrorType(1);
				
		try {
			InapOperationsCoding.decodeOperation(encode.get(0), errInd);					
		} catch (Exception e) {
			assertFalse(true);
			e.printStackTrace();
		}
		
	}
	
	public void testEncodeDecodeRestartNot() throws Exception
	{
		RestartNotificationArg rn = new RestartNotificationArg();
		
		java.util.Collection<RestartedNodeID>  restartedNodeIDs = new java.util.LinkedList<RestartedNodeID>();
		RestartedNodeID rnId = new RestartedNodeID();
		rnId.setValue(ScfId.encodeScfId(SPCIndicatorEnum.SPC_PRESENT, SSNIndicatorEnum.SSN_PRESENT, GTIndicatorEnum.NO_GT, RoutingIndicatorEnum.ROUTING_PC_SSN, 24, 3, 10, 191));		
		restartedNodeIDs.add(rnId);
		restartedNodeIDs.add(rnId);
		
		rn.setRestartedNodeIDs(restartedNodeIDs);
		
		LinkedList<Object> objLL = new LinkedList<Object>();
		LinkedList<String> opCode = new LinkedList<String>();
		objLL.add(rn);
		opCode.add(InapOpCodes.RESTART_NOTIFICATION);
		
		LinkedList<byte[]> encode = InapOperationsCoding.encodeOperations(objLL, opCode);
		if (logger.isDebugEnabled()) {
			logger.debug("Encoded RN: " + Util.formatBytes(encode.get(0)));
		}
		RestartNotificationArg decode = (RestartNotificationArg)InapOperationsCoding.decodeOperationsForOpCode(encode.get(0), opCode.get(0));		
		assertEquals("0x43 0x30 0x6a 0xbf", Util.formatBytes(decode.getRestartedNodeIDs().iterator().next().getValue()));
		
		
		
		RestartNotificationAcknowledgementArg rna = new RestartNotificationAcknowledgementArg();
		CarrierCode cc = new CarrierCode();
		cc.setValue(CarrierIdentificationCode.encodeCarrierIdentCode("12"));
		rna.setCarrierCode(cc);
		
		LinkedList<Object> objLL1 = new LinkedList<Object>();
		LinkedList<String> opCode1 = new LinkedList<String>();
		objLL1.add(rna);
		opCode1.add(InapOpCodes.RESTART_NOTIFICATION_ACK);
		
		LinkedList<byte[]> encode1 = InapOperationsCoding.encodeOperations(objLL1, opCode1);
		if (logger.isDebugEnabled()) {
			logger.debug("Encoded RNA: " + Util.formatBytes(encode1.get(0)));
		}

		RestartNotificationAcknowledgementArg decode1 = (RestartNotificationAcknowledgementArg)InapOperationsCoding.decodeOperationsForOpCode(encode1.get(0), opCode1.get(0));		
		assertEquals("0x00 0x21", Util.formatBytes(decode1.getCarrierCode().getValue()));
		
	}
}
