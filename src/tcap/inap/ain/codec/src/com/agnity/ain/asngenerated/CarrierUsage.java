
package com.agnity.ain.asngenerated;
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
    @ASN1BoxedType ( name = "CarrierUsage" )
    public class CarrierUsage implements IASN1PreparedElement {
                
        

    @ASN1PreparedElement
    @ASN1Enum (
        name = "CarrierUsageEnumType"
    )
    public static class CarrierUsageEnumType implements IASN1PreparedElement {        
        public enum EnumType {
            
            @ASN1EnumItem ( name = "alwaysOverride", hasTag = true , tag = 0 )
            alwaysOverride , 
            @ASN1EnumItem ( name = "onlyInterLATAOverride", hasTag = true , tag = 1 )
            onlyInterLATAOverride , 
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

        private static IASN1PreparedElementData preparedData = CoderFactory.getInstance().newPreparedElementData(CarrierUsageEnumType.class);
        public IASN1PreparedElementData getPreparedData() {
            return preparedData;
        }

    }
                
        @ASN1Element ( name = "CarrierUsage", isOptional =  false , hasTag =  true, tag = 79 , hasDefaultValue =  false  )
    
        private CarrierUsageEnumType  value;        

        
        
        public CarrierUsage () {
        }
        
        
        
        public void setValue(CarrierUsageEnumType value) {
            this.value = value;
        }
        
        
        
        public CarrierUsageEnumType getValue() {
            return this.value;
        }            
        

	    public void initWithDefaults() {
	    }

        private static IASN1PreparedElementData preparedData = CoderFactory.getInstance().newPreparedElementData(CarrierUsage.class);
        public IASN1PreparedElementData getPreparedData() {
            return preparedData;
        }

            
    }
            