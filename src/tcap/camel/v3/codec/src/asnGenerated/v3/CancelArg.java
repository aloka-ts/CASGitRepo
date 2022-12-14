
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
    @ASN1Choice ( name = "CancelArg" )
    public class CancelArg implements IASN1PreparedElement {
            
        @ASN1Element ( name = "invokeID", isOptional =  false , hasTag =  true, tag = 0 , hasDefaultValue =  false  )
    
	private InvokeID invokeID = null;
                
  
        @ASN1Null ( name = "allRequests" ) 
    
        @ASN1Element ( name = "allRequests", isOptional =  false , hasTag =  true, tag = 1 , hasDefaultValue =  false  )
    
	private org.bn.types.NullObject allRequests = null;
                
  
        @ASN1Element ( name = "callSegmentToCancel", isOptional =  false , hasTag =  true, tag = 2 , hasDefaultValue =  false  )
    
	private CallSegmentToCancel callSegmentToCancel = null;
                
  
        
        public InvokeID getInvokeID () {
            return this.invokeID;
        }

        public boolean isInvokeIDSelected () {
            return this.invokeID != null;
        }

        private void setInvokeID (InvokeID value) {
            this.invokeID = value;
        }

        
        public void selectInvokeID (InvokeID value) {
            this.invokeID = value;
            
                    setAllRequests(null);
                
                    setCallSegmentToCancel(null);
                            
        }

        
  
        
        public org.bn.types.NullObject getAllRequests () {
            return this.allRequests;
        }

        public boolean isAllRequestsSelected () {
            return this.allRequests != null;
        }

        private void setAllRequests (org.bn.types.NullObject value) {
            this.allRequests = value;
        }

        
        public void selectAllRequests () {
            selectAllRequests (new org.bn.types.NullObject());
	}
	
        public void selectAllRequests (org.bn.types.NullObject value) {
            this.allRequests = value;
            
                    setInvokeID(null);
                
                    setCallSegmentToCancel(null);
                            
        }

        
  
        
        public CallSegmentToCancel getCallSegmentToCancel () {
            return this.callSegmentToCancel;
        }

        public boolean isCallSegmentToCancelSelected () {
            return this.callSegmentToCancel != null;
        }

        private void setCallSegmentToCancel (CallSegmentToCancel value) {
            this.callSegmentToCancel = value;
        }

        
        public void selectCallSegmentToCancel (CallSegmentToCancel value) {
            this.callSegmentToCancel = value;
            
                    setInvokeID(null);
                
                    setAllRequests(null);
                            
        }

        
  

	    public void initWithDefaults() {
	    }

        private static IASN1PreparedElementData preparedData = CoderFactory.getInstance().newPreparedElementData(CancelArg.class);
        public IASN1PreparedElementData getPreparedData() {
            return preparedData;
        }


    }
            