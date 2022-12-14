
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
    @ASN1BoxedType ( name = "SMSDeliveryPointToPoint" )
    public class SMSDeliveryPointToPoint implements IASN1PreparedElement {
                
        

       @ASN1PreparedElement
       @ASN1Sequence ( name = "SMSDeliveryPointToPoint" , isSet = true )
       public static class SMSDeliveryPointToPointSequenceType implements IASN1PreparedElement {
                
        @ASN1Element ( name = "sms-BearerData", isOptional =  false , hasTag =  true, tag = 105 , hasDefaultValue =  false  )
    
	private SMS_BearerData sms_BearerData = null;
                
  
        @ASN1Element ( name = "sms-TeleserviceIdentifier", isOptional =  false , hasTag =  true, tag = 116 , hasDefaultValue =  false  )
    
	private SMS_TeleserviceIdentifier sms_TeleserviceIdentifier = null;
                
  
        @ASN1Element ( name = "actionCode", isOptional =  true , hasTag =  true, tag = 128 , hasDefaultValue =  false  )
    
	private ActionCode actionCode = null;
                
  
        @ASN1Element ( name = "cdmaServingOneWayDelay2", isOptional =  true , hasTag =  true, tag = 347 , hasDefaultValue =  false  )
    
	private CDMAServingOneWayDelay2 cdmaServingOneWayDelay2 = null;
                
  
        @ASN1Element ( name = "electronicSerialNumber", isOptional =  true , hasTag =  true, tag = 9 , hasDefaultValue =  false  )
    
	private ElectronicSerialNumber electronicSerialNumber = null;
                
  
        @ASN1Element ( name = "meid", isOptional =  true , hasTag =  true, tag = 390 , hasDefaultValue =  false  )
    
	private MEID meid = null;
                
  
        @ASN1Element ( name = "interMessageTime", isOptional =  true , hasTag =  true, tag = 325 , hasDefaultValue =  false  )
    
	private InterMessageTime interMessageTime = null;
                
  
        @ASN1Element ( name = "mscid", isOptional =  true , hasTag =  true, tag = 21 , hasDefaultValue =  false  )
    
	private MSCID mscid = null;
                
  
        @ASN1Element ( name = "imsi", isOptional =  true , hasTag =  true, tag = 242 , hasDefaultValue =  false  )
    
	private IMSI imsi = null;
                
  
        @ASN1Element ( name = "mobileIdentificationNumber", isOptional =  true , hasTag =  true, tag = 8 , hasDefaultValue =  false  )
    
	private MobileIdentificationNumber mobileIdentificationNumber = null;
                
  
        @ASN1Element ( name = "newlyAssignedIMSI", isOptional =  true , hasTag =  true, tag = 287 , hasDefaultValue =  false  )
    
	private NewlyAssignedIMSI newlyAssignedIMSI = null;
                
  
        @ASN1Element ( name = "newlyAssignedMIN", isOptional =  true , hasTag =  true, tag = 187 , hasDefaultValue =  false  )
    
	private NewlyAssignedMIN newlyAssignedMIN = null;
                
  
        @ASN1Element ( name = "newMINExtension", isOptional =  true , hasTag =  true, tag = 328 , hasDefaultValue =  false  )
    
	private NewMINExtension newMINExtension = null;
                
  
        @ASN1Element ( name = "serviceIndicator", isOptional =  true , hasTag =  true, tag = 193 , hasDefaultValue =  false  )
    
	private ServiceIndicator serviceIndicator = null;
                
  
        @ASN1Element ( name = "sms-ChargeIndicator", isOptional =  true , hasTag =  true, tag = 106 , hasDefaultValue =  false  )
    
	private SMS_ChargeIndicator sms_ChargeIndicator = null;
                
  
        @ASN1Element ( name = "sms-DestinationAddress", isOptional =  true , hasTag =  true, tag = 107 , hasDefaultValue =  false  )
    
	private SMS_DestinationAddress sms_DestinationAddress = null;
                
  
        @ASN1Element ( name = "sms-MessageCount", isOptional =  true , hasTag =  true, tag = 108 , hasDefaultValue =  false  )
    
	private SMS_MessageCount sms_MessageCount = null;
                
  
        @ASN1Element ( name = "sms-NotificationIndicator", isOptional =  true , hasTag =  true, tag = 109 , hasDefaultValue =  false  )
    
	private SMS_NotificationIndicator sms_NotificationIndicator = null;
                
  
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
                
  
        @ASN1Element ( name = "teleservice-Priority", isOptional =  true , hasTag =  true, tag = 290 , hasDefaultValue =  false  )
    
	private Teleservice_Priority teleservice_Priority = null;
                
  
        @ASN1Element ( name = "temporaryReferenceNumber", isOptional =  true , hasTag =  true, tag = 195 , hasDefaultValue =  false  )
    
	private TemporaryReferenceNumber temporaryReferenceNumber = null;
                
  
        
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
        
  
        
        public ActionCode getActionCode () {
            return this.actionCode;
        }

        
        public boolean isActionCodePresent () {
            return this.actionCode != null;
        }
        

        public void setActionCode (ActionCode value) {
            this.actionCode = value;
        }
        
  
        
        public CDMAServingOneWayDelay2 getCdmaServingOneWayDelay2 () {
            return this.cdmaServingOneWayDelay2;
        }

        
        public boolean isCdmaServingOneWayDelay2Present () {
            return this.cdmaServingOneWayDelay2 != null;
        }
        

        public void setCdmaServingOneWayDelay2 (CDMAServingOneWayDelay2 value) {
            this.cdmaServingOneWayDelay2 = value;
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
        
  
        
        public MEID getMeid () {
            return this.meid;
        }

        
        public boolean isMeidPresent () {
            return this.meid != null;
        }
        

        public void setMeid (MEID value) {
            this.meid = value;
        }
        
  
        
        public InterMessageTime getInterMessageTime () {
            return this.interMessageTime;
        }

        
        public boolean isInterMessageTimePresent () {
            return this.interMessageTime != null;
        }
        

        public void setInterMessageTime (InterMessageTime value) {
            this.interMessageTime = value;
        }
        
  
        
        public MSCID getMscid () {
            return this.mscid;
        }

        
        public boolean isMscidPresent () {
            return this.mscid != null;
        }
        

        public void setMscid (MSCID value) {
            this.mscid = value;
        }
        
  
        
        public IMSI getImsi () {
            return this.imsi;
        }

        
        public boolean isImsiPresent () {
            return this.imsi != null;
        }
        

        public void setImsi (IMSI value) {
            this.imsi = value;
        }
        
  
        
        public MobileIdentificationNumber getMobileIdentificationNumber () {
            return this.mobileIdentificationNumber;
        }

        
        public boolean isMobileIdentificationNumberPresent () {
            return this.mobileIdentificationNumber != null;
        }
        

        public void setMobileIdentificationNumber (MobileIdentificationNumber value) {
            this.mobileIdentificationNumber = value;
        }
        
  
        
        public NewlyAssignedIMSI getNewlyAssignedIMSI () {
            return this.newlyAssignedIMSI;
        }

        
        public boolean isNewlyAssignedIMSIPresent () {
            return this.newlyAssignedIMSI != null;
        }
        

        public void setNewlyAssignedIMSI (NewlyAssignedIMSI value) {
            this.newlyAssignedIMSI = value;
        }
        
  
        
        public NewlyAssignedMIN getNewlyAssignedMIN () {
            return this.newlyAssignedMIN;
        }

        
        public boolean isNewlyAssignedMINPresent () {
            return this.newlyAssignedMIN != null;
        }
        

        public void setNewlyAssignedMIN (NewlyAssignedMIN value) {
            this.newlyAssignedMIN = value;
        }
        
  
        
        public NewMINExtension getNewMINExtension () {
            return this.newMINExtension;
        }

        
        public boolean isNewMINExtensionPresent () {
            return this.newMINExtension != null;
        }
        

        public void setNewMINExtension (NewMINExtension value) {
            this.newMINExtension = value;
        }
        
  
        
        public ServiceIndicator getServiceIndicator () {
            return this.serviceIndicator;
        }

        
        public boolean isServiceIndicatorPresent () {
            return this.serviceIndicator != null;
        }
        

        public void setServiceIndicator (ServiceIndicator value) {
            this.serviceIndicator = value;
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
        
  
        
        public SMS_MessageCount getSms_MessageCount () {
            return this.sms_MessageCount;
        }

        
        public boolean isSms_MessageCountPresent () {
            return this.sms_MessageCount != null;
        }
        

        public void setSms_MessageCount (SMS_MessageCount value) {
            this.sms_MessageCount = value;
        }
        
  
        
        public SMS_NotificationIndicator getSms_NotificationIndicator () {
            return this.sms_NotificationIndicator;
        }

        
        public boolean isSms_NotificationIndicatorPresent () {
            return this.sms_NotificationIndicator != null;
        }
        

        public void setSms_NotificationIndicator (SMS_NotificationIndicator value) {
            this.sms_NotificationIndicator = value;
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
        
  
        
        public Teleservice_Priority getTeleservice_Priority () {
            return this.teleservice_Priority;
        }

        
        public boolean isTeleservice_PriorityPresent () {
            return this.teleservice_Priority != null;
        }
        

        public void setTeleservice_Priority (Teleservice_Priority value) {
            this.teleservice_Priority = value;
        }
        
  
        
        public TemporaryReferenceNumber getTemporaryReferenceNumber () {
            return this.temporaryReferenceNumber;
        }

        
        public boolean isTemporaryReferenceNumberPresent () {
            return this.temporaryReferenceNumber != null;
        }
        

        public void setTemporaryReferenceNumber (TemporaryReferenceNumber value) {
            this.temporaryReferenceNumber = value;
        }
        
  
                
                
        public void initWithDefaults() {
            
        }

        public IASN1PreparedElementData getPreparedData() {
            return preparedData_SMSDeliveryPointToPointSequenceType;
        }

       private static IASN1PreparedElementData preparedData_SMSDeliveryPointToPointSequenceType = CoderFactory.getInstance().newPreparedElementData(SMSDeliveryPointToPointSequenceType.class);
                
       }

       
                
        @ASN1Element ( name = "SMSDeliveryPointToPoint", isOptional =  false , hasTag =  true, tag = 18, 
        tagClass =  TagClass.Private  , hasDefaultValue =  false  )
    
        private SMSDeliveryPointToPointSequenceType  value;        

        
        
        public SMSDeliveryPointToPoint () {
        }
        
        
        
        public void setValue(SMSDeliveryPointToPointSequenceType value) {
            this.value = value;
        }
        
        
        
        public SMSDeliveryPointToPointSequenceType getValue() {
            return this.value;
        }            
        

	    public void initWithDefaults() {
	    }

        private static IASN1PreparedElementData preparedData = CoderFactory.getInstance().newPreparedElementData(SMSDeliveryPointToPoint.class);
        public IASN1PreparedElementData getPreparedData() {
            return preparedData;
        }

            
    }
            