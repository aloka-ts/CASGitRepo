
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
    @ASN1Sequence ( name = "ExtensionField", isSet = false )
    public class ExtensionField implements IASN1PreparedElement {
            @ASN1Integer( name = "" )
    
        @ASN1Element ( name = "type", isOptional =  true , hasTag =  false  , hasDefaultValue =  false  )
    
	private Long type = null;
                
  

    @ASN1PreparedElement
    @ASN1Enum (
        name = "CriticalityEnumType"
    )
    public static class CriticalityEnumType implements IASN1PreparedElement {        
        public enum EnumType {
            
            @ASN1EnumItem ( name = "ignore", hasTag = true , tag = 0 )
            ignore , 
            @ASN1EnumItem ( name = "abort", hasTag = true , tag = 1 )
            abort
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

        private static IASN1PreparedElementData preparedData = CoderFactory.getInstance().newPreparedElementData(CriticalityEnumType.class);
        public IASN1PreparedElementData getPreparedData() {
            return preparedData;
        }

    }
                
        @ASN1Element ( name = "criticality", isOptional =  false , hasTag =  false  , hasDefaultValue =  true  )
    
	private CriticalityEnumType criticality = null;
                
  @ASN1Any( name = "" )
    
        @ASN1Element ( name = "value", isOptional =  false , hasTag =  true, tag = 1 , hasDefaultValue =  false  )
    
	private byte[] value = null;
                
  
        
        public Long getType () {
            return this.type;
        }

        

        public void setType (Long value) {
            this.type = value;
        }
        
  
        
        public CriticalityEnumType getCriticality () {
            return this.criticality;
        }

        

        public void setCriticality (CriticalityEnumType value) {
            this.criticality = value;
        }
        
  
        
        public byte[] getValue () {
            return this.value;
        }

        

        public void setValue (byte[] value) {
            this.value = value;
        }
        
  
                    
        
        public void initWithDefaults() {
            CriticalityEnumType param_Criticality =   new CriticalityEnumType();
            param_Criticality.setValue(CriticalityEnumType.EnumType.ignore);
        setCriticality(param_Criticality);
    
        }

        private static IASN1PreparedElementData preparedData = CoderFactory.getInstance().newPreparedElementData(ExtensionField.class);
        public IASN1PreparedElementData getPreparedData() {
            return preparedData;
        }

            
    }
            