
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
    @ASN1Enum (
        name = "ResumePIC"
    )
    public class ResumePIC implements IASN1PreparedElement {        
        public enum EnumType {
            
            @ASN1EnumItem ( name = "continue-Call-Processing", hasTag = true , tag = 1 )
            continue_Call_Processing , 
            @ASN1EnumItem ( name = "collect-Information-PIC", hasTag = true , tag = 2 )
            collect_Information_PIC , 
            @ASN1EnumItem ( name = "analyze-Information-PIC", hasTag = true , tag = 3 )
            analyze_Information_PIC , 
            @ASN1EnumItem ( name = "select-Route-PIC", hasTag = true , tag = 4 )
            select_Route_PIC , 
            @ASN1EnumItem ( name = "authorize-Origination-Attempt-PIC", hasTag = true , tag = 5 )
            authorize_Origination_Attempt_PIC , 
            @ASN1EnumItem ( name = "authorize-Call-Setup-PIC", hasTag = true , tag = 6 )
            authorize_Call_Setup_PIC , 
            @ASN1EnumItem ( name = "send-Call-PIC", hasTag = true , tag = 7 )
            send_Call_PIC , 
            @ASN1EnumItem ( name = "o-Alerting-PIC", hasTag = true , tag = 8 )
            o_Alerting_PIC , 
            @ASN1EnumItem ( name = "o-Active-PIC", hasTag = true , tag = 9 )
            o_Active_PIC , 
            @ASN1EnumItem ( name = "o-Suspended-PIC", hasTag = true , tag = 10 )
            o_Suspended_PIC , 
            @ASN1EnumItem ( name = "o-Null-PIC", hasTag = true , tag = 11 )
            o_Null_PIC , 
            @ASN1EnumItem ( name = "select-Facility-PIC", hasTag = true , tag = 32 )
            select_Facility_PIC , 
            @ASN1EnumItem ( name = "present-Call-PIC", hasTag = true , tag = 33 )
            present_Call_PIC , 
            @ASN1EnumItem ( name = "authorize-Termination-Attempt-PIC", hasTag = true , tag = 34 )
            authorize_Termination_Attempt_PIC , 
            @ASN1EnumItem ( name = "t-Alerting-PIC", hasTag = true , tag = 35 )
            t_Alerting_PIC , 
            @ASN1EnumItem ( name = "t-Active-PIC", hasTag = true , tag = 36 )
            t_Active_PIC , 
            @ASN1EnumItem ( name = "t-Suspended-PIC", hasTag = true , tag = 37 )
            t_Suspended_PIC , 
            @ASN1EnumItem ( name = "t-Null-PIC", hasTag = true , tag = 38 )
            t_Null_PIC , 
        }
        
        private EnumType value;
        private Integer integerForm;
        
        public EnumType getValue() {
            return this.value;
        }
        
        public void setValue(EnumType value) {
            this.value = value;
        }
        
        public Integer getIntegerForm() {
            return integerForm;
        }
        
        public void setIntegerForm(Integer value) {
            integerForm = value;
        }

	    public void initWithDefaults() {
	    }

        private static IASN1PreparedElementData preparedData = CoderFactory.getInstance().newPreparedElementData(ResumePIC.class);
        public IASN1PreparedElementData getPreparedData() {
            return preparedData;
        }


    }
            