
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
    @ASN1BoxedType ( name = "DropService" )
    public class DropService implements IASN1PreparedElement {
                
        

       @ASN1PreparedElement
       @ASN1Sequence ( name = "DropService" , isSet = true )
       public static class DropServiceSequenceType implements IASN1PreparedElement {
                
        @ASN1Element ( name = "billingID", isOptional =  false , hasTag =  true, tag = 1 , hasDefaultValue =  false  )
    
	private BillingID billingID = null;
                
  
        @ASN1Element ( name = "cdmaConnectionReferenceList", isOptional =  true , hasTag =  true, tag = 212 , hasDefaultValue =  false  )
    
	private CDMAConnectionReferenceList cdmaConnectionReferenceList = null;
                
  
        @ASN1Element ( name = "interMSCCircuitID", isOptional =  false , hasTag =  true, tag = 6 , hasDefaultValue =  false  )
    
	private InterMSCCircuitID interMSCCircuitID = null;
                
  
        @ASN1Element ( name = "imsi", isOptional =  true , hasTag =  true, tag = 242 , hasDefaultValue =  false  )
    
	private IMSI imsi = null;
                
  
        @ASN1Element ( name = "mobileIdentificationNumber", isOptional =  true , hasTag =  true, tag = 8 , hasDefaultValue =  false  )
    
	private MobileIdentificationNumber mobileIdentificationNumber = null;
                
  
        @ASN1Element ( name = "releaseReason", isOptional =  true , hasTag =  true, tag = 10 , hasDefaultValue =  false  )
    
	private ReleaseReason releaseReason = null;
                
  
        
        public BillingID getBillingID () {
            return this.billingID;
        }

        

        public void setBillingID (BillingID value) {
            this.billingID = value;
        }
        
  
        
        public CDMAConnectionReferenceList getCdmaConnectionReferenceList () {
            return this.cdmaConnectionReferenceList;
        }

        
        public boolean isCdmaConnectionReferenceListPresent () {
            return this.cdmaConnectionReferenceList != null;
        }
        

        public void setCdmaConnectionReferenceList (CDMAConnectionReferenceList value) {
            this.cdmaConnectionReferenceList = value;
        }
        
  
        
        public InterMSCCircuitID getInterMSCCircuitID () {
            return this.interMSCCircuitID;
        }

        

        public void setInterMSCCircuitID (InterMSCCircuitID value) {
            this.interMSCCircuitID = value;
        }
        
  
        
        public IMSI getImsi () {
            return this.imsi;
        }

        
        public boolean isImsiPresent () {
            return this.imsi != null;
        }
        

        public void setImsi (IMSI value) {
            this.imsi = value;
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
        
  
        
        public ReleaseReason getReleaseReason () {
            return this.releaseReason;
        }

        
        public boolean isReleaseReasonPresent () {
            return this.releaseReason != null;
        }
        

        public void setReleaseReason (ReleaseReason value) {
            this.releaseReason = value;
        }
        
  
                
                
        public void initWithDefaults() {
            
        }

        public IASN1PreparedElementData getPreparedData() {
            return preparedData_DropServiceSequenceType;
        }

       private static IASN1PreparedElementData preparedData_DropServiceSequenceType = CoderFactory.getInstance().newPreparedElementData(DropServiceSequenceType.class);
                
       }

       
                
        @ASN1Element ( name = "DropService", isOptional =  false , hasTag =  true, tag = 18, 
        tagClass =  TagClass.Private  , hasDefaultValue =  false  )
    
        private DropServiceSequenceType  value;        

        
        
        public DropService () {
        }
        
        
        
        public void setValue(DropServiceSequenceType value) {
            this.value = value;
        }
        
        
        
        public DropServiceSequenceType getValue() {
            return this.value;
        }            
        

	    public void initWithDefaults() {
	    }

        private static IASN1PreparedElementData preparedData = CoderFactory.getInstance().newPreparedElementData(DropService.class);
        public IASN1PreparedElementData getPreparedData() {
            return preparedData;
        }

            
    }
            