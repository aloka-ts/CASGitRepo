
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
    @ASN1Sequence ( name = "ExtensionContainer", isSet = false )
    public class ExtensionContainer implements IASN1PreparedElement {
            
        @ASN1Element ( name = "privateExtensionList", isOptional =  true , hasTag =  true, tag = 0 , hasDefaultValue =  false  )
    
	private PrivateExtensionList privateExtensionList = null;
                
  
        @ASN1Element ( name = "pcs-Extensions", isOptional =  true , hasTag =  true, tag = 1 , hasDefaultValue =  false  )
    
	private PCS_Extensions pcs_Extensions = null;
                
  
        
        public PrivateExtensionList getPrivateExtensionList () {
            return this.privateExtensionList;
        }

        
        public boolean isPrivateExtensionListPresent () {
            return this.privateExtensionList != null;
        }
        

        public void setPrivateExtensionList (PrivateExtensionList value) {
            this.privateExtensionList = value;
        }
        
  
        
        public PCS_Extensions getPcs_Extensions () {
            return this.pcs_Extensions;
        }

        
        public boolean isPcs_ExtensionsPresent () {
            return this.pcs_Extensions != null;
        }
        

        public void setPcs_Extensions (PCS_Extensions value) {
            this.pcs_Extensions = value;
        }
        
  
                    
        
        public void initWithDefaults() {
            
        }

        private static IASN1PreparedElementData preparedData = CoderFactory.getInstance().newPreparedElementData(ExtensionContainer.class);
        public IASN1PreparedElementData getPreparedData() {
            return preparedData;
        }

            
    }
            