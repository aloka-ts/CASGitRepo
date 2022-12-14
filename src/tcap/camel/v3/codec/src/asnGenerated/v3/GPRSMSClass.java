
package asnGenerated.v3;
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
    @ASN1Sequence ( name = "GPRSMSClass", isSet = false )
    public class GPRSMSClass implements IASN1PreparedElement {
            
        @ASN1Element ( name = "mSNetworkCapability", isOptional =  false , hasTag =  true, tag = 0 , hasDefaultValue =  false  )
    
	private MSNetworkCapability mSNetworkCapability = null;
                
  
        @ASN1Element ( name = "mSRadioAccessCapability", isOptional =  true , hasTag =  true, tag = 1 , hasDefaultValue =  false  )
    
	private MSRadioAccessCapability mSRadioAccessCapability = null;
                
  
        
        public MSNetworkCapability getMSNetworkCapability () {
            return this.mSNetworkCapability;
        }

        

        public void setMSNetworkCapability (MSNetworkCapability value) {
            this.mSNetworkCapability = value;
        }
        
  
        
        public MSRadioAccessCapability getMSRadioAccessCapability () {
            return this.mSRadioAccessCapability;
        }

        
        public boolean isMSRadioAccessCapabilityPresent () {
            return this.mSRadioAccessCapability != null;
        }
        

        public void setMSRadioAccessCapability (MSRadioAccessCapability value) {
            this.mSRadioAccessCapability = value;
        }
        
  
                    
        
        public void initWithDefaults() {
            
        }

        private static IASN1PreparedElementData preparedData = CoderFactory.getInstance().newPreparedElementData(GPRSMSClass.class);
        public IASN1PreparedElementData getPreparedData() {
            return preparedData;
        }

            
    }
            