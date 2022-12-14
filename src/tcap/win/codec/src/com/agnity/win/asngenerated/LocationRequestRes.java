
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
    @ASN1BoxedType ( name = "LocationRequestRes" )
    public class LocationRequestRes implements IASN1PreparedElement {
                
        

       @ASN1PreparedElement
       @ASN1Sequence ( name = "LocationRequestRes" , isSet = true )
       public static class LocationRequestResSequenceType implements IASN1PreparedElement {
                
        @ASN1Element ( name = "electronicSerialNumber", isOptional =  false , hasTag =  true, tag = 9 , hasDefaultValue =  false  )
    
	private ElectronicSerialNumber electronicSerialNumber = null;
                
  
        @ASN1Element ( name = "mobileIdentificationNumber", isOptional =  false , hasTag =  true, tag = 8 , hasDefaultValue =  false  )
    
	private MobileIdentificationNumber mobileIdentificationNumber = null;
                
  
        @ASN1Element ( name = "mscid", isOptional =  false , hasTag =  true, tag = 21 , hasDefaultValue =  false  )
    
	private MSCID mscid = null;
                
  
        @ASN1Element ( name = "accessDeniedReason", isOptional =  true , hasTag =  true, tag = 20 , hasDefaultValue =  false  )
    
	private AccessDeniedReason accessDeniedReason = null;
                
  
        @ASN1Element ( name = "announcementList", isOptional =  true , hasTag =  true, tag = 130 , hasDefaultValue =  false  )
    
	private AnnouncementList announcementList = null;
                
  
        @ASN1Element ( name = "callingPartyNumberString1", isOptional =  true , hasTag =  true, tag = 82 , hasDefaultValue =  false  )
    
	private CallingPartyNumberString1 callingPartyNumberString1 = null;
                
  
        @ASN1Element ( name = "callingPartyNumberString2", isOptional =  true , hasTag =  true, tag = 83 , hasDefaultValue =  false  )
    
	private CallingPartyNumberString2 callingPartyNumberString2 = null;
                
  
        @ASN1Element ( name = "cdmaServiceOption", isOptional =  true , hasTag =  true, tag = 175 , hasDefaultValue =  false  )
    
	private CDMAServiceOption cdmaServiceOption = null;
                
  
        @ASN1Element ( name = "controlNetworkID", isOptional =  true , hasTag =  true, tag = 307 , hasDefaultValue =  false  )
    
	private ControlNetworkID controlNetworkID = null;
                
  
        @ASN1Element ( name = "digits-carrier", isOptional =  true , hasTag =  true, tag = 4 , hasDefaultValue =  false  )
    
	private Digits digits_carrier = null;
                
  
        @ASN1Element ( name = "digits-dest", isOptional =  true , hasTag =  true, tag = 4 , hasDefaultValue =  false  )
    
	private Digits digits_dest = null;
                
  
        @ASN1Element ( name = "displayText", isOptional =  true , hasTag =  true, tag = 244 , hasDefaultValue =  false  )
    
	private DisplayText displayText = null;
                
  
        @ASN1Element ( name = "displayText2", isOptional =  true , hasTag =  true, tag = 299 , hasDefaultValue =  false  )
    
	private DisplayText2 displayText2 = null;
                
  
        @ASN1Element ( name = "dmh-AccountCodeDigits", isOptional =  true , hasTag =  true, tag = 140 , hasDefaultValue =  false  )
    
	private DMH_AccountCodeDigits dmh_AccountCodeDigits = null;
                
  
        @ASN1Element ( name = "dmh-AlternateBillingDigits", isOptional =  true , hasTag =  true, tag = 141 , hasDefaultValue =  false  )
    
	private DMH_AlternateBillingDigits dmh_AlternateBillingDigits = null;
                
  
        @ASN1Element ( name = "dmh-BillingDigits", isOptional =  true , hasTag =  true, tag = 142 , hasDefaultValue =  false  )
    
	private DMH_BillingDigits dmh_BillingDigits = null;
                
  
        @ASN1Element ( name = "dmh-RedirectionIndicator", isOptional =  true , hasTag =  true, tag = 88 , hasDefaultValue =  false  )
    
	private DMH_RedirectionIndicator dmh_RedirectionIndicator = null;
                
  
        @ASN1Element ( name = "dmh-ServiceID", isOptional =  true , hasTag =  true, tag = 305 , hasDefaultValue =  false  )
    
	private DMH_ServiceID dmh_ServiceID = null;
                
  
        @ASN1Element ( name = "groupInformation", isOptional =  true , hasTag =  true, tag = 163 , hasDefaultValue =  false  )
    
	private GroupInformation groupInformation = null;
                
  
        @ASN1Element ( name = "mobileDirectoryNumber", isOptional =  true , hasTag =  true, tag = 93 , hasDefaultValue =  false  )
    
	private MobileDirectoryNumber mobileDirectoryNumber = null;
                
  
        @ASN1Element ( name = "noAnswerTime", isOptional =  true , hasTag =  true, tag = 96 , hasDefaultValue =  false  )
    
	private NoAnswerTime noAnswerTime = null;
                
  
        @ASN1Element ( name = "oneTimeFeatureIndicator", isOptional =  true , hasTag =  true, tag = 97 , hasDefaultValue =  false  )
    
	private OneTimeFeatureIndicator oneTimeFeatureIndicator = null;
                
  
        @ASN1Element ( name = "pc-ssn", isOptional =  true , hasTag =  true, tag = 32 , hasDefaultValue =  false  )
    
	private PC_SSN pc_ssn = null;
                
  
        @ASN1Element ( name = "preferredLanguageIndicator", isOptional =  true , hasTag =  true, tag = 147 , hasDefaultValue =  false  )
    
	private PreferredLanguageIndicator preferredLanguageIndicator = null;
                
  
        @ASN1Element ( name = "redirectingNumberDigits", isOptional =  true , hasTag =  true, tag = 100 , hasDefaultValue =  false  )
    
	private RedirectingNumberDigits redirectingNumberDigits = null;
                
  
        @ASN1Element ( name = "redirectingNumberString", isOptional =  true , hasTag =  true, tag = 101 , hasDefaultValue =  false  )
    
	private RedirectingNumberString redirectingNumberString = null;
                
  
        @ASN1Element ( name = "redirectingSubaddress", isOptional =  true , hasTag =  true, tag = 102 , hasDefaultValue =  false  )
    
	private RedirectingSubaddress redirectingSubaddress = null;
                
  
        @ASN1Element ( name = "routingDigits", isOptional =  true , hasTag =  true, tag = 150 , hasDefaultValue =  false  )
    
	private RoutingDigits routingDigits = null;
                
  
        @ASN1Element ( name = "tdmaServiceCode", isOptional =  true , hasTag =  true, tag = 178 , hasDefaultValue =  false  )
    
	private TDMAServiceCode tdmaServiceCode = null;
                
  
        @ASN1Element ( name = "terminationList", isOptional =  true , hasTag =  true, tag = 120 , hasDefaultValue =  false  )
    
	private TerminationList terminationList = null;
                
  
        @ASN1Element ( name = "terminationTriggers", isOptional =  true , hasTag =  true, tag = 122 , hasDefaultValue =  false  )
    
	private TerminationTriggers terminationTriggers = null;
                
  
        @ASN1Element ( name = "triggerAddressList", isOptional =  true , hasTag =  true, tag = 276 , hasDefaultValue =  false  )
    
	private TriggerAddressList triggerAddressList = null;
                
  
        @ASN1Element ( name = "meid", isOptional =  true , hasTag =  true, tag = 390 , hasDefaultValue =  false  )
    
	private MEID meid = null;
                
  
        
        public ElectronicSerialNumber getElectronicSerialNumber () {
            return this.electronicSerialNumber;
        }

        

        public void setElectronicSerialNumber (ElectronicSerialNumber value) {
            this.electronicSerialNumber = value;
        }
        
  
        
        public MobileIdentificationNumber getMobileIdentificationNumber () {
            return this.mobileIdentificationNumber;
        }

        

        public void setMobileIdentificationNumber (MobileIdentificationNumber value) {
            this.mobileIdentificationNumber = value;
        }
        
  
        
        public MSCID getMscid () {
            return this.mscid;
        }

        

        public void setMscid (MSCID value) {
            this.mscid = value;
        }
        
  
        
        public AccessDeniedReason getAccessDeniedReason () {
            return this.accessDeniedReason;
        }

        
        public boolean isAccessDeniedReasonPresent () {
            return this.accessDeniedReason != null;
        }
        

        public void setAccessDeniedReason (AccessDeniedReason value) {
            this.accessDeniedReason = value;
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
        
  
        
        public CDMAServiceOption getCdmaServiceOption () {
            return this.cdmaServiceOption;
        }

        
        public boolean isCdmaServiceOptionPresent () {
            return this.cdmaServiceOption != null;
        }
        

        public void setCdmaServiceOption (CDMAServiceOption value) {
            this.cdmaServiceOption = value;
        }
        
  
        
        public ControlNetworkID getControlNetworkID () {
            return this.controlNetworkID;
        }

        
        public boolean isControlNetworkIDPresent () {
            return this.controlNetworkID != null;
        }
        

        public void setControlNetworkID (ControlNetworkID value) {
            this.controlNetworkID = value;
        }
        
  
        
        public Digits getDigits_carrier () {
            return this.digits_carrier;
        }

        
        public boolean isDigits_carrierPresent () {
            return this.digits_carrier != null;
        }
        

        public void setDigits_carrier (Digits value) {
            this.digits_carrier = value;
        }
        
  
        
        public Digits getDigits_dest () {
            return this.digits_dest;
        }

        
        public boolean isDigits_destPresent () {
            return this.digits_dest != null;
        }
        

        public void setDigits_dest (Digits value) {
            this.digits_dest = value;
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
        
  
        
        public DMH_AccountCodeDigits getDmh_AccountCodeDigits () {
            return this.dmh_AccountCodeDigits;
        }

        
        public boolean isDmh_AccountCodeDigitsPresent () {
            return this.dmh_AccountCodeDigits != null;
        }
        

        public void setDmh_AccountCodeDigits (DMH_AccountCodeDigits value) {
            this.dmh_AccountCodeDigits = value;
        }
        
  
        
        public DMH_AlternateBillingDigits getDmh_AlternateBillingDigits () {
            return this.dmh_AlternateBillingDigits;
        }

        
        public boolean isDmh_AlternateBillingDigitsPresent () {
            return this.dmh_AlternateBillingDigits != null;
        }
        

        public void setDmh_AlternateBillingDigits (DMH_AlternateBillingDigits value) {
            this.dmh_AlternateBillingDigits = value;
        }
        
  
        
        public DMH_BillingDigits getDmh_BillingDigits () {
            return this.dmh_BillingDigits;
        }

        
        public boolean isDmh_BillingDigitsPresent () {
            return this.dmh_BillingDigits != null;
        }
        

        public void setDmh_BillingDigits (DMH_BillingDigits value) {
            this.dmh_BillingDigits = value;
        }
        
  
        
        public DMH_RedirectionIndicator getDmh_RedirectionIndicator () {
            return this.dmh_RedirectionIndicator;
        }

        
        public boolean isDmh_RedirectionIndicatorPresent () {
            return this.dmh_RedirectionIndicator != null;
        }
        

        public void setDmh_RedirectionIndicator (DMH_RedirectionIndicator value) {
            this.dmh_RedirectionIndicator = value;
        }
        
  
        
        public DMH_ServiceID getDmh_ServiceID () {
            return this.dmh_ServiceID;
        }

        
        public boolean isDmh_ServiceIDPresent () {
            return this.dmh_ServiceID != null;
        }
        

        public void setDmh_ServiceID (DMH_ServiceID value) {
            this.dmh_ServiceID = value;
        }
        
  
        
        public GroupInformation getGroupInformation () {
            return this.groupInformation;
        }

        
        public boolean isGroupInformationPresent () {
            return this.groupInformation != null;
        }
        

        public void setGroupInformation (GroupInformation value) {
            this.groupInformation = value;
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
        
  
        
        public NoAnswerTime getNoAnswerTime () {
            return this.noAnswerTime;
        }

        
        public boolean isNoAnswerTimePresent () {
            return this.noAnswerTime != null;
        }
        

        public void setNoAnswerTime (NoAnswerTime value) {
            this.noAnswerTime = value;
        }
        
  
        
        public OneTimeFeatureIndicator getOneTimeFeatureIndicator () {
            return this.oneTimeFeatureIndicator;
        }

        
        public boolean isOneTimeFeatureIndicatorPresent () {
            return this.oneTimeFeatureIndicator != null;
        }
        

        public void setOneTimeFeatureIndicator (OneTimeFeatureIndicator value) {
            this.oneTimeFeatureIndicator = value;
        }
        
  
        
        public PC_SSN getPc_ssn () {
            return this.pc_ssn;
        }

        
        public boolean isPc_ssnPresent () {
            return this.pc_ssn != null;
        }
        

        public void setPc_ssn (PC_SSN value) {
            this.pc_ssn = value;
        }
        
  
        
        public PreferredLanguageIndicator getPreferredLanguageIndicator () {
            return this.preferredLanguageIndicator;
        }

        
        public boolean isPreferredLanguageIndicatorPresent () {
            return this.preferredLanguageIndicator != null;
        }
        

        public void setPreferredLanguageIndicator (PreferredLanguageIndicator value) {
            this.preferredLanguageIndicator = value;
        }
        
  
        
        public RedirectingNumberDigits getRedirectingNumberDigits () {
            return this.redirectingNumberDigits;
        }

        
        public boolean isRedirectingNumberDigitsPresent () {
            return this.redirectingNumberDigits != null;
        }
        

        public void setRedirectingNumberDigits (RedirectingNumberDigits value) {
            this.redirectingNumberDigits = value;
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
        
  
        
        public RoutingDigits getRoutingDigits () {
            return this.routingDigits;
        }

        
        public boolean isRoutingDigitsPresent () {
            return this.routingDigits != null;
        }
        

        public void setRoutingDigits (RoutingDigits value) {
            this.routingDigits = value;
        }
        
  
        
        public TDMAServiceCode getTdmaServiceCode () {
            return this.tdmaServiceCode;
        }

        
        public boolean isTdmaServiceCodePresent () {
            return this.tdmaServiceCode != null;
        }
        

        public void setTdmaServiceCode (TDMAServiceCode value) {
            this.tdmaServiceCode = value;
        }
        
  
        
        public TerminationList getTerminationList () {
            return this.terminationList;
        }

        
        public boolean isTerminationListPresent () {
            return this.terminationList != null;
        }
        

        public void setTerminationList (TerminationList value) {
            this.terminationList = value;
        }
        
  
        
        public TerminationTriggers getTerminationTriggers () {
            return this.terminationTriggers;
        }

        
        public boolean isTerminationTriggersPresent () {
            return this.terminationTriggers != null;
        }
        

        public void setTerminationTriggers (TerminationTriggers value) {
            this.terminationTriggers = value;
        }
        
  
        
        public TriggerAddressList getTriggerAddressList () {
            return this.triggerAddressList;
        }

        
        public boolean isTriggerAddressListPresent () {
            return this.triggerAddressList != null;
        }
        

        public void setTriggerAddressList (TriggerAddressList value) {
            this.triggerAddressList = value;
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
            return preparedData_LocationRequestResSequenceType;
        }

       private static IASN1PreparedElementData preparedData_LocationRequestResSequenceType = CoderFactory.getInstance().newPreparedElementData(LocationRequestResSequenceType.class);
                
       }

       
                
        @ASN1Element ( name = "LocationRequestRes", isOptional =  false , hasTag =  true, tag = 18, 
        tagClass =  TagClass.Private  , hasDefaultValue =  false  )
    
        private LocationRequestResSequenceType  value;        

        
        
        public LocationRequestRes () {
        }
        
        
        
        public void setValue(LocationRequestResSequenceType value) {
            this.value = value;
        }
        
        
        
        public LocationRequestResSequenceType getValue() {
            return this.value;
        }            
        

	    public void initWithDefaults() {
	    }

        private static IASN1PreparedElementData preparedData = CoderFactory.getInstance().newPreparedElementData(LocationRequestRes.class);
        public IASN1PreparedElementData getPreparedData() {
            return preparedData;
        }

            
    }
            