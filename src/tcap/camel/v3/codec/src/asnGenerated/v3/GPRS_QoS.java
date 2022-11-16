
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
    @ASN1Choice ( name = "GPRS_QoS" )
    public class GPRS_QoS implements IASN1PreparedElement {
            
        @ASN1Element ( name = "short-QoS-format", isOptional =  false , hasTag =  true, tag = 0 , hasDefaultValue =  false  )
    
	private QoS_Subscribed short_QoS_format = null;
                
  
        @ASN1Element ( name = "long-QoS-format", isOptional =  false , hasTag =  true, tag = 1 , hasDefaultValue =  false  )
    
	private Ext_QoS_Subscribed long_QoS_format = null;
                
  
        
        public QoS_Subscribed getShort_QoS_format () {
            return this.short_QoS_format;
        }

        public boolean isShort_QoS_formatSelected () {
            return this.short_QoS_format != null;
        }

        private void setShort_QoS_format (QoS_Subscribed value) {
            this.short_QoS_format = value;
        }

        
        public void selectShort_QoS_format (QoS_Subscribed value) {
            this.short_QoS_format = value;
            
                    setLong_QoS_format(null);
                            
        }

        
  
        
        public Ext_QoS_Subscribed getLong_QoS_format () {
            return this.long_QoS_format;
        }

        public boolean isLong_QoS_formatSelected () {
            return this.long_QoS_format != null;
        }

        private void setLong_QoS_format (Ext_QoS_Subscribed value) {
            this.long_QoS_format = value;
        }

        
        public void selectLong_QoS_format (Ext_QoS_Subscribed value) {
            this.long_QoS_format = value;
            
                    setShort_QoS_format(null);
                            
        }

        
  

	    public void initWithDefaults() {
	    }

        private static IASN1PreparedElementData preparedData = CoderFactory.getInstance().newPreparedElementData(GPRS_QoS.class);
        public IASN1PreparedElementData getPreparedData() {
            return preparedData;
        }


    }
            