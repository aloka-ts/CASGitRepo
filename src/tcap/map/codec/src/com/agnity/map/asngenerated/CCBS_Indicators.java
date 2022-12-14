
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
    @ASN1Sequence ( name = "CCBS_Indicators", isSet = false )
    public class CCBS_Indicators implements IASN1PreparedElement {
            
        @ASN1Null ( name = "ccbs-Possible" ) 
    
        @ASN1Element ( name = "ccbs-Possible", isOptional =  true , hasTag =  true, tag = 0 , hasDefaultValue =  false  )
    
	private org.bn.types.NullObject ccbs_Possible = null;
                
  
        @ASN1Null ( name = "keepCCBS-CallIndicator" ) 
    
        @ASN1Element ( name = "keepCCBS-CallIndicator", isOptional =  true , hasTag =  true, tag = 1 , hasDefaultValue =  false  )
    
	private org.bn.types.NullObject keepCCBS_CallIndicator = null;
                
  
        @ASN1Element ( name = "extensionContainer", isOptional =  true , hasTag =  true, tag = 2 , hasDefaultValue =  false  )
    
	private ExtensionContainer extensionContainer = null;
                
  
        
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

        private static IASN1PreparedElementData preparedData = CoderFactory.getInstance().newPreparedElementData(CCBS_Indicators.class);
        public IASN1PreparedElementData getPreparedData() {
            return preparedData;
        }

            
    }
            