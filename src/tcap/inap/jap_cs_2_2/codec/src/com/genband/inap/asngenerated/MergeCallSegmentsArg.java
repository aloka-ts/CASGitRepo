
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
    @ASN1Sequence ( name = "MergeCallSegmentsArg", isSet = false )
    public class MergeCallSegmentsArg implements IASN1PreparedElement {
            
        @ASN1Element ( name = "sourceCallSegment", isOptional =  false , hasTag =  true, tag = 0 , hasDefaultValue =  false  )
    
	private CallSegmentID sourceCallSegment = null;
                
  
        @ASN1Element ( name = "targetCallSegment", isOptional =  false , hasTag =  true, tag = 1 , hasDefaultValue =  true  )
    
	private CallSegmentID targetCallSegment = null;
                
  
@ASN1SequenceOf( name = "", isSetOf = false ) 

    @ASN1ValueRangeConstraint ( 
		
		min = 1L, 
		
		max = 1L 
		
	   )
	   
        @ASN1Element ( name = "extensions", isOptional =  true , hasTag =  true, tag = 2 , hasDefaultValue =  false  )
    
	private java.util.Collection<ExtensionField>  extensions = null;
                
  
        
        public CallSegmentID getSourceCallSegment () {
            return this.sourceCallSegment;
        }

        

        public void setSourceCallSegment (CallSegmentID value) {
            this.sourceCallSegment = value;
        }
        
  
        
        public CallSegmentID getTargetCallSegment () {
            return this.targetCallSegment;
        }

        

        public void setTargetCallSegment (CallSegmentID value) {
            this.targetCallSegment = value;
        }
        
  
        
        public java.util.Collection<ExtensionField>  getExtensions () {
            return this.extensions;
        }

        
        public boolean isExtensionsPresent () {
            return this.extensions != null;
        }
        

        public void setExtensions (java.util.Collection<ExtensionField>  value) {
            this.extensions = value;
        }
        
  
                    
        
        public void initWithDefaults() {
            CallSegmentID param_TargetCallSegment =     null    
            ;
        setTargetCallSegment(param_TargetCallSegment);
    
        }

        private static IASN1PreparedElementData preparedData = CoderFactory.getInstance().newPreparedElementData(MergeCallSegmentsArg.class);
        public IASN1PreparedElementData getPreparedData() {
            return preparedData;
        }

            
    }
            