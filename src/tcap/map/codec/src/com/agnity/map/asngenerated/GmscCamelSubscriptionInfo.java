
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
    @ASN1Sequence ( name = "GmscCamelSubscriptionInfo", isSet = false )
    public class GmscCamelSubscriptionInfo implements IASN1PreparedElement {
            
        @ASN1Element ( name = "t-CSI", isOptional =  true , hasTag =  true, tag = 0 , hasDefaultValue =  false  )
    
	private T_CSI t_CSI = null;
                
  
        @ASN1Element ( name = "o-CSI", isOptional =  true , hasTag =  true, tag = 1 , hasDefaultValue =  false  )
    
	private O_CSI o_CSI = null;
                
  
        @ASN1Element ( name = "extensionContainer", isOptional =  true , hasTag =  true, tag = 2 , hasDefaultValue =  false  )
    
	private ExtensionContainer extensionContainer = null;
                
  
        @ASN1Element ( name = "o-BcsmCamelTDP-CriteriaList", isOptional =  true , hasTag =  true, tag = 3 , hasDefaultValue =  false  )
    
	private O_BcsmCamelTDPCriteriaList o_BcsmCamelTDP_CriteriaList = null;
                
  
        @ASN1Element ( name = "t-BCSM-CAMEL-TDP-CriteriaList", isOptional =  true , hasTag =  true, tag = 4 , hasDefaultValue =  false  )
    
	private T_BCSM_CAMEL_TDP_CriteriaList t_BCSM_CAMEL_TDP_CriteriaList = null;
                
  
        @ASN1Element ( name = "d-csi", isOptional =  true , hasTag =  true, tag = 5 , hasDefaultValue =  false  )
    
	private D_CSI d_csi = null;
                
  
        
        public T_CSI getT_CSI () {
            return this.t_CSI;
        }

        
        public boolean isT_CSIPresent () {
            return this.t_CSI != null;
        }
        

        public void setT_CSI (T_CSI value) {
            this.t_CSI = value;
        }
        
  
        
        public O_CSI getO_CSI () {
            return this.o_CSI;
        }

        
        public boolean isO_CSIPresent () {
            return this.o_CSI != null;
        }
        

        public void setO_CSI (O_CSI value) {
            this.o_CSI = value;
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
        
  
        
        public O_BcsmCamelTDPCriteriaList getO_BcsmCamelTDP_CriteriaList () {
            return this.o_BcsmCamelTDP_CriteriaList;
        }

        
        public boolean isO_BcsmCamelTDP_CriteriaListPresent () {
            return this.o_BcsmCamelTDP_CriteriaList != null;
        }
        

        public void setO_BcsmCamelTDP_CriteriaList (O_BcsmCamelTDPCriteriaList value) {
            this.o_BcsmCamelTDP_CriteriaList = value;
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
        
  
        
        public D_CSI getD_csi () {
            return this.d_csi;
        }

        
        public boolean isD_csiPresent () {
            return this.d_csi != null;
        }
        

        public void setD_csi (D_CSI value) {
            this.d_csi = value;
        }
        
  
                    
        
        public void initWithDefaults() {
            
        }

        private static IASN1PreparedElementData preparedData = CoderFactory.getInstance().newPreparedElementData(GmscCamelSubscriptionInfo.class);
        public IASN1PreparedElementData getPreparedData() {
            return preparedData;
        }

            
    }
            