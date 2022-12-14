
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
    @ASN1BoxedType ( name = "TimeOfDay" )
    public class TimeOfDay implements IASN1PreparedElement {
    
            @ASN1Integer( name = "TimeOfDay" )
            
            private Long value;
            
            public TimeOfDay() {
            }

            public TimeOfDay(Long value) {
                this.value = value;
            }
            
            public void setValue(Long value) {
                this.value = value;
            }
            
            public Long getValue() {
                return this.value;
            }

	    public void initWithDefaults() {
	    }

        private static IASN1PreparedElementData preparedData = CoderFactory.getInstance().newPreparedElementData(TimeOfDay.class);
        public IASN1PreparedElementData getPreparedData() {
            return preparedData;
        }


    }
            