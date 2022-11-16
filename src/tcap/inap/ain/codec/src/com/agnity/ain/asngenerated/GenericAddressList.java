
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
    @ASN1BoxedType ( name = "GenericAddressList" )
    public class GenericAddressList implements IASN1PreparedElement {
                
        
@ASN1SequenceOf( name = "GenericAddressList", isSetOf = false ) 

    @ASN1ValueRangeConstraint ( 
		
		min = 1L, 
		
		max = 5L 
		
	   )
	   
        @ASN1Element ( name = "GenericAddressList", isOptional =  false , hasTag =  true, tag = 107 , hasDefaultValue =  false  )
    
        private java.util.Collection<GenericAddress>   value;        

        
        
        public GenericAddressList () {
        }
        
        
        
        public void setValue(java.util.Collection<GenericAddress>  value) {
            this.value = value;
        }
        
        
        
        public java.util.Collection<GenericAddress>  getValue() {
            return this.value;
        }            
        

	    public void initWithDefaults() {
	    }

        private static IASN1PreparedElementData preparedData = CoderFactory.getInstance().newPreparedElementData(GenericAddressList.class);
        public IASN1PreparedElementData getPreparedData() {
            return preparedData;
        }

            
    }
            