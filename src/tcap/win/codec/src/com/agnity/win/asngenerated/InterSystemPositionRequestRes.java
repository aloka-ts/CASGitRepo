
package com.agnity.win.asngenerated;
//
// This file was generated by the BinaryNotes compiler.
// See http://bnotes.sourceforge.net 
// Any modifications to this file will be lost upon recompilation of the source ASN.1. 
//

import org.bn.*;
import org.bn.annotations.*;
import org.bn.annotations.constraints.*;
import org.bn.coders.*;
import org.bn.types.*;




    @ASN1PreparedElement
    @ASN1BoxedType ( name = "InterSystemPositionRequestRes" )
    public class InterSystemPositionRequestRes implements IASN1PreparedElement {
                
        

       @ASN1PreparedElement
       @ASN1Sequence ( name = "InterSystemPositionRequestRes" , isSet = true )
       public static class InterSystemPositionRequestResSequenceType implements IASN1PreparedElement {
                
        @ASN1Element ( name = "positionResult", isOptional =  false , hasTag =  true, tag = 338 , hasDefaultValue =  false  )
    
	private PositionResult positionResult = null;
                
  
        @ASN1Element ( name = "lcsBillingID", isOptional =  true , hasTag =  true, tag = 367 , hasDefaultValue =  false  )
    
	private LCSBillingID lcsBillingID = null;
                
  
        @ASN1Element ( name = "mobilePositionCapability", isOptional =  true , hasTag =  true, tag = 335 , hasDefaultValue =  false  )
    
	private MobilePositionCapability mobilePositionCapability = null;
                
  
        @ASN1Element ( name = "channelData", isOptional =  true , hasTag =  true, tag = 5 , hasDefaultValue =  false  )
    
	private ChannelData channelData = null;
                
  
        @ASN1Element ( name = "dtxIndication", isOptional =  true , hasTag =  true, tag = 329 , hasDefaultValue =  false  )
    
	private DTXIndication dtxIndication = null;
                
  
        @ASN1Element ( name = "receivedSignalQuality", isOptional =  true , hasTag =  true, tag = 72 , hasDefaultValue =  false  )
    
	private ReceivedSignalQuality receivedSignalQuality = null;
                
  
        @ASN1Element ( name = "cdmaChannelData", isOptional =  true , hasTag =  true, tag = 63 , hasDefaultValue =  false  )
    
	private CDMAChannelData cdmaChannelData = null;
                
  
        @ASN1Element ( name = "cdmaCodeChannel", isOptional =  true , hasTag =  true, tag = 68 , hasDefaultValue =  false  )
    
	private CDMACodeChannel cdmaCodeChannel = null;
                
  
        @ASN1Element ( name = "cdmaMobileCapabilities", isOptional =  true , hasTag =  true, tag = 330 , hasDefaultValue =  false  )
    
	private CDMAMobileCapabilities cdmaMobileCapabilities = null;
                
  
        @ASN1Element ( name = "cdmaPrivateLongCodeMask", isOptional =  true , hasTag =  true, tag = 67 , hasDefaultValue =  false  )
    
	private CDMAPrivateLongCodeMask cdmaPrivateLongCodeMask = null;
                
  
        @ASN1Element ( name = "cdmaServingOneWayDelay2", isOptional =  true , hasTag =  true, tag = 347 , hasDefaultValue =  false  )
    
	private CDMAServingOneWayDelay2 cdmaServingOneWayDelay2 = null;
                
  
        @ASN1Element ( name = "cdmaServiceOption", isOptional =  true , hasTag =  true, tag = 175 , hasDefaultValue =  false  )
    
	private CDMAServiceOption cdmaServiceOption = null;
                
  
        @ASN1Element ( name = "cdmaTargetMAHOList", isOptional =  true , hasTag =  true, tag = 136 , hasDefaultValue =  false  )
    
	private CDMATargetMAHOList cdmaTargetMAHOList = null;
                
  
        @ASN1Element ( name = "cdmaPSMMList", isOptional =  true , hasTag =  true, tag = 346 , hasDefaultValue =  false  )
    
	private CDMAPSMMList cdmaPSMMList = null;
                
  
        @ASN1Element ( name = "nampsChannelData", isOptional =  true , hasTag =  true, tag = 76 , hasDefaultValue =  false  )
    
	private NAMPSChannelData nampsChannelData = null;
                
  
        @ASN1Element ( name = "tdmaChannelData", isOptional =  false , hasTag =  true, tag = 28 , hasDefaultValue =  false  )
    
	private TDMAChannelData tdmaChannelData = null;
                
  
        @ASN1Element ( name = "targetMeasurementList", isOptional =  true , hasTag =  true, tag = 157 , hasDefaultValue =  false  )
    
	private TargetMeasurementList targetMeasurementList = null;
                
  
        @ASN1Element ( name = "tdma-MAHO-CELLID", isOptional =  true , hasTag =  true, tag = 359 , hasDefaultValue =  false  )
    
	private TDMA_MAHO_CELLID tdma_MAHO_CELLID = null;
                
  
        @ASN1Element ( name = "tdma-MAHO-CHANNEL", isOptional =  true , hasTag =  true, tag = 360 , hasDefaultValue =  false  )
    
	private TDMA_MAHO_CHANNEL tdma_MAHO_CHANNEL = null;
                
  
        @ASN1Element ( name = "tdma-TimeAlignment", isOptional =  true , hasTag =  true, tag = 362 , hasDefaultValue =  false  )
    
	private TDMA_TimeAlignment tdma_TimeAlignment = null;
                
  
        @ASN1Element ( name = "tdmaVoiceMode", isOptional =  true , hasTag =  true, tag = 223 , hasDefaultValue =  false  )
    
	private TDMAVoiceMode tdmaVoiceMode = null;
                
  
        @ASN1Element ( name = "voicePrivacyMask", isOptional =  true , hasTag =  true, tag = 48 , hasDefaultValue =  false  )
    
	private VoicePrivacyMask voicePrivacyMask = null;
                
  
        @ASN1Element ( name = "mscid", isOptional =  true , hasTag =  true, tag = 21 , hasDefaultValue =  false  )
    
	private MSCID mscid = null;
                
  
        @ASN1Element ( name = "positionInformation", isOptional =  true , hasTag =  true, tag = 336 , hasDefaultValue =  false  )
    
	private PositionInformation positionInformation = null;
                
  
        @ASN1Element ( name = "servingCellID", isOptional =  true , hasTag =  true, tag = 2 , hasDefaultValue =  false  )
    
	private ServingCellID servingCellID = null;
                
  
        
        public PositionResult getPositionResult () {
            return this.positionResult;
        }

        

        public void setPositionResult (PositionResult value) {
            this.positionResult = value;
        }
        
  
        
        public LCSBillingID getLcsBillingID () {
            return this.lcsBillingID;
        }

        
        public boolean isLcsBillingIDPresent () {
            return this.lcsBillingID != null;
        }
        

        public void setLcsBillingID (LCSBillingID value) {
            this.lcsBillingID = value;
        }
        
  
        
        public MobilePositionCapability getMobilePositionCapability () {
            return this.mobilePositionCapability;
        }

        
        public boolean isMobilePositionCapabilityPresent () {
            return this.mobilePositionCapability != null;
        }
        

        public void setMobilePositionCapability (MobilePositionCapability value) {
            this.mobilePositionCapability = value;
        }
        
  
        
        public ChannelData getChannelData () {
            return this.channelData;
        }

        
        public boolean isChannelDataPresent () {
            return this.channelData != null;
        }
        

        public void setChannelData (ChannelData value) {
            this.channelData = value;
        }
        
  
        
        public DTXIndication getDtxIndication () {
            return this.dtxIndication;
        }

        
        public boolean isDtxIndicationPresent () {
            return this.dtxIndication != null;
        }
        

        public void setDtxIndication (DTXIndication value) {
            this.dtxIndication = value;
        }
        
  
        
        public ReceivedSignalQuality getReceivedSignalQuality () {
            return this.receivedSignalQuality;
        }

        
        public boolean isReceivedSignalQualityPresent () {
            return this.receivedSignalQuality != null;
        }
        

        public void setReceivedSignalQuality (ReceivedSignalQuality value) {
            this.receivedSignalQuality = value;
        }
        
  
        
        public CDMAChannelData getCdmaChannelData () {
            return this.cdmaChannelData;
        }

        
        public boolean isCdmaChannelDataPresent () {
            return this.cdmaChannelData != null;
        }
        

        public void setCdmaChannelData (CDMAChannelData value) {
            this.cdmaChannelData = value;
        }
        
  
        
        public CDMACodeChannel getCdmaCodeChannel () {
            return this.cdmaCodeChannel;
        }

        
        public boolean isCdmaCodeChannelPresent () {
            return this.cdmaCodeChannel != null;
        }
        

        public void setCdmaCodeChannel (CDMACodeChannel value) {
            this.cdmaCodeChannel = value;
        }
        
  
        
        public CDMAMobileCapabilities getCdmaMobileCapabilities () {
            return this.cdmaMobileCapabilities;
        }

        
        public boolean isCdmaMobileCapabilitiesPresent () {
            return this.cdmaMobileCapabilities != null;
        }
        

        public void setCdmaMobileCapabilities (CDMAMobileCapabilities value) {
            this.cdmaMobileCapabilities = value;
        }
        
  
        
        public CDMAPrivateLongCodeMask getCdmaPrivateLongCodeMask () {
            return this.cdmaPrivateLongCodeMask;
        }

        
        public boolean isCdmaPrivateLongCodeMaskPresent () {
            return this.cdmaPrivateLongCodeMask != null;
        }
        

        public void setCdmaPrivateLongCodeMask (CDMAPrivateLongCodeMask value) {
            this.cdmaPrivateLongCodeMask = value;
        }
        
  
        
        public CDMAServingOneWayDelay2 getCdmaServingOneWayDelay2 () {
            return this.cdmaServingOneWayDelay2;
        }

        
        public boolean isCdmaServingOneWayDelay2Present () {
            return this.cdmaServingOneWayDelay2 != null;
        }
        

        public void setCdmaServingOneWayDelay2 (CDMAServingOneWayDelay2 value) {
            this.cdmaServingOneWayDelay2 = value;
        }
        
  
        
        public CDMAServiceOption getCdmaServiceOption () {
            return this.cdmaServiceOption;
        }

        
        public boolean isCdmaServiceOptionPresent () {
            return this.cdmaServiceOption != null;
        }
        

        public void setCdmaServiceOption (CDMAServiceOption value) {
            this.cdmaServiceOption = value;
        }
        
  
        
        public CDMATargetMAHOList getCdmaTargetMAHOList () {
            return this.cdmaTargetMAHOList;
        }

        
        public boolean isCdmaTargetMAHOListPresent () {
            return this.cdmaTargetMAHOList != null;
        }
        

        public void setCdmaTargetMAHOList (CDMATargetMAHOList value) {
            this.cdmaTargetMAHOList = value;
        }
        
  
        
        public CDMAPSMMList getCdmaPSMMList () {
            return this.cdmaPSMMList;
        }

        
        public boolean isCdmaPSMMListPresent () {
            return this.cdmaPSMMList != null;
        }
        

        public void setCdmaPSMMList (CDMAPSMMList value) {
            this.cdmaPSMMList = value;
        }
        
  
        
        public NAMPSChannelData getNampsChannelData () {
            return this.nampsChannelData;
        }

        
        public boolean isNampsChannelDataPresent () {
            return this.nampsChannelData != null;
        }
        

        public void setNampsChannelData (NAMPSChannelData value) {
            this.nampsChannelData = value;
        }
        
  
        
        public TDMAChannelData getTdmaChannelData () {
            return this.tdmaChannelData;
        }

        

        public void setTdmaChannelData (TDMAChannelData value) {
            this.tdmaChannelData = value;
        }
        
  
        
        public TargetMeasurementList getTargetMeasurementList () {
            return this.targetMeasurementList;
        }

        
        public boolean isTargetMeasurementListPresent () {
            return this.targetMeasurementList != null;
        }
        

        public void setTargetMeasurementList (TargetMeasurementList value) {
            this.targetMeasurementList = value;
        }
        
  
        
        public TDMA_MAHO_CELLID getTdma_MAHO_CELLID () {
            return this.tdma_MAHO_CELLID;
        }

        
        public boolean isTdma_MAHO_CELLIDPresent () {
            return this.tdma_MAHO_CELLID != null;
        }
        

        public void setTdma_MAHO_CELLID (TDMA_MAHO_CELLID value) {
            this.tdma_MAHO_CELLID = value;
        }
        
  
        
        public TDMA_MAHO_CHANNEL getTdma_MAHO_CHANNEL () {
            return this.tdma_MAHO_CHANNEL;
        }

        
        public boolean isTdma_MAHO_CHANNELPresent () {
            return this.tdma_MAHO_CHANNEL != null;
        }
        

        public void setTdma_MAHO_CHANNEL (TDMA_MAHO_CHANNEL value) {
            this.tdma_MAHO_CHANNEL = value;
        }
        
  
        
        public TDMA_TimeAlignment getTdma_TimeAlignment () {
            return this.tdma_TimeAlignment;
        }

        
        public boolean isTdma_TimeAlignmentPresent () {
            return this.tdma_TimeAlignment != null;
        }
        

        public void setTdma_TimeAlignment (TDMA_TimeAlignment value) {
            this.tdma_TimeAlignment = value;
        }
        
  
        
        public TDMAVoiceMode getTdmaVoiceMode () {
            return this.tdmaVoiceMode;
        }

        
        public boolean isTdmaVoiceModePresent () {
            return this.tdmaVoiceMode != null;
        }
        

        public void setTdmaVoiceMode (TDMAVoiceMode value) {
            this.tdmaVoiceMode = value;
        }
        
  
        
        public VoicePrivacyMask getVoicePrivacyMask () {
            return this.voicePrivacyMask;
        }

        
        public boolean isVoicePrivacyMaskPresent () {
            return this.voicePrivacyMask != null;
        }
        

        public void setVoicePrivacyMask (VoicePrivacyMask value) {
            this.voicePrivacyMask = value;
        }
        
  
        
        public MSCID getMscid () {
            return this.mscid;
        }

        
        public boolean isMscidPresent () {
            return this.mscid != null;
        }
        

        public void setMscid (MSCID value) {
            this.mscid = value;
        }
        
  
        
        public PositionInformation getPositionInformation () {
            return this.positionInformation;
        }

        
        public boolean isPositionInformationPresent () {
            return this.positionInformation != null;
        }
        

        public void setPositionInformation (PositionInformation value) {
            this.positionInformation = value;
        }
        
  
        
        public ServingCellID getServingCellID () {
            return this.servingCellID;
        }

        
        public boolean isServingCellIDPresent () {
            return this.servingCellID != null;
        }
        

        public void setServingCellID (ServingCellID value) {
            this.servingCellID = value;
        }
        
  
                
                
        public void initWithDefaults() {
            
        }

        public IASN1PreparedElementData getPreparedData() {
            return preparedData_InterSystemPositionRequestResSequenceType;
        }

       private static IASN1PreparedElementData preparedData_InterSystemPositionRequestResSequenceType = CoderFactory.getInstance().newPreparedElementData(InterSystemPositionRequestResSequenceType.class);
                
       }

       
                
        @ASN1Element ( name = "InterSystemPositionRequestRes", isOptional =  false , hasTag =  true, tag = 18, 
        tagClass =  TagClass.Private  , hasDefaultValue =  false  )
    
        private InterSystemPositionRequestResSequenceType  value;        

        
        
        public InterSystemPositionRequestRes () {
        }
        
        
        
        public void setValue(InterSystemPositionRequestResSequenceType value) {
            this.value = value;
        }
        
        
        
        public InterSystemPositionRequestResSequenceType getValue() {
            return this.value;
        }            
        

	    public void initWithDefaults() {
	    }

        private static IASN1PreparedElementData preparedData = CoderFactory.getInstance().newPreparedElementData(InterSystemPositionRequestRes.class);
        public IASN1PreparedElementData getPreparedData() {
            return preparedData;
        }

            
    }
            