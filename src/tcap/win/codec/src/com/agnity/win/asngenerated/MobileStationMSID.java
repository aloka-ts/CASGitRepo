
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
    @ASN1Choice ( name = "MobileStationMSID" )
    public class MobileStationMSID implements IASN1PreparedElement {
            
        @ASN1Element ( name = "mobileStationMIN", isOptional =  false , hasTag =  true, tag = 184 , hasDefaultValue =  false  )
    
	private MobileStationMIN mobileStationMIN = null;
                
  
        @ASN1Element ( name = "mobileStationIMSI", isOptional =  false , hasTag =  true, tag = 286 , hasDefaultValue =  false  )
    
	private MobileStationIMSI mobileStationIMSI = null;
                
  
        
        public MobileStationMIN getMobileStationMIN () {
            return this.mobileStationMIN;
        }

        public boolean isMobileStationMINSelected () {
            return this.mobileStationMIN != null;
        }

        private void setMobileStationMIN (MobileStationMIN value) {
            this.mobileStationMIN = value;
        }

        
        public void selectMobileStationMIN (MobileStationMIN value) {
            this.mobileStationMIN = value;
            
                    setMobileStationIMSI(null);
                            
        }

        
  
        
        public MobileStationIMSI getMobileStationIMSI () {
            return this.mobileStationIMSI;
        }

        public boolean isMobileStationIMSISelected () {
            return this.mobileStationIMSI != null;
        }

        private void setMobileStationIMSI (MobileStationIMSI value) {
            this.mobileStationIMSI = value;
        }

        
        public void selectMobileStationIMSI (MobileStationIMSI value) {
            this.mobileStationIMSI = value;
            
                    setMobileStationMIN(null);
                            
        }

        
  

	    public void initWithDefaults() {
	    }

        private static IASN1PreparedElementData preparedData = CoderFactory.getInstance().newPreparedElementData(MobileStationMSID.class);
        public IASN1PreparedElementData getPreparedData() {
            return preparedData;
        }


    }
            