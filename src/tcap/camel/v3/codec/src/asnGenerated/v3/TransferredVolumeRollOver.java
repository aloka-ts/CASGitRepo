
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
    @ASN1Choice ( name = "TransferredVolumeRollOver" )
    public class TransferredVolumeRollOver implements IASN1PreparedElement {
            @ASN1Integer( name = "" )
    @ASN1ValueRangeConstraint ( 
		
		min = 0L, 
		
		max = 255L 
		
	   )
	   
        @ASN1Element ( name = "rOVolumeIfNoTariffSwitch", isOptional =  false , hasTag =  true, tag = 0 , hasDefaultValue =  false  )
    
	private Integer rOVolumeIfNoTariffSwitch = null;
                
  

       @ASN1PreparedElement
       @ASN1Sequence ( name = "rOVolumeIfTariffSwitch" , isSet = false )
       public static class ROVolumeIfTariffSwitchSequenceType implements IASN1PreparedElement {
                @ASN1Integer( name = "" )
    @ASN1ValueRangeConstraint ( 
		
		min = 0L, 
		
		max = 255L 
		
	   )
	   
        @ASN1Element ( name = "rOVolumeSinceLastTariffSwitch", isOptional =  true , hasTag =  true, tag = 0 , hasDefaultValue =  false  )
    
	private Integer rOVolumeSinceLastTariffSwitch = null;
                
  @ASN1Integer( name = "" )
    @ASN1ValueRangeConstraint ( 
		
		min = 0L, 
		
		max = 255L 
		
	   )
	   
        @ASN1Element ( name = "rOVolumeTariffSwitchInterval", isOptional =  true , hasTag =  true, tag = 1 , hasDefaultValue =  false  )
    
	private Integer rOVolumeTariffSwitchInterval = null;
                
  
        
        public Integer getROVolumeSinceLastTariffSwitch () {
            return this.rOVolumeSinceLastTariffSwitch;
        }

        
        public boolean isROVolumeSinceLastTariffSwitchPresent () {
            return this.rOVolumeSinceLastTariffSwitch != null;
        }
        

        public void setROVolumeSinceLastTariffSwitch (Integer value) {
            this.rOVolumeSinceLastTariffSwitch = value;
        }
        
  
        
        public Integer getROVolumeTariffSwitchInterval () {
            return this.rOVolumeTariffSwitchInterval;
        }

        
        public boolean isROVolumeTariffSwitchIntervalPresent () {
            return this.rOVolumeTariffSwitchInterval != null;
        }
        

        public void setROVolumeTariffSwitchInterval (Integer value) {
            this.rOVolumeTariffSwitchInterval = value;
        }
        
  
                
                
        public void initWithDefaults() {
            
        }

        public IASN1PreparedElementData getPreparedData() {
            return preparedData_ROVolumeIfTariffSwitchSequenceType;
        }

       private static IASN1PreparedElementData preparedData_ROVolumeIfTariffSwitchSequenceType = CoderFactory.getInstance().newPreparedElementData(ROVolumeIfTariffSwitchSequenceType.class);
                
       }

       
                
        @ASN1Element ( name = "rOVolumeIfTariffSwitch", isOptional =  false , hasTag =  true, tag = 1 , hasDefaultValue =  false  )
    
	private ROVolumeIfTariffSwitchSequenceType rOVolumeIfTariffSwitch = null;
                
  
        
        public Integer getROVolumeIfNoTariffSwitch () {
            return this.rOVolumeIfNoTariffSwitch;
        }

        public boolean isROVolumeIfNoTariffSwitchSelected () {
            return this.rOVolumeIfNoTariffSwitch != null;
        }

        private void setROVolumeIfNoTariffSwitch (Integer value) {
            this.rOVolumeIfNoTariffSwitch = value;
        }

        
        public void selectROVolumeIfNoTariffSwitch (Integer value) {
            this.rOVolumeIfNoTariffSwitch = value;
            
                    setROVolumeIfTariffSwitch(null);
                            
        }

        
  
        
        public ROVolumeIfTariffSwitchSequenceType getROVolumeIfTariffSwitch () {
            return this.rOVolumeIfTariffSwitch;
        }

        public boolean isROVolumeIfTariffSwitchSelected () {
            return this.rOVolumeIfTariffSwitch != null;
        }

        private void setROVolumeIfTariffSwitch (ROVolumeIfTariffSwitchSequenceType value) {
            this.rOVolumeIfTariffSwitch = value;
        }

        
        public void selectROVolumeIfTariffSwitch (ROVolumeIfTariffSwitchSequenceType value) {
            this.rOVolumeIfTariffSwitch = value;
            
                    setROVolumeIfNoTariffSwitch(null);
                            
        }

        
  

	    public void initWithDefaults() {
	    }

        private static IASN1PreparedElementData preparedData = CoderFactory.getInstance().newPreparedElementData(TransferredVolumeRollOver.class);
        public IASN1PreparedElementData getPreparedData() {
            return preparedData;
        }


    }
            