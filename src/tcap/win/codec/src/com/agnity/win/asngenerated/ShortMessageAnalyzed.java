
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
    @ASN1BoxedType ( name = "ShortMessageAnalyzed" )
    public class ShortMessageAnalyzed implements IASN1PreparedElement {
                
        

       @ASN1PreparedElement
       @ASN1Sequence ( name = "ShortMessageAnalyzed" , isSet = true )
       public static class ShortMessageAnalyzedSequenceType implements IASN1PreparedElement {
                
        @ASN1Element ( name = "sms-BearerLength", isOptional =  false , hasTag =  true, tag = 404 , hasDefaultValue =  false  )
    
	private SMS_BearerLength sms_BearerLength = null;
                
  
        @ASN1Element ( name = "sms-BillingID", isOptional =  false , hasTag =  true, tag = 405 , hasDefaultValue =  false  )
    
	private SMS_BillingID sms_BillingID = null;
                
  
        @ASN1Element ( name = "sms-Event", isOptional =  false , hasTag =  true, tag = 406 , hasDefaultValue =  false  )
    
	private SMS_Event sms_Event = null;
                
  
        @ASN1Element ( name = "sms-TeleserviceIdentifier", isOptional =  false , hasTag =  true, tag = 116 , hasDefaultValue =  false  )
    
	private SMS_TeleserviceIdentifier sms_TeleserviceIdentifier = null;
                
  
        @ASN1Element ( name = "electronicSerialNumber", isOptional =  true , hasTag =  true, tag = 9 , hasDefaultValue =  false  )
    
	private ElectronicSerialNumber electronicSerialNumber = null;
                
  
        @ASN1Element ( name = "mobileDirectoryNumber", isOptional =  true , hasTag =  true, tag = 93 , hasDefaultValue =  false  )
    
	private MobileDirectoryNumber mobileDirectoryNumber = null;
                
  
        @ASN1Element ( name = "msid", isOptional =  true , hasTag =  false  , hasDefaultValue =  false  )
    
	private MSID msid = null;
                
  
        @ASN1Element ( name = "sms-ChargeIndicator", isOptional =  true , hasTag =  true, tag = 106 , hasDefaultValue =  false  )
    
	private SMS_ChargeIndicator sms_ChargeIndicator = null;
                
  
        @ASN1Element ( name = "sms-DestinationAddress", isOptional =  true , hasTag =  true, tag = 107 , hasDefaultValue =  false  )
    
	private SMS_DestinationAddress sms_DestinationAddress = null;
                
  
        @ASN1Element ( name = "sms-PendingMessageCount", isOptional =  true , hasTag =  true, tag = 407 , hasDefaultValue =  false  )
    
	private SMS_PendingMessageCount sms_PendingMessageCount = null;
                
  
        @ASN1Element ( name = "sms-OriginalDestinationAddress", isOptional =  true , hasTag =  true, tag = 110 , hasDefaultValue =  false  )
    
	private SMS_OriginalDestinationAddress sms_OriginalDestinationAddress = null;
                
  
        @ASN1Element ( name = "sms-OriginalDestinationSubaddress", isOptional =  true , hasTag =  true, tag = 111 , hasDefaultValue =  false  )
    
	private SMS_OriginalDestinationSubaddress sms_OriginalDestinationSubaddress = null;
                
  
        @ASN1Element ( name = "sms-OriginalOriginatingAddress", isOptional =  true , hasTag =  true, tag = 112 , hasDefaultValue =  false  )
    
	private SMS_OriginalOriginatingAddress sms_OriginalOriginatingAddress = null;
                
  
        @ASN1Element ( name = "sms-OriginalOriginatingSubaddress", isOptional =  true , hasTag =  true, tag = 113 , hasDefaultValue =  false  )
    
	private SMS_OriginalOriginatingSubaddress sms_OriginalOriginatingSubaddress = null;
                
  
        @ASN1Element ( name = "sms-OriginatingAddress", isOptional =  true , hasTag =  true, tag = 114 , hasDefaultValue =  false  )
    
	private SMS_OriginatingAddress sms_OriginatingAddress = null;
                
  
        @ASN1Element ( name = "timeDateOffset", isOptional =  true , hasTag =  true, tag = 275 , hasDefaultValue =  false  )
    
	private TimeDateOffset timeDateOffset = null;
                
  
        @ASN1Element ( name = "timeOfDay", isOptional =  true , hasTag =  true, tag = 309 , hasDefaultValue =  false  )
    
	private TimeOfDay timeOfDay = null;
                
  
        
        public SMS_BearerLength getSms_BearerLength () {
            return this.sms_BearerLength;
        }

        

        public void setSms_BearerLength (SMS_BearerLength value) {
            this.sms_BearerLength = value;
        }
        
  
        
        public SMS_BillingID getSms_BillingID () {
            return this.sms_BillingID;
        }

        

        public void setSms_BillingID (SMS_BillingID value) {
            this.sms_BillingID = value;
        }
        
  
        
        public SMS_Event getSms_Event () {
            return this.sms_Event;
        }

        

        public void setSms_Event (SMS_Event value) {
            this.sms_Event = value;
        }
        
  
        
        public SMS_TeleserviceIdentifier getSms_TeleserviceIdentifier () {
            return this.sms_TeleserviceIdentifier;
        }

        

        public void setSms_TeleserviceIdentifier (SMS_TeleserviceIdentifier value) {
            this.sms_TeleserviceIdentifier = value;
        }
        
  
        
        public ElectronicSerialNumber getElectronicSerialNumber () {
            return this.electronicSerialNumber;
        }

        
        public boolean isElectronicSerialNumberPresent () {
            return this.electronicSerialNumber != null;
        }
        

        public void setElectronicSerialNumber (ElectronicSerialNumber value) {
            this.electronicSerialNumber = value;
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
        
  
        
        public MSID getMsid () {
            return this.msid;
        }

        
        public boolean isMsidPresent () {
            return this.msid != null;
        }
        

        public void setMsid (MSID value) {
            this.msid = value;
        }
        
  
        
        public SMS_ChargeIndicator getSms_ChargeIndicator () {
            return this.sms_ChargeIndicator;
        }

        
        public boolean isSms_ChargeIndicatorPresent () {
            return this.sms_ChargeIndicator != null;
        }
        

        public void setSms_ChargeIndicator (SMS_ChargeIndicator value) {
            this.sms_ChargeIndicator = value;
        }
        
  
        
        public SMS_DestinationAddress getSms_DestinationAddress () {
            return this.sms_DestinationAddress;
        }

        
        public boolean isSms_DestinationAddressPresent () {
            return this.sms_DestinationAddress != null;
        }
        

        public void setSms_DestinationAddress (SMS_DestinationAddress value) {
            this.sms_DestinationAddress = value;
        }
        
  
        
        public SMS_PendingMessageCount getSms_PendingMessageCount () {
            return this.sms_PendingMessageCount;
        }

        
        public boolean isSms_PendingMessageCountPresent () {
            return this.sms_PendingMessageCount != null;
        }
        

        public void setSms_PendingMessageCount (SMS_PendingMessageCount value) {
            this.sms_PendingMessageCount = value;
        }
        
  
        
        public SMS_OriginalDestinationAddress getSms_OriginalDestinationAddress () {
            return this.sms_OriginalDestinationAddress;
        }

        
        public boolean isSms_OriginalDestinationAddressPresent () {
            return this.sms_OriginalDestinationAddress != null;
        }
        

        public void setSms_OriginalDestinationAddress (SMS_OriginalDestinationAddress value) {
            this.sms_OriginalDestinationAddress = value;
        }
        
  
        
        public SMS_OriginalDestinationSubaddress getSms_OriginalDestinationSubaddress () {
            return this.sms_OriginalDestinationSubaddress;
        }

        
        public boolean isSms_OriginalDestinationSubaddressPresent () {
            return this.sms_OriginalDestinationSubaddress != null;
        }
        

        public void setSms_OriginalDestinationSubaddress (SMS_OriginalDestinationSubaddress value) {
            this.sms_OriginalDestinationSubaddress = value;
        }
        
  
        
        public SMS_OriginalOriginatingAddress getSms_OriginalOriginatingAddress () {
            return this.sms_OriginalOriginatingAddress;
        }

        
        public boolean isSms_OriginalOriginatingAddressPresent () {
            return this.sms_OriginalOriginatingAddress != null;
        }
        

        public void setSms_OriginalOriginatingAddress (SMS_OriginalOriginatingAddress value) {
            this.sms_OriginalOriginatingAddress = value;
        }
        
  
        
        public SMS_OriginalOriginatingSubaddress getSms_OriginalOriginatingSubaddress () {
            return this.sms_OriginalOriginatingSubaddress;
        }

        
        public boolean isSms_OriginalOriginatingSubaddressPresent () {
            return this.sms_OriginalOriginatingSubaddress != null;
        }
        

        public void setSms_OriginalOriginatingSubaddress (SMS_OriginalOriginatingSubaddress value) {
            this.sms_OriginalOriginatingSubaddress = value;
        }
        
  
        
        public SMS_OriginatingAddress getSms_OriginatingAddress () {
            return this.sms_OriginatingAddress;
        }

        
        public boolean isSms_OriginatingAddressPresent () {
            return this.sms_OriginatingAddress != null;
        }
        

        public void setSms_OriginatingAddress (SMS_OriginatingAddress value) {
            this.sms_OriginatingAddress = value;
        }
        
  
        
        public TimeDateOffset getTimeDateOffset () {
            return this.timeDateOffset;
        }

        
        public boolean isTimeDateOffsetPresent () {
            return this.timeDateOffset != null;
        }
        

        public void setTimeDateOffset (TimeDateOffset value) {
            this.timeDateOffset = value;
        }
        
  
        
        public TimeOfDay getTimeOfDay () {
            return this.timeOfDay;
        }

        
        public boolean isTimeOfDayPresent () {
            return this.timeOfDay != null;
        }
        

        public void setTimeOfDay (TimeOfDay value) {
            this.timeOfDay = value;
        }
        
  
                
                
        public void initWithDefaults() {
            
        }

        public IASN1PreparedElementData getPreparedData() {
            return preparedData_ShortMessageAnalyzedSequenceType;
        }

       private static IASN1PreparedElementData preparedData_ShortMessageAnalyzedSequenceType = CoderFactory.getInstance().newPreparedElementData(ShortMessageAnalyzedSequenceType.class);
                
       }

       
                
        @ASN1Element ( name = "ShortMessageAnalyzed", isOptional =  false , hasTag =  true, tag = 18, 
        tagClass =  TagClass.Private  , hasDefaultValue =  false  )
    
        private ShortMessageAnalyzedSequenceType  value;        

        
        
        public ShortMessageAnalyzed () {
        }
        
        
        
        public void setValue(ShortMessageAnalyzedSequenceType value) {
            this.value = value;
        }
        
        
        
        public ShortMessageAnalyzedSequenceType getValue() {
            return this.value;
        }            
        

	    public void initWithDefaults() {
	    }

        private static IASN1PreparedElementData preparedData = CoderFactory.getInstance().newPreparedElementData(ShortMessageAnalyzed.class);
        public IASN1PreparedElementData getPreparedData() {
            return preparedData;
        }

            
    }
            