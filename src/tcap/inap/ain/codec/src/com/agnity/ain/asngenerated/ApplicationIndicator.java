
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
    @ASN1BoxedType ( name = "ApplicationIndicator" )
    public class ApplicationIndicator implements IASN1PreparedElement {
                
        

    @ASN1PreparedElement
    @ASN1Enum (
        name = "ApplicationIndicatorEnumType"
    )
    public static class ApplicationIndicatorEnumType implements IASN1PreparedElement {        
        public enum EnumType {
            
            @ASN1EnumItem ( name = "routeToApplicationProcessOrSLP", hasTag = true , tag = 0 )
            routeToApplicationProcessOrSLP , 
            @ASN1EnumItem ( name = "processEchoRequestMessage", hasTag = true , tag = 1 )
            processEchoRequestMessage , 
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

        private static IASN1PreparedElementData preparedData = CoderFactory.getInstance().newPreparedElementData(ApplicationIndicatorEnumType.class);
        public IASN1PreparedElementData getPreparedData() {
            return preparedData;
        }

    }
                
        @ASN1Element ( name = "ApplicationIndicator", isOptional =  false , hasTag =  true, tag = 90 , hasDefaultValue =  false  )
    
        private ApplicationIndicatorEnumType  value;        

        
        
        public ApplicationIndicator () {
        }
        
        
        
        public void setValue(ApplicationIndicatorEnumType value) {
            this.value = value;
        }
        
        
        
        public ApplicationIndicatorEnumType getValue() {
            return this.value;
        }            
        

	    public void initWithDefaults() {
	    }

        private static IASN1PreparedElementData preparedData = CoderFactory.getInstance().newPreparedElementData(ApplicationIndicator.class);
        public IASN1PreparedElementData getPreparedData() {
            return preparedData;
        }

            
    }
            