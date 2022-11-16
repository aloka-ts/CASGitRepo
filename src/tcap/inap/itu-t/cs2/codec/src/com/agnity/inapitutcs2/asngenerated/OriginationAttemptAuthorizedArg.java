
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
    @ASN1Sequence ( name = "OriginationAttemptAuthorizedArg", isSet = false )
    public class OriginationAttemptAuthorizedArg implements IASN1PreparedElement {
            
        @ASN1Element ( name = "dpSpecificCommonParameters", isOptional =  false , hasTag =  true, tag = 0 , hasDefaultValue =  false  )
    
	private DpSpecificCommonParameters dpSpecificCommonParameters = null;
                
  
        @ASN1Element ( name = "dialledDigits", isOptional =  true , hasTag =  true, tag = 1 , hasDefaultValue =  false  )
    
	private CalledPartyNumber dialledDigits = null;
                
  
        @ASN1Element ( name = "callingPartyBusinessGroupID", isOptional =  true , hasTag =  true, tag = 2 , hasDefaultValue =  false  )
    
	private CallingPartyBusinessGroupID callingPartyBusinessGroupID = null;
                
  
        @ASN1Element ( name = "callingPartySubaddress", isOptional =  true , hasTag =  true, tag = 3 , hasDefaultValue =  false  )
    
	private CallingPartySubaddress callingPartySubaddress = null;
                
  
        @ASN1Element ( name = "callingFacilityGroup", isOptional =  true , hasTag =  true, tag = 4 , hasDefaultValue =  false  )
    
	private FacilityGroup callingFacilityGroup = null;
                
  
        @ASN1Element ( name = "callingFacilityGroupMember", isOptional =  true , hasTag =  true, tag = 5 , hasDefaultValue =  false  )
    
	private FacilityGroupMember callingFacilityGroupMember = null;
                
  
        @ASN1Element ( name = "travellingClassMark", isOptional =  true , hasTag =  true, tag = 6 , hasDefaultValue =  false  )
    
	private TravellingClassMark travellingClassMark = null;
                
  
@ASN1SequenceOf( name = "extensions", isSetOf = false ) 

    @ASN1ValueRangeConstraint ( 
		
		min = 1L, 
		
		max = 5L 
		
	   )
	   
        @ASN1Element ( name = "extensions", isOptional =  true , hasTag =  true, tag = 7 , hasDefaultValue =  false  )
    
	private java.util.Collection<ExtensionField>  extensions = null;
                
  
        @ASN1Element ( name = "carrier", isOptional =  true , hasTag =  true, tag = 8 , hasDefaultValue =  false  )
    
	private Carrier carrier = null;
                
  
        @ASN1Element ( name = "componentType", isOptional =  true , hasTag =  true, tag = 9 , hasDefaultValue =  false  )
    
	private ComponentType componentType = null;
                
  
        @ASN1Element ( name = "component", isOptional =  true , hasTag =  true, tag = 10 , hasDefaultValue =  false  )
    
	private Component component = null;
                
  
        @ASN1Element ( name = "componentCorrelationID", isOptional =  true , hasTag =  true, tag = 11 , hasDefaultValue =  false  )
    
	private ComponentCorrelationID componentCorrelationID = null;
                
  
        
        public DpSpecificCommonParameters getDpSpecificCommonParameters () {
            return this.dpSpecificCommonParameters;
        }

        

        public void setDpSpecificCommonParameters (DpSpecificCommonParameters value) {
            this.dpSpecificCommonParameters = value;
        }
        
  
        
        public CalledPartyNumber getDialledDigits () {
            return this.dialledDigits;
        }

        
        public boolean isDialledDigitsPresent () {
            return this.dialledDigits != null;
        }
        

        public void setDialledDigits (CalledPartyNumber value) {
            this.dialledDigits = value;
        }
        
  
        
        public CallingPartyBusinessGroupID getCallingPartyBusinessGroupID () {
            return this.callingPartyBusinessGroupID;
        }

        
        public boolean isCallingPartyBusinessGroupIDPresent () {
            return this.callingPartyBusinessGroupID != null;
        }
        

        public void setCallingPartyBusinessGroupID (CallingPartyBusinessGroupID value) {
            this.callingPartyBusinessGroupID = value;
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
        
  
        
        public FacilityGroup getCallingFacilityGroup () {
            return this.callingFacilityGroup;
        }

        
        public boolean isCallingFacilityGroupPresent () {
            return this.callingFacilityGroup != null;
        }
        

        public void setCallingFacilityGroup (FacilityGroup value) {
            this.callingFacilityGroup = value;
        }
        
  
        
        public FacilityGroupMember getCallingFacilityGroupMember () {
            return this.callingFacilityGroupMember;
        }

        
        public boolean isCallingFacilityGroupMemberPresent () {
            return this.callingFacilityGroupMember != null;
        }
        

        public void setCallingFacilityGroupMember (FacilityGroupMember value) {
            this.callingFacilityGroupMember = value;
        }
        
  
        
        public TravellingClassMark getTravellingClassMark () {
            return this.travellingClassMark;
        }

        
        public boolean isTravellingClassMarkPresent () {
            return this.travellingClassMark != null;
        }
        

        public void setTravellingClassMark (TravellingClassMark value) {
            this.travellingClassMark = value;
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
        
  
        
        public Carrier getCarrier () {
            return this.carrier;
        }

        
        public boolean isCarrierPresent () {
            return this.carrier != null;
        }
        

        public void setCarrier (Carrier value) {
            this.carrier = value;
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

        private static IASN1PreparedElementData preparedData = CoderFactory.getInstance().newPreparedElementData(OriginationAttemptAuthorizedArg.class);
        public IASN1PreparedElementData getPreparedData() {
            return preparedData;
        }

            
    }
            