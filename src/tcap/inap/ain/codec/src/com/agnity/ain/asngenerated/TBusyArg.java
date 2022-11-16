
package com.agnity.ain.asngenerated;
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
    @ASN1Sequence ( name = "TBusyArg", isSet = false )
    public class TBusyArg implements IASN1PreparedElement {
            
        @ASN1Element ( name = "userID", isOptional =  false , hasTag =  false  , hasDefaultValue =  false  )
    
	private UserID userID = null;
                
  
        @ASN1Element ( name = "bearerCapability", isOptional =  false , hasTag =  false  , hasDefaultValue =  false  )
    
	private BearerCapability bearerCapability = null;
                
  
        @ASN1Element ( name = "calledPartyID", isOptional =  true , hasTag =  false  , hasDefaultValue =  false  )
    
	private CalledPartyID calledPartyID = null;
                
  
        @ASN1Element ( name = "lata", isOptional =  true , hasTag =  false  , hasDefaultValue =  false  )
    
	private Lata lata = null;
                
  
        @ASN1Element ( name = "triggerCriteriaType", isOptional =  true , hasTag =  false  , hasDefaultValue =  false  )
    
	private TriggerCriteriaType triggerCriteriaType = null;
                
  
        @ASN1Element ( name = "chargeNumber", isOptional =  true , hasTag =  false  , hasDefaultValue =  false  )
    
	private ChargeNumber chargeNumber = null;
                
  
        @ASN1Element ( name = "callingPartyID", isOptional =  true , hasTag =  false  , hasDefaultValue =  false  )
    
	private CallingPartyID callingPartyID = null;
                
  
        @ASN1Element ( name = "chargePartyStationType", isOptional =  true , hasTag =  false  , hasDefaultValue =  false  )
    
	private ChargePartyStationType chargePartyStationType = null;
                
  
        @ASN1Element ( name = "originalCalledPartyID", isOptional =  true , hasTag =  false  , hasDefaultValue =  false  )
    
	private OriginalCalledPartyID originalCalledPartyID = null;
                
  
        @ASN1Element ( name = "redirectingPartyID", isOptional =  true , hasTag =  false  , hasDefaultValue =  false  )
    
	private RedirectingPartyID redirectingPartyID = null;
                
  
        @ASN1Element ( name = "redirectionInformation", isOptional =  true , hasTag =  false  , hasDefaultValue =  false  )
    
	private RedirectionInformation redirectionInformation = null;
                
  
        @ASN1Element ( name = "busyCause", isOptional =  true , hasTag =  false  , hasDefaultValue =  false  )
    
	private BusyCause busyCause = null;
                
  
        @ASN1Element ( name = "busyType", isOptional =  true , hasTag =  false  , hasDefaultValue =  false  )
    
	private BusyType busyType = null;
                
  
        @ASN1Element ( name = "calledPartyStationType", isOptional =  true , hasTag =  false  , hasDefaultValue =  false  )
    
	private CalledPartyStationType calledPartyStationType = null;
                
  
        @ASN1Element ( name = "sap", isOptional =  true , hasTag =  false  , hasDefaultValue =  false  )
    
	private Sap sap = null;
                
  
        @ASN1Element ( name = "genericName", isOptional =  true , hasTag =  false  , hasDefaultValue =  false  )
    
	private GenericName genericName = null;
                
  
        @ASN1Element ( name = "notificationIndicator", isOptional =  true , hasTag =  false  , hasDefaultValue =  false  )
    
	private NotificationIndicator notificationIndicator = null;
                
  
        @ASN1Element ( name = "aCGEncountered", isOptional =  true , hasTag =  false  , hasDefaultValue =  false  )
    
	private ACGEncountered aCGEncountered = null;
                
  
        @ASN1Element ( name = "amp1", isOptional =  true , hasTag =  false  , hasDefaultValue =  false  )
    
	private Amp1 amp1 = null;
                
  
        @ASN1Element ( name = "amp2", isOptional =  true , hasTag =  false  , hasDefaultValue =  false  )
    
	private Amp2 amp2 = null;
                
  
        @ASN1Element ( name = "sTRConnection", isOptional =  true , hasTag =  false  , hasDefaultValue =  false  )
    
	private STRConnection sTRConnection = null;
                
  
        @ASN1Element ( name = "aMASequenceNumber", isOptional =  true , hasTag =  false  , hasDefaultValue =  false  )
    
	private AMASequenceNumber aMASequenceNumber = null;
                
  
        @ASN1Element ( name = "extensionParameter", isOptional =  true , hasTag =  false  , hasDefaultValue =  false  )
    
	private ExtensionParameter extensionParameter = null;
                
  
        @ASN1Element ( name = "cTRConnection", isOptional =  true , hasTag =  false  , hasDefaultValue =  false  )
    
	private CTRConnection cTRConnection = null;
                
  
        @ASN1Element ( name = "triggerInformation", isOptional =  true , hasTag =  false  , hasDefaultValue =  false  )
    
	private TriggerInformation triggerInformation = null;
                
  
        
        public UserID getUserID () {
            return this.userID;
        }

        

        public void setUserID (UserID value) {
            this.userID = value;
        }
        
  
        
        public BearerCapability getBearerCapability () {
            return this.bearerCapability;
        }

        

        public void setBearerCapability (BearerCapability value) {
            this.bearerCapability = value;
        }
        
  
        
        public CalledPartyID getCalledPartyID () {
            return this.calledPartyID;
        }

        
        public boolean isCalledPartyIDPresent () {
            return this.calledPartyID != null;
        }
        

        public void setCalledPartyID (CalledPartyID value) {
            this.calledPartyID = value;
        }
        
  
        
        public Lata getLata () {
            return this.lata;
        }

        
        public boolean isLataPresent () {
            return this.lata != null;
        }
        

        public void setLata (Lata value) {
            this.lata = value;
        }
        
  
        
        public TriggerCriteriaType getTriggerCriteriaType () {
            return this.triggerCriteriaType;
        }

        
        public boolean isTriggerCriteriaTypePresent () {
            return this.triggerCriteriaType != null;
        }
        

        public void setTriggerCriteriaType (TriggerCriteriaType value) {
            this.triggerCriteriaType = value;
        }
        
  
        
        public ChargeNumber getChargeNumber () {
            return this.chargeNumber;
        }

        
        public boolean isChargeNumberPresent () {
            return this.chargeNumber != null;
        }
        

        public void setChargeNumber (ChargeNumber value) {
            this.chargeNumber = value;
        }
        
  
        
        public CallingPartyID getCallingPartyID () {
            return this.callingPartyID;
        }

        
        public boolean isCallingPartyIDPresent () {
            return this.callingPartyID != null;
        }
        

        public void setCallingPartyID (CallingPartyID value) {
            this.callingPartyID = value;
        }
        
  
        
        public ChargePartyStationType getChargePartyStationType () {
            return this.chargePartyStationType;
        }

        
        public boolean isChargePartyStationTypePresent () {
            return this.chargePartyStationType != null;
        }
        

        public void setChargePartyStationType (ChargePartyStationType value) {
            this.chargePartyStationType = value;
        }
        
  
        
        public OriginalCalledPartyID getOriginalCalledPartyID () {
            return this.originalCalledPartyID;
        }

        
        public boolean isOriginalCalledPartyIDPresent () {
            return this.originalCalledPartyID != null;
        }
        

        public void setOriginalCalledPartyID (OriginalCalledPartyID value) {
            this.originalCalledPartyID = value;
        }
        
  
        
        public RedirectingPartyID getRedirectingPartyID () {
            return this.redirectingPartyID;
        }

        
        public boolean isRedirectingPartyIDPresent () {
            return this.redirectingPartyID != null;
        }
        

        public void setRedirectingPartyID (RedirectingPartyID value) {
            this.redirectingPartyID = value;
        }
        
  
        
        public RedirectionInformation getRedirectionInformation () {
            return this.redirectionInformation;
        }

        
        public boolean isRedirectionInformationPresent () {
            return this.redirectionInformation != null;
        }
        

        public void setRedirectionInformation (RedirectionInformation value) {
            this.redirectionInformation = value;
        }
        
  
        
        public BusyCause getBusyCause () {
            return this.busyCause;
        }

        
        public boolean isBusyCausePresent () {
            return this.busyCause != null;
        }
        

        public void setBusyCause (BusyCause value) {
            this.busyCause = value;
        }
        
  
        
        public BusyType getBusyType () {
            return this.busyType;
        }

        
        public boolean isBusyTypePresent () {
            return this.busyType != null;
        }
        

        public void setBusyType (BusyType value) {
            this.busyType = value;
        }
        
  
        
        public CalledPartyStationType getCalledPartyStationType () {
            return this.calledPartyStationType;
        }

        
        public boolean isCalledPartyStationTypePresent () {
            return this.calledPartyStationType != null;
        }
        

        public void setCalledPartyStationType (CalledPartyStationType value) {
            this.calledPartyStationType = value;
        }
        
  
        
        public Sap getSap () {
            return this.sap;
        }

        
        public boolean isSapPresent () {
            return this.sap != null;
        }
        

        public void setSap (Sap value) {
            this.sap = value;
        }
        
  
        
        public GenericName getGenericName () {
            return this.genericName;
        }

        
        public boolean isGenericNamePresent () {
            return this.genericName != null;
        }
        

        public void setGenericName (GenericName value) {
            this.genericName = value;
        }
        
  
        
        public NotificationIndicator getNotificationIndicator () {
            return this.notificationIndicator;
        }

        
        public boolean isNotificationIndicatorPresent () {
            return this.notificationIndicator != null;
        }
        

        public void setNotificationIndicator (NotificationIndicator value) {
            this.notificationIndicator = value;
        }
        
  
        
        public ACGEncountered getACGEncountered () {
            return this.aCGEncountered;
        }

        
        public boolean isACGEncounteredPresent () {
            return this.aCGEncountered != null;
        }
        

        public void setACGEncountered (ACGEncountered value) {
            this.aCGEncountered = value;
        }
        
  
        
        public Amp1 getAmp1 () {
            return this.amp1;
        }

        
        public boolean isAmp1Present () {
            return this.amp1 != null;
        }
        

        public void setAmp1 (Amp1 value) {
            this.amp1 = value;
        }
        
  
        
        public Amp2 getAmp2 () {
            return this.amp2;
        }

        
        public boolean isAmp2Present () {
            return this.amp2 != null;
        }
        

        public void setAmp2 (Amp2 value) {
            this.amp2 = value;
        }
        
  
        
        public STRConnection getSTRConnection () {
            return this.sTRConnection;
        }

        
        public boolean isSTRConnectionPresent () {
            return this.sTRConnection != null;
        }
        

        public void setSTRConnection (STRConnection value) {
            this.sTRConnection = value;
        }
        
  
        
        public AMASequenceNumber getAMASequenceNumber () {
            return this.aMASequenceNumber;
        }

        
        public boolean isAMASequenceNumberPresent () {
            return this.aMASequenceNumber != null;
        }
        

        public void setAMASequenceNumber (AMASequenceNumber value) {
            this.aMASequenceNumber = value;
        }
        
  
        
        public ExtensionParameter getExtensionParameter () {
            return this.extensionParameter;
        }

        
        public boolean isExtensionParameterPresent () {
            return this.extensionParameter != null;
        }
        

        public void setExtensionParameter (ExtensionParameter value) {
            this.extensionParameter = value;
        }
        
  
        
        public CTRConnection getCTRConnection () {
            return this.cTRConnection;
        }

        
        public boolean isCTRConnectionPresent () {
            return this.cTRConnection != null;
        }
        

        public void setCTRConnection (CTRConnection value) {
            this.cTRConnection = value;
        }
        
  
        
        public TriggerInformation getTriggerInformation () {
            return this.triggerInformation;
        }

        
        public boolean isTriggerInformationPresent () {
            return this.triggerInformation != null;
        }
        

        public void setTriggerInformation (TriggerInformation value) {
            this.triggerInformation = value;
        }
        
  
                    
        
        public void initWithDefaults() {
            
        }

        private static IASN1PreparedElementData preparedData = CoderFactory.getInstance().newPreparedElementData(TBusyArg.class);
        public IASN1PreparedElementData getPreparedData() {
            return preparedData;
        }

            
    }
            