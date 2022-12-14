
package com.agnity.win.asngenerated;
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
    @ASN1Sequence ( name = "CDMAConnectionReferenceInformation", isSet = false )
    public class CDMAConnectionReferenceInformation implements IASN1PreparedElement {
            
        @ASN1Element ( name = "cdmaConnectionReference", isOptional =  false , hasTag =  true, tag = 208 , hasDefaultValue =  false  )
    
	private CDMAConnectionReference cdmaConnectionReference = null;
                
  
        @ASN1Element ( name = "cdmaServiceOption", isOptional =  false , hasTag =  true, tag = 175 , hasDefaultValue =  false  )
    
	private CDMAServiceOption cdmaServiceOption = null;
                
  
        @ASN1Element ( name = "cdmaState", isOptional =  true , hasTag =  true, tag = 213 , hasDefaultValue =  false  )
    
	private CDMAState cdmaState = null;
                
  
        @ASN1Element ( name = "dataPrivacyParameters", isOptional =  true , hasTag =  true, tag = 216 , hasDefaultValue =  false  )
    
	private DataPrivacyParameters dataPrivacyParameters = null;
                
  
        @ASN1Element ( name = "cdmaServiceOptionConnectionIdentifier", isOptional =  true , hasTag =  true, tag = 361 , hasDefaultValue =  false  )
    
	private CDMAServiceOptionConnectionIdentifier cdmaServiceOptionConnectionIdentifier = null;
                
  
        
        public CDMAConnectionReference getCdmaConnectionReference () {
            return this.cdmaConnectionReference;
        }

        

        public void setCdmaConnectionReference (CDMAConnectionReference value) {
            this.cdmaConnectionReference = value;
        }
        
  
        
        public CDMAServiceOption getCdmaServiceOption () {
            return this.cdmaServiceOption;
        }

        

        public void setCdmaServiceOption (CDMAServiceOption value) {
            this.cdmaServiceOption = value;
        }
        
  
        
        public CDMAState getCdmaState () {
            return this.cdmaState;
        }

        
        public boolean isCdmaStatePresent () {
            return this.cdmaState != null;
        }
        

        public void setCdmaState (CDMAState value) {
            this.cdmaState = value;
        }
        
  
        
        public DataPrivacyParameters getDataPrivacyParameters () {
            return this.dataPrivacyParameters;
        }

        
        public boolean isDataPrivacyParametersPresent () {
            return this.dataPrivacyParameters != null;
        }
        

        public void setDataPrivacyParameters (DataPrivacyParameters value) {
            this.dataPrivacyParameters = value;
        }
        
  
        
        public CDMAServiceOptionConnectionIdentifier getCdmaServiceOptionConnectionIdentifier () {
            return this.cdmaServiceOptionConnectionIdentifier;
        }

        
        public boolean isCdmaServiceOptionConnectionIdentifierPresent () {
            return this.cdmaServiceOptionConnectionIdentifier != null;
        }
        

        public void setCdmaServiceOptionConnectionIdentifier (CDMAServiceOptionConnectionIdentifier value) {
            this.cdmaServiceOptionConnectionIdentifier = value;
        }
        
  
                    
        
        public void initWithDefaults() {
            
        }

        private static IASN1PreparedElementData preparedData = CoderFactory.getInstance().newPreparedElementData(CDMAConnectionReferenceInformation.class);
        public IASN1PreparedElementData getPreparedData() {
            return preparedData;
        }

            
    }
            