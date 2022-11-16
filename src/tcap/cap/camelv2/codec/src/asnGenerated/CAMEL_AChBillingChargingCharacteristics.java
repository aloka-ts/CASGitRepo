
package asnGenerated;
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
    @ASN1Choice ( name = "CAMEL_AChBillingChargingCharacteristics" )
    public class CAMEL_AChBillingChargingCharacteristics implements IASN1PreparedElement {
            

       @ASN1PreparedElement
       @ASN1Sequence ( name = "timeDurationCharging" , isSet = false )
       public static class TimeDurationChargingSequenceType implements IASN1PreparedElement {
                @ASN1Integer( name = "" )
    @ASN1ValueRangeConstraint ( 
		
		min = 1L, 
		
		max = 864000L 
		
	   )
	   
        @ASN1Element ( name = "maxCallPeriodDuration", isOptional =  false , hasTag =  true, tag = 0 , hasDefaultValue =  false  )
    
	private Integer maxCallPeriodDuration = null;
                
  @ASN1Boolean( name = "" )
    
        @ASN1Element ( name = "releaseIfdurationExceeded", isOptional =  false , hasTag =  true, tag = 1 , hasDefaultValue =  true  )
    
	private Boolean releaseIfdurationExceeded = null;
                
  @ASN1Integer( name = "" )
    @ASN1ValueRangeConstraint ( 
		
		min = 1L, 
		
		max = 86400L 
		
	   )
	   
        @ASN1Element ( name = "tariffSwitchInterval", isOptional =  true , hasTag =  true, tag = 2 , hasDefaultValue =  false  )
    
	private Integer tariffSwitchInterval = null;
                
  @ASN1Boolean( name = "" )
    
        @ASN1Element ( name = "tone", isOptional =  false , hasTag =  true, tag = 3 , hasDefaultValue =  true  )
    
	private Boolean tone = null;
                
  
        
        public Integer getMaxCallPeriodDuration () {
            return this.maxCallPeriodDuration;
        }

        

        public void setMaxCallPeriodDuration (Integer value) {
            this.maxCallPeriodDuration = value;
        }
        
  
        
        public Boolean getReleaseIfdurationExceeded () {
            return this.releaseIfdurationExceeded;
        }

        

        public void setReleaseIfdurationExceeded (Boolean value) {
            this.releaseIfdurationExceeded = value;
        }
        
  
        
        public Integer getTariffSwitchInterval () {
            return this.tariffSwitchInterval;
        }

        
        public boolean isTariffSwitchIntervalPresent () {
            return this.tariffSwitchInterval != null;
        }
        

        public void setTariffSwitchInterval (Integer value) {
            this.tariffSwitchInterval = value;
        }
        
  
        
        public Boolean getTone () {
            return this.tone;
        }

        

        public void setTone (Boolean value) {
            this.tone = value;
        }
        
  
                
                
        public void initWithDefaults() {
            Boolean param_ReleaseIfdurationExceeded =         
            null;
        setReleaseIfdurationExceeded(param_ReleaseIfdurationExceeded);
    Boolean param_Tone =         
            null;
        setTone(param_Tone);
    
        }

        public IASN1PreparedElementData getPreparedData() {
            return preparedData_TimeDurationChargingSequenceType;
        }

       private static IASN1PreparedElementData preparedData_TimeDurationChargingSequenceType = CoderFactory.getInstance().newPreparedElementData(TimeDurationChargingSequenceType.class);
                
       }

       
                
        @ASN1Element ( name = "timeDurationCharging", isOptional =  false , hasTag =  true, tag = 0 , hasDefaultValue =  false  )
    
	private TimeDurationChargingSequenceType timeDurationCharging = null;
                
  
        
        public TimeDurationChargingSequenceType getTimeDurationCharging () {
            return this.timeDurationCharging;
        }

        public boolean isTimeDurationChargingSelected () {
            return this.timeDurationCharging != null;
        }

        private void setTimeDurationCharging (TimeDurationChargingSequenceType value) {
            this.timeDurationCharging = value;
        }

        
        public void selectTimeDurationCharging (TimeDurationChargingSequenceType value) {
            this.timeDurationCharging = value;
                        
        }

        
  

	    public void initWithDefaults() {
	    }

        private static IASN1PreparedElementData preparedData = CoderFactory.getInstance().newPreparedElementData(CAMEL_AChBillingChargingCharacteristics.class);
        public IASN1PreparedElementData getPreparedData() {
            return preparedData;
        }


    }
            