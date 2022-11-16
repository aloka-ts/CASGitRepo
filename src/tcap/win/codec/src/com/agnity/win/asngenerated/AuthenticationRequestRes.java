
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
    @ASN1BoxedType ( name = "AuthenticationRequestRes" )
    public class AuthenticationRequestRes implements IASN1PreparedElement {
                
        

       @ASN1PreparedElement
       @ASN1Sequence ( name = "AuthenticationRequestRes" , isSet = true )
       public static class AuthenticationRequestResSequenceType implements IASN1PreparedElement {
                
        @ASN1Element ( name = "analogRedirectRecord", isOptional =  true , hasTag =  true, tag = 225 , hasDefaultValue =  false  )
    
	private AnalogRedirectRecord analogRedirectRecord = null;
                
  
        @ASN1Element ( name = "authenticationAlgorithmVersion", isOptional =  true , hasTag =  true, tag = 77 , hasDefaultValue =  false  )
    
	private AuthenticationAlgorithmVersion authenticationAlgorithmVersion = null;
                
  
        @ASN1Element ( name = "authenticationResponseUniqueChallenge", isOptional =  true , hasTag =  true, tag = 37 , hasDefaultValue =  false  )
    
	private AuthenticationResponseUniqueChallenge authenticationResponseUniqueChallenge = null;
                
  
        @ASN1Element ( name = "callHistoryCount", isOptional =  true , hasTag =  true, tag = 38 , hasDefaultValue =  false  )
    
	private CallHistoryCount callHistoryCount = null;
                
  
        @ASN1Element ( name = "carrierDigits", isOptional =  true , hasTag =  true, tag = 86 , hasDefaultValue =  false  )
    
	private CarrierDigits carrierDigits = null;
                
  
        @ASN1Element ( name = "cdmaPrivateLongCodeMask", isOptional =  true , hasTag =  true, tag = 67 , hasDefaultValue =  false  )
    
	private CDMAPrivateLongCodeMask cdmaPrivateLongCodeMask = null;
                
  
        @ASN1Element ( name = "cdmaRedirectRecord", isOptional =  true , hasTag =  true, tag = 229 , hasDefaultValue =  false  )
    
	private CDMARedirectRecord cdmaRedirectRecord = null;
                
  
        @ASN1Element ( name = "dataKey", isOptional =  true , hasTag =  true, tag = 215 , hasDefaultValue =  false  )
    
	private DataKey dataKey = null;
                
  
        @ASN1Element ( name = "denyAccess", isOptional =  true , hasTag =  true, tag = 50 , hasDefaultValue =  false  )
    
	private DenyAccess denyAccess = null;
                
  
        @ASN1Element ( name = "mobileIdentificationNumber", isOptional =  true , hasTag =  true, tag = 8 , hasDefaultValue =  false  )
    
	private MobileIdentificationNumber mobileIdentificationNumber = null;
                
  
        @ASN1Element ( name = "roamingIndication", isOptional =  true , hasTag =  true, tag = 239 , hasDefaultValue =  false  )
    
	private RoamingIndication roamingIndication = null;
                
  
        @ASN1Element ( name = "serviceRedirectionInfo", isOptional =  true , hasTag =  true, tag = 238 , hasDefaultValue =  false  )
    
	private ServiceRedirectionInfo serviceRedirectionInfo = null;
                
  
        @ASN1Element ( name = "destinationDigits", isOptional =  true , hasTag =  true, tag = 87 , hasDefaultValue =  false  )
    
	private DestinationDigits destinationDigits = null;
                
  
        @ASN1Element ( name = "randomVariableSSD", isOptional =  true , hasTag =  true, tag = 42 , hasDefaultValue =  false  )
    
	private RandomVariableSSD randomVariableSSD = null;
                
  
        @ASN1Element ( name = "randomVariableUniqueChallenge", isOptional =  true , hasTag =  true, tag = 43 , hasDefaultValue =  false  )
    
	private RandomVariableUniqueChallenge randomVariableUniqueChallenge = null;
                
  
        @ASN1Element ( name = "routingDigits", isOptional =  true , hasTag =  true, tag = 150 , hasDefaultValue =  false  )
    
	private RoutingDigits routingDigits = null;
                
  
        @ASN1Element ( name = "sharedSecretData", isOptional =  true , hasTag =  true, tag = 46 , hasDefaultValue =  false  )
    
	private SharedSecretData sharedSecretData = null;
                
  
        @ASN1Element ( name = "signalingMessageEncryptionKey", isOptional =  true , hasTag =  true, tag = 45 , hasDefaultValue =  false  )
    
	private SignalingMessageEncryptionKey signalingMessageEncryptionKey = null;
                
  
        @ASN1Element ( name = "ssdnotShared", isOptional =  true , hasTag =  true, tag = 52 , hasDefaultValue =  false  )
    
	private SSDNotShared ssdnotShared = null;
                
  
        @ASN1Element ( name = "updateCount", isOptional =  true , hasTag =  true, tag = 51 , hasDefaultValue =  false  )
    
	private UpdateCount updateCount = null;
                
  
        @ASN1Element ( name = "voicePrivacyMask", isOptional =  true , hasTag =  true, tag = 48 , hasDefaultValue =  false  )
    
	private VoicePrivacyMask voicePrivacyMask = null;
                
  
        
        public AnalogRedirectRecord getAnalogRedirectRecord () {
            return this.analogRedirectRecord;
        }

        
        public boolean isAnalogRedirectRecordPresent () {
            return this.analogRedirectRecord != null;
        }
        

        public void setAnalogRedirectRecord (AnalogRedirectRecord value) {
            this.analogRedirectRecord = value;
        }
        
  
        
        public AuthenticationAlgorithmVersion getAuthenticationAlgorithmVersion () {
            return this.authenticationAlgorithmVersion;
        }

        
        public boolean isAuthenticationAlgorithmVersionPresent () {
            return this.authenticationAlgorithmVersion != null;
        }
        

        public void setAuthenticationAlgorithmVersion (AuthenticationAlgorithmVersion value) {
            this.authenticationAlgorithmVersion = value;
        }
        
  
        
        public AuthenticationResponseUniqueChallenge getAuthenticationResponseUniqueChallenge () {
            return this.authenticationResponseUniqueChallenge;
        }

        
        public boolean isAuthenticationResponseUniqueChallengePresent () {
            return this.authenticationResponseUniqueChallenge != null;
        }
        

        public void setAuthenticationResponseUniqueChallenge (AuthenticationResponseUniqueChallenge value) {
            this.authenticationResponseUniqueChallenge = value;
        }
        
  
        
        public CallHistoryCount getCallHistoryCount () {
            return this.callHistoryCount;
        }

        
        public boolean isCallHistoryCountPresent () {
            return this.callHistoryCount != null;
        }
        

        public void setCallHistoryCount (CallHistoryCount value) {
            this.callHistoryCount = value;
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
        
  
        
        public CDMAPrivateLongCodeMask getCdmaPrivateLongCodeMask () {
            return this.cdmaPrivateLongCodeMask;
        }

        
        public boolean isCdmaPrivateLongCodeMaskPresent () {
            return this.cdmaPrivateLongCodeMask != null;
        }
        

        public void setCdmaPrivateLongCodeMask (CDMAPrivateLongCodeMask value) {
            this.cdmaPrivateLongCodeMask = value;
        }
        
  
        
        public CDMARedirectRecord getCdmaRedirectRecord () {
            return this.cdmaRedirectRecord;
        }

        
        public boolean isCdmaRedirectRecordPresent () {
            return this.cdmaRedirectRecord != null;
        }
        

        public void setCdmaRedirectRecord (CDMARedirectRecord value) {
            this.cdmaRedirectRecord = value;
        }
        
  
        
        public DataKey getDataKey () {
            return this.dataKey;
        }

        
        public boolean isDataKeyPresent () {
            return this.dataKey != null;
        }
        

        public void setDataKey (DataKey value) {
            this.dataKey = value;
        }
        
  
        
        public DenyAccess getDenyAccess () {
            return this.denyAccess;
        }

        
        public boolean isDenyAccessPresent () {
            return this.denyAccess != null;
        }
        

        public void setDenyAccess (DenyAccess value) {
            this.denyAccess = value;
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
        
  
        
        public RoamingIndication getRoamingIndication () {
            return this.roamingIndication;
        }

        
        public boolean isRoamingIndicationPresent () {
            return this.roamingIndication != null;
        }
        

        public void setRoamingIndication (RoamingIndication value) {
            this.roamingIndication = value;
        }
        
  
        
        public ServiceRedirectionInfo getServiceRedirectionInfo () {
            return this.serviceRedirectionInfo;
        }

        
        public boolean isServiceRedirectionInfoPresent () {
            return this.serviceRedirectionInfo != null;
        }
        

        public void setServiceRedirectionInfo (ServiceRedirectionInfo value) {
            this.serviceRedirectionInfo = value;
        }
        
  
        
        public DestinationDigits getDestinationDigits () {
            return this.destinationDigits;
        }

        
        public boolean isDestinationDigitsPresent () {
            return this.destinationDigits != null;
        }
        

        public void setDestinationDigits (DestinationDigits value) {
            this.destinationDigits = value;
        }
        
  
        
        public RandomVariableSSD getRandomVariableSSD () {
            return this.randomVariableSSD;
        }

        
        public boolean isRandomVariableSSDPresent () {
            return this.randomVariableSSD != null;
        }
        

        public void setRandomVariableSSD (RandomVariableSSD value) {
            this.randomVariableSSD = value;
        }
        
  
        
        public RandomVariableUniqueChallenge getRandomVariableUniqueChallenge () {
            return this.randomVariableUniqueChallenge;
        }

        
        public boolean isRandomVariableUniqueChallengePresent () {
            return this.randomVariableUniqueChallenge != null;
        }
        

        public void setRandomVariableUniqueChallenge (RandomVariableUniqueChallenge value) {
            this.randomVariableUniqueChallenge = value;
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
        
  
        
        public SharedSecretData getSharedSecretData () {
            return this.sharedSecretData;
        }

        
        public boolean isSharedSecretDataPresent () {
            return this.sharedSecretData != null;
        }
        

        public void setSharedSecretData (SharedSecretData value) {
            this.sharedSecretData = value;
        }
        
  
        
        public SignalingMessageEncryptionKey getSignalingMessageEncryptionKey () {
            return this.signalingMessageEncryptionKey;
        }

        
        public boolean isSignalingMessageEncryptionKeyPresent () {
            return this.signalingMessageEncryptionKey != null;
        }
        

        public void setSignalingMessageEncryptionKey (SignalingMessageEncryptionKey value) {
            this.signalingMessageEncryptionKey = value;
        }
        
  
        
        public SSDNotShared getSsdnotShared () {
            return this.ssdnotShared;
        }

        
        public boolean isSsdnotSharedPresent () {
            return this.ssdnotShared != null;
        }
        

        public void setSsdnotShared (SSDNotShared value) {
            this.ssdnotShared = value;
        }
        
  
        
        public UpdateCount getUpdateCount () {
            return this.updateCount;
        }

        
        public boolean isUpdateCountPresent () {
            return this.updateCount != null;
        }
        

        public void setUpdateCount (UpdateCount value) {
            this.updateCount = value;
        }
        
  
        
        public VoicePrivacyMask getVoicePrivacyMask () {
            return this.voicePrivacyMask;
        }

        
        public boolean isVoicePrivacyMaskPresent () {
            return this.voicePrivacyMask != null;
        }
        

        public void setVoicePrivacyMask (VoicePrivacyMask value) {
            this.voicePrivacyMask = value;
        }
        
  
                
                
        public void initWithDefaults() {
            
        }

        public IASN1PreparedElementData getPreparedData() {
            return preparedData_AuthenticationRequestResSequenceType;
        }

       private static IASN1PreparedElementData preparedData_AuthenticationRequestResSequenceType = CoderFactory.getInstance().newPreparedElementData(AuthenticationRequestResSequenceType.class);
                
       }

       
                
        @ASN1Element ( name = "AuthenticationRequestRes", isOptional =  false , hasTag =  true, tag = 18, 
        tagClass =  TagClass.Private  , hasDefaultValue =  false  )
    
        private AuthenticationRequestResSequenceType  value;        

        
        
        public AuthenticationRequestRes () {
        }
        
        
        
        public void setValue(AuthenticationRequestResSequenceType value) {
            this.value = value;
        }
        
        
        
        public AuthenticationRequestResSequenceType getValue() {
            return this.value;
        }            
        

	    public void initWithDefaults() {
	    }

        private static IASN1PreparedElementData preparedData = CoderFactory.getInstance().newPreparedElementData(AuthenticationRequestRes.class);
        public IASN1PreparedElementData getPreparedData() {
            return preparedData;
        }

            
    }
            