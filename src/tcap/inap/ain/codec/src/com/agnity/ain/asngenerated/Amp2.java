
package com.agnity.ain.asngenerated;
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
    @ASN1BoxedType ( name = "Amp2" )
    public class Amp2 implements IASN1PreparedElement {
                
        

       @ASN1PreparedElement
       @ASN1Sequence ( name = "Amp2" , isSet = false )
       public static class Amp2SequenceType implements IASN1PreparedElement {
                
        @ASN1Element ( name = "ampAINNodeID", isOptional =  false , hasTag =  false  , hasDefaultValue =  false  )
    
	private AmpAINNodeID ampAINNodeID = null;
                
  
        @ASN1Element ( name = "ampCLogSeqNo", isOptional =  true , hasTag =  true, tag = 3 , hasDefaultValue =  false  )
    
	private AmpCLogSeqNo ampCLogSeqNo = null;
                
  
        @ASN1Element ( name = "ampCLogRepInd", isOptional =  true , hasTag =  true, tag = 4 , hasDefaultValue =  false  )
    
	private AmpCLogRepInd ampCLogRepInd = null;
                
  
        @ASN1Element ( name = "ampCallProgInd", isOptional =  true , hasTag =  true, tag = 5 , hasDefaultValue =  false  )
    
	private AmpCallProgInd ampCallProgInd = null;
                
  
        @ASN1Element ( name = "ampTestReqInd", isOptional =  true , hasTag =  true, tag = 6 , hasDefaultValue =  false  )
    
	private AmpTestReqInd ampTestReqInd = null;
                
  
        @ASN1Element ( name = "ampCLogName", isOptional =  true , hasTag =  true, tag = 7 , hasDefaultValue =  false  )
    
	private AmpCLogName ampCLogName = null;
                
  
        @ASN1Element ( name = "ampSvcProvID", isOptional =  true , hasTag =  true, tag = 8 , hasDefaultValue =  false  )
    
	private AmpSvcProvID ampSvcProvID = null;
                
  
        
        public AmpAINNodeID getAmpAINNodeID () {
            return this.ampAINNodeID;
        }

        

        public void setAmpAINNodeID (AmpAINNodeID value) {
            this.ampAINNodeID = value;
        }
        
  
        
        public AmpCLogSeqNo getAmpCLogSeqNo () {
            return this.ampCLogSeqNo;
        }

        
        public boolean isAmpCLogSeqNoPresent () {
            return this.ampCLogSeqNo != null;
        }
        

        public void setAmpCLogSeqNo (AmpCLogSeqNo value) {
            this.ampCLogSeqNo = value;
        }
        
  
        
        public AmpCLogRepInd getAmpCLogRepInd () {
            return this.ampCLogRepInd;
        }

        
        public boolean isAmpCLogRepIndPresent () {
            return this.ampCLogRepInd != null;
        }
        

        public void setAmpCLogRepInd (AmpCLogRepInd value) {
            this.ampCLogRepInd = value;
        }
        
  
        
        public AmpCallProgInd getAmpCallProgInd () {
            return this.ampCallProgInd;
        }

        
        public boolean isAmpCallProgIndPresent () {
            return this.ampCallProgInd != null;
        }
        

        public void setAmpCallProgInd (AmpCallProgInd value) {
            this.ampCallProgInd = value;
        }
        
  
        
        public AmpTestReqInd getAmpTestReqInd () {
            return this.ampTestReqInd;
        }

        
        public boolean isAmpTestReqIndPresent () {
            return this.ampTestReqInd != null;
        }
        

        public void setAmpTestReqInd (AmpTestReqInd value) {
            this.ampTestReqInd = value;
        }
        
  
        
        public AmpCLogName getAmpCLogName () {
            return this.ampCLogName;
        }

        
        public boolean isAmpCLogNamePresent () {
            return this.ampCLogName != null;
        }
        

        public void setAmpCLogName (AmpCLogName value) {
            this.ampCLogName = value;
        }
        
  
        
        public AmpSvcProvID getAmpSvcProvID () {
            return this.ampSvcProvID;
        }

        
        public boolean isAmpSvcProvIDPresent () {
            return this.ampSvcProvID != null;
        }
        

        public void setAmpSvcProvID (AmpSvcProvID value) {
            this.ampSvcProvID = value;
        }
        
  
                
                
        public void initWithDefaults() {
            
        }

        public IASN1PreparedElementData getPreparedData() {
            return preparedData_Amp2SequenceType;
        }

       private static IASN1PreparedElementData preparedData_Amp2SequenceType = CoderFactory.getInstance().newPreparedElementData(Amp2SequenceType.class);
                
       }

       
                
        @ASN1Element ( name = "Amp2", isOptional =  false , hasTag =  true, tag = 109 , hasDefaultValue =  false  )
    
        private Amp2SequenceType  value;        

        
        
        public Amp2 () {
        }
        
        
        
        public void setValue(Amp2SequenceType value) {
            this.value = value;
        }
        
        
        
        public Amp2SequenceType getValue() {
            return this.value;
        }            
        

	    public void initWithDefaults() {
	    }

        private static IASN1PreparedElementData preparedData = CoderFactory.getInstance().newPreparedElementData(Amp2.class);
        public IASN1PreparedElementData getPreparedData() {
            return preparedData;
        }

            
    }
            