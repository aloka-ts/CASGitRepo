/*
 * TelnetServer.java
 *
 * Created on August 6, 2004, 9:45 AM.
 */
package com.baypackets.ase.util;

import java.io.BufferedReader;
import java.io.Console;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeMap;
import java.util.TreeSet;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import com.baypackets.ase.common.Registry;
import com.baypackets.ase.spi.util.CliInterface;
import com.baypackets.ase.spi.util.CommandFailedException;
import com.baypackets.ase.spi.util.CommandHandler;
import com.baypackets.ase.util.exceptions.TelnetServerException;
import com.baypackets.bayprocessor.agent.MComponent;
import com.baypackets.bayprocessor.agent.MComponentState;
import com.baypackets.bayprocessor.agent.OperationType;
import com.baypackets.bayprocessor.slee.common.BaseContext;
import com.baypackets.bayprocessor.slee.common.ConfigRepository;
import com.baypackets.bayprocessor.slee.common.Pair;
import com.baypackets.bayprocessor.slee.common.ThreadAlreadyRegisteredException;
import com.baypackets.bayprocessor.slee.common.ThreadNotRegisteredException;
import com.baypackets.bayprocessor.slee.common.UnableToChangeStateException;
import com.baypackets.bayprocessor.slee.common.UnableToUpdateConfigException;
import com.baypackets.bayprocessor.slee.threadmonitor.MonitoredThread;
import com.baypackets.bayprocessor.slee.threadmonitor.MonitoredThreadState;
import com.baypackets.bayprocessor.slee.threadmonitor.ThreadMonitor;
import com.baypackets.bayprocessor.slee.threadmonitor.ThreadOwner;


/**
 * An instance of this class runs as a multi-threaded service listening on a
 * specified port for requests sent from telnet based clients to invoke 
 * commands that are registered with this object.  A client sends a request 
 * as a string in the form of [commandName] [paramList], where 
 * "commandName" is the name of a command to execute and "paramList" is an 
 * optional space delimited list of arguments to invoke the command with.
 *
 * @author  Zoltan Medveczky
 */
public final class TelnetServer
	extends MonitoredThread
	implements CommandHandler, CliInterface, MComponent, ThreadOwner {
    
    private static Logger _logger = Logger.getLogger(TelnetServer.class);
    private static StringManager _strings = StringManager.getInstance(TelnetServer.class.getPackage());        
	private static int sessionCount = 0;
    
    private int _port = Integer.parseInt(_strings.getString("TelnetServer.defaultPort"));
    private String _prompt = _strings.getString("TelnetServer.defaultPrompt");
    private Map _handlers = Collections.synchronizedSortedMap(new TreeMap()); 
    private Set _hidden = Collections.synchronizedSortedSet(new TreeSet());
    private ServerSocket _serverSocket = null;    
    private boolean _stopped = false;
    private static List<String> authorisedAddressesList = Collections.synchronizedList(new ArrayList<String>());
	private ThreadMonitor threadMonitor = null;
    private String storedPassword = null;
    private ConfigRepository m_configRepository	= (ConfigRepository)Registry.lookup(Constants.NAME_CONFIG_REPOSITORY);
    
    public String getStoredPassword() {
		return storedPassword;
	}


	public void setStoredPassword(String storedPassword) {
		this.storedPassword = storedPassword;
	}


	/**
     * Executes a test driver for this class.
     *
     * @param args contains the port number on which to listen for incoming
     * client requests
     */
    public static void main(String[] args) throws Exception {
        TelnetServer server = new TelnetServer(Integer.parseInt(args[0]));
        
        // Register a handler that simply echos requests back to the client
        server.registerHandler("echo", new CommandHandler() {
            public String execute(String command, String[] args, InputStream in, OutputStream out) throws CommandFailedException {
                StringBuffer buffer = new StringBuffer();  
                
                for (int i = 0; i < args.length; i++) {
                    buffer.append(args[i]);
                    buffer.append(" ");
                }
                
                return buffer.toString();
            }
            
            public String getUsage(String command) {
                // No op
                return null;
            }
        });
        
        server.start();
        server.join();
    }
    
    
    /**
     * Default Constructor.
     */
    public TelnetServer() {
		super("TelnetServer", AseThreadMonitor.getThreadTimeoutTime(),
										BaseContext.getTraceService());

		threadMonitor = (ThreadMonitor)Registry.lookup(
											Constants.NAME_THREAD_MONITOR);

        // Register ourselves as the default handler for the "help" and
        // "set-prompt" commands.
        _handlers.put(AseStrings.CMD_HELP, this);
      //  _handlers.put("set-prompt", this);
        
        registerHandler(AseStrings.CMD_SET_PROMT, this);
    }
    
    
    /**
     * Initializes the server by specifying the port on which to listen
     * for incoming client requests.
     */
    public TelnetServer(int port) {
        this();
        _port = port;
    }
    
    
    /**
     * Starts the server and binds it to the configured port.
     */
    public void start() {
        try {
            _serverSocket = new ServerSocket(_port);

            if (_logger.isDebugEnabled()) {
                _logger.debug("Socket initialized on port :" + _port);
            }
        
            super.start();
        } catch (Exception e) {
            _logger.error(e.toString(), e);
            throw new RuntimeException(e.toString());
        }
    }
    
    
    /**
     * Listens for client connections on a specified port.  When a client 
     * connects, a new thread is spawned to handle that client's session.
     */
    public void run() {
		// Register thread with thread monitor
		try {
			// Set thread state to idle before registering
			this.setThreadState(MonitoredThreadState.Idle);

			threadMonitor.registerThread(this);
		} catch(ThreadAlreadyRegisteredException exp) {
			_logger.error("This thread is already registered with Thread Monitor", exp);
		}

        try {            
            while (!_stopped) {
            	try {
                    if (_logger.isDebugEnabled()) {
                        _logger.debug("Listening for client connections...");
                    }
		            
                    Socket socket = _serverSocket.accept();
		
                    if (_logger.isDebugEnabled()) {
                        _logger.debug("Client connected...spawning new thread");
                    }                   
		            
					// Update time in thread monitor
					this.updateTimeStamp();

					// Set thread state to running
					this.setThreadState(MonitoredThreadState.Running);

                    ClientSession cs = new ClientSession(socket);

					// Set thread owner for this client session
					cs.setThreadOwner(this);

					// Now start thread
					cs.start();

					// Set monitored thread state to idle before blocking on accept
					this.setThreadState(MonitoredThreadState.Idle);
                } catch (SocketException e){
                    if (!_stopped) {
                        throw e;
                    }
                }
            }
        } catch (Exception e) {
            _logger.error(e.toString(), e);
            
            if (e instanceof TelnetServerException) {
                throw (TelnetServerException)e;
            }
            throw new TelnetServerException(e.toString());
        } finally {
			// Unregister thread with thread monitor
			try {
				threadMonitor.unregisterThread(this);
			} catch(ThreadNotRegisteredException exp) {
				_logger.error("This thread is not registered with Thread Monitor", exp);
			}
		}
    }
    
    
    /**
     * Registers a new command handler with this server.
     *
     * @param command  The name used to bind the given handler to this server.
     * @param handler  The handler to invoke when a client requests execution
     * of the specified command.
     */
    public void registerHandler(String command, CommandHandler handler) {
    	registerHandler(command, handler, false);
    }

    
    /**
     * Registers a new command handler with this server.
     *
     * @param command  The name used to bind the given handler to this server.
     * @param handler  The handler to invoke when a client requests execution
     * of the specified command.
     * @param hidden  A flag indicating whether this command should appear in
     * the list of registered commands when a "help" request is submitted.
     */
    public void registerHandler(String command, CommandHandler handler, boolean hidden) {
    	if(_handlers.get(command) != null){
    		throw new IllegalArgumentException("This command is already registered :" + command);
    	}
    	
        _handlers.put(command, handler);
        if (hidden) {
            _hidden.add(command);
        }
    } 
    
    public void unregisterHandler(String command, CommandHandler handler){
    	Object value = _handlers.get(command);
    	if(value != null && value == handler){
    		_handlers.remove(command);
    		if(_hidden.contains(command)){
    			_hidden.remove(command);
        	}
    	}
    }
            
    
    /**
     * Initializes this object's state using the parameters from the
     * ConfigRepository class.
     */
    public void initialize(){
    	ConfigRepository configRep = null;
    	configRep = (ConfigRepository)Registry.lookup(Constants.NAME_CONFIG_REPOSITORY);
    	
    	String strPort = configRep.getValue(Constants.OID_TELNETSERVER_PORT);
        if(_logger.isEnabledFor(Level.INFO)){
            _logger.info("Telnet server port :::" + strPort);
	}
    	this._port = Integer.parseInt(strPort);
    	if (_logger.isDebugEnabled()) {
            _logger.debug("TELNET ROOT PASSWORD :" + (String) configRep.getValue(Constants.TELNET_ROOT_PASSWORD));
        }
    	this.setStoredPassword((String) configRep.getValue(Constants.TELNET_ROOT_PASSWORD));
		// Get configure value for thread timeout time
		String timeout = configRep.getValue(Constants.PROP_MT_MONITOR_THREAD_TIMEOUT);
		if(timeout != null) {
			this.setTimeoutTime(Integer.parseInt(timeout));
		}
		
    }
    
    
    /**
     * Shuts down the server.
     */
    public void shutdown() {
    	this._stopped = true;
		try {
    		this._serverSocket.close();
		} catch(IOException exp) {
			_logger.error("Closing socket", exp);
		}
    }
    
    
    /**
     * Implemented from the MComponent inteface and called by the EMS 
     * management application to set this object's running state.
     */
    public void changeState(MComponentState state)
			throws UnableToChangeStateException {
        try {
            if(_logger.isEnabledFor(Level.INFO)){
                _logger.info("Change state called on telnet server :::" + state.getValue());
            }
            if(state.getValue() == MComponentState.LOADED){
                this.initialize();
            } else if(state.getValue() == MComponentState.RUNNING){
                this.start();
            } else if(state.getValue() == MComponentState.STOPPED){
				this.shutdown();
            }
        } catch(Exception e){
            throw new UnableToChangeStateException(e.getMessage());
        }
    }

    
    /**
     * Implemented from the MComponent interface and called by the EMS 
     * management application to update this object's configuration.
     */
    public void updateConfiguration(Pair[] configData, OperationType opType) throws UnableToUpdateConfigException {
        // No op.
    }
    
    
    /**
     * Implemented from the CommandHandler interface to provide default 
     * functionality for the "help" and "set-prompt" commands.
     */
    public String execute(String command, String[] args, InputStream is, OutputStream os) throws CommandFailedException {
        
//    	
//    	_logger.debug("Command typed is up key"+command); 
//    	if(command.equals("^[[A") ){
//    		if(_logger.isDebugEnabled()){
//    		_logger.debug("Command typed is up key");
//    		}
//    	}else if(command.equals("^[[B")){
//    		if(_logger.isDebugEnabled()){
//        		_logger.debug("Command typed is down key");
//        		}
//    	}
    	if (command.equals("help")) {
            // Output the name of each registered command
    		if (args == null || args.length == 0) {                            
                StringBuffer buffer = new StringBuffer(_strings.getString("TelnetServer.registeredCommands"));
            
                Iterator iterator = _handlers.keySet().iterator();
                String telnetRestrictedCommandDisplay = (String)m_configRepository.getValue(Constants.TELNET_RESTRICTED_COMMAND_DISPLAY_FLAG);
                while (iterator.hasNext()) {
                    String cmd = (String)iterator.next();
                    CommandHandler handler = (CommandHandler)_handlers.get(cmd); 
                    
                    // Only output the command if it is not a "hidden" command
                    if (!_hidden.contains(cmd)) {
                    	if(telnetRestrictedCommandDisplay.equalsIgnoreCase(AseStrings.FALSE_SMALL)){
                    		if(cmd.startsWith(AseStrings.CMD_STOP) || cmd.startsWith(AseStrings.CMD_UNDEPLOY)|| cmd.startsWith(AseStrings.CMD_UPGRADE) || cmd.equals(AseStrings.CMD_DEPLOY) || cmd.equals(AseStrings.CMD_GET_COUNT) || cmd.equals(AseStrings.CMD_CLEAR_COUNT) || 
                    						cmd.equals(AseStrings.CMD_LOG) || cmd.equals(AseStrings.CMD_LOGGING) || cmd.equals(AseStrings.CMD_ADD_PREPAID_PATTERNS) || cmd.equals(AseStrings.CMD_APP_ROUTER_RELOAD) || cmd.equals(AseStrings.CMD_CALL_GAPPING) || 
                    						cmd.equals(AseStrings.CMD_DUMP_POLICIES) || cmd.equals(AseStrings.CMD_GW_INFO) || cmd.equals(AseStrings.CMD_GET_PREPAID_PATTERNS) || cmd.equals(AseStrings.CMD_MS_ADMIN) || cmd.equals(AseStrings.CMD_DUMP_STACK) || 
                    						cmd.equals(AseStrings.CMD_RELOAD_POLICIES) || cmd.equals(AseStrings.CMD_REMOVE_PREPAID_PATTERNS) || cmd.equals(AseStrings.CMD_SET_POLICY_MODE) || cmd.equals(AseStrings.CMD_SET_PROMT) || cmd.equals(AseStrings.CMD_START)){
                    			continue;
                    		}
                    		
                    	}
                        buffer.append(cmd);                    
                        buffer.append(AseStrings.NEWLINE_WITH_CR);
                    }
                }
            
                buffer.append(AseStrings.NEWLINE_WITH_CR);
                buffer.append(_strings.getString("TelnetServer.helpUsage"));
                
                return buffer.toString();
            } else {    // Output the usage statement for the specified command
                CommandHandler handler = (CommandHandler)_handlers.get(args[0]);
                
                if (handler == null) {
                    return _strings.getString("TelnetServer.noSuchHandler", args[0]);
                }
                
                String usage = handler.getUsage(args[0]);
                
                if (usage == null) {
                    return _strings.getString("TelnetServer.noFoundUsageStatement", args[0]);
                }
                return usage;
            }
        } else if (command.equals("set-prompt")) {
            if (args == null || args.length == 0) {
                _prompt = AseStrings.BLANK_STRING;
            } else {
                _prompt = args[0];
            }
            
            _prompt += "> ";
            
            return _strings.getString("TelnetServer.promptChanged");
        }
        
        return null;
    }    
    
    
    /**
     * Returns the usage statement for either the "help" or "set-prompt"
     * commands.
     */
    public String getUsage(String command) {
        if (command.equals(AseStrings.CMD_HELP)) {
            return _strings.getString("TelnetServer.helpUsage");
        }
        if (command.equals(AseStrings.CMD_SET_PROMT)) {
            return _strings.getString("TelnetServer.setPromptUsage");
        }
        return null;
    }
    
    //////////////////// ThreadMonitor methods for TelnetServer starts ////////////////// 

	// As MonitoredThread
	public ThreadOwner getThreadOwner() {
		return this;
	}

	// As ThreadOwner
	public int threadExpired(MonitoredThread thread) {
		_logger.error(thread.getName() + " expired");

		// Print the stack trace
		StackDumpLogger.logStackTraces();

		return ThreadOwner.SYSTEM_RESTART;
	}

    //////////////////// ThreadMonitor methods for TelnetServer ends //////////////////// 

    /**
     * This thread is spawned by the TelnetServer class to handle a client's 
     * session.
     */
    private class ClientSession extends Thread {
        private Socket _socket;
		private ThreadOwner _threadOwner = null;
		 
            

		public ClientSession(Socket socket) {
//			super(	"ClientSession#" + sessionCount++,
//					AseThreadMonitor.getThreadTimeoutTime(),
//					BaseContext.getTraceService());
			super("ClientSession#");
            _socket = socket;

			// Initialize thread state to idle
			//this.setThreadState(MonitoredThreadState.Idle);
        }
        
        public void run() {
			// Register thread with thread monitor
//			try {
//				// Set thread state to idle before registering
//				this.setThreadState(MonitoredThreadState.Idle);
//
//				threadMonitor.registerThread(this);
//			} catch(ThreadAlreadyRegisteredException exp) {
//				_logger.error("This thread is already registered with Thread Monitor", exp);
//			}

            OutputStream out = null;
            InputStream in = null;
            PrintWriter writer = null;
            
            BufferedReader reader = null;            
            try {
                // Establish streams for reading and writing to the client
                out = _socket.getOutputStream();
                in = _socket.getInputStream();
                writer = new PrintWriter(out);
                // Storing the Network IP from where telnet connection has been established to make
                //sure that next time ownwards password would not be asked from the user
                reader = new BufferedReader(new InputStreamReader(in));
                SocketAddress remoteSocketAddress = _socket.getRemoteSocketAddress();
                String remoteAddress = remoteSocketAddress.toString().substring(0, remoteSocketAddress.toString().indexOf(":"));    
                if (_logger.isDebugEnabled()) {
                    _logger.debug("Telnet Connection request coming from :" + remoteAddress);
                    
                }
                String password = null;
                //Decrypting the stored password
               /* if (_logger.isDebugEnabled()) {
                    _logger.debug("Encrypted Password: " + getStoredPassword());
                    
                }*/
                String decryptedPassword = DataEncryption.decrypt(getStoredPassword());
               
                //Commented because customer did not want Telnet Authentication Password to be
                //published in the logs
              /*  if (_logger.isDebugEnabled()) {
                    _logger.debug("Decrypted Password :" + decryptedPassword);
                    
                }*/
                boolean askForAuthorisation = false;
            	if(!authorisedAddressesList.contains(remoteAddress)){
            		askForAuthorisation = true;
            	}
                if (_logger.isDebugEnabled()) {
                    _logger.debug("Authorisation Status :" + askForAuthorisation);
                    _logger.debug("Size of Saved Addresses's List :" + authorisedAddressesList.size());
                    _logger.debug("Saved Addresses's List :" + authorisedAddressesList.size());
                }
            	int authCounter = 0;
               
                while (authCounter<3){
                	if(askForAuthorisation){
                		writer.print("Please enter password to authenticate:" + AseStrings.NEWLINE_WITH_CR);
                    	writer.flush();
                    	//Masking the Password entered by the user
                    	password = DataFormatter.readAndMaskPassword(reader, writer);
                	}
                	if ((askForAuthorisation && password!=null && password.equals(decryptedPassword)) || !askForAuthorisation){
                		// Display greeting if user is an authorised one
                		String telnetAuthEverytimeFlag = (String)m_configRepository.getValue(Constants.TELNET_AUTH_EVERYTIME);
                		_logger.debug("Not adding in authorisedAddressesList : "+telnetAuthEverytimeFlag);
                		if(!telnetAuthEverytimeFlag.equalsIgnoreCase(AseStrings.TRUE_SMALL)){
                			_logger.debug("Not adding in authorisedAddressesList .....");
                			if(!authorisedAddressesList.contains(remoteAddress)){
                			authorisedAddressesList.add(remoteAddress);
                			}
                		}
                		writer.print(_strings.getString("TelnetServer.greeting") + AseStrings.NEWLINE_WITH_CR);
                		do {
                			// 	Display prompt
                			writer.print(_prompt);
                			writer.flush();

                    // Read input from client
		            String input = reader.readLine();
                    StringTokenizer tokens = new StringTokenizer(processLine(input));
                    
					// // Update time in thread monitor
				//	this.updateTimeStamp();

					// Set thread state to running before blocking on dequeue
				//	this.setThreadState(MonitoredThreadState.Running);

                    // Parse input from client...
                    while (tokens.hasMoreTokens()) {
                        String command = tokens.nextToken().trim();
                       
                        if ((command.equalsIgnoreCase(AseStrings.CMD_EXIT)) ||
                            (command.equalsIgnoreCase(AseStrings.CMD_QUIT))) {
                            writer.print(_strings.getString("TelnetServer.bye") + AseStrings.NEWLINE_WITH_CR);
                            return;
                        }
                        
                        CommandHandler handler = (CommandHandler)_handlers.get(command);
                        
                        if (handler == null) {
                            String[] matches = findByPrefix(command);
                            
                            if (matches != null) {
                                for (int i = 0; i < matches.length; i++) {
                                    writer.print(matches[i] + AseStrings.NEWLINE_WITH_CR);
                                }
                            } else {
                                writer.print(_strings.getString("TelnetServer.noSuchHandler", command) + AseStrings.NEWLINE_WITH_CR);
                            }
                        } else {                        
                            String[] args = new String[tokens.countTokens()];  
                            
                            for (int i = 0; tokens.hasMoreTokens(); i++) {
                                args[i] = tokens.nextToken();                            
                            }   
                            
                            writer.print(handler.execute(command, args, in, out) + AseStrings.NEWLINE_WITH_CR);
                        }
                    } // while

					// Set thread state to idle before blocking on readLine
				//	this.setThreadState(MonitoredThreadState.Idle);
                } while (true);  
                } else {
                	if (authCounter == 2){ 
                		writer.print("You are not authorsied user" + AseStrings.NEWLINE_WITH_CR);
                	}else{
                		writer.print("User Credentials are wrong. Please try again." + AseStrings.NEWLINE_WITH_CR);
                	}
                }
                authCounter = authCounter+1;
               }	
            } catch (Throwable e) {
				_logger.error("Caught at TelnetServer Thread Level", e);
            } finally {
				// Unregister thread with thread monitor
//				try {
//					threadMonitor.unregisterThread(this);
//				} catch(ThreadNotRegisteredException exp) {
//					_logger.error("This thread is not registered with Thread Monitor", exp);
//				}

                try {
                    writer.close();
                    reader.close();
                    _socket.close();
                } catch (Exception e) {}
            }
        }
        
        /**
         * Returns the names of all registerd commands that begin with the
         * specified prefix.
         */
        private String[] findByPrefix(String prefix) {
            List matches = null;
            
            Iterator commands = _handlers.keySet().iterator();
            
            while (commands.hasNext()) {
                String command = (String)commands.next();
                
                if (command.startsWith(prefix) && !_hidden.contains(command)) {
                    if (matches == null) {
                        matches = new ArrayList();
                    }
                    matches.add(command);
                }
            }  
            
            return matches != null ? (String[])matches.toArray(new String[matches.size()]) : null;
        }

    	//////////////// ThreadMonitor methods for ClientSession starts ////////////////// 

		public void setThreadOwner(ThreadOwner threadOwner) {
			_threadOwner = threadOwner;
		}

		public ThreadOwner getThreadOwner() {
			return _threadOwner;
		}

		
	private String processLine (String input)
	{
		StringBuffer line = new StringBuffer (64);
		int pos = 0, count = 0;
		String ret;
		
		for (int len = input.length();count <len;) {
			int c = input.charAt (count++);
			
			if (c == '\b' && pos > 0) {
				pos--;
				continue;
			}
			
			if (c == AseStrings.CHAR_NEWLINE) {
				line.insert (pos, (char) c);
				return line.substring(0, pos);
			}
			
			if (c == 127) {	// backspace
				if (pos > 0) {
					pos--;
					line.deleteCharAt (pos);
				}
				continue;
			}
			
			if (c != 27) {	// control
				line.insert (pos, (char) c);
				pos++;
				continue;
			}
								
			c = input.charAt (count++);
			if (c != AseStrings.SQUARE_BRACKET_CHAR_OPEN)
				continue;	// ignore
			
			c = input.charAt (count++);
			switch (c) {
				case 'A':	// up
				case 'B':	// down
					break;
				
				case 'C':	// right
					if (pos < line.length ()) {
						pos++;
					}
					break;
					
				case 'D':	// left
					if (pos > 0) {
						pos--;
					}
					break;
				
				default:
					break;
			}
		}
		return line.substring(0, pos);
	}


    	//////////////// ThreadMonitor methods for ClientSession starts ////////////////// 
    } // End of ClientSession
    
    
}
