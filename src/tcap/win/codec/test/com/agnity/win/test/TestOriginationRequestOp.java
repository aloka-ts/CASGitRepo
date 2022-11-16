package com.agnity.win.test;

import jain.protocol.ss7.tcap.component.InvokeIndEvent;
import jain.protocol.ss7.tcap.component.Operation;
import jain.protocol.ss7.tcap.component.ResultIndEvent;

import java.util.LinkedList;

import junit.framework.TestCase;

import org.apache.log4j.Logger;

import com.agnity.win.asngenerated.ActionCode;
import com.agnity.win.asngenerated.BillingID;
import com.agnity.win.asngenerated.DMH_ServiceID;
import com.agnity.win.asngenerated.Digits;
import com.agnity.win.asngenerated.DigitsType;
import com.agnity.win.asngenerated.ElectronicSerialNumber;
import com.agnity.win.asngenerated.MINType;
import com.agnity.win.asngenerated.MSCID;
import com.agnity.win.asngenerated.MSID;
import com.agnity.win.asngenerated.MobileIdentificationNumber;
import com.agnity.win.asngenerated.OriginationRequest;
import com.agnity.win.asngenerated.OriginationRequestRes;
import com.agnity.win.asngenerated.OriginationTriggers;
import com.agnity.win.asngenerated.TransactionCapability;
import com.agnity.win.datatypes.NonASNActionCode;
import com.agnity.win.datatypes.NonASNBillingID;
import com.agnity.win.datatypes.NonASNDigitsType;
import com.agnity.win.datatypes.NonASNDmhServiceId;
import com.agnity.win.datatypes.NonASNMINType;
import com.agnity.win.datatypes.NonASNMSCID;
import com.agnity.win.enumdata.ActionCodeEnum;
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

public class TestOriginationRequestOp extends TestCase {

	private static Logger logger = Logger
			.getLogger(TestOriginationRequestOp.class);

	/*
	 * Test encoding of orreq
	 */
	public void testEncodeOriginationReq() throws Exception {

		LinkedList<byte[]> encode = null;

		try {
			LinkedList<Object> objLL = new LinkedList<Object>();
			LinkedList<String> opCode = new LinkedList<String>();

			objLL.add(getOrreqMessage());
			opCode.add(WinOpCodes.OR);

			encode = WinOperationsCoding.encodeOperations(objLL, opCode, true);
			System.out.println("OriginationRequest: "
					+ Util.formatBytes(encode.get(0)));

		} catch (Exception e) {
			System.out.println("exception : " + e.getMessage());
			assertFalse(true);
			e.printStackTrace();
		}
	}

	// mandatory fields missing in orreq
	public void testEncodeOriginationReq_invalidMessage() throws Exception {

		try {
			LinkedList<byte[]> encode = null;
			LinkedList<Object> objLL = new LinkedList<Object>();
			LinkedList<String> opCode = new LinkedList<String>();

			// error scenario : mandatory fields missing in orreq
			objLL.add(getInvalidOrreqMessage());
			opCode.add(WinOpCodes.OR);

			encode = WinOperationsCoding.encodeOperations(objLL, opCode, true);
		} catch (Exception e) {
			System.out.println("exception : " + e.getMessage());
			assertFalse("Mandatory fileds missing in this message", true);
			e.printStackTrace();
		}
	}

	/*
	 * Test decoding of orreq
	 */
	public void testDecodeOriginationReq() throws Exception {
		LinkedList<byte[]> encode = null;
		LinkedList<Object> objLL = new LinkedList<Object>();
		LinkedList<String> opCode = new LinkedList<String>();

		try {
			objLL.add(getOrreqMessage());
			opCode.add(WinOpCodes.OR);

			encode = WinOperationsCoding.encodeOperations(objLL, opCode, true);
			System.out.println("OriginationRequest: "
					+ Util.formatBytes(encode.get(0)));
		} catch (Exception e) {
			assertFalse(true);
			e.printStackTrace();
		}

		OriginationRequest orreq1 = null;
		try {
			InvokeIndEvent rie = new InvokeIndEvent(new Object());
			byte[] orreqOpCode = { WinOpCodes.OR_BYTE };
			Operation op = new Operation(Operation.OPERATIONTYPE_LOCAL,
					orreqOpCode);
			rie.setOperation(op);

			orreq1 = (OriginationRequest) WinOperationsCoding.decodeOperation(
					encode.get(0), rie);
			 assertNotNull(orreq1.getValue().getMsid());
			// issue with msid having no tag
			 assertNotNull(orreq1.getValue().getBillingID());
			 
		
			 System.out.println("Decoded value Billing ID:" +Util.formatBytes(orreq1.getValue().getBillingID().getValue()));

			assertNotNull(orreq1.getValue().getMscid());
			System.out
					.println("Decoded value Mscid:"
							+ Util.formatBytes(orreq1.getValue().getMscid()
									.getValue()));

			assertNotNull(orreq1.getValue().getOriginationTriggers());
			System.out.println("Decoded value OriginationTriggers:"
					+ Util.formatBytes(orreq1.getValue()
							.getOriginationTriggers().getValue()));

			assertNotNull(orreq1.getValue().getMobileIdentificationNumber());
			System.out.println("Decoded value MobileIdentificationNumber:"
					+ orreq1.getValue().getMobileIdentificationNumber()
							.getValue());

			assertNotNull(orreq1.getValue().getTransactionCapability());
			System.out.println("Decoded value :"
					+ Util.formatBytes(orreq1.getValue()
							.getTransactionCapability().getValue()));
		} catch (Exception e) {
			assertFalse(true);
			e.printStackTrace();
		}

	}

	/*
	 * Test encoding of orreq
	 */
	public void testEncodeOriginationReqResp() throws Exception {

		LinkedList<byte[]> encode = null;

		try {
			LinkedList<Object> objLL = new LinkedList<Object>();
			LinkedList<String> opCode = new LinkedList<String>();

			objLL.add(getOrreqResMessage());
			opCode.add(WinOpCodes.OR);

			encode = WinOperationsCoding.encodeOperations(objLL, opCode, false);
			System.out.println("OriginationRequestResponse: "
					+ Util.formatBytes(encode.get(0)));
		} catch (Exception e) {
			assertFalse(true);
			e.printStackTrace();
		}
	}

	/*
	 * Test decoding of orreq Response
	 */
	public void testDecodeOriginationReqResp() throws Exception {
		LinkedList<byte[]> encode = null;
		LinkedList<Object> objLL = new LinkedList<Object>();
		LinkedList<String> opCode = new LinkedList<String>();

		try {
			objLL.add(getOrreqResMessage());
			opCode.add(WinOpCodes.OR);

			encode = WinOperationsCoding.encodeOperations(objLL, opCode, false);
			System.out.println("OriginationRequestResponse: "
					+ Util.formatBytes(encode.get(0)));
		} catch (Exception e) {
			assertFalse(true);
			e.printStackTrace();
		}

		OriginationRequestRes orreq1 = null;
		try {
			ResultIndEvent rie = new ResultIndEvent(new Object());
			byte[] orreqOpCode = { WinOpCodes.OR_BYTE };
			Operation op = new Operation(Operation.OPERATIONTYPE_LOCAL,
					orreqOpCode);
			rie.setOperation(op);

		//	orreq1 = (OriginationRequestRes) WinOperationsCoding
			//		.decodeOperation(encode.get(0), rie);
			byte qq[]={(byte) 0xf2, 0x0e, (byte) 0x9f ,(byte) 0x81, 0x00, 0x01, 0x01, (byte) 0x9f, (byte) 0x82, 0x31 ,0x05, 0x00, 0x0f, 0x12, 0x00, 0x31};
			orreq1 = (OriginationRequestRes) WinOperationsCoding
						.decodeOperation(qq, rie);
			System.out.println("orreq1 :"
					+ orreq1.getValue().getActionCode());
			assertTrue(orreq1.getValue().isDmh_ServiceIDPresent());
			System.out.println("Decoded value DMH Service ID:"
					+ Util.formatBytes(orreq1.getValue().getDmh_ServiceID()
							.getValue()));

			assertTrue(orreq1.getValue().isActionCodePresent());
			System.out.println("Decided value of Action Code:"
					+ Util.formatBytes(orreq1.getValue().getActionCode()
							.getValue()));
		} catch (Exception e) {
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

	private OriginationRequestRes getOrreqResMessage() {

		OriginationRequestRes resp = new OriginationRequestRes();

		try {
			OriginationRequestRes.OriginationRequestResSequenceType respType = new OriginationRequestRes.OriginationRequestResSequenceType();

			// ActionDeniedCode
			ActionCode ac = new ActionCode();
			ac.setValue(getActionCode());
			respType.setActionCode(ac);

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
			respType.setDmh_ServiceID(value);

			// Add Type to resp
			resp.setValue(respType);
		} catch (Exception ex) {
			System.out.println("Exception in creating orreq response message");
			ex.printStackTrace();
		}

		return resp;
	}

	public byte[] getBillingId() throws Exception {

		byte[] b = NonASNBillingID.encodeBillingID((short) 4, (short) 3, 2,
				(short) 6);
		return b;
	}

	public byte[] getMSCID() throws Exception {

		byte[] b = NonASNMSCID.encodeMSCID((short) 23, 45);
		return b;
	}

	// Mandatory fileds missing in this message
	private OriginationRequest getInvalidOrreqMessage() {

		OriginationRequest resp = new OriginationRequest();

		try {
			OriginationRequest.OriginationRequestSequenceType reqType = new OriginationRequest.OriginationRequestSequenceType();

			// min
			MINType mt = new MINType();
			mt.setValue(NonASNMINType.encodeMINType("3476a09876"));
			MobileIdentificationNumber min = new MobileIdentificationNumber();
			min.setValue(mt);
			reqType.setMobileIdentificationNumber(min);

			// Add Type to resp
			resp.setValue(reqType);
		} catch (Exception ex) {
			System.out.println("Exception in creating orreq message");
			ex.printStackTrace();
		}

		return resp;
	}

	private OriginationRequest getOrreqMessage() {

		OriginationRequest resp = new OriginationRequest();

		try {
			OriginationRequest.OriginationRequestSequenceType reqType = new OriginationRequest.OriginationRequestSequenceType();

			// BillingID
			BillingID billingID = new BillingID();
			billingID.setValue(getBillingId());
			reqType.setBillingID(billingID);

			// digits
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

			// ElectronicSerialNumber
			ElectronicSerialNumber esn = new ElectronicSerialNumber();
			esn.setValue(new byte[] { 0x01, 0x02 });
			reqType.setElectronicSerialNumber(esn);

			// MSCID
			MSCID mscid = new MSCID();
			mscid.setValue(getMSCID());
			reqType.setMscid(mscid);

			// transactionCapability
			TransactionCapability tranCap = new TransactionCapability();
			byte dummy[] = { 0x01, 0x01 };
			tranCap.setValue(dummy);
			reqType.setTransactionCapability(tranCap);

			// OriginationTrigger
			OriginationTriggers ot = new OriginationTriggers();
			ot.setValue(new byte[] { 0x01, 0x02 });
			reqType.setOriginationTriggers(ot);

			// min
			MINType mt = new MINType();
			mt.setValue(NonASNMINType.encodeMINType("3476a09876"));
			MobileIdentificationNumber min = new MobileIdentificationNumber();
			min.setValue(mt);
			reqType.setMobileIdentificationNumber(min);
			
			MSID msid = new MSID();
			msid.selectMobileIdentificationNumber(min);
			reqType.setMsid(msid);

			// Add Type to resp
			resp.setValue(reqType);
		} catch (Exception ex) {
			System.out.println("Exception in creating orreq message");
			ex.printStackTrace();
		}

		return resp;
	}
}
