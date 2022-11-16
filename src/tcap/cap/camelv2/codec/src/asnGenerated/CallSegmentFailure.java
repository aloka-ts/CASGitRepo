
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
    @ASN1Sequence ( name = "CallSegmentFailure", isSet = false )
    public class CallSegmentFailure implements IASN1PreparedElement {
            
        @ASN1Element ( name = "callSegmentID", isOptional =  true , hasTag =  true, tag = 0 , hasDefaultValue =  false  )
    
	private CallSegmentID callSegmentID = null;
                
  
        @ASN1Element ( name = "cause", isOptional =  true , hasTag =  true, tag = 2 , hasDefaultValue =  false  )
    
	private Cause cause = null;
                
  
        
        public CallSegmentID getCallSegmentID () {
            return this.callSegmentID;
        }

        
        public boolean isCallSegmentIDPresent () {
            return this.callSegmentID != null;
        }
        

        public void setCallSegmentID (CallSegmentID value) {
            this.callSegmentID = value;
        }
        
  
        
        public Cause getCause () {
            return this.cause;
        }

        
        public boolean isCausePresent () {
            return this.cause != null;
        }
        

        public void setCause (Cause value) {
            this.cause = value;
        }
        
  
                    
        
        public void initWithDefaults() {
            
        }

        private static IASN1PreparedElementData preparedData = CoderFactory.getInstance().newPreparedElementData(CallSegmentFailure.class);
        public IASN1PreparedElementData getPreparedData() {
            return preparedData;
        }

            
    }
            