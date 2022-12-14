
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
    @ASN1BoxedType ( name = "UninterAnnounceBlock" )
    public class UninterAnnounceBlock implements IASN1PreparedElement {
                
            @ASN1ValueRangeConstraint ( 
		
		min = 1L, 
		
		max = 10L 
		
	   )
	   
            @ASN1SequenceOf( name = "UninterAnnounceBlock" , isSetOf = false)
	    private java.util.Collection<AnnounceElement> value = null; 
    
            public UninterAnnounceBlock () {
            }
        
            public UninterAnnounceBlock ( java.util.Collection<AnnounceElement> value ) {
                setValue(value);
            }
                        
            public void setValue(java.util.Collection<AnnounceElement> value) {
                this.value = value;
            }
            
            public java.util.Collection<AnnounceElement> getValue() {
                return this.value;
            }            
            
            public void initValue() {
                setValue(new java.util.LinkedList<AnnounceElement>()); 
            }
            
            public void add(AnnounceElement item) {
                value.add(item);
            }

	    public void initWithDefaults() {
	    }

        private static IASN1PreparedElementData preparedData = CoderFactory.getInstance().newPreparedElementData(UninterAnnounceBlock.class);
        public IASN1PreparedElementData getPreparedData() {
            return preparedData;
        }


    }
            