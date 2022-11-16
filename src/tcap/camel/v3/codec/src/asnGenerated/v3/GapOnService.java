
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
    @ASN1Sequence ( name = "GapOnService", isSet = false )
    public class GapOnService implements IASN1PreparedElement {
            
        @ASN1Element ( name = "serviceKey", isOptional =  false , hasTag =  true, tag = 0 , hasDefaultValue =  false  )
    
	private ServiceKey serviceKey = null;
                
  
        
        public ServiceKey getServiceKey () {
            return this.serviceKey;
        }

        

        public void setServiceKey (ServiceKey value) {
            this.serviceKey = value;
        }
        
  
                    
        
        public void initWithDefaults() {
            
        }

        private static IASN1PreparedElementData preparedData = CoderFactory.getInstance().newPreparedElementData(GapOnService.class);
        public IASN1PreparedElementData getPreparedData() {
            return preparedData;
        }

            
    }
            