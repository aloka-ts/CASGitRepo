
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
    @ASN1BoxedType ( name = "FailureCause" )
    public class FailureCause implements IASN1PreparedElement {
                
        

    @ASN1PreparedElement
    @ASN1Enum (
        name = "FailureCauseEnumType"
    )
    public static class FailureCauseEnumType implements IASN1PreparedElement {        
        public enum EnumType {
            
            @ASN1EnumItem ( name = "rateTooHigh", hasTag = true , tag = 1 )
            rateTooHigh , 
            @ASN1EnumItem ( name = "unavailableResources", hasTag = true , tag = 2 )
            unavailableResources , 
            @ASN1EnumItem ( name = "apTimeout", hasTag = true , tag = 3 )
            apTimeout , 
            @ASN1EnumItem ( name = "apBusy", hasTag = true , tag = 4 )
            apBusy , 
            @ASN1EnumItem ( name = "channelsBusy", hasTag = true , tag = 13 )
            channelsBusy , 
            @ASN1EnumItem ( name = "abort", hasTag = true , tag = 14 )
            abort , 
            @ASN1EnumItem ( name = "resourceLimitation", hasTag = true , tag = 15 )
            resourceLimitation , 
            @ASN1EnumItem ( name = "applicationError", hasTag = true , tag = 16 )
            applicationError , 
            @ASN1EnumItem ( name = "securityError", hasTag = true , tag = 17 )
            securityError , 
            @ASN1EnumItem ( name = "protocolError", hasTag = true , tag = 18 )
            protocolError , 
            @ASN1EnumItem ( name = "timerExpired", hasTag = true , tag = 19 )
            timerExpired , 
            @ASN1EnumItem ( name = "temporaryFailure", hasTag = true , tag = 20 )
            temporaryFailure , 
            @ASN1EnumItem ( name = "msridDoesNotMatchUserProfile", hasTag = true , tag = 21 )
            msridDoesNotMatchUserProfile , 
            @ASN1EnumItem ( name = "segmentationError", hasTag = true , tag = 22 )
            segmentationError , 
            @ASN1EnumItem ( name = "ncasDisallowed", hasTag = true , tag = 23 )
            ncasDisallowed , 
            @ASN1EnumItem ( name = "controlEncountered", hasTag = true , tag = 24 )
            controlEncountered , 
            @ASN1EnumItem ( name = "improperCoding", hasTag = true , tag = 25 )
            improperCoding , 
            @ASN1EnumItem ( name = "inappropriateCondition", hasTag = true , tag = 26 )
            inappropriateCondition , 
            @ASN1EnumItem ( name = "inappropriateUserInterface", hasTag = true , tag = 27 )
            inappropriateUserInterface , 
            @ASN1EnumItem ( name = "inappropriateLegManipulation", hasTag = true , tag = 28 )
            inappropriateLegManipulation , 
            @ASN1EnumItem ( name = "callingInterfaceBusy", hasTag = true , tag = 29 )
            callingInterfaceBusy , 
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

        private static IASN1PreparedElementData preparedData = CoderFactory.getInstance().newPreparedElementData(FailureCauseEnumType.class);
        public IASN1PreparedElementData getPreparedData() {
            return preparedData;
        }

    }
                
        @ASN1Element ( name = "FailureCause", isOptional =  false , hasTag =  true, tag = 32 , hasDefaultValue =  false  )
    
        private FailureCauseEnumType  value;        

        
        
        public FailureCause () {
        }
        
        
        
        public void setValue(FailureCauseEnumType value) {
            this.value = value;
        }
        
        
        
        public FailureCauseEnumType getValue() {
            return this.value;
        }            
        

	    public void initWithDefaults() {
	    }

        private static IASN1PreparedElementData preparedData = CoderFactory.getInstance().newPreparedElementData(FailureCause.class);
        public IASN1PreparedElementData getPreparedData() {
            return preparedData;
        }

            
    }
            