package com.agnity.win.test;

import jain.protocol.ss7.tcap.component.InvokeIndEvent;
import jain.protocol.ss7.tcap.component.Operation;
import jain.protocol.ss7.tcap.component.ResultIndEvent;

import java.util.LinkedList;

import org.apache.log4j.Logger;

import com.agnity.win.asngenerated.ActionCode;
import com.agnity.win.asngenerated.AnalyzedInformationRes;
import com.agnity.win.asngenerated.BillingID;
import com.agnity.win.asngenerated.DMH_ServiceID;
import com.agnity.win.asngenerated.MSCID;
import com.agnity.win.asngenerated.ODisconnect;
import com.agnity.win.asngenerated.ODisconnectRes;
import com.agnity.win.asngenerated.TimeOfDay;
import com.agnity.win.asngenerated.TransactionCapability;
import com.agnity.win.asngenerated.TriggerCapability;
import com.agnity.win.asngenerated.TriggerType;
import com.agnity.win.asngenerated.WINCapability;
import com.agnity.win.asngenerated.WINOperationsCapability;
import com.agnity.win.datatypes.NonASNActionCode;
import com.agnity.win.datatypes.NonASNBillingID;
import com.agnity.win.datatypes.NonASNDmhServiceId;
import com.agnity.win.datatypes.NonASNMSCID;
import com.agnity.win.datatypes.NonASNTriggerCapability;
import com.agnity.win.datatypes.NonASNWINOperationCapability;
import com.agnity.win.enumdata.ActionCodeEnum;
import com.agnity.win.enumdata.CCDIREnum;
import com.agnity.win.enumdata.CircuitSwitchedDataEnum;
import com.agnity.win.enumdata.ConnectResourceEnum;
import com.agnity.win.enumdata.PositionRequestEnum;
import com.agnity.win.operations.WinOpCodes;
import com.agnity.win.operations.WinOperationsCoding;
import com.agnity.win.util.Util;

import junit.framework.TestCase;

public class TestODisconnectOp extends TestCase {

	private static Logger logger = Logger.getLogger(TestODisconnectOp.class);

	/*
	 * Test encoding of ODisconnect Response
	 */
	public void testEncodeODisconnectResp() throws Exception {

		LinkedList<byte[]> encode = null;

		try {
			LinkedList<Object> objLL = new LinkedList<Object>();
			LinkedList<String> opCode = new LinkedList<String>();

			objLL.add(getODisconnectResMessage());
			opCode.add(WinOpCodes.O_DISC);

			encode = WinOperationsCoding.encodeOperations(objLL, opCode, false);
			System.out.println("ODisconnectResponse: "
					+ Util.formatBytes(encode.get(0)));
		} catch (Exception e) {
			assertFalse(true);
			e.printStackTrace();
		}
	}

	/*
	 * Test encoding of ODisconnect Request
	 */
	public void testEncodeODisconnect() throws Exception {

		LinkedList<byte[]> encode = null;

		try {
			LinkedList<Object> objLL = new LinkedList<Object>();
			LinkedList<String> opCode = new LinkedList<String>();

			objLL.add(getODisconnectMessage());
			opCode.add(WinOpCodes.O_DISC);

			encode = WinOperationsCoding.encodeOperations(objLL, opCode, true);
			System.out.println("ODisconnect: "
					+ Util.formatBytes(encode.get(0)));
		} catch (Exception e) {
			System.out.println("exception : " + e.getMessage());
			assertFalse(true);
			e.printStackTrace();
		}
	}

	/*
	 * Test encoding of Invalid ODisconnect Request
	 */
	public void testEncodeODisconnect_Invalid() throws Exception {

		LinkedList<byte[]> encode = null;

		try {
			LinkedList<Object> objLL = new LinkedList<Object>();
			LinkedList<String> opCode = new LinkedList<String>();

			objLL.add(getODisconnect_InvalidMessage());
			opCode.add(WinOpCodes.O_DISC);

			encode = WinOperationsCoding.encodeOperations(objLL, opCode, true);
			System.out.println("ODisconnect: "
					+ Util.formatBytes(encode.get(0)));
		} catch (Exception e) {
			System.out.println("exception : " + e.getMessage());
			assertFalse("Mandatory parameters missing in this message", true);
			e.printStackTrace();
		}
	}

	/*
	 * Test decoding of ODisconnect Request
	 */
	public void testDecodeODisconnect() throws Exception {
		LinkedList<byte[]> encode = null;
		LinkedList<Object> objLL = new LinkedList<Object>();
		LinkedList<String> opCode = new LinkedList<String>();

		try {
			objLL.add(getODisconnectMessage());
			opCode.add(WinOpCodes.O_DISC);

			encode = WinOperationsCoding.encodeOperations(objLL, opCode, true);
			System.out.println("ODisconnect: "
					+ Util.formatBytes(encode.get(0)));
		} catch (Exception e) {
			System.out.println("exception : " + e.getMessage());
			assertFalse(true);
			e.printStackTrace();
		}

		try {
			InvokeIndEvent rie = new InvokeIndEvent(new Object());
			byte[] orreqOpCode = { WinOpCodes.O_DISC_BYTE };
			Operation op = new Operation(Operation.OPERATIONTYPE_LOCAL,
					orreqOpCode);
			rie.setOperation(op);
			ODisconnect anl = (ODisconnect) WinOperationsCoding
					.decodeOperation(encode.get(0), rie);
			assertNotNull(anl.getValue().getMscid());
			assertNotNull(anl.getValue().getTransactionCapability());
			// issue
			// assertNotNull(anl.getValue().getBillingID());
		} catch (Exception e) {
			System.out.println("exception : " + e.getMessage());
			assertFalse(true);
			e.printStackTrace();
		}

	}

	// Test decoding of ODisconnect Response

	public void testDecodeODisconnectRes() throws Exception {
		LinkedList<byte[]> encode = null;
		LinkedList<Object> objLL = new LinkedList<Object>();
		LinkedList<String> opCode = new LinkedList<String>();

		try {
			objLL.add(getODisconnectResMessage());
			opCode.add(WinOpCodes.O_DISC);

			encode = WinOperationsCoding.encodeOperations(objLL, opCode, false);
			System.out.println("ODisconnect Response: "
					+ Util.formatBytes(encode.get(0)));
		} catch (Exception e) {
			System.out.println("exception : " + e.getMessage());
			assertFalse(true);
			e.printStackTrace();
		}

		try {
			ResultIndEvent rie = new ResultIndEvent(new Object());
			byte[] orreqOpCode = { WinOpCodes.O_DISC_BYTE };
			Operation op = new Operation(Operation.OPERATIONTYPE_LOCAL,
					orreqOpCode);
			rie.setOperation(op);
			ODisconnectRes anl = (ODisconnectRes) WinOperationsCoding
					.decodeOperation(encode.get(0), rie);
			assertNotNull(anl.getValue().getDmh_ServiceID());
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

	private ODisconnectRes getODisconnectResMessage() {

		ODisconnectRes resp = new ODisconnectRes();

		try {
			ODisconnectRes.ODisconnectResSequenceType reqType = new ODisconnectRes.ODisconnectResSequenceType();

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
		return b;
	}

	public byte[] getBillingId() throws Exception {

		byte[] b = NonASNBillingID.encodeBillingID((short) 4, (short) 3, 2,
				(short) 6);
		return b;
	}

	// Mandatory parameters missing in this message
	private ODisconnect getODisconnect_InvalidMessage() {

		ODisconnect req = new ODisconnect();

		try {
			ODisconnect.ODisconnectSequenceType reqType = new ODisconnect.ODisconnectSequenceType();

			// transactionCapability
			TransactionCapability tranCap = new TransactionCapability();
			byte dummy[] = { 0x01, 0x01 };
			tranCap.setValue(dummy);
			reqType.setTransactionCapability(tranCap);

			req.setValue(reqType);
		} catch (Exception ex) {
			System.out.println("Exception in creating ODisconnect message");
			ex.printStackTrace();
		}

		return req;
	}

	private ODisconnect getODisconnectMessage() {

		ODisconnect req = new ODisconnect();

		try {
			ODisconnect.ODisconnectSequenceType reqType = new ODisconnect.ODisconnectSequenceType();

			// BillingID
			BillingID billingID = new BillingID();
			billingID.setValue(getBillingId());
			System.out.println("billingIDbillingID: " + billingID);
			reqType.setBillingID(billingID);

			// MSCID
			MSCID mscid = new MSCID();
			mscid.setValue(getMSCID());
			reqType.setMscid(mscid);

			// timeOfDay
			TimeOfDay timeOfDay = new TimeOfDay();
			Long time = new Long(700);
			timeOfDay.setValue(time);
			reqType.setTimeOfDay(timeOfDay);

			TriggerType.EnumType enumVal = TriggerType.EnumType.specific_Called_Party_Digit_String;
			// TriggerType
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

			req.setValue(reqType);
		} catch (Exception ex) {
			System.out.println("Exception in creating ODisconnect message");
			ex.printStackTrace();
		}

		return req;
	}

}
