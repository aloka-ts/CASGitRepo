package com.genband.inap.test;

import java.util.LinkedList;

import junit.framework.TestCase;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import com.genband.inap.asngenerated.TTCEventSpecificInformationCharging;
import com.genband.inap.asngenerated.TTCEventTypeCharging;
import com.genband.inap.asngenerated.TTCSCIBillingChargingCharacteristics;
import com.genband.inap.asngenerated.TTCSpecificEventTypeCharging.TTCSpecificEventTypeChargingEnumType;
import com.genband.inap.datatypes.BearerCapability;
import com.genband.inap.datatypes.CalledPartyNum;
import com.genband.inap.datatypes.CallingPartyNum;
import com.genband.inap.datatypes.CarrierIdentificationCode;
import com.genband.inap.datatypes.CarrierInfoSubordinate;
import com.genband.inap.datatypes.CarrierInformation;
import com.genband.inap.datatypes.EventSpecificInfoChar;
import com.genband.inap.datatypes.EventTypeChar;
import com.genband.inap.datatypes.FwCallIndicators;
import com.genband.inap.datatypes.POIChargeAreaInfo;
import com.genband.inap.datatypes.POILevelInfo;
import com.genband.inap.datatypes.Reason;
import com.genband.inap.datatypes.SCIBillingChargingChar;
import com.genband.inap.datatypes.TtcAdtnalPartyCategory;
import com.genband.inap.datatypes.TtcBwCallIndicators;
import com.genband.inap.datatypes.TtcCalledINNumber;
import com.genband.inap.datatypes.TtcCarrierInfoTrfr;
import com.genband.inap.datatypes.TtcChargeAreaInfo;
import com.genband.inap.datatypes.TtcChargeInfoDelay;
import com.genband.inap.datatypes.TtcContractorNum;
import com.genband.inap.enumdata.AddPrsntRestEnum;
import com.genband.inap.enumdata.AdtnlPartyCat1Enum;
import com.genband.inap.enumdata.AdtnlPartyCatNameEnum;
import com.genband.inap.enumdata.CalgPartyCatgEnum;
import com.genband.inap.enumdata.CalledPartyCatIndEnum;
import com.genband.inap.enumdata.CalledPartyStatusIndEnum;
import com.genband.inap.enumdata.CarrierInfoNameEnum;
import com.genband.inap.enumdata.CarrierInfoSubordinateEnum;
import com.genband.inap.enumdata.ChargeIndicatorEnum;
import com.genband.inap.enumdata.ChargeInfoDelayEnum;
import com.genband.inap.enumdata.CodingStndEnum;
import com.genband.inap.enumdata.EchoContDeviceIndEnum;
import com.genband.inap.enumdata.EndToEndInfoIndEnum;
import com.genband.inap.enumdata.EndToEndMethodIndEnum;
import com.genband.inap.enumdata.HoldingIndEnum;
import com.genband.inap.enumdata.ISDNAccessIndEnum;
import com.genband.inap.enumdata.ISDNUserPartIndEnum;
import com.genband.inap.enumdata.ISDNUserPartPrefIndEnum;
import com.genband.inap.enumdata.InfoDiscriminationIndiEnum;
import com.genband.inap.enumdata.IntNwNumEnum;
import com.genband.inap.enumdata.InterNwIndEnum;
import com.genband.inap.enumdata.MobileAdtnlPartyCat1Enum;
import com.genband.inap.enumdata.MobileAdtnlPartyCat2Enum;
import com.genband.inap.enumdata.MobileAdtnlPartyCat3Enum;
import com.genband.inap.enumdata.NatIntNatCallIndEnum;
import com.genband.inap.enumdata.NatureOfAddEnum;
import com.genband.inap.enumdata.NumIncmpltEnum;
import com.genband.inap.enumdata.NumPlanEnum;
import com.genband.inap.enumdata.OutsidePOIGradeInfoEnum;
import com.genband.inap.enumdata.ReasonEnum;
import com.genband.inap.enumdata.SCCPMethodIndENum;
import com.genband.inap.enumdata.ScreeningIndEnum;
import com.genband.inap.enumdata.TTCNatureOfAddEnum;
import com.genband.inap.enumdata.TransitCarrierIndEnum;
import com.genband.inap.enumdata.TransmissionMedReqEnum;
import com.genband.inap.enumdata.bearercapability.InfoTrfrRateEnum;
import com.genband.inap.enumdata.bearercapability.InfoTrnsfrCapEnum;
import com.genband.inap.enumdata.bearercapability.LayerIdentifierEnum;
import com.genband.inap.enumdata.bearercapability.TransferModeEnum;
import com.genband.inap.enumdata.bearercapability.UserInfoLayer1ProtocolEnum;
import com.genband.inap.exceptions.InvalidInputException;
import com.genband.inap.util.NonAsnArg;
import com.genband.inap.util.Util;

public class TestIdp extends TestCase {

	private static Logger logger = Logger.getLogger(TestIdp.class);
	
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
			b = CalledPartyNum.encodeCaldParty("1234", NatureOfAddEnum.NATIONAL_NO, NumPlanEnum.TELEX_NP, IntNwNumEnum.ROUTING_ALLWD);
			//System.out.println(Util.formatBytes(b));
			cp = null ;
			cp = CalledPartyNum.decodeCaldParty(b);
		} catch (InvalidInputException e) {
			assertFalse(true);
			e.printStackTrace();
		}
		assertEquals("1234", cp.getAddrSignal());
		assertEquals(NatureOfAddEnum.NATIONAL_NO, cp.getNatureOfAdrs());
		assertEquals(IntNwNumEnum.ROUTING_ALLWD, cp.getIntNtwrkNum());
		assertEquals(NumPlanEnum.TELEX_NP, cp.getNumPlan());
	}
	
	public void testEncodeDecodeCalgParty(){
		
		CallingPartyNum cp = new CallingPartyNum();
		byte[] b;
		try {
			b = CallingPartyNum.encodeCalgParty("1234", NatureOfAddEnum.NATIONAL_NO,  NumPlanEnum.TELEX_NP ,AddPrsntRestEnum.PRSNT_ALLWD,
					ScreeningIndEnum.USER_PROVD, NumIncmpltEnum.COMPLETE);
			//System.out.println(Util.formatBytes(b));
			cp = null ;
			cp = CallingPartyNum.decodeCalgParty(b);
		} catch (InvalidInputException e) {
			assertFalse(true);
			e.printStackTrace();
		}
		
		assertEquals("1234", cp.getAddrSignal());
		assertEquals(NatureOfAddEnum.NATIONAL_NO, cp.getNatureOfAdrs());
		assertEquals(NumIncmpltEnum.COMPLETE, cp.getNumIncomplte());
		assertEquals(NumPlanEnum.TELEX_NP, cp.getNumPlan());
		assertEquals(ScreeningIndEnum.USER_PROVD, cp.getScreening());
		assertEquals(AddPrsntRestEnum.PRSNT_ALLWD, cp.getAdrsPresntRestd());
	}
	
	public void testEncDecCalgPartyCatg(){
		
		byte[] data = NonAsnArg.encodeCalgPartyCatg(CalgPartyCatgEnum.OPRT_SPANISH);
		assertEquals("00000101", Util.conversiontoBinary(data[0]));
		
		CalgPartyCatgEnum calgEnum =  null;
		try {
			calgEnum = NonAsnArg.decodeCalgPartyCatg(data);
		} catch (InvalidInputException e) {
			assertFalse(true);
			e.printStackTrace();
		}
		assertEquals(CalgPartyCatgEnum.OPRT_SPANISH, calgEnum);
	}
	
	public void testEncDecFwCallInd(){
		
		FwCallIndicators fw = null;
		byte[] b;
		try {
			b = FwCallIndicators.encodeFwCallInd(NatIntNatCallIndEnum.INTERNATIONAL_CALL, EndToEndMethodIndEnum.PASS_ALONG_METHOD, InterNwIndEnum.INTER_NW_ENC, 
					EndToEndInfoIndEnum.NO_END_INFO_AVAILABLE, ISDNUserPartIndEnum.ISDN_USER_PART_NOT_USED, ISDNUserPartPrefIndEnum.ISDN_REQUIRED, 
					ISDNAccessIndEnum.NON_ISDN, SCCPMethodIndENum.NO_INDICATION);
			fw = FwCallIndicators.decodeFwCallInd(b);
		} catch (InvalidInputException e) {
			assertFalse(true);
			e.printStackTrace();
		}
		
		assertEquals(NatIntNatCallIndEnum.INTERNATIONAL_CALL, fw.getNatIntNatCallIndEnum());
		assertEquals(EndToEndMethodIndEnum.PASS_ALONG_METHOD, fw.getEndMethodIndEnum());
		assertEquals(InterNwIndEnum.INTER_NW_ENC, fw.getInterNwIndEnum());		
		assertEquals(EndToEndInfoIndEnum.NO_END_INFO_AVAILABLE, fw.getEndToEndInfoIndEnum());
		assertEquals(ISDNUserPartIndEnum.ISDN_USER_PART_NOT_USED, fw.getIsdnUserPartIndEnum());
		assertEquals(ISDNUserPartPrefIndEnum.ISDN_REQUIRED, fw.getIsdnUserPartPrefIndEnum());
		assertEquals(ISDNAccessIndEnum.NON_ISDN, fw.getIsdnAccessIndEnum());
		assertEquals(SCCPMethodIndENum.NO_INDICATION, fw.getSccpMethodIndENum());
	}
	
	public void testEncDecTmr(){
		
		byte[] data = NonAsnArg.encodeTmr(TransmissionMedReqEnum.KBPS_15_64);
		assertEquals("00011100", Util.conversiontoBinary(data[0]));
		
		TransmissionMedReqEnum tmrEnum =  null;
		try {
			tmrEnum = NonAsnArg.decodeTmr(data);
		} catch (InvalidInputException e) {
			assertFalse(true);
			e.printStackTrace();
		}
		assertEquals(TransmissionMedReqEnum.KBPS_15_64, tmrEnum);
	}
	
	public void testDecodeBearerCapability(){
		byte[] data = new byte[]{(byte)0x80,(byte)0x90,(byte)0xa3};
		try {
			BearerCapability bearerCapabilityDataType = BearerCapability.decodeBearerCapability(data);
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
	
	public void testEncDecSciBillingChar(){
		byte[] data = {(byte)0xaf, (byte)0x05, (byte)0xa0, (byte)0x03, (byte)0x80, (byte)0x01, (byte)0xff};
		try {
			TTCSCIBillingChargingCharacteristics ttcsci = SCIBillingChargingChar.decodeSciBillingChar(data);
			assertTrue(ttcsci.getTTCNOSpecificParametersSCIBCC().getValue().iterator().next().getTTCSpecificSCIBCC().getNoChargeIndicator());
						
			assertEquals(Util.formatBytes(data),Util.formatBytes(SCIBillingChargingChar.encodeSciBillingChar(ttcsci)));
		} catch (Exception e) {
			e.printStackTrace();
			assertFalse(true);
		}
	}
	
	public void testEncDecEventTypeChar(){
		byte[] data = {(byte)0xaf, (byte)0x0d, (byte)0xa0, (byte)0x0b, (byte)0xa0, (byte)0x09, (byte)0x0a, (byte)0x01, 
						(byte)0x00, (byte)0x0a, (byte)0x01, (byte)0x02, (byte)0x0a, (byte)0x01, (byte)0x03};
		try {
			TTCEventTypeCharging ttcetc = EventTypeChar.decodeEventTypeCharging((data));
			
			for(TTCSpecificEventTypeChargingEnumType ttc : ttcetc.getTTCNOSpecificParametersETChg().getValue().iterator().next().getTTCSpecificETChg().getTTCSpecificEventTypeCharging().getValue()) {
				if (logger.isDebugEnabled()) {
					logger.debug("enc event type charging: "+ttc.getValue());
				}
			}
						
			assertEquals(Util.formatBytes(data),Util.formatBytes(EventTypeChar.encodeEventTypeCharging(ttcetc)));
		} catch (Exception e) {
			e.printStackTrace();
			assertFalse(true);
		}
	}
	
	public void testEncDecEventSpecificInfoChar(){
		byte[] data = {(byte)0xaf, (byte)0x28, (byte)0xa0, (byte)0x26, (byte)0xa0, (byte)0x24, (byte)0x80, (byte)0x18,
						(byte)0x00, (byte)0xfc, (byte)0x05, (byte)0xfe, (byte)0x03, (byte)0x00, (byte)0x02, (byte)0x31, (byte)0xfe, (byte)0x0e,
						(byte)0xfe, (byte)0x03, (byte)0x00, (byte)0x22, (byte)0x33, (byte)0xfd, (byte)0x04, (byte)0x80, (byte)0x23, (byte)0x00, 
						(byte)0x00, (byte)0xfc, (byte)0x01, (byte)0x02, (byte)0x82, (byte)0x02, (byte)0x16, (byte)0x04, (byte)0x83, (byte)0x04,
						(byte)0x81, (byte)0x23, (byte)0x00, (byte)0x00};
		try {
			TTCEventSpecificInformationCharging ttcetc = EventSpecificInfoChar.decodeEventSpecificInfoCharging(data);
			if (logger.isDebugEnabled()) {
				logger.debug("enc event specific info charging cit: "+Util.formatBytes(ttcetc.getTTCNOSpecificParametersESIC().getValue().iterator().next().getTTCSpecificEventSpecificInfo().getTTCSpecificChargeEvent().getTtcCarrierInformationTransfer().getValue()));
				logger.debug("enc event specific info charging bwi: "+Util.formatBytes(ttcetc.getTTCNOSpecificParametersESIC().getValue().iterator().next().getTTCSpecificEventSpecificInfo().getTTCSpecificChargeEvent().getTtcBackwardCallIndicators().getValue()));
				logger.debug("enc event specific info charging cai: "+Util.formatBytes(ttcetc.getTTCNOSpecificParametersESIC().getValue().iterator().next().getTTCSpecificEventSpecificInfo().getTTCSpecificChargeEvent().getTtcChargeAreaInformation().getValue()));
			}
			assertEquals(Util.formatBytes(data),Util.formatBytes(EventSpecificInfoChar.encodeEventSpecificInfoCharging(ttcetc)));
		} catch (Exception e) {
			e.printStackTrace();
			assertFalse(true);
		}
	}
	
	public void testEncodeDecodeTtcContractorNum(){
		
		TtcContractorNum cp = new TtcContractorNum();
		byte[] b;
		try {
			b = TtcContractorNum.encodeTtcContractorNum("1234", NatureOfAddEnum.NATIONAL_NO, NumPlanEnum.TELEX_NP);
			cp = null ;
			cp = TtcContractorNum.decodeTtcContractorNum(b);
		} catch (InvalidInputException e) {
			assertFalse(true);
			e.printStackTrace();
		}
		assertEquals("1234", cp.getAddrSignal());
		assertEquals(NatureOfAddEnum.NATIONAL_NO, cp.getNatureOfAdrs());
		assertEquals(NumPlanEnum.TELEX_NP, cp.getNumPlan());
	}
	
	
	public void testEncodeDecodeCalledInNum(){
		
		TtcCalledINNumber cp = new TtcCalledINNumber();
		byte[] b;
		try {
			b = TtcCalledINNumber.encodeTtcCalledINNum("1234", TTCNatureOfAddEnum.NATIONAL_NO,  NumPlanEnum.TELEX_NP ,AddPrsntRestEnum.PRSNT_ALLWD);
			cp = null ;
			cp = TtcCalledINNumber.decodeTtcCalledINNum(b);
		} catch (InvalidInputException e) {
			assertFalse(true);
			e.printStackTrace();
		}
		
		assertEquals("1234", cp.getAddrSignal());
		assertEquals(TTCNatureOfAddEnum.NATIONAL_NO, cp.getNatureOfAdrs());
		assertEquals(NumPlanEnum.TELEX_NP, cp.getNumPlan());
		assertEquals(AddPrsntRestEnum.PRSNT_ALLWD, cp.getAdrsPresntRestd());
	}
	
	public void testEncDecTtcBwCallInd(){
		
		TtcBwCallIndicators bw = null;
		byte[] b;
		try {
			b = TtcBwCallIndicators.encodeTtcBwCallInd(ChargeIndicatorEnum.NO_CHARGE, CalledPartyStatusIndEnum.SUBSCRIBER_FREE, CalledPartyCatIndEnum.NO_INDICATION, 
					EndToEndMethodIndEnum.SCCP_METHOD, InterNwIndEnum.INTER_NW_ENC, EndToEndInfoIndEnum.NO_END_INFO_AVAILABLE, ISDNUserPartIndEnum.ISDN_USER_PART_NOT_USED, 
					HoldingIndEnum.HOLDING_REQUESTED, ISDNAccessIndEnum.NON_ISDN, EchoContDeviceIndEnum.DEVICE_INCLUDED, SCCPMethodIndENum.NO_INDICATION);
			
			bw = TtcBwCallIndicators.decodeTtcBwCallInd(b);
		} catch (InvalidInputException e) {
			assertFalse(true);
			e.printStackTrace();
		}
		
		assertEquals(ChargeIndicatorEnum.NO_CHARGE, bw.getChargeIndicatorEnum());
		assertEquals(CalledPartyStatusIndEnum.SUBSCRIBER_FREE, bw.getCalledPartyStatusIndEnum());
		assertEquals(CalledPartyCatIndEnum.NO_INDICATION, bw.getCalledPartyCatIndEnum());
		assertEquals(HoldingIndEnum.HOLDING_REQUESTED, bw.getHoldingIndEnum());
		assertEquals(EndToEndMethodIndEnum.SCCP_METHOD, bw.getEndMethodIndEnum());
		assertEquals(InterNwIndEnum.INTER_NW_ENC, bw.getInterNwIndEnum());		
		assertEquals(EndToEndInfoIndEnum.NO_END_INFO_AVAILABLE, bw.getEndToEndInfoIndEnum());
		assertEquals(ISDNUserPartIndEnum.ISDN_USER_PART_NOT_USED, bw.getIsdnUserPartIndEnum());
		assertEquals(EchoContDeviceIndEnum.DEVICE_INCLUDED, bw.getEchoContDeviceIndEnum());
		assertEquals(ISDNAccessIndEnum.NON_ISDN, bw.getIsdnAccessIndEnum());
		assertEquals(SCCPMethodIndENum.NO_INDICATION, bw.getSccpMethodIndENum());
	}
	
	
	public void testEncodeDecodeTtcChargeAreaInfo(){
		
		TtcChargeAreaInfo cai = new TtcChargeAreaInfo();
		byte[] b;
		try {
			b = TtcChargeAreaInfo.encodeTtcChargeAreaInfo("1234", InfoDiscriminationIndiEnum.MA_CODE);
			//System.out.println(Util.formatBytes(b));
			cai = TtcChargeAreaInfo.decodeTtcChargeAreaInfo(b);
		} catch (InvalidInputException e) {
			e.printStackTrace();
			assertFalse(true);			
		}
		assertEquals("1234", cai.getAddrSignal());
		assertEquals(InfoDiscriminationIndiEnum.MA_CODE, cai.getInfoDiscriminationIndiEnum());
	}
	
	
	public void testEncodeDecodeTtcChargeInfoDelay(){
		
		TtcChargeInfoDelay cid = new TtcChargeInfoDelay();
		byte[] b;
		try {
			LinkedList<ChargeInfoDelayEnum> chargeInfoDelay = new LinkedList<ChargeInfoDelayEnum>();
			chargeInfoDelay.add(ChargeInfoDelayEnum.CHARGING_RATE_TRANSFER);
			chargeInfoDelay.add(ChargeInfoDelayEnum.TERMINATING_CHARGE_AREA_INFO);
			b = TtcChargeInfoDelay.encodeTtcChargeInfoDelay(chargeInfoDelay);
			//System.out.println(Util.formatBytes(b));
			
			cid = TtcChargeInfoDelay.decodeTtcChargeInfoDelay(b);
		} catch (InvalidInputException e) {
			e.printStackTrace();
			assertFalse(true);			
		}
		
		
		assertEquals(253, cid.getChargeInfoDelay().iterator().next().getCode());		
	}
	
	
	public void testEncodeDecodeTtcAdtnlPartyCat(){
		
		LinkedList<TtcAdtnalPartyCategory> list = new LinkedList<TtcAdtnalPartyCategory>();
		
		TtcAdtnalPartyCategory apc1 = new TtcAdtnalPartyCategory();
		apc1.setAdtnlPartyCatNameEnum(AdtnlPartyCatNameEnum.PSTN_CATEGORY_1);
		apc1.setAdtnlPartyCat1Enum(AdtnlPartyCat1Enum.PINK_PUBLIC_TELEPHONE);
		
		TtcAdtnalPartyCategory apc2 = new TtcAdtnalPartyCategory();
		apc2.setAdtnlPartyCatNameEnum(AdtnlPartyCatNameEnum.MOBILE_CATEGORY_3);
		apc2.setMobileAdtnlPartyCat3Enum(MobileAdtnlPartyCat3Enum.RESERVED);
		
		TtcAdtnalPartyCategory apc3 = new TtcAdtnalPartyCategory();
		apc3.setAdtnlPartyCatNameEnum(AdtnlPartyCatNameEnum.MOBILE_CATEGORY_1);
		apc3.setMobileAdtnlPartyCat1Enum(MobileAdtnlPartyCat1Enum.INFLIGHT_TELEPHONE);
		
		TtcAdtnalPartyCategory apc4 = new TtcAdtnalPartyCategory();
		apc4.setAdtnlPartyCatNameEnum(AdtnlPartyCatNameEnum.MOBILE_CATEGORY_2);
		apc4.setMobileAdtnlPartyCat2Enum(MobileAdtnlPartyCat2Enum.NJ_TACS);
		
		list.add(apc1);
		list.add(apc2);
		list.add(apc3);
		list.add(apc4);
		
		byte[] b;
		try {
			b = TtcAdtnalPartyCategory.encodeTtcAdtnlPartyCat(list);
			//System.out.println(Util.formatBytes(b));
			
			list = TtcAdtnalPartyCategory.decodeTtcAdtnlPartyCat(b);
		} catch (InvalidInputException e) {
			e.printStackTrace();
			assertFalse(true);			
		}
				
		//System.out.println(list);
		assertEquals(AdtnlPartyCatNameEnum.PSTN_CATEGORY_1, list.iterator().next().getAdtnlPartyCatNameEnum());		
	}
	
	
	public void testEncodeDecodeReason(){

		Reason re= new Reason();
		byte[] b;
		try {
			b = Reason.encodeReason(ReasonEnum.APP_TIMER_EXPIRED);
			//System.out.println(Util.formatBytes(b));
			
			re = Reason.decodeReason(b);
		} catch (InvalidInputException e) {
			e.printStackTrace();
			assertFalse(true);			
		}
				
		//System.out.println(list);
		assertEquals(ReasonEnum.APP_TIMER_EXPIRED, re.getReasonEnum());		
	}
	
	public void testEncodeDecodePOILevelInfo(){

		POILevelInfo poi = new POILevelInfo();
		byte[] b;
		try {
			b = POILevelInfo.encodePOILevelInfo(OutsidePOIGradeInfoEnum.LEVEL_1, OutsidePOIGradeInfoEnum.LEVEL_2);
			//System.out.println(Util.formatBytes(b));
			
			poi = POILevelInfo.decodePOILevelInfo(b);
		} catch (InvalidInputException e) {
			e.printStackTrace();
			assertFalse(true);			
		}
				
		//System.out.println(list);
		assertEquals(OutsidePOIGradeInfoEnum.LEVEL_1, poi.getOutsidePOIGradeInfoEnum_LSB());
		assertEquals(OutsidePOIGradeInfoEnum.LEVEL_2, poi.getOutsidePOIGradeInfoEnum_MSB());
	}
	
	public void testEncodeDecodeCarrierIdentCode(){

		CarrierIdentificationCode cic = new CarrierIdentificationCode();
		byte[] b;
		try {
			b = CarrierIdentificationCode.encodeCarrierIdentCode("12345");
			//System.out.println(Util.formatBytes(b));
			
			cic = CarrierIdentificationCode.decodeCarrierIdentCode(b);
		} catch (InvalidInputException e) {
			e.printStackTrace();
			assertFalse(true);			
		}
				
		//System.out.println(list);
		assertEquals("12345", cic.getCarrierIdentCode());
	}
	
	public void testEncodeDecodeTTCCit(){

		TtcCarrierInfoTrfr ttcCit = new TtcCarrierInfoTrfr();
		byte[] b;
		try {
			LinkedList<CarrierInformation> ciList = new LinkedList<CarrierInformation>();
			LinkedList<CarrierInfoSubordinate> cisList = new LinkedList<CarrierInfoSubordinate>();
			LinkedList<CarrierInfoSubordinate> cisList1 = new LinkedList<CarrierInfoSubordinate>();
			
			CarrierInfoSubordinate cis = new CarrierInfoSubordinate();
			cis.setCarrierInfoSubordinateEnum(CarrierInfoSubordinateEnum.CARRIER_IDENT_CODE);
			CarrierIdentificationCode cic =  new CarrierIdentificationCode();
			cic.setCarrierIdentCode("0203");
			cis.setCarrierIdentificationCode(cic);
			cisList.add(cis);
			
			CarrierInformation ci = new CarrierInformation();
			ci.setCarrierInfoNameEnum(CarrierInfoNameEnum.OLEC);
			ci.setCarrierInfoSubordinate(cisList);
			
			CarrierInfoSubordinate cis1 = new CarrierInfoSubordinate();
			cis1.setCarrierInfoSubordinateEnum(CarrierInfoSubordinateEnum.CARRIER_IDENT_CODE);
			CarrierIdentificationCode cic1 =  new CarrierIdentificationCode();
			cic1.setCarrierIdentCode("2233");
			cis1.setCarrierIdentificationCode(cic1);
			cisList1.add(cis1);
			
			CarrierInfoSubordinate cis2 = new CarrierInfoSubordinate();
			cis2.setCarrierInfoSubordinateEnum(CarrierInfoSubordinateEnum.POI_CHARGE_AREA_INFO);
			POIChargeAreaInfo poiCAI =  new POIChargeAreaInfo();
			poiCAI.setChargeAreaInfo("32000");
			cis2.setPoiChargeAreaInfo(poiCAI);
			cisList1.add(cis2);
			
			CarrierInfoSubordinate cis3 = new CarrierInfoSubordinate();
			cis3.setCarrierInfoSubordinateEnum(CarrierInfoSubordinateEnum.POI_LEVEL_INFO);
			POILevelInfo poiLI =  new POILevelInfo();
			poiLI.setOutsidePOIGradeInfoEnum_LSB(OutsidePOIGradeInfoEnum.NO_INDICATION);
			poiLI.setOutsidePOIGradeInfoEnum_MSB(OutsidePOIGradeInfoEnum.LEVEL_2);
			cis3.setPoiLevelInfo(poiLI);
			cisList1.add(cis3);
			
			CarrierInformation ci1 = new CarrierInformation();
			ci1.setCarrierInfoNameEnum(CarrierInfoNameEnum.TRANSIT);
			ci1.setCarrierInfoSubordinate(cisList1);			
			
			ciList.add(ci);
			ciList.add(ci1);
			
			b = TtcCarrierInfoTrfr.encodeTtcCarrierInfoTrfr(TransitCarrierIndEnum.BI_DIRECTION, ciList);
			//System.out.println(Util.formatBytes(b));
			
			//byte[] b1 = {(byte)0x03, (byte)0xFA, (byte)0x05, (byte)0xFE, (byte)0x03, (byte)0x00, (byte)0x02, (byte)0x31};
			ttcCit = TtcCarrierInfoTrfr.decodeTtcCarrierInfoTrfr(b);
		} catch (InvalidInputException e) {
			e.printStackTrace();
			assertFalse(true);			
		}
				
		//System.out.println(ttcCit);
		assertEquals(TransitCarrierIndEnum.BI_DIRECTION, ttcCit.getTransitCarrierIndEnum());
	}
		
}
