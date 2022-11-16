/*
 * CommandHandler.java
 *
 * Created on August 6, 2004, 10:25 AM
 */
package com.baypackets.ase.spi.util;

import java.io.InputStream;
import java.io.OutputStream;


/**
 * This interface defines an object that is registered with the TelnetServer
 * class and is invoked to handle all client requests submitted from a telnet 
 * interface to execute a specified command.
 *
 * @see com.baypackets.ase.util.TelnetServer
 *
 * @author  Zoltan Medveczky
 */
public interface CommandHandler {

    /**
     * This method is invoked by the TelnetServer class to execute the 
     * specified command.
     *
     * @param command  A command submitted from a telnet interface
     * @param args  The command parameters
     * @param in  A stream for reading content from the client.
     * @param out  A stream for sending content to the client.
     * @return  A response message indicating the status of the executed
     * command. 
     */
    public String execute(String command, String[] args, InputStream in, OutputStream out) throws CommandFailedException;
    
    /**
     * Returns a usage statement for the specified command.  This will 
     * typically be called by the TelnetServer in response to a client 
     * submitting a "help" command from a telnet interface.
     *
     * @param command  The command for which to return a usage statement.
     */
    public String getUsage(String command);           
    
}