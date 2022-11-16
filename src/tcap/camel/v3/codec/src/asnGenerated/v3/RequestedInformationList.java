
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
    @ASN1BoxedType ( name = "RequestedInformationList" )
    public class RequestedInformationList implements IASN1PreparedElement {
                
            @ASN1ValueRangeConstraint ( 
		
		min = 1L, 
		
		max = 4L 
		
	   )
	   
            @ASN1SequenceOf( name = "RequestedInformationList" , isSetOf = false)
	    private java.util.Collection<RequestedInformation> value = null; 
    
            public RequestedInformationList () {
            }
        
            public RequestedInformationList ( java.util.Collection<RequestedInformation> value ) {
                setValue(value);
            }
                        
            public void setValue(java.util.Collection<RequestedInformation> value) {
                this.value = value;
            }
            
            public java.util.Collection<RequestedInformation> getValue() {
                return this.value;
            }            
            
            public void initValue() {
                setValue(new java.util.LinkedList<RequestedInformation>()); 
            }
            
            public void add(RequestedInformation item) {
                value.add(item);
            }

	    public void initWithDefaults() {
	    }

        private static IASN1PreparedElementData preparedData = CoderFactory.getInstance().newPreparedElementData(RequestedInformationList.class);
        public IASN1PreparedElementData getPreparedData() {
            return preparedData;
        }


    }
            