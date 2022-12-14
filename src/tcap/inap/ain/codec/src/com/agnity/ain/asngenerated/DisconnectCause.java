
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
    @ASN1BoxedType ( name = "DisconnectCause" )
    public class DisconnectCause implements IASN1PreparedElement {
                
        

    @ASN1PreparedElement
    @ASN1Enum (
        name = "DisconnectCauseEnumType"
    )
    public static class DisconnectCauseEnumType implements IASN1PreparedElement {        
        public enum EnumType {
            
            @ASN1EnumItem ( name = "farEnd", hasTag = true , tag = 0 )
            farEnd , 
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

        private static IASN1PreparedElementData preparedData = CoderFactory.getInstance().newPreparedElementData(DisconnectCauseEnumType.class);
        public IASN1PreparedElementData getPreparedData() {
            return preparedData;
        }

    }
                
        @ASN1Element ( name = "DisconnectCause", isOptional =  false , hasTag =  true, tag = 116 , hasDefaultValue =  false  )
    
        private DisconnectCauseEnumType  value;        

        
        
        public DisconnectCause () {
        }
        
        
        
        public void setValue(DisconnectCauseEnumType value) {
            this.value = value;
        }
        
        
        
        public DisconnectCauseEnumType getValue() {
            return this.value;
        }            
        

	    public void initWithDefaults() {
	    }

        private static IASN1PreparedElementData preparedData = CoderFactory.getInstance().newPreparedElementData(DisconnectCause.class);
        public IASN1PreparedElementData getPreparedData() {
            return preparedData;
        }

            
    }
            