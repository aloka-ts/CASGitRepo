
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
    @ASN1Sequence ( name = "SS_CamelData", isSet = false )
    public class SS_CamelData implements IASN1PreparedElement {
            
        @ASN1Element ( name = "ss-EventList", isOptional =  false , hasTag =  false  , hasDefaultValue =  false  )
    
	private SS_EventList ss_EventList = null;
                
  
        @ASN1Element ( name = "gsmSCF-Address", isOptional =  false , hasTag =  false  , hasDefaultValue =  false  )
    
	private ISDN_AddressString gsmSCF_Address = null;
                
  
        @ASN1Element ( name = "extensionContainer", isOptional =  true , hasTag =  true, tag = 0 , hasDefaultValue =  false  )
    
	private ExtensionContainer extensionContainer = null;
                
  
        
        public SS_EventList getSs_EventList () {
            return this.ss_EventList;
        }

        

        public void setSs_EventList (SS_EventList value) {
            this.ss_EventList = value;
        }
        
  
        
        public ISDN_AddressString getGsmSCF_Address () {
            return this.gsmSCF_Address;
        }

        

        public void setGsmSCF_Address (ISDN_AddressString value) {
            this.gsmSCF_Address = value;
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

        private static IASN1PreparedElementData preparedData = CoderFactory.getInstance().newPreparedElementData(SS_CamelData.class);
        public IASN1PreparedElementData getPreparedData() {
            return preparedData;
        }

            
    }
            