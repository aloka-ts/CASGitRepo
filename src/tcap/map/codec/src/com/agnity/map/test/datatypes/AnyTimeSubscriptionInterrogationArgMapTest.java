package com.agnity.map.test.datatypes;

import static org.junit.Assert.*;

import java.util.LinkedList;

import org.junit.Test;

import com.agnity.map.asngenerated.AddressString;
import com.agnity.map.asngenerated.AnyTimeInterrogationArg;
import com.agnity.map.asngenerated.AnyTimeSubscriptionInterrogationArg;
import com.agnity.map.asngenerated.IMSI;
import com.agnity.map.asngenerated.ISDN_AddressString;
import com.agnity.map.asngenerated.RequestedSubscriptionInfo;
import com.agnity.map.asngenerated.SubscriberIdentity;
import com.agnity.map.asngenerated.TBCD_STRING;
import com.agnity.map.datatypes.AnyTimeInterrogationArgMap;
import com.agnity.map.datatypes.AnyTimeSubscriptionInterrogationArgMap;
import com.agnity.map.datatypes.BasicServiceCodeMap;
import com.agnity.map.datatypes.ISDNAddressStringMap;
import com.agnity.map.datatypes.ImsiDataType;
import com.agnity.map.datatypes.RequestedNodesMap;
import com.agnity.map.datatypes.RequestedSubscriptionInfoMap;
import com.agnity.map.datatypes.SsCodeMap;
import com.agnity.map.datatypes.SsForBSCodeMap;
import com.agnity.map.datatypes.SubscriberIdentityMap;
import com.agnity.map.enumdata.AddlRequestedCAMELSubscriptionInfoMapEnum;
import com.agnity.map.enumdata.BearerServiceCodeMapEnum;
import com.agnity.map.enumdata.ExtentionMapEnum;
import com.agnity.map.enumdata.NatureOfAddressMapEnum;
import com.agnity.map.enumdata.NatureOfAddressIndicatorMapEnum;
import com.agnity.map.enumdata.NumberPlanMapEnum;
import com.agnity.map.enumdata.RequestedCAMELSubscriptionInfoMapEnum;
import com.agnity.map.enumdata.RequestedNodesMapEnum;
import com.agnity.map.enumdata.SupplementaryServicesMapEnum;
import com.agnity.map.operations.MapOpCodes;
import com.agnity.map.operations.MapOperationsCoding;
import com.agnity.map.parser.ParseAsnToUserType;
import com.agnity.map.parser.ParseUserToAsnType;
import com.genband.tcap.parser.Util;

public class AnyTimeSubscriptionInterrogationArgMapTest {

	@Test
	public void testEncode() {
		Throwable e = null;
		byte[] actuals = null;
		
		try {
			
			
			// Subscriber Identity User object
			ImsiDataType imsi = new ImsiDataType();
			imsi.setMobileCountryCode("1227");
			imsi.setMobileNetworkCode("28");
			imsi.setMsin("0982321332");
			System.out.println("imsi  = "+imsi);
			SubscriberIdentityMap subId = new SubscriberIdentityMap(imsi);

			// Subscription Info User object
			RequestedNodesMap nodes = new RequestedNodesMap();
			nodes.enableRequestedNodeAtIndex(RequestedNodesMapEnum.SGSN);
			
			RequestedSubscriptionInfoMap reqInfo = new RequestedSubscriptionInfoMap();
			
			SsForBSCodeMap reqSsInfo = new SsForBSCodeMap(
					new SsCodeMap(SupplementaryServicesMapEnum.CALL_SESSION_RELATED_SS));
			
			reqSsInfo.setBasicServiceCode(new BasicServiceCodeMap(BearerServiceCodeMapEnum.ALLDATACIRCUITSYNCHRONOUS));
		
			reqInfo.setSsforBSCode(reqSsInfo);
			
			reqInfo.setReqCAMELSubsInfo(RequestedCAMELSubscriptionInfoMapEnum.D_CSI);
			reqInfo.setAddlReqCAMELSubsInfo(AddlRequestedCAMELSubscriptionInfoMapEnum.MT_SMS_CSI);

			// GSM ScfAddr User object
			ISDNAddressStringMap gsmScfAddr = new ISDNAddressStringMap();
			gsmScfAddr.setAddressDigits("3214793122");
			gsmScfAddr.setExtention(ExtentionMapEnum.NO_EXTENTION);
			gsmScfAddr.setNatureOfNumber(NatureOfAddressMapEnum.INTERNATIONAL_NUMBER);
			gsmScfAddr.setNumberPlan(NumberPlanMapEnum.ISDN_TELEPHONY_NUMBERING);
			
			AnyTimeSubscriptionInterrogationArgMap request = 
					new AnyTimeSubscriptionInterrogationArgMap(
					subId, reqInfo, gsmScfAddr);
			
			
			AnyTimeSubscriptionInterrogationArg atsiasn = ParseUserToAsnType.encodeUserToAtsiArg(request);
			
			LinkedList<String> opCode = new LinkedList<String>();
			opCode.add(MapOpCodes.MAP_ANY_TIME_SUBSCRIPTION_INTERROGATION);
	
			LinkedList<Object> operationObjs = new LinkedList<Object>();
			operationObjs.add(atsiasn);
			
			actuals = MapOperationsCoding.encodeOperations(
					operationObjs, opCode, true).getFirst();
		}
		catch(Exception ex) {
			ex.printStackTrace();
			e = ex;
		}	
		byte[] e1 = new byte[] {
				(byte)0x30, (byte)0x24, (byte)0xA0, (byte)0x0a, (byte)0x80, (byte)0x08, (byte)0x21, (byte)0x72, 
				(byte)0x82, 
				(byte)0x90, (byte)0x28, (byte)0x23, (byte)0x31, (byte)0x23, (byte)0xA1, (byte)0x0E, (byte)0xA1,
				(byte)0x06, (byte)0x04, (byte)0x01, (byte)0xb2, (byte)0x82, (byte)0x01, (byte)0x58, 
				(byte)0x83, (byte)0x01, (byte)0x08, (byte)0x87, (byte)0x01, (byte)0x00, (byte)0x82, (byte)0x06, 
				(byte)0x91, (byte)0x23, (byte)0x41, (byte)0x97, (byte)0x13, (byte)0x22
		};

		
		assertNull(e);
		assertNotNull(actuals);
		assertArrayEquals(e1, actuals);
	}
}
