
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
    @ASN1Choice ( name = "VariablePart" )
    public class VariablePart implements IASN1PreparedElement {
            
        @ASN1Element ( name = "integer", isOptional =  false , hasTag =  true, tag = 0 , hasDefaultValue =  false  )
    
	private Integer4 integer = null;
                
  
        @ASN1Element ( name = "number", isOptional =  false , hasTag =  true, tag = 1 , hasDefaultValue =  false  )
    
	private Digits number = null;
                
  @ASN1OctetString( name = "" )
    
            @ASN1SizeConstraint ( max = 2L )
        
        @ASN1Element ( name = "time", isOptional =  false , hasTag =  true, tag = 2 , hasDefaultValue =  false  )
    
	private byte[] time = null;
                
  @ASN1OctetString( name = "" )
    
            @ASN1SizeConstraint ( max = 4L )
        
        @ASN1Element ( name = "date", isOptional =  false , hasTag =  true, tag = 3 , hasDefaultValue =  false  )
    
	private byte[] date = null;
                
  @ASN1OctetString( name = "" )
    
            @ASN1SizeConstraint ( max = 4L )
        
        @ASN1Element ( name = "price", isOptional =  false , hasTag =  true, tag = 4 , hasDefaultValue =  false  )
    
	private byte[] price = null;
                
  
        
        public Integer4 getInteger () {
            return this.integer;
        }

        public boolean isIntegerSelected () {
            return this.integer != null;
        }

        private void setInteger (Integer4 value) {
            this.integer = value;
        }

        
        public void selectInteger (Integer4 value) {
            this.integer = value;
            
                    setNumber(null);
                
                    setTime(null);
                
                    setDate(null);
                
                    setPrice(null);
                            
        }

        
  
        
        public Digits getNumber () {
            return this.number;
        }

        public boolean isNumberSelected () {
            return this.number != null;
        }

        private void setNumber (Digits value) {
            this.number = value;
        }

        
        public void selectNumber (Digits value) {
            this.number = value;
            
                    setInteger(null);
                
                    setTime(null);
                
                    setDate(null);
                
                    setPrice(null);
                            
        }

        
  
        
        public byte[] getTime () {
            return this.time;
        }

        public boolean isTimeSelected () {
            return this.time != null;
        }

        private void setTime (byte[] value) {
            this.time = value;
        }

        
        public void selectTime (byte[] value) {
            this.time = value;
            
                    setInteger(null);
                
                    setNumber(null);
                
                    setDate(null);
                
                    setPrice(null);
                            
        }

        
  
        
        public byte[] getDate () {
            return this.date;
        }

        public boolean isDateSelected () {
            return this.date != null;
        }

        private void setDate (byte[] value) {
            this.date = value;
        }

        
        public void selectDate (byte[] value) {
            this.date = value;
            
                    setInteger(null);
                
                    setNumber(null);
                
                    setTime(null);
                
                    setPrice(null);
                            
        }

        
  
        
        public byte[] getPrice () {
            return this.price;
        }

        public boolean isPriceSelected () {
            return this.price != null;
        }

        private void setPrice (byte[] value) {
            this.price = value;
        }

        
        public void selectPrice (byte[] value) {
            this.price = value;
            
                    setInteger(null);
                
                    setNumber(null);
                
                    setTime(null);
                
                    setDate(null);
                            
        }

        
  

	    public void initWithDefaults() {
	    }

        private static IASN1PreparedElementData preparedData = CoderFactory.getInstance().newPreparedElementData(VariablePart.class);
        public IASN1PreparedElementData getPreparedData() {
            return preparedData;
        }


    }
            