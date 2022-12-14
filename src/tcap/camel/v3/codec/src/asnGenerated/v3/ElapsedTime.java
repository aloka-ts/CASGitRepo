
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
    @ASN1Choice ( name = "ElapsedTime" )
    public class ElapsedTime implements IASN1PreparedElement {
            @ASN1Integer( name = "" )
    @ASN1ValueRangeConstraint ( 
		
		min = 0L, 
		
		max = 86400L 
		
	   )
	   
        @ASN1Element ( name = "timeGPRSIfNoTariffSwitch", isOptional =  false , hasTag =  true, tag = 0 , hasDefaultValue =  false  )
    
	private Integer timeGPRSIfNoTariffSwitch = null;
                
  

       @ASN1PreparedElement
       @ASN1Sequence ( name = "timeGPRSIfTariffSwitch" , isSet = false )
       public static class TimeGPRSIfTariffSwitchSequenceType implements IASN1PreparedElement {
                @ASN1Integer( name = "" )
    @ASN1ValueRangeConstraint ( 
		
		min = 0L, 
		
		max = 86400L 
		
	   )
	   
        @ASN1Element ( name = "timeGPRSSinceLastTariffSwitch", isOptional =  false , hasTag =  true, tag = 0 , hasDefaultValue =  false  )
    
	private Integer timeGPRSSinceLastTariffSwitch = null;
                
  @ASN1Integer( name = "" )
    @ASN1ValueRangeConstraint ( 
		
		min = 0L, 
		
		max = 86400L 
		
	   )
	   
        @ASN1Element ( name = "timeGPRSTariffSwitchInterval", isOptional =  true , hasTag =  true, tag = 1 , hasDefaultValue =  false  )
    
	private Integer timeGPRSTariffSwitchInterval = null;
                
  
        
        public Integer getTimeGPRSSinceLastTariffSwitch () {
            return this.timeGPRSSinceLastTariffSwitch;
        }

        

        public void setTimeGPRSSinceLastTariffSwitch (Integer value) {
            this.timeGPRSSinceLastTariffSwitch = value;
        }
        
  
        
        public Integer getTimeGPRSTariffSwitchInterval () {
            return this.timeGPRSTariffSwitchInterval;
        }

        
        public boolean isTimeGPRSTariffSwitchIntervalPresent () {
            return this.timeGPRSTariffSwitchInterval != null;
        }
        

        public void setTimeGPRSTariffSwitchInterval (Integer value) {
            this.timeGPRSTariffSwitchInterval = value;
        }
        
  
                
                
        public void initWithDefaults() {
            
        }

        public IASN1PreparedElementData getPreparedData() {
            return preparedData_TimeGPRSIfTariffSwitchSequenceType;
        }

       private static IASN1PreparedElementData preparedData_TimeGPRSIfTariffSwitchSequenceType = CoderFactory.getInstance().newPreparedElementData(TimeGPRSIfTariffSwitchSequenceType.class);
                
       }

       
                
        @ASN1Element ( name = "timeGPRSIfTariffSwitch", isOptional =  false , hasTag =  true, tag = 1 , hasDefaultValue =  false  )
    
	private TimeGPRSIfTariffSwitchSequenceType timeGPRSIfTariffSwitch = null;
                
  
        
        public Integer getTimeGPRSIfNoTariffSwitch () {
            return this.timeGPRSIfNoTariffSwitch;
        }

        public boolean isTimeGPRSIfNoTariffSwitchSelected () {
            return this.timeGPRSIfNoTariffSwitch != null;
        }

        private void setTimeGPRSIfNoTariffSwitch (Integer value) {
            this.timeGPRSIfNoTariffSwitch = value;
        }

        
        public void selectTimeGPRSIfNoTariffSwitch (Integer value) {
            this.timeGPRSIfNoTariffSwitch = value;
            
                    setTimeGPRSIfTariffSwitch(null);
                            
        }

        
  
        
        public TimeGPRSIfTariffSwitchSequenceType getTimeGPRSIfTariffSwitch () {
            return this.timeGPRSIfTariffSwitch;
        }

        public boolean isTimeGPRSIfTariffSwitchSelected () {
            return this.timeGPRSIfTariffSwitch != null;
        }

        private void setTimeGPRSIfTariffSwitch (TimeGPRSIfTariffSwitchSequenceType value) {
            this.timeGPRSIfTariffSwitch = value;
        }

        
        public void selectTimeGPRSIfTariffSwitch (TimeGPRSIfTariffSwitchSequenceType value) {
            this.timeGPRSIfTariffSwitch = value;
            
                    setTimeGPRSIfNoTariffSwitch(null);
                            
        }

        
  

	    public void initWithDefaults() {
	    }

        private static IASN1PreparedElementData preparedData = CoderFactory.getInstance().newPreparedElementData(ElapsedTime.class);
        public IASN1PreparedElementData getPreparedData() {
            return preparedData;
        }


    }
            