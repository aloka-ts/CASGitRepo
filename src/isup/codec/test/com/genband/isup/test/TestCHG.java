package com.genband.isup.test;

import java.util.LinkedList;

import junit.framework.TestCase;

import org.apache.log4j.PropertyConfigurator;

import com.genband.isup.datatypes.AppRateTrfrChargingInfo;
import com.genband.isup.datatypes.ChargingInfo;
import com.genband.isup.datatypes.ChargingInfoCategory;
import com.genband.isup.datatypes.ChgRateTrfrChargingInfo;
import com.genband.isup.datatypes.FlexibleChgChargingInfo;
import com.genband.isup.datatypes.LMNCChargingInfo;
import com.genband.isup.datatypes.NttPhsChargingInfo;
import com.genband.isup.enumdata.ChargeCollectionMethodEnum;
import com.genband.isup.enumdata.ChargeRateIndEnum;
import com.genband.isup.enumdata.ChargedPartyTypeEnum;
import com.genband.isup.enumdata.ChargingInfoCatEnum;
import com.genband.isup.enumdata.ChargingInfoIndEnum;
import com.genband.isup.enumdata.ChgRateInfoCatEnum;
import com.genband.isup.enumdata.OperationClassEnum;
import com.genband.isup.enumdata.OperationTypeEnum;
import com.genband.isup.enumdata.SignalElementTypeEnum;
import com.genband.isup.enumdata.UnitRateIndEnum;
import com.genband.isup.exceptions.InvalidInputException;
import com.genband.isup.util.Util;

public class TestCHG extends TestCase {

	protected void setUp() throws Exception {
		super.setUp();
		 PropertyConfigurator.configure("config" + System.getProperty("file.separator") + "log4j.properties");
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}
	
	public void testEncDecChargingInfoCat(){
		
		ChargingInfoCategory cic = null;
		byte[] b;
		try {
			b = ChargingInfoCategory.encodeChargingInfoCat(ChargingInfoCatEnum.FLEXIBLE_CHARGING);
			cic = ChargingInfoCategory.decodeChargingInfoCat(b);
		} catch (InvalidInputException e) {
			e.printStackTrace();
			assertFalse(true);
		}
		
		assertEquals(ChargingInfoCatEnum.FLEXIBLE_CHARGING, cic.getChargingInfoCatEnum());
	}
	
	public void testEncDecChargingInfo(){
		
		byte[] b;
		ChargingInfo ci = new ChargingInfo();		
		try {
			LMNCChargingInfo lmnc = new LMNCChargingInfo();
			lmnc.setChargingData(123);
			lmnc.setChargingInfoIndEnum(ChargingInfoIndEnum.CHARGING_PULSE_INTERVAL_INFO_LMNC);
			ci.setLmncChargingInfo(lmnc);
			b = ChargingInfo.encodeChargingInfo(ci, ChargingInfoCatEnum.INT_AUTO_SUBS_PAYPHONE);
			//System.out.println(Util.formatBytes(b));
			ci = ChargingInfo.decodeChargingInfo(b, ChargingInfoCatEnum.INT_AUTO_SUBS_PAYPHONE);
		} catch (InvalidInputException e) {			
			e.printStackTrace();
			assertFalse(true);
		}		
		//System.out.println(ci);
		assertEquals(ChargingInfoIndEnum.CHARGING_PULSE_INTERVAL_INFO_LMNC, ci.getLmncChargingInfo().getChargingInfoIndEnum());
		assertEquals(123, ci.getLmncChargingInfo().getChargingData());
		
		
		ChargingInfo ci1 = new ChargingInfo();
		byte[] cb= {(byte)0x01, (byte)0x02};
		try {
			AppRateTrfrChargingInfo appRate = new AppRateTrfrChargingInfo();
			appRate.setSignalElementTypeEnum(SignalElementTypeEnum.INVOKE_ACTIVATION);
			appRate.setChargeRateIndEnum(ChargeRateIndEnum.NO_RATE_INFO);
			appRate.setInvokeId(12);
			appRate.setOperationClassEnum(OperationClassEnum.CLASS_1);
			appRate.setOperationTypeEnum(OperationTypeEnum.IMMEDIATE_CHARGE_COMMAND);
			appRate.setChargeCollectionMethodEnum(ChargeCollectionMethodEnum.BILL_SUBSCRIBER);
			appRate.setChargedPartyTypeEnum(ChargedPartyTypeEnum.CALLING_PARTY);
			appRate.setChargeRateInfo(cb);
			ci1.setAppRateTrfrChargingInfo(appRate);
			b = ChargingInfo.encodeChargingInfo(ci1, ChargingInfoCatEnum.APP_CHARGING_RATE_TRFR);
			//System.out.println(Util.formatBytes(b));
			ci1 = ChargingInfo.decodeChargingInfo(b, ChargingInfoCatEnum.APP_CHARGING_RATE_TRFR);
		} catch (InvalidInputException e) {			
			e.printStackTrace();
			assertFalse(true);
		}		
		//System.out.println(ci1);
		assertEquals(SignalElementTypeEnum.INVOKE_ACTIVATION, ci1.getAppRateTrfrChargingInfo().getSignalElementTypeEnum());
		assertEquals(ChargeRateIndEnum.NO_RATE_INFO, ci1.getAppRateTrfrChargingInfo().getChargeRateIndEnum());
		assertEquals(12, ci1.getAppRateTrfrChargingInfo().getInvokeId());
		assertEquals(OperationClassEnum.CLASS_1, ci1.getAppRateTrfrChargingInfo().getOperationClassEnum());
		assertEquals(OperationTypeEnum.IMMEDIATE_CHARGE_COMMAND, ci1.getAppRateTrfrChargingInfo().getOperationTypeEnum());
		assertEquals(ChargeCollectionMethodEnum.BILL_SUBSCRIBER, ci1.getAppRateTrfrChargingInfo().getChargeCollectionMethodEnum());
		assertEquals(ChargedPartyTypeEnum.CALLING_PARTY, ci1.getAppRateTrfrChargingInfo().getChargedPartyTypeEnum());
		assertEquals(Util.formatBytes(cb), Util.formatBytes(ci1.getAppRateTrfrChargingInfo().getChargeRateInfo()));
		
		
		ChargingInfo ci2 = new ChargingInfo();		
		try {
			NttPhsChargingInfo nttphs = new NttPhsChargingInfo();
			nttphs.setSignalElementTypeEnum(SignalElementTypeEnum.SUCCESS);
			nttphs.setCalledAreaInformation("1234");
			ci2.setNttPhsChargingInfo(nttphs);
			b = ChargingInfo.encodeChargingInfo(ci2, ChargingInfoCatEnum.NTT_NW_CONN_PHS);
			//System.out.println(Util.formatBytes(b));
			ci2 = ChargingInfo.decodeChargingInfo(b, ChargingInfoCatEnum.NTT_NW_CONN_PHS);
		} catch (InvalidInputException e) {			
			e.printStackTrace();
			assertFalse(true);
		}		
		//System.out.println(ci2);
		assertEquals(SignalElementTypeEnum.SUCCESS, ci2.getNttPhsChargingInfo().getSignalElementTypeEnum());
		assertEquals("1234", ci2.getNttPhsChargingInfo().getCalledAreaInformation());
		
		
		ChargingInfo ci3 = new ChargingInfo();		
		try {
			ChgRateTrfrChargingInfo chgRate = new ChgRateTrfrChargingInfo();
			LinkedList<Float> cp = new LinkedList<Float>();
			cp.add((float) 14);
			cp.add((float) 12);
			cp.add((float) 235);
			chgRate.setChgRateInfoCatEnum(ChgRateInfoCatEnum.ORDINARY_CALLING_SUBS);
			chgRate.setUnitRateIndEnum(UnitRateIndEnum.YEN_10);
			chgRate.setInitialCallRate(15);
			chgRate.setChargingPeriods(cp);
			ci3.setChgRateTrfrChargingInfo(chgRate);
			b = ChargingInfo.encodeChargingInfo(ci3, ChargingInfoCatEnum.CHARGING_RATE_TRFR);
			//System.out.println(Util.formatBytes(b));
			ci3 = ChargingInfo.decodeChargingInfo(b, ChargingInfoCatEnum.CHARGING_RATE_TRFR);
		} catch (InvalidInputException e) {			
			e.printStackTrace();
			assertFalse(true);
		}		
		//System.out.println(ci3);
		assertEquals(ChgRateInfoCatEnum.ORDINARY_CALLING_SUBS, ci3.getChgRateTrfrChargingInfo().getChgRateInfoCatEnum());
		assertEquals(UnitRateIndEnum.YEN_10, ci3.getChgRateTrfrChargingInfo().getUnitRateIndEnum());
		assertEquals(15, ci3.getChgRateTrfrChargingInfo().getInitialCallRate());
		assertEquals(14, ci3.getChgRateTrfrChargingInfo().getChargingPeriods().get(0).intValue());
		assertEquals(12, ci3.getChgRateTrfrChargingInfo().getChargingPeriods().get(1).intValue());
		assertEquals(235, ci3.getChgRateTrfrChargingInfo().getChargingPeriods().get(2).intValue());
		
		
		ChargingInfo ci4 = new ChargingInfo();
		byte[] cb1= {(byte)0x02, (byte)0x03};
		try {
			FlexibleChgChargingInfo flex = new FlexibleChgChargingInfo();
			flex.setSignalElementTypeEnum(SignalElementTypeEnum.INVOKE_ACTIVATION);
			flex.setChargeRateIndEnum(ChargeRateIndEnum.SECONDS_10_YEN);
			flex.setActivationId(12);
			flex.setOperationClassEnum(OperationClassEnum.CLASS_4);
			flex.setOperationTypeEnum(OperationTypeEnum.IMMEDIATE_CHARGE_COMMAND);
			flex.setChargeCollectionMethodEnum(ChargeCollectionMethodEnum.BILL_SUBSCRIBER);
			flex.setChargedPartyTypeEnum(ChargedPartyTypeEnum.CALLING_PARTY);
			flex.setChargeRateInfo(cb1);
			ci4.setFlexibleChgChargingInfo(flex);
			b = ChargingInfo.encodeChargingInfo(ci4, ChargingInfoCatEnum.FLEXIBLE_CHARGING);
			//System.out.println(Util.formatBytes(b));
			ci4 = ChargingInfo.decodeChargingInfo(b, ChargingInfoCatEnum.FLEXIBLE_CHARGING);
		} catch (InvalidInputException e) {			
			e.printStackTrace();
			assertFalse(true);
		}		
		//System.out.println(ci4);
		assertEquals(SignalElementTypeEnum.INVOKE_ACTIVATION, ci4.getFlexibleChgChargingInfo().getSignalElementTypeEnum());
		assertEquals(ChargeRateIndEnum.SECONDS_10_YEN, ci4.getFlexibleChgChargingInfo().getChargeRateIndEnum());
		assertEquals(12, ci4.getFlexibleChgChargingInfo().getActivationId());
		assertEquals(OperationClassEnum.CLASS_4, ci4.getFlexibleChgChargingInfo().getOperationClassEnum());
		assertEquals(OperationTypeEnum.IMMEDIATE_CHARGE_COMMAND, ci4.getFlexibleChgChargingInfo().getOperationTypeEnum());
		assertEquals(ChargeCollectionMethodEnum.BILL_SUBSCRIBER, ci4.getFlexibleChgChargingInfo().getChargeCollectionMethodEnum());
		assertEquals(ChargedPartyTypeEnum.CALLING_PARTY, ci4.getFlexibleChgChargingInfo().getChargedPartyTypeEnum());
		assertEquals(Util.formatBytes(cb1), Util.formatBytes(ci4.getFlexibleChgChargingInfo().getChargeRateInfo()));
	}
}
