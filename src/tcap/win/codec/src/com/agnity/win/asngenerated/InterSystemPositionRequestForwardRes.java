
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
    @ASN1BoxedType ( name = "InterSystemPositionRequestForwardRes" )
    public class InterSystemPositionRequestForwardRes implements IASN1PreparedElement {
                
        

       @ASN1PreparedElement
       @ASN1Sequence ( name = "InterSystemPositionRequestForwardRes" , isSet = true )
       public static class InterSystemPositionRequestForwardResSequenceType implements IASN1PreparedElement {
                
        @ASN1Element ( name = "mscid", isOptional =  false , hasTag =  true, tag = 21 , hasDefaultValue =  false  )
    
	private MSCID mscid = null;
                
  
        @ASN1Element ( name = "positionResult", isOptional =  false , hasTag =  true, tag = 338 , hasDefaultValue =  false  )
    
	private PositionResult positionResult = null;
                
  
        @ASN1Element ( name = "lcsBillingID", isOptional =  true , hasTag =  true, tag = 367 , hasDefaultValue =  false  )
    
	private LCSBillingID lcsBillingID = null;
                
  
        @ASN1Element ( name = "positionInformation", isOptional =  true , hasTag =  true, tag = 336 , hasDefaultValue =  false  )
    
	private PositionInformation positionInformation = null;
                
  
        @ASN1Element ( name = "servingCellID", isOptional =  true , hasTag =  true, tag = 2 , hasDefaultValue =  false  )
    
	private ServingCellID servingCellID = null;
                
  
        
        public MSCID getMscid () {
            return this.mscid;
        }

        

        public void setMscid (MSCID value) {
            this.mscid = value;
        }
        
  
        
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
            return preparedData_InterSystemPositionRequestForwardResSequenceType;
        }

       private static IASN1PreparedElementData preparedData_InterSystemPositionRequestForwardResSequenceType = CoderFactory.getInstance().newPreparedElementData(InterSystemPositionRequestForwardResSequenceType.class);
                
       }

       
                
        @ASN1Element ( name = "InterSystemPositionRequestForwardRes", isOptional =  false , hasTag =  true, tag = 18, 
        tagClass =  TagClass.Private  , hasDefaultValue =  false  )
    
        private InterSystemPositionRequestForwardResSequenceType  value;        

        
        
        public InterSystemPositionRequestForwardRes () {
        }
        
        
        
        public void setValue(InterSystemPositionRequestForwardResSequenceType value) {
            this.value = value;
        }
        
        
        
        public InterSystemPositionRequestForwardResSequenceType getValue() {
            return this.value;
        }            
        

	    public void initWithDefaults() {
	    }

        private static IASN1PreparedElementData preparedData = CoderFactory.getInstance().newPreparedElementData(InterSystemPositionRequestForwardRes.class);
        public IASN1PreparedElementData getPreparedData() {
            return preparedData;
        }

            
    }
            