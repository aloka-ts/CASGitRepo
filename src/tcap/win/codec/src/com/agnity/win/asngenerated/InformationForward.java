
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
    @ASN1BoxedType ( name = "InformationForward" )
    public class InformationForward implements IASN1PreparedElement {
                
        

       @ASN1PreparedElement
       @ASN1Sequence ( name = "InformationForward" , isSet = true )
       public static class InformationForwardSequenceType implements IASN1PreparedElement {
                
        @ASN1Element ( name = "interMSCCircuitID", isOptional =  false , hasTag =  true, tag = 6 , hasDefaultValue =  false  )
    
	private InterMSCCircuitID interMSCCircuitID = null;
                
  
        @ASN1Element ( name = "mobileIdentificationNumber", isOptional =  false , hasTag =  true, tag = 8 , hasDefaultValue =  false  )
    
	private MobileIdentificationNumber mobileIdentificationNumber = null;
                
  
        @ASN1Element ( name = "alertCode", isOptional =  true , hasTag =  true, tag = 75 , hasDefaultValue =  false  )
    
	private AlertCode alertCode = null;
                
  
        @ASN1Element ( name = "announcementList", isOptional =  true , hasTag =  true, tag = 130 , hasDefaultValue =  false  )
    
	private AnnouncementList announcementList = null;
                
  
        @ASN1Element ( name = "callingPartyNumberString1", isOptional =  true , hasTag =  true, tag = 82 , hasDefaultValue =  false  )
    
	private CallingPartyNumberString1 callingPartyNumberString1 = null;
                
  
        @ASN1Element ( name = "callingPartyNumberString2", isOptional =  true , hasTag =  true, tag = 83 , hasDefaultValue =  false  )
    
	private CallingPartyNumberString2 callingPartyNumberString2 = null;
                
  
        @ASN1Element ( name = "callingPartySubaddress", isOptional =  true , hasTag =  true, tag = 84 , hasDefaultValue =  false  )
    
	private CallingPartySubaddress callingPartySubaddress = null;
                
  
        @ASN1Element ( name = "displayText", isOptional =  true , hasTag =  true, tag = 244 , hasDefaultValue =  false  )
    
	private DisplayText displayText = null;
                
  
        @ASN1Element ( name = "displayText2", isOptional =  true , hasTag =  true, tag = 299 , hasDefaultValue =  false  )
    
	private DisplayText2 displayText2 = null;
                
  
        @ASN1Element ( name = "electronicSerialNumber", isOptional =  true , hasTag =  true, tag = 9 , hasDefaultValue =  false  )
    
	private ElectronicSerialNumber electronicSerialNumber = null;
                
  
        @ASN1Element ( name = "messageWaitingNotificationCount", isOptional =  true , hasTag =  true, tag = 92 , hasDefaultValue =  false  )
    
	private MessageWaitingNotificationCount messageWaitingNotificationCount = null;
                
  
        @ASN1Element ( name = "messageWaitingNotificationType", isOptional =  true , hasTag =  true, tag = 145 , hasDefaultValue =  false  )
    
	private MessageWaitingNotificationType messageWaitingNotificationType = null;
                
  
        @ASN1Element ( name = "redirectingNumberString", isOptional =  true , hasTag =  true, tag = 101 , hasDefaultValue =  false  )
    
	private RedirectingNumberString redirectingNumberString = null;
                
  
        @ASN1Element ( name = "redirectingSubaddress", isOptional =  true , hasTag =  true, tag = 102 , hasDefaultValue =  false  )
    
	private RedirectingSubaddress redirectingSubaddress = null;
                
  
        @ASN1Element ( name = "meid", isOptional =  true , hasTag =  true, tag = 390 , hasDefaultValue =  false  )
    
	private MEID meid = null;
                
  
        
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
        
  
        
        public AlertCode getAlertCode () {
            return this.alertCode;
        }

        
        public boolean isAlertCodePresent () {
            return this.alertCode != null;
        }
        

        public void setAlertCode (AlertCode value) {
            this.alertCode = value;
        }
        
  
        
        public AnnouncementList getAnnouncementList () {
            return this.announcementList;
        }

        
        public boolean isAnnouncementListPresent () {
            return this.announcementList != null;
        }
        

        public void setAnnouncementList (AnnouncementList value) {
            this.announcementList = value;
        }
        
  
        
        public CallingPartyNumberString1 getCallingPartyNumberString1 () {
            return this.callingPartyNumberString1;
        }

        
        public boolean isCallingPartyNumberString1Present () {
            return this.callingPartyNumberString1 != null;
        }
        

        public void setCallingPartyNumberString1 (CallingPartyNumberString1 value) {
            this.callingPartyNumberString1 = value;
        }
        
  
        
        public CallingPartyNumberString2 getCallingPartyNumberString2 () {
            return this.callingPartyNumberString2;
        }

        
        public boolean isCallingPartyNumberString2Present () {
            return this.callingPartyNumberString2 != null;
        }
        

        public void setCallingPartyNumberString2 (CallingPartyNumberString2 value) {
            this.callingPartyNumberString2 = value;
        }
        
  
        
        public CallingPartySubaddress getCallingPartySubaddress () {
            return this.callingPartySubaddress;
        }

        
        public boolean isCallingPartySubaddressPresent () {
            return this.callingPartySubaddress != null;
        }
        

        public void setCallingPartySubaddress (CallingPartySubaddress value) {
            this.callingPartySubaddress = value;
        }
        
  
        
        public DisplayText getDisplayText () {
            return this.displayText;
        }

        
        public boolean isDisplayTextPresent () {
            return this.displayText != null;
        }
        

        public void setDisplayText (DisplayText value) {
            this.displayText = value;
        }
        
  
        
        public DisplayText2 getDisplayText2 () {
            return this.displayText2;
        }

        
        public boolean isDisplayText2Present () {
            return this.displayText2 != null;
        }
        

        public void setDisplayText2 (DisplayText2 value) {
            this.displayText2 = value;
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
        
  
        
        public MessageWaitingNotificationCount getMessageWaitingNotificationCount () {
            return this.messageWaitingNotificationCount;
        }

        
        public boolean isMessageWaitingNotificationCountPresent () {
            return this.messageWaitingNotificationCount != null;
        }
        

        public void setMessageWaitingNotificationCount (MessageWaitingNotificationCount value) {
            this.messageWaitingNotificationCount = value;
        }
        
  
        
        public MessageWaitingNotificationType getMessageWaitingNotificationType () {
            return this.messageWaitingNotificationType;
        }

        
        public boolean isMessageWaitingNotificationTypePresent () {
            return this.messageWaitingNotificationType != null;
        }
        

        public void setMessageWaitingNotificationType (MessageWaitingNotificationType value) {
            this.messageWaitingNotificationType = value;
        }
        
  
        
        public RedirectingNumberString getRedirectingNumberString () {
            return this.redirectingNumberString;
        }

        
        public boolean isRedirectingNumberStringPresent () {
            return this.redirectingNumberString != null;
        }
        

        public void setRedirectingNumberString (RedirectingNumberString value) {
            this.redirectingNumberString = value;
        }
        
  
        
        public RedirectingSubaddress getRedirectingSubaddress () {
            return this.redirectingSubaddress;
        }

        
        public boolean isRedirectingSubaddressPresent () {
            return this.redirectingSubaddress != null;
        }
        

        public void setRedirectingSubaddress (RedirectingSubaddress value) {
            this.redirectingSubaddress = value;
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
            return preparedData_InformationForwardSequenceType;
        }

       private static IASN1PreparedElementData preparedData_InformationForwardSequenceType = CoderFactory.getInstance().newPreparedElementData(InformationForwardSequenceType.class);
                
       }

       
                
        @ASN1Element ( name = "InformationForward", isOptional =  false , hasTag =  true, tag = 18, 
        tagClass =  TagClass.Private  , hasDefaultValue =  false  )
    
        private InformationForwardSequenceType  value;        

        
        
        public InformationForward () {
        }
        
        
        
        public void setValue(InformationForwardSequenceType value) {
            this.value = value;
        }
        
        
        
        public InformationForwardSequenceType getValue() {
            return this.value;
        }            
        

	    public void initWithDefaults() {
	    }

        private static IASN1PreparedElementData preparedData = CoderFactory.getInstance().newPreparedElementData(InformationForward.class);
        public IASN1PreparedElementData getPreparedData() {
            return preparedData;
        }

            
    }
            