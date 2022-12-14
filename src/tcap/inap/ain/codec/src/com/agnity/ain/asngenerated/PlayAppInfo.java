
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
    @ASN1BoxedType ( name = "PlayAppInfo" )
    public class PlayAppInfo implements IASN1PreparedElement {
                
        
@ASN1SequenceOf( name = "PlayAppInfo", isSetOf = false ) 

    @ASN1ValueRangeConstraint ( 
		
		min = 0L, 
		
		max = 20L 
		
	   )
	   
        @ASN1Element ( name = "PlayAppInfo", isOptional =  false , hasTag =  true, tag = 96 , hasDefaultValue =  false  )
    
        private java.util.Collection<PlayAppSubParam>   value;        

        
        
        public PlayAppInfo () {
        }
        
        
        
        public void setValue(java.util.Collection<PlayAppSubParam>  value) {
            this.value = value;
        }
        
        
        
        public java.util.Collection<PlayAppSubParam>  getValue() {
            return this.value;
        }            
        

	    public void initWithDefaults() {
	    }

        private static IASN1PreparedElementData preparedData = CoderFactory.getInstance().newPreparedElementData(PlayAppInfo.class);
        public IASN1PreparedElementData getPreparedData() {
            return preparedData;
        }

            
    }
            