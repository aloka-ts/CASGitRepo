
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
    @ASN1BoxedType ( name = "SS_EventSpecification" )
    public class SS_EventSpecification implements IASN1PreparedElement {
                
            @ASN1ValueRangeConstraint ( 
		
		min = 1L, 
		
		max = 2L 
		
	   )
	   
            @ASN1SequenceOf( name = "SS-EventSpecification" , isSetOf = false)
	    private java.util.Collection<AddressString> value = null; 
    
            public SS_EventSpecification () {
            }
        
            public SS_EventSpecification ( java.util.Collection<AddressString> value ) {
                setValue(value);
            }
                        
            public void setValue(java.util.Collection<AddressString> value) {
                this.value = value;
            }
            
            public java.util.Collection<AddressString> getValue() {
                return this.value;
            }            
            
            public void initValue() {
                setValue(new java.util.LinkedList<AddressString>()); 
            }
            
            public void add(AddressString item) {
                value.add(item);
            }

	    public void initWithDefaults() {
	    }

        private static IASN1PreparedElementData preparedData = CoderFactory.getInstance().newPreparedElementData(SS_EventSpecification.class);
        public IASN1PreparedElementData getPreparedData() {
            return preparedData;
        }


    }
            