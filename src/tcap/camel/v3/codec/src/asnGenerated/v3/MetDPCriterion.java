
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
    @ASN1Choice ( name = "MetDPCriterion" )
    public class MetDPCriterion implements IASN1PreparedElement {
            
        @ASN1Element ( name = "enteringCellGlobalId", isOptional =  false , hasTag =  true, tag = 0 , hasDefaultValue =  false  )
    
	private CellGlobalIdOrServiceAreaIdFixedLength enteringCellGlobalId = null;
                
  
        @ASN1Element ( name = "leavingCellGlobalId", isOptional =  false , hasTag =  true, tag = 1 , hasDefaultValue =  false  )
    
	private CellGlobalIdOrServiceAreaIdFixedLength leavingCellGlobalId = null;
                
  
        @ASN1Element ( name = "enteringServiceAreaId", isOptional =  false , hasTag =  true, tag = 2 , hasDefaultValue =  false  )
    
	private CellGlobalIdOrServiceAreaIdFixedLength enteringServiceAreaId = null;
                
  
        @ASN1Element ( name = "leavingServiceAreaId", isOptional =  false , hasTag =  true, tag = 3 , hasDefaultValue =  false  )
    
	private CellGlobalIdOrServiceAreaIdFixedLength leavingServiceAreaId = null;
                
  
        @ASN1Element ( name = "enteringLocationAreaId", isOptional =  false , hasTag =  true, tag = 4 , hasDefaultValue =  false  )
    
	private LAIFixedLength enteringLocationAreaId = null;
                
  
        @ASN1Element ( name = "leavingLocationAreaId", isOptional =  false , hasTag =  true, tag = 5 , hasDefaultValue =  false  )
    
	private LAIFixedLength leavingLocationAreaId = null;
                
  
        @ASN1Null ( name = "inter-SystemHandOverToUMTS" ) 
    
        @ASN1Element ( name = "inter-SystemHandOverToUMTS", isOptional =  false , hasTag =  true, tag = 6 , hasDefaultValue =  false  )
    
	private org.bn.types.NullObject inter_SystemHandOverToUMTS = null;
                
  
        @ASN1Null ( name = "inter-SystemHandOverToGSM" ) 
    
        @ASN1Element ( name = "inter-SystemHandOverToGSM", isOptional =  false , hasTag =  true, tag = 7 , hasDefaultValue =  false  )
    
	private org.bn.types.NullObject inter_SystemHandOverToGSM = null;
                
  
        @ASN1Null ( name = "inter-PLMNHandOver" ) 
    
        @ASN1Element ( name = "inter-PLMNHandOver", isOptional =  false , hasTag =  true, tag = 8 , hasDefaultValue =  false  )
    
	private org.bn.types.NullObject inter_PLMNHandOver = null;
                
  
        @ASN1Null ( name = "inter-MSCHandOver" ) 
    
        @ASN1Element ( name = "inter-MSCHandOver", isOptional =  false , hasTag =  true, tag = 9 , hasDefaultValue =  false  )
    
	private org.bn.types.NullObject inter_MSCHandOver = null;
                
  
        @ASN1Element ( name = "metDPCriterionAlt", isOptional =  false , hasTag =  true, tag = 10 , hasDefaultValue =  false  )
    
	private MetDPCriterionAlt metDPCriterionAlt = null;
                
  
        
        public CellGlobalIdOrServiceAreaIdFixedLength getEnteringCellGlobalId () {
            return this.enteringCellGlobalId;
        }

        public boolean isEnteringCellGlobalIdSelected () {
            return this.enteringCellGlobalId != null;
        }

        private void setEnteringCellGlobalId (CellGlobalIdOrServiceAreaIdFixedLength value) {
            this.enteringCellGlobalId = value;
        }

        
        public void selectEnteringCellGlobalId (CellGlobalIdOrServiceAreaIdFixedLength value) {
            this.enteringCellGlobalId = value;
            
                    setLeavingCellGlobalId(null);
                
                    setEnteringServiceAreaId(null);
                
                    setLeavingServiceAreaId(null);
                
                    setEnteringLocationAreaId(null);
                
                    setLeavingLocationAreaId(null);
                
                    setInter_SystemHandOverToUMTS(null);
                
                    setInter_SystemHandOverToGSM(null);
                
                    setInter_PLMNHandOver(null);
                
                    setInter_MSCHandOver(null);
                
                    setMetDPCriterionAlt(null);
                            
        }

        
  
        
        public CellGlobalIdOrServiceAreaIdFixedLength getLeavingCellGlobalId () {
            return this.leavingCellGlobalId;
        }

        public boolean isLeavingCellGlobalIdSelected () {
            return this.leavingCellGlobalId != null;
        }

        private void setLeavingCellGlobalId (CellGlobalIdOrServiceAreaIdFixedLength value) {
            this.leavingCellGlobalId = value;
        }

        
        public void selectLeavingCellGlobalId (CellGlobalIdOrServiceAreaIdFixedLength value) {
            this.leavingCellGlobalId = value;
            
                    setEnteringCellGlobalId(null);
                
                    setEnteringServiceAreaId(null);
                
                    setLeavingServiceAreaId(null);
                
                    setEnteringLocationAreaId(null);
                
                    setLeavingLocationAreaId(null);
                
                    setInter_SystemHandOverToUMTS(null);
                
                    setInter_SystemHandOverToGSM(null);
                
                    setInter_PLMNHandOver(null);
                
                    setInter_MSCHandOver(null);
                
                    setMetDPCriterionAlt(null);
                            
        }

        
  
        
        public CellGlobalIdOrServiceAreaIdFixedLength getEnteringServiceAreaId () {
            return this.enteringServiceAreaId;
        }

        public boolean isEnteringServiceAreaIdSelected () {
            return this.enteringServiceAreaId != null;
        }

        private void setEnteringServiceAreaId (CellGlobalIdOrServiceAreaIdFixedLength value) {
            this.enteringServiceAreaId = value;
        }

        
        public void selectEnteringServiceAreaId (CellGlobalIdOrServiceAreaIdFixedLength value) {
            this.enteringServiceAreaId = value;
            
                    setEnteringCellGlobalId(null);
                
                    setLeavingCellGlobalId(null);
                
                    setLeavingServiceAreaId(null);
                
                    setEnteringLocationAreaId(null);
                
                    setLeavingLocationAreaId(null);
                
                    setInter_SystemHandOverToUMTS(null);
                
                    setInter_SystemHandOverToGSM(null);
                
                    setInter_PLMNHandOver(null);
                
                    setInter_MSCHandOver(null);
                
                    setMetDPCriterionAlt(null);
                            
        }

        
  
        
        public CellGlobalIdOrServiceAreaIdFixedLength getLeavingServiceAreaId () {
            return this.leavingServiceAreaId;
        }

        public boolean isLeavingServiceAreaIdSelected () {
            return this.leavingServiceAreaId != null;
        }

        private void setLeavingServiceAreaId (CellGlobalIdOrServiceAreaIdFixedLength value) {
            this.leavingServiceAreaId = value;
        }

        
        public void selectLeavingServiceAreaId (CellGlobalIdOrServiceAreaIdFixedLength value) {
            this.leavingServiceAreaId = value;
            
                    setEnteringCellGlobalId(null);
                
                    setLeavingCellGlobalId(null);
                
                    setEnteringServiceAreaId(null);
                
                    setEnteringLocationAreaId(null);
                
                    setLeavingLocationAreaId(null);
                
                    setInter_SystemHandOverToUMTS(null);
                
                    setInter_SystemHandOverToGSM(null);
                
                    setInter_PLMNHandOver(null);
                
                    setInter_MSCHandOver(null);
                
                    setMetDPCriterionAlt(null);
                            
        }

        
  
        
        public LAIFixedLength getEnteringLocationAreaId () {
            return this.enteringLocationAreaId;
        }

        public boolean isEnteringLocationAreaIdSelected () {
            return this.enteringLocationAreaId != null;
        }

        private void setEnteringLocationAreaId (LAIFixedLength value) {
            this.enteringLocationAreaId = value;
        }

        
        public void selectEnteringLocationAreaId (LAIFixedLength value) {
            this.enteringLocationAreaId = value;
            
                    setEnteringCellGlobalId(null);
                
                    setLeavingCellGlobalId(null);
                
                    setEnteringServiceAreaId(null);
                
                    setLeavingServiceAreaId(null);
                
                    setLeavingLocationAreaId(null);
                
                    setInter_SystemHandOverToUMTS(null);
                
                    setInter_SystemHandOverToGSM(null);
                
                    setInter_PLMNHandOver(null);
                
                    setInter_MSCHandOver(null);
                
                    setMetDPCriterionAlt(null);
                            
        }

        
  
        
        public LAIFixedLength getLeavingLocationAreaId () {
            return this.leavingLocationAreaId;
        }

        public boolean isLeavingLocationAreaIdSelected () {
            return this.leavingLocationAreaId != null;
        }

        private void setLeavingLocationAreaId (LAIFixedLength value) {
            this.leavingLocationAreaId = value;
        }

        
        public void selectLeavingLocationAreaId (LAIFixedLength value) {
            this.leavingLocationAreaId = value;
            
                    setEnteringCellGlobalId(null);
                
                    setLeavingCellGlobalId(null);
                
                    setEnteringServiceAreaId(null);
                
                    setLeavingServiceAreaId(null);
                
                    setEnteringLocationAreaId(null);
                
                    setInter_SystemHandOverToUMTS(null);
                
                    setInter_SystemHandOverToGSM(null);
                
                    setInter_PLMNHandOver(null);
                
                    setInter_MSCHandOver(null);
                
                    setMetDPCriterionAlt(null);
                            
        }

        
  
        
        public org.bn.types.NullObject getInter_SystemHandOverToUMTS () {
            return this.inter_SystemHandOverToUMTS;
        }

        public boolean isInter_SystemHandOverToUMTSSelected () {
            return this.inter_SystemHandOverToUMTS != null;
        }

        private void setInter_SystemHandOverToUMTS (org.bn.types.NullObject value) {
            this.inter_SystemHandOverToUMTS = value;
        }

        
        public void selectInter_SystemHandOverToUMTS () {
            selectInter_SystemHandOverToUMTS (new org.bn.types.NullObject());
	}
	
        public void selectInter_SystemHandOverToUMTS (org.bn.types.NullObject value) {
            this.inter_SystemHandOverToUMTS = value;
            
                    setEnteringCellGlobalId(null);
                
                    setLeavingCellGlobalId(null);
                
                    setEnteringServiceAreaId(null);
                
                    setLeavingServiceAreaId(null);
                
                    setEnteringLocationAreaId(null);
                
                    setLeavingLocationAreaId(null);
                
                    setInter_SystemHandOverToGSM(null);
                
                    setInter_PLMNHandOver(null);
                
                    setInter_MSCHandOver(null);
                
                    setMetDPCriterionAlt(null);
                            
        }

        
  
        
        public org.bn.types.NullObject getInter_SystemHandOverToGSM () {
            return this.inter_SystemHandOverToGSM;
        }

        public boolean isInter_SystemHandOverToGSMSelected () {
            return this.inter_SystemHandOverToGSM != null;
        }

        private void setInter_SystemHandOverToGSM (org.bn.types.NullObject value) {
            this.inter_SystemHandOverToGSM = value;
        }

        
        public void selectInter_SystemHandOverToGSM () {
            selectInter_SystemHandOverToGSM (new org.bn.types.NullObject());
	}
	
        public void selectInter_SystemHandOverToGSM (org.bn.types.NullObject value) {
            this.inter_SystemHandOverToGSM = value;
            
                    setEnteringCellGlobalId(null);
                
                    setLeavingCellGlobalId(null);
                
                    setEnteringServiceAreaId(null);
                
                    setLeavingServiceAreaId(null);
                
                    setEnteringLocationAreaId(null);
                
                    setLeavingLocationAreaId(null);
                
                    setInter_SystemHandOverToUMTS(null);
                
                    setInter_PLMNHandOver(null);
                
                    setInter_MSCHandOver(null);
                
                    setMetDPCriterionAlt(null);
                            
        }

        
  
        
        public org.bn.types.NullObject getInter_PLMNHandOver () {
            return this.inter_PLMNHandOver;
        }

        public boolean isInter_PLMNHandOverSelected () {
            return this.inter_PLMNHandOver != null;
        }

        private void setInter_PLMNHandOver (org.bn.types.NullObject value) {
            this.inter_PLMNHandOver = value;
        }

        
        public void selectInter_PLMNHandOver () {
            selectInter_PLMNHandOver (new org.bn.types.NullObject());
	}
	
        public void selectInter_PLMNHandOver (org.bn.types.NullObject value) {
            this.inter_PLMNHandOver = value;
            
                    setEnteringCellGlobalId(null);
                
                    setLeavingCellGlobalId(null);
                
                    setEnteringServiceAreaId(null);
                
                    setLeavingServiceAreaId(null);
                
                    setEnteringLocationAreaId(null);
                
                    setLeavingLocationAreaId(null);
                
                    setInter_SystemHandOverToUMTS(null);
                
                    setInter_SystemHandOverToGSM(null);
                
                    setInter_MSCHandOver(null);
                
                    setMetDPCriterionAlt(null);
                            
        }

        
  
        
        public org.bn.types.NullObject getInter_MSCHandOver () {
            return this.inter_MSCHandOver;
        }

        public boolean isInter_MSCHandOverSelected () {
            return this.inter_MSCHandOver != null;
        }

        private void setInter_MSCHandOver (org.bn.types.NullObject value) {
            this.inter_MSCHandOver = value;
        }

        
        public void selectInter_MSCHandOver () {
            selectInter_MSCHandOver (new org.bn.types.NullObject());
	}
	
        public void selectInter_MSCHandOver (org.bn.types.NullObject value) {
            this.inter_MSCHandOver = value;
            
                    setEnteringCellGlobalId(null);
                
                    setLeavingCellGlobalId(null);
                
                    setEnteringServiceAreaId(null);
                
                    setLeavingServiceAreaId(null);
                
                    setEnteringLocationAreaId(null);
                
                    setLeavingLocationAreaId(null);
                
                    setInter_SystemHandOverToUMTS(null);
                
                    setInter_SystemHandOverToGSM(null);
                
                    setInter_PLMNHandOver(null);
                
                    setMetDPCriterionAlt(null);
                            
        }

        
  
        
        public MetDPCriterionAlt getMetDPCriterionAlt () {
            return this.metDPCriterionAlt;
        }

        public boolean isMetDPCriterionAltSelected () {
            return this.metDPCriterionAlt != null;
        }

        private void setMetDPCriterionAlt (MetDPCriterionAlt value) {
            this.metDPCriterionAlt = value;
        }

        
        public void selectMetDPCriterionAlt (MetDPCriterionAlt value) {
            this.metDPCriterionAlt = value;
            
                    setEnteringCellGlobalId(null);
                
                    setLeavingCellGlobalId(null);
                
                    setEnteringServiceAreaId(null);
                
                    setLeavingServiceAreaId(null);
                
                    setEnteringLocationAreaId(null);
                
                    setLeavingLocationAreaId(null);
                
                    setInter_SystemHandOverToUMTS(null);
                
                    setInter_SystemHandOverToGSM(null);
                
                    setInter_PLMNHandOver(null);
                
                    setInter_MSCHandOver(null);
                            
        }

        
  

	    public void initWithDefaults() {
	    }

        private static IASN1PreparedElementData preparedData = CoderFactory.getInstance().newPreparedElementData(MetDPCriterion.class);
        public IASN1PreparedElementData getPreparedData() {
            return preparedData;
        }


    }
            