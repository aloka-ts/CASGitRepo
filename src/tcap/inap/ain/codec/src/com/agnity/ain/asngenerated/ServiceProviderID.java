
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
    @ASN1Choice ( name = "ServiceProviderID" )
    public class ServiceProviderID implements IASN1PreparedElement {
            
        @ASN1Element ( name = "ocn", isOptional =  false , hasTag =  false  , hasDefaultValue =  false  )
    
	private Ocn ocn = null;
                
  
        @ASN1Element ( name = "msrID", isOptional =  false , hasTag =  false  , hasDefaultValue =  false  )
    
	private MsrID msrID = null;
                
  
        
        public Ocn getOcn () {
            return this.ocn;
        }

        public boolean isOcnSelected () {
            return this.ocn != null;
        }

        private void setOcn (Ocn value) {
            this.ocn = value;
        }

        
        public void selectOcn (Ocn value) {
            this.ocn = value;
            
                    setMsrID(null);
                            
        }

        
  
        
        public MsrID getMsrID () {
            return this.msrID;
        }

        public boolean isMsrIDSelected () {
            return this.msrID != null;
        }

        private void setMsrID (MsrID value) {
            this.msrID = value;
        }

        
        public void selectMsrID (MsrID value) {
            this.msrID = value;
            
                    setOcn(null);
                            
        }

        
  

	    public void initWithDefaults() {
	    }

        private static IASN1PreparedElementData preparedData = CoderFactory.getInstance().newPreparedElementData(ServiceProviderID.class);
        public IASN1PreparedElementData getPreparedData() {
            return preparedData;
        }


    }
            