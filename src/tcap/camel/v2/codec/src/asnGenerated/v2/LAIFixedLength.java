
package asnGenerated.v2;
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
    @ASN1BoxedType ( name = "LAIFixedLength" )
    public class LAIFixedLength implements IASN1PreparedElement {
    
            @ASN1OctetString( name = "LAIFixedLength" )
            
            @ASN1SizeConstraint ( max = 5L )
        
            private byte[] value = null;
            
            public LAIFixedLength() {
            }

            public LAIFixedLength(byte[] value) {
                this.value = value;
            }

            public LAIFixedLength(BitString value) {
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

        private static IASN1PreparedElementData preparedData = CoderFactory.getInstance().newPreparedElementData(LAIFixedLength.class);
        public IASN1PreparedElementData getPreparedData() {
            return preparedData;
        }


    }
            