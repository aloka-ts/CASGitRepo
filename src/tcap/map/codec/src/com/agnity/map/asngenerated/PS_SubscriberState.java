
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
    @ASN1Choice ( name = "PS_SubscriberState" )
    public class PS_SubscriberState implements IASN1PreparedElement {
            
        @ASN1Null ( name = "notProvidedFromSGSNorMME" ) 
    
        @ASN1Element ( name = "notProvidedFromSGSNorMME", isOptional =  false , hasTag =  true, tag = 0 , hasDefaultValue =  false  )
    
	private org.bn.types.NullObject notProvidedFromSGSNorMME = null;
                
  
        @ASN1Null ( name = "ps-Detached" ) 
    
        @ASN1Element ( name = "ps-Detached", isOptional =  false , hasTag =  true, tag = 1 , hasDefaultValue =  false  )
    
	private org.bn.types.NullObject ps_Detached = null;
                
  
        @ASN1Null ( name = "ps-AttachedNotReachableForPaging" ) 
    
        @ASN1Element ( name = "ps-AttachedNotReachableForPaging", isOptional =  false , hasTag =  true, tag = 2 , hasDefaultValue =  false  )
    
	private org.bn.types.NullObject ps_AttachedNotReachableForPaging = null;
                
  
        @ASN1Null ( name = "ps-AttachedReachableForPaging" ) 
    
        @ASN1Element ( name = "ps-AttachedReachableForPaging", isOptional =  false , hasTag =  true, tag = 3 , hasDefaultValue =  false  )
    
	private org.bn.types.NullObject ps_AttachedReachableForPaging = null;
                
  
        @ASN1Element ( name = "ps-PDP-ActiveNotReachableForPaging", isOptional =  false , hasTag =  true, tag = 4 , hasDefaultValue =  false  )
    
	private PDP_ContextInfoList ps_PDP_ActiveNotReachableForPaging = null;
                
  
        @ASN1Element ( name = "ps-PDP-ActiveReachableForPaging", isOptional =  false , hasTag =  true, tag = 5 , hasDefaultValue =  false  )
    
	private PDP_ContextInfoList ps_PDP_ActiveReachableForPaging = null;
                
  
        @ASN1Element ( name = "netDetNotReachable", isOptional =  false , hasTag =  false  , hasDefaultValue =  false  )
    
	private NotReachableReason netDetNotReachable = null;
                
  
        
        public org.bn.types.NullObject getNotProvidedFromSGSNorMME () {
            return this.notProvidedFromSGSNorMME;
        }

        public boolean isNotProvidedFromSGSNorMMESelected () {
            return this.notProvidedFromSGSNorMME != null;
        }

        private void setNotProvidedFromSGSNorMME (org.bn.types.NullObject value) {
            this.notProvidedFromSGSNorMME = value;
        }

        
        public void selectNotProvidedFromSGSNorMME () {
            selectNotProvidedFromSGSNorMME (new org.bn.types.NullObject());
	}
	
        public void selectNotProvidedFromSGSNorMME (org.bn.types.NullObject value) {
            this.notProvidedFromSGSNorMME = value;
            
                    setPs_Detached(null);
                
                    setPs_AttachedNotReachableForPaging(null);
                
                    setPs_AttachedReachableForPaging(null);
                
                    setPs_PDP_ActiveNotReachableForPaging(null);
                
                    setPs_PDP_ActiveReachableForPaging(null);
                
                    setNetDetNotReachable(null);
                            
        }

        
  
        
        public org.bn.types.NullObject getPs_Detached () {
            return this.ps_Detached;
        }

        public boolean isPs_DetachedSelected () {
            return this.ps_Detached != null;
        }

        private void setPs_Detached (org.bn.types.NullObject value) {
            this.ps_Detached = value;
        }

        
        public void selectPs_Detached () {
            selectPs_Detached (new org.bn.types.NullObject());
	}
	
        public void selectPs_Detached (org.bn.types.NullObject value) {
            this.ps_Detached = value;
            
                    setNotProvidedFromSGSNorMME(null);
                
                    setPs_AttachedNotReachableForPaging(null);
                
                    setPs_AttachedReachableForPaging(null);
                
                    setPs_PDP_ActiveNotReachableForPaging(null);
                
                    setPs_PDP_ActiveReachableForPaging(null);
                
                    setNetDetNotReachable(null);
                            
        }

        
  
        
        public org.bn.types.NullObject getPs_AttachedNotReachableForPaging () {
            return this.ps_AttachedNotReachableForPaging;
        }

        public boolean isPs_AttachedNotReachableForPagingSelected () {
            return this.ps_AttachedNotReachableForPaging != null;
        }

        private void setPs_AttachedNotReachableForPaging (org.bn.types.NullObject value) {
            this.ps_AttachedNotReachableForPaging = value;
        }

        
        public void selectPs_AttachedNotReachableForPaging () {
            selectPs_AttachedNotReachableForPaging (new org.bn.types.NullObject());
	}
	
        public void selectPs_AttachedNotReachableForPaging (org.bn.types.NullObject value) {
            this.ps_AttachedNotReachableForPaging = value;
            
                    setNotProvidedFromSGSNorMME(null);
                
                    setPs_Detached(null);
                
                    setPs_AttachedReachableForPaging(null);
                
                    setPs_PDP_ActiveNotReachableForPaging(null);
                
                    setPs_PDP_ActiveReachableForPaging(null);
                
                    setNetDetNotReachable(null);
                            
        }

        
  
        
        public org.bn.types.NullObject getPs_AttachedReachableForPaging () {
            return this.ps_AttachedReachableForPaging;
        }

        public boolean isPs_AttachedReachableForPagingSelected () {
            return this.ps_AttachedReachableForPaging != null;
        }

        private void setPs_AttachedReachableForPaging (org.bn.types.NullObject value) {
            this.ps_AttachedReachableForPaging = value;
        }

        
        public void selectPs_AttachedReachableForPaging () {
            selectPs_AttachedReachableForPaging (new org.bn.types.NullObject());
	}
	
        public void selectPs_AttachedReachableForPaging (org.bn.types.NullObject value) {
            this.ps_AttachedReachableForPaging = value;
            
                    setNotProvidedFromSGSNorMME(null);
                
                    setPs_Detached(null);
                
                    setPs_AttachedNotReachableForPaging(null);
                
                    setPs_PDP_ActiveNotReachableForPaging(null);
                
                    setPs_PDP_ActiveReachableForPaging(null);
                
                    setNetDetNotReachable(null);
                            
        }

        
  
        
        public PDP_ContextInfoList getPs_PDP_ActiveNotReachableForPaging () {
            return this.ps_PDP_ActiveNotReachableForPaging;
        }

        public boolean isPs_PDP_ActiveNotReachableForPagingSelected () {
            return this.ps_PDP_ActiveNotReachableForPaging != null;
        }

        private void setPs_PDP_ActiveNotReachableForPaging (PDP_ContextInfoList value) {
            this.ps_PDP_ActiveNotReachableForPaging = value;
        }

        
        public void selectPs_PDP_ActiveNotReachableForPaging (PDP_ContextInfoList value) {
            this.ps_PDP_ActiveNotReachableForPaging = value;
            
                    setNotProvidedFromSGSNorMME(null);
                
                    setPs_Detached(null);
                
                    setPs_AttachedNotReachableForPaging(null);
                
                    setPs_AttachedReachableForPaging(null);
                
                    setPs_PDP_ActiveReachableForPaging(null);
                
                    setNetDetNotReachable(null);
                            
        }

        
  
        
        public PDP_ContextInfoList getPs_PDP_ActiveReachableForPaging () {
            return this.ps_PDP_ActiveReachableForPaging;
        }

        public boolean isPs_PDP_ActiveReachableForPagingSelected () {
            return this.ps_PDP_ActiveReachableForPaging != null;
        }

        private void setPs_PDP_ActiveReachableForPaging (PDP_ContextInfoList value) {
            this.ps_PDP_ActiveReachableForPaging = value;
        }

        
        public void selectPs_PDP_ActiveReachableForPaging (PDP_ContextInfoList value) {
            this.ps_PDP_ActiveReachableForPaging = value;
            
                    setNotProvidedFromSGSNorMME(null);
                
                    setPs_Detached(null);
                
                    setPs_AttachedNotReachableForPaging(null);
                
                    setPs_AttachedReachableForPaging(null);
                
                    setPs_PDP_ActiveNotReachableForPaging(null);
                
                    setNetDetNotReachable(null);
                            
        }

        
  
        
        public NotReachableReason getNetDetNotReachable () {
            return this.netDetNotReachable;
        }

        public boolean isNetDetNotReachableSelected () {
            return this.netDetNotReachable != null;
        }

        private void setNetDetNotReachable (NotReachableReason value) {
            this.netDetNotReachable = value;
        }

        
        public void selectNetDetNotReachable (NotReachableReason value) {
            this.netDetNotReachable = value;
            
                    setNotProvidedFromSGSNorMME(null);
                
                    setPs_Detached(null);
                
                    setPs_AttachedNotReachableForPaging(null);
                
                    setPs_AttachedReachableForPaging(null);
                
                    setPs_PDP_ActiveNotReachableForPaging(null);
                
                    setPs_PDP_ActiveReachableForPaging(null);
                            
        }

        
  

	    public void initWithDefaults() {
	    }

        private static IASN1PreparedElementData preparedData = CoderFactory.getInstance().newPreparedElementData(PS_SubscriberState.class);
        public IASN1PreparedElementData getPreparedData() {
            return preparedData;
        }


    }
            