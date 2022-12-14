
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
    @ASN1Sequence ( name = "InformationToRecord", isSet = false )
    public class InformationToRecord implements IASN1PreparedElement {
            
        @ASN1Element ( name = "messageID", isOptional =  true , hasTag =  true, tag = 0 , hasDefaultValue =  false  )
    
	private ElementaryMessageID messageID = null;
                
  @ASN1Integer( name = "" )
    @ASN1ValueRangeConstraint ( 
		
		min = 1L, 
		
		max = 3600L 
		
	   )
	   
        @ASN1Element ( name = "messageDeletionTimeOut", isOptional =  true , hasTag =  true, tag = 1 , hasDefaultValue =  false  )
    
	private Integer messageDeletionTimeOut = null;
                
  @ASN1Integer( name = "" )
    @ASN1ValueRangeConstraint ( 
		
		min = 0L, 
		
		max = 5L 
		
	   )
	   
        @ASN1Element ( name = "timeToRecord", isOptional =  true , hasTag =  true, tag = 3 , hasDefaultValue =  false  )
    
	private Integer timeToRecord = null;
                
  

       @ASN1PreparedElement
       @ASN1Sequence ( name = "controlDigits" , isSet = false )
       public static class ControlDigitsSequenceType implements IASN1PreparedElement {
                @ASN1OctetString( name = "" )
    @ASN1ValueRangeConstraint ( 
		
		min = 1L, 
		
		max = 2L 
		
	   )
	   
        @ASN1Element ( name = "endOfRecordingDigit", isOptional =  true , hasTag =  true, tag = 0 , hasDefaultValue =  false  )
    
	private byte[] endOfRecordingDigit = null;
                
  @ASN1OctetString( name = "" )
    @ASN1ValueRangeConstraint ( 
		
		min = 1L, 
		
		max = 2L 
		
	   )
	   
        @ASN1Element ( name = "cancelDigit", isOptional =  true , hasTag =  true, tag = 1 , hasDefaultValue =  false  )
    
	private byte[] cancelDigit = null;
                
  @ASN1OctetString( name = "" )
    @ASN1ValueRangeConstraint ( 
		
		min = 1L, 
		
		max = 2L 
		
	   )
	   
        @ASN1Element ( name = "replayDigit", isOptional =  true , hasTag =  true, tag = 2 , hasDefaultValue =  false  )
    
	private byte[] replayDigit = null;
                
  @ASN1OctetString( name = "" )
    @ASN1ValueRangeConstraint ( 
		
		min = 1L, 
		
		max = 2L 
		
	   )
	   
        @ASN1Element ( name = "restartRecordingDigit", isOptional =  true , hasTag =  true, tag = 3 , hasDefaultValue =  false  )
    
	private byte[] restartRecordingDigit = null;
                
  @ASN1Boolean( name = "" )
    
        @ASN1Element ( name = "restartAllowed", isOptional =  false , hasTag =  true, tag = 4 , hasDefaultValue =  true  )
    
	private Boolean restartAllowed = null;
                
  @ASN1Boolean( name = "" )
    
        @ASN1Element ( name = "replayAllowed", isOptional =  false , hasTag =  true, tag = 5 , hasDefaultValue =  true  )
    
	private Boolean replayAllowed = null;
                
  
        
        public byte[] getEndOfRecordingDigit () {
            return this.endOfRecordingDigit;
        }

        
        public boolean isEndOfRecordingDigitPresent () {
            return this.endOfRecordingDigit != null;
        }
        

        public void setEndOfRecordingDigit (byte[] value) {
            this.endOfRecordingDigit = value;
        }
        
  
        
        public byte[] getCancelDigit () {
            return this.cancelDigit;
        }

        
        public boolean isCancelDigitPresent () {
            return this.cancelDigit != null;
        }
        

        public void setCancelDigit (byte[] value) {
            this.cancelDigit = value;
        }
        
  
        
        public byte[] getReplayDigit () {
            return this.replayDigit;
        }

        
        public boolean isReplayDigitPresent () {
            return this.replayDigit != null;
        }
        

        public void setReplayDigit (byte[] value) {
            this.replayDigit = value;
        }
        
  
        
        public byte[] getRestartRecordingDigit () {
            return this.restartRecordingDigit;
        }

        
        public boolean isRestartRecordingDigitPresent () {
            return this.restartRecordingDigit != null;
        }
        

        public void setRestartRecordingDigit (byte[] value) {
            this.restartRecordingDigit = value;
        }
        
  
        
        public Boolean getRestartAllowed () {
            return this.restartAllowed;
        }

        

        public void setRestartAllowed (Boolean value) {
            this.restartAllowed = value;
        }
        
  
        
        public Boolean getReplayAllowed () {
            return this.replayAllowed;
        }

        

        public void setReplayAllowed (Boolean value) {
            this.replayAllowed = value;
        }
        
  
                
                
        public void initWithDefaults() {
            Boolean param_RestartAllowed =         
            null;
        setRestartAllowed(param_RestartAllowed);
    Boolean param_ReplayAllowed =         
            null;
        setReplayAllowed(param_ReplayAllowed);
    
        }

        public IASN1PreparedElementData getPreparedData() {
            return preparedData_ControlDigitsSequenceType;
        }

       private static IASN1PreparedElementData preparedData_ControlDigitsSequenceType = CoderFactory.getInstance().newPreparedElementData(ControlDigitsSequenceType.class);
                
       }

       
                
        @ASN1Element ( name = "controlDigits", isOptional =  false , hasTag =  true, tag = 4 , hasDefaultValue =  false  )
    
	private ControlDigitsSequenceType controlDigits = null;
                
  
        
        public ElementaryMessageID getMessageID () {
            return this.messageID;
        }

        
        public boolean isMessageIDPresent () {
            return this.messageID != null;
        }
        

        public void setMessageID (ElementaryMessageID value) {
            this.messageID = value;
        }
        
  
        
        public Integer getMessageDeletionTimeOut () {
            return this.messageDeletionTimeOut;
        }

        
        public boolean isMessageDeletionTimeOutPresent () {
            return this.messageDeletionTimeOut != null;
        }
        

        public void setMessageDeletionTimeOut (Integer value) {
            this.messageDeletionTimeOut = value;
        }
        
  
        
        public Integer getTimeToRecord () {
            return this.timeToRecord;
        }

        
        public boolean isTimeToRecordPresent () {
            return this.timeToRecord != null;
        }
        

        public void setTimeToRecord (Integer value) {
            this.timeToRecord = value;
        }
        
  
        
        public ControlDigitsSequenceType getControlDigits () {
            return this.controlDigits;
        }

        

        public void setControlDigits (ControlDigitsSequenceType value) {
            this.controlDigits = value;
        }
        
  
                    
        
        public void initWithDefaults() {
            
        }

        private static IASN1PreparedElementData preparedData = CoderFactory.getInstance().newPreparedElementData(InformationToRecord.class);
        public IASN1PreparedElementData getPreparedData() {
            return preparedData;
        }

            
    }
            