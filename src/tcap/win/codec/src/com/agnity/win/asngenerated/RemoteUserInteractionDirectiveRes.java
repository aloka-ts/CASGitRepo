
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
    @ASN1BoxedType ( name = "RemoteUserInteractionDirectiveRes" )
    public class RemoteUserInteractionDirectiveRes implements IASN1PreparedElement {
                
        

       @ASN1PreparedElement
       @ASN1Sequence ( name = "RemoteUserInteractionDirectiveRes" , isSet = true )
       public static class RemoteUserInteractionDirectiveResSequenceType implements IASN1PreparedElement {
                
        @ASN1Element ( name = "digits", isOptional =  true , hasTag =  true, tag = 4 , hasDefaultValue =  false  )
    
	private Digits digits = null;
                
  
        
        public Digits getDigits () {
            return this.digits;
        }

        
        public boolean isDigitsPresent () {
            return this.digits != null;
        }
        

        public void setDigits (Digits value) {
            this.digits = value;
        }
        
  
                
                
        public void initWithDefaults() {
            
        }

        public IASN1PreparedElementData getPreparedData() {
            return preparedData_RemoteUserInteractionDirectiveResSequenceType;
        }

       private static IASN1PreparedElementData preparedData_RemoteUserInteractionDirectiveResSequenceType = CoderFactory.getInstance().newPreparedElementData(RemoteUserInteractionDirectiveResSequenceType.class);
                
       }

       
                
        @ASN1Element ( name = "RemoteUserInteractionDirectiveRes", isOptional =  false , hasTag =  true, tag = 18, 
        tagClass =  TagClass.Private  , hasDefaultValue =  false  )
    
        private RemoteUserInteractionDirectiveResSequenceType  value;        

        
        
        public RemoteUserInteractionDirectiveRes () {
        }
        
        
        
        public void setValue(RemoteUserInteractionDirectiveResSequenceType value) {
            this.value = value;
        }
        
        
        
        public RemoteUserInteractionDirectiveResSequenceType getValue() {
            return this.value;
        }            
        

	    public void initWithDefaults() {
	    }

        private static IASN1PreparedElementData preparedData = CoderFactory.getInstance().newPreparedElementData(RemoteUserInteractionDirectiveRes.class);
        public IASN1PreparedElementData getPreparedData() {
            return preparedData;
        }

            
    }
            