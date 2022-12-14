
package asnGenerated.v3;
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
    @ASN1Choice ( name = "GPRSEventSpecificInformation" )
    public class GPRSEventSpecificInformation implements IASN1PreparedElement {
            

       @ASN1PreparedElement
       @ASN1Sequence ( name = "attachChangeOfPositionSpecificInformation" , isSet = false )
       public static class AttachChangeOfPositionSpecificInformationSequenceType implements IASN1PreparedElement {
                
        @ASN1Element ( name = "locationInformationGPRS", isOptional =  true , hasTag =  true, tag = 0 , hasDefaultValue =  false  )
    
	private LocationInformationGPRS locationInformationGPRS = null;
                
  
        
        public LocationInformationGPRS getLocationInformationGPRS () {
            return this.locationInformationGPRS;
        }

        
        public boolean isLocationInformationGPRSPresent () {
            return this.locationInformationGPRS != null;
        }
        

        public void setLocationInformationGPRS (LocationInformationGPRS value) {
            this.locationInformationGPRS = value;
        }
        
  
                
                
        public void initWithDefaults() {
            
        }

        public IASN1PreparedElementData getPreparedData() {
            return preparedData_AttachChangeOfPositionSpecificInformationSequenceType;
        }

       private static IASN1PreparedElementData preparedData_AttachChangeOfPositionSpecificInformationSequenceType = CoderFactory.getInstance().newPreparedElementData(AttachChangeOfPositionSpecificInformationSequenceType.class);
                
       }

       
                
        @ASN1Element ( name = "attachChangeOfPositionSpecificInformation", isOptional =  false , hasTag =  true, tag = 0 , hasDefaultValue =  false  )
    
	private AttachChangeOfPositionSpecificInformationSequenceType attachChangeOfPositionSpecificInformation = null;
                
  

       @ASN1PreparedElement
       @ASN1Sequence ( name = "pdp-ContextchangeOfPositionSpecificInformation" , isSet = false )
       public static class Pdp_ContextchangeOfPositionSpecificInformationSequenceType implements IASN1PreparedElement {
                
        @ASN1Element ( name = "accessPointName", isOptional =  true , hasTag =  true, tag = 0 , hasDefaultValue =  false  )
    
	private AccessPointName accessPointName = null;
                
  
        @ASN1Element ( name = "chargingID", isOptional =  true , hasTag =  true, tag = 1 , hasDefaultValue =  false  )
    
	private GPRSChargingID chargingID = null;
                
  
        @ASN1Element ( name = "locationInformationGPRS", isOptional =  true , hasTag =  true, tag = 2 , hasDefaultValue =  false  )
    
	private LocationInformationGPRS locationInformationGPRS = null;
                
  
        @ASN1Element ( name = "pDPType", isOptional =  true , hasTag =  true, tag = 3 , hasDefaultValue =  false  )
    
	private PDPType pDPType = null;
                
  
        @ASN1Element ( name = "qualityOfService", isOptional =  true , hasTag =  true, tag = 4 , hasDefaultValue =  false  )
    
	private QualityOfService qualityOfService = null;
                
  
        @ASN1Element ( name = "timeAndTimeZone", isOptional =  true , hasTag =  true, tag = 5 , hasDefaultValue =  false  )
    
	private TimeAndTimezone timeAndTimeZone = null;
                
  
        @ASN1Element ( name = "gGSNAddress", isOptional =  true , hasTag =  true, tag = 6 , hasDefaultValue =  false  )
    
	private GSN_Address gGSNAddress = null;
                
  
        
        public AccessPointName getAccessPointName () {
            return this.accessPointName;
        }

        
        public boolean isAccessPointNamePresent () {
            return this.accessPointName != null;
        }
        

        public void setAccessPointName (AccessPointName value) {
            this.accessPointName = value;
        }
        
  
        
        public GPRSChargingID getChargingID () {
            return this.chargingID;
        }

        
        public boolean isChargingIDPresent () {
            return this.chargingID != null;
        }
        

        public void setChargingID (GPRSChargingID value) {
            this.chargingID = value;
        }
        
  
        
        public LocationInformationGPRS getLocationInformationGPRS () {
            return this.locationInformationGPRS;
        }

        
        public boolean isLocationInformationGPRSPresent () {
            return this.locationInformationGPRS != null;
        }
        

        public void setLocationInformationGPRS (LocationInformationGPRS value) {
            this.locationInformationGPRS = value;
        }
        
  
        
        public PDPType getPDPType () {
            return this.pDPType;
        }

        
        public boolean isPDPTypePresent () {
            return this.pDPType != null;
        }
        

        public void setPDPType (PDPType value) {
            this.pDPType = value;
        }
        
  
        
        public QualityOfService getQualityOfService () {
            return this.qualityOfService;
        }

        
        public boolean isQualityOfServicePresent () {
            return this.qualityOfService != null;
        }
        

        public void setQualityOfService (QualityOfService value) {
            this.qualityOfService = value;
        }
        
  
        
        public TimeAndTimezone getTimeAndTimeZone () {
            return this.timeAndTimeZone;
        }

        
        public boolean isTimeAndTimeZonePresent () {
            return this.timeAndTimeZone != null;
        }
        

        public void setTimeAndTimeZone (TimeAndTimezone value) {
            this.timeAndTimeZone = value;
        }
        
  
        
        public GSN_Address getGGSNAddress () {
            return this.gGSNAddress;
        }

        
        public boolean isGGSNAddressPresent () {
            return this.gGSNAddress != null;
        }
        

        public void setGGSNAddress (GSN_Address value) {
            this.gGSNAddress = value;
        }
        
  
                
                
        public void initWithDefaults() {
            
        }

        public IASN1PreparedElementData getPreparedData() {
            return preparedData_Pdp_ContextchangeOfPositionSpecificInformationSequenceType;
        }

       private static IASN1PreparedElementData preparedData_Pdp_ContextchangeOfPositionSpecificInformationSequenceType = CoderFactory.getInstance().newPreparedElementData(Pdp_ContextchangeOfPositionSpecificInformationSequenceType.class);
                
       }

       
                
        @ASN1Element ( name = "pdp-ContextchangeOfPositionSpecificInformation", isOptional =  false , hasTag =  true, tag = 1 , hasDefaultValue =  false  )
    
	private Pdp_ContextchangeOfPositionSpecificInformationSequenceType pdp_ContextchangeOfPositionSpecificInformation = null;
                
  

       @ASN1PreparedElement
       @ASN1Sequence ( name = "detachSpecificInformation" , isSet = false )
       public static class DetachSpecificInformationSequenceType implements IASN1PreparedElement {
                
        @ASN1Element ( name = "inititatingEntity", isOptional =  true , hasTag =  true, tag = 0 , hasDefaultValue =  false  )
    
	private InitiatingEntity inititatingEntity = null;
                
  
        @ASN1Null ( name = "routeingAreaUpdate" ) 
    
        @ASN1Element ( name = "routeingAreaUpdate", isOptional =  true , hasTag =  true, tag = 1 , hasDefaultValue =  false  )
    
	private org.bn.types.NullObject routeingAreaUpdate = null;
                
  
        
        public InitiatingEntity getInititatingEntity () {
            return this.inititatingEntity;
        }

        
        public boolean isInititatingEntityPresent () {
            return this.inititatingEntity != null;
        }
        

        public void setInititatingEntity (InitiatingEntity value) {
            this.inititatingEntity = value;
        }
        
  
                
                
        public void initWithDefaults() {
            
        }

        public IASN1PreparedElementData getPreparedData() {
            return preparedData_DetachSpecificInformationSequenceType;
        }

       private static IASN1PreparedElementData preparedData_DetachSpecificInformationSequenceType = CoderFactory.getInstance().newPreparedElementData(DetachSpecificInformationSequenceType.class);
                
       }

       
                
        @ASN1Element ( name = "detachSpecificInformation", isOptional =  false , hasTag =  true, tag = 2 , hasDefaultValue =  false  )
    
	private DetachSpecificInformationSequenceType detachSpecificInformation = null;
                
  

       @ASN1PreparedElement
       @ASN1Sequence ( name = "disconnectSpecificInformation" , isSet = false )
       public static class DisconnectSpecificInformationSequenceType implements IASN1PreparedElement {
                
        @ASN1Element ( name = "inititatingEntity", isOptional =  true , hasTag =  true, tag = 0 , hasDefaultValue =  false  )
    
	private InitiatingEntity inititatingEntity = null;
                
  
        @ASN1Null ( name = "routeingAreaUpdate" ) 
    
        @ASN1Element ( name = "routeingAreaUpdate", isOptional =  true , hasTag =  true, tag = 1 , hasDefaultValue =  false  )
    
	private org.bn.types.NullObject routeingAreaUpdate = null;
                
  
        
        public InitiatingEntity getInititatingEntity () {
            return this.inititatingEntity;
        }

        
        public boolean isInititatingEntityPresent () {
            return this.inititatingEntity != null;
        }
        

        public void setInititatingEntity (InitiatingEntity value) {
            this.inititatingEntity = value;
        }
        
  
                
                
        public void initWithDefaults() {
            
        }

        public IASN1PreparedElementData getPreparedData() {
            return preparedData_DisconnectSpecificInformationSequenceType;
        }

       private static IASN1PreparedElementData preparedData_DisconnectSpecificInformationSequenceType = CoderFactory.getInstance().newPreparedElementData(DisconnectSpecificInformationSequenceType.class);
                
       }

       
                
        @ASN1Element ( name = "disconnectSpecificInformation", isOptional =  false , hasTag =  true, tag = 3 , hasDefaultValue =  false  )
    
	private DisconnectSpecificInformationSequenceType disconnectSpecificInformation = null;
                
  

       @ASN1PreparedElement
       @ASN1Sequence ( name = "pDPContextEstablishmentSpecificInformation" , isSet = false )
       public static class PDPContextEstablishmentSpecificInformationSequenceType implements IASN1PreparedElement {
                
        @ASN1Element ( name = "accessPointName", isOptional =  true , hasTag =  true, tag = 0 , hasDefaultValue =  false  )
    
	private AccessPointName accessPointName = null;
                
  
        @ASN1Element ( name = "pDPType", isOptional =  true , hasTag =  true, tag = 1 , hasDefaultValue =  false  )
    
	private PDPType pDPType = null;
                
  
        @ASN1Element ( name = "qualityOfService", isOptional =  true , hasTag =  true, tag = 2 , hasDefaultValue =  false  )
    
	private QualityOfService qualityOfService = null;
                
  
        @ASN1Element ( name = "locationInformationGPRS", isOptional =  true , hasTag =  true, tag = 3 , hasDefaultValue =  false  )
    
	private LocationInformationGPRS locationInformationGPRS = null;
                
  
        @ASN1Element ( name = "timeAndTimeZone", isOptional =  true , hasTag =  true, tag = 4 , hasDefaultValue =  false  )
    
	private TimeAndTimezone timeAndTimeZone = null;
                
  
        @ASN1Element ( name = "pDPInitiationType", isOptional =  true , hasTag =  true, tag = 5 , hasDefaultValue =  false  )
    
	private PDPInitiationType pDPInitiationType = null;
                
  
        @ASN1Null ( name = "secondaryPDPContext" ) 
    
        @ASN1Element ( name = "secondaryPDPContext", isOptional =  true , hasTag =  true, tag = 6 , hasDefaultValue =  false  )
    
	private org.bn.types.NullObject secondaryPDPContext = null;
                
  
        
        public AccessPointName getAccessPointName () {
            return this.accessPointName;
        }

        
        public boolean isAccessPointNamePresent () {
            return this.accessPointName != null;
        }
        

        public void setAccessPointName (AccessPointName value) {
            this.accessPointName = value;
        }
        
  
        
        public PDPType getPDPType () {
            return this.pDPType;
        }

        
        public boolean isPDPTypePresent () {
            return this.pDPType != null;
        }
        

        public void setPDPType (PDPType value) {
            this.pDPType = value;
        }
        
  
        
        public QualityOfService getQualityOfService () {
            return this.qualityOfService;
        }

        
        public boolean isQualityOfServicePresent () {
            return this.qualityOfService != null;
        }
        

        public void setQualityOfService (QualityOfService value) {
            this.qualityOfService = value;
        }
        
  
        
        public LocationInformationGPRS getLocationInformationGPRS () {
            return this.locationInformationGPRS;
        }

        
        public boolean isLocationInformationGPRSPresent () {
            return this.locationInformationGPRS != null;
        }
        

        public void setLocationInformationGPRS (LocationInformationGPRS value) {
            this.locationInformationGPRS = value;
        }
        
  
        
        public TimeAndTimezone getTimeAndTimeZone () {
            return this.timeAndTimeZone;
        }

        
        public boolean isTimeAndTimeZonePresent () {
            return this.timeAndTimeZone != null;
        }
        

        public void setTimeAndTimeZone (TimeAndTimezone value) {
            this.timeAndTimeZone = value;
        }
        
  
        
        public PDPInitiationType getPDPInitiationType () {
            return this.pDPInitiationType;
        }

        
        public boolean isPDPInitiationTypePresent () {
            return this.pDPInitiationType != null;
        }
        

        public void setPDPInitiationType (PDPInitiationType value) {
            this.pDPInitiationType = value;
        }
        
  
                
                
        public void initWithDefaults() {
            
        }

        public IASN1PreparedElementData getPreparedData() {
            return preparedData_PDPContextEstablishmentSpecificInformationSequenceType;
        }

       private static IASN1PreparedElementData preparedData_PDPContextEstablishmentSpecificInformationSequenceType = CoderFactory.getInstance().newPreparedElementData(PDPContextEstablishmentSpecificInformationSequenceType.class);
                
       }

       
                
        @ASN1Element ( name = "pDPContextEstablishmentSpecificInformation", isOptional =  false , hasTag =  true, tag = 4 , hasDefaultValue =  false  )
    
	private PDPContextEstablishmentSpecificInformationSequenceType pDPContextEstablishmentSpecificInformation = null;
                
  

       @ASN1PreparedElement
       @ASN1Sequence ( name = "pDPContextEstablishmentAcknowledgementSpecificInformation" , isSet = false )
       public static class PDPContextEstablishmentAcknowledgementSpecificInformationSequenceType implements IASN1PreparedElement {
                
        @ASN1Element ( name = "accessPointName", isOptional =  true , hasTag =  true, tag = 0 , hasDefaultValue =  false  )
    
	private AccessPointName accessPointName = null;
                
  
        @ASN1Element ( name = "chargingID", isOptional =  true , hasTag =  true, tag = 1 , hasDefaultValue =  false  )
    
	private GPRSChargingID chargingID = null;
                
  
        @ASN1Element ( name = "pDPType", isOptional =  true , hasTag =  true, tag = 2 , hasDefaultValue =  false  )
    
	private PDPType pDPType = null;
                
  
        @ASN1Element ( name = "qualityOfService", isOptional =  true , hasTag =  true, tag = 3 , hasDefaultValue =  false  )
    
	private QualityOfService qualityOfService = null;
                
  
        @ASN1Element ( name = "locationInformationGPRS", isOptional =  true , hasTag =  true, tag = 4 , hasDefaultValue =  false  )
    
	private LocationInformationGPRS locationInformationGPRS = null;
                
  
        @ASN1Element ( name = "timeAndTimeZone", isOptional =  true , hasTag =  true, tag = 5 , hasDefaultValue =  false  )
    
	private TimeAndTimezone timeAndTimeZone = null;
                
  
        @ASN1Element ( name = "gGSNAddress", isOptional =  true , hasTag =  true, tag = 6 , hasDefaultValue =  false  )
    
	private GSN_Address gGSNAddress = null;
                
  
        
        public AccessPointName getAccessPointName () {
            return this.accessPointName;
        }

        
        public boolean isAccessPointNamePresent () {
            return this.accessPointName != null;
        }
        

        public void setAccessPointName (AccessPointName value) {
            this.accessPointName = value;
        }
        
  
        
        public GPRSChargingID getChargingID () {
            return this.chargingID;
        }

        
        public boolean isChargingIDPresent () {
            return this.chargingID != null;
        }
        

        public void setChargingID (GPRSChargingID value) {
            this.chargingID = value;
        }
        
  
        
        public PDPType getPDPType () {
            return this.pDPType;
        }

        
        public boolean isPDPTypePresent () {
            return this.pDPType != null;
        }
        

        public void setPDPType (PDPType value) {
            this.pDPType = value;
        }
        
  
        
        public QualityOfService getQualityOfService () {
            return this.qualityOfService;
        }

        
        public boolean isQualityOfServicePresent () {
            return this.qualityOfService != null;
        }
        

        public void setQualityOfService (QualityOfService value) {
            this.qualityOfService = value;
        }
        
  
        
        public LocationInformationGPRS getLocationInformationGPRS () {
            return this.locationInformationGPRS;
        }

        
        public boolean isLocationInformationGPRSPresent () {
            return this.locationInformationGPRS != null;
        }
        

        public void setLocationInformationGPRS (LocationInformationGPRS value) {
            this.locationInformationGPRS = value;
        }
        
  
        
        public TimeAndTimezone getTimeAndTimeZone () {
            return this.timeAndTimeZone;
        }

        
        public boolean isTimeAndTimeZonePresent () {
            return this.timeAndTimeZone != null;
        }
        

        public void setTimeAndTimeZone (TimeAndTimezone value) {
            this.timeAndTimeZone = value;
        }
        
  
        
        public GSN_Address getGGSNAddress () {
            return this.gGSNAddress;
        }

        
        public boolean isGGSNAddressPresent () {
            return this.gGSNAddress != null;
        }
        

        public void setGGSNAddress (GSN_Address value) {
            this.gGSNAddress = value;
        }
        
  
                
                
        public void initWithDefaults() {
            
        }

        public IASN1PreparedElementData getPreparedData() {
            return preparedData_PDPContextEstablishmentAcknowledgementSpecificInformationSequenceType;
        }

       private static IASN1PreparedElementData preparedData_PDPContextEstablishmentAcknowledgementSpecificInformationSequenceType = CoderFactory.getInstance().newPreparedElementData(PDPContextEstablishmentAcknowledgementSpecificInformationSequenceType.class);
                
       }

       
                
        @ASN1Element ( name = "pDPContextEstablishmentAcknowledgementSpecificInformation", isOptional =  false , hasTag =  true, tag = 5 , hasDefaultValue =  false  )
    
	private PDPContextEstablishmentAcknowledgementSpecificInformationSequenceType pDPContextEstablishmentAcknowledgementSpecificInformation = null;
                
  
        
        public AttachChangeOfPositionSpecificInformationSequenceType getAttachChangeOfPositionSpecificInformation () {
            return this.attachChangeOfPositionSpecificInformation;
        }

        public boolean isAttachChangeOfPositionSpecificInformationSelected () {
            return this.attachChangeOfPositionSpecificInformation != null;
        }

        private void setAttachChangeOfPositionSpecificInformation (AttachChangeOfPositionSpecificInformationSequenceType value) {
            this.attachChangeOfPositionSpecificInformation = value;
        }

        
        public void selectAttachChangeOfPositionSpecificInformation (AttachChangeOfPositionSpecificInformationSequenceType value) {
            this.attachChangeOfPositionSpecificInformation = value;
            
                    setPdp_ContextchangeOfPositionSpecificInformation(null);
                
                    setDetachSpecificInformation(null);
                
                    setDisconnectSpecificInformation(null);
                
                    setPDPContextEstablishmentSpecificInformation(null);
                
                    setPDPContextEstablishmentAcknowledgementSpecificInformation(null);
                            
        }

        
  
        
        public Pdp_ContextchangeOfPositionSpecificInformationSequenceType getPdp_ContextchangeOfPositionSpecificInformation () {
            return this.pdp_ContextchangeOfPositionSpecificInformation;
        }

        public boolean isPdp_ContextchangeOfPositionSpecificInformationSelected () {
            return this.pdp_ContextchangeOfPositionSpecificInformation != null;
        }

        private void setPdp_ContextchangeOfPositionSpecificInformation (Pdp_ContextchangeOfPositionSpecificInformationSequenceType value) {
            this.pdp_ContextchangeOfPositionSpecificInformation = value;
        }

        
        public void selectPdp_ContextchangeOfPositionSpecificInformation (Pdp_ContextchangeOfPositionSpecificInformationSequenceType value) {
            this.pdp_ContextchangeOfPositionSpecificInformation = value;
            
                    setAttachChangeOfPositionSpecificInformation(null);
                
                    setDetachSpecificInformation(null);
                
                    setDisconnectSpecificInformation(null);
                
                    setPDPContextEstablishmentSpecificInformation(null);
                
                    setPDPContextEstablishmentAcknowledgementSpecificInformation(null);
                            
        }

        
  
        
        public DetachSpecificInformationSequenceType getDetachSpecificInformation () {
            return this.detachSpecificInformation;
        }

        public boolean isDetachSpecificInformationSelected () {
            return this.detachSpecificInformation != null;
        }

        private void setDetachSpecificInformation (DetachSpecificInformationSequenceType value) {
            this.detachSpecificInformation = value;
        }

        
        public void selectDetachSpecificInformation (DetachSpecificInformationSequenceType value) {
            this.detachSpecificInformation = value;
            
                    setAttachChangeOfPositionSpecificInformation(null);
                
                    setPdp_ContextchangeOfPositionSpecificInformation(null);
                
                    setDisconnectSpecificInformation(null);
                
                    setPDPContextEstablishmentSpecificInformation(null);
                
                    setPDPContextEstablishmentAcknowledgementSpecificInformation(null);
                            
        }

        
  
        
        public DisconnectSpecificInformationSequenceType getDisconnectSpecificInformation () {
            return this.disconnectSpecificInformation;
        }

        public boolean isDisconnectSpecificInformationSelected () {
            return this.disconnectSpecificInformation != null;
        }

        private void setDisconnectSpecificInformation (DisconnectSpecificInformationSequenceType value) {
            this.disconnectSpecificInformation = value;
        }

        
        public void selectDisconnectSpecificInformation (DisconnectSpecificInformationSequenceType value) {
            this.disconnectSpecificInformation = value;
            
                    setAttachChangeOfPositionSpecificInformation(null);
                
                    setPdp_ContextchangeOfPositionSpecificInformation(null);
                
                    setDetachSpecificInformation(null);
                
                    setPDPContextEstablishmentSpecificInformation(null);
                
                    setPDPContextEstablishmentAcknowledgementSpecificInformation(null);
                            
        }

        
  
        
        public PDPContextEstablishmentSpecificInformationSequenceType getPDPContextEstablishmentSpecificInformation () {
            return this.pDPContextEstablishmentSpecificInformation;
        }

        public boolean isPDPContextEstablishmentSpecificInformationSelected () {
            return this.pDPContextEstablishmentSpecificInformation != null;
        }

        private void setPDPContextEstablishmentSpecificInformation (PDPContextEstablishmentSpecificInformationSequenceType value) {
            this.pDPContextEstablishmentSpecificInformation = value;
        }

        
        public void selectPDPContextEstablishmentSpecificInformation (PDPContextEstablishmentSpecificInformationSequenceType value) {
            this.pDPContextEstablishmentSpecificInformation = value;
            
                    setAttachChangeOfPositionSpecificInformation(null);
                
                    setPdp_ContextchangeOfPositionSpecificInformation(null);
                
                    setDetachSpecificInformation(null);
                
                    setDisconnectSpecificInformation(null);
                
                    setPDPContextEstablishmentAcknowledgementSpecificInformation(null);
                            
        }

        
  
        
        public PDPContextEstablishmentAcknowledgementSpecificInformationSequenceType getPDPContextEstablishmentAcknowledgementSpecificInformation () {
            return this.pDPContextEstablishmentAcknowledgementSpecificInformation;
        }

        public boolean isPDPContextEstablishmentAcknowledgementSpecificInformationSelected () {
            return this.pDPContextEstablishmentAcknowledgementSpecificInformation != null;
        }

        private void setPDPContextEstablishmentAcknowledgementSpecificInformation (PDPContextEstablishmentAcknowledgementSpecificInformationSequenceType value) {
            this.pDPContextEstablishmentAcknowledgementSpecificInformation = value;
        }

        
        public void selectPDPContextEstablishmentAcknowledgementSpecificInformation (PDPContextEstablishmentAcknowledgementSpecificInformationSequenceType value) {
            this.pDPContextEstablishmentAcknowledgementSpecificInformation = value;
            
                    setAttachChangeOfPositionSpecificInformation(null);
                
                    setPdp_ContextchangeOfPositionSpecificInformation(null);
                
                    setDetachSpecificInformation(null);
                
                    setDisconnectSpecificInformation(null);
                
                    setPDPContextEstablishmentSpecificInformation(null);
                            
        }

        
  

	    public void initWithDefaults() {
	    }

        private static IASN1PreparedElementData preparedData = CoderFactory.getInstance().newPreparedElementData(GPRSEventSpecificInformation.class);
        public IASN1PreparedElementData getPreparedData() {
            return preparedData;
        }


    }
            