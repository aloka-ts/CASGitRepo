package com.agnity.win.test;

import jain.protocol.ss7.tcap.component.InvokeIndEvent;
import jain.protocol.ss7.tcap.component.Operation;
import jain.protocol.ss7.tcap.component.ResultIndEvent;

import java.util.LinkedList;

import junit.framework.TestCase;

import org.apache.log4j.Logger;

import com.agnity.win.asngenerated.ActionCode;
import com.agnity.win.asngenerated.AnalyzedInformation;
import com.agnity.win.asngenerated.AnalyzedInformationRes;
import com.agnity.win.asngenerated.BillingID;
import com.agnity.win.asngenerated.DMH_ServiceID;
import com.agnity.win.asngenerated.Digits;
import com.agnity.win.asngenerated.DigitsType;
import com.agnity.win.asngenerated.MSCID;
import com.agnity.win.asngenerated.TransactionCapability;
import com.agnity.win.asngenerated.TriggerCapability;
import com.agnity.win.asngenerated.TriggerType;
import com.agnity.win.asngenerated.WINCapability;
import com.agnity.win.asngenerated.WINOperationsCapability;
import com.agnity.win.datatypes.NonASNActionCode;
import com.agnity.win.datatypes.NonASNBillingID;
import com.agnity.win.datatypes.NonASNDigitsType;
import com.agnity.win.datatypes.NonASNDmhServiceId;
import com.agnity.win.datatypes.NonASNMSCID;
import com.agnity.win.datatypes.NonASNTriggerCapability;
import com.agnity.win.datatypes.NonASNWINOperationCapability;
import com.agnity.win.enumdata.ActionCodeEnum;
import com.agnity.win.enumdata.CCDIREnum;
import com.agnity.win.enumdata.CircuitSwitchedDataEnum;
import com.agnity.win.enumdata.ConnectResourceEnum;
import com.agnity.win.enumdata.EncodingSchemeEnum;
import com.agnity.win.enumdata.NatureOfNumAvailIndEnum;
import com.agnity.win.enumdata.NatureOfNumIndEnum;
import com.agnity.win.enumdata.NatureOfNumPresentationIndEnum;
import com.agnity.win.enumdata.NatureOfNumScreenIndEnum;
import com.agnity.win.enumdata.NumPlanEnum;
import com.agnity.win.enumdata.PositionRequestEnum;
import com.agnity.win.enumdata.TypeOfDigitsEnum;
import com.agnity.win.operations.WinOpCodes;
import com.agnity.win.operations.WinOperationsCoding;
import com.agnity.win.util.Util;

public class TestAnalyzedInformationOp extends TestCase {

	private static Logger logger = Logger
			.getLogger(TestAnalyzedInformationOp.class);

	/*
	 * Test encoding of anlyzd response
	 */
	public void testEncodeAnalyzedInformationResp() throws Exception {

		LinkedList<byte[]> encode = null;

		try {
			LinkedList<Object> objLL = new LinkedList<Object>();
			LinkedList<String> opCode = new LinkedList<String>();

			objLL.add(getAnlyzdResMessage());
			opCode.add(WinOpCodes.AIR);

			encode = WinOperationsCoding.encodeOperations(objLL, opCode, false);
			System.out.println("AnalyzedInformationResponse: "
					+ Util.formatBytes(encode.get(0)));
		} catch (Exception e) {
			assertFalse(true);
			e.printStackTrace();
		}
	}

	/*
	 * Test encoding of anlyzd req
	 */
	public void testEncodeAnalyzedInformation() throws Exception {

		LinkedList<byte[]> encode = null;

		try {
			LinkedList<Object> objLL = new LinkedList<Object>();
			LinkedList<String> opCode = new LinkedList<String>();

			objLL.add(getAnlyzdMessage());
			opCode.add(WinOpCodes.AIR);

			encode = WinOperationsCoding.encodeOperations(objLL, opCode, true);
			System.out.println("AnalyzedInformation: "
					+ Util.formatBytes(encode.get(0)));
		} catch (Exception e) {
			assertFalse(true);
			e.printStackTrace();
		}
	}

	/*
	 * Test encoding of invalid anlyzd req
	 */
	public void testEncodeAnalyzedInformation_Invalid() throws Exception {

		LinkedList<byte[]> encode = null;

		try {
			LinkedList<Object> objLL = new LinkedList<Object>();
			LinkedList<String> opCode = new LinkedList<String>();

			objLL.add(getAnlyzd_InvalidMessage());
			opCode.add(WinOpCodes.AIR);

			encode = WinOperationsCoding.encodeOperations(objLL, opCode, true);
			System.out.println("AnalyzedInformation: "
					+ Util.formatBytes(encode.get(0)));
		} catch (Exception e) {
			assertFalse("Request message had mandatory parameter missing", true);
			e.printStackTrace();
		}
	}

	/*
	 * Test decoding of analyzereq
	 */
	public void testDecodeAnalyzedInformation() throws Exception {
		LinkedList<byte[]> encode = null;
		LinkedList<Object> objLL = new LinkedList<Object>();
		LinkedList<String> opCode = new LinkedList<String>();

		try {
			objLL.add(getAnlyzdMessage());
			opCode.add(WinOpCodes.AIR);

			encode = WinOperationsCoding.encodeOperations(objLL, opCode, true);
			System.out.println("AnalyzeRequest: "
					+ Util.formatBytes(encode.get(0)));
		} catch (Exception e) {
			System.out.println("exception : " + e.getMessage());
			assertFalse(true);
			e.printStackTrace();
		}

		try {
			InvokeIndEvent rie = new InvokeIndEvent(new Object());
			byte[] orreqOpCode = { WinOpCodes.AIR_BYTE };
			Operation op = new Operation(Operation.OPERATIONTYPE_LOCAL,
					orreqOpCode);
			rie.setOperation(op);
			AnalyzedInformation anl = (AnalyzedInformation) WinOperationsCoding
					.decodeOperation(encode.get(0), rie);
			assertNotNull(anl.getValue().getWinCapability().toString());
			assertNotNull(anl.getValue().getMscid());
			assertNotNull(anl.getValue().getDigits());
			assertNotNull(anl.getValue().getTransactionCapability());
			assertNotNull(anl.getValue().getTriggerType());
			// issue
			// assertNotNull(anl.getValue().getBillingID());
		} catch (Exception e) {
			System.out.println("exception : " + e.getMessage());
			System.out.println("e.printStackTrace() : " + e.getStackTrace());
			assertFalse(true);
			e.printStackTrace();
		}

	}

	/*
	 * Test decoding of AnalyzedInformationResponse
	 */
	public void testDecodeAnalyzedInformationRes() throws Exception {
		LinkedList<byte[]> encode = null;
		LinkedList<Object> objLL = new LinkedList<Object>();
		LinkedList<String> opCode = new LinkedList<String>();

		try {
			objLL.add(getAnlyzdResMessage());
			opCode.add(WinOpCodes.AIR);

			encode = WinOperationsCoding.encodeOperations(objLL, opCode, false);
			System.out.println("AnalyzeResponse: "
					+ Util.formatBytes(encode.get(0)));
		} catch (Exception e) {
			System.out.println("exception : " + e.getMessage());
			assertFalse(true);
			e.printStackTrace();
		}

		try {
			ResultIndEvent rie = new ResultIndEvent(new Object());
			byte[] orreqOpCode = { WinOpCodes.AIR_BYTE };
			Operation op = new Operation(Operation.OPERATIONTYPE_LOCAL,
					orreqOpCode);
			rie.setOperation(op);
			AnalyzedInformationRes anl = (AnalyzedInformationRes) WinOperationsCoding
					.decodeOperation(encode.get(0), rie);
			assertNotNull(anl.getValue().getActionCode());
		} catch (Exception e) {
			System.out.println("exception : " + e.getMessage());
			assertFalse(true);
			e.printStackTrace();
		}

	}

	public byte[] getActionCode() throws Exception {
		LinkedList<ActionCodeEnum> acList = new LinkedList<ActionCodeEnum>();
		acList.add(ActionCodeEnum.DISCONNECT_CALL);

		byte[] b = NonASNActionCode.encodeActionCode(acList);
		return b;
	}

	private AnalyzedInformationRes getAnlyzdResMessage() {

		AnalyzedInformationRes resp = new AnalyzedInformationRes();

		try {
			AnalyzedInformationRes.AnalyzedInformationResSequenceType reqType = new AnalyzedInformationRes.AnalyzedInformationResSequenceType();

			// ActionDeniedCode
			ActionCode ac = new ActionCode();
			ac.setValue(getActionCode());
			reqType.setActionCode(ac);

			// DMH_serviceId
			LinkedList<Short> mktIdList = new LinkedList<Short>();
			LinkedList<Byte> mktSegIdList = new LinkedList<Byte>();
			LinkedList<Short> svcIdList = new LinkedList<Short>();
			mktIdList.add((short) 4661);
			mktSegIdList.add((byte) 0x12);
			svcIdList.add((short) 65530);

			byte[] b = NonASNDmhServiceId.encodeDmhServiceId(mktIdList,
					mktSegIdList, svcIdList);

			DMH_ServiceID value = new DMH_ServiceID();
			value.setValue(b);
			reqType.setDmh_ServiceID(value);

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
		System.out.println("b: " + Util.formatBytes(b));
		return b;
	}

	public byte[] getBillingId() throws Exception {

		byte[] b = NonASNBillingID.encodeBillingID((short) 4, (short) 3, 2,
				(short) 6);
		System.out.println("b: " + Util.formatBytes(b));
		return b;
	}

	private AnalyzedInformation getAnlyzdMessage() {

		AnalyzedInformation req = new AnalyzedInformation();

		try {
			AnalyzedInformation.AnalyzedInformationSequenceType reqType = new AnalyzedInformation.AnalyzedInformationSequenceType();

			// BillingID
			BillingID billingID = new BillingID();
			billingID.setValue(getBillingId());
			reqType.setBillingID(billingID);

			// MSCID
			MSCID mscid = new MSCID();
			mscid.setValue(getMSCID());
			reqType.setMscid(mscid);

			// TriggerType
			TriggerType.EnumType enumVal = TriggerType.EnumType.specific_Called_Party_Digit_String;
			TriggerType triggerType = new TriggerType();
			triggerType.setValue(enumVal);
			reqType.setTriggerType(triggerType);
		
			// wincap
			WINCapability winCapability = new WINCapability();
			TriggerCapability triggerCapability = new TriggerCapability();
			WINOperationsCapability winOperationsCapability = new WINOperationsCapability();
			byte a = 0x01;
			LinkedList<CircuitSwitchedDataEnum> csd = new LinkedList<CircuitSwitchedDataEnum>();
			LinkedList<CCDIREnum> ccdir = new LinkedList<CCDIREnum>();
			LinkedList<ConnectResourceEnum> cr = new LinkedList<ConnectResourceEnum>();
			LinkedList<PositionRequestEnum> pr = new LinkedList<PositionRequestEnum>();
			csd.add(CircuitSwitchedDataEnum.SENDER_CANNOT_SUPPORT_WIN_CS_DATASERVICES);
			csd.add(CircuitSwitchedDataEnum.SENDER_CAN_SUPPORT_WIN_CS_DATASERVICES);
			ccdir.add(CCDIREnum.SENDER_CANNOT_SUPPORT_CCD_OPERATIONS);
			ccdir.add(CCDIREnum.SENDER_CAN_SUPPORT_CCD_OPERATIONS);
			cr.add(ConnectResourceEnum.SENDER_CANT_SUPPORT_OPERATIONS);
			cr.add(ConnectResourceEnum.SENDER_CAN_SUPPORT_OPERATIONS);
			pr.add(PositionRequestEnum.SENDER_CANNOT_SUPPORT_POS_REQUEST_OPERATIONS);
			pr.add(PositionRequestEnum.SENDER_CAN_SUPPORT_POS_REQUEST_OPERATIONS);
			triggerCapability.setValue(NonASNTriggerCapability
					.encodeTriggerCapability(a, a, a, a, a, a, a, a, a, a, a));
			winOperationsCapability.setValue(NonASNWINOperationCapability
					.encodeWINOperationCapability(csd, ccdir, pr, cr));
			winCapability.setTriggerCapability((triggerCapability));
			winCapability.setWINOperationsCapability(winOperationsCapability);
			reqType.setWinCapability(winCapability);

			// transactionCapability
			TransactionCapability tranCap = new TransactionCapability();
			byte dummy[] = { 0x01, 0x01 };
			tranCap.setValue(dummy);
			reqType.setTransactionCapability(tranCap);
			
			//digits
			
	DigitsType digType = new DigitsType();
	byte b[]=NonASNDigitsType.encodeDigits("989145912", TypeOfDigitsEnum.DIALED_NUM, NatureOfNumIndEnum.NATIONAL, NatureOfNumAvailIndEnum.NUM_AVAILABLE, NatureOfNumPresentationIndEnum.PRES_ALLOW,
			NatureOfNumScreenIndEnum.USER_PROVIDED_SCREEN_FAILED, NumPlanEnum.TEL_NP, EncodingSchemeEnum.BCD);
	digType.setValue(b);
	Digits dig = new Digits();
dig.setValue(digType);
reqType.setDigits(dig);
			
			req.setValue(reqType);
		} catch (Exception ex) {
			System.out.println("Exception in creating anlyzd message");
			ex.printStackTrace();
		}

		return req;
	}

	// mandatory filed: wincapability missing in this message
	private AnalyzedInformation getAnlyzd_InvalidMessage() {

		AnalyzedInformation req = new AnalyzedInformation();

		try {
			AnalyzedInformation.AnalyzedInformationSequenceType reqType = new AnalyzedInformation.AnalyzedInformationSequenceType();

			// BillingID
			BillingID billingID = new BillingID();
			billingID.setValue(getBillingId());
			reqType.setBillingID(billingID);

			// MSCID
			MSCID mscid = new MSCID();
			mscid.setValue(getMSCID());
			reqType.setMscid(mscid);

			req.setValue(reqType);
		} catch (Exception ex) {
			System.out.println("Exception in creating anlyzd message");
			ex.printStackTrace();
		}

		return req;
	}

}
