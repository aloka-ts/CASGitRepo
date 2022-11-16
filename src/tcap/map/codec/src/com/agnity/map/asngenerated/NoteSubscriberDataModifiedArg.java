
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
    @ASN1Sequence ( name = "NoteSubscriberDataModifiedArg", isSet = false )
    public class NoteSubscriberDataModifiedArg implements IASN1PreparedElement {
            
        @ASN1Element ( name = "imsi", isOptional =  false , hasTag =  false  , hasDefaultValue =  false  )
    
	private IMSI imsi = null;
                
  
        @ASN1Element ( name = "msisdn", isOptional =  false , hasTag =  false  , hasDefaultValue =  false  )
    
	private ISDN_AddressString msisdn = null;
                
  
        @ASN1Element ( name = "forwardingInfoFor-CSE", isOptional =  true , hasTag =  true, tag = 0 , hasDefaultValue =  false  )
    
	private Ext_ForwardingInfoFor_CSE forwardingInfoFor_CSE = null;
                
  
        @ASN1Element ( name = "callBarringInfoFor-CSE", isOptional =  true , hasTag =  true, tag = 1 , hasDefaultValue =  false  )
    
	private Ext_CallBarringInfoFor_CSE callBarringInfoFor_CSE = null;
                
  
        @ASN1Element ( name = "odb-Info", isOptional =  true , hasTag =  true, tag = 2 , hasDefaultValue =  false  )
    
	private ODB_Info odb_Info = null;
                
  
        @ASN1Element ( name = "camel-SubscriptionInfo", isOptional =  true , hasTag =  true, tag = 3 , hasDefaultValue =  false  )
    
	private CAMEL_SubscriptionInfo camel_SubscriptionInfo = null;
                
  
        @ASN1Null ( name = "allInformationSent" ) 
    
        @ASN1Element ( name = "allInformationSent", isOptional =  true , hasTag =  true, tag = 4 , hasDefaultValue =  false  )
    
	private org.bn.types.NullObject allInformationSent = null;
                
  
        @ASN1Element ( name = "extensionContainer", isOptional =  true , hasTag =  false  , hasDefaultValue =  false  )
    
	private ExtensionContainer extensionContainer = null;
                
  
        @ASN1Element ( name = "ue-reachable", isOptional =  true , hasTag =  true, tag = 5 , hasDefaultValue =  false  )
    
	private ServingNode ue_reachable = null;
                
  
        @ASN1Element ( name = "csg-SubscriptionDataList", isOptional =  true , hasTag =  true, tag = 6 , hasDefaultValue =  false  )
    
	private CSG_SubscriptionDataList csg_SubscriptionDataList = null;
                
  
        
        public IMSI getImsi () {
            return this.imsi;
        }

        

        public void setImsi (IMSI value) {
            this.imsi = value;
        }
        
  
        
        public ISDN_AddressString getMsisdn () {
            return this.msisdn;
        }

        

        public void setMsisdn (ISDN_AddressString value) {
            this.msisdn = value;
        }
        
  
        
        public Ext_ForwardingInfoFor_CSE getForwardingInfoFor_CSE () {
            return this.forwardingInfoFor_CSE;
        }

        
        public boolean isForwardingInfoFor_CSEPresent () {
            return this.forwardingInfoFor_CSE != null;
        }
        

        public void setForwardingInfoFor_CSE (Ext_ForwardingInfoFor_CSE value) {
            this.forwardingInfoFor_CSE = value;
        }
        
  
        
        public Ext_CallBarringInfoFor_CSE getCallBarringInfoFor_CSE () {
            return this.callBarringInfoFor_CSE;
        }

        
        public boolean isCallBarringInfoFor_CSEPresent () {
            return this.callBarringInfoFor_CSE != null;
        }
        

        public void setCallBarringInfoFor_CSE (Ext_CallBarringInfoFor_CSE value) {
            this.callBarringInfoFor_CSE = value;
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
        
  
        
        public ExtensionContainer getExtensionContainer () {
            return this.extensionContainer;
        }

        
        public boolean isExtensionContainerPresent () {
            return this.extensionContainer != null;
        }
        

        public void setExtensionContainer (ExtensionContainer value) {
            this.extensionContainer = value;
        }
        
  
        
        public ServingNode getUe_reachable () {
            return this.ue_reachable;
        }

        
        public boolean isUe_reachablePresent () {
            return this.ue_reachable != null;
        }
        

        public void setUe_reachable (ServingNode value) {
            this.ue_reachable = value;
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

        private static IASN1PreparedElementData preparedData = CoderFactory.getInstance().newPreparedElementData(NoteSubscriberDataModifiedArg.class);
        public IASN1PreparedElementData getPreparedData() {
            return preparedData;
        }

            
    }
            