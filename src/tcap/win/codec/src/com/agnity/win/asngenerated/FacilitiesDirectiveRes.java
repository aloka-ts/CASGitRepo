
package com.agnity.win.asngenerated;
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
    @ASN1BoxedType ( name = "FacilitiesDirectiveRes" )
    public class FacilitiesDirectiveRes implements IASN1PreparedElement {
                
        

       @ASN1PreparedElement
       @ASN1Sequence ( name = "FacilitiesDirectiveRes" , isSet = true )
       public static class FacilitiesDirectiveResSequenceType implements IASN1PreparedElement {
                
        @ASN1Element ( name = "channelData", isOptional =  false , hasTag =  true, tag = 5 , hasDefaultValue =  false  )
    
	private ChannelData channelData = null;
                
  
        @ASN1Element ( name = "confidentialityModes", isOptional =  true , hasTag =  true, tag = 39 , hasDefaultValue =  false  )
    
	private ConfidentialityModes confidentialityModes = null;
                
  
        @ASN1Element ( name = "tdmaBurstIndicator", isOptional =  true , hasTag =  true, tag = 31 , hasDefaultValue =  false  )
    
	private TDMABurstIndicator tdmaBurstIndicator = null;
                
  
        @ASN1Element ( name = "tdmaChannelData", isOptional =  true , hasTag =  true, tag = 28 , hasDefaultValue =  false  )
    
	private TDMAChannelData tdmaChannelData = null;
                
  
        
        public ChannelData getChannelData () {
            return this.channelData;
        }

        

        public void setChannelData (ChannelData value) {
            this.channelData = value;
        }
        
  
        
        public ConfidentialityModes getConfidentialityModes () {
            return this.confidentialityModes;
        }

        
        public boolean isConfidentialityModesPresent () {
            return this.confidentialityModes != null;
        }
        

        public void setConfidentialityModes (ConfidentialityModes value) {
            this.confidentialityModes = value;
        }
        
  
        
        public TDMABurstIndicator getTdmaBurstIndicator () {
            return this.tdmaBurstIndicator;
        }

        
        public boolean isTdmaBurstIndicatorPresent () {
            return this.tdmaBurstIndicator != null;
        }
        

        public void setTdmaBurstIndicator (TDMABurstIndicator value) {
            this.tdmaBurstIndicator = value;
        }
        
  
        
        public TDMAChannelData getTdmaChannelData () {
            return this.tdmaChannelData;
        }

        
        public boolean isTdmaChannelDataPresent () {
            return this.tdmaChannelData != null;
        }
        

        public void setTdmaChannelData (TDMAChannelData value) {
            this.tdmaChannelData = value;
        }
        
  
                
                
        public void initWithDefaults() {
            
        }

        public IASN1PreparedElementData getPreparedData() {
            return preparedData_FacilitiesDirectiveResSequenceType;
        }

       private static IASN1PreparedElementData preparedData_FacilitiesDirectiveResSequenceType = CoderFactory.getInstance().newPreparedElementData(FacilitiesDirectiveResSequenceType.class);
                
       }

       
                
        @ASN1Element ( name = "FacilitiesDirectiveRes", isOptional =  false , hasTag =  true, tag = 18, 
        tagClass =  TagClass.Private  , hasDefaultValue =  false  )
    
        private FacilitiesDirectiveResSequenceType  value;        

        
        
        public FacilitiesDirectiveRes () {
        }
        
        
        
        public void setValue(FacilitiesDirectiveResSequenceType value) {
            this.value = value;
        }
        
        
        
        public FacilitiesDirectiveResSequenceType getValue() {
            return this.value;
        }            
        

	    public void initWithDefaults() {
	    }

        private static IASN1PreparedElementData preparedData = CoderFactory.getInstance().newPreparedElementData(FacilitiesDirectiveRes.class);
        public IASN1PreparedElementData getPreparedData() {
            return preparedData;
        }

            
    }
            