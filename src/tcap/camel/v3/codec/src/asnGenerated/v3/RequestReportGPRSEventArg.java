
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
    @ASN1Sequence ( name = "RequestReportGPRSEventArg", isSet = false )
    public class RequestReportGPRSEventArg implements IASN1PreparedElement {
            
        @ASN1Element ( name = "gPRSEvent", isOptional =  false , hasTag =  true, tag = 0 , hasDefaultValue =  false  )
    
	private GPRSEventArray gPRSEvent = null;
                
  
        @ASN1Element ( name = "pDPID", isOptional =  true , hasTag =  true, tag = 1 , hasDefaultValue =  false  )
    
	private PDPId pDPID = null;
                
  
        
        public GPRSEventArray getGPRSEvent () {
            return this.gPRSEvent;
        }

        

        public void setGPRSEvent (GPRSEventArray value) {
            this.gPRSEvent = value;
        }
        
  
        
        public PDPId getPDPID () {
            return this.pDPID;
        }

        
        public boolean isPDPIDPresent () {
            return this.pDPID != null;
        }
        

        public void setPDPID (PDPId value) {
            this.pDPID = value;
        }
        
  
                    
        
        public void initWithDefaults() {
            
        }

        private static IASN1PreparedElementData preparedData = CoderFactory.getInstance().newPreparedElementData(RequestReportGPRSEventArg.class);
        public IASN1PreparedElementData getPreparedData() {
            return preparedData;
        }

            
    }
            