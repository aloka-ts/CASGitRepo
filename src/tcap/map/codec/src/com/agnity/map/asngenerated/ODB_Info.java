
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
    @ASN1Sequence ( name = "ODB_Info", isSet = false )
    public class ODB_Info implements IASN1PreparedElement {
            
        @ASN1Element ( name = "odb-Data", isOptional =  false , hasTag =  false  , hasDefaultValue =  false  )
    
	private ODB_Data odb_Data = null;
                
  
        @ASN1Null ( name = "notificationToCSE" ) 
    
        @ASN1Element ( name = "notificationToCSE", isOptional =  true , hasTag =  false  , hasDefaultValue =  false  )
    
	private org.bn.types.NullObject notificationToCSE = null;
                
  
        @ASN1Element ( name = "extensionContainer", isOptional =  true , hasTag =  false  , hasDefaultValue =  false  )
    
	private ExtensionContainer extensionContainer = null;
                
  
        
        public ODB_Data getOdb_Data () {
            return this.odb_Data;
        }

        

        public void setOdb_Data (ODB_Data value) {
            this.odb_Data = value;
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
        
  
                    
        
        public void initWithDefaults() {
            
        }

        private static IASN1PreparedElementData preparedData = CoderFactory.getInstance().newPreparedElementData(ODB_Info.class);
        public IASN1PreparedElementData getPreparedData() {
            return preparedData;
        }

            
    }
            