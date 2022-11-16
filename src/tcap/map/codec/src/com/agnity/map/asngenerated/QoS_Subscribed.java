
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
    @ASN1BoxedType ( name = "QoS_Subscribed" )
    public class QoS_Subscribed implements IASN1PreparedElement {
    
            @ASN1OctetString( name = "QoS-Subscribed" )
            
            @ASN1SizeConstraint ( max = 3L )
        
            private byte[] value = null;
            
            public QoS_Subscribed() {
            }

            public QoS_Subscribed(byte[] value) {
                this.value = value;
            }

            public QoS_Subscribed(BitString value) {
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

        private static IASN1PreparedElementData preparedData = CoderFactory.getInstance().newPreparedElementData(QoS_Subscribed.class);
        public IASN1PreparedElementData getPreparedData() {
            return preparedData;
        }


    }
            