
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
    @ASN1Sequence ( name = "ConnectArg", isSet = false )
    public class ConnectArg implements IASN1PreparedElement {
            
        @ASN1Element ( name = "destinationRoutingAddress", isOptional =  false , hasTag =  true, tag = 0 , hasDefaultValue =  false  )
    
	private DestinationRoutingAddress destinationRoutingAddress = null;
                
  
        @ASN1Element ( name = "alertingPattern", isOptional =  true , hasTag =  true, tag = 1 , hasDefaultValue =  false  )
    
	private AlertingPattern alertingPattern = null;
                
  
        @ASN1Element ( name = "originalCalledPartyID", isOptional =  true , hasTag =  true, tag = 6 , hasDefaultValue =  false  )
    
	private OriginalCalledPartyID originalCalledPartyID = null;
                
  
@ASN1SequenceOf( name = "extensions", isSetOf = false ) 

    @ASN1ValueRangeConstraint ( 
		
		min = 1L, 
		
		max = 10L 
		
	   )
	   
        @ASN1Element ( name = "extensions", isOptional =  true , hasTag =  true, tag = 10 , hasDefaultValue =  false  )
    
	private java.util.Collection<ExtensionField>  extensions = null;
                
  
        @ASN1Element ( name = "callingPartysCategory", isOptional =  true , hasTag =  true, tag = 28 , hasDefaultValue =  false  )
    
	private CallingPartysCategory callingPartysCategory = null;
                
  
        @ASN1Element ( name = "redirectingPartyID", isOptional =  true , hasTag =  true, tag = 29 , hasDefaultValue =  false  )
    
	private RedirectingPartyID redirectingPartyID = null;
                
  
        @ASN1Element ( name = "redirectionInformation", isOptional =  true , hasTag =  true, tag = 30 , hasDefaultValue =  false  )
    
	private RedirectionInformation redirectionInformation = null;
                
  
        @ASN1Element ( name = "genericNumbers", isOptional =  true , hasTag =  true, tag = 14 , hasDefaultValue =  false  )
    
	private GenericNumbers genericNumbers = null;
                
  
        @ASN1Element ( name = "suppressionOfAnnouncement", isOptional =  true , hasTag =  true, tag = 55 , hasDefaultValue =  false  )
    
	private SuppressionOfAnnouncement suppressionOfAnnouncement = null;
                
  
        @ASN1Element ( name = "oCSIApplicable", isOptional =  true , hasTag =  true, tag = 56 , hasDefaultValue =  false  )
    
	private OCSIApplicable oCSIApplicable = null;
                
  
        @ASN1Element ( name = "na-Info", isOptional =  true , hasTag =  true, tag = 57 , hasDefaultValue =  false  )
    
	private NA_Info na_Info = null;
                
  
        
        public DestinationRoutingAddress getDestinationRoutingAddress () {
            return this.destinationRoutingAddress;
        }

        

        public void setDestinationRoutingAddress (DestinationRoutingAddress value) {
            this.destinationRoutingAddress = value;
        }
        
  
        
        public AlertingPattern getAlertingPattern () {
            return this.alertingPattern;
        }

        
        public boolean isAlertingPatternPresent () {
            return this.alertingPattern != null;
        }
        

        public void setAlertingPattern (AlertingPattern value) {
            this.alertingPattern = value;
        }
        
  
        
        public OriginalCalledPartyID getOriginalCalledPartyID () {
            return this.originalCalledPartyID;
        }

        
        public boolean isOriginalCalledPartyIDPresent () {
            return this.originalCalledPartyID != null;
        }
        

        public void setOriginalCalledPartyID (OriginalCalledPartyID value) {
            this.originalCalledPartyID = value;
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
        
  
        
        public CallingPartysCategory getCallingPartysCategory () {
            return this.callingPartysCategory;
        }

        
        public boolean isCallingPartysCategoryPresent () {
            return this.callingPartysCategory != null;
        }
        

        public void setCallingPartysCategory (CallingPartysCategory value) {
            this.callingPartysCategory = value;
        }
        
  
        
        public RedirectingPartyID getRedirectingPartyID () {
            return this.redirectingPartyID;
        }

        
        public boolean isRedirectingPartyIDPresent () {
            return this.redirectingPartyID != null;
        }
        

        public void setRedirectingPartyID (RedirectingPartyID value) {
            this.redirectingPartyID = value;
        }
        
  
        
        public RedirectionInformation getRedirectionInformation () {
            return this.redirectionInformation;
        }

        
        public boolean isRedirectionInformationPresent () {
            return this.redirectionInformation != null;
        }
        

        public void setRedirectionInformation (RedirectionInformation value) {
            this.redirectionInformation = value;
        }
        
  
        
        public GenericNumbers getGenericNumbers () {
            return this.genericNumbers;
        }

        
        public boolean isGenericNumbersPresent () {
            return this.genericNumbers != null;
        }
        

        public void setGenericNumbers (GenericNumbers value) {
            this.genericNumbers = value;
        }
        
  
        
        public SuppressionOfAnnouncement getSuppressionOfAnnouncement () {
            return this.suppressionOfAnnouncement;
        }

        
        public boolean isSuppressionOfAnnouncementPresent () {
            return this.suppressionOfAnnouncement != null;
        }
        

        public void setSuppressionOfAnnouncement (SuppressionOfAnnouncement value) {
            this.suppressionOfAnnouncement = value;
        }
        
  
        
        public OCSIApplicable getOCSIApplicable () {
            return this.oCSIApplicable;
        }

        
        public boolean isOCSIApplicablePresent () {
            return this.oCSIApplicable != null;
        }
        

        public void setOCSIApplicable (OCSIApplicable value) {
            this.oCSIApplicable = value;
        }
        
  
        
        public NA_Info getNa_Info () {
            return this.na_Info;
        }

        
        public boolean isNa_InfoPresent () {
            return this.na_Info != null;
        }
        

        public void setNa_Info (NA_Info value) {
            this.na_Info = value;
        }
        
  
                    
        
        public void initWithDefaults() {
            
        }

        private static IASN1PreparedElementData preparedData = CoderFactory.getInstance().newPreparedElementData(ConnectArg.class);
        public IASN1PreparedElementData getPreparedData() {
            return preparedData;
        }

            
    }
            