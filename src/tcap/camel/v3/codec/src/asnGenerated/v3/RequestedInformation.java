
package asnGenerated.v3;
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
    @ASN1Sequence ( name = "RequestedInformation", isSet = false )
    public class RequestedInformation implements IASN1PreparedElement {
            
        @ASN1Element ( name = "requestedInformationType", isOptional =  false , hasTag =  true, tag = 0 , hasDefaultValue =  false  )
    
	private RequestedInformationType requestedInformationType = null;
                
  
        @ASN1Element ( name = "requestedInformationValue", isOptional =  false , hasTag =  true, tag = 1 , hasDefaultValue =  false  )
    
	private RequestedInformationValue requestedInformationValue = null;
                
  
        
        public RequestedInformationType getRequestedInformationType () {
            return this.requestedInformationType;
        }

        

        public void setRequestedInformationType (RequestedInformationType value) {
            this.requestedInformationType = value;
        }
        
  
        
        public RequestedInformationValue getRequestedInformationValue () {
            return this.requestedInformationValue;
        }

        

        public void setRequestedInformationValue (RequestedInformationValue value) {
            this.requestedInformationValue = value;
        }
        
  
                    
        
        public void initWithDefaults() {
            
        }

        private static IASN1PreparedElementData preparedData = CoderFactory.getInstance().newPreparedElementData(RequestedInformation.class);
        public IASN1PreparedElementData getPreparedData() {
            return preparedData;
        }

            
    }
            