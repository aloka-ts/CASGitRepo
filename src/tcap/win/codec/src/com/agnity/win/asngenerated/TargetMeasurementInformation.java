
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
    @ASN1Sequence ( name = "TargetMeasurementInformation", isSet = false )
    public class TargetMeasurementInformation implements IASN1PreparedElement {
            
        @ASN1Element ( name = "targetCellID", isOptional =  false , hasTag =  true, tag = 3 , hasDefaultValue =  false  )
    
	private TargetCellID targetCellID = null;
                
  
        @ASN1Element ( name = "signalQuality", isOptional =  false , hasTag =  true, tag = 11 , hasDefaultValue =  false  )
    
	private SignalQuality signalQuality = null;
                
  
        
        public TargetCellID getTargetCellID () {
            return this.targetCellID;
        }

        

        public void setTargetCellID (TargetCellID value) {
            this.targetCellID = value;
        }
        
  
        
        public SignalQuality getSignalQuality () {
            return this.signalQuality;
        }

        

        public void setSignalQuality (SignalQuality value) {
            this.signalQuality = value;
        }
        
  
                    
        
        public void initWithDefaults() {
            
        }

        private static IASN1PreparedElementData preparedData = CoderFactory.getInstance().newPreparedElementData(TargetMeasurementInformation.class);
        public IASN1PreparedElementData getPreparedData() {
            return preparedData;
        }

            
    }
            