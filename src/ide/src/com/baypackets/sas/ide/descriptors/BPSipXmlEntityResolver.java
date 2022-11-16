package com.baypackets.sas.ide.descriptors;


import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;

import com.baypackets.sas.ide.SasPlugin;

import java.io.InputStream;


/**
 * An instance of this class is registered with an XML parser to resolve
 * any entities found in the "sip.xml" and "sas.xml" deployment descriptor
 * files.
 */
public class BPSipXmlEntityResolver implements EntityResolver {
    
    
    
    /**
     * Public ID of the SIP Servlet DTD.  This should appear in all "sip.xml" 
     * files and must be resolved before validation.
     */
    public static final String SIP_XML_PUBLIC_ID =
            "-//Java Community Process//DTD SIP Application 1.0//EN";

    public static final String WEB_XML_PUBLIC_ID =
        "-//Sun Microsystems, Inc.//DTD Web Application 2.3//EN";
    
    
    /**
     * Public ID of SAS deployment descriptor.
     */
    public static final String SAS_XML_PUBLIC_ID = 
            "-//Baypackets SIP Application Server//DTD SAS Descriptor//EN";
    
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
    
    public static final String WEB_XML_RES =
        "/javax/servlet/resources/web-app_2_3.dtd";
    
    
    
    /**
     * Absolute name of the SAS descriptor DTD resource in the CLASSPATH.
     */
    public static final String SAS_XML_RES = 
            "/com/baypackets/ase/container/schemas/sas-app_1_0.dtd";

    /**
     * Absolute name of the SAS descriptor DTD resource in the CLASSPATH.
     */
    public static final String SAS_RESOURCE_XML_RES = 
            "/com/baypackets/ase/container/schemas/sas-resource.dtd";
    
    /**
     * Resolves the entity specified by the given public ID. 
     */
    public InputSource resolveEntity(String publicId, String systemId) 
    {
        try 
        {
            if (publicId.equals(SIP_XML_PUBLIC_ID)) 
            {
                InputStream in = this.getClass().getResourceAsStream(SIP_XML_RES);
                    
                if (in != null) 
                {
                    
                    return new InputSource(in);
                } 
                else 
                	return null;
                
            } 
        	else if (publicId.equals(SAS_XML_PUBLIC_ID)) 
        	{
                InputStream in = this.getClass().getResourceAsStream(SAS_XML_RES);
                
                if (in != null) 
                {
                    return new InputSource(in);
                } 
                else 
                {
                    return null;
                }
            } 
        	else if (publicId.equals(SAS_RESOURCE_XML_PUBLIC_ID)) 
            {
                InputStream in = this.getClass().getResourceAsStream(SAS_RESOURCE_XML_RES);
                
                if (in != null) 
                {
                    
                    return new InputSource(in);
                } 
                else 
                {
                   
                    return null;
                } 
            }
        
        	else if(publicId.equals(WEB_XML_PUBLIC_ID))
        	{
        		InputStream in = this.getClass().getResourceAsStream(WEB_XML_RES);
            
        		if (in != null) 
        		{
                
        			return new InputSource(in);
        		} 
        		else 
        			return null;
            
        	
        	}
            
        }
         catch(Exception e)
        {
        	SasPlugin.getDefault().log("Exception thrown resolveEntity() BPSipXmlEntityResolver.java..."+e);
        }
    	
        return null;
    }        

}
