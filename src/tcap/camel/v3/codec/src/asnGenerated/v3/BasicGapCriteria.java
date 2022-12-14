
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
    @ASN1Choice ( name = "BasicGapCriteria" )
    public class BasicGapCriteria implements IASN1PreparedElement {
            
        @ASN1Element ( name = "calledAddressValue", isOptional =  false , hasTag =  true, tag = 0 , hasDefaultValue =  false  )
    
	private Digits calledAddressValue = null;
                
  
        @ASN1Element ( name = "gapOnService", isOptional =  false , hasTag =  true, tag = 2 , hasDefaultValue =  false  )
    
	private GapOnService gapOnService = null;
                
  

       @ASN1PreparedElement
       @ASN1Sequence ( name = "calledAddressAndService" , isSet = false )
       public static class CalledAddressAndServiceSequenceType implements IASN1PreparedElement {
                
        @ASN1Element ( name = "calledAddressValue", isOptional =  false , hasTag =  true, tag = 0 , hasDefaultValue =  false  )
    
	private Digits calledAddressValue = null;
                
  
        @ASN1Element ( name = "serviceKey", isOptional =  false , hasTag =  true, tag = 1 , hasDefaultValue =  false  )
    
	private ServiceKey serviceKey = null;
                
  
        
        public Digits getCalledAddressValue () {
            return this.calledAddressValue;
        }

        

        public void setCalledAddressValue (Digits value) {
            this.calledAddressValue = value;
        }
        
  
        
        public ServiceKey getServiceKey () {
            return this.serviceKey;
        }

        

        public void setServiceKey (ServiceKey value) {
            this.serviceKey = value;
        }
        
  
                
                
        public void initWithDefaults() {
            
        }

        public IASN1PreparedElementData getPreparedData() {
            return preparedData_CalledAddressAndServiceSequenceType;
        }

       private static IASN1PreparedElementData preparedData_CalledAddressAndServiceSequenceType = CoderFactory.getInstance().newPreparedElementData(CalledAddressAndServiceSequenceType.class);
                
       }

       
                
        @ASN1Element ( name = "calledAddressAndService", isOptional =  false , hasTag =  true, tag = 29 , hasDefaultValue =  false  )
    
	private CalledAddressAndServiceSequenceType calledAddressAndService = null;
                
  

       @ASN1PreparedElement
       @ASN1Sequence ( name = "callingAddressAndService" , isSet = false )
       public static class CallingAddressAndServiceSequenceType implements IASN1PreparedElement {
                
        @ASN1Element ( name = "callingAddressValue", isOptional =  false , hasTag =  true, tag = 0 , hasDefaultValue =  false  )
    
	private Digits callingAddressValue = null;
                
  
        @ASN1Element ( name = "serviceKey", isOptional =  false , hasTag =  true, tag = 1 , hasDefaultValue =  false  )
    
	private ServiceKey serviceKey = null;
                
  
        
        public Digits getCallingAddressValue () {
            return this.callingAddressValue;
        }

        

        public void setCallingAddressValue (Digits value) {
            this.callingAddressValue = value;
        }
        
  
        
        public ServiceKey getServiceKey () {
            return this.serviceKey;
        }

        

        public void setServiceKey (ServiceKey value) {
            this.serviceKey = value;
        }
        
  
                
                
        public void initWithDefaults() {
            
        }

        public IASN1PreparedElementData getPreparedData() {
            return preparedData_CallingAddressAndServiceSequenceType;
        }

       private static IASN1PreparedElementData preparedData_CallingAddressAndServiceSequenceType = CoderFactory.getInstance().newPreparedElementData(CallingAddressAndServiceSequenceType.class);
                
       }

       
                
        @ASN1Element ( name = "callingAddressAndService", isOptional =  false , hasTag =  true, tag = 30 , hasDefaultValue =  false  )
    
	private CallingAddressAndServiceSequenceType callingAddressAndService = null;
                
  
        
        public Digits getCalledAddressValue () {
            return this.calledAddressValue;
        }

        public boolean isCalledAddressValueSelected () {
            return this.calledAddressValue != null;
        }

        private void setCalledAddressValue (Digits value) {
            this.calledAddressValue = value;
        }

        
        public void selectCalledAddressValue (Digits value) {
            this.calledAddressValue = value;
            
                    setGapOnService(null);
                
                    setCalledAddressAndService(null);
                
                    setCallingAddressAndService(null);
                            
        }

        
  
        
        public GapOnService getGapOnService () {
            return this.gapOnService;
        }

        public boolean isGapOnServiceSelected () {
            return this.gapOnService != null;
        }

        private void setGapOnService (GapOnService value) {
            this.gapOnService = value;
        }

        
        public void selectGapOnService (GapOnService value) {
            this.gapOnService = value;
            
                    setCalledAddressValue(null);
                
                    setCalledAddressAndService(null);
                
                    setCallingAddressAndService(null);
                            
        }

        
  
        
        public CalledAddressAndServiceSequenceType getCalledAddressAndService () {
            return this.calledAddressAndService;
        }

        public boolean isCalledAddressAndServiceSelected () {
            return this.calledAddressAndService != null;
        }

        private void setCalledAddressAndService (CalledAddressAndServiceSequenceType value) {
            this.calledAddressAndService = value;
        }

        
        public void selectCalledAddressAndService (CalledAddressAndServiceSequenceType value) {
            this.calledAddressAndService = value;
            
                    setCalledAddressValue(null);
                
                    setGapOnService(null);
                
                    setCallingAddressAndService(null);
                            
        }

        
  
        
        public CallingAddressAndServiceSequenceType getCallingAddressAndService () {
            return this.callingAddressAndService;
        }

        public boolean isCallingAddressAndServiceSelected () {
            return this.callingAddressAndService != null;
        }

        private void setCallingAddressAndService (CallingAddressAndServiceSequenceType value) {
            this.callingAddressAndService = value;
        }

        
        public void selectCallingAddressAndService (CallingAddressAndServiceSequenceType value) {
            this.callingAddressAndService = value;
            
                    setCalledAddressValue(null);
                
                    setGapOnService(null);
                
                    setCalledAddressAndService(null);
                            
        }

        
  

	    public void initWithDefaults() {
	    }

        private static IASN1PreparedElementData preparedData = CoderFactory.getInstance().newPreparedElementData(BasicGapCriteria.class);
        public IASN1PreparedElementData getPreparedData() {
            return preparedData;
        }


    }
            