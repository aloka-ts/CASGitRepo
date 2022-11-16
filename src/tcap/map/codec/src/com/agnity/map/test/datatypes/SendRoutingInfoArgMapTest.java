package com.agnity.map.test.datatypes;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.LinkedList;

import org.junit.Test;

import com.agnity.map.asngenerated.SendRoutingInfoArg;
import com.agnity.map.datatypes.AlertingPatternDataType;
import com.agnity.map.datatypes.ExtBasicServiceCodeMap;
import com.agnity.map.datatypes.ExtBearerServiceCodeMap;
import com.agnity.map.datatypes.ExtTeleserviceCodeMap;
import com.agnity.map.datatypes.ISDNAddressStringMap;
import com.agnity.map.datatypes.SendRoutingInfoArgMap;
import com.agnity.map.datatypes.SuppressMtssMap;
import com.agnity.map.enumdata.BearerServiceCodeMapEnum;
import com.agnity.map.enumdata.ExtentionMapEnum;
import com.agnity.map.enumdata.ForwardingReasonMapEnum;
import com.agnity.map.enumdata.InterrogationTypeEnumMap;
import com.agnity.map.enumdata.IstSupportIndicatorMapEnum;
import com.agnity.map.enumdata.NatureOfAddressMapEnum;
import com.agnity.map.enumdata.NumberPlanMapEnum;
import com.agnity.map.enumdata.SuppressMtssMapEnum;
import com.agnity.map.enumdata.TeleServiceCodeMapEnum;
import com.agnity.map.enumdata.TypeOfAlertCatgEnum;
import com.agnity.map.enumdata.TypeOfAlertLevelEnum;
import com.agnity.map.enumdata.TypeOfPatrnEnum;
import com.agnity.map.enumdata.UnusedBitsMapEnum;
import com.agnity.map.operations.MapOpCodes;
import com.agnity.map.operations.MapOperationsCoding;
import com.agnity.map.parser.ParseUserToAsnType;
import com.agnity.map.util.Util;

public class SendRoutingInfoArgMapTest {

	@Test
	public void testEncode() {
		byte[] expected = new byte[] {
																							(byte)0x30,
		(byte)0x36, (byte)0x80, (byte)0x06, (byte)0x91, (byte)0x89, (byte)0x39, (byte)0x28, (byte)0x13,
		(byte)0x32, (byte)0x82, (byte)0x01, (byte)0x03, (byte)0x83, (byte)0x01, (byte)0x00, (byte)0x85, 
		(byte)0x01, (byte)0x7f, (byte)0x86, (byte)0x05, (byte)0xa1, (byte)0x81, (byte)0x32, (byte)0x22,
		(byte)0xf2, (byte)0x88, (byte)0x01, (byte)0x02, (byte)0xa9, (byte)0x03, (byte)0x82, (byte)0x01, 
		(byte)0x18, (byte)0x8e, (byte)0x01, (byte)0x01, (byte)0x90, (byte)0x01, (byte)0x02, (byte)0x92, 
		(byte)0x01, (byte)0x00, (byte)0xb9, (byte)0x03, (byte)0x83, (byte)0x01, (byte)0x60, (byte)0x9b,
		(byte)0x03, (byte)0x00, (byte)0x40, (byte)0x00, (byte)0x9d, (byte)0x01, (byte)0x03 
		};

		SendRoutingInfoArg asnarg = null;
		
		ISDNAddressStringMap msisdn = new ISDNAddressStringMap();
		msisdn.setNumberPlan(NumberPlanMapEnum.ISDN_TELEPHONY_NUMBERING);
		msisdn.setExtention(ExtentionMapEnum.NO_EXTENTION);
		msisdn.setNatureOfNumber(NatureOfAddressMapEnum.INTERNATIONAL_NUMBER);
		msisdn.setAddressDigits("9893823123");
		
		// Get the gsmScfAddress
		ISDNAddressStringMap gmscOrGsmScfAddress = new ISDNAddressStringMap();
		gmscOrGsmScfAddress.setExtention(ExtentionMapEnum.NO_EXTENTION);
		gmscOrGsmScfAddress.setNatureOfNumber(NatureOfAddressMapEnum.NATIONAL_SIGNIFICANT_NUMBER);
		gmscOrGsmScfAddress.setNumberPlan(NumberPlanMapEnum.ISDN_TELEPHONY_NUMBERING);
		gmscOrGsmScfAddress.setAddressDigits("1823222");
		
		SendRoutingInfoArgMap sriarguser = new SendRoutingInfoArgMap(msisdn, 
				InterrogationTypeEnumMap.BASIC_CALL, gmscOrGsmScfAddress);
		
		AlertingPatternDataType alertingType = new AlertingPatternDataType();
		alertingType.setTypeOfAlertCatgEnum(TypeOfAlertCatgEnum.CATEGORY3);
		alertingType.setTypeOfAlertLevelEnum(TypeOfAlertLevelEnum.ALERT_LEVEL1);
		alertingType.setTypeOfPatrnEnum(TypeOfPatrnEnum.LEVEL);
		sriarguser.setAlertingPattern(alertingType);
		
		sriarguser.setNumberOfForwarding(new Integer(3));
		
		sriarguser.setOrCapability(0x7f);
		
		sriarguser.setForwardingReason(ForwardingReasonMapEnum.NO_REPLY);
		ExtBearerServiceCodeMap bearer = new ExtBearerServiceCodeMap();
		bearer.setBearerServicecode(BearerServiceCodeMapEnum.ALLDATACDS_SERVICES);
		ExtBasicServiceCodeMap bscode = new ExtBasicServiceCodeMap(bearer);
		sriarguser.setBasicServiceGroup(bscode);
		
		ExtTeleserviceCodeMap tele = new ExtTeleserviceCodeMap();
		tele.setTeleserviceCode(TeleServiceCodeMapEnum.ALLFACSIMILETRANSMISSIONSERVICES);
		
		sriarguser.setBasicServiceGroup2(new ExtBasicServiceCodeMap(tele));
		
		sriarguser.setCallingPriority(new Integer(3));
		
		sriarguser.setSupportedCCBSPhase(new Integer(2));
		
		SuppressMtssMap mtssMap = new SuppressMtssMap();
		mtssMap.setUnusedBits(UnusedBitsMapEnum.LAST_6_BIT_UNUSED);
		mtssMap.setSuppressMtss(SuppressMtssMapEnum.SUPPRESS_CCBS, true);
		sriarguser.setSuppressMTSS(mtssMap);
		
		sriarguser.setIstSupportIndicator(IstSupportIndicatorMapEnum.BASIC_IST_SUPPORTED);
		
		Throwable e = null;
		
		byte[] actualEncodedData = null;
		try{
			asnarg = ParseUserToAsnType.encodeUserToSriArg(sriarguser);
			
			LinkedList<String> opCode = new LinkedList<String>();
			opCode.add(MapOpCodes.MAP_SEND_ROUTING_INFO);
	
			LinkedList<Object> operationObjs = new LinkedList<Object>();
			operationObjs.add(asnarg);
	
			actualEncodedData = MapOperationsCoding.encodeOperations(
					operationObjs, opCode, true).getFirst();
		}
		catch(Exception ex){
			System.out.println("ex1 = "+ex);
			e = ex;
			ex.printStackTrace();
		}

		assertNull(e);
		assertNotNull(actualEncodedData);
		System.out.println("encoded  data = "+Util.formatBytes(actualEncodedData));
		System.out.println("expected data = "+Util.formatBytes(expected));
		assertNull(e);
		assertArrayEquals(expected, actualEncodedData);
	}

}
