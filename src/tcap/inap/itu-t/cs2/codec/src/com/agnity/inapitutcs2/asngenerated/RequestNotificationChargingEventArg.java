
package com.agnity.inapitutcs2.asngenerated;
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
    @ASN1BoxedType ( name = "RequestNotificationChargingEventArg" )
    public class RequestNotificationChargingEventArg implements IASN1PreparedElement {
                
            @ASN1ValueRangeConstraint ( 
		
		min = 1L, 
		
		max = 5L 
		
	   )
	   
            @ASN1SequenceOf( name = "RequestNotificationChargingEventArg" , isSetOf = false)
	    private java.util.Collection<ChargingEvent> value = null; 
    
            public RequestNotificationChargingEventArg () {
            }
        
            public RequestNotificationChargingEventArg ( java.util.Collection<ChargingEvent> value ) {
                setValue(value);
            }
                        
            public void setValue(java.util.Collection<ChargingEvent> value) {
                this.value = value;
            }
            
            public java.util.Collection<ChargingEvent> getValue() {
                return this.value;
            }            
            
            public void initValue() {
                setValue(new java.util.LinkedList<ChargingEvent>()); 
            }
            
            public void add(ChargingEvent item) {
                value.add(item);
            }

	    public void initWithDefaults() {
	    }

        private static IASN1PreparedElementData preparedData = CoderFactory.getInstance().newPreparedElementData(RequestNotificationChargingEventArg.class);
        public IASN1PreparedElementData getPreparedData() {
            return preparedData;
        }


    }
            