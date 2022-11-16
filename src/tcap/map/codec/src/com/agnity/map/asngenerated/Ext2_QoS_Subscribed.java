
package com.agnity.map.asngenerated;
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
    @ASN1BoxedType ( name = "Ext2_QoS_Subscribed" )
    public class Ext2_QoS_Subscribed implements IASN1PreparedElement {
    
            @ASN1OctetString( name = "Ext2-QoS-Subscribed" )
            @ASN1ValueRangeConstraint ( 
		
		min = 1L, 
		
		max = 3L 
		
	   )
	   
            private byte[] value = null;
            
            public Ext2_QoS_Subscribed() {
            }

            public Ext2_QoS_Subscribed(byte[] value) {
                this.value = value;
            }

            public Ext2_QoS_Subscribed(BitString value) {
                setValue(value);
            }
            
            public void setValue(byte[] value) {
                this.value = value;
            }

            public void setValue(BitString btStr) {
                this.value = btStr.getValue();
            }
            
            public byte[] getValue() {
                return this.value;
            }

	    public void initWithDefaults() {
	    }

        private static IASN1PreparedElementData preparedData = CoderFactory.getInstance().newPreparedElementData(Ext2_QoS_Subscribed.class);
        public IASN1PreparedElementData getPreparedData() {
            return preparedData;
        }


    }
            