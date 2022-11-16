
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
    @ASN1Sequence ( name = "MPCAddressList", isSet = true )
    public class MPCAddressList implements IASN1PreparedElement {
            
        @ASN1Element ( name = "mpcAddress", isOptional =  false , hasTag =  true, tag = 370 , hasDefaultValue =  false  )
    
	private MPCAddress mpcAddress = null;
                
  
        @ASN1Element ( name = "mpcAddress2", isOptional =  true , hasTag =  true, tag = 370 , hasDefaultValue =  false  )
    
	private MPCAddress mpcAddress2 = null;
                
  
        
        public MPCAddress getMpcAddress () {
            return this.mpcAddress;
        }

        

        public void setMpcAddress (MPCAddress value) {
            this.mpcAddress = value;
        }
        
  
        
        public MPCAddress getMpcAddress2 () {
            return this.mpcAddress2;
        }

        
        public boolean isMpcAddress2Present () {
            return this.mpcAddress2 != null;
        }
        

        public void setMpcAddress2 (MPCAddress value) {
            this.mpcAddress2 = value;
        }
        
  
                    
        
        public void initWithDefaults() {
            
        }

        private static IASN1PreparedElementData preparedData = CoderFactory.getInstance().newPreparedElementData(MPCAddressList.class);
        public IASN1PreparedElementData getPreparedData() {
            return preparedData;
        }

            
    }
            