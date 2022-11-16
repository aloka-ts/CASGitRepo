
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
    @ASN1BoxedType ( name = "T_BCSM_CAMEL_TDP_CriteriaList" )
    public class T_BCSM_CAMEL_TDP_CriteriaList implements IASN1PreparedElement {
                
            @ASN1ValueRangeConstraint ( 
		
		min = 1L, 
		
		max = 10L 
		
	   )
	   
            @ASN1SequenceOf( name = "T-BCSM-CAMEL-TDP-CriteriaList" , isSetOf = false)
	    private java.util.Collection<T_BCSM_CAMEL_TDP_Criteria> value = null; 
    
            public T_BCSM_CAMEL_TDP_CriteriaList () {
            }
        
            public T_BCSM_CAMEL_TDP_CriteriaList ( java.util.Collection<T_BCSM_CAMEL_TDP_Criteria> value ) {
                setValue(value);
            }
                        
            public void setValue(java.util.Collection<T_BCSM_CAMEL_TDP_Criteria> value) {
                this.value = value;
            }
            
            public java.util.Collection<T_BCSM_CAMEL_TDP_Criteria> getValue() {
                return this.value;
            }            
            
            public void initValue() {
                setValue(new java.util.LinkedList<T_BCSM_CAMEL_TDP_Criteria>()); 
            }
            
            public void add(T_BCSM_CAMEL_TDP_Criteria item) {
                value.add(item);
            }

	    public void initWithDefaults() {
	    }

        private static IASN1PreparedElementData preparedData = CoderFactory.getInstance().newPreparedElementData(T_BCSM_CAMEL_TDP_CriteriaList.class);
        public IASN1PreparedElementData getPreparedData() {
            return preparedData;
        }


    }
            