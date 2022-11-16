
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
    @ASN1Choice ( name = "SubscriberState" )
    public class SubscriberState implements IASN1PreparedElement {
            
        @ASN1Null ( name = "assumedIdle" ) 
    
        @ASN1Element ( name = "assumedIdle", isOptional =  false , hasTag =  true, tag = 0 , hasDefaultValue =  false  )
    
	private org.bn.types.NullObject assumedIdle = null;
                
  
        @ASN1Null ( name = "camelBusy" ) 
    
        @ASN1Element ( name = "camelBusy", isOptional =  false , hasTag =  true, tag = 1 , hasDefaultValue =  false  )
    
	private org.bn.types.NullObject camelBusy = null;
                
  
        @ASN1Element ( name = "netDetNotReachable", isOptional =  false , hasTag =  false  , hasDefaultValue =  false  )
    
	private NotReachableReason netDetNotReachable = null;
                
  
        @ASN1Null ( name = "notProvidedFromVLR" ) 
    
        @ASN1Element ( name = "notProvidedFromVLR", isOptional =  false , hasTag =  true, tag = 2 , hasDefaultValue =  false  )
    
	private org.bn.types.NullObject notProvidedFromVLR = null;
                
  
        
        public org.bn.types.NullObject getAssumedIdle () {
            return this.assumedIdle;
        }

        public boolean isAssumedIdleSelected () {
            return this.assumedIdle != null;
        }

        private void setAssumedIdle (org.bn.types.NullObject value) {
            this.assumedIdle = value;
        }

        
        public void selectAssumedIdle () {
            selectAssumedIdle (new org.bn.types.NullObject());
	}
	
        public void selectAssumedIdle (org.bn.types.NullObject value) {
            this.assumedIdle = value;
            
                    setCamelBusy(null);
                
                    setNetDetNotReachable(null);
                
                    setNotProvidedFromVLR(null);
                            
        }

        
  
        
        public org.bn.types.NullObject getCamelBusy () {
            return this.camelBusy;
        }

        public boolean isCamelBusySelected () {
            return this.camelBusy != null;
        }

        private void setCamelBusy (org.bn.types.NullObject value) {
            this.camelBusy = value;
        }

        
        public void selectCamelBusy () {
            selectCamelBusy (new org.bn.types.NullObject());
	}
	
        public void selectCamelBusy (org.bn.types.NullObject value) {
            this.camelBusy = value;
            
                    setAssumedIdle(null);
                
                    setNetDetNotReachable(null);
                
                    setNotProvidedFromVLR(null);
                            
        }

        
  
        
        public NotReachableReason getNetDetNotReachable () {
            return this.netDetNotReachable;
        }

        public boolean isNetDetNotReachableSelected () {
            return this.netDetNotReachable != null;
        }

        private void setNetDetNotReachable (NotReachableReason value) {
            this.netDetNotReachable = value;
        }

        
        public void selectNetDetNotReachable (NotReachableReason value) {
            this.netDetNotReachable = value;
            
                    setAssumedIdle(null);
                
                    setCamelBusy(null);
                
                    setNotProvidedFromVLR(null);
                            
        }

        
  
        
        public org.bn.types.NullObject getNotProvidedFromVLR () {
            return this.notProvidedFromVLR;
        }

        public boolean isNotProvidedFromVLRSelected () {
            return this.notProvidedFromVLR != null;
        }

        private void setNotProvidedFromVLR (org.bn.types.NullObject value) {
            this.notProvidedFromVLR = value;
        }

        
        public void selectNotProvidedFromVLR () {
            selectNotProvidedFromVLR (new org.bn.types.NullObject());
	}
	
        public void selectNotProvidedFromVLR (org.bn.types.NullObject value) {
            this.notProvidedFromVLR = value;
            
                    setAssumedIdle(null);
                
                    setCamelBusy(null);
                
                    setNetDetNotReachable(null);
                            
        }

        
  

	    public void initWithDefaults() {
	    }

        private static IASN1PreparedElementData preparedData = CoderFactory.getInstance().newPreparedElementData(SubscriberState.class);
        public IASN1PreparedElementData getPreparedData() {
            return preparedData;
        }


    }
            