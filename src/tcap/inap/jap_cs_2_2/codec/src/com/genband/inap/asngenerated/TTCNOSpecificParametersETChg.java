
package com.genband.inap.asngenerated;
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
    @ASN1BoxedType ( name = "TTCNOSpecificParametersETChg" )
    public class TTCNOSpecificParametersETChg implements IASN1PreparedElement {
                
            @ASN1ValueRangeConstraint ( 
		
		min = 1L, 
		
		max = 1L 
		
	   )
	   
            @ASN1SequenceOf( name = "TTCNOSpecificParametersETChg" , isSetOf = true)
	    private java.util.Collection<TTCNOSpecificParameterETChg> value = null; 
    
            public TTCNOSpecificParametersETChg () {
            }
        
            public TTCNOSpecificParametersETChg ( java.util.Collection<TTCNOSpecificParameterETChg> value ) {
                setValue(value);
            }
                        
            public void setValue(java.util.Collection<TTCNOSpecificParameterETChg> value) {
                this.value = value;
            }
            
            public java.util.Collection<TTCNOSpecificParameterETChg> getValue() {
                return this.value;
            }            
            
            public void initValue() {
                setValue(new java.util.LinkedList<TTCNOSpecificParameterETChg>()); 
            }
            
            public void add(TTCNOSpecificParameterETChg item) {
                value.add(item);
            }

	    public void initWithDefaults() {
	    }

        private static IASN1PreparedElementData preparedData = CoderFactory.getInstance().newPreparedElementData(TTCNOSpecificParametersETChg.class);
        public IASN1PreparedElementData getPreparedData() {
            return preparedData;
        }


    }
            