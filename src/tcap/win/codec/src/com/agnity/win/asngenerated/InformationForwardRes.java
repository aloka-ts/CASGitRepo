
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
    @ASN1BoxedType ( name = "InformationForwardRes" )
    public class InformationForwardRes implements IASN1PreparedElement {
                
        

       @ASN1PreparedElement
       @ASN1Sequence ( name = "InformationForwardRes" , isSet = true )
       public static class InformationForwardResSequenceType implements IASN1PreparedElement {
                
        @ASN1Element ( name = "alertResult", isOptional =  true , hasTag =  true, tag = 129 , hasDefaultValue =  false  )
    
	private AlertResult alertResult = null;
                
  
        
        public AlertResult getAlertResult () {
            return this.alertResult;
        }

        
        public boolean isAlertResultPresent () {
            return this.alertResult != null;
        }
        

        public void setAlertResult (AlertResult value) {
            this.alertResult = value;
        }
        
  
                
                
        public void initWithDefaults() {
            
        }

        public IASN1PreparedElementData getPreparedData() {
            return preparedData_InformationForwardResSequenceType;
        }

       private static IASN1PreparedElementData preparedData_InformationForwardResSequenceType = CoderFactory.getInstance().newPreparedElementData(InformationForwardResSequenceType.class);
                
       }

       
                
        @ASN1Element ( name = "InformationForwardRes", isOptional =  false , hasTag =  true, tag = 18, 
        tagClass =  TagClass.Private  , hasDefaultValue =  false  )
    
        private InformationForwardResSequenceType  value;        

        
        
        public InformationForwardRes () {
        }
        
        
        
        public void setValue(InformationForwardResSequenceType value) {
            this.value = value;
        }
        
        
        
        public InformationForwardResSequenceType getValue() {
            return this.value;
        }            
        

	    public void initWithDefaults() {
	    }

        private static IASN1PreparedElementData preparedData = CoderFactory.getInstance().newPreparedElementData(InformationForwardRes.class);
        public IASN1PreparedElementData getPreparedData() {
            return preparedData;
        }

            
    }
            