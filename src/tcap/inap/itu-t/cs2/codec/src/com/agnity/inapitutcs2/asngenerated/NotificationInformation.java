
package com.agnity.inapitutcs2.asngenerated;
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
    @ASN1Choice ( name = "NotificationInformation" )
    public class NotificationInformation implements IASN1PreparedElement {
            

       @ASN1PreparedElement
       @ASN1Sequence ( name = "userAbandonSpecificInfo" , isSet = false )
       public static class UserAbandonSpecificInfoSequenceType implements IASN1PreparedElement {
                
                
                
        public void initWithDefaults() {
            
        }

        public IASN1PreparedElementData getPreparedData() {
            return preparedData_UserAbandonSpecificInfoSequenceType;
        }

       private static IASN1PreparedElementData preparedData_UserAbandonSpecificInfoSequenceType = CoderFactory.getInstance().newPreparedElementData(UserAbandonSpecificInfoSequenceType.class);
                
       }

       
                
        @ASN1Element ( name = "userAbandonSpecificInfo", isOptional =  false , hasTag =  true, tag = 0 , hasDefaultValue =  false  )
    
	private UserAbandonSpecificInfoSequenceType userAbandonSpecificInfo = null;
                
  

       @ASN1PreparedElement
       @ASN1Sequence ( name = "callFailureSpecificInfo" , isSet = false )
       public static class CallFailureSpecificInfoSequenceType implements IASN1PreparedElement {
                
        @ASN1Element ( name = "failureCause", isOptional =  true , hasTag =  true, tag = 0 , hasDefaultValue =  false  )
    
	private Cause failureCause = null;
                
  
        
        public Cause getFailureCause () {
            return this.failureCause;
        }

        
        public boolean isFailureCausePresent () {
            return this.failureCause != null;
        }
        

        public void setFailureCause (Cause value) {
            this.failureCause = value;
        }
        
  
                
                
        public void initWithDefaults() {
            
        }

        public IASN1PreparedElementData getPreparedData() {
            return preparedData_CallFailureSpecificInfoSequenceType;
        }

       private static IASN1PreparedElementData preparedData_CallFailureSpecificInfoSequenceType = CoderFactory.getInstance().newPreparedElementData(CallFailureSpecificInfoSequenceType.class);
                
       }

       
                
        @ASN1Element ( name = "callFailureSpecificInfo", isOptional =  false , hasTag =  true, tag = 1 , hasDefaultValue =  false  )
    
	private CallFailureSpecificInfoSequenceType callFailureSpecificInfo = null;
                
  

       @ASN1PreparedElement
       @ASN1Sequence ( name = "noReplySpecificInfo" , isSet = false )
       public static class NoReplySpecificInfoSequenceType implements IASN1PreparedElement {
                
                
                
        public void initWithDefaults() {
            
        }

        public IASN1PreparedElementData getPreparedData() {
            return preparedData_NoReplySpecificInfoSequenceType;
        }

       private static IASN1PreparedElementData preparedData_NoReplySpecificInfoSequenceType = CoderFactory.getInstance().newPreparedElementData(NoReplySpecificInfoSequenceType.class);
                
       }

       
                
        @ASN1Element ( name = "noReplySpecificInfo", isOptional =  false , hasTag =  true, tag = 2 , hasDefaultValue =  false  )
    
	private NoReplySpecificInfoSequenceType noReplySpecificInfo = null;
                
  

       @ASN1PreparedElement
       @ASN1Sequence ( name = "callReleaseSpecificInfo" , isSet = false )
       public static class CallReleaseSpecificInfoSequenceType implements IASN1PreparedElement {
                
        @ASN1Element ( name = "releaseCause", isOptional =  true , hasTag =  true, tag = 0 , hasDefaultValue =  false  )
    
	private Cause releaseCause = null;
                
  
        @ASN1Element ( name = "timeStamp", isOptional =  true , hasTag =  true, tag = 1 , hasDefaultValue =  false  )
    
	private DateAndTime timeStamp = null;
                
  
        
        public Cause getReleaseCause () {
            return this.releaseCause;
        }

        
        public boolean isReleaseCausePresent () {
            return this.releaseCause != null;
        }
        

        public void setReleaseCause (Cause value) {
            this.releaseCause = value;
        }
        
  
        
        public DateAndTime getTimeStamp () {
            return this.timeStamp;
        }

        
        public boolean isTimeStampPresent () {
            return this.timeStamp != null;
        }
        

        public void setTimeStamp (DateAndTime value) {
            this.timeStamp = value;
        }
        
  
                
                
        public void initWithDefaults() {
            
        }

        public IASN1PreparedElementData getPreparedData() {
            return preparedData_CallReleaseSpecificInfoSequenceType;
        }

       private static IASN1PreparedElementData preparedData_CallReleaseSpecificInfoSequenceType = CoderFactory.getInstance().newPreparedElementData(CallReleaseSpecificInfoSequenceType.class);
                
       }

       
                
        @ASN1Element ( name = "callReleaseSpecificInfo", isOptional =  false , hasTag =  true, tag = 3 , hasDefaultValue =  false  )
    
	private CallReleaseSpecificInfoSequenceType callReleaseSpecificInfo = null;
                
  

       @ASN1PreparedElement
       @ASN1Sequence ( name = "ssInvocationSpecificInfo" , isSet = false )
       public static class SsInvocationSpecificInfoSequenceType implements IASN1PreparedElement {
                
        @ASN1Element ( name = "invokedService", isOptional =  false , hasTag =  true, tag = 0 , hasDefaultValue =  false  )
    
	private InvokableService invokedService = null;
                
  
        
        public InvokableService getInvokedService () {
            return this.invokedService;
        }

        

        public void setInvokedService (InvokableService value) {
            this.invokedService = value;
        }
        
  
                
                
        public void initWithDefaults() {
            
        }

        public IASN1PreparedElementData getPreparedData() {
            return preparedData_SsInvocationSpecificInfoSequenceType;
        }

       private static IASN1PreparedElementData preparedData_SsInvocationSpecificInfoSequenceType = CoderFactory.getInstance().newPreparedElementData(SsInvocationSpecificInfoSequenceType.class);
                
       }

       
                
        @ASN1Element ( name = "ssInvocationSpecificInfo", isOptional =  false , hasTag =  true, tag = 4 , hasDefaultValue =  false  )
    
	private SsInvocationSpecificInfoSequenceType ssInvocationSpecificInfo = null;
                
  

       @ASN1PreparedElement
       @ASN1Sequence ( name = "creditLimitReachedSpecificInfo" , isSet = false )
       public static class CreditLimitReachedSpecificInfoSequenceType implements IASN1PreparedElement {
                
        @ASN1Element ( name = "timeStamp", isOptional =  true , hasTag =  true, tag = 0 , hasDefaultValue =  false  )
    
	private DateAndTime timeStamp = null;
                
  
        
        public DateAndTime getTimeStamp () {
            return this.timeStamp;
        }

        
        public boolean isTimeStampPresent () {
            return this.timeStamp != null;
        }
        

        public void setTimeStamp (DateAndTime value) {
            this.timeStamp = value;
        }
        
  
                
                
        public void initWithDefaults() {
            
        }

        public IASN1PreparedElementData getPreparedData() {
            return preparedData_CreditLimitReachedSpecificInfoSequenceType;
        }

       private static IASN1PreparedElementData preparedData_CreditLimitReachedSpecificInfoSequenceType = CoderFactory.getInstance().newPreparedElementData(CreditLimitReachedSpecificInfoSequenceType.class);
                
       }

       
                
        @ASN1Element ( name = "creditLimitReachedSpecificInfo", isOptional =  false , hasTag =  true, tag = 5 , hasDefaultValue =  false  )
    
	private CreditLimitReachedSpecificInfoSequenceType creditLimitReachedSpecificInfo = null;
                
  

       @ASN1PreparedElement
       @ASN1Sequence ( name = "callDurationSpecificInfo" , isSet = false )
       public static class CallDurationSpecificInfoSequenceType implements IASN1PreparedElement {
                
        @ASN1Element ( name = "timeStamp", isOptional =  true , hasTag =  true, tag = 0 , hasDefaultValue =  false  )
    
	private DateAndTime timeStamp = null;
                
  
        
        public DateAndTime getTimeStamp () {
            return this.timeStamp;
        }

        
        public boolean isTimeStampPresent () {
            return this.timeStamp != null;
        }
        

        public void setTimeStamp (DateAndTime value) {
            this.timeStamp = value;
        }
        
  
                
                
        public void initWithDefaults() {
            
        }

        public IASN1PreparedElementData getPreparedData() {
            return preparedData_CallDurationSpecificInfoSequenceType;
        }

       private static IASN1PreparedElementData preparedData_CallDurationSpecificInfoSequenceType = CoderFactory.getInstance().newPreparedElementData(CallDurationSpecificInfoSequenceType.class);
                
       }

       
                
        @ASN1Element ( name = "callDurationSpecificInfo", isOptional =  false , hasTag =  true, tag = 6 , hasDefaultValue =  false  )
    
	private CallDurationSpecificInfoSequenceType callDurationSpecificInfo = null;
                
  

       @ASN1PreparedElement
       @ASN1Sequence ( name = "calledNumberSpecificInfo" , isSet = false )
       public static class CalledNumberSpecificInfoSequenceType implements IASN1PreparedElement {
                
        @ASN1Element ( name = "calledNumber", isOptional =  true , hasTag =  true, tag = 0 , hasDefaultValue =  false  )
    
	private CalledPartyNumber calledNumber = null;
                
  
        
        public CalledPartyNumber getCalledNumber () {
            return this.calledNumber;
        }

        
        public boolean isCalledNumberPresent () {
            return this.calledNumber != null;
        }
        

        public void setCalledNumber (CalledPartyNumber value) {
            this.calledNumber = value;
        }
        
  
                
                
        public void initWithDefaults() {
            
        }

        public IASN1PreparedElementData getPreparedData() {
            return preparedData_CalledNumberSpecificInfoSequenceType;
        }

       private static IASN1PreparedElementData preparedData_CalledNumberSpecificInfoSequenceType = CoderFactory.getInstance().newPreparedElementData(CalledNumberSpecificInfoSequenceType.class);
                
       }

       
                
        @ASN1Element ( name = "calledNumberSpecificInfo", isOptional =  false , hasTag =  true, tag = 7 , hasDefaultValue =  false  )
    
	private CalledNumberSpecificInfoSequenceType calledNumberSpecificInfo = null;
                
  

       @ASN1PreparedElement
       @ASN1Sequence ( name = "answeredCallSpecificInfo" , isSet = false )
       public static class AnsweredCallSpecificInfoSequenceType implements IASN1PreparedElement {
                
        @ASN1Element ( name = "timeStamp", isOptional =  true , hasTag =  true, tag = 0 , hasDefaultValue =  false  )
    
	private DateAndTime timeStamp = null;
                
  
        
        public DateAndTime getTimeStamp () {
            return this.timeStamp;
        }

        
        public boolean isTimeStampPresent () {
            return this.timeStamp != null;
        }
        

        public void setTimeStamp (DateAndTime value) {
            this.timeStamp = value;
        }
        
  
                
                
        public void initWithDefaults() {
            
        }

        public IASN1PreparedElementData getPreparedData() {
            return preparedData_AnsweredCallSpecificInfoSequenceType;
        }

       private static IASN1PreparedElementData preparedData_AnsweredCallSpecificInfoSequenceType = CoderFactory.getInstance().newPreparedElementData(AnsweredCallSpecificInfoSequenceType.class);
                
       }

       
                
        @ASN1Element ( name = "answeredCallSpecificInfo", isOptional =  false , hasTag =  true, tag = 8 , hasDefaultValue =  false  )
    
	private AnsweredCallSpecificInfoSequenceType answeredCallSpecificInfo = null;
                
  
        
        public UserAbandonSpecificInfoSequenceType getUserAbandonSpecificInfo () {
            return this.userAbandonSpecificInfo;
        }

        public boolean isUserAbandonSpecificInfoSelected () {
            return this.userAbandonSpecificInfo != null;
        }

        private void setUserAbandonSpecificInfo (UserAbandonSpecificInfoSequenceType value) {
            this.userAbandonSpecificInfo = value;
        }

        
        public void selectUserAbandonSpecificInfo (UserAbandonSpecificInfoSequenceType value) {
            this.userAbandonSpecificInfo = value;
            
                    setCallFailureSpecificInfo(null);
                
                    setNoReplySpecificInfo(null);
                
                    setCallReleaseSpecificInfo(null);
                
                    setSsInvocationSpecificInfo(null);
                
                    setCreditLimitReachedSpecificInfo(null);
                
                    setCallDurationSpecificInfo(null);
                
                    setCalledNumberSpecificInfo(null);
                
                    setAnsweredCallSpecificInfo(null);
                            
        }

        
  
        
        public CallFailureSpecificInfoSequenceType getCallFailureSpecificInfo () {
            return this.callFailureSpecificInfo;
        }

        public boolean isCallFailureSpecificInfoSelected () {
            return this.callFailureSpecificInfo != null;
        }

        private void setCallFailureSpecificInfo (CallFailureSpecificInfoSequenceType value) {
            this.callFailureSpecificInfo = value;
        }

        
        public void selectCallFailureSpecificInfo (CallFailureSpecificInfoSequenceType value) {
            this.callFailureSpecificInfo = value;
            
                    setUserAbandonSpecificInfo(null);
                
                    setNoReplySpecificInfo(null);
                
                    setCallReleaseSpecificInfo(null);
                
                    setSsInvocationSpecificInfo(null);
                
                    setCreditLimitReachedSpecificInfo(null);
                
                    setCallDurationSpecificInfo(null);
                
                    setCalledNumberSpecificInfo(null);
                
                    setAnsweredCallSpecificInfo(null);
                            
        }

        
  
        
        public NoReplySpecificInfoSequenceType getNoReplySpecificInfo () {
            return this.noReplySpecificInfo;
        }

        public boolean isNoReplySpecificInfoSelected () {
            return this.noReplySpecificInfo != null;
        }

        private void setNoReplySpecificInfo (NoReplySpecificInfoSequenceType value) {
            this.noReplySpecificInfo = value;
        }

        
        public void selectNoReplySpecificInfo (NoReplySpecificInfoSequenceType value) {
            this.noReplySpecificInfo = value;
            
                    setUserAbandonSpecificInfo(null);
                
                    setCallFailureSpecificInfo(null);
                
                    setCallReleaseSpecificInfo(null);
                
                    setSsInvocationSpecificInfo(null);
                
                    setCreditLimitReachedSpecificInfo(null);
                
                    setCallDurationSpecificInfo(null);
                
                    setCalledNumberSpecificInfo(null);
                
                    setAnsweredCallSpecificInfo(null);
                            
        }

        
  
        
        public CallReleaseSpecificInfoSequenceType getCallReleaseSpecificInfo () {
            return this.callReleaseSpecificInfo;
        }

        public boolean isCallReleaseSpecificInfoSelected () {
            return this.callReleaseSpecificInfo != null;
        }

        private void setCallReleaseSpecificInfo (CallReleaseSpecificInfoSequenceType value) {
            this.callReleaseSpecificInfo = value;
        }

        
        public void selectCallReleaseSpecificInfo (CallReleaseSpecificInfoSequenceType value) {
            this.callReleaseSpecificInfo = value;
            
                    setUserAbandonSpecificInfo(null);
                
                    setCallFailureSpecificInfo(null);
                
                    setNoReplySpecificInfo(null);
                
                    setSsInvocationSpecificInfo(null);
                
                    setCreditLimitReachedSpecificInfo(null);
                
                    setCallDurationSpecificInfo(null);
                
                    setCalledNumberSpecificInfo(null);
                
                    setAnsweredCallSpecificInfo(null);
                            
        }

        
  
        
        public SsInvocationSpecificInfoSequenceType getSsInvocationSpecificInfo () {
            return this.ssInvocationSpecificInfo;
        }

        public boolean isSsInvocationSpecificInfoSelected () {
            return this.ssInvocationSpecificInfo != null;
        }

        private void setSsInvocationSpecificInfo (SsInvocationSpecificInfoSequenceType value) {
            this.ssInvocationSpecificInfo = value;
        }

        
        public void selectSsInvocationSpecificInfo (SsInvocationSpecificInfoSequenceType value) {
            this.ssInvocationSpecificInfo = value;
            
                    setUserAbandonSpecificInfo(null);
                
                    setCallFailureSpecificInfo(null);
                
                    setNoReplySpecificInfo(null);
                
                    setCallReleaseSpecificInfo(null);
                
                    setCreditLimitReachedSpecificInfo(null);
                
                    setCallDurationSpecificInfo(null);
                
                    setCalledNumberSpecificInfo(null);
                
                    setAnsweredCallSpecificInfo(null);
                            
        }

        
  
        
        public CreditLimitReachedSpecificInfoSequenceType getCreditLimitReachedSpecificInfo () {
            return this.creditLimitReachedSpecificInfo;
        }

        public boolean isCreditLimitReachedSpecificInfoSelected () {
            return this.creditLimitReachedSpecificInfo != null;
        }

        private void setCreditLimitReachedSpecificInfo (CreditLimitReachedSpecificInfoSequenceType value) {
            this.creditLimitReachedSpecificInfo = value;
        }

        
        public void selectCreditLimitReachedSpecificInfo (CreditLimitReachedSpecificInfoSequenceType value) {
            this.creditLimitReachedSpecificInfo = value;
            
                    setUserAbandonSpecificInfo(null);
                
                    setCallFailureSpecificInfo(null);
                
                    setNoReplySpecificInfo(null);
                
                    setCallReleaseSpecificInfo(null);
                
                    setSsInvocationSpecificInfo(null);
                
                    setCallDurationSpecificInfo(null);
                
                    setCalledNumberSpecificInfo(null);
                
                    setAnsweredCallSpecificInfo(null);
                            
        }

        
  
        
        public CallDurationSpecificInfoSequenceType getCallDurationSpecificInfo () {
            return this.callDurationSpecificInfo;
        }

        public boolean isCallDurationSpecificInfoSelected () {
            return this.callDurationSpecificInfo != null;
        }

        private void setCallDurationSpecificInfo (CallDurationSpecificInfoSequenceType value) {
            this.callDurationSpecificInfo = value;
        }

        
        public void selectCallDurationSpecificInfo (CallDurationSpecificInfoSequenceType value) {
            this.callDurationSpecificInfo = value;
            
                    setUserAbandonSpecificInfo(null);
                
                    setCallFailureSpecificInfo(null);
                
                    setNoReplySpecificInfo(null);
                
                    setCallReleaseSpecificInfo(null);
                
                    setSsInvocationSpecificInfo(null);
                
                    setCreditLimitReachedSpecificInfo(null);
                
                    setCalledNumberSpecificInfo(null);
                
                    setAnsweredCallSpecificInfo(null);
                            
        }

        
  
        
        public CalledNumberSpecificInfoSequenceType getCalledNumberSpecificInfo () {
            return this.calledNumberSpecificInfo;
        }

        public boolean isCalledNumberSpecificInfoSelected () {
            return this.calledNumberSpecificInfo != null;
        }

        private void setCalledNumberSpecificInfo (CalledNumberSpecificInfoSequenceType value) {
            this.calledNumberSpecificInfo = value;
        }

        
        public void selectCalledNumberSpecificInfo (CalledNumberSpecificInfoSequenceType value) {
            this.calledNumberSpecificInfo = value;
            
                    setUserAbandonSpecificInfo(null);
                
                    setCallFailureSpecificInfo(null);
                
                    setNoReplySpecificInfo(null);
                
                    setCallReleaseSpecificInfo(null);
                
                    setSsInvocationSpecificInfo(null);
                
                    setCreditLimitReachedSpecificInfo(null);
                
                    setCallDurationSpecificInfo(null);
                
                    setAnsweredCallSpecificInfo(null);
                            
        }

        
  
        
        public AnsweredCallSpecificInfoSequenceType getAnsweredCallSpecificInfo () {
            return this.answeredCallSpecificInfo;
        }

        public boolean isAnsweredCallSpecificInfoSelected () {
            return this.answeredCallSpecificInfo != null;
        }

        private void setAnsweredCallSpecificInfo (AnsweredCallSpecificInfoSequenceType value) {
            this.answeredCallSpecificInfo = value;
        }

        
        public void selectAnsweredCallSpecificInfo (AnsweredCallSpecificInfoSequenceType value) {
            this.answeredCallSpecificInfo = value;
            
                    setUserAbandonSpecificInfo(null);
                
                    setCallFailureSpecificInfo(null);
                
                    setNoReplySpecificInfo(null);
                
                    setCallReleaseSpecificInfo(null);
                
                    setSsInvocationSpecificInfo(null);
                
                    setCreditLimitReachedSpecificInfo(null);
                
                    setCallDurationSpecificInfo(null);
                
                    setCalledNumberSpecificInfo(null);
                            
        }

        
  

	    public void initWithDefaults() {
	    }

        private static IASN1PreparedElementData preparedData = CoderFactory.getInstance().newPreparedElementData(NotificationInformation.class);
        public IASN1PreparedElementData getPreparedData() {
            return preparedData;
        }


    }
            