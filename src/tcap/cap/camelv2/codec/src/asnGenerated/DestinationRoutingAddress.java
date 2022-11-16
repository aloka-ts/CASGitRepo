
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
    @ASN1BoxedType ( name = "DestinationRoutingAddress" )
    public class DestinationRoutingAddress implements IASN1PreparedElement {
                
            
            @ASN1SizeConstraint ( max = 1L )
        
            @ASN1SequenceOf( name = "DestinationRoutingAddress" , isSetOf = false)
	    private java.util.Collection<CalledPartyNumber> value = null; 
    
            public DestinationRoutingAddress () {
            }
        
            public DestinationRoutingAddress ( java.util.Collection<CalledPartyNumber> value ) {
                setValue(value);
            }
                        
            public void setValue(java.util.Collection<CalledPartyNumber> value) {
                this.value = value;
            }
            
            public java.util.Collection<CalledPartyNumber> getValue() {
                return this.value;
            }            
            
            public void initValue() {
                setValue(new java.util.LinkedList<CalledPartyNumber>()); 
            }
            
            public void add(CalledPartyNumber item) {
                value.add(item);
            }

	    public void initWithDefaults() {
	    }

        private static IASN1PreparedElementData preparedData = CoderFactory.getInstance().newPreparedElementData(DestinationRoutingAddress.class);
        public IASN1PreparedElementData getPreparedData() {
            return preparedData;
        }


    }
            