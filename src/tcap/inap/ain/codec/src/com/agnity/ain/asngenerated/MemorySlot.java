
package com.agnity.ain.asngenerated;
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
    @ASN1Sequence ( name = "MemorySlot", isSet = false )
    public class MemorySlot implements IASN1PreparedElement {
            
        @ASN1Element ( name = "incoming", isOptional =  true , hasTag =  true, tag = 1 , hasDefaultValue =  false  )
    
	private Incoming incoming = null;
                
  
        @ASN1Element ( name = "outgoing", isOptional =  true , hasTag =  true, tag = 2 , hasDefaultValue =  false  )
    
	private Outgoing outgoing = null;
                
  
        
        public Incoming getIncoming () {
            return this.incoming;
        }

        
        public boolean isIncomingPresent () {
            return this.incoming != null;
        }
        

        public void setIncoming (Incoming value) {
            this.incoming = value;
        }
        
  
        
        public Outgoing getOutgoing () {
            return this.outgoing;
        }

        
        public boolean isOutgoingPresent () {
            return this.outgoing != null;
        }
        

        public void setOutgoing (Outgoing value) {
            this.outgoing = value;
        }
        
  
                    
        
        public void initWithDefaults() {
            
        }

        private static IASN1PreparedElementData preparedData = CoderFactory.getInstance().newPreparedElementData(MemorySlot.class);
        public IASN1PreparedElementData getPreparedData() {
            return preparedData;
        }

            
    }
            