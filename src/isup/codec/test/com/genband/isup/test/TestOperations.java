package com.genband.isup.test;

import java.util.LinkedList;

import junit.framework.TestCase;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import com.genband.isup.datatypes.AccessTransport;
import com.genband.isup.datatypes.BwCallIndicators;
import com.genband.isup.datatypes.CalledPartyNum;
import com.genband.isup.datatypes.CallingPartyNum;
import com.genband.isup.datatypes.CalledPartySubaddress;
import com.genband.isup.datatypes.Cause;
import com.genband.isup.datatypes.FwCallIndicators;
import com.genband.isup.datatypes.GenericDigits;
import com.genband.isup.datatypes.NatOfConnIndicators;
import com.genband.isup.datatypes.OptBwCallIndicators;
import com.genband.isup.datatypes.ScfId;
import com.genband.isup.datatypes.TtcCalledINNumber;
import com.genband.isup.datatypes.TtcChargeAreaInfo;
import com.genband.isup.enumdata.AddPrsntRestEnum;
import com.genband.isup.enumdata.CalgPartyCatgEnum;
import com.genband.isup.enumdata.CallDiversionIndEnum;
import com.genband.isup.enumdata.CalledPartyCatIndEnum;
import com.genband.isup.enumdata.CalledPartyStatusIndEnum;
import com.genband.isup.enumdata.CauseValEnum;
import com.genband.isup.enumdata.ChargeIndEnum;
import com.genband.isup.enumdata.CodingStndEnum;
import com.genband.isup.enumdata.ContCheckIndEnum;
import com.genband.isup.enumdata.DigitCatEnum;
import com.genband.isup.enumdata.EchoContDeviceIndEnum;
import com.genband.isup.enumdata.EncodingSchemeEnum;
import com.genband.isup.enumdata.EndToEndInfoIndEnum;
import com.genband.isup.enumdata.EndToEndMethodIndEnum;
import com.genband.isup.enumdata.GTIndicatorEnum;
import com.genband.isup.enumdata.HoldingIndEnum;
import com.genband.isup.enumdata.ISDNAccessIndEnum;
import com.genband.isup.enumdata.ISDNUserPartIndEnum;
import com.genband.isup.enumdata.ISDNUserPartPrefIndEnum;
import com.genband.isup.enumdata.InbandInfoIndEnum;
import com.genband.isup.enumdata.InfoDiscriminationIndiEnum;
import com.genband.isup.enumdata.IntNwNumEnum;
import com.genband.isup.enumdata.InterNwIndEnum;
import com.genband.isup.enumdata.LocationEnum;
import com.genband.isup.enumdata.MLPPUserIndEnum;
import com.genband.isup.enumdata.NatIntNatCallIndEnum;
import com.genband.isup.enumdata.NatureOfAddEnum;
import com.genband.isup.enumdata.NumIncmpltEnum;
import com.genband.isup.enumdata.NumPlanEnum;
import com.genband.isup.enumdata.RoutingIndicatorEnum;
import com.genband.isup.enumdata.SCCPMethodIndENum;
import com.genband.isup.enumdata.SPCIndicatorEnum;
import com.genband.isup.enumdata.SSNIndicatorEnum;
import com.genband.isup.enumdata.SatelliteIndEnum;
import com.genband.isup.enumdata.ScreeningIndEnum;
import com.genband.isup.enumdata.SimpleSegmentationIndEnum;
import com.genband.isup.enumdata.TTCNatureOfAddEnum;
import com.genband.isup.enumdata.TransmissionMedReqEnum;
import com.genband.isup.enumdata.TypeOfSubaddress;
import com.genband.isup.messagetypes.ACMMessage;
import com.genband.isup.messagetypes.ANMMessage;
import com.genband.isup.messagetypes.IAMMessage;
import com.genband.isup.messagetypes.RELMessage;
import com.genband.isup.messagetypes.RLCMessage;
import com.genband.isup.operations.ISUPConstants;
import com.genband.isup.operations.ISUPOperationsCoding;
import com.genband.isup.util.NonAsnArg;
import com.genband.isup.util.Util;

public class TestOperations extends TestCase {

	private static Logger logger = Logger.getLogger(TestOperations.class);

	protected void setUp() throws Exception {
		super.setUp();
		PropertyConfigurator.configure("config" + System.getProperty("file.separator") + "log4j.properties");
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testDecodeEncodeOperations() throws Exception
	{
		LinkedList<byte[]> opBuffer = new LinkedList<byte[]>();
		LinkedList<String> opCodes = new LinkedList<String>();

		byte[] b= {(byte)0x01, (byte)0x10, (byte)0x48, (byte)0x00, (byte)0x0f, (byte)0x03, (byte)0x02, (byte)0x09, (byte)0x07, (byte)0x01, (byte)0x90, 
				(byte)0x08, (byte)0x10, (byte)0x32, (byte)0x21, (byte)0x63, (byte)0x08, (byte)0x01, (byte)0x00, (byte)0x8a, (byte)0x07, (byte)0x01, 
				(byte)0x13, (byte)0x18, (byte)0x41, (byte)0x45, (byte)0x00, (byte)0x21, (byte)0x31, (byte)0x02, (byte)0x00, (byte)0x00, (byte)0x00};

		opBuffer.add(b);
		opCodes.add(ISUPConstants.OP_CODE_IAM);

		LinkedList<Object> out = ISUPOperationsCoding.decodeOperations(opBuffer, opCodes, ISUPConstants.ISUP_ITUT);
		//System.out.println(out.get(0));
		IAMMessage iam = (IAMMessage)out.get(0);
		assertEquals("1", iam.getMessageType());
		//assertEquals(ContCheckIndEnum.CONTINUITY_NOT_REQUIRED, iam.getNatureOfConnIndicators().getContCheckIndEnum());
		//assertEquals(1, iam.getForwardCallIndicators());
		//assertEquals(1, iam.getCalledPartyNumber());
		//assertEquals(1, iam.getCallingPartyCategory());
		//assertEquals(1, iam.getTmr());
		//assertEquals(1, iam.getCallingPartyNumber());
		//assertEquals("0x00 0x00", Util.formatBytes(iam.getPropagationDelayCounter()));

		LinkedList<byte[]> buffer = ISUPOperationsCoding.encodeOperations(out, opCodes);
		logger.debug(ISUPOperationsCoding.decodeOperations(buffer, opCodes, ISUPConstants.ISUP_ITUT));
		//System.out.println(Util.formatBytes(buffer.get(0)));
		//assertEquals("0x01 0x10 0x48 0x00 0x0f 0x03 0x02 0x09 0x07 0x01 0x90 0x08 0x10 0x32 0x21 0x63 0x8a 0x07 " +
		//		"0x01 0x13 0x18 0x41 0x45 0x00 0x21 0x31 0x02 0x00 0x00 0x08 0x01 0x00 0x00", Util.formatBytes(buffer.get(0)));

		// IAM ANSI
		byte[] bAnsi = {(byte)0x01, (byte)0x00, (byte)0x20, (byte)0x10, (byte)0x0A, (byte)0x03, (byte)0x06, (byte)0x0D, (byte)0x03, (byte)0x80, 
				(byte)0x90, (byte)0xA2, (byte)0x07, (byte)0x03, (byte)0x10, (byte)0x12, (byte)0x53, (byte)0x27, (byte)0x11,  
				(byte)0x34, (byte)0x0A, (byte)0x07, (byte)0x03, (byte)0x13, (byte)0x49, (byte)0x79, (byte)0x77, (byte)0x17, 
				(byte)0x22, (byte)0xEB, (byte)0x07, (byte)0x03, (byte)0x10, (byte)0x49, (byte)0x79, (byte)0x77, (byte)0x17, 
				(byte)0x22, (byte)0xC0, (byte)0x08, (byte)0x00, (byte)0x03, (byte)0x10, (byte)0x68, (byte)0x96, (byte)0x17, 
				(byte)0x62, (byte)0x65, (byte)0xC4, (byte)0x03, (byte)0x49, (byte)0x79, (byte)0x77, (byte)0x00};

		LinkedList<byte[]> opBufAnsi = new LinkedList<byte[]>();
		LinkedList<String> opCodeAnsi = new LinkedList<String>();

		opBufAnsi.add(bAnsi);
		opCodeAnsi.add(ISUPConstants.OP_CODE_IAM);

		LinkedList<Object> outAnsi = ISUPOperationsCoding.decodeOperations(opBufAnsi, opCodeAnsi, ISUPConstants.ISUP_ANSI);
		IAMMessage iamAnsi = (IAMMessage)outAnsi.get(0);
		assertEquals("1", iamAnsi.getMessageType());
		System.out.println(iamAnsi.toString());
		logger.debug(ISUPOperationsCoding.decodeOperations(opBufAnsi, opCodeAnsi, ISUPConstants.ISUP_ANSI));

		LinkedList<Object> mTypes = new LinkedList<Object>();
		LinkedList<String> opCodesOut = new LinkedList<String>();
		opCodesOut.add(ISUPConstants.OP_CODE_IAM);
		mTypes.add(iamAnsi);

		LinkedList<byte[]> bufferOut = ISUPOperationsCoding.encodeOperations(mTypes, opCodesOut);

		System.out.print("Original Buffer:" + Util.formatBytes(bAnsi) + "\n"); 
		System.out.print("Encoded  Buffer:" + Util.formatBytes(bufferOut.get(0)) + "\n"); 
	}


	public void testDecodeEncodeREL() throws Exception
	{
		LinkedList<Object> mTypes = new LinkedList<Object>();
		LinkedList<String> opCodes = new LinkedList<String>();
		opCodes.add(ISUPConstants.OP_CODE_REL);		

		RELMessage rel = new RELMessage();		
		byte[] mt = {(byte)0x0c};
		mTypes.add(rel);
		byte[] causeBytes = Cause.encodeCauseVal(LocationEnum.TRANSIT_NETWORK, CodingStndEnum.ITUT_STANDARDIZED_CODING, CauseValEnum.Service_not_available);
		//System.out.println(Util.formatBytes(causeBytes));
		rel.setMessageType(mt);
		rel.setCause(causeBytes);

		LinkedList<byte[]> buffer = ISUPOperationsCoding.encodeOperations(mTypes, opCodes);
		//System.out.println(Util.formatBytes(buffer.get(0)));
		assertEquals("0x0c 0x02 0x00 0x02 0x83 0xbf", Util.formatBytes(buffer.get(0)));

		RLCMessage rlc = new RLCMessage();
		byte[] mt1 = {(byte)0x10};
		rlc.setMessageType(mt1);
		mTypes.clear();
		mTypes.add(rlc);
		opCodes.clear();
		opCodes.add(ISUPConstants.OP_CODE_RLC);	

		buffer = ISUPOperationsCoding.encodeOperations(mTypes, opCodes);
		//System.out.println(Util.formatBytes(buffer.get(0)));
		assertEquals("0x10 0x00 0x00", Util.formatBytes(buffer.get(0)));

	}

	public void testDecodeEncodeANM() throws Exception
	{
		LinkedList<Object> mTypes = new LinkedList<Object>();
		LinkedList<String> opCodes = new LinkedList<String>();

		ANMMessage anm = new ANMMessage();		
		byte[] mt = {(byte)0x09};
		byte[] bwBtes = BwCallIndicators.encodeBwCallInd(ChargeIndEnum.NO_CHARGE, CalledPartyStatusIndEnum.CONNECT_WHEN_FREE, CalledPartyCatIndEnum.NO_INDICATION, 
				EndToEndMethodIndEnum.PASS_ALONG_SCCP_METHOD, InterNwIndEnum.NO_INTER_NW_ENC, EndToEndInfoIndEnum.END_INFO_AVAILABLE, 
				ISDNUserPartIndEnum.ISDN_USER_PART_USED, HoldingIndEnum.HOLDING_NOT_REQUESTED, ISDNAccessIndEnum.NON_ISDN, 
				EchoContDeviceIndEnum.DEVICE_NOT_INCLUDED, SCCPMethodIndENum.CONNECTIONORIENTED_METHOD);
		//System.out.println(Util.formatBytes(bwBtes));
		anm.setMessageType(mt);
		anm.setBackwardCallIndicators(bwBtes);

		mTypes.add(anm);
		opCodes.add(ISUPConstants.OP_CODE_ANM);		

		LinkedList<byte[]> buffer = ISUPOperationsCoding.encodeOperations(mTypes, opCodes);
		//System.out.println(Util.formatBytes(buffer.get(0)));
		assertEquals("0x09 0x00 0x01 0x11 0x02 0xc9 0x86 0x00", Util.formatBytes(buffer.get(0)));
		buffer.clear();

		byte[] testBA = {(byte)0x09, (byte)0x00, (byte)0x01, (byte)0x11, (byte)0x02, (byte)0xc9, (byte)0x86, (byte)0x12, (byte)0x01, (byte)0x13, (byte)0x00};
		buffer.add(testBA);
		LinkedList<Object> outObj = ISUPOperationsCoding.decodeOperations(buffer, opCodes, ISUPConstants.ISUP_ITUT);
		logger.debug(outObj.get(0));

		anm.setOtherOptParams(((ANMMessage)outObj.get(0)).getOtherOptParams());
		buffer = ISUPOperationsCoding.encodeOperations(mTypes, opCodes);
		//System.out.println(outObj.get(0));
		//System.out.println(Util.formatBytes(buffer.get(0)));
		assertEquals("0x09 0x00 0x01 0x11 0x02 0xc9 0x86 0x12 0x01 0x13 0x00", Util.formatBytes(buffer.get(0)));

	}


	public void testDecodeEncodeACM() throws Exception
	{
		LinkedList<Object> mTypes = new LinkedList<Object>();
		LinkedList<String> opCodes = new LinkedList<String>();

		ACMMessage acm = new ACMMessage();		
		byte[] mt = {(byte)0x06};
		byte[] bwBtes = BwCallIndicators.encodeBwCallInd(ChargeIndEnum.NO_CHARGE, CalledPartyStatusIndEnum.CONNECT_WHEN_FREE, CalledPartyCatIndEnum.NO_INDICATION, 
				EndToEndMethodIndEnum.PASS_ALONG_SCCP_METHOD, InterNwIndEnum.NO_INTER_NW_ENC, EndToEndInfoIndEnum.END_INFO_AVAILABLE, 
				ISDNUserPartIndEnum.ISDN_USER_PART_USED, HoldingIndEnum.HOLDING_NOT_REQUESTED, ISDNAccessIndEnum.NON_ISDN, 
				EchoContDeviceIndEnum.DEVICE_NOT_INCLUDED, SCCPMethodIndENum.CONNECTIONORIENTED_METHOD);
		acm.setMessageType(mt);
		acm.setBackwardCallIndicators(bwBtes);

		byte[] cause = Cause.encodeCauseVal(LocationEnum.NETWORK_BEYOND_INTERWORKING_POINT, CodingStndEnum.ITUT_STANDARDIZED_CODING,CauseValEnum.Call_rejected);
		acm.setCauseIndicators(cause);

		byte[] optBCI = OptBwCallIndicators.encodeOptBwCallInd(InbandInfoIndEnum.INBAND_INFO, CallDiversionIndEnum.NO_INDICATION, 
				SimpleSegmentationIndEnum.NO_INFORMATION, MLPPUserIndEnum.NO_INDICATION);
		acm.setOptBwCallIndicators(optBCI);

		byte[] ttcCAI = TtcChargeAreaInfo.encodeTtcChargeAreaInfo("1234", InfoDiscriminationIndiEnum.MA_CODE);
		acm.setChargeAreaInfo(ttcCAI);

		byte[] ttcCIT = {(byte)0x03, (byte)0xfb, (byte)0x05, (byte)0xfe, (byte)0x03, (byte)0x00, (byte)0x20, (byte)0x30};
		acm.setCarrierInfoTrfr(ttcCIT);

		mTypes.add(acm);
		opCodes.add(ISUPConstants.OP_CODE_ACM);		

		LinkedList<byte[]> buffer = ISUPOperationsCoding.encodeOperations(mTypes, opCodes);
		//System.out.println(Util.formatBytes(buffer.get(0)));
		assertEquals("0x06 0xc9 0x86 0x00 0x01 0x12 0x02 0x8a 0x95 0x29 0x01 0x01 0xf1 0x08 0x03 0xfb 0x05" +
				" 0xfe 0x03 0x00 0x20 0x30 0xfd 0x03 0x00 0x21 0x43 0x00", Util.formatBytes(buffer.get(0)));

		logger.debug(ISUPOperationsCoding.decodeOperations(buffer, opCodes, ISUPConstants.ISUP_ITUT));
	}


	public void testDecodeEncodeIAM() throws Exception
	{
		LinkedList<Object> mTypes = new LinkedList<Object>();
		LinkedList<String> opCodes = new LinkedList<String>();

		IAMMessage iam = new IAMMessage();		
		byte[] mt = {(byte)0x01};
		iam.setMessageType(mt);

		byte[] natOfConn = NatOfConnIndicators.encodeConnIndicators(SatelliteIndEnum.ONE_SATELLITE, ContCheckIndEnum.CONTINUITY_PREVIOUS, EchoContDeviceIndEnum.DEVICE_NOT_INCLUDED);
		iam.setNatureOfConnIndicators(natOfConn);

		byte[] fwCI = FwCallIndicators.encodeFwCallInd(NatIntNatCallIndEnum.INTERNATIONAL_CALL, EndToEndMethodIndEnum.PASS_ALONG_METHOD, InterNwIndEnum.INTER_NW_ENC, 
				EndToEndInfoIndEnum.NO_END_INFO_AVAILABLE, ISDNUserPartIndEnum.ISDN_USER_PART_NOT_USED, ISDNUserPartPrefIndEnum.ISDN_REQUIRED, 
				ISDNAccessIndEnum.NON_ISDN, SCCPMethodIndENum.NO_INDICATION);
		iam.setForwardCallIndicators(fwCI);

		byte[] cpCat = NonAsnArg.encodeCalgPartyCatg(CalgPartyCatgEnum.OPRT_SPANISH);
		iam.setCallingPartyCategory(cpCat);

		byte[] tmr = NonAsnArg.encodeTmr(TransmissionMedReqEnum.KBPS_15_64);
		iam.setTmr(tmr);

		byte[] cdpNum = CalledPartyNum.encodeCaldParty("1234", NatureOfAddEnum.NATIONAL_NO, NumPlanEnum.TELEX_NP, IntNwNumEnum.ROUTING_ALLWD);
		iam.setCalledPartyNumber(cdpNum);

		byte[] cpNum = CallingPartyNum.encodeCalgParty("1234", NatureOfAddEnum.NATIONAL_NO,  NumPlanEnum.TELEX_NP ,AddPrsntRestEnum.PRSNT_ALLWD,
				ScreeningIndEnum.USER_PROVD, NumIncmpltEnum.COMPLETE);
		iam.setCallingPartyNumber(cpNum);

		byte[] crrId = GenericDigits.encodeGenericDigits(EncodingSchemeEnum.BCD_EVEN, DigitCatEnum.CORRELATION_ID, "1234");
		iam.setCorrelationId(crrId);

		byte[] scfId = ScfId.encodeScfId(SPCIndicatorEnum.SPC_PRESENT, SSNIndicatorEnum.SSN_PRESENT, GTIndicatorEnum.NO_GT, RoutingIndicatorEnum.ROUTING_PC_SSN, 24, 3, 10);
		iam.setScfId(scfId);

		byte[] cldInNum = TtcCalledINNumber.encodeTtcCalledINNum("1234", TTCNatureOfAddEnum.NATIONAL_NO,  NumPlanEnum.TELEX_NP ,AddPrsntRestEnum.PRSNT_ALLWD);
		iam.setCalledINNumber(cldInNum);

		CalledPartySubaddress cpSA = new CalledPartySubaddress();
		cpSA.setTypeOfSubaddress(TypeOfSubaddress.NSAP);
		cpSA.setSubAddInfo("abc");
		byte[] at = AccessTransport.encodeAccessTransport(cpSA, null);
		iam.setAccessTransport(at);

		mTypes.add(iam);
		opCodes.add(ISUPConstants.OP_CODE_IAM);		

		LinkedList<byte[]> buffer = ISUPOperationsCoding.encodeOperations(mTypes, opCodes);
		//System.out.println(Util.formatBytes(buffer.get(0)));
		assertEquals("0x01 0x09 0x8b 0x00 0x05 0x1c 0x02 0x06 0x04 0x03 0x40 0x21 0x43 0x8a 0x04 0x03 0x41 0x21 0x43 0xc1 0x03 0x1e 0x21 " +
				"0x43 0x66 0x04 0x43 0x6a 0x30 0xbf 0x6f 0x04 0x03 0x40 0x21 0x43 0x03 0x07 0x71 0x05 0x80 0x50 0x61 0x62 0x63 0x00", Util.formatBytes(buffer.get(0)));	

		logger.debug(ISUPOperationsCoding.decodeOperations(buffer, opCodes, ISUPConstants.ISUP_ITUT));
	}

	public void testDecodeEncodeMsg() throws Exception
	{
		LinkedList<byte[]> opBuffer = new LinkedList<byte[]>();
		LinkedList<String> opCodes = new LinkedList<String>();
		opCodes.add(ISUPConstants.OP_CODE_CHG);

		byte[] b= {(byte)0x31, (byte)0x00, (byte)0x01, (byte)0x01, (byte)0xff, (byte)0x03, (byte)0x02, (byte)0x09, (byte)0x07, (byte)0x00 };

		opBuffer.add(b);

		LinkedList<Object> buffer = ISUPOperationsCoding.decodeOperations(opBuffer, opCodes, ISUPConstants.ISUP_ITUT);
		logger.debug(buffer);

	}

}
