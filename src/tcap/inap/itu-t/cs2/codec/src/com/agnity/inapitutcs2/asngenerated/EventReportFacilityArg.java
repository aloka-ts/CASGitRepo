
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
    @ASN1Sequence ( name = "EventReportFacilityArg", isSet = false )
    public class EventReportFacilityArg implements IASN1PreparedElement {
            
        @ASN1Element ( name = "componentType", isOptional =  true , hasTag =  true, tag = 0 , hasDefaultValue =  false  )
    
	private ComponentType componentType = null;
                
  
        @ASN1Element ( name = "component", isOptional =  true , hasTag =  true, tag = 1 , hasDefaultValue =  false  )
    
	private Component component = null;
                
  
        @ASN1Element ( name = "legID", isOptional =  true , hasTag =  true, tag = 2 , hasDefaultValue =  false  )
    
	private LegID legID = null;
                
  
        @ASN1Element ( name = "componentCorrelationID", isOptional =  true , hasTag =  true, tag = 3 , hasDefaultValue =  false  )
    
	private ComponentCorrelationID componentCorrelationID = null;
                
  
@ASN1SequenceOf( name = "extensions", isSetOf = false ) 

    @ASN1ValueRangeConstraint ( 
		
		min = 1L, 
		
		max = 5L 
		
	   )
	   
        @ASN1Element ( name = "extensions", isOptional =  true , hasTag =  true, tag = 4 , hasDefaultValue =  false  )
    
	private java.util.Collection<ExtensionField>  extensions = null;
                
  
        
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
        
  
        
        public LegID getLegID () {
            return this.legID;
        }

        
        public boolean isLegIDPresent () {
            return this.legID != null;
        }
        

        public void setLegID (LegID value) {
            this.legID = value;
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
        
  
        
        public java.util.Collection<ExtensionField>  getExtensions () {
            return this.extensions;
        }

        
        public boolean isExtensionsPresent () {
            return this.extensions != null;
        }
        

        public void setExtensions (java.util.Collection<ExtensionField>  value) {
            this.extensions = value;
        }
        
  
                    
        
        public void initWithDefaults() {
            
        }

        private static IASN1PreparedElementData preparedData = CoderFactory.getInstance().newPreparedElementData(EventReportFacilityArg.class);
        public IASN1PreparedElementData getPreparedData() {
            return preparedData;
        }

            
    }
            