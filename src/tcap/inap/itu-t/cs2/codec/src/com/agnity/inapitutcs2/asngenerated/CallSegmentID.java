
package com.agnity.inapitutcs2.asngenerated;
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
    @ASN1BoxedType ( name = "CallSegmentID" )
    public class CallSegmentID implements IASN1PreparedElement {
    
            @ASN1Integer( name = "CallSegmentID" )
            @ASN1ValueRangeConstraint ( 
		
		min = 1L, 
		
		max = 2L 
		
	   )
	   
            private Integer value;
            
            public CallSegmentID() {
            }

            public CallSegmentID(Integer value) {
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

        private static IASN1PreparedElementData preparedData = CoderFactory.getInstance().newPreparedElementData(CallSegmentID.class);
        public IASN1PreparedElementData getPreparedData() {
            return preparedData;
        }


    }
            