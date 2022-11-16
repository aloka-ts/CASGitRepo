
package com.agnity.map.asngenerated;
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
    @ASN1Sequence ( name = "AnyTimeSubscriptionInterrogationRes", isSet = false )
    public class AnyTimeSubscriptionInterrogationRes implements IASN1PreparedElement {
            
        @ASN1Element ( name = "callForwardingData", isOptional =  true , hasTag =  true, tag = 1 , hasDefaultValue =  false  )
    
	private CallForwardingData callForwardingData = null;
                
  
        @ASN1Element ( name = "callBarringData", isOptional =  true , hasTag =  true, tag = 2 , hasDefaultValue =  false  )
    
	private CallBarringData callBarringData = null;
                
  
        @ASN1Element ( name = "odb-Info", isOptional =  true , hasTag =  true, tag = 3 , hasDefaultValue =  false  )
    
	private ODB_Info odb_Info = null;
                
  
        @ASN1Element ( name = "camel-SubscriptionInfo", isOptional =  true , hasTag =  true, tag = 4 , hasDefaultValue =  false  )
    
	private CAMEL_SubscriptionInfo camel_SubscriptionInfo = null;
                
  
        @ASN1Element ( name = "supportedVLR-CAMEL-Phases", isOptional =  true , hasTag =  true, tag = 5 , hasDefaultValue =  false  )
    
	private SupportedCamelPhases supportedVLR_CAMEL_Phases = null;
                
  
        @ASN1Element ( name = "supportedSGSN-CAMEL-Phases", isOptional =  true , hasTag =  true, tag = 6 , hasDefaultValue =  false  )
    
	private SupportedCamelPhases supportedSGSN_CAMEL_Phases = null;
                
  
        @ASN1Element ( name = "extensionContainer", isOptional =  true , hasTag =  true, tag = 7 , hasDefaultValue =  false  )
    
	private ExtensionContainer extensionContainer = null;
                
  
        @ASN1Element ( name = "offeredCamel4CSIsInVLR", isOptional =  true , hasTag =  true, tag = 8 , hasDefaultValue =  false  )
    
	private OfferedCamel4CSIs offeredCamel4CSIsInVLR = null;
                
  
        @ASN1Element ( name = "offeredCamel4CSIsInSGSN", isOptional =  true , hasTag =  true, tag = 9 , hasDefaultValue =  false  )
    
	private OfferedCamel4CSIs offeredCamel4CSIsInSGSN = null;
                
  
        @ASN1Element ( name = "msisdn-BS-List", isOptional =  true , hasTag =  true, tag = 10 , hasDefaultValue =  false  )
    
	private MSISDN_BS_List msisdn_BS_List = null;
                
  
        @ASN1Element ( name = "csg-SubscriptionDataList", isOptional =  true , hasTag =  true, tag = 11 , hasDefaultValue =  false  )
    
	private CSG_SubscriptionDataList csg_SubscriptionDataList = null;
                
  
        
        public CallForwardingData getCallForwardingData () {
            return this.callForwardingData;
        }

        
        public boolean isCallForwardingDataPresent () {
            return this.callForwardingData != null;
        }
        

        public void setCallForwardingData (CallForwardingData value) {
            this.callForwardingData = value;
        }
        
  
        
        public CallBarringData getCallBarringData () {
            return this.callBarringData;
        }

        
        public boolean isCallBarringDataPresent () {
            return this.callBarringData != null;
        }
        

        public void setCallBarringData (CallBarringData value) {
            this.callBarringData = value;
        }
        
  
        
        public ODB_Info getOdb_Info () {
            return this.odb_Info;
        }

        
        public boolean isOdb_InfoPresent () {
            return this.odb_Info != null;
        }
        

        public void setOdb_Info (ODB_Info value) {
            this.odb_Info = value;
        }
        
  
        
        public CAMEL_SubscriptionInfo getCamel_SubscriptionInfo () {
            return this.camel_SubscriptionInfo;
        }

        
        public boolean isCamel_SubscriptionInfoPresent () {
            return this.camel_SubscriptionInfo != null;
        }
        

        public void setCamel_SubscriptionInfo (CAMEL_SubscriptionInfo value) {
            this.camel_SubscriptionInfo = value;
        }
        
  
        
        public SupportedCamelPhases getSupportedVLR_CAMEL_Phases () {
            return this.supportedVLR_CAMEL_Phases;
        }

        
        public boolean isSupportedVLR_CAMEL_PhasesPresent () {
            return this.supportedVLR_CAMEL_Phases != null;
        }
        

        public void setSupportedVLR_CAMEL_Phases (SupportedCamelPhases value) {
            this.supportedVLR_CAMEL_Phases = value;
        }
        
  
        
        public SupportedCamelPhases getSupportedSGSN_CAMEL_Phases () {
            return this.supportedSGSN_CAMEL_Phases;
        }

        
        public boolean isSupportedSGSN_CAMEL_PhasesPresent () {
            return this.supportedSGSN_CAMEL_Phases != null;
        }
        

        public void setSupportedSGSN_CAMEL_Phases (SupportedCamelPhases value) {
            this.supportedSGSN_CAMEL_Phases = value;
        }
        
  
        
        public ExtensionContainer getExtensionContainer () {
            return this.extensionContainer;
        }

        
        public boolean isExtensionContainerPresent () {
            return this.extensionContainer != null;
        }
        

        public void setExtensionContainer (ExtensionContainer value) {
            this.extensionContainer = value;
        }
        
  
        
        public OfferedCamel4CSIs getOfferedCamel4CSIsInVLR () {
            return this.offeredCamel4CSIsInVLR;
        }

        
        public boolean isOfferedCamel4CSIsInVLRPresent () {
            return this.offeredCamel4CSIsInVLR != null;
        }
        

        public void setOfferedCamel4CSIsInVLR (OfferedCamel4CSIs value) {
            this.offeredCamel4CSIsInVLR = value;
        }
        
  
        
        public OfferedCamel4CSIs getOfferedCamel4CSIsInSGSN () {
            return this.offeredCamel4CSIsInSGSN;
        }

        
        public boolean isOfferedCamel4CSIsInSGSNPresent () {
            return this.offeredCamel4CSIsInSGSN != null;
        }
        

        public void setOfferedCamel4CSIsInSGSN (OfferedCamel4CSIs value) {
            this.offeredCamel4CSIsInSGSN = value;
        }
        
  
        
        public MSISDN_BS_List getMsisdn_BS_List () {
            return this.msisdn_BS_List;
        }

        
        public boolean isMsisdn_BS_ListPresent () {
            return this.msisdn_BS_List != null;
        }
        

        public void setMsisdn_BS_List (MSISDN_BS_List value) {
            this.msisdn_BS_List = value;
        }
        
  
        
        public CSG_SubscriptionDataList getCsg_SubscriptionDataList () {
            return this.csg_SubscriptionDataList;
        }

        
        public boolean isCsg_SubscriptionDataListPresent () {
            return this.csg_SubscriptionDataList != null;
        }
        

        public void setCsg_SubscriptionDataList (CSG_SubscriptionDataList value) {
            this.csg_SubscriptionDataList = value;
        }
        
  
                    
        
        public void initWithDefaults() {
            
        }

        private static IASN1PreparedElementData preparedData = CoderFactory.getInstance().newPreparedElementData(AnyTimeSubscriptionInterrogationRes.class);
        public IASN1PreparedElementData getPreparedData() {
            return preparedData;
        }

            
    }
            