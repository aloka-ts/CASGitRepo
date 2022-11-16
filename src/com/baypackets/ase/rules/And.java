package com.baypackets.ase.rules;


import java.util.*;
import javax.servlet.sip.*;
import com.baypackets.ase.container.*;

/**
 * 
 */ 
public class And implements Condition {
/**************************************
    List conditions;

    public And(List conditions) {
        this.conditions = conditions;
    }

    public boolean evaluate(AseBaseRequest req) {
        for (int i = 0; i < conditions.size(); i++) {
            if (! ((Condition) conditions.get(i)).evaluate(req)) {
                return false;
            }
        }
        return true;
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();
        
        for (int i = 0; i < conditions.size(); i++) {
            if (i > 0) {
                sb.append(" && ");
            }
            sb.append("(" + conditions.get(i) + ")");
        }
        
        return sb.toString();
    }
**********************/
public boolean evaluate (AseBaseRequest req) {return false;}
}
