
package asnGenerated;
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
    @ASN1Choice ( name = "AudibleIndicator" )
    public class AudibleIndicator implements IASN1PreparedElement {
            @ASN1Boolean( name = "" )
    
        @ASN1Element ( name = "tone", isOptional =  false , hasTag =  false  , hasDefaultValue =  false  )
    
	private Boolean tone = null;
                
  
        @ASN1Element ( name = "burstList", isOptional =  false , hasTag =  true, tag = 1 , hasDefaultValue =  false  )
    
	private BurstList burstList = null;
                
  
        
        public Boolean getTone () {
            return this.tone;
        }

        public boolean isToneSelected () {
            return this.tone != null;
        }

        private void setTone (Boolean value) {
            this.tone = value;
        }

        
        public void selectTone (Boolean value) {
            this.tone = value;
            
                    setBurstList(null);
                            
        }

        
  
        
        public BurstList getBurstList () {
            return this.burstList;
        }

        public boolean isBurstListSelected () {
            return this.burstList != null;
        }

        private void setBurstList (BurstList value) {
            this.burstList = value;
        }

        
        public void selectBurstList (BurstList value) {
            this.burstList = value;
            
                    setTone(null);
                            
        }

        
  

	    public void initWithDefaults() {
	    }

        private static IASN1PreparedElementData preparedData = CoderFactory.getInstance().newPreparedElementData(AudibleIndicator.class);
        public IASN1PreparedElementData getPreparedData() {
            return preparedData;
        }


    }
            