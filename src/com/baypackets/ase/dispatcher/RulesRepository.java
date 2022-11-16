/*------------------------------------------
* RulesRepository interface
* Nasir
* Version 1.0   08/19/04
* BayPackets Inc.
* Revisions:
* BugID : Date : Info
*------------------------------------------*/

package com.baypackets.ase.dispatcher;


import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;

import com.baypackets.ase.spi.container.SasMessage;


/**
 * This interface defines an object that manages a repository of triggering
 * rules for Servlet applications.
 */
public interface RulesRepository {
    
    /**
     * Removes the specified triggering rule from the repository.
     */
    public boolean removeRule (String ruleId);

    
    /**
     * Removes all triggering rules for the specified application.
     */
    public boolean removeRulesForApp (String appName);

    
    /**
     * Returns a data structure containing the application and Servlet
     * name that matches the specified request.
     */
    public RuleDataComposite findMatchingRule (SasMessage req, ArrayList lastRuleData, String applicationName);

    
    /**
     * Returns "true" if the repository has triggering rules registered
     * for the specified application or returns "false" otherwise.
     */
    public boolean hasRules(String appName);
    
    
    /**
     * Parses the given input stream for triggering rules and returns a
     * Collection of RuleObjects as the result.
     *
     * @return  A collection of RuleObjects.
     * @see com.baypackets.ase.dispatcher.RuleObject
     */
    public Collection generateRules(InputStream stream, String appName) throws ParseRulesException;

    
    /**
     * Adds the given set of RuleObjects to the repository keyed by the
     * specified application name.
     *
     * @param rules  A collection of RuleObjects.
     * @see com.baypackets.ase.dispatcher.RuleObject
     */
    public void addRules(String appName, int priority, Collection rules);
    
    
    /**
     * Causes all threads that invoke the "findMatchingRule" method to be 
     * blocked until the "unlock" method is called.
     */
    public void lock();
    
    
    /**
     * Unlocks the rules repository and un-blocks any threads that were blocked
     * by the "findMatchingRule" method.
     */
    public void unlock();
    
}

