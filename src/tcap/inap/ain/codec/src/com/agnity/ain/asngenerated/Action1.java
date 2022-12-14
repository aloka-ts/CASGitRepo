
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
    @ASN1Choice ( name = "Action1" )
    public class Action1 implements IASN1PreparedElement {
            
        @ASN1Element ( name = "activationStateCode", isOptional =  false , hasTag =  true, tag = 2 , hasDefaultValue =  false  )
    
	private ActivationStateCode activationStateCode = null;
                
  
        
        public ActivationStateCode getActivationStateCode () {
            return this.activationStateCode;
        }

        public boolean isActivationStateCodeSelected () {
            return this.activationStateCode != null;
        }

        private void setActivationStateCode (ActivationStateCode value) {
            this.activationStateCode = value;
        }

        
        public void selectActivationStateCode (ActivationStateCode value) {
            this.activationStateCode = value;
                        
        }

        
  

	    public void initWithDefaults() {
	    }

        private static IASN1PreparedElementData preparedData = CoderFactory.getInstance().newPreparedElementData(Action1.class);
        public IASN1PreparedElementData getPreparedData() {
            return preparedData;
        }


    }
            