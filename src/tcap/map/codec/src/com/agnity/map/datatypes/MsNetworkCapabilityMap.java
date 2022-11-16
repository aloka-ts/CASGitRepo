/****
 * Copyright (c) 2013 Agnity, Inc. All rights reserved.
 * 
 * This is proprietary source code of Agnity, Inc.
 * 
 * Agnity, Inc. retains all intellectual property rights associated with this
 * source code. Use is subject to license terms.
 * 
 * This source code contains trade secrets owned by Agnity, Inc. Confidentiality
 * of this computer program must be maintained at all times, unless explicitly
 * authorized by Agnity, Inc.
 ****/


package com.agnity.map.datatypes;

import org.apache.log4j.Logger;

import com.agnity.map.enumdata.EmmCapabilityMapEnum;
import com.agnity.map.enumdata.EpcCapabilityMapEnum;
import com.agnity.map.enumdata.GeaAlgorithmMapEnum;
import com.agnity.map.enumdata.IsrSupportMapEnum;
import com.agnity.map.enumdata.LcsVaCapabilityMapEnum;
import com.agnity.map.enumdata.NfCapabilityMapEnum;
import com.agnity.map.enumdata.PfcFeatureModeMapEnum;
import com.agnity.map.enumdata.PsInterRatHoFromGeranToEutranS1ModeCapMapEnum;
import com.agnity.map.enumdata.PsInterRatHoFromGeranToUtranIuModeCapMapEnum;
import com.agnity.map.enumdata.RevisionLevelIndicatorMapEnum;
import com.agnity.map.enumdata.SmCapabilityMapEnum;
import com.agnity.map.enumdata.SoLsaSupportMapEnum;
import com.agnity.map.enumdata.SrvccToGeranOrUtranCapMapEnum;
import com.agnity.map.enumdata.SsScreeningIndicatorMapEnum;
import com.agnity.map.enumdata.Ucs2TreatmentMapEnum;
import com.agnity.map.exceptions.InvalidInputException;
import com.agnity.map.util.Util;

/**
 * 3GPP TS 24.008 V9.3.0 (2010-06), 10.5.5.12 MS network capability
 *  
 * @author sanjay
 *
 */
public class MsNetworkCapabilityMap {
	private GeaAlgorithmMapEnum gea1Algo;
	private SmCapabilityMapEnum smCapViaCsDomain;
	private SmCapabilityMapEnum smCapViaPsDomain;
	private Ucs2TreatmentMapEnum cs2Support;
	private SsScreeningIndicatorMapEnum screeningInd;
	private SoLsaSupportMapEnum solsaCap;
	private RevisionLevelIndicatorMapEnum revInd;
	private PfcFeatureModeMapEnum pfcMode;
	private GeaAlgorithmMapEnum extGea2Algo;
	private GeaAlgorithmMapEnum extGea3Algo;
	private GeaAlgorithmMapEnum extGea4Algo;
	private GeaAlgorithmMapEnum extGea5Algo;
	private GeaAlgorithmMapEnum extGea6Algo;
	private GeaAlgorithmMapEnum extGea7Algo;
	private LcsVaCapabilityMapEnum lcsVaCap;
	private PsInterRatHoFromGeranToUtranIuModeCapMapEnum psIu;
	private PsInterRatHoFromGeranToEutranS1ModeCapMapEnum psS1;
	private EmmCapabilityMapEnum emmCap;
	private IsrSupportMapEnum isr;
	private SrvccToGeranOrUtranCapMapEnum srvccCap;
	private EpcCapabilityMapEnum epcCap;
	private NfCapabilityMapEnum nfCap;
	
	private static Logger logger = Logger.getLogger(MsNetworkCapabilityMap.class);
	
	public void setGea1AlgorithmStatus(GeaAlgorithmMapEnum status) {
		this.gea1Algo = status;
	}
	
	public GeaAlgorithmMapEnum getGea1AlgorithmStatus() {
		return this.gea1Algo;
	}
	
	public void setSmCapabilityViaDedicatedCh(SmCapabilityMapEnum status)  {
		this.smCapViaCsDomain = status;
	}
	
	public SmCapabilityMapEnum getSmCapabilityViaDedicatedCh() {
		return this.smCapViaCsDomain;
	}
	
	public void setSmCapabilityViaGprsCh(SmCapabilityMapEnum status) {
		this.smCapViaPsDomain = status;
	}
	
	public SmCapabilityMapEnum getSmCapabilityViaGprsCh() {
		return this.smCapViaPsDomain;
	}
	
	public void setUcs2Support(Ucs2TreatmentMapEnum status) {
		this.cs2Support = status;
	}
	
	public Ucs2TreatmentMapEnum getUcs2Support() {
		return this.cs2Support;
	}
	
	public void setSsScreeningIndicator(SsScreeningIndicatorMapEnum status) {
		this.screeningInd = status;
	}
	
	public SsScreeningIndicatorMapEnum getSsScreeningIndicator() {
		return this.screeningInd;
	}
	
	public void setSoLsaCapability(SoLsaSupportMapEnum status) {
		this.solsaCap = status;
	}
	
	public SoLsaSupportMapEnum getSolsaCapability() {
		return this.solsaCap;
	}
	
	public void setRevisionLevelIndicator(RevisionLevelIndicatorMapEnum status) {
		this.revInd = status;
	}
	
	public RevisionLevelIndicatorMapEnum getRevisionLevelIndicator() {
		return this.revInd;
	}
	
	public void setPfcFeatureMode(PfcFeatureModeMapEnum mode) {
		this.pfcMode = mode;
	}
	
	public PfcFeatureModeMapEnum getPfcFeatureMode() {
		return this.pfcMode;
	}
	
	public GeaAlgorithmMapEnum getExtGea2AlgorithmStatus() {
		return this.extGea2Algo;
	}
	
	public void setExtGea2AlgorithmStatus(GeaAlgorithmMapEnum status){
		this.extGea2Algo = status; 
	}
	
	public GeaAlgorithmMapEnum getExtGea3AlgorithmStatus() {
		return this.extGea3Algo;
	}
	
	public void setExtGea3AlgorithmStatus(GeaAlgorithmMapEnum status){
		this.extGea3Algo = status; 
	}

	public GeaAlgorithmMapEnum getExtGea4AlgorithmStatus() {
		return this.extGea4Algo;
	}
	
	public void setExtGea4AlgorithmStatus(GeaAlgorithmMapEnum status){
		this.extGea4Algo = status; 
	}

	public GeaAlgorithmMapEnum getExtGea5AlgorithmStatus() {
		return this.extGea5Algo;
	}
	
	public void setExtGea5AlgorithmStatus(GeaAlgorithmMapEnum status){
		this.extGea5Algo = status; 
	}

	public GeaAlgorithmMapEnum getExtGea6AlgorithmStatus() {
		return this.extGea6Algo;
	}
	
	public void setExtGea6AlgorithmStatus(GeaAlgorithmMapEnum status){
		this.extGea6Algo = status; 
	}

	public GeaAlgorithmMapEnum getExtGea7AlgorithmStatus() {
		return this.extGea7Algo;
	}
	
	public void setExtGea7AlgorithmStatus(GeaAlgorithmMapEnum status){
		this.extGea7Algo = status; 
	}
	
	
	public void setLcsVaCapability(LcsVaCapabilityMapEnum status) {
		this.lcsVaCap = status;
	}
	
	public LcsVaCapabilityMapEnum getLcsVaCapability() {
		return this.lcsVaCap;
	}

	public void setPsInterRatHoFromGeranToUtranIuMode (
			PsInterRatHoFromGeranToUtranIuModeCapMapEnum status) {
	
		this.psIu = status;
	}
	
	public PsInterRatHoFromGeranToUtranIuModeCapMapEnum getPsInterRatHoFromGeranToUtranIuMode() {
		return this.psIu;
	}

	public void setPsInterRatHoFromGeranToEutranS1Mode (
			PsInterRatHoFromGeranToEutranS1ModeCapMapEnum status){
		this.psS1 =  status;
	}
	
	public PsInterRatHoFromGeranToEutranS1ModeCapMapEnum getPsInterRatHoFromGeranToEutranS1Mode() {
		return this.psS1;
	}
	
	public void setEmmCombinedProceduresCapability(EmmCapabilityMapEnum status) {
		this.emmCap = status;
	}
	
	public EmmCapabilityMapEnum getEmmCombinedProceduresCapability() {
		return this.emmCap;
	}
	
	public void setIsrSupportStatus(IsrSupportMapEnum status){
		this.isr = status;
	}
	
	public IsrSupportMapEnum getIsrSupportStatus() {
		return this.isr;
	}
	
	public void setSrvccToGeranOrUtranCapability(SrvccToGeranOrUtranCapMapEnum status) {
		this.srvccCap = status;
	}
	
	public SrvccToGeranOrUtranCapMapEnum getSrvccToGeranOrUtranCapability() {
		return this.srvccCap;
	}
	
	public void setEpcCapability(EpcCapabilityMapEnum status) {
		this.epcCap = status;
	}
	
	public EpcCapabilityMapEnum getEpcCapability() {
		return this.epcCap;
	}
	
	public void setNfCapability(NfCapabilityMapEnum status){
		this.nfCap = status;
	}
	
	public NfCapabilityMapEnum getNfCapability() {
		return this.nfCap;
	}

	public static byte[] encode(MsNetworkCapabilityMap msNwObj)
			throws InvalidInputException {
		byte[] encData = null;
		logger.info("MsNetworkCapabilityMap:encode Entr");
		
		if(logger.isDebugEnabled()) {
			logger.debug("Encoding the object "+ msNwObj);
		}
		
		byte b0 = (byte)((msNwObj.getGea1AlgorithmStatus().getCode()&0x01) << 7 | 
				(msNwObj.getSmCapabilityViaDedicatedCh().getCode()&0x01) << 6 |
				(msNwObj.getSmCapabilityViaGprsCh().getCode()&0x01) << 5 |
				(msNwObj.getUcs2Support().getCode()&0x01) << 4 |
				(msNwObj.getSsScreeningIndicator().getCode()&0x03) << 2 |
				(msNwObj.getSolsaCapability().getCode()&0x01) << 1 |
				(msNwObj.getRevisionLevelIndicator().getCode()&0x01));
		
		byte b1 = (byte)((msNwObj.getPfcFeatureMode().getCode()&0x01)<<7|
				(msNwObj.getExtGea2AlgorithmStatus().getCode()&0x01)<<6 |
				(msNwObj.getExtGea3AlgorithmStatus().getCode()&0x01)<<5 |
				(msNwObj.getExtGea4AlgorithmStatus().getCode()&0x01)<<4 |
				(msNwObj.getExtGea5AlgorithmStatus().getCode()&0x01)<<3 |
				(msNwObj.getExtGea6AlgorithmStatus().getCode()&0x01)<<2 |
				(msNwObj.getExtGea7AlgorithmStatus().getCode()&0x01)<<1 |
				msNwObj.getLcsVaCapability().getCode()&0x01);
				
		byte b2 = (byte)((msNwObj.getPsInterRatHoFromGeranToUtranIuMode().getCode()&0x01)<<7 |
				(msNwObj.getPsInterRatHoFromGeranToEutranS1Mode().getCode()&0x01)<<6 |
				(msNwObj.getEmmCombinedProceduresCapability().getCode()&0x01)<<5 |
				(msNwObj.getIsrSupportStatus().getCode()&0x01)<<4 |
				(msNwObj.getSrvccToGeranOrUtranCapability().getCode()&0x01)<<3 |
				(msNwObj.getEpcCapability().getCode()&0x01)<<2 |
				(msNwObj.getNfCapability().getCode()&0x01)<<1 );
		
		encData = new byte[]{b0, b1, b2};
		
		if(logger.isDebugEnabled()){
			logger.debug("Encoded binary data = "+Util.formatBytes(encData));
		}
		logger.info("MsNetworkCapabilityMap:encode Exit");
		
		return encData;
	}
	
	public static MsNetworkCapabilityMap decode(byte[] encData) 
			throws InvalidInputException {
		if(encData == null){
			logger.error("Data to decode is null");
			throw new InvalidInputException("MsNetworkCapabilityMap:decode data to decode is null");
		}
		if(encData.length < 1 || encData.length > 8) {
			logger.error("Invalid length of encode data, expected length is 1 to 8");
			throw new InvalidInputException("MsNetworkCapabilityMap:decode" +
					"Invalid length of encode data, expected length is 1 to 8");
			
		}
		logger.info("MsNetworkCapabilityMap:decode Entr");
		
		if(logger.isDebugEnabled()) {
			logger.debug("binary data to decode is "+Util.formatBytes(encData));
		}
		
		MsNetworkCapabilityMap msNwObj =  new MsNetworkCapabilityMap();
		
		if(encData.length == 1) {
			msNwObj.setGea1AlgorithmStatus( GeaAlgorithmMapEnum.getValue((encData[0]>>7)&0x01) );
			msNwObj.setSmCapabilityViaDedicatedCh( SmCapabilityMapEnum.getValue((encData[0]>>6)&0x01) );
			msNwObj.setSmCapabilityViaGprsCh( SmCapabilityMapEnum.getValue((encData[0]>>5)&0x01) );
			msNwObj.setUcs2Support( Ucs2TreatmentMapEnum.getValue((encData[0]>>4)&0x01) );
			msNwObj.setSsScreeningIndicator(SsScreeningIndicatorMapEnum.getValue((encData[0]>>2)&0x03));
			msNwObj.setSoLsaCapability(SoLsaSupportMapEnum.getValue((encData[0]>>1)&0x01 ));
			msNwObj.setRevisionLevelIndicator(RevisionLevelIndicatorMapEnum.getValue(encData[0]&0x01));
		}
		else if(encData.length == 2) {
			msNwObj.setPfcFeatureMode(PfcFeatureModeMapEnum.getvalue((encData[1]>>7)&0x01));
			msNwObj.setExtGea2AlgorithmStatus(GeaAlgorithmMapEnum.getValue((encData[1]>>6)&0x01));
			msNwObj.setExtGea3AlgorithmStatus(GeaAlgorithmMapEnum.getValue((encData[1]>>5)&0x01));
			msNwObj.setExtGea4AlgorithmStatus(GeaAlgorithmMapEnum.getValue((encData[1]>>4)&0x01));
			msNwObj.setExtGea5AlgorithmStatus(GeaAlgorithmMapEnum.getValue((encData[1]>>3)&0x01));
			msNwObj.setExtGea6AlgorithmStatus(GeaAlgorithmMapEnum.getValue((encData[1]>>2)&0x01));
			msNwObj.setExtGea7AlgorithmStatus(GeaAlgorithmMapEnum.getValue((encData[1]>>1)&0x01));
			msNwObj.setLcsVaCapability(LcsVaCapabilityMapEnum.getValue(encData[1]&0x01));
		}
		else if(encData.length == 3) {
			msNwObj.setPsInterRatHoFromGeranToUtranIuMode(
					PsInterRatHoFromGeranToUtranIuModeCapMapEnum.getValue((encData[2]>>7)&0x01));
			msNwObj.setPsInterRatHoFromGeranToEutranS1Mode(
					PsInterRatHoFromGeranToEutranS1ModeCapMapEnum.getValue((encData[2]>>6)&0x01));
			msNwObj.setEmmCombinedProceduresCapability(
					EmmCapabilityMapEnum.getValue((encData[2]>>5)&0x01));
			msNwObj.setIsrSupportStatus(IsrSupportMapEnum.getValue((encData[2]>>4)&0x01));
			msNwObj.setSrvccToGeranOrUtranCapability(
					SrvccToGeranOrUtranCapMapEnum.getValue((encData[2]>>3)&0x01));
			msNwObj.setEpcCapability(EpcCapabilityMapEnum.getValue((encData[2]>>2)&0x01));
			msNwObj.setNfCapability(NfCapabilityMapEnum.getValue((encData[2]>>1)&0x01));
		}
		
		if(logger.isDebugEnabled()) {
			logger.debug("Binary data decoded to object "+msNwObj);
		}
		logger.info("MsNetworkCapabilityMap:decode Exit");
		return msNwObj;
	}
	
	public String toString() {
		StringBuilder str = new StringBuilder();
		
		str.append("GEA 1 Algorithm = ").append(gea1Algo).append("\n");
		str.append("SM Cap. = ").append(smCapViaCsDomain).append("\n");
		str.append("SM Cap Via CS Domain = ").append(smCapViaCsDomain).append("\n");
		str.append("SM Cap Via PS Domain = ").append(smCapViaPsDomain).append("\n");
		str.append("CS2 Support = ").append(cs2Support).append("\n");
		str.append("SS Screen Indicator = ").append(screeningInd).append("\n");
		str.append("SoLSA Support = ").append(solsaCap).append("\n");
		str.append("Revision Level Indicator = ").append(revInd).append("\n");
		str.append("PFC Feature Mode = ").append(pfcMode).append("\n");
		str.append("Ext Gea2Algorithm = ").append(extGea2Algo).append("\n");
		str.append("Ext Gea3Algorithm = ").append(extGea3Algo).append("\n");
		str.append("Ext Gea4Algorithm = ").append(extGea4Algo).append("\n");
		str.append("Ext Gea5Algorithm = ").append(extGea5Algo).append("\n");
		str.append("Ext Gea6Algorithm = ").append(extGea6Algo).append("\n");
		str.append("Ext Gea7Algorithm = ").append(extGea7Algo).append("\n");
		str.append("LcsVa Capability = ").append(lcsVaCap).append("\n");
		str.append("Ps Inter RAT HO From Geran To Utran Iu ModeCap = ").append(psIu).append("\n");
		str.append("Ps Inter RAT HO From Geran To E-Utran S1 ModeCap = ").append(psS1).append("\n");
		str.append("Emm Capability = ").append(emmCap).append("\n");
		str.append("ISR Support = ").append(isr).append("\n");
		str.append("Srvcc To Geran Or Utran Capability Support = ").append(srvccCap).append("\n");
		str.append("EPC Capability = ").append(epcCap).append("\n");
		str.append("NF Capability = ").append(nfCap).append("\n");
		
		return str.toString();
	}
}
