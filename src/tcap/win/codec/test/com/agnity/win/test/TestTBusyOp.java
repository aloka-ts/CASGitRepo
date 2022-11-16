package com.agnity.win.test;

import java.util.LinkedList;

import junit.framework.TestCase;

import org.apache.log4j.Logger;

import com.agnity.win.asngenerated.BillingID;
import com.agnity.win.asngenerated.MSCID;
import com.agnity.win.asngenerated.TBusy;
import com.agnity.win.asngenerated.TransactionCapability;
import com.agnity.win.asngenerated.TriggerCapability;
import com.agnity.win.asngenerated.TriggerType;
import com.agnity.win.asngenerated.WINCapability;
import com.agnity.win.asngenerated.WINOperationsCapability;
import com.agnity.win.datatypes.NonASNBillingID;
import com.agnity.win.datatypes.NonASNMSCID;
import com.agnity.win.datatypes.NonASNTriggerCapability;
import com.agnity.win.datatypes.NonASNWINOperationCapability;
import com.agnity.win.enumdata.CCDIREnum;
import com.agnity.win.enumdata.CircuitSwitchedDataEnum;
import com.agnity.win.enumdata.ConnectResourceEnum;
import com.agnity.win.enumdata.PositionRequestEnum;
import com.agnity.win.operations.WinOpCodes;
import com.agnity.win.operations.WinOperationsCoding;
import com.agnity.win.util.Util;

public class TestTBusyOp extends TestCase {

	private static Logger logger = Logger
			.getLogger(TestTBusyOp.class);

	/*
	/*
	 * Test encoding of T_BUSY
	 */
	public void testEncodeTBusy() throws Exception {

		LinkedList<byte[]> encode = null;

		try {
			LinkedList<Object> objLL = new LinkedList<Object>();
			LinkedList<String> opCode = new LinkedList<String>();

			objLL.add(getTBusyMessage());
			opCode.add(WinOpCodes.T_BUSY);

			encode = WinOperationsCoding.encodeOperations(objLL, opCode, true);
			System.out.println("T_BUSY: "
					+ Util.formatBytes(encode.get(0)));
		} catch (Exception e) {
			assertFalse(true);
			e.printStackTrace();
		}
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

	private TBusy getTBusyMessage() {

		TBusy req = new TBusy();

		try {
			TBusy.TBusySequenceType reqType = new TBusy.TBusySequenceType();

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
			
			req.setValue(reqType);
		} catch (Exception ex) {
			System.out.println("Exception in creating tbusy message");
			ex.printStackTrace();
		}

		return req;
	}

}
