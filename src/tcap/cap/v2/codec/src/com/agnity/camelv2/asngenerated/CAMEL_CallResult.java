
package com.agnity.camelv2.asngenerated;
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
    @ASN1Choice ( name = "CAMEL_CallResult" )
    public class CAMEL_CallResult implements IASN1PreparedElement {
            

       @ASN1PreparedElement
       @ASN1Sequence ( name = "timeDurationChargingResult" , isSet = false )
       public static class TimeDurationChargingResultSequenceType implements IASN1PreparedElement {
                
        @ASN1Element ( name = "partyToCharge", isOptional =  false , hasTag =  true, tag = 0 , hasDefaultValue =  false  )
    
	private ReceivingSideID partyToCharge = null;
                
  
        @ASN1Element ( name = "timeInformation", isOptional =  false , hasTag =  true, tag = 1 , hasDefaultValue =  false  )
    
	private TimeInformation timeInformation = null;
                
  @ASN1Boolean( name = "" )
    
        @ASN1Element ( name = "callActive", isOptional =  false , hasTag =  true, tag = 2 , hasDefaultValue =  true  )
    
	private Boolean callActive = null;
                
  
        
        public ReceivingSideID getPartyToCharge () {
            return this.partyToCharge;
        }

        

        public void setPartyToCharge (ReceivingSideID value) {
            this.partyToCharge = value;
        }
        
  
        
        public TimeInformation getTimeInformation () {
            return this.timeInformation;
        }

        

        public void setTimeInformation (TimeInformation value) {
            this.timeInformation = value;
        }
        
  
        
        public Boolean getCallActive () {
            return this.callActive;
        }

        

        public void setCallActive (Boolean value) {
            this.callActive = value;
        }
        
  
                
                
        public void initWithDefaults() {
            Boolean param_CallActive =         
            null;
        setCallActive(param_CallActive);
    
        }

        public IASN1PreparedElementData getPreparedData() {
            return preparedData_TimeDurationChargingResultSequenceType;
        }

       private static IASN1PreparedElementData preparedData_TimeDurationChargingResultSequenceType = CoderFactory.getInstance().newPreparedElementData(TimeDurationChargingResultSequenceType.class);
                
       }

       
                
        @ASN1Element ( name = "timeDurationChargingResult", isOptional =  false , hasTag =  true, tag = 0 , hasDefaultValue =  false  )
    
	private TimeDurationChargingResultSequenceType timeDurationChargingResult = null;
                
  
        
        public TimeDurationChargingResultSequenceType getTimeDurationChargingResult () {
            return this.timeDurationChargingResult;
        }

        public boolean isTimeDurationChargingResultSelected () {
            return this.timeDurationChargingResult != null;
        }

        private void setTimeDurationChargingResult (TimeDurationChargingResultSequenceType value) {
            this.timeDurationChargingResult = value;
        }

        
        public void selectTimeDurationChargingResult (TimeDurationChargingResultSequenceType value) {
            this.timeDurationChargingResult = value;
                        
        }

        
  

	    public void initWithDefaults() {
	    }

        private static IASN1PreparedElementData preparedData = CoderFactory.getInstance().newPreparedElementData(CAMEL_CallResult.class);
        public IASN1PreparedElementData getPreparedData() {
            return preparedData;
        }


    }
            