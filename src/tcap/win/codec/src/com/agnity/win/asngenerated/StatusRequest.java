
package com.agnity.win.asngenerated;
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
    @ASN1BoxedType ( name = "StatusRequest" )
    public class StatusRequest implements IASN1PreparedElement {
                
        

       @ASN1PreparedElement
       @ASN1Sequence ( name = "StatusRequest" , isSet = true )
       public static class StatusRequestSequenceType implements IASN1PreparedElement {
                
        @ASN1Element ( name = "msid", isOptional =  false , hasTag =  false  , hasDefaultValue =  false  )
    
	private MSID msid = null;
                
  
        @ASN1Element ( name = "record-Type", isOptional =  false , hasTag =  true, tag = 392 , hasDefaultValue =  false  )
    
	private Record_Type record_Type = null;
                
  
        
        public MSID getMsid () {
            return this.msid;
        }

        

        public void setMsid (MSID value) {
            this.msid = value;
        }
        
  
        
        public Record_Type getRecord_Type () {
            return this.record_Type;
        }

        

        public void setRecord_Type (Record_Type value) {
            this.record_Type = value;
        }
        
  
                
                
        public void initWithDefaults() {
            
        }

        public IASN1PreparedElementData getPreparedData() {
            return preparedData_StatusRequestSequenceType;
        }

       private static IASN1PreparedElementData preparedData_StatusRequestSequenceType = CoderFactory.getInstance().newPreparedElementData(StatusRequestSequenceType.class);
                
       }

       
                
        @ASN1Element ( name = "StatusRequest", isOptional =  false , hasTag =  true, tag = 18, 
        tagClass =  TagClass.Private  , hasDefaultValue =  false  )
    
        private StatusRequestSequenceType  value;        

        
        
        public StatusRequest () {
        }
        
        
        
        public void setValue(StatusRequestSequenceType value) {
            this.value = value;
        }
        
        
        
        public StatusRequestSequenceType getValue() {
            return this.value;
        }            
        

	    public void initWithDefaults() {
	    }

        private static IASN1PreparedElementData preparedData = CoderFactory.getInstance().newPreparedElementData(StatusRequest.class);
        public IASN1PreparedElementData getPreparedData() {
            return preparedData;
        }

            
    }
            