/*
 * Registry.java
 *
 * Created on August 7, 2004, 2:28 PM
 */
package com.baypackets.ase.common;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


/**
 * This class provides a globally accessible registry of Objects.
 *
 * @author  Zoltan Medveczky
 */
public final class Registry {
    
    private static Map _objects = new ConcurrentHashMap();
    
    /**
     * Private constructor.
     *
     */
    private Registry() {        
    }
    
    /**
     * Returns the Object that is registered under the specified name or 
     * returns null if no Object is found for that name.
     *
     * @param name  The key used to lookup an Object in this registry
     */
    public static Object lookup(String name) {
        return _objects.get(name);
    }
    
    /**
     * Binds the specified Object to the registry keyed by the given name.
     * This method will replace any Object already registered under the 
     * specified name.
     *
     * @param name  The key used to register the given Object
     * @param object  The Object to bind to the registry
     */
    public static void bind(String name, Object object) {
        _objects.put(name, object);
    }
    
}
