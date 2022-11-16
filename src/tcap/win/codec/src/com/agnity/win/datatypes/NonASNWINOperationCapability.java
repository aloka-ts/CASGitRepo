package com.agnity.win.datatypes;

import java.util.LinkedList;

import org.apache.log4j.Logger;

import com.agnity.win.asngenerated.WINOperationsCapability;
import com.agnity.win.enumdata.CCDIREnum;
import com.agnity.win.enumdata.CircuitSwitchedDataEnum;
import com.agnity.win.enumdata.ConnectResourceEnum;
import com.agnity.win.enumdata.PositionRequestEnum;
import com.agnity.win.exceptions.InvalidInputException;
import com.agnity.win.util.Util;

/*
 * This class provides encode and decode methods for NonASNTriggerType
 *  as per definition given in TIA/EIA/IS-771, section 6.5.2.dj.
 *   @author Supriya Jain
 */
public class NonASNWINOperationCapability {
	private static Logger logger = Logger
			.getLogger(NonASNWINOperationCapability.class);
	LinkedList<CircuitSwitchedDataEnum> circuitSwitchedData;

	LinkedList<CCDIREnum> cCDIR;

	LinkedList<PositionRequestEnum> positionRequest;

	LinkedList<ConnectResourceEnum> connectResource;

	/**
	 * This function will encode NonASNWINOperationCapability as per
	 * specification TIA/EIA/IS-771, section 6.5.2.dj.
	 * 
	 * @param list
	 *            of CircuitSwitchedDataEnum,
	 *            CCDIREnum,PositionRequestEnum,ConnectResourceEnum
	 * @return encoded data byte[]
	 * @throws InvalidInputException
	 */
	public static byte[] encodeWINOperationCapability(
			LinkedList<CircuitSwitchedDataEnum> circuitSwitchedDataEnum,
			LinkedList<CCDIREnum> cCDIREnum,
			LinkedList<PositionRequestEnum> positionRequestEnum,
			LinkedList<ConnectResourceEnum> connectResourceEnum)
			throws InvalidInputException {
		logger.info("encodeWINOperationCapability");
		if (circuitSwitchedDataEnum == null) {
			logger.error("encodeWINOperationCapability: InvalidInputException(circuitSwitchedDataEnum is null)");
			throw new InvalidInputException("circuitSwitchedDataEnum is null");
		}
		if (cCDIREnum == null) {
			logger.error("encodeWINOperationCapability: InvalidInputException(cCDIREnum is null)");
			throw new InvalidInputException("cCDIREnum is null");
		}
		if (positionRequestEnum == null) {
			logger.error("encodeWINOperationCapability: InvalidInputException(positionRequestEnum is null)");
			throw new InvalidInputException("positionRequestEnum is null");
		}
		if (connectResourceEnum == null) {
			logger.error("encodeWINOperationCapability: InvalidInputException(connectResourceEnum is null)");
			throw new InvalidInputException("connectResourceEnum is null");
		}
		byte[] myParams = new byte[circuitSwitchedDataEnum.size()];
		for (int i = 0, n = circuitSwitchedDataEnum.size(); i < n; i++) {
			myParams[i] = (byte) (((circuitSwitchedDataEnum.get(i).getCode() & 0x01) << 3)
					| ((positionRequestEnum.get(i).getCode() & 0x01) << 2)
					| ((connectResourceEnum.get(i).getCode()) & 0x01) | ((cCDIREnum
					.get(i).getCode() & 0x01) << 1));
		}
		if (logger.isDebugEnabled())
			logger.debug("encodeWINOperationCapability: Encoded : "
					+ Util.formatBytes(myParams));
		logger.info("encodeWINOperationCapability");
		return myParams;
	}

	/**
	 * This function will encode Non ASN WINOperationCapability to ASN WINOperationsCapability object
	 * @param nonASNWINOperationCapability
	 * @return WINOperationCapability
	 * @throws InvalidInputException
	 */
	public static WINOperationsCapability encodeWINOperationCapability(NonASNWINOperationCapability nonASNWINOperationCapability)
			throws InvalidInputException {
		
		logger.info("Before encodeWINOperationCapability : nonASN to ASN");
		WINOperationsCapability winOperationCapability = new WINOperationsCapability();
		winOperationCapability.setValue(encodeWINOperationCapability(nonASNWINOperationCapability.getCircuitSwitchedData(),nonASNWINOperationCapability.getCCDIR(),
				nonASNWINOperationCapability.getPositionRequest(),nonASNWINOperationCapability.getConnectResource()));
		logger.info("After encodeWINOperationCapability : nonASN to ASN");
		return winOperationCapability;
	}
	
	/**
	 * This function will decode calling NonASNWINOperationCapability.
	 * 
	 * @param data
	 * @return object of NonASNWINOperationCapability
	 * @throws InvalidInputException
	 */
	public static NonASNWINOperationCapability decodeWINOperationCapability(
			byte[] data) throws InvalidInputException {
		if (logger.isDebugEnabled())
			logger.debug("decodeWINOperationCapability: Input--> data:"
					+ Util.formatBytes(data));
		if (data == null || data.length==0) {
			logger.error("decodeWINOperationCapability: InvalidInputException(Input data(bytes) not present or null)");
			throw new InvalidInputException("Input data(bytes) not present or null");
		}
		NonASNWINOperationCapability winOperationCapability = new NonASNWINOperationCapability();
		winOperationCapability.connectResource = new LinkedList<ConnectResourceEnum>();
		winOperationCapability.cCDIR = new LinkedList<CCDIREnum>();
		winOperationCapability.circuitSwitchedData = new LinkedList<CircuitSwitchedDataEnum>();
		winOperationCapability.positionRequest = new LinkedList<PositionRequestEnum>();

		// every byte represents an WIN Operation Capability ,decoding gives
		// list of WIN Operation Capabilities
		for (int i = 0; i < data.length; i++) {
			winOperationCapability.connectResource.add(ConnectResourceEnum
					.fromInt(data[i] & 0x1));
			winOperationCapability.cCDIR.add(CCDIREnum
					.fromInt(data[i] >> 1 & 0x1));
			winOperationCapability.circuitSwitchedData
					.add(CircuitSwitchedDataEnum.fromInt(data[i] >> 3 & 0xFF));
			winOperationCapability.positionRequest.add(PositionRequestEnum
					.fromInt(data[i] >> 2 & 0x1));

		}
		if (logger.isDebugEnabled())
			logger.debug("decodeWINOperationCapability: Output<--"
					+ winOperationCapability.toString());
		logger.info("decodeWINOperationCapability");
		return winOperationCapability;
	}

	public LinkedList<CircuitSwitchedDataEnum> getCircuitSwitchedData() {
		return circuitSwitchedData;
	}

	public LinkedList<CCDIREnum> getCCDIR() {
		return cCDIR;
	}

	public LinkedList<PositionRequestEnum> getPositionRequest() {
		return positionRequest;
	}

	public LinkedList<ConnectResourceEnum> getConnectResource() {
		return connectResource;
	}

	public void setCircuitSwitchedData(
			LinkedList<CircuitSwitchedDataEnum> circuitSwitchedData) {
		this.circuitSwitchedData = circuitSwitchedData;
	}

	public void setCCDIR(LinkedList<CCDIREnum> cCDIR) {
		this.cCDIR = cCDIR;
	}

	public void setPositionRequest(
			LinkedList<PositionRequestEnum> positionRequest) {
		this.positionRequest = positionRequest;
	}

	public void setConnectResource(
			LinkedList<ConnectResourceEnum> connectResource) {
		this.connectResource = connectResource;
	}

	public String toString() {

		String obj = "circuitSwitchedData:" + circuitSwitchedData + " ,cCDIR:"
				+ cCDIR + " ,positionRequest:" + positionRequest
				+ " ,connectResource:" + connectResource;
		return obj;
	}

}
