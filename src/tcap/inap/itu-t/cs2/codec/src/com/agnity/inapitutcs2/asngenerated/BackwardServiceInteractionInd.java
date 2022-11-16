
package com.agnity.inapitutcs2.asngenerated;
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
    @ASN1Sequence ( name = "BackwardServiceInteractionInd", isSet = false )
    public class BackwardServiceInteractionInd implements IASN1PreparedElement {
            @ASN1OctetString( name = "" )
    
            @ASN1SizeConstraint ( max = 1L )
        
        @ASN1Element ( name = "conferenceTreatmentIndicator", isOptional =  true , hasTag =  true, tag = 1 , hasDefaultValue =  false  )
    
	private byte[] conferenceTreatmentIndicator = null;
                
  @ASN1OctetString( name = "" )
    
            @ASN1SizeConstraint ( max = 1L )
        
        @ASN1Element ( name = "callCompletionTreatmentIndicator", isOptional =  true , hasTag =  true, tag = 2 , hasDefaultValue =  false  )
    
	private byte[] callCompletionTreatmentIndicator = null;
                
  
        
        public byte[] getConferenceTreatmentIndicator () {
            return this.conferenceTreatmentIndicator;
        }

        
        public boolean isConferenceTreatmentIndicatorPresent () {
            return this.conferenceTreatmentIndicator != null;
        }
        

        public void setConferenceTreatmentIndicator (byte[] value) {
            this.conferenceTreatmentIndicator = value;
        }
        
  
        
        public byte[] getCallCompletionTreatmentIndicator () {
            return this.callCompletionTreatmentIndicator;
        }

        
        public boolean isCallCompletionTreatmentIndicatorPresent () {
            return this.callCompletionTreatmentIndicator != null;
        }
        

        public void setCallCompletionTreatmentIndicator (byte[] value) {
            this.callCompletionTreatmentIndicator = value;
        }
        
  
                    
        
        public void initWithDefaults() {
            
        }

        private static IASN1PreparedElementData preparedData = CoderFactory.getInstance().newPreparedElementData(BackwardServiceInteractionInd.class);
        public IASN1PreparedElementData getPreparedData() {
            return preparedData;
        }

            
    }
            