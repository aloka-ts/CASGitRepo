
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
    @ASN1BoxedType ( name = "INServiceCompatibilityResponse" )
    public class INServiceCompatibilityResponse implements IASN1PreparedElement {
                
        
        @ASN1Element ( name = "INServiceCompatibilityResponse", isOptional =  false , hasTag =  false  , hasDefaultValue =  false  )
    
        private Entry  value;        

        
        
        public INServiceCompatibilityResponse () {
        }
        
        
        
        public void setValue(Entry value) {
            this.value = value;
        }
        
        
        
        public Entry getValue() {
            return this.value;
        }            
        

	    public void initWithDefaults() {
	    }

        private static IASN1PreparedElementData preparedData = CoderFactory.getInstance().newPreparedElementData(INServiceCompatibilityResponse.class);
        public IASN1PreparedElementData getPreparedData() {
            return preparedData;
        }

            
    }
            