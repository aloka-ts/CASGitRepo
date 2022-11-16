
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
    @ASN1BoxedType ( name = "AMAServiceProviderID" )
    public class AMAServiceProviderID implements IASN1PreparedElement {
                
        @ASN1OctetString( name = "" )
    
            @ASN1SizeConstraint ( max = 7L )
        
        @ASN1Element ( name = "AMAServiceProviderID", isOptional =  false , hasTag =  true, tag = 101 , hasDefaultValue =  false  )
    
        private byte[]  value;        

        
        
        public AMAServiceProviderID () {
        }
        
        
        
        public void setValue(byte[] value) {
            this.value = value;
        }
        
        
        
        public byte[] getValue() {
            return this.value;
        }            
        

	    public void initWithDefaults() {
	    }

        private static IASN1PreparedElementData preparedData = CoderFactory.getInstance().newPreparedElementData(AMAServiceProviderID.class);
        public IASN1PreparedElementData getPreparedData() {
            return preparedData;
        }

            
    }
            