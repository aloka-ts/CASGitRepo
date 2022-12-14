
package com.agnity.map.asngenerated;
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
    @ASN1Sequence ( name = "UserCSGInformation", isSet = false )
    public class UserCSGInformation implements IASN1PreparedElement {
            
        @ASN1Element ( name = "csg-Id", isOptional =  false , hasTag =  true, tag = 0 , hasDefaultValue =  false  )
    
	private CSG_Id csg_Id = null;
                
  
        @ASN1Element ( name = "extensionContainer", isOptional =  true , hasTag =  true, tag = 1 , hasDefaultValue =  false  )
    
	private ExtensionContainer extensionContainer = null;
                
  @ASN1OctetString( name = "" )
    
            @ASN1SizeConstraint ( max = 1L )
        
        @ASN1Element ( name = "accessMode", isOptional =  true , hasTag =  true, tag = 2 , hasDefaultValue =  false  )
    
	private byte[] accessMode = null;
                
  @ASN1OctetString( name = "" )
    
            @ASN1SizeConstraint ( max = 1L )
        
        @ASN1Element ( name = "cmi", isOptional =  true , hasTag =  true, tag = 3 , hasDefaultValue =  false  )
    
	private byte[] cmi = null;
                
  
        
        public CSG_Id getCsg_Id () {
            return this.csg_Id;
        }

        

        public void setCsg_Id (CSG_Id value) {
            this.csg_Id = value;
        }
        
  
        
        public ExtensionContainer getExtensionContainer () {
            return this.extensionContainer;
        }

        
        public boolean isExtensionContainerPresent () {
            return this.extensionContainer != null;
        }
        

        public void setExtensionContainer (ExtensionContainer value) {
            this.extensionContainer = value;
        }
        
  
        
        public byte[] getAccessMode () {
            return this.accessMode;
        }

        
        public boolean isAccessModePresent () {
            return this.accessMode != null;
        }
        

        public void setAccessMode (byte[] value) {
            this.accessMode = value;
        }
        
  
        
        public byte[] getCmi () {
            return this.cmi;
        }

        
        public boolean isCmiPresent () {
            return this.cmi != null;
        }
        

        public void setCmi (byte[] value) {
            this.cmi = value;
        }
        
  
                    
        
        public void initWithDefaults() {
            
        }

        private static IASN1PreparedElementData preparedData = CoderFactory.getInstance().newPreparedElementData(UserCSGInformation.class);
        public IASN1PreparedElementData getPreparedData() {
            return preparedData;
        }

            
    }
            