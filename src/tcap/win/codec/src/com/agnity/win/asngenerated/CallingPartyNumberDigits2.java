
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
    @ASN1BoxedType ( name = "CallingPartyNumberDigits2" )
    public class CallingPartyNumberDigits2 implements IASN1PreparedElement {
                
        
        @ASN1Element ( name = "CallingPartyNumberDigits2", isOptional =  false , hasTag =  false  , hasDefaultValue =  false  )
    
        private DigitsType  value;        

        
        
        public CallingPartyNumberDigits2 () {
        }
        
        
        
        public void setValue(DigitsType value) {
            this.value = value;
        }
        
        
        
        public DigitsType getValue() {
            return this.value;
        }            
        

	    public void initWithDefaults() {
	    }

        private static IASN1PreparedElementData preparedData = CoderFactory.getInstance().newPreparedElementData(CallingPartyNumberDigits2.class);
        public IASN1PreparedElementData getPreparedData() {
            return preparedData;
        }

            
    }
            