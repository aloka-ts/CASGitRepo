
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
    @ASN1BoxedType ( name = "ResetCircuitRes" )
    public class ResetCircuitRes implements IASN1PreparedElement {
                
        

       @ASN1PreparedElement
       @ASN1Sequence ( name = "ResetCircuitRes" , isSet = true )
       public static class ResetCircuitResSequenceType implements IASN1PreparedElement {
                
        @ASN1Element ( name = "trunkStatus", isOptional =  false , hasTag =  true, tag = 16 , hasDefaultValue =  false  )
    
	private TrunkStatus trunkStatus = null;
                
  
        
        public TrunkStatus getTrunkStatus () {
            return this.trunkStatus;
        }

        

        public void setTrunkStatus (TrunkStatus value) {
            this.trunkStatus = value;
        }
        
  
                
                
        public void initWithDefaults() {
            
        }

        public IASN1PreparedElementData getPreparedData() {
            return preparedData_ResetCircuitResSequenceType;
        }

       private static IASN1PreparedElementData preparedData_ResetCircuitResSequenceType = CoderFactory.getInstance().newPreparedElementData(ResetCircuitResSequenceType.class);
                
       }

       
                
        @ASN1Element ( name = "ResetCircuitRes", isOptional =  false , hasTag =  true, tag = 18, 
        tagClass =  TagClass.Private  , hasDefaultValue =  false  )
    
        private ResetCircuitResSequenceType  value;        

        
        
        public ResetCircuitRes () {
        }
        
        
        
        public void setValue(ResetCircuitResSequenceType value) {
            this.value = value;
        }
        
        
        
        public ResetCircuitResSequenceType getValue() {
            return this.value;
        }            
        

	    public void initWithDefaults() {
	    }

        private static IASN1PreparedElementData preparedData = CoderFactory.getInstance().newPreparedElementData(ResetCircuitRes.class);
        public IASN1PreparedElementData getPreparedData() {
            return preparedData;
        }

            
    }
            