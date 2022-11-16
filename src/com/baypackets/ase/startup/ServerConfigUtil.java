/*------------------------------------------------------*
 * ServerConfigUtil : Used to parse the server-config.xml
 * this utility can be used by any other class to get 
 * different attributes from server.xml  NK
 *
 *------------------------------------------------------*/
package com.baypackets.ase.startup;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.log4j.spi.LoggerFactory;
import org.apache.log4j.xml.DOMConfigurator;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.baypackets.ase.util.Constants;


public final class ServerConfigUtil
{

  private static Logger logger = Logger.getLogger (ServerConfigUtil.class);
  private static ServerConfigUtil _self; 
  private Document doc = null;

  private ServerConfigUtil ()
  {
    initialize();
  }


  public static ServerConfigUtil instance()
  {
    if (_self == null)
    {
      _self = new ServerConfigUtil();
    }
    return _self; 
  }




  private void initialize () 
  {
    //Get the server-config file's name
    String fileName = Constants.FILE_CONFIG;

    //If the system property is used, 
    //load the configuration from the file specified
    //Else print error message
    if (fileName != null)
      {
	  if(logger.isInfoEnabled())
		logger.info ("Loading the required element info from :" +
		     fileName);
        try 
        {
	  doc = this.getXMLDocument (fileName);
        }
        catch (Exception e)
        {
          logger.
          error("Exception occured in reading config XML file.", e);
        }
      }
    else
      {
	logger.
	  error
	  ("No server-config file present...");
      }
  }


  public NodeList getNodeList (String tag)
  {
    if (logger.isEnabledFor (Level.INFO))
      {
        logger.info ("Returning the node "+tag);
      }
    Element rootEl = doc.getDocumentElement ();
    return rootEl.getElementsByTagName (tag);
  }

  


  private Document getXMLDocument (String name) throws Exception
  {
    InputStream stream = null;
    Document doc = null;
      try
    {
      stream = this.getClass ().getResourceAsStream (name);

      //Parse the XML file and create the Document object
      DocumentBuilderFactory factory = null;
      factory = DocumentBuilderFactory.newInstance ();
      DocumentBuilder builder = factory.newDocumentBuilder ();
      doc = builder.parse (stream);
    }
    finally
    {
      if (stream != null)
	stream.close ();
    }
    return doc;
  }

}
