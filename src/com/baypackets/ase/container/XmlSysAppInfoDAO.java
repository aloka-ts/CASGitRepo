/*
 * XmlSysAppInfoDAO.java
 *
 * Created on July 3, 2005, 6:55 PM
 */
package com.baypackets.ase.container;

import com.baypackets.ase.control.AseRoles;
import com.baypackets.ase.deployer.SysAppInfo;
import com.baypackets.ase.util.AseStrings;

import java.util.Collection;
import java.util.ArrayList;
import java.net.URI;
import java.net.URL;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.apache.log4j.Logger;

/**
 * This implementation of the SysAppInfoDAO interface reads the meta data
 * on all system applications from an XML config file.
 *
 * @author Baypackets
 */
public class XmlSysAppInfoDAO implements SysAppInfoDAO {
    
    private static Logger _logger = Logger.getLogger(XmlSysAppInfoDAO.class);
    
    private URI _xmlUri;
    private URI _sysAppsLocation;
    
    /**
     * Default constructor.
     */
    public XmlSysAppInfoDAO() {        
    }
    
    /**
     * @param xmlUri  The URI of the XML file that contains the system 
     * application info.
     * @param sysAppsLocation  The URI of the root directory where the system
     * application archive files reside.
     */
    public XmlSysAppInfoDAO(URI xmlUri, URI sysAppsLocation) {
        this.setXmlUri(xmlUri);
        this.setSysAppsLocation(sysAppsLocation);
    }

    
    /**
     * This method parses an XML file to extract the meta data on all system
     * applications and returns it as a list of SysAppInfo objects.  The XML 
		 * file to be parsed is determined by the value of this object's 
		 * "xmlUri" property.
     *
     * @return  A Collection of SysAppInfo objects each of which contain
     * the meta data on a specific system app.
     * @see com.baypackets.ase.container.SysAppInfo
     */
    public Collection getSysAppInfoList() {
        boolean loggerEnabled = _logger.isDebugEnabled();
        
        if (loggerEnabled) {
            _logger.debug("getSysAppInfoList(): Extracting the meta data on all system applications from XML file: " + _xmlUri);
        }
        
        try {            
            Collection appInfoList = null;
            DocumentBuilder parser = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document doc = parser.parse(_xmlUri.toString());
            
            NodeList nodes = doc.getElementsByTagName("System-Apps");
            
            for (int i = 0; i < nodes.getLength(); i++) {
                Element elem = (Element)nodes.item(i);
                
                NodeList nodes2 = elem.getElementsByTagName("App");
                
                appInfoList = new ArrayList(nodes2.getLength());
               
                for (int j = 0; j < nodes2.getLength(); j++) {
                    Element elem2 = (Element)nodes2.item(j);
                    
                    SysAppInfo appInfo = new SysAppInfo();
                    appInfo.setArchive(new URL(_sysAppsLocation.toString() + "/" + elem2.getAttribute("archive")));
                    appInfo.setDeployOnRole(this.getRole(elem2.getAttribute("deploy-on-role")));
                    appInfo.setStartOnRole(this.getRole(elem2.getAttribute("start-on-role")));
                    appInfo.setName(elem2.getAttribute("name")); 
                    appInfoList.add(appInfo);
                }
            }
            
            if (loggerEnabled && (appInfoList == null || appInfoList.isEmpty())) {
                _logger.debug("getSysAppInfoList(): No system applications specified in XML file.  Returning empty list...");
            }
            
            return appInfoList != null ? appInfoList : new ArrayList(0);
        } catch (Exception e) {
            String msg = "Error occurred while parsing the system app config file: " + e.getMessage();
            _logger.error(msg, e);
            throw new RuntimeException(msg);
        }
    }
    
    
    private Short getRole(String role) {
        if (role == null || role.trim().equals(AseStrings.BLANK_STRING)) {
            return null;
        }
        if (AseStrings.ACTIVE.equals(role)) {
            return new Short(AseRoles.ACTIVE);
        }
        if (AseStrings.STANDBY.equals(role)) {
            return new Short(AseRoles.STANDBY);
        }
        throw new IllegalArgumentException("Invalid role name specified in system app XML file: " + role);
    }
    
    
    /**
     * Returns the URI of the XML file to extract the system app info
     * from.
     */
    public URI getXmlUri() {
        return _xmlUri;
    }
    
    /**
     * Sets the URI of the XML file to parse.
     */
    public void setXmlUri(URI _xmlUri) {
        this._xmlUri = _xmlUri;
    }
    
    /**
     * Returns the URI of the root directory containing the archive files
     * for all system applications.
     */
    public URI getSysAppsLocation() {
        return _sysAppsLocation;
    }
    
    /**
     * Sets the URI of the root directory containing the archive files
     * for all system applications.
     */
    public void setSysAppsLocation(URI _sysAppsLocation) {
        this._sysAppsLocation = _sysAppsLocation;
    }
    
}
