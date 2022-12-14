
package com.agnity.camelv2.asngenerated;
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
    @ASN1BoxedType ( name = "ExtensionField2Value" )
    public class ExtensionField2Value implements IASN1PreparedElement {
    
            @ASN1OctetString( name = "ExtensionField2Value" )
            @ASN1ValueRangeConstraint ( 
		
		min = 1L, 
		
		max = 100L 
		
	   )
	   
            private byte[] value = null;
            
            public ExtensionField2Value() {
            }

            public ExtensionField2Value(byte[] value) {
                this.value = value;
            }

            public ExtensionField2Value(BitString value) {
                setValue(value);
            }
            
            public void setValue(byte[] value) {
                this.value = value;
            }

            public void setValue(BitString btStr) {
                this.value = btStr.getValue();
            }
            
            public byte[] getValue() {
                return this.value;
            }

	    public void initWithDefaults() {
	    }

        private static IASN1PreparedElementData preparedData = CoderFactory.getInstance().newPreparedElementData(ExtensionField2Value.class);
        public IASN1PreparedElementData getPreparedData() {
            return preparedData;
        }


    }
            