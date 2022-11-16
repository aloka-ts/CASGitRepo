
package com.baypackets.ase.rules;


import java.util.*;
import javax.servlet.sip.*;

/**
 * 
 */ 
public class Or implements Condition {
/*******************8
    List conditions;

    public Or(List conditions) {
        this.conditions = conditions;
    }

    public boolean evaluate(AseBaseRequest req) {
        for (int i = 0; i < conditions.size(); i++) {
            if (((Condition) conditions.get(i)).evaluate(req)) {
                return true;
            }
        }
        return false;
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();
        
        for (int i = 0; i < conditions.size(); i++) {
            if (i > 0) {
                sb.append(" || ");
            }
            sb.append("(" + conditions.get(i) + ")");
        }
        
        return sb.toString();
    }
*************************/
}
