
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
    @ASN1Choice ( name = "Action7" )
    public class Action7 implements IASN1PreparedElement {
            
        @ASN1Element ( name = "toggle", isOptional =  false , hasTag =  true, tag = 2 , hasDefaultValue =  false  )
    
	private Toggle toggle = null;
                
  
        
        public Toggle getToggle () {
            return this.toggle;
        }

        public boolean isToggleSelected () {
            return this.toggle != null;
        }

        private void setToggle (Toggle value) {
            this.toggle = value;
        }

        
        public void selectToggle (Toggle value) {
            this.toggle = value;
                        
        }

        
  

	    public void initWithDefaults() {
	    }

        private static IASN1PreparedElementData preparedData = CoderFactory.getInstance().newPreparedElementData(Action7.class);
        public IASN1PreparedElementData getPreparedData() {
            return preparedData;
        }


    }
            