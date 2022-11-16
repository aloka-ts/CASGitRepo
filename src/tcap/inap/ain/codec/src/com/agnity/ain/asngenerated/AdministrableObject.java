
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
    @ASN1Choice ( name = "AdministrableObject" )
    public class AdministrableObject implements IASN1PreparedElement {
            
        @ASN1Element ( name = "triggerItemAssignment", isOptional =  false , hasTag =  false  , hasDefaultValue =  false  )
    
	private TriggerItemAssignment triggerItemAssignment = null;
                
  
        @ASN1Element ( name = "sSPUserResource", isOptional =  false , hasTag =  false  , hasDefaultValue =  false  )
    
	private SSPUserResource sSPUserResource = null;
                
  
        @ASN1Element ( name = "srhrGroup", isOptional =  false , hasTag =  false  , hasDefaultValue =  false  )
    
	private SrhrGroup srhrGroup = null;
                
  
        @ASN1Element ( name = "networkTestDesignator", isOptional =  false , hasTag =  false  , hasDefaultValue =  false  )
    
	private NetworkTestDesignator networkTestDesignator = null;
                
  
        @ASN1Element ( name = "operationsMonitoringAssignment", isOptional =  false , hasTag =  false  , hasDefaultValue =  false  )
    
	private OperationsMonitoringAssignment operationsMonitoringAssignment = null;
                
  
        
        public TriggerItemAssignment getTriggerItemAssignment () {
            return this.triggerItemAssignment;
        }

        public boolean isTriggerItemAssignmentSelected () {
            return this.triggerItemAssignment != null;
        }

        private void setTriggerItemAssignment (TriggerItemAssignment value) {
            this.triggerItemAssignment = value;
        }

        
        public void selectTriggerItemAssignment (TriggerItemAssignment value) {
            this.triggerItemAssignment = value;
            
                    setSSPUserResource(null);
                
                    setSrhrGroup(null);
                
                    setNetworkTestDesignator(null);
                
                    setOperationsMonitoringAssignment(null);
                            
        }

        
  
        
        public SSPUserResource getSSPUserResource () {
            return this.sSPUserResource;
        }

        public boolean isSSPUserResourceSelected () {
            return this.sSPUserResource != null;
        }

        private void setSSPUserResource (SSPUserResource value) {
            this.sSPUserResource = value;
        }

        
        public void selectSSPUserResource (SSPUserResource value) {
            this.sSPUserResource = value;
            
                    setTriggerItemAssignment(null);
                
                    setSrhrGroup(null);
                
                    setNetworkTestDesignator(null);
                
                    setOperationsMonitoringAssignment(null);
                            
        }

        
  
        
        public SrhrGroup getSrhrGroup () {
            return this.srhrGroup;
        }

        public boolean isSrhrGroupSelected () {
            return this.srhrGroup != null;
        }

        private void setSrhrGroup (SrhrGroup value) {
            this.srhrGroup = value;
        }

        
        public void selectSrhrGroup (SrhrGroup value) {
            this.srhrGroup = value;
            
                    setTriggerItemAssignment(null);
                
                    setSSPUserResource(null);
                
                    setNetworkTestDesignator(null);
                
                    setOperationsMonitoringAssignment(null);
                            
        }

        
  
        
        public NetworkTestDesignator getNetworkTestDesignator () {
            return this.networkTestDesignator;
        }

        public boolean isNetworkTestDesignatorSelected () {
            return this.networkTestDesignator != null;
        }

        private void setNetworkTestDesignator (NetworkTestDesignator value) {
            this.networkTestDesignator = value;
        }

        
        public void selectNetworkTestDesignator (NetworkTestDesignator value) {
            this.networkTestDesignator = value;
            
                    setTriggerItemAssignment(null);
                
                    setSSPUserResource(null);
                
                    setSrhrGroup(null);
                
                    setOperationsMonitoringAssignment(null);
                            
        }

        
  
        
        public OperationsMonitoringAssignment getOperationsMonitoringAssignment () {
            return this.operationsMonitoringAssignment;
        }

        public boolean isOperationsMonitoringAssignmentSelected () {
            return this.operationsMonitoringAssignment != null;
        }

        private void setOperationsMonitoringAssignment (OperationsMonitoringAssignment value) {
            this.operationsMonitoringAssignment = value;
        }

        
        public void selectOperationsMonitoringAssignment (OperationsMonitoringAssignment value) {
            this.operationsMonitoringAssignment = value;
            
                    setTriggerItemAssignment(null);
                
                    setSSPUserResource(null);
                
                    setSrhrGroup(null);
                
                    setNetworkTestDesignator(null);
                            
        }

        
  

	    public void initWithDefaults() {
	    }

        private static IASN1PreparedElementData preparedData = CoderFactory.getInstance().newPreparedElementData(AdministrableObject.class);
        public IASN1PreparedElementData getPreparedData() {
            return preparedData;
        }


    }
            