
package com.agnity.win.asngenerated;
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
        name = "DataResult"
    )
    public class DataResult implements IASN1PreparedElement {        
        public enum EnumType {
            
            @ASN1EnumItem ( name = "not-used", hasTag = true , tag = 0 )
            not_used , 
            @ASN1EnumItem ( name = "successful", hasTag = true , tag = 1 )
            successful , 
            @ASN1EnumItem ( name = "unsuccessful-unspecified", hasTag = true , tag = 2 )
            unsuccessful_unspecified , 
            @ASN1EnumItem ( name = "unsuccessful-no-default-value-available", hasTag = true , tag = 3 )
            unsuccessful_no_default_value_available , 
            @ASN1EnumItem ( name = "reserved", hasTag = true , tag = 4 )
            reserved , 
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

        private static IASN1PreparedElementData preparedData = CoderFactory.getInstance().newPreparedElementData(DataResult.class);
        public IASN1PreparedElementData getPreparedData() {
            return preparedData;
        }


    }
            