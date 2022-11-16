
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
    @ASN1Sequence ( name = "EventReportSMSArg", isSet = false )
    public class EventReportSMSArg implements IASN1PreparedElement {
            
        @ASN1Element ( name = "eventTypeSMS", isOptional =  false , hasTag =  true, tag = 0 , hasDefaultValue =  false  )
    
	private EventTypeSMS eventTypeSMS = null;
                
  
        @ASN1Element ( name = "eventSpecificInformationSMS", isOptional =  true , hasTag =  true, tag = 1 , hasDefaultValue =  false  )
    
	private EventSpecificInformationSMS eventSpecificInformationSMS = null;
                
  
        @ASN1Element ( name = "miscCallInfo", isOptional =  true , hasTag =  true, tag = 2 , hasDefaultValue =  false  )
    
	private MiscCallInfo miscCallInfo = null;
                
  
        @ASN1Element ( name = "extensions", isOptional =  true , hasTag =  true, tag = 10 , hasDefaultValue =  false  )
    
	private ExtensionsArray extensions = null;
                
  
        
        public EventTypeSMS getEventTypeSMS () {
            return this.eventTypeSMS;
        }

        

        public void setEventTypeSMS (EventTypeSMS value) {
            this.eventTypeSMS = value;
        }
        
  
        
        public EventSpecificInformationSMS getEventSpecificInformationSMS () {
            return this.eventSpecificInformationSMS;
        }

        
        public boolean isEventSpecificInformationSMSPresent () {
            return this.eventSpecificInformationSMS != null;
        }
        

        public void setEventSpecificInformationSMS (EventSpecificInformationSMS value) {
            this.eventSpecificInformationSMS = value;
        }
        
  
        
        public MiscCallInfo getMiscCallInfo () {
            return this.miscCallInfo;
        }

        
        public boolean isMiscCallInfoPresent () {
            return this.miscCallInfo != null;
        }
        

        public void setMiscCallInfo (MiscCallInfo value) {
            this.miscCallInfo = value;
        }
        
  
        
        public ExtensionsArray getExtensions () {
            return this.extensions;
        }

        
        public boolean isExtensionsPresent () {
            return this.extensions != null;
        }
        

        public void setExtensions (ExtensionsArray value) {
            this.extensions = value;
        }
        
  
                    
        
        public void initWithDefaults() {
            
        }

        private static IASN1PreparedElementData preparedData = CoderFactory.getInstance().newPreparedElementData(EventReportSMSArg.class);
        public IASN1PreparedElementData getPreparedData() {
            return preparedData;
        }

            
    }
            