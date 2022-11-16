
package com.agnity.map.asngenerated;
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
    @ASN1BoxedType ( name = "SuppressMTSS" )
    public class SuppressMTSS implements IASN1PreparedElement {
    
            @ASN1BitString( name = "SuppressMTSS" )
            @ASN1ValueRangeConstraint ( 
		
		min = 2L, 
		
		max = 16L 
		
	   )
	   
            private BitString value = null;
            
            public SuppressMTSS() {
            }

            public SuppressMTSS(BitString value) {
                this.value = value;
            }
            
            public void setValue(BitString value) {
                this.value = value;
            }
            
            public BitString getValue() {
                return this.value;
            }

	    public void initWithDefaults() {
	    }

        private static IASN1PreparedElementData preparedData = CoderFactory.getInstance().newPreparedElementData(SuppressMTSS.class);
        public IASN1PreparedElementData getPreparedData() {
            return preparedData;
        }

    }
            