
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
    @ASN1Sequence ( name = "ServiceDataResult", isSet = false )
    public class ServiceDataResult implements IASN1PreparedElement {
            
        @ASN1Element ( name = "dataUpdateResultList", isOptional =  false , hasTag =  true, tag = 255 , hasDefaultValue =  false  )
    
	private DataUpdateResultList dataUpdateResultList = null;
                
  
        @ASN1Element ( name = "serviceID", isOptional =  true , hasTag =  true, tag = 246 , hasDefaultValue =  false  )
    
	private ServiceID serviceID = null;
                
  
        
        public DataUpdateResultList getDataUpdateResultList () {
            return this.dataUpdateResultList;
        }

        

        public void setDataUpdateResultList (DataUpdateResultList value) {
            this.dataUpdateResultList = value;
        }
        
  
        
        public ServiceID getServiceID () {
            return this.serviceID;
        }

        
        public boolean isServiceIDPresent () {
            return this.serviceID != null;
        }
        

        public void setServiceID (ServiceID value) {
            this.serviceID = value;
        }
        
  
                    
        
        public void initWithDefaults() {
            
        }

        private static IASN1PreparedElementData preparedData = CoderFactory.getInstance().newPreparedElementData(ServiceDataResult.class);
        public IASN1PreparedElementData getPreparedData() {
            return preparedData;
        }

            
    }
            