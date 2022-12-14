
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
    @ASN1BoxedType ( name = "InterSystemPage2" )
    public class InterSystemPage2 implements IASN1PreparedElement {
                
        

       @ASN1PreparedElement
       @ASN1Sequence ( name = "InterSystemPage2" , isSet = true )
       public static class InterSystemPage2SequenceType implements IASN1PreparedElement {
                
        @ASN1Element ( name = "billingID", isOptional =  false , hasTag =  true, tag = 1 , hasDefaultValue =  false  )
    
	private BillingID billingID = null;
                
  
        @ASN1Element ( name = "electronicSerialNumber", isOptional =  false , hasTag =  true, tag = 9 , hasDefaultValue =  false  )
    
	private ElectronicSerialNumber electronicSerialNumber = null;
                
  
        @ASN1Element ( name = "alertCode", isOptional =  true , hasTag =  true, tag = 75 , hasDefaultValue =  false  )
    
	private AlertCode alertCode = null;
                
  
        @ASN1Element ( name = "callingPartyNumberString1", isOptional =  true , hasTag =  true, tag = 82 , hasDefaultValue =  false  )
    
	private CallingPartyNumberString1 callingPartyNumberString1 = null;
                
  
        @ASN1Element ( name = "callingPartyNumberString2", isOptional =  true , hasTag =  true, tag = 83 , hasDefaultValue =  false  )
    
	private CallingPartyNumberString2 callingPartyNumberString2 = null;
                
  
        @ASN1Element ( name = "callingPartySubaddress", isOptional =  true , hasTag =  true, tag = 84 , hasDefaultValue =  false  )
    
	private CallingPartySubaddress callingPartySubaddress = null;
                
  
        @ASN1Element ( name = "cdmaBandClass", isOptional =  true , hasTag =  true, tag = 170 , hasDefaultValue =  false  )
    
	private CDMABandClass cdmaBandClass = null;
                
  
        @ASN1Element ( name = "cdmaMobileProtocolRevision", isOptional =  true , hasTag =  true, tag = 66 , hasDefaultValue =  false  )
    
	private CDMAMobileProtocolRevision cdmaMobileProtocolRevision = null;
                
  
        @ASN1Element ( name = "controlChannelMode", isOptional =  true , hasTag =  true, tag = 199 , hasDefaultValue =  false  )
    
	private ControlChannelMode controlChannelMode = null;
                
  
        @ASN1Element ( name = "cdmaServiceOption", isOptional =  true , hasTag =  true, tag = 175 , hasDefaultValue =  false  )
    
	private CDMAServiceOption cdmaServiceOption = null;
                
  
        @ASN1Element ( name = "cdmaServiceOptionList", isOptional =  true , hasTag =  true, tag = 176 , hasDefaultValue =  false  )
    
	private CDMAServiceOptionList cdmaServiceOptionList = null;
                
  
        @ASN1Element ( name = "cdmaSlotCycleIndex", isOptional =  true , hasTag =  true, tag = 166 , hasDefaultValue =  false  )
    
	private CDMASlotCycleIndex cdmaSlotCycleIndex = null;
                
  
        @ASN1Element ( name = "cdmaStationClassMark", isOptional =  true , hasTag =  true, tag = 59 , hasDefaultValue =  false  )
    
	private CDMAStationClassMark cdmaStationClassMark = null;
                
  
        @ASN1Element ( name = "cdmaStationClassMark2", isOptional =  true , hasTag =  true, tag = 177 , hasDefaultValue =  false  )
    
	private CDMAStationClassMark2 cdmaStationClassMark2 = null;
                
  
        @ASN1Element ( name = "displayText", isOptional =  true , hasTag =  true, tag = 244 , hasDefaultValue =  false  )
    
	private DisplayText displayText = null;
                
  
        @ASN1Element ( name = "displayText2", isOptional =  true , hasTag =  true, tag = 299 , hasDefaultValue =  false  )
    
	private DisplayText2 displayText2 = null;
                
  
        @ASN1Element ( name = "imsi", isOptional =  true , hasTag =  true, tag = 242 , hasDefaultValue =  false  )
    
	private IMSI imsi = null;
                
  
        @ASN1Element ( name = "locationAreaID", isOptional =  true , hasTag =  true, tag = 33 , hasDefaultValue =  false  )
    
	private LocationAreaID locationAreaID = null;
                
  
        @ASN1Element ( name = "mobileDirectoryNumber", isOptional =  true , hasTag =  true, tag = 93 , hasDefaultValue =  false  )
    
	private MobileDirectoryNumber mobileDirectoryNumber = null;
                
  
        @ASN1Element ( name = "mobileIdentificationNumber", isOptional =  false , hasTag =  true, tag = 8 , hasDefaultValue =  false  )
    
	private MobileIdentificationNumber mobileIdentificationNumber = null;
                
  
        @ASN1Element ( name = "mSIDUsage", isOptional =  true , hasTag =  true, tag = 327 , hasDefaultValue =  false  )
    
	private MSIDUsage mSIDUsage = null;
                
  
        @ASN1Element ( name = "networkTMSI", isOptional =  true , hasTag =  true, tag = 233 , hasDefaultValue =  false  )
    
	private NetworkTMSI networkTMSI = null;
                
  
        @ASN1Element ( name = "nonPublicData", isOptional =  true , hasTag =  true, tag = 200 , hasDefaultValue =  false  )
    
	private NonPublicData nonPublicData = null;
                
  
        @ASN1Element ( name = "pageCount", isOptional =  true , hasTag =  true, tag = 300 , hasDefaultValue =  false  )
    
	private PageCount pageCount = null;
                
  
        @ASN1Element ( name = "pageIndicator", isOptional =  true , hasTag =  true, tag = 71 , hasDefaultValue =  false  )
    
	private PageIndicator pageIndicator = null;
                
  
        @ASN1Element ( name = "pagingFrameClass", isOptional =  true , hasTag =  true, tag = 210 , hasDefaultValue =  false  )
    
	private PagingFrameClass pagingFrameClass = null;
                
  
        @ASN1Element ( name = "pageResponseTime", isOptional =  true , hasTag =  true, tag = 301 , hasDefaultValue =  false  )
    
	private PageResponseTime pageResponseTime = null;
                
  
        @ASN1Element ( name = "pSID-RSIDList", isOptional =  true , hasTag =  true, tag = 203 , hasDefaultValue =  false  )
    
	private PSID_RSIDList pSID_RSIDList = null;
                
  
        @ASN1Element ( name = "redirectingNumberString", isOptional =  true , hasTag =  true, tag = 101 , hasDefaultValue =  false  )
    
	private RedirectingNumberString redirectingNumberString = null;
                
  
        @ASN1Element ( name = "redirectingSubaddress", isOptional =  true , hasTag =  true, tag = 102 , hasDefaultValue =  false  )
    
	private RedirectingSubaddress redirectingSubaddress = null;
                
  
        @ASN1Element ( name = "tdmaDataFeaturesIndicator", isOptional =  true , hasTag =  true, tag = 221 , hasDefaultValue =  false  )
    
	private TDMADataFeaturesIndicator tdmaDataFeaturesIndicator = null;
                
  
        @ASN1Element ( name = "tdmaServiceCode", isOptional =  true , hasTag =  true, tag = 178 , hasDefaultValue =  false  )
    
	private TDMAServiceCode tdmaServiceCode = null;
                
  
        @ASN1Element ( name = "terminalType", isOptional =  true , hasTag =  true, tag = 47 , hasDefaultValue =  false  )
    
	private TerminalType terminalType = null;
                
  
        @ASN1Element ( name = "userZoneData", isOptional =  true , hasTag =  true, tag = 209 , hasDefaultValue =  false  )
    
	private UserZoneData userZoneData = null;
                
  
        
        public BillingID getBillingID () {
            return this.billingID;
        }

        

        public void setBillingID (BillingID value) {
            this.billingID = value;
        }
        
  
        
        public ElectronicSerialNumber getElectronicSerialNumber () {
            return this.electronicSerialNumber;
        }

        

        public void setElectronicSerialNumber (ElectronicSerialNumber value) {
            this.electronicSerialNumber = value;
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
        
  
        
        public CDMABandClass getCdmaBandClass () {
            return this.cdmaBandClass;
        }

        
        public boolean isCdmaBandClassPresent () {
            return this.cdmaBandClass != null;
        }
        

        public void setCdmaBandClass (CDMABandClass value) {
            this.cdmaBandClass = value;
        }
        
  
        
        public CDMAMobileProtocolRevision getCdmaMobileProtocolRevision () {
            return this.cdmaMobileProtocolRevision;
        }

        
        public boolean isCdmaMobileProtocolRevisionPresent () {
            return this.cdmaMobileProtocolRevision != null;
        }
        

        public void setCdmaMobileProtocolRevision (CDMAMobileProtocolRevision value) {
            this.cdmaMobileProtocolRevision = value;
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
        
  
        
        public CDMAServiceOption getCdmaServiceOption () {
            return this.cdmaServiceOption;
        }

        
        public boolean isCdmaServiceOptionPresent () {
            return this.cdmaServiceOption != null;
        }
        

        public void setCdmaServiceOption (CDMAServiceOption value) {
            this.cdmaServiceOption = value;
        }
        
  
        
        public CDMAServiceOptionList getCdmaServiceOptionList () {
            return this.cdmaServiceOptionList;
        }

        
        public boolean isCdmaServiceOptionListPresent () {
            return this.cdmaServiceOptionList != null;
        }
        

        public void setCdmaServiceOptionList (CDMAServiceOptionList value) {
            this.cdmaServiceOptionList = value;
        }
        
  
        
        public CDMASlotCycleIndex getCdmaSlotCycleIndex () {
            return this.cdmaSlotCycleIndex;
        }

        
        public boolean isCdmaSlotCycleIndexPresent () {
            return this.cdmaSlotCycleIndex != null;
        }
        

        public void setCdmaSlotCycleIndex (CDMASlotCycleIndex value) {
            this.cdmaSlotCycleIndex = value;
        }
        
  
        
        public CDMAStationClassMark getCdmaStationClassMark () {
            return this.cdmaStationClassMark;
        }

        
        public boolean isCdmaStationClassMarkPresent () {
            return this.cdmaStationClassMark != null;
        }
        

        public void setCdmaStationClassMark (CDMAStationClassMark value) {
            this.cdmaStationClassMark = value;
        }
        
  
        
        public CDMAStationClassMark2 getCdmaStationClassMark2 () {
            return this.cdmaStationClassMark2;
        }

        
        public boolean isCdmaStationClassMark2Present () {
            return this.cdmaStationClassMark2 != null;
        }
        

        public void setCdmaStationClassMark2 (CDMAStationClassMark2 value) {
            this.cdmaStationClassMark2 = value;
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
        
  
        
        public IMSI getImsi () {
            return this.imsi;
        }

        
        public boolean isImsiPresent () {
            return this.imsi != null;
        }
        

        public void setImsi (IMSI value) {
            this.imsi = value;
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
        
  
        
        public MobileDirectoryNumber getMobileDirectoryNumber () {
            return this.mobileDirectoryNumber;
        }

        
        public boolean isMobileDirectoryNumberPresent () {
            return this.mobileDirectoryNumber != null;
        }
        

        public void setMobileDirectoryNumber (MobileDirectoryNumber value) {
            this.mobileDirectoryNumber = value;
        }
        
  
        
        public MobileIdentificationNumber getMobileIdentificationNumber () {
            return this.mobileIdentificationNumber;
        }

        

        public void setMobileIdentificationNumber (MobileIdentificationNumber value) {
            this.mobileIdentificationNumber = value;
        }
        
  
        
        public MSIDUsage getMSIDUsage () {
            return this.mSIDUsage;
        }

        
        public boolean isMSIDUsagePresent () {
            return this.mSIDUsage != null;
        }
        

        public void setMSIDUsage (MSIDUsage value) {
            this.mSIDUsage = value;
        }
        
  
        
        public NetworkTMSI getNetworkTMSI () {
            return this.networkTMSI;
        }

        
        public boolean isNetworkTMSIPresent () {
            return this.networkTMSI != null;
        }
        

        public void setNetworkTMSI (NetworkTMSI value) {
            this.networkTMSI = value;
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
        
  
        
        public PageCount getPageCount () {
            return this.pageCount;
        }

        
        public boolean isPageCountPresent () {
            return this.pageCount != null;
        }
        

        public void setPageCount (PageCount value) {
            this.pageCount = value;
        }
        
  
        
        public PageIndicator getPageIndicator () {
            return this.pageIndicator;
        }

        
        public boolean isPageIndicatorPresent () {
            return this.pageIndicator != null;
        }
        

        public void setPageIndicator (PageIndicator value) {
            this.pageIndicator = value;
        }
        
  
        
        public PagingFrameClass getPagingFrameClass () {
            return this.pagingFrameClass;
        }

        
        public boolean isPagingFrameClassPresent () {
            return this.pagingFrameClass != null;
        }
        

        public void setPagingFrameClass (PagingFrameClass value) {
            this.pagingFrameClass = value;
        }
        
  
        
        public PageResponseTime getPageResponseTime () {
            return this.pageResponseTime;
        }

        
        public boolean isPageResponseTimePresent () {
            return this.pageResponseTime != null;
        }
        

        public void setPageResponseTime (PageResponseTime value) {
            this.pageResponseTime = value;
        }
        
  
        
        public PSID_RSIDList getPSID_RSIDList () {
            return this.pSID_RSIDList;
        }

        
        public boolean isPSID_RSIDListPresent () {
            return this.pSID_RSIDList != null;
        }
        

        public void setPSID_RSIDList (PSID_RSIDList value) {
            this.pSID_RSIDList = value;
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
        
  
        
        public TDMADataFeaturesIndicator getTdmaDataFeaturesIndicator () {
            return this.tdmaDataFeaturesIndicator;
        }

        
        public boolean isTdmaDataFeaturesIndicatorPresent () {
            return this.tdmaDataFeaturesIndicator != null;
        }
        

        public void setTdmaDataFeaturesIndicator (TDMADataFeaturesIndicator value) {
            this.tdmaDataFeaturesIndicator = value;
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
        
  
        
        public TerminalType getTerminalType () {
            return this.terminalType;
        }

        
        public boolean isTerminalTypePresent () {
            return this.terminalType != null;
        }
        

        public void setTerminalType (TerminalType value) {
            this.terminalType = value;
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
        
  
                
                
        public void initWithDefaults() {
            
        }

        public IASN1PreparedElementData getPreparedData() {
            return preparedData_InterSystemPage2SequenceType;
        }

       private static IASN1PreparedElementData preparedData_InterSystemPage2SequenceType = CoderFactory.getInstance().newPreparedElementData(InterSystemPage2SequenceType.class);
                
       }

       
                
        @ASN1Element ( name = "InterSystemPage2", isOptional =  false , hasTag =  true, tag = 18, 
        tagClass =  TagClass.Private  , hasDefaultValue =  false  )
    
        private InterSystemPage2SequenceType  value;        

        
        
        public InterSystemPage2 () {
        }
        
        
        
        public void setValue(InterSystemPage2SequenceType value) {
            this.value = value;
        }
        
        
        
        public InterSystemPage2SequenceType getValue() {
            return this.value;
        }            
        

	    public void initWithDefaults() {
	    }

        private static IASN1PreparedElementData preparedData = CoderFactory.getInstance().newPreparedElementData(InterSystemPage2.class);
        public IASN1PreparedElementData getPreparedData() {
            return preparedData;
        }

            
    }
            