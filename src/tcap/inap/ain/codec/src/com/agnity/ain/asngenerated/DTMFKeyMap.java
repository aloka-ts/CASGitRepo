
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
    @ASN1Sequence ( name = "DTMFKeyMap", isSet = false )
    public class DTMFKeyMap implements IASN1PreparedElement {
            
        @ASN1Element ( name = "ignoreDigits", isOptional =  true , hasTag =  true, tag = 0 , hasDefaultValue =  false  )
    
	private IgnoreDigits ignoreDigits = null;
                
  
        @ASN1Element ( name = "terminateDigits", isOptional =  true , hasTag =  true, tag = 1 , hasDefaultValue =  false  )
    
	private TerminateDigits terminateDigits = null;
                
  
        @ASN1Element ( name = "resetDigits", isOptional =  true , hasTag =  true, tag = 2 , hasDefaultValue =  false  )
    
	private ResetDigits resetDigits = null;
                
  
        @ASN1Element ( name = "backspaceDigits", isOptional =  true , hasTag =  true, tag = 3 , hasDefaultValue =  false  )
    
	private BackspaceDigits backspaceDigits = null;
                
  
        
        public IgnoreDigits getIgnoreDigits () {
            return this.ignoreDigits;
        }

        
        public boolean isIgnoreDigitsPresent () {
            return this.ignoreDigits != null;
        }
        

        public void setIgnoreDigits (IgnoreDigits value) {
            this.ignoreDigits = value;
        }
        
  
        
        public TerminateDigits getTerminateDigits () {
            return this.terminateDigits;
        }

        
        public boolean isTerminateDigitsPresent () {
            return this.terminateDigits != null;
        }
        

        public void setTerminateDigits (TerminateDigits value) {
            this.terminateDigits = value;
        }
        
  
        
        public ResetDigits getResetDigits () {
            return this.resetDigits;
        }

        
        public boolean isResetDigitsPresent () {
            return this.resetDigits != null;
        }
        

        public void setResetDigits (ResetDigits value) {
            this.resetDigits = value;
        }
        
  
        
        public BackspaceDigits getBackspaceDigits () {
            return this.backspaceDigits;
        }

        
        public boolean isBackspaceDigitsPresent () {
            return this.backspaceDigits != null;
        }
        

        public void setBackspaceDigits (BackspaceDigits value) {
            this.backspaceDigits = value;
        }
        
  
                    
        
        public void initWithDefaults() {
            
        }

        private static IASN1PreparedElementData preparedData = CoderFactory.getInstance().newPreparedElementData(DTMFKeyMap.class);
        public IASN1PreparedElementData getPreparedData() {
            return preparedData;
        }

            
    }
            