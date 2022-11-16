package com.agnity.win.test;

import jain.protocol.ss7.tcap.component.InvokeIndEvent;
import jain.protocol.ss7.tcap.component.Operation;
import jain.protocol.ss7.tcap.component.ResultIndEvent;

import java.util.LinkedList;

import junit.framework.TestCase;

import org.apache.log4j.Logger;

import com.agnity.win.asngenerated.Digits;
import com.agnity.win.asngenerated.DigitsType;
import com.agnity.win.asngenerated.MobileDirectoryNumber;
import com.agnity.win.asngenerated.SRFDirective;
import com.agnity.win.asngenerated.SRFDirective.SRFDirectiveSequenceType;
import com.agnity.win.asngenerated.SRFDirectiveRes;
import com.agnity.win.asngenerated.SRFDirectiveRes.SRFDirectiveResSequenceType;
import com.agnity.win.datatypes.NonASNDigitsType;
import com.agnity.win.enumdata.EncodingSchemeEnum;
import com.agnity.win.enumdata.NatureOfNumAvailIndEnum;
import com.agnity.win.enumdata.NatureOfNumIndEnum;
import com.agnity.win.enumdata.NatureOfNumPresentationIndEnum;
import com.agnity.win.enumdata.NatureOfNumScreenIndEnum;
import com.agnity.win.enumdata.NumPlanEnum;
import com.agnity.win.enumdata.TypeOfDigitsEnum;
import com.agnity.win.operations.WinOpCodes;
import com.agnity.win.operations.WinOperationsCoding;
import com.agnity.win.util.Util;

public class TestSRFDirectiveOp extends TestCase {
	private static Logger logger = Logger.getLogger(TestSRFDirectiveOp.class);

	/*
	 * Test encoding of SRFDirective Response
	 */
	public void testEncodeSRFDirectiveResp() throws Exception {

		LinkedList<byte[]> encode = null;

		try {
			LinkedList<Object> objLL = new LinkedList<Object>();
			LinkedList<String> opCode = new LinkedList<String>();

			objLL.add(getSRFDirectiveResMessage());
			opCode.add(WinOpCodes.SRF_DIR);

			encode = WinOperationsCoding.encodeOperations(objLL, opCode, false);
			System.out.println("SRFDirectiveResponse: "
					+ Util.formatBytes(encode.get(0)));
		} catch (Exception e) {
			assertFalse(true);
			e.printStackTrace();
		}
	}

	/*
	 * Test encoding of SRFDirective Request
	 */
	public void testEncodeSRFDirective() throws Exception {

		LinkedList<byte[]> encode = null;

		try {
			LinkedList<Object> objLL = new LinkedList<Object>();
			LinkedList<String> opCode = new LinkedList<String>();

			objLL.add(getSRFDirectiveMessage());
			opCode.add(WinOpCodes.SRF_DIR);

			encode = WinOperationsCoding.encodeOperations(objLL, opCode, true);
			System.out.println("SRFDirective: "
					+ Util.formatBytes(encode.get(0)));
		} catch (Exception e) {
			System.out.println("exception : " + e.getMessage());
			assertFalse(true);
			e.printStackTrace();
		}
	}

	/*
	 * Test decoding of SRFDirective Request
	 */
	public void testDecodeSRFDirective() throws Exception {
		LinkedList<byte[]> encode = null;
		LinkedList<Object> objLL = new LinkedList<Object>();
		LinkedList<String> opCode = new LinkedList<String>();

		try {
			objLL.add(getSRFDirectiveMessage());
			opCode.add(WinOpCodes.SRF_DIR);

			encode = WinOperationsCoding.encodeOperations(objLL, opCode, true);
			System.out.println("SRFDirective: "
					+ Util.formatBytes(encode.get(0)));
		} catch (Exception e) {
			System.out.println("exception : " + e.getMessage());
			assertFalse(true);
			e.printStackTrace();
		}

		try {
			InvokeIndEvent rie = new InvokeIndEvent(new Object());
			byte[] orreqOpCode = { WinOpCodes.SRF_DIR_BYTE };
			Operation op = new Operation(Operation.OPERATIONTYPE_LOCAL,
					orreqOpCode);
			rie.setOperation(op);
			SRFDirective anl = (SRFDirective) WinOperationsCoding
					.decodeOperation(encode.get(0), rie);
			assertTrue(anl.getValue().isMobileDirectoryNumberPresent());
		} catch (Exception e) {
			System.out.println("exception : " + e.getMessage());
			System.out.println("e.printStackTrace() : " + e.getStackTrace());
			assertFalse(true);
			e.printStackTrace();
		}

	}

	// Test decoding of SRFDirectiveResponse

	public void testDecodeSRFDirectiveRes() throws Exception {
		LinkedList<byte[]> encode = null;
		LinkedList<Object> objLL = new LinkedList<Object>();
		LinkedList<String> opCode = new LinkedList<String>();

		try {
			objLL.add(getSRFDirectiveResMessage());
			opCode.add(WinOpCodes.SRF_DIR);

			encode = WinOperationsCoding.encodeOperations(objLL, opCode, false);
			System.out.println("SRFDirective Response: "
					+ Util.formatBytes(encode.get(0)));
		} catch (Exception e) {
			System.out.println("exception : " + e.getMessage());
			assertFalse(true);
			e.printStackTrace();
		}

		try {
			ResultIndEvent rie = new ResultIndEvent(new Object());
			byte[] orreqOpCode = { WinOpCodes.SRF_DIR_BYTE };
			Operation op = new Operation(Operation.OPERATIONTYPE_LOCAL,
					orreqOpCode);
			rie.setOperation(op);
			SRFDirectiveRes anl = (SRFDirectiveRes) WinOperationsCoding
					.decodeOperation(encode.get(0), rie);
			assertNotNull(anl.getValue().getDigits());
		} catch (Exception e) {
			System.out.println("exception : " + e.getMessage());
			System.out.println("e.printStackTrace() : " + e.getStackTrace());
			assertFalse(true);
			e.printStackTrace();
		}

	}

	private SRFDirectiveRes getSRFDirectiveResMessage() {

		SRFDirectiveRes resp = new SRFDirectiveRes();

		SRFDirectiveResSequenceType reqType = new SRFDirectiveRes.SRFDirectiveResSequenceType();

		try {
			byte[] b = NonASNDigitsType.encodeDigits("989145912",
					TypeOfDigitsEnum.DIALED_NUM, NatureOfNumIndEnum.NATIONAL,
					NatureOfNumAvailIndEnum.NUM_AVAILABLE,
					NatureOfNumPresentationIndEnum.PRES_ALLOW,
					NatureOfNumScreenIndEnum.USER_PROVIDED_SCREEN_FAILED,
					NumPlanEnum.TEL_NP, EncodingSchemeEnum.BCD);
			DigitsType digTyp = new DigitsType();
			digTyp.setValue(b);
			Digits dd = new Digits();
			dd.setValue(digTyp); // issue
			reqType.setDigits(dd);

			// Add Type to resp
			resp.setValue(reqType);
		} catch (Exception ex) {
			System.out
					.println("Exception in creating SRFDirective Response message");
			ex.printStackTrace();
		}
		return resp;
	}

	private SRFDirective getSRFDirectiveMessage() {

		SRFDirective req = new SRFDirective();

		try {
			SRFDirectiveSequenceType reqType = new SRFDirectiveSequenceType();

			byte[] b = NonASNDigitsType.encodeDigits("989145912",
					TypeOfDigitsEnum.DIALED_NUM, NatureOfNumIndEnum.NATIONAL,
					NatureOfNumAvailIndEnum.NUM_AVAILABLE,
					NatureOfNumPresentationIndEnum.PRES_ALLOW,
					NatureOfNumScreenIndEnum.USER_PROVIDED_SCREEN_FAILED,
					NumPlanEnum.TEL_NP, EncodingSchemeEnum.BCD);

			DigitsType digTyp = new DigitsType();
			digTyp.setValue(b);
			MobileDirectoryNumber mdn = new MobileDirectoryNumber();
			mdn.setValue(digTyp);
			reqType.setMobileDirectoryNumber(mdn);
			req.setValue(reqType);
		} catch (Exception ex) {
			System.out.println("Exception in creating SRFDirective message");
			ex.printStackTrace();
		}

		return req;
	}

}
