
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
    @ASN1BoxedType ( name = "RoutingRequestRes" )
    public class RoutingRequestRes implements IASN1PreparedElement {
                
        

       @ASN1PreparedElement
       @ASN1Sequence ( name = "RoutingRequestRes" , isSet = true )
       public static class RoutingRequestResSequenceType implements IASN1PreparedElement {
                
        @ASN1Element ( name = "mscid", isOptional =  false , hasTag =  true, tag = 21 , hasDefaultValue =  false  )
    
	private MSCID mscid = null;
                
  
        @ASN1Element ( name = "accessDeniedReason", isOptional =  true , hasTag =  true, tag = 20 , hasDefaultValue =  false  )
    
	private AccessDeniedReason accessDeniedReason = null;
                
  
        @ASN1Element ( name = "billingID", isOptional =  true , hasTag =  true, tag = 1 , hasDefaultValue =  false  )
    
	private BillingID billingID = null;
                
  
        @ASN1Element ( name = "cdmaServiceOption", isOptional =  true , hasTag =  true, tag = 175 , hasDefaultValue =  false  )
    
	private CDMAServiceOption cdmaServiceOption = null;
                
  
        @ASN1Element ( name = "conditionallyDeniedReason", isOptional =  true , hasTag =  true, tag = 162 , hasDefaultValue =  false  )
    
	private ConditionallyDeniedReason conditionallyDeniedReason = null;
                
  
        @ASN1Element ( name = "digits-Destination", isOptional =  true , hasTag =  true, tag = 4 , hasDefaultValue =  false  )
    
	private Digits digits_Destination = null;
                
  
        @ASN1Element ( name = "mSCIdentificationNumber", isOptional =  true , hasTag =  true, tag = 94 , hasDefaultValue =  false  )
    
	private MSCIdentificationNumber mSCIdentificationNumber = null;
                
  
        @ASN1Element ( name = "pc-ssn", isOptional =  true , hasTag =  true, tag = 32 , hasDefaultValue =  false  )
    
	private PC_SSN pc_ssn = null;
                
  
        @ASN1Element ( name = "tdmaServiceCode", isOptional =  true , hasTag =  true, tag = 178 , hasDefaultValue =  false  )
    
	private TDMAServiceCode tdmaServiceCode = null;
                
  
        
        public MSCID getMscid () {
            return this.mscid;
        }

        

        public void setMscid (MSCID value) {
            this.mscid = value;
        }
        
  
        
        public AccessDeniedReason getAccessDeniedReason () {
            return this.accessDeniedReason;
        }

        
        public boolean isAccessDeniedReasonPresent () {
            return this.accessDeniedReason != null;
        }
        

        public void setAccessDeniedReason (AccessDeniedReason value) {
            this.accessDeniedReason = value;
        }
        
  
        
        public BillingID getBillingID () {
            return this.billingID;
        }

        
        public boolean isBillingIDPresent () {
            return this.billingID != null;
        }
        

        public void setBillingID (BillingID value) {
            this.billingID = value;
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
        
  
        
        public ConditionallyDeniedReason getConditionallyDeniedReason () {
            return this.conditionallyDeniedReason;
        }

        
        public boolean isConditionallyDeniedReasonPresent () {
            return this.conditionallyDeniedReason != null;
        }
        

        public void setConditionallyDeniedReason (ConditionallyDeniedReason value) {
            this.conditionallyDeniedReason = value;
        }
        
  
        
        public Digits getDigits_Destination () {
            return this.digits_Destination;
        }

        
        public boolean isDigits_DestinationPresent () {
            return this.digits_Destination != null;
        }
        

        public void setDigits_Destination (Digits value) {
            this.digits_Destination = value;
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
        
  
        
        public PC_SSN getPc_ssn () {
            return this.pc_ssn;
        }

        
        public boolean isPc_ssnPresent () {
            return this.pc_ssn != null;
        }
        

        public void setPc_ssn (PC_SSN value) {
            this.pc_ssn = value;
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
        
  
                
                
        public void initWithDefaults() {
            
        }

        public IASN1PreparedElementData getPreparedData() {
            return preparedData_RoutingRequestResSequenceType;
        }

       private static IASN1PreparedElementData preparedData_RoutingRequestResSequenceType = CoderFactory.getInstance().newPreparedElementData(RoutingRequestResSequenceType.class);
                
       }

       
                
        @ASN1Element ( name = "RoutingRequestRes", isOptional =  false , hasTag =  true, tag = 18, 
        tagClass =  TagClass.Private  , hasDefaultValue =  false  )
    
        private RoutingRequestResSequenceType  value;        

        
        
        public RoutingRequestRes () {
        }
        
        
        
        public void setValue(RoutingRequestResSequenceType value) {
            this.value = value;
        }
        
        
        
        public RoutingRequestResSequenceType getValue() {
            return this.value;
        }            
        

	    public void initWithDefaults() {
	    }

        private static IASN1PreparedElementData preparedData = CoderFactory.getInstance().newPreparedElementData(RoutingRequestRes.class);
        public IASN1PreparedElementData getPreparedData() {
            return preparedData;
        }

            
    }
            