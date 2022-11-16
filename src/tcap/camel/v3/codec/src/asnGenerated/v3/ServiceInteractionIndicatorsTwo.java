
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
    @ASN1Sequence ( name = "ServiceInteractionIndicatorsTwo", isSet = false )
    public class ServiceInteractionIndicatorsTwo implements IASN1PreparedElement {
            
        @ASN1Element ( name = "forwardServiceInteractionInd", isOptional =  true , hasTag =  true, tag = 0 , hasDefaultValue =  false  )
    
	private ForwardServiceInteractionInd forwardServiceInteractionInd = null;
                
  
        @ASN1Element ( name = "backwardServiceInteractionInd", isOptional =  true , hasTag =  true, tag = 1 , hasDefaultValue =  false  )
    
	private BackwardServiceInteractionInd backwardServiceInteractionInd = null;
                
  
        @ASN1Element ( name = "bothwayThroughConnectionInd", isOptional =  true , hasTag =  true, tag = 2 , hasDefaultValue =  false  )
    
	private BothwayThroughConnectionInd bothwayThroughConnectionInd = null;
                
  
        @ASN1Element ( name = "connectedNumberTreatmentInd", isOptional =  true , hasTag =  true, tag = 4 , hasDefaultValue =  false  )
    
	private ConnectedNumberTreatmentInd connectedNumberTreatmentInd = null;
                
  
        @ASN1Null ( name = "nonCUGCall" ) 
    
        @ASN1Element ( name = "nonCUGCall", isOptional =  true , hasTag =  true, tag = 13 , hasDefaultValue =  false  )
    
	private org.bn.types.NullObject nonCUGCall = null;
                
  @ASN1OctetString( name = "" )
    
            @ASN1SizeConstraint ( max = 1L )
        
        @ASN1Element ( name = "holdTreatmentIndicator", isOptional =  true , hasTag =  true, tag = 50 , hasDefaultValue =  false  )
    
	private byte[] holdTreatmentIndicator = null;
                
  @ASN1OctetString( name = "" )
    
            @ASN1SizeConstraint ( max = 1L )
        
        @ASN1Element ( name = "cwTreatmentIndicator", isOptional =  true , hasTag =  true, tag = 51 , hasDefaultValue =  false  )
    
	private byte[] cwTreatmentIndicator = null;
                
  @ASN1OctetString( name = "" )
    
            @ASN1SizeConstraint ( max = 1L )
        
        @ASN1Element ( name = "ectTreatmentIndicator", isOptional =  true , hasTag =  true, tag = 52 , hasDefaultValue =  false  )
    
	private byte[] ectTreatmentIndicator = null;
                
  
        
        public ForwardServiceInteractionInd getForwardServiceInteractionInd () {
            return this.forwardServiceInteractionInd;
        }

        
        public boolean isForwardServiceInteractionIndPresent () {
            return this.forwardServiceInteractionInd != null;
        }
        

        public void setForwardServiceInteractionInd (ForwardServiceInteractionInd value) {
            this.forwardServiceInteractionInd = value;
        }
        
  
        
        public BackwardServiceInteractionInd getBackwardServiceInteractionInd () {
            return this.backwardServiceInteractionInd;
        }

        
        public boolean isBackwardServiceInteractionIndPresent () {
            return this.backwardServiceInteractionInd != null;
        }
        

        public void setBackwardServiceInteractionInd (BackwardServiceInteractionInd value) {
            this.backwardServiceInteractionInd = value;
        }
        
  
        
        public BothwayThroughConnectionInd getBothwayThroughConnectionInd () {
            return this.bothwayThroughConnectionInd;
        }

        
        public boolean isBothwayThroughConnectionIndPresent () {
            return this.bothwayThroughConnectionInd != null;
        }
        

        public void setBothwayThroughConnectionInd (BothwayThroughConnectionInd value) {
            this.bothwayThroughConnectionInd = value;
        }
        
  
        
        public ConnectedNumberTreatmentInd getConnectedNumberTreatmentInd () {
            return this.connectedNumberTreatmentInd;
        }

        
        public boolean isConnectedNumberTreatmentIndPresent () {
            return this.connectedNumberTreatmentInd != null;
        }
        

        public void setConnectedNumberTreatmentInd (ConnectedNumberTreatmentInd value) {
            this.connectedNumberTreatmentInd = value;
        }
        
  
        
        public byte[] getHoldTreatmentIndicator () {
            return this.holdTreatmentIndicator;
        }

        
        public boolean isHoldTreatmentIndicatorPresent () {
            return this.holdTreatmentIndicator != null;
        }
        

        public void setHoldTreatmentIndicator (byte[] value) {
            this.holdTreatmentIndicator = value;
        }
        
  
        
        public byte[] getCwTreatmentIndicator () {
            return this.cwTreatmentIndicator;
        }

        
        public boolean isCwTreatmentIndicatorPresent () {
            return this.cwTreatmentIndicator != null;
        }
        

        public void setCwTreatmentIndicator (byte[] value) {
            this.cwTreatmentIndicator = value;
        }
        
  
        
        public byte[] getEctTreatmentIndicator () {
            return this.ectTreatmentIndicator;
        }

        
        public boolean isEctTreatmentIndicatorPresent () {
            return this.ectTreatmentIndicator != null;
        }
        

        public void setEctTreatmentIndicator (byte[] value) {
            this.ectTreatmentIndicator = value;
        }
        
  
                    
        
        public void initWithDefaults() {
            
        }

        private static IASN1PreparedElementData preparedData = CoderFactory.getInstance().newPreparedElementData(ServiceInteractionIndicatorsTwo.class);
        public IASN1PreparedElementData getPreparedData() {
            return preparedData;
        }

            
    }
            