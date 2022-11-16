
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
    @ASN1BoxedType ( name = "MS_Classmark2" )
    public class MS_Classmark2 implements IASN1PreparedElement {
    
            @ASN1OctetString( name = "MS-Classmark2" )
            
            @ASN1SizeConstraint ( max = 3L )
        
            private byte[] value = null;
            
            public MS_Classmark2() {
            }

            public MS_Classmark2(byte[] value) {
                this.value = value;
            }

            public MS_Classmark2(BitString value) {
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

        private static IASN1PreparedElementData preparedData = CoderFactory.getInstance().newPreparedElementData(MS_Classmark2.class);
        public IASN1PreparedElementData getPreparedData() {
            return preparedData;
        }


    }
            