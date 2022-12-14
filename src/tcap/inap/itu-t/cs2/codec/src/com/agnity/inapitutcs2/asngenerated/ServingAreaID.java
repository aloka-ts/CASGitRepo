
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
    @ASN1BoxedType ( name = "ServingAreaID" )
    public class ServingAreaID implements IASN1PreparedElement {
                
        
        @ASN1Element ( name = "ServingAreaID", isOptional =  false , hasTag =  false  , hasDefaultValue =  false  )
    
        private LocationNumber  value;        

        
        
        public ServingAreaID () {
        }
        
        
        
        public void setValue(LocationNumber value) {
            this.value = value;
        }
        
        
        
        public LocationNumber getValue() {
            return this.value;
        }            
        

	    public void initWithDefaults() {
	    }

        private static IASN1PreparedElementData preparedData = CoderFactory.getInstance().newPreparedElementData(ServingAreaID.class);
        public IASN1PreparedElementData getPreparedData() {
            return preparedData;
        }

            
    }
            