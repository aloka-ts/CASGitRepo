
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
    @ASN1Sequence ( name = "OTermSeizedArg", isSet = false )
    public class OTermSeizedArg implements IASN1PreparedElement {
            
        @ASN1Element ( name = "userID", isOptional =  false , hasTag =  false  , hasDefaultValue =  false  )
    
	private UserID userID = null;
                
  
        @ASN1Element ( name = "bearerCapability", isOptional =  false , hasTag =  false  , hasDefaultValue =  false  )
    
	private BearerCapability bearerCapability = null;
                
  
        @ASN1Element ( name = "notificationIndicator", isOptional =  true , hasTag =  false  , hasDefaultValue =  false  )
    
	private NotificationIndicator notificationIndicator = null;
                
  
        @ASN1Element ( name = "amp1", isOptional =  true , hasTag =  false  , hasDefaultValue =  false  )
    
	private Amp1 amp1 = null;
                
  
        @ASN1Element ( name = "amp2", isOptional =  true , hasTag =  false  , hasDefaultValue =  false  )
    
	private Amp2 amp2 = null;
                
  
        @ASN1Element ( name = "extensionParameter", isOptional =  true , hasTag =  false  , hasDefaultValue =  false  )
    
	private ExtensionParameter extensionParameter = null;
                
  
        
        public UserID getUserID () {
            return this.userID;
        }

        

        public void setUserID (UserID value) {
            this.userID = value;
        }
        
  
        
        public BearerCapability getBearerCapability () {
            return this.bearerCapability;
        }

        

        public void setBearerCapability (BearerCapability value) {
            this.bearerCapability = value;
        }
        
  
        
        public NotificationIndicator getNotificationIndicator () {
            return this.notificationIndicator;
        }

        
        public boolean isNotificationIndicatorPresent () {
            return this.notificationIndicator != null;
        }
        

        public void setNotificationIndicator (NotificationIndicator value) {
            this.notificationIndicator = value;
        }
        
  
        
        public Amp1 getAmp1 () {
            return this.amp1;
        }

        
        public boolean isAmp1Present () {
            return this.amp1 != null;
        }
        

        public void setAmp1 (Amp1 value) {
            this.amp1 = value;
        }
        
  
        
        public Amp2 getAmp2 () {
            return this.amp2;
        }

        
        public boolean isAmp2Present () {
            return this.amp2 != null;
        }
        

        public void setAmp2 (Amp2 value) {
            this.amp2 = value;
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

        private static IASN1PreparedElementData preparedData = CoderFactory.getInstance().newPreparedElementData(OTermSeizedArg.class);
        public IASN1PreparedElementData getPreparedData() {
            return preparedData;
        }

            
    }
            