
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
    @ASN1Sequence ( name = "M_CSI", isSet = false )
    public class M_CSI implements IASN1PreparedElement {
            
        @ASN1Element ( name = "mobilityTriggers", isOptional =  false , hasTag =  false  , hasDefaultValue =  false  )
    
	private MobilityTriggers mobilityTriggers = null;
                
  
        @ASN1Element ( name = "serviceKey", isOptional =  false , hasTag =  false  , hasDefaultValue =  false  )
    
	private ServiceKey serviceKey = null;
                
  
        @ASN1Element ( name = "gsmSCF-Address", isOptional =  false , hasTag =  true, tag = 0 , hasDefaultValue =  false  )
    
	private ISDN_AddressString gsmSCF_Address = null;
                
  
        @ASN1Element ( name = "extensionContainer", isOptional =  true , hasTag =  true, tag = 1 , hasDefaultValue =  false  )
    
	private ExtensionContainer extensionContainer = null;
                
  
        @ASN1Null ( name = "notificationToCSE" ) 
    
        @ASN1Element ( name = "notificationToCSE", isOptional =  true , hasTag =  true, tag = 2 , hasDefaultValue =  false  )
    
	private org.bn.types.NullObject notificationToCSE = null;
                
  
        @ASN1Null ( name = "csi-Active" ) 
    
        @ASN1Element ( name = "csi-Active", isOptional =  true , hasTag =  true, tag = 3 , hasDefaultValue =  false  )
    
	private org.bn.types.NullObject csi_Active = null;
                
  
        
        public MobilityTriggers getMobilityTriggers () {
            return this.mobilityTriggers;
        }

        

        public void setMobilityTriggers (MobilityTriggers value) {
            this.mobilityTriggers = value;
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

        private static IASN1PreparedElementData preparedData = CoderFactory.getInstance().newPreparedElementData(M_CSI.class);
        public IASN1PreparedElementData getPreparedData() {
            return preparedData;
        }

            
    }
            