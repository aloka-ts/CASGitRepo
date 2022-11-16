
package com.agnity.camelv2.asngenerated;
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
    @ASN1BoxedType ( name = "IPRoutingAddress" )
    public class IPRoutingAddress implements IASN1PreparedElement {
                
        
        @ASN1Element ( name = "IPRoutingAddress", isOptional =  false , hasTag =  false  , hasDefaultValue =  false  )
    
        private CalledPartyNumber  value;        

        
        
        public IPRoutingAddress () {
        }
        
        
        
        public void setValue(CalledPartyNumber value) {
            this.value = value;
        }
        
        
        
        public CalledPartyNumber getValue() {
            return this.value;
        }            
        

	    public void initWithDefaults() {
	    }

        private static IASN1PreparedElementData preparedData = CoderFactory.getInstance().newPreparedElementData(IPRoutingAddress.class);
        public IASN1PreparedElementData getPreparedData() {
            return preparedData;
        }

            
    }
            