
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
    @ASN1Sequence ( name = "ConnectToResourceArg", isSet = false )
    public class ConnectToResourceArg implements IASN1PreparedElement {
            
        
    @ASN1PreparedElement
    @ASN1Choice ( name = "resourceAddress" )
    public static class ResourceAddressChoiceType implements IASN1PreparedElement {
            
        @ASN1Element ( name = "ipRoutingAddress", isOptional =  false , hasTag =  true, tag = 0 , hasDefaultValue =  false  )
    
	private IPRoutingAddress ipRoutingAddress = null;
                
  
        @ASN1Null ( name = "none" ) 
    
        @ASN1Element ( name = "none", isOptional =  false , hasTag =  true, tag = 3 , hasDefaultValue =  false  )
    
	private org.bn.types.NullObject none = null;
                
  
        
        public IPRoutingAddress getIpRoutingAddress () {
            return this.ipRoutingAddress;
        }

        public boolean isIpRoutingAddressSelected () {
            return this.ipRoutingAddress != null;
        }

        private void setIpRoutingAddress (IPRoutingAddress value) {
            this.ipRoutingAddress = value;
        }

        
        public void selectIpRoutingAddress (IPRoutingAddress value) {
            this.ipRoutingAddress = value;
            
                    setNone(null);
                            
        }

        
  
        
        public org.bn.types.NullObject getNone () {
            return this.none;
        }

        public boolean isNoneSelected () {
            return this.none != null;
        }

        private void setNone (org.bn.types.NullObject value) {
            this.none = value;
        }

        
        public void selectNone () {
            selectNone (new org.bn.types.NullObject());
	}
	
        public void selectNone (org.bn.types.NullObject value) {
            this.none = value;
            
                    setIpRoutingAddress(null);
                            
        }

        
  

	    public void initWithDefaults() {
	    }

        public IASN1PreparedElementData getPreparedData() {
            return preparedData_ResourceAddressChoiceType;
        }

        private static IASN1PreparedElementData preparedData_ResourceAddressChoiceType = CoderFactory.getInstance().newPreparedElementData(ResourceAddressChoiceType.class);

    }

                
        @ASN1Element ( name = "resourceAddress", isOptional =  false , hasTag =  false  , hasDefaultValue =  false  )
    
	private ResourceAddressChoiceType resourceAddress = null;
                
  
@ASN1SequenceOf( name = "extensions", isSetOf = false ) 

    @ASN1ValueRangeConstraint ( 
		
		min = 1L, 
		
		max = 10L 
		
	   )
	   
        @ASN1Element ( name = "extensions", isOptional =  true , hasTag =  true, tag = 4 , hasDefaultValue =  false  )
    
	private java.util.Collection<ExtensionField>  extensions = null;
                
  
        @ASN1Element ( name = "callSegmentID", isOptional =  true , hasTag =  true, tag = 50 , hasDefaultValue =  false  )
    
	private CallSegmentID callSegmentID = null;
                
  
        @ASN1Element ( name = "serviceInteractionIndicatorsTwo", isOptional =  true , hasTag =  true, tag = 7 , hasDefaultValue =  false  )
    
	private ServiceInteractionIndicatorsTwo serviceInteractionIndicatorsTwo = null;
                
  
        
        public ResourceAddressChoiceType getResourceAddress () {
            return this.resourceAddress;
        }

        

        public void setResourceAddress (ResourceAddressChoiceType value) {
            this.resourceAddress = value;
        }
        
  
        
        public java.util.Collection<ExtensionField>  getExtensions () {
            return this.extensions;
        }

        
        public boolean isExtensionsPresent () {
            return this.extensions != null;
        }
        

        public void setExtensions (java.util.Collection<ExtensionField>  value) {
            this.extensions = value;
        }
        
  
        
        public CallSegmentID getCallSegmentID () {
            return this.callSegmentID;
        }

        
        public boolean isCallSegmentIDPresent () {
            return this.callSegmentID != null;
        }
        

        public void setCallSegmentID (CallSegmentID value) {
            this.callSegmentID = value;
        }
        
  
        
        public ServiceInteractionIndicatorsTwo getServiceInteractionIndicatorsTwo () {
            return this.serviceInteractionIndicatorsTwo;
        }

        
        public boolean isServiceInteractionIndicatorsTwoPresent () {
            return this.serviceInteractionIndicatorsTwo != null;
        }
        

        public void setServiceInteractionIndicatorsTwo (ServiceInteractionIndicatorsTwo value) {
            this.serviceInteractionIndicatorsTwo = value;
        }
        
  
                    
        
        public void initWithDefaults() {
            
        }

        private static IASN1PreparedElementData preparedData = CoderFactory.getInstance().newPreparedElementData(ConnectToResourceArg.class);
        public IASN1PreparedElementData getPreparedData() {
            return preparedData;
        }

            
    }
            