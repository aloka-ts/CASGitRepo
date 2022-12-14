
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
    @ASN1Choice ( name = "GapCriteria" )
    public class GapCriteria implements IASN1PreparedElement {
            
        @ASN1Element ( name = "basicGapCriteria", isOptional =  false , hasTag =  false  , hasDefaultValue =  false  )
    
	private BasicGapCriteria basicGapCriteria = null;
                
  
        @ASN1Element ( name = "compoundGapCriteria", isOptional =  false , hasTag =  false  , hasDefaultValue =  false  )
    
	private CompoundCriteria compoundGapCriteria = null;
                
  
        
        public BasicGapCriteria getBasicGapCriteria () {
            return this.basicGapCriteria;
        }

        public boolean isBasicGapCriteriaSelected () {
            return this.basicGapCriteria != null;
        }

        private void setBasicGapCriteria (BasicGapCriteria value) {
            this.basicGapCriteria = value;
        }

        
        public void selectBasicGapCriteria (BasicGapCriteria value) {
            this.basicGapCriteria = value;
            
                    setCompoundGapCriteria(null);
                            
        }

        
  
        
        public CompoundCriteria getCompoundGapCriteria () {
            return this.compoundGapCriteria;
        }

        public boolean isCompoundGapCriteriaSelected () {
            return this.compoundGapCriteria != null;
        }

        private void setCompoundGapCriteria (CompoundCriteria value) {
            this.compoundGapCriteria = value;
        }

        
        public void selectCompoundGapCriteria (CompoundCriteria value) {
            this.compoundGapCriteria = value;
            
                    setBasicGapCriteria(null);
                            
        }

        
  

	    public void initWithDefaults() {
	    }

        private static IASN1PreparedElementData preparedData = CoderFactory.getInstance().newPreparedElementData(GapCriteria.class);
        public IASN1PreparedElementData getPreparedData() {
            return preparedData;
        }


    }
            