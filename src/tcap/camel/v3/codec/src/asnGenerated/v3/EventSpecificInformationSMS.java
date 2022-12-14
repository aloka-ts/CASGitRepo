
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
    @ASN1Choice ( name = "EventSpecificInformationSMS" )
    public class EventSpecificInformationSMS implements IASN1PreparedElement {
            

       @ASN1PreparedElement
       @ASN1Sequence ( name = "o-smsFailureSpecificInfo" , isSet = false )
       public static class O_smsFailureSpecificInfoSequenceType implements IASN1PreparedElement {
                
        @ASN1Element ( name = "smsfailureCause", isOptional =  true , hasTag =  true, tag = 0 , hasDefaultValue =  false  )
    
	private MO_SMSCause smsfailureCause = null;
                
  
        
        public MO_SMSCause getSmsfailureCause () {
            return this.smsfailureCause;
        }

        
        public boolean isSmsfailureCausePresent () {
            return this.smsfailureCause != null;
        }
        

        public void setSmsfailureCause (MO_SMSCause value) {
            this.smsfailureCause = value;
        }
        
  
                
                
        public void initWithDefaults() {
            
        }

        public IASN1PreparedElementData getPreparedData() {
            return preparedData_O_smsFailureSpecificInfoSequenceType;
        }

       private static IASN1PreparedElementData preparedData_O_smsFailureSpecificInfoSequenceType = CoderFactory.getInstance().newPreparedElementData(O_smsFailureSpecificInfoSequenceType.class);
                
       }

       
                
        @ASN1Element ( name = "o-smsFailureSpecificInfo", isOptional =  false , hasTag =  true, tag = 0 , hasDefaultValue =  false  )
    
	private O_smsFailureSpecificInfoSequenceType o_smsFailureSpecificInfo = null;
                
  

       @ASN1PreparedElement
       @ASN1Sequence ( name = "o-smsSubmittedSpecificInfo" , isSet = false )
       public static class O_smsSubmittedSpecificInfoSequenceType implements IASN1PreparedElement {
                @ASN1Integer( name = "" )
    
            @ASN1SizeConstraint ( max = 0L )
        
        @ASN1Element ( name = "foo", isOptional =  true , hasTag =  false  , hasDefaultValue =  false  )
    
	private Long foo = null;
                
  
        
        public Long getFoo () {
            return this.foo;
        }

        
        public boolean isFooPresent () {
            return this.foo != null;
        }
        

        public void setFoo (Long value) {
            this.foo = value;
        }
        
  
                
                
        public void initWithDefaults() {
            
        }

        public IASN1PreparedElementData getPreparedData() {
            return preparedData_O_smsSubmittedSpecificInfoSequenceType;
        }

       private static IASN1PreparedElementData preparedData_O_smsSubmittedSpecificInfoSequenceType = CoderFactory.getInstance().newPreparedElementData(O_smsSubmittedSpecificInfoSequenceType.class);
                
       }

       
                
        @ASN1Element ( name = "o-smsSubmittedSpecificInfo", isOptional =  false , hasTag =  true, tag = 1 , hasDefaultValue =  false  )
    
	private O_smsSubmittedSpecificInfoSequenceType o_smsSubmittedSpecificInfo = null;
                
  

       @ASN1PreparedElement
       @ASN1Sequence ( name = "t-smsFailureSpecificInfo" , isSet = false )
       public static class T_smsFailureSpecificInfoSequenceType implements IASN1PreparedElement {
                
        @ASN1Element ( name = "failureCause", isOptional =  true , hasTag =  true, tag = 0 , hasDefaultValue =  false  )
    
	private MT_SMSCause failureCause = null;
                
  
        
        public MT_SMSCause getFailureCause () {
            return this.failureCause;
        }

        
        public boolean isFailureCausePresent () {
            return this.failureCause != null;
        }
        

        public void setFailureCause (MT_SMSCause value) {
            this.failureCause = value;
        }
        
  
                
                
        public void initWithDefaults() {
            
        }

        public IASN1PreparedElementData getPreparedData() {
            return preparedData_T_smsFailureSpecificInfoSequenceType;
        }

       private static IASN1PreparedElementData preparedData_T_smsFailureSpecificInfoSequenceType = CoderFactory.getInstance().newPreparedElementData(T_smsFailureSpecificInfoSequenceType.class);
                
       }

       
                
        @ASN1Element ( name = "t-smsFailureSpecificInfo", isOptional =  false , hasTag =  true, tag = 2 , hasDefaultValue =  false  )
    
	private T_smsFailureSpecificInfoSequenceType t_smsFailureSpecificInfo = null;
                
  

       @ASN1PreparedElement
       @ASN1Sequence ( name = "t-smsDeliverySpecificInfo" , isSet = false )
       public static class T_smsDeliverySpecificInfoSequenceType implements IASN1PreparedElement {
                
                
                
        public void initWithDefaults() {
            
        }

        public IASN1PreparedElementData getPreparedData() {
            return preparedData_T_smsDeliverySpecificInfoSequenceType;
        }

       private static IASN1PreparedElementData preparedData_T_smsDeliverySpecificInfoSequenceType = CoderFactory.getInstance().newPreparedElementData(T_smsDeliverySpecificInfoSequenceType.class);
                
       }

       
                
        @ASN1Element ( name = "t-smsDeliverySpecificInfo", isOptional =  false , hasTag =  true, tag = 3 , hasDefaultValue =  false  )
    
	private T_smsDeliverySpecificInfoSequenceType t_smsDeliverySpecificInfo = null;
                
  
        
        public O_smsFailureSpecificInfoSequenceType getO_smsFailureSpecificInfo () {
            return this.o_smsFailureSpecificInfo;
        }

        public boolean isO_smsFailureSpecificInfoSelected () {
            return this.o_smsFailureSpecificInfo != null;
        }

        private void setO_smsFailureSpecificInfo (O_smsFailureSpecificInfoSequenceType value) {
            this.o_smsFailureSpecificInfo = value;
        }

        
        public void selectO_smsFailureSpecificInfo (O_smsFailureSpecificInfoSequenceType value) {
            this.o_smsFailureSpecificInfo = value;
            
                    setO_smsSubmittedSpecificInfo(null);
                
                    setT_smsFailureSpecificInfo(null);
                
                    setT_smsDeliverySpecificInfo(null);
                            
        }

        
  
        
        public O_smsSubmittedSpecificInfoSequenceType getO_smsSubmittedSpecificInfo () {
            return this.o_smsSubmittedSpecificInfo;
        }

        public boolean isO_smsSubmittedSpecificInfoSelected () {
            return this.o_smsSubmittedSpecificInfo != null;
        }

        private void setO_smsSubmittedSpecificInfo (O_smsSubmittedSpecificInfoSequenceType value) {
            this.o_smsSubmittedSpecificInfo = value;
        }

        
        public void selectO_smsSubmittedSpecificInfo (O_smsSubmittedSpecificInfoSequenceType value) {
            this.o_smsSubmittedSpecificInfo = value;
            
                    setO_smsFailureSpecificInfo(null);
                
                    setT_smsFailureSpecificInfo(null);
                
                    setT_smsDeliverySpecificInfo(null);
                            
        }

        
  
        
        public T_smsFailureSpecificInfoSequenceType getT_smsFailureSpecificInfo () {
            return this.t_smsFailureSpecificInfo;
        }

        public boolean isT_smsFailureSpecificInfoSelected () {
            return this.t_smsFailureSpecificInfo != null;
        }

        private void setT_smsFailureSpecificInfo (T_smsFailureSpecificInfoSequenceType value) {
            this.t_smsFailureSpecificInfo = value;
        }

        
        public void selectT_smsFailureSpecificInfo (T_smsFailureSpecificInfoSequenceType value) {
            this.t_smsFailureSpecificInfo = value;
            
                    setO_smsFailureSpecificInfo(null);
                
                    setO_smsSubmittedSpecificInfo(null);
                
                    setT_smsDeliverySpecificInfo(null);
                            
        }

        
  
        
        public T_smsDeliverySpecificInfoSequenceType getT_smsDeliverySpecificInfo () {
            return this.t_smsDeliverySpecificInfo;
        }

        public boolean isT_smsDeliverySpecificInfoSelected () {
            return this.t_smsDeliverySpecificInfo != null;
        }

        private void setT_smsDeliverySpecificInfo (T_smsDeliverySpecificInfoSequenceType value) {
            this.t_smsDeliverySpecificInfo = value;
        }

        
        public void selectT_smsDeliverySpecificInfo (T_smsDeliverySpecificInfoSequenceType value) {
            this.t_smsDeliverySpecificInfo = value;
            
                    setO_smsFailureSpecificInfo(null);
                
                    setO_smsSubmittedSpecificInfo(null);
                
                    setT_smsFailureSpecificInfo(null);
                            
        }

        
  

	    public void initWithDefaults() {
	    }

        private static IASN1PreparedElementData preparedData = CoderFactory.getInstance().newPreparedElementData(EventSpecificInformationSMS.class);
        public IASN1PreparedElementData getPreparedData() {
            return preparedData;
        }


    }
            