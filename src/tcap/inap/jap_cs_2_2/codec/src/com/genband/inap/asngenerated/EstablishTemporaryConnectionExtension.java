
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
    @ASN1Sequence ( name = "EstablishTemporaryConnectionExtension", isSet = false )
    public class EstablishTemporaryConnectionExtension implements IASN1PreparedElement {
            
        @ASN1Element ( name = "ttcCarrierInformation", isOptional =  true , hasTag =  true, tag = 0 , hasDefaultValue =  false  )
    
	private TtcCarrierInformation ttcCarrierInformation = null;
                
  
        
        public TtcCarrierInformation getTtcCarrierInformation () {
            return this.ttcCarrierInformation;
        }

        
        public boolean isTtcCarrierInformationPresent () {
            return this.ttcCarrierInformation != null;
        }
        

        public void setTtcCarrierInformation (TtcCarrierInformation value) {
            this.ttcCarrierInformation = value;
        }
        
  
                    
        
        public void initWithDefaults() {
            
        }

        private static IASN1PreparedElementData preparedData = CoderFactory.getInstance().newPreparedElementData(EstablishTemporaryConnectionExtension.class);
        public IASN1PreparedElementData getPreparedData() {
            return preparedData;
        }

            
    }
            