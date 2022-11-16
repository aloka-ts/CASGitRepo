
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
    @ASN1BoxedType ( name = "InvokeID" )
    public class InvokeID implements IASN1PreparedElement {
                
        
        @ASN1Element ( name = "InvokeID", isOptional =  false , hasTag =  false  , hasDefaultValue =  false  )
    
        private InvokeIdType  value;        

        
        
        public InvokeID () {
        }
        
        
        
        public void setValue(InvokeIdType value) {
            this.value = value;
        }
        
        
        
        public InvokeIdType getValue() {
            return this.value;
        }            
        

	    public void initWithDefaults() {
	    }

        private static IASN1PreparedElementData preparedData = CoderFactory.getInstance().newPreparedElementData(InvokeID.class);
        public IASN1PreparedElementData getPreparedData() {
            return preparedData;
        }

            
    }
            