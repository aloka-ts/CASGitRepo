
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
    @ASN1BoxedType ( name = "ApplyRestrictions" )
    public class ApplyRestrictions implements IASN1PreparedElement {
                
        @ASN1BitString( name = "" )
    
        @ASN1Element ( name = "ApplyRestrictions", isOptional =  false , hasTag =  true, tag = 152 , hasDefaultValue =  false  )
    
        private BitString  value;        

        
        
        public ApplyRestrictions () {
        }
        
        
        
        public void setValue(BitString value) {
            this.value = value;
        }
        
        
        
        public BitString getValue() {
            return this.value;
        }            
        

	    public void initWithDefaults() {
	    }

        private static IASN1PreparedElementData preparedData = CoderFactory.getInstance().newPreparedElementData(ApplyRestrictions.class);
        public IASN1PreparedElementData getPreparedData() {
            return preparedData;
        }

            
    }
            