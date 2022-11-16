
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
    @ASN1Choice ( name = "Action4" )
    public class Action4 implements IASN1PreparedElement {
            
        @ASN1Element ( name = "activationStateCode", isOptional =  false , hasTag =  true, tag = 2 , hasDefaultValue =  false  )
    
	private ActivationStateCode activationStateCode = null;
                
  
        @ASN1Element ( name = "forwardingDn", isOptional =  false , hasTag =  false  , hasDefaultValue =  false  )
    
	private ForwardingDn forwardingDn = null;
                
  
        
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
            
                    setForwardingDn(null);
                            
        }

        
  
        
        public ForwardingDn getForwardingDn () {
            return this.forwardingDn;
        }

        public boolean isForwardingDnSelected () {
            return this.forwardingDn != null;
        }

        private void setForwardingDn (ForwardingDn value) {
            this.forwardingDn = value;
        }

        
        public void selectForwardingDn (ForwardingDn value) {
            this.forwardingDn = value;
            
                    setActivationStateCode(null);
                            
        }

        
  

	    public void initWithDefaults() {
	    }

        private static IASN1PreparedElementData preparedData = CoderFactory.getInstance().newPreparedElementData(Action4.class);
        public IASN1PreparedElementData getPreparedData() {
            return preparedData;
        }


    }
            