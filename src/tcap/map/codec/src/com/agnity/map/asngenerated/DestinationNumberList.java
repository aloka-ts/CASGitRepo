
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
    @ASN1BoxedType ( name = "DestinationNumberList" )
    public class DestinationNumberList implements IASN1PreparedElement {
                
            @ASN1ValueRangeConstraint ( 
		
		min = 1L, 
		
		max = 10L 
		
	   )
	   
            @ASN1SequenceOf( name = "DestinationNumberList" , isSetOf = false)
	    private java.util.Collection<ISDN_AddressString> value = null; 
    
            public DestinationNumberList () {
            }
        
            public DestinationNumberList ( java.util.Collection<ISDN_AddressString> value ) {
                setValue(value);
            }
                        
            public void setValue(java.util.Collection<ISDN_AddressString> value) {
                this.value = value;
            }
            
            public java.util.Collection<ISDN_AddressString> getValue() {
                return this.value;
            }            
            
            public void initValue() {
                setValue(new java.util.LinkedList<ISDN_AddressString>()); 
            }
            
            public void add(ISDN_AddressString item) {
                value.add(item);
            }

	    public void initWithDefaults() {
	    }

        private static IASN1PreparedElementData preparedData = CoderFactory.getInstance().newPreparedElementData(DestinationNumberList.class);
        public IASN1PreparedElementData getPreparedData() {
            return preparedData;
        }


    }
            