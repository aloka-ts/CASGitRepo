
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
    @ASN1BoxedType ( name = "PositionRequestRes" )
    public class PositionRequestRes implements IASN1PreparedElement {
                
        

       @ASN1PreparedElement
       @ASN1Sequence ( name = "PositionRequestRes" , isSet = true )
       public static class PositionRequestResSequenceType implements IASN1PreparedElement {
                
        @ASN1Element ( name = "electronicSerialNumber", isOptional =  true , hasTag =  true, tag = 9 , hasDefaultValue =  false  )
    
	private ElectronicSerialNumber electronicSerialNumber = null;
                
  
        @ASN1Element ( name = "extendedMSCID", isOptional =  true , hasTag =  true, tag = 53 , hasDefaultValue =  false  )
    
	private ExtendedMSCID extendedMSCID = null;
                
  
        @ASN1Element ( name = "mSCIdentificationNumber", isOptional =  true , hasTag =  true, tag = 94 , hasDefaultValue =  false  )
    
	private MSCIdentificationNumber mSCIdentificationNumber = null;
                
  
        @ASN1Element ( name = "mscid", isOptional =  true , hasTag =  true, tag = 21 , hasDefaultValue =  false  )
    
	private MSCID mscid = null;
                
  
        @ASN1Element ( name = "msid", isOptional =  true , hasTag =  false  , hasDefaultValue =  false  )
    
	private MSID msid = null;
                
  
        @ASN1Element ( name = "mSStatus", isOptional =  true , hasTag =  true, tag = 313 , hasDefaultValue =  false  )
    
	private MSStatus mSStatus = null;
                
  
        @ASN1Element ( name = "pc-ssn", isOptional =  true , hasTag =  true, tag = 32 , hasDefaultValue =  false  )
    
	private PC_SSN pc_ssn = null;
                
  
        @ASN1Element ( name = "pSID-RSIDInformation", isOptional =  true , hasTag =  true, tag = 202 , hasDefaultValue =  false  )
    
	private PSID_RSIDInformation pSID_RSIDInformation = null;
                
  
        @ASN1Element ( name = "locationAreaID", isOptional =  true , hasTag =  true, tag = 33 , hasDefaultValue =  false  )
    
	private LocationAreaID locationAreaID = null;
                
  
        @ASN1Element ( name = "servingCellID", isOptional =  true , hasTag =  true, tag = 2 , hasDefaultValue =  false  )
    
	private ServingCellID servingCellID = null;
                
  
        
        public ElectronicSerialNumber getElectronicSerialNumber () {
            return this.electronicSerialNumber;
        }

        
        public boolean isElectronicSerialNumberPresent () {
            return this.electronicSerialNumber != null;
        }
        

        public void setElectronicSerialNumber (ElectronicSerialNumber value) {
            this.electronicSerialNumber = value;
        }
        
  
        
        public ExtendedMSCID getExtendedMSCID () {
            return this.extendedMSCID;
        }

        
        public boolean isExtendedMSCIDPresent () {
            return this.extendedMSCID != null;
        }
        

        public void setExtendedMSCID (ExtendedMSCID value) {
            this.extendedMSCID = value;
        }
        
  
        
        public MSCIdentificationNumber getMSCIdentificationNumber () {
            return this.mSCIdentificationNumber;
        }

        
        public boolean isMSCIdentificationNumberPresent () {
            return this.mSCIdentificationNumber != null;
        }
        

        public void setMSCIdentificationNumber (MSCIdentificationNumber value) {
            this.mSCIdentificationNumber = value;
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
        
  
        
        public MSID getMsid () {
            return this.msid;
        }

        
        public boolean isMsidPresent () {
            return this.msid != null;
        }
        

        public void setMsid (MSID value) {
            this.msid = value;
        }
        
  
        
        public MSStatus getMSStatus () {
            return this.mSStatus;
        }

        
        public boolean isMSStatusPresent () {
            return this.mSStatus != null;
        }
        

        public void setMSStatus (MSStatus value) {
            this.mSStatus = value;
        }
        
  
        
        public PC_SSN getPc_ssn () {
            return this.pc_ssn;
        }

        
        public boolean isPc_ssnPresent () {
            return this.pc_ssn != null;
        }
        

        public void setPc_ssn (PC_SSN value) {
            this.pc_ssn = value;
        }
        
  
        
        public PSID_RSIDInformation getPSID_RSIDInformation () {
            return this.pSID_RSIDInformation;
        }

        
        public boolean isPSID_RSIDInformationPresent () {
            return this.pSID_RSIDInformation != null;
        }
        

        public void setPSID_RSIDInformation (PSID_RSIDInformation value) {
            this.pSID_RSIDInformation = value;
        }
        
  
        
        public LocationAreaID getLocationAreaID () {
            return this.locationAreaID;
        }

        
        public boolean isLocationAreaIDPresent () {
            return this.locationAreaID != null;
        }
        

        public void setLocationAreaID (LocationAreaID value) {
            this.locationAreaID = value;
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
            return preparedData_PositionRequestResSequenceType;
        }

       private static IASN1PreparedElementData preparedData_PositionRequestResSequenceType = CoderFactory.getInstance().newPreparedElementData(PositionRequestResSequenceType.class);
                
       }

       
                
        @ASN1Element ( name = "PositionRequestRes", isOptional =  false , hasTag =  true, tag = 18, 
        tagClass =  TagClass.Private  , hasDefaultValue =  false  )
    
        private PositionRequestResSequenceType  value;        

        
        
        public PositionRequestRes () {
        }
        
        
        
        public void setValue(PositionRequestResSequenceType value) {
            this.value = value;
        }
        
        
        
        public PositionRequestResSequenceType getValue() {
            return this.value;
        }            
        

	    public void initWithDefaults() {
	    }

        private static IASN1PreparedElementData preparedData = CoderFactory.getInstance().newPreparedElementData(PositionRequestRes.class);
        public IASN1PreparedElementData getPreparedData() {
            return preparedData;
        }

            
    }
            