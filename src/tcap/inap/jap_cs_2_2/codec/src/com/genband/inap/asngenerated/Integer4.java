
package com.genband.inap.asngenerated;
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
    @ASN1BoxedType ( name = "Integer4" )
    public class Integer4 implements IASN1PreparedElement {
    
            @ASN1Integer( name = "Integer4" )
            @ASN1ValueRangeConstraint ( 
		
		min = 0L, 
		
		max = 2147483647L 
		
	   )
	   
            private Integer value;
            
            public Integer4() {
            }

            public Integer4(Integer value) {
                this.value = value;
            }
            
            public void setValue(Integer value) {
                this.value = value;
            }
            
            public Integer getValue() {
                return this.value;
            }

	    public void initWithDefaults() {
	    }

        private static IASN1PreparedElementData preparedData = CoderFactory.getInstance().newPreparedElementData(Integer4.class);
        public IASN1PreparedElementData getPreparedData() {
            return preparedData;
        }


    }
            