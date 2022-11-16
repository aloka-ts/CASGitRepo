
package com.baypackets.ase.rules;


import javax.servlet.sip.*;

/**
 * A Condition is a part of a Rule. It is the interface that is
 * implemented by all operators (equal, exists, contains, subdomain-of)
 * and logical connectors (and, or, not) of the SIP Servlet API rule
 * language.
 */ 
public interface Condition {
    /**
     * Returns true if the specified request satisfies this condition,
     * and false otherwise.
     */
    //public boolean evaluate(AseBaseRequest req);
}
