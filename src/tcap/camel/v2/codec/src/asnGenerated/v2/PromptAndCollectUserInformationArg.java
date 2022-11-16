
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
    @ASN1Sequence ( name = "PromptAndCollectUserInformationArg", isSet = false )
    public class PromptAndCollectUserInformationArg implements IASN1PreparedElement {
            
        @ASN1Element ( name = "collectedInfo", isOptional =  false , hasTag =  true, tag = 0 , hasDefaultValue =  false  )
    
	private CollectedInfo collectedInfo = null;
                
  @ASN1Boolean( name = "" )
    
        @ASN1Element ( name = "disconnectFromIPForbidden", isOptional =  false , hasTag =  true, tag = 1 , hasDefaultValue =  true  )
    
	private Boolean disconnectFromIPForbidden = null;
                
  
        @ASN1Element ( name = "informationToSend", isOptional =  true , hasTag =  true, tag = 2 , hasDefaultValue =  false  )
    
	private InformationToSend informationToSend = null;
                
  
        @ASN1Element ( name = "callSegmentID", isOptional =  true , hasTag =  true, tag = 4 , hasDefaultValue =  false  )
    
	private CallSegmentID callSegmentID = null;
                
  @ASN1Boolean( name = "" )
    
        @ASN1Element ( name = "requestAnnouncementStartedNotification", isOptional =  false , hasTag =  true, tag = 51 , hasDefaultValue =  true  )
    
	private Boolean requestAnnouncementStartedNotification = null;
                
  
@ASN1SequenceOf( name = "extensions", isSetOf = false ) 

    @ASN1ValueRangeConstraint ( 
		
		min = 1L, 
		
		max = 10L 
		
	   )
	   
        @ASN1Element ( name = "extensions", isOptional =  true , hasTag =  true, tag = 3 , hasDefaultValue =  false  )
    
	private java.util.Collection<ExtensionField>  extensions = null;
                
  
        
        public CollectedInfo getCollectedInfo () {
            return this.collectedInfo;
        }

        

        public void setCollectedInfo (CollectedInfo value) {
            this.collectedInfo = value;
        }
        
  
        
        public Boolean getDisconnectFromIPForbidden () {
            return this.disconnectFromIPForbidden;
        }

        

        public void setDisconnectFromIPForbidden (Boolean value) {
            this.disconnectFromIPForbidden = value;
        }
        
  
        
        public InformationToSend getInformationToSend () {
            return this.informationToSend;
        }

        
        public boolean isInformationToSendPresent () {
            return this.informationToSend != null;
        }
        

        public void setInformationToSend (InformationToSend value) {
            this.informationToSend = value;
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
        
  
        
        public Boolean getRequestAnnouncementStartedNotification () {
            return this.requestAnnouncementStartedNotification;
        }

        

        public void setRequestAnnouncementStartedNotification (Boolean value) {
            this.requestAnnouncementStartedNotification = value;
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
        
  
                    
        
        public void initWithDefaults() {
            Boolean param_DisconnectFromIPForbidden =         
            null;
        setDisconnectFromIPForbidden(param_DisconnectFromIPForbidden);
    Boolean param_RequestAnnouncementStartedNotification =         
            null;
        setRequestAnnouncementStartedNotification(param_RequestAnnouncementStartedNotification);
    
        }

        private static IASN1PreparedElementData preparedData = CoderFactory.getInstance().newPreparedElementData(PromptAndCollectUserInformationArg.class);
        public IASN1PreparedElementData getPreparedData() {
            return preparedData;
        }

            
    }
            