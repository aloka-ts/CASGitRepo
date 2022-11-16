
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
    @ASN1Sequence ( name = "CDMATargetMeasurementInformation", isSet = false )
    public class CDMATargetMeasurementInformation implements IASN1PreparedElement {
            
        @ASN1Element ( name = "targetCellID", isOptional =  false , hasTag =  true, tag = 3 , hasDefaultValue =  false  )
    
	private TargetCellID targetCellID = null;
                
  
        @ASN1Element ( name = "cdmaSignalQuality", isOptional =  false , hasTag =  true, tag = 64 , hasDefaultValue =  false  )
    
	private CDMASignalQuality cdmaSignalQuality = null;
                
  
        @ASN1Element ( name = "cdmaTargetOneWayDelay", isOptional =  true , hasTag =  true, tag = 61 , hasDefaultValue =  false  )
    
	private CDMATargetOneWayDelay cdmaTargetOneWayDelay = null;
                
  
        
        public TargetCellID getTargetCellID () {
            return this.targetCellID;
        }

        

        public void setTargetCellID (TargetCellID value) {
            this.targetCellID = value;
        }
        
  
        
        public CDMASignalQuality getCdmaSignalQuality () {
            return this.cdmaSignalQuality;
        }

        

        public void setCdmaSignalQuality (CDMASignalQuality value) {
            this.cdmaSignalQuality = value;
        }
        
  
        
        public CDMATargetOneWayDelay getCdmaTargetOneWayDelay () {
            return this.cdmaTargetOneWayDelay;
        }

        
        public boolean isCdmaTargetOneWayDelayPresent () {
            return this.cdmaTargetOneWayDelay != null;
        }
        

        public void setCdmaTargetOneWayDelay (CDMATargetOneWayDelay value) {
            this.cdmaTargetOneWayDelay = value;
        }
        
  
                    
        
        public void initWithDefaults() {
            
        }

        private static IASN1PreparedElementData preparedData = CoderFactory.getInstance().newPreparedElementData(CDMATargetMeasurementInformation.class);
        public IASN1PreparedElementData getPreparedData() {
            return preparedData;
        }

            
    }
            