package com.genband.isup.test;

import java.util.LinkedList;

import junit.framework.TestCase;

import org.apache.log4j.PropertyConfigurator;

import com.genband.isup.datatypes.BwCallIndicators;
import com.genband.isup.datatypes.CarrierIdentificationCode;
import com.genband.isup.datatypes.CarrierInfoSubordinate;
import com.genband.isup.datatypes.CarrierInformation;
import com.genband.isup.datatypes.OptBwCallIndicators;
import com.genband.isup.datatypes.POIChargeAreaInfo;
import com.genband.isup.datatypes.POILevelInfo;
import com.genband.isup.datatypes.TtcCarrierInfoTrfr;
import com.genband.isup.datatypes.TtcChargeAreaInfo;
import com.genband.isup.enumdata.CallDiversionIndEnum;
import com.genband.isup.enumdata.CalledPartyCatIndEnum;
import com.genband.isup.enumdata.CalledPartyStatusIndEnum;
import com.genband.isup.enumdata.CarrierInfoNameEnum;
import com.genband.isup.enumdata.CarrierInfoSubordinateEnum;
import com.genband.isup.enumdata.ChargeIndEnum;
import com.genband.isup.enumdata.EchoContDeviceIndEnum;
import com.genband.isup.enumdata.EndToEndInfoIndEnum;
import com.genband.isup.enumdata.EndToEndMethodIndEnum;
import com.genband.isup.enumdata.HoldingIndEnum;
import com.genband.isup.enumdata.ISDNAccessIndEnum;
import com.genband.isup.enumdata.ISDNUserPartIndEnum;
import com.genband.isup.enumdata.InbandInfoIndEnum;
import com.genband.isup.enumdata.InfoDiscriminationIndiEnum;
import com.genband.isup.enumdata.InterNwIndEnum;
import com.genband.isup.enumdata.MLPPUserIndEnum;
import com.genband.isup.enumdata.OutsidePOIGradeInfoEnum;
import com.genband.isup.enumdata.SCCPMethodIndENum;
import com.genband.isup.enumdata.SimpleSegmentationIndEnum;
import com.genband.isup.enumdata.TransitCarrierIndEnum;
import com.genband.isup.exceptions.InvalidInputException;

public class TestACM extends TestCase {

	protected void setUp() throws Exception {
		super.setUp();
		 PropertyConfigurator.configure("config" + System.getProperty("file.separator") + "log4j.properties");
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}
	
	public void testEncDecBwCallInd(){
		
		BwCallIndicators bw = null;
		byte[] b;
		try {
			b = BwCallIndicators.encodeBwCallInd(ChargeIndEnum.NO_CHARGE, CalledPartyStatusIndEnum.CONNECT_WHEN_FREE, CalledPartyCatIndEnum.NO_INDICATION, 
					EndToEndMethodIndEnum.PASS_ALONG_SCCP_METHOD, InterNwIndEnum.NO_INTER_NW_ENC, EndToEndInfoIndEnum.END_INFO_AVAILABLE, 
					ISDNUserPartIndEnum.ISDN_USER_PART_USED, HoldingIndEnum.HOLDING_NOT_REQUESTED, ISDNAccessIndEnum.NON_ISDN, 
					EchoContDeviceIndEnum.DEVICE_NOT_INCLUDED, SCCPMethodIndENum.CONNECTIONORIENTED_METHOD);
			//System.out.println(Util.formatBytes(b));
			bw = BwCallIndicators.decodeBwCallInd(b);
		} catch (InvalidInputException e) {
			assertFalse(true);
			e.printStackTrace();
		}
		
		assertEquals(ChargeIndEnum.NO_CHARGE, bw.getChargeIndEnum());
		assertEquals(CalledPartyStatusIndEnum.CONNECT_WHEN_FREE, bw.getCalledPartyStatusIndEnum());
		assertEquals(CalledPartyCatIndEnum.NO_INDICATION, bw.getCalledPartyCatIndEnum());		
		assertEquals(EndToEndMethodIndEnum.PASS_ALONG_SCCP_METHOD, bw.getEndToEndMethodIndEnum());
		assertEquals(InterNwIndEnum.NO_INTER_NW_ENC, bw.getInterNwIndEnum());
		assertEquals(EndToEndInfoIndEnum.END_INFO_AVAILABLE, bw.getEndToEndInfoIndEnum());
		assertEquals(ISDNUserPartIndEnum.ISDN_USER_PART_USED, bw.getIsdnUserPartIndEnum());
		assertEquals(HoldingIndEnum.HOLDING_NOT_REQUESTED, bw.getHoldingIndEnum());
		assertEquals(ISDNAccessIndEnum.NON_ISDN, bw.getIsdnAccessIndEnum());
		assertEquals(EchoContDeviceIndEnum.DEVICE_NOT_INCLUDED, bw.getEchoContDeviceIndEnum());		
		assertEquals(SCCPMethodIndENum.CONNECTIONORIENTED_METHOD, bw.getSccpMethodIndENum());
	}
	
	
	public void testEncDecOptBwCallInd(){
		
		OptBwCallIndicators bw = null;
		byte[] b;
		try {
			b = OptBwCallIndicators.encodeOptBwCallInd(InbandInfoIndEnum.INBAND_INFO, CallDiversionIndEnum.NO_INDICATION, 
					SimpleSegmentationIndEnum.NO_INFORMATION, MLPPUserIndEnum.NO_INDICATION);
			//System.out.println(Util.formatBytes(b));
			bw = OptBwCallIndicators.decodeOptBwCallInd(b);
		} catch (InvalidInputException e) {
			assertFalse(true);
			e.printStackTrace();
		}
		
		assertEquals(InbandInfoIndEnum.INBAND_INFO, bw.getInbandInfoIndEnum());
		assertEquals(CallDiversionIndEnum.NO_INDICATION, bw.getCallDiversionIndEnum());
		assertEquals(SimpleSegmentationIndEnum.NO_INFORMATION, bw.getSimpleSegmentationIndEnum());		
		assertEquals(MLPPUserIndEnum.NO_INDICATION, bw.getMlppUserIndEnum());
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
