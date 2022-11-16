
package com.baypackets.ase.rules;


import javax.servlet.sip.*;

/**
 * Implementation of the equal operator. Test an attribute of request
 * objects for equality with a literal value.
 */ 
public class Equal extends Operator {
    private String value;
    /** Case sensitive or not. */
/***********************
    private boolean ignoreCase;

    public Equal(String varName, String value, boolean ignoreCase, int operatorIdx) {
        super(varName);
        this.value = value;
        this.ignoreCase = ignoreCase;
        this.operatorIdx = operatorIdx ;

        if (value == null) {
            throw new NullPointerException("equals value must be non-null");
        }
    }
    
    public boolean evaluate(AseBaseRequest req) {
        String reqVal = getValue(req);
           if (( ignoreCase
            ? value.equalsIgnoreCase(reqVal)
            : value.equals(reqVal))) {

	     req.getMatchObject().setData (operatorIdx, reqVal);
             return true;	     
           }
        return false; 
    }

    public String toString() {
        return "" + varName() + " == \"" + value + "\"";
    }
****************************/
}
