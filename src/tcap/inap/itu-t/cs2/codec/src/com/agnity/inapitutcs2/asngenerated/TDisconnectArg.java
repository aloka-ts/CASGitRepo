
package com.agnity.inapitutcs2.asngenerated;
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
    @ASN1Sequence ( name = "TDisconnectArg", isSet = false )
    public class TDisconnectArg implements IASN1PreparedElement {
            
        @ASN1Element ( name = "dpSpecificCommonParameters", isOptional =  false , hasTag =  true, tag = 0 , hasDefaultValue =  false  )
    
	private DpSpecificCommonParameters dpSpecificCommonParameters = null;
                
  
        @ASN1Element ( name = "calledPartyBusinessGroupID", isOptional =  true , hasTag =  true, tag = 1 , hasDefaultValue =  false  )
    
	private CalledPartyBusinessGroupID calledPartyBusinessGroupID = null;
                
  
        @ASN1Element ( name = "calledPartySubaddress", isOptional =  true , hasTag =  true, tag = 2 , hasDefaultValue =  false  )
    
	private CalledPartySubaddress calledPartySubaddress = null;
                
  
        @ASN1Element ( name = "calledFacilityGroup", isOptional =  true , hasTag =  true, tag = 3 , hasDefaultValue =  false  )
    
	private FacilityGroup calledFacilityGroup = null;
                
  
        @ASN1Element ( name = "calledFacilityGroupMember", isOptional =  true , hasTag =  true, tag = 4 , hasDefaultValue =  false  )
    
	private FacilityGroupMember calledFacilityGroupMember = null;
                
  
        @ASN1Element ( name = "releaseCause", isOptional =  true , hasTag =  true, tag = 5 , hasDefaultValue =  false  )
    
	private Cause releaseCause = null;
                
  
@ASN1SequenceOf( name = "extensions", isSetOf = false ) 

    @ASN1ValueRangeConstraint ( 
		
		min = 1L, 
		
		max = 5L 
		
	   )
	   
        @ASN1Element ( name = "extensions", isOptional =  true , hasTag =  true, tag = 6 , hasDefaultValue =  false  )
    
	private java.util.Collection<ExtensionField>  extensions = null;
                
  
        @ASN1Element ( name = "connectTime", isOptional =  true , hasTag =  true, tag = 7 , hasDefaultValue =  false  )
    
	private Integer4 connectTime = null;
                
  
        @ASN1Element ( name = "componentType", isOptional =  true , hasTag =  true, tag = 8 , hasDefaultValue =  false  )
    
	private ComponentType componentType = null;
                
  
        @ASN1Element ( name = "component", isOptional =  true , hasTag =  true, tag = 9 , hasDefaultValue =  false  )
    
	private Component component = null;
                
  
        @ASN1Element ( name = "componentCorrelationID", isOptional =  true , hasTag =  true, tag = 10 , hasDefaultValue =  false  )
    
	private ComponentCorrelationID componentCorrelationID = null;
                
  
        
        public DpSpecificCommonParameters getDpSpecificCommonParameters () {
            return this.dpSpecificCommonParameters;
        }

        

        public void setDpSpecificCommonParameters (DpSpecificCommonParameters value) {
            this.dpSpecificCommonParameters = value;
        }
        
  
        
        public CalledPartyBusinessGroupID getCalledPartyBusinessGroupID () {
            return this.calledPartyBusinessGroupID;
        }

        
        public boolean isCalledPartyBusinessGroupIDPresent () {
            return this.calledPartyBusinessGroupID != null;
        }
        

        public void setCalledPartyBusinessGroupID (CalledPartyBusinessGroupID value) {
            this.calledPartyBusinessGroupID = value;
        }
        
  
        
        public CalledPartySubaddress getCalledPartySubaddress () {
            return this.calledPartySubaddress;
        }

        
        public boolean isCalledPartySubaddressPresent () {
            return this.calledPartySubaddress != null;
        }
        

        public void setCalledPartySubaddress (CalledPartySubaddress value) {
            this.calledPartySubaddress = value;
        }
        
  
        
        public FacilityGroup getCalledFacilityGroup () {
            return this.calledFacilityGroup;
        }

        
        public boolean isCalledFacilityGroupPresent () {
            return this.calledFacilityGroup != null;
        }
        

        public void setCalledFacilityGroup (FacilityGroup value) {
            this.calledFacilityGroup = value;
        }
        
  
        
        public FacilityGroupMember getCalledFacilityGroupMember () {
            return this.calledFacilityGroupMember;
        }

        
        public boolean isCalledFacilityGroupMemberPresent () {
            return this.calledFacilityGroupMember != null;
        }
        

        public void setCalledFacilityGroupMember (FacilityGroupMember value) {
            this.calledFacilityGroupMember = value;
        }
        
  
        
        public Cause getReleaseCause () {
            return this.releaseCause;
        }

        
        public boolean isReleaseCausePresent () {
            return this.releaseCause != null;
        }
        

        public void setReleaseCause (Cause value) {
            this.releaseCause = value;
        }
        
  
        
        public java.util.Collection<ExtensionField>  getExtensions () {
            return this.extensions;
        }

        
        public boolean isExtensionsPresent () {
            return this.extensions != null;
        }
        

        public void setExtensions (java.util.Collection<ExtensionField>  value) {
            this.extensions = value;
        }
        
  
        
        public Integer4 getConnectTime () {
            return this.connectTime;
        }

        
        public boolean isConnectTimePresent () {
            return this.connectTime != null;
        }
        

        public void setConnectTime (Integer4 value) {
            this.connectTime = value;
        }
        
  
        
        public ComponentType getComponentType () {
            return this.componentType;
        }

        
        public boolean isComponentTypePresent () {
            return this.componentType != null;
        }
        

        public void setComponentType (ComponentType value) {
            this.componentType = value;
        }
        
  
        
        public Component getComponent () {
            return this.component;
        }

        
        public boolean isComponentPresent () {
            return this.component != null;
        }
        

        public void setComponent (Component value) {
            this.component = value;
        }
        
  
        
        public ComponentCorrelationID getComponentCorrelationID () {
            return this.componentCorrelationID;
        }

        
        public boolean isComponentCorrelationIDPresent () {
            return this.componentCorrelationID != null;
        }
        

        public void setComponentCorrelationID (ComponentCorrelationID value) {
            this.componentCorrelationID = value;
        }
        
  
                    
        
        public void initWithDefaults() {
            
        }

        private static IASN1PreparedElementData preparedData = CoderFactory.getInstance().newPreparedElementData(TDisconnectArg.class);
        public IASN1PreparedElementData getPreparedData() {
            return preparedData;
        }

            
    }
            