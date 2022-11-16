package com.genband.isup.datatypes;

import org.apache.log4j.Logger;

import com.genband.isup.enumdata.AdditionalPartyCatNameEnum;
import com.genband.isup.enumdata.IngressTrunkCategoryEnum;
import com.genband.isup.enumdata.MemberStatusIndEnum;
import com.genband.isup.exceptions.InvalidInputException;
import com.genband.isup.util.Util;

/**
 * This class have parameters for Charging Information. Exactly one of the
 * fields will be not null based on ChargingInfoCategory.
 * 
 * @author rarya
 * 
 */
public class DPCInfo {

	private static Logger logger = Logger.getLogger(DPCInfo.class);

	private byte[] dpcCode1;

	private byte[] dpcCode2;

	private byte publicCallInd;
	private byte memberCheckStatusInd;
	private byte noIdCallInd;
	private byte daCallInd;
	private byte originatingIGSInd;
	private byte prefix0088Ind;
	private byte virtualNetworkHandOffInd;
	private byte originatingByPassInd;

	/**
	 * @see MemberStatusIndEnum
	 */
	private MemberStatusIndEnum memberStatusInd;

	/**
	 * @see IngressTrunkCategoryEnum
	 */
	private IngressTrunkCategoryEnum ingressTrunkCategory;

	public byte[] getDPCCode1() {
		return dpcCode1;
	}

	public byte[] getDPCCode2() {
		return dpcCode2;
	}

	public byte getPublicCallIndicator() {
		return publicCallInd;
	}

	public byte getMemberCheckStatusIndicator() {
		return memberCheckStatusInd;
	}

	public byte getNoIdCallIndicator() {
		return noIdCallInd;
	}

	public byte getOriginatingIGSIndicator() {
		return originatingIGSInd;
	}

	public byte getPrefix0088Indicator() {
		return prefix0088Ind;
	}

	public byte getVirtualNetworkHandOffIndicator() {
		return virtualNetworkHandOffInd;
	}

	public byte getOriginatingByPassIndicator() {
		return originatingByPassInd;
	}

	public void setDPCCode1(byte[] code1) {
		this.dpcCode1 = code1;
	}

	public void setDPCCode2(byte[] code2) {
		this.dpcCode2 = code2;
	}

	public void setPublicCallIndicator(byte pubCallInd) {
		this.publicCallInd = pubCallInd;
	}

	public void setMemberCheckStatusIndicator(byte memberChkSts) {
		this.memberCheckStatusInd = memberChkSts;
	}

	public void setNoIdCallIndicator(byte noCallInd) {
		this.noIdCallInd = noCallInd;
	}

	public void setOriginatingIGSIndicator(byte origIGSInd) {
		this.originatingIGSInd = origIGSInd;
	}

	public void setPrefix0088Indicator(byte prefixInd) {
		this.prefix0088Ind = prefixInd;
	}

	public void setVirtualNetworkHandOffIndicator(byte virtualNwInd) {
		this.virtualNetworkHandOffInd = virtualNwInd;
	}

	public void setOriginatingByPassIndicator(byte origByPassInd) {
		this.originatingByPassInd = origByPassInd;
	}

	public void setDACallInd(byte dACallInd) {
		this.daCallInd = dACallInd;
	}

	public byte getDACallInd() {
		return daCallInd;
	}

	public void setMemberStatusInd(MemberStatusIndEnum memberStatusInd) {
		this.memberStatusInd = memberStatusInd;
	}

	public MemberStatusIndEnum getMemberStatusInd() {
		return memberStatusInd;
	}

	public void setIngressTrunkCategory(
			IngressTrunkCategoryEnum ingressTrunkCategory) {
		this.ingressTrunkCategory = ingressTrunkCategory;
	}

	public IngressTrunkCategoryEnum getIngressTrunkCategory() {
		return ingressTrunkCategory;
	}

	/**
	 * This function will encode DPCInfo. This parameter is proprietary 
	 * to SBTM. Details are available in PCR# 1405.
	 * @param dpcInfo
	 * @return byte[]
	 * @throws InvalidInputException
	 */
	public static byte[] encodeDPCInfo(DPCInfo dpcInfo)
			throws InvalidInputException {
		logger.info("encodeDpcInfo:Enter");

		byte[] data = null;
		int chargingInfoCat;
		if (dpcInfo.dpcCode1 == null || dpcInfo.dpcCode2 == null) {
			logger
					.error("encodeDpcInfo: InvalidInputException(DpcCode1 or DpcCode2 is null)");
			throw new InvalidInputException("DpcCode1 or DpcCode2 is null");
		}

		data = new byte[6];
		byte[] code1 = null;

		code1 = dpcInfo.getDPCCode1();
		data[0] = code1[0];
		data[1] = code1[1];

		code1 = dpcInfo.getDPCCode2();
		data[2] = code1[0];
		data[3] = code1[1];

		byte field;
		// Encode Member Status Ind
		field = (byte) dpcInfo.getMemberStatusInd().getCode();
		data[4] |= ((field << 4) & 0xF0);

		// ENcode DA Call Indicator
		field = dpcInfo.getDACallInd();
		data[4] |= ((field << 3) & 0x08);

		// Encode NO-ID Call Indicator
		field = dpcInfo.getNoIdCallIndicator();
		data[4] |= ((field << 2) & 04);

		// Encode Member Check Status Ind
		field = dpcInfo.getMemberCheckStatusIndicator();
		data[4] |= ((field << 1) & 02);

		// ENcode Public Call Indicator
		field = dpcInfo.getPublicCallIndicator();
		data[4] |= (field & 01);

		// Encode Ingress Trunk Category Info
		field = (byte) dpcInfo.getIngressTrunkCategory().getCode();
		data[5] |= ((field << 4) & 0xF0);

		// ENcode Originating by-pass Indicator
		field = dpcInfo.getOriginatingByPassIndicator();
		data[5] |= ((field << 3) & 0x08);

		// Encode Virtual network hand-off indicator
		field = dpcInfo.getVirtualNetworkHandOffIndicator();
		data[5] |= ((field << 2) & 04);

		// Encode 0088 Prefix indicator
		field = dpcInfo.getPrefix0088Indicator();
		data[5] |= ((field << 1) & 02);

		// ENcode Public Call Indicator
		field = dpcInfo.getOriginatingIGSIndicator();
		data[5] |= (field & 01);

		if (logger.isDebugEnabled())
			logger.debug("encodeDPCInfo:Encoded DPC Info: "
					+ Util.formatBytes(data));
		logger.info("encodeDPCInfo:Exit");

		return data;
	}

	/**
	 * This function will encode DPCInfo. This parameter is proprietary 
	 * to SBTM. Details are available in PCR# 1405.
	 * @param data
	 * @return DPCInfo
	 * @throws InvalidInputException
	 */
	public static DPCInfo decodeDPCInfo(byte[] data)
			throws InvalidInputException {
		logger.info("decodeDPCInfo:Enter");
		if (logger.isDebugEnabled())
			logger.debug("decodeDPCInfo: Input--> data:"
					+ Util.formatBytes(data));
		if (data == null) {
			logger.error("deocdeDPCInfo: InvalidInputException(data is null)");
			throw new InvalidInputException("data is null");
		}

		if (data.length != 6) {
			logger
					.error("deocdeDPCInfo: InvalidInputException(Length is not equal to 6)");
			throw new InvalidInputException(
					"Invalid Length of Input Buffer, Req: 6 bytes, Actual:"
							+ data.length);
		}

		DPCInfo dpcInfo = new DPCInfo();

		// DPC Code 1
		byte[] dpcCode1 = new byte[2];
		dpcCode1[0] = data[0];
		dpcCode1[1] = data[1];

		dpcInfo.setDPCCode1(dpcCode1);

		// DPC Code 2
		byte[] dpcCode2 = new byte[2];
		dpcCode2[0] = data[2];
		dpcCode2[1] = data[3];

		dpcInfo.setDPCCode2(dpcCode2);

		// Member StatusInd
		dpcInfo.setMemberStatusInd(MemberStatusIndEnum
				.fromInt(data[4] >> 4 & 0x0F));
		dpcInfo.setPublicCallIndicator((byte) (data[4] & 0x01));
		dpcInfo.setMemberCheckStatusIndicator((byte) (data[4] >> 1 & 01));
		dpcInfo.setNoIdCallIndicator((byte) (data[4] >> 2 & 01));
		dpcInfo.setDACallInd((byte) (data[4] >> 3 & 01));

		// Ingress Trunk Category Ind
		dpcInfo.setIngressTrunkCategory(IngressTrunkCategoryEnum
				.fromInt(data[5] >> 4 & 0x0F));
		dpcInfo.setOriginatingIGSIndicator((byte) (data[5] & 0x01));
		dpcInfo.setPrefix0088Indicator((byte) (data[5] >> 1 & 01));
		dpcInfo.setVirtualNetworkHandOffIndicator((byte) (data[5] >> 2 & 01));
		dpcInfo.setOriginatingByPassIndicator((byte) (data[5] >> 3 & 01));

		if (logger.isDebugEnabled())
			logger.debug("deocdeDPCInfo: Output<--" + dpcInfo.toString());
		logger.info("decodeDPCInfo:Exit");

		return dpcInfo;
	}

	public String toString() {

		String obj = "DPC Code1:" + Util.formatBytes(dpcCode1) + ", DPC Code2:"
				+ Util.formatBytes(dpcCode2) + " MemberStatusIndicator: "
				+ memberStatusInd + " Public Call Ind:" + publicCallInd
				+ " MemberCheckStatusInd:" + memberCheckStatusInd
				+ " NoIdCallInd:" + noIdCallInd + " DACallIndicator:"
				+ daCallInd + " OriginatingIGSInd:" + originatingIGSInd
				+ " 0088PrefixInd:" + prefix0088Ind
				+ " Virtual network handoff:" + virtualNetworkHandOffInd
				+ " OriginatingByPass:" + originatingByPassInd
				+ " IngressTrunkCategoryInfo:" + ingressTrunkCategory;
		return obj;
	}

}
