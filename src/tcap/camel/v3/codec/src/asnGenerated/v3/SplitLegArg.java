
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
    @ASN1Sequence ( name = "SplitLegArg", isSet = false )
    public class SplitLegArg implements IASN1PreparedElement {
            
        @ASN1Element ( name = "legToBeSplit", isOptional =  false , hasTag =  true, tag = 0 , hasDefaultValue =  false  )
    
	private LegID legToBeSplit = null;
                
  
        @ASN1Element ( name = "newCallSegment", isOptional =  true , hasTag =  true, tag = 1 , hasDefaultValue =  false  )
    
	private CallSegmentID newCallSegment = null;
                
  
        @ASN1Element ( name = "extensions", isOptional =  true , hasTag =  true, tag = 2 , hasDefaultValue =  false  )
    
	private Extensions extensions = null;
                
  
        
        public LegID getLegToBeSplit () {
            return this.legToBeSplit;
        }

        

        public void setLegToBeSplit (LegID value) {
            this.legToBeSplit = value;
        }
        
  
        
        public CallSegmentID getNewCallSegment () {
            return this.newCallSegment;
        }

        
        public boolean isNewCallSegmentPresent () {
            return this.newCallSegment != null;
        }
        

        public void setNewCallSegment (CallSegmentID value) {
            this.newCallSegment = value;
        }
        
  
        
        public Extensions getExtensions () {
            return this.extensions;
        }

        
        public boolean isExtensionsPresent () {
            return this.extensions != null;
        }
        

        public void setExtensions (Extensions value) {
            this.extensions = value;
        }
        
  
                    
        
        public void initWithDefaults() {
            
        }

        private static IASN1PreparedElementData preparedData = CoderFactory.getInstance().newPreparedElementData(SplitLegArg.class);
        public IASN1PreparedElementData getPreparedData() {
            return preparedData;
        }

            
    }
            