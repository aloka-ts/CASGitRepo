
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
    @ASN1Choice ( name = "DPSpecificCriteria" )
    public class DPSpecificCriteria implements IASN1PreparedElement {
            
        @ASN1Element ( name = "applicationTimer", isOptional =  false , hasTag =  true, tag = 1 , hasDefaultValue =  false  )
    
	private ApplicationTimer applicationTimer = null;
                
  
        
        public ApplicationTimer getApplicationTimer () {
            return this.applicationTimer;
        }

        public boolean isApplicationTimerSelected () {
            return this.applicationTimer != null;
        }

        private void setApplicationTimer (ApplicationTimer value) {
            this.applicationTimer = value;
        }

        
        public void selectApplicationTimer (ApplicationTimer value) {
            this.applicationTimer = value;
                        
        }

        
  

	    public void initWithDefaults() {
	    }

        private static IASN1PreparedElementData preparedData = CoderFactory.getInstance().newPreparedElementData(DPSpecificCriteria.class);
        public IASN1PreparedElementData getPreparedData() {
            return preparedData;
        }


    }
            