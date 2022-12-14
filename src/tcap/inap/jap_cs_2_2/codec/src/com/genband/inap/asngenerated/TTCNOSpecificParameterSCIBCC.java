
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
    @ASN1Choice ( name = "TTCNOSpecificParameterSCIBCC" )
    public class TTCNOSpecificParameterSCIBCC implements IASN1PreparedElement {
            
        @ASN1Element ( name = "tTCSpecificSCIBCC", isOptional =  false , hasTag =  true, tag = 0 , hasDefaultValue =  false  )
    
	private TTCSpecificSCIBCC tTCSpecificSCIBCC = null;
                
  
        
        public TTCSpecificSCIBCC getTTCSpecificSCIBCC () {
            return this.tTCSpecificSCIBCC;
        }

        public boolean isTTCSpecificSCIBCCSelected () {
            return this.tTCSpecificSCIBCC != null;
        }

        private void setTTCSpecificSCIBCC (TTCSpecificSCIBCC value) {
            this.tTCSpecificSCIBCC = value;
        }

        
        public void selectTTCSpecificSCIBCC (TTCSpecificSCIBCC value) {
            this.tTCSpecificSCIBCC = value;
                        
        }

        
  

	    public void initWithDefaults() {
	    }

        private static IASN1PreparedElementData preparedData = CoderFactory.getInstance().newPreparedElementData(TTCNOSpecificParameterSCIBCC.class);
        public IASN1PreparedElementData getPreparedData() {
            return preparedData;
        }


    }
            