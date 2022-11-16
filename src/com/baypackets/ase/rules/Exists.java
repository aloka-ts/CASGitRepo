
package com.baypackets.ase.rules;


import javax.servlet.sip.*;

/**
 * Implementation of the exists operator. Test for non-null ness of
 * some variable in the context of a request.
 */ 
public class Exists extends Operator {
/***************************
    public Exists(String varName, int operatorIdx) {
        super(varName);
        this.operatorIdx = operatorIdx ;
    }
    
    public boolean evaluate(AseBaseRequest req) {
        String reqVal = getValue(req);
        if (reqVal != null) {
	  req.getMatchObject().setData (operatorIdx, reqVal);
	  return true;
        }
	return false;
    }

    public String toString() {
        return "" + varName() + " != null";
    }
*************************/
}
