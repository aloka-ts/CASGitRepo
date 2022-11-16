/*
 * SipXmlEntityResolver.java
 *
 * Created on August 6, 2004, 4:51 PM
 */
package com.baypackets.ase.container;

import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.apache.log4j.Logger;

import java.io.InputStream;


/**
 * An instance of this class is registered with an XML parser to resolve
 * any entities found in the "sip.xml" and "sas.xml" deployment descriptor
 * files.
 */
public class SipXmlEntityResolver implements EntityResolver {
    
    private static Logger _logger = Logger.getLogger(SipXmlEntityResolver.class);
    
    /**
     * Public ID of the SIP Servlet DTD.  This should appear in all "sip.xml" 
     * files and must be resolved before validation.
     */
    public static final String SIP_XML_PUBLIC_ID =
            "-//Java Community Process//DTD SIP Application 1.0//EN";

    /**
     * Public ID of SAS deployment descriptor.
     */
    public static final String SAS_XML_PUBLIC_ID = 
            "-//Baypackets SIP Application Server//DTD SAS Descriptor//EN";
    
    /**
     * Public ID of SAS deployment descriptor.
     */
    public static final String CAS_XML_PUBLIC_ID = 
            "-//Baypackets SIP Application Server//DTD CAS Descriptor//EN";

    
    /**
     * Public ID of SAS Resource deployment descriptor.
     */
    public static final String SAS_RESOURCE_XML_PUBLIC_ID = 
            "-//Baypackets SIP Application Server//DTD SAS Resource Descriptor//EN";
    
    public static final String CAS_RESOURCE_XML_PUBLIC_ID = 
    		"-//Baypackets SIP Application Server//DTD CAS Resource Descriptor//EN";
    
    /**
     * Public ID of SOA deployment descriptor.
     */
    public static final String SAS_SOA_XML_PUBLIC_ID = 
            "-//Baypackets SIP Application Server//DTD SAS SOA Descriptor//EN";
    
    /** 
     * Absolute name of the SIP servlet DTD resource in the CLASSPATH.
     */
    public static final String SIP_XML_RES =
            "/javax/servlet/sip/resources/sip-app_1_0.dtd";
    
    /**
     * Absolute name of the SAS descriptor DTD resource in the CLASSPATH.
     */
    public static final String SAS_XML_RES = 
            "/com/baypackets/ase/container/schemas/sas-app_1_0.dtd";
    
    /**
     * Absolute name of the CAS descriptor DTD resource in the CLASSPATH.
     */
    public static final String CAS_XML_RES = 
            "/com/baypackets/ase/container/schemas/cas-app_1_0.dtd";

    /**
     * Absolute name of the SAS descriptor DTD resource in the CLASSPATH.
     */
    public static final String SAS_RESOURCE_XML_RES = 
            "/com/baypackets/ase/container/schemas/sas-resource.dtd";
    
    public static final String CAS_RESOURCE_XML_RES = 
        "/com/baypackets/ase/container/schemas/cas-resource.dtd";
    
    /**
     * Absolute name of the SAS SOA DTD in the CLASSPATH.
     */
    public static final String SAS_SOA_XML_RES = 
            "/com/baypackets/ase/container/schemas/sas-soa.dtd";
    
    /**
     * Resolves the entity specified by the given public ID. 
     */
    public InputSource resolveEntity(String publicId, String systemId) {
        try {
            if (publicId.equals(SIP_XML_PUBLIC_ID)) {
                InputStream in = this.getClass().getResourceAsStream(SIP_XML_RES);
                    
                if (in != null) {
                    if (_logger.isDebugEnabled()) {
                        _logger.debug("Resolved entity \"" + publicId + "\" to \"" + SIP_XML_RES + "\"");
                    }
                    return new InputSource(in);
                } else {
                    _logger.error("sip-app_1_0.dtd resource is not available");                        
                    return null;
                }
            } else if (publicId.equals(SAS_XML_PUBLIC_ID)) {
                InputStream in = this.getClass().getResourceAsStream(SAS_XML_RES);
                
                if (in != null) {
                    if (_logger.isDebugEnabled()) {
                        _logger.debug("Resolved entity \"" + publicId + "\" to \"" + SAS_XML_RES + "\"");
                    }
                    return new InputSource(in);
                } else {
                    _logger.error("sas-app_1_0.dtd resource is not available");
                    return null;
                }
            }else if (publicId.equals(CAS_XML_PUBLIC_ID)) {
                InputStream in = this.getClass().getResourceAsStream(CAS_XML_RES);
                
                if (in != null) {
                    if (_logger.isDebugEnabled()) {
                        _logger.debug("Resolved entity \"" + publicId + "\" to \"" + CAS_XML_RES + "\"");
                    }
                    return new InputSource(in);
                } else {
                    _logger.error("cas-app_1_0.dtd resource is not available");
                    return null;
                }
            }else if (publicId.equals(SAS_RESOURCE_XML_PUBLIC_ID)) {
                InputStream in = this.getClass().getResourceAsStream(SAS_RESOURCE_XML_RES);
                
                if (in != null) {
                    if (_logger.isDebugEnabled()) {
                        _logger.debug("Resolved entity \"" + publicId + "\" to \"" + SAS_RESOURCE_XML_RES + "\"");
                    }
                    return new InputSource(in);
                } else {
                    _logger.error("sas-resource.dtd resource is not available");
                    return null;
                } 
            }else if (publicId.equals(CAS_RESOURCE_XML_PUBLIC_ID)) {
                InputStream in = this.getClass().getResourceAsStream(CAS_RESOURCE_XML_RES);
                
                if (in != null) {
                    if (_logger.isDebugEnabled()) {
                        _logger.debug("Resolved entity \"" + publicId + "\" to \"" + CAS_RESOURCE_XML_RES + "\"");
                    }
                    return new InputSource(in);
                } else {
                    _logger.error("cas-resource.dtd resource is not available");
                    return null;
                } 
            }else if(publicId.equals(SAS_SOA_XML_PUBLIC_ID)) {
				InputStream in = this.getClass().getResourceAsStream(SAS_SOA_XML_RES);

				if (in != null) {
					if (_logger.isDebugEnabled()) {
						_logger.debug("Resolved entity \"" + publicId + "\" to \"" +SAS_SOA_XML_RES+"\"");
					}
					return new InputSource(in);
				} else {
					_logger.error("sas-soa.dtd resource is not available");
					return null;
				}
			}
        } catch (Exception e) {
            _logger.error(e.toString(), e);
        }
        return null;
    }        

}
