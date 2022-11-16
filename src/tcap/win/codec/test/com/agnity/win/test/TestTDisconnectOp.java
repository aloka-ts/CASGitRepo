package com.agnity.win.test;

import jain.protocol.ss7.tcap.component.InvokeIndEvent;
import jain.protocol.ss7.tcap.component.Operation;

import java.util.LinkedList;

import junit.framework.TestCase;

import org.apache.log4j.Logger;

import com.agnity.win.asngenerated.BillingID;
import com.agnity.win.asngenerated.MINType;
import com.agnity.win.asngenerated.MSCID;
import com.agnity.win.asngenerated.MSID;
import com.agnity.win.asngenerated.MobileIdentificationNumber;
import com.agnity.win.asngenerated.TDisconnect;
import com.agnity.win.asngenerated.TimeOfDay;
import com.agnity.win.asngenerated.TriggerType;
import com.agnity.win.datatypes.NonASNActionCode;
import com.agnity.win.datatypes.NonASNBillingID;
import com.agnity.win.datatypes.NonASNMINType;
import com.agnity.win.datatypes.NonASNMSCID;
import com.agnity.win.enumdata.ActionCodeEnum;
import com.agnity.win.operations.WinOpCodes;
import com.agnity.win.operations.WinOperationsCoding;
import com.agnity.win.util.Util;

public class TestTDisconnectOp extends TestCase {

	

	private static Logger logger = Logger.getLogger(TestTDisconnectOp.class);

	/*
	 * Test encoding of TDisconnectt Request
	 */
	public void testEncodeTDisconnectt() throws Exception {

		LinkedList<byte[]> encode = null;

		try {
			LinkedList<Object> objLL = new LinkedList<Object>();
			LinkedList<String> opCode = new LinkedList<String>();

			objLL.add(getTDisconnecttMessage());
			opCode.add(WinOpCodes.T_DISC);

			encode = WinOperationsCoding.encodeOperations(objLL, opCode, true);
			System.out.println("TDisconnectt: "
					+ Util.formatBytes(encode.get(0)));
		} catch (Exception e) {
			System.out.println("exception : " + e.getMessage());
			assertFalse(true);
			e.printStackTrace();
		}
	}

	
	/*
	 * Test decoding of TDisconnectt Request
	 */
	public void testDecodeTDisconnectt() throws Exception {
		LinkedList<byte[]> encode = null;
		LinkedList<Object> objLL = new LinkedList<Object>();
		LinkedList<String> opCode = new LinkedList<String>();

		try {
			objLL.add(getTDisconnecttMessage());
			opCode.add(WinOpCodes.T_DISC);

			encode = WinOperationsCoding.encodeOperations(objLL, opCode, true);
			System.out.println("TDisconnectt: "
					+ Util.formatBytes(encode.get(0)));
		} catch (Exception e) {
			System.out.println("exception : " + e.getMessage());
			assertFalse(true);
			e.printStackTrace();
		}

		try {
			InvokeIndEvent rie = new InvokeIndEvent(new Object());
			byte[] orreqOpCode = { WinOpCodes.T_DISC_BYTE };
			Operation op = new Operation(Operation.OPERATIONTYPE_LOCAL,
					orreqOpCode);
			rie.setOperation(op);
			TDisconnect anl = (TDisconnect) WinOperationsCoding
					.decodeOperation(encode.get(0), rie);
			System.out.println("TDisconnect : " + anl.toString());
			System.out.println("TDisconnect timeofday: " + anl.getValue().getTimeOfDay());
			assertNotNull(anl.getValue().getMscid());
			// issue
			// assertNotNull(anl.getValue().getBillingID());
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

	
	public byte[] getMSCID() throws Exception {

		byte[] b = NonASNMSCID.encodeMSCID((short) 23, 45);
		return b;
	}

	public byte[] getBillingId() throws Exception {

		byte[] b = NonASNBillingID.encodeBillingID((short) 4, (short) 3, 2,
				(short) 6);
		return b;
	}


	private TDisconnect getTDisconnecttMessage() {

		TDisconnect req = new TDisconnect();

		try {
			TDisconnect.TDisconnectSequenceType reqType = new TDisconnect.TDisconnectSequenceType();

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

			// min
						MINType mt = new MINType();
						mt.setValue(NonASNMINType.encodeMINType("3476409876"));
						MobileIdentificationNumber min = new MobileIdentificationNumber();
						min.setValue(mt);
						
						MSID msid = new MSID();
						msid.selectMobileIdentificationNumber(min);
						reqType.setMsid(msid);

			req.setValue(reqType);
		} catch (Exception ex) {
			System.out.println("Exception in creating TDisconnectt message");
			ex.printStackTrace();
		}

		return req;
	}
}
