
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
    @ASN1Choice ( name = "BearerCapability" )
    public class BearerCapability implements IASN1PreparedElement {
            @ASN1OctetString( name = "" )
    @ASN1ValueRangeConstraint ( 
		
		min = 2L, 
		
		max = 11L 
		
	   )
	   
        @ASN1Element ( name = "bearerCap", isOptional =  false , hasTag =  true, tag = 0 , hasDefaultValue =  false  )
    
	private byte[] bearerCap = null;
                
  
        
        public byte[] getBearerCap () {
            return this.bearerCap;
        }

        public boolean isBearerCapSelected () {
            return this.bearerCap != null;
        }

        private void setBearerCap (byte[] value) {
            this.bearerCap = value;
        }

        
        public void selectBearerCap (byte[] value) {
            this.bearerCap = value;
                        
        }

        
  

	    public void initWithDefaults() {
	    }

        private static IASN1PreparedElementData preparedData = CoderFactory.getInstance().newPreparedElementData(BearerCapability.class);
        public IASN1PreparedElementData getPreparedData() {
            return preparedData;
        }


    }
            