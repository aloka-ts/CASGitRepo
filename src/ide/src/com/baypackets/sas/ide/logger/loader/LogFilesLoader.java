/*
 * Created on Mar 6, 2004
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package com.baypackets.sas.ide.logger.loader;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Reader;
import java.util.Calendar;
import java.util.Vector;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.eclipse.core.runtime.IPath;
//import org.eclipse.datatools.connectivity.oda.design.Properties;
import org.eclipse.jdt.core.JavaCore;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import java.util.*;
import org.eclipse.ui.part.ViewPart;

import com.baypackets.sas.ide.SasPlugin;
import com.baypackets.sas.ide.logger.filters.FilterLoader;
import com.baypackets.sas.ide.logger.views.SASDebugLoggerView;
import com.baypackets.sas.ide.logger.views.SASServerLoggerView;

/**
 * Loader for watchers stored in XML. Also starts each watcher in the view.
 */
public class LogFilesLoader
{
	FilterLoader filterLoader = new FilterLoader();
	private SASServerLoggerView m_view;
	private SASDebugLoggerView debug_view;
	
	private final String SIP_DEBUG_LOG="sipDebug.log";
	private final String CAS_LOG="CAS.log";
	
	public LogFilesLoader(SASServerLoggerView view)
	{
	    m_view = view;
	}
	
	public LogFilesLoader(SASDebugLoggerView view)
	{
		debug_view = view;
	}
	
	public void loadWatchers(Reader r) throws Exception
	{
		org.w3c.dom.Document doc = createDocument(r);
		loadWatchers(doc);
	}

	public void loadWatchers(org.w3c.dom.Document doc)
	{
		NodeList watcherNodes = doc.getElementsByTagName("watcher");
		SasPlugin.getDefault().log("LogFilesLoader:loaderWatchers():::: the Number of watcher nodes are"+watcherNodes.getLength());
		
//		if(watcherNodes.getLength()==0){
//			File file=getCurrentSASLogFileLocation();
//			System.out.println("LogFilesLoader:::: loadWatcher() the file path is.."+file.getAbsolutePath());
//			m_view.addWatcher(file, 1, 100, null, true);
//		}else{
			for (int i = 0; i < watcherNodes.getLength(); i++) {
				Node node = watcherNodes.item(i);
				loadWatcher(node);
	//		}
		}
		
	}

	protected void loadWatcher(Node watcherNode)
	{
		File file = null;
		int interval = 0;
		int numLines = 0;
		
		Vector filters = new Vector();
		NodeList children = watcherNode.getChildNodes();
		for (int i = 0; i < children.getLength(); i++) {
			Node node = children.item(i);
			String name = node.getNodeName();
//			if (name.equals("file")) {
//				file = new File(node.getFirstChild().getNodeValue());
//				if(getCurrentSASLogFileLocation()!=null){
//					file=getCurrentSASLogFileLocation();
//				}
//				
//			}
			if (name.equals("numLines")) {
				numLines = Integer.parseInt(node.getFirstChild().getNodeValue());
			}
			else if (name.equals("interval")) {
				interval = Integer.parseInt(node.getFirstChild().getNodeValue());
			}
			else if (name.equals("filter")) {
				filters.add(filterLoader.loadFilter(node));
			}
		}
		SasPlugin.getDefault().log("LogFilesLoader:::: loadWatcher() the file path is..");
		if(m_view!=null){
		m_view.addWatcher(interval, numLines, filters, false);
		}else if(debug_view!=null){
			debug_view.addWatcher(interval, numLines, filters, false);
		}
	}

	/**
	 * Create a Document with content based on the content of the given Reader.
	 */
	protected org.w3c.dom.Document createDocument(Reader r) throws Exception
	{
		org.w3c.dom.Document document;
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder parser = factory.newDocumentBuilder();
			document = parser.parse(new InputSource(r));
			return document;
		}
		catch (Exception e) {
			throw e;
		}
	}
	
//	public File getCurrentSASLogFileLocation(){
//
//      String filePath = "";
//      FileInputStream fin = null;
//      java.util.Properties ase = new java.util.Properties();
//		String dateStamp=null;
//		String curentPath="";
//		try{
//		IPath pathASEHOME = JavaCore.getClasspathVariable("ASE_HOME");
//		if (pathASEHOME != null) {
//			filePath = pathASEHOME.append("conf").append("ase.properties")
//					.toString();
//			System.out.println("The filepath is...." + filePath);
//		} else {
//			System.out.println("The filepath is...null");
//			
//		}
//
//		fin = new FileInputStream(filePath);
//		ase.load(fin);
//		String saslogLocation=ase.getProperty("1.1.2");
//					String dt="";	
//					String mon="";
//					int month=	(Calendar.getInstance().get(Calendar.MONTH)+1);
//					int date=(Calendar.getInstance().get(Calendar.DATE));
//					if(Integer.toString(date).length()==1){
//						dt="0"+Integer.toString(date);
//					}else{
//						dt=Integer.toString(date);
//					}
//					if(Integer.toString(month).length()==1){
//						mon="0"+Integer.toString(month);
//					}else{
//						mon=Integer.toString(month);
//					}
//					
//					int year=(Calendar.getInstance().get(Calendar.YEAR));
//					dateStamp=mon+"_"+dt+"_"+year;
//					if(debug_view!=null){
//				         curentPath=saslogLocation+"/"+dateStamp+"/"+this.SIP_DEBUG_LOG;
//					}else if(m_view!=null){
//						 curentPath=saslogLocation+"/"+dateStamp+"/"+SAS_LOG;
//					}
//			System.out.println("The directory for logging is..."+dateStamp);
//			System.out.println("The current path is..."+curentPath);
//		}catch (FileNotFoundException e) {
//			SasPlugin.getDefault().log(
//					"ase.properties FileNotFound....", e);
//		} catch (IOException i) {
//			SasPlugin.getDefault().log(
//					"ase.properties IOException....", i);
//		} finally {
//			try {
//				fin.close();
//			} catch (IOException i) {
//				SasPlugin.getDefault().log(
//						"ase.properties close IOException....", i);
//			}
//		}
//		File file=new File(curentPath);
//		if(file.exists()){
//			System.out.println("getCurrentSASLogFileLocation()  the file path is.."+file.getAbsolutePath()+"Exists");
//		return new File(curentPath);
//		}
//		else{
//			return null;}
//	}
	
}