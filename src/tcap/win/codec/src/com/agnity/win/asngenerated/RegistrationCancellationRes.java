
package com.agnity.win.asngenerated;
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
    @ASN1BoxedType ( name = "RegistrationCancellationRes" )
    public class RegistrationCancellationRes implements IASN1PreparedElement {
                
        

       @ASN1PreparedElement
       @ASN1Sequence ( name = "RegistrationCancellationRes" , isSet = true )
       public static class RegistrationCancellationResSequenceType implements IASN1PreparedElement {
                
        @ASN1Element ( name = "callHistoryCount", isOptional =  true , hasTag =  true, tag = 38 , hasDefaultValue =  false  )
    
	private CallHistoryCount callHistoryCount = null;
                
  
        @ASN1Element ( name = "cancellationDenied", isOptional =  true , hasTag =  true, tag = 57 , hasDefaultValue =  false  )
    
	private CancellationDenied cancellationDenied = null;
                
  
        @ASN1Element ( name = "controlChannelData", isOptional =  true , hasTag =  true, tag = 55 , hasDefaultValue =  false  )
    
	private ControlChannelData controlChannelData = null;
                
  
        @ASN1Element ( name = "receivedSignalQuality", isOptional =  true , hasTag =  true, tag = 72 , hasDefaultValue =  false  )
    
	private ReceivedSignalQuality receivedSignalQuality = null;
                
  
        @ASN1Element ( name = "sms-MessageWaitingIndicator", isOptional =  true , hasTag =  true, tag = 118 , hasDefaultValue =  false  )
    
	private SMS_MessageWaitingIndicator sms_MessageWaitingIndicator = null;
                
  
        @ASN1Element ( name = "systemAccessData", isOptional =  true , hasTag =  true, tag = 56 , hasDefaultValue =  false  )
    
	private SystemAccessData systemAccessData = null;
                
  
        
        public CallHistoryCount getCallHistoryCount () {
            return this.callHistoryCount;
        }

        
        public boolean isCallHistoryCountPresent () {
            return this.callHistoryCount != null;
        }
        

        public void setCallHistoryCount (CallHistoryCount value) {
            this.callHistoryCount = value;
        }
        
  
        
        public CancellationDenied getCancellationDenied () {
            return this.cancellationDenied;
        }

        
        public boolean isCancellationDeniedPresent () {
            return this.cancellationDenied != null;
        }
        

        public void setCancellationDenied (CancellationDenied value) {
            this.cancellationDenied = value;
        }
        
  
        
        public ControlChannelData getControlChannelData () {
            return this.controlChannelData;
        }

        
        public boolean isControlChannelDataPresent () {
            return this.controlChannelData != null;
        }
        

        public void setControlChannelData (ControlChannelData value) {
            this.controlChannelData = value;
        }
        
  
        
        public ReceivedSignalQuality getReceivedSignalQuality () {
            return this.receivedSignalQuality;
        }

        
        public boolean isReceivedSignalQualityPresent () {
            return this.receivedSignalQuality != null;
        }
        

        public void setReceivedSignalQuality (ReceivedSignalQuality value) {
            this.receivedSignalQuality = value;
        }
        
  
        
        public SMS_MessageWaitingIndicator getSms_MessageWaitingIndicator () {
            return this.sms_MessageWaitingIndicator;
        }

        
        public boolean isSms_MessageWaitingIndicatorPresent () {
            return this.sms_MessageWaitingIndicator != null;
        }
        

        public void setSms_MessageWaitingIndicator (SMS_MessageWaitingIndicator value) {
            this.sms_MessageWaitingIndicator = value;
        }
        
  
        
        public SystemAccessData getSystemAccessData () {
            return this.systemAccessData;
        }

        
        public boolean isSystemAccessDataPresent () {
            return this.systemAccessData != null;
        }
        

        public void setSystemAccessData (SystemAccessData value) {
            this.systemAccessData = value;
        }
        
  
                
                
        public void initWithDefaults() {
            
        }

        public IASN1PreparedElementData getPreparedData() {
            return preparedData_RegistrationCancellationResSequenceType;
        }

       private static IASN1PreparedElementData preparedData_RegistrationCancellationResSequenceType = CoderFactory.getInstance().newPreparedElementData(RegistrationCancellationResSequenceType.class);
                
       }

       
                
        @ASN1Element ( name = "RegistrationCancellationRes", isOptional =  false , hasTag =  true, tag = 18, 
        tagClass =  TagClass.Private  , hasDefaultValue =  false  )
    
        private RegistrationCancellationResSequenceType  value;        

        
        
        public RegistrationCancellationRes () {
        }
        
        
        
        public void setValue(RegistrationCancellationResSequenceType value) {
            this.value = value;
        }
        
        
        
        public RegistrationCancellationResSequenceType getValue() {
            return this.value;
        }            
        

	    public void initWithDefaults() {
	    }

        private static IASN1PreparedElementData preparedData = CoderFactory.getInstance().newPreparedElementData(RegistrationCancellationRes.class);
        public IASN1PreparedElementData getPreparedData() {
            return preparedData;
        }

            
    }
            