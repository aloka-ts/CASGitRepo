
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
    @ASN1Sequence ( name = "ActivityTestArg", isSet = false )
    public class ActivityTestArg implements IASN1PreparedElement {
            
        @ASN1Element ( name = "transID", isOptional =  false , hasTag =  false  , hasDefaultValue =  false  )
    
	private TransID transID = null;
                
  
        @ASN1Element ( name = "extensionParameter", isOptional =  true , hasTag =  false  , hasDefaultValue =  false  )
    
	private ExtensionParameter extensionParameter = null;
                
  
        
        public TransID getTransID () {
            return this.transID;
        }

        

        public void setTransID (TransID value) {
            this.transID = value;
        }
        
  
        
        public ExtensionParameter getExtensionParameter () {
            return this.extensionParameter;
        }

        
        public boolean isExtensionParameterPresent () {
            return this.extensionParameter != null;
        }
        

        public void setExtensionParameter (ExtensionParameter value) {
            this.extensionParameter = value;
        }
        
  
                    
        
        public void initWithDefaults() {
            
        }

        private static IASN1PreparedElementData preparedData = CoderFactory.getInstance().newPreparedElementData(ActivityTestArg.class);
        public IASN1PreparedElementData getPreparedData() {
            return preparedData;
        }

            
    }
            