
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
    @ASN1BoxedType ( name = "SR3511SuccessIndicator" )
    public class SR3511SuccessIndicator implements IASN1PreparedElement {
    
            @ASN1Boolean( name = "SR3511SuccessIndicator" )
            
            private Boolean value = null;
            
            public SR3511SuccessIndicator() {
            }

            public SR3511SuccessIndicator(Boolean value) {
                this.value = value;
            }
          
            public void setValue(Boolean value) {
                this.value = value;
            }

            public Boolean getValue() {
                return this.value;
            }

	    public void initWithDefaults() {
	    }

        private static IASN1PreparedElementData preparedData = CoderFactory.getInstance().newPreparedElementData(SR3511SuccessIndicator.class);
        public IASN1PreparedElementData getPreparedData() {
            return preparedData;
        }


    }
            