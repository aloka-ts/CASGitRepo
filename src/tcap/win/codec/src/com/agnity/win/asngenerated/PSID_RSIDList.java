
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
    @ASN1Sequence ( name = "PSID_RSIDList", isSet = false )
    public class PSID_RSIDList implements IASN1PreparedElement {
            
        @ASN1Element ( name = "pSID-RSIDInformation", isOptional =  false , hasTag =  true, tag = 202 , hasDefaultValue =  false  )
    
	private PSID_RSIDInformation pSID_RSIDInformation = null;
                
  
        @ASN1Element ( name = "pSID-RSIDInformation1", isOptional =  true , hasTag =  true, tag = 202 , hasDefaultValue =  false  )
    
	private PSID_RSIDInformation pSID_RSIDInformation1 = null;
                
  
        
        public PSID_RSIDInformation getPSID_RSIDInformation () {
            return this.pSID_RSIDInformation;
        }

        

        public void setPSID_RSIDInformation (PSID_RSIDInformation value) {
            this.pSID_RSIDInformation = value;
        }
        
  
        
        public PSID_RSIDInformation getPSID_RSIDInformation1 () {
            return this.pSID_RSIDInformation1;
        }

        
        public boolean isPSID_RSIDInformation1Present () {
            return this.pSID_RSIDInformation1 != null;
        }
        

        public void setPSID_RSIDInformation1 (PSID_RSIDInformation value) {
            this.pSID_RSIDInformation1 = value;
        }
        
  
                    
        
        public void initWithDefaults() {
            
        }

        private static IASN1PreparedElementData preparedData = CoderFactory.getInstance().newPreparedElementData(PSID_RSIDList.class);
        public IASN1PreparedElementData getPreparedData() {
            return preparedData;
        }

            
    }
            