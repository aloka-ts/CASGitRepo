
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
    @ASN1Sequence ( name = "CallForwardingData", isSet = false )
    public class CallForwardingData implements IASN1PreparedElement {
            
        @ASN1Element ( name = "forwardingFeatureList", isOptional =  false , hasTag =  false  , hasDefaultValue =  false  )
    
	private Ext_ForwFeatureList forwardingFeatureList = null;
                
  
        @ASN1Null ( name = "notificationToCSE" ) 
    
        @ASN1Element ( name = "notificationToCSE", isOptional =  true , hasTag =  false  , hasDefaultValue =  false  )
    
	private org.bn.types.NullObject notificationToCSE = null;
                
  
        @ASN1Element ( name = "extensionContainer", isOptional =  true , hasTag =  true, tag = 0 , hasDefaultValue =  false  )
    
	private ExtensionContainer extensionContainer = null;
                
  
        
        public Ext_ForwFeatureList getForwardingFeatureList () {
            return this.forwardingFeatureList;
        }

        

        public void setForwardingFeatureList (Ext_ForwFeatureList value) {
            this.forwardingFeatureList = value;
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

        private static IASN1PreparedElementData preparedData = CoderFactory.getInstance().newPreparedElementData(CallForwardingData.class);
        public IASN1PreparedElementData getPreparedData() {
            return preparedData;
        }

            
    }
            