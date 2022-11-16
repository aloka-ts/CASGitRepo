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

import com.agnity.map.enumdata.A51AlgorithmMapEnum;
import com.agnity.map.enumdata.A52AlgorithmMapEnum;
import com.agnity.map.enumdata.A53AlgorithmMapEnum;
import com.agnity.map.enumdata.Cm3OptionsMapEnum;
import com.agnity.map.enumdata.CmspSupportMapEnum;
import com.agnity.map.enumdata.EsIndicatorMapEnum;
import com.agnity.map.enumdata.FrequencyCapabilityMapEnum;
import com.agnity.map.enumdata.LcsVaCapabilityMapEnum;
import com.agnity.map.enumdata.PsCapabilityMapEnum;
import com.agnity.map.enumdata.RevisionLevelMapEnum;
import com.agnity.map.enumdata.RfPowerCapabilityMapEnum;
import com.agnity.map.enumdata.SmCapabilityMapEnum;
import com.agnity.map.enumdata.SoLsaSupportMapEnum;
import com.agnity.map.enumdata.SsScreeningIndicatorMapEnum;
import com.agnity.map.enumdata.Ucs2TreatmentMapEnum;
import com.agnity.map.enumdata.VbsCapabilityMapEnum;
import com.agnity.map.enumdata.VgsCapabilityMapEnum;
import com.agnity.map.exceptions.InvalidInputException;
import com.agnity.map.util.Util;

/**
 * 3GPP TS 24.008 V9.3.0 (2010-06)
 * 
 * Octet   MSB                                            LSB
 *          _________________________________________________
 *         | 8  |  7  |  6  |  5  |  4  |  3  |  2  |  1     |
 *         |____|_____|_____|_____|_____|_____|_____|________|
 *         |sp. | Rev. Lvl. |ESInd| A5/1| RF Pow. Cap        |
 *   e     |____|___________|_____|_____|____________________|
 *         |sp  |PsCap| SS Sc.Ind |SMCap|vbs  |vgcs |fc      |
 *   e+1   |____|_____|_ _________|_____|_____|_____|_ ______| 
 *         |cm3 |sp   |lcsVa|ucs2 |SoLSA|cmsp |A5/3 | A5/2   |
 *   e+2   |____|_____|_cap_|_____|_____|_____|_____|________|
 *  
 * 
 * @author sanjay
 *
 */
public class MsClassMark2Map {
	
	private RevisionLevelMapEnum revisionLevel;
	private EsIndicatorMapEnum esiIndicator;
	private A51AlgorithmMapEnum a51Algo;
	private RfPowerCapabilityMapEnum rfPowCap;
	private PsCapabilityMapEnum psCap;
	private SsScreeningIndicatorMapEnum screenInd;
	private SmCapabilityMapEnum smCap;
	private VbsCapabilityMapEnum vbs;
	private VgsCapabilityMapEnum vgs;
	private FrequencyCapabilityMapEnum fc;
	private Cm3OptionsMapEnum cm3;
	private LcsVaCapabilityMapEnum lcsVa;
	private Ucs2TreatmentMapEnum ucs2;
	private SoLsaSupportMapEnum soLsa;
	private CmspSupportMapEnum cmsp;
	private A53AlgorithmMapEnum a53Algo;
	private A52AlgorithmMapEnum a52Algo;
	
	private static final Logger logger = Logger.getLogger(MsClassMark2Map.class);
	
	private static final int OCTET_SIZE = 3;
	
	public RevisionLevelMapEnum getRevisionLevel() {
		return this.revisionLevel;
	}
	
	public void setRevisionLevel(RevisionLevelMapEnum level) {
		this.revisionLevel = level;
	}
	
	public EsIndicatorMapEnum getEsIndicator() {
		return this.esiIndicator;
	}
	
	public void setEsIndicator(EsIndicatorMapEnum ind) {
		this.esiIndicator = ind;
	}
	
	public A51AlgorithmMapEnum getA51Algorithm() {
		return this.a51Algo;
	}
	
	public void setA51Algorithm(A51AlgorithmMapEnum a51) {
		this.a51Algo = a51;
	}
	
	public RfPowerCapabilityMapEnum getRfPowCapability() {
		return this.rfPowCap;
	}
	
	public void setRfPowCapability(RfPowerCapabilityMapEnum powCap) {
		this.rfPowCap = powCap;
	}
	
	public PsCapabilityMapEnum getPsCapability() {
		return this.psCap;
	}
	
	public void setPsCapability(PsCapabilityMapEnum psCap) {
		this.psCap = psCap;
	}
	
	public SsScreeningIndicatorMapEnum getSsScreeningInd() {
		return this.screenInd;
	}
	
	public void  setSsScreeningInd(SsScreeningIndicatorMapEnum ssInd) {
		this.screenInd = ssInd;
	}
	
	public SmCapabilityMapEnum getSmCapability() {
		return this.smCap;
	}
	
	public void setSmCapability(SmCapabilityMapEnum smCap) {
		this.smCap = smCap;
	}
	
	public VbsCapabilityMapEnum getVbsCapability() {
		return this.vbs;
	}
	
	public void setVbsCapability(VbsCapabilityMapEnum vbsCap) {
		this.vbs = vbsCap;
	}
	
	public VgsCapabilityMapEnum getVgsCapability() {
		return this.vgs;
	}
	
	public void setVgsCapability(VgsCapabilityMapEnum vgsCap) {
		this.vgs = vgsCap;
	}
	
	public FrequencyCapabilityMapEnum getFrequencyCap() {
		return this.fc;
	}
	
	public void setFrequencyCap(FrequencyCapabilityMapEnum fCap) {
		this.fc = fCap;
	}
	
	public Cm3OptionsMapEnum getCm3Options() {
		return this.cm3;
	}
	
	public void setCm3Options(Cm3OptionsMapEnum cm3Opt) {
		this.cm3 = cm3Opt;
	}
	
	public  void setLcsVaCapability(LcsVaCapabilityMapEnum lcsVaCap) {
		this.lcsVa = lcsVaCap;
	}
	
	public LcsVaCapabilityMapEnum getLcsVaCapability() {
		return this.lcsVa;
	}

	public Ucs2TreatmentMapEnum getUcs2Treatment() {
		return this.ucs2;
	}
	
	public void setUcs2Treatment(Ucs2TreatmentMapEnum ucs2){
		this.ucs2 = ucs2;
	}
	
	public SoLsaSupportMapEnum getSoLsaSupport() {
		return this.soLsa;
	}
	
	public void setSoLsaSupport(SoLsaSupportMapEnum solsa) {
		this.soLsa = solsa;
	}
	
	public CmspSupportMapEnum getCmspSupport() {
		return this.cmsp;
	}
	
	public void setCmspSupport(CmspSupportMapEnum cmsp) {
		this.cmsp = cmsp;
	}
	
	public A52AlgorithmMapEnum getA52Algorithm() {
		return this.a52Algo;
	}
	
	public void setA52Algorithm(A52AlgorithmMapEnum a52) {
		this.a52Algo = a52;
	}
	
	public A53AlgorithmMapEnum getA53Algorithm() {
		return this.a53Algo;
	}
	
	public void setA53Algorithm(A53AlgorithmMapEnum a53) {
		this.a53Algo = a53;
	}
	
	
	
	
	
	
	public static byte[] encode(MsClassMark2Map cm) throws InvalidInputException{
		byte[] encData = null;
		logger.info("MsClassMark2Map:encode Entr");
		
		if(logger.isDebugEnabled()){
			logger.debug("Encoding MsClassMark2Map Obj " + cm);
		}

		RevisionLevelMapEnum revisionLevel = cm.getRevisionLevel();
		EsIndicatorMapEnum esiIndicator = cm.getEsIndicator();
		A51AlgorithmMapEnum a51Algo = cm.getA51Algorithm();
		RfPowerCapabilityMapEnum rfPowCap = cm.getRfPowCapability();
		PsCapabilityMapEnum psCap = cm.getPsCapability();
		SsScreeningIndicatorMapEnum screenInd = cm.getSsScreeningInd();
		SmCapabilityMapEnum smCap = cm.getSmCapability();
		VbsCapabilityMapEnum vbs = cm.getVbsCapability();
		VgsCapabilityMapEnum vgs = cm.getVgsCapability();
		FrequencyCapabilityMapEnum fc = cm.getFrequencyCap();
		Cm3OptionsMapEnum cm3 = cm.getCm3Options();
		LcsVaCapabilityMapEnum lcsVa = cm.getLcsVaCapability();
		Ucs2TreatmentMapEnum ucs2 = cm.getUcs2Treatment();
		SoLsaSupportMapEnum soLsa = cm.getSoLsaSupport();
		CmspSupportMapEnum cmsp = cm.getCmspSupport();
		A53AlgorithmMapEnum a53Algo = cm.getA53Algorithm();
		A52AlgorithmMapEnum a52Algo = cm.getA52Algorithm();
		
		byte b0 = (byte)(revisionLevel.getCode()<<5 | 
				esiIndicator.getCode() << 4 |	
				a51Algo.getCode() << 3 | 
				rfPowCap.getCode());
		
		byte b1 = (byte) (psCap.getCode() << 6|
				screenInd.getCode()<<4|
				smCap.getCode()<<3|
				vbs.getCode()<<2|
				vgs.getCode()<<1|
				fc.getCode());
		
		byte b2 = (byte) (cm3.getCode()<<7|
				lcsVa.getCode()<<5|
				ucs2.getCode()<<4|
				soLsa.getCode()<<3|
				cmsp.getCode()<<2|
				a53Algo.getCode()<<1|
				a52Algo.getCode());
				
		encData = new byte[] {b0, b1, b2};
		
		if(logger.isDebugEnabled()){
			logger.debug("MsClassMark2Map Object successfully encoded");
			logger.debug("encoded data "+Util.formatBytes(encData));
			
		}
		logger.info("MsClassMark2Map:encode Exit");

		return encData;
	}
	
	public static MsClassMark2Map decode(byte[] data) throws InvalidInputException {
		logger.info("MsClassMark2Map:decode Entr");
		if(data == null) {
			logger.error("binary data for decoding is null");
			throw new InvalidInputException("MsClassMark2Map:decode data provided for decoding is null");
		}
		
		if( data.length != MsClassMark2Map.OCTET_SIZE) {
			logger.error("binary data provided is of invalid length");
			throw new InvalidInputException("MsClassMark2Map:decode data provided "
					+ "for decoding is of invalid length, expected size is "+MsClassMark2Map.OCTET_SIZE);
		}
		if(logger.isDebugEnabled()) {
			logger.debug("data to decode "+Util.formatBytes(data));
		}
		
		MsClassMark2Map msObject = new MsClassMark2Map();
		msObject.setRevisionLevel(RevisionLevelMapEnum.getValue( (data[0]>>5)&0x03 ));
		msObject.setEsIndicator(EsIndicatorMapEnum.getValue( (data[0]>>4)&0x01 ));
		msObject.setA51Algorithm(A51AlgorithmMapEnum.getValue( (data[0]>>3)&0x01 ));
		msObject.setRfPowCapability(RfPowerCapabilityMapEnum.getValue(data[0]&07));
		
		msObject.setPsCapability(PsCapabilityMapEnum.getValue( (data[1]>>6)&0x01 ));
		msObject.setSsScreeningInd(SsScreeningIndicatorMapEnum.getValue( (data[1]>>4)&0x03 ));
		msObject.setSmCapability(SmCapabilityMapEnum.getValue( (data[0]>>3)&0x01 ));
		msObject.setVbsCapability(VbsCapabilityMapEnum.getValue( (data[1]>>2)&0x01 ));
		msObject.setVgsCapability(VgsCapabilityMapEnum.getValue( (data[1]>>1)&0x01 ));
		msObject.setFrequencyCap(FrequencyCapabilityMapEnum.getValue( data[1]&0x01 ));
		
		msObject.setCm3Options(Cm3OptionsMapEnum.getValue( (data[2]>>7)&0x01 ));
		msObject.setLcsVaCapability(LcsVaCapabilityMapEnum.getValue( (data[2]>>5)&0x01));
		msObject.setUcs2Treatment(Ucs2TreatmentMapEnum.getValue((data[2]>>4)&0x01));
		msObject.setSoLsaSupport(SoLsaSupportMapEnum.getValue((data[2]>>3)&0x01));
		msObject.setCmspSupport(CmspSupportMapEnum.getValue((data[2]>>2)&0x01));
		msObject.setA53Algorithm(A53AlgorithmMapEnum.getValue((data[2]>>1)&0x01));
		msObject.setA52Algorithm(A52AlgorithmMapEnum.getValue(data[2]&0x01));
		
		if(logger.isDebugEnabled()) {
			logger.debug("data encoded to object "+msObject);
		}
		
		logger.info("MsClassMark2Map:decode Exit");

		return msObject;
	}
	
	public String toString() {
		
		StringBuilder msg = new StringBuilder();
		msg.append("Revision Level = ").append(this.revisionLevel).append("\n");
		msg.append("ES Indicator = "+this.esiIndicator+"\n");
		msg.append("A51 Algorithm = "+this.a51Algo+"\n");
		msg.append("RF PowerCapability = "+this.rfPowCap+"\n");
		msg.append("Ps Capability = "+this.psCap+"\n");
		msg.append("SS Screening Ind.= "+this.screenInd+"\n");
		msg.append("SM Capability = "+this.smCap+"\n");
		msg.append("VBS = "+this.vbs+"\n");
		msg.append("VGS = "+this.vgs+"\n");
		msg.append("Frequency Capability = "+this.fc+"\n");
		msg.append("CM3 Options = "+this.cm3+"\n");
		msg.append("LCS Va Capability = "+this.lcsVa+"\n");
		msg.append("UCS2 Treatment = "+this.ucs2+"\n");
		msg.append("SoLSA Support = "+this.soLsa+"\n");
		msg.append("CMSP Support = "+this.cmsp+"\n");
		msg.append("A53 Algorithm = "+this.a53Algo+"\n");
		msg.append("A52 Algorithm = "+this.a52Algo+"\n");
		
		return msg.toString();
	}
}
