
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
    @ASN1Choice ( name = "LegID" )
    public class LegID implements IASN1PreparedElement {
            
        @ASN1Element ( name = "sendingSideID", isOptional =  false , hasTag =  true, tag = 0 , hasDefaultValue =  false  )
    
	private LegType sendingSideID = null;
                
  
        @ASN1Element ( name = "receivingSideID", isOptional =  false , hasTag =  true, tag = 1 , hasDefaultValue =  false  )
    
	private LegType receivingSideID = null;
                
  
        
        public LegType getSendingSideID () {
            return this.sendingSideID;
        }

        public boolean isSendingSideIDSelected () {
            return this.sendingSideID != null;
        }

        private void setSendingSideID (LegType value) {
            this.sendingSideID = value;
        }

        
        public void selectSendingSideID (LegType value) {
            this.sendingSideID = value;
            
                    setReceivingSideID(null);
                            
        }

        
  
        
        public LegType getReceivingSideID () {
            return this.receivingSideID;
        }

        public boolean isReceivingSideIDSelected () {
            return this.receivingSideID != null;
        }

        private void setReceivingSideID (LegType value) {
            this.receivingSideID = value;
        }

        
        public void selectReceivingSideID (LegType value) {
            this.receivingSideID = value;
            
                    setSendingSideID(null);
                            
        }

        
  

	    public void initWithDefaults() {
	    }

        private static IASN1PreparedElementData preparedData = CoderFactory.getInstance().newPreparedElementData(LegID.class);
        public IASN1PreparedElementData getPreparedData() {
            return preparedData;
        }


    }
            