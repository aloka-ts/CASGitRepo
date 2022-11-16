
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
    @ASN1BoxedType ( name = "AuthenticationResponseUniqueChallenge" )
    public class AuthenticationResponseUniqueChallenge implements IASN1PreparedElement {
    
            @ASN1OctetString( name = "AuthenticationResponseUniqueChallenge" )
            
            private byte[] value = null;
            
            public AuthenticationResponseUniqueChallenge() {
            }

            public AuthenticationResponseUniqueChallenge(byte[] value) {
                this.value = value;
            }

            public AuthenticationResponseUniqueChallenge(BitString value) {
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

        private static IASN1PreparedElementData preparedData = CoderFactory.getInstance().newPreparedElementData(AuthenticationResponseUniqueChallenge.class);
        public IASN1PreparedElementData getPreparedData() {
            return preparedData;
        }


    }
            