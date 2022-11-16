
package asnGenerated;
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
        name = "MonitorMode"
    )
    public class MonitorMode implements IASN1PreparedElement {        
        public enum EnumType {
            
            @ASN1EnumItem ( name = "interrupted", hasTag = true , tag = 0 )
            interrupted , 
            @ASN1EnumItem ( name = "notifyAndContinue", hasTag = true , tag = 1 )
            notifyAndContinue , 
            @ASN1EnumItem ( name = "transparent", hasTag = true , tag = 2 )
            transparent , 
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

        private static IASN1PreparedElementData preparedData = CoderFactory.getInstance().newPreparedElementData(MonitorMode.class);
        public IASN1PreparedElementData getPreparedData() {
            return preparedData;
        }


    }
            