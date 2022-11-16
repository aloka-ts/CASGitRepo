/*
 * StringManager.java
 *
 * Created on July 13, 2004, 5:08 PM
 */
package com.baypackets.ase.util;

import com.baypackets.ase.util.exceptions.StringManagerException;

import org.apache.log4j.Logger;

import java.text.MessageFormat;
import java.util.Map;
import java.util.HashMap;
import java.util.ResourceBundle;


/**
 * This utility class provides a repository of resource strings that are
 * specific to a given package.
 *
 * @author Zoltan Medveczky
 */
public final class StringManager {
    
    private static Logger _logger = Logger.getLogger(StringManager.class);
    private static Map _managers = new HashMap();
    
    private ResourceBundle _bundle;
        
    /**
     * Private constructor invoked internally by "getInstance()" method.
     *
     */
    private StringManager(String packageName) {
        try {
            _bundle = ResourceBundle.getBundle(packageName + ".strings");
        } catch (Exception e) {
            _logger.error(e.toString(), e);
            throw new StringManagerException(e.toString());
        }
    }
    
    /**
     * This static factory method returns an instance of the StringManager 
     * class that contains the resource strings for the specified package.
     *
     * @param packageName  The name of the package for which the returned 
     * StringManager instance will contain the resource strings for.
     * @return  An instance of StringManager that contains the resource strings
     * for the specified package.
     * @throws StringManagerException if an error occurs instantiating this class.
     */
    public static synchronized StringManager getInstance(String packageName) {
        if (!_managers.containsKey(packageName)) {
            _managers.put(packageName, new StringManager(packageName));
        }
        return (StringManager)_managers.get(packageName);
    }
    
    /**
     * Invokes "getInstance(String packageName)" passing a string
     * representation of the given Package object.
     *
     */
    public static StringManager getInstance(Package _package) {
        return getInstance(_package.getName());
    }
    
    /**
     * Returns the resource string for the specified key or null if no such
     * mapping exists.
     *
     * @param key  Used to look up the specified resource string.
     * @return  The requested resource string.
     */
    public String getString(String key) {
        return _bundle.getString(key);
    }
    
    /**
     * Returns the resultant string after inserting a string representation
     * of each Object in the given array into the template specified by the 
     * given key.
     *
     * @param key  The key used to look up the requested resource string.
     * In this case, the resource string is expected to be a template 
     * containing the indices of the Objects to insert from the given array.
     * @param args  An array of Objects whose String representations are 
     * inserted into the template specified by the given key.
     */
    public String getString(String key, Object[] args) {
        return MessageFormat.format(getString(key), args);
    }
    
    /**
     * Invokes "getString(String key, Object[] args)" passing the given 
     * argument in an Object array.
     *
     */
    public String getString(String key, Object arg) {
        return MessageFormat.format(getString(key), new Object[] {arg});
    }
    
    /**
     * Invokes "getString(String key, Object[] args)" passing the given
     * arguments as an Object array.
     *
     */
    public String getString(String key, Object arg1, Object arg2) {
        return MessageFormat.format(getString(key), new Object[] {arg1, arg2});
    }
    
}
