/*
 * FileBasedMsDAOFactory.java
 *
 * Created on July 2, 2005, 3:02 PM
 */
package com.baypackets.ase.mediaserver;

import com.baypackets.ase.util.Constants;
import java.io.File;

/**
 * @author Baypackets
 */
public class FileBasedMsDAOFactory extends MediaServerDAOFactory {
    
    /**
     * Instantiates and returns a FileBasedMsDAO object.
		 *
		 * @see com.baypackets.ase.mediaserver.FileBasedMsDAO
     */
    public MediaServerDAO getMediaServerDAO() {        
        return new FileBasedMsDAO(new File(Constants.ASE_HOME, Constants.FILE_MEDIA_SERVER_CONFIG).toURI());        
    }
    
}
