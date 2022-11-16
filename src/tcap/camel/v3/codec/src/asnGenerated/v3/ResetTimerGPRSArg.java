
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
    @ASN1Sequence ( name = "ResetTimerGPRSArg", isSet = false )
    public class ResetTimerGPRSArg implements IASN1PreparedElement {
            
        @ASN1Element ( name = "timerID", isOptional =  false , hasTag =  true, tag = 0 , hasDefaultValue =  true  )
    
	private TimerID timerID = null;
                
  
        @ASN1Element ( name = "timervalue", isOptional =  false , hasTag =  true, tag = 1 , hasDefaultValue =  false  )
    
	private TimerValue timervalue = null;
                
  
        
        public TimerID getTimerID () {
            return this.timerID;
        }

        

        public void setTimerID (TimerID value) {
            this.timerID = value;
        }
        
  
        
        public TimerValue getTimervalue () {
            return this.timervalue;
        }

        

        public void setTimervalue (TimerValue value) {
            this.timervalue = value;
        }
        
  
        public void initWithDefaults() {
            TimerID param_TimerID =  new TimerID();       
            param_TimerID.setValue(TimerID.EnumType.tssf);
        setTimerID(param_TimerID);
    
        }
                    
        
        /*public void initWithDefaults() {
            TimerID param_TimerID =         
            ;
        setTimerID(param_TimerID);
    
        }
*/
        private static IASN1PreparedElementData preparedData = CoderFactory.getInstance().newPreparedElementData(ResetTimerGPRSArg.class);
        public IASN1PreparedElementData getPreparedData() {
            return preparedData;
        }

            
    }
            