package com.agnity.win.test;

import jain.protocol.ss7.tcap.component.InvokeIndEvent;
import jain.protocol.ss7.tcap.component.Operation;
import jain.protocol.ss7.tcap.component.ResultIndEvent;

import java.util.LinkedList;

import junit.framework.TestCase;

import org.apache.log4j.Logger;

import com.agnity.win.asngenerated.DestinationDigits;
import com.agnity.win.asngenerated.DigitsType;
import com.agnity.win.asngenerated.PreferredLanguageIndicator;
import com.agnity.win.asngenerated.SeizeResource;
import com.agnity.win.asngenerated.SeizeResource.SeizeResourceSequenceType;
import com.agnity.win.asngenerated.SeizeResourceRes;
import com.agnity.win.asngenerated.SeizeResourceRes.SeizeResourceResSequenceType;
import com.agnity.win.datatypes.NonASNBillingID;
import com.agnity.win.datatypes.NonASNMSCID;
import com.agnity.win.datatypes.NonASNPreferredLanguageIndicator;
import com.agnity.win.enumdata.PreferredLanguageEnum;
import com.agnity.win.operations.WinOpCodes;
import com.agnity.win.operations.WinOperationsCoding;
import com.agnity.win.util.Util;

public class TestSeizeResourceOp extends TestCase {

	private static Logger logger = Logger.getLogger(TestSeizeResourceOp.class);

	/*
	 * Test encoding of SeizeResource Response
	 */
	public void testEncodeSeizeResourceResp() throws Exception {

		LinkedList<byte[]> encode = null;

		try {
			LinkedList<Object> objLL = new LinkedList<Object>();
			LinkedList<String> opCode = new LinkedList<String>();

			objLL.add(getSeizeResourceResMessage());
			opCode.add(WinOpCodes.SR);

			encode = WinOperationsCoding.encodeOperations(objLL, opCode, false);
			System.out.println("SeizeResourceResponse: "
					+ Util.formatBytes(encode.get(0)));
		} catch (Exception e) {
			assertFalse(true);
			e.printStackTrace();
		}
	}

	public void testEncodeSeizeResourceResp_InvalidMessage() throws Exception {
		// mandatory field destination digit missing
		LinkedList<byte[]> encode = null;

		try {
			LinkedList<Object> objLL = new LinkedList<Object>();
			LinkedList<String> opCode = new LinkedList<String>();

			objLL.add(getSeizeResourceRes_InvalidMessage());
			opCode.add(WinOpCodes.SR);

			encode = WinOperationsCoding.encodeOperations(objLL, opCode, false);
		} catch (Exception e) {
			assertFalse("Mandatory field missing", true);
			e.printStackTrace();
		}
	}

	/*
	 * Test encoding of SeizeResource Request
	 */
	public void testEncodeSeizeResource() throws Exception {

		LinkedList<byte[]> encode = null;

		try {
			LinkedList<Object> objLL = new LinkedList<Object>();
			LinkedList<String> opCode = new LinkedList<String>();

			objLL.add(getSeizeResourceMessage());
			opCode.add(WinOpCodes.SR);

			encode = WinOperationsCoding.encodeOperations(objLL, opCode, true);
			System.out.println("SeizeResource: "
					+ Util.formatBytes(encode.get(0)));
		} catch (Exception e) {
			System.out.println("exception : " + e.getMessage());
			assertFalse(true);
			e.printStackTrace();
		}
	}

	/*
	 * Test decoding of SeizeResource Request
	 */
	public void testDecodeSeizeResource() throws Exception {
		LinkedList<byte[]> encode = null;
		LinkedList<Object> objLL = new LinkedList<Object>();
		LinkedList<String> opCode = new LinkedList<String>();

		try {
			objLL.add(getSeizeResourceMessage());
			opCode.add(WinOpCodes.SR);

			encode = WinOperationsCoding.encodeOperations(objLL, opCode, true);
			System.out.println("SeizeResource: "
					+ Util.formatBytes(encode.get(0)));
		} catch (Exception e) {
			System.out.println("exception : " + e.getMessage());
			assertFalse(true);
			e.printStackTrace();
		}

		try {
			InvokeIndEvent rie = new InvokeIndEvent(new Object());
			byte[] orreqOpCode = { WinOpCodes.SR_BYTE };
			Operation op = new Operation(Operation.OPERATIONTYPE_LOCAL,
					orreqOpCode);
			rie.setOperation(op);
			SeizeResource anl = (SeizeResource) WinOperationsCoding
					.decodeOperation(encode.get(0), rie);
			assertNotNull(anl.getValue().getPreferredLanguageIndicator());
		} catch (Exception e) {
			System.out.println("exception : " + e.getMessage());
			assertFalse(true);
			e.printStackTrace();
		}

	}

	// Test decoding of SeizeResourceResponse

	public void testDecodeSeizeResourceRes() throws Exception {
		LinkedList<byte[]> encode = null;
		LinkedList<Object> objLL = new LinkedList<Object>();
		LinkedList<String> opCode = new LinkedList<String>();

		try {
			objLL.add(getSeizeResourceResMessage());
			opCode.add(WinOpCodes.SR);

			encode = WinOperationsCoding.encodeOperations(objLL, opCode, false);
			System.out.println("SeizeResource Response: "
					+ Util.formatBytes(encode.get(0)));
		} catch (Exception e) {
			System.out.println("exception : " + e.getMessage());
			assertFalse(true);
			e.printStackTrace();
		}

		try {
			ResultIndEvent rie = new ResultIndEvent(new Object());
			byte[] orreqOpCode = { WinOpCodes.SR_BYTE };
			Operation op = new Operation(Operation.OPERATIONTYPE_LOCAL,
					orreqOpCode);
			rie.setOperation(op);
			SeizeResourceRes anl = (SeizeResourceRes) WinOperationsCoding
					.decodeOperation(encode.get(0), rie);
			assertNotNull(anl.getValue().getDestinationDigits());
		} catch (Exception e) {
			System.out.println("exception : " + e.getMessage());
			assertFalse(true);
			e.printStackTrace();
		}

	}

	private SeizeResourceRes getSeizeResourceRes_InvalidMessage() {

		SeizeResourceRes resp = new SeizeResourceRes();

		try {
			SeizeResourceResSequenceType reqType = new SeizeResourceRes.SeizeResourceResSequenceType();

			// Add Type to resp
			resp.setValue(reqType);
		} catch (Exception ex) {
			System.out.println("Exception in creating orreq message");
			ex.printStackTrace();
		}

		return resp;
	}

	private SeizeResourceRes getSeizeResourceResMessage() {

		SeizeResourceRes resp = new SeizeResourceRes();

		try {
			SeizeResourceResSequenceType reqType = new SeizeResourceRes.SeizeResourceResSequenceType();

			DigitsType digTyp = new DigitsType();
			digTyp.setValue(new byte[] { 0x01, 0x02 });
			DestinationDigits dd = new DestinationDigits();
			dd.setValue(digTyp);
			reqType.setDestinationDigits(dd);

			// Add Type to resp
			resp.setValue(reqType);
		} catch (Exception ex) {
			System.out.println("Exception in creating orreq message");
			ex.printStackTrace();
		}

		return resp;
	}

	public byte[] getMSCID() throws Exception {

		byte[] b = NonASNMSCID.encodeMSCID((short) 23, 45);
		return b;
	}

	public byte[] getBillingId() throws Exception {

		byte[] b = NonASNBillingID.encodeBillingID((short) 4, (short) 3, 2,
				(short) 6);
		return b;
	}

	public byte[] getPreferredLanguage() throws Exception {
		LinkedList<PreferredLanguageEnum> acList = new LinkedList<PreferredLanguageEnum>();
		acList.add(PreferredLanguageEnum.GERMAN);

		byte[] b = NonASNPreferredLanguageIndicator
				.encodePreferredLanguageIndicator(acList);
		return b;
	}

	private SeizeResource getSeizeResourceMessage() {

		SeizeResource req = new SeizeResource();

		try {
			SeizeResourceSequenceType reqType = new SeizeResourceSequenceType();

			PreferredLanguageIndicator pref = new PreferredLanguageIndicator();
			pref.setValue(getPreferredLanguage());
			reqType.setPreferredLanguageIndicator(pref);
			req.setValue(reqType);
		} catch (Exception ex) {
			System.out.println("Exception in creating SeizeResource message");
			ex.printStackTrace();
		}

		return req;
	}

}
