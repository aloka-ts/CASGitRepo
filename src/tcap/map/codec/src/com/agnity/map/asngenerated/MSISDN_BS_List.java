
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
    @ASN1BoxedType ( name = "MSISDN_BS_List" )
    public class MSISDN_BS_List implements IASN1PreparedElement {
                
            @ASN1ValueRangeConstraint ( 
		
		min = 1L, 
		
		max = 50L 
		
	   )
	   
            @ASN1SequenceOf( name = "MSISDN-BS-List" , isSetOf = false)
	    private java.util.Collection<MSISDN_BS> value = null; 
    
            public MSISDN_BS_List () {
            }
        
            public MSISDN_BS_List ( java.util.Collection<MSISDN_BS> value ) {
                setValue(value);
            }
                        
            public void setValue(java.util.Collection<MSISDN_BS> value) {
                this.value = value;
            }
            
            public java.util.Collection<MSISDN_BS> getValue() {
                return this.value;
            }            
            
            public void initValue() {
                setValue(new java.util.LinkedList<MSISDN_BS>()); 
            }
            
            public void add(MSISDN_BS item) {
                value.add(item);
            }

	    public void initWithDefaults() {
	    }

        private static IASN1PreparedElementData preparedData = CoderFactory.getInstance().newPreparedElementData(MSISDN_BS_List.class);
        public IASN1PreparedElementData getPreparedData() {
            return preparedData;
        }


    }
            