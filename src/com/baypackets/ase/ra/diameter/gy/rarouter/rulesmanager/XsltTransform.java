package com.baypackets.ase.ra.diameter.gy.rarouter.rulesmanager;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Pattern;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.baypackets.ase.common.Registry;
import com.baypackets.ase.ra.diameter.gy.utils.Constants;
import com.baypackets.ase.util.FileUtils;
import com.baypackets.bayprocessor.slee.common.ConfigRepository;

public class XsltTransform extends DefaultHandler
{
	private static Logger logger = Logger.getLogger(XsltTransform.class);
	private String ruleCompDirName;
	private File ruleCompDir;
	private DefaultHandler handler;
	private SAXParserFactory factory = SAXParserFactory.newInstance();
	private SAXParser saxParser; 
	private Class servletRuleClass;
	private RuleObject tmpRuleObject;
	private ArrayList ruleObjectArray;
	private String propFileName;
	private boolean isSip = false;
	private String servletName = null;
	private String servletMappingName = null;
	private BufferedWriter xmlWriter = null;
	private boolean toWriteXml = false;
	private String appName;
	private int errno = 0;
	private boolean hasRules = false;
	private boolean hasAppName = false;
	private String nowParsing = null;
	private ClassLoader loader = null;
	private int seqNum;
	private String genClassName;
        private String osName;
	boolean isPatternDefined=false;

	public XsltTransform(ClassLoader loader, String rcDirName) {

		if (loader != null) {
			this.loader = loader;
		} else {
			this.loader = this.getClass().getClassLoader();
		}

		ruleCompDirName = rcDirName;

		try 
		{
			ruleCompDir = new File (ruleCompDirName);
			ruleCompDir.mkdirs ();
			handler = this;
			factory.setValidating(false);
			saxParser = factory.newSAXParser();
                        osName = System.getProperty("os.name");
			if(osName.startsWith("Windows")){
                        logger.debug("Operating system is windows");			
	                System.setProperty ("java.class.path", 
						System.getProperty("java.class.path")+";" +ruleCompDirName +";");
			}else{
                             logger.debug("Operating system is not windows");
			    System.setProperty ("java.class.path", 
					    System.getProperty("java.class.path")+":" +ruleCompDirName +":");
			 }  


			
			if(logger.isEnabledFor(Level.INFO))
			{
				logger.debug("Succesfully created directory and "+
						" set classpath for "+ruleCompDirName);
			}
		}
		catch (Exception e)
		{
			logger.debug("Ctor():Encountered an exception."+ e);
		} 
	} 

	private void transform (String classname) throws Exception	{
		
		try	{
		
			if(logger.isEnabledFor(Level.INFO))	
			{
				logger.debug("transform(): in for class "+classname);
			}

			InputStream xsltStream = 
				(this.getClass()).getResourceAsStream("generate_code.xsl");
			if(logger.isEnabledFor(Level.INFO))	
			{
				logger.debug("transform(): got the generate_code xslt handle " +xsltStream);
			}

			InputStream xmlStream = new FileInputStream (ruleCompDirName + classname + ".xml");
			String tmpclassName = ruleCompDirName+"1" + classname + ".java";
			String realclassNameFull = ruleCompDirName + classname + ".java";
			
			FileOutputStream outStream = new FileOutputStream (tmpclassName);
			
			Source xslSource = new StreamSource(xsltStream);
			Source xmlSource = new StreamSource(xmlStream);
			
			Result result = new StreamResult(outStream);
			
			TransformerFactory f = TransformerFactory.newInstance();
			Transformer t = f.newTransformer(xslSource);
			t.setOutputProperty(OutputKeys.INDENT, "yes");

			if(logger.isEnabledFor(Level.INFO))
			{
				logger.debug("transform(): before processing xsl for "+classname);
			}

			t.transform(xmlSource,result);

			if(logger.isEnabledFor(Level.INFO))	
			{
				logger.debug("transform(): succesfully processed "+classname);
			}

			outStream.close ();

			xsltStream.close ();
			xmlStream.close ();

			File file = new File (tmpclassName);
			BufferedReader fin = new BufferedReader (new FileReader (file));

			BufferedWriter fout = new BufferedWriter
			(new FileWriter (realclassNameFull));

			String line = fin.readLine ();	//ignore xml decl

			String clazzDecl =
				"import com.baypackets.ase.ra.diameter.gy.rarouter.rulesmanager.*;\n" +
				"import java.util.ArrayList;\n" +
				"import java.util.HashMap;\n" + 
				" public class " + classname + " extends RuleObject { ";
			
			fout.write (clazzDecl);
			
			while ((line = fin.readLine ()) != null) 
			{
				if ((line.indexOf("request.")>-1)&&(line.indexOf("param[")>-1))
				{
					line = flattenDots (line);
				} 

				fout.write (line);
			}

			if(logger.isEnabledFor(Level.INFO))	
			{
				logger.debug("transform():: succesfully generated java code for "+realclassNameFull);
			}

			fout.close ();
			fin.close ();
			file.delete ();

			// Compile the java code
			String clspath = this.getClassPath();

			String[] compArgs = new String[] {
					"-classpath", clspath,
					"-g",
					"-d", ruleCompDirName,
					realclassNameFull};

			ByteArrayOutputStream out = new ByteArrayOutputStream();
			PrintWriter pw = new PrintWriter(out, true);
			
			int compileStatus = com.sun.tools.javac.Main.compile(compArgs, pw);

			// Print compiler output
			if (logger.isDebugEnabled()) 
			{
				String compOutput = new String(out.toByteArray());
				logger.debug(compOutput);
			}

			pw.close();
			out.close();

			if(compileStatus != 0) {
				logger.debug("transform(): Compilation error occured"); 
				throw new Exception ("Exception occured while compiling rules");
			} 

			if(logger.isEnabledFor(Level.INFO))
			{
				logger.debug("transform(): succesfully compiled java code for "+
						realclassNameFull);
			}

			// now process the properties ordered list
			xsltStream = (this.getClass()).getResourceAsStream("generate_properties.xsl");
			xmlStream =	new FileInputStream (ruleCompDirName + classname + ".xml");
			
			tmpclassName = ruleCompDirName+"1" + classname + ".prop";
			realclassNameFull = ruleCompDirName + classname + ".prop";
			
			outStream = new FileOutputStream (tmpclassName);
			
			xslSource = new StreamSource(xsltStream);
			xmlSource = new StreamSource(xmlStream);
			result = new StreamResult(outStream);
			
			Transformer t1 = f.newTransformer(xslSource);
			t1.setOutputProperty(OutputKeys.INDENT, "yes");

			t1.transform(xmlSource,result);
			
			if(logger.isEnabledFor(Level.INFO))
			{
				logger.debug("transform(): succesfully processed xsl for props");
			}
			
			outStream.close ();
			xsltStream.close ();
			xmlStream.close ();
			
			file = new File (tmpclassName);
			fin = new BufferedReader (new FileReader (file));
			fout = new BufferedWriter (new FileWriter (realclassNameFull));
			
			line = fin.readLine ();	//ignore xml decl

			while ((line = fin.readLine ()) != null) {
				fout.write (line);
			}
			
			if(logger.isEnabledFor(Level.INFO))
			{
				logger.debug("transform(): succesfully created "+realclassNameFull);
			}

			fout.close ();
			fin.close ();
			file.delete ();
		}

		catch (SAXException ex)
		{
			logger.debug("transform():Encountered a SAX exception.."+ ex);
			throw ex;
		}
		catch (TransformerConfigurationException ex)
		{
			logger.debug("transform():Encountered a TransformerConfigurationException .."+ ex);
			throw ex;
		}
		catch (Exception e)
		{
			logger.debug("transform():encountered an exception in transforming.." + e);
			throw e;
		}
	}

	/**
	 * Called by the "transform" method to return the class path to be used by 
	 * the child process that will compile the generated triggering rule classes.
	 */
	private String getClassPath() {

		StringBuffer buffer = new StringBuffer(System.getProperty("java.class.path"));

		ConfigRepository config = (ConfigRepository)Registry.lookup(com.baypackets.ase.util.Constants.NAME_CONFIG_REPOSITORY);
		String jarDirs = config.getValue(com.baypackets.ase.util.Constants.PROP_RA_JARS_DIR);
		
		File[] jars = FileUtils.findFiles(jarDirs, ",", Pattern.compile(Constants.JAR_NAME), false);

		for (int i = 0; i < jars.length; i++) {
			buffer.append(jars[i].getAbsolutePath());
			if (i != jars.length - 1) {
				buffer.append(File.pathSeparator);
				//buffer.append(";");
			}
		}

		String classPath = buffer.toString();

		if (logger.isDebugEnabled()) {
			logger.debug("Using the following class path to generate the triggering rule classes:\n" + classPath);
		}

		return classPath;
	}

	// will take a input stream for the DD
	private ArrayList createAllServletFiles(FileInputStream deploymentDescriptor) throws Exception
	{
		if(logger.isEnabledFor(Level.INFO))
		{
			logger.debug("createAllServletFiles(): in for "+appName);
		}

		try
		{
			if(logger.isEnabledFor(Level.INFO))
			{
				logger.debug("createAllServletFiles(): created temp dir for "+
						appName);
			}
		
			ruleObjectArray = new ArrayList();
			errno = 0;
			
			saxParser.getXMLReader().setContentHandler(handler);
			saxParser.getXMLReader().parse(new InputSource(deploymentDescriptor));

			if (errno != 0)
			{
				throw new Exception 
				("Exception occured trying to parse the deployment descriptor");
			}

		}
		catch (Exception e)
		{
			logger.debug ("createAllServletFiles(): encountered exception "+ e);
			throw e;
		}
		return ruleObjectArray;
	}

	// will take a input stream for the DD
	private ArrayList createAllServletFiles
	(String appName, FileInputStream deploymentDescriptor) throws Exception
	{
		return null;
	}

	ArrayList createRuleObjects (FileInputStream dd)
	{
		ArrayList ruleObjArray = null;
		try
		{
			ruleObjArray = createAllServletFiles (dd);
		}
		catch (Exception e)
		{
			String msg = "createRuleObjects(): encountered exception";        

			try
			{
				dd.close ();
			}
			catch (Exception ex) {}

			logger.debug (msg+ e);
			throw new RuntimeException(msg + ": " + e.toString());      
		}
		return ruleObjArray;
	}

	HashMap createRuleObjects (String appName, FileInputStream dd)
	{
		return null;
	}


	private String flattenDots(String line) 
	{
		StringBuffer newStr = new StringBuffer();
		char chArr[] = line.toCharArray();
		int idx = line.indexOf ("request.");

		// copy till just before request.
		newStr.append (line.substring(0, idx));
		boolean done = false;

		// add RequestHelper.request to new chars
		newStr.append ("RequestHelper.request");

		for (int i=(idx+7); i<line.length(); i++) 
		{
			// even '-' dash is cannot be in varname
			if (!done && ((line.charAt(i)=='.')||(line.charAt(i)=='-')))
			{
				newStr.append('_');
			}
			else 
			{
				newStr.append(chArr[i]);
				if (chArr[i] == ']') done = true;  
			}
		}
		return  newStr.toString();
	}


	//===========================================================
	// SAX DocumentHandler methods
	//===========================================================

	public void startDocument()
	throws SAXException
	{
	}

	public void endDocument()
	throws SAXException
	{
	}

	public void startElement(String namespaceURI,
			String lName, // local name
			String qName, // qualified name
			Attributes attrs)
	throws SAXException
	{
		logger.debug("Inside startElement with namespaceURI="+namespaceURI +" lName=" +lName +" qName=" +qName +" attrs=" +attrs);
		try 
		{
			String eName = lName; // element name
			if ("".equals(eName)) {
				eName = qName; // namespaceAware = false
			}

			nowParsing = eName;

			if (eName.equals("app-mapping"))
			{
				hasRules = true;
				isPatternDefined=true;

				if(logger.isEnabledFor(Level.INFO))
				{
					logger.debug("startElement(): found "+
					"<app-mapping> tag. So is has rules");
				}
				
			}
			else if (eName.equals("app-name"))
			{
				logger.debug("startElement(): app-name found ");
				hasAppName = true;
			}
			else if (eName.equals("pattern") && hasRules)
			{
				servletName = appName + "_" + servletMappingName;
				this.genClassName = Constants.PROTOCOL +"Rule_" + this.seqNum++;

				xmlWriter = new BufferedWriter(new FileWriter (ruleCompDirName + this.genClassName + ".xml"));
				toWriteXml = true;
				
				xmlWriter.write("<pattern>");
				xmlWriter.newLine();
				
				if(logger.isEnabledFor(Level.INFO))
				{
					logger.debug("startElement(): setting toWriteXml to true ");
				}
			}
			else if (toWriteXml)
			{
				xmlWriter.write("<"+eName);
				if (attrs != null) {
					for (int i = 0; i < attrs.getLength(); i++) {
						String aName = attrs.getLocalName(i); // Attr name
						if ("".equals(aName)) aName = attrs.getQName(i);
						xmlWriter.write(" ");
						xmlWriter.write(aName+"=\""+attrs.getValue(i)+"\"");
					}
				}
				
				xmlWriter.write(">");
				xmlWriter.newLine();
				xmlWriter.flush();
			}
		}
		catch (Exception e) 
		{
			errno = -1;
			logger.debug("startElement():Encountered an exception."+ e);
		}
	}


	public void endElement(String namespaceURI,
			String sName, // simple name
			String qName  // qualified name
	)
	throws SAXException
	{
		nowParsing = null;
		try 
		{
			String eName = sName;
			if ("".equals(eName)) {
				eName = qName; // namespaceAware = false
			}
			if ((eName.equals("app-mapping"))&& hasRules)
			{
				toWriteXml = false;
				hasRules = false;
				xmlWriter.flush();
				xmlWriter.close();
				if(logger.isEnabledFor(Level.INFO))
				{
					logger.debug ("endElement(): app xml "+
							ruleCompDirName + this.genClassName + ".xml created.");
				}
				if (this.genClassName != null)
				{
					transform (this.genClassName);
					if(logger.isEnabledFor(Level.INFO))
					{
						logger.debug("endElement(): transform OK "+
								this.genClassName);
					}
					// the servlet name is known here and you can
					// populate you data structures here.

					servletRuleClass = Class.forName(this.genClassName, true, this.loader);

					if(logger.isEnabledFor(Level.INFO))
					{
						logger.debug("endElement(): loaded classes "+
								this.genClassName);
					}
					tmpRuleObject = (RuleObject) servletRuleClass.newInstance ();

					if(logger.isEnabledFor(Level.INFO))
					{
						logger.debug("endElement(): instantiated "+
								this.genClassName);
					}
					propFileName = ruleCompDirName + this.genClassName + ".prop";
					BufferedReader propReader =
						new BufferedReader (new FileReader (propFileName));
					String tmpPropLine = null;
					String propLine = "";
					while ((tmpPropLine = propReader.readLine ()) != null)
					{
						propLine += tmpPropLine;
					}

					// prime the rule object
					if(logger.isEnabledFor(Level.INFO))
					{
						logger.debug("endElement(): created props "+
								this.genClassName + " --- "+propLine);
					}
					tmpRuleObject.setBitField (propLine);
					tmpRuleObject.setName (servletName);
					tmpRuleObject.setAppName (appName);

					ruleObjectArray.add(tmpRuleObject);
					if(logger.isEnabledFor(Level.INFO))
					{
						logger.debug("endElement():Adding instance of " +
								this.genClassName);
					}
					servletName = null;
					this.genClassName = null;
				}
			}
			else if (eName.equals("app-name"))
			{
				logger.debug("endElement(): app-name found ");
				hasAppName = true;
			}
			else if (toWriteXml)
			{
				xmlWriter.write("</"+eName+">");
				xmlWriter.newLine();
				xmlWriter.flush();
			}
		}
		catch (Exception e) 
		{
			errno = -1;
			logger.debug("endElement():Encountered an exception."+ e);
		}
	}



	public void characters(char buf[], int offset, int len)
	throws SAXException
	{ 
		try 
		{
			String s = new String(buf, offset, len);

			if (nowParsing != null && nowParsing.equals("app-name") && hasAppName)
			{
				appName = s.replaceAll("\n","");
				if(logger.isEnabledFor(Level.INFO))
				{
					logger.debug ("characters(): The app Mapping name is "+appName);
				}
			}
			else if (toWriteXml)
				xmlWriter.write(s);
		}
		catch (Exception e) 
		{
			errno = -1;
			logger.debug("characters():Encountered an exception."+ e);
		}
	}
}
