
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
    @ASN1Sequence ( name = "AnyTimeInterrogationArg", isSet = false )
    public class AnyTimeInterrogationArg implements IASN1PreparedElement {
            
        @ASN1Element ( name = "subscriberIdentity", isOptional =  false , hasTag =  true, tag = 0 , hasDefaultValue =  false  )
    
	private SubscriberIdentity subscriberIdentity = null;
                
  
        @ASN1Element ( name = "requestedInfo", isOptional =  false , hasTag =  true, tag = 1 , hasDefaultValue =  false  )
    
	private RequestedInfo requestedInfo = null;
                
  
        @ASN1Element ( name = "gsmSCF-Address", isOptional =  false , hasTag =  true, tag = 3 , hasDefaultValue =  false  )
    
	private ISDN_AddressString gsmSCF_Address = null;
                
  
        @ASN1Element ( name = "extensionContainer", isOptional =  true , hasTag =  true, tag = 2 , hasDefaultValue =  false  )
    
	private ExtensionContainer extensionContainer = null;
                
  
        
        public SubscriberIdentity getSubscriberIdentity () {
            return this.subscriberIdentity;
        }

        

        public void setSubscriberIdentity (SubscriberIdentity value) {
            this.subscriberIdentity = value;
        }
        
  
        
        public RequestedInfo getRequestedInfo () {
            return this.requestedInfo;
        }

        

        public void setRequestedInfo (RequestedInfo value) {
            this.requestedInfo = value;
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

        private static IASN1PreparedElementData preparedData = CoderFactory.getInstance().newPreparedElementData(AnyTimeInterrogationArg.class);
        public IASN1PreparedElementData getPreparedData() {
            return preparedData;
        }

            
    }
            