
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
    @ASN1Choice ( name = "LegOrCallSegment" )
    public class LegOrCallSegment implements IASN1PreparedElement {
            
        @ASN1Element ( name = "callSegmentID", isOptional =  false , hasTag =  true, tag = 0 , hasDefaultValue =  false  )
    
	private CallSegmentID callSegmentID = null;
                
  
        @ASN1Element ( name = "legID", isOptional =  false , hasTag =  true, tag = 1 , hasDefaultValue =  false  )
    
	private LegID legID = null;
                
  
        
        public CallSegmentID getCallSegmentID () {
            return this.callSegmentID;
        }

        public boolean isCallSegmentIDSelected () {
            return this.callSegmentID != null;
        }

        private void setCallSegmentID (CallSegmentID value) {
            this.callSegmentID = value;
        }

        
        public void selectCallSegmentID (CallSegmentID value) {
            this.callSegmentID = value;
            
                    setLegID(null);
                            
        }

        
  
        
        public LegID getLegID () {
            return this.legID;
        }

        public boolean isLegIDSelected () {
            return this.legID != null;
        }

        private void setLegID (LegID value) {
            this.legID = value;
        }

        
        public void selectLegID (LegID value) {
            this.legID = value;
            
                    setCallSegmentID(null);
                            
        }

        
  

	    public void initWithDefaults() {
	    }

        private static IASN1PreparedElementData preparedData = CoderFactory.getInstance().newPreparedElementData(LegOrCallSegment.class);
        public IASN1PreparedElementData getPreparedData() {
            return preparedData;
        }


    }
            