
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
    @ASN1Choice ( name = "CAMEL_FCIBillingChargingCharacteristics" )
    public class CAMEL_FCIBillingChargingCharacteristics implements IASN1PreparedElement {
            

       @ASN1PreparedElement
       @ASN1Sequence ( name = "fCIBCCCAMELsequence1" , isSet = false )
       public static class FCIBCCCAMELsequence1SequenceType implements IASN1PreparedElement {
                @ASN1OctetString( name = "" )
    @ASN1ValueRangeConstraint ( 
		
		min = 1L, 
		
		max = 40L 
		
	   )
	   
        @ASN1Element ( name = "freeFormatData", isOptional =  false , hasTag =  true, tag = 0 , hasDefaultValue =  false  )
    
	private byte[] freeFormatData = null;
                
  
        @ASN1Element ( name = "partyToCharge", isOptional =  false , hasTag =  true, tag = 1 , hasDefaultValue =  true  )
    
	private SendingSideID partyToCharge = null;
                
  
        
        public byte[] getFreeFormatData () {
            return this.freeFormatData;
        }

        

        public void setFreeFormatData (byte[] value) {
            this.freeFormatData = value;
        }
        
  
        
        public SendingSideID getPartyToCharge () {
            return this.partyToCharge;
        }

        

        public void setPartyToCharge (SendingSideID value) {
            this.partyToCharge = value;
        }
        
  
                
                
        public void initWithDefaults() {
            SendingSideID param_PartyToCharge =         
            null;
        setPartyToCharge(param_PartyToCharge);
    
        }

        public IASN1PreparedElementData getPreparedData() {
            return preparedData_FCIBCCCAMELsequence1SequenceType;
        }

       private static IASN1PreparedElementData preparedData_FCIBCCCAMELsequence1SequenceType = CoderFactory.getInstance().newPreparedElementData(FCIBCCCAMELsequence1SequenceType.class);
                
       }

       
                
        @ASN1Element ( name = "fCIBCCCAMELsequence1", isOptional =  false , hasTag =  true, tag = 0 , hasDefaultValue =  false  )
    
	private FCIBCCCAMELsequence1SequenceType fCIBCCCAMELsequence1 = null;
                
  
        
        public FCIBCCCAMELsequence1SequenceType getFCIBCCCAMELsequence1 () {
            return this.fCIBCCCAMELsequence1;
        }

        public boolean isFCIBCCCAMELsequence1Selected () {
            return this.fCIBCCCAMELsequence1 != null;
        }

        private void setFCIBCCCAMELsequence1 (FCIBCCCAMELsequence1SequenceType value) {
            this.fCIBCCCAMELsequence1 = value;
        }

        
        public void selectFCIBCCCAMELsequence1 (FCIBCCCAMELsequence1SequenceType value) {
            this.fCIBCCCAMELsequence1 = value;
                        
        }

        
  

	    public void initWithDefaults() {
	    }

        private static IASN1PreparedElementData preparedData = CoderFactory.getInstance().newPreparedElementData(CAMEL_FCIBillingChargingCharacteristics.class);
        public IASN1PreparedElementData getPreparedData() {
            return preparedData;
        }


    }
            