/*
 * SampleApp.java
 *
 * Created on August 16, 2004, 4:51 PM
 */
package com.baypackets.ase.container.tests;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

import org.apache.log4j.Logger;

import com.baypackets.ase.common.Registry;
import com.baypackets.ase.container.AseHost;
import com.baypackets.ase.spi.util.CommandFailedException;
import com.baypackets.ase.spi.util.CommandHandler;
import com.baypackets.ase.util.AseStrings;
import com.baypackets.ase.util.AseTraceService;
import com.baypackets.ase.util.Constants;
import com.baypackets.ase.util.TelnetServer;


/**
 * Provides a sample app to test the functionality of the Agility Servlet
 * Engine's container sub-system.
 *
 * @author Zoltan Medveczky
 */
public final class SampleApp {

    private static Logger _logger = Logger.getLogger(SampleApp.class);

    /**
     *
     */
    public static void main(String[] args) throws Exception {
        _logger.debug("main()");

	// read the parameters for running the app from a properties file
        Properties props = new Properties();
        props.load(new FileInputStream(System.getProperty("params.properties")));

	// instantiate the TelnetServer and bind it to the Registry
        TelnetServer server = new TelnetServer(Integer.parseInt(props.getProperty("port")));
        Registry.bind(Constants.NAME_TELNET_SERVER, server);

        // register a dummy handler with the TelnetServer
        server.registerHandler("echo", new EchoCommand());    
        
        // initialize the log4j adapter
        new AseTraceService().initialize();
        
        // Bind an implementation of the ContextDAO to the Registry.
        // This will be used by the AseHost to find and persist AseContexts.
        //Class daoClass = Class.forName(props.getProperty(Constants.CONTEXT_DAO));
        //Registry.bind(Constants.CONTEXT_DAO, daoClass.newInstance());

        //initialize an AseHost object
        AseHost host = new AseHost("SampleHost");
        //host.setHostDir(new File(props.getProperty("hostDir")));
        host.start(); // host will register itself with the TelnetServer here

        _logger.debug("started AseHost....");

        // wait on the TelnetServer thread to terminate
        server.start();
        server.join();
    }

    
    /**
     *
     *
     */
    private static class EchoCommand implements CommandHandler {
        
        public String execute(String command, String[] args, InputStream in, OutputStream os) throws CommandFailedException {
                StringBuffer buffer = new StringBuffer();                
                for (int i = 0; i < args.length; i++) {
                    buffer.append(args[i]);
                    buffer.append(AseStrings.SPACE);
                }
                return buffer.toString();        
        }
        
        public String getUsage(String command) {
            return null;
        }
        
    }
}
