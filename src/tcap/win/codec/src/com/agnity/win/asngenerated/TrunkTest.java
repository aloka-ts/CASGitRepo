
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
    @ASN1BoxedType ( name = "TrunkTest" )
    public class TrunkTest implements IASN1PreparedElement {
                
        

       @ASN1PreparedElement
       @ASN1Sequence ( name = "TrunkTest" , isSet = true )
       public static class TrunkTestSequenceType implements IASN1PreparedElement {
                
        @ASN1Element ( name = "interMSCCircuitID", isOptional =  false , hasTag =  true, tag = 6 , hasDefaultValue =  false  )
    
	private InterMSCCircuitID interMSCCircuitID = null;
                
  
        @ASN1Element ( name = "seizureType", isOptional =  false , hasTag =  true, tag = 15 , hasDefaultValue =  false  )
    
	private SeizureType seizureType = null;
                
  
        
        public InterMSCCircuitID getInterMSCCircuitID () {
            return this.interMSCCircuitID;
        }

        

        public void setInterMSCCircuitID (InterMSCCircuitID value) {
            this.interMSCCircuitID = value;
        }
        
  
        
        public SeizureType getSeizureType () {
            return this.seizureType;
        }

        

        public void setSeizureType (SeizureType value) {
            this.seizureType = value;
        }
        
  
                
                
        public void initWithDefaults() {
            
        }

        public IASN1PreparedElementData getPreparedData() {
            return preparedData_TrunkTestSequenceType;
        }

       private static IASN1PreparedElementData preparedData_TrunkTestSequenceType = CoderFactory.getInstance().newPreparedElementData(TrunkTestSequenceType.class);
                
       }

       
                
        @ASN1Element ( name = "TrunkTest", isOptional =  false , hasTag =  true, tag = 18, 
        tagClass =  TagClass.Private  , hasDefaultValue =  false  )
    
        private TrunkTestSequenceType  value;        

        
        
        public TrunkTest () {
        }
        
        
        
        public void setValue(TrunkTestSequenceType value) {
            this.value = value;
        }
        
        
        
        public TrunkTestSequenceType getValue() {
            return this.value;
        }            
        

	    public void initWithDefaults() {
	    }

        private static IASN1PreparedElementData preparedData = CoderFactory.getInstance().newPreparedElementData(TrunkTest.class);
        public IASN1PreparedElementData getPreparedData() {
            return preparedData;
        }

            
    }
            