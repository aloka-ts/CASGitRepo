/*------------------------------------------
* SIP Rules Code generator
* Nasir
* Version 1.0   08/19/04
* BayPackets Inc.
* Revisions:
* BugID : Date : Info
*
* BPUsa06502_18 : 10/08/04 : This change is to
* take into account the arbitrary uri params
* that can be included in the request as per
* section 6.6.1 of Sip Servlet Specification
* We have added the ability to add upto 10
* arbitrary parameters in a request and trigger
* the application based on this. 
* We are taking the params as _1.._10 and keeping
* a list of parameters with actual names in an
* arraylist in this RuleObject. The props are 
* the actual names but the generated code is 
* modified to have _1 etc.
*
* BPUsa06724_1 : 10/14/04 : Uses a new classloader 
* to load the Rule Object to refresh the rules class
* if the rules are changed and we re-deploy
*
* BPUsa06759_3 : 10/26/2004 : Modified generation of rule matching class to 
*   use [appName]_[seqNum] as the generated class's name instead of the 
*   Servlet's name.  This was done to prevent possible compilation errors 
*   due to Servlet names with white space or special characters.  
*
* BPInd19694 : 01/31/2008 : Modified code to use Transformer and TransformerFactory
* and replacing XSLTProcessor and XSLTProcessorFactory as JDK1.6 uses this approach
* which is also same as used by latest xerces from apache.
*
*------------------------------------------*/

package com.baypackets.ase.dispatcher;

import org.xml.sax.*;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.SAXParserFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.transform.*;
import javax.xml.transform.stream.*;

import java.io.*;
import java.util.*;
import java.util.regex.Pattern;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import com.baypackets.ase.container.*;
import com.baypackets.ase.container.exceptions.DeploymentFailedException;
import com.baypackets.ase.util.*;
import com.baypackets.ase.common.*;
import com.baypackets.bayprocessor.slee.common.ConfigRepository;

public class XsltTransform extends DefaultHandler
{
  private static Logger _logger =
    Logger.getLogger(XsltTransform.class);
  private String ruleCompDirName;
  private File ruleCompDir;
  private DefaultHandler handler;
  private SAXParserFactory factory = 
    SAXParserFactory.newInstance();
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
  private String nowParsing = null;
  private ClassLoader loader = null;
  private int seqNum;
  private String genClassName;
  //This boolean variable captures that only of the elements servlet-mapping or mail-servlet should be defined
  //amit
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
      saxParser.getXMLReader().setEntityResolver(new SipXmlEntityResolver());
      //saxParser.getXMLReader().setFeature (
      //   "http://xml.org/sax/features/validation", false);
      System.setProperty (AseStrings.PROP_JAVA_CLASS_PATH, 
          System.getProperty(AseStrings.PROP_JAVA_CLASS_PATH)+AseStrings.COLON+ruleCompDirName);
      if(_logger.isEnabledFor(Level.INFO))
        {
          _logger.info("Ctor(): Succesfully created directory and "+
             " set classpath for "+ruleCompDirName);
        }
     }
     catch (Exception e)
     {
      _logger.error("Ctor():Encountered an exception.", e);
     } 
  } 

	private void transform (String classname) throws Exception	{
		try	{
      		if(_logger.isEnabledFor(Level.INFO))	{
          		_logger.info("transform(): in for servlet "+classname);
        	}
      
      	InputStream xsltStream = 
       		(this.getClass()).getResourceAsStream("generate_code.xsl");
      	if(_logger.isEnabledFor(Level.INFO))	{
        	_logger.info("transform(): got the generate_code xslt handle");
        }

      	InputStream xmlStream =
			new FileInputStream (ruleCompDirName + classname + ".xml");
      	String tmpclassName = ruleCompDirName+"1" + classname + ".java";
      	String realclassNameFull = ruleCompDirName + classname + ".java";
		FileOutputStream outStream = new FileOutputStream (tmpclassName);
		Source xslSource = new StreamSource(xsltStream);
		Source xmlSource = new StreamSource(xmlStream);
		Result result = new StreamResult(outStream);
		TransformerFactory f = TransformerFactory.newInstance();
		Transformer t = f.newTransformer(xslSource);
		t.setOutputProperty(OutputKeys.INDENT, "yes");

    	if(_logger.isEnabledFor(Level.INFO))	{
    		_logger.info("transform(): before processing xsl for "+classname);
    	}
		t.transform(xmlSource,result);

   		if(_logger.isEnabledFor(Level.INFO))	{
   			_logger.info("transform(): succesfully processed "+classname);
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
			"import com.baypackets.ase.dispatcher.*;\n" + 
     		"import java.util.ArrayList;\n" +
     		"import java.util.HashMap;\n" + 
        	" public class " + classname + " extends RuleObject { ";
        fout.write (clazzDecl);
      	while ((line = fin.readLine ()) != null) 
      	{
        if ((line.indexOf("request.")>-1)&&(line.indexOf("param[")>-1))
          {
            
            if (line.indexOf("request.uri.param")>-1)
            {
              if(_logger.isEnabledFor(Level.INFO))
                {
                  _logger.info("transform(): found a request_uri_param_ in string");
                }
            }
            
            else if (line.indexOf("request.to.uri.param")>-1)
            {
              if(_logger.isEnabledFor(Level.INFO))
                {
                  _logger.info("transform(): found a request_to_uri_param_ in string");
                }
            }
            
            else if (line.indexOf("request.from.uri.param")>-1)
            {
              if(_logger.isEnabledFor(Level.INFO))
                {
                  _logger.info("transform(): found a request_from_uri_param_ in string");
                }
            }
            else
              line = flattenDots (line);
          } 
	      fout.write (line);
      }

      if(_logger.isEnabledFor(Level.INFO))	{
          _logger.info("transform(): succesfully generated java code for "+
            realclassNameFull);
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
		if (_logger.isDebugEnabled()) {
			String compOutput = new String(out.toByteArray());
			_logger.debug(compOutput);
		}

		pw.close();
		out.close();

      if(compileStatus != 0) {
        _logger.error("transform(): Compilation error occured"); 
        throw new Exception ("Exception occured while compiling rules");
      } 

      if(_logger.isEnabledFor(Level.INFO))
        {
          _logger.info("transform(): succesfully compiled java code for "+
            realclassNameFull);
        }

      	// now process the properties ordered list
        xsltStream = 
       		(this.getClass()).getResourceAsStream("generate_properties.xsl");
        xmlStream =
			new FileInputStream (ruleCompDirName + classname + ".xml");
        tmpclassName = ruleCompDirName+"1" + classname + ".prop";
        realclassNameFull = ruleCompDirName + classname + ".prop";
        outStream = new FileOutputStream (tmpclassName);

		xslSource = new StreamSource(xsltStream);
    	xmlSource = new StreamSource(xmlStream);
    	result = new StreamResult(outStream);
		Transformer t1 = f.newTransformer(xslSource);
		t1.setOutputProperty(OutputKeys.INDENT, "yes");

		t1.transform(xmlSource,result);
        if(_logger.isEnabledFor(Level.INFO))
          {
            _logger.info("transform(): succesfully processed xsl for props");
          }
        outStream.close ();
        xsltStream.close ();
        xmlStream.close ();
        file = new File (tmpclassName);
        fin = new BufferedReader (new FileReader (file));
        fout = new BufferedWriter (new FileWriter (realclassNameFull));
        line = fin.readLine ();	//ignore xml decl

        while ((line = fin.readLine ()) != null)
	  		fout.write (line);
        if(_logger.isEnabledFor(Level.INFO))
          {
            _logger.info("transform(): succesfully created "+realclassNameFull);
          }

        fout.close ();
        fin.close ();
        file.delete ();
    }

    catch (SAXException ex)
    {
      _logger.error("transform():Encountered a SAX exception..", ex);
      throw ex;
    }
    catch (TransformerConfigurationException ex)
    {
      _logger.error("transform():Encountered a TransformerConfigurationException ..", ex);
      throw ex;
    }
    catch (Exception e)
    {
      _logger.error("transform():encountered an exception in transforming..",
        e);
      throw e;
    }
  }

/**
* Commented by Rajendra in favor of using new way to transform xml to java
* using Transformer and TransformerFactory
*/

/*
 private void transform (String classname) throws Exception
  {
    try
    {
      if(_logger.isEnabledFor(Level.INFO))
        {
          _logger.info("transform(): in for servlet "+classname);
        }
                                                                                                                                                
      // first process the java code
      XSLTProcessor processor =
    XSLTProcessorFactory.getProcessor (new org.apache.xalan.xpath.xdom.
                       XercesLiaison ());
      InputStream xsltStream =
       (this.getClass()).getResourceAsStream("generate_code.xsl");
      if(_logger.isEnabledFor(Level.INFO))
        {
          _logger.info("transform(): got the generate_code xslt handle");
        }
                                                                                                                                                
      InputStream xmlStream =
    new FileInputStream (ruleCompDirName + classname + ".xml");
      StylesheetRoot parsedStylesheet =
    processor.processStylesheet (new XSLTInputSource (xsltStream));
                                                                                                                                                
        processor.setStylesheet (parsedStylesheet);
      String tmpclassName = ruleCompDirName+"1" + classname + ".java";
      String realclassNameFull = ruleCompDirName + classname + ".java";
      OutputStream outStream = new FileOutputStream (tmpclassName);
      if(_logger.isEnabledFor(Level.INFO))
        {
          _logger.info("transform(): before processing xsl for "+classname);
        }
                                                                                                                                                
      processor.process (new XSLTInputSource (xmlStream), null,
               new XSLTResultTarget (outStream));
      if(_logger.isEnabledFor(Level.INFO))
        {
          _logger.info("transform(): succesfully processed "+classname);
        }
                                                                                                                                                
        outStream.close ();
        xsltStream.close ();
        xmlStream.close ();
File file = new File (tmpclassName);
      BufferedReader fin = new BufferedReader (new FileReader (file));
      BufferedWriter fout = new BufferedWriter
    (new FileWriter (realclassNameFull));
      String line = fin.readLine ();    //ignore xml decl
      String clazzDecl =
    "import com.baypackets.ase.dispatcher.*;\n" +
     "import java.util.ArrayList;\n" +
     "import java.util.HashMap;\n" +
        " public class " + classname + " extends RuleObject { ";
        fout.write (clazzDecl);
      while ((line = fin.readLine ()) != null)
      {
        if ((line.indexOf("request.")>-1)&&(line.indexOf("param[")>-1))
          {
                                                                                                                                                
            if (line.indexOf("request.uri.param")>-1)
            {
              if(_logger.isEnabledFor(Level.INFO))
                {
                  _logger.info("transform(): found a request_uri_param_ in string");
                }
            }
                                                                                                                                                
            else if (line.indexOf("request.to.uri.param")>-1)
            {
              if(_logger.isEnabledFor(Level.INFO))
                {
                  _logger.info("transform(): found a request_to_uri_param_ in string");
                }
            }
                                                                                                                                                
            else if (line.indexOf("request.from.uri.param")>-1)
            {
              if(_logger.isEnabledFor(Level.INFO))
                {
                  _logger.info("transform(): found a request_from_uri_param_ in string");
                }
            }
            else
              line = flattenDots (line);
          }
          fout.write (line);
      }
if(_logger.isEnabledFor(Level.INFO))
        {
          _logger.info("transform(): succesfully generated java code for "+
            realclassNameFull);
        }
                                                                                                                                                
        fout.close ();
        fin.close ();
        file.delete ();
                                                                                                                                                
        // Compile the java code
        String clspath = '"' + this.getClassPath() + '"';
        String[] compArgs = new String[] {
                        "-classpath", clspath,
                        "-g",
                        "-d", ruleCompDirName,
                        realclassNameFull};
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PrintWriter pw = new PrintWriter(out, true);
        int compileStatus = com.sun.tools.javac.Main.compile(compArgs, pw);
                                                                                                                                                
        // Print compiler output
        if (_logger.isDebugEnabled()) {
            String compOutput = new String(out.toByteArray());
            _logger.debug(compOutput);
        }
                                                                                                                                                
        pw.close();
        out.close();
                                                                                                                                                
      if(compileStatus != 0) {
        _logger.error("transform(): Compilation error occured");
        throw new Exception ("Exception occured while compiling rules");
      }
                                                                                                                                                
      if(_logger.isEnabledFor(Level.INFO))
        {
          _logger.info("transform(): succesfully compiled java code for "+
            realclassNameFull);
        }
 // now process the properties ordered list
        xsltStream =
       (this.getClass()).getResourceAsStream("generate_properties.xsl");
        xmlStream =
    new FileInputStream (ruleCompDirName + classname + ".xml");
        parsedStylesheet =
    processor.processStylesheet (new XSLTInputSource (xsltStream));
        processor.setStylesheet (parsedStylesheet);
        tmpclassName = ruleCompDirName+"1" + classname + ".prop";
        realclassNameFull = ruleCompDirName + classname + ".prop";
        outStream = new FileOutputStream (tmpclassName);
        processor.process (new XSLTInputSource (xmlStream), null,
               new XSLTResultTarget (outStream));
        if(_logger.isEnabledFor(Level.INFO))
          {
            _logger.info("transform(): succesfully processed xsl for props");
          }
        outStream.close ();
        xsltStream.close ();
        xmlStream.close ();
        file = new File (tmpclassName);
        fin = new BufferedReader (new FileReader (file));
        fout = new BufferedWriter (new FileWriter (realclassNameFull));
        line = fin.readLine (); //ignore xml decl
                                                                                                                                                
        while ((line = fin.readLine ()) != null)
      fout.write (line);
        if(_logger.isEnabledFor(Level.INFO))
          {
            _logger.info("transform(): succesfully created "+realclassNameFull);
          }
                                                                                                                                                
        fout.close ();
        fin.close ();
        file.delete ();
    }
                                                                                                                                                
    catch (SAXException ex)
    {
      _logger.error("transform():Encountered a SAX exception..", ex);
      throw ex;
    }
    catch (Exception e)
    {
      _logger.error("transform():encountered an exception in transforming..",
        e);
      throw e;
    }
  }

*/



	/**
	 * Called by the "transform" method to return the class path to be used by 
	 * the child process that will compile the generated triggering rule classes.
	 */
	private String getClassPath() {
		StringBuffer buffer = new StringBuffer(System.getProperty(AseStrings.PROP_JAVA_CLASS_PATH));

		ConfigRepository config = (ConfigRepository)Registry.lookup(com.baypackets.ase.util.Constants.NAME_CONFIG_REPOSITORY);
		String jarDirs = config.getValue(com.baypackets.ase.util.Constants.PROP_ASE_JAR_DIRS);
		
		File[] jars = FileUtils.findFiles(jarDirs, AseStrings.COMMA, Pattern.compile(".*.jar"), false);
		
		
		for (int i = 0; i < jars.length; i++) {
			
			buffer.append(jars[i].getAbsolutePath());
			
			if (i != jars.length - 1) {
				buffer.append(File.pathSeparator);
			}
		}
		

		String classPath = buffer.toString();
		

		//if (_logger.isDebugEnabled()) {
		//	_logger.debug("Using the following class path to generate the triggering rule classes:\n" + classPath);
		//}
		
		return classPath;
	}


  // will take a input stream for the DD
  private ArrayList createAllServletFiles
    (String appName, FileInputStream deploymentDescriptor) throws Exception
  {
    if(_logger.isEnabledFor(Level.INFO))
      {
        _logger.info("createAllServletFiles(): in for "+appName);
      }
    this.appName = appName;
    try
    {
      if(_logger.isEnabledFor(Level.INFO))
        {
          _logger.info("createAllServletFiles(): created temp dir for "+
             appName);
        }
      ruleObjectArray = new ArrayList ();
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
      _logger.error ("createAllServletFiles(): encountered exception", e);
      throw e;
    }
    return ruleObjectArray;
  }




  ArrayList createRuleObjects (String appName, FileInputStream dd)
  {
    ArrayList ruleObjArray = null;
    try
    {
      ruleObjArray = createAllServletFiles (appName, dd);
    }
    catch (Exception e)
    {
      String msg = "createRuleObjects(): encountered exception";        
      
      try
      {
	dd.close ();
      }
      catch (Exception ex) {}
      
      _logger.error (msg, e);
      throw new RuntimeException(msg + ": " + e.toString());      
    }
    return ruleObjArray;
  }

  // function that replaces say request_uri_param_service with request_uri_param_1
  // BPInd16460 No need for this function anymore.Now applications
  //can be triggered by the parameter naem itself 
  /*private String updateUriParamName (String line)
  {
    int count = 0;
    int sidx = 0, idx=0;
    StringBuffer newStr = new StringBuffer();
    while ((idx = line.indexOf ("request_uri_param_", sidx))>-1)
    {
      newStr.append (line.substring(sidx, idx));
      newStr.append ("request_uri_param_");
      newStr.append (++count);
      sidx = line.indexOf ("]", idx);
    }
    // rest of the line 
    newStr.append(line.substring(sidx, line.length()));
    return newStr.toString();
  }*/

  // replace (.) with (_) only in variable names and add
  // RequestHelper. in front of vars. 

  private String flattenDots(String line) 
  {
    StringBuffer newStr = new StringBuffer();
    char chArr[] = line.toCharArray();
    int idx = line.indexOf ("request.");

    // copy till just befor request.
    newStr.append (line.substring(0, idx));
    boolean done = false;

    // add RequestHelper.request to new chars
    newStr.append ("RequestHelper.request");

    for (int i=(idx+7); i<line.length(); i++) 
      {
        // even '-' dash is cannot be in varname
        if (!done && ((line.charAt(i)=='.')||(line.charAt(i)=='-')))
          {
            newStr.append(AseStrings.CHAR_UNDERSCORE);
          }
        else 
          {
            newStr.append(chArr[i]);
            if (chArr[i] == AseStrings.SQUARE_BRACKET_CHAR_CLOSE) done = true;  
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
      try 
      {
        String eName = lName; // element name
        if ("".equals(eName)) eName = qName; // namespaceAware = false

        nowParsing = eName;

        if(_logger.isEnabledFor(Level.INFO))
          {
            _logger.info("startElement(): found -- "+eName
            +" -- tag." );
          }
          
        if (eName.equals("sip-app")) 
        {
          isSip = true;
          if(_logger.isEnabledFor(Level.INFO))
            {
              _logger.info("startElement(): found "+
              "<sip-app> tag. So is a SIP application");
            }
        }
        else if (eName.equals("servlet-mapping"))
        {
         if(isPatternDefined){
        		throw new DeploymentFailedException("<main-servlet> and <servlet-mapping> cannot coexist in sip xml file");
        	}
          hasRules = true;
           isPatternDefined=true;
          if(_logger.isEnabledFor(Level.INFO))
            {
              _logger.info("startElement(): found "+
              "<servlet-mapping> tag. So is has rules");
            }
        }else if (eName.equals("sip-white-list")){
		isSip = true;
		hasRules = true;		
	}
        
        else if (eName.equals("pattern") && isSip && hasRules)
        {
          servletName = appName + "_" + servletMappingName;
          //this.genClassName = this.appName + "_" + this.seqNum++;
          this.genClassName = "Rule_" + this.seqNum++;
          xmlWriter = new BufferedWriter
            (new FileWriter (ruleCompDirName + this.genClassName + ".xml"));
          toWriteXml = true;
          xmlWriter.write("<pattern>");
          xmlWriter.newLine();
          if(_logger.isEnabledFor(Level.INFO))
            {
              _logger.info("startElement(): setting toWriteXml to true ");
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
        }else if(eName.equals("main-servlet")){
        	//throw exception if servlet mapping is already defined
        	if(isPatternDefined){
        		throw new DeploymentFailedException("<main-servlet> and <servlet-mapping> cannot coexist in sip xml file");
        	}
        	
          isPatternDefined=true;
        }
      }
      catch (Exception e) 
      {
        errno = -1;
       _logger.error("startElement():Encountered an exception.", e);
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
        if ("".equals(eName)) eName = qName; // namespaceAware = false
        if ((eName.equals("servlet-mapping") || eName.equals("sip-white-list"))&& isSip && hasRules)
        {
          toWriteXml = false;
          hasRules = false;
          xmlWriter.flush();
          xmlWriter.close();
          if(_logger.isEnabledFor(Level.INFO))
          {
             _logger.info ("endElement(): servlet xml "+
             ruleCompDirName + this.genClassName + ".xml created.");
          }
          if (this.genClassName != null)
            {
              transform (this.genClassName);
              if(_logger.isEnabledFor(Level.INFO))
                {
                  _logger.info("endElement(): transform OK "+
                    this.genClassName);
                }
              // the servlet name is known here and you can
              // populate you data structures here.
              servletRuleClass = Class.forName(this.genClassName, true, this.loader);

              if(_logger.isEnabledFor(Level.INFO))
                {
                  _logger.info("endElement(): loaded classes "+
                     this.genClassName);
                }
              tmpRuleObject = (RuleObject) servletRuleClass.newInstance ();
              if(_logger.isEnabledFor(Level.INFO))
                {
                  _logger.info("endElement(): instantiated "+
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
              if(_logger.isEnabledFor(Level.INFO))
                {
                  _logger.info("endElement(): created props "+
                    this.genClassName + " --- "+propLine);
                }
              tmpRuleObject.setBitField (propLine);
              tmpRuleObject.setName (servletName);
              tmpRuleObject.setAppName (appName);
              tmpRuleObject.setServletName (servletMappingName);

              ruleObjectArray.add (tmpRuleObject);
              if(_logger.isEnabledFor(Level.INFO))
                {
                  _logger.info("endElement():Adding instance of " +
                    this.genClassName);
                }
              servletName = null;
              this.genClassName = null;
            }
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
       _logger.error("endElement():Encountered an exception.", e);
      }
    }



    public void characters(char buf[], int offset, int len)
    throws SAXException
    { 
      try 
      {
        String s = new String(buf, offset, len);

        if (nowParsing != null && nowParsing.equals("servlet-name") && 
            isSip && hasRules)
        {
          servletMappingName = s.replaceAll("\n","");
          if(_logger.isEnabledFor(Level.INFO))
           {
             _logger.info ("characters(): The servletMapping name is "+
               servletMappingName);
           }
        }
        else if (toWriteXml)
          xmlWriter.write(s);
      }
      catch (Exception e) 
      {
        errno = -1;
       _logger.error("characters():Encountered an exception.", e);
      }
    }








  public static void main (String args[])
  {
    XsltTransform xslt = new XsltTransform (null, "App");
    try
    {
      FileInputStream fis = new FileInputStream ("input.xml");
      System.out.println (xslt.createRuleObjects ("App", fis));
    } catch (Exception e)
    {
    };
  }


}
