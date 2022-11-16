
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
    @ASN1Sequence ( name = "CallInformationReportArg", isSet = false )
    public class CallInformationReportArg implements IASN1PreparedElement {
            
        @ASN1Element ( name = "requestedInformationList", isOptional =  false , hasTag =  true, tag = 0 , hasDefaultValue =  false  )
    
	private RequestedInformationList requestedInformationList = null;
                
  
        @ASN1Element ( name = "correlationID", isOptional =  true , hasTag =  true, tag = 1 , hasDefaultValue =  false  )
    
	private CorrelationID correlationID = null;
                
  
@ASN1SequenceOf( name = "extensions", isSetOf = false ) 

    @ASN1ValueRangeConstraint ( 
		
		min = 1L, 
		
		max = 5L 
		
	   )
	   
        @ASN1Element ( name = "extensions", isOptional =  true , hasTag =  true, tag = 2 , hasDefaultValue =  false  )
    
	private java.util.Collection<ExtensionField>  extensions = null;
                
  
        @ASN1Element ( name = "legID", isOptional =  true , hasTag =  true, tag = 3 , hasDefaultValue =  false  )
    
	private LegID legID = null;
                
  @ASN1Boolean( name = "" )
    
        @ASN1Element ( name = "lastEventIndicator", isOptional =  false , hasTag =  true, tag = 4 , hasDefaultValue =  true  )
    
	private Boolean lastEventIndicator = null;
                
  
        
        public RequestedInformationList getRequestedInformationList () {
            return this.requestedInformationList;
        }

        

        public void setRequestedInformationList (RequestedInformationList value) {
            this.requestedInformationList = value;
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
        
  
        
        public java.util.Collection<ExtensionField>  getExtensions () {
            return this.extensions;
        }

        
        public boolean isExtensionsPresent () {
            return this.extensions != null;
        }
        

        public void setExtensions (java.util.Collection<ExtensionField>  value) {
            this.extensions = value;
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
        
  
        
        public Boolean getLastEventIndicator () {
            return this.lastEventIndicator;
        }

        

        public void setLastEventIndicator (Boolean value) {
            this.lastEventIndicator = value;
        }
        
  
                    
        
        public void initWithDefaults() {
            Boolean param_LastEventIndicator =         
            null;
        setLastEventIndicator(param_LastEventIndicator);
    
        }

        private static IASN1PreparedElementData preparedData = CoderFactory.getInstance().newPreparedElementData(CallInformationReportArg.class);
        public IASN1PreparedElementData getPreparedData() {
            return preparedData;
        }

            
    }
            