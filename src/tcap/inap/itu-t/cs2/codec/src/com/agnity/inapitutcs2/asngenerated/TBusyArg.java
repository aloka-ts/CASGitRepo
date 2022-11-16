
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
    @ASN1Sequence ( name = "TBusyArg", isSet = false )
    public class TBusyArg implements IASN1PreparedElement {
            
        @ASN1Element ( name = "dpSpecificCommonParameters", isOptional =  false , hasTag =  true, tag = 0 , hasDefaultValue =  false  )
    
	private DpSpecificCommonParameters dpSpecificCommonParameters = null;
                
  
        @ASN1Element ( name = "busyCause", isOptional =  true , hasTag =  true, tag = 1 , hasDefaultValue =  false  )
    
	private Cause busyCause = null;
                
  
        @ASN1Element ( name = "calledPartyBusinessGroupID", isOptional =  true , hasTag =  true, tag = 2 , hasDefaultValue =  false  )
    
	private CalledPartyBusinessGroupID calledPartyBusinessGroupID = null;
                
  
        @ASN1Element ( name = "calledPartySubaddress", isOptional =  true , hasTag =  true, tag = 3 , hasDefaultValue =  false  )
    
	private CalledPartySubaddress calledPartySubaddress = null;
                
  
        @ASN1Element ( name = "originalCalledPartyID", isOptional =  true , hasTag =  true, tag = 4 , hasDefaultValue =  false  )
    
	private OriginalCalledPartyID originalCalledPartyID = null;
                
  
        @ASN1Element ( name = "redirectingPartyID", isOptional =  true , hasTag =  true, tag = 5 , hasDefaultValue =  false  )
    
	private RedirectingPartyID redirectingPartyID = null;
                
  
        @ASN1Element ( name = "redirectionInformation", isOptional =  true , hasTag =  true, tag = 6 , hasDefaultValue =  false  )
    
	private RedirectionInformation redirectionInformation = null;
                
  
        @ASN1Element ( name = "routeList", isOptional =  true , hasTag =  true, tag = 7 , hasDefaultValue =  false  )
    
	private RouteList routeList = null;
                
  
        @ASN1Element ( name = "travellingClassMark", isOptional =  true , hasTag =  true, tag = 8 , hasDefaultValue =  false  )
    
	private TravellingClassMark travellingClassMark = null;
                
  
@ASN1SequenceOf( name = "extensions", isSetOf = false ) 

    @ASN1ValueRangeConstraint ( 
		
		min = 1L, 
		
		max = 5L 
		
	   )
	   
        @ASN1Element ( name = "extensions", isOptional =  true , hasTag =  true, tag = 9 , hasDefaultValue =  false  )
    
	private java.util.Collection<ExtensionField>  extensions = null;
                
  
        
        public DpSpecificCommonParameters getDpSpecificCommonParameters () {
            return this.dpSpecificCommonParameters;
        }

        

        public void setDpSpecificCommonParameters (DpSpecificCommonParameters value) {
            this.dpSpecificCommonParameters = value;
        }
        
  
        
        public Cause getBusyCause () {
            return this.busyCause;
        }

        
        public boolean isBusyCausePresent () {
            return this.busyCause != null;
        }
        

        public void setBusyCause (Cause value) {
            this.busyCause = value;
        }
        
  
        
        public CalledPartyBusinessGroupID getCalledPartyBusinessGroupID () {
            return this.calledPartyBusinessGroupID;
        }

        
        public boolean isCalledPartyBusinessGroupIDPresent () {
            return this.calledPartyBusinessGroupID != null;
        }
        

        public void setCalledPartyBusinessGroupID (CalledPartyBusinessGroupID value) {
            this.calledPartyBusinessGroupID = value;
        }
        
  
        
        public CalledPartySubaddress getCalledPartySubaddress () {
            return this.calledPartySubaddress;
        }

        
        public boolean isCalledPartySubaddressPresent () {
            return this.calledPartySubaddress != null;
        }
        

        public void setCalledPartySubaddress (CalledPartySubaddress value) {
            this.calledPartySubaddress = value;
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
        
  
        
        public RouteList getRouteList () {
            return this.routeList;
        }

        
        public boolean isRouteListPresent () {
            return this.routeList != null;
        }
        

        public void setRouteList (RouteList value) {
            this.routeList = value;
        }
        
  
        
        public TravellingClassMark getTravellingClassMark () {
            return this.travellingClassMark;
        }

        
        public boolean isTravellingClassMarkPresent () {
            return this.travellingClassMark != null;
        }
        

        public void setTravellingClassMark (TravellingClassMark value) {
            this.travellingClassMark = value;
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
            
        }

        private static IASN1PreparedElementData preparedData = CoderFactory.getInstance().newPreparedElementData(TBusyArg.class);
        public IASN1PreparedElementData getPreparedData() {
            return preparedData;
        }

            
    }
            