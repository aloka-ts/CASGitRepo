
package com.agnity.inapitutcs2.asngenerated;
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
    @ASN1Sequence ( name = "ExtensionField", isSet = false )
    public class ExtensionField implements IASN1PreparedElement {
            @ASN1Integer( name = "" )
    
        @ASN1Element ( name = "type", isOptional =  false , hasTag =  false  , hasDefaultValue =  false  )
    
	private Long type = null;
                
  
        @ASN1Element ( name = "criticality", isOptional =  false , hasTag =  false  , hasDefaultValue =  true  )
    
	private CriticalityType criticality = null;
                
  @ASN1Any( name = "" )
    
        @ASN1Element ( name = "value", isOptional =  false , hasTag =  true, tag = 1 , hasDefaultValue =  false  )
    
	private byte[] value = null;
                
  
        
        public Long getType () {
            return this.type;
        }

        

        public void setType (Long value) {
            this.type = value;
        }
        
  
        
        public CriticalityType getCriticality () {
            return this.criticality;
        }

        

        public void setCriticality (CriticalityType value) {
            this.criticality = value;
        }
        
  
        
        public byte[] getValue () {
            return this.value;
        }

        

        public void setValue (byte[] value) {
            this.value = value;
        }
        
  
                    
        
        public void initWithDefaults() {
            CriticalityType param_Criticality =         
            null;
        setCriticality(param_Criticality);
    
        }

        private static IASN1PreparedElementData preparedData = CoderFactory.getInstance().newPreparedElementData(ExtensionField.class);
        public IASN1PreparedElementData getPreparedData() {
            return preparedData;
        }

            
    }
            
