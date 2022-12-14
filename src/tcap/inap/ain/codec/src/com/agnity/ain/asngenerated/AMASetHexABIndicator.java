
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
    @ASN1BoxedType ( name = "AMASetHexABIndicator" )
    public class AMASetHexABIndicator implements IASN1PreparedElement {
                
        @ASN1Boolean( name = "" )
    
        @ASN1Element ( name = "AMASetHexABIndicator", isOptional =  false , hasTag =  true, tag = 82 , hasDefaultValue =  false  )
    
        private Boolean  value;        

        
        
        public AMASetHexABIndicator () {
        }
        
        
        
        public void setValue(Boolean value) {
            this.value = value;
        }
        
        
        
        public Boolean getValue() {
            return this.value;
        }            
        

	    public void initWithDefaults() {
	    }

        private static IASN1PreparedElementData preparedData = CoderFactory.getInstance().newPreparedElementData(AMASetHexABIndicator.class);
        public IASN1PreparedElementData getPreparedData() {
            return preparedData;
        }

            
    }
            