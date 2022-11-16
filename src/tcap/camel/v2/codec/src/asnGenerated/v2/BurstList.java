
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
    @ASN1Sequence ( name = "BurstList", isSet = false )
    public class BurstList implements IASN1PreparedElement {
            @ASN1Integer( name = "" )
    @ASN1ValueRangeConstraint ( 
		
		min = 1L, 
		
		max = 1200L 
		
	   )
	   
        @ASN1Element ( name = "warningPeriod", isOptional =  false , hasTag =  true, tag = 0 , hasDefaultValue =  true  )
    
	private Integer warningPeriod = null;
                
  
        @ASN1Element ( name = "bursts", isOptional =  false , hasTag =  true, tag = 1 , hasDefaultValue =  false  )
    
	private Burst bursts = null;
                
  
        
        public Integer getWarningPeriod () {
            return this.warningPeriod;
        }

        

        public void setWarningPeriod (Integer value) {
            this.warningPeriod = value;
        }
        
  
        
        public Burst getBursts () {
            return this.bursts;
        }

        

        public void setBursts (Burst value) {
            this.bursts = value;
        }
        
  
                    
        
        public void initWithDefaults() {
            Integer param_WarningPeriod =         
            new Integer ( 30);
        setWarningPeriod(param_WarningPeriod);
    
        }

        private static IASN1PreparedElementData preparedData = CoderFactory.getInstance().newPreparedElementData(BurstList.class);
        public IASN1PreparedElementData getPreparedData() {
            return preparedData;
        }

            
    }
            