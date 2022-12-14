
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
    @ASN1Choice ( name = "TransferredVolume" )
    public class TransferredVolume implements IASN1PreparedElement {
            @ASN1Integer( name = "" )
    @ASN1ValueRangeConstraint ( 
		
		min = 0L, 
		
		max = 2147483647L 
		
	   )
	   
        @ASN1Element ( name = "volumeIfNoTariffSwitch", isOptional =  false , hasTag =  true, tag = 0 , hasDefaultValue =  false  )
    
	private Integer volumeIfNoTariffSwitch = null;
                
  

       @ASN1PreparedElement
       @ASN1Sequence ( name = "volumeIfTariffSwitch" , isSet = false )
       public static class VolumeIfTariffSwitchSequenceType implements IASN1PreparedElement {
                @ASN1Integer( name = "" )
    @ASN1ValueRangeConstraint ( 
		
		min = 0L, 
		
		max = 2147483647L 
		
	   )
	   
        @ASN1Element ( name = "volumeSinceLastTariffSwitch", isOptional =  false , hasTag =  true, tag = 0 , hasDefaultValue =  false  )
    
	private Integer volumeSinceLastTariffSwitch = null;
                
  @ASN1Integer( name = "" )
    @ASN1ValueRangeConstraint ( 
		
		min = 0L, 
		
		max = 2147483647L 
		
	   )
	   
        @ASN1Element ( name = "volumeTariffSwitchInterval", isOptional =  true , hasTag =  true, tag = 1 , hasDefaultValue =  false  )
    
	private Integer volumeTariffSwitchInterval = null;
                
  
        
        public Integer getVolumeSinceLastTariffSwitch () {
            return this.volumeSinceLastTariffSwitch;
        }

        

        public void setVolumeSinceLastTariffSwitch (Integer value) {
            this.volumeSinceLastTariffSwitch = value;
        }
        
  
        
        public Integer getVolumeTariffSwitchInterval () {
            return this.volumeTariffSwitchInterval;
        }

        
        public boolean isVolumeTariffSwitchIntervalPresent () {
            return this.volumeTariffSwitchInterval != null;
        }
        

        public void setVolumeTariffSwitchInterval (Integer value) {
            this.volumeTariffSwitchInterval = value;
        }
        
  
                
                
        public void initWithDefaults() {
            
        }

        public IASN1PreparedElementData getPreparedData() {
            return preparedData_VolumeIfTariffSwitchSequenceType;
        }

       private static IASN1PreparedElementData preparedData_VolumeIfTariffSwitchSequenceType = CoderFactory.getInstance().newPreparedElementData(VolumeIfTariffSwitchSequenceType.class);
                
       }

       
                
        @ASN1Element ( name = "volumeIfTariffSwitch", isOptional =  false , hasTag =  true, tag = 1 , hasDefaultValue =  false  )
    
	private VolumeIfTariffSwitchSequenceType volumeIfTariffSwitch = null;
                
  
        
        public Integer getVolumeIfNoTariffSwitch () {
            return this.volumeIfNoTariffSwitch;
        }

        public boolean isVolumeIfNoTariffSwitchSelected () {
            return this.volumeIfNoTariffSwitch != null;
        }

        private void setVolumeIfNoTariffSwitch (Integer value) {
            this.volumeIfNoTariffSwitch = value;
        }

        
        public void selectVolumeIfNoTariffSwitch (Integer value) {
            this.volumeIfNoTariffSwitch = value;
            
                    setVolumeIfTariffSwitch(null);
                            
        }

        
  
        
        public VolumeIfTariffSwitchSequenceType getVolumeIfTariffSwitch () {
            return this.volumeIfTariffSwitch;
        }

        public boolean isVolumeIfTariffSwitchSelected () {
            return this.volumeIfTariffSwitch != null;
        }

        private void setVolumeIfTariffSwitch (VolumeIfTariffSwitchSequenceType value) {
            this.volumeIfTariffSwitch = value;
        }

        
        public void selectVolumeIfTariffSwitch (VolumeIfTariffSwitchSequenceType value) {
            this.volumeIfTariffSwitch = value;
            
                    setVolumeIfNoTariffSwitch(null);
                            
        }

        
  

	    public void initWithDefaults() {
	    }

        private static IASN1PreparedElementData preparedData = CoderFactory.getInstance().newPreparedElementData(TransferredVolume.class);
        public IASN1PreparedElementData getPreparedData() {
            return preparedData;
        }


    }
            