
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
    @ASN1Sequence ( name = "ServiceFilteringResponseArg", isSet = false )
    public class ServiceFilteringResponseArg implements IASN1PreparedElement {
            
        @ASN1Element ( name = "countersValue", isOptional =  false , hasTag =  true, tag = 0 , hasDefaultValue =  false  )
    
	private CountersValue countersValue = null;
                
  
        @ASN1Element ( name = "filteringCriteria", isOptional =  false , hasTag =  true, tag = 1 , hasDefaultValue =  false  )
    
	private FilteringCriteria filteringCriteria = null;
                
  
@ASN1SequenceOf( name = "extensions", isSetOf = false ) 

    @ASN1ValueRangeConstraint ( 
		
		min = 1L, 
		
		max = 5L 
		
	   )
	   
        @ASN1Element ( name = "extensions", isOptional =  true , hasTag =  true, tag = 2 , hasDefaultValue =  false  )
    
	private java.util.Collection<ExtensionField>  extensions = null;
                
  
        @ASN1Element ( name = "responseCondition", isOptional =  true , hasTag =  true, tag = 3 , hasDefaultValue =  false  )
    
	private ResponseCondition responseCondition = null;
                
  
        
        public CountersValue getCountersValue () {
            return this.countersValue;
        }

        

        public void setCountersValue (CountersValue value) {
            this.countersValue = value;
        }
        
  
        
        public FilteringCriteria getFilteringCriteria () {
            return this.filteringCriteria;
        }

        

        public void setFilteringCriteria (FilteringCriteria value) {
            this.filteringCriteria = value;
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
        
  
        
        public ResponseCondition getResponseCondition () {
            return this.responseCondition;
        }

        
        public boolean isResponseConditionPresent () {
            return this.responseCondition != null;
        }
        

        public void setResponseCondition (ResponseCondition value) {
            this.responseCondition = value;
        }
        
  
                    
        
        public void initWithDefaults() {
            
        }

        private static IASN1PreparedElementData preparedData = CoderFactory.getInstance().newPreparedElementData(ServiceFilteringResponseArg.class);
        public IASN1PreparedElementData getPreparedData() {
            return preparedData;
        }

            
    }
            