
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
    @ASN1Sequence ( name = "QueryRequestArg", isSet = false )
    public class QueryRequestArg implements IASN1PreparedElement {
            
        @ASN1Element ( name = "userID", isOptional =  true , hasTag =  false  , hasDefaultValue =  false  )
    
	private UserID userID = null;
                
  
        @ASN1Element ( name = "amp1", isOptional =  true , hasTag =  false  , hasDefaultValue =  false  )
    
	private Amp1 amp1 = null;
                
  
        @ASN1Element ( name = "amp2", isOptional =  true , hasTag =  false  , hasDefaultValue =  false  )
    
	private Amp2 amp2 = null;
                
  
        @ASN1Element ( name = "extensionParameter", isOptional =  true , hasTag =  false  , hasDefaultValue =  false  )
    
	private ExtensionParameter extensionParameter = null;
                
  
        @ASN1Element ( name = "controlEncountered", isOptional =  true , hasTag =  false  , hasDefaultValue =  false  )
    
	private ControlEncountered controlEncountered = null;
                
  
        @ASN1Element ( name = "provideInfo", isOptional =  true , hasTag =  false  , hasDefaultValue =  false  )
    
	private ProvideInfo provideInfo = null;
                
  
        
        public UserID getUserID () {
            return this.userID;
        }

        
        public boolean isUserIDPresent () {
            return this.userID != null;
        }
        

        public void setUserID (UserID value) {
            this.userID = value;
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
        
  
        
        public ControlEncountered getControlEncountered () {
            return this.controlEncountered;
        }

        
        public boolean isControlEncounteredPresent () {
            return this.controlEncountered != null;
        }
        

        public void setControlEncountered (ControlEncountered value) {
            this.controlEncountered = value;
        }
        
  
        
        public ProvideInfo getProvideInfo () {
            return this.provideInfo;
        }

        
        public boolean isProvideInfoPresent () {
            return this.provideInfo != null;
        }
        

        public void setProvideInfo (ProvideInfo value) {
            this.provideInfo = value;
        }
        
  
                    
        
        public void initWithDefaults() {
            
        }

        private static IASN1PreparedElementData preparedData = CoderFactory.getInstance().newPreparedElementData(QueryRequestArg.class);
        public IASN1PreparedElementData getPreparedData() {
            return preparedData;
        }

            
    }
            