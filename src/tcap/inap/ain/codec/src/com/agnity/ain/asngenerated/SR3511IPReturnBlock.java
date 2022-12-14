
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
    @ASN1Sequence ( name = "SR3511IPReturnBlock", isSet = false )
    public class SR3511IPReturnBlock implements IASN1PreparedElement {
            
        @ASN1Element ( name = "announcementResult", isOptional =  true , hasTag =  true, tag = 2 , hasDefaultValue =  false  )
    
	private AnnouncementResult announcementResult = null;
                
  
        @ASN1Element ( name = "announcementDigitResult", isOptional =  true , hasTag =  true, tag = 3 , hasDefaultValue =  false  )
    
	private AnnouncementDigitResult announcementDigitResult = null;
                
  
        @ASN1Element ( name = "textSpeechResult", isOptional =  true , hasTag =  true, tag = 4 , hasDefaultValue =  false  )
    
	private TextSpeechResult textSpeechResult = null;
                
  
        @ASN1Element ( name = "textSpeechDigitResult", isOptional =  true , hasTag =  true, tag = 5 , hasDefaultValue =  false  )
    
	private TextSpeechDigitResult textSpeechDigitResult = null;
                
  
        @ASN1Element ( name = "collectedInformationResult", isOptional =  true , hasTag =  true, tag = 6 , hasDefaultValue =  false  )
    
	private CollectedInformationResult collectedInformationResult = null;
                
  
        
        public AnnouncementResult getAnnouncementResult () {
            return this.announcementResult;
        }

        
        public boolean isAnnouncementResultPresent () {
            return this.announcementResult != null;
        }
        

        public void setAnnouncementResult (AnnouncementResult value) {
            this.announcementResult = value;
        }
        
  
        
        public AnnouncementDigitResult getAnnouncementDigitResult () {
            return this.announcementDigitResult;
        }

        
        public boolean isAnnouncementDigitResultPresent () {
            return this.announcementDigitResult != null;
        }
        

        public void setAnnouncementDigitResult (AnnouncementDigitResult value) {
            this.announcementDigitResult = value;
        }
        
  
        
        public TextSpeechResult getTextSpeechResult () {
            return this.textSpeechResult;
        }

        
        public boolean isTextSpeechResultPresent () {
            return this.textSpeechResult != null;
        }
        

        public void setTextSpeechResult (TextSpeechResult value) {
            this.textSpeechResult = value;
        }
        
  
        
        public TextSpeechDigitResult getTextSpeechDigitResult () {
            return this.textSpeechDigitResult;
        }

        
        public boolean isTextSpeechDigitResultPresent () {
            return this.textSpeechDigitResult != null;
        }
        

        public void setTextSpeechDigitResult (TextSpeechDigitResult value) {
            this.textSpeechDigitResult = value;
        }
        
  
        
        public CollectedInformationResult getCollectedInformationResult () {
            return this.collectedInformationResult;
        }

        
        public boolean isCollectedInformationResultPresent () {
            return this.collectedInformationResult != null;
        }
        

        public void setCollectedInformationResult (CollectedInformationResult value) {
            this.collectedInformationResult = value;
        }
        
  
                    
        
        public void initWithDefaults() {
            
        }

        private static IASN1PreparedElementData preparedData = CoderFactory.getInstance().newPreparedElementData(SR3511IPReturnBlock.class);
        public IASN1PreparedElementData getPreparedData() {
            return preparedData;
        }

            
    }
            