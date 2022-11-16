/*
 * SysAppInfoDAOFactory.java
 *
 * Created on July 3, 2005, 6:51 PM
 */
package com.baypackets.ase.container;

import com.baypackets.ase.common.Registry;
import com.baypackets.ase.util.Constants;

/**
 * This class provides a factory for obtaining SysAppInfoDAO objects.
 *
 * @author Baypackets
 */
public abstract class SysAppInfoDAOFactory {
    
    /**
     * Returns a concrete implementation of this abstract factory class
     * as a singleton instance.
     */
    public static SysAppInfoDAOFactory getInstance() {
        return (SysAppInfoDAOFactory)Registry.lookup(Constants.NAME_SYSAPP_INFO_DAO_FACTORY);
    }
    
    /**
     * Instantiates and returns an implementation of the SysAppInfoDAO 
     * interface.
     */
    public abstract SysAppInfoDAO getSysAppInfoDAO();
    
}
