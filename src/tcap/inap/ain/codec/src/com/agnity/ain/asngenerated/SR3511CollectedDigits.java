
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
    @ASN1BoxedType ( name = "SR3511CollectedDigits" )
    public class SR3511CollectedDigits implements IASN1PreparedElement {
                
        
        @ASN1Element ( name = "SR3511CollectedDigits", isOptional =  false , hasTag =  false  , hasDefaultValue =  false  )
    
        private KeyCodes  value;        

        
        
        public SR3511CollectedDigits () {
        }
        
        
        
        public void setValue(KeyCodes value) {
            this.value = value;
        }
        
        
        
        public KeyCodes getValue() {
            return this.value;
        }            
        

	    public void initWithDefaults() {
	    }

        private static IASN1PreparedElementData preparedData = CoderFactory.getInstance().newPreparedElementData(SR3511CollectedDigits.class);
        public IASN1PreparedElementData getPreparedData() {
            return preparedData;
        }

            
    }
            