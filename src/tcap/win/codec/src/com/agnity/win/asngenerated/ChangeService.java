
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
    @ASN1BoxedType ( name = "ChangeService" )
    public class ChangeService implements IASN1PreparedElement {
                
        

       @ASN1PreparedElement
       @ASN1Sequence ( name = "ChangeService" , isSet = true )
       public static class ChangeServiceSequenceType implements IASN1PreparedElement {
                
        @ASN1Element ( name = "cdmaServiceConfigurationRecord", isOptional =  true , hasTag =  true, tag = 174 , hasDefaultValue =  false  )
    
	private CDMAServiceConfigurationRecord cdmaServiceConfigurationRecord = null;
                
  
        @ASN1Element ( name = "cdmaServiceOptionList", isOptional =  true , hasTag =  true, tag = 176 , hasDefaultValue =  false  )
    
	private CDMAServiceOptionList cdmaServiceOptionList = null;
                
  
        @ASN1Element ( name = "changeServiceAttributes", isOptional =  true , hasTag =  true, tag = 214 , hasDefaultValue =  false  )
    
	private ChangeServiceAttributes changeServiceAttributes = null;
                
  
        @ASN1Element ( name = "electronicSerialNumber", isOptional =  true , hasTag =  true, tag = 9 , hasDefaultValue =  false  )
    
	private ElectronicSerialNumber electronicSerialNumber = null;
                
  
        @ASN1Element ( name = "ilspInformation", isOptional =  true , hasTag =  true, tag = 217 , hasDefaultValue =  false  )
    
	private ISLPInformation ilspInformation = null;
                
  
        @ASN1Element ( name = "mobileIdentificationNumber", isOptional =  true , hasTag =  true, tag = 8 , hasDefaultValue =  false  )
    
	private MobileIdentificationNumber mobileIdentificationNumber = null;
                
  
        @ASN1Element ( name = "tdmaBandwidth", isOptional =  true , hasTag =  true, tag = 220 , hasDefaultValue =  false  )
    
	private TDMABandwidth tdmaBandwidth = null;
                
  
        @ASN1Element ( name = "tdmaDataMode", isOptional =  true , hasTag =  true, tag = 222 , hasDefaultValue =  false  )
    
	private TDMADataMode tdmaDataMode = null;
                
  
        @ASN1Element ( name = "tdmaServiceCode", isOptional =  true , hasTag =  true, tag = 178 , hasDefaultValue =  false  )
    
	private TDMAServiceCode tdmaServiceCode = null;
                
  
        @ASN1Element ( name = "tdmaVoiceMode", isOptional =  true , hasTag =  true, tag = 223 , hasDefaultValue =  false  )
    
	private TDMAVoiceMode tdmaVoiceMode = null;
                
  
        
        public CDMAServiceConfigurationRecord getCdmaServiceConfigurationRecord () {
            return this.cdmaServiceConfigurationRecord;
        }

        
        public boolean isCdmaServiceConfigurationRecordPresent () {
            return this.cdmaServiceConfigurationRecord != null;
        }
        

        public void setCdmaServiceConfigurationRecord (CDMAServiceConfigurationRecord value) {
            this.cdmaServiceConfigurationRecord = value;
        }
        
  
        
        public CDMAServiceOptionList getCdmaServiceOptionList () {
            return this.cdmaServiceOptionList;
        }

        
        public boolean isCdmaServiceOptionListPresent () {
            return this.cdmaServiceOptionList != null;
        }
        

        public void setCdmaServiceOptionList (CDMAServiceOptionList value) {
            this.cdmaServiceOptionList = value;
        }
        
  
        
        public ChangeServiceAttributes getChangeServiceAttributes () {
            return this.changeServiceAttributes;
        }

        
        public boolean isChangeServiceAttributesPresent () {
            return this.changeServiceAttributes != null;
        }
        

        public void setChangeServiceAttributes (ChangeServiceAttributes value) {
            this.changeServiceAttributes = value;
        }
        
  
        
        public ElectronicSerialNumber getElectronicSerialNumber () {
            return this.electronicSerialNumber;
        }

        
        public boolean isElectronicSerialNumberPresent () {
            return this.electronicSerialNumber != null;
        }
        

        public void setElectronicSerialNumber (ElectronicSerialNumber value) {
            this.electronicSerialNumber = value;
        }
        
  
        
        public ISLPInformation getIlspInformation () {
            return this.ilspInformation;
        }

        
        public boolean isIlspInformationPresent () {
            return this.ilspInformation != null;
        }
        

        public void setIlspInformation (ISLPInformation value) {
            this.ilspInformation = value;
        }
        
  
        
        public MobileIdentificationNumber getMobileIdentificationNumber () {
            return this.mobileIdentificationNumber;
        }

        
        public boolean isMobileIdentificationNumberPresent () {
            return this.mobileIdentificationNumber != null;
        }
        

        public void setMobileIdentificationNumber (MobileIdentificationNumber value) {
            this.mobileIdentificationNumber = value;
        }
        
  
        
        public TDMABandwidth getTdmaBandwidth () {
            return this.tdmaBandwidth;
        }

        
        public boolean isTdmaBandwidthPresent () {
            return this.tdmaBandwidth != null;
        }
        

        public void setTdmaBandwidth (TDMABandwidth value) {
            this.tdmaBandwidth = value;
        }
        
  
        
        public TDMADataMode getTdmaDataMode () {
            return this.tdmaDataMode;
        }

        
        public boolean isTdmaDataModePresent () {
            return this.tdmaDataMode != null;
        }
        

        public void setTdmaDataMode (TDMADataMode value) {
            this.tdmaDataMode = value;
        }
        
  
        
        public TDMAServiceCode getTdmaServiceCode () {
            return this.tdmaServiceCode;
        }

        
        public boolean isTdmaServiceCodePresent () {
            return this.tdmaServiceCode != null;
        }
        

        public void setTdmaServiceCode (TDMAServiceCode value) {
            this.tdmaServiceCode = value;
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
        
  
                
                
        public void initWithDefaults() {
            
        }

        public IASN1PreparedElementData getPreparedData() {
            return preparedData_ChangeServiceSequenceType;
        }

       private static IASN1PreparedElementData preparedData_ChangeServiceSequenceType = CoderFactory.getInstance().newPreparedElementData(ChangeServiceSequenceType.class);
                
       }

       
                
        @ASN1Element ( name = "ChangeService", isOptional =  false , hasTag =  true, tag = 18, 
        tagClass =  TagClass.Private  , hasDefaultValue =  false  )
    
        private ChangeServiceSequenceType  value;        

        
        
        public ChangeService () {
        }
        
        
        
        public void setValue(ChangeServiceSequenceType value) {
            this.value = value;
        }
        
        
        
        public ChangeServiceSequenceType getValue() {
            return this.value;
        }            
        

	    public void initWithDefaults() {
	    }

        private static IASN1PreparedElementData preparedData = CoderFactory.getInstance().newPreparedElementData(ChangeService.class);
        public IASN1PreparedElementData getPreparedData() {
            return preparedData;
        }

            
    }
            