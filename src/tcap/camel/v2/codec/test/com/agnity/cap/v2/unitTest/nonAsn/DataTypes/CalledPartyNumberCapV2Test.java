package com.agnity.cap.v2.unitTest.nonAsn.DataTypes;

import java.lang.reflect.Array;
import java.util.Arrays;

import asnGenerated.v2.CalledPartyNumber;
import asnGenerated.v2.InitialDPArg;
import com.agnity.cap.v2.datatypes.CalledPartyNumberCapV2;
import com.agnity.cap.v2.exceptions.InvalidInputException;
import com.agnity.cap.v2.unitTest.expectedValues.InitialDPArgExpectedValues;

import junit.framework.TestCase;

public class CalledPartyNumberCapV2Test extends TestCase {

/*	protected void setUp() throws Exception {
		super.setUp();
	}

	public void testGetOddEvenIndicator() {
		fail("Not yet implemented");
	}

	public void testGetNatureOfAddressIndicator() {
		fail("Not yet implemented");
	}

	public void testGetInnIndicator() {
		fail("Not yet implemented");
	}

	public void testGetNumberingPlanIndicator() {
		fail("Not yet implemented");
	}

	public void testGetCalledPartyNumber() {
		fail("Not yet implemented");
	}

	public void testDecode() {
		fail("Not yet implemented");
	}

	public void testEncode() {
		fail("Not yet implemented");
	}*/
	
	public void testDecodeEncode() throws InvalidInputException {
		InitialDPArgExpectedValues expt =  InitialDPArgExpectedValues.getObject();
		expt.setDefaultValues();
		InitialDPArg ida = (InitialDPArg) expt.decode();
		CalledPartyNumber cpn = new CalledPartyNumber();
		cpn.setValue(new byte[]{(byte)0x84,(byte)0x10,(byte)0x19,(byte)0x41,(byte)0x23,(byte)0x02,(byte)0x00,(byte)0x57,(byte)0x0f});
		ida.setCalledPartyNumber(cpn);
		byte[] bytesD = ida.getCalledPartyNumber().getValue(); 
		CalledPartyNumberCapV2 obj = CalledPartyNumberCapV2.decode(bytesD);
		byte[] bytesI = CalledPartyNumberCapV2.encode(obj);
		//System.out.println("result::"+Arrays.equals(bytesI,bytesD ));
	    assertTrue(Arrays.equals(bytesI,bytesD));
		/*CalledPartyNumberCapV2 d= CalledPartyNumberCapV2.decode(bytesD);
		System.out.println(d.getCalledPartyNumber()+"[]"+obj.getCalledPartyNumber());
		System.out.println(d.getInnIndicator()+"[]"+obj.getInnIndicator());
		System.out.println(d.getNatureOfAddressIndicator()+"[]"+obj.getNatureOfAddressIndicator());
		System.out.println(d.getNumberingPlanIndicator()+"[]"+obj.getNumberingPlanIndicator());
		System.out.println(d.getOddEvenIndicator()+"[]"+obj.getOddEvenIndicator());
		System.out.println(bytesD.length+"[]"+ bytesI.length);
		
		for(int i=0;i<9;i++){
			System.out.println(i+"::"+Arrays.equals(new byte[]{bytesD[i]}, new byte[]{bytesI[i]}));
			System.out.println(bytesD[i]+"[]"+bytesI[i]);
		}*/
	}

}
