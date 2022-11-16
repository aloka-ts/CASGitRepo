
package com.agnity.map.asngenerated;
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
    @ASN1Sequence ( name = "NAEA_PreferredCI", isSet = false )
    public class NAEA_PreferredCI implements IASN1PreparedElement {
            
        @ASN1Element ( name = "naea-PreferredCIC", isOptional =  false , hasTag =  true, tag = 0 , hasDefaultValue =  false  )
    
	private NAEA_CIC naea_PreferredCIC = null;
                
  
        @ASN1Element ( name = "extensionContainer", isOptional =  true , hasTag =  true, tag = 1 , hasDefaultValue =  false  )
    
	private ExtensionContainer extensionContainer = null;
                
  
        
        public NAEA_CIC getNaea_PreferredCIC () {
            return this.naea_PreferredCIC;
        }

        

        public void setNaea_PreferredCIC (NAEA_CIC value) {
            this.naea_PreferredCIC = value;
        }
        
  
        
        public ExtensionContainer getExtensionContainer () {
            return this.extensionContainer;
        }

        
        public boolean isExtensionContainerPresent () {
            return this.extensionContainer != null;
        }
        

        public void setExtensionContainer (ExtensionContainer value) {
            this.extensionContainer = value;
        }
        
  
                    
        
        public void initWithDefaults() {
            
        }

        private static IASN1PreparedElementData preparedData = CoderFactory.getInstance().newPreparedElementData(NAEA_PreferredCI.class);
        public IASN1PreparedElementData getPreparedData() {
            return preparedData;
        }

            
    }
            