
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
    @ASN1Sequence ( name = "AOCBeforeAnswer", isSet = false )
    public class AOCBeforeAnswer implements IASN1PreparedElement {
            
        @ASN1Element ( name = "aOCInitial", isOptional =  false , hasTag =  true, tag = 0 , hasDefaultValue =  false  )
    
	private CAI_Gsm0224 aOCInitial = null;
                
  
        @ASN1Element ( name = "aOCSubsequent", isOptional =  true , hasTag =  true, tag = 1 , hasDefaultValue =  false  )
    
	private AOCSubsequent aOCSubsequent = null;
                
  
        
        public CAI_Gsm0224 getAOCInitial () {
            return this.aOCInitial;
        }

        

        public void setAOCInitial (CAI_Gsm0224 value) {
            this.aOCInitial = value;
        }
        
  
        
        public AOCSubsequent getAOCSubsequent () {
            return this.aOCSubsequent;
        }

        
        public boolean isAOCSubsequentPresent () {
            return this.aOCSubsequent != null;
        }
        

        public void setAOCSubsequent (AOCSubsequent value) {
            this.aOCSubsequent = value;
        }
        
  
                    
        
        public void initWithDefaults() {
            
        }

        private static IASN1PreparedElementData preparedData = CoderFactory.getInstance().newPreparedElementData(AOCBeforeAnswer.class);
        public IASN1PreparedElementData getPreparedData() {
            return preparedData;
        }

            
    }
            