
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
    @ASN1Sequence ( name = "CAMEL_SubscriptionInfo", isSet = false )
    public class CAMEL_SubscriptionInfo implements IASN1PreparedElement {
            
        @ASN1Element ( name = "o-CSI", isOptional =  true , hasTag =  true, tag = 0 , hasDefaultValue =  false  )
    
	private O_CSI o_CSI = null;
                
  
        @ASN1Element ( name = "o-BcsmCamelTDP-CriteriaList", isOptional =  true , hasTag =  true, tag = 1 , hasDefaultValue =  false  )
    
	private O_BcsmCamelTDPCriteriaList o_BcsmCamelTDP_CriteriaList = null;
                
  
        @ASN1Element ( name = "d-CSI", isOptional =  true , hasTag =  true, tag = 2 , hasDefaultValue =  false  )
    
	private D_CSI d_CSI = null;
                
  
        @ASN1Element ( name = "t-CSI", isOptional =  true , hasTag =  true, tag = 3 , hasDefaultValue =  false  )
    
	private T_CSI t_CSI = null;
                
  
        @ASN1Element ( name = "t-BCSM-CAMEL-TDP-CriteriaList", isOptional =  true , hasTag =  true, tag = 4 , hasDefaultValue =  false  )
    
	private T_BCSM_CAMEL_TDP_CriteriaList t_BCSM_CAMEL_TDP_CriteriaList = null;
                
  
        @ASN1Element ( name = "vt-CSI", isOptional =  true , hasTag =  true, tag = 5 , hasDefaultValue =  false  )
    
	private T_CSI vt_CSI = null;
                
  
        @ASN1Element ( name = "vt-BCSM-CAMEL-TDP-CriteriaList", isOptional =  true , hasTag =  true, tag = 6 , hasDefaultValue =  false  )
    
	private T_BCSM_CAMEL_TDP_CriteriaList vt_BCSM_CAMEL_TDP_CriteriaList = null;
                
  
        @ASN1Null ( name = "tif-CSI" ) 
    
        @ASN1Element ( name = "tif-CSI", isOptional =  true , hasTag =  true, tag = 7 , hasDefaultValue =  false  )
    
	private org.bn.types.NullObject tif_CSI = null;
                
  
        @ASN1Null ( name = "tif-CSI-NotificationToCSE" ) 
    
        @ASN1Element ( name = "tif-CSI-NotificationToCSE", isOptional =  true , hasTag =  true, tag = 8 , hasDefaultValue =  false  )
    
	private org.bn.types.NullObject tif_CSI_NotificationToCSE = null;
                
  
        @ASN1Element ( name = "gprs-CSI", isOptional =  true , hasTag =  true, tag = 9 , hasDefaultValue =  false  )
    
	private GPRS_CSI gprs_CSI = null;
                
  
        @ASN1Element ( name = "mo-sms-CSI", isOptional =  true , hasTag =  true, tag = 10 , hasDefaultValue =  false  )
    
	private SMS_CSI mo_sms_CSI = null;
                
  
        @ASN1Element ( name = "ss-CSI", isOptional =  true , hasTag =  true, tag = 11 , hasDefaultValue =  false  )
    
	private SS_CSI ss_CSI = null;
                
  
        @ASN1Element ( name = "m-CSI", isOptional =  true , hasTag =  true, tag = 12 , hasDefaultValue =  false  )
    
	private M_CSI m_CSI = null;
                
  
        @ASN1Element ( name = "extensionContainer", isOptional =  true , hasTag =  true, tag = 13 , hasDefaultValue =  false  )
    
	private ExtensionContainer extensionContainer = null;
                
  
        @ASN1Element ( name = "specificCSIDeletedList", isOptional =  true , hasTag =  true, tag = 14 , hasDefaultValue =  false  )
    
	private SpecificCSI_Withdraw specificCSIDeletedList = null;
                
  
        @ASN1Element ( name = "mt-sms-CSI", isOptional =  true , hasTag =  true, tag = 15 , hasDefaultValue =  false  )
    
	private SMS_CSI mt_sms_CSI = null;
                
  
        @ASN1Element ( name = "mt-smsCAMELTDP-CriteriaList", isOptional =  true , hasTag =  true, tag = 16 , hasDefaultValue =  false  )
    
	private MT_smsCAMELTDP_CriteriaList mt_smsCAMELTDP_CriteriaList = null;
                
  
        @ASN1Element ( name = "mg-csi", isOptional =  true , hasTag =  true, tag = 17 , hasDefaultValue =  false  )
    
	private MG_CSI mg_csi = null;
                
  
        @ASN1Element ( name = "o-IM-CSI", isOptional =  true , hasTag =  true, tag = 18 , hasDefaultValue =  false  )
    
	private O_CSI o_IM_CSI = null;
                
  
        @ASN1Element ( name = "o-IM-BcsmCamelTDP-CriteriaList", isOptional =  true , hasTag =  true, tag = 19 , hasDefaultValue =  false  )
    
	private O_BcsmCamelTDPCriteriaList o_IM_BcsmCamelTDP_CriteriaList = null;
                
  
        @ASN1Element ( name = "d-IM-CSI", isOptional =  true , hasTag =  true, tag = 20 , hasDefaultValue =  false  )
    
	private D_CSI d_IM_CSI = null;
                
  
        @ASN1Element ( name = "vt-IM-CSI", isOptional =  true , hasTag =  true, tag = 21 , hasDefaultValue =  false  )
    
	private T_CSI vt_IM_CSI = null;
                
  
        @ASN1Element ( name = "vt-IM-BCSM-CAMEL-TDP-CriteriaList", isOptional =  true , hasTag =  true, tag = 22 , hasDefaultValue =  false  )
    
	private T_BCSM_CAMEL_TDP_CriteriaList vt_IM_BCSM_CAMEL_TDP_CriteriaList = null;
                
  
        
        public O_CSI getO_CSI () {
            return this.o_CSI;
        }

        
        public boolean isO_CSIPresent () {
            return this.o_CSI != null;
        }
        

        public void setO_CSI (O_CSI value) {
            this.o_CSI = value;
        }
        
  
        
        public O_BcsmCamelTDPCriteriaList getO_BcsmCamelTDP_CriteriaList () {
            return this.o_BcsmCamelTDP_CriteriaList;
        }

        
        public boolean isO_BcsmCamelTDP_CriteriaListPresent () {
            return this.o_BcsmCamelTDP_CriteriaList != null;
        }
        

        public void setO_BcsmCamelTDP_CriteriaList (O_BcsmCamelTDPCriteriaList value) {
            this.o_BcsmCamelTDP_CriteriaList = value;
        }
        
  
        
        public D_CSI getD_CSI () {
            return this.d_CSI;
        }

        
        public boolean isD_CSIPresent () {
            return this.d_CSI != null;
        }
        

        public void setD_CSI (D_CSI value) {
            this.d_CSI = value;
        }
        
  
        
        public T_CSI getT_CSI () {
            return this.t_CSI;
        }

        
        public boolean isT_CSIPresent () {
            return this.t_CSI != null;
        }
        

        public void setT_CSI (T_CSI value) {
            this.t_CSI = value;
        }
        
  
        
        public T_BCSM_CAMEL_TDP_CriteriaList getT_BCSM_CAMEL_TDP_CriteriaList () {
            return this.t_BCSM_CAMEL_TDP_CriteriaList;
        }

        
        public boolean isT_BCSM_CAMEL_TDP_CriteriaListPresent () {
            return this.t_BCSM_CAMEL_TDP_CriteriaList != null;
        }
        

        public void setT_BCSM_CAMEL_TDP_CriteriaList (T_BCSM_CAMEL_TDP_CriteriaList value) {
            this.t_BCSM_CAMEL_TDP_CriteriaList = value;
        }
        
  
        
        public T_CSI getVt_CSI () {
            return this.vt_CSI;
        }

        
        public boolean isVt_CSIPresent () {
            return this.vt_CSI != null;
        }
        

        public void setVt_CSI (T_CSI value) {
            this.vt_CSI = value;
        }
        
  
        
        public T_BCSM_CAMEL_TDP_CriteriaList getVt_BCSM_CAMEL_TDP_CriteriaList () {
            return this.vt_BCSM_CAMEL_TDP_CriteriaList;
        }

        
        public boolean isVt_BCSM_CAMEL_TDP_CriteriaListPresent () {
            return this.vt_BCSM_CAMEL_TDP_CriteriaList != null;
        }
        

        public void setVt_BCSM_CAMEL_TDP_CriteriaList (T_BCSM_CAMEL_TDP_CriteriaList value) {
            this.vt_BCSM_CAMEL_TDP_CriteriaList = value;
        }
        
  
        
        public GPRS_CSI getGprs_CSI () {
            return this.gprs_CSI;
        }

        
        public boolean isGprs_CSIPresent () {
            return this.gprs_CSI != null;
        }
        

        public void setGprs_CSI (GPRS_CSI value) {
            this.gprs_CSI = value;
        }
        
  
        
        public SMS_CSI getMo_sms_CSI () {
            return this.mo_sms_CSI;
        }

        
        public boolean isMo_sms_CSIPresent () {
            return this.mo_sms_CSI != null;
        }
        

        public void setMo_sms_CSI (SMS_CSI value) {
            this.mo_sms_CSI = value;
        }
        
  
        
        public SS_CSI getSs_CSI () {
            return this.ss_CSI;
        }

        
        public boolean isSs_CSIPresent () {
            return this.ss_CSI != null;
        }
        

        public void setSs_CSI (SS_CSI value) {
            this.ss_CSI = value;
        }
        
  
        
        public M_CSI getM_CSI () {
            return this.m_CSI;
        }

        
        public boolean isM_CSIPresent () {
            return this.m_CSI != null;
        }
        

        public void setM_CSI (M_CSI value) {
            this.m_CSI = value;
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
        
  
        
        public SpecificCSI_Withdraw getSpecificCSIDeletedList () {
            return this.specificCSIDeletedList;
        }

        
        public boolean isSpecificCSIDeletedListPresent () {
            return this.specificCSIDeletedList != null;
        }
        

        public void setSpecificCSIDeletedList (SpecificCSI_Withdraw value) {
            this.specificCSIDeletedList = value;
        }
        
  
        
        public SMS_CSI getMt_sms_CSI () {
            return this.mt_sms_CSI;
        }

        
        public boolean isMt_sms_CSIPresent () {
            return this.mt_sms_CSI != null;
        }
        

        public void setMt_sms_CSI (SMS_CSI value) {
            this.mt_sms_CSI = value;
        }
        
  
        
        public MT_smsCAMELTDP_CriteriaList getMt_smsCAMELTDP_CriteriaList () {
            return this.mt_smsCAMELTDP_CriteriaList;
        }

        
        public boolean isMt_smsCAMELTDP_CriteriaListPresent () {
            return this.mt_smsCAMELTDP_CriteriaList != null;
        }
        

        public void setMt_smsCAMELTDP_CriteriaList (MT_smsCAMELTDP_CriteriaList value) {
            this.mt_smsCAMELTDP_CriteriaList = value;
        }
        
  
        
        public MG_CSI getMg_csi () {
            return this.mg_csi;
        }

        
        public boolean isMg_csiPresent () {
            return this.mg_csi != null;
        }
        

        public void setMg_csi (MG_CSI value) {
            this.mg_csi = value;
        }
        
  
        
        public O_CSI getO_IM_CSI () {
            return this.o_IM_CSI;
        }

        
        public boolean isO_IM_CSIPresent () {
            return this.o_IM_CSI != null;
        }
        

        public void setO_IM_CSI (O_CSI value) {
            this.o_IM_CSI = value;
        }
        
  
        
        public O_BcsmCamelTDPCriteriaList getO_IM_BcsmCamelTDP_CriteriaList () {
            return this.o_IM_BcsmCamelTDP_CriteriaList;
        }

        
        public boolean isO_IM_BcsmCamelTDP_CriteriaListPresent () {
            return this.o_IM_BcsmCamelTDP_CriteriaList != null;
        }
        

        public void setO_IM_BcsmCamelTDP_CriteriaList (O_BcsmCamelTDPCriteriaList value) {
            this.o_IM_BcsmCamelTDP_CriteriaList = value;
        }
        
  
        
        public D_CSI getD_IM_CSI () {
            return this.d_IM_CSI;
        }

        
        public boolean isD_IM_CSIPresent () {
            return this.d_IM_CSI != null;
        }
        

        public void setD_IM_CSI (D_CSI value) {
            this.d_IM_CSI = value;
        }
        
  
        
        public T_CSI getVt_IM_CSI () {
            return this.vt_IM_CSI;
        }

        
        public boolean isVt_IM_CSIPresent () {
            return this.vt_IM_CSI != null;
        }
        

        public void setVt_IM_CSI (T_CSI value) {
            this.vt_IM_CSI = value;
        }
        
  
        
        public T_BCSM_CAMEL_TDP_CriteriaList getVt_IM_BCSM_CAMEL_TDP_CriteriaList () {
            return this.vt_IM_BCSM_CAMEL_TDP_CriteriaList;
        }

        
        public boolean isVt_IM_BCSM_CAMEL_TDP_CriteriaListPresent () {
            return this.vt_IM_BCSM_CAMEL_TDP_CriteriaList != null;
        }
        

        public void setVt_IM_BCSM_CAMEL_TDP_CriteriaList (T_BCSM_CAMEL_TDP_CriteriaList value) {
            this.vt_IM_BCSM_CAMEL_TDP_CriteriaList = value;
        }
        
  
                    
        
        public void initWithDefaults() {
            
        }

        private static IASN1PreparedElementData preparedData = CoderFactory.getInstance().newPreparedElementData(CAMEL_SubscriptionInfo.class);
        public IASN1PreparedElementData getPreparedData() {
            return preparedData;
        }

            
    }
            