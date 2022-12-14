
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
    @ASN1BoxedType ( name = "TNoAnswerRes" )
    public class TNoAnswerRes implements IASN1PreparedElement {
                
        

       @ASN1PreparedElement
       @ASN1Sequence ( name = "TNoAnswerRes" , isSet = true )
       public static class TNoAnswerResSequenceType implements IASN1PreparedElement {
                
        @ASN1Element ( name = "accessDeniedReason", isOptional =  true , hasTag =  true, tag = 20 , hasDefaultValue =  false  )
    
	private AccessDeniedReason accessDeniedReason = null;
                
  
        @ASN1Element ( name = "actionCode", isOptional =  true , hasTag =  true, tag = 128 , hasDefaultValue =  false  )
    
	private ActionCode actionCode = null;
                
  
        @ASN1Element ( name = "announcementList", isOptional =  true , hasTag =  true, tag = 130 , hasDefaultValue =  false  )
    
	private AnnouncementList announcementList = null;
                
  
        @ASN1Element ( name = "callingPartyNumberString1", isOptional =  true , hasTag =  true, tag = 82 , hasDefaultValue =  false  )
    
	private CallingPartyNumberString1 callingPartyNumberString1 = null;
                
  
        @ASN1Element ( name = "callingPartyNumberString2", isOptional =  true , hasTag =  true, tag = 83 , hasDefaultValue =  false  )
    
	private CallingPartyNumberString2 callingPartyNumberString2 = null;
                
  
        @ASN1Element ( name = "carrierDigits", isOptional =  true , hasTag =  true, tag = 86 , hasDefaultValue =  false  )
    
	private CarrierDigits carrierDigits = null;
                
  
        @ASN1Element ( name = "displayText", isOptional =  true , hasTag =  true, tag = 244 , hasDefaultValue =  false  )
    
	private DisplayText displayText = null;
                
  
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
                
  
        @ASN1Element ( name = "oneTimeFeatureIndicator", isOptional =  true , hasTag =  true, tag = 97 , hasDefaultValue =  false  )
    
	private OneTimeFeatureIndicator oneTimeFeatureIndicator = null;
                
  
        @ASN1Element ( name = "pilotNumber", isOptional =  true , hasTag =  true, tag = 168 , hasDefaultValue =  false  )
    
	private PilotNumber pilotNumber = null;
                
  
        @ASN1Element ( name = "preferredLanguageIndicator", isOptional =  true , hasTag =  true, tag = 147 , hasDefaultValue =  false  )
    
	private PreferredLanguageIndicator preferredLanguageIndicator = null;
                
  
        @ASN1Element ( name = "redirectingNumberDigits", isOptional =  true , hasTag =  true, tag = 100 , hasDefaultValue =  false  )
    
	private RedirectingNumberDigits redirectingNumberDigits = null;
                
  
        @ASN1Element ( name = "resumePIC", isOptional =  true , hasTag =  true, tag = 266 , hasDefaultValue =  false  )
    
	private ResumePIC resumePIC = null;
                
  
        @ASN1Element ( name = "routingDigits", isOptional =  true , hasTag =  true, tag = 150 , hasDefaultValue =  false  )
    
	private RoutingDigits routingDigits = null;
                
  
        @ASN1Element ( name = "terminationList", isOptional =  true , hasTag =  true, tag = 120 , hasDefaultValue =  false  )
    
	private TerminationList terminationList = null;
                
  
        @ASN1Element ( name = "terminationTriggers", isOptional =  true , hasTag =  true, tag = 122 , hasDefaultValue =  false  )
    
	private TerminationTriggers terminationTriggers = null;
                
  
        @ASN1Element ( name = "triggerAddressList", isOptional =  true , hasTag =  true, tag = 276 , hasDefaultValue =  false  )
    
	private TriggerAddressList triggerAddressList = null;
                
  
        
        public AccessDeniedReason getAccessDeniedReason () {
            return this.accessDeniedReason;
        }

        
        public boolean isAccessDeniedReasonPresent () {
            return this.accessDeniedReason != null;
        }
        

        public void setAccessDeniedReason (AccessDeniedReason value) {
            this.accessDeniedReason = value;
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
        
  
        
        public CarrierDigits getCarrierDigits () {
            return this.carrierDigits;
        }

        
        public boolean isCarrierDigitsPresent () {
            return this.carrierDigits != null;
        }
        

        public void setCarrierDigits (CarrierDigits value) {
            this.carrierDigits = value;
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
        
  
        
        public OneTimeFeatureIndicator getOneTimeFeatureIndicator () {
            return this.oneTimeFeatureIndicator;
        }

        
        public boolean isOneTimeFeatureIndicatorPresent () {
            return this.oneTimeFeatureIndicator != null;
        }
        

        public void setOneTimeFeatureIndicator (OneTimeFeatureIndicator value) {
            this.oneTimeFeatureIndicator = value;
        }
        
  
        
        public PilotNumber getPilotNumber () {
            return this.pilotNumber;
        }

        
        public boolean isPilotNumberPresent () {
            return this.pilotNumber != null;
        }
        

        public void setPilotNumber (PilotNumber value) {
            this.pilotNumber = value;
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
        
  
        
        public ResumePIC getResumePIC () {
            return this.resumePIC;
        }

        
        public boolean isResumePICPresent () {
            return this.resumePIC != null;
        }
        

        public void setResumePIC (ResumePIC value) {
            this.resumePIC = value;
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
        
  
                
                
        public void initWithDefaults() {
            
        }

        public IASN1PreparedElementData getPreparedData() {
            return preparedData_TNoAnswerResSequenceType;
        }

       private static IASN1PreparedElementData preparedData_TNoAnswerResSequenceType = CoderFactory.getInstance().newPreparedElementData(TNoAnswerResSequenceType.class);
                
       }

       
                
        @ASN1Element ( name = "TNoAnswerRes", isOptional =  false , hasTag =  true, tag = 18, 
        tagClass =  TagClass.Private  , hasDefaultValue =  false  )
    
        private TNoAnswerResSequenceType  value;        

        
        
        public TNoAnswerRes () {
        }
        
        
        
        public void setValue(TNoAnswerResSequenceType value) {
            this.value = value;
        }
        
        
        
        public TNoAnswerResSequenceType getValue() {
            return this.value;
        }            
        

	    public void initWithDefaults() {
	    }

        private static IASN1PreparedElementData preparedData = CoderFactory.getInstance().newPreparedElementData(TNoAnswerRes.class);
        public IASN1PreparedElementData getPreparedData() {
            return preparedData;
        }

            
    }
            