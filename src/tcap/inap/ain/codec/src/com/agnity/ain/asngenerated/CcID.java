
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
    @ASN1BoxedType ( name = "CcID" )
    public class CcID implements IASN1PreparedElement {
                
        

    @ASN1PreparedElement
    @ASN1Enum (
        name = "CcIDEnumType"
    )
    public static class CcIDEnumType implements IASN1PreparedElement {        
        public enum EnumType {
            
            @ASN1EnumItem ( name = "null", hasTag = true , tag = 0 )
            null_ , 
            @ASN1EnumItem ( name = "originatingSetup", hasTag = true , tag = 1 )
            originatingSetup , 
            @ASN1EnumItem ( name = "stable2Party", hasTag = true , tag = 2 )
            stable2Party , 
            @ASN1EnumItem ( name = "terminatingSetup", hasTag = true , tag = 3 )
            terminatingSetup , 
            @ASN1EnumItem ( name = "threePartySetup", hasTag = true , tag = 4 )
            threePartySetup , 
            @ASN1EnumItem ( name = "threePartySetupComplement", hasTag = true , tag = 5 )
            threePartySetupComplement , 
            @ASN1EnumItem ( name = "partyOnHold", hasTag = true , tag = 6 )
            partyOnHold , 
            @ASN1EnumItem ( name = "partyOnHoldComplement", hasTag = true , tag = 7 )
            partyOnHoldComplement , 
            @ASN1EnumItem ( name = "callWaiting", hasTag = true , tag = 8 )
            callWaiting , 
            @ASN1EnumItem ( name = "callWaitingComplement", hasTag = true , tag = 9 )
            callWaitingComplement , 
            @ASN1EnumItem ( name = "stableMParty", hasTag = true , tag = 10 )
            stableMParty , 
            @ASN1EnumItem ( name = "transfer", hasTag = true , tag = 11 )
            transfer , 
            @ASN1EnumItem ( name = "forward", hasTag = true , tag = 12 )
            forward , 
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

        private static IASN1PreparedElementData preparedData = CoderFactory.getInstance().newPreparedElementData(CcIDEnumType.class);
        public IASN1PreparedElementData getPreparedData() {
            return preparedData;
        }

    }
                
        @ASN1Element ( name = "CcID", isOptional =  false , hasTag =  true, tag = 133 , hasDefaultValue =  false  )
    
        private CcIDEnumType  value;        

        
        
        public CcID () {
        }
        
        
        
        public void setValue(CcIDEnumType value) {
            this.value = value;
        }
        
        
        
        public CcIDEnumType getValue() {
            return this.value;
        }            
        

	    public void initWithDefaults() {
	    }

        private static IASN1PreparedElementData preparedData = CoderFactory.getInstance().newPreparedElementData(CcID.class);
        public IASN1PreparedElementData getPreparedData() {
            return preparedData;
        }

            
    }
            