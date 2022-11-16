/*------------------------------------------
* RulesRepository interface
* Nasir
* Version 1.0   08/19/04
* BayPackets Inc.
* Revisions:
* BugID : Date : Info
*------------------------------------------*/

package com.baypackets.ase.ra.diameter.sh.rarouter.rulesmanager;


import com.baypackets.ase.ra.diameter.sh.ShRequest;

import java.io.InputStream;
import java.util.ArrayList;


/**
 * This interface defines an object that manages a repository of triggering
 * rules for Servlet applications.
 */
public interface RulesRepository {
    
    
    /**
     * Removes all triggering rules for the specified application.
     */
    boolean removeRulesForApp(String appName);

    
    /**
     * Returns a data structure containing the application and Servlet
     * name that matches the specified request.
     */
    String findMatchingRule(ShRequest req);

    
    /**
     * Returns "true" if the repository has triggering rules registered
     * for the specified application or returns "false" otherwise.
     */
    boolean hasRules(String appName);
    
    
    /**
     * Parses the given input stream for triggering rules and returns a
     * Collection of RuleObjects as the result.
     *
     * @return  A collection of RuleObjects.
     * @see com.baypackets.ase.dispatcher.RuleObject
     */

    ArrayList generateRules(InputStream stream) throws SHParseRulesException;

    /**
     * Causes all threads that invoke the "findMatchingRule" method to be 
     * blocked until the "unlock" method is called.
     */
    void lock();
    
    
    /**
     * Unlocks the rules repository and un-blocks any threads that were blocked
     * by the "findMatchingRule" method.
     */
    void unlock();
    
}

