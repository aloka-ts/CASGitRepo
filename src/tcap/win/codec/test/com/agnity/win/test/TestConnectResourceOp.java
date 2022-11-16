package com.agnity.win.test;

import jain.protocol.ss7.tcap.component.InvokeIndEvent;
import jain.protocol.ss7.tcap.component.Operation;

import java.util.LinkedList;

import junit.framework.TestCase;

import org.apache.log4j.Logger;

import com.agnity.win.asngenerated.ConnectResource;
import com.agnity.win.asngenerated.DestinationDigits;
import com.agnity.win.asngenerated.DigitsType;
import com.agnity.win.operations.WinOpCodes;
import com.agnity.win.operations.WinOperationsCoding;
import com.agnity.win.util.Util;

public class TestConnectResourceOp extends TestCase {

	private static Logger logger = Logger
			.getLogger(TestConnectResourceOp.class);

	/*
	 * Test encoding of ConnectResource Request
	 */
	public void testEncodeTestConnectResource() throws Exception {

		LinkedList<byte[]> encode = null;

		try {
			LinkedList<Object> objLL = new LinkedList<Object>();
			LinkedList<String> opCode = new LinkedList<String>();

			objLL.add(getConnectResourceMessage());
			opCode.add(WinOpCodes.CR);

			encode = WinOperationsCoding.encodeOperations(objLL, opCode, true);
			System.out.println("encode size: " + encode.size());
			System.out.println("ConnectResource: "
					+ Util.formatBytes(encode.get(0)));
		} catch (Exception e) {
			System.out.println("exception : " + e.getMessage());
			assertFalse(true);
			e.printStackTrace();
		}
	}

	/*
	 * Test encoding of Invalid ConnectResource message
	 */
	public void testEncodeTestConnectResource_Invalid() throws Exception {

		LinkedList<byte[]> encode = null;

		try {
			LinkedList<Object> objLL = new LinkedList<Object>();
			LinkedList<String> opCode = new LinkedList<String>();

			objLL.add(getConnectResource_InvalidMessage());
			opCode.add(WinOpCodes.CR);

			encode = WinOperationsCoding.encodeOperations(objLL, opCode, true);

			System.out.println("ConnectResource: "
					+ Util.formatBytes(encode.get(0)));
		} catch (Exception e) {
			System.out.println("exception : " + e.getMessage());
			assertFalse("Mandatory parameter missing in message", true);
			e.printStackTrace();
		}
	}

	/*
	 * Test decoding of ConnectResource
	 */
	public void testDecodeConnectResource() throws Exception {
		LinkedList<byte[]> encode = null;
		LinkedList<Object> objLL = new LinkedList<Object>();
		LinkedList<String> opCode = new LinkedList<String>();

		try {
			objLL.add(getConnectResourceMessage());
			opCode.add(WinOpCodes.CR);

			encode = WinOperationsCoding.encodeOperations(objLL, opCode, true);
			System.out.println("ConnectResource: "
					+ Util.formatBytes(encode.get(0)));
		} catch (Exception e) {
			System.out.println("exception : " + e.getMessage());
			assertFalse(true);
			e.printStackTrace();
		}

		try {
			InvokeIndEvent rie = new InvokeIndEvent(new Object());
			byte[] orreqOpCode = { WinOpCodes.CR_BYTE };
			Operation op = new Operation(Operation.OPERATIONTYPE_LOCAL,
					orreqOpCode);
			rie.setOperation(op);
			ConnectResource anl = (ConnectResource) WinOperationsCoding
					.decodeOperation(encode.get(0), rie);
			assertNotNull(anl.getValue().getDestinationDigits());

		} catch (Exception e) {
			System.out.println("exception : " + e.getMessage());
			assertFalse(true);
			e.printStackTrace();
		}

	}

	private ConnectResource getConnectResourceMessage() {

		ConnectResource req = new ConnectResource();

		try {
			ConnectResource.ConnectResourceSequenceType reqType = new ConnectResource.ConnectResourceSequenceType();

			// DigitsType
			DigitsType digTyp = new DigitsType();
			digTyp.setValue(new byte[] { 0x01, 0x02 });
			// destination digits
			DestinationDigits dd = new DestinationDigits();
			dd.setValue(digTyp);
			reqType.setDestinationDigits(dd);

			req.setValue(reqType);
		} catch (Exception ex) {
			System.out.println("Exception in creating ConnectResource message");
			ex.printStackTrace();
		}

		return req;
	}

	// mandatory param : destinationDigits missing
	private ConnectResource getConnectResource_InvalidMessage() {

		ConnectResource req = new ConnectResource();

		try {
			ConnectResource.ConnectResourceSequenceType reqType = new ConnectResource.ConnectResourceSequenceType();

			req.setValue(reqType);
		} catch (Exception ex) {
			System.out.println("Exception in creating ConnectResource message");
			ex.printStackTrace();
		}

		return req;
	}

}
