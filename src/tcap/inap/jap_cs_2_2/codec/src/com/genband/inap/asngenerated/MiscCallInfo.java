
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
    @ASN1Sequence ( name = "MiscCallInfo", isSet = false )
    public class MiscCallInfo implements IASN1PreparedElement {
            

    @ASN1PreparedElement
    @ASN1Enum (
        name = "MessageTypeEnumType"
    )
    public static class MessageTypeEnumType implements IASN1PreparedElement {        
        public enum EnumType {
            
            @ASN1EnumItem ( name = "request", hasTag = true , tag = 0 )
            request , 
            @ASN1EnumItem ( name = "notification", hasTag = true , tag = 1 )
            notification , 
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

        private static IASN1PreparedElementData preparedData = CoderFactory.getInstance().newPreparedElementData(MessageTypeEnumType.class);
        public IASN1PreparedElementData getPreparedData() {
            return preparedData;
        }

    }
                
        @ASN1Element ( name = "messageType", isOptional =  false , hasTag =  true, tag = 0 , hasDefaultValue =  false  )
    
	private MessageTypeEnumType messageType = null;
                
  

    @ASN1PreparedElement
    @ASN1Enum (
        name = "DpAssignmentEnumType"
    )
    public static class DpAssignmentEnumType implements IASN1PreparedElement {        
        public enum EnumType {
            
            @ASN1EnumItem ( name = "individualLine", hasTag = true , tag = 0 )
            individualLine , 
            @ASN1EnumItem ( name = "officeBased", hasTag = true , tag = 2 )
            officeBased
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

        private static IASN1PreparedElementData preparedData = CoderFactory.getInstance().newPreparedElementData(DpAssignmentEnumType.class);
        public IASN1PreparedElementData getPreparedData() {
            return preparedData;
        }

    }
                
        @ASN1Element ( name = "dpAssignment", isOptional =  true , hasTag =  true, tag = 1 , hasDefaultValue =  false  )
    
	private DpAssignmentEnumType dpAssignment = null;
                
  
        
        public MessageTypeEnumType getMessageType () {
            return this.messageType;
        }

        

        public void setMessageType (MessageTypeEnumType value) {
            this.messageType = value;
        }
        
  
        
        public DpAssignmentEnumType getDpAssignment () {
            return this.dpAssignment;
        }

        
        public boolean isDpAssignmentPresent () {
            return this.dpAssignment != null;
        }
        

        public void setDpAssignment (DpAssignmentEnumType value) {
            this.dpAssignment = value;
        }
        
  
                    
        
        public void initWithDefaults() {
            
        }

        private static IASN1PreparedElementData preparedData = CoderFactory.getInstance().newPreparedElementData(MiscCallInfo.class);
        public IASN1PreparedElementData getPreparedData() {
            return preparedData;
        }

            
    }
            