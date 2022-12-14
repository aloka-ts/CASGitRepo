
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
    @ASN1BoxedType ( name = "SMSDeliveryBackward" )
    public class SMSDeliveryBackward implements IASN1PreparedElement {
                
        

       @ASN1PreparedElement
       @ASN1Sequence ( name = "SMSDeliveryBackward" , isSet = true )
       public static class SMSDeliveryBackwardSequenceType implements IASN1PreparedElement {
                
        @ASN1Element ( name = "interMSCCircuitID", isOptional =  false , hasTag =  true, tag = 6 , hasDefaultValue =  false  )
    
	private InterMSCCircuitID interMSCCircuitID = null;
                
  
        @ASN1Element ( name = "mobileIdentificationNumber", isOptional =  false , hasTag =  true, tag = 8 , hasDefaultValue =  false  )
    
	private MobileIdentificationNumber mobileIdentificationNumber = null;
                
  
        @ASN1Element ( name = "sms-BearerData", isOptional =  false , hasTag =  true, tag = 105 , hasDefaultValue =  false  )
    
	private SMS_BearerData sms_BearerData = null;
                
  
        @ASN1Element ( name = "sms-TeleserviceIdentifier", isOptional =  false , hasTag =  true, tag = 116 , hasDefaultValue =  false  )
    
	private SMS_TeleserviceIdentifier sms_TeleserviceIdentifier = null;
                
  
        @ASN1Element ( name = "electronicSerialNumber", isOptional =  true , hasTag =  true, tag = 9 , hasDefaultValue =  false  )
    
	private ElectronicSerialNumber electronicSerialNumber = null;
                
  
        @ASN1Element ( name = "sms-ChargeIndicator", isOptional =  true , hasTag =  true, tag = 106 , hasDefaultValue =  false  )
    
	private SMS_ChargeIndicator sms_ChargeIndicator = null;
                
  
        @ASN1Element ( name = "sms-DestinationAddress", isOptional =  true , hasTag =  true, tag = 107 , hasDefaultValue =  false  )
    
	private SMS_DestinationAddress sms_DestinationAddress = null;
                
  
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
                
  
        
        public InterMSCCircuitID getInterMSCCircuitID () {
            return this.interMSCCircuitID;
        }

        

        public void setInterMSCCircuitID (InterMSCCircuitID value) {
            this.interMSCCircuitID = value;
        }
        
  
        
        public MobileIdentificationNumber getMobileIdentificationNumber () {
            return this.mobileIdentificationNumber;
        }

        

        public void setMobileIdentificationNumber (MobileIdentificationNumber value) {
            this.mobileIdentificationNumber = value;
        }
        
  
        
        public SMS_BearerData getSms_BearerData () {
            return this.sms_BearerData;
        }

        

        public void setSms_BearerData (SMS_BearerData value) {
            this.sms_BearerData = value;
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
        
  
                
                
        public void initWithDefaults() {
            
        }

        public IASN1PreparedElementData getPreparedData() {
            return preparedData_SMSDeliveryBackwardSequenceType;
        }

       private static IASN1PreparedElementData preparedData_SMSDeliveryBackwardSequenceType = CoderFactory.getInstance().newPreparedElementData(SMSDeliveryBackwardSequenceType.class);
                
       }

       
                
        @ASN1Element ( name = "SMSDeliveryBackward", isOptional =  false , hasTag =  true, tag = 18, 
        tagClass =  TagClass.Private  , hasDefaultValue =  false  )
    
        private SMSDeliveryBackwardSequenceType  value;        

        
        
        public SMSDeliveryBackward () {
        }
        
        
        
        public void setValue(SMSDeliveryBackwardSequenceType value) {
            this.value = value;
        }
        
        
        
        public SMSDeliveryBackwardSequenceType getValue() {
            return this.value;
        }            
        

	    public void initWithDefaults() {
	    }

        private static IASN1PreparedElementData preparedData = CoderFactory.getInstance().newPreparedElementData(SMSDeliveryBackward.class);
        public IASN1PreparedElementData getPreparedData() {
            return preparedData;
        }

            
    }
            