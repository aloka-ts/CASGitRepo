
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
    @ASN1Sequence ( name = "CDMABandClassInformation", isSet = false )
    public class CDMABandClassInformation implements IASN1PreparedElement {
            
        @ASN1Element ( name = "cdmaBandClass", isOptional =  false , hasTag =  true, tag = 170 , hasDefaultValue =  false  )
    
	private CDMABandClass cdmaBandClass = null;
                
  
        @ASN1Element ( name = "cdmaMobileProtocolRevision", isOptional =  true , hasTag =  true, tag = 66 , hasDefaultValue =  false  )
    
	private CDMAMobileProtocolRevision cdmaMobileProtocolRevision = null;
                
  
        @ASN1Element ( name = "cdmaStationClassMark2", isOptional =  true , hasTag =  true, tag = 177 , hasDefaultValue =  false  )
    
	private CDMAStationClassMark2 cdmaStationClassMark2 = null;
                
  
        
        public CDMABandClass getCdmaBandClass () {
            return this.cdmaBandClass;
        }

        

        public void setCdmaBandClass (CDMABandClass value) {
            this.cdmaBandClass = value;
        }
        
  
        
        public CDMAMobileProtocolRevision getCdmaMobileProtocolRevision () {
            return this.cdmaMobileProtocolRevision;
        }

        
        public boolean isCdmaMobileProtocolRevisionPresent () {
            return this.cdmaMobileProtocolRevision != null;
        }
        

        public void setCdmaMobileProtocolRevision (CDMAMobileProtocolRevision value) {
            this.cdmaMobileProtocolRevision = value;
        }
        
  
        
        public CDMAStationClassMark2 getCdmaStationClassMark2 () {
            return this.cdmaStationClassMark2;
        }

        
        public boolean isCdmaStationClassMark2Present () {
            return this.cdmaStationClassMark2 != null;
        }
        

        public void setCdmaStationClassMark2 (CDMAStationClassMark2 value) {
            this.cdmaStationClassMark2 = value;
        }
        
  
                    
        
        public void initWithDefaults() {
            
        }

        private static IASN1PreparedElementData preparedData = CoderFactory.getInstance().newPreparedElementData(CDMABandClassInformation.class);
        public IASN1PreparedElementData getPreparedData() {
            return preparedData;
        }

            
    }
            