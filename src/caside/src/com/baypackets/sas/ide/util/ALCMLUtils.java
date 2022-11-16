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
package com.baypackets.sas.ide.util;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.URL;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.log4j.BasicConfigurator;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;

import com.baypackets.sas.ide.SasPlugin;
import com.genband.ase.alc.alcml.jaxb.ServiceDefinition;
import com.genband.ase.alc.alcml.jaxb.xjc.ContextParamtype;
import com.genband.ase.alc.alcml.jaxb.xjc.Patterntype;

public class ALCMLUtils // implements CommandHandler
{
//	 private static DeployerFactory deployerFactory = (DeployerFactory) Registry.lookup(DeployerFactory.class.getName());
//	 private static Deployer appDeployer = deployerFactory.getDeployer(DeployableObject.TYPE_SAS_APPLICATION);
//	static Logger logger = Logger.getLogger(ALCMLUtils.class.getName());

	// public static String CMD_DEPLOY_ALC = new String("deploy-alcml");

	private ALCMLUtils() {
//		 this.deployerFactory = ;
//		 this.appDeployer = ;

		/*
		 * TelnetServer telnetServer = (TelnetServer)
		 * Registry.lookup(Constants.NAME_TELNET_SERVER);
		 * telnetServer.registerHandler(CMD_DEPLOY_ALC, this);
		 */
	}

	public void cleanup() {
		/*
		 * TelnetServer telnetServer = (TelnetServer)
		 * Registry.lookup(Constants.NAME_TELNET_SERVER);
		 * telnetServer.unregisterHandler(CMD_DEPLOY_ALC, this);
		 */
	}

	private static String fileAsString(InputStream file) throws IOException {
		InputStreamReader inr = new InputStreamReader(file);
		BufferedReader bufferedReader = new BufferedReader(inr);

		String returnVal = "";
		String line = null;
		while ((line = bufferedReader.readLine()) != null)
			returnVal += line + "\n";
		bufferedReader.close();
		inr.close();
		file.close();
		return returnVal;
	}

	public static void checkDirectory(String directoryName,
			JarOutputStream jos, int iBaseFolderLength) {
		File dirobject = new File(directoryName);
		if (dirobject.exists() == true) {
			if (dirobject.isDirectory() == true) {
				File[] fileList = dirobject.listFiles();
				// Loop through the files
				for (int i = 0; i < fileList.length; i++) {
					if (fileList[i].isDirectory()) {
						checkDirectory(fileList[i].getPath(), jos,
								iBaseFolderLength);
					} else if (fileList[i].isFile()) {
						// Call the zipFunc function
						jarFile(fileList[i].getPath(), jos, iBaseFolderLength);
					}
				}
			} else {
				// System.out.println(directoryName+" is not a directory.");
			}
		} else {
			// System.out.println("Directory "+directoryName+" does not exist.");
		}
	}

	// a Jar method.
	private static void jarFile(String filePath, JarOutputStream jos,
			int iBaseFolderLength) {
		try {
			FileInputStream fis = new FileInputStream(filePath);
			BufferedInputStream bis = new BufferedInputStream(fis);
			JarEntry fileEntry = new JarEntry(filePath
					.substring(iBaseFolderLength));
			jos.putNextEntry(fileEntry);
			byte[] data = new byte[1024];
			int byteCount;
			while ((byteCount = bis.read(data, 0, 1024)) > -1) {
				jos.write(data, 0, byteCount);
			}
		} catch (IOException e) {
		}
	}

	private static void createJar(String checkDir, String outputPath,
			String manifestVerion, String mainClass, String classPath) {
		FileOutputStream fos = null;
		JarOutputStream jos = null;
		int iBaseFolderLength = 0;

		try {
			String resultant = checkDir;
			Pattern p = Pattern.compile("\\/\\/");
			Matcher m = p.matcher(resultant);
			while (m.find()) {
				resultant = m.replaceAll("/");
				m = p.matcher(resultant);
			}

			String strBaseFolder = resultant + File.separator;
			iBaseFolderLength = strBaseFolder.length();
			fos = new FileOutputStream(outputPath);
			Manifest manifest = new Manifest();
			Attributes manifestAttr = manifest.getMainAttributes();
			// note:Must set Manifest-Version,or the manifest file will be
			// empty!
			if (manifestVerion != null) {
				manifestAttr.putValue("Manifest-Version", manifestVerion);
				if (mainClass != null) {
					manifestAttr.putValue("Main-Class", mainClass);
				}
				if (classPath != null) {
					manifestAttr.putValue("Class-Path", classPath);
				}
			}
			java.util.Set entries = manifestAttr.entrySet();

			jos = new JarOutputStream(fos, manifest);

			checkDirectory(resultant, jos, iBaseFolderLength);
			// Close the file output streams
			jos.flush();
			jos.close();
			fos.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	public static String createSarAndGetPath(String appName, String version,
			String priority, String urlName, String baseDir){
		return createSarAndGetPath( appName,  version,
				 priority,  urlName,  baseDir , null);
	}

	public static String createSarAndGetPath(String appName, String version,
			String priority, String urlName, String baseDir , String remoteDir) {
		
		SasPlugin.getDefault()
		.log(" App Name: " + appName + " Version :" + version
				+ " Priority :" + priority + " Url Name: " + urlName
				+ " Base Dir:" + baseDir);
		
		File compDir = null;
		String compDirName = baseDir + "/" + appName;
		compDir = new File(compDirName);
		compDir.mkdirs();

		String sipXml = null;
		String sasXml = null;
		String webXml = null;

		try {
			InputStream in = SasPlugin.getDefault().getBundle().getEntry(
					"resources/alc/sipxmltemplate.xml").openStream();
			sipXml = fileAsString(in);
			System.out.println(" sipXml: " + sipXml );
			in = SasPlugin.getDefault().getBundle().getEntry(
					"resources/alc/sasxmltemplate.xml").openStream();
			sasXml = fileAsString(in);
			System.out.println(" sasXml: " + sasXml);
			in = SasPlugin.getDefault().getBundle().getEntry(
					"resources/alc/webxmltemplate.xml").openStream();
			webXml = fileAsString(in);
			System.out.println(" webXml: " + webXml);

		} catch (Exception e) {
			System.out.println(" Unable to fetch file from resource folder");
			SasPlugin.getDefault()
			.log(
					"Unable to fetch the files from resource folder.");
			return e.getMessage();
		}
		Map<String , String> sContext = new LinkedHashMap<String , String>();
		sContext.put("ServletName", appName);
		sContext.put("ServletVersion", version);
		sContext.put("ServletPriority", priority);
		sContext
				.put("ServletClass",
						"com.genband.sip.ServiceDefinitionApp.ApplicationLogicControlSipService");
		sContext.put("HttpServletClass",
				"com.genband.ase.alc.http.ApplicationLogicControlHttpService");
		sContext.put("ALCFileURL", remoteDir==null ?urlName : remoteDir);

		try {
			BasicConfigurator.configure();
			ServiceDefinition.setReloadClass(false);
			ServiceDefinition.setJAXBLoader(ALCMLUtils.class.getClassLoader());
			ServiceDefinition.CreateALCMLDefinition("__" + appName, new URL(urlName),false,false);
		} catch (Exception e) {
			System.out.println("CreateALCMLDefinition: Unable to create definition ");
			SasPlugin.getDefault()
			.log("CreateALCMLDefinition: Unable to create definition ");
			e.printStackTrace();
			return e.getMessage();
		} finally{
		   }

		String context = "";
		String urlPattern = "";
		try {
			String patterns = "";
			List<Object> oList = ServiceDefinition.getAlcMapping("__" +appName);
			if (oList != null)
			{
				Iterator<Object> oListIterator = oList.iterator();

				DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
				DocumentBuilder db = dbf.newDocumentBuilder();
				Document d = db.newDocument();

				while (oListIterator.hasNext())
				{
					Object obj = oListIterator.next();
					

					if (obj instanceof Patterntype)
					{
						Element serv = d.createElement("servlet-mapping");
						Element servName = d.createElement("servlet-name");
						Text nameOfApp = d.createTextNode(appName);
						servName.appendChild(nameOfApp);
						serv.appendChild(servName);
						Element root = d.createElement("pattern");
						serv.appendChild(root);
						List<Element> eList = ((Patterntype)obj).getAny();
						Iterator<Element> eListIterator = eList.iterator();
						if (eListIterator.hasNext())
						{
							try
							{
								while (eListIterator.hasNext())
								{
									root.appendChild(d.importNode(eListIterator.next(), true));
								}
							}
							catch (Exception xmle)
							{
								return xmle.getMessage();
							}
						}
						ByteArrayOutputStream baos = new ByteArrayOutputStream();
						TransformerFactory transformerFactory = TransformerFactory.newInstance();
						Transformer transformer = transformerFactory.newTransformer();
						transformer.setOutputProperty(OutputKeys.INDENT, "yes");
						transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
						transformer.transform(new DOMSource(serv), new StreamResult(baos));
						String namespaceHack = baos.toString();
//						Pattern p = Pattern.compile(" xmlns:x0=\"http://www\\.w3\\.org/[0-9]+/XMLSchema\"");
//						Matcher m = p.matcher(namespaceHack);
//						patterns += m.replaceAll("") + "\n";	
						
						
						Pattern p = Pattern.compile("xmlns:java=\"org.eclipse.vtp.desktop.editors.core\"");
						Matcher m = p.matcher(namespaceHack);
						patterns += m.replaceAll("") + "\n";
						
					//	m=p.matcher("xmlns:java=\"org.eclipse.vtp.desktop.editors.core\"");
						
						SasPlugin.getDefault()
						.log("Patterns after ------------------->>>> "+patterns);	
					}
					else if (obj instanceof ContextParamtype)
					{
						Element serv = d.createElement("context-param");

						List<Element> eList = ((ContextParamtype)obj).getAny();
						Iterator<Element> eListIterator = eList.iterator();
						if (eListIterator.hasNext())
						{
							try
							{
								while (eListIterator.hasNext())
								{
									serv.appendChild(d.importNode(eListIterator.next(), true));
								}
							}
							catch (Exception xmle)
							{
								return xmle.getMessage();
							}
						}
						ByteArrayOutputStream baos = new ByteArrayOutputStream();
						TransformerFactory transformerFactory = TransformerFactory.newInstance();
						Transformer transformer = transformerFactory.newTransformer();
						transformer.setOutputProperty(OutputKeys.INDENT, "yes");
						transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
						transformer.transform(new DOMSource(serv), new StreamResult(baos));
						String namespaceHack = baos.toString();
						Pattern p = Pattern.compile(" xmlns:x0=\"http://www\\.w3\\.org/[0-9]+/XMLSchema\"");
						Matcher m = p.matcher(namespaceHack);
						context += m.replaceAll("") + "\n";
					}else if(obj instanceof String){
						
						
						Element serv = d.createElement("servlet-mapping");
						Element servName = d.createElement("servlet-name");
						Text nameOfApp = d.createTextNode(appName);
						servName.appendChild(nameOfApp);
						serv.appendChild(servName);
						Element root = d.createElement("url-pattern");
						serv.appendChild(root);
						String pattern =(String)obj;
						Text pat =d.createTextNode(pattern);
						root.appendChild(pat);
							
						ByteArrayOutputStream baos = new ByteArrayOutputStream();
						TransformerFactory transformerFactory = TransformerFactory.newInstance();
						Transformer transformer = transformerFactory.newTransformer();
						transformer.setOutputProperty(OutputKeys.INDENT, "yes");
						transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
						transformer.transform(new DOMSource(serv), new StreamResult(baos));
						String namespaceHack = baos.toString();
						Pattern p = Pattern.compile(" xmlns:x0=\"http://www\\.w3\\.org/[0-9]+/XMLSchema\"");
						Matcher m = p.matcher(namespaceHack);
						urlPattern += m.replaceAll("") + "\n";
						
					}
				}
			
			}

			sContext.put("HttpURLMapping", urlPattern);
			sContext.put("ServletMappings", patterns);
		} catch (Exception xmle) {
			System.out.println("Unable to Create XML ");
			SasPlugin.getDefault()
			.log("Unable to Create XML ");
			return xmle.getMessage();
		}

		context += "<context-param>\n";
		context += "<param-name>ALCNameSpace</param-name>\n";
		context += "<param-value>" + appName + "</param-value>\n";
		context += "</context-param>\n";

		context += "<context-param>\n";
		context += "<param-name>ServiceURL</param-name>\n";
		context += "<param-value>" + (remoteDir==null ?urlName : remoteDir) + "</param-value>\n";
		context += "</context-param>\n";

		sContext.put("ContextParms", context);

		try {
			(new File(compDirName + "/WEB-INF")).mkdirs();
		} catch (Exception e) {
			System.out.println("Unable to create WEB-INF Directory ");
			SasPlugin.getDefault()
			.log("Unable to create WEB-INF Directory ");
			return e.getMessage();
		}

		try {
			File sipDotXml = new File(compDirName + "/WEB-INF/sip.xml");
			sipDotXml.createNewFile();
			PrintStream sipDotXmlOut = new PrintStream(sipDotXml);
//			sipDotXmlOut.println(ALCMLExpression.toString(sContext, sipXml));
			sipDotXmlOut.println(_ReplaceContextVariables(sipXml , true , sContext));
			sipDotXmlOut.close();

			File sasDotXml = new File(compDirName + "/WEB-INF/sas.xml");
			sasDotXml.createNewFile();
			PrintStream sasDotXmlOut = new PrintStream(sasDotXml);
//			sasDotXmlOut.println(ALCMLExpression.toString(sContext, sasXml));
			sasDotXmlOut.println(_ReplaceContextVariables(sasXml , true , sContext));
			sasDotXmlOut.close();

			File webDotXml = new File(compDirName + "/WEB-INF/web.xml");
			webDotXml.createNewFile();
			PrintStream webDotXmlOut = new PrintStream(webDotXml);
//			webDotXmlOut.println(ALCMLExpression.toString(sContext, webXml));
			webDotXmlOut.println(_ReplaceContextVariables(webXml , true , sContext));
			webDotXmlOut.close();

		}

		catch (Exception e) {
			System.out.println("Unable to create XML files: sip.xml , sas.xml , web.xml ");
			SasPlugin.getDefault()
			.log("Unable to create XML files: sip.xml , sas.xml , web.xml ");
			return e.getMessage();
		}

		try {
			createJar(compDirName, compDirName + "/../" + appName + ".sar",
					null, null, null);
		} catch (Exception e) {
			System.out.println("Uable to create the JAR file. ");
			return e.getMessage();
		}

		try {
			String path = compDirName + "/../" + appName + ".sar";
			 System.out.println(" creating Stream is...  " +path);
			
//			 InputStream stream = new BufferedInputStream(new
//			 URL(path).openStream());
			 // System.out.println("The Stream is...alcml  "+stream );
//			 DeployableObject app = null;
			 
//			 app = appDeployer.deploy(appName, version,Integer.parseInt(priority), null, stream, Deployer.CLIENT_TELNET);
//			
//			 appDeployer.start(app.getId());
			System.out.println("Path is : " + path);
			return path;
		} catch (Exception e) {
			return e.getMessage();
		}
	}
	
	
	static Object _ReplaceContextVariables(String sInput, boolean makeString , Map serviceContext)
	{
		String sValue = sInput;
		if (sInput != null)
		{
			int size = sValue.length();
			int StartOfVariable = size;
			int EndOfVariable = 0;
			while (sValue.contains("${"))
			{
				StartOfVariable = sValue.lastIndexOf("${", StartOfVariable);
				EndOfVariable = StartOfVariable;
				while (++EndOfVariable < size)
				{

					char alnum = sValue.charAt(EndOfVariable);
					if (alnum == '}')
					{
						String key = sValue.substring(StartOfVariable, EndOfVariable + 1);
						String value = sValue.substring(StartOfVariable+2, EndOfVariable);
						Object rValue = getAttribute(value , serviceContext);
						if (rValue == null)
							sValue = sValue.replace(key, "null");
						else
						{
							if (makeString)
								sValue = sValue.replace(key, (String)_ReplaceContextVariables(rValue.toString(), makeString,serviceContext));
							else
							{
								if (rValue instanceof String)
								{
									sValue = sValue.replace(key, _ReplaceContextVariables((String)rValue, makeString,serviceContext).toString());
								}
								else
								{
									if ((key.length() + 3) < sInput.length())
									{
										sValue = sValue.replace(key, _ReplaceContextVariables(rValue.toString(), makeString,serviceContext).toString());
									}
									else
										return rValue;
								}
							}
						}
						size = sValue.length();
						break;
					}
				}
			}
		}
		return sValue;
	}

	
	static Object getAttribute(String variable , Map serviceContext) {
		return serviceContext.get(variable);
	}
	
	public void finalize(){
		System.out.print("I am dying");
	}
	
	private static void mkWritable(File file) {
		try {
			file.setWritable(true, false);
			
			if (file.isDirectory()) {
				File[] chmodFiles = file.listFiles();
				for (File subfile : chmodFiles) {
					System.out.println("File name:" + subfile);
					if (subfile.isDirectory())
						mkWritable(subfile);
					else
						subfile.setWritable(true, false);

				}
			}
		} catch (Exception mue) {
			
		} 
	}

	
}

