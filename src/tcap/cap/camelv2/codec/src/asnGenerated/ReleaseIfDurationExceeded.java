
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
    @ASN1Sequence ( name = "ReleaseIfDurationExceeded", isSet = false )
    public class ReleaseIfDurationExceeded implements IASN1PreparedElement {
            @ASN1Boolean( name = "" )
    
        @ASN1Element ( name = "tone", isOptional =  false , hasTag =  false  , hasDefaultValue =  true  )
    
	private Boolean tone = null;
                
  
@ASN1SequenceOf( name = "", isSetOf = false ) 

    @ASN1ValueRangeConstraint ( 
		
		min = 1L, 
		
		max = 10L 
		
	   )
	   
        @ASN1Element ( name = "extensions", isOptional =  true , hasTag =  true, tag = 10 , hasDefaultValue =  false  )
    
	private java.util.Collection<ExtensionField>  extensions = null;
                
  
        
        public Boolean getTone () {
            return this.tone;
        }

        

        public void setTone (Boolean value) {
            this.tone = value;
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
            Boolean param_Tone =         
            null;
        setTone(param_Tone);
    
        }

        private static IASN1PreparedElementData preparedData = CoderFactory.getInstance().newPreparedElementData(ReleaseIfDurationExceeded.class);
        public IASN1PreparedElementData getPreparedData() {
            return preparedData;
        }

            
    }
            