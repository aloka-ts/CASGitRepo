
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
    @ASN1Sequence ( name = "CollectInformationArg", isSet = false )
    public class CollectInformationArg implements IASN1PreparedElement {
            
        @ASN1Element ( name = "callingPartyID", isOptional =  true , hasTag =  false  , hasDefaultValue =  false  )
    
	private CallingPartyID callingPartyID = null;
                
  
        @ASN1Element ( name = "collectedDigits", isOptional =  true , hasTag =  false  , hasDefaultValue =  false  )
    
	private CollectedDigits collectedDigits = null;
                
  
        @ASN1Element ( name = "dPConverter", isOptional =  true , hasTag =  false  , hasDefaultValue =  false  )
    
	private DPConverter dPConverter = null;
                
  
        @ASN1Element ( name = "primaryBillingIndicator", isOptional =  true , hasTag =  false  , hasDefaultValue =  false  )
    
	private PrimaryBillingIndicator primaryBillingIndicator = null;
                
  
        @ASN1Element ( name = "alternateBillingIndicator", isOptional =  true , hasTag =  false  , hasDefaultValue =  false  )
    
	private AlternateBillingIndicator alternateBillingIndicator = null;
                
  
        @ASN1Element ( name = "secondAlternateBillingIndicator", isOptional =  true , hasTag =  false  , hasDefaultValue =  false  )
    
	private SecondAlternateBillingIndicator secondAlternateBillingIndicator = null;
                
  
        @ASN1Element ( name = "overflowBillingIndicator", isOptional =  true , hasTag =  false  , hasDefaultValue =  false  )
    
	private OverflowBillingIndicator overflowBillingIndicator = null;
                
  
        @ASN1Element ( name = "aMAAlternateBillingNumber", isOptional =  true , hasTag =  false  , hasDefaultValue =  false  )
    
	private AMAAlternateBillingNumber aMAAlternateBillingNumber = null;
                
  
@ASN1SequenceOf( name = "aMALineNumber", isSetOf = false ) 

    @ASN1ValueRangeConstraint ( 
		
		min = 1L, 
		
		max = 2L 
		
	   )
	   
        @ASN1Element ( name = "aMALineNumber", isOptional =  true , hasTag =  false  , hasDefaultValue =  false  )
    
	private java.util.Collection<AMALineNumber>  aMALineNumber = null;
                
  
        @ASN1Element ( name = "aMAslpID", isOptional =  true , hasTag =  false  , hasDefaultValue =  false  )
    
	private AMAslpID aMAslpID = null;
                
  
@ASN1SequenceOf( name = "aMADigitsDialedWC", isSetOf = false ) 

    @ASN1ValueRangeConstraint ( 
		
		min = 1L, 
		
		max = 5L 
		
	   )
	   
        @ASN1Element ( name = "aMADigitsDialedWC", isOptional =  true , hasTag =  false  , hasDefaultValue =  false  )
    
	private java.util.Collection<AMADigitsDialedWC>  aMADigitsDialedWC = null;
                
  
        @ASN1Element ( name = "amp1", isOptional =  true , hasTag =  false  , hasDefaultValue =  false  )
    
	private Amp1 amp1 = null;
                
  
        @ASN1Element ( name = "amp2", isOptional =  true , hasTag =  false  , hasDefaultValue =  false  )
    
	private Amp2 amp2 = null;
                
  
        @ASN1Element ( name = "serviceProviderID", isOptional =  true , hasTag =  false  , hasDefaultValue =  false  )
    
	private ServiceProviderID serviceProviderID = null;
                
  
        @ASN1Element ( name = "serviceContext", isOptional =  true , hasTag =  false  , hasDefaultValue =  false  )
    
	private ServiceContext serviceContext = null;
                
  
        @ASN1Element ( name = "aMABillingFeature", isOptional =  true , hasTag =  false  , hasDefaultValue =  false  )
    
	private AMABillingFeature aMABillingFeature = null;
                
  
        @ASN1Element ( name = "aMASequenceNumber", isOptional =  true , hasTag =  false  , hasDefaultValue =  false  )
    
	private AMASequenceNumber aMASequenceNumber = null;
                
  
        @ASN1Element ( name = "extensionParameter", isOptional =  true , hasTag =  false  , hasDefaultValue =  false  )
    
	private ExtensionParameter extensionParameter = null;
                
  
        @ASN1Element ( name = "alternateDialingPlanInd", isOptional =  true , hasTag =  false  , hasDefaultValue =  false  )
    
	private AlternateDialingPlanInd alternateDialingPlanInd = null;
                
  
        @ASN1Element ( name = "aMAServiceProviderID", isOptional =  true , hasTag =  false  , hasDefaultValue =  false  )
    
	private AMAServiceProviderID aMAServiceProviderID = null;
                
  
        
        public CallingPartyID getCallingPartyID () {
            return this.callingPartyID;
        }

        
        public boolean isCallingPartyIDPresent () {
            return this.callingPartyID != null;
        }
        

        public void setCallingPartyID (CallingPartyID value) {
            this.callingPartyID = value;
        }
        
  
        
        public CollectedDigits getCollectedDigits () {
            return this.collectedDigits;
        }

        
        public boolean isCollectedDigitsPresent () {
            return this.collectedDigits != null;
        }
        

        public void setCollectedDigits (CollectedDigits value) {
            this.collectedDigits = value;
        }
        
  
        
        public DPConverter getDPConverter () {
            return this.dPConverter;
        }

        
        public boolean isDPConverterPresent () {
            return this.dPConverter != null;
        }
        

        public void setDPConverter (DPConverter value) {
            this.dPConverter = value;
        }
        
  
        
        public PrimaryBillingIndicator getPrimaryBillingIndicator () {
            return this.primaryBillingIndicator;
        }

        
        public boolean isPrimaryBillingIndicatorPresent () {
            return this.primaryBillingIndicator != null;
        }
        

        public void setPrimaryBillingIndicator (PrimaryBillingIndicator value) {
            this.primaryBillingIndicator = value;
        }
        
  
        
        public AlternateBillingIndicator getAlternateBillingIndicator () {
            return this.alternateBillingIndicator;
        }

        
        public boolean isAlternateBillingIndicatorPresent () {
            return this.alternateBillingIndicator != null;
        }
        

        public void setAlternateBillingIndicator (AlternateBillingIndicator value) {
            this.alternateBillingIndicator = value;
        }
        
  
        
        public SecondAlternateBillingIndicator getSecondAlternateBillingIndicator () {
            return this.secondAlternateBillingIndicator;
        }

        
        public boolean isSecondAlternateBillingIndicatorPresent () {
            return this.secondAlternateBillingIndicator != null;
        }
        

        public void setSecondAlternateBillingIndicator (SecondAlternateBillingIndicator value) {
            this.secondAlternateBillingIndicator = value;
        }
        
  
        
        public OverflowBillingIndicator getOverflowBillingIndicator () {
            return this.overflowBillingIndicator;
        }

        
        public boolean isOverflowBillingIndicatorPresent () {
            return this.overflowBillingIndicator != null;
        }
        

        public void setOverflowBillingIndicator (OverflowBillingIndicator value) {
            this.overflowBillingIndicator = value;
        }
        
  
        
        public AMAAlternateBillingNumber getAMAAlternateBillingNumber () {
            return this.aMAAlternateBillingNumber;
        }

        
        public boolean isAMAAlternateBillingNumberPresent () {
            return this.aMAAlternateBillingNumber != null;
        }
        

        public void setAMAAlternateBillingNumber (AMAAlternateBillingNumber value) {
            this.aMAAlternateBillingNumber = value;
        }
        
  
        
        public java.util.Collection<AMALineNumber>  getAMALineNumber () {
            return this.aMALineNumber;
        }

        
        public boolean isAMALineNumberPresent () {
            return this.aMALineNumber != null;
        }
        

        public void setAMALineNumber (java.util.Collection<AMALineNumber>  value) {
            this.aMALineNumber = value;
        }
        
  
        
        public AMAslpID getAMAslpID () {
            return this.aMAslpID;
        }

        
        public boolean isAMAslpIDPresent () {
            return this.aMAslpID != null;
        }
        

        public void setAMAslpID (AMAslpID value) {
            this.aMAslpID = value;
        }
        
  
        
        public java.util.Collection<AMADigitsDialedWC>  getAMADigitsDialedWC () {
            return this.aMADigitsDialedWC;
        }

        
        public boolean isAMADigitsDialedWCPresent () {
            return this.aMADigitsDialedWC != null;
        }
        

        public void setAMADigitsDialedWC (java.util.Collection<AMADigitsDialedWC>  value) {
            this.aMADigitsDialedWC = value;
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
        
  
        
        public ServiceProviderID getServiceProviderID () {
            return this.serviceProviderID;
        }

        
        public boolean isServiceProviderIDPresent () {
            return this.serviceProviderID != null;
        }
        

        public void setServiceProviderID (ServiceProviderID value) {
            this.serviceProviderID = value;
        }
        
  
        
        public ServiceContext getServiceContext () {
            return this.serviceContext;
        }

        
        public boolean isServiceContextPresent () {
            return this.serviceContext != null;
        }
        

        public void setServiceContext (ServiceContext value) {
            this.serviceContext = value;
        }
        
  
        
        public AMABillingFeature getAMABillingFeature () {
            return this.aMABillingFeature;
        }

        
        public boolean isAMABillingFeaturePresent () {
            return this.aMABillingFeature != null;
        }
        

        public void setAMABillingFeature (AMABillingFeature value) {
            this.aMABillingFeature = value;
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
        
  
        
        public AlternateDialingPlanInd getAlternateDialingPlanInd () {
            return this.alternateDialingPlanInd;
        }

        
        public boolean isAlternateDialingPlanIndPresent () {
            return this.alternateDialingPlanInd != null;
        }
        

        public void setAlternateDialingPlanInd (AlternateDialingPlanInd value) {
            this.alternateDialingPlanInd = value;
        }
        
  
        
        public AMAServiceProviderID getAMAServiceProviderID () {
            return this.aMAServiceProviderID;
        }

        
        public boolean isAMAServiceProviderIDPresent () {
            return this.aMAServiceProviderID != null;
        }
        

        public void setAMAServiceProviderID (AMAServiceProviderID value) {
            this.aMAServiceProviderID = value;
        }
        
  
                    
        
        public void initWithDefaults() {
            
        }

        private static IASN1PreparedElementData preparedData = CoderFactory.getInstance().newPreparedElementData(CollectInformationArg.class);
        public IASN1PreparedElementData getPreparedData() {
            return preparedData;
        }

            
    }
            