
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
    @ASN1BoxedType ( name = "QualificationDirectiveRes" )
    public class QualificationDirectiveRes implements IASN1PreparedElement {
                
        

       @ASN1PreparedElement
       @ASN1Sequence ( name = "QualificationDirectiveRes" , isSet = true )
       public static class QualificationDirectiveResSequenceType implements IASN1PreparedElement {
                
                
                
        public void initWithDefaults() {
            
        }

        public IASN1PreparedElementData getPreparedData() {
            return preparedData_QualificationDirectiveResSequenceType;
        }

       private static IASN1PreparedElementData preparedData_QualificationDirectiveResSequenceType = CoderFactory.getInstance().newPreparedElementData(QualificationDirectiveResSequenceType.class);
                
       }

       
                
        @ASN1Element ( name = "QualificationDirectiveRes", isOptional =  false , hasTag =  true, tag = 18, 
        tagClass =  TagClass.Private  , hasDefaultValue =  false  )
    
        private QualificationDirectiveResSequenceType  value;        

        
        
        public QualificationDirectiveRes () {
        }
        
        
        
        public void setValue(QualificationDirectiveResSequenceType value) {
            this.value = value;
        }
        
        
        
        public QualificationDirectiveResSequenceType getValue() {
            return this.value;
        }            
        

	    public void initWithDefaults() {
	    }

        private static IASN1PreparedElementData preparedData = CoderFactory.getInstance().newPreparedElementData(QualificationDirectiveRes.class);
        public IASN1PreparedElementData getPreparedData() {
            return preparedData;
        }

            
    }
            