
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
    @ASN1BoxedType ( name = "ExtensionsArray" )
    public class ExtensionsArray implements IASN1PreparedElement {
                
            @ASN1ValueRangeConstraint ( 
		
		min = 1L, 
		
		max = 10L 
		
	   )
	   
            @ASN1SequenceOf( name = "ExtensionsArray" , isSetOf = false)
	    private java.util.Collection<ExtensionField> value = null; 
    
            public ExtensionsArray () {
            }
        
            public ExtensionsArray ( java.util.Collection<ExtensionField> value ) {
                setValue(value);
            }
                        
            public void setValue(java.util.Collection<ExtensionField> value) {
                this.value = value;
            }
            
            public java.util.Collection<ExtensionField> getValue() {
                return this.value;
            }            
            
            public void initValue() {
                setValue(new java.util.LinkedList<ExtensionField>()); 
            }
            
            public void add(ExtensionField item) {
                value.add(item);
            }

	    public void initWithDefaults() {
	    }

        private static IASN1PreparedElementData preparedData = CoderFactory.getInstance().newPreparedElementData(ExtensionsArray.class);
        public IASN1PreparedElementData getPreparedData() {
            return preparedData;
        }


    }
            