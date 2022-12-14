
package com.genband.inap.asngenerated;
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
    @ASN1Sequence ( name = "DisconnectForwardConnectionWithArgumentArg", isSet = false )
    public class DisconnectForwardConnectionWithArgumentArg implements IASN1PreparedElement {
            
        
    @ASN1PreparedElement
    @ASN1Choice ( name = "partyToDisconnect" )
    public static class PartyToDisconnectChoiceType implements IASN1PreparedElement {
            
        @ASN1Element ( name = "legID", isOptional =  false , hasTag =  true, tag = 0 , hasDefaultValue =  false  )
    
	private LegID legID = null;
                
  
        
        public LegID getLegID () {
            return this.legID;
        }

        public boolean isLegIDSelected () {
            return this.legID != null;
        }

        private void setLegID (LegID value) {
            this.legID = value;
        }

        
        public void selectLegID (LegID value) {
            this.legID = value;
                        
        }

        
  

	    public void initWithDefaults() {
	    }

        public IASN1PreparedElementData getPreparedData() {
            return preparedData_PartyToDisconnectChoiceType;
        }

        private static IASN1PreparedElementData preparedData_PartyToDisconnectChoiceType = CoderFactory.getInstance().newPreparedElementData(PartyToDisconnectChoiceType.class);

    }

                
        @ASN1Element ( name = "partyToDisconnect", isOptional =  false , hasTag =  false  , hasDefaultValue =  false  )
    
	private PartyToDisconnectChoiceType partyToDisconnect = null;
                
  
@ASN1SequenceOf( name = "", isSetOf = false ) 

    @ASN1ValueRangeConstraint ( 
		
		min = 1L, 
		
		max = 1L 
		
	   )
	   
        @ASN1Element ( name = "extensions", isOptional =  true , hasTag =  true, tag = 2 , hasDefaultValue =  false  )
    
	private java.util.Collection<ExtensionField>  extensions = null;
                
  
        
        public PartyToDisconnectChoiceType getPartyToDisconnect () {
            return this.partyToDisconnect;
        }

        

        public void setPartyToDisconnect (PartyToDisconnectChoiceType value) {
            this.partyToDisconnect = value;
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
            
        }

        private static IASN1PreparedElementData preparedData = CoderFactory.getInstance().newPreparedElementData(DisconnectForwardConnectionWithArgumentArg.class);
        public IASN1PreparedElementData getPreparedData() {
            return preparedData;
        }

            
    }
            