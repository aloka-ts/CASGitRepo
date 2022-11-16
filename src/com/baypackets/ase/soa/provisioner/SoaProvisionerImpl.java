//***********************************************************************************
//GENBAND, Inc. Confidential and Proprietary
//
//This work contains valuable confidential and proprietary
//information.
//Disclosure, use or reproduction without the written authorization of
//GENBAND, Inc. is prohibited. This unpublished work by GENBAND, Inc.
//is protected by laws of United States and other countries.
//If publication of work should occur the following notice shall
//apply:
//
//"Copyright 2007 GENBAND, Inc. All right reserved."
//***********************************************************************************


//***********************************************************************************
//
//      File:   SoaProvisionerImpl.java
//
//      Desc:   This file implements com.baypackets.ase.soa.provisioner.SoaProvisioner interface
//
//      Author                          Date            Description
//      ----------------------------------------------------------------------
//      Suresh Kr. Jangir               17/12/07        Initial Creation
//
//***********************************************************************************


package com.baypackets.ase.soa.provisioner;

import org.apache.log4j.Logger;

import java.net.URI;
import java.net.URL;
import java.util.Map;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;

import java.io.*;
import java.util.zip.ZipInputStream;
//import java.io.BufferedOutputStream;
//import java.io.FileOutputStream;
//import java.io.OutputStream;
//import java.io.InputStream;
import java.net.URLConnection;

import javax.xml.stream.*;
import javax.xml.stream.events.*;
import javax.xml.stream.XMLInputFactory;



import com.baypackets.ase.soa.codegenerator.CodeGenerator;
import com.baypackets.ase.soa.fw.SoaFrameworkContext;
import com.baypackets.ase.soa.ServiceMap;
import com.baypackets.ase.soa.SoaContextImpl;
import com.baypackets.ase.soa.iface.SoaContext;
import com.baypackets.ase.soa.common.SoaConstants;
import com.baypackets.ase.soa.common.WebServiceDataObject;
import com.baypackets.ase.soa.common.AseSoaService;
import com.baypackets.ase.soa.exceptions.SoaException;
//import com.baypackets.ase.soa.util.SoaUtils;
//import com.baypackets.ase.soa.util.JibxDataBinder;

import com.baypackets.ase.common.Registry;
import com.baypackets.ase.util.AseStrings;
import com.baypackets.ase.util.Constants;
import com.baypackets.ase.util.FileUtils;
import com.baypackets.ase.util.exceptions.FileCopyException;
import com.baypackets.ase.container.exceptions.FinderException;
import com.baypackets.ase.spi.deployer.DeployableObject;
import com.baypackets.ase.container.AppClassLoader;

import com.baypackets.bayprocessor.slee.common.ConfigRepository;




/**
 * SoaProvisioner is the abstraction for provisioning the remote services in the M5 SAS container.
 * It generates the code using the Code generator, creates the proxy and stubs and 
 * register them in the service map. 
 * After adding it in the Service Map the service becomes available to all the service 
 * or application deployed on the M5 SAS Container. 
 *
 * @author Suresh Kr. Jangir
 */

public class SoaProvisionerImpl implements SoaProvisioner {

	private static Logger m_logger = Logger.getLogger(SoaProvisionerImpl.class);

	private static final String NAME_SCHEME_FILE = "file";
	private static final String NAME_SCHEME_HTTP = "http";

	private List<AseRemoteService> remoteServices = new ArrayList<AseRemoteService>();
	private List<AseRemoteService> remoteServicesRepository = null;
	private SoaProvisionerDAO provisionerDAO = null;
	private CodeGenerator m_codeGenerator = null;
	private SoaFrameworkContext m_fwContext = null;
	private ServiceMap m_serviceMap = null;
	private ProvisionerAdaptor m_provisionerAdaptor = null;
	private ConfigRepository m_configRep = null;
	private Map<String, String> paramMap = new HashMap<String, String>();
	private File unpackedDir = null;
	private String serviceName = null;

	
	public void initialize() {

		if(m_logger.isDebugEnabled()) {
			m_logger.debug("Initializing the SOA Provisioner");
		}
		provisionerDAO = ProvisionerDAOFactory.getInstance().
					getSoaProvisionerDAO(SoaProvisionerDAO.FILE_BASED);
		try {
			remoteServicesRepository = provisionerDAO.loadRemoteServices();
			//remoteServices = provisionerDAO.loadRemoteServices();
		} catch(FinderException exp) {
			m_logger.error("Unable to load Remote Services:", exp);
		} catch(Exception e) {
			m_logger.error("Caught Exception while loading remote Services",e);
		}

		m_fwContext = (SoaFrameworkContext)Registry.lookup(Constants.NAME_SOA_FW_CONTEXT);
		m_configRep = (ConfigRepository)Registry.lookup(Constants.NAME_CONFIG_REPOSITORY);
		m_codeGenerator = m_fwContext.getCodeGenerator();
		m_serviceMap = m_fwContext.getServiceMap();
		
		boolean provisioningEnabled = false;
		try {
			provisioningEnabled = Boolean.valueOf(m_configRep.
										getValue(SoaConstants.PROP_PROVISION_REMOTE_SERVICE)).booleanValue();
			if(provisioningEnabled) {
				m_provisionerAdaptor = new ProvisionerAdaptor();
			}
		} catch(Exception e) {
			m_logger.error("Unable to read Provisioning flag",e);
		}
		
	}

	public void start() {
		
		if(m_logger.isDebugEnabled()) {
			m_logger.debug("Starting the SOA Provisioner");
		}
		if(m_provisionerAdaptor != null) {
			m_provisionerAdaptor.start();
		}

		for (AseRemoteService service : remoteServicesRepository) {
			try {
				this.addRemoteService(service.getServiceName(),service.getVersion(), service.getWsdlUri());
			} catch(SoaException e) {
				m_logger.error("Unable to add remote Service",e);
			}catch(Throwable thr)	{
				m_logger.error("Unable to add remote Service",thr);
			}
		}
	}

	/**
	* Adding new version of addRemoteService() method to accept jar file instead of wsdl URL
	*
	public void addRemoteService(String name, String version, URI jarUri) throws SoaException {
                                                                                                                          
            if(m_logger.isDebugEnabled()) {
                m_logger.debug("Entering addRemoteService(): Name = "+name);
                m_logger.debug("Entering addRemoteService(): version = "+version);
                m_logger.debug("Entering addRemoteService(): URI = "+jarUri);
            }
                                                                                                                          
            if(this.getRemoteService(name) != null) {
                m_logger.debug("Service with same name already added");
                throw new SoaException("Service with name"+name+" already added");
            }
                                                                                                                          
            // Reading the file
            unpackedDir = this.getUnpackDir();
            URI extractedUri = this.extractFile(name,version, jarUri);
            if(extractedUri == null) {
                m_logger.error("Unable to copy jar file in archives folder for the service "+name);
                throw new SoaException("Unable to copy jar file in archives folder for the service "+name);
            }
			// Extract supplied jar file containing service interface classes into WEB-INF/classes dir.
			File classesDir = null;
        	String classesPath = "classes";
        	classesPath = "WEB-INF/" + classesPath;
			try	{
				classesDir = new File(unpackedDir,classesPath);
				URL jarFileUrl = extractedUri.toURL();
				if(m_logger.isDebugEnabled())	{
					m_logger.debug("Jar file URL: "+jarFileUrl);
				}
				InputStream tempStream = jarFileUrl.openStream();
        		m_logger.debug("TEMP STREAM: av = "+tempStream.available());
        		ZipInputStream zipStream = new ZipInputStream(tempStream);
        		boolean extracted = FileUtils.extract(zipStream, classesDir);
				if(m_logger.isDebugEnabled())	{
					m_logger.debug("Jar file is successfully extracted to WEB-INF/classes directory");
				}
			}catch(Exception e)	{
				 m_logger.error("Unable to extract jar file for the service "+name);
				throw new SoaException("Unable to extract jar file for the service "+name);
			}
			//Now parse service.xml located in WEB-INF/classes directory to extract service URI
			// and service interface values
			String serviceXml = classesDir.getAbsolutePath() + "/service.xml";
                                                                                                                          
			//create new AseSoaService and invoke setServiceName(),setRemoteServiceUri() & setServiceApi() on it.
			// Add this object to WebServiceDataObject by calling addService(AseSoaService) on it.
			// Set WebServiceDataObject on Soacontext 
            try {
				AseSoaService soaService = parseServiceXML(serviceXml);
				soaService.setServiceName(name);
                WebServiceDataObject dataObj = new WebServiceDataObject();
                dataObj.setUnpackedDir(this.unpackedDir.getAbsolutePath());
                //dataObj.parseWsdl(jarUri.toString());
                dataObj.setName(name);
				//Add SoaService to WebServiceDataObject
				dataObj.addService(soaService);
                SoaContextImpl soaContext = (SoaContextImpl)m_fwContext.createSoaContext(name, paramMap);
                soaContext.setWebServiceDataObject(dataObj);
                AseRemoteService remoteService = this.getRemoteService(name);
                if(remoteService == null) {
                    remoteService = new AseRemoteService(name, version, extractedUri);
                    remoteServices.add(remoteService);
                }
                String destUri = this.unpackedDir.getAbsolutePath();
                Map generatedObj = this.m_codeGenerator.generateCode(destUri,
                                                extractedUri.toString(),
                                                SoaConstants.OPERATION_PROVISION,
                                                SoaConstants.WS_TYPE_SERVICE,
                                                dataObj.getName(),
                                                soaService.getServiceName(),
                                                this.getClassLoader());
				URI serviceUri = new URI(soaService.getRemoteServiceUri());
                //URI serviceUri = new URI(soaService.getServiceUri());
                remoteService.addServiceUri(serviceUri);
                m_serviceMap.addService(serviceUri, generatedObj.get(SoaConstants.PROXY_REMOTE_SERVICE));
                m_serviceMap.activateService(serviceUri);
                remoteService.setVersion(version);
                remoteService.setWsdlUri(extractedUri);
                provisionerDAO.persistRemoteService(remoteService);
            } catch (Exception exp) {
                m_logger.error("Unable to add Remote Service",exp);
                throw new SoaException(exp.getMessage());
            }
                                                                                                                          
            if(m_logger.isDebugEnabled()) {
                m_logger.debug("Leaving addRemoteService(): Name = "+name);
            }
    }
	*/	
	/*
	* This method accepts service name, service version and WSDL file URL as arguments.
	*/
	public void addRemoteService(String name, String version, URI wsdlUri) throws SoaException {
	
			if(m_logger.isDebugEnabled()) {
				m_logger.debug("Entering addRemoteService(): Name = "+name);
			}
			serviceName = name;

			if(this.getRemoteService(name) != null) {
				if(m_logger.isDebugEnabled())
				m_logger.debug("Service with same name already added");
				throw new SoaException("Service with name"+name+" already added");
			}
			try {

				// Reading the file
				unpackedDir = this.getUnpackDir();
				String wsdlFileName = name +".wsdl";
				//URI extractedUri = this.extractFile(name,version, wsdlUri);
				URI extractedUri = this.extractFile(wsdlFileName,version, wsdlUri);
				if(extractedUri == null) {
					m_logger.error("Unable to extract WSDL for the service "+name);
					throw new SoaException("Unable to extract WSDL for the service "+name);
				}
				File archivesDir = new File(this.getBaseUriString(),name+"_"+version);
				WebServiceDataObject dataObj = new WebServiceDataObject();
				dataObj.setArchivesDir(archivesDir.getAbsolutePath());
				dataObj.setUnpackedDir(this.unpackedDir.getAbsolutePath());
				dataObj.setName(name);
				dataObj.parseWsdl(wsdlUri.toString());
				SoaContextImpl soaContext = (SoaContextImpl)m_fwContext.createSoaContext(name, paramMap);
				soaContext.setWebServiceDataObject(dataObj);
				AseRemoteService remoteService = this.getRemoteService(name);
				if(remoteService == null) {
					remoteService = new AseRemoteService(name, version, extractedUri);
					remoteServices.add(remoteService);
				}
				Iterator<URI> imports = dataObj.getImportedUris().iterator();
				URI importedUri = null;
				String scheme = null;
				String query = null;
            	String fileName = null;
				while(imports.hasNext()) {
					importedUri = imports.next();
					if(m_logger.isDebugEnabled())
					m_logger.debug("Imported URI: " + importedUri);
					scheme = importedUri.getScheme();
					if(m_logger.isDebugEnabled())
					m_logger.debug("scheme: " + scheme);
					if(scheme.equals(AseStrings.PROTOCOL_HTTP)) {
            			query = wsdlUri.getQuery();
						if(m_logger.isDebugEnabled())
						m_logger.debug("query: " + query);
            			if(query != null)   {
                			fileName = query.substring(query.indexOf(AseStrings.EQUALS)+1);
            			}
						if(m_logger.isDebugEnabled())
						m_logger.debug("fileName: " + fileName);

					this.extractFile(fileName,version,importedUri);
					}
				}
				for(Iterator<AseSoaService> it = dataObj.getServices() ; it.hasNext();) {
					AseSoaService soaService = it.next();
					String destUri = this.unpackedDir.getAbsolutePath();
					Map generatedObj = this.m_codeGenerator.generateCode(destUri,
												extractedUri.toString(),
												SoaConstants.OPERATION_PROVISION,
												SoaConstants.WS_TYPE_SERVICE,
												dataObj.getName(),
												soaService.getServiceName(),
												this.getClassLoader());
					URI serviceUri = new URI(soaService.getServiceUri());
					remoteService.addServiceUri(serviceUri);
					m_serviceMap.addService(serviceUri, generatedObj.get(SoaConstants.PROXY_REMOTE_SERVICE));
					m_serviceMap.activateService(serviceUri);
				}
				if(m_logger.isDebugEnabled()) {
					m_logger.debug("Persist remote service with: version = "+version+" , WSDLURI= "+extractedUri);
				}
				remoteService.setVersion(version);
				remoteService.setWsdlUri(extractedUri);
				provisionerDAO.persistRemoteService(remoteService);
			} catch (Exception exp) {
				m_logger.error("Unable to add Remote Service",exp);
				throw new SoaException(exp.getMessage());
			}catch(Throwable thr)	{
				m_logger.error("Unable to add Remote Service",thr);
			}
			
			if(m_logger.isDebugEnabled()) {
				m_logger.debug("Leaving addRemoteService(): Name = "+name);
			}
	}


	
	public void updateRemoteService(String name, String version, URI wsdlUri) throws SoaException {
	
			if(m_logger.isDebugEnabled()) {
				m_logger.debug("Entering updateRemoteService(): Name = "+name);
			}
			serviceName = name;
			String wsdlFileName = name+".wsdl";
			AseRemoteService service = this.getRemoteService(name);
			if(service == null) {
				m_logger.error("Unable to get service with name "+name);
				throw new SoaException("Unable to get service with name "+name);
			}
			try {
				// Reading the file
				unpackedDir = this.getUnpackDir();
				URI extractedUri = this.extractFile(wsdlFileName, version, wsdlUri);
				if(extractedUri == null) {
					m_logger.error("Unable to extract WSDL for the service "+name);
					throw new SoaException("Unable to extract WSDL for the service "+name);
				}
				// Servie URI may change so clear them in AseRemoteService
				service.clearServiceUris();
				WebServiceDataObject dataObj = new WebServiceDataObject(wsdlUri);
				dataObj.setName(name);
				SoaContextImpl soaContext = (SoaContextImpl)m_fwContext.createSoaContext(name, paramMap);
				soaContext.setWebServiceDataObject(dataObj);
				Iterator<URI> imports = dataObj.getImportedUris().iterator();
				while(imports.hasNext()) {
					this.extractFile(name,version,imports.next());
				}
				for(Iterator<AseSoaService> it = dataObj.getServices() ; it.hasNext();) {
					AseSoaService soaService = it.next();
					String destUri = this.unpackedDir.getAbsolutePath();
					Map generatedObj = m_codeGenerator.generateCode(destUri, 
														extractedUri.toString(),	
														SoaConstants.OPERATION_PROVISION,
														SoaConstants.WS_TYPE_SERVICE,
														dataObj.getName(),
														soaService.getServiceName(),
														this.getClassLoader());
					URI serviceUri = new URI(soaService.getServiceUri());
					service.addServiceUri(serviceUri);
					m_serviceMap.upgradeService(serviceUri, generatedObj.get(SoaConstants.PROXY_REMOTE_SERVICE));
					m_serviceMap.activateService(serviceUri);
				}
				service.setVersion(version);
				service.setWsdlUri(extractedUri);
				provisionerDAO.persistRemoteService(service);
			} catch(Exception exp) {
				m_logger.error("Unable to update Remote Service",exp);
				throw new SoaException(exp.getMessage());
			}catch(Throwable thr)	{
				m_logger.error("Unable to update Remote Service",thr);
			}
			if(m_logger.isDebugEnabled()) {
				m_logger.debug("Leaving updateRemoteService(): Name = "+name);
			}
	}	

	public void removeRemoteService(String name) {
		
		if(m_logger.isDebugEnabled()) {
			m_logger.debug("Entering removeRemoteService(): Name = "+name);
		}
		serviceName = name;
		String wsdlFileName = name+".wsdl";

		AseRemoteService service = this.getRemoteService(name);
		try	{
			if(service != null) {
				for (URI uri : service.getServiceUris()) {
					m_serviceMap.removeService(uri);
				}
				provisionerDAO.removeRemoteService(name);	
				File fileInAppArchive = new File(this.getBaseUriString(),name+"_"+ service.getVersion());
				try {
					FileUtils.delete(fileInAppArchive);
				}catch (Exception e) {
					if(m_logger.isDebugEnabled())
					m_logger.debug(e.getMessage(), e);
				}
				remoteServices.remove(service);
			}
		}catch(Throwable thr)	{
			m_logger.error("Unable to remove service: ", thr);
		}
		if(m_logger.isDebugEnabled()) {
			m_logger.debug("Exiting removeRemoteService(): Name = "+name);
		}
				
	}
	
	/*
	* name is name of wsdl/xsd file to be extracted in archives directory.
	* version is service version.
	* wsdlUri stores URI of wsdl file.
	*/
	private URI extractFile(String name, String version, URI wsdlUri) {
		if(m_logger.isDebugEnabled()) {
		m_logger.debug("File to be extracted = "+name);
		m_logger.debug("WSDL URI = "+wsdlUri);
		m_logger.debug("WSDL PATH = "+wsdlUri.getPath());
		}
		String scheme = wsdlUri.getScheme();
		URI resolvedFileUri = null;
		File destionDir = new File(this.getBaseUriString(),serviceName+"_"+version);
		//make unpacked**/WEB-INF/ dir structure
		File webInf = new File(unpackedDir, "WEB-INF");
		File genDir = new File(unpackedDir, "gen");
		File classes = new File(webInf, "classes");
		File wsdl = new File(webInf, "wsdl");
		webInf.mkdirs();
		classes.mkdirs();
		wsdl.mkdirs();
		genDir.mkdirs();	
		 if(m_logger.isDebugEnabled())
		m_logger.debug("Destination Directory = "+destionDir);
		if(! destionDir.exists()) {
			if(m_logger.isDebugEnabled()) {
				m_logger.debug("Creating directory "+destionDir);
			}
			destionDir.mkdirs();
		}else	{
			if(m_logger.isDebugEnabled()) {
                m_logger.debug("Destination directory "+destionDir +" already exists");
            }
		}

		if(m_logger.isDebugEnabled()) {
			m_logger.debug("Extracting the WSDL scheme = "+scheme);
		}

		if(scheme.equals(NAME_SCHEME_FILE)) {
			try {
				File wsdlFile = new File(wsdlUri.getPath());
				//if this file already exists in destionDir directory, don't copy it again
				File temp = new File(destionDir,name);
				File resolvedFile = null;
				if(!temp.exists())	{
					resolvedFile = FileUtils.copy(wsdlFile, destionDir);
					resolvedFileUri = resolvedFile.toURI();
				}else	{
					resolvedFileUri = wsdlUri;
				}
				//Copy this file under /WEB-INF/wsdl directory
				FileUtils.copy(wsdlFile, wsdl);
			} catch (FileCopyException exp) {
				m_logger.error("Unable to copy the file from "+wsdlUri,exp);
			}
		} else if(scheme.equals(NAME_SCHEME_HTTP)) {
			OutputStream out = null;
			URLConnection conn = null;
			InputStream in = null;
			try {
				if(m_logger.isDebugEnabled())
				m_logger.debug("Name of file being extracted: " + name);
				String destwsdl = destionDir.getPath().concat("/"+name);
				out = new BufferedOutputStream( new FileOutputStream(destwsdl));
				conn = wsdlUri.toURL().openConnection();
				in = conn.getInputStream();
				byte[] buffer = new byte[1024];
				int numRead;
				long numWrite = 0;
				while(( numRead = in.read(buffer)) != -1) {
					out.write(buffer,0,numRead);
					numWrite += numRead;
				}
				resolvedFileUri = new File(destwsdl).toURI();
			} catch (Exception exp) {
				m_logger.error("Unable to download file from "+wsdlUri, exp);
			} finally {
				try {
					if(in != null) {
						in.close();
					}
					if(out != null) {
						out.close();
					}
				} catch(Exception e) {
					m_logger.error("Unable to close Streams",e);
				}
			}
		}
		if(m_logger.isDebugEnabled()) {
			m_logger.debug("Exiting extractFile(): Resolved URI = "+resolvedFileUri);
		}
		return resolvedFileUri;
	}
	
	/*private URI getServiceUri(String name, URI uri) {
		WebServiceDataObject obj = new WebServiceDataObject(uri);
		AseSoaService service = obj.getService(name);
		URI returi = null;
		try {
			returi = new URI(service.getServiceUri());
		} catch (Exception e) {
			m_logger.error(e.getMessage(),e);
		}
		return returi;
	}*/	

	private File getUnpackDir() {
		Long time = System.currentTimeMillis();
		unpackedDir = new File(Constants.ASE_HOME, "tmp/unpacked_" + time.toString());
		if (!unpackedDir.exists()) {
			unpackedDir.mkdirs();
		}
		return unpackedDir;
	}

	private ClassLoader getClassLoader() { 
		AppClassLoader classLoader = new AppClassLoader(this.getClass().getClassLoader());
		URL url = null;
		try {
			url = this.unpackedDir.toURL();
		}catch(Exception e) {
			if(m_logger.isDebugEnabled())
			m_logger.debug(e.getMessage(), e);
		}

		if(m_logger.isDebugEnabled()){
			m_logger.debug("Adding URL to ClassLoader :"  + url );
		}
		classLoader.addRepository(url);
		//Add the Classes directory to the Class Loader's Class Path
        String classesDir = "classes";
        classesDir = "WEB-INF/" + classesDir;
        try {
			url = new File(unpackedDir, classesDir).toURL();
		}catch (Exception e) {
			if(m_logger.isDebugEnabled())
			m_logger.debug(e.getMessage(), e);
		}

        if(m_logger.isDebugEnabled()){
           m_logger.debug("Adding URL to ClassLoader :"  + url );
        }
		classLoader.addRepository(url);
		return classLoader;	
	}

	private String getBaseUriString() {
		return Constants.ASE_HOME+"/apps/archives/";
	}

	private AseRemoteService getRemoteService(String name) {
		for (AseRemoteService tmp : remoteServices) {
			if(tmp.getServiceName().equals(name)) {
				return tmp;
			}
		}

		return null;
	}

	private AseSoaService parseServiceXML(String serviceXml) throws Exception	{
		AseSoaService soaService = new AseSoaService();	
		String serviceInterface = null;
		String serviceUri = null;
		String elementName = null;
		if(m_logger.isDebugEnabled())	{
			m_logger.debug("Path of service.xml file:  " + serviceXml);
		}
		try {
			XMLInputFactory inputFactory=XMLInputFactory.newInstance();
			InputStream input=new FileInputStream(new File(serviceXml));
			XMLStreamReader  xmlStreamReader = inputFactory.createXMLStreamReader(input);

			while(xmlStreamReader.hasNext())	{
			int event=xmlStreamReader.next();
			if(event==XMLStreamConstants.START_DOCUMENT){
				if(m_logger.isDebugEnabled())
				m_logger.debug("Event Type:START_DOCUMENT");
			}
			if(event==XMLStreamConstants.START_ELEMENT){
				if(m_logger.isDebugEnabled())
				m_logger.debug("Event Type: START_ELEMENT");
				elementName = xmlStreamReader.getLocalName();
				if(m_logger.isDebugEnabled())
				m_logger.debug("Element Name: "+ elementName);
				if(elementName.equals("interface"))	{
					xmlStreamReader.next();
					serviceInterface = xmlStreamReader.getText();
				}
				if(elementName.equals("uri"))	{
					xmlStreamReader.next();
					serviceUri = xmlStreamReader.getText();
				}

			}

			}// while ends here
		}catch(FactoryConfigurationError e){
			if(m_logger.isDebugEnabled())
			m_logger.debug("FactoryConfigurationError"+e.getMessage());
			throw e;
		}catch(XMLStreamException e){
			if(m_logger.isDebugEnabled())
			m_logger.debug("XMLStreamException"+e.getMessage());
			throw e;
		}catch(IOException e){
			if(m_logger.isDebugEnabled())
			m_logger.debug("IOException"+e.getMessage());
			throw e;
		}
		soaService.setServiceApi(serviceInterface);
		soaService.setRemoteServiceUri(serviceUri);
		return soaService;
	

	}

	public List<AseRemoteService> listServices() {
		return remoteServices;
	}
	
}


