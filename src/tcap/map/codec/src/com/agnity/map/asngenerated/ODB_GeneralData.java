
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
    @ASN1BoxedType ( name = "ODB_GeneralData" )
    public class ODB_GeneralData implements IASN1PreparedElement {
    
            @ASN1BitString( name = "ODB-GeneralData" )
            @ASN1ValueRangeConstraint ( 
		
		min = 15L, 
		
		max = 32L 
		
	   )
	   
            private BitString value = null;
            
            public ODB_GeneralData() {
            }

            public ODB_GeneralData(BitString value) {
                this.value = value;
            }
            
            public void setValue(BitString value) {
                this.value = value;
            }
            
            public BitString getValue() {
                return this.value;
            }

	    public void initWithDefaults() {
	    }

        private static IASN1PreparedElementData preparedData = CoderFactory.getInstance().newPreparedElementData(ODB_GeneralData.class);
        public IASN1PreparedElementData getPreparedData() {
            return preparedData;
        }

    }
            