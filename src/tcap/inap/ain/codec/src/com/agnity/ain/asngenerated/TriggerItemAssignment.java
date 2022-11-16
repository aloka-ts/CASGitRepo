
package com.agnity.ain.asngenerated;
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
    @ASN1BoxedType ( name = "TriggerItemAssignment" )
    public class TriggerItemAssignment implements IASN1PreparedElement {
                
        

       @ASN1PreparedElement
       @ASN1Sequence ( name = "TriggerItemAssignment" , isSet = false )
       public static class TriggerItemAssignmentSequenceType implements IASN1PreparedElement {
                
        @ASN1Element ( name = "sSPUserResourceID", isOptional =  false , hasTag =  true, tag = 1 , hasDefaultValue =  false  )
    
	private SSPUserResourceID sSPUserResourceID = null;
                
  
        @ASN1Element ( name = "triggerItemID", isOptional =  false , hasTag =  true, tag = 2 , hasDefaultValue =  false  )
    
	private TriggerItemID triggerItemID = null;
                
  
        @ASN1Element ( name = "activationStateCode", isOptional =  true , hasTag =  true, tag = 3 , hasDefaultValue =  false  )
    
	private ActivationStateCode activationStateCode = null;
                
  
        @ASN1Element ( name = "potentialUse", isOptional =  true , hasTag =  true, tag = 4 , hasDefaultValue =  false  )
    
	private PotentialUse potentialUse = null;
                
  
        @ASN1Element ( name = "sSPUserResourceSubID", isOptional =  true , hasTag =  true, tag = 5 , hasDefaultValue =  false  )
    
	private SSPUserResourceSubID sSPUserResourceSubID = null;
                
  
        
        public SSPUserResourceID getSSPUserResourceID () {
            return this.sSPUserResourceID;
        }

        

        public void setSSPUserResourceID (SSPUserResourceID value) {
            this.sSPUserResourceID = value;
        }
        
  
        
        public TriggerItemID getTriggerItemID () {
            return this.triggerItemID;
        }

        

        public void setTriggerItemID (TriggerItemID value) {
            this.triggerItemID = value;
        }
        
  
        
        public ActivationStateCode getActivationStateCode () {
            return this.activationStateCode;
        }

        
        public boolean isActivationStateCodePresent () {
            return this.activationStateCode != null;
        }
        

        public void setActivationStateCode (ActivationStateCode value) {
            this.activationStateCode = value;
        }
        
  
        
        public PotentialUse getPotentialUse () {
            return this.potentialUse;
        }

        
        public boolean isPotentialUsePresent () {
            return this.potentialUse != null;
        }
        

        public void setPotentialUse (PotentialUse value) {
            this.potentialUse = value;
        }
        
  
        
        public SSPUserResourceSubID getSSPUserResourceSubID () {
            return this.sSPUserResourceSubID;
        }

        
        public boolean isSSPUserResourceSubIDPresent () {
            return this.sSPUserResourceSubID != null;
        }
        

        public void setSSPUserResourceSubID (SSPUserResourceSubID value) {
            this.sSPUserResourceSubID = value;
        }
        
  
                
                
        public void initWithDefaults() {
            
        }

        public IASN1PreparedElementData getPreparedData() {
            return preparedData_TriggerItemAssignmentSequenceType;
        }

       private static IASN1PreparedElementData preparedData_TriggerItemAssignmentSequenceType = CoderFactory.getInstance().newPreparedElementData(TriggerItemAssignmentSequenceType.class);
                
       }

       
                
        @ASN1Element ( name = "TriggerItemAssignment", isOptional =  false , hasTag =  true, tag = 102 , hasDefaultValue =  false  )
    
        private TriggerItemAssignmentSequenceType  value;        

        
        
        public TriggerItemAssignment () {
        }
        
        
        
        public void setValue(TriggerItemAssignmentSequenceType value) {
            this.value = value;
        }
        
        
        
        public TriggerItemAssignmentSequenceType getValue() {
            return this.value;
        }            
        

	    public void initWithDefaults() {
	    }

        private static IASN1PreparedElementData preparedData = CoderFactory.getInstance().newPreparedElementData(TriggerItemAssignment.class);
        public IASN1PreparedElementData getPreparedData() {
            return preparedData;
        }

            
    }
            