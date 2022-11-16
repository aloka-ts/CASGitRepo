
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
    @ASN1BoxedType ( name = "FacilitiesDirective" )
    public class FacilitiesDirective implements IASN1PreparedElement {
                
        

       @ASN1PreparedElement
       @ASN1Sequence ( name = "FacilitiesDirective" , isSet = true )
       public static class FacilitiesDirectiveSequenceType implements IASN1PreparedElement {
                
        @ASN1Element ( name = "billingID", isOptional =  false , hasTag =  true, tag = 1 , hasDefaultValue =  false  )
    
	private BillingID billingID = null;
                
  
        @ASN1Element ( name = "channelData", isOptional =  false , hasTag =  true, tag = 5 , hasDefaultValue =  false  )
    
	private ChannelData channelData = null;
                
  
        @ASN1Element ( name = "electronicSerialNumber", isOptional =  false , hasTag =  true, tag = 9 , hasDefaultValue =  false  )
    
	private ElectronicSerialNumber electronicSerialNumber = null;
                
  
        @ASN1Element ( name = "interMSCCircuitID", isOptional =  false , hasTag =  true, tag = 6 , hasDefaultValue =  false  )
    
	private InterMSCCircuitID interMSCCircuitID = null;
                
  
        @ASN1Element ( name = "interSwitchCount", isOptional =  false , hasTag =  true, tag = 7 , hasDefaultValue =  false  )
    
	private InterSwitchCount interSwitchCount = null;
                
  
        @ASN1Element ( name = "mobileIdentificationNumber", isOptional =  false , hasTag =  true, tag = 8 , hasDefaultValue =  false  )
    
	private MobileIdentificationNumber mobileIdentificationNumber = null;
                
  
        @ASN1Element ( name = "servingCellID", isOptional =  false , hasTag =  true, tag = 2 , hasDefaultValue =  false  )
    
	private ServingCellID servingCellID = null;
                
  
        @ASN1Element ( name = "stationClassMark", isOptional =  false , hasTag =  true, tag = 18 , hasDefaultValue =  false  )
    
	private StationClassMark stationClassMark = null;
                
  
        @ASN1Element ( name = "targetCellID", isOptional =  false , hasTag =  true, tag = 3 , hasDefaultValue =  false  )
    
	private TargetCellID targetCellID = null;
                
  
        @ASN1Element ( name = "confidentialityModes", isOptional =  true , hasTag =  true, tag = 39 , hasDefaultValue =  false  )
    
	private ConfidentialityModes confidentialityModes = null;
                
  
        @ASN1Element ( name = "handoffReason", isOptional =  true , hasTag =  true, tag = 30 , hasDefaultValue =  false  )
    
	private HandoffReason handoffReason = null;
                
  
        @ASN1Element ( name = "handoffState", isOptional =  true , hasTag =  true, tag = 164 , hasDefaultValue =  false  )
    
	private HandoffState handoffState = null;
                
  
        @ASN1Element ( name = "signalingMessageEncryptionKey", isOptional =  true , hasTag =  true, tag = 45 , hasDefaultValue =  false  )
    
	private SignalingMessageEncryptionKey signalingMessageEncryptionKey = null;
                
  
        @ASN1Element ( name = "tdmaBurstIndicator", isOptional =  true , hasTag =  true, tag = 31 , hasDefaultValue =  false  )
    
	private TDMABurstIndicator tdmaBurstIndicator = null;
                
  
        @ASN1Element ( name = "tdmaCallMode", isOptional =  true , hasTag =  true, tag = 29 , hasDefaultValue =  false  )
    
	private TDMACallMode tdmaCallMode = null;
                
  
        @ASN1Element ( name = "tdmaChannelData", isOptional =  true , hasTag =  true, tag = 28 , hasDefaultValue =  false  )
    
	private TDMAChannelData tdmaChannelData = null;
                
  
        @ASN1Element ( name = "voicePrivacyMask", isOptional =  true , hasTag =  true, tag = 48 , hasDefaultValue =  false  )
    
	private VoicePrivacyMask voicePrivacyMask = null;
                
  
        
        public BillingID getBillingID () {
            return this.billingID;
        }

        

        public void setBillingID (BillingID value) {
            this.billingID = value;
        }
        
  
        
        public ChannelData getChannelData () {
            return this.channelData;
        }

        

        public void setChannelData (ChannelData value) {
            this.channelData = value;
        }
        
  
        
        public ElectronicSerialNumber getElectronicSerialNumber () {
            return this.electronicSerialNumber;
        }

        

        public void setElectronicSerialNumber (ElectronicSerialNumber value) {
            this.electronicSerialNumber = value;
        }
        
  
        
        public InterMSCCircuitID getInterMSCCircuitID () {
            return this.interMSCCircuitID;
        }

        

        public void setInterMSCCircuitID (InterMSCCircuitID value) {
            this.interMSCCircuitID = value;
        }
        
  
        
        public InterSwitchCount getInterSwitchCount () {
            return this.interSwitchCount;
        }

        

        public void setInterSwitchCount (InterSwitchCount value) {
            this.interSwitchCount = value;
        }
        
  
        
        public MobileIdentificationNumber getMobileIdentificationNumber () {
            return this.mobileIdentificationNumber;
        }

        

        public void setMobileIdentificationNumber (MobileIdentificationNumber value) {
            this.mobileIdentificationNumber = value;
        }
        
  
        
        public ServingCellID getServingCellID () {
            return this.servingCellID;
        }

        

        public void setServingCellID (ServingCellID value) {
            this.servingCellID = value;
        }
        
  
        
        public StationClassMark getStationClassMark () {
            return this.stationClassMark;
        }

        

        public void setStationClassMark (StationClassMark value) {
            this.stationClassMark = value;
        }
        
  
        
        public TargetCellID getTargetCellID () {
            return this.targetCellID;
        }

        

        public void setTargetCellID (TargetCellID value) {
            this.targetCellID = value;
        }
        
  
        
        public ConfidentialityModes getConfidentialityModes () {
            return this.confidentialityModes;
        }

        
        public boolean isConfidentialityModesPresent () {
            return this.confidentialityModes != null;
        }
        

        public void setConfidentialityModes (ConfidentialityModes value) {
            this.confidentialityModes = value;
        }
        
  
        
        public HandoffReason getHandoffReason () {
            return this.handoffReason;
        }

        
        public boolean isHandoffReasonPresent () {
            return this.handoffReason != null;
        }
        

        public void setHandoffReason (HandoffReason value) {
            this.handoffReason = value;
        }
        
  
        
        public HandoffState getHandoffState () {
            return this.handoffState;
        }

        
        public boolean isHandoffStatePresent () {
            return this.handoffState != null;
        }
        

        public void setHandoffState (HandoffState value) {
            this.handoffState = value;
        }
        
  
        
        public SignalingMessageEncryptionKey getSignalingMessageEncryptionKey () {
            return this.signalingMessageEncryptionKey;
        }

        
        public boolean isSignalingMessageEncryptionKeyPresent () {
            return this.signalingMessageEncryptionKey != null;
        }
        

        public void setSignalingMessageEncryptionKey (SignalingMessageEncryptionKey value) {
            this.signalingMessageEncryptionKey = value;
        }
        
  
        
        public TDMABurstIndicator getTdmaBurstIndicator () {
            return this.tdmaBurstIndicator;
        }

        
        public boolean isTdmaBurstIndicatorPresent () {
            return this.tdmaBurstIndicator != null;
        }
        

        public void setTdmaBurstIndicator (TDMABurstIndicator value) {
            this.tdmaBurstIndicator = value;
        }
        
  
        
        public TDMACallMode getTdmaCallMode () {
            return this.tdmaCallMode;
        }

        
        public boolean isTdmaCallModePresent () {
            return this.tdmaCallMode != null;
        }
        

        public void setTdmaCallMode (TDMACallMode value) {
            this.tdmaCallMode = value;
        }
        
  
        
        public TDMAChannelData getTdmaChannelData () {
            return this.tdmaChannelData;
        }

        
        public boolean isTdmaChannelDataPresent () {
            return this.tdmaChannelData != null;
        }
        

        public void setTdmaChannelData (TDMAChannelData value) {
            this.tdmaChannelData = value;
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
        
  
                
                
        public void initWithDefaults() {
            
        }

        public IASN1PreparedElementData getPreparedData() {
            return preparedData_FacilitiesDirectiveSequenceType;
        }

       private static IASN1PreparedElementData preparedData_FacilitiesDirectiveSequenceType = CoderFactory.getInstance().newPreparedElementData(FacilitiesDirectiveSequenceType.class);
                
       }

       
                
        @ASN1Element ( name = "FacilitiesDirective", isOptional =  false , hasTag =  true, tag = 18, 
        tagClass =  TagClass.Private  , hasDefaultValue =  false  )
    
        private FacilitiesDirectiveSequenceType  value;        

        
        
        public FacilitiesDirective () {
        }
        
        
        
        public void setValue(FacilitiesDirectiveSequenceType value) {
            this.value = value;
        }
        
        
        
        public FacilitiesDirectiveSequenceType getValue() {
            return this.value;
        }            
        

	    public void initWithDefaults() {
	    }

        private static IASN1PreparedElementData preparedData = CoderFactory.getInstance().newPreparedElementData(FacilitiesDirective.class);
        public IASN1PreparedElementData getPreparedData() {
            return preparedData;
        }

            
    }
            