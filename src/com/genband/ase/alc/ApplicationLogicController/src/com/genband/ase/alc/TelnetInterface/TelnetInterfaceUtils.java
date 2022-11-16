package com.genband.ase.alc.TelnetInterface;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.URL;
import java.util.Iterator;
import java.util.List;
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

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;

import com.baypackets.ase.common.Registry;
import com.baypackets.ase.spi.deployer.DeployableObject;
import com.baypackets.ase.spi.deployer.Deployer;
import com.baypackets.ase.spi.deployer.DeployerFactory;
import com.baypackets.ase.spi.util.CommandFailedException;
import com.baypackets.ase.spi.util.CommandHandler;
import com.baypackets.ase.util.Constants;
import com.baypackets.ase.util.TelnetServer;
import com.genband.ase.alc.alcml.jaxb.ALCMLExpression;
import com.genband.ase.alc.alcml.jaxb.ServiceContext;
import com.genband.ase.alc.alcml.jaxb.ServiceDefinition;
import com.genband.ase.alc.alcml.jaxb.xjc.ContextParamtype;
import com.genband.ase.alc.alcml.jaxb.xjc.Patterntype;

public class TelnetInterfaceUtils implements CommandHandler
{
	private DeployerFactory deployerFactory = null;
	private Deployer appDeployer = null;
    static Logger logger = Logger.getLogger(TelnetInterfaceUtils.class.getName());
	private static String CMD_DEPLOY_ALC = new String("deploy-alcml");

	public TelnetInterfaceUtils()
	{
		this.deployerFactory = (DeployerFactory) Registry.lookup(DeployerFactory.class.getName());
		this.appDeployer = this.deployerFactory.getDeployer(DeployableObject.TYPE_SAS_APPLICATION);

		TelnetServer telnetServer = (TelnetServer)
										Registry.lookup(Constants.NAME_TELNET_SERVER);
		telnetServer.registerHandler(CMD_DEPLOY_ALC, this);
	}

	public void cleanup()
	{
		TelnetServer telnetServer = (TelnetServer)
										Registry.lookup(Constants.NAME_TELNET_SERVER);
		telnetServer.unregisterHandler(CMD_DEPLOY_ALC, this);
	}

	private String fileAsString(String fileName) throws IOException
	{
		FileReader fr = new FileReader(fileName);
		BufferedReader bufferedReader = new BufferedReader(fr);

		String returnVal = "";
		String line = null;
		while ((line = bufferedReader.readLine()) != null)
			returnVal += line + "\n";
		bufferedReader.close();
		fr.close();
		return returnVal;
	}

    public static void checkDirectory(String directoryName, JarOutputStream jos, int iBaseFolderLength) {
        File dirobject = new File(directoryName);
        if (dirobject.exists() == true) {
            if (dirobject.isDirectory() == true) {
                File [] fileList = dirobject.listFiles();
                // Loop through the files
                for (int i = 0; i < fileList.length; i++) {
                    if (fileList[i].isDirectory()) {
                        checkDirectory(fileList[i].getPath(), jos, iBaseFolderLength);
                    } else if (fileList[i].isFile()) {
                        // Call the zipFunc function
                        jarFile(fileList[i].getPath(), jos, iBaseFolderLength);
                    }
                }
            }
            else {
                //System.out.println(directoryName+" is not a directory.");
            }
        }
        else {
            //System.out.println("Directory "+directoryName+" does not exist.");
        }
    }

    // a Jar method.
    private static void jarFile(String filePath, JarOutputStream jos, int iBaseFolderLength) {
        try {
            FileInputStream fis = new FileInputStream(filePath);
            BufferedInputStream bis = new BufferedInputStream(fis);
            JarEntry fileEntry = new JarEntry(filePath.substring(iBaseFolderLength));
            jos.putNextEntry(fileEntry);
            byte[] data = new byte[1024];
            int byteCount;
            while ((byteCount = bis.read(data, 0, 1024)) > -1) {
                jos.write(data, 0, byteCount);
            }
            } catch (IOException e) {
		}
    }

    private static void createJar(String checkDir, String outputPath, String manifestVerion, String mainClass, String classPath)
    {
    	FileOutputStream fos = null;
    	JarOutputStream jos = null;
    	int iBaseFolderLength = 0;

         try {
				String resultant = checkDir;
				Pattern p = Pattern.compile("\\/\\/");
				Matcher m = p.matcher(resultant);
				while (m.find())
				{
					resultant = m.replaceAll("/");
					m = p.matcher(resultant);
				}

                String strBaseFolder = resultant + File.separator ;
                iBaseFolderLength = strBaseFolder.length();
                fos = new FileOutputStream(outputPath);
                Manifest manifest =new Manifest();
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

                jos = new JarOutputStream(fos,manifest);

                checkDirectory(resultant, jos, iBaseFolderLength);
                // Close the file output streams
                jos.flush();
                jos.close();
                fos.close();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
    }

	public String execute(String command, String[] args, InputStream in, OutputStream out)
			throws CommandFailedException
	{
		StringBuffer buffer = new StringBuffer();

		if (command.equals(CMD_DEPLOY_ALC))
		{
                 	
			String urlName = null;
			String version = "1.0";
			String priority = "5";
			String appName ="";  
			if(args.length > 0)
				 appName = args[0];
			
			if (args.length == 4)
			{
				version = args[1];
				priority = args[2];
				urlName = args[3];
			}
			else if (args.length == 2)
				urlName = args[1];
			else
				return getUsage(CMD_DEPLOY_ALC);
			
			
			if(!urlName.startsWith("file") && !urlName.startsWith("http") ){
				return "Please specify the file protocol !!!";
			}

        	File compDir = null;
            String compDirName =
                com.baypackets.ase.util.Constants.ASE_HOME + "/alcjars/alc_deploy_tmp/" + appName ;
            compDir = new File (compDirName);
            compDir.mkdirs ();

			String sipXml = null;
			String sasXml = null;
                        String webXml = null; 

			try
			{
				sipXml = fileAsString(com.baypackets.ase.util.Constants.ASE_HOME + "/alcjars/sipxmltemplate.xml");
				sasXml = fileAsString(com.baypackets.ase.util.Constants.ASE_HOME + "/alcjars/sasxmltemplate.xml");
		                webXml = fileAsString(com.baypackets.ase.util.Constants.ASE_HOME + "/alcjars/webxmltemplate.xml"); 	
                        }
			catch (Exception e)
			{
				return e.getMessage();
			}


			ServiceContext sContext = new ServiceContext();
			sContext.setAttribute("ServletName", appName);
			sContext.setAttribute("ServletVersion", version);
			sContext.setAttribute("ServletPriority", priority);
			sContext.setAttribute("ServletClass", "com.genband.sip.ServiceDefinitionApp.ApplicationLogicControlSipService");
			sContext.setAttribute("HttpServletClass", "com.genband.ase.alc.http.ApplicationLogicControlHttpService");
			sContext.setAttribute("ALCFileURL", urlName);

			try
			{
				ServiceDefinition.CreateALCMLDefinition("__" + appName, new URL(urlName),false,false);
			}
			catch (Exception e)
			{
				return e.getMessage();
			}

			String context = "";
			String urlPattern ="";
			try
			{
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
							Pattern p = Pattern.compile(" xmlns:x0=\"http://www\\.w3\\.org/[0-9]+/XMLSchema\"");
							Matcher m = p.matcher(namespaceHack);
							patterns += m.replaceAll("") + "\n";
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
				
				
				sContext.setAttribute("HttpURLMapping", urlPattern);
				sContext.setAttribute("ServletMappings", patterns);
			}
			catch (Exception xmle)
			{
				return xmle.getMessage();
			}

			context += "<context-param>\n";
			context += "<param-name>ALCNameSpace</param-name>\n";
			context += "<param-value>"+appName+"</param-value>\n";
			context += "</context-param>\n";

			context += "<context-param>\n";
			context += "<param-name>ServiceURL</param-name>\n";
			context += "<param-value>"+urlName+"</param-value>\n";
			context += "</context-param>\n";

			sContext.setAttribute("ContextParms", context);

			try
			{
				(new File(compDirName + "/WEB-INF")).mkdirs();
			}
			catch (Exception e)
			{
				return e.getMessage();
			}


			try
			{
				File sipDotXml = new File(compDirName + "/WEB-INF/sip.xml");
				sipDotXml.createNewFile();
				PrintStream sipDotXmlOut = new PrintStream(sipDotXml);
				sipDotXmlOut.println(ALCMLExpression.toString(sContext, sipXml));
				sipDotXmlOut.close();

				File sasDotXml = new File(compDirName + "/WEB-INF/sas.xml");
				sasDotXml.createNewFile();
				PrintStream sasDotXmlOut = new PrintStream(sasDotXml);
				sasDotXmlOut.println(ALCMLExpression.toString(sContext, sasXml));
				sasDotXmlOut.close();
                                

                                 File webDotXml = new File(compDirName + "/WEB-INF/web.xml");
                                webDotXml.createNewFile();
                                PrintStream webDotXmlOut = new PrintStream(webDotXml);
                                webDotXmlOut.println(ALCMLExpression.toString(sContext, webXml));
                                webDotXmlOut.close();

			}

			catch (Exception e)
			{
				return e.getMessage();
			}

			try
			{
				createJar(compDirName, compDirName + "/../" + appName + ".sar", null, null, null);
			}
			catch (Exception e)
			{
				return e.getMessage();
			}

			try
			{
	               String path =""; 
                       
                         if(isWindows()) 
                        path= "file:" + compDirName + "/../" + appName + ".sar";	
                        else
                         path= "file://" + compDirName + "/../" + appName + ".sar"; 
                        
                          logger.info(" creating Stream from path...  " +path); 	

                        InputStream stream = new BufferedInputStream(new URL(path).openStream());
                              logger.info("The Stream is...alcml  "+stream ); 				
                                 DeployableObject app = null;
				if (args.length == 4)
					app = appDeployer.deploy(appName, args[1], Integer.parseInt(args[2]), null, stream, Deployer.CLIENT_TELNET);
				else if (args.length == 2)
					app = appDeployer.deploy(stream, Deployer.CLIENT_TELNET);

				appDeployer.start(app.getId());
			}
			catch (Exception e)
			{
				return e.toString();
			}

	        return "Telnet command "+ CMD_DEPLOY_ALC+" executed successfully.";

		}

		return buffer.toString();
	}

	public String getUsage(String command)
	{
			StringBuffer buffer = new StringBuffer();

			if (command.equals(CMD_DEPLOY_ALC))
			{
				buffer.append("Usage: " + CMD_DEPLOY_ALC + " <service name> [<version>] [<priority>] <ALC URL>");
				buffer.append("Deploys a service based on the alcml in the <ALC URL> parameter.");
			}
			return buffer.toString();
	}



         public static boolean isWindows(){
          if (System.getProperty("os.name").indexOf("Win") == 0)
          return true;
          else
                  return false;
          }
 
}
