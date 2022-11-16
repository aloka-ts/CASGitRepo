
package com.agnity.camelv2.asngenerated;
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
    @ASN1Sequence ( name = "InbandInfo", isSet = false )
    public class InbandInfo implements IASN1PreparedElement {
            
        @ASN1Element ( name = "messageID", isOptional =  false , hasTag =  true, tag = 0 , hasDefaultValue =  false  )
    
	private MessageID messageID = null;
                
  @ASN1Integer( name = "" )
    @ASN1ValueRangeConstraint ( 
		
		min = 1L, 
		
		max = 127L 
		
	   )
	   
        @ASN1Element ( name = "numberOfRepetitions", isOptional =  true , hasTag =  true, tag = 1 , hasDefaultValue =  false  )
    
	private Integer numberOfRepetitions = null;
                
  @ASN1Integer( name = "" )
    @ASN1ValueRangeConstraint ( 
		
		min = 0L, 
		
		max = 32767L 
		
	   )
	   
        @ASN1Element ( name = "duration", isOptional =  true , hasTag =  true, tag = 2 , hasDefaultValue =  false  )
    
	private Integer duration = null;
                
  @ASN1Integer( name = "" )
    @ASN1ValueRangeConstraint ( 
		
		min = 0L, 
		
		max = 32767L 
		
	   )
	   
        @ASN1Element ( name = "interval", isOptional =  true , hasTag =  true, tag = 3 , hasDefaultValue =  false  )
    
	private Integer interval = null;
                
  
        
        public MessageID getMessageID () {
            return this.messageID;
        }

        

        public void setMessageID (MessageID value) {
            this.messageID = value;
        }
        
  
        
        public Integer getNumberOfRepetitions () {
            return this.numberOfRepetitions;
        }

        
        public boolean isNumberOfRepetitionsPresent () {
            return this.numberOfRepetitions != null;
        }
        

        public void setNumberOfRepetitions (Integer value) {
            this.numberOfRepetitions = value;
        }
        
  
        
        public Integer getDuration () {
            return this.duration;
        }

        
        public boolean isDurationPresent () {
            return this.duration != null;
        }
        

        public void setDuration (Integer value) {
            this.duration = value;
        }
        
  
        
        public Integer getInterval () {
            return this.interval;
        }

        
        public boolean isIntervalPresent () {
            return this.interval != null;
        }
        

        public void setInterval (Integer value) {
            this.interval = value;
        }
        
  
                    
        
        public void initWithDefaults() {
            
        }

        private static IASN1PreparedElementData preparedData = CoderFactory.getInstance().newPreparedElementData(InbandInfo.class);
        public IASN1PreparedElementData getPreparedData() {
            return preparedData;
        }

            
    }
            