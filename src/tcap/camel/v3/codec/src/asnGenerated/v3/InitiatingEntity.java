
package asnGenerated.v3;
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
        name = "InitiatingEntity"
    )
    public class InitiatingEntity implements IASN1PreparedElement {        
        public enum EnumType {
            
            @ASN1EnumItem ( name = "mobileStation", hasTag = true , tag = 0 )
            mobileStation , 
            @ASN1EnumItem ( name = "sgsn", hasTag = true , tag = 1 )
            sgsn , 
            @ASN1EnumItem ( name = "hlr", hasTag = true , tag = 2 )
            hlr , 
            @ASN1EnumItem ( name = "ggsn", hasTag = true , tag = 3 )
            ggsn , 
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

        private static IASN1PreparedElementData preparedData = CoderFactory.getInstance().newPreparedElementData(InitiatingEntity.class);
        public IASN1PreparedElementData getPreparedData() {
            return preparedData;
        }


    }
            