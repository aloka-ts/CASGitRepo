
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
    @ASN1Sequence ( name = "TimeIfTariffSwitch", isSet = false )
    public class TimeIfTariffSwitch implements IASN1PreparedElement {
            @ASN1Integer( name = "" )
    @ASN1ValueRangeConstraint ( 
		
		min = 0L, 
		
		max = 864000L 
		
	   )
	   
        @ASN1Element ( name = "timeSinceTariffSwitch", isOptional =  false , hasTag =  true, tag = 0 , hasDefaultValue =  false  )
    
	private Integer timeSinceTariffSwitch = null;
                
  @ASN1Integer( name = "" )
    @ASN1ValueRangeConstraint ( 
		
		min = 1L, 
		
		max = 864000L 
		
	   )
	   
        @ASN1Element ( name = "tariffSwitchInterval", isOptional =  true , hasTag =  true, tag = 1 , hasDefaultValue =  false  )
    
	private Integer tariffSwitchInterval = null;
                
  
        
        public Integer getTimeSinceTariffSwitch () {
            return this.timeSinceTariffSwitch;
        }

        

        public void setTimeSinceTariffSwitch (Integer value) {
            this.timeSinceTariffSwitch = value;
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
        
  
                    
        
        public void initWithDefaults() {
            
        }

        private static IASN1PreparedElementData preparedData = CoderFactory.getInstance().newPreparedElementData(TimeIfTariffSwitch.class);
        public IASN1PreparedElementData getPreparedData() {
            return preparedData;
        }

            
    }
            