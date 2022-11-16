
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
    @ASN1BoxedType ( name = "SMS_OriginalDestinationSubaddress" )
    public class SMS_OriginalDestinationSubaddress implements IASN1PreparedElement {
                
        
        @ASN1Element ( name = "SMS-OriginalDestinationSubaddress", isOptional =  false , hasTag =  false  , hasDefaultValue =  false  )
    
        private Subaddress  value;        

        
        
        public SMS_OriginalDestinationSubaddress () {
        }
        
        
        
        public void setValue(Subaddress value) {
            this.value = value;
        }
        
        
        
        public Subaddress getValue() {
            return this.value;
        }            
        

	    public void initWithDefaults() {
	    }

        private static IASN1PreparedElementData preparedData = CoderFactory.getInstance().newPreparedElementData(SMS_OriginalDestinationSubaddress.class);
        public IASN1PreparedElementData getPreparedData() {
            return preparedData;
        }

            
    }
            