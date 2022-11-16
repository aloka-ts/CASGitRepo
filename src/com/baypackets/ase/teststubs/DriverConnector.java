
package com.baypackets.ase.teststubs;

import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

import javax.servlet.sip.Address;
import javax.servlet.sip.URI;

import org.apache.log4j.Logger;

import com.baypackets.ase.common.AseMessage;
import com.baypackets.ase.common.Registry;
import com.baypackets.ase.dispatcher.DispatcherImpl;
import com.baypackets.ase.spi.util.CommandFailedException;
import com.baypackets.ase.spi.util.CommandHandler;
import com.baypackets.ase.util.Constants;
import com.baypackets.ase.util.TelnetServer;

public class DriverConnector implements CommandHandler {
  private static Logger logger = Logger.getLogger(DriverConnector.class);
  Properties input;
  DummySipConnector  dsp;
  InputStream is;
  private String sipMsgFileName =
    com.baypackets.ase.util.Constants.ASE_HOME + "/tmp/SIP.msg";

  
  /**
   *
   *
   */
  public DriverConnector () 
  {
    ((TelnetServer) (Registry.lookup (Constants.NAME_TELNET_SERVER))).
      registerHandler ("send-request", this, true);
     input = new Properties();
     dsp = new DummySipConnector();
  }

  
  /**
   *
   *
   */
  public String execute (String command,
 		       String args[], InputStream in, OutputStream out) throws CommandFailedException
  {
    if (command.equals("send-request"))
    {  
     try 
     {
        is = new FileInputStream (sipMsgFileName);
        input.load (is);
        URI requestURI = new DummySipUri (
        input.getProperty("request.uri.user"),
        input.getProperty("request.uri.host"),
        Integer.parseInt(input.getProperty("request.uri.port")) ,
        null,
        null,
        input.getProperty ("request.uri.scheme")
        );


      URI fromURI  = new DummySipUri ( 
        input.getProperty("request.from.uri.user"),
        input.getProperty("request.from.uri.host"),
        Integer.parseInt(input.getProperty("request.from.uri.port")) ,
        null,
        null,
        input.getProperty ("request.from.uri.scheme")
        );

      URI toURI  = new DummySipUri ( 
        input.getProperty("request.to.uri.user"),
        input.getProperty("request.to.uri.host"),
        Integer.parseInt(input.getProperty("request.to.uri.port")) ,
        null,
        null,
        input.getProperty ("request.to.uri.scheme")
        );

      Address fromAddr = new DummyAddress (fromURI, 
        input.getProperty ("request.from.display-name"));


      Address toAddr = new DummyAddress (toURI, 
        input.getProperty ("request.to.display-name"));
      
      DummySipServletRequest request = new DummySipServletRequest (
        requestURI,
        Integer.parseInt ( input.getProperty("maxForwards")),
        ((input.getProperty("initial").equals("true")) ? true : false),
        input.getProperty("serverHost"),
        Integer.parseInt(input.getProperty("serverPort")),         
	input.getProperty("remoteAddress"),
	input.getProperty("remoteHost"),
	Integer.parseInt(input.getProperty("remotePort")),
        input.getProperty("localAddress"),
	Integer.parseInt(input.getProperty("localPort")),
	fromAddr,
	toAddr,
	input.getProperty("method"),
	input.getProperty("callId"),
	Integer.parseInt(input.getProperty("expires")),
	input.getProperty("remoteUser"),
        (new DispatcherImpl()),
	(new DummySipSession("test"))
      );
      
      dsp.sendToContainer (new AseMessage (request)); 
      return ("Message sent to Container \n "+ request);
      }
      catch (Exception e ) {
	logger.error(e.getMessage(), e);
        return "Error sending the request..\n";
      }
      finally {
        try {
          is.close();
        }
        catch (Exception ex) {
		logger.error(ex.getMessage(), ex);
        }
      }
    }
    return null;
  }
  
  
  /**
   *
   *
   */
  public String getUsage(String command) {
      // WIP
      return null;
  }  
  
}
