
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
    @ASN1Sequence ( name = "SendChargingInformationGPRSArg", isSet = false )
    public class SendChargingInformationGPRSArg implements IASN1PreparedElement {
            
        @ASN1Element ( name = "sCIGPRSBillingChargingCharacteristics", isOptional =  false , hasTag =  true, tag = 0 , hasDefaultValue =  false  )
    
	private SCIGPRSBillingChargingCharacteristics sCIGPRSBillingChargingCharacteristics = null;
                
  
        
        public SCIGPRSBillingChargingCharacteristics getSCIGPRSBillingChargingCharacteristics () {
            return this.sCIGPRSBillingChargingCharacteristics;
        }

        

        public void setSCIGPRSBillingChargingCharacteristics (SCIGPRSBillingChargingCharacteristics value) {
            this.sCIGPRSBillingChargingCharacteristics = value;
        }
        
  
                    
        
        public void initWithDefaults() {
            
        }

        private static IASN1PreparedElementData preparedData = CoderFactory.getInstance().newPreparedElementData(SendChargingInformationGPRSArg.class);
        public IASN1PreparedElementData getPreparedData() {
            return preparedData;
        }

            
    }
            