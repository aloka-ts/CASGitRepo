package com.agnity.win.test;

import jain.protocol.ss7.tcap.component.InvokeIndEvent;
import jain.protocol.ss7.tcap.component.Operation;

import java.util.LinkedList;

import junit.framework.TestCase;

import org.apache.log4j.Logger;

import com.agnity.win.asngenerated.BillingID;
import com.agnity.win.asngenerated.MSCID;
import com.agnity.win.asngenerated.OAnswer;
import com.agnity.win.asngenerated.TimeDateOffset;
import com.agnity.win.asngenerated.TimeOfDay;
import com.agnity.win.asngenerated.TriggerType;
import com.agnity.win.datatypes.NonASNBillingID;
import com.agnity.win.datatypes.NonASNMSCID;
import com.agnity.win.operations.WinOpCodes;
import com.agnity.win.operations.WinOperationsCoding;
import com.agnity.win.util.Util;

public class TestOAnswerOp extends TestCase {
	

	private static Logger logger = Logger.getLogger(TestOAnswerOp.class);


	
	/*
	 * Test encoding of OAnswer
	 */
	public void testEncodeOAnswer() throws Exception{

		LinkedList<byte[]> encode = null;

		try{
			LinkedList<Object> objLL = new LinkedList<Object>();
			LinkedList<String> opCode = new LinkedList<String>();

			objLL.add(getOAnswerMessage());
			System.out.println("getOAnswerMessage : "+getOAnswerMessage());
			opCode.add(WinOpCodes.O_ANS);

			encode = WinOperationsCoding.encodeOperations(objLL, opCode,true);
			System.out.println("OAnswer: "+Util.formatBytes(encode.get(0)));
		}
		catch(Exception e){
			System.out.println("exception : "+e.getMessage());
			assertFalse(true);
			e.printStackTrace();
		}
	}

	/*
	 * Test encoding of invalid OAnswer msg
	 */
	public void testEncodeOAnswer_Invalid() throws Exception{

		LinkedList<byte[]> encode = null;

		try{
			LinkedList<Object> objLL = new LinkedList<Object>();
			LinkedList<String> opCode = new LinkedList<String>();

			objLL.add(getOAnswer_InvalidMessage());
			opCode.add(WinOpCodes.O_ANS);

			encode = WinOperationsCoding.encodeOperations(objLL, opCode,true);
			System.out.println("OAnswer: "+Util.formatBytes(encode.get(0)));
		}
		catch(Exception e){
			System.out.println("exception : "+e.getMessage());
			assertFalse("Mandatory parameters missing in this message",true);
			e.printStackTrace();
		}
	}

	/*
	 * Test decoding of OAnswer
	 */
	public void testDecodeOAnswer() throws Exception{
		LinkedList<byte[]> encode = null;
		LinkedList<Object> objLL = new LinkedList<Object>();
		LinkedList<String> opCode = new LinkedList<String>();

		try{
			objLL.add(getOAnswerMessage());
			opCode.add(WinOpCodes.O_ANS);

			encode = WinOperationsCoding.encodeOperations(objLL, opCode,true);
			System.out.println("OAnswer: "+Util.formatBytes(encode.get(0)));
		}
		catch(Exception e){
			System.out.println("exception : "+e.getMessage());
			assertFalse(true);
			e.printStackTrace();
		}

		try {
			InvokeIndEvent rie = new InvokeIndEvent(new Object());
			byte[] orreqOpCode =  { WinOpCodes.O_ANS_BYTE };
			Operation op = new Operation(Operation.OPERATIONTYPE_LOCAL, orreqOpCode);
			rie.setOperation(op);
			OAnswer anl = (OAnswer) WinOperationsCoding.decodeOperation(encode.get(0), rie);
			assertNotNull(anl.getValue().getMscid());
			assertNotNull(anl.getValue().getTriggerType());
			assertNotNull(anl.getValue().getTimeOfDay());
			assertNotNull(anl.getValue().getTimeDateOffset());
		
			//issue
			//assertNotNull(anl.getValue().getBillingID());
		}
		catch(Exception e){
			System.out.println("exception : "+e.getMessage());
			System.out.println("e.printStackTrace() : "+e.getStackTrace());
			assertFalse(true);
			e.printStackTrace();
		}

	}

	public byte[] getMSCID() throws Exception {

		byte [] b = NonASNMSCID.encodeMSCID((short) 23, 45);
		System.out.println("b: "+ Util.formatBytes(b));
		return b;	 
	}
	
	public byte[] getBillingId() throws Exception {

		byte [] b = NonASNBillingID.encodeBillingID((short)10,(short) 5, 22, (short)61);
		System.out.println("b: "+ Util.formatBytes(b));
		return b;	 
	}

	// mandatory parameters missing in this message
	private OAnswer getOAnswer_InvalidMessage(){ 

		OAnswer req = new OAnswer();
		try {
			OAnswer.OAnswerSequenceType reqType = 
				new OAnswer.OAnswerSequenceType();
			
			// BillingID
			BillingID billingID = new BillingID();
			billingID.setValue(getBillingId());
			reqType.setBillingID(billingID);
			req.setValue(reqType);
		}
		catch(Exception ex) {
			System.out.println("Exception in creating OAnswer message");
			ex.printStackTrace();
		}
		return req;
	}
	
	private OAnswer getOAnswerMessage(){ 

		OAnswer req = new OAnswer();

		try {
			OAnswer.OAnswerSequenceType reqType = 
				new OAnswer.OAnswerSequenceType();
			
		// BillingID
			BillingID billingID = new BillingID();
			billingID.setValue(getBillingId());
			reqType.setBillingID(billingID);
		
			// MSCID
			MSCID mscid = new MSCID();
			mscid.setValue(getMSCID());
			reqType.setMscid(mscid); 
			
			TriggerType.EnumType enumVal = TriggerType.EnumType.specific_Called_Party_Digit_String;
			// TriggerType
			TriggerType triggerType = new TriggerType();
			triggerType.setValue(enumVal);
			reqType.setTriggerType(triggerType);
		
		// timeDateOffset
			byte[] dummydata = {0x02,0x03};
			TimeDateOffset timeDateOffset = new TimeDateOffset();
			timeDateOffset.setValue(dummydata)	;
			reqType.setTimeDateOffset(timeDateOffset);
					
			// timeOfDay
			TimeOfDay timeOfDay = new TimeOfDay();
			Long time = new Long(700);
			timeOfDay.setValue(time)	;
			reqType.setTimeOfDay(timeOfDay);
	
			req.setValue(reqType);
		}
		catch(Exception ex) {
			System.out.println("Exception in creating OAnswer message");
			ex.printStackTrace();
		}

		return req;
	}

}
