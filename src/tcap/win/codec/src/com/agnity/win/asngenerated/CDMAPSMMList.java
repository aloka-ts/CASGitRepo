
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
    @ASN1BoxedType ( name = "CDMAPSMMList" )
    public class CDMAPSMMList implements IASN1PreparedElement {
                
            

       @ASN1PreparedElement
       @ASN1Sequence ( name = "CDMAPSMMList" , isSet = true )
       public static class CDMAPSMMListSequenceType implements IASN1PreparedElement {
                
        @ASN1Element ( name = "cdmaServingOneWayDelay2", isOptional =  false , hasTag =  true, tag = 347 , hasDefaultValue =  false  )
    
	private CDMAServingOneWayDelay2 cdmaServingOneWayDelay2 = null;
                
  
        @ASN1Element ( name = "cdmaTargetMAHOList", isOptional =  false , hasTag =  true, tag = 136 , hasDefaultValue =  false  )
    
	private CDMATargetMAHOList cdmaTargetMAHOList = null;
                
  
        @ASN1Element ( name = "cdmaTargetMAHOList2", isOptional =  true , hasTag =  true, tag = 136 , hasDefaultValue =  false  )
    
	private CDMATargetMAHOList cdmaTargetMAHOList2 = null;
                
  
        
        public CDMAServingOneWayDelay2 getCdmaServingOneWayDelay2 () {
            return this.cdmaServingOneWayDelay2;
        }

        

        public void setCdmaServingOneWayDelay2 (CDMAServingOneWayDelay2 value) {
            this.cdmaServingOneWayDelay2 = value;
        }
        
  
        
        public CDMATargetMAHOList getCdmaTargetMAHOList () {
            return this.cdmaTargetMAHOList;
        }

        

        public void setCdmaTargetMAHOList (CDMATargetMAHOList value) {
            this.cdmaTargetMAHOList = value;
        }
        
  
        
        public CDMATargetMAHOList getCdmaTargetMAHOList2 () {
            return this.cdmaTargetMAHOList2;
        }

        
        public boolean isCdmaTargetMAHOList2Present () {
            return this.cdmaTargetMAHOList2 != null;
        }
        

        public void setCdmaTargetMAHOList2 (CDMATargetMAHOList value) {
            this.cdmaTargetMAHOList2 = value;
        }
        
  
                
                
        public void initWithDefaults() {
            
        }

        public IASN1PreparedElementData getPreparedData() {
            return preparedData_CDMAPSMMListSequenceType;
        }

       private static IASN1PreparedElementData preparedData_CDMAPSMMListSequenceType = CoderFactory.getInstance().newPreparedElementData(CDMAPSMMListSequenceType.class);
                
       }

       
                
            @ASN1SequenceOf( name = "CDMAPSMMList" , isSetOf = true)
	    private java.util.Collection<CDMAPSMMListSequenceType> value = null; 
    
            public CDMAPSMMList () {
            }
        
            public CDMAPSMMList ( java.util.Collection<CDMAPSMMListSequenceType> value ) {
                setValue(value);
            }
                        
            public void setValue(java.util.Collection<CDMAPSMMListSequenceType> value) {
                this.value = value;
            }
            
            public java.util.Collection<CDMAPSMMListSequenceType> getValue() {
                return this.value;
            }            
            
            public void initValue() {
                setValue(new java.util.LinkedList<CDMAPSMMListSequenceType>()); 
            }
            
            public void add(CDMAPSMMListSequenceType item) {
                value.add(item);
            }

	    public void initWithDefaults() {
	    }

        private static IASN1PreparedElementData preparedData = CoderFactory.getInstance().newPreparedElementData(CDMAPSMMList.class);
        public IASN1PreparedElementData getPreparedData() {
            return preparedData;
        }


    }
            