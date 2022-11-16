
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
    @ASN1BoxedType ( name = "NewMINExtension" )
    public class NewMINExtension implements IASN1PreparedElement {
                
        
        @ASN1Element ( name = "NewMINExtension", isOptional =  false , hasTag =  false  , hasDefaultValue =  false  )
    
        private MINType  value;        

        
        
        public NewMINExtension () {
        }
        
        
        
        public void setValue(MINType value) {
            this.value = value;
        }
        
        
        
        public MINType getValue() {
            return this.value;
        }            
        

	    public void initWithDefaults() {
	    }

        private static IASN1PreparedElementData preparedData = CoderFactory.getInstance().newPreparedElementData(NewMINExtension.class);
        public IASN1PreparedElementData getPreparedData() {
            return preparedData;
        }

            
    }
            