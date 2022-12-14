
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
    @ASN1Sequence ( name = "InitialDPArgExtension", isSet = false )
    public class InitialDPArgExtension implements IASN1PreparedElement {
            
        @ASN1Element ( name = "gmscAddress", isOptional =  true , hasTag =  true, tag = 0 , hasDefaultValue =  false  )
    
	private ISDN_AddressString gmscAddress = null;
                
  
        @ASN1Element ( name = "forwardingDestinationNumber", isOptional =  true , hasTag =  true, tag = 1 , hasDefaultValue =  false  )
    
	private CalledPartyNumber forwardingDestinationNumber = null;
                
  
        @ASN1Element ( name = "ms-Classmark2", isOptional =  true , hasTag =  true, tag = 2 , hasDefaultValue =  false  )
    
	private MS_Classmark2 ms_Classmark2 = null;
                
  
        @ASN1Element ( name = "iMEI", isOptional =  true , hasTag =  true, tag = 3 , hasDefaultValue =  false  )
    
	private IMEI iMEI = null;
                
  
        @ASN1Element ( name = "supportedCamelPhases", isOptional =  true , hasTag =  true, tag = 4 , hasDefaultValue =  false  )
    
	private SupportedCamelPhases supportedCamelPhases = null;
                
  
        @ASN1Element ( name = "offeredCamel4Functionalities", isOptional =  true , hasTag =  true, tag = 5 , hasDefaultValue =  false  )
    
	private OfferedCamel4Functionalities offeredCamel4Functionalities = null;
                
  
        @ASN1Element ( name = "bearerCapability2", isOptional =  true , hasTag =  true, tag = 6 , hasDefaultValue =  false  )
    
	private BearerCapability bearerCapability2 = null;
                
  
        @ASN1Element ( name = "ext-basicServiceCode2", isOptional =  true , hasTag =  true, tag = 7 , hasDefaultValue =  false  )
    
	private Ext_BasicServiceCode ext_basicServiceCode2 = null;
                
  
        @ASN1Element ( name = "highLayerCompatibility2", isOptional =  true , hasTag =  true, tag = 8 , hasDefaultValue =  false  )
    
	private HighLayerCompatibility highLayerCompatibility2 = null;
                
  
        @ASN1Element ( name = "lowLayerCompatibility", isOptional =  true , hasTag =  true, tag = 9 , hasDefaultValue =  false  )
    
	private LowLayerCompatibility lowLayerCompatibility = null;
                
  
        @ASN1Element ( name = "lowLayerCompatibility2", isOptional =  true , hasTag =  true, tag = 10 , hasDefaultValue =  false  )
    
	private LowLayerCompatibility lowLayerCompatibility2 = null;
                
  
        @ASN1Null ( name = "enhancedDialledServicesAllowed" ) 
    
        @ASN1Element ( name = "enhancedDialledServicesAllowed", isOptional =  true , hasTag =  true, tag = 11 , hasDefaultValue =  false  )
    
	private org.bn.types.NullObject enhancedDialledServicesAllowed = null;
                
  
        @ASN1Element ( name = "uu-Data", isOptional =  true , hasTag =  true, tag = 12 , hasDefaultValue =  false  )
    
	private UU_Data uu_Data = null;
                
  
        
        public ISDN_AddressString getGmscAddress () {
            return this.gmscAddress;
        }

        
        public boolean isGmscAddressPresent () {
            return this.gmscAddress != null;
        }
        

        public void setGmscAddress (ISDN_AddressString value) {
            this.gmscAddress = value;
        }
        
  
        
        public CalledPartyNumber getForwardingDestinationNumber () {
            return this.forwardingDestinationNumber;
        }

        
        public boolean isForwardingDestinationNumberPresent () {
            return this.forwardingDestinationNumber != null;
        }
        

        public void setForwardingDestinationNumber (CalledPartyNumber value) {
            this.forwardingDestinationNumber = value;
        }
        
  
        
        public MS_Classmark2 getMs_Classmark2 () {
            return this.ms_Classmark2;
        }

        
        public boolean isMs_Classmark2Present () {
            return this.ms_Classmark2 != null;
        }
        

        public void setMs_Classmark2 (MS_Classmark2 value) {
            this.ms_Classmark2 = value;
        }
        
  
        
        public IMEI getIMEI () {
            return this.iMEI;
        }

        
        public boolean isIMEIPresent () {
            return this.iMEI != null;
        }
        

        public void setIMEI (IMEI value) {
            this.iMEI = value;
        }
        
  
        
        public SupportedCamelPhases getSupportedCamelPhases () {
            return this.supportedCamelPhases;
        }

        
        public boolean isSupportedCamelPhasesPresent () {
            return this.supportedCamelPhases != null;
        }
        

        public void setSupportedCamelPhases (SupportedCamelPhases value) {
            this.supportedCamelPhases = value;
        }
        
  
        
        public OfferedCamel4Functionalities getOfferedCamel4Functionalities () {
            return this.offeredCamel4Functionalities;
        }

        
        public boolean isOfferedCamel4FunctionalitiesPresent () {
            return this.offeredCamel4Functionalities != null;
        }
        

        public void setOfferedCamel4Functionalities (OfferedCamel4Functionalities value) {
            this.offeredCamel4Functionalities = value;
        }
        
  
        
        public BearerCapability getBearerCapability2 () {
            return this.bearerCapability2;
        }

        
        public boolean isBearerCapability2Present () {
            return this.bearerCapability2 != null;
        }
        

        public void setBearerCapability2 (BearerCapability value) {
            this.bearerCapability2 = value;
        }
        
  
        
        public Ext_BasicServiceCode getExt_basicServiceCode2 () {
            return this.ext_basicServiceCode2;
        }

        
        public boolean isExt_basicServiceCode2Present () {
            return this.ext_basicServiceCode2 != null;
        }
        

        public void setExt_basicServiceCode2 (Ext_BasicServiceCode value) {
            this.ext_basicServiceCode2 = value;
        }
        
  
        
        public HighLayerCompatibility getHighLayerCompatibility2 () {
            return this.highLayerCompatibility2;
        }

        
        public boolean isHighLayerCompatibility2Present () {
            return this.highLayerCompatibility2 != null;
        }
        

        public void setHighLayerCompatibility2 (HighLayerCompatibility value) {
            this.highLayerCompatibility2 = value;
        }
        
  
        
        public LowLayerCompatibility getLowLayerCompatibility () {
            return this.lowLayerCompatibility;
        }

        
        public boolean isLowLayerCompatibilityPresent () {
            return this.lowLayerCompatibility != null;
        }
        

        public void setLowLayerCompatibility (LowLayerCompatibility value) {
            this.lowLayerCompatibility = value;
        }
        
  
        
        public LowLayerCompatibility getLowLayerCompatibility2 () {
            return this.lowLayerCompatibility2;
        }

        
        public boolean isLowLayerCompatibility2Present () {
            return this.lowLayerCompatibility2 != null;
        }
        

        public void setLowLayerCompatibility2 (LowLayerCompatibility value) {
            this.lowLayerCompatibility2 = value;
        }
        
  
        
        public UU_Data getUu_Data () {
            return this.uu_Data;
        }

        
        public boolean isUu_DataPresent () {
            return this.uu_Data != null;
        }
        

        public void setUu_Data (UU_Data value) {
            this.uu_Data = value;
        }
        
  
                    
        
        public void initWithDefaults() {
            
        }

        private static IASN1PreparedElementData preparedData = CoderFactory.getInstance().newPreparedElementData(InitialDPArgExtension.class);
        public IASN1PreparedElementData getPreparedData() {
            return preparedData;
        }

            
    }
            