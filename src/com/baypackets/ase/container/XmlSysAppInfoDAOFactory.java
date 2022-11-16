/*
 * XmlSysAppInfoDAOFactory.java
 *
 * Created on July 3, 2005, 6:56 PM
 */
package com.baypackets.ase.container;

import com.baypackets.ase.util.Constants;
import java.net.URI;
import java.net.URL;
import java.io.File;
import org.apache.log4j.Logger;


/**
 * @author Baypackets
 */
public class XmlSysAppInfoDAOFactory extends SysAppInfoDAOFactory {
            
    private static Logger _logger = Logger.getLogger(XmlSysAppInfoDAOFactory.class);
    
    /**
     * Instantiates and returns an XmlSysAppInfoDAO object.
     *
     * @see com.baypackets.ase.container.XmlSysAppInfoDAO
     */
    public SysAppInfoDAO getSysAppInfoDAO() {
        boolean loggerEnabled = _logger.isDebugEnabled();
        
        if (loggerEnabled) {
            _logger.debug("getSysAppInfoDAO() called...");
        }
        
        try {
            XmlSysAppInfoDAO dao = new XmlSysAppInfoDAO();

            URL xmlURL = this.getClass().getClassLoader().getResource("com/baypackets/ase/startup/server-config.xml");
                        
            if (loggerEnabled) {
                _logger.debug("getSysAppInfoDAO(): URI of XML file containing the system app info to parse: " + xmlURL);
            }
            
            dao.setXmlUri(new URI(xmlURL.toString()));
            
            File sysAppLocation = new File(Constants.ASE_HOME, "sysapps");
            
            if (loggerEnabled) {
                _logger.debug("getSysAppInfoDAO(): URI of system app archive root directory: " + sysAppLocation.toURI());
            }

            dao.setSysAppsLocation(sysAppLocation.toURI());
            
            return dao;
        } catch (Exception e) {
            String msg = "Error occurred while initializing XmlSysAppInfoDAO object: " + e.getMessage();
            _logger.error(msg, e);
            throw new RuntimeException(msg, e);
        }
    }
    
}
