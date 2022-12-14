
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
    @ASN1Sequence ( name = "GPRSEvent", isSet = false )
    public class GPRSEvent implements IASN1PreparedElement {
            
        @ASN1Element ( name = "gPRSEventType", isOptional =  false , hasTag =  true, tag = 0 , hasDefaultValue =  false  )
    
	private GPRSEventType gPRSEventType = null;
                
  
        @ASN1Element ( name = "monitorMode", isOptional =  false , hasTag =  true, tag = 1 , hasDefaultValue =  false  )
    
	private MonitorMode monitorMode = null;
                
  
        
        public GPRSEventType getGPRSEventType () {
            return this.gPRSEventType;
        }

        

        public void setGPRSEventType (GPRSEventType value) {
            this.gPRSEventType = value;
        }
        
  
        
        public MonitorMode getMonitorMode () {
            return this.monitorMode;
        }

        

        public void setMonitorMode (MonitorMode value) {
            this.monitorMode = value;
        }
        
  
                    
        
        public void initWithDefaults() {
            
        }

        private static IASN1PreparedElementData preparedData = CoderFactory.getInstance().newPreparedElementData(GPRSEvent.class);
        public IASN1PreparedElementData getPreparedData() {
            return preparedData;
        }

            
    }
            