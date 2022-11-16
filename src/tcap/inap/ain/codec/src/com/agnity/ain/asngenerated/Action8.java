
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
    @ASN1Choice ( name = "Action8" )
    public class Action8 implements IASN1PreparedElement {
            
        @ASN1Element ( name = "invoke", isOptional =  false , hasTag =  true, tag = 2 , hasDefaultValue =  false  )
    
	private Invoke invoke = null;
                
  
        
        public Invoke getInvoke () {
            return this.invoke;
        }

        public boolean isInvokeSelected () {
            return this.invoke != null;
        }

        private void setInvoke (Invoke value) {
            this.invoke = value;
        }

        
        public void selectInvoke (Invoke value) {
            this.invoke = value;
                        
        }

        
  

	    public void initWithDefaults() {
	    }

        private static IASN1PreparedElementData preparedData = CoderFactory.getInstance().newPreparedElementData(Action8.class);
        public IASN1PreparedElementData getPreparedData() {
            return preparedData;
        }


    }
            