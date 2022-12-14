
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
    @ASN1Choice ( name = "GapInterval" )
    public class GapInterval implements IASN1PreparedElement {
            
        @ASN1Element ( name = "sCFOverloadGapInterval", isOptional =  false , hasTag =  true, tag = 343 , hasDefaultValue =  false  )
    
	private SCFOverloadGapInterval sCFOverloadGapInterval = null;
                
  
        @ASN1Element ( name = "serviceManagementSystemGapInterval", isOptional =  false , hasTag =  true, tag = 344 , hasDefaultValue =  false  )
    
	private ServiceManagementSystemGapInterval serviceManagementSystemGapInterval = null;
                
  
        
        public SCFOverloadGapInterval getSCFOverloadGapInterval () {
            return this.sCFOverloadGapInterval;
        }

        public boolean isSCFOverloadGapIntervalSelected () {
            return this.sCFOverloadGapInterval != null;
        }

        private void setSCFOverloadGapInterval (SCFOverloadGapInterval value) {
            this.sCFOverloadGapInterval = value;
        }

        
        public void selectSCFOverloadGapInterval (SCFOverloadGapInterval value) {
            this.sCFOverloadGapInterval = value;
            
                    setServiceManagementSystemGapInterval(null);
                            
        }

        
  
        
        public ServiceManagementSystemGapInterval getServiceManagementSystemGapInterval () {
            return this.serviceManagementSystemGapInterval;
        }

        public boolean isServiceManagementSystemGapIntervalSelected () {
            return this.serviceManagementSystemGapInterval != null;
        }

        private void setServiceManagementSystemGapInterval (ServiceManagementSystemGapInterval value) {
            this.serviceManagementSystemGapInterval = value;
        }

        
        public void selectServiceManagementSystemGapInterval (ServiceManagementSystemGapInterval value) {
            this.serviceManagementSystemGapInterval = value;
            
                    setSCFOverloadGapInterval(null);
                            
        }

        
  

	    public void initWithDefaults() {
	    }

        private static IASN1PreparedElementData preparedData = CoderFactory.getInstance().newPreparedElementData(GapInterval.class);
        public IASN1PreparedElementData getPreparedData() {
            return preparedData;
        }


    }
            