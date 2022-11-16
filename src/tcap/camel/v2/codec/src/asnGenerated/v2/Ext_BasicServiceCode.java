
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
    @ASN1Choice ( name = "Ext_BasicServiceCode" )
    public class Ext_BasicServiceCode implements IASN1PreparedElement {
            
        @ASN1Element ( name = "ext-BearerService", isOptional =  false , hasTag =  true, tag = 2 , hasDefaultValue =  false  )
    
	private Ext_BearerServiceCode ext_BearerService = null;
                
  
        @ASN1Element ( name = "ext-Teleservice", isOptional =  false , hasTag =  true, tag = 3 , hasDefaultValue =  false  )
    
	private Ext_TeleserviceCode ext_Teleservice = null;
                
  
        
        public Ext_BearerServiceCode getExt_BearerService () {
            return this.ext_BearerService;
        }

        public boolean isExt_BearerServiceSelected () {
            return this.ext_BearerService != null;
        }

        private void setExt_BearerService (Ext_BearerServiceCode value) {
            this.ext_BearerService = value;
        }

        
        public void selectExt_BearerService (Ext_BearerServiceCode value) {
            this.ext_BearerService = value;
            
                    setExt_Teleservice(null);
                            
        }

        
  
        
        public Ext_TeleserviceCode getExt_Teleservice () {
            return this.ext_Teleservice;
        }

        public boolean isExt_TeleserviceSelected () {
            return this.ext_Teleservice != null;
        }

        private void setExt_Teleservice (Ext_TeleserviceCode value) {
            this.ext_Teleservice = value;
        }

        
        public void selectExt_Teleservice (Ext_TeleserviceCode value) {
            this.ext_Teleservice = value;
            
                    setExt_BearerService(null);
                            
        }

        
  

	    public void initWithDefaults() {
	    }

        private static IASN1PreparedElementData preparedData = CoderFactory.getInstance().newPreparedElementData(Ext_BasicServiceCode.class);
        public IASN1PreparedElementData getPreparedData() {
            return preparedData;
        }


    }
            