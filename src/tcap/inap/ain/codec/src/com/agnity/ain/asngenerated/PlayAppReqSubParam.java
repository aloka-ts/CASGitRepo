
package com.agnity.ain.asngenerated;
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
    @ASN1Choice ( name = "PlayAppReqSubParam" )
    public class PlayAppReqSubParam implements IASN1PreparedElement {
            
        @ASN1Element ( name = "inputParams", isOptional =  false , hasTag =  false  , hasDefaultValue =  false  )
    
	private PlayAppInfo inputParams = null;
                
  
        
        public PlayAppInfo getInputParams () {
            return this.inputParams;
        }

        public boolean isInputParamsSelected () {
            return this.inputParams != null;
        }

        private void setInputParams (PlayAppInfo value) {
            this.inputParams = value;
        }

        
        public void selectInputParams (PlayAppInfo value) {
            this.inputParams = value;
                        
        }

        
  

	    public void initWithDefaults() {
	    }

        private static IASN1PreparedElementData preparedData = CoderFactory.getInstance().newPreparedElementData(PlayAppReqSubParam.class);
        public IASN1PreparedElementData getPreparedData() {
            return preparedData;
        }


    }
            