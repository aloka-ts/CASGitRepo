
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
    @ASN1Enum (
        name = "TypeOfInteraction"
    )
    public class TypeOfInteraction implements IASN1PreparedElement {        
        public enum EnumType {
            
            @ASN1EnumItem ( name = "switchListUpdate", hasTag = true , tag = 0 )
            switchListUpdate , 
            @ASN1EnumItem ( name = "ipListUpdate", hasTag = true , tag = 1 )
            ipListUpdate , 
            @ASN1EnumItem ( name = "help", hasTag = true , tag = 2 )
            help , 
            @ASN1EnumItem ( name = "digitTranslation", hasTag = true , tag = 3 )
            digitTranslation , 
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

        private static IASN1PreparedElementData preparedData = CoderFactory.getInstance().newPreparedElementData(TypeOfInteraction.class);
        public IASN1PreparedElementData getPreparedData() {
            return preparedData;
        }


    }
            