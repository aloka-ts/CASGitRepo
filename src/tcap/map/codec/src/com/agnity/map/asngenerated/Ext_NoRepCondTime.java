
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
    @ASN1BoxedType ( name = "Ext_NoRepCondTime" )
    public class Ext_NoRepCondTime implements IASN1PreparedElement {
    
            @ASN1Integer( name = "Ext-NoRepCondTime" )
            @ASN1ValueRangeConstraint ( 
		
		min = 1L, 
		
		max = 100L 
		
	   )
	   
            private Integer value;
            
            public Ext_NoRepCondTime() {
            }

            public Ext_NoRepCondTime(Integer value) {
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

        private static IASN1PreparedElementData preparedData = CoderFactory.getInstance().newPreparedElementData(Ext_NoRepCondTime.class);
        public IASN1PreparedElementData getPreparedData() {
            return preparedData;
        }


    }
            