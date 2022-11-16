
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
    @ASN1BoxedType ( name = "SMSNotification" )
    public class SMSNotification implements IASN1PreparedElement {
                
        

       @ASN1PreparedElement
       @ASN1Sequence ( name = "SMSNotification" , isSet = true )
       public static class SMSNotificationSequenceType implements IASN1PreparedElement {
                
        @ASN1Element ( name = "electronicSerialNumber", isOptional =  false , hasTag =  true, tag = 9 , hasDefaultValue =  false  )
    
	private ElectronicSerialNumber electronicSerialNumber = null;
                
  
        @ASN1Element ( name = "msid", isOptional =  true , hasTag =  false  , hasDefaultValue =  false  )
    
	private MSID msid = null;
                
  
        @ASN1Element ( name = "mobileDirectoryNumber", isOptional =  true , hasTag =  true, tag = 93 , hasDefaultValue =  false  )
    
	private MobileDirectoryNumber mobileDirectoryNumber = null;
                
  
        @ASN1Element ( name = "sms-AccessDeniedReason", isOptional =  true , hasTag =  true, tag = 152 , hasDefaultValue =  false  )
    
	private SMS_AccessDeniedReason sms_AccessDeniedReason = null;
                
  
        @ASN1Element ( name = "sms-Address", isOptional =  true , hasTag =  true, tag = 104 , hasDefaultValue =  false  )
    
	private SMS_Address sms_Address = null;
                
  
        @ASN1Element ( name = "sms-TeleserviceIdentifier", isOptional =  true , hasTag =  true, tag = 116 , hasDefaultValue =  false  )
    
	private SMS_TeleserviceIdentifier sms_TeleserviceIdentifier = null;
                
  
        @ASN1Element ( name = "meid", isOptional =  true , hasTag =  true, tag = 390 , hasDefaultValue =  false  )
    
	private MEID meid = null;
                
  
        
        public ElectronicSerialNumber getElectronicSerialNumber () {
            return this.electronicSerialNumber;
        }

        

        public void setElectronicSerialNumber (ElectronicSerialNumber value) {
            this.electronicSerialNumber = value;
        }
        
  
        
        public MSID getMsid () {
            return this.msid;
        }

        
        public boolean isMsidPresent () {
            return this.msid != null;
        }
        

        public void setMsid (MSID value) {
            this.msid = value;
        }
        
  
        
        public MobileDirectoryNumber getMobileDirectoryNumber () {
            return this.mobileDirectoryNumber;
        }

        
        public boolean isMobileDirectoryNumberPresent () {
            return this.mobileDirectoryNumber != null;
        }
        

        public void setMobileDirectoryNumber (MobileDirectoryNumber value) {
            this.mobileDirectoryNumber = value;
        }
        
  
        
        public SMS_AccessDeniedReason getSms_AccessDeniedReason () {
            return this.sms_AccessDeniedReason;
        }

        
        public boolean isSms_AccessDeniedReasonPresent () {
            return this.sms_AccessDeniedReason != null;
        }
        

        public void setSms_AccessDeniedReason (SMS_AccessDeniedReason value) {
            this.sms_AccessDeniedReason = value;
        }
        
  
        
        public SMS_Address getSms_Address () {
            return this.sms_Address;
        }

        
        public boolean isSms_AddressPresent () {
            return this.sms_Address != null;
        }
        

        public void setSms_Address (SMS_Address value) {
            this.sms_Address = value;
        }
        
  
        
        public SMS_TeleserviceIdentifier getSms_TeleserviceIdentifier () {
            return this.sms_TeleserviceIdentifier;
        }

        
        public boolean isSms_TeleserviceIdentifierPresent () {
            return this.sms_TeleserviceIdentifier != null;
        }
        

        public void setSms_TeleserviceIdentifier (SMS_TeleserviceIdentifier value) {
            this.sms_TeleserviceIdentifier = value;
        }
        
  
        
        public MEID getMeid () {
            return this.meid;
        }

        
        public boolean isMeidPresent () {
            return this.meid != null;
        }
        

        public void setMeid (MEID value) {
            this.meid = value;
        }
        
  
                
                
        public void initWithDefaults() {
            
        }

        public IASN1PreparedElementData getPreparedData() {
            return preparedData_SMSNotificationSequenceType;
        }

       private static IASN1PreparedElementData preparedData_SMSNotificationSequenceType = CoderFactory.getInstance().newPreparedElementData(SMSNotificationSequenceType.class);
                
       }

       
                
        @ASN1Element ( name = "SMSNotification", isOptional =  false , hasTag =  true, tag = 18, 
        tagClass =  TagClass.Private  , hasDefaultValue =  false  )
    
        private SMSNotificationSequenceType  value;        

        
        
        public SMSNotification () {
        }
        
        
        
        public void setValue(SMSNotificationSequenceType value) {
            this.value = value;
        }
        
        
        
        public SMSNotificationSequenceType getValue() {
            return this.value;
        }            
        

	    public void initWithDefaults() {
	    }

        private static IASN1PreparedElementData preparedData = CoderFactory.getInstance().newPreparedElementData(SMSNotification.class);
        public IASN1PreparedElementData getPreparedData() {
            return preparedData;
        }

            
    }
            