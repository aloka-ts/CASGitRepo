
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
    @ASN1BoxedType ( name = "SMSNotificationRes" )
    public class SMSNotificationRes implements IASN1PreparedElement {
                
        

       @ASN1PreparedElement
       @ASN1Sequence ( name = "SMSNotificationRes" , isSet = true )
       public static class SMSNotificationResSequenceType implements IASN1PreparedElement {
                
        @ASN1Element ( name = "sms-MessageCount", isOptional =  true , hasTag =  true, tag = 108 , hasDefaultValue =  false  )
    
	private SMS_MessageCount sms_MessageCount = null;
                
  
        
        public SMS_MessageCount getSms_MessageCount () {
            return this.sms_MessageCount;
        }

        
        public boolean isSms_MessageCountPresent () {
            return this.sms_MessageCount != null;
        }
        

        public void setSms_MessageCount (SMS_MessageCount value) {
            this.sms_MessageCount = value;
        }
        
  
                
                
        public void initWithDefaults() {
            
        }

        public IASN1PreparedElementData getPreparedData() {
            return preparedData_SMSNotificationResSequenceType;
        }

       private static IASN1PreparedElementData preparedData_SMSNotificationResSequenceType = CoderFactory.getInstance().newPreparedElementData(SMSNotificationResSequenceType.class);
                
       }

       
                
        @ASN1Element ( name = "SMSNotificationRes", isOptional =  false , hasTag =  true, tag = 18, 
        tagClass =  TagClass.Private  , hasDefaultValue =  false  )
    
        private SMSNotificationResSequenceType  value;        

        
        
        public SMSNotificationRes () {
        }
        
        
        
        public void setValue(SMSNotificationResSequenceType value) {
            this.value = value;
        }
        
        
        
        public SMSNotificationResSequenceType getValue() {
            return this.value;
        }            
        

	    public void initWithDefaults() {
	    }

        private static IASN1PreparedElementData preparedData = CoderFactory.getInstance().newPreparedElementData(SMSNotificationRes.class);
        public IASN1PreparedElementData getPreparedData() {
            return preparedData;
        }

            
    }
            