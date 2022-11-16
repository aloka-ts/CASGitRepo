
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
    @ASN1BoxedType ( name = "SMSDeliveryPointToPointAck" )
    public class SMSDeliveryPointToPointAck implements IASN1PreparedElement {
                
        

       @ASN1PreparedElement
       @ASN1Sequence ( name = "SMSDeliveryPointToPointAck" , isSet = true )
       public static class SMSDeliveryPointToPointAckSequenceType implements IASN1PreparedElement {
                
        @ASN1Element ( name = "interMSCCircuitID", isOptional =  false , hasTag =  true, tag = 6 , hasDefaultValue =  false  )
    
	private InterMSCCircuitID interMSCCircuitID = null;
                
  
        @ASN1Element ( name = "imsi", isOptional =  true , hasTag =  true, tag = 242 , hasDefaultValue =  false  )
    
	private IMSI imsi = null;
                
  
        @ASN1Element ( name = "msid", isOptional =  true , hasTag =  false  , hasDefaultValue =  false  )
    
	private MSID msid = null;
                
  
        @ASN1Element ( name = "sms-BearerData", isOptional =  true , hasTag =  true, tag = 105 , hasDefaultValue =  false  )
    
	private SMS_BearerData sms_BearerData = null;
                
  
        @ASN1Element ( name = "sms-CauseCode", isOptional =  true , hasTag =  true, tag = 153 , hasDefaultValue =  false  )
    
	private SMS_CauseCode sms_CauseCode = null;
                
  
        @ASN1Element ( name = "sms-TransactionID", isOptional =  true , hasTag =  true, tag = 302 , hasDefaultValue =  false  )
    
	private SMS_TransactionID sms_TransactionID = null;
                
  
        
        public InterMSCCircuitID getInterMSCCircuitID () {
            return this.interMSCCircuitID;
        }

        

        public void setInterMSCCircuitID (InterMSCCircuitID value) {
            this.interMSCCircuitID = value;
        }
        
  
        
        public IMSI getImsi () {
            return this.imsi;
        }

        
        public boolean isImsiPresent () {
            return this.imsi != null;
        }
        

        public void setImsi (IMSI value) {
            this.imsi = value;
        }
        
  
        
        public MSID getMsid () {
            return this.msid;
        }

        
        public boolean isMsidPresent () {
            return this.msid != null;
        }
        

        public void setMsid (MSID value) {
            this.msid = value;
        }
        
  
        
        public SMS_BearerData getSms_BearerData () {
            return this.sms_BearerData;
        }

        
        public boolean isSms_BearerDataPresent () {
            return this.sms_BearerData != null;
        }
        

        public void setSms_BearerData (SMS_BearerData value) {
            this.sms_BearerData = value;
        }
        
  
        
        public SMS_CauseCode getSms_CauseCode () {
            return this.sms_CauseCode;
        }

        
        public boolean isSms_CauseCodePresent () {
            return this.sms_CauseCode != null;
        }
        

        public void setSms_CauseCode (SMS_CauseCode value) {
            this.sms_CauseCode = value;
        }
        
  
        
        public SMS_TransactionID getSms_TransactionID () {
            return this.sms_TransactionID;
        }

        
        public boolean isSms_TransactionIDPresent () {
            return this.sms_TransactionID != null;
        }
        

        public void setSms_TransactionID (SMS_TransactionID value) {
            this.sms_TransactionID = value;
        }
        
  
                
                
        public void initWithDefaults() {
            
        }

        public IASN1PreparedElementData getPreparedData() {
            return preparedData_SMSDeliveryPointToPointAckSequenceType;
        }

       private static IASN1PreparedElementData preparedData_SMSDeliveryPointToPointAckSequenceType = CoderFactory.getInstance().newPreparedElementData(SMSDeliveryPointToPointAckSequenceType.class);
                
       }

       
                
        @ASN1Element ( name = "SMSDeliveryPointToPointAck", isOptional =  false , hasTag =  true, tag = 18, 
        tagClass =  TagClass.Private  , hasDefaultValue =  false  )
    
        private SMSDeliveryPointToPointAckSequenceType  value;        

        
        
        public SMSDeliveryPointToPointAck () {
        }
        
        
        
        public void setValue(SMSDeliveryPointToPointAckSequenceType value) {
            this.value = value;
        }
        
        
        
        public SMSDeliveryPointToPointAckSequenceType getValue() {
            return this.value;
        }            
        

	    public void initWithDefaults() {
	    }

        private static IASN1PreparedElementData preparedData = CoderFactory.getInstance().newPreparedElementData(SMSDeliveryPointToPointAck.class);
        public IASN1PreparedElementData getPreparedData() {
            return preparedData;
        }

            
    }
            