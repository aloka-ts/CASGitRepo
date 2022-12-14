
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
    @ASN1BoxedType ( name = "DataUpdateResultList" )
    public class DataUpdateResultList implements IASN1PreparedElement {
                
            
            @ASN1SequenceOf( name = "DataUpdateResultList" , isSetOf = false)
	    private java.util.Collection<DataUpdateResult> value = null; 
    
            public DataUpdateResultList () {
            }
        
            public DataUpdateResultList ( java.util.Collection<DataUpdateResult> value ) {
                setValue(value);
            }
                        
            public void setValue(java.util.Collection<DataUpdateResult> value) {
                this.value = value;
            }
            
            public java.util.Collection<DataUpdateResult> getValue() {
                return this.value;
            }            
            
            public void initValue() {
                setValue(new java.util.LinkedList<DataUpdateResult>()); 
            }
            
            public void add(DataUpdateResult item) {
                value.add(item);
            }

	    public void initWithDefaults() {
	    }

        private static IASN1PreparedElementData preparedData = CoderFactory.getInstance().newPreparedElementData(DataUpdateResultList.class);
        public IASN1PreparedElementData getPreparedData() {
            return preparedData;
        }


    }
            