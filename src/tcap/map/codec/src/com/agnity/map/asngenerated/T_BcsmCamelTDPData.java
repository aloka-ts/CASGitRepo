
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
    @ASN1Sequence ( name = "T_BcsmCamelTDPData", isSet = false )
    public class T_BcsmCamelTDPData implements IASN1PreparedElement {
            
        @ASN1Element ( name = "t-BcsmTriggerDetectionPoint", isOptional =  false , hasTag =  false  , hasDefaultValue =  false  )
    
	private T_BcsmTriggerDetectionPoint t_BcsmTriggerDetectionPoint = null;
                
  
        @ASN1Element ( name = "serviceKey", isOptional =  false , hasTag =  false  , hasDefaultValue =  false  )
    
	private ServiceKey serviceKey = null;
                
  
        @ASN1Element ( name = "gsmSCF-Address", isOptional =  false , hasTag =  true, tag = 0 , hasDefaultValue =  false  )
    
	private ISDN_AddressString gsmSCF_Address = null;
                
  
        @ASN1Element ( name = "defaultCallHandling", isOptional =  false , hasTag =  true, tag = 1 , hasDefaultValue =  false  )
    
	private DefaultCallHandling defaultCallHandling = null;
                
  
        @ASN1Element ( name = "extensionContainer", isOptional =  true , hasTag =  true, tag = 2 , hasDefaultValue =  false  )
    
	private ExtensionContainer extensionContainer = null;
                
  
        
        public T_BcsmTriggerDetectionPoint getT_BcsmTriggerDetectionPoint () {
            return this.t_BcsmTriggerDetectionPoint;
        }

        

        public void setT_BcsmTriggerDetectionPoint (T_BcsmTriggerDetectionPoint value) {
            this.t_BcsmTriggerDetectionPoint = value;
        }
        
  
        
        public ServiceKey getServiceKey () {
            return this.serviceKey;
        }

        

        public void setServiceKey (ServiceKey value) {
            this.serviceKey = value;
        }
        
  
        
        public ISDN_AddressString getGsmSCF_Address () {
            return this.gsmSCF_Address;
        }

        

        public void setGsmSCF_Address (ISDN_AddressString value) {
            this.gsmSCF_Address = value;
        }
        
  
        
        public DefaultCallHandling getDefaultCallHandling () {
            return this.defaultCallHandling;
        }

        

        public void setDefaultCallHandling (DefaultCallHandling value) {
            this.defaultCallHandling = value;
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

        private static IASN1PreparedElementData preparedData = CoderFactory.getInstance().newPreparedElementData(T_BcsmCamelTDPData.class);
        public IASN1PreparedElementData getPreparedData() {
            return preparedData;
        }

            
    }
            