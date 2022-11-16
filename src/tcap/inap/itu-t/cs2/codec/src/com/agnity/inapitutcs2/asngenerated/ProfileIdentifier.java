
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
    @ASN1Choice ( name = "ProfileIdentifier" )
    public class ProfileIdentifier implements IASN1PreparedElement {
            
        @ASN1Element ( name = "access", isOptional =  false , hasTag =  true, tag = 0 , hasDefaultValue =  false  )
    
	private CalledPartyNumber access = null;
                
  
        @ASN1Element ( name = "group", isOptional =  false , hasTag =  true, tag = 1 , hasDefaultValue =  false  )
    
	private FacilityGroup group = null;
                
  
        
        public CalledPartyNumber getAccess () {
            return this.access;
        }

        public boolean isAccessSelected () {
            return this.access != null;
        }

        private void setAccess (CalledPartyNumber value) {
            this.access = value;
        }

        
        public void selectAccess (CalledPartyNumber value) {
            this.access = value;
            
                    setGroup(null);
                            
        }

        
  
        
        public FacilityGroup getGroup () {
            return this.group;
        }

        public boolean isGroupSelected () {
            return this.group != null;
        }

        private void setGroup (FacilityGroup value) {
            this.group = value;
        }

        
        public void selectGroup (FacilityGroup value) {
            this.group = value;
            
                    setAccess(null);
                            
        }

        
  

	    public void initWithDefaults() {
	    }

        private static IASN1PreparedElementData preparedData = CoderFactory.getInstance().newPreparedElementData(ProfileIdentifier.class);
        public IASN1PreparedElementData getPreparedData() {
            return preparedData;
        }


    }
            