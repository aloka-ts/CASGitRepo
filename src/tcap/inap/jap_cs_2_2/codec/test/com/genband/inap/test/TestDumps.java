package com.genband.inap.test;

import jain.protocol.ss7.tcap.component.InvokeIndEvent;
import jain.protocol.ss7.tcap.component.Operation;

import java.util.Collection;
import java.util.LinkedList;

import junit.framework.TestCase;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import com.genband.inap.asngenerated.BCSMEvent;
import com.genband.inap.asngenerated.CalledPartyNumber;
import com.genband.inap.asngenerated.ConnectArg;
import com.genband.inap.asngenerated.ConnectExtension;
import com.genband.inap.asngenerated.EstablishTemporaryConnectionArg;
import com.genband.inap.asngenerated.EstablishTemporaryConnectionExtension;
import com.genband.inap.asngenerated.EventNotificationChargingArg;
import com.genband.inap.asngenerated.EventReportBCSMArg;
import com.genband.inap.asngenerated.ExtensionField;
import com.genband.inap.asngenerated.ForwardCallIndicators;
import com.genband.inap.asngenerated.GenericNumber;
import com.genband.inap.asngenerated.GenericNumbers;
import com.genband.inap.asngenerated.InitialDPArg;
import com.genband.inap.asngenerated.InitialDPExtension;
import com.genband.inap.asngenerated.Integer4;
import com.genband.inap.asngenerated.LegID;
import com.genband.inap.asngenerated.LegType;
import com.genband.inap.asngenerated.RequestNotificationChargingEventArg;
import com.genband.inap.asngenerated.RequestReportBCSMEventArg;
import com.genband.inap.asngenerated.SCIBillingChargingCharacteristics;
import com.genband.inap.asngenerated.SendChargingInformationArg;
import com.genband.inap.asngenerated.ServiceKey;
import com.genband.inap.asngenerated.TTCEventSpecificInformationCharging;
import com.genband.inap.asngenerated.TTCEventTypeCharging;
import com.genband.inap.asngenerated.TTCNOSpecificParameterSCIBCC;
import com.genband.inap.asngenerated.TTCNOSpecificParametersSCIBCC;
import com.genband.inap.asngenerated.TTCSCIBillingChargingCharacteristics;
import com.genband.inap.asngenerated.TTCSpecificSCIBCC;
import com.genband.inap.asngenerated.TerminalType;
import com.genband.inap.asngenerated.TTCSpecificEventTypeCharging.TTCSpecificEventTypeChargingEnumType;
import com.genband.inap.asngenerated.TerminalType.EnumType;
import com.genband.inap.datatypes.CalledPartyNum;
import com.genband.inap.datatypes.CallingPartyNum;
import com.genband.inap.datatypes.EventSpecificInfoChar;
import com.genband.inap.datatypes.EventTypeChar;
import com.genband.inap.datatypes.FwCallIndicators;
import com.genband.inap.datatypes.SCIBillingChargingChar;
import com.genband.inap.datatypes.TtcCalledINNumber;
import com.genband.inap.datatypes.TtcChargeAreaInfo;
import com.genband.inap.enumdata.AddPrsntRestEnum;
import com.genband.inap.enumdata.CalgPartyCatgEnum;
import com.genband.inap.enumdata.NatureOfAddEnum;
import com.genband.inap.enumdata.NumIncmpltEnum;
import com.genband.inap.enumdata.NumPlanEnum;
import com.genband.inap.enumdata.NumQualifierIndEnum;
import com.genband.inap.enumdata.ScreeningIndEnum;
import com.genband.inap.enumdata.TransmissionMedReqEnum;
import com.genband.inap.exceptions.InvalidInputException;
import com.genband.inap.operations.InapOpCodes;
import com.genband.inap.operations.InapOperationsCoding;
import com.genband.inap.util.Util;


public class TestDumps extends TestCase
{
	private static Logger logger = Logger.getLogger(TestDumps.class);
	
	protected void setUp() throws Exception {
		super.setUp();
		PropertyConfigurator.configure("config" + System.getProperty("file.separator") + "log4j.properties");
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testDecIDP() throws InvalidInputException
	{
		//encoding
		CalledPartyNumber cp = new CalledPartyNumber();
		byte[] cpbyte = {(byte)0x06, (byte)0x10, (byte)0x21, (byte)0x90, (byte)0x28, (byte)0x00};
		cp.setValue(cpbyte);
		
		TerminalType tt= new TerminalType();
		tt.setValue(EnumType.isdn);
		tt.setIntegerForm(new Integer(1));
		
		InitialDPArg idpA = new InitialDPArg();
		ServiceKey sk = new ServiceKey();
		Integer4 int4 = new Integer4();
		int4.setValue(new Integer(44));
		sk.setValue(int4);
		
		Collection<ExtensionField> coll = new LinkedList<ExtensionField>();
		ExtensionField ext = new ExtensionField();
		ext.setType(new Long(254));
		byte[] extVal = {(byte)0xa1, (byte)0x2e, (byte)0x30, (byte)0x2c, (byte)0x81, (byte)0x04, 
				(byte)0x81, (byte)0x23, (byte)0x00, (byte)0x00, (byte)0x83, (byte)0x18, (byte)0x03, (byte)0xfb, (byte)0x05, (byte)0xfe, 
				(byte)0x03, (byte)0x00, (byte)0x20, (byte)0x30, (byte)0xfe, (byte)0x0e, (byte)0xfe, (byte)0x03, (byte)0x00, (byte)0x22,
				(byte)0x33, (byte)0xfd, (byte)0x04, (byte)0x80, (byte)0x23, (byte)0x00, (byte)0x00, (byte)0xfc, (byte)0x01, (byte)0x20,
				(byte)0x84, (byte)0x04, (byte)0xfd, (byte)0x01, (byte)0xfc, (byte)0x03, (byte)0x85, (byte)0x04, (byte)0x81, (byte)0x23, 
				(byte)0x00, (byte)0x00};
		ext.setValue(extVal);
		coll.add(ext);
		
		ForwardCallIndicators fw = new ForwardCallIndicators();
		byte[] fwb = {(byte)0x20, (byte)0x01};
		fw.setValue(fwb);
		
		GenericNumbers gns = new GenericNumbers();
		GenericNumber gn = new GenericNumber();
		byte[] b1 = null;
		try {
			b1 = com.genband.inap.datatypes.GenericNumber.encodeGenericNum(NumQualifierIndEnum.ADD_CALLING_NO, "12345", NatureOfAddEnum.NATIONAL_NO, NumPlanEnum.TELEX_NP,AddPrsntRestEnum.PRSNT_ALLWD,ScreeningIndEnum.USER_PROVD, NumIncmpltEnum.COMPLETE);

		} catch (InvalidInputException e) {
			e.printStackTrace();
		}
		gn.setValue(b1);
		Collection<GenericNumber> gnlist = new LinkedList<GenericNumber>();
		gnlist.add(gn);		
		gns.setValue(gnlist);
		
		idpA.setServiceKey(sk);
		idpA.setTerminalType(tt);
		idpA.setCalledPartyNumber(cp);
		idpA.setExtensions(coll);
		//idpA.setForwardCallIndicators(fw);
		idpA.setGenericNumbers(gns);
		
		LinkedList<Object> objLL = new LinkedList<Object>();
		LinkedList<String> opCodes = new LinkedList<String>();
		objLL.add(idpA);
		opCodes.add(InapOpCodes.IDP);
		LinkedList<byte[]> out = null;
		
		try {
			out = InapOperationsCoding.encodeOperations(objLL, opCodes);
		} catch (Exception e1) {
			e1.printStackTrace();
			assertFalse(true);
		}
		if (logger.isDebugEnabled()) {
			logger.debug("Encoded IDP: " + Util.formatBytes(out.get(0)));
		}
		//System.out.println(Util.formatBytes(out.get(0)));
		
		
		//decoding
		//byte[] --- opcode, oplength, datatypecode, datatypelength, datattypedata (TLV)
		//initialdp1
		byte[] b = {(byte)0x30, (byte)0x5f, (byte)0x80, (byte)0x01, (byte)0x2c, (byte)0x82, (byte)0x06, (byte)0x06, (byte)0x10, (byte)0x21, 
					(byte)0x90, (byte)0x28, (byte)0x00, (byte)0x83, (byte)0x07, (byte)0x83, (byte)0x13, (byte)0x11, (byte)0x11, (byte)0x11, 
					(byte)0x11, (byte)0x11, (byte)0x85, (byte)0x01, (byte)0x0a, (byte)0xab, (byte)0x06, (byte)0x80, (byte)0x01, (byte)0x00, 
					(byte)0x81, (byte)0x01, (byte)0x02, (byte)0x8e, (byte)0x01, (byte)0x02, (byte)0xaf, (byte)0x2f, (byte)0x30, (byte)0x2d,
					(byte)0x02, (byte)0x01, (byte)0xfe, (byte)0xa1, (byte)0x28, (byte)0x30, (byte)0x26, (byte)0x81, (byte)0x04, (byte)0x81,
					(byte)0x23, (byte)0x00, (byte)0x00, (byte)0x82, (byte)0x07, (byte)0x83, (byte)0x14, (byte)0x11, (byte)0x11, (byte)0x11,
					(byte)0x11, (byte)0x11, (byte)0x83, (byte)0x0f, (byte)0x03, (byte)0xfb, (byte)0x05, (byte)0xfe, (byte)0x03, (byte)0x00,
					(byte)0x22, (byte)0x33, (byte)0xf8, (byte)0x05, (byte)0xfe, (byte)0x03, (byte)0x00, (byte)0x22, (byte)0x53, (byte)0x85,
					(byte)0x04, (byte)0x81, (byte)0x23, (byte)0x00, (byte)0x00,	(byte)0x9a, (byte)0x02, (byte)0xa0, (byte)0x00, (byte)0xbb,
					(byte)0x03, (byte)0x81, (byte)0x01, (byte)0x03, (byte)0x9c, (byte)0x01, (byte)0x03	};
		//modified initialdp1 (CritType)
		/*byte[] b = {(byte)0x30, (byte)0x62, (byte)0x80, (byte)0x01, (byte)0x2c, (byte)0x82, (byte)0x06, (byte)0x06, (byte)0x10, (byte)0x21, 
					(byte)0x90, (byte)0x28, (byte)0x00, (byte)0x83, (byte)0x07, (byte)0x83, (byte)0x13, (byte)0x11, (byte)0x11, (byte)0x11, 
					(byte)0x11, (byte)0x11, (byte)0x85, (byte)0x01, (byte)0x0a, (byte)0xab, (byte)0x06, (byte)0x80, (byte)0x01, (byte)0x00, 
					(byte)0x81, (byte)0x01, (byte)0x02, (byte)0x8e, (byte)0x01, (byte)0x02, (byte)0xaf, (byte)0x32, (byte)0x30, (byte)0x30,
					(byte)0x02, (byte)0x01, (byte)0xfe, (byte)0x0a, (byte)0x01, (byte)0x00, (byte)0xa1, (byte)0x28, (byte)0x30, (byte)0x26, 
					(byte)0x81, (byte)0x04, (byte)0x81, (byte)0x23, (byte)0x00, (byte)0x00, (byte)0x82, (byte)0x07, (byte)0x83, (byte)0x14, 
					(byte)0x11, (byte)0x11, (byte)0x11, (byte)0x11, (byte)0x11, (byte)0x83, (byte)0x0f, (byte)0x03, (byte)0xfb, (byte)0x05, 
					(byte)0xfe, (byte)0x03, (byte)0x00, (byte)0x22, (byte)0x33, (byte)0xf8, (byte)0x05, (byte)0xfe, (byte)0x03, (byte)0x00, 
					(byte)0x22, (byte)0x53, (byte)0x85, (byte)0x04, (byte)0x81, (byte)0x23, (byte)0x00, (byte)0x00,	(byte)0x9a, (byte)0x02, 
					(byte)0xa0, (byte)0x00, (byte)0xbb, (byte)0x03, (byte)0x81, (byte)0x01, (byte)0x03, (byte)0x9c, (byte)0x01, (byte)0x03	};*/
		
		//initialdp2
		/*byte[] b = {(byte)0x30, (byte)0x66, (byte)0x80, (byte)0x01, (byte)0x2c, (byte)0x82, (byte)0x07, (byte)0x83, (byte)0x10, (byte)0x11,
					(byte)0x11, (byte)0x11, (byte)0x11, (byte)0x11, (byte)0x83, (byte)0x07, (byte)0x03, (byte)0x11, (byte)0x11, (byte)0x11, 
					(byte)0x11, (byte)0x11, (byte)0x11, (byte)0x85, (byte)0x01, (byte)0x0a, (byte)0xab, (byte)0x06, (byte)0x80, (byte)0x01, 
					(byte)0x00, (byte)0x81, (byte)0x01, (byte)0x02, (byte)0x8e, (byte)0x01, (byte)0x00, (byte)0xaf, (byte)0x35, (byte)0x30, 
					(byte)0x33, (byte)0x02, (byte)0x01, (byte)0xfe, (byte)0xa1, (byte)0x2e, (byte)0x30, (byte)0x2c, (byte)0x81, (byte)0x04, 
					(byte)0x81, (byte)0x23, (byte)0x00, (byte)0x00, (byte)0x83, (byte)0x18, (byte)0x03, (byte)0xfb, (byte)0x05, (byte)0xfe, 
					(byte)0x03, (byte)0x00, (byte)0x20, (byte)0x30, (byte)0xfe, (byte)0x0e, (byte)0xfe, (byte)0x03, (byte)0x00, (byte)0x22,
					(byte)0x33, (byte)0xfd, (byte)0x04, (byte)0x80, (byte)0x23, (byte)0x00, (byte)0x00, (byte)0xfc, (byte)0x01, (byte)0x20,
					(byte)0x84, (byte)0x04, (byte)0xfd, (byte)0x01, (byte)0xfc, (byte)0x03, (byte)0x85, (byte)0x04, (byte)0x81, (byte)0x23, 
					(byte)0x00, (byte)0x00, (byte)0x9a, (byte)0x02, (byte)0x20, (byte)0x01, (byte)0xbb, (byte)0x03, (byte)0x81, (byte)0x01,
					(byte)0x00, (byte)0x9c, (byte)0x01, (byte)0x03	};	*/
		
		
		InvokeIndEvent iie = new InvokeIndEvent(new Object());		
		byte[] opCode =  { (byte)0x00 };
		Operation op = new Operation(Operation.OPERATIONTYPE_LOCAL, opCode);
		iie.setOperation(op);
		
		InitialDPArg idp = null;
		try{
			idp = (InitialDPArg)InapOperationsCoding.decodeOperation(b, iie);
		}
		catch(Exception e){
			e.printStackTrace();
			assertFalse(true);
		}
		
		assertEquals(44, idp.getServiceKey().getValue().getValue().intValue());
		if (logger.isDebugEnabled()) {
			logger.debug("service key: " + idp.getServiceKey().getValue().getValue().intValue());
			logger.debug("called party num: " + Util.formatBytes(idp.getCalledPartyNumber().getValue()));
			logger.debug("calling party num: " + Util.formatBytes(idp.getCallingPartyNumber().getValue()));
			logger.debug("calling party category: " + CalgPartyCatgEnum.fromInt(idp.getCallingPartysCategory().getValue()[0]));
			logger.debug("Misc call info message type: " + idp.getMiscCallInfo().getMessageType().getValue());
			logger.debug("Misc call info DP: " + idp.getMiscCallInfo().getDpAssignment().getValue());
			logger.debug("terminal type: " + idp.getTerminalType().getValue());
			logger.debug("FwdCallInd: " + Util.formatBytes(idp.getForwardCallIndicators().getValue()));
			logger.debug("Bearer Cap tmr: " + Util.formatBytes(idp.getBearerCapability().getTmr()));
			logger.debug("BCSM: " + idp.getEventTypeBCSM().getValue());
		}
		long extType = idp.getExtensions().iterator().next().getType();
		extType = extType<0 ? 256+extType : extType;
		if (logger.isDebugEnabled()) {
			logger.debug("extension type: " + extType);
			//logger.debug("extension crit type: " + idp.getExtensions().iterator().next().getCriticality().getValue());
			logger.debug("extension value: " + Util.formatBytes(idp.getExtensions().iterator().next().getValue()));
		}
		
		byte[] idpextB = idp.getExtensions().iterator().next().getValue();
		byte[] idpextB1 = new byte[idpextB.length-2];
		for(int i=0; i<idpextB.length-2; i++){
			idpextB1[i] = idpextB[i+2];
		}
		InitialDPExtension idpExt = null;
		try {
			idpExt = InapOperationsCoding.decodeInitialDPExt(idpextB);
		} catch (Exception e) {
			e.printStackTrace();
			assertFalse(true);
		}
		if (logger.isDebugEnabled()) {
			logger.debug("ttc charge area info: " + Util.formatBytes(idpExt.getTtcChargeAreaInformation().getValue()));
			logger.debug("ttc called in num: " + Util.formatBytes(idpExt.getTtcCalledINNumber().getValue()));
			logger.debug("ttc carrier info trfr: " + Util.formatBytes(idpExt.getTtcCarrierInformationTransfer().getValue()));
			logger.debug("ttc ssp charge area info: " + Util.formatBytes(idpExt.getTtcSSPChargeAreaInformation().getValue()));
			//non-asn decoding
			logger.debug("NON-ASN DECODING");
			logger.debug("called party num: " + CalledPartyNum.decodeCaldParty(idp.getCalledPartyNumber().getValue()));
			logger.debug("calling party num: " + CallingPartyNum.decodeCalgParty(idp.getCallingPartyNumber().getValue()));
			logger.debug("FwdCallInd: " + FwCallIndicators.decodeFwCallInd(idp.getForwardCallIndicators().getValue()));
			logger.debug("Bearer Cap tmr: " + TransmissionMedReqEnum.fromInt(idp.getBearerCapability().getTmr()[0]));
			
			//non-asn ttc decoding
			logger.debug("NON-ASN TTC DECODING");
			logger.debug("ttc charge area info: " +TtcChargeAreaInfo.decodeTtcChargeAreaInfo(idpExt.getTtcChargeAreaInformation().getValue()));
			logger.debug("ttc called in num: " + TtcCalledINNumber.decodeTtcCalledINNum(idpExt.getTtcCalledINNumber().getValue()));
			logger.debug("ttc ssp charge area info: " + TtcChargeAreaInfo.decodeTtcChargeAreaInfo(idpExt.getTtcSSPChargeAreaInformation().getValue()));
		}
	}
	
	public void testDecSCI() throws Exception
	{
		//encoding
		SendChargingInformationArg sci = new SendChargingInformationArg();
		LegID leg = new LegID();
		LegType lt = new LegType();
		byte[] ltb = {(byte)0x01};
		lt.setValue(ltb);
		leg.selectSendingSideID(lt);
		sci.setPartyToCharge(leg);
		
		SCIBillingChargingCharacteristics scibill = new SCIBillingChargingCharacteristics();
		TTCSCIBillingChargingCharacteristics ttcsci = new TTCSCIBillingChargingCharacteristics();
		
		TTCNOSpecificParametersSCIBCC ttc = new TTCNOSpecificParametersSCIBCC();
		TTCSpecificSCIBCC ttsp = new TTCSpecificSCIBCC();
		ttsp.setNoChargeIndicator(true);
		TTCNOSpecificParameterSCIBCC ttcsp = new TTCNOSpecificParameterSCIBCC();
		ttcsp.selectTTCSpecificSCIBCC(ttsp);
		ttc.initValue();
		ttc.add(ttcsp);	
		ttcsci.selectTTCNOSpecificParametersSCIBCC(ttc);
		
		byte[] scib = SCIBillingChargingChar.encodeSciBillingChar(ttcsci);
		
		scibill.setValue(scib);
		sci.setSCIBillingChargingCharacteristics(scibill);
		
		LinkedList<Object> objLL = new LinkedList<Object>();
		LinkedList<String> opCodes = new LinkedList<String>();
		objLL.add(sci);
		opCodes.add(InapOpCodes.SCI);
		
		try {
			InapOperationsCoding.encodeOperations(objLL, opCodes);
		} catch (Exception e1) {
			e1.printStackTrace();
			assertFalse(true);
		}
		
		//decoding
		byte[] b = {(byte)0x30, (byte)0x0e, (byte)0x80, (byte)0x07, (byte)0xaf, (byte)0x05, (byte)0xa0, (byte)0x03, (byte)0x80, (byte)0x01,
				(byte)0xff, (byte)0xa1, (byte)0x03, (byte)0x80, (byte)0x01, (byte)0x01};		
		
		/*byte[] b = {(byte)0x30, (byte)0x0e, (byte)0x80, (byte)0x07, (byte)0xaf, (byte)0x05, (byte)0xa0, (byte)0x03, (byte)0x80, (byte)0x01, (byte)0x01,
					(byte)0xa1, (byte)0x03, (byte)0x80, (byte)0x01, (byte)0x01, (byte)0x00	};*/

		
		InvokeIndEvent iie = new InvokeIndEvent(new Object());		
		byte[] opCode =  { (byte)0x2e };
		Operation op = new Operation(Operation.OPERATIONTYPE_LOCAL, opCode);
		iie.setOperation(op);
		
		SendChargingInformationArg sciA = null;
		try{
			sciA = (SendChargingInformationArg)InapOperationsCoding.decodeOperation(b, iie);
		}
		catch(Exception e){
			e.printStackTrace();
			assertFalse(true);
		}
		
		TTCSCIBillingChargingCharacteristics sciB = null;
		try{
			sciB = SCIBillingChargingChar.decodeSciBillingChar(sciA.getSCIBillingChargingCharacteristics().getValue());
		}
		catch(Exception e){
			e.printStackTrace();
			assertFalse(true);
		}
		if (logger.isDebugEnabled()) {
			//logger.debug("sci billing charging char: "+sciA.getSCIBillingChargingCharacteristics().getTTCNOSpecificParametersSCIBCC().getValue().iterator().next().getTTCSpecificSCIBCC().getNoChargeIndicator());
			logger.debug("sending side id: "+Util.formatBytes(sciA.getPartyToCharge().getSendingSideID().getValue()));
			logger.debug("sending billing charging char: "+sciB.getTTCNOSpecificParametersSCIBCC().getValue().iterator().next().getTTCSpecificSCIBCC().getNoChargeIndicator());
		}
	}
	
	
	public void testDecRNCE()
	{		
		//decoding
		byte[] b = {(byte)0x30, (byte)0x1b, (byte)0x30, (byte)0x19, (byte)0x80, (byte)0x0f, (byte)0xaf, (byte)0x0d, (byte)0xa0, (byte)0x0b, 
					(byte)0xa0, (byte)0x09, (byte)0x0a, (byte)0x01, (byte)0x00, (byte)0x0a, (byte)0x01, (byte)0x02, (byte)0x0a, (byte)0x01, 
					(byte)0x03, (byte)0x81, (byte)0x01, (byte)0x01, (byte)0xa2, (byte)0x03, (byte)0x80, (byte)0x01, (byte)0x02 };		
		
		InvokeIndEvent iie = new InvokeIndEvent(new Object());		
		byte[] opCode =  { (byte)0x19 };
		Operation op = new Operation(Operation.OPERATIONTYPE_LOCAL, opCode);
		iie.setOperation(op);
		
		RequestNotificationChargingEventArg rnce = null;
		try{
			rnce = (RequestNotificationChargingEventArg)InapOperationsCoding.decodeOperation(b, iie);
		}
		catch(Exception e){
			e.printStackTrace();
			assertFalse(true);
		}
		
		TTCEventTypeCharging ttcEtc = null;
		try{
			ttcEtc = EventTypeChar.decodeEventTypeCharging(rnce.getValue().iterator().next().getEventTypeCharging().getValue());
		}
		catch(Exception e){
			e.printStackTrace();
			assertFalse(true);
		}
		
		//3 values
		for(TTCSpecificEventTypeChargingEnumType singleTTC : ttcEtc.getTTCNOSpecificParametersETChg().getValue().iterator().next().getTTCSpecificETChg().getTTCSpecificEventTypeCharging().getValue()) {
			if (logger.isDebugEnabled()) {
				logger.debug("rnce event type charging: "+singleTTC.getValue());
			}
		}
		if (logger.isDebugEnabled()) {
			logger.debug("rnce monitor mode: "+rnce.getValue().iterator().next().getMonitorMode().getValue());
			logger.debug("rnce leg id: "+Util.formatBytes(rnce.getValue().iterator().next().getLegID().getSendingSideID().getValue()));
		}
		
		LinkedList<Object> objLL = new LinkedList<Object>();
		LinkedList<String> opCodes = new LinkedList<String>();
		objLL.add(rnce);
		opCodes.add(InapOpCodes.RNCE);
		try {
			if (logger.isDebugEnabled()) {
				logger.debug("encoded rnce: " + Util.formatBytes(InapOperationsCoding.encodeOperations(objLL, opCodes).get(0)));
			}
		} catch (Exception e) {
			e.printStackTrace();
			assertFalse(true);
		}
	}
	
	
	public void testDecRRBE()
	{		
		//decoding
		byte[] b = {(byte)0x30, (byte)0x2c, (byte)0xa0, (byte)0x2a, (byte)0x30, (byte)0x06, (byte)0x80, (byte)0x01, (byte)0x07, (byte)0x81,
					(byte)0x01, (byte)0x01, (byte)0x30, (byte)0x0b, (byte)0x80, (byte)0x01, (byte)0x09, (byte)0x81, (byte)0x01, (byte)0x01,
					(byte)0xa2, (byte)0x03, (byte)0x80, (byte)0x01, (byte)0x01, (byte)0x30, (byte)0x0b, (byte)0x80, (byte)0x01, (byte)0x09, 
					(byte)0x81, (byte)0x01, (byte)0x01, (byte)0xa2, (byte)0x03, (byte)0x80, (byte)0x01, (byte)0x02, (byte)0x30, (byte)0x06,
					(byte)0x80, (byte)0x01, (byte)0x0a, (byte)0x81, (byte)0x01, (byte)0x01 };		
		
		InvokeIndEvent iie = new InvokeIndEvent(new Object());		
		byte[] opCode =  { (byte)0x17 };
		Operation op = new Operation(Operation.OPERATIONTYPE_LOCAL, opCode);
		iie.setOperation(op);
		
		RequestReportBCSMEventArg rrbe = null;
		try{
			rrbe = (RequestReportBCSMEventArg)InapOperationsCoding.decodeOperation(b, iie);
		}
		catch(Exception e){
			e.printStackTrace();
			assertFalse(true);
		}
		

		for(BCSMEvent bcsm : rrbe.getBcsmEvents()){
			if (logger.isDebugEnabled()) {
				logger.debug("rrbe bcsm: "+bcsm.getEventTypeBCSM().getValue());
				logger.debug("rrbe monitor mode: "+bcsm.getMonitorMode().getValue());
			}
			if(bcsm.getLegID() != null) {
				if (logger.isDebugEnabled()) {
					logger.debug("rrbe monitor mode: "+Util.formatBytes(bcsm.getLegID().getSendingSideID().getValue()));
				}
			}
		}
		
		
		LinkedList<Object> objLL = new LinkedList<Object>();
		LinkedList<String> opCodes = new LinkedList<String>();
		objLL.add(rrbe);
		opCodes.add(InapOpCodes.RRBE);
		try {
			if (logger.isDebugEnabled()) {
				logger.debug("encoded rrbe: " + Util.formatBytes(InapOperationsCoding.encodeOperations(objLL, opCodes).get(0)));
			}
		} catch (Exception e) {
			e.printStackTrace();
			assertFalse(true);
		}
		
	}
	
	
	public void testDecConnect() throws Exception
	{		
		//decoding
		//connect1
		byte[] b = {(byte)0x30, (byte)0x2d, (byte)0xa0, (byte)0x09, (byte)0x04, (byte)0x07, (byte)0x83, (byte)0x10, (byte)0x11, (byte)0x11, (byte)0x11,
					(byte)0x11, (byte)0x11, (byte)0xaa, (byte)0x18, (byte)0x30, (byte)0x16, (byte)0x02, (byte)0x01, (byte)0xff, (byte)0x0a, (byte)0x01,
					(byte)0x00, (byte)0xa1, (byte)0x0e, (byte)0x30, (byte)0x0c, (byte)0xa1, (byte)0x0a, (byte)0x80, (byte)0x08, (byte)0x03, (byte)0xfa,
					(byte)0x05, (byte)0xfe, (byte)0x03, (byte)0x00, (byte)0x02, (byte)0x31, (byte)0xaf, (byte)0x06, (byte)0x87, (byte)0x01, (byte)0x00, 
					(byte)0x8B, (byte)0x01, (byte)0x00 };
		
		//connect2
		/*byte[] b = {(byte)0x30, (byte)0x31, (byte)0xa0, (byte)0x07, (byte)0x04, (byte)0x05, (byte)0x06, (byte)0x10, (byte)0x21, (byte)0x80, (byte)0x05,
					(byte)0xaa, (byte)0x1f, (byte)0x30, (byte)0x1d, (byte)0x02, (byte)0x01, (byte)0xff, (byte)0x0a, (byte)0x01, (byte)0x00, (byte)0xa1, 
					(byte)0x15, (byte)0x30, (byte)0x13, (byte)0xa1, (byte)0x11, (byte)0x80, (byte)0x0f, (byte)0x03, (byte)0xf8, (byte)0x05, (byte)0xfe,
					(byte)0x03, (byte)0x00, (byte)0x02, (byte)0x31, (byte)0xf9, (byte)0x05, (byte)0xfe, (byte)0x03, (byte)0x00, (byte)0x22, (byte)0x53,
					(byte)0xaf, (byte)0x05, (byte)0xac, (byte)0x03, (byte)0x80, (byte)0x01, (byte)0x01	};
					
		//connect3
		/*byte[] b = {(byte)0x30, (byte)0x44, (byte)0xa0, (byte)0x0e, (byte)0x04, (byte)0x0c, (byte)0x7e, (byte)0x10, (byte)0x00, (byte)0x88, (byte)0x36,
					(byte)0x91, (byte)0x09, (byte)0x08, (byte)0x80, (byte)0x88, (byte)0x90, (byte)0x81, (byte)0x82, (byte)0x0d, (byte)0x1e, (byte)0x10,
					(byte)0x71, (byte)0x25, (byte)0x54, (byte)0x98, (byte)0x00, (byte)0x20, (byte)0x04, (byte)0x60, (byte)0x34, (byte)0x72, (byte)0x48,
					(byte)0x88, (byte)0x04, (byte)0x43, (byte)0x6a, (byte)0x30, (byte)0xbf, (byte)0xaa, (byte)0x18, (byte)0x30, (byte)0x16, (byte)0x02,
					(byte)0x01, (byte)0xff, (byte)0x0a, (byte)0x01, (byte)0x00, (byte)0xa1, (byte)0x0e, (byte)0x30, (byte)0x0c, (byte)0xa1, (byte)0x0a,
					(byte)0x80, (byte)0x08, (byte)0x03, (byte)0xfa, (byte)0x05, (byte)0xfe, (byte)0x03, (byte)0x00, (byte)0x02, (byte)0x31, (byte)0xaf,
					(byte)0x03, (byte)0x87, (byte)0x01, (byte)0x00};*/

		
		InvokeIndEvent iie = new InvokeIndEvent(new Object());	
		byte[] opCode =  { (byte)0x14 };
		Operation op = new Operation(Operation.OPERATIONTYPE_LOCAL, opCode);
		iie.setOperation(op);
		
		ConnectArg con = null;
		try{
			con = (ConnectArg)InapOperationsCoding.decodeOperation(b, iie);
		}
		catch(Exception e){
			e.printStackTrace();
			assertFalse(true);
		}
		if (logger.isDebugEnabled()) {
			logger.debug("connect called party num: " + Util.formatBytes(con.getDestinationRoutingAddress().getValue().iterator().next().getValue()));
			logger.debug("connect ext type: " + con.getExtensions().iterator().next().getType());
			logger.debug("connect ext crit type: " + con.getExtensions().iterator().next().getCriticality().getValue());
			logger.debug("connect ext value: " + Util.formatBytes(con.getExtensions().iterator().next().getValue()));
			logger.debug("connect service int ind: " + con.getServiceInteractionIndicatorsTwo().getAllowedCdINNoPresentaionInd());
		}
		//logger.debug("connect service int ind: " + Util.formatBytes(con.getServiceInteractionIndicatorsTwo().getRedirectServiceTreatmentInd().getRedirectReason().getValue()));
		//logger.debug("connect service scfid: " + Util.formatBytes(con.getScfID().getValue()));
		
		byte[] conextB = con.getExtensions().iterator().next().getValue();
		byte[] conextB1 = new byte[conextB.length-2];
		for(int i=0; i<conextB.length-2; i++){
			conextB1[i] = conextB[i+2];
		}
		
		ConnectExtension conExt = null;
		try {
			conExt = InapOperationsCoding.decodeConnectExt(conextB);
		} catch (Exception e) {
			e.printStackTrace();
			assertFalse(true);
		}
		if (logger.isDebugEnabled()) {
			logger.debug("connect ext ttcit: " + Util.formatBytes(conExt.getTtcCarrierInformation().getTtcCarrierInformationTransfer().getValue()));
		
			logger.debug("encoded connect ext: " + Util.formatBytes(InapOperationsCoding.encodeConnectExt(conExt)));
		}
		LinkedList<Object> objLL = new LinkedList<Object>();
		LinkedList<String> opCodes = new LinkedList<String>();
		objLL.add(con);
		opCodes.add(InapOpCodes.CONNECT);
		try {
			if (logger.isDebugEnabled()) {
				logger.debug("encoded con: " + Util.formatBytes(InapOperationsCoding.encodeOperations(objLL, opCodes).get(0)));
			}
		} catch (Exception e) {
			e.printStackTrace();
			assertFalse(true);
		}
	}

	
	public void testDecENC()
	{		
		//decoding
		byte[] b = {(byte)0x30, (byte)0x42, (byte)0x80, (byte)0x0f, (byte)0xaf, (byte)0x0d, (byte)0xa0, (byte)0x0b, (byte)0xa0,
					(byte)0x09, (byte)0x0a, (byte)0x01, (byte)0x00, (byte)0x0a, (byte)0x01, (byte)0x02, (byte)0x0a, (byte)0x01, (byte)0x03,
					(byte)0x81, (byte)0x2a, (byte)0xaf, (byte)0x28, (byte)0xa0, (byte)0x26, (byte)0xa0, (byte)0x24, (byte)0x80, (byte)0x18,
					(byte)0x00, (byte)0xfc, (byte)0x05, (byte)0xfe, (byte)0x03, (byte)0x00, (byte)0x02, (byte)0x31, (byte)0xfe, (byte)0x0e,
					(byte)0xfe, (byte)0x03, (byte)0x00, (byte)0x22, (byte)0x33, (byte)0xfd, (byte)0x04, (byte)0x80, (byte)0x23, (byte)0x00, 
					(byte)0x00, (byte)0xfc, (byte)0x01, (byte)0x02, (byte)0x82, (byte)0x02, (byte)0x16, (byte)0x04, (byte)0x83, (byte)0x04,
					(byte)0x81, (byte)0x23, (byte)0x00, (byte)0x00, (byte)0xa2, (byte)0x03, (byte)0x81, (byte)0x01, (byte)0x02 };
		
		/*byte[] b = {(byte)0x30, (byte)0x1c, (byte)0x80, (byte)0x09, (byte)0xaf, (byte)0x07, (byte)0xa0, (byte)0x05, (byte)0xa0, 
					(byte)0x03, (byte)0x0a, (byte)0x01, (byte)0x02, (byte)0x81, (byte)0x0a, (byte)0xaf, (byte)0x08, (byte)0xa0, (byte)0x06, 
					(byte)0xa0, (byte)0x04, (byte)0x82, (byte)0x02, (byte)0x16, (byte)0x04, (byte)0xa2, (byte)0x03, (byte)0x81, (byte)0x01, 
					(byte)0x02 };*/
		
		InvokeIndEvent iie = new InvokeIndEvent(new Object());	
		byte[] opCode =  { (byte)0x1A };
		Operation op = new Operation(Operation.OPERATIONTYPE_LOCAL, opCode);
		iie.setOperation(op);
		
		EventNotificationChargingArg enc = null;
		try{
			enc = (EventNotificationChargingArg)InapOperationsCoding.decodeOperation(b, iie);
		}
		catch(Exception e){
			e.printStackTrace();
			assertFalse(true);
		}
		
		TTCEventTypeCharging ttcetc = null;
		try{
			ttcetc = EventTypeChar.decodeEventTypeCharging(enc.getEventTypeCharging().getValue());
		}
		catch(Exception e){
			e.printStackTrace();
			assertFalse(true);
		}
		
		TTCEventSpecificInformationCharging ttcesc = null;
		try{
			ttcesc = EventSpecificInfoChar.decodeEventSpecificInfoCharging(enc.getEventSpecificInformationCharging().getValue());
		}
		catch(Exception e){
			e.printStackTrace();
			assertFalse(true);
		}

		for(TTCSpecificEventTypeChargingEnumType ttc : ttcetc.getTTCNOSpecificParametersETChg().getValue().iterator().next().getTTCSpecificETChg().getTTCSpecificEventTypeCharging().getValue()) {
			if (logger.isDebugEnabled()) {
				logger.debug("enc event type charging: "+ttc.getValue());
			}
		}
		if (logger.isDebugEnabled()) {
			logger.debug("enc event specific info charging cit: "+Util.formatBytes(ttcesc.getTTCNOSpecificParametersESIC().getValue().iterator().next().getTTCSpecificEventSpecificInfo().getTTCSpecificChargeEvent().getTtcCarrierInformationTransfer().getValue()));
			logger.debug("enc event specific info charging bwi: "+Util.formatBytes(ttcesc.getTTCNOSpecificParametersESIC().getValue().iterator().next().getTTCSpecificEventSpecificInfo().getTTCSpecificChargeEvent().getTtcBackwardCallIndicators().getValue()));
			logger.debug("enc event specific info charging cai: "+Util.formatBytes(ttcesc.getTTCNOSpecificParametersESIC().getValue().iterator().next().getTTCSpecificEventSpecificInfo().getTTCSpecificChargeEvent().getTtcChargeAreaInformation().getValue()));
			logger.debug("enc legid: "+Util.formatBytes(enc.getLegID().getReceivingSideID().getValue()));
		}
				
		LinkedList<Object> objLL = new LinkedList<Object>();
		LinkedList<String> opCodes = new LinkedList<String>();
		objLL.add(enc);
		opCodes.add(InapOpCodes.ENC);
		try {
			if (logger.isDebugEnabled()) {
				logger.debug("encoded enc: " + Util.formatBytes(InapOperationsCoding.encodeOperations(objLL, opCodes).get(0)));
			}
		} catch (Exception e) {
			e.printStackTrace();
			assertFalse(true);
		}
	}
	
	
	public void testDecERB()
	{		
		//decoding
		//erb1
		byte[] b = {(byte)0x30, (byte)0x18, (byte)0x80, (byte)0x01, (byte)0x09, (byte)0xa2, (byte)0x09, (byte)0xa7, (byte)0x07, (byte)0x80,
					(byte)0x02, (byte)0x84, (byte)0x90, (byte)0x81, (byte)0x01, (byte)0x7d, (byte)0xa3, (byte)0x03, (byte)0x81, (byte)0x01,
					(byte)0x01, (byte)0xa4, (byte)0x03, (byte)0x80, (byte)0x01, (byte)0x01 };		
		
		//erb2
		//byte[] b = {(byte)0x30, (byte)0x08, (byte)0x80, (byte)0x01, (byte)0x07, (byte)0xa4, (byte)0x03, (byte)0x80, (byte)0x01, (byte)0x01 };		
		
		InvokeIndEvent iie = new InvokeIndEvent(new Object());	
		byte[] opCode =  { (byte)0x18 };
		Operation op = new Operation(Operation.OPERATIONTYPE_LOCAL, opCode);
		iie.setOperation(op);
		
		EventReportBCSMArg erb = null;
		try{
			erb = (EventReportBCSMArg)InapOperationsCoding.decodeOperation(b, iie);
		}
		catch(Exception e){
			e.printStackTrace();
			assertFalse(true);
		}
		if (logger.isDebugEnabled()) {
			logger.debug("erb event type bcsm: " + erb.getEventTypeBCSM().getValue());
			logger.debug("erb event specific info type bcsm release cause: " + Util.formatBytes(erb.getEventSpecificInformationBCSM().getODisconnectSpecificInfo().getReleaseCause().getValue()));
			logger.debug("erb event specific info type bcsm connect time: " + erb.getEventSpecificInformationBCSM().getODisconnectSpecificInfo().getConnectTime().getValue().intValue());
			logger.debug("erb leg id: " + Util.formatBytes(erb.getLegID().getReceivingSideID().getValue()));
			logger.debug("erb msg type: " + erb.getMiscCallInfo().getMessageType().getValue());
		}
		
		LinkedList<Object> objLL = new LinkedList<Object>();
		LinkedList<String> opCodes = new LinkedList<String>();
		objLL.add(erb);
		opCodes.add(InapOpCodes.ERB);
		try {
			if (logger.isDebugEnabled()) {
				logger.debug("encoded erb: " + Util.formatBytes(InapOperationsCoding.encodeOperations(objLL, opCodes).get(0)));
			}
		} catch (Exception e) {
			e.printStackTrace();
			assertFalse(true);
		}
	}
	
	
	public void testDecETC()
	{		
		//decoding
		//erb1
		byte[] b = {(byte)0x30, (byte)0x3e, (byte)0x80, (byte)0x08, (byte)0x00, (byte)0xfe, (byte)0x17, (byte)0x00, (byte)0x88, (byte)0x36, (byte)0x00, 
					(byte)0x03, (byte)0x81, (byte)0x0d, (byte)0x1e, (byte)0x10, (byte)0x71, (byte)0x25, (byte)0x54, (byte)0x98, (byte)0x00,	(byte)0x10, 
					(byte)0x04, (byte)0x60, (byte)0x34, (byte)0x72, (byte)0x48, (byte)0xa2, (byte)0x03, (byte)0x80, (byte)0x01, (byte)0x01,	(byte)0x83, 
					(byte)0x04, (byte)0x43, (byte)0x6a, (byte)0x30, (byte)0xbf, (byte)0xa4, (byte)0x18, (byte)0x30, (byte)0x16, (byte)0x02,	(byte)0x01, 
					(byte)0xfd, (byte)0x0a, (byte)0x01, (byte)0x00, (byte)0xa1, (byte)0x0e, (byte)0x30, (byte)0x0c, (byte)0xa0, (byte)0x0a,	(byte)0x80, 
					(byte)0x08, (byte)0x03, (byte)0xfa, (byte)0x05, (byte)0xfe, (byte)0x03, (byte)0x00, (byte)0x02, (byte)0x31 };		
	
		
		InvokeIndEvent iie = new InvokeIndEvent(new Object());	
		byte[] opCode =  { (byte)0x11 };
		Operation op = new Operation(Operation.OPERATIONTYPE_LOCAL, opCode);
		iie.setOperation(op);
		
		EstablishTemporaryConnectionArg etc = null;
		try{
			etc = (EstablishTemporaryConnectionArg)InapOperationsCoding.decodeOperation(b, iie);
		}
		catch(Exception e){
			e.printStackTrace();
			assertFalse(true);
		}
		if (logger.isDebugEnabled()) {
			logger.debug("etc assist ssp ra: " + Util.formatBytes(etc.getAssistingSSPIPRoutingAddress().getValue().getValue()));
			logger.debug("etc correlationid: " + Util.formatBytes(etc.getCorrelationID().getValue().getValue()));
			logger.debug("etc legid: " + Util.formatBytes(etc.getPartyToConnect().getLegID().getSendingSideID().getValue()));
			logger.debug("etc scfid: " + Util.formatBytes(etc.getScfID().getValue()));
			logger.debug("etc ext crit type: " + etc.getExtensions().iterator().next().getCriticality().getValue());
			logger.debug("etc ext type: " + etc.getExtensions().iterator().next().getType());
			logger.debug("etc ext value: " + Util.formatBytes(etc.getExtensions().iterator().next().getValue()));
		}
		
		byte[] etcB = etc.getExtensions().iterator().next().getValue();
		byte[] etcB1 = new byte[etcB.length-2];
		for(int i=0; i<etcB.length-2; i++){
			etcB1[i] = etcB[i+2];
		}
		
		EstablishTemporaryConnectionExtension etcExt = null;
		try {
			etcExt = InapOperationsCoding.decodeEtcExt(etcB);
		} catch (Exception e) {
			e.printStackTrace();
			assertFalse(true);
		}
		if (logger.isDebugEnabled()) {
			logger.debug("connect ext ttcit: " + Util.formatBytes(etcExt.getTtcCarrierInformation().getTtcCarrierInformationTransfer().getValue()));
		}
		
		
		LinkedList<Object> objLL = new LinkedList<Object>();
		LinkedList<String> opCodes = new LinkedList<String>();
		objLL.add(etc);
		opCodes.add(InapOpCodes.ETC);
		try {
			if (logger.isDebugEnabled()) {
				logger.debug("encoded erb: " + Util.formatBytes(InapOperationsCoding.encodeOperations(objLL, opCodes).get(0)));
			}
		} catch (Exception e) {
			e.printStackTrace();
			assertFalse(true);
		}
	}

}

