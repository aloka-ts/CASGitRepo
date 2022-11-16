/*
 * MediaServerDAO.java
 *
 * Created on July 1, 2005, 6:35 PM
 */
package com.baypackets.ase.mediaserver;

import java.util.Collection;

import com.baypackets.ase.sbb.MediaServer;


/**
 * This interface defines an object used to access the data store to 
 * retreive the info on all media servers provisioned with the
 * platform.
 *
 * @author Baypackets
 */
public interface MediaServerDAO {
    
    /**
     * This method returns the list of all MediaServer objects from the backing
     * store each of which encapsulate the meta data on a media server that is
		 * provisioned with the platform.
     *
     * @return  A Collection of MediaServer objects.
     * @see com.baypackets.ase.sbb.MediaServer
     */
    public Collection<MediaServer> getAllMediaServers();
    
    
    /**
     * This method returns the MediaServer object from the backing
     * store which encapsulate the meta data on a media server that is
		 * provisioned with the platform.
     * @param id media server object id
     * @return  MediaServer object.
     * @see com.baypackets.ase.sbb.MediaServer
     */
    public MediaServer getMediaServer(String id);
}
