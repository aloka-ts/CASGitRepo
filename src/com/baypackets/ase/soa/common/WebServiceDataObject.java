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
//      File:   WebServiceDataObject.java
//
//      Desc:   This class contains the details provided in the WSDL. 
//
//      Author                          Date            Description
//      ----------------------------------------------------------------------
//      Suresh Kr. Jangir               04/01/08        Initial Creation
//
//***********************************************************************************


package com.baypackets.ase.soa.common;

import java.net.URI;

import org.apache.log4j.Logger;

import java.util.*;
import java.io.*;
import java.net.URLConnection;

import javax.wsdl.factory.WSDLFactory;
import javax.wsdl.xml.WSDLReader;
import javax.wsdl.Port;
import javax.wsdl.Service;
import javax.wsdl.Definition;
import javax.wsdl.extensions.soap.SOAPAddress;
import javax.wsdl.extensions.schema.*;
import javax.wsdl.extensions.*;

import com.baypackets.ase.util.AseStrings;
import com.baypackets.ase.util.FileUtils;
import com.baypackets.ase.util.exceptions.FileCopyException;




public class WebServiceDataObject {

	private static Logger m_logger = Logger.getLogger(WebServiceDataObject.class);
	private String m_name; 
	private URI m_wsdlUri;
	private String unpackedDir;
	private String archivesDir;
	private String m_targetNameSpace;
	private AseSoaApplication m_application;
	private String m_listenerSvcName = null;
	private Map<String, String> attributesMap = new HashMap<String, String>();
	private Map<String, AseSoaService> m_services = new Hashtable<String,AseSoaService>();
	private List<URI> importedUris = new ArrayList<URI>();

	public WebServiceDataObject(URI wsdlUri) {
		this.m_wsdlUri = wsdlUri;
		if(wsdlUri != null) {
			this.parseWsdl();
		}
	}

	public WebServiceDataObject() {

	}

	public String getName() {
		if(m_logger.isDebugEnabled())	{
			m_logger.debug("Returning value of m_name: " + m_name);
		}
		return m_name;
	}

	public void setName(String name) {
		if(m_logger.isDebugEnabled())	{
			m_logger.debug("Setting value of m_name to: " + name);
		}
		m_name = name;
	}

	public Iterator<AseSoaService> getServices() {
		return m_services.values().iterator();
	}

	public AseSoaService getService(String name) {
		return m_services.get(name);
	}

	public void addService(AseSoaService service) {
		m_services.put(service.getServiceName(), service);
	}

	public void addApplication(AseSoaApplication app) {
		m_application = app;
	}

	public AseSoaApplication getApplication() {
		return this.m_application;
	}

	public String getListenerServiceName() {
		return this.m_listenerSvcName;
	}

	public void addAttribute(String key, String val) {
		attributesMap.put(key, val);
	}

	public String getAttributeValue(String key) {
		return attributesMap.get(key);
	}

	public Map<String, String> getAttributeMap() {
		return attributesMap;


	}

	public String getTargetNameSpace() {
		return this.m_targetNameSpace;
	}

	public void setTargetNameSpace(String name) {
		this.m_targetNameSpace = name;
	}

	public List<URI> getImportedUris() {
		return this.importedUris;
	}

	public String getUnpackedDir() {
		return this.unpackedDir;
	}

	public void setUnpackedDir(String st) {
		this.unpackedDir = st;
	}

	public void setArchivesDir(String st) {
		this.archivesDir = st;
	}
	public void parseWsdl(String wsdlPath) {
		if(this.m_wsdlUri != null) {
			throw new IllegalStateException("This object already contains info of WSDL");
		}
		try {
			this.m_wsdlUri = new URI(wsdlPath);
			this.parseWsdl();
		} catch(Exception e) {
			m_logger.error(e.getMessage(),e);
		}
	}
		

	private void parseWsdl() {
		try {
			if(m_logger.isDebugEnabled()) {
				m_logger.debug("Parsing the wsdl at "+this.m_wsdlUri.toString());
			}
			WSDLFactory wsdlfactory = WSDLFactory.newInstance();
			WSDLReader wsdlreader = wsdlfactory.newWSDLReader();
			Definition definition = wsdlreader.readWSDL(this.m_wsdlUri.toString());
			this.m_targetNameSpace = definition.getTargetNamespace();
			Iterator extIt = definition.getTypes().getExtensibilityElements().iterator();
			while(extIt.hasNext()) {
				ExtensibilityElement ee = (ExtensibilityElement)extIt.next();
				if(ee instanceof Schema) {
					Schema schema = (Schema) ee;
					ArrayList allSchemaRefs = new ArrayList();
					Collection ic = schema.getImports().values();
					Iterator importsIterator = ic.iterator();
					while(importsIterator.hasNext()) {
						allSchemaRefs.addAll((Collection)importsIterator.next());
					}
					allSchemaRefs.addAll(schema.getIncludes());
					allSchemaRefs.addAll(schema.getRedefines());
					ListIterator schemaRefIterator = allSchemaRefs.listIterator();
					while(schemaRefIterator.hasNext()) {
						SchemaReference schemaRef = (SchemaReference) schemaRefIterator.next();
						URI location = new URI(schemaRef.getSchemaLocationURI());
						String docBaseStr = schema.getDocumentBaseURI();
						URI baseUri = null;
						URI importedURI = null;
						if(docBaseStr == null) {
							importedURI = location;
						} else {
							baseUri = new URI(docBaseStr);
							importedURI = baseUri.resolve(location);
						}
						this.importedUris.add(importedURI);
						this.extractFile(this.unpackedDir,importedURI);
							
					}
				}
			}
			
			Iterator itor = definition.getServices().values().iterator();
			while(itor.hasNext()) {
				Service service = (Service)itor.next();
				String name = service.getQName().getLocalPart();
				if(this.m_application != null) {
					m_listenerSvcName = name;
					return;
				}
				AseSoaService aseService = this.getService(name);
				if(aseService == null) {
					aseService = new AseSoaService();
				}
				aseService.setServiceName(service.getQName().getLocalPart());
				Iterator ports = service.getPorts().values().iterator();
				while(ports.hasNext()) {
					Port port = (Port)ports.next();
					String locationUri = null;
					Iterator it = port.getExtensibilityElements().iterator();
					while(it.hasNext()) {
						Object el = it.next();
						if(el instanceof SOAPAddress) {
							SOAPAddress add = (SOAPAddress)el;
							locationUri = add.getLocationURI();
							aseService.addPort(port.getName(),port.getBinding().getQName().toString(),locationUri);
						}
					}
				}
				this.m_services.put(aseService.getServiceName(),aseService);
			}//end of outer while
		} catch(Exception e) {
			m_logger.error("Unable to parse WSDL",e);
		}
	}


	private void extractFile(String destDir, URI wsdlUri) {
		if(m_logger.isDebugEnabled()) {
		m_logger.debug("WSDL URI = "+wsdlUri);
		m_logger.debug("WSDL PATH = "+wsdlUri.getPath());
		}
		if(archivesDir == null)	{
			if(m_logger.isDebugEnabled())
			m_logger.debug("No need to extract WSDL or XSD files, so return");
			return;
		}
		String scheme = wsdlUri.getScheme();
		URI resolvedFileUri = null;
		//Modify destinationDir to baseUrl/WEB-INF/wsdl
		//Earlier this was pointing to baseUrl only
		File destionDir = new File(destDir+"/WEB-INF/wsdl");
		File archivesAppDir = new File(archivesDir);
		if(m_logger.isDebugEnabled()) {
		m_logger.debug("Destination Directory = "+destionDir);
		m_logger.debug("Archives Directory = "+archivesAppDir);
		}
		if(! destionDir.exists()) {
			if(m_logger.isDebugEnabled()) {
				m_logger.debug("Creating directory "+destionDir);
			}
			destionDir.mkdirs();
		}

		if(m_logger.isDebugEnabled()) {
			m_logger.debug("Extracting the WSDL scheme = "+scheme);
		}

		if(scheme.equals(AseStrings.PROTOCOL_FILE)) {
			try {
				File resolvedFile = FileUtils.copy(new File(wsdlUri.getPath()), destionDir);
				if(!((wsdlUri.getPath()).contains(archivesAppDir.getName())))	{
					FileUtils.copy(new File(wsdlUri.getPath()), archivesAppDir);
				}
				resolvedFileUri = resolvedFile.toURI();
			} catch (FileCopyException exp) {
				m_logger.error("Unable to copy the file from "+wsdlUri,exp);
			}
		} else if(scheme.equals(AseStrings.PROTOCOL_HTTP)) {
			if(m_logger.isDebugEnabled())
			m_logger.debug("Processing http scheme  ");
			OutputStream out = null;
			URLConnection conn = null;
			InputStream in = null;
			String query = null;
			String fileName = null;
			query = wsdlUri.getQuery();
			if(m_logger.isDebugEnabled())
			m_logger.debug("QUERY = "+query);
			if(query != null)	{
				fileName = query.substring(query.indexOf(AseStrings.EQUALS)+1);
			}
			if(m_logger.isDebugEnabled()) {
            	m_logger.debug(" Extracting " + fileName + " to Destination Directory = "+archivesAppDir);
        	}
			StringBuffer strBuf = new StringBuffer();
			strBuf.append(archivesAppDir.getPath());
			strBuf.append(File.separatorChar);
			strBuf.append(m_name);
			strBuf.append(File.separatorChar);
			strBuf.append(fileName);
			String destwsdl = strBuf.toString();
			if(m_logger.isDebugEnabled()) {
            	m_logger.debug(" Value of destwsdl: " + destwsdl);
        	}
			File importsDir = new File(archivesAppDir,m_name);
			if(!importsDir.exists()) {
            	if(m_logger.isDebugEnabled()) {
                	m_logger.debug("Creating directory "+importsDir);
            	}
            	importsDir.mkdirs();
        	}

			try {

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
				resolvedFileUri = new URI(destwsdl);
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
			try {
           		File resolvedFile = FileUtils.copy(new File(destwsdl), destionDir);
				//resolvedFileUri = resolvedFile.toURI();
			} catch (FileCopyException exp) {
       			m_logger.error("Unable to copy the file from "+destwsdl,exp);
  			}
		}

		if(m_logger.isDebugEnabled()) {
			m_logger.debug("Exiting extractFile(): Resolved URI = "+resolvedFileUri);
		}
	}

	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("WebServiceDataObject [Name=");
		buffer.append(m_name);
		buffer.append(", WSDL URI=");
		buffer.append(m_wsdlUri);
		buffer.append(AseStrings.SPACE);
		if(m_services != null) {
			Iterator<AseSoaService> itor = this.getServices();
			while(itor.hasNext()) {
				buffer.append(itor.next());
				buffer.append(AseStrings.COMMA);
			}
		}
		if(m_application != null) {
			buffer.append(m_application);
		}
		buffer.append(AseStrings.SQUARE_BRACKET_CLOSE);
		return buffer.toString();
	}
}
	 
	



