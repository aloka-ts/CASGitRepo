
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
    @ASN1BoxedType ( name = "GenericNumbers" )
    public class GenericNumbers implements IASN1PreparedElement {
                
            @ASN1ValueRangeConstraint ( 
		
		min = 1L, 
		
		max = 2L 
		
	   )
	   
            @ASN1SequenceOf( name = "GenericNumbers" , isSetOf = true)
	    private java.util.Collection<GenericNumber> value = null; 
    
            public GenericNumbers () {
            }
        
            public GenericNumbers ( java.util.Collection<GenericNumber> value ) {
                setValue(value);
            }
                        
            public void setValue(java.util.Collection<GenericNumber> value) {
                this.value = value;
            }
            
            public java.util.Collection<GenericNumber> getValue() {
                return this.value;
            }            
            
            public void initValue() {
                setValue(new java.util.LinkedList<GenericNumber>()); 
            }
            
            public void add(GenericNumber item) {
                value.add(item);
            }

	    public void initWithDefaults() {
	    }

        private static IASN1PreparedElementData preparedData = CoderFactory.getInstance().newPreparedElementData(GenericNumbers.class);
        public IASN1PreparedElementData getPreparedData() {
            return preparedData;
        }


    }
            