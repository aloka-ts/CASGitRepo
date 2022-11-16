
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
    @ASN1Choice ( name = "SSPUserResourceID" )
    public class SSPUserResourceID implements IASN1PreparedElement {
            
        @ASN1Element ( name = "dn", isOptional =  false , hasTag =  true, tag = 1 , hasDefaultValue =  false  )
    
	private Dn dn = null;
                
  
        @ASN1Element ( name = "dnCtID", isOptional =  false , hasTag =  true, tag = 2 , hasDefaultValue =  false  )
    
	private DnCtID dnCtID = null;
                
  
        @ASN1Element ( name = "spid", isOptional =  false , hasTag =  true, tag = 3 , hasDefaultValue =  false  )
    
	private Spid spid = null;
                
  
        @ASN1Element ( name = "trunkGroupID", isOptional =  false , hasTag =  true, tag = 4 , hasDefaultValue =  false  )
    
	private TrunkGroupID trunkGroupID = null;
                
  
        @ASN1Element ( name = "localSSPID", isOptional =  false , hasTag =  true, tag = 5 , hasDefaultValue =  false  )
    
	private LocalSSPID localSSPID = null;
                
  
        @ASN1Element ( name = "publicDialingPl", isOptional =  false , hasTag =  true, tag = 6 , hasDefaultValue =  false  )
    
	private PublicDialingPlanID publicDialingPl = null;
                
  
        @ASN1Element ( name = "pRIOfficeEquipmentID", isOptional =  false , hasTag =  true, tag = 7 , hasDefaultValue =  false  )
    
	private PRIOfficeEquipmentID pRIOfficeEquipmentID = null;
                
  
        @ASN1Element ( name = "basicBusinessGroupID", isOptional =  false , hasTag =  true, tag = 8 , hasDefaultValue =  false  )
    
	private BasicBusinessGroupID basicBusinessGroupID = null;
                
  
        @ASN1Element ( name = "basicBusinessGroupDialingPlanID", isOptional =  false , hasTag =  true, tag = 9 , hasDefaultValue =  false  )
    
	private BasicBusinessGroupDialingPlanID basicBusinessGroupDialingPlanID = null;
                
  
        @ASN1Element ( name = "aFRPatternID", isOptional =  false , hasTag =  true, tag = 10 , hasDefaultValue =  false  )
    
	private AFRPatternID aFRPatternID = null;
                
  
        @ASN1Element ( name = "officeEquipmentID", isOptional =  false , hasTag =  true, tag = 11 , hasDefaultValue =  false  )
    
	private OfficeEquipmentID officeEquipmentID = null;
                
  
        
        public Dn getDn () {
            return this.dn;
        }

        public boolean isDnSelected () {
            return this.dn != null;
        }

        private void setDn (Dn value) {
            this.dn = value;
        }

        
        public void selectDn (Dn value) {
            this.dn = value;
            
                    setDnCtID(null);
                
                    setSpid(null);
                
                    setTrunkGroupID(null);
                
                    setLocalSSPID(null);
                
                    setPublicDialingPl(null);
                
                    setPRIOfficeEquipmentID(null);
                
                    setBasicBusinessGroupID(null);
                
                    setBasicBusinessGroupDialingPlanID(null);
                
                    setAFRPatternID(null);
                
                    setOfficeEquipmentID(null);
                            
        }

        
  
        
        public DnCtID getDnCtID () {
            return this.dnCtID;
        }

        public boolean isDnCtIDSelected () {
            return this.dnCtID != null;
        }

        private void setDnCtID (DnCtID value) {
            this.dnCtID = value;
        }

        
        public void selectDnCtID (DnCtID value) {
            this.dnCtID = value;
            
                    setDn(null);
                
                    setSpid(null);
                
                    setTrunkGroupID(null);
                
                    setLocalSSPID(null);
                
                    setPublicDialingPl(null);
                
                    setPRIOfficeEquipmentID(null);
                
                    setBasicBusinessGroupID(null);
                
                    setBasicBusinessGroupDialingPlanID(null);
                
                    setAFRPatternID(null);
                
                    setOfficeEquipmentID(null);
                            
        }

        
  
        
        public Spid getSpid () {
            return this.spid;
        }

        public boolean isSpidSelected () {
            return this.spid != null;
        }

        private void setSpid (Spid value) {
            this.spid = value;
        }

        
        public void selectSpid (Spid value) {
            this.spid = value;
            
                    setDn(null);
                
                    setDnCtID(null);
                
                    setTrunkGroupID(null);
                
                    setLocalSSPID(null);
                
                    setPublicDialingPl(null);
                
                    setPRIOfficeEquipmentID(null);
                
                    setBasicBusinessGroupID(null);
                
                    setBasicBusinessGroupDialingPlanID(null);
                
                    setAFRPatternID(null);
                
                    setOfficeEquipmentID(null);
                            
        }

        
  
        
        public TrunkGroupID getTrunkGroupID () {
            return this.trunkGroupID;
        }

        public boolean isTrunkGroupIDSelected () {
            return this.trunkGroupID != null;
        }

        private void setTrunkGroupID (TrunkGroupID value) {
            this.trunkGroupID = value;
        }

        
        public void selectTrunkGroupID (TrunkGroupID value) {
            this.trunkGroupID = value;
            
                    setDn(null);
                
                    setDnCtID(null);
                
                    setSpid(null);
                
                    setLocalSSPID(null);
                
                    setPublicDialingPl(null);
                
                    setPRIOfficeEquipmentID(null);
                
                    setBasicBusinessGroupID(null);
                
                    setBasicBusinessGroupDialingPlanID(null);
                
                    setAFRPatternID(null);
                
                    setOfficeEquipmentID(null);
                            
        }

        
  
        
        public LocalSSPID getLocalSSPID () {
            return this.localSSPID;
        }

        public boolean isLocalSSPIDSelected () {
            return this.localSSPID != null;
        }

        private void setLocalSSPID (LocalSSPID value) {
            this.localSSPID = value;
        }

        
        public void selectLocalSSPID (LocalSSPID value) {
            this.localSSPID = value;
            
                    setDn(null);
                
                    setDnCtID(null);
                
                    setSpid(null);
                
                    setTrunkGroupID(null);
                
                    setPublicDialingPl(null);
                
                    setPRIOfficeEquipmentID(null);
                
                    setBasicBusinessGroupID(null);
                
                    setBasicBusinessGroupDialingPlanID(null);
                
                    setAFRPatternID(null);
                
                    setOfficeEquipmentID(null);
                            
        }

        
  
        
        public PublicDialingPlanID getPublicDialingPl () {
            return this.publicDialingPl;
        }

        public boolean isPublicDialingPlSelected () {
            return this.publicDialingPl != null;
        }

        private void setPublicDialingPl (PublicDialingPlanID value) {
            this.publicDialingPl = value;
        }

        
        public void selectPublicDialingPl (PublicDialingPlanID value) {
            this.publicDialingPl = value;
            
                    setDn(null);
                
                    setDnCtID(null);
                
                    setSpid(null);
                
                    setTrunkGroupID(null);
                
                    setLocalSSPID(null);
                
                    setPRIOfficeEquipmentID(null);
                
                    setBasicBusinessGroupID(null);
                
                    setBasicBusinessGroupDialingPlanID(null);
                
                    setAFRPatternID(null);
                
                    setOfficeEquipmentID(null);
                            
        }

        
  
        
        public PRIOfficeEquipmentID getPRIOfficeEquipmentID () {
            return this.pRIOfficeEquipmentID;
        }

        public boolean isPRIOfficeEquipmentIDSelected () {
            return this.pRIOfficeEquipmentID != null;
        }

        private void setPRIOfficeEquipmentID (PRIOfficeEquipmentID value) {
            this.pRIOfficeEquipmentID = value;
        }

        
        public void selectPRIOfficeEquipmentID (PRIOfficeEquipmentID value) {
            this.pRIOfficeEquipmentID = value;
            
                    setDn(null);
                
                    setDnCtID(null);
                
                    setSpid(null);
                
                    setTrunkGroupID(null);
                
                    setLocalSSPID(null);
                
                    setPublicDialingPl(null);
                
                    setBasicBusinessGroupID(null);
                
                    setBasicBusinessGroupDialingPlanID(null);
                
                    setAFRPatternID(null);
                
                    setOfficeEquipmentID(null);
                            
        }

        
  
        
        public BasicBusinessGroupID getBasicBusinessGroupID () {
            return this.basicBusinessGroupID;
        }

        public boolean isBasicBusinessGroupIDSelected () {
            return this.basicBusinessGroupID != null;
        }

        private void setBasicBusinessGroupID (BasicBusinessGroupID value) {
            this.basicBusinessGroupID = value;
        }

        
        public void selectBasicBusinessGroupID (BasicBusinessGroupID value) {
            this.basicBusinessGroupID = value;
            
                    setDn(null);
                
                    setDnCtID(null);
                
                    setSpid(null);
                
                    setTrunkGroupID(null);
                
                    setLocalSSPID(null);
                
                    setPublicDialingPl(null);
                
                    setPRIOfficeEquipmentID(null);
                
                    setBasicBusinessGroupDialingPlanID(null);
                
                    setAFRPatternID(null);
                
                    setOfficeEquipmentID(null);
                            
        }

        
  
        
        public BasicBusinessGroupDialingPlanID getBasicBusinessGroupDialingPlanID () {
            return this.basicBusinessGroupDialingPlanID;
        }

        public boolean isBasicBusinessGroupDialingPlanIDSelected () {
            return this.basicBusinessGroupDialingPlanID != null;
        }

        private void setBasicBusinessGroupDialingPlanID (BasicBusinessGroupDialingPlanID value) {
            this.basicBusinessGroupDialingPlanID = value;
        }

        
        public void selectBasicBusinessGroupDialingPlanID (BasicBusinessGroupDialingPlanID value) {
            this.basicBusinessGroupDialingPlanID = value;
            
                    setDn(null);
                
                    setDnCtID(null);
                
                    setSpid(null);
                
                    setTrunkGroupID(null);
                
                    setLocalSSPID(null);
                
                    setPublicDialingPl(null);
                
                    setPRIOfficeEquipmentID(null);
                
                    setBasicBusinessGroupID(null);
                
                    setAFRPatternID(null);
                
                    setOfficeEquipmentID(null);
                            
        }

        
  
        
        public AFRPatternID getAFRPatternID () {
            return this.aFRPatternID;
        }

        public boolean isAFRPatternIDSelected () {
            return this.aFRPatternID != null;
        }

        private void setAFRPatternID (AFRPatternID value) {
            this.aFRPatternID = value;
        }

        
        public void selectAFRPatternID (AFRPatternID value) {
            this.aFRPatternID = value;
            
                    setDn(null);
                
                    setDnCtID(null);
                
                    setSpid(null);
                
                    setTrunkGroupID(null);
                
                    setLocalSSPID(null);
                
                    setPublicDialingPl(null);
                
                    setPRIOfficeEquipmentID(null);
                
                    setBasicBusinessGroupID(null);
                
                    setBasicBusinessGroupDialingPlanID(null);
                
                    setOfficeEquipmentID(null);
                            
        }

        
  
        
        public OfficeEquipmentID getOfficeEquipmentID () {
            return this.officeEquipmentID;
        }

        public boolean isOfficeEquipmentIDSelected () {
            return this.officeEquipmentID != null;
        }

        private void setOfficeEquipmentID (OfficeEquipmentID value) {
            this.officeEquipmentID = value;
        }

        
        public void selectOfficeEquipmentID (OfficeEquipmentID value) {
            this.officeEquipmentID = value;
            
                    setDn(null);
                
                    setDnCtID(null);
                
                    setSpid(null);
                
                    setTrunkGroupID(null);
                
                    setLocalSSPID(null);
                
                    setPublicDialingPl(null);
                
                    setPRIOfficeEquipmentID(null);
                
                    setBasicBusinessGroupID(null);
                
                    setBasicBusinessGroupDialingPlanID(null);
                
                    setAFRPatternID(null);
                            
        }

        
  

	    public void initWithDefaults() {
	    }

        private static IASN1PreparedElementData preparedData = CoderFactory.getInstance().newPreparedElementData(SSPUserResourceID.class);
        public IASN1PreparedElementData getPreparedData() {
            return preparedData;
        }


    }
            