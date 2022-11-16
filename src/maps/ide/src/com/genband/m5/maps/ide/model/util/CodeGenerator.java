package com.genband.m5.maps.ide.model.util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.runtime.CoreException;

import com.genband.m5.maps.ide.CPFPlugin;
import com.genband.m5.maps.ide.Util;
import com.genband.m5.maps.ide.model.CPFPortlet;
import com.genband.m5.maps.ide.model.CPFScreen;

public abstract class CodeGenerator {
	
	protected static String destServices;
	protected static String destMessages;
	protected static String destMbeans;
	protected static String destWebDescriptor;
	
	static {
		if (File.separatorChar == '/') { //unix
			
			destServices = "/src/com/genband/m5/maps/services/";

			destMessages = "/src/com/genband/m5/maps/messages/";

			destMbeans = "/src/com/genband/m5/maps/mbeans/";
			
			destWebDescriptor = "/WebContent/WEB-INF/web.xml";
		}
		else { //win

			destServices = "\\src\\com\\genband\\m5\\maps\\services\\";

			destMessages = "\\src\\com\\genband\\m5\\maps\\messages\\";
			
			destMbeans = "\\src\\com\\genband\\m5\\maps\\mbeans\\";
			
			destWebDescriptor = "\\WebContent\\WEB-INF\\web.xml";
		}
	}
	
	private static CPFPlugin LOG = CPFPlugin.getDefault ();	

	public abstract IFile generateResource (CPFPortlet screenCapture) throws Exception;
	
	 public abstract void createPackage(IFolder src );
	
	public abstract void generateResource (CPFScreen screenCapture, CPFListener listener, Object handback) throws Exception;
	
	public static void createPackages (CPFPortlet p_portlet) throws Exception {

		String path = Util.getProjectPath (p_portlet.getCurrentProject()).concat(destServices);
		
		File serviceFolder = new File (path);

		path = Util.getProjectPath (p_portlet.getCurrentProject()).concat(destMessages);
		
		File messagesFolder = new File (path);
		
		path = Util.getProjectPath (p_portlet.getCurrentProject()).concat(destMbeans);
		
		File mbeansFolder = new File (path);
		
		LOG.info ("service folder: " + serviceFolder.getName());
		LOG.info ("messages folder: " + messagesFolder.getName());
		LOG.info ("mbeans folder: " + mbeansFolder.getName());

		if(! serviceFolder.exists()) {
			
			boolean created = serviceFolder.mkdirs();
			
			if (! created) {
				LOG.error ("Faiiled to create service Folder: " + serviceFolder);
			}
			LOG.info ("Successfully created file: " + serviceFolder);
		}

		if(! messagesFolder.exists()) {
			
			boolean created = messagesFolder.mkdirs();
			
			if (! created) {
				LOG.error ("Faiiled to create messages Folder: " + messagesFolder);
			}
			LOG.info ("Successfully created file: " + messagesFolder);
		}

		if(! mbeansFolder.exists()) {
			
			boolean created = mbeansFolder.mkdirs();
			
			if (! created) {
				LOG.error ("Faiiled to create mbeans Folder: " + mbeansFolder);
			}
			LOG.info ("Successfully created file: " + mbeansFolder);
		}
	}
	
	/**
	 * Creating or Updating web.xml in WebContent/WEB-INF......
	 */
	public static void createWebXml (CPFPortlet p_portlet, File file) throws Exception {
	
		FileWriter fr = null;
		try {

			if(! file.exists()) {
				
				boolean created = file.createNewFile();
				
				if (! created) {
					LOG.error ("Faiiled to create web.xml file: " + file);
				}
				
				LOG.info ("Successfully created file: " + file);
				fr = new FileWriter(file);
				fr.flush();
				
				String xml = new String("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
				fr.write(xml);
				fr.write("\n");
				xml = new String("<web-app xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" ");
				xml = xml.concat("xmlns=\"http://java.sun.com/xml/ns/javaee\" ");
				xml = xml.concat("xmlns:web=\"http://java.sun.com/xml/ns/javaee/web-app_2_5.xsd\" ");
				xml = xml.concat("xsi:schemaLocation=\"http://java.sun.com/xml/ns/javaee http://");
				xml = xml.concat("java.sun.com/xml/ns/javaee/web-app_2_5.xsd\" id=\"WebApp_ID\" version=\"2.5\">");
				fr.write(xml);
				fr.write("\n");
				xml = new String("	<display-name>" + p_portlet.getCurrentProject() 
									+ "</display-name>");
				fr.write(xml);
				xml = new String("	<session-config>");
				fr.write("\n");
				fr.write(xml);
				fr.write("\n");
				xml = new String("		<session-timeout>300</session-timeout>");
				fr.write(xml);
				fr.write("\n");
				xml = new String("	</session-config>");
				fr.write(xml);
				fr.write("\n");
				
					//Added on 14th March...
				xml = new String("<security-constraint>");
				fr.write(xml);
				fr.write("\n");
				xml = new String(" <web-resource-collection>");
				fr.write(xml);
				fr.write("\n");
				xml = new String("		<web-resource-name>WS Resources</web-resource-name>");
				fr.write(xml);
				fr.write("\n");
				xml = new String("		<url-pattern>/*</url-pattern>");
				fr.write(xml);
				fr.write("\n");
				xml = new String("	</web-resource-collection>");
				fr.write(xml);
				fr.write("\n");
				xml = new String("	<auth-constraint>");
				fr.write(xml);
				fr.write("\n");
				xml = new String("		<role-name>Authenticated</role-name>");
				fr.write(xml);
				fr.write("\n");
				xml = new String("	</auth-constraint>");
				fr.write(xml);
				fr.write("\n");
				xml = new String("</security-constraint>");
				fr.write(xml);
				fr.write("\n");
				fr.write("\n");
				xml = new String("<security-role>");
				fr.write(xml);
				fr.write("\n");
				xml = new String("	<role-name>Authenticated</role-name>");
				fr.write(xml);
				fr.write("\n");
				xml = new String("</security-role>");
				fr.write(xml);
				fr.write("\n");
				xml = new String("<login-config>");
				fr.write(xml);
				fr.write("\n");
				xml = new String("	<auth-method>BASIC</auth-method>");
				fr.write(xml);
				fr.write("\n");
				xml = new String("	<realm-name>Test Realm</realm-name>");
				fr.write(xml);
				fr.write("\n");
				xml = new String("</login-config>");
				fr.write(xml);
				fr.write("\n");
					//Added upto this on 14th March...
				xml = new String("</web-app>");
				fr.write(xml);
				
				LOG.info ("Done writing to webxml file: " + file);
			}
			
			LOG.info ("webxml file already exists: " + file);
			
		} catch (IOException e) {
			
			LOG.error ("Got exception dealing with file I/O", e);
			throw e;
		} finally {
			if (fr != null) {
				try {
					fr.close ();
				} catch (IOException e) {
					LOG.error ("Failed to close file writer ...");
				}
			}
		}
		
	
	}
	
	
	
		 
 

}
