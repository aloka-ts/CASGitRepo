
package com.agnity.ain.asngenerated;
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
    @ASN1Sequence ( name = "VoiceConnectionBlock", isSet = false )
    public class VoiceConnectionBlock implements IASN1PreparedElement {
            
        @ASN1Element ( name = "userID", isOptional =  false , hasTag =  true, tag = 1 , hasDefaultValue =  false  )
    
	private UserID userID = null;
                
  
        @ASN1Element ( name = "serviceType", isOptional =  false , hasTag =  true, tag = 2 , hasDefaultValue =  false  )
    
	private ServiceType serviceType = null;
                
  
        @ASN1Element ( name = "mwiTone", isOptional =  true , hasTag =  true, tag = 3 , hasDefaultValue =  false  )
    
	private MwiTone mwiTone = null;
                
  
        @ASN1Element ( name = "iPResourceMeasure", isOptional =  true , hasTag =  true, tag = 4 , hasDefaultValue =  false  )
    
	private IPResourceMeasure iPResourceMeasure = null;
                
  
        @ASN1Element ( name = "iPStayOnLine", isOptional =  true , hasTag =  true, tag = 5 , hasDefaultValue =  false  )
    
	private IPStayOnLine iPStayOnLine = null;
                
  
        
        public UserID getUserID () {
            return this.userID;
        }

        

        public void setUserID (UserID value) {
            this.userID = value;
        }
        
  
        
        public ServiceType getServiceType () {
            return this.serviceType;
        }

        

        public void setServiceType (ServiceType value) {
            this.serviceType = value;
        }
        
  
        
        public MwiTone getMwiTone () {
            return this.mwiTone;
        }

        
        public boolean isMwiTonePresent () {
            return this.mwiTone != null;
        }
        

        public void setMwiTone (MwiTone value) {
            this.mwiTone = value;
        }
        
  
        
        public IPResourceMeasure getIPResourceMeasure () {
            return this.iPResourceMeasure;
        }

        
        public boolean isIPResourceMeasurePresent () {
            return this.iPResourceMeasure != null;
        }
        

        public void setIPResourceMeasure (IPResourceMeasure value) {
            this.iPResourceMeasure = value;
        }
        
  
        
        public IPStayOnLine getIPStayOnLine () {
            return this.iPStayOnLine;
        }

        
        public boolean isIPStayOnLinePresent () {
            return this.iPStayOnLine != null;
        }
        

        public void setIPStayOnLine (IPStayOnLine value) {
            this.iPStayOnLine = value;
        }
        
  
                    
        
        public void initWithDefaults() {
            
        }

        private static IASN1PreparedElementData preparedData = CoderFactory.getInstance().newPreparedElementData(VoiceConnectionBlock.class);
        public IASN1PreparedElementData getPreparedData() {
            return preparedData;
        }

            
    }
            