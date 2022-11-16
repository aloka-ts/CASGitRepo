/*******************************************************************************
 *   Copyright (c) 2014 Agnity, Inc. All rights reserved.
 *   
 *   This is proprietary source code of Agnity, Inc. 
 *   
 *   Agnity, Inc. retains all intellectual property rights associated 
 *   with this source code. Use is subject to license terms.
 *   
 *   This source code contains trade secrets owned by Agnity, Inc.
 *   Confidentiality of this computer program must be maintained at 
 *   all times, unless explicitly authorized by Agnity, Inc.
 *******************************************************************************/
/*
 * SipXmlEntityResolver.java
 *
 * Created on August 6, 2004, 4:51 PM
 */
package com.baypackets.sas.ide.editor.model;

import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import com.baypackets.sas.ide.SasPlugin;

import java.io.InputStream;


/**
 * An instance of this class is registered with an XML parser to resolve
 * any entities found in the "sip.xml" and "sas.xml" deployment descriptor
 * files.
 */
public class XMLEntityResolver implements EntityResolver {
    
	
	public static final String SIP_QUALIFIED_NAME = "sip-app".intern();
	
	public static final String SIP_SYSTEM_ID = "http://www.jcp.org/dtd/sip-app_1_0.dtd";
	
	public static final String SIP289_SYSTEM_ID = "http://www.jcp.org/xml/ns/sipservlet/sip-app_1_1.xsd";
	
	//added by reeta
    public static final String WEB_QUALIFIED_NAME = "web-app".intern();
	
	public static final String WEB_SYSTEM_ID = "http://java.sun.com/dtd/web-app_2_3.dtd";
	//
	
	public static final String SAS_QUALIFIED_NAME = "sas-app".intern();
	
	public static final String SAS_SYSTEM_ID = "http://www.baypackets.com/dtds/sas-app_1_0.dtd";
	
    public static final String CAS_QUALIFIED_NAME = "cas-app".intern();
	
	public static final String CAS_SYSTEM_ID = "http://www.baypackets.com/dtds/cas-app_1_0.dtd";
	
    public static final String SOA_QUALIFIED_NAME = "soa".intern();
	
	public static final String SOA_SYSTEM_ID = "http://www.baypackets.com/dtds/sas-soa.dtd";
	
	
	
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
    
    //added by reeta
    /**
     * Public ID of WEB deployment descriptor.
     */
    public static final String WEB_XML_PUBLIC_ID = 
        "-//Sun Microsystems, Inc.//DTD Web Application 2.3//EN";
    
    //added by reeta
    
    /**
     * Public ID of SOA deployment descriptor.
     */
    public static final String SOA_XML_PUBLIC_ID = 
            "-//Baypackets SIP Application Server//DTD SOA Descriptor//EN";
    
    /**
     * Public ID of SAS Resource deployment descriptor.
     */
    public static final String SAS_RESOURCE_XML_PUBLIC_ID = 
            "-//Baypackets SIP Application Server//DTD SAS Resource Descriptor//EN";
    
    /** 
     * Absolute name of the SIP servlet DTD resource in the CLASSPATH.
     */
    public static final String SIP_XML_RES =
            "/javax/servlet/sip/resources/sip-app_1_0.dtd";
    
    /** 
     * Absolute name of the SIP servlet DTD resource in the CLASSPATH.
     */
    public static final String SIP289_XML_RES =
            "/javax/servlet/sip/resources/sip-app_1_1.xsd";
    
    //added by reeta
    /** 
     * Absolute name of the Http servlet DTD resource in the CLASSPATH.
     */
    public static final String WEB_XML_RES =
            "/javax/servlet/resources/web-app_2_3.dtd";
    
    //added by reeta
    
    /**
     * Absolute name of the SOA descriptor DTD resource in the CLASSPATH.
     */
    public static final String SOA_XML_RES = 
            "/com/baypackets/ase/container/schemas/sas-soa.dtd";

    
    /**
     * Absolute name of the SAS descriptor DTD resource in the CLASSPATH.
     */
    public static final String SAS_XML_RES = 
            "/com/baypackets/ase/container/schemas/sas-app_1_0.dtd";
    
    /**
     * Absolute name of the SAS descriptor DTD resource in the CLASSPATH.
     */
    public static final String CAS_XML_RES = 
            "/com/baypackets/ase/container/schemas/cas-app_1_0.dtd";

    /**
     * Absolute name of the SAS descriptor DTD resource in the CLASSPATH.
     */
    public static final String SAS_RESOURCE_XML_RES = 
            "/com/baypackets/ase/container/schemas/sas-resource.dtd";
    
    /**
     * Resolves the entity specified by the given public ID. 
     */
    public InputSource resolveEntity(String publicId, String systemId) {
        try {
        	SasPlugin.getDefault().log("publicId is "+publicId +" SystemId "+systemId);       
        	
            if (publicId.equals(SIP_XML_PUBLIC_ID)) {
                InputStream in = this.getClass().getResourceAsStream(SIP_XML_RES);
                    
                if (in != null) {
        //            System.out.println("Resolved entity \"" + publicId + "\" to \"" + SIP_XML_RES + "\"");
                    return new InputSource(in);
                } else {
                	SasPlugin.getDefault().log("sip-app_1_0.dtd resource is not available");                        
                    return null;
                }
            } if (systemId.equals(SIP289_SYSTEM_ID)) {
                InputStream in = this.getClass().getResourceAsStream(SIP289_XML_RES);
                    
                if (in != null) {
        //            System.out.println("Resolved entity \"" + publicId + "\" to \"" + SIP_XML_RES + "\"");
                    return new InputSource(in);
                } else {
                	SasPlugin.getDefault().log("sip-app_1_1.xsd resource is not available");                        
                    return null;
                }
            }  else if(publicId.equals(WEB_XML_PUBLIC_ID)){ // else if added by reeta
                   InputStream in = this.getClass().getResourceAsStream(WEB_XML_RES);
                
                if (in != null) {
          //      	System.out.println("Resolved entity \"" + publicId + "\" to \"" + WEB_XML_RES + "\"");
                    return new InputSource(in);
                } else {
                	SasPlugin.getDefault().log("web-app_2_3.dtd resource is not available");
                    return null;
                }
            	
            } else if(publicId.equals(SOA_XML_PUBLIC_ID)){ // else if added by reeta
                   InputStream in = this.getClass().getResourceAsStream(SOA_XML_RES);
                
                if (in != null) {
//                	System.out.println("Resolved entity \"" + publicId + "\" to \"" + SOA_XML_RES + "\"");
                    return new InputSource(in);
                } else {
                	SasPlugin.getDefault().log("sas-soa.dtd resource is not available");
                    return null;
                }
            	
            }else if (publicId.equals(SAS_XML_PUBLIC_ID)) {
            
                InputStream in = this.getClass().getResourceAsStream(SAS_XML_RES);
                
                if (in != null) {
//                	System.out.println("Resolved entity \"" + publicId + "\" to \"" + SAS_XML_RES + "\"");
                    return new InputSource(in);
                } else {
                	SasPlugin.getDefault().log("sas-app_1_0.dtd resource is not available");
                    return null;
                }
             } else if (publicId.equals(CAS_XML_PUBLIC_ID)) {
                    
                    InputStream in = this.getClass().getResourceAsStream(CAS_XML_RES);
                    
                    if (in != null) {
//                    	System.out.println("Resolved entity \"" + publicId + "\" to \"" + SAS_XML_RES + "\"");
                        return new InputSource(in);
                    } else {
                    	SasPlugin.getDefault().log("cas-app_1_0.dtd resource is not available");
                        return null;
                    }
            } else if (publicId.equals(SAS_RESOURCE_XML_PUBLIC_ID)) {
                InputStream in = this.getClass().getResourceAsStream(SAS_RESOURCE_XML_RES);
                
                if (in != null) {
                	SasPlugin.getDefault().log("Resolved entity \"" + publicId + "\" to \"" + SAS_RESOURCE_XML_RES + "\"");
                    return new InputSource(in);
                } else {
                	SasPlugin.getDefault().log("sas-resource.dtd resource is not available");
                    return null;
                } 
            }
        } catch (Exception e) {
        	SasPlugin.getDefault().log("Exception thrown resolveEntity() XMLEntityResolver.java..."+e);
        }
        return null;
    }        

}
