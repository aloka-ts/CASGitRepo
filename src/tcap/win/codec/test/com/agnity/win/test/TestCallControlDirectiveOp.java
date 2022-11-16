package com.agnity.win.test;

import jain.protocol.ss7.tcap.component.InvokeIndEvent;
import jain.protocol.ss7.tcap.component.Operation;
import jain.protocol.ss7.tcap.component.ResultIndEvent;

import java.util.LinkedList;

import junit.framework.TestCase;

import org.apache.log4j.Logger;

import com.agnity.win.asngenerated.BillingID;
import com.agnity.win.asngenerated.CallControlDirective;
import com.agnity.win.asngenerated.CallControlDirectiveRes;
import com.agnity.win.asngenerated.CallStatus;
import com.agnity.win.asngenerated.MSCID;
import com.agnity.win.datatypes.NonASNActionCode;
import com.agnity.win.datatypes.NonASNBillingID;
import com.agnity.win.datatypes.NonASNMSCID;
import com.agnity.win.enumdata.ActionCodeEnum;
import com.agnity.win.operations.WinOpCodes;
import com.agnity.win.operations.WinOperationsCoding;
import com.agnity.win.util.Util;

public class TestCallControlDirectiveOp extends TestCase {


	private static Logger logger = Logger.getLogger(TestCallControlDirectiveOp.class);

	/*
	 * Test encoding of Call Control Directive Response
	 */
	public void testEncodeCallControlDirectiveResp() throws Exception{

		LinkedList<byte[]> encode = null;

		try{
			LinkedList<Object> objLL = new LinkedList<Object>();
			LinkedList<String> opCode = new LinkedList<String>();

			objLL.add(getCallControlDirectiveResMessage());
			opCode.add(WinOpCodes.CALL_CNTRL_DIR);

			encode = WinOperationsCoding.encodeOperations(objLL, opCode,false);
			System.out.println("CallControlDirectiveResponse: "+Util.formatBytes(encode.get(0)));
		}
		catch(Exception e){
			assertFalse(true);
			e.printStackTrace();
		}
	}
	
	/*
	 * Test encoding of CallControlDirective Request
	 */
	public void testEncodeCallControlDirective() throws Exception{

		LinkedList<byte[]> encode = null;

		try{
			LinkedList<Object> objLL = new LinkedList<Object>();
			LinkedList<String> opCode = new LinkedList<String>();

			objLL.add(getCallControlDirectiveMessage());
			opCode.add(WinOpCodes.CALL_CNTRL_DIR);

			encode = WinOperationsCoding.encodeOperations(objLL, opCode,true);
			System.out.println("CallControlDirective: "+Util.formatBytes(encode.get(0)));
		}
		catch(Exception e){
			assertFalse(true);
			e.printStackTrace();
		}
	}

	/*
	 * Test encoding of CallControlDirective Invalid Request
	 */
	public void testEncodeCallControlDirective_Invalid() throws Exception{

		LinkedList<byte[]> encode = null;

		try{
			LinkedList<Object> objLL = new LinkedList<Object>();
			LinkedList<String> opCode = new LinkedList<String>();

			objLL.add(getCallControlDirective_InvalidMessage());
			opCode.add(WinOpCodes.CALL_CNTRL_DIR);
			encode = WinOperationsCoding.encodeOperations(objLL, opCode,true);
			System.out.println("CallControlDirective: "+Util.formatBytes(encode.get(0)));
		}
		catch(Exception e){
			assertFalse("Mandatory field missing in this message",true);
			e.printStackTrace();
		}
	}
	
	/*
	 * Test decoding of CallControlDirective Request
	 */
	public void testDecodeCallControlDirective() throws Exception{
		LinkedList<byte[]> encode = null;
		LinkedList<Object> objLL = new LinkedList<Object>();
		LinkedList<String> opCode = new LinkedList<String>();

		try{
			objLL.add(getCallControlDirectiveMessage());
			opCode.add(WinOpCodes.CALL_CNTRL_DIR);

			encode = WinOperationsCoding.encodeOperations(objLL, opCode,true);
			System.out.println("CallControlDirective: "+Util.formatBytes(encode.get(0)));
		}
		catch(Exception e){
			System.out.println("exception : "+e.getMessage());
			assertFalse(true);
			e.printStackTrace();
		}

		try {
			InvokeIndEvent rie = new InvokeIndEvent(new Object());
			byte[] orreqOpCode =  { WinOpCodes.CALL_CNTRL_DIR_BYTE };
			Operation op = new Operation(Operation.OPERATIONTYPE_LOCAL, orreqOpCode);
			rie.setOperation(op);
			CallControlDirective anl = (CallControlDirective) WinOperationsCoding.decodeOperation(encode.get(0), rie);
			assertNotNull(anl.getValue().getMscid());
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

	/*
	 * Test decoding of CallControlDirectiveRes
	 */
	public void testDecodeCallControlDirectiveRes() throws Exception{
		LinkedList<byte[]> encode = null;
		LinkedList<Object> objLL = new LinkedList<Object>();
		LinkedList<String> opCode = new LinkedList<String>();

		try{
			objLL.add(getCallControlDirectiveResMessage());
			opCode.add(WinOpCodes.CALL_CNTRL_DIR);

			encode = WinOperationsCoding.encodeOperations(objLL, opCode,false);
			System.out.println("CallControlDirective Response: "+Util.formatBytes(encode.get(0)));
		}
		catch(Exception e){
			System.out.println("exception : "+e.getMessage());
			assertFalse(true);
			e.printStackTrace();
		}
		try {
			ResultIndEvent rie = new ResultIndEvent(new Object());
			byte[] orreqOpCode =  { WinOpCodes.CALL_CNTRL_DIR_BYTE };
			Operation op = new Operation(Operation.OPERATIONTYPE_LOCAL, orreqOpCode);
			rie.setOperation(op);
			CallControlDirectiveRes anl = (CallControlDirectiveRes) WinOperationsCoding.decodeOperation(encode.get(0), rie);
			assertNotNull(anl.getValue().getCallStatus());
		}
		catch(Exception e){
			System.out.println("exception : "+e.getMessage());
			System.out.println("e.printStackTrace() : "+e.getStackTrace());
			assertFalse(true);
			e.printStackTrace();
		}

	}
	
	public byte[] getActionCode() throws Exception {
		LinkedList<ActionCodeEnum> acList = new LinkedList<ActionCodeEnum>();
		acList.add(ActionCodeEnum.DISCONNECT_CALL);

		byte [] b = NonASNActionCode.encodeActionCode(acList);
		return b;	 
	}

	private CallControlDirectiveRes getCallControlDirectiveResMessage(){

		CallControlDirectiveRes resp = new CallControlDirectiveRes();

		try {
			CallControlDirectiveRes.CallControlDirectiveResSequenceType reqType = 
				new CallControlDirectiveRes.CallControlDirectiveResSequenceType();

			// CallStatus 
			CallStatus.EnumType enumVal = CallStatus.EnumType.locally_Allowed_Call_No_Action;
			CallStatus callStat = new CallStatus();
			callStat.setValue(enumVal);
			reqType.setCallStatus(callStat);
		
			// Add Type to resp
			resp.setValue(reqType);
		}
		catch(Exception ex) {
			System.out.println("Exception in creating CallControlDirective message");
			ex.printStackTrace();
		}

		return resp;
	}

	public byte[] getMSCID() throws Exception {

		byte [] b = NonASNMSCID.encodeMSCID((short) 23, 45);
		return b;	 
	}
	
	public byte[] getBillingId() throws Exception {

		byte [] b = NonASNBillingID.encodeBillingID((short)4,(short) 3, 2, (short)6);
		return b;	 
	}
	
	
	private CallControlDirective getCallControlDirectiveMessage(){ 

		CallControlDirective req = new CallControlDirective();

		try {
			CallControlDirective.CallControlDirectiveSequenceType reqType = 
				new CallControlDirective.CallControlDirectiveSequenceType();
			
			// BillingID
			BillingID billingID = new BillingID();
			billingID.setValue(getBillingId());
			reqType.setBillingID(billingID);

			// MSCID
			MSCID mscid = new MSCID();
			mscid.setValue(getMSCID());
			reqType.setMscid(mscid);
						
			req.setValue(reqType);
		}
		catch(Exception ex) {
			System.out.println("Exception in creating CallControlDirective message");
			ex.printStackTrace();
		}

		return req;
	}
	
	// mandatory field billingid missing in this msg
	private CallControlDirective getCallControlDirective_InvalidMessage(){ 

		CallControlDirective req = new CallControlDirective();

		try {
			CallControlDirective.CallControlDirectiveSequenceType reqType = 
				new CallControlDirective.CallControlDirectiveSequenceType();

			// MSCID
			MSCID mscid = new MSCID();
			mscid.setValue(getMSCID());
			reqType.setMscid(mscid);
						
			req.setValue(reqType);
		}
		catch(Exception ex) {
			System.out.println("Exception in creating CallControlDirective message");
			ex.printStackTrace();
		}

		return req;
	}
	
}
