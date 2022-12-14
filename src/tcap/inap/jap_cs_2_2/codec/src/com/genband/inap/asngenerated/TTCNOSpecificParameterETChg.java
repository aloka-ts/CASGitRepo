
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
    @ASN1Choice ( name = "TTCNOSpecificParameterETChg" )
    public class TTCNOSpecificParameterETChg implements IASN1PreparedElement {
            
        @ASN1Element ( name = "tTCSpecificETChg", isOptional =  false , hasTag =  true, tag = 0 , hasDefaultValue =  false  )
    
	private TTCSpecificETChg tTCSpecificETChg = null;
                
  
        
        public TTCSpecificETChg getTTCSpecificETChg () {
            return this.tTCSpecificETChg;
        }

        public boolean isTTCSpecificETChgSelected () {
            return this.tTCSpecificETChg != null;
        }

        private void setTTCSpecificETChg (TTCSpecificETChg value) {
            this.tTCSpecificETChg = value;
        }

        
        public void selectTTCSpecificETChg (TTCSpecificETChg value) {
            this.tTCSpecificETChg = value;
                        
        }

        
  

	    public void initWithDefaults() {
	    }

        private static IASN1PreparedElementData preparedData = CoderFactory.getInstance().newPreparedElementData(TTCNOSpecificParameterETChg.class);
        public IASN1PreparedElementData getPreparedData() {
            return preparedData;
        }


    }
            