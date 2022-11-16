/*
 * SysAppInfoDAO.java
 *
 * Created on July 3, 2005, 6:50 PM
 */
package com.baypackets.ase.container;

import java.util.Collection;

/**
 * This interface defines an object used to retrieve the meta data on all
 * system applications that are provisioned with the platform.  Specific
 * implementations of this interface may access a flat file, XML file,
 * database, or some other medium to retrieve the system app information.
 *
 * @author Baypackets
 */
public interface SysAppInfoDAO {
    
    /**
     * Returns a list of SysAppInfo objects each ecapsulating the meta data
		 * on a specific system app.
     *
     * @see com.baypackets.ase.container.SysAppInfo
     */
    public Collection getSysAppInfoList();
    
}
