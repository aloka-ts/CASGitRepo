
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
    @ASN1BoxedType ( name = "PartyOnHold" )
    public class PartyOnHold implements IASN1PreparedElement {
                
        
        @ASN1Null ( name = "PartyOnHold" ) 
    
        @ASN1Element ( name = "PartyOnHold", isOptional =  false , hasTag =  true, tag = 146 , hasDefaultValue =  false  )
    
        private org.bn.types.NullObject  value;        

        

	    public void initWithDefaults() {
	    }

        private static IASN1PreparedElementData preparedData = CoderFactory.getInstance().newPreparedElementData(PartyOnHold.class);
        public IASN1PreparedElementData getPreparedData() {
            return preparedData;
        }

            
    }
            