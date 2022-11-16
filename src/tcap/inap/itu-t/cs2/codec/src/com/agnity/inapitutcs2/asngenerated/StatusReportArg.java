
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
    @ASN1Sequence ( name = "StatusReportArg", isSet = false )
    public class StatusReportArg implements IASN1PreparedElement {
            
        @ASN1Element ( name = "resourceStatus", isOptional =  true , hasTag =  true, tag = 0 , hasDefaultValue =  false  )
    
	private ResourceStatus resourceStatus = null;
                
  
        @ASN1Element ( name = "correlationID", isOptional =  true , hasTag =  true, tag = 1 , hasDefaultValue =  false  )
    
	private CorrelationID correlationID = null;
                
  
        @ASN1Element ( name = "resourceID", isOptional =  true , hasTag =  true, tag = 2 , hasDefaultValue =  false  )
    
	private ResourceID resourceID = null;
                
  
@ASN1SequenceOf( name = "extensions", isSetOf = false ) 

    @ASN1ValueRangeConstraint ( 
		
		min = 1L, 
		
		max = 5L 
		
	   )
	   
        @ASN1Element ( name = "extensions", isOptional =  true , hasTag =  true, tag = 3 , hasDefaultValue =  false  )
    
	private java.util.Collection<ExtensionField>  extensions = null;
                
  
        @ASN1Element ( name = "reportCondition", isOptional =  true , hasTag =  true, tag = 4 , hasDefaultValue =  false  )
    
	private ReportCondition reportCondition = null;
                
  
        
        public ResourceStatus getResourceStatus () {
            return this.resourceStatus;
        }

        
        public boolean isResourceStatusPresent () {
            return this.resourceStatus != null;
        }
        

        public void setResourceStatus (ResourceStatus value) {
            this.resourceStatus = value;
        }
        
  
        
        public CorrelationID getCorrelationID () {
            return this.correlationID;
        }

        
        public boolean isCorrelationIDPresent () {
            return this.correlationID != null;
        }
        

        public void setCorrelationID (CorrelationID value) {
            this.correlationID = value;
        }
        
  
        
        public ResourceID getResourceID () {
            return this.resourceID;
        }

        
        public boolean isResourceIDPresent () {
            return this.resourceID != null;
        }
        

        public void setResourceID (ResourceID value) {
            this.resourceID = value;
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
        
  
        
        public ReportCondition getReportCondition () {
            return this.reportCondition;
        }

        
        public boolean isReportConditionPresent () {
            return this.reportCondition != null;
        }
        

        public void setReportCondition (ReportCondition value) {
            this.reportCondition = value;
        }
        
  
                    
        
        public void initWithDefaults() {
            
        }

        private static IASN1PreparedElementData preparedData = CoderFactory.getInstance().newPreparedElementData(StatusReportArg.class);
        public IASN1PreparedElementData getPreparedData() {
            return preparedData;
        }

            
    }
            