
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
    @ASN1Choice ( name = "InfoToSend" )
    public class InfoToSend implements IASN1PreparedElement {
            
        @ASN1Element ( name = "messageID", isOptional =  false , hasTag =  true, tag = 0 , hasDefaultValue =  false  )
    
	private MessageID messageID = null;
                
  
        @ASN1Element ( name = "toneId", isOptional =  false , hasTag =  true, tag = 1 , hasDefaultValue =  false  )
    
	private ToneId toneId = null;
                
  
        @ASN1Element ( name = "displayInformation", isOptional =  false , hasTag =  true, tag = 2 , hasDefaultValue =  false  )
    
	private DisplayInformation displayInformation = null;
                
  
        
        public MessageID getMessageID () {
            return this.messageID;
        }

        public boolean isMessageIDSelected () {
            return this.messageID != null;
        }

        private void setMessageID (MessageID value) {
            this.messageID = value;
        }

        
        public void selectMessageID (MessageID value) {
            this.messageID = value;
            
                    setToneId(null);
                
                    setDisplayInformation(null);
                            
        }

        
  
        
        public ToneId getToneId () {
            return this.toneId;
        }

        public boolean isToneIdSelected () {
            return this.toneId != null;
        }

        private void setToneId (ToneId value) {
            this.toneId = value;
        }

        
        public void selectToneId (ToneId value) {
            this.toneId = value;
            
                    setMessageID(null);
                
                    setDisplayInformation(null);
                            
        }

        
  
        
        public DisplayInformation getDisplayInformation () {
            return this.displayInformation;
        }

        public boolean isDisplayInformationSelected () {
            return this.displayInformation != null;
        }

        private void setDisplayInformation (DisplayInformation value) {
            this.displayInformation = value;
        }

        
        public void selectDisplayInformation (DisplayInformation value) {
            this.displayInformation = value;
            
                    setMessageID(null);
                
                    setToneId(null);
                            
        }

        
  

	    public void initWithDefaults() {
	    }

        private static IASN1PreparedElementData preparedData = CoderFactory.getInstance().newPreparedElementData(InfoToSend.class);
        public IASN1PreparedElementData getPreparedData() {
            return preparedData;
        }


    }
            