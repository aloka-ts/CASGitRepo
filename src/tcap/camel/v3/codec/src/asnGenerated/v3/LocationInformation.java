
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
    @ASN1Sequence ( name = "LocationInformation", isSet = false )
    public class LocationInformation implements IASN1PreparedElement {
            
        @ASN1Element ( name = "ageOfLocationInformation", isOptional =  true , hasTag =  false  , hasDefaultValue =  false  )
    
	private AgeOfLocationInformation ageOfLocationInformation = null;
                
  
        @ASN1Element ( name = "geographicalInformation", isOptional =  true , hasTag =  true, tag = 0 , hasDefaultValue =  false  )
    
	private GeographicalInformation geographicalInformation = null;
                
  
        @ASN1Element ( name = "vlr-number", isOptional =  true , hasTag =  true, tag = 1 , hasDefaultValue =  false  )
    
	private ISDN_AddressString vlr_number = null;
                
  
        @ASN1Element ( name = "locationNumber", isOptional =  true , hasTag =  true, tag = 2 , hasDefaultValue =  false  )
    
	private LocationNumber locationNumber = null;
                
  
        @ASN1Element ( name = "cellGlobalIdOrServiceAreaIdOrLAI", isOptional =  true , hasTag =  true, tag = 3 , hasDefaultValue =  false  )
    
	private CellGlobalIdOrServiceAreaIdOrLAI cellGlobalIdOrServiceAreaIdOrLAI = null;
                
  
        @ASN1Element ( name = "extensionContainer", isOptional =  true , hasTag =  true, tag = 4 , hasDefaultValue =  false  )
    
	private ExtensionContainer extensionContainer = null;
                
  
        @ASN1Element ( name = "selectedLSA-Id", isOptional =  true , hasTag =  true, tag = 5 , hasDefaultValue =  false  )
    
	private LSAIdentity selectedLSA_Id = null;
                
  
        @ASN1Element ( name = "msc-Number", isOptional =  true , hasTag =  true, tag = 6 , hasDefaultValue =  false  )
    
	private ISDN_AddressString msc_Number = null;
                
  
        @ASN1Element ( name = "geodeticInformation", isOptional =  true , hasTag =  true, tag = 7 , hasDefaultValue =  false  )
    
	private GeodeticInformation geodeticInformation = null;
                
  
       @ASN1Null ( name = "currentLocationRetrieved" ) 
    
        @ASN1Element ( name = "currentLocationRetrieved", isOptional =  true , hasTag =  true, tag = 8 , hasDefaultValue =  false  )
    
	private org.bn.types.NullObject currentLocationRetrieved = null;
                
  
        @ASN1Null ( name = "sai-Present" ) 
    
        @ASN1Element ( name = "sai-Present", isOptional =  true , hasTag =  true, tag = 9 , hasDefaultValue =  false  )
    
	private org.bn.types.NullObject sai_Present = null;
                
  
        
        public AgeOfLocationInformation getAgeOfLocationInformation () {
            return this.ageOfLocationInformation;
        }

        
        public boolean isAgeOfLocationInformationPresent () {
            return this.ageOfLocationInformation != null;
        }
        

        public void setAgeOfLocationInformation (AgeOfLocationInformation value) {
            this.ageOfLocationInformation = value;
        }
        
  
        
        public GeographicalInformation getGeographicalInformation () {
            return this.geographicalInformation;
        }

        
        public boolean isGeographicalInformationPresent () {
            return this.geographicalInformation != null;
        }
        

        public void setGeographicalInformation (GeographicalInformation value) {
            this.geographicalInformation = value;
        }
        
  
        
        public ISDN_AddressString getVlr_number () {
            return this.vlr_number;
        }

        
        public boolean isVlr_numberPresent () {
            return this.vlr_number != null;
        }
        

        public void setVlr_number (ISDN_AddressString value) {
            this.vlr_number = value;
        }
        
  
        
        public LocationNumber getLocationNumber () {
            return this.locationNumber;
        }

        
        public boolean isLocationNumberPresent () {
            return this.locationNumber != null;
        }
        

        public void setLocationNumber (LocationNumber value) {
            this.locationNumber = value;
        }
        
  
        
        public CellGlobalIdOrServiceAreaIdOrLAI getCellGlobalIdOrServiceAreaIdOrLAI () {
            return this.cellGlobalIdOrServiceAreaIdOrLAI;
        }

        
        public boolean isCellGlobalIdOrServiceAreaIdOrLAIPresent () {
            return this.cellGlobalIdOrServiceAreaIdOrLAI != null;
        }
        

        public void setCellGlobalIdOrServiceAreaIdOrLAI (CellGlobalIdOrServiceAreaIdOrLAI value) {
            this.cellGlobalIdOrServiceAreaIdOrLAI = value;
        }
        
  
        
        public ExtensionContainer getExtensionContainer () {
            return this.extensionContainer;
        }

        
        public boolean isExtensionContainerPresent () {
            return this.extensionContainer != null;
        }
        

        public void setExtensionContainer (ExtensionContainer value) {
            this.extensionContainer = value;
        }
        
  
        
        public LSAIdentity getSelectedLSA_Id () {
            return this.selectedLSA_Id;
        }

        
        public boolean isSelectedLSA_IdPresent () {
            return this.selectedLSA_Id != null;
        }
        

        public void setSelectedLSA_Id (LSAIdentity value) {
            this.selectedLSA_Id = value;
        }
        
  
        
        public ISDN_AddressString getMsc_Number () {
            return this.msc_Number;
        }

        
        public boolean isMsc_NumberPresent () {
            return this.msc_Number != null;
        }
        

        public void setMsc_Number (ISDN_AddressString value) {
            this.msc_Number = value;
        }
        
  
        
        public GeodeticInformation getGeodeticInformation () {
            return this.geodeticInformation;
        }

        
        public boolean isGeodeticInformationPresent () {
            return this.geodeticInformation != null;
        }
        

        public void setGeodeticInformation (GeodeticInformation value) {
            this.geodeticInformation = value;
        }
        
  
                    
        
        public void initWithDefaults() {
            
        }

        private static IASN1PreparedElementData preparedData = CoderFactory.getInstance().newPreparedElementData(LocationInformation.class);
        public IASN1PreparedElementData getPreparedData() {
            return preparedData;
        }

            
    }
            