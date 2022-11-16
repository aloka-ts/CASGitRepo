
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
    @ASN1Sequence ( name = "GapIndicators", isSet = false )
    public class GapIndicators implements IASN1PreparedElement {
            
        @ASN1Element ( name = "duration1", isOptional =  false , hasTag =  true, tag = 0 , hasDefaultValue =  false  )
    
	private Duration duration1 = null;
                
  
        @ASN1Element ( name = "gapInterval", isOptional =  false , hasTag =  true, tag = 1 , hasDefaultValue =  false  )
    
	private Interval gapInterval = null;
                
  
        
        public Duration getDuration1 () {
            return this.duration1;
        }

        

        public void setDuration1 (Duration value) {
            this.duration1 = value;
        }
        
  
        
        public Interval getGapInterval () {
            return this.gapInterval;
        }

        

        public void setGapInterval (Interval value) {
            this.gapInterval = value;
        }
        
  
                    
        
        public void initWithDefaults() {
            
        }

        private static IASN1PreparedElementData preparedData = CoderFactory.getInstance().newPreparedElementData(GapIndicators.class);
        public IASN1PreparedElementData getPreparedData() {
            return preparedData;
        }

            
    }
            