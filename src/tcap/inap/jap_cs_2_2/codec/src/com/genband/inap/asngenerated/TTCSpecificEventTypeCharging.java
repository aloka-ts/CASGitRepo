
package com.genband.inap.asngenerated;
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
    @ASN1BoxedType ( name = "TTCSpecificEventTypeCharging" )
    public class TTCSpecificEventTypeCharging implements IASN1PreparedElement {
                
            

    @ASN1PreparedElement
    @ASN1Enum (
        name = "TTCSpecificEventTypeChargingEnumType"
    )
    public static class TTCSpecificEventTypeChargingEnumType implements IASN1PreparedElement {        
        public enum EnumType {
            
            @ASN1EnumItem ( name = "carrierInformationTransfer", hasTag = true , tag = 0 )
            carrierInformationTransfer , 
            @ASN1EnumItem ( name = "additionalPartysCategory", hasTag = true , tag = 1 )
            additionalPartysCategory , 
            @ASN1EnumItem ( name = "backwardCallIndicators", hasTag = true , tag = 2 )
            backwardCallIndicators , 
            @ASN1EnumItem ( name = "chargeAreaInformation", hasTag = true , tag = 3 )
            chargeAreaInformation , 
            @ASN1EnumItem ( name = "chargeInformationDelay", hasTag = true , tag = 4 )
            chargeInformationDelay
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

        private static IASN1PreparedElementData preparedData = CoderFactory.getInstance().newPreparedElementData(TTCSpecificEventTypeChargingEnumType.class);
        public IASN1PreparedElementData getPreparedData() {
            return preparedData;
        }

    }
                @ASN1ValueRangeConstraint ( 
		
		min = 1L, 
		
		max = 5L 
		
	   )
	   
            @ASN1SequenceOf( name = "TTCSpecificEventTypeCharging" , isSetOf = true)
	    private java.util.Collection<TTCSpecificEventTypeChargingEnumType> value = null; 
    
            public TTCSpecificEventTypeCharging () {
            }
        
            public TTCSpecificEventTypeCharging ( java.util.Collection<TTCSpecificEventTypeChargingEnumType> value ) {
                setValue(value);
            }
                        
            public void setValue(java.util.Collection<TTCSpecificEventTypeChargingEnumType> value) {
                this.value = value;
            }
            
            public java.util.Collection<TTCSpecificEventTypeChargingEnumType> getValue() {
                return this.value;
            }            
            
            public void initValue() {
                setValue(new java.util.LinkedList<TTCSpecificEventTypeChargingEnumType>()); 
            }
            
            public void add(TTCSpecificEventTypeChargingEnumType item) {
                value.add(item);
            }

	    public void initWithDefaults() {
	    }

        private static IASN1PreparedElementData preparedData = CoderFactory.getInstance().newPreparedElementData(TTCSpecificEventTypeCharging.class);
        public IASN1PreparedElementData getPreparedData() {
            return preparedData;
        }


    }
            