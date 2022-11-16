
package asnGenerated.v2;
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
    @ASN1Sequence ( name = "ApplyChargingArg", isSet = false )
    public class ApplyChargingArg implements IASN1PreparedElement {
            
        @ASN1Element ( name = "aChBillingChargingCharacteristics", isOptional =  false , hasTag =  true, tag = 0 , hasDefaultValue =  false  )
    
	private AChBillingChargingCharacteristics aChBillingChargingCharacteristics = null;
                
  
        @ASN1Element ( name = "partyToCharge", isOptional =  false , hasTag =  true, tag = 2 , hasDefaultValue =  true  )
    
	private SendingSideID partyToCharge = null;
                
  
@ASN1SequenceOf( name = "extensions", isSetOf = false ) 

    @ASN1ValueRangeConstraint ( 
		
		min = 1L, 
		
		max = 10L 
		
	   )
	   
        @ASN1Element ( name = "extensions", isOptional =  true , hasTag =  true, tag = 3 , hasDefaultValue =  false  )
    
	private java.util.Collection<ExtensionField>  extensions = null;
                
  
        
        public AChBillingChargingCharacteristics getAChBillingChargingCharacteristics () {
            return this.aChBillingChargingCharacteristics;
        }

        

        public void setAChBillingChargingCharacteristics (AChBillingChargingCharacteristics value) {
            this.aChBillingChargingCharacteristics = value;
        }
        
  
        
        public SendingSideID getPartyToCharge () {
            return this.partyToCharge;
        }

        

        public void setPartyToCharge (SendingSideID value) {
            this.partyToCharge = value;
        }
        
  
        
        public java.util.Collection<ExtensionField>  getExtensions () {
            return this.extensions;
        }

        
        public boolean isExtensionsPresent () {
            return this.extensions != null;
        }
        

        public void setExtensions (java.util.Collection<ExtensionField>  value) {
            this.extensions = value;
        }
        
  
                    
        
        public void initWithDefaults() {
            SendingSideID param_PartyToCharge =         
            null;
        setPartyToCharge(param_PartyToCharge);
    
        }

        private static IASN1PreparedElementData preparedData = CoderFactory.getInstance().newPreparedElementData(ApplyChargingArg.class);
        public IASN1PreparedElementData getPreparedData() {
            return preparedData;
        }

            
    }
            