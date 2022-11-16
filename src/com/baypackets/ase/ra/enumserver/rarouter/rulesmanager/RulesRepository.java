package com.baypackets.ase.ra.enumserver.rarouter.rulesmanager;
import java.io.InputStream;
import java.util.ArrayList;

import com.baypackets.ase.ra.enumserver.message.EnumRequest;



/**
 * This interface defines an object that manages a repository of triggering
 * rules for Servlet applications.
 */
public interface RulesRepository {
    
    
    /**
     * Removes all triggering rules for the specified application.
     */
    public boolean removeRulesForApp (String appName);

    
    /**
     * Returns a data structure containing the application and Servlet
     * name that matches the specified request.
     * @return 
     */
    public String findMatchingRule (EnumRequest req);

    
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
    
    public ArrayList generateRules(InputStream stream) throws RAParseRulesException;

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

