
package asnGenerated;
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
    @ASN1BoxedType ( name = "CalledPartyBCDNumber" )
    public class CalledPartyBCDNumber implements IASN1PreparedElement {
    
            @ASN1OctetString( name = "CalledPartyBCDNumber" )
            @ASN1ValueRangeConstraint ( 
		
		min = 1L, 
		
		max = 41L 
		
	   )
	   
            private byte[] value = null;
            
            public CalledPartyBCDNumber() {
            }

            public CalledPartyBCDNumber(byte[] value) {
                this.value = value;
            }

            public CalledPartyBCDNumber(BitString value) {
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

        private static IASN1PreparedElementData preparedData = CoderFactory.getInstance().newPreparedElementData(CalledPartyBCDNumber.class);
        public IASN1PreparedElementData getPreparedData() {
            return preparedData;
        }


    }
            