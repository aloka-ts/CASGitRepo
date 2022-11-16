
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
    @ASN1BoxedType ( name = "QualificationRequest" )
    public class QualificationRequest implements IASN1PreparedElement {
                
        

       @ASN1PreparedElement
       @ASN1Sequence ( name = "QualificationRequest" , isSet = true )
       public static class QualificationRequestSequenceType implements IASN1PreparedElement {
                
        @ASN1Element ( name = "electronicSerialNumber", isOptional =  false , hasTag =  true, tag = 9 , hasDefaultValue =  false  )
    
	private ElectronicSerialNumber electronicSerialNumber = null;
                
  
        @ASN1Element ( name = "msid", isOptional =  true , hasTag =  false  , hasDefaultValue =  false  )
    
	private MSID msid = null;
                
  
        @ASN1Element ( name = "qualificationInformationCode", isOptional =  false , hasTag =  true, tag = 17 , hasDefaultValue =  false  )
    
	private QualificationInformationCode qualificationInformationCode = null;
                
  
        @ASN1Element ( name = "systemMyTypeCode", isOptional =  false , hasTag =  true, tag = 22 , hasDefaultValue =  false  )
    
	private SystemMyTypeCode systemMyTypeCode = null;
                
  
        @ASN1Element ( name = "locationAreaID", isOptional =  true , hasTag =  true, tag = 33 , hasDefaultValue =  false  )
    
	private LocationAreaID locationAreaID = null;
                
  
        @ASN1Element ( name = "cdmaNetworkIdentification", isOptional =  true , hasTag =  true, tag = 232 , hasDefaultValue =  false  )
    
	private CDMANetworkIdentification cdmaNetworkIdentification = null;
                
  
        @ASN1Element ( name = "controlChannelMode", isOptional =  true , hasTag =  true, tag = 199 , hasDefaultValue =  false  )
    
	private ControlChannelMode controlChannelMode = null;
                
  
        @ASN1Element ( name = "mscid", isOptional =  true , hasTag =  true, tag = 21 , hasDefaultValue =  false  )
    
	private MSCID mscid = null;
                
  
        @ASN1Element ( name = "senderIdentificationNumber", isOptional =  true , hasTag =  true, tag = 103 , hasDefaultValue =  false  )
    
	private SenderIdentificationNumber senderIdentificationNumber = null;
                
  
        @ASN1Element ( name = "systemAccessType", isOptional =  true , hasTag =  true, tag = 34 , hasDefaultValue =  false  )
    
	private SystemAccessType systemAccessType = null;
                
  
        @ASN1Element ( name = "terminalType", isOptional =  true , hasTag =  true, tag = 47 , hasDefaultValue =  false  )
    
	private TerminalType terminalType = null;
                
  
        @ASN1Element ( name = "transactionCapability", isOptional =  true , hasTag =  true, tag = 123 , hasDefaultValue =  false  )
    
	private TransactionCapability transactionCapability = null;
                
  
        @ASN1Element ( name = "winCapability", isOptional =  true , hasTag =  true, tag = 280 , hasDefaultValue =  false  )
    
	private WINCapability winCapability = null;
                
  
        @ASN1Element ( name = "nonPublicData", isOptional =  true , hasTag =  true, tag = 200 , hasDefaultValue =  false  )
    
	private NonPublicData nonPublicData = null;
                
  
        @ASN1Element ( name = "userZoneData", isOptional =  true , hasTag =  true, tag = 209 , hasDefaultValue =  false  )
    
	private UserZoneData userZoneData = null;
                
  
        @ASN1Element ( name = "meid", isOptional =  true , hasTag =  true, tag = 390 , hasDefaultValue =  false  )
    
	private MEID meid = null;
                
  
        
        public ElectronicSerialNumber getElectronicSerialNumber () {
            return this.electronicSerialNumber;
        }

        

        public void setElectronicSerialNumber (ElectronicSerialNumber value) {
            this.electronicSerialNumber = value;
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
        
  
        
        public QualificationInformationCode getQualificationInformationCode () {
            return this.qualificationInformationCode;
        }

        

        public void setQualificationInformationCode (QualificationInformationCode value) {
            this.qualificationInformationCode = value;
        }
        
  
        
        public SystemMyTypeCode getSystemMyTypeCode () {
            return this.systemMyTypeCode;
        }

        

        public void setSystemMyTypeCode (SystemMyTypeCode value) {
            this.systemMyTypeCode = value;
        }
        
  
        
        public LocationAreaID getLocationAreaID () {
            return this.locationAreaID;
        }

        
        public boolean isLocationAreaIDPresent () {
            return this.locationAreaID != null;
        }
        

        public void setLocationAreaID (LocationAreaID value) {
            this.locationAreaID = value;
        }
        
  
        
        public CDMANetworkIdentification getCdmaNetworkIdentification () {
            return this.cdmaNetworkIdentification;
        }

        
        public boolean isCdmaNetworkIdentificationPresent () {
            return this.cdmaNetworkIdentification != null;
        }
        

        public void setCdmaNetworkIdentification (CDMANetworkIdentification value) {
            this.cdmaNetworkIdentification = value;
        }
        
  
        
        public ControlChannelMode getControlChannelMode () {
            return this.controlChannelMode;
        }

        
        public boolean isControlChannelModePresent () {
            return this.controlChannelMode != null;
        }
        

        public void setControlChannelMode (ControlChannelMode value) {
            this.controlChannelMode = value;
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
        
  
        
        public SenderIdentificationNumber getSenderIdentificationNumber () {
            return this.senderIdentificationNumber;
        }

        
        public boolean isSenderIdentificationNumberPresent () {
            return this.senderIdentificationNumber != null;
        }
        

        public void setSenderIdentificationNumber (SenderIdentificationNumber value) {
            this.senderIdentificationNumber = value;
        }
        
  
        
        public SystemAccessType getSystemAccessType () {
            return this.systemAccessType;
        }

        
        public boolean isSystemAccessTypePresent () {
            return this.systemAccessType != null;
        }
        

        public void setSystemAccessType (SystemAccessType value) {
            this.systemAccessType = value;
        }
        
  
        
        public TerminalType getTerminalType () {
            return this.terminalType;
        }

        
        public boolean isTerminalTypePresent () {
            return this.terminalType != null;
        }
        

        public void setTerminalType (TerminalType value) {
            this.terminalType = value;
        }
        
  
        
        public TransactionCapability getTransactionCapability () {
            return this.transactionCapability;
        }

        
        public boolean isTransactionCapabilityPresent () {
            return this.transactionCapability != null;
        }
        

        public void setTransactionCapability (TransactionCapability value) {
            this.transactionCapability = value;
        }
        
  
        
        public WINCapability getWinCapability () {
            return this.winCapability;
        }

        
        public boolean isWinCapabilityPresent () {
            return this.winCapability != null;
        }
        

        public void setWinCapability (WINCapability value) {
            this.winCapability = value;
        }
        
  
        
        public NonPublicData getNonPublicData () {
            return this.nonPublicData;
        }

        
        public boolean isNonPublicDataPresent () {
            return this.nonPublicData != null;
        }
        

        public void setNonPublicData (NonPublicData value) {
            this.nonPublicData = value;
        }
        
  
        
        public UserZoneData getUserZoneData () {
            return this.userZoneData;
        }

        
        public boolean isUserZoneDataPresent () {
            return this.userZoneData != null;
        }
        

        public void setUserZoneData (UserZoneData value) {
            this.userZoneData = value;
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
            return preparedData_QualificationRequestSequenceType;
        }

       private static IASN1PreparedElementData preparedData_QualificationRequestSequenceType = CoderFactory.getInstance().newPreparedElementData(QualificationRequestSequenceType.class);
                
       }

       
                
        @ASN1Element ( name = "QualificationRequest", isOptional =  false , hasTag =  true, tag = 18, 
        tagClass =  TagClass.Private  , hasDefaultValue =  false  )
    
        private QualificationRequestSequenceType  value;        

        
        
        public QualificationRequest () {
        }
        
        
        
        public void setValue(QualificationRequestSequenceType value) {
            this.value = value;
        }
        
        
        
        public QualificationRequestSequenceType getValue() {
            return this.value;
        }            
        

	    public void initWithDefaults() {
	    }

        private static IASN1PreparedElementData preparedData = CoderFactory.getInstance().newPreparedElementData(QualificationRequest.class);
        public IASN1PreparedElementData getPreparedData() {
            return preparedData;
        }

            
    }
            