
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
    @ASN1BoxedType ( name = "DestinationAddress" )
    public class DestinationAddress implements IASN1PreparedElement {
                
        
        @ASN1Element ( name = "DestinationAddress", isOptional =  false , hasTag =  true, tag = 86 , hasDefaultValue =  false  )
    
        private AINDigits  value;        

        
        
        public DestinationAddress () {
        }
        
        
        
        public void setValue(AINDigits value) {
            this.value = value;
        }
        
        
        
        public AINDigits getValue() {
            return this.value;
        }            
        

	    public void initWithDefaults() {
	    }

        private static IASN1PreparedElementData preparedData = CoderFactory.getInstance().newPreparedElementData(DestinationAddress.class);
        public IASN1PreparedElementData getPreparedData() {
            return preparedData;
        }

            
    }
            