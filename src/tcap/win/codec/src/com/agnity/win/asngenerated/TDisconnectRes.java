
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
    @ASN1BoxedType ( name = "TDisconnectRes" )
    public class TDisconnectRes implements IASN1PreparedElement {
                
        

       @ASN1PreparedElement
       @ASN1Sequence ( name = "TDisconnectRes" , isSet = true )
       public static class TDisconnectResSequenceType implements IASN1PreparedElement {
                
        @ASN1Element ( name = "dmh-ServiceID", isOptional =  true , hasTag =  true, tag = 305 , hasDefaultValue =  false  )
    
	private DMH_ServiceID dmh_ServiceID = null;
                
  
        
        public DMH_ServiceID getDmh_ServiceID () {
            return this.dmh_ServiceID;
        }

        
        public boolean isDmh_ServiceIDPresent () {
            return this.dmh_ServiceID != null;
        }
        

        public void setDmh_ServiceID (DMH_ServiceID value) {
            this.dmh_ServiceID = value;
        }
        
  
                
                
        public void initWithDefaults() {
            
        }

        public IASN1PreparedElementData getPreparedData() {
            return preparedData_TDisconnectResSequenceType;
        }

       private static IASN1PreparedElementData preparedData_TDisconnectResSequenceType = CoderFactory.getInstance().newPreparedElementData(TDisconnectResSequenceType.class);
                
       }

       
                
        @ASN1Element ( name = "TDisconnectRes", isOptional =  false , hasTag =  true, tag = 18, 
        tagClass =  TagClass.Private  , hasDefaultValue =  false  )
    
        private TDisconnectResSequenceType  value;        

        
        
        public TDisconnectRes () {
        }
        
        
        
        public void setValue(TDisconnectResSequenceType value) {
            this.value = value;
        }
        
        
        
        public TDisconnectResSequenceType getValue() {
            return this.value;
        }            
        

	    public void initWithDefaults() {
	    }

        private static IASN1PreparedElementData preparedData = CoderFactory.getInstance().newPreparedElementData(TDisconnectRes.class);
        public IASN1PreparedElementData getPreparedData() {
            return preparedData;
        }

            
    }
            