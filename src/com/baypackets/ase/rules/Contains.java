
package com.baypackets.ase.rules;


import javax.servlet.sip.*;

/**
 * Implementation of the equal operator. Test an attribute of request
 * objects for equality with a literal value.
 */ 
public class Contains extends Operator {
    private String value;
    /** Case sensitive or not. */
    private boolean ignoreCase;
/*****************************
    public Contains(String varName, String value, boolean ignoreCase, int operatorIdx) {
        super(varName);
        this.value = value;
        this.ignoreCase = ignoreCase;
   	this.operatorIdx = operatorIdx;

        if (value == null) {
            throw new IllegalArgumentException(
                "contains value must be non-null");
        }
        if (ignoreCase) {
            this.value = this.value.toLowerCase();
        }
    }
    
    public boolean evaluate(AseBaseRequest req) {
        String reqVal = getValue(req);
        if (reqVal == null) return false;
        if (ignoreCase) reqVal = reqVal.toLowerCase();
 	if (reqVal.indexOf(value) > -1) {
	  req.getMatchObject().setData (operatorIdx, reqVal);
          return true;
        }
        return false;
    }

    public String toString() {
        return "" + varName() + " contains \"" + value + "\"";
    }
******************************/
}
