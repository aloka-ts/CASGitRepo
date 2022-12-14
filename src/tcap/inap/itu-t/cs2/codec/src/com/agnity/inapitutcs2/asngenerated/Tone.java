
package com.agnity.inapitutcs2.asngenerated;
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
    @ASN1Sequence ( name = "Tone", isSet = false )
    public class Tone implements IASN1PreparedElement {
            
        @ASN1Element ( name = "toneID", isOptional =  false , hasTag =  true, tag = 0 , hasDefaultValue =  false  )
    
	private Integer4 toneID = null;
                
  
        @ASN1Element ( name = "duration", isOptional =  true , hasTag =  true, tag = 1 , hasDefaultValue =  false  )
    
	private Integer4 duration = null;
                
  
        
        public Integer4 getToneID () {
            return this.toneID;
        }

        

        public void setToneID (Integer4 value) {
            this.toneID = value;
        }
        
  
        
        public Integer4 getDuration () {
            return this.duration;
        }

        
        public boolean isDurationPresent () {
            return this.duration != null;
        }
        

        public void setDuration (Integer4 value) {
            this.duration = value;
        }
        
  
                    
        
        public void initWithDefaults() {
            
        }

        private static IASN1PreparedElementData preparedData = CoderFactory.getInstance().newPreparedElementData(Tone.class);
        public IASN1PreparedElementData getPreparedData() {
            return preparedData;
        }

            
    }
            