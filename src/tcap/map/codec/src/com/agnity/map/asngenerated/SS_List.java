
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
    @ASN1BoxedType ( name = "SS_List" )
    public class SS_List implements IASN1PreparedElement {
                
            @ASN1ValueRangeConstraint ( 
		
		min = 1L, 
		
		max = 30L 
		
	   )
	   
            @ASN1SequenceOf( name = "SS-List" , isSetOf = false)
	    private java.util.Collection<SS_Code> value = null; 
    
            public SS_List () {
            }
        
            public SS_List ( java.util.Collection<SS_Code> value ) {
                setValue(value);
            }
                        
            public void setValue(java.util.Collection<SS_Code> value) {
                this.value = value;
            }
            
            public java.util.Collection<SS_Code> getValue() {
                return this.value;
            }            
            
            public void initValue() {
                setValue(new java.util.LinkedList<SS_Code>()); 
            }
            
            public void add(SS_Code item) {
                value.add(item);
            }

	    public void initWithDefaults() {
	    }

        private static IASN1PreparedElementData preparedData = CoderFactory.getInstance().newPreparedElementData(SS_List.class);
        public IASN1PreparedElementData getPreparedData() {
            return preparedData;
        }


    }
            