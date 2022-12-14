
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
    @ASN1Sequence ( name = "AssistRequestInstructionsArg", isSet = false )
    public class AssistRequestInstructionsArg implements IASN1PreparedElement {
            
        @ASN1Element ( name = "correlationID", isOptional =  false , hasTag =  true, tag = 0 , hasDefaultValue =  false  )
    
	private CorrelationID correlationID = null;
                
  
        @ASN1Element ( name = "iPSSPCapabilities", isOptional =  false , hasTag =  true, tag = 2 , hasDefaultValue =  false  )
    
	private IPSSPCapabilities iPSSPCapabilities = null;
                
  
@ASN1SequenceOf( name = "extensions", isSetOf = false ) 

    @ASN1ValueRangeConstraint ( 
		
		min = 1L, 
		
		max = 10L 
		
	   )
	   
        @ASN1Element ( name = "extensions", isOptional =  true , hasTag =  true, tag = 3 , hasDefaultValue =  false  )
    
	private java.util.Collection<ExtensionField>  extensions = null;
                
  
        
        public CorrelationID getCorrelationID () {
            return this.correlationID;
        }

        

        public void setCorrelationID (CorrelationID value) {
            this.correlationID = value;
        }
        
  
        
        public IPSSPCapabilities getIPSSPCapabilities () {
            return this.iPSSPCapabilities;
        }

        

        public void setIPSSPCapabilities (IPSSPCapabilities value) {
            this.iPSSPCapabilities = value;
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
            
        }

        private static IASN1PreparedElementData preparedData = CoderFactory.getInstance().newPreparedElementData(AssistRequestInstructionsArg.class);
        public IASN1PreparedElementData getPreparedData() {
            return preparedData;
        }

            
    }
            