package com.agnity.win.test;

import java.util.ArrayList;

import junit.framework.TestCase;

import com.agnity.win.datatypes.NonASNDigitCollectionControl;
import com.agnity.win.enumdata.BreakEnum;
import com.agnity.win.enumdata.TypeAheadEnum;
import com.agnity.win.exceptions.InvalidInputException;
import com.agnity.win.util.Util;

public class TestNonAsnDigitCollectionControl extends TestCase {
	

	TypeAheadEnum ta ;
	BreakEnum brk;
	int maximumCollect;
	int minimumCollect ;
	int maximumInteractionTime ;
	int initialInterdigitTime ;
	int normalInterdigitTime;
    char clearDigit;
    char enterDigit ;
    char allowedDigit;
    int specialInterdigitTime;
    ArrayList<Integer> sitDigitSpecific = new ArrayList<Integer>();
	
	byte[] bytesToDecode = null;
	int codes[][];
		
	
public void setUp() throws Exception {
		
		// possible Input values

	ta = TypeAheadEnum.NO_TYPE_AHEAD;
	brk = BreakEnum.NO_BREAK;
	maximumCollect = 6;
	 minimumCollect =1 ;
	 maximumInteractionTime = 7;
	 initialInterdigitTime =3;
	 normalInterdigitTime =6;
    clearDigit = '2';
     enterDigit = '*';
     allowedDigit = '5';
     specialInterdigitTime=8;
     sitDigitSpecific.add(22);
     sitDigitSpecific.add(13);
    byte[] a = {0x06, 0x01, 0x07, 0x03, 0x06, 0x04, 0x00,0x00, 0x00, 0x20 ,0x00 ,0x08, 0x00, 0x10, 0x20, 0x00};

    int[][] c = {{1,1,1,1,1,1,0},{0,0,0,0,0,0,2}};
	    bytesToDecode = a;
	    codes = c;
}	
	/*
	 * Test encoding all possible values of NonASNDigitCollectionControl
	 */
	public void testEncodeDigitCollectionControl() throws Exception{
		byte[] b = null;

		try {
			b = NonASNDigitCollectionControl.encodeDigitCollectionControl(maximumCollect,ta,brk,minimumCollect,maximumInteractionTime,
				initialInterdigitTime,normalInterdigitTime,clearDigit,enterDigit,allowedDigit,specialInterdigitTime,sitDigitSpecific);
		}
		catch(InvalidInputException ex){
			System.out.println("Exception: "+ ex.getMessage());
			assertFalse(true);
			ex.printStackTrace();
		}
		catch(Exception e )
		{
			System.out.println("Exception: "+ e.getMessage());
			assertFalse(true);
			e.printStackTrace();
		}
		System.out.println("Encoded NonASNDigitCollectionControl: "+ Util.formatBytes(b));

	}

	/*
	 * Testing decoding of NonASNDigitCollectionControl 
	 */
	public void testDecodeDigitCollectionControl () throws Exception{
		try {
		
			NonASNDigitCollectionControl  dcc = NonASNDigitCollectionControl.decodeDigitCollectionControl(bytesToDecode);
			System.out.println("Decoded NonASNDigitCollectionControl: "+ dcc.toString());
			assertEquals("TypeAheadEnum is maching",dcc.getTa(),ta);
			assertEquals("maximumCollect is maching",dcc.getMaximumCollect(),maximumCollect);
			assertEquals("clearDigit is maching",dcc.getClearDigit(),clearDigit);
		}
		catch(InvalidInputException ex){
			assertFalse(true);
			ex.printStackTrace();
		}
	}
}
