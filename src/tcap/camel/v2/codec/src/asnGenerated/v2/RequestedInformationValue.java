
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
    @ASN1Choice ( name = "RequestedInformationValue" )
    public class RequestedInformationValue implements IASN1PreparedElement {
            @ASN1Integer( name = "" )
    @ASN1ValueRangeConstraint ( 
		
		min = 0L, 
		
		max = 255L 
		
	   )
	   
        @ASN1Element ( name = "callAttemptElapsedTimeValue", isOptional =  false , hasTag =  true, tag = 0 , hasDefaultValue =  false  )
    
	private Integer callAttemptElapsedTimeValue = null;
                
  
        @ASN1Element ( name = "callStopTimeValue", isOptional =  false , hasTag =  true, tag = 1 , hasDefaultValue =  false  )
    
	private DateAndTime callStopTimeValue = null;
                
  
        @ASN1Element ( name = "callConnectedElapsedTimeValue", isOptional =  false , hasTag =  true, tag = 2 , hasDefaultValue =  false  )
    
	private Integer4 callConnectedElapsedTimeValue = null;
                
  
        @ASN1Element ( name = "releaseCauseValue", isOptional =  false , hasTag =  true, tag = 30 , hasDefaultValue =  false  )
    
	private Cause releaseCauseValue = null;
                
  
        
        public Integer getCallAttemptElapsedTimeValue () {
            return this.callAttemptElapsedTimeValue;
        }

        public boolean isCallAttemptElapsedTimeValueSelected () {
            return this.callAttemptElapsedTimeValue != null;
        }

        private void setCallAttemptElapsedTimeValue (Integer value) {
            this.callAttemptElapsedTimeValue = value;
        }

        
        public void selectCallAttemptElapsedTimeValue (Integer value) {
            this.callAttemptElapsedTimeValue = value;
            
                    setCallStopTimeValue(null);
                
                    setCallConnectedElapsedTimeValue(null);
                
                    setReleaseCauseValue(null);
                            
        }

        
  
        
        public DateAndTime getCallStopTimeValue () {
            return this.callStopTimeValue;
        }

        public boolean isCallStopTimeValueSelected () {
            return this.callStopTimeValue != null;
        }

        private void setCallStopTimeValue (DateAndTime value) {
            this.callStopTimeValue = value;
        }

        
        public void selectCallStopTimeValue (DateAndTime value) {
            this.callStopTimeValue = value;
            
                    setCallAttemptElapsedTimeValue(null);
                
                    setCallConnectedElapsedTimeValue(null);
                
                    setReleaseCauseValue(null);
                            
        }

        
  
        
        public Integer4 getCallConnectedElapsedTimeValue () {
            return this.callConnectedElapsedTimeValue;
        }

        public boolean isCallConnectedElapsedTimeValueSelected () {
            return this.callConnectedElapsedTimeValue != null;
        }

        private void setCallConnectedElapsedTimeValue (Integer4 value) {
            this.callConnectedElapsedTimeValue = value;
        }

        
        public void selectCallConnectedElapsedTimeValue (Integer4 value) {
            this.callConnectedElapsedTimeValue = value;
            
                    setCallAttemptElapsedTimeValue(null);
                
                    setCallStopTimeValue(null);
                
                    setReleaseCauseValue(null);
                            
        }

        
  
        
        public Cause getReleaseCauseValue () {
            return this.releaseCauseValue;
        }

        public boolean isReleaseCauseValueSelected () {
            return this.releaseCauseValue != null;
        }

        private void setReleaseCauseValue (Cause value) {
            this.releaseCauseValue = value;
        }

        
        public void selectReleaseCauseValue (Cause value) {
            this.releaseCauseValue = value;
            
                    setCallAttemptElapsedTimeValue(null);
                
                    setCallStopTimeValue(null);
                
                    setCallConnectedElapsedTimeValue(null);
                            
        }

        
  

	    public void initWithDefaults() {
	    }

        private static IASN1PreparedElementData preparedData = CoderFactory.getInstance().newPreparedElementData(RequestedInformationValue.class);
        public IASN1PreparedElementData getPreparedData() {
            return preparedData;
        }


    }
            