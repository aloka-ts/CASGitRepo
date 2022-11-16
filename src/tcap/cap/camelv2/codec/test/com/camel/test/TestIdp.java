package com.camel.test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import junit.framework.TestCase;

import org.apache.log4j.PropertyConfigurator;
import org.bn.CoderFactory;
import org.bn.IDecoder;
import org.bn.IEncoder;

import asnGenerated.AddressString;
import asnGenerated.AgeOfLocationInformation;
import asnGenerated.CellGlobalIdOrServiceAreaIdFixedLength;
import asnGenerated.CellGlobalIdOrServiceAreaIdOrLAI;
import asnGenerated.Ext_BasicServiceCode;
import asnGenerated.Ext_TeleserviceCode;
import asnGenerated.ISDN_AddressString;
import asnGenerated.InitialDPArg;
import asnGenerated.LocationInformation;

import com.camel.dataTypes.AdrsStringDataType;
import com.camel.dataTypes.BearerCapabilityDataType;
import com.camel.dataTypes.CalledPartyNum;
import com.camel.dataTypes.CallingPartyNum;
import com.camel.dataTypes.CellIdFixedLenDataType;
import com.camel.dataTypes.IPSSCapabilitiesDataType;
import com.camel.dataTypes.LAIFixedLenDataType;
import com.camel.dataTypes.LocationNum;
import com.camel.dataTypes.PartyId;
import com.camel.dataTypes.TimeAndTimeZoneDataType;
import com.camel.enumData.AdrsPrsntRestdEnum;
import com.camel.enumData.CalgPartyCatgEnum;
import com.camel.enumData.CodingStndEnum;
import com.camel.enumData.ExtTeleServiceCodeEnum;
import com.camel.enumData.IntNtwrkNumEnum;
import com.camel.enumData.NatureOfAdrsEnum;
import com.camel.enumData.NatureOfAdrsStringEnum;
import com.camel.enumData.NumINcomplteEnum;
import com.camel.enumData.NumPlanEnum;
import com.camel.enumData.NumPlan_AdrsStringEnum;
import com.camel.enumData.ScreeningIndEnum;
import com.camel.enumData.bearerCapabilty.InfoTrfrRateEnum;
import com.camel.enumData.bearerCapabilty.InfoTrnsfrCapEnum;
import com.camel.enumData.bearerCapabilty.LayerIdentifierEnum;
import com.camel.enumData.bearerCapabilty.TransferModeEnum;
import com.camel.enumData.bearerCapabilty.UserInfoLayer1ProtocolEnum;
import com.camel.exceptions.InvalidInputException;
import com.camel.util.NonAsnArg;
import com.camel.util.Util;

public class TestIdp extends TestCase {

	protected void setUp() throws Exception {
		super.setUp();
		 PropertyConfigurator.configure("config" + System.getProperty("file.separator") + "log4j.properties");
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}
	
	public void testEncodeDecodeCaldParty(){
		
		CalledPartyNum cp = new CalledPartyNum();
		byte[] b;
		try {
			b = CalledPartyNum.encodeCaldParty("1234", NatureOfAdrsEnum.NATIONAL_NO, NumPlanEnum.TELEX_NP, IntNtwrkNumEnum.ROUTING_ALLWD);
			cp = null ;
			cp = CalledPartyNum.decodeCaldParty(b);
		} catch (InvalidInputException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		assertEquals("1234", cp.getAddrSignal());
		assertEquals(NatureOfAdrsEnum.NATIONAL_NO, cp.getNatureOfAdrs());
		assertEquals(IntNtwrkNumEnum.ROUTING_ALLWD, cp.getIntNtwrkNum());
		assertEquals(NumPlanEnum.TELEX_NP, cp.getNumPlan());
	}
	
	public void testEncodeDecodeCalgParty(){
		
		CallingPartyNum cp = new CallingPartyNum();
		byte[] b;
		try {
			b = CallingPartyNum.encodeCalgParty("1234", NatureOfAdrsEnum.NATIONAL_NO,  NumPlanEnum.TELEX_NP ,AdrsPrsntRestdEnum.PRSNT_ALLWD,
					ScreeningIndEnum.USER_PROVD, NumINcomplteEnum.COMPLETE);
			cp = null ;
			cp = CallingPartyNum.decodeCalgParty(b);
		} catch (InvalidInputException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		assertEquals("1234", cp.getAddrSignal());
		assertEquals(NatureOfAdrsEnum.NATIONAL_NO, cp.getNatureOfAdrs());
		assertEquals(NumINcomplteEnum.COMPLETE, cp.getNumIncomplte());
		assertEquals(NumPlanEnum.TELEX_NP, cp.getNumPlan());
		assertEquals(ScreeningIndEnum.USER_PROVD, cp.getScreening());
		assertEquals(AdrsPrsntRestdEnum.PRSNT_ALLWD, cp.getAdrsPresntRestd());
	}
	
	public void testEncDecCalgPartyCatg(){
		
		byte[] data = NonAsnArg.encodeCalgPartyCatg(CalgPartyCatgEnum.OPRT_SPANISH);
		assertEquals("00000101", Util.conversiontoBinary(data[0]));
		
		CalgPartyCatgEnum calgEnum =  null;
		try {
			calgEnum = NonAsnArg.decodeCalgPartyCatg(data);
		} catch (InvalidInputException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		assertEquals(CalgPartyCatgEnum.OPRT_SPANISH, calgEnum);
	}
	
	public void testEncDecIpss(){
		
		IPSSCapabilitiesDataType ipCb = new IPSSCapabilitiesDataType();
		ipCb.voiceBack = true;
		ipCb.voiceInforamtionViaSpeech = true;
		ipCb.voiceInformationViavoice = true;
		byte[] b = IPSSCapabilitiesDataType.encodeIpSS(ipCb);
		assertEquals("00001110" ,Util.conversiontoBinary(b[0]));

		try {
			ipCb = IPSSCapabilitiesDataType.decodeIpSS(b);
		} catch (InvalidInputException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		assertEquals(false, ipCb.ipRouting);
		assertEquals(true, ipCb.voiceBack);
		assertEquals(true, ipCb.voiceInforamtionViaSpeech);
		assertEquals(true, ipCb.voiceInformationViavoice);
	}
	
	public void testEncDecLocationNum(){
		LocationNum cp = new LocationNum();
		byte[] b;
		try {
			b = LocationNum.encodeLocationNum("1234", NatureOfAdrsEnum.NATIONAL_NO,NumPlanEnum.TELEX_NP,
												AdrsPrsntRestdEnum.ADRS_NOT_AVAIL,ScreeningIndEnum.USER_PROVD, IntNtwrkNumEnum.ROUTING_NOT_ALLWD);
			assertEquals("00000000" , Util.conversiontoBinary(b[0]));
			assertEquals("00001011" , Util.conversiontoBinary(b[1]));
			cp = null ;
			cp = LocationNum.decodeLocationNum(b);
		} catch (InvalidInputException e) {
			e.printStackTrace();
		}
		
		assertEquals("1234", cp.getAddrSignal());
		assertEquals(NatureOfAdrsEnum.SPARE, cp.getNatureOfAdrs());
		assertEquals(IntNtwrkNumEnum.ROUTING_ALLWD, cp.getIntNtwrkNumEnum());
		assertEquals(NumPlanEnum.SPARE, cp.getNumPlan());
		assertEquals(ScreeningIndEnum.NETWORK_PROVD, cp.getScreening());
		assertEquals(AdrsPrsntRestdEnum.ADRS_NOT_AVAIL, cp.getAdrsPresntRestd());
	}
	
	public void testEncDecPartyId(){
		PartyId cp = new PartyId();
		byte[] b;
		try {
			b = PartyId.encodePartyId("1234", NatureOfAdrsEnum.NATIONAL_NO, NumPlanEnum.TELEX_NP, AdrsPrsntRestdEnum.PRSNT_ALLWD);
			assertEquals("00000011" , Util.conversiontoBinary(b[0]));
			assertEquals("01000000" , Util.conversiontoBinary(b[1]));
			cp = null ;
			cp = PartyId.decodePartyId(b);
		} catch (InvalidInputException e) {
			e.printStackTrace();
		}
		
		assertEquals("1234", cp.getAddrSignal());
		assertEquals(NatureOfAdrsEnum.NATIONAL_NO, cp.getNatureOfAdrs());
		assertEquals(null, cp.getNumIncomplte());
		assertEquals(NumPlanEnum.TELEX_NP, cp.getNumPlan());
		assertEquals(null, cp.getScreening());
		assertEquals(AdrsPrsntRestdEnum.PRSNT_ALLWD, cp.getAdrsPresntRestd());
	}
	
	public void testEncDecAdrsString(){
		AdrsStringDataType cp = new AdrsStringDataType();
		byte[] b;
		try {
			b = AdrsStringDataType.encodeAdrsString("*#abc123", NatureOfAdrsStringEnum.NATIONAL_NO, NumPlan_AdrsStringEnum.TELEX_NP);
			assertEquals("10100100" , Util.conversiontoBinary(b[0]));
			cp = null ;
			cp = AdrsStringDataType.decodeAdrsString(b);
		} catch (InvalidInputException e) {
			e.printStackTrace();
		}
		
		assertEquals("*#abc123", cp.getMscAdrs());
		assertEquals(NatureOfAdrsStringEnum.NATIONAL_NO, cp.getNatureOfAdrs());
		assertEquals(NumPlan_AdrsStringEnum.TELEX_NP, cp.getNumPlan());
	}
	
	public void testEncDecLai(){
		LAIFixedLenDataType lai = new LAIFixedLenDataType();
		byte[] data ;
		try {
			data = LAIFixedLenDataType.encodeLAI("091", "12", "123");
			assertEquals("10010000", Util.conversiontoBinary(data[0]));
			assertEquals("11110001", Util.conversiontoBinary(data[1]));
			assertEquals("00100001", Util.conversiontoBinary(data[2]));
			lai = LAIFixedLenDataType.decodeLAI(data);
			
		} catch (InvalidInputException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		assertEquals("091", lai.getMobileCountryCode());
		assertEquals("12", lai.getMobileNetworkCode());
		assertEquals("123", lai.getLocationAreaCode());
	}
	
	public void testEncDecCellId(){
		CellIdFixedLenDataType lai = new CellIdFixedLenDataType();
		byte[] data ;
		try {
			data = CellIdFixedLenDataType.encodeCellId("123","091", "123", "123");
			assertEquals("10010000", Util.conversiontoBinary(data[0]));
			assertEquals("00110001", Util.conversiontoBinary(data[1]));
			assertEquals("00100001", Util.conversiontoBinary(data[2]));
			lai = CellIdFixedLenDataType.decodeCellId(data);
			
		} catch (InvalidInputException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		assertEquals("091", lai.getMobileCountryCode());
		assertEquals("123", lai.getMobileNetworkCode());
		assertEquals("123", lai.getLocationAreaCode());
		assertEquals("123", lai.getCellIdentity());
	}
	
	public void testDecodeBearerCapability(){
		byte[] data = new byte[]{(byte)0x80,(byte)0x90,(byte)0xa3};
		try {
			BearerCapabilityDataType bearerCapabilityDataType = BearerCapabilityDataType.decodeBearerCapability(data);
			assertEquals(bearerCapabilityDataType.getCodingStnd(), CodingStndEnum.ITUT_STANDARDIZED_CODING);
			assertEquals(bearerCapabilityDataType.getInfoTrnsfrCap(), InfoTrnsfrCapEnum.SPEECH);
			assertEquals(bearerCapabilityDataType.getTransferMode(), TransferModeEnum.CIRCUIT_MODE);
			assertEquals(bearerCapabilityDataType.getInfoTrfrRate(), InfoTrfrRateEnum.KBITS_64);
			assertEquals(bearerCapabilityDataType.getLayerIdentifier(), LayerIdentifierEnum.LAYER_1);
			assertEquals(bearerCapabilityDataType.getUserInfoLayer1Protocol(), UserInfoLayer1ProtocolEnum.RECOMMEND_G711_A);
			
		} catch (InvalidInputException e) {
			e.printStackTrace();
		}
	}
	
	public void testDecodeTimeAndTimeZone(){
		byte[] data = new byte[]{(byte)0x02,(byte)0x11,(byte)0x10,(byte)0x71,(byte)0x60,(byte)0x03,(byte)0x00,(byte)0x84};
		try{
			TimeAndTimeZoneDataType timeZoneDataType = TimeAndTimeZoneDataType.deocdeTimeAndTimeZone(data);
			assertEquals("20110117063000", timeZoneDataType.getTime());
			assertEquals("GMT+12:00", timeZoneDataType.getTimeZone());
		}catch(Exception e){
			e.printStackTrace() ;
			assertFalse(true);
		}
	}
	
	public void testEncDecExtBasicServiceCode(){
		Ext_BasicServiceCode extBasicServiceCode = new Ext_BasicServiceCode() ;
		Ext_TeleserviceCode teleserviceCode = new Ext_TeleserviceCode() ;
		byte[] val = {0x11} ;
		teleserviceCode.setValue(val);
		extBasicServiceCode.selectExt_Teleservice(teleserviceCode);
		try{
		IEncoder<Ext_BasicServiceCode> encoder = CoderFactory.getInstance().newEncoder("BER");
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		encoder.encode(extBasicServiceCode, outputStream);
		byte[] encoded = outputStream.toByteArray();
		
		extBasicServiceCode = null ;
		InputStream ins = new ByteArrayInputStream(encoded);
		IDecoder idDecoder = CoderFactory.getInstance().newDecoder("BER");
		extBasicServiceCode = idDecoder.decode(ins, Ext_BasicServiceCode.class);
		byte[]telServiceCode = extBasicServiceCode.getExt_Teleservice().getValue();
		int teleCode = telServiceCode[0] ;
		ExtTeleServiceCodeEnum teleSrvCodeEnum  = ExtTeleServiceCodeEnum.fromInt(teleCode);
		assertEquals(ExtTeleServiceCodeEnum.TELEPHONY, teleSrvCodeEnum);
		}catch(Exception e){
			e.printStackTrace();
			assertFalse(true);
		}
	}
	
	public void testEncDecLocationInfo(){
		LocationInformation locationInformation = new LocationInformation();
		AgeOfLocationInformation ageOfLocationInformation = new AgeOfLocationInformation(0);
		locationInformation.setAgeOfLocationInformation(ageOfLocationInformation);
		try{
		byte[] encodedVlr = AdrsStringDataType.encodeAdrsString("0123456789", NatureOfAdrsStringEnum.INTER_NO, NumPlan_AdrsStringEnum.ISDN_NP);
		ISDN_AddressString isdnAddressString = new ISDN_AddressString();
		isdnAddressString.setValue(new AddressString(encodedVlr));
		locationInformation.setVlr_number(isdnAddressString);
		
		CellGlobalIdOrServiceAreaIdOrLAI cellGlobalIdOrServiceAreaIdOrLAI = new CellGlobalIdOrServiceAreaIdOrLAI() ;
		CellGlobalIdOrServiceAreaIdFixedLength cellGlobalIdOrServiceAreaIdFixedLength = new CellGlobalIdOrServiceAreaIdFixedLength() ;
		byte[] cellGlobal = {(byte)0x64, (byte)0xf0, (byte)0xf0, 0x00, 0x01 , 0x00, 0x00};
		cellGlobalIdOrServiceAreaIdFixedLength.setValue(cellGlobal);
		cellGlobalIdOrServiceAreaIdOrLAI.selectCellGlobalIdOrServiceAreaIdFixedLength(cellGlobalIdOrServiceAreaIdFixedLength);
		locationInformation.setCellGlobalIdOrServiceAreaIdOrLAI(cellGlobalIdOrServiceAreaIdOrLAI);
		IEncoder<LocationInformation> encoder = CoderFactory.getInstance().newEncoder("BER");
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		encoder.encode(locationInformation, outputStream);
		byte[] encoded = outputStream.toByteArray();
		
		System.out.println(Util.formatBytes(encoded));
		locationInformation = null ;
		InputStream ins = new ByteArrayInputStream(encoded);
		IDecoder idDecoder = CoderFactory.getInstance().newDecoder("BER");
		locationInformation = idDecoder.decode(ins, LocationInformation.class);
		assertEquals(new Integer(0), locationInformation.getAgeOfLocationInformation().getValue());
		}catch(Exception e){
			assertFalse(true);
		}
	}
}
