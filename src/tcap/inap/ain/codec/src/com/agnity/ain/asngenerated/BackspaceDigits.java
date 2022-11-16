
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
    @ASN1BoxedType ( name = "BackspaceDigits" )
    public class BackspaceDigits implements IASN1PreparedElement {
                
        
        @ASN1Element ( name = "BackspaceDigits", isOptional =  false , hasTag =  false  , hasDefaultValue =  false  )
    
        private KeyCodes  value;        

        
        
        public BackspaceDigits () {
        }
        
        
        
        public void setValue(KeyCodes value) {
            this.value = value;
        }
        
        
        
        public KeyCodes getValue() {
            return this.value;
        }            
        

	    public void initWithDefaults() {
	    }

        private static IASN1PreparedElementData preparedData = CoderFactory.getInstance().newPreparedElementData(BackspaceDigits.class);
        public IASN1PreparedElementData getPreparedData() {
            return preparedData;
        }

            
    }
            