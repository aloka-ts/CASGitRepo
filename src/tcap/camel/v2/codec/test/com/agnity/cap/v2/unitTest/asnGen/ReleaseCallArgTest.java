package com.agnity.cap.v2.unitTest.asnGen;

import java.util.Arrays;


import asnGenerated.v2.ReleaseCallArg;

import com.agnity.cap.v2.unitTest.expectedValues.ReleaseCallArgExpectedValues;

import junit.framework.TestCase;

public class ReleaseCallArgTest extends TestCase {
    
	 private ReleaseCallArgExpectedValues expectedValues;
	 private ReleaseCallArg relCallArg;
	
	@Override
	protected void setUp() throws Exception {
		expectedValues = ReleaseCallArgExpectedValues.getObject();
		expectedValues.setDefaultValues();
		relCallArg = (ReleaseCallArg) expectedValues.decode();
		super.setUp();
	}
	
	public void testGetValue() {
		assertTrue(Arrays.equals(expectedValues.value,relCallArg.getValue().getValue()));
	}

}
