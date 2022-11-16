package com.camel.test;

import jain.protocol.ss7.tcap.component.InvokeIndEvent;
import jain.protocol.ss7.tcap.component.Operation;
import jain.protocol.ss7.tcap.component.Parameters;
import jain.protocol.ss7.tcap.component.ResultIndEvent;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.apache.log4j.PropertyConfigurator;
import org.bn.CoderFactory;
import org.bn.IEncoder;

import asnGenerated.AddressString;
import asnGenerated.AgeOfLocationInformation;
import asnGenerated.ApplyChargingReportArg;
import asnGenerated.AudibleIndicator;
import asnGenerated.BearerCapability;
import asnGenerated.CAMEL_CallResult;
import asnGenerated.CallReferenceNumber;
import asnGenerated.CallResult;
import asnGenerated.CallSegmentID;
import asnGenerated.CalledPartyBCDNumber;
import asnGenerated.CalledPartyNumber;
import asnGenerated.CallingPartyNumber;
import asnGenerated.CallingPartysCategory;
import asnGenerated.CellGlobalIdOrServiceAreaIdFixedLength;
import asnGenerated.CellGlobalIdOrServiceAreaIdOrLAI;
import asnGenerated.EventReportBCSMArg;
import asnGenerated.EventTypeBCSM;
import asnGenerated.Ext_BasicServiceCode;
import asnGenerated.Ext_TeleserviceCode;
import asnGenerated.IMSI;
import asnGenerated.IPSSPCapabilities;
import asnGenerated.ISDN_AddressString;
import asnGenerated.InitialDPArg;
import asnGenerated.Integer4;
import asnGenerated.LegType;
import asnGenerated.LocationInformation;
import asnGenerated.LocationNumber;
import asnGenerated.MiscCallInfo;
import asnGenerated.ReceivingSideID;
import asnGenerated.ServiceKey;
import asnGenerated.TBCD_STRING;
import asnGenerated.TimeAndTimezone;
import asnGenerated.TimeIfNoTariffSwitch;
import asnGenerated.TimeIfTariffSwitch;
import asnGenerated.TimeInformation;
import asnGenerated.Tone;
import asnGenerated.CAMEL_CallResult.TimeDurationChargingResultSequenceType;
import asnGenerated.EventTypeBCSM.EnumType;
import asnGenerated.MiscCallInfo.MessageTypeEnumType;

import com.camel.CAPMsg.CAPOpcode;
import com.camel.CAPMsg.CAPSbb;
import com.camel.CAPMsg.SS7IndicationType;
import com.camel.CAPMsg.SasCapCallProcessBuffer;
import com.camel.CAPMsg.SasCapCallStateEnum;
import com.camel.CAPMsg.SasCapInbandInfoDataType;
import com.camel.CAPMsg.SasCapInformationtoSendEnum;
import com.camel.CAPMsg.SasCapMsgIdEnum;
import com.camel.CAPMsg.SasCapMsgsToSend;
import com.camel.CAPMsg.SasCapVariableMsgDataType;
import com.camel.CAPMsg.SasCapVariablePartDataType;
import com.camel.dataTypes.AdrsSignalDataType;
import com.camel.dataTypes.AdrsStringDataType;
import com.camel.dataTypes.BearerCapabilityDataType;
import com.camel.dataTypes.CalledPartyNum;
import com.camel.dataTypes.CallingPartyNum;
import com.camel.dataTypes.GenericDigitsDataType;
import com.camel.dataTypes.GenericNumDataType;
import com.camel.dataTypes.IPSSCapabilitiesDataType;
import com.camel.dataTypes.ImsiDataType;
import com.camel.dataTypes.LocationNum;
import com.camel.enumData.AdrsPrsntRestdEnum;
import com.camel.enumData.CalgPartyCatgEnum;
import com.camel.enumData.EncodingSchemeEnum;
import com.camel.enumData.IntNtwrkNumEnum;
import com.camel.enumData.NatureOfAdrsEnum;
import com.camel.enumData.NatureOfAdrsStringEnum;
import com.camel.enumData.NumINcomplteEnum;
import com.camel.enumData.NumPlanEnum;
import com.camel.enumData.NumPlan_AdrsStringEnum;
import com.camel.enumData.NumQualifierIndEnum;
import com.camel.enumData.ScreeningIndEnum;
import com.camel.util.NonAsnArg;
import com.camel.util.Util;
import com.sun.org.apache.xpath.internal.operations.Variable;

public class TestCapSbb extends TestCase {
	
	SasCapCallProcessBuffer cpb = new SasCapCallProcessBuffer();
	CAPSbb capSbb ;
	protected void setUp() throws Exception {
		super.setUp();
		 PropertyConfigurator.configure("config" + System.getProperty("file.separator") + "log4j.properties");
		 capSbb = CAPSbb.getInstance();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}
	
	public void testConnect(){	
		cpb.stateInfo.setPrevState(null);
		cpb.stateInfo.setCurrState(SasCapCallStateEnum.ANALYZED_INFORMATION);
		cpb.dlgId = 12 ;
		CalledPartyNum cldNum = new CalledPartyNum() ;
		cldNum.setAddrSignal("1234");
		cldNum.setIntNtwrkNum(IntNtwrkNumEnum.ROUTING_ALLWD);
		cldNum.setNatureOfAdrs(NatureOfAdrsEnum.NATIONAL_NO);
		cldNum.setNumPlan(NumPlanEnum.DATA_NP);
		ArrayList<CalledPartyNum> list = new ArrayList<CalledPartyNum>();
		list.add(cldNum);
		cpb.destRoutingAdd = list ;
		SasCapMsgsToSend msgToSend = new SasCapMsgsToSend() ;
		try {
			//send requestReport, ApplyCharging, CallINformationReq, Connect
			cpb.applyChargingReq = true ;
			cpb.callInfoRequest = true ;
			cpb.maxCallPeriodDuration = 100 ;
			capSbb.connect(capSbb, cpb, msgToSend);
			assertEquals(5, msgToSend.getCompReqEvents().size());
			assertNotNull(msgToSend.getDlgReqEvent());
			
			cpb.destRoutingAdd = null ;
			cpb.applyChargingReq = false ;
			cpb.callInfoRequest = false ;
			cpb.stateInfo.setCurrState(SasCapCallStateEnum.O_BUSY);
			//cpb.calSegmentIDForContinueWithArg = new CallSegmentID(1);
			cpb.legIdForContinueWithArg = "01" ;
			//send requestReport, ApplyCharging,CallINformationReq, ContinueWithArg
			capSbb.connect(capSbb, cpb, msgToSend);
			assertEquals(2, msgToSend.getCompReqEvents().size());
			assertNotNull(msgToSend.getDlgReqEvent());
			
		} catch (Exception e) {
			e.printStackTrace();
			assertTrue(false);
		}
	}
	
	public void testDisconnectIvr(){
		cpb.stateInfo.setCurrState(SasCapCallStateEnum.USER_INTERACTION_COMPLETED);
		SasCapMsgsToSend msgToSend = new SasCapMsgsToSend() ;
		try {
			//send DFC
			capSbb.disconnectIvr(capSbb, cpb, msgToSend);
			assertEquals(1, msgToSend.getCompReqEvents().size());
			assertNotNull(msgToSend.getDlgReqEvent());
			cpb.calSegmentIDForDFCWithArg = new CallSegmentID();
			cpb.calSegmentIDForDFCWithArg.setValue(1);
			cpb.stateInfo.setCurrState(SasCapCallStateEnum.USER_INTERACTION_COMPLETED);
			//send DFCWithArg
			capSbb.disconnectIvr(capSbb, cpb, msgToSend);
			assertEquals(1, msgToSend.getCompReqEvents().size());
			assertNotNull(msgToSend.getDlgReqEvent());
		} catch (Exception e) {
			e.printStackTrace();
			assertTrue(false);
		}
	}
	
	public void testReleaseCall(){
		cpb.stateInfo.setCurrState(SasCapCallStateEnum.O_BUSY);
		SasCapMsgsToSend msgToSend = new SasCapMsgsToSend() ;
		try {
			//send requestReport, ApplyCharging, Connect
			capSbb.releaseCall(capSbb, cpb, msgToSend);
			assertEquals(1, msgToSend.getCompReqEvents().size());
			assertNotNull(msgToSend.getDlgReqEvent());
			
		} catch (Exception e) {
			e.printStackTrace();
			assertTrue(false);
		}
	}
	
	public void testPlay(){
		cpb.stateInfo.setCurrState(SasCapCallStateEnum.IVR_DISCONNECTED);
		SasCapMsgsToSend msgToSend = new SasCapMsgsToSend() ;
		CalledPartyNum cldNum = new CalledPartyNum() ;
		cldNum.setAddrSignal("1234");
		cldNum.setIntNtwrkNum(IntNtwrkNumEnum.ROUTING_ALLWD);
		cldNum.setNatureOfAdrs(NatureOfAdrsEnum.NATIONAL_NO);
		cldNum.setNumPlan(NumPlanEnum.DATA_NP);
		cpb.ipRoutingAdrs = cldNum ;
		cpb.informationtoSendEnum = SasCapInformationtoSendEnum.TONE ;
		cpb.tone = new Tone();
		cpb.tone.setToneID(new Integer4(4));
		try {
			//send CTR, play
			capSbb.play(capSbb, cpb, msgToSend);
			assertEquals(2, msgToSend.getCompReqEvents().size());
			assertNotNull(msgToSend.getDlgReqEvent());
			
		} catch (Exception e) {
			e.printStackTrace();
			assertTrue(false);
		}
	}
	
	public void testPlayAndCollect(){
		cpb.stateInfo.setCurrState(SasCapCallStateEnum.IVR_DISCONNECTED);
		SasCapMsgsToSend msgToSend = new SasCapMsgsToSend() ;
		
		cpb.collectedDigits.errorTreatment = null ;
		cpb.collectedDigits.voiceBack = null ;
		cpb.collectedDigits.voiceInformation = null ;
		
		CalledPartyNum cldNum = new CalledPartyNum() ;
		cldNum.setAddrSignal("1234");
		cldNum.setIntNtwrkNum(IntNtwrkNumEnum.ROUTING_ALLWD);
		cldNum.setNatureOfAdrs(NatureOfAdrsEnum.NATIONAL_NO);
		cldNum.setNumPlan(NumPlanEnum.DATA_NP);
		cpb.ipRoutingAdrs = cldNum ;
		cpb.informationtoSendEnum = SasCapInformationtoSendEnum.INBAND_INFO ;
		cpb.inbandInfoDataType = new SasCapInbandInfoDataType() ;
		//cpb.elementryMsgId = 5 ;
		cpb.inbandInfoDataType.msgId = SasCapMsgIdEnum.VARIABLEMSG ;
		cpb.variableMsg = new SasCapVariableMsgDataType();
		cpb.variableMsg.elementryMsgId = 1 ;
		SasCapVariablePartDataType varPartData =  new SasCapVariablePartDataType() ;
	//	varPartData.time = "1234" ;
	//	varPartData.date = "20100208" ;
		varPartData.price = "00000078" ;
		GenericDigitsDataType number = new GenericDigitsDataType();
		number.digits = "1234" ;
		number.encodingSchemeEnum = EncodingSchemeEnum.BCD_EVEN ;
		//varPartData.number = number ;
		List<SasCapVariablePartDataType> list = new ArrayList<SasCapVariablePartDataType>();
		list.add(varPartData);
		cpb.variableMsg.variablePartList = list ;
		try {
			//send CTR , playAndCollect
			cpb.resetTimer = true ;
			capSbb.playAndCollect(capSbb, cpb, msgToSend);
			assertEquals(3, msgToSend.getCompReqEvents().size());
			assertNotNull(msgToSend.getDlgReqEvent());
			
		} catch (Exception e) {
			e.printStackTrace();
			assertTrue(false);
		}
	}
	
	public void testApplyCharge(){
		cpb.stateInfo.setCurrState(SasCapCallStateEnum.CONNECTED_ACHRPT);
		
		SasCapMsgsToSend msgToSend = new SasCapMsgsToSend() ;
		try {
			cpb.maxCallPeriodDuration =100 ;
			cpb.audibleIndicator = false;
			cpb.releaseIfDurationExced = true ;
			//cpb.tariifSwitchInterval = 80000 ;
			//send ApplyCharging, Continue
			capSbb.applyCharging(capSbb, cpb, msgToSend);
			assertEquals(2, msgToSend.getCompReqEvents().size());
			assertNotNull(msgToSend.getDlgReqEvent());
			cpb.legIdForContinueWithArg = "01" ;
			//send ApplyCharging, ContinueWithArg
			capSbb.applyCharging(capSbb, cpb, msgToSend);
			assertEquals(2, msgToSend.getCompReqEvents().size());
			
		} catch (Exception e) {
			e.printStackTrace();
			assertTrue(false);
		}
	}
	
	public void testFurnishCharging(){
		cpb.stateInfo.setCurrState(SasCapCallStateEnum.CONNECTED_ACHRPT);
		cpb.freeFormatData = new byte[]{0x01 , 0x02, 0x03, 0x04};
		SasCapMsgsToSend msgToSend = new SasCapMsgsToSend() ;
		try {
			//send requestReport, ApplyCharging, Connect
			capSbb.furnishCharging(capSbb, cpb, msgToSend);
			assertEquals(1, msgToSend.getCompReqEvents().size());
			assertNotNull(msgToSend.getDlgReqEvent());
			
		} catch (Exception e) {
			e.printStackTrace();
			assertTrue(false);
		}
	}
	
	public void testUpdateForIdp(){
		InitialDPArg idp = new InitialDPArg();
		ServiceKey key = new ServiceKey();
		key.setValue(new Integer4(110));
		idp.setServiceKey(key);
		try {
		byte[] cldParty = CalledPartyNum.encodeCaldParty("9999111333", NatureOfAdrsEnum.INTER_NO, NumPlanEnum.ISDN_NP, IntNtwrkNumEnum.ROUTING_ALLWD);
		byte[] clgParty = CallingPartyNum.encodeCalgParty("9999111333", NatureOfAdrsEnum.INTER_NO, NumPlanEnum.ISDN_NP, AdrsPrsntRestdEnum.PRSNT_ALLWD,ScreeningIndEnum.USER_PROVD_NOT_VERFD , NumINcomplteEnum.COMPLETE);
		//idp.setCalledPartyNumber(new CalledPartyNumber(cldParty));
		idp.setCallingPartyNumber(new CallingPartyNumber(clgParty));
		
		CallingPartysCategory calgCallingPartysCategory = new CallingPartysCategory();
		calgCallingPartysCategory.setValue(NonAsnArg.encodeCalgPartyCatg(CalgPartyCatgEnum.ORD_SUBSR));
		idp.setCallingPartysCategory(calgCallingPartysCategory);
		
		IPSSPCapabilities ip = new IPSSPCapabilities();
		IPSSCapabilitiesDataType ipData = new IPSSCapabilitiesDataType();
		ipData.voiceInformationViavoice = false ;
		ip.setValue(IPSSCapabilitiesDataType.encodeIpSS(ipData));
		idp.setIPSSPCapabilities(ip);
		
		LocationNumber locNum = new LocationNumber();
		locNum.setValue(LocationNum.encodeLocationNum("0123456789", NatureOfAdrsEnum.INTER_NO, NumPlanEnum.ISDN_NP,AdrsPrsntRestdEnum.PRSNT_ALLWD, ScreeningIndEnum.USER_PROVD_NOT_VERFD, IntNtwrkNumEnum.ROUTING_ALLWD));
		idp.setLocationNumber(locNum);
		
		BearerCapability bearerCapability = new BearerCapability();
		byte[] data = new byte[]{(byte)0x80,(byte)0x90,(byte)0xa3};
		bearerCapability.selectBearerCap(data);
		idp.setBearerCapability(bearerCapability);
		
		EventTypeBCSM eventTypeBCSM = new EventTypeBCSM();
		eventTypeBCSM.setValue(EnumType.collectedInfo);
		idp.setEventTypeBCSM(eventTypeBCSM);
		
		IMSI imsi = new IMSI();
		byte[] imsiData = new byte[]{(byte)0x64,(byte)0x00,(byte)0x12,(byte)0x00,(byte)0x14,(byte)0x10,(byte)0x33,(byte)0xf3};
		imsi.setValue(new TBCD_STRING(imsiData));
		idp.setIMSI(imsi);
		
		Ext_BasicServiceCode extBasicServiceCode = new Ext_BasicServiceCode() ;
		Ext_TeleserviceCode teleserviceCode = new Ext_TeleserviceCode() ;
		byte[] val = {0x11} ;
		teleserviceCode.setValue(val);
		extBasicServiceCode.selectExt_Teleservice(teleserviceCode);
		idp.setExt_basicServiceCode(extBasicServiceCode);
		
		byte[] timedata = new byte[]{(byte)0x02,(byte)0x11,(byte)0x10,(byte)0x71,(byte)0x60,(byte)0x03,(byte)0x00,(byte)0x84};
		TimeAndTimezone timeAndTimezone = new TimeAndTimezone(timedata);
		//idp.setTimeAndTimezone(timeAndTimezone);
		
		LocationInformation locInformation = new LocationInformation() ;
		locInformation.setAgeOfLocationInformation(new AgeOfLocationInformation(0));
		ISDN_AddressString isdnAddressString = new ISDN_AddressString() ;
		byte[] adrsStringDataType = AdrsStringDataType.encodeAdrsString("0123456798", NatureOfAdrsStringEnum.INTER_NO, NumPlan_AdrsStringEnum.ISDN_NP);
		isdnAddressString.setValue(new AddressString(adrsStringDataType));
		locInformation.setVlr_number(isdnAddressString);
		
		
		CellGlobalIdOrServiceAreaIdOrLAI cellGlobalIdOrServiceAreaIdOrLAI = new CellGlobalIdOrServiceAreaIdOrLAI();
		CellGlobalIdOrServiceAreaIdFixedLength cellGlobalIdOrServiceAreaIdFixedLength = new CellGlobalIdOrServiceAreaIdFixedLength() ;
		byte[] cellGlobal = {(byte)0x64, (byte)0xf0, (byte)0xf0, 0x00, 0x01 , 0x00, 0x00};
		cellGlobalIdOrServiceAreaIdFixedLength.setValue(cellGlobal);
		cellGlobalIdOrServiceAreaIdOrLAI.selectCellGlobalIdOrServiceAreaIdFixedLength(cellGlobalIdOrServiceAreaIdFixedLength);
		locInformation.setCellGlobalIdOrServiceAreaIdOrLAI(cellGlobalIdOrServiceAreaIdOrLAI);
		idp.setLocationInformation(locInformation);
		
		
		
		byte[] callref = {(byte)0x00, (byte)0x01, (byte)0x0b, 0x01, 0x00 , 0x02, 0x00};
		CallReferenceNumber callReferenceNumber = new CallReferenceNumber(callref);
		//idp.setCallReferenceNumber(callReferenceNumber);
		
		byte[] mscAdrsSData = {(byte)0x91, (byte)0x10, (byte)0x32, 0x54, 0x76 , (byte)0x98};
		AddressString adrsString = new AddressString(AdrsStringDataType.encodeAdrsString("0123456789", NatureOfAdrsStringEnum.INTER_NO, NumPlan_AdrsStringEnum.ISDN_NP));
		ISDN_AddressString isdnString = new ISDN_AddressString();
		isdnAddressString.setValue(adrsString);
		//idp.setMscAddress(isdnAddressString);
		
		byte[] calledBCDNum = {(byte)0xa1, (byte)0x99, (byte)0x99, 0x11, 0x31 , (byte)0x22};
		CalledPartyBCDNumber calledPartyBCDNumber = new CalledPartyBCDNumber(calledBCDNum);
		//idp.setCalledPartyBCDNumber(calledPartyBCDNumber);
		
		
		
		IEncoder<InitialDPArg> encoder = CoderFactory.getInstance().newEncoder("BER");
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		encoder.encode(idp, outputStream);
		//byte[] encoded = outputStream.toByteArray();
		
		// with srvKey and locInfo only and it working
		//byte[] encoded = {0x30,(byte)0x1c,(byte)0x80,(byte)0x01,(byte)0x6e,(byte)0xbf,(byte)0x34,(byte)0x16,(byte)0x02,(byte)0x01,(byte)0x00,(byte)0x81,(byte)0x06,(byte)0x91,(byte)0x10,(byte)0x32,(byte)0x54,(byte)0x76,(byte)0x98,(byte)0xa3,(byte)0x09,(byte)0x80,(byte)0x07,(byte)0x64,(byte)0xf0,(byte)0xf0,(byte)0x00,(byte)0x01,(byte)0x00,(byte)0x00};
		
		byte[] encoded = {(byte)0x30,(byte)0x76,(byte)0x80,(byte)0x01,(byte)0x6e,(byte)0x83,(byte)0x07,(byte)0x04,(byte)0x10,(byte)0x99,(byte)0x99,(byte)0x11,(byte)0x31,(byte)0x33,(byte)0x85,(byte)0x01,(byte)0x0a,(byte)0x88,(byte)0x01,(byte)0x00,(byte)0x8a,(byte)0x07,(byte)0x04,(byte)0x10,(byte)0x10,(byte)0x32,(byte)0x54,(byte)0x76,(byte)0x98,(byte)0xbb,(byte)0x05,(byte)0x80,(byte)0x03,(byte)0x80,(byte)0x90,(byte)0xa3,(byte)0x9c,(byte)0x01,(byte)0x02,(byte)0x9f,(byte)0x32,(byte)0x08,(byte)0x64,(byte)0x00,(byte)0x12,(byte)0x00,(byte)0x14,(byte)0x10,(byte)0x33,(byte)0xf3,(byte)0xbf,(byte)0x34,(byte)0x16,(byte)0x02,(byte)0x01,(byte)0x00,(byte)0x81,(byte)0x06,(byte)0x91,(byte)0x10,(byte)0x32,(byte)0x54,(byte)0x76,(byte)0x98,(byte)0xa3,(byte)0x09,(byte)0x80,(byte)0x07,(byte)0x64,(byte)0xf0,(byte)0xf0,(byte)0x00,(byte)0x01,(byte)0x01,(byte)0x01,(byte)0xbf,(byte)0x35,(byte)0x03,(byte)0x83,(byte)0x01,(byte)0x11,(byte)0x9f,(byte)0x36,(byte)0x07,(byte)0x00,(byte)0x01,(byte)0x0b,(byte)0x01,(byte)0x00,(byte)0x08,(byte)0x00,(byte)0x9f,(byte)0x37,(byte)0x06,(byte)0x91,(byte)0x10,(byte)0x32,(byte)0x54,(byte)0x76,(byte)0x98,(byte)0x9f,(byte)0x38,(byte)0x06,(byte)0xa1,(byte)0x99,(byte)0x99,(byte)0x11,(byte)0x31,(byte)0x22,(byte)0x9f,(byte)0x39,(byte)0x08,(byte)0x02,(byte)0x11,(byte)0x10,(byte)0x13,(byte)0x12,(byte)0x40,(byte)0x14,(byte)0x22};
		//byte[] encoded = {(byte)0x30,(byte)0x5D,(byte)0x80,(byte)0x01,(byte)0x6e,(byte)0x83,(byte)0x07,(byte)0x04,(byte)0x10,(byte)0x99,(byte)0x99,(byte)0x11,(byte)0x31,(byte)0x33,(byte)0x85,(byte)0x01,(byte)0x0a,(byte)0x88,(byte)0x01,(byte)0x00,(byte)0x8a,(byte)0x07,(byte)0x04,(byte)0x10,(byte)0x10,(byte)0x32,(byte)0x54,(byte)0x76,(byte)0x98,(byte)0xbb,(byte)0x05,(byte)0x80,(byte)0x03,(byte)0x80,(byte)0x90,(byte)0xa3,(byte)0x9c,(byte)0x01,(byte)0x02,(byte)0x9f,(byte)0x32,(byte)0x08,(byte)0x64,(byte)0x00,(byte)0x12,(byte)0x00,(byte)0x14,(byte)0x10,(byte)0x33,(byte)0xf3,(byte)0xbf,(byte)0x35,(byte)0x03,(byte)0x83,(byte)0x01,(byte)0x11,(byte)0x9f,(byte)0x36,(byte)0x07,(byte)0x00,(byte)0x01,(byte)0x0b,(byte)0x01,(byte)0x00,(byte)0x08,(byte)0x00,(byte)0x9f,(byte)0x37,(byte)0x06,(byte)0x91,(byte)0x10,(byte)0x32,(byte)0x54,(byte)0x76,(byte)0x98,(byte)0x9f,(byte)0x38,(byte)0x06,(byte)0xa1,(byte)0x99,(byte)0x99,(byte)0x11,(byte)0x31,(byte)0x22,(byte)0x9f,(byte)0x39,(byte)0x08,(byte)0x02,(byte)0x11,(byte)0x10,(byte)0x13,(byte)0x12,(byte)0x40,(byte)0x14,(byte)0x22};
		//byte[] encoded = {(byte)0x30,(byte)0x76,(byte)0x80,(byte)0x01,(byte)0x6e,(byte)0x83,(byte)0x07,(byte)0x04,(byte)0x10,(byte)0x99,(byte)0x99,(byte)0x11,(byte)0x31,(byte)0x33,(byte)0x85,(byte)0x01,(byte)0x0a,(byte)0x88,(byte)0x01,(byte)0x00,(byte)0x8a,(byte)0x07,(byte)0x04,(byte)0x10,(byte)0x10,(byte)0x32,(byte)0x54,(byte)0x76,(byte)0x98,(byte)0xbb,(byte)0x05,(byte)0x80,(byte)0x03,(byte)0x80,(byte)0x90,(byte)0xa3,(byte)0x9c,(byte)0x01,(byte)0x02,(byte)0x9f,(byte)0x32,(byte)0x08,(byte)0x64,(byte)0x00,(byte)0x12,(byte)0x00,(byte)0x14,(byte)0x10,(byte)0x33,(byte)0xf3,(byte)0xbf,(byte)0x34,(byte)0x16,(byte)0x02,(byte)0x01,(byte)0x00,(byte)0x81,(byte)0x06,(byte)0x91,(byte)0x10,(byte)0x32,(byte)0x54,(byte)0x76,(byte)0x98,(byte)0xa3,(byte)0x09,(byte)0x80,(byte)0x07,(byte)0x64,(byte)0xf0,(byte)0xf0,(byte)0x00,(byte)0x01,(byte)0x01,(byte)0x01,(byte)0xbf,(byte)0x35,(byte)0x03,(byte)0x83,(byte)0x01,(byte)0x11,(byte)0x9f,(byte)0x36,(byte)0x07,(byte)0x00,(byte)0x01,(byte)0x0b,(byte)0x01,(byte)0x00,(byte)0x08,(byte)0x00,(byte)0x9f,(byte)0x37,(byte)0x06,(byte)0x91,(byte)0x10,(byte)0x32,(byte)0x54,(byte)0x76,(byte)0x98,(byte)0x9f,(byte)0x38,(byte)0x06,(byte)0xa1,(byte)0x99,(byte)0x99,(byte)0x11,(byte)0x31,(byte)0x22,(byte)0x9f,(byte)0x39,(byte)0x08,(byte)0x02,(byte)0x11,(byte)0x10,(byte)0x13,(byte)0x12,(byte)0x40,(byte)0x14,(byte)0x22};
		
		byte[] idpOpCode =  { CAPOpcode.IDP_CODE };
		Operation idpOp = new Operation(Operation.OPERATIONTYPE_LOCAL, idpOpCode);
		InvokeIndEvent ireContinue = new InvokeIndEvent(capSbb);			
		ireContinue.setOperation(idpOp);
		ireContinue.setParameters(new Parameters(Parameters.PARAMETERTYPE_SEQUENCE, encoded));
		ireContinue.setClassType(CAPOpcode.CLASS_TWO);
		ireContinue.setInvokeId(1);
		capSbb.updateCAPObj(ireContinue, SS7IndicationType.COMPONENT, cpb);
			
		} catch (Exception e) {
			e.printStackTrace();
			assertTrue(false);
		}
	}
	
	public void testUpdateForACR(){
		ApplyChargingReportArg acArg = new ApplyChargingReportArg();
		CAMEL_CallResult camelCallResult = new CAMEL_CallResult();
		TimeDurationChargingResultSequenceType timeCharging = new TimeDurationChargingResultSequenceType();
		
		//timeCharging.setCallActive(true);
		
		ReceivingSideID side = new ReceivingSideID() ;
		byte[] legId = {0x01} ;
		side.selectReceivingSideID(new LegType(legId));
		
		timeCharging.setPartyToCharge(side);
		
		TimeInformation timeInfo = new TimeInformation() ;
		timeInfo.selectTimeIfNoTariffSwitch(new TimeIfNoTariffSwitch(277));
		
		TimeIfTariffSwitch timeIfTariffSwitch = new TimeIfTariffSwitch();		
		//timeIfTariffSwitch.setTariffSwitchInterval(5);		
		timeIfTariffSwitch.setTimeSinceTariffSwitch(5);
		//timeInfo.selectTimeIfTariffSwitch(timeIfTariffSwitch);
		
		timeCharging.setTimeInformation(timeInfo);
		
		
		
		camelCallResult.selectTimeDurationChargingResult(timeCharging);
		try {
			IEncoder<CAMEL_CallResult> enocder = CoderFactory.getInstance().newEncoder("BER");
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			enocder.encode(camelCallResult, outputStream);
			byte[] encodedCallResult = outputStream.toByteArray();
			System.out.println("Camel-CallResult:"+ Util.formatBytes(encodedCallResult));
			acArg.setValue(new CallResult(encodedCallResult));
			IEncoder<ApplyChargingReportArg> enocder1 = CoderFactory.getInstance().newEncoder("BER");
			ByteArrayOutputStream outputStream1 = new ByteArrayOutputStream();
			enocder1.encode(acArg, outputStream1);
			byte[] encodedAcr = outputStream1.toByteArray();
		//byte[] encodedAcr = {0x04,(byte)0x11,(byte)0xa0,(byte)0x0f,(byte)0xa0,(byte)0x03,(byte)0x81,(byte)0x01,(byte)0x01,(byte)0x82,(byte)0x01,(byte)0xff,(byte)0xa1,(byte)0x05,(byte)0xa1,(byte)0x03,(byte)0x80,(byte)0x01,(byte)0x05};
			//byte[] encodedAcr = {(byte)0xa0,(byte)0x12,(byte)0xa0,(byte)0x03,(byte)0x81,(byte)0x01,(byte)0x01,(byte)0xa1,(byte)0x08,(byte)0xa1,(byte)0x06,(byte)0x80,(byte)0x01,(byte)0x0a,(byte)0x81,(byte)0x01,(byte)0x0a,(byte)0x82,(byte)0x01,(byte)0xff} ;
			//byte[] encodedAcr = {0x04, 0x16, (byte)0x81, 0x01 ,0x01, (byte)0xa0,0x11,0x30,(byte)0x0F,(byte)0xa0,0x03,(byte)0x81,0x01,0x01,(byte)0xa1,0x05,(byte)0xa0,0x03,(byte)0xa0,0x01,0x60,(byte)0x82,0x01,(byte)0xff};
			//byte []encodedAcr = {0x04, 0x13,(byte)0xa0 ,0x12, (byte)0xa0 ,0x03 ,(byte)0x81 ,0x01 ,0x01 ,(byte)0xa1, 0x08 ,(byte)0xa1, 0x06,(byte) 0x80 ,0x01 ,0x1c ,(byte)0x81 ,0x01 ,0x18, (byte)0x82 ,0x01, (byte)0xff};
			byte[] acrCode =  { 0x24 };
			Operation acrOp = new Operation(Operation.OPERATIONTYPE_LOCAL, acrCode);
			InvokeIndEvent ireContinue = new InvokeIndEvent(capSbb);			
			ireContinue.setOperation(acrOp);
			ireContinue.setParameters(new Parameters(Parameters.PARAMETERTYPE_SEQUENCE, encodedAcr));
			ireContinue.setClassType(CAPOpcode.RRBCSM_CLASS);
			//ireContinue.setInvokeId(1);
			//ireContinue.setLastInvokeEvent(true);
			//System.out.println(ireContinue.getPrimitiveType());
			cpb.maxCallPeriodDuration =100 ;
			cpb.totalTimeDuration = 200 ;
			capSbb.updateCAPObj(ireContinue, SS7IndicationType.COMPONENT, cpb);
			
		}catch(Exception e){
			e.printStackTrace();
			assertTrue(false);
		}		
	}
	
	public void testUpdateForEventReport(){
		EventReportBCSMArg eventReportArg = new EventReportBCSMArg();
		EventTypeBCSM event = new EventTypeBCSM();
		event.setValue(EnumType.oAnswer);
		eventReportArg.setEventTypeBCSM(event);
		
		ReceivingSideID side = new ReceivingSideID() ;
		byte[] legId = {0x02} ;
		side.selectReceivingSideID(new LegType(legId));
		
		eventReportArg.setLegID(side);
		MiscCallInfo miscCallInfo = new MiscCallInfo();
		MessageTypeEnumType messageTypeEnumType = new MessageTypeEnumType();
		messageTypeEnumType.setValue(asnGenerated.MiscCallInfo.MessageTypeEnumType.EnumType.request);
		miscCallInfo.setMessageType(messageTypeEnumType);
		eventReportArg.setMiscCallInfo(miscCallInfo);
		try {
			IEncoder<EventReportBCSMArg> enocder = CoderFactory.getInstance().newEncoder("BER");
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			enocder.encode(eventReportArg, outputStream);
			byte[] encodedEventArg = outputStream.toByteArray();
						
			byte[] acrCode =  { 0x18 };
			//byte[] encodedEventArg ={(byte)0x30,(byte)0x14,(byte)0x80,(byte)0x01,(byte)0x07,(byte)0x81,(byte)0x05,(byte)0x55,(byte)0x44,(byte)0x65,(byte)0x88,(byte)0x09,(byte)0xa3,(byte)0x03,(byte)0x81,(byte)0x01,(byte)0x02,(byte)0xa4,(byte)0x03,(byte)0x80,(byte)0x01,(byte)0x00};
			Operation acrOp = new Operation(Operation.OPERATIONTYPE_LOCAL, acrCode);
			InvokeIndEvent ireContinue = new InvokeIndEvent(capSbb);			
			ireContinue.setOperation(acrOp);
			ireContinue.setParameters(new Parameters(Parameters.PARAMETERTYPE_SEQUENCE, encodedEventArg));
			ireContinue.setClassType(CAPOpcode.CALL_INFORMATION_RPT_CLASS);
			ireContinue.setInvokeId(1);
			capSbb.updateCAPObj(ireContinue, SS7IndicationType.COMPONENT, cpb);
			
		}catch(Exception e){
			e.printStackTrace();
			assertTrue(false);
		}		
	}
	
	public void testUpdateForRRSLT(){
		try {
		byte[] rrSltCode =  { 0x30 };
		byte[] encodedData = {(byte)0x80,0x02,0x10, 0x03};
		Operation rrSltOp = new Operation(Operation.OPERATIONTYPE_LOCAL, rrSltCode);
		ResultIndEvent resultIndEvent = new ResultIndEvent(capSbb,54321, true, true);			
		resultIndEvent.setOperation(rrSltOp);
		resultIndEvent.setParameters(new Parameters(Parameters.PARAMETERTYPE_SEQUENCE, encodedData));
		resultIndEvent.setInvokeId(1);
		capSbb.updateCAPObj(resultIndEvent, SS7IndicationType.COMPONENT, cpb);
		}catch(Exception e){
			e.printStackTrace();
			assertTrue(false);
		}
	}
	
	public void testUpdateForEntityReleased(){
		try {
		byte[] entityRlsdCode =  { 0x60 };
		byte[] encodedData = {(byte)0xA0,(byte)0x07,(byte)0x80,(byte)0x01,(byte)0x01,(byte)0x82,(byte)0x02,(byte)0x0C,(byte)0x00};
		Operation entityRlsdOp = new Operation(Operation.OPERATIONTYPE_LOCAL, entityRlsdCode);
		InvokeIndEvent ireContinue = new InvokeIndEvent(capSbb);			
		ireContinue.setOperation(entityRlsdOp);
		ireContinue.setParameters(new Parameters(Parameters.PARAMETERTYPE_SEQUENCE, encodedData));
		ireContinue.setClassType(CAPOpcode.CLASS_FOUR);
		ireContinue.setInvokeId(1);
		capSbb.updateCAPObj(ireContinue, SS7IndicationType.COMPONENT, cpb);
		}catch(Exception e){
			e.printStackTrace();
			assertTrue(false);
		}
	}
	
	public void testConnectToIVR(){
		cpb.stateInfo.setCurrState(SasCapCallStateEnum.ANALYZED_INFORMATION);
		cpb.assistSSPIPRoutingAdrs = new GenericNumDataType();
		cpb.assistSSPIPRoutingAdrs.setAddrSignal("123456");
		cpb.assistSSPIPRoutingAdrs.setAdrsPresntRestd(AdrsPrsntRestdEnum.PRSNT_ALLWD);
		cpb.assistSSPIPRoutingAdrs.setNatureOfAdrs(NatureOfAdrsEnum.NATIONAL_NO);
		cpb.assistSSPIPRoutingAdrs.setNumIncomplte(NumINcomplteEnum.COMPLETE);
		cpb.assistSSPIPRoutingAdrs.setNumPlan(NumPlanEnum.DATA_NP);
		cpb.assistSSPIPRoutingAdrs.setNumQualifier(NumQualifierIndEnum.ADD_CALLED_NO);
		cpb.assistSSPIPRoutingAdrs.setScreening(ScreeningIndEnum.USER_PROVD);
		SasCapMsgsToSend msgToSend = new SasCapMsgsToSend() ;
		
		cpb.correlationId = new GenericDigitsDataType();
		cpb.correlationId.setDigits("123456");
		cpb.correlationId.setEncodingSchemeEnum(EncodingSchemeEnum.BCD_EVEN);
		try {
			//send requestReport, ApplyCharging, Connect
			capSbb.connectToIVR(capSbb, cpb, msgToSend);
			assertEquals(1, msgToSend.getCompReqEvents().size());
			assertNotNull(msgToSend.getDlgReqEvent());
			
		} catch (Exception e) {
			e.printStackTrace();
			assertTrue(false);
		}
		
	}
}
