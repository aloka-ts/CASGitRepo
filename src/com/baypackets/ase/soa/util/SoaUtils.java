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
//      File:   SoaUtils.java
//
//      Desc:   This file defines utility methods used in SOA Framework implementation
//
//      Author                          Date            Description
//      ----------------------------------------------------------------------
//      Rajendra Singh                  08/01/08        Initial Creation
//
//***********************************************************************************
                                                                                                                             
                                                                                                                             
package com.baypackets.ase.soa.util;
                                                                                                                             
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.FileWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.FileNotFoundException;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;
import java.util.jar.Attributes;
import java.util.ArrayList;
import java.util.List;
import java.util.Collection;
import java.util.Vector;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.StringTokenizer;
import java.util.regex.Pattern;

import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.*;
import javax.xml.transform.stream.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import org.apache.log4j.Logger;

import com.baypackets.ase.util.AseStrings;
import com.baypackets.ase.util.FileUtils;
import com.baypackets.ase.soa.codegenerator.ClassInspector;
import com.baypackets.ase.util.Constants; 
import com.baypackets.ase.soa.common.SoaConstants; 
import com.baypackets.ase.soa.common.SoapServerFactory; 
import com.baypackets.ase.soa.common.SoapServer; 
import com.baypackets.ase.common.Registry;
import com.baypackets.bayprocessor.slee.common.ConfigRepository;

public final class SoaUtils	{
	private static Logger logger = Logger.getLogger(SoaUtils.class);
    private static JarOutputStream jos = null;
    private static FileOutputStream fos = null;
    private static int iBaseFolderLength = 0;


    private static String svcProxy = null;
	private static StringBuffer stringBuffer = new StringBuffer();
    private  static String file = null;
    private static BufferedReader in = null;
	private static String line = null;
    private static String lastLine = null;
	private static String NL = System.getProperties().getProperty("line.separator");

	
	public static void writeJavaFileToDisk(String baseUrl,ClassInspector inspector,String src,String suffix)   throws Exception    {
        StringBuffer path = new StringBuffer();
		if(logger.isDebugEnabled())	{
			logger.debug("BASE URL: "+baseUrl);
			logger.debug("ClassInspector: "+inspector);
			logger.debug("JAVA SOURCE file: "+src);
			logger.debug("SUFFIX: "+suffix);
		}
        try {
            String basePath = new File(baseUrl+File.separatorChar
                              +SoaConstants.JAVA_SOURCE_DIR_NAME+File.separatorChar).getCanonicalPath();
			if(logger.isDebugEnabled())					
			logger.debug("BASE PATH 1: "+basePath);
            //basePath = basePath.substring(0, basePath.lastIndexOf(File.separatorChar)+1);
	//		logger.debug("BASE PATH 2: "+basePath);
            path.append(basePath+File.separatorChar);
			if(logger.isDebugEnabled())
			logger.debug("PATH : "+path.toString());
        } catch (IOException e) {
			logger.error("I/O Exception: " +e);
            throw e;
        }catch(Throwable th)	{
			logger.error("Exception: " +th);
		}
        String packageName = inspector.getPackageName();
		if(logger.isDebugEnabled())
		logger.debug("PACKAGE NAME: "+packageName);
        if(packageName.trim().length() > 0) {
            packageName = packageName.replace('.',File.separatorChar)+File.separatorChar;
            path.append(packageName);
        }
		String dirs = path.toString();
        path.append(inspector.getClassName());
		if(suffix.trim().length() > 0)	{
        	path.append(suffix);
		}
        String completePath = path.toString();
		if(logger.isDebugEnabled())
		logger.debug("COMPLETE PATH: "+completePath);
        FileWriter file = null;
        File f = null;
        try {
			f = new File(dirs);
			if(!f.exists())
				f.mkdirs();
            file = new FileWriter (completePath);
            file.flush();
            file.write(src);
            file.close();
        } catch (Exception e) {
			logger.error("Exception in writing file to disk: " +e);
            throw e;
        }
    }

 	/**
     * Called by the codegenerator  classes to return the class path to be passed to
     * the RuntimCompiler process that will compile the generated proxy classes.
     */
    public static List<File> getClassPath(List<File> list) throws Exception	{
		List<File> classpath = new ArrayList<File>();

        StringBuffer buffer = new StringBuffer(System.getProperty("java.class.path"));
        ConfigRepository config = (ConfigRepository)Registry.lookup(Constants.NAME_CONFIG_REPOSITORY);
        String jarDirs = config.getValue(Constants.PROP_ASE_JAR_DIRS);
        File[] aseJars = FileUtils.findFiles(jarDirs, ",", Pattern.compile(".*.jar"), false);
        File f = null;
		if(logger.isDebugEnabled())	{
			logger.debug("java.class.path buffer: " + buffer);
			logger.debug("ASE JAR DIRs: " + jarDirs);
		}                                                                                                                     
        for (int i = 0; i < aseJars.length; i++) {
			try	{
            	f = new File(aseJars[i].getAbsolutePath());
			}catch(Exception exp)	{
				//log error condition here
				logger.error("Exception for jar: "+aseJars[i] +": "+exp);
			}
			classpath.add(f);
		}

        jarDirs = config.getValue(Constants.PROP_OTHER_JAR_DIRS);
		if(logger.isDebugEnabled())	{
			logger.debug("OTHER JAR DIRs: " + jarDirs);
		}                                                                                                                     
        File[] otherJars = FileUtils.findFiles(jarDirs, ",", Pattern.compile(".*.jar"), false);
        for (int i = 0; i < otherJars.length; i++) {
			try	{
            	f = new File(otherJars[i].getAbsolutePath());
			}catch(Exception exp)	{
				//log error condition here
				logger.error("Exception for jar: "+otherJars[i] +": "+exp);
			}
			classpath.add(f);
		}

		//Add SOAP server libraries to CLASSPATH
        String soapServerName = config.getValue(SoaConstants.NAME_SOAP_SERVER);
        SoapServer soapServer = SoapServerFactory.getSoapServer(soapServerName);
		String soapServerLibDir = soapServer.getSoapServerLibDir();
		if(logger.isDebugEnabled())	{
			logger.debug("soapServerName: " + soapServerName);
			logger.debug("soapServerLibDir: " + soapServerLibDir);
		}
		File[] soapServerJars = FileUtils.findFiles(soapServerLibDir, AseStrings.COMMA, Pattern.compile(".*.jar"), false); 
		for(int j = 0; j < soapServerJars.length; j++)    {
            try {
                f = new File(soapServerJars[j].getAbsolutePath());
            }catch(Exception e) {
                logger.error("Exception for jar: "+soapServerJars[j] +AseStrings.COLON+e);
            }
            classpath.add(f);
                                                                                                                                    
        }

		//Add additional  interface classes of application being deployed in CLASSPATH
		
		for(int j = 0; j < list.size(); j++)	{
			try	{
				f = new File((list.get(j)).getAbsolutePath());	
			}catch(Exception e)	{
				logger.error("Exception for jar: "+list.get(j) +AseStrings.COLON+e);
			}
			classpath.add(f);

		}
                                                                                                                             
                                                                                                                             
        //if (logger.isDebugEnabled()) {
        //  logger.debug("Using the following class path to generate the triggering rule classes:\n" + classPath);
        //}
                                                                                                                             
                                                                                                                             
        return classpath;
    }

	/**
	* This method uses DOM approach to modify xsd2jibx generated binding.xml
	* file from supplied .xsd file for provisioning of remote service on SAS.
	* The modified file will be written to filesystem and will be used by jibx
	* to generate service interface java source file.
	*/
	public static boolean modifyBindings(String baseUrl, String bindingFile, String bindingPkg) {
		boolean success = false;
		String uriAttr = null;
		Element bindingElement = null;
		Element mappingElement = null;
		Element namespaceElem = null;
		String nameAttrValue = null;
		if(logger.isDebugEnabled())	{
			logger.debug("baseUrl for this service: " + baseUrl);
			logger.debug("bindingFile path : " + bindingFile);
			logger.debug("bindingPkg for this service: " + bindingPkg);
		}
      	try {
			//we can also get package from class attribute of <mapping> element
			String packageName = bindingPkg;
			String modifiedFileLocation = baseUrl + "/gen/binding.xml"; //add something to it
			
      		DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
			Document doc = docBuilder.parse(bindingFile);
			//get <binding> element from input XML file
			NodeList bindings = doc.getElementsByTagName("binding");
			if((bindings == null) ||(bindings.getLength() == 0))	{
				if(logger.isDebugEnabled())	{
					logger.debug("No binding element found in : " + bindingFile);
				}
				return success;
				//throw exception here
			}
			if(bindings.getLength() != 0)	{
				bindingElement = (Element)bindings.item(0);
				if(logger.isDebugEnabled())	{
					logger.debug("bindingElement is: " + bindingElement.getTagName());
				}
				//Now modify attributes of all <mapping> elements in input xml file
				NodeList mappings = bindingElement.getElementsByTagName("mapping");
				for(int j=0;j<mappings.getLength();j++)	{
					mappingElement = (Element)mappings.item(j);
					if(uriAttr == null)	{
						String strVal = mappingElement.getAttribute("class");
						packageName = strVal.substring(0,strVal.lastIndexOf(AseStrings.PERIOD));
						NodeList namespace = bindingElement.getElementsByTagName("namespace");
						namespaceElem = (Element)namespace.item(0);
						uriAttr = namespaceElem.getAttribute("uri");
						if(logger.isDebugEnabled())	{
							logger.debug("Namespace URI is :  " + uriAttr);
						}
					}

					//Modify attributes of each <mapping> element
					//get value of 'name' attribute of current mapping element
					nameAttrValue = mappingElement.getAttribute("name");
					mappingElement.removeAttribute("name");
					mappingElement.setAttribute("abstract","true");
					mappingElement.setAttribute("type-name","tns:"+nameAttrValue);

				}
					//Add jibx required attributes to <binding> element
					bindingElement.setAttribute("xmlns:tns",uriAttr);
					bindingElement.setAttribute("name","binding");
					bindingElement.setAttribute("package",packageName);
					bindingElement.setAttribute("force-classes","true");
					bindingElement.setAttribute("add-constructors","true");
					success = true;
			}
			if(success)	{	
				Transformer transformer = TransformerFactory.newInstance().newTransformer();
				transformer.setOutputProperty(OutputKeys.INDENT, "yes");
				//initialize StreamResult with File object to save to file
				StreamResult result = new StreamResult(new FileOutputStream(modifiedFileLocation));
				DOMSource source = new DOMSource(doc);
				transformer.transform(source, result);
			}
      } catch (TransformerConfigurationException e) {
		logger.error("TransformerConfigurationException: ",e);
		success = false;
      } catch (TransformerException e) {
		logger.error("TransformerException: ",e);
		success = false;
      }catch(Exception exp)	{
		logger.error("Exception: ",exp);
		success = false;
	  }
		return success;
   }


	public static File[] listFilesAsArray(File directory, FilenameFilter filter, boolean recurse)	{
		Collection<File> files = listFiles(directory, filter, recurse);
		File[] arr = new File[files.size()];
		return files.toArray(arr);
	}

	private static Collection<File> listFiles(File directory, FilenameFilter filter, boolean recurse)	{
		// List of files / directories
		Vector<File> files = new Vector<File>();
		// Get files / directories in the directory
		File[] entries = directory.listFiles();
		// Go over entries
		for (File entry : entries)	{
			// If there is no filter or the filter accepts the 
			// file / directory, add it to the list
			if (filter == null || filter.accept(directory, entry.getName()))	{
				files.add(entry);
			}
			// If the file is a directory and the recurse flag
			// is set, recurse into the directory
			if (recurse && entry.isDirectory())	{
				files.addAll(listFiles(entry, filter, recurse));
			}
		}
		// Return collection of files
		return files;		
	}


    private static void checkDirectory(String directoryName, File outputFile) {
        File dirobject = new File(directoryName);
        if (dirobject.exists() == true) {
            if (dirobject.isDirectory() == true) {
                File [] fileList = dirobject.listFiles();
                // Loop through the files
                for (int i = 0; i < fileList.length; i++) {
                    if (fileList[i].isDirectory()) {
                       
					   if( (fileList[i].getName()).equals("src") )
					   	continue;

						checkDirectory(fileList[i].getPath(), outputFile);
                    } else if (fileList[i].isFile()) {
                        
						if( (fileList[i].getName()).equals("build.xml") )
							continue;
						if( fileList[i].equals(outputFile) )
							continue;
						// Call the zipFunc function
                        jarFile(fileList[i].getPath());
                    }
                }
            }
            else {
				if(logger.isDebugEnabled())
                logger.debug(directoryName+" is not a directory.");
            }
        }
        else {
			if(logger.isDebugEnabled())
            logger.debug("Directory "+directoryName+" does not exist.");
        }
    }
    
    // a Jar method.
    private static void jarFile(String filePath) {
		if (logger.isDebugEnabled()) {
			logger.debug("Going to add file: " + filePath);
		}

        try {
            FileInputStream fis = new FileInputStream(filePath);
            BufferedInputStream bis = new BufferedInputStream(fis);
			int extraLength = SoaConstants.JAVA_CLASS_DIR_NAME.length()+1;
			
			if( (!filePath.endsWith("java") && !filePath.endsWith("xml") ) || filePath.endsWith("services.xml"))  {
				JarEntry fileEntry = null;
				if(filePath.endsWith("class"))	{
					fileEntry = new JarEntry(filePath.substring(iBaseFolderLength + extraLength));
				}else {
					fileEntry = new JarEntry(filePath.substring(iBaseFolderLength));
				}
            	jos.putNextEntry(fileEntry);
            	byte[] data = new byte[1024];
            	int byteCount;
            	while ((byteCount = bis.read(data, 0, 1024)) > -1) {
                	jos.write(data, 0, byteCount);
            	}
				jos.closeEntry();
				bis.close();
				fis.close();
			}	
        } catch (IOException e) {
			logger.error("Exception: " +e);
        }
    }
    
    
	public static File createJar(String p_sourceDir,String outputPath,String manifestVerion,String mainClass,String classPath){
         File jarFile = null;
		 try {
				if(logger.isDebugEnabled())
               logger.debug("source dir given is   " +p_sourceDir);
                // Create the file output streams for both the file and the zip.
                
                
                String strBaseFolder = p_sourceDir + File.separator ;
                iBaseFolderLength = strBaseFolder.length();
                
				jarFile = new File(outputPath);
				fos = new FileOutputStream(jarFile);
                /*Manifest manifest =new Manifest();
                Attributes manifestAttr = manifest.getMainAttributes();
                //note:Must set Manifest-Version,or the manifest file will be empty!
                if (manifestVerion!=null){
                    manifestAttr.putValue("Manifest-Version",manifestVerion);
                    if (mainClass!=null){
                        manifestAttr.putValue("Main-Class",mainClass);
                    }
                    if (classPath!=null){
                        manifestAttr.putValue("Class-Path",classPath);
                    }
                }
                java.util.Set entries= manifestAttr.entrySet();
 				for(java.util.Iterator i = entries.iterator(); i.hasNext();){
    	 			logger.debug("Manifest attribute:>> " +i.next().toString());
				} */
 
                jos = new JarOutputStream(fos);
                
                if(logger.isDebugEnabled())
                logger.debug( strBaseFolder);
 
                checkDirectory(p_sourceDir, jarFile);
                // Close the file output streams
                jos.flush();
                jos.close();
                fos.close();
            }
            catch (IOException e) {
                e.printStackTrace();
				return null;
            }
		return jarFile;
    }

	/*************************************************************************/
	
	public static void modifySkeleton(String baseDir, String skeleton,
										 String proxy) throws Exception {
        svcProxy = proxy;
	  	file = baseDir + File.separatorChar + "src" + File.separatorChar  
							+ skeleton.replace('.', File.separatorChar) + ".java";
		if(logger.isDebugEnabled())	{	
	  		logger.debug("Original Skeleton file path: " + file);
		}
        try {
            in = new BufferedReader(new FileReader(file));
            while ((line = in.readLine()) != null) {
                if (line.indexOf(" class ") > 0) { //class definition starts
					if(logger.isDebugEnabled()) {
                    	logger.debug("Current Line: " + line);
                    	logger.debug("adding private member variable");
					}
                    addSvcProxyMemberVariable(line);
                } else if (line.indexOf("public ") > 0) {
					if(logger.isDebugEnabled()) {
                    	logger.debug("Current Line: " + line);
                    	logger.debug("adding proxy delegation method");
					}
                    addProxyDelegationMethod(line);
					if(logger.isDebugEnabled()) {
                    	logger.debug("After adding delegation method");
					}
				} else if ((line.indexOf("throw") > 0)||(line.indexOf("this") > 0)) {

                } else {
                    stringBuffer.append(line);
                    stringBuffer.append(NL);
                }
            } //While Ends here
 
            in.close();
        } catch (Exception exp) {
            logger.error("Error in Readaing skeleton: " + exp);
        	throw exp; 
        }
		String modifiedSkel = stringBuffer.toString();
        FileWriter fileWriter = null;
 
        try {
            fileWriter = new FileWriter(file);
            fileWriter.flush();
            fileWriter.write(modifiedSkel);
            fileWriter.close();
        } catch (IOException e) {
            logger.error("Error in writing modified skeleton: " + e);
            throw e;
        }
    }

	 //This method returns ServiceProxy delegation method as String
    private static String getDelegationMethod(String mthd) {
        StringTokenizer st = new StringTokenizer(mthd);
        StringBuffer strBuff = new StringBuffer();
        int i = 0;
        st.nextToken();
 
        String tempStr = st.nextToken();
        strBuff.append("         ");
 
        if (!tempStr.equals("void")) {
            strBuff.append("return ");
        }
 
        strBuff.append("proxy");
        strBuff.append(AseStrings.PERIOD);
 
        String str = st.nextToken();
        String methodName = str;
 
        int indx = methodName.lastIndexOf(AseStrings.PARENTHESES_CHAR_OPEN);
 
        if (indx > 0) {
            strBuff.append(methodName.substring(0, indx));
        } else {
            strBuff.append(methodName);
        }
 
        int beginIndex = mthd.indexOf(AseStrings.PARENTHESES_CHAR_OPEN);
        int endIndex = mthd.indexOf(AseStrings.PARENTHESES_CHAR_CLOSE);
        String args = mthd.substring(beginIndex + 1, endIndex).trim();
        System.out.println(" value of args: " + args);
 
        StringTokenizer argsTokenizer = null;
        String[] result = null;
        strBuff.append(AseStrings.PARENTHESES_OPEN);
		if (args.indexOf(AseStrings.COMMA) > 0) {
			if(logger.isDebugEnabled())	{
				logger.debug("args conatin comma");
			}
            argsTokenizer = new StringTokenizer(args, AseStrings.COMMA);
 
            //Now extract arguments
            //String str = null;
            while (argsTokenizer.hasMoreTokens()) {
                str = argsTokenizer.nextToken();
                result = str.split(AseStrings.SPACE);
                strBuff.append(result[1]);
                strBuff.append(AseStrings.COMMA);
            }
 
            int index = strBuff.length();
            strBuff.deleteCharAt(index - 1);
        } else {
			if(logger.isDebugEnabled())	{
				logger.debug("args do not conatin comma");
			}
 
            if (args.trim().length() > 0) {
                result = args.split(AseStrings.SPACE);
				if(logger.isDebugEnabled())	{
					logger.debug("After splitting args using space");
				}
                /*
                   for (int j =0; i< result.length; j++)        {
                           System.out.println("result["+j+"] : " + result[j]);
                   }
                 */
                strBuff.append(result[1]);
            }
        }
 
        strBuff.append(AseStrings.PARENTHESES_CLOSE);
 
        return strBuff.toString();
    }
	//Insert method delegation to ServiceProxy in each method of generated Skeleton
    private static void addProxyDelegationMethod(String methodstart)
        throws Exception {
        StringBuffer strBuf = new StringBuffer();
 
        stringBuffer.append(methodstart);
        stringBuffer.append(NL);
        strBuf.append(methodstart);
 
        if (!methodstart.endsWith(AseStrings.BRACES_OPEN)) {
            while ((line = in.readLine()) != null) {
                stringBuffer.append(line);
                stringBuffer.append(NL);
                strBuf.append(line);
 
                if (line.endsWith(AseStrings.BRACES_OPEN)) {
                    break;
                }
            }
 
            //System.out.println(" Breaking from While loop");
        }
 
        stringBuffer.append(NL);
 
        String methodInvocation = getDelegationMethod(strBuf.toString());
		if(logger.isDebugEnabled())	{
			logger.debug("methodInvocation: " + methodInvocation);
		}
        stringBuffer.append(methodInvocation);
        stringBuffer.append(AseStrings.SEMI_COLON);
        stringBuffer.append(NL);
        //System.out.println(" Returning control from addProxyDelegationMethod()");
    }

	//Add a private member variable of type ServiceProxy
    private static void addSvcProxyMemberVariable(String defineclass) {
        stringBuffer.append(NL);
        stringBuffer.append(defineclass);
        stringBuffer.append(NL);
        stringBuffer.append("    ");
        stringBuffer.append("private ");
        stringBuffer.append(svcProxy);
        stringBuffer.append(" proxy");
        //create proxy object and assign it to variable proxy
        stringBuffer.append(" = new ");
        stringBuffer.append(svcProxy);
        stringBuffer.append("();");
        stringBuffer.append(NL);
    }

 	//This method returns ServiceProxy delegation method as String
	private static String AgetDelegationMethod(String mthd)	{
		StringTokenizer st = new StringTokenizer(mthd);
		StringBuffer strBuff = new StringBuffer(); 
		int i = 0;
		st.nextToken();
		String tempStr = st.nextToken();
		strBuff.append("         ");
		if(!tempStr.equals("void"))	{
			strBuff.append("return ");
		}
		
        strBuff.append("proxy");
        strBuff.append(AseStrings.PERIOD);
		String str = st.nextToken();
		String methodName = str;

		int indx = methodName.lastIndexOf(AseStrings.PARENTHESES_CHAR_OPEN);
		if(indx>0)	{
			strBuff.append(methodName.substring(0,indx));
		}else	{
			strBuff.append(methodName);
		}
		int beginIndex = mthd.indexOf(AseStrings.PARENTHESES_CHAR_OPEN);
		int endIndex = mthd.indexOf(AseStrings.PARENTHESES_CHAR_CLOSE);
		String args = mthd.substring(beginIndex+1,endIndex).trim();
		if(logger.isDebugEnabled())	{
			logger.debug(" value of args: " +args);
		}
		StringTokenizer argsTokenizer = null;
		String[] result = null;
		strBuff.append(AseStrings.PARENTHESES_OPEN);
		if(args.indexOf(AseStrings.COMMA) > 0)	{
			if(logger.isDebugEnabled()) {
				logger.debug("Method accepts multiple arguments");
			}
			argsTokenizer = new StringTokenizer(args,AseStrings.COMMA);
			//Now extract arguments
			//String str = null;
			while(argsTokenizer.hasMoreTokens())	{
				str = argsTokenizer.nextToken();
				result = str.split(AseStrings.SPACE);
				strBuff.append(result[1]);
				strBuff.append(AseStrings.COMMA);
			}
			int index = strBuff.length();
			strBuff.deleteCharAt(index -1);
		}else	{
			if(args.trim().length() > 0)	{
				if(logger.isDebugEnabled())	{
					logger.debug("Method accepts single argument");
				}
				result = args.split(AseStrings.SPACE);
				if(logger.isDebugEnabled()) {
					for (int j =0; i< result.length; j++)	{
						logger.debug("result["+j+"] : " + result[j]);
					}
				}
				strBuff.append(result[1]);
			}
		}
		strBuff.append(AseStrings.PARENTHESES_CLOSE);
		return strBuff.toString();
	}
	
	//Insert method delegation to ServiceProxy in each method of generated Skeleton
	private static void AaddProxyDelegationMethod(String methodstart,StringBuffer stringBuffer,
						String NL,BufferedReader in) throws Exception	{
		StringBuffer strBuf = new StringBuffer();
		String line = null;

		stringBuffer.append(methodstart);
		stringBuffer.append(NL);
        strBuf.append(methodstart);
		if(!methodstart.endsWith(AseStrings.BRACES_OPEN))	{
			while ((line = in.readLine()) != null) {
				stringBuffer.append(line);
				stringBuffer.append(NL);
				strBuf.append(line);
				if(line.endsWith(AseStrings.BRACES_OPEN))
					break;
			}
		}

		stringBuffer.append(NL);
		String methodInvocation = AgetDelegationMethod(strBuf.toString());
		if(logger.isDebugEnabled())	{
			logger.debug("methodInvocation: " + methodInvocation);
		}
        stringBuffer.append(methodInvocation);
        stringBuffer.append(AseStrings.SEMI_COLON);
		stringBuffer.append(NL);
	}
	
	//Add a private member variable of type ServiceProxy
	private static void AaddSvcProxyMemberVariable(String defineclass,StringBuffer stringBuffer,
												 String NL, String svcProxy)	{
		stringBuffer.append(NL);
        stringBuffer.append(defineclass); 
        stringBuffer.append(NL);
		stringBuffer.append("    ");
        stringBuffer.append("private ");
        stringBuffer.append(svcProxy);
        stringBuffer.append(" proxy");
		//create proxy object and assign it to variable proxy
		stringBuffer.append(" = new ");
		stringBuffer.append(svcProxy);
		stringBuffer.append("();");
        stringBuffer.append(NL);
	}


	/** @param filePath the name of the file to open.
    */ 
    public static String readFileAsString(String filePath)
    throws java.io.IOException{
        StringBuffer fileData = new StringBuffer(1000);
        BufferedReader reader = new BufferedReader(
                new FileReader(filePath));
        char[] buf = new char[1024];
        int numRead=0;
        while((numRead=reader.read(buf)) != -1){
            String readData = String.valueOf(buf, 0, numRead);
            fileData.append(readData);
            buf = new char[1024];
        }
        reader.close();
        return fileData.toString();
    }



}
