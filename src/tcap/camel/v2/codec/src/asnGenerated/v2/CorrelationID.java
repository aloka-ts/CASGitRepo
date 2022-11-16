
package asnGenerated.v2;
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
    @ASN1BoxedType ( name = "CorrelationID" )
    public class CorrelationID implements IASN1PreparedElement {
                
        
        @ASN1Element ( name = "CorrelationID", isOptional =  false , hasTag =  false  , hasDefaultValue =  false  )
    
        private Digits  value;        

        
        
        public CorrelationID () {
        }
        
        
        
        public void setValue(Digits value) {
            this.value = value;
        }
        
        
        
        public Digits getValue() {
            return this.value;
        }            
        

	    public void initWithDefaults() {
	    }

        private static IASN1PreparedElementData preparedData = CoderFactory.getInstance().newPreparedElementData(CorrelationID.class);
        public IASN1PreparedElementData getPreparedData() {
            return preparedData;
        }

            
    }
            