
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
    @ASN1Enum (
        name = "EventTypeBCSM"
    )
    public class EventTypeBCSM implements IASN1PreparedElement {        
        public enum EnumType {
            
            @ASN1EnumItem ( name = "origAttemptAuthorized", hasTag = true , tag = 1 )
            origAttemptAuthorized , 
            @ASN1EnumItem ( name = "collectedInfo", hasTag = true , tag = 2 )
            collectedInfo , 
            @ASN1EnumItem ( name = "analysedInformation", hasTag = true , tag = 3 )
            analysedInformation , 
            @ASN1EnumItem ( name = "routeSelectFailure", hasTag = true , tag = 4 )
            routeSelectFailure , 
            @ASN1EnumItem ( name = "oCalledPartyBusy", hasTag = true , tag = 5 )
            oCalledPartyBusy , 
            @ASN1EnumItem ( name = "oNoAnswer", hasTag = true , tag = 6 )
            oNoAnswer , 
            @ASN1EnumItem ( name = "oAnswer", hasTag = true , tag = 7 )
            oAnswer , 
            @ASN1EnumItem ( name = "oMidCall", hasTag = true , tag = 8 )
            oMidCall , 
            @ASN1EnumItem ( name = "oDisconnect", hasTag = true , tag = 9 )
            oDisconnect , 
            @ASN1EnumItem ( name = "oAbandon", hasTag = true , tag = 10 )
            oAbandon , 
            @ASN1EnumItem ( name = "termAttemptAuthorized", hasTag = true , tag = 12 )
            termAttemptAuthorized , 
            @ASN1EnumItem ( name = "tBusy", hasTag = true , tag = 13 )
            tBusy , 
            @ASN1EnumItem ( name = "tNoAnswer", hasTag = true , tag = 14 )
            tNoAnswer , 
            @ASN1EnumItem ( name = "tAnswer", hasTag = true , tag = 15 )
            tAnswer , 
            @ASN1EnumItem ( name = "tMidCall", hasTag = true , tag = 16 )
            tMidCall , 
            @ASN1EnumItem ( name = "tDisconnect", hasTag = true , tag = 17 )
            tDisconnect , 
            @ASN1EnumItem ( name = "tAbandon", hasTag = true , tag = 18 )
            tAbandon , 
            @ASN1EnumItem ( name = "oTermSeized", hasTag = true , tag = 19 )
            oTermSeized , 
            @ASN1EnumItem ( name = "oSuspended", hasTag = true , tag = 20 )
            oSuspended , 
            @ASN1EnumItem ( name = "tSuspended", hasTag = true , tag = 21 )
            tSuspended , 
            @ASN1EnumItem ( name = "origAttempt", hasTag = true , tag = 22 )
            origAttempt , 
            @ASN1EnumItem ( name = "termAttempt", hasTag = true , tag = 23 )
            termAttempt , 
            @ASN1EnumItem ( name = "oReAnswer", hasTag = true , tag = 24 )
            oReAnswer , 
            @ASN1EnumItem ( name = "tReAnswer", hasTag = true , tag = 25 )
            tReAnswer , 
            @ASN1EnumItem ( name = "facilitySelectedAndAvailable", hasTag = true , tag = 26 )
            facilitySelectedAndAvailable , 
            @ASN1EnumItem ( name = "callAccepted", hasTag = true , tag = 27 )
            callAccepted , 
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

        private static IASN1PreparedElementData preparedData = CoderFactory.getInstance().newPreparedElementData(EventTypeBCSM.class);
        public IASN1PreparedElementData getPreparedData() {
            return preparedData;
        }


    }
            