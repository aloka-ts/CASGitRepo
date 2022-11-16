
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
    @ASN1Sequence ( name = "SMS_CAMEL_TDP_Data", isSet = false )
    public class SMS_CAMEL_TDP_Data implements IASN1PreparedElement {
            
        @ASN1Element ( name = "sms-TriggerDetectionPoint", isOptional =  false , hasTag =  true, tag = 0 , hasDefaultValue =  false  )
    
	private SMS_TriggerDetectionPoint sms_TriggerDetectionPoint = null;
                
  
        @ASN1Element ( name = "serviceKey", isOptional =  false , hasTag =  true, tag = 1 , hasDefaultValue =  false  )
    
	private ServiceKey serviceKey = null;
                
  
        @ASN1Element ( name = "gsmSCF-Address", isOptional =  false , hasTag =  true, tag = 2 , hasDefaultValue =  false  )
    
	private ISDN_AddressString gsmSCF_Address = null;
                
  
        @ASN1Element ( name = "defaultSMS-Handling", isOptional =  false , hasTag =  true, tag = 3 , hasDefaultValue =  false  )
    
	private DefaultSMS_Handling defaultSMS_Handling = null;
                
  
        @ASN1Element ( name = "extensionContainer", isOptional =  true , hasTag =  true, tag = 4 , hasDefaultValue =  false  )
    
	private ExtensionContainer extensionContainer = null;
                
  
        
        public SMS_TriggerDetectionPoint getSms_TriggerDetectionPoint () {
            return this.sms_TriggerDetectionPoint;
        }

        

        public void setSms_TriggerDetectionPoint (SMS_TriggerDetectionPoint value) {
            this.sms_TriggerDetectionPoint = value;
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
        
  
        
        public DefaultSMS_Handling getDefaultSMS_Handling () {
            return this.defaultSMS_Handling;
        }

        

        public void setDefaultSMS_Handling (DefaultSMS_Handling value) {
            this.defaultSMS_Handling = value;
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

        private static IASN1PreparedElementData preparedData = CoderFactory.getInstance().newPreparedElementData(SMS_CAMEL_TDP_Data.class);
        public IASN1PreparedElementData getPreparedData() {
            return preparedData;
        }

            
    }
            