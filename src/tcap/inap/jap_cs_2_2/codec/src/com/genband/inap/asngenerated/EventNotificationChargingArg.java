
package com.genband.inap.asngenerated;
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
    @ASN1Sequence ( name = "EventNotificationChargingArg", isSet = false )
    public class EventNotificationChargingArg implements IASN1PreparedElement {
            
        @ASN1Element ( name = "eventTypeCharging", isOptional =  false , hasTag =  true, tag = 0 , hasDefaultValue =  false  )
    
	private EventTypeCharging eventTypeCharging = null;
                
  
        @ASN1Element ( name = "eventSpecificInformationCharging", isOptional =  true , hasTag =  true, tag = 1 , hasDefaultValue =  false  )
    
	private EventSpecificInformationCharging eventSpecificInformationCharging = null;
                
  
        @ASN1Element ( name = "legID", isOptional =  true , hasTag =  true, tag = 2 , hasDefaultValue =  false  )
    
	private LegID legID = null;
                
  
@ASN1SequenceOf( name = "", isSetOf = false ) 

    @ASN1ValueRangeConstraint ( 
		
		min = 1L, 
		
		max = 1L 
		
	   )
	   
        @ASN1Element ( name = "extensions", isOptional =  true , hasTag =  true, tag = 3 , hasDefaultValue =  false  )
    
	private java.util.Collection<ExtensionField>  extensions = null;
                
  
        @ASN1Element ( name = "monitorMode", isOptional =  false , hasTag =  true, tag = 30 , hasDefaultValue =  true  )
    
	private MonitorMode monitorMode = null;
                
  
        
        public EventTypeCharging getEventTypeCharging () {
            return this.eventTypeCharging;
        }

        

        public void setEventTypeCharging (EventTypeCharging value) {
            this.eventTypeCharging = value;
        }
        
  
        
        public EventSpecificInformationCharging getEventSpecificInformationCharging () {
            return this.eventSpecificInformationCharging;
        }

        
        public boolean isEventSpecificInformationChargingPresent () {
            return this.eventSpecificInformationCharging != null;
        }
        

        public void setEventSpecificInformationCharging (EventSpecificInformationCharging value) {
            this.eventSpecificInformationCharging = value;
        }
        
  
        
        public LegID getLegID () {
            return this.legID;
        }

        
        public boolean isLegIDPresent () {
            return this.legID != null;
        }
        

        public void setLegID (LegID value) {
            this.legID = value;
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
        
  
        
        public MonitorMode getMonitorMode () {
            return this.monitorMode;
        }

        

        public void setMonitorMode (MonitorMode value) {
            this.monitorMode = value;
        }
        
  
                    
        
        public void initWithDefaults() {
            MonitorMode param_MonitorMode =         null
            ;
        setMonitorMode(param_MonitorMode);
    
        }

        private static IASN1PreparedElementData preparedData = CoderFactory.getInstance().newPreparedElementData(EventNotificationChargingArg.class);
        public IASN1PreparedElementData getPreparedData() {
            return preparedData;
        }

            
    }
            