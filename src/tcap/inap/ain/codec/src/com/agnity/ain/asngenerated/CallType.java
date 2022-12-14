
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
    @ASN1BoxedType ( name = "CallType" )
    public class CallType implements IASN1PreparedElement {
                
        

    @ASN1PreparedElement
    @ASN1Enum (
        name = "CallTypeEnumType"
    )
    public static class CallTypeEnumType implements IASN1PreparedElement {        
        public enum EnumType {
            
            @ASN1EnumItem ( name = "noIndication", hasTag = true , tag = 0 )
            noIndication , 
            @ASN1EnumItem ( name = "local", hasTag = true , tag = 1 )
            local , 
            @ASN1EnumItem ( name = "intraLATAToll", hasTag = true , tag = 2 )
            intraLATAToll , 
            @ASN1EnumItem ( name = "interLATAToll", hasTag = true , tag = 3 )
            interLATAToll , 
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

        private static IASN1PreparedElementData preparedData = CoderFactory.getInstance().newPreparedElementData(CallTypeEnumType.class);
        public IASN1PreparedElementData getPreparedData() {
            return preparedData;
        }

    }
                
        @ASN1Element ( name = "CallType", isOptional =  false , hasTag =  true, tag = 165 , hasDefaultValue =  false  )
    
        private CallTypeEnumType  value;        

        
        
        public CallType () {
        }
        
        
        
        public void setValue(CallTypeEnumType value) {
            this.value = value;
        }
        
        
        
        public CallTypeEnumType getValue() {
            return this.value;
        }            
        

	    public void initWithDefaults() {
	    }

        private static IASN1PreparedElementData preparedData = CoderFactory.getInstance().newPreparedElementData(CallType.class);
        public IASN1PreparedElementData getPreparedData() {
            return preparedData;
        }

            
    }
            