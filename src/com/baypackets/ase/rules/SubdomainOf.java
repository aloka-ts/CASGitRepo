package com.baypackets.ase.rules;

import javax.servlet.sip.*;

/**
 * Implementation of the subdomain-of operator. Test an attribute of request
 * objects for equality with a literal value.
 * 
 * <p>XXX: doesn't handle telephone numbers correctly...
 */ 
public class SubdomainOf extends Operator {
/************************
    private String value;
    private boolean matchExactly;

    public SubdomainOf(String varName, String value, int operatorIdx) {
        super(varName);
        this.value = value;
        this.operatorIdx = operatorIdx ;

        if (value == null) {
            throw new NullPointerException("equals value must be non-null");
        }
    }
    
    //XXX: handle tel args
    public boolean evaluate(AseBaseRequest req) {
        String actualValue = getValue(req);

        if (actualValue.endsWith(value)) {
            int len1 = actualValue.length();
            int len2 = value.length();
            if ( (len1 == len2 ||
                    (!matchExactly && actualValue.charAt(len1-len2-1) == '.'))) {
	      req.getMatchObject().setData (operatorIdx, actualValue);
	      return true;
            }
        }
        return false;
    }

    public String toString() {
        return "" + varName() + " subdomain-of " + value;
    }
**************************/
}
