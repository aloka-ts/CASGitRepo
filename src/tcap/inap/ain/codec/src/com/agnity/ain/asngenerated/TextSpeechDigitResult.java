
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
    @ASN1BoxedType ( name = "TextSpeechDigitResult" )
    public class TextSpeechDigitResult implements IASN1PreparedElement {
                
        
        @ASN1Element ( name = "TextSpeechDigitResult", isOptional =  false , hasTag =  false  , hasDefaultValue =  false  )
    
        private AnnouncementDigitResult  value;        

        
        
        public TextSpeechDigitResult () {
        }
        
        
        
        public void setValue(AnnouncementDigitResult value) {
            this.value = value;
        }
        
        
        
        public AnnouncementDigitResult getValue() {
            return this.value;
        }            
        

	    public void initWithDefaults() {
	    }

        private static IASN1PreparedElementData preparedData = CoderFactory.getInstance().newPreparedElementData(TextSpeechDigitResult.class);
        public IASN1PreparedElementData getPreparedData() {
            return preparedData;
        }

            
    }
            