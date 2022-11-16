/*
 * MediaServerDAOFactory.java
 *
 * Created on July 1, 2005, 6:41 PM
 */
package com.baypackets.ase.mediaserver;

import com.baypackets.ase.common.Registry;
import com.baypackets.ase.util.Constants;


/**
 * This class provides a factory for obtaining MediaServerDAO objects.
 *
 * @author Baypackets
 */
public abstract class MediaServerDAOFactory {
        
    /**
     * Returns a singleton instance of this abstract factory class.
     *
     * @throws MediaServerDAOFactoryException if an error occurs while
     * instantiating the factory class.
     */
    public static MediaServerDAOFactory getInstance() {
        return (MediaServerDAOFactory)Registry.lookup(Constants.NAME_MS_DAO_FACTORY);
    }
    
    /**
     * Creates and returns an implementation of the MediaServerDAO
     * interface.
     *
     * @throws MediaServerDAOFactoryException if an error occurs while
     * instantiating the implemenation class.
     */
    public abstract MediaServerDAO getMediaServerDAO();
    
}
