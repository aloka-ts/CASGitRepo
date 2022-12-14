
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
    @ASN1Sequence ( name = "EstablishTemporaryConnectionArg", isSet = false )
    public class EstablishTemporaryConnectionArg implements IASN1PreparedElement {
            
        @ASN1Element ( name = "assistingSSPIPRoutingAddress", isOptional =  false , hasTag =  true, tag = 0 , hasDefaultValue =  false  )
    
	private AssistingSSPIPRoutingAddress assistingSSPIPRoutingAddress = null;
                
  
        @ASN1Element ( name = "correlationID", isOptional =  true , hasTag =  true, tag = 1 , hasDefaultValue =  false  )
    
	private CorrelationID correlationID = null;
                
  
        
    @ASN1PreparedElement
    @ASN1Choice ( name = "partyToConnect" )
    public static class PartyToConnectChoiceType implements IASN1PreparedElement {
            
        @ASN1Element ( name = "legID", isOptional =  false , hasTag =  true, tag = 2 , hasDefaultValue =  false  )
    
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
            return preparedData_PartyToConnectChoiceType;
        }

        private static IASN1PreparedElementData preparedData_PartyToConnectChoiceType = CoderFactory.getInstance().newPreparedElementData(PartyToConnectChoiceType.class);

    }

                
        @ASN1Element ( name = "partyToConnect", isOptional =  true , hasTag =  false  , hasDefaultValue =  false  )
    
	private PartyToConnectChoiceType partyToConnect = null;
                
  
        @ASN1Element ( name = "scfID", isOptional =  true , hasTag =  true, tag = 3 , hasDefaultValue =  false  )
    
	private ScfID scfID = null;
                
  
@ASN1SequenceOf( name = "", isSetOf = false ) 

    @ASN1ValueRangeConstraint ( 
		
		min = 1L, 
		
		max = 1L 
		
	   )
	   
        @ASN1Element ( name = "extensions", isOptional =  true , hasTag =  true, tag = 4 , hasDefaultValue =  false  )
    
	private java.util.Collection<ExtensionField>  extensions = null;
                
  
        
        public AssistingSSPIPRoutingAddress getAssistingSSPIPRoutingAddress () {
            return this.assistingSSPIPRoutingAddress;
        }

        

        public void setAssistingSSPIPRoutingAddress (AssistingSSPIPRoutingAddress value) {
            this.assistingSSPIPRoutingAddress = value;
        }
        
  
        
        public CorrelationID getCorrelationID () {
            return this.correlationID;
        }

        
        public boolean isCorrelationIDPresent () {
            return this.correlationID != null;
        }
        

        public void setCorrelationID (CorrelationID value) {
            this.correlationID = value;
        }
        
  
        
        public PartyToConnectChoiceType getPartyToConnect () {
            return this.partyToConnect;
        }

        
        public boolean isPartyToConnectPresent () {
            return this.partyToConnect != null;
        }
        

        public void setPartyToConnect (PartyToConnectChoiceType value) {
            this.partyToConnect = value;
        }
        
  
        
        public ScfID getScfID () {
            return this.scfID;
        }

        
        public boolean isScfIDPresent () {
            return this.scfID != null;
        }
        

        public void setScfID (ScfID value) {
            this.scfID = value;
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

        private static IASN1PreparedElementData preparedData = CoderFactory.getInstance().newPreparedElementData(EstablishTemporaryConnectionArg.class);
        public IASN1PreparedElementData getPreparedData() {
            return preparedData;
        }

            
    }
            