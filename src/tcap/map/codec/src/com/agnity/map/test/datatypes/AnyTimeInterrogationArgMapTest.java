package com.agnity.map.test.datatypes;

import static org.junit.Assert.*;

import java.io.ByteArrayOutputStream;
import java.util.LinkedList;

import org.bn.CoderFactory;
import org.bn.IEncoder;
import org.bn.types.BitString;
import org.junit.Test;

import com.agnity.map.asngenerated.*;
import com.agnity.map.datatypes.AnyTimeInterrogationArgMap;
import com.agnity.map.datatypes.ISDNAddressStringMap;
import com.agnity.map.datatypes.ImsiDataType;
import com.agnity.map.datatypes.RequestedInfoMap;
import com.agnity.map.datatypes.RequestedNodesMap;
import com.agnity.map.datatypes.SubscriberIdentityMap;
import com.agnity.map.enumdata.DomainTypeMapEnum;
import com.agnity.map.enumdata.ExtentionMapEnum;
import com.agnity.map.enumdata.NatureOfAddressMapEnum;
import com.agnity.map.enumdata.NatureOfAddressIndicatorMapEnum;
import com.agnity.map.enumdata.NumberPlanMapEnum;
import com.agnity.map.enumdata.RequestedNodesMapEnum;
import com.agnity.map.operations.MapOpCodes;
import com.agnity.map.operations.MapOperationsCoding;
import com.agnity.map.util.CompatUtil;
import com.agnity.map.util.Util;

public class AnyTimeInterrogationArgMapTest {

	@Test
	public void testEncoding() {
		
		Throwable e = null;
		byte[] actualEncodedData = null;
		try{
			ImsiDataType imsi = new ImsiDataType();
			imsi.setMobileCountryCode("320");
			imsi.setMobileNetworkCode("331");
			imsi.setMsin("2323231111");
			SubscriberIdentityMap subId = new SubscriberIdentityMap(imsi);
			
			RequestedInfoMap reqInfo = new RequestedInfoMap();
			
			RequestedNodesMap reqNodes = new RequestedNodesMap();
			reqNodes.enableRequestedNodeAtIndex(RequestedNodesMapEnum.MME);
			
			reqInfo.setDomainType(DomainTypeMapEnum.PS_DOMAIN);
			reqInfo.setRequestedNodes(reqNodes);
			
			ISDNAddressStringMap gsmScfAddr = new ISDNAddressStringMap();
			gsmScfAddr.setAddressDigits("2123344556");
			gsmScfAddr.setExtention(ExtentionMapEnum.NO_EXTENTION);
			gsmScfAddr.setNatureOfNumber(NatureOfAddressMapEnum.INTERNATIONAL_NUMBER);
			gsmScfAddr.setNumberPlan(NumberPlanMapEnum.PRIVATE_NUMBERING_PLAN);
			
			AnyTimeInterrogationArgMap request = new AnyTimeInterrogationArgMap(
					subId, reqInfo, gsmScfAddr);
			
			
			
			
			AnyTimeInterrogationArg atiArgAsn = new AnyTimeInterrogationArg();
	
			DomainType domain = new DomainType();
			if(request.getRequestedInfo().getDomainType() == DomainTypeMapEnum.CS_DOMAIN) {
				domain.setValue(DomainType.EnumType.cs_Domain);
			}
			else if (request.getRequestedInfo().getDomainType() == DomainTypeMapEnum.PS_DOMAIN) {
				domain.setValue(DomainType.EnumType.ps_Domain);
			}
			
			RequestedNodes reqNdsAsn = new RequestedNodes();
			reqNdsAsn.setValue(new BitString(RequestedNodesMap.encode(request.getRequestedInfo().getRequestedNodes())));
			
			// Get the reqInfo 
			RequestedInfo reqInfoAsn = new RequestedInfo();
			reqInfoAsn.setRequestedDomain(domain);
			reqInfoAsn.setRequestedNodes(reqNdsAsn);
			
			// Get the subscriber Identity
			
			LinkedList<byte[]> encodeList = null;
		
		
			SubscriberIdentity subIdAsn = new SubscriberIdentity();
			IMSI imsiAsn = new IMSI();
			imsiAsn.setValue(new TBCD_STRING(request.getSubscriberIdentity().getImsi().encode()));
			subIdAsn.selectImsi(imsiAsn);
			
			// Get the gsmScfAddress
			ISDN_AddressString gsmScfAddrAsn = new ISDN_AddressString();
			gsmScfAddrAsn.setValue(new AddressString(request.getGsmScfAddress().encode()));
			
			// Populate the request argument to be sent to network end point
			atiArgAsn.setRequestedInfo(reqInfoAsn);
			atiArgAsn.setSubscriberIdentity(subIdAsn);
			atiArgAsn.setGsmSCF_Address(gsmScfAddrAsn);
			/*
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			
			System.out.println("Encoding requested info");/////////////////
			IEncoder<RequestedInfo> encoder = CoderFactory.getInstance().newEncoder("BER");
            encoder.encode((RequestedInfo)reqInfoAsn, outputStream);
            System.out.println("Requested Info null object byte seq = "+ Util.formatBytes(outputStream.toByteArray()));
            ////////////////////////
			*/
			
			LinkedList<String> opCode = new LinkedList<String>();
			opCode.add(MapOpCodes.MAP_ANY_TIME_INTERROGATION);
	
			LinkedList<Object> operationObjs = new LinkedList<Object>();
			operationObjs.add(atiArgAsn);
	
			actualEncodedData = MapOperationsCoding.encodeOperations(
					operationObjs, opCode, true).getFirst();
		}
		catch(Exception ex){
			System.out.println("exception "+ex);
			e = ex;
		}
		byte[] expected = new byte[] { 
																								(byte)0x30, 
			(byte)0x2a, (byte)0xA0, (byte)0x0A, (byte)0x80, (byte)0x08, (byte)0x23, (byte)0x10, (byte)0x33,
			(byte)0x32, (byte)0x32, (byte)0x32, (byte)0x11, (byte)0x11, (byte)0xa1, (byte)0x14, (byte)0x80, 
			(byte)0x00, (byte)0x81, (byte)0x00, (byte)0x83, (byte)0x00, (byte)0x84, (byte)0x01, (byte)0x01, 
			(byte)0x86, (byte)0x00, (byte)0x85, (byte)0x00, (byte)0x87, (byte)0x00, (byte)0x88, (byte)0x00,
			(byte)0x89, (byte)0x01, (byte)0x01, (byte)0x83, (byte)0x06, (byte)0x99, (byte)0x12, (byte)0x32,
			(byte)0x43, (byte)0x54, (byte)0x65
		};
		
		System.out.println("encoded  data = "+Util.formatBytes(actualEncodedData));
		System.out.println("expected data = "+Util.formatBytes(expected));

		assertNotNull(actualEncodedData);
		assertNull(e);
		assertArrayEquals(expected, actualEncodedData);
	}

}
