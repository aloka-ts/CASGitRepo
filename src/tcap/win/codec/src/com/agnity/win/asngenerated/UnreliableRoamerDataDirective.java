
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
    @ASN1BoxedType ( name = "UnreliableRoamerDataDirective" )
    public class UnreliableRoamerDataDirective implements IASN1PreparedElement {
                
        

       @ASN1PreparedElement
       @ASN1Sequence ( name = "UnreliableRoamerDataDirective" , isSet = true )
       public static class UnreliableRoamerDataDirectiveSequenceType implements IASN1PreparedElement {
                
        @ASN1Element ( name = "mscid", isOptional =  true , hasTag =  true, tag = 21 , hasDefaultValue =  false  )
    
	private MSCID mscid = null;
                
  
        @ASN1Element ( name = "senderIdentificationNumber", isOptional =  true , hasTag =  true, tag = 103 , hasDefaultValue =  false  )
    
	private SenderIdentificationNumber senderIdentificationNumber = null;
                
  
        
        public MSCID getMscid () {
            return this.mscid;
        }

        
        public boolean isMscidPresent () {
            return this.mscid != null;
        }
        

        public void setMscid (MSCID value) {
            this.mscid = value;
        }
        
  
        
        public SenderIdentificationNumber getSenderIdentificationNumber () {
            return this.senderIdentificationNumber;
        }

        
        public boolean isSenderIdentificationNumberPresent () {
            return this.senderIdentificationNumber != null;
        }
        

        public void setSenderIdentificationNumber (SenderIdentificationNumber value) {
            this.senderIdentificationNumber = value;
        }
        
  
                
                
        public void initWithDefaults() {
            
        }

        public IASN1PreparedElementData getPreparedData() {
            return preparedData_UnreliableRoamerDataDirectiveSequenceType;
        }

       private static IASN1PreparedElementData preparedData_UnreliableRoamerDataDirectiveSequenceType = CoderFactory.getInstance().newPreparedElementData(UnreliableRoamerDataDirectiveSequenceType.class);
                
       }

       
                
        @ASN1Element ( name = "UnreliableRoamerDataDirective", isOptional =  false , hasTag =  true, tag = 18, 
        tagClass =  TagClass.Private  , hasDefaultValue =  false  )
    
        private UnreliableRoamerDataDirectiveSequenceType  value;        

        
        
        public UnreliableRoamerDataDirective () {
        }
        
        
        
        public void setValue(UnreliableRoamerDataDirectiveSequenceType value) {
            this.value = value;
        }
        
        
        
        public UnreliableRoamerDataDirectiveSequenceType getValue() {
            return this.value;
        }            
        

	    public void initWithDefaults() {
	    }

        private static IASN1PreparedElementData preparedData = CoderFactory.getInstance().newPreparedElementData(UnreliableRoamerDataDirective.class);
        public IASN1PreparedElementData getPreparedData() {
            return preparedData;
        }

            
    }
            