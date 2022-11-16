package com.baypackets.ase.deployer;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.commons.digester.Digester;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;
import com.baypackets.bayprocessor.slee.common.ConfigRepository;
import com.baypackets.ase.common.Registry;
import com.baypackets.ase.container.AppRuleSet;
import com.baypackets.ase.container.SipXmlEntityResolver;
import com.baypackets.ase.router.AseSysApplicationRouter;
import com.baypackets.ase.spi.deployer.DeployableObject;
import com.baypackets.ase.util.AseStrings;
import com.baypackets.ase.util.Constants;

public class SipDDHandler extends DefaultDDHandler {

	public static final String SIP_XML_XSD = "/xsd/sip-app_1_1.xsd";
    
	public SipDDHandler() {
		super();
	}

	public void parse(DeploymentDescriptor dd, DeployableObject deployable) 
							throws Exception{
		
		 ConfigRepository cr = (ConfigRepository)Registry.lookup(Constants.NAME_CONFIG_REPOSITORY);
		 
		 boolean isValidate = false;		 
		 if (cr != null) {	        	
			 String ifvalidate = (String)cr.getValue(Constants.PROP_SIP_DD_VALIDATION);	            
			 if(ifvalidate !=null && !ifvalidate.equals(AseStrings.BLANK_STRING)){	            	
				 if(ifvalidate.trim().equalsIgnoreCase(AseStrings.TRUE_SMALL))
					 isValidate =true;       	  
			 }	            
		 }

		 Digester digester = new Digester();
		 digester.addRuleSet(new AppRuleSet(deployable));
		 digester.setNamespaceAware(true);
		 digester.setValidating(isValidate);
        
		 ByteArrayInputStream bstream = null;
		 
		 if(isValidate) {

			 // Bug 6268
			 InputStream ddStream = dd.getStream();
			 BufferedReader reader = new BufferedReader(new InputStreamReader(ddStream));
			 StringBuilder sb = new StringBuilder();
			 String line = null;
			 while ((line = reader.readLine()) != null) {
				 sb.append(line + "\n");
			 }
			 ddStream.close();
			 
			 String xmlContent = sb.toString();
			 bstream = new ByteArrayInputStream(xmlContent.getBytes()) ;
			 if(xmlContent.contains("DOCTYPE")) {
				 digester.setEntityResolver(new SipXmlEntityResolver());
			 }else {
				 digester.setSchema(Constants.ASE_HOME.concat(SIP_XML_XSD));
			 }

		 }
		 
        digester.setErrorHandler(new DefaultHandler() {
            public void error(SAXParseException e) throws SAXException {
                throw e;
            }
        });
 
        if(bstream != null)
        	digester.parse(new InputSource(bstream));
        else
        	digester.parse(new InputSource(dd.getStream()));
	}
}
