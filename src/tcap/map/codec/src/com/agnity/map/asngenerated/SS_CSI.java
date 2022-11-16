
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
    @ASN1Sequence ( name = "SS_CSI", isSet = false )
    public class SS_CSI implements IASN1PreparedElement {
            
        @ASN1Element ( name = "ss-CamelData", isOptional =  false , hasTag =  false  , hasDefaultValue =  false  )
    
	private SS_CamelData ss_CamelData = null;
                
  
        @ASN1Element ( name = "extensionContainer", isOptional =  true , hasTag =  false  , hasDefaultValue =  false  )
    
	private ExtensionContainer extensionContainer = null;
                
  
        @ASN1Null ( name = "notificationToCSE" ) 
    
        @ASN1Element ( name = "notificationToCSE", isOptional =  true , hasTag =  true, tag = 0 , hasDefaultValue =  false  )
    
	private org.bn.types.NullObject notificationToCSE = null;
                
  
        @ASN1Null ( name = "csi-Active" ) 
    
        @ASN1Element ( name = "csi-Active", isOptional =  true , hasTag =  true, tag = 1 , hasDefaultValue =  false  )
    
	private org.bn.types.NullObject csi_Active = null;
                
  
        
        public SS_CamelData getSs_CamelData () {
            return this.ss_CamelData;
        }

        

        public void setSs_CamelData (SS_CamelData value) {
            this.ss_CamelData = value;
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

        private static IASN1PreparedElementData preparedData = CoderFactory.getInstance().newPreparedElementData(SS_CSI.class);
        public IASN1PreparedElementData getPreparedData() {
            return preparedData;
        }

            
    }
            