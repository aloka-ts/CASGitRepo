
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
    @ASN1Sequence ( name = "WINCapability", isSet = true )
    public class WINCapability implements IASN1PreparedElement {
            
        @ASN1Element ( name = "triggerCapability", isOptional =  true , hasTag =  true, tag = 277 , hasDefaultValue =  false  )
    
	private TriggerCapability triggerCapability = null;
                
  
        @ASN1Element ( name = "wINOperationsCapability", isOptional =  true , hasTag =  true, tag = 281 , hasDefaultValue =  false  )
    
	private WINOperationsCapability wINOperationsCapability = null;
                
  
        
        public TriggerCapability getTriggerCapability () {
            return this.triggerCapability;
        }

        
        public boolean isTriggerCapabilityPresent () {
            return this.triggerCapability != null;
        }
        

        public void setTriggerCapability (TriggerCapability value) {
            this.triggerCapability = value;
        }
        
  
        
        public WINOperationsCapability getWINOperationsCapability () {
            return this.wINOperationsCapability;
        }

        
        public boolean isWINOperationsCapabilityPresent () {
            return this.wINOperationsCapability != null;
        }
        

        public void setWINOperationsCapability (WINOperationsCapability value) {
            this.wINOperationsCapability = value;
        }
        
  
                    
        
        public void initWithDefaults() {
            
        }

        private static IASN1PreparedElementData preparedData = CoderFactory.getInstance().newPreparedElementData(WINCapability.class);
        public IASN1PreparedElementData getPreparedData() {
            return preparedData;
        }

            
    }
            