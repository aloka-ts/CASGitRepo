
package asnGenerated.v2;
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
    @ASN1Sequence ( name = "InitialDPArgExtension", isSet = false )
    public class InitialDPArgExtension implements IASN1PreparedElement {
            
        @ASN1Element ( name = "naCarrierInformation", isOptional =  true , hasTag =  true, tag = 0 , hasDefaultValue =  false  )
    
	private NACarrierInformation naCarrierInformation = null;
                
  
        @ASN1Element ( name = "gmscAddress", isOptional =  true , hasTag =  true, tag = 1 , hasDefaultValue =  false  )
    
	private ISDN_AddressString gmscAddress = null;
                
  
        
        public NACarrierInformation getNaCarrierInformation () {
            return this.naCarrierInformation;
        }

        
        public boolean isNaCarrierInformationPresent () {
            return this.naCarrierInformation != null;
        }
        

        public void setNaCarrierInformation (NACarrierInformation value) {
            this.naCarrierInformation = value;
        }
        
  
        
        public ISDN_AddressString getGmscAddress () {
            return this.gmscAddress;
        }

        
        public boolean isGmscAddressPresent () {
            return this.gmscAddress != null;
        }
        

        public void setGmscAddress (ISDN_AddressString value) {
            this.gmscAddress = value;
        }
        
  
                    
        
        public void initWithDefaults() {
            
        }

        private static IASN1PreparedElementData preparedData = CoderFactory.getInstance().newPreparedElementData(InitialDPArgExtension.class);
        public IASN1PreparedElementData getPreparedData() {
            return preparedData;
        }

            
    }
            