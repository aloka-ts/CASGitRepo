
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
    @ASN1Sequence ( name = "ForwardServiceInteractionInd", isSet = false )
    public class ForwardServiceInteractionInd implements IASN1PreparedElement {
            @ASN1OctetString( name = "" )
    
            @ASN1SizeConstraint ( max = 1L )
        
        @ASN1Element ( name = "conferenceTreatmentIndicator", isOptional =  true , hasTag =  true, tag = 1 , hasDefaultValue =  false  )
    
	private byte[] conferenceTreatmentIndicator = null;
                
  @ASN1OctetString( name = "" )
    
            @ASN1SizeConstraint ( max = 1L )
        
        @ASN1Element ( name = "callDiversionTreatmentIndicator", isOptional =  true , hasTag =  true, tag = 2 , hasDefaultValue =  false  )
    
	private byte[] callDiversionTreatmentIndicator = null;
                
  @ASN1OctetString( name = "" )
    
            @ASN1SizeConstraint ( max = 1L )
        
        @ASN1Element ( name = "callingPartyRestrictionIndicator", isOptional =  true , hasTag =  true, tag = 4 , hasDefaultValue =  false  )
    
	private byte[] callingPartyRestrictionIndicator = null;
                
  
        
        public byte[] getConferenceTreatmentIndicator () {
            return this.conferenceTreatmentIndicator;
        }

        
        public boolean isConferenceTreatmentIndicatorPresent () {
            return this.conferenceTreatmentIndicator != null;
        }
        

        public void setConferenceTreatmentIndicator (byte[] value) {
            this.conferenceTreatmentIndicator = value;
        }
        
  
        
        public byte[] getCallDiversionTreatmentIndicator () {
            return this.callDiversionTreatmentIndicator;
        }

        
        public boolean isCallDiversionTreatmentIndicatorPresent () {
            return this.callDiversionTreatmentIndicator != null;
        }
        

        public void setCallDiversionTreatmentIndicator (byte[] value) {
            this.callDiversionTreatmentIndicator = value;
        }
        
  
        
        public byte[] getCallingPartyRestrictionIndicator () {
            return this.callingPartyRestrictionIndicator;
        }

        
        public boolean isCallingPartyRestrictionIndicatorPresent () {
            return this.callingPartyRestrictionIndicator != null;
        }
        

        public void setCallingPartyRestrictionIndicator (byte[] value) {
            this.callingPartyRestrictionIndicator = value;
        }
        
  
                    
        
        public void initWithDefaults() {
            
        }

        private static IASN1PreparedElementData preparedData = CoderFactory.getInstance().newPreparedElementData(ForwardServiceInteractionInd.class);
        public IASN1PreparedElementData getPreparedData() {
            return preparedData;
        }

            
    }
            