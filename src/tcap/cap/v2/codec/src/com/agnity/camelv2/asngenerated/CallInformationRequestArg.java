
package com.agnity.camelv2.asngenerated;
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
    @ASN1Sequence ( name = "CallInformationRequestArg", isSet = false )
    public class CallInformationRequestArg implements IASN1PreparedElement {
            
        @ASN1Element ( name = "requestedInformationTypeList", isOptional =  false , hasTag =  true, tag = 0 , hasDefaultValue =  false  )
    
	private RequestedInformationTypeList requestedInformationTypeList = null;
                
  
@ASN1SequenceOf( name = "extensions", isSetOf = false ) 

    @ASN1ValueRangeConstraint ( 
		
		min = 1L, 
		
		max = 10L 
		
	   )
	   
        @ASN1Element ( name = "extensions", isOptional =  true , hasTag =  true, tag = 2 , hasDefaultValue =  false  )
    
	private java.util.Collection<ExtensionField>  extensions = null;
                
  
        @ASN1Element ( name = "legID", isOptional =  true , hasTag =  true, tag = 3 , hasDefaultValue =  false  )
    
	private SendingSideID legID = null;
                
  
        
        public RequestedInformationTypeList getRequestedInformationTypeList () {
            return this.requestedInformationTypeList;
        }

        

        public void setRequestedInformationTypeList (RequestedInformationTypeList value) {
            this.requestedInformationTypeList = value;
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
        
  
        
        public SendingSideID getLegID () {
            return this.legID;
        }

        
        public boolean isLegIDPresent () {
            return this.legID != null;
        }
        

        public void setLegID (SendingSideID value) {
            this.legID = value;
        }
        
  
                    
        
        public void initWithDefaults() {
            
        }

        private static IASN1PreparedElementData preparedData = CoderFactory.getInstance().newPreparedElementData(CallInformationRequestArg.class);
        public IASN1PreparedElementData getPreparedData() {
            return preparedData;
        }

            
    }
            