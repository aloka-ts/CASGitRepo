
package com.agnity.win.asngenerated;
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
    @ASN1Sequence ( name = "TargetCellIDList", isSet = false )
    public class TargetCellIDList implements IASN1PreparedElement {
            
        @ASN1Element ( name = "targetCellID", isOptional =  false , hasTag =  true, tag = 3 , hasDefaultValue =  false  )
    
	private TargetCellID targetCellID = null;
                
  
        @ASN1Element ( name = "targetCellID1", isOptional =  true , hasTag =  true, tag = 3 , hasDefaultValue =  false  )
    
	private TargetCellID targetCellID1 = null;
                
  
        
        public TargetCellID getTargetCellID () {
            return this.targetCellID;
        }

        

        public void setTargetCellID (TargetCellID value) {
            this.targetCellID = value;
        }
        
  
        
        public TargetCellID getTargetCellID1 () {
            return this.targetCellID1;
        }

        
        public boolean isTargetCellID1Present () {
            return this.targetCellID1 != null;
        }
        

        public void setTargetCellID1 (TargetCellID value) {
            this.targetCellID1 = value;
        }
        
  
                    
        
        public void initWithDefaults() {
            
        }

        private static IASN1PreparedElementData preparedData = CoderFactory.getInstance().newPreparedElementData(TargetCellIDList.class);
        public IASN1PreparedElementData getPreparedData() {
            return preparedData;
        }

            
    }
            