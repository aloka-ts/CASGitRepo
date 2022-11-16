
package com.agnity.ain.asngenerated;
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
    @ASN1BoxedType ( name = "NotificationDuration" )
    public class NotificationDuration implements IASN1PreparedElement {
                
        @ASN1Integer( name = "" )
    @ASN1ValueRangeConstraint ( 
		
		min = 1L, 
		
		max = 99L 
		
	   )
	   
        @ASN1Element ( name = "NotificationDuration", isOptional =  false , hasTag =  true, tag = 128 , hasDefaultValue =  false  )
    
        private Integer  value;        

        
        
        public NotificationDuration () {
        }
        
        
        
        public void setValue(Integer value) {
            this.value = value;
        }
        
        
        
        public Integer getValue() {
            return this.value;
        }            
        

	    public void initWithDefaults() {
	    }

        private static IASN1PreparedElementData preparedData = CoderFactory.getInstance().newPreparedElementData(NotificationDuration.class);
        public IASN1PreparedElementData getPreparedData() {
            return preparedData;
        }

            
    }
            