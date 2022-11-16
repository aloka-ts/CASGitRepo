
package com.genband.inap.asngenerated;
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
    @ASN1Choice ( name = "TTCSpecificETChg" )
    public class TTCSpecificETChg implements IASN1PreparedElement {
            
        @ASN1Element ( name = "tTCSpecificEventTypeCharging", isOptional =  false , hasTag =  true, tag = 0 , hasDefaultValue =  false  )
    
	private TTCSpecificEventTypeCharging tTCSpecificEventTypeCharging = null;
                
  
        
        public TTCSpecificEventTypeCharging getTTCSpecificEventTypeCharging () {
            return this.tTCSpecificEventTypeCharging;
        }

        public boolean isTTCSpecificEventTypeChargingSelected () {
            return this.tTCSpecificEventTypeCharging != null;
        }

        private void setTTCSpecificEventTypeCharging (TTCSpecificEventTypeCharging value) {
            this.tTCSpecificEventTypeCharging = value;
        }

        
        public void selectTTCSpecificEventTypeCharging (TTCSpecificEventTypeCharging value) {
            this.tTCSpecificEventTypeCharging = value;
                        
        }

        
  

	    public void initWithDefaults() {
	    }

        private static IASN1PreparedElementData preparedData = CoderFactory.getInstance().newPreparedElementData(TTCSpecificETChg.class);
        public IASN1PreparedElementData getPreparedData() {
            return preparedData;
        }


    }
            