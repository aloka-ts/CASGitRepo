package com.agnity.cap.v2.unitTest.asnGen;

import java.io.ByteArrayInputStream;
import java.util.Arrays;

import org.bn.CoderFactory;
import org.bn.IDecoder;


import asnGenerated.v2.InitialDPArg;

import com.agnity.cap.v2.unitTest.expectedValues.InitialDPArgExpectedValues;

import junit.framework.TestCase;

public class InitialDPArgTest extends TestCase {
	InitialDPArg ida;
	InitialDPArgExpectedValues expectedValues;
	
	@Override
	protected void setUp() throws Exception {
		expectedValues = InitialDPArgExpectedValues.getObject();
		expectedValues.setDefaultValues();
		ida = (InitialDPArg) expectedValues.decode();
		//this.decoded(expectedValues.initialDPArg);
		super.setUp();
	}

	public void testGetServiceKey() {
		assertTrue(expectedValues.serviceKey==ida.getServiceKey().getValue().getValue());
	}

	public void testGetCalledPartyNumber() {
		if(expectedValues.calledPartyNumber!=null){
			assertTrue(Arrays.equals(expectedValues.calledPartyNumber, ida.getCalledPartyNumber().getValue()));
		}else{
			assertNull(ida.getCalledPartyNumber());
		}
	}

	public void testIsCalledPartyNumberPresent() {
		assertEquals(expectedValues.calledPartyNumber!=null, ida.isCalledPartyNumberPresent());
	}

	public void testGetCallingPartyNumber() {
		if(expectedValues.callingPartyNumber!=null){
			assertTrue(Arrays.equals(expectedValues.callingPartyNumber, ida.getCallingPartyNumber().getValue()));
		}else{
			assertNull(ida.getCallingPartyNumber());
		}
		
	}

	public void testIsCallingPartyNumberPresent() {
		assertEquals(expectedValues.callingPartyNumber!=null, ida.isCallingPartyNumberPresent());
	}

	public void testGetCallingPartysCategory() {
		if(expectedValues.callingPartysCategory!=null){
			assertTrue(Arrays.equals(expectedValues.callingPartysCategory, ida.getCallingPartysCategory().getValue()));
		}else{
			assertNull(ida.getCallingPartysCategory());
		}
		
	}

	public void testIsCallingPartysCategoryPresent() {
		assertEquals(expectedValues.callingPartysCategory!=null, ida.isCallingPartysCategoryPresent());
	}

	public void testGetIPSSPCapabilities() {
		if(expectedValues.iPSSPCapabilities!=null){
			assertTrue(Arrays.equals(expectedValues.iPSSPCapabilities, ida.getIPSSPCapabilities().getValue()));
		}else{
			assertNull(ida.getIPSSPCapabilities());
		}
		
	}

	public void testIsIPSSPCapabilitiesPresent() {
		assertEquals(expectedValues.iPSSPCapabilities!=null, ida.isIPSSPCapabilitiesPresent());
	}


	public void testGetLocationNumber() {
		if(expectedValues.locationNumber!=null){
			assertTrue(Arrays.equals(expectedValues.locationNumber, ida.getLocationNumber().getValue()));
		}else{
			assertNull(ida.getLocationNumber());
		}
		
	}

	public void testIsLocationNumberPresent() {
		assertEquals(expectedValues.locationNumber!=null, ida.isLocationNumberPresent());
	}

	public void testGetOriginalCalledPartyID() {
		if(expectedValues.originalCalledPartyID!=null){
			assertTrue(Arrays.equals(expectedValues.originalCalledPartyID, ida.getOriginalCalledPartyID().getValue()));			
		}else{
			assertNull(ida.getOriginalCalledPartyID());
		}
	}

	public void testIsOriginalCalledPartyIDPresent() {
		assertEquals(expectedValues.originalCalledPartyID!=null, ida.isOriginalCalledPartyIDPresent());
	}

	public void testGetExtensions() {
		if(expectedValues.extensions!=null){
			assertNotNull(ida.getExtensions());
		}else{
			assertNull(ida.getExtensions());
		}
	}

	public void testIsExtensionsPresent() {
		assertEquals(expectedValues.extensions!=null, ida.isExtensionsPresent());
	}

	public void testGetHighLayerCompatibility() {
		if(expectedValues.highLayerCompatibility!=null){
			assertTrue(Arrays.equals(expectedValues.highLayerCompatibility, ida.getHighLayerCompatibility().getValue()));
		}else{
			assertNull(ida.getHighLayerCompatibility());
		}
		
	}

	public void testIsHighLayerCompatibilityPresent() {
		assertEquals(expectedValues.highLayerCompatibility!=null, ida.isHighLayerCompatibilityPresent());
	}

	public void testGetAdditionalCallingPartyNumber() {
		if(expectedValues.additionalCallingPartyNumber!=null){
			assertTrue(Arrays.equals(expectedValues.additionalCallingPartyNumber, ida.getAdditionalCallingPartyNumber().getValue().getValue()));
		}else{
			assertNull(ida.getAdditionalCallingPartyNumber());
		}
	}

	public void testIsAdditionalCallingPartyNumberPresent() {
		assertEquals(expectedValues.additionalCallingPartyNumber!=null, ida.isAdditionalCallingPartyNumberPresent());
	}

	public void testGetBearerCapability() {
		if(expectedValues.bearerCapability!=null){
			assertTrue(Arrays.equals(expectedValues.bearerCapability, ida.getBearerCapability().getBearerCap()));
		}else{
			assertNull( ida.getBearerCapability());
		}
	}

	public void testIsBearerCapabilityPresent() {
		assertEquals(expectedValues.bearerCapability!=null, ida.isBearerCapabilityPresent());
	}
	
	public void testGetEventTypeBCSM() {
		assertEquals(expectedValues.eventTypeBCSM, ida.getEventTypeBCSM().getValue());
	}

	public void testIsEventTypeBCSMPresent() {
		assertEquals(expectedValues.eventTypeBCSM!=null, ida.isEventTypeBCSMPresent());
	}

	public void testGetRedirectingPartyID() {
		if(expectedValues.redirectingPartyID!=null){
			assertTrue(Arrays.equals(expectedValues.redirectingPartyID, ida.getRedirectingPartyID().getValue()));
		}else{
			assertNull(ida.getRedirectingPartyID());
		}
		
	}

	public void testIsRedirectingPartyIDPresent() {
		assertEquals(expectedValues.redirectingPartyID!=null, ida.isRedirectingPartyIDPresent());
	}

	public void testGetRedirectionInformation() {
		if(expectedValues.redirectionInformation!=null){
			assertTrue(Arrays.equals(expectedValues.redirectionInformation, ida.getRedirectionInformation().getValue()));
		}else{
			assertNull(ida.getRedirectionInformation());
		}
		
	}

	public void testIsRedirectionInformationPresent() {
		assertEquals(expectedValues.redirectionInformation!=null, ida.isRedirectionInformationPresent());
	}
	
	public void testGetIMSI() {
		if(expectedValues.iMSI!=null){
			assertTrue(Arrays.equals(expectedValues.iMSI, ida.getIMSI().getValue().getValue()));
		}else{
		    assertNull(ida.getIMSI());	
		}
	}

	public void testIsIMSIPresent() {
		assertEquals(expectedValues.iMSI!=null, ida.isIMSIPresent());
	}

	public void testGetSubscriberState() {
		if(expectedValues.subscriberState!=null){
			assertNotNull(ida.getSubscriberState());
		}else{
			assertNull(ida.getSubscriberState());
		}
	}

	public void testIsSubscriberStatePresent() {
		assertEquals(expectedValues.subscriberState!=null, ida.isSubscriberStatePresent());
	}

	public void testGetLocationInformation() {
		if(expectedValues.locationInformation!=null){
			assertNotNull(ida.getLocationInformation());
		}else{
			assertNull(ida.getLocationInformation());
		}
	}

	public void testIsLocationInformationPresent() {
		assertEquals(expectedValues.locationInformation!=null, ida.isLocationInformationPresent());
	}
	
	public void testGetExt_basicServiceCode() {
		if(expectedValues.ext_basicServiceCode!=null){
			assertNotNull(ida.getExt_basicServiceCode());
		}else{
			assertNull(ida.getExt_basicServiceCode());
		}
	}

	public void testIsExt_basicServiceCodePresent() {
		assertEquals(expectedValues.ext_basicServiceCode!=null, ida.isExt_basicServiceCodePresent());
	}

	public void testGetCallReferenceNumber() {
		if(expectedValues.callReferenceNumber!=null){
			assertTrue(Arrays.equals(expectedValues.callReferenceNumber, ida.getCallReferenceNumber().getValue()));
		}else{
			assertNull(ida.getCallReferenceNumber());
		}
		
	}

	public void testIsCallReferenceNumberPresent() {
		assertEquals(expectedValues.callReferenceNumber!=null, ida.isCallReferenceNumberPresent());
	}
	
	public void testGetMscAddress() {
		if(expectedValues.mscAddress!=null){
			assertTrue(Arrays.equals(expectedValues.mscAddress, ida.getMscAddress().getValue().getValue()));
		}else{
			assertNull(ida.getMscAddress());
		}  
	}

	public void testIsMscAddressPresent() {
		assertEquals(expectedValues.mscAddress!=null, ida.isMscAddressPresent());
	}

	public void testGetCalledPartyBCDNumber() {
		if(expectedValues.calledPartyBCDNumber!=null){
			assertTrue(Arrays.equals(expectedValues.calledPartyBCDNumber, ida.getCalledPartyBCDNumber().getValue()));
		}else{
			assertNull( ida.getCalledPartyBCDNumber());
		}
		
	}

	public void testIsCalledPartyBCDNumberPresent() {
		assertEquals(expectedValues.calledPartyBCDNumber!=null, ida.isCalledPartyBCDNumberPresent());
	}

	public void testGetTimeAndTimezone() {
		if(expectedValues.timeAndTimezone!=null){
			assertTrue(Arrays.equals(expectedValues.timeAndTimezone, ida.getTimeAndTimezone().getValue()));
		}else{
			assertNull(ida.getTimeAndTimezone());
		}	
	}

	public void testIsTimeAndTimezonePresent() {
		assertEquals(expectedValues.timeAndTimezone!=null, ida.isTimeAndTimezonePresent());
	}
	
	public void testGetInitialDPArgExtension() {
		if(expectedValues.initialDPArgExtension!=null){
			assertNotNull(ida.getInitialDPArgExtension());
		}else{
			assertNull(ida.getInitialDPArgExtension());
		}
	}

	public void testIsInitialDPArgExtensionPresent() {
		assertEquals(expectedValues.initialDPArgExtension!=null, ida.isInitialDPArgExtensionPresent());
	}

}
