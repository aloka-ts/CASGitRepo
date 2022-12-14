
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
    @ASN1Enum (
        name = "ControlType"
    )
    public class ControlType implements IASN1PreparedElement {        
        public enum EnumType {
            
            @ASN1EnumItem ( name = "sCPOverloaded", hasTag = true , tag = 0 )
            sCPOverloaded , 
            @ASN1EnumItem ( name = "manuallyInitiated", hasTag = true , tag = 1 )
            manuallyInitiated , 
            @ASN1EnumItem ( name = "destinationOverload", hasTag = true , tag = 2 )
            destinationOverload , 
        }
        
        private EnumType value;
        private Integer integerForm;
        
        public EnumType getValue() {
            return this.value;
        }
        
        public void setValue(EnumType value) {
            this.value = value;
        }
        
        public Integer getIntegerForm() {
            return integerForm;
        }
        
        public void setIntegerForm(Integer value) {
            integerForm = value;
        }

	    public void initWithDefaults() {
	    }

        private static IASN1PreparedElementData preparedData = CoderFactory.getInstance().newPreparedElementData(ControlType.class);
        public IASN1PreparedElementData getPreparedData() {
            return preparedData;
        }


    }
            