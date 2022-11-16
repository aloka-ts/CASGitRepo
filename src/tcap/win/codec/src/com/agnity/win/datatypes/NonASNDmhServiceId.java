package com.agnity.win.datatypes;

import java.io.Serializable;
import java.util.LinkedList;

import org.apache.log4j.Logger;

import com.agnity.win.asngenerated.DMH_ServiceID;
import com.agnity.win.exceptions.InvalidInputException;
import com.agnity.win.util.Util;

/*
 * This class provides encode and decode methods for Digits
 * as per definition given in N.S0018 section 6.5.2.e1
 * Refer Section 7.2.14 of Functional_Spec_FlexyCharge_Platform_V0.2
 * for details
 */
public class NonASNDmhServiceId implements Serializable{

	private static Logger logger = Logger.getLogger(NonASNDmhServiceId.class);

	/*
	 * LinkedList of MarketId
	 */
	LinkedList<Short> marketId;

	/*
	 * LinkedList of Market Segment ID
	 */
	LinkedList<Byte> marketSegId;

	/*
	 * LinkedList of DMH_ServiceID value
	 */
	LinkedList<Short> dmhServiceId;

	/**
	 * This function will decode DMH_ServiceID
	 * 
	 * @param data
	 * @return decoded NonAnsDmhServiceId
	 * @throws InvalidInputException
	 */
	public static NonASNDmhServiceId decodeDmhServiceId(byte[] data)
			throws InvalidInputException {
		if (logger.isDebugEnabled())
			logger.debug("decodeDmhServiceId: Input--> data:"
					+ Util.formatBytes(data));

		if (data == null) {
			logger.error("decodeDigits: InvalidInputException(Input data(bytes) not present or null)");
			throw new InvalidInputException("Input data(bytes) not present or null");
		}

		NonASNDmhServiceId dmhSvcId = new NonASNDmhServiceId();
		dmhSvcId.marketId = new LinkedList<Short>();
		dmhSvcId.marketSegId = new LinkedList<Byte>();
		dmhSvcId.dmhServiceId = new LinkedList<Short>();

		int offset = 0;
		int dataLen = data.length;
		short mktId, svcId = 0;

		while (offset < dataLen) {

			if ((mktId = getShortFromBytes(data, offset)) != -1) {
				offset += 2;
				dmhSvcId.marketId.add(mktId);
			} else {
				if (logger.isDebugEnabled()) {
					logger.debug("decodeDmhServiceId: Length not multiple of 5 octet, Offset:"
							+ offset + " length:" + dataLen);
				}
				break;
			}

			if (offset < dataLen) {
				dmhSvcId.marketSegId.add(data[offset]);
				offset += 1;
			}

			if ((svcId = getShortFromBytes(data, offset)) != -1) {
				offset += 2;
				dmhSvcId.dmhServiceId.add(svcId);
			} else {
				if (logger.isDebugEnabled()) {
					logger.debug("decodeDmhServiceId: Length not multiple of 5 octet, Offset:"
							+ offset + " length:" + dataLen);
				}
				break;
			}
		}

		if (logger.isDebugEnabled())
			logger.debug("decodeDmhServiceId: Output<--" + dmhSvcId.toString());
		logger.info("decodeDmhServiceId");

		return dmhSvcId;
	}

	/**
	 * This function will encode DMH_ServiceID for multiple marketID and service
	 * ID This function takes linkedList of tuple <MarketId, MarketSegmentId,
	 * ServiceId>
	 * 
	 * @param LinkedList
	 *            <Short> - Market Id
	 * @param LinkedList
	 *            <Byte> - Market Segment Id
	 * @param LinkedList
	 *            <Short> svcId - Service ID
	 * @return byte[]
	 * @throws InvalidInputException
	 */
	public static byte[] encodeDmhServiceId(LinkedList<Short> mktIdList,
			LinkedList<Byte> mktSegIdList, LinkedList<Short> svcIdList)
			throws InvalidInputException {

		logger.info("encodeDmhServiceId");

		if (mktIdList == null || mktSegIdList == null || svcIdList == null) {
			logger.error("encodeDmhServiceId: InvalidInputException(Input data(bytes) not present or null)");
			throw new InvalidInputException("Input data(bytes) not present or null");
		}

		// size of each linklist must be same
		int linkLstSize = mktIdList.size();
		if (mktSegIdList.size() != linkLstSize
				|| svcIdList.size() != linkLstSize) {
			logger.error("encodeDmhServiceId: Number of element in MarketId, MarketSegmentId and SVCID Must be same");
			throw new InvalidInputException("Input data(bytes) not present or null");
		}

		byte[] myParams = new byte[linkLstSize * 5];
		int offset = 0;

		for (int i = 0; i < linkLstSize; ++i) {
			short val = mktIdList.get(i);

			// Market Id
			myParams[offset++] = (byte) ((val >> 8) & 0x00FF);
			myParams[offset++] = (byte) (val & 0x00FF);

			// Market Segment id
			myParams[offset++] = (byte) mktSegIdList.get(i);

			// Service ID
			val = svcIdList.get(i);
			myParams[offset++] = (byte) ((val >> 8) & 0x00FF);
			myParams[offset++] = (byte) (val & 0x00FF);
		}
		if (logger.isDebugEnabled())
			logger.debug("encodeDmhServiceId: Encoded encodeDmhServiceId: "
					+ Util.formatBytes(myParams));
		logger.info("encodeDmhServiceId:Exit");

		return myParams;
	}
	
	
	/**
	 * This function will encode Non ASN DmhServiceId to ASN DmhServiceId object
	 * @param nonASNDmhServiceId
	 * @return DmhServiceId
	 * @throws InvalidInputException
	 */
	public static DMH_ServiceID encodeDmhServiceId(NonASNDmhServiceId nonASNDmhServiceId)
			throws InvalidInputException {
		
		logger.info("Before encodeDmhServiceId : nonASN to ASN");
		DMH_ServiceID DmhServiceId = new DMH_ServiceID();
		DmhServiceId.setValue(encodeDmhServiceId(nonASNDmhServiceId.getMarketId(),nonASNDmhServiceId.getMarketSegId(),
				nonASNDmhServiceId.getDmhServiceId()));
		logger.info("After encodeDmhServiceId : nonASN to ASN");
		return DmhServiceId;
	}

	/*
	 * This method returns short from byte array
	 */
	private static short getShortFromBytes(byte[] data, int offset) {
		short val = -1;
		if (offset + 2 <= data.length) {
			val = (short) ((short) ((data[offset] & 0xff) << 8) | (data[offset + 1] & 0xff));
		}
		return val;
	}

	public LinkedList<Short> getMarketId() {
		return marketId;
	}

	public void setMarketId(LinkedList<Short> marketId) {
		this.marketId = marketId;
	}

	public LinkedList<Byte> getMarketSegId() {
		return marketSegId;
	}

	public void setMarketSegId(LinkedList<Byte> marketSegId) {
		this.marketSegId = marketSegId;
	}

	public LinkedList<Short> getDmhServiceId() {
		return dmhServiceId;
	}

	public void setDmhServiceId(LinkedList<Short> dmhServiceId) {
		this.dmhServiceId = dmhServiceId;
	}

	public String toString() {
		String obj = null;
		for (int i = 0; i < marketId.size(); ++i) {
			obj += "Element " + (i + 1) + ": " + "MarketId:" + marketId.get(i)
					+ " MarketSegmentId: " + marketSegId.get(i) + " ServieId:"
					+ dmhServiceId.get(i) + "\n";
		}

		return obj;
	}

}
