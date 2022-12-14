
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
    @ASN1Choice ( name = "Action2" )
    public class Action2 implements IASN1PreparedElement {
            
        @ASN1Element ( name = "activationStateCode", isOptional =  false , hasTag =  true, tag = 2 , hasDefaultValue =  false  )
    
	private ActivationStateCode activationStateCode = null;
                
  
        @ASN1Element ( name = "delayInterval", isOptional =  false , hasTag =  true, tag = 3 , hasDefaultValue =  false  )
    
	private DelayInterval delayInterval = null;
                
  
        
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
            
                    setDelayInterval(null);
                            
        }

        
  
        
        public DelayInterval getDelayInterval () {
            return this.delayInterval;
        }

        public boolean isDelayIntervalSelected () {
            return this.delayInterval != null;
        }

        private void setDelayInterval (DelayInterval value) {
            this.delayInterval = value;
        }

        
        public void selectDelayInterval (DelayInterval value) {
            this.delayInterval = value;
            
                    setActivationStateCode(null);
                            
        }

        
  

	    public void initWithDefaults() {
	    }

        private static IASN1PreparedElementData preparedData = CoderFactory.getInstance().newPreparedElementData(Action2.class);
        public IASN1PreparedElementData getPreparedData() {
            return preparedData;
        }


    }
            