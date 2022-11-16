/*
 * MediaServerManagerImpl.java
 *
 * Created on Jun 20, 2005
 */
package com.baypackets.ase.mediaserver;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.log4j.Logger;

import com.baypackets.ase.common.Registry;
import com.baypackets.ase.control.PartitionInfo;
import com.baypackets.ase.control.RoleChangeListener;
import com.baypackets.ase.sbb.MediaServer;
import com.baypackets.ase.sbb.MediaServerSelector;
import com.baypackets.ase.spi.util.CommandFailedException;
import com.baypackets.ase.spi.util.CommandHandler;
import com.baypackets.ase.util.AseAlarmService;
import com.baypackets.ase.util.AseStrings;
import com.baypackets.ase.util.Constants;
import com.baypackets.ase.util.StringManager;
import com.baypackets.ase.util.TelnetServer;
import com.baypackets.bayprocessor.agent.MComponent;
import com.baypackets.bayprocessor.agent.MComponentState;
import com.baypackets.bayprocessor.agent.OperationType;
import com.baypackets.bayprocessor.slee.common.ConfigRepository;
import com.baypackets.bayprocessor.slee.common.InitializationFailedException;
import com.baypackets.bayprocessor.slee.common.Pair;
import com.baypackets.bayprocessor.slee.common.UnableToChangeStateException;
import com.baypackets.bayprocessor.slee.common.UnableToUpdateConfigException;

/**
 * Enables provisioning of the media servers in SAS by implmenting the
 * <code>MediaServerManager</code> and <code>MediaServerSelector</code>
 * interfaces.
 * 
 * <p>
 * The MediaServerManagerImpl implements the selectXXX methods by load balancing
 * the requests by random distribution among the ACTIVE media servers.
 * 
 * <p>
 * The Media Server Status will be considered active as soon as it is added to
 * the list. If the HeartBeat servlet reports the failure, then it will be
 * marked INACTIVE. The MediaServerManager will raise an ALARM to EMS to notify
 * the failure of the Media Server.
 * 
 * <p>
 * TBD - Media server addition/removal/status update from EMS.
 * 
 * @author Ravi
 */
public class MediaServerManagerImpl implements MediaServerManager,
		MediaServerSelector, MComponent, RoleChangeListener, CommandHandler {

	/**
	 * This property is passed to this object as a configuration parameter
	 * specifying the interval (in seconds) for which to ping all the
	 * provisioned media servers.
	 */
	public static final String HEARTBEAT_INTERVAL = Constants.OID_MS_HEARTBEAT_INTERVAL;

	/**
	 * This property is passed as a configuration parameter to this object
	 * specifying the number of times to ping a non-responsive media server
	 * before marking it as unavailable.
	 */
	public static final String NUM_OF_RETRIES = Constants.OID_MS_NUM_OF_RETRIES;

	/**
	 * The property is passed as a configuration parameter to specify the number
	 * of seconds for a media server interaction to timeout.
	 */
	public static final String OPERATION_TIMEOUT = Constants.OID_MS_OP_TIMEOUT;

	/**
	 * Alarm code used to notify EMS that a media server went down.
	 */
	public static final int ALARM_MEDIA_SERVER_DOWN = Constants.ALARM_MEDIA_SERVER_DOWN;

	/**
	 * Alarm code used to notify EMS that a media server went down.
	 */
	public static final int ALARM_MEDIA_SERVER_ADMIN = Constants.ALARM_MEDIA_SERVER_ADMIN;

	/**
	 * Alarm code used to notify EMS that a media server has become available.
	 */
	public static final int ALARM_MEDIA_SERVER_UP = Constants.ALARM_MEDIA_SERVER_UP;

	/**
	 * Media Server Status file
	 */
	public static String MEDIA_SERVER_STATUS_FILE = Constants.ASE_HOME;

	/**
	 * Command used to display the status of all media servers to a telnet
	 * console.
	 */
	private static String MS_INFO = "ms-info";
	private static String MS_ADMIN = "ms-admin".intern();

	private static final short CMD_MS_INFO = 8;
	private static final short CMD_MS_ADMIN = 9;

	private static final String ADMIN_DOWN = "down";
	private static final String ADMIN_UP = "up";

	private static short FIND_BY_NAME = 0;
	private static short FIND_BY_CAPABILITIES = 1;
	private static short FIND_BY_INTERFACE = 2;

	private static Logger _logger = Logger
			.getLogger(MediaServerManagerImpl.class);
	private static StringManager _strings = StringManager
			.getInstance("com.baypackets.ase.mediaserver");

	private Map _mediaServers;
	private static MediaServerList mediaServerList; // circular list
	private int _heartBeatInterval = 10;
	private int _retryCount = 4;
	private int _operationTimeout = 1800;
	private AseAlarmService _alarmService;
	private boolean disabled = false;

	private int counter = 0;
	private int localMSLBCounter = 0;
	private int remoteMSLBCounter = 0;

	private static Properties mediaServerStatusFile;

	private static final String DOWN = "DOWN";
	private static final String ACTIVE = "ACTIVE";
	private static final String ADMIN = "ADMIN";
	private static final String SUSPECT = "SUSPECT";

	/**
	 * Default constructor.
	 */
	public MediaServerManagerImpl() {
		super();
		MEDIA_SERVER_STATUS_FILE = MEDIA_SERVER_STATUS_FILE + File.separator
				+ "sysapps" + File.separator + "media-server-status.properties";

	}

	/**
	 * Returns the specified MediaServer or NULL if none is found for the given
	 * ID.
	 */
	public MediaServer findById(String id) {

		return mediaServerList != null ? (MediaServer) mediaServerList.find(id)
				: null;
	}

	/**
	 * Returns an iteration over all provisioned media servers. If no media
	 * servers are currently provisioned with the platform, an empty Iterator is
	 * returned.
	 */
	public Iterator findAll() {

		return mediaServerList != null ? mediaServerList.findAll()
				: new ArrayList(0).iterator();
	}

	/**
	 * This method is invoked to add a new media server to the list of
	 * provisioned media servers.
	 */
	public void addMediaServer(MediaServer ms) {

		 if (_logger.isDebugEnabled())
			{
			_logger.debug("addMediaServer : " + ms);
			}
		if (_mediaServers == null) {
			_mediaServers = new Hashtable();
		}

		if (mediaServerList == null) {
			mediaServerList = new MediaServerList();
		}

		mediaServerList.add(ms);
		_mediaServers.put(ms.getId(), ms);
	}

	/**
	 * This method removes the specified media server from the list of
	 * provisioned media servers.
	 */
	public void removeMediaServer(String id) {
		MediaServer ms = null;
		if (_mediaServers != null) {
			ms = (MediaServer) _mediaServers.remove(id);
		}

		if (mediaServerList != null) {
			mediaServerList.remove(id);
		}
	}

	/**
	 * Returns the configured heart beat interval for the media servers.
	 */
	public int getMediaServerHeartBeatInterval() {
		return _heartBeatInterval;
	}

	/**
	 * Sets the interval (in seconds) for which to ping the provisioned media
	 * servers.
	 */
	public void setMediaServerHeartBeatInterval(int interval) {
		_heartBeatInterval = interval;
	}

	/**
	 * This method returns the Media Server using round-robin
	 */
	public MediaServer selectMediaServer() {

		return mediaServerList.getNextActiveMediaServer();

	}

	/**
	 * This method selects a media server by applying the following alogrithm:
	 * <ul>
	 * <li> Finds all currently available media servers that have the specified
	 * vendor name and stores them in an indexed list.
	 * <li> Generates a random integer whose value is between 0 and the number
	 * of found media servers minus 1.
	 * <li> Uses the random number as an index to select a media server from
	 * this list.
	 * </ul>
	 * 
	 * @param name
	 *            A media server vendor name.
	 * @return One of the media servers with the specified vendor name or NULL
	 *         if none was found.
	 */
	public MediaServer selectByName(String name) {
		return selectMediaServer(FIND_BY_NAME, name);
	}

	/**
	 * This method returns a media server by applying the same logic as is done
	 * in the "selectByName" method only it selects a server based on
	 * capabilities rather than vendor name.
	 * 
	 * @param capabilities
	 *            Specifies the set of capabilities for the media server to
	 *            find. This value can be constructed by performing a bitwise OR
	 *            of one or more of the CAPABILITY public static constants
	 *            defined in the MediaServer interface.
	 */
	public MediaServer selectByCapabilities(int capabilities) {
		return selectMediaServer(FIND_BY_CAPABILITIES,
				new Integer(capabilities));
	}

	/**
	 * Helper method invoked by the "selectXXX" methods.
	 */
	private MediaServer selectMediaServer(short criteriaType, Object criteria) {

		if (mediaServerList == null) {
			return null;
		}

		Collection results = null;

		synchronized (mediaServerList) {

			Iterator iterator = mediaServerList.findAll();
			boolean returnSuspects = true;
			while (iterator.hasNext()) {
				MediaServer server = (MediaServer) iterator.next();

				if (server.getState() == MediaServer.STATE_DOWN || server.getState() == MediaServer.STATE_ADMIN) {
					continue;
				}

				if (criteriaType == FIND_BY_NAME
						&& !criteria.equals(server.getName())) {
					continue;
				}
				if (criteriaType == FIND_BY_CAPABILITIES
						&& !server.isCapable(((Integer) criteria).intValue())) {
					continue;
				}

				if (server.getState() == MediaServer.STATE_ACTIVE) {
					if (returnSuspects && (results != null)) {
						results.clear();
					}
					returnSuspects = false;
				} else if (!returnSuspects) {
					// Don't add suspect media servers.
					continue;
				}

				if (results == null) {
					results = new ArrayList();
				}
				results.add(server);
			}
		}

		if (results == null) {
			return null;
		}

		// Apply "load balancing" logic...
		int index = counter++ % results.size();
		_logger.debug("mediaServerManagerImple === > " +  index + " - size - " + results.size() +" -counter- " +counter);
		return (MediaServer) results.toArray()[index];
		
		
	}

	
	/**
	 * This method returns a media server by applying the same logic as is done
	 * in the above "selectByCapabilities" method only it selects a particular type (local/remote) 
	 * media server based on capabilities (support for Geographically closer MS functionality).
	 * 
	 * @param capabilities
	 *            Specifies the set of capabilities for the media server to
	 *            find. This value can be constructed by performing a bitwise OR
	 *            of one or more of the CAPABILITY public static constants
	 *            defined in the MediaServer interface.
	 * @param isRemote
	 * 			  Specifies whether select local media server or remote media server
	 * 			  1 = remote media server, default = local media server, 
	 */
	public MediaServer selectByCapabilities(int capabilities, int isRemote,int isPrivate) {
				
		if (mediaServerList == null) {
			return null;
		}
		
		isRemote = (isRemote == 1 ? 1 : 0);
		isPrivate = (isPrivate == 1 ? 1 : 0);
		List<MediaServer> results = null;
		int index = 0;
		Integer criteria = new Integer(capabilities);
		
		if ( _logger.isDebugEnabled()) {
			_logger
					.debug("selectByCapabilities(): Select media server wiith vapabilities " + capabilities +" isRemote "+isRemote +" isPrivate "+isPrivate);
		}

		//synchronized (mediaServerList) {

			Iterator<MediaServer> iterator = mediaServerList.findAll();
			boolean returnSuspects = true;
			
			while (iterator.hasNext()) {
				MediaServer server = iterator.next();

				if (server.getState() == MediaServer.STATE_DOWN || server.getState() == MediaServer.STATE_ADMIN) {
					continue;
				}
				if (!server.isCapable(((Integer) criteria).intValue())) {
					continue;
				}
				
				MediaServerImpl serverImpl=((MediaServerImpl) server);
				
				if ( _logger.isDebugEnabled()) {
					_logger
							.debug("selectByCapabilities(): Active media server found" +server + " isRemote "+ serverImpl.getIsRemote()+"  isPrivate "+serverImpl.getIsPrivate());
				}
				//checking whether MS is remote or local also its private or public
				if (serverImpl.getIsRemote() != isRemote
						|| serverImpl.getIsPrivate() != isPrivate) {
					
					if ( _logger.isDebugEnabled()) {
						_logger
								.debug("selectByCapabilities(): Try next isRemote isPrivate not matching");
					}
					continue;
				}

				if (server.getState() == MediaServer.STATE_ACTIVE) {
					if (returnSuspects && (results != null)) {
						results.clear();
					}
					returnSuspects = false;
				} else if (!returnSuspects) {
					// Don't add suspect media servers.
					continue;
				}

				if (results == null) {
					results = new ArrayList<MediaServer>();
				}
				
				
				if ( _logger.isDebugEnabled()) {
					_logger
							.debug("selectByCapabilities(): Found matching media server " +server);
				}
				results.add(server);
			}

			if (results == null) {
				return null;
			}
	
			// Apply "load balancing" logic separately on local and remote media servers...
			synchronized(this){
				if(isRemote == 1)	//remote MS
					index= remoteMSLBCounter++ % results.size();
				else	//local MS
					index= localMSLBCounter++ % results.size();
			}
		//}
		
		return (MediaServer) results.toArray()[index];
	}
	
	
	/**
	 * Gets the no. of active local/remote media servers configured
	 * for particular capabilities
	 */
	public int getActiveMSCount(int capabilities, int isRemote) {
		
		int msCount = 0;
		Integer criteria = new Integer(capabilities);
		
		synchronized (mediaServerList) {

			Iterator iterator = mediaServerList.findAll();
			
			while (iterator.hasNext()) {
				MediaServer server = (MediaServer) iterator.next();

				if (server.getState() == MediaServer.STATE_ACTIVE && server.isCapable(((Integer) criteria).intValue())
						&& ((MediaServerImpl)server).getIsRemote() == isRemote) {
					msCount ++ ;
				}
				else {
					continue;
				}
				
			}
		}
		
		return msCount;
	}
	
	/**
	 * This method marks the specified media server as being unavailable and
	 * sends an error alarm to EMS.
	 * 
	 * @param id
	 *            The ID of the media server that has gone down.
	 */
	public void mediaServerSuspect(String id) {
		boolean loggerEnabled = _logger.isDebugEnabled();

		if (loggerEnabled) {
			_logger
					.debug("mediaServerSuspect(): Received notification that media server with ID, "
							+ id + " is suspect.");
		}

		// MediaServerImpl server = _mediaServers != null ?
		// (MediaServerImpl)_mediaServers.get(id) : null;
		MediaServerImpl server = mediaServerList != null ? (MediaServerImpl) mediaServerList
				.find(id)
				: null;

		if (server != null) {
			server.setState(MediaServer.STATE_SUSPECT);
			mediaServerStatusFile.setProperty(server.getId(), SUSPECT);
			storeProperty();
		} else if (loggerEnabled) {
			_logger
					.debug("mediaServerSuspect(): No media server found for the specified ID.");
		}
	}

	/**
	 * This method marks the specified media server as being unavailable and
	 * sends an error alarm to EMS.
	 * 
	 * @param id
	 *            The ID of the media server that has gone down.
	 */
	public void mediaServerDown(String id) {
		boolean loggerEnabled = _logger.isDebugEnabled();

		if (loggerEnabled) {
			_logger
					.debug("mediaServerDown(): Received notification that media server with ID, "
							+ id + " is down.");
		}

		// MediaServerImpl server = _mediaServers != null ?
		// (MediaServerImpl)_mediaServers.get(id) : null;
		MediaServerImpl server = mediaServerList != null ? (MediaServerImpl) mediaServerList
				.find(id)
				: null;

		if (server != null) {
			if (server.getState() != MediaServer.STATE_DOWN) {
				server.setState(MediaServer.STATE_DOWN);
				mediaServerStatusFile.setProperty(server.getId(), DOWN);
				storeProperty();

				if (loggerEnabled) {
					_logger
							.debug("mediaServerDown(): Notifying EMS that media server is down...");
				}

				try {
					_alarmService.sendAlarm(ALARM_MEDIA_SERVER_DOWN, server.getMediaServerId(),_strings.getString("MediaServerManagerImpl.mediaServerDown",
							new String[] {server.getId(),server.getHost().getHostAddress(),server.getPort()+AseStrings.BLANK_STRING}));
					if (loggerEnabled) {
						_logger.debug("Trouble System Id for MS: " + server.getMediaServerId());
					}
					_logger.error("MediaServer is Down  :: ID : "+server.getId()+" : Host : "+server.getHost().getHostAddress());
				} catch (Exception e) {
					_logger.error(
							"Error occurred while notifying EMS of downed media server: "
									+ e.getMessage(), e);
				}
			}
		} else if (loggerEnabled) {
			_logger
					.debug("mediaServerDown(): No media server found for the specified ID.");
		}
	}

	public void mediaServerAdmin(String id) {
		boolean loggerEnabled = _logger.isDebugEnabled();

		if (loggerEnabled) {
			_logger
					.debug("mediaServerAdmin(): Received notification that media server with ID, "
							+ id + " is in Admin State.");
		}

		// MediaServerImpl server = _mediaServers != null ?
		// (MediaServerImpl)_mediaServers.get(id) : null;
		MediaServerImpl server = mediaServerList != null ? (MediaServerImpl) mediaServerList
				.find(id)
				: null;

		if (loggerEnabled) {
			_logger.debug("mediaServerAdmin() Media Server : " + server
					+ "  with ID, " + id + " changing State to Admin State.");
		}

		if (server != null) {
			if (server.isHeartbeatEnabled()) {
				server.disableHeartbeat();// disable heartbeat as this server is
				// administarted by Admin
				server.setHeartbeatAdminDisabled(true); // Admin has disabled the
				// heart beat

				if (loggerEnabled) {
					_logger
							.debug("mediaServerAdmin() The Heartbeat of Media Server : "
									+ "  with ID, "
									+ id
									+ " is Diabled by Admin on going in ADMIN state .");
				}
			}
			if (server.getState() != MediaServer.STATE_ADMIN) {
				server.setState(MediaServer.STATE_ADMIN);
				mediaServerStatusFile.setProperty(server.getId(), ADMIN);
				storeProperty();

				if (loggerEnabled) {
					_logger
							.debug("mediaServerAdmin(): Notifying EMS that media server is in Admin State...");
				}

				try {
					_alarmService.sendAlarm(ALARM_MEDIA_SERVER_ADMIN, _strings.getString("MediaServerManagerImpl.mediaServerAdmin",
									new String[] {server.getId(),server.getHost().getHostAddress(),server.getPort()+AseStrings.BLANK_STRING}));
				} catch (Exception e) {
					_logger.error(
							"Error occurred while notifying EMS of Admin media server: "
									+ e.getMessage(), e);
				}
			}
		} else if (loggerEnabled) {
			_logger
					.debug("mediaServerAdmin(): No media server found for the specified ID.");
		}
	}

	/**
	 * This method marks the specified media server as being available and then
	 * sends a clearing alarm to EMS for the previously sent error alarm.
	 * 
	 * @param id
	 *            The ID of the media server that has come up.
	 */
	public void mediaServerUp(String id) {
		boolean loggerEnabled = _logger.isDebugEnabled();

		// MediaServerImpl server = _mediaServers != null ?
		// (MediaServerImpl)_mediaServers.get(id) : null;
		MediaServerImpl server = mediaServerList != null ? (MediaServerImpl) mediaServerList
				.find(id)
				: null;

		if (loggerEnabled) {
			_logger.debug("mediaServerUp(): Received notification that media server with ID, '"
							+ id + "' is now up." + server);
		}

	
		if (server != null) {
			
			if (server.isHeartbeatAdminDisabled()) {
				server.enableHeartbeat();// enable heartbeat as this server is
				// now up by Admin
				if (loggerEnabled) {
					_logger.debug("mediaServerAdmin() The Heartbeat of Media Server : "
									+ "  with ID, "
									+ id
									+ " is again enabled by Admin on going in ADMIN UP state .");
				}
			}
		
			if (server.getState() != MediaServer.STATE_ACTIVE) {
				
				int oldState = server.getState();
				
				if(oldState == MediaServer.STATE_ADMIN){
					// Media Server re Initialize from media-server-config.xml ///////////
					try {

						MediaServerDAO dao = MediaServerDAOFactory.getInstance().getMediaServerDAO();
						MediaServerImpl updatedMediaServer=(MediaServerImpl)dao.getMediaServer(id);
						if(updatedMediaServer!=null){
							if(_logger.isDebugEnabled()){
								_logger.debug("Updating media server id "+id+" with new configuration");
							}
							server.updateMediaServer(updatedMediaServer);
						}else{
							_logger.error("No media server found for the specified ID in config file.");
						}

					}catch (Exception e) {
						_logger.error("Error while reading media servers from file DAO",e);
					} 	
					// Media Server re Initialize from media-server-config.xml ///////////
				}
				
				server.setState(MediaServer.STATE_ACTIVE);
				mediaServerStatusFile.setProperty(server.getId(), ACTIVE);
				storeProperty();

				if ((oldState == MediaServer.STATE_DOWN)
						|| (oldState == MediaServer.STATE_ADMIN)) {
					if (loggerEnabled) {
						_logger
								.debug("mediaServerUp(): Notifying EMS that media server is up...");
					}

					try {
						_alarmService.sendAlarm(ALARM_MEDIA_SERVER_UP,server.getMediaServerId(),_strings.getString("MediaServerManagerImpl.mediaServerUp",
										new String[] {server.getId(),server.getHost().getHostAddress(),server.getPort()+AseStrings.BLANK_STRING}));
						if (loggerEnabled) {
							_logger.debug("Trouble System Id for MS: " + server.getMediaServerId());
						}
						_logger.error("MediaServer is up  :: ID : "+server.getId()+" : Host : "+server.getHost().getHostAddress());
					} catch (Exception e) {
						_logger.error(
								"Error occurred while notifying EMS of downed media server: "
										+ e.getMessage(), e);
					}
				}
			}
		} else if (loggerEnabled) {
			_logger
					.debug("mediaServerUp(): No media server found for the specified ID.");
		}
	}

	private void storeProperty() {

		FileOutputStream os = null;
		try {
			File statusFile =new File(
					MEDIA_SERVER_STATUS_FILE);
			
			if(!statusFile.exists())
				statusFile.createNewFile();
			
			os = new FileOutputStream(statusFile);
		} 
		catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} 
		try {
			if (_logger.isDebugEnabled()) {
				_logger.debug(" Storing the state in property file "
						+ MEDIA_SERVER_STATUS_FILE);
			}
			mediaServerStatusFile.store(os, null);
		} catch (IOException e1) {
			_logger.error(
					"Error occurred while soring mediaserverstatus file: "
							+ e1.getMessage());
		} finally {
			try {
				os.close();
			} catch (IOException e) {
				_logger.error(e);
			}
		}

	}

	/**
	 * Returns the configured number of times that a suspected media server will
	 * be pinged before it is marked as unavailable.
	 */
	public int getRetryCount() {
		return _retryCount;
	}

	/**
	 * Sets the number of times that a suspected media server will continue to
	 * be pinged before that server is marked as unavailable.
	 * 
	 * @throws IllegalArgumentException
	 *             if the given value is negative.
	 */
	public void setRetryCount(int count) throws IllegalArgumentException {
		if (count < 0) {
			throw new IllegalArgumentException(
					"Retry count for media servers cannot be negative.");
		}
		_retryCount = count;
	}

	/**
	 * Returns the number of seconds for a media server interaction to timeout.
	 */
	public int getOperationTimeout() {
		return _operationTimeout;
	}

	/**
	 * 
	 */
	public void setOperationTimeout(int timeout) {
		_operationTimeout = timeout;
	}

	/**
	 * This method is invoked by the EMS to update the state of this component.
	 * If the value of the given "state" parameter is LOADED, the meta data on
	 * all provisioned media servers will be read from the backing store.
	 */
	public void changeState(MComponentState state)
			throws UnableToChangeStateException {
		if (_logger.isDebugEnabled()) {
			_logger.debug("changeState() called.  Setting component state to: "
					+ state);
		}

		try {
			if (state.getValue() == MComponentState.LOADED) {
				this.initialize();
			}
		} catch (Exception e) {
			String msg = "Error occurred while setting component state: "
					+ e.getMessage();
			_logger.error(msg, e);
			throw new UnableToChangeStateException(msg);
		}
	}

	/**
	 * This method initializes this object's state using the parameters
	 * specified in the ConfigRepository singleton. It internally calls the
	 * "initialize(Properties)" method.
	 * 
	 * @see com.baypackets.slee.common.ConfigRepository
	 * @see #initialize(Properties)
	 */
	public void initialize() throws InitializationFailedException {
		if (_logger.isDebugEnabled()) {
			_logger
					.debug("initialize(): Initializing component state from ConfigRepository...");
		}

		ConfigRepository config = (ConfigRepository) Registry
				.lookup(Constants.NAME_CONFIG_REPOSITORY);

		String sysappEnable = (String) config
				.getValue(Constants.PROP_SYSAPP_ENABLE);

		if (sysappEnable == null
				|| !sysappEnable.trim().contains("msheartbeat")) {
			_logger
					.info("roleChanged(): sysapp deploy properties does not contain 'msheartbeat'.");
			disabled = true;
		}

		Properties props = new Properties();

		if (config.getValue(HEARTBEAT_INTERVAL) != null) {
			props.setProperty(HEARTBEAT_INTERVAL, config
					.getValue(HEARTBEAT_INTERVAL));
		}
		if (config.getValue(NUM_OF_RETRIES) != null) {
			props.setProperty(NUM_OF_RETRIES, config.getValue(NUM_OF_RETRIES));
		}
		if (config.getValue(OPERATION_TIMEOUT) != null) {
			props.setProperty(OPERATION_TIMEOUT, config
					.getValue(OPERATION_TIMEOUT));
		}

		this.initialize(props);
	}

	/**
	 * This method initializes this object's state using the parameters
	 * specified in the given Properties object.
	 * 
	 * @throws InitializationFailedException
	 *             if an error occurs during initialization.
	 */
	public void initialize(Properties props)
			throws InitializationFailedException {
		boolean loggerEnabled = _logger.isDebugEnabled();

		if (loggerEnabled) {
			_logger
					.debug("initialize(): Obtaining info on all media servers from the backing store...");
		}

		getServerInfo();
		if (loggerEnabled) {
			_logger.debug(this.toString());
		}

		String heartBeatInterval = props.getProperty(HEARTBEAT_INTERVAL);
		String numOfRetries = props.getProperty(NUM_OF_RETRIES);
		String operationTimeout = props.getProperty(OPERATION_TIMEOUT);

		if (loggerEnabled) {
			_logger.debug("initialize(): HEARTBEAT_INTERVAL: "
					+ heartBeatInterval);
			_logger.debug("initialize(): NUM_OF_RETRIES: " + numOfRetries);
			_logger.debug("initialize(): OPERATION_TIMEOUT: "
					+ operationTimeout);
		}

		if (heartBeatInterval != null && !heartBeatInterval.trim().equals("")) {
			_heartBeatInterval = Integer.parseInt(heartBeatInterval);
		} else if (loggerEnabled) {
			_logger
					.debug("initialize(): Using default HEARTBEAT_INTERVAL value of: "
							+ _heartBeatInterval);
		}

		if (numOfRetries != null && !numOfRetries.trim().equals("")) {
			_retryCount = Integer.parseInt(numOfRetries);
		} else if (loggerEnabled) {
			_logger
					.debug("initialize(): Using default NUM_OF_RETRIES value of: "
							+ _retryCount);
		}

		if (operationTimeout != null && !operationTimeout.trim().equals("")) {
			_operationTimeout = Integer.parseInt(operationTimeout);
		} else if (loggerEnabled) {
			_logger
					.debug("initialize(): Using default OPERATION_TIMEOUT value of: "
							+ _operationTimeout);
		}

		if (loggerEnabled) {
			_logger.debug("initialize(): Registering command, " + MS_INFO
					+ " with the TelnetServer...");
		}

		TelnetServer telnetServer = (TelnetServer) Registry
				.lookup(Constants.NAME_TELNET_SERVER);
		telnetServer.registerHandler(MS_INFO, this, false);
		telnetServer.registerHandler(MS_ADMIN, this, false);

		// Obtain handle to Alarm service...
		_alarmService = (AseAlarmService) Registry
				.lookup(Constants.NAME_ALARM_SERVICE);

		if (loggerEnabled) {
			_logger
					.debug("initialize(): Successfully initialized MediaServerManager.");
		}
	}

	private void getServerInfo() {

		FileInputStream inPropStream = null;
		try {

			MediaServerDAO dao = MediaServerDAOFactory.getInstance()
					.getMediaServerDAO();
			Collection<MediaServer> mediaServers = dao.getAllMediaServers();

			if (_logger.isDebugEnabled()) {
				_logger
						.debug("MediaServerManagerImpl(): the Media server Status file is :"
								+ MEDIA_SERVER_STATUS_FILE);
			}

			mediaServerStatusFile = new Properties();
			File statusFile =new File(
					MEDIA_SERVER_STATUS_FILE);
			
			if(!statusFile.exists())
				statusFile.createNewFile();
			
			
			inPropStream = new FileInputStream(statusFile);
			try {
				mediaServerStatusFile.load(inPropStream);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			if (_logger.isDebugEnabled()) {
				_logger.debug("The MediaServers info from db is "
						+ mediaServers);
			}
			mediaServerList = new MediaServerList();

			if (mediaServers != null) {

				Iterator iterator = mediaServers.iterator();

				boolean updateMsPropertyFile=false;
				
				while (iterator.hasNext()) {
					MediaServerImpl mediaServer = (MediaServerImpl) iterator
							.next();

					String state = ACTIVE;

					String defaultstate = mediaServer.getDefaultState();

					String currentstate = mediaServerStatusFile
							.getProperty(mediaServer.getId());

					if (currentstate != null && !currentstate.isEmpty()) {
						state = currentstate;
						if (_logger.isDebugEnabled()) {
							_logger.debug("The MediaServer   "
									+ mediaServer.getId()
									+ " current state is " + state);
						}
					} else if (defaultstate != null && !defaultstate.isEmpty()) {
						state = defaultstate;
						if (_logger.isDebugEnabled()) {
							_logger.debug("The MediaServer "
									+ mediaServer.getId()
									+ " default state is " + state);
						}
					}

					if (state.equals(ACTIVE))
						mediaServer.setState(MediaServer.STATE_ACTIVE);
					else if (state.equals(DOWN))
						mediaServer.setState(MediaServer.STATE_DOWN);
					else if (state.equals(ADMIN)) {
						mediaServer.setState(MediaServer.STATE_ADMIN);
						mediaServer.disableHeartbeat();
						mediaServer.setHeartbeatAdminDisabled(true);
					} else if (state.equals(SUSPECT))
						mediaServer.setState(MediaServer.STATE_SUSPECT);

					// Current Media server entry is not available in Media server Status property
					// This can happen when new entry has been added in XML with new ID
					if(currentstate == null){
						mediaServerStatusFile.setProperty(mediaServer.getId(), state);
						updateMsPropertyFile=true;
					}
					if (_logger.isDebugEnabled()) {
						_logger.debug("The MediaServer State "
								+ mediaServer.getId() + " state is " + state);
					}

					mediaServerList.add(mediaServer);
				}
				
				// mediaServerStatusFile may have stale ID which have been removed from Media Server XML
				// file. We need to remove it from Status file and update status file. 
			 
				for(String msId : mediaServerStatusFile.stringPropertyNames()) {
					if(null == mediaServerList.find(msId)) {
						mediaServerStatusFile.remove(msId);
						updateMsPropertyFile = true;
					}
				}

				if(updateMsPropertyFile)
					storeProperty();
			}
		} catch (Exception e) {
			_logger.error("Error while reading media servers from file DAO could not read media servers !!!!!!!!!" +e);
			e.printStackTrace();
		} finally {
			try {
				inPropStream.close();
			} catch (IOException e) {
				_logger.error(e);
			}
		}

	}

	/**
	 * This method is invoked to display the current status of all provisioned
	 * media servers when the user enters the command, "ms-status" from a telnet
	 * console. The following info is displayed for each media server:
	 * <ul>
	 * <li> Unique identifier
	 * <li> Vendor name
	 * <li> IP address
	 * <li> Port number of listening process
	 * <li> Availability status (i.e. UP or DOWN)
	 * <li> Interval (in seconds) for which to ping the media server.
	 * <li> HeartBeat retry count.
	 * </ul>
	 */
	public String execute(String cmd, String[] args, InputStream in,
			OutputStream out) throws CommandFailedException {
		if (_logger.isDebugEnabled()) {
			_logger.debug("execute() called..." + cmd);
		}
		short command = -1;

		// if (args.length > 0) {
		// if (args[0].equals("-r")) {
		// getServerInfo();
		// } else {
		// return getUsage(cmd);
		// }
		// }

		if (cmd.equals(MS_INFO)) {
			command = CMD_MS_INFO;
		} else if (cmd.equals(MS_ADMIN)) {
			command = CMD_MS_ADMIN;
		}

		String retValue = this.execute(command, cmd, args, in, out);
		return retValue;
	}

	public String execute(short cmd, String command, String[] args,
			InputStream is, OutputStream os) throws CommandFailedException {

		try {
			switch (cmd) {
			case CMD_MS_INFO:

				if (args.length == 0)
					return reportMSStatus();
				else
					return this.getUsage(MS_INFO);
			case CMD_MS_ADMIN:

				if (args.length == 2) {

					if (args[1].equalsIgnoreCase(ADMIN_UP)) {
						return adminMediaServer(args[0], ADMIN_UP);
					} else if (args[1].equalsIgnoreCase(ADMIN_DOWN)) {
						return adminMediaServer(args[0], ADMIN_DOWN);
					} else
						return this.getUsage(MS_ADMIN);

				} else {
					return this.getUsage(MS_ADMIN);
				}

			}

		} catch (Exception e) {
			_logger.error("execute"+e.toString(), e);

			return e.getMessage();
		}
		return this.getUsage(MS_INFO);
	}

	/**
	 * Called by the "execute" method to handle a "status" command submitted
	 * from the telnet console. This will return the running status of all
	 * applications currently deployed to this AseHost.
	 */
	private String reportMSStatus() {

		// MediaServerManager msMgr = (MediaServerManager) Registry
		// .lookup(Constants.NAME_MEDIA_SERVER_MANAGER);

		if (_logger.isDebugEnabled()) {
			_logger.debug("reportMSStatus() called...");
		}

		Iterator it = this.findAll();

		if (!it.hasNext()) {
			return "No Media Servers Currently found !!!";
		}

		StringBuffer buffer = new StringBuffer();

		while (it.hasNext()) {
			MediaServer ms = (MediaServer) it.next();
			String msName = ms.getName();
			 if (_logger.isDebugEnabled()){
				 _logger.debug("MS Name = " + msName);
			}

			buffer.append("Media Server: ");
			buffer.append(AseStrings.DOUBLE_QUOT);
			buffer.append(ms.getName());
			buffer.append("\" ID \"");
			buffer.append(ms.getId());
			buffer.append("\" IP \"");
			buffer.append(ms.getHost().getHostAddress());
			buffer.append("\" Port \"");
			buffer.append(ms.getPort());
			buffer.append("\" Heartbeat Support \"");
			String hb =  _strings.getString("MediaServerImpl.OFF");

			if (ms.isHeartbeatEnabled()) {
				hb = _strings.getString("MediaServerImpl.ON");
			}
			buffer.append(hb);
			buffer.append("\" is currently in ");

			String state = "";
			if (ms.getState() == MediaServer.STATE_ACTIVE)
				state = ACTIVE;
			else if (ms.getState() == MediaServer.STATE_DOWN)
				state = DOWN;
			else if (ms.getState() == MediaServer.STATE_ADMIN)
				state = ADMIN;
			else if (ms.getState() == MediaServer.STATE_SUSPECT)
				state = SUSPECT;

			buffer.append(state);
			buffer.append(" state.");
			buffer.append("\r\n");
		}

		return buffer.toString();
	}

	private String adminMediaServer(String id, String adminState) {

		_logger.error("Received ms-admin command for id:"+id+" with admin state:"+adminState);

		MediaServer ms = this.findById(id);

		StringBuffer buffer = new StringBuffer();

		if (ms != null) {

			String msName = ms.getName();
			if(_logger.isDebugEnabled())
				_logger.debug("MediaServer Name = " + msName);

			if (adminState.equals(ADMIN_DOWN))
				this.mediaServerAdmin(id);
			else if (adminState.equals(ADMIN_UP)) {

				if (ms.getState() == MediaServer.STATE_ADMIN)
					this.mediaServerUp(id);
				else
					buffer.append(_strings.getString("MediaServerManagerImpl.NotInAdminState"));

			}

			buffer.append("Media Server: ");
			buffer.append(AseStrings.DOUBLE_QUOT);
			buffer.append(ms.getName());
			buffer.append("\" ID \"");
			buffer.append(ms.getId());
			buffer.append("\" is now in ");
			String state = "";

			if (ms.getState() == MediaServer.STATE_ACTIVE)
				state = ACTIVE;
			else if (ms.getState() == MediaServer.STATE_DOWN)
				state = DOWN;
			else if (ms.getState() == MediaServer.STATE_ADMIN)
				state = ADMIN;
			else if (ms.getState() == MediaServer.STATE_SUSPECT)
				state = SUSPECT;

			buffer.append(state);
			buffer.append(" state.");
			buffer.append("\r\n");

		} else {
			buffer.append(_strings.getString("MediaServerManagerImpl.NoMSFoundForAdmin") +id);
		}
		String reult=buffer.toString();

		_logger.error("Final result for command : "+reult);

		return reult;

	}

	/**
	 * Returns the usage statement for the "ms-info" telnet command.
	 */
	public String getUsage(String cmd) {
		return _strings.getString("MediaServerManagerImpl." + cmd + "Help");
	}

	/**
	 * Called by the EMS to update this component's configuration.
	 */
	public void updateConfiguration(Pair[] pairs, OperationType arg1)
			throws UnableToUpdateConfigException {
		boolean loggerEnabled = _logger.isDebugEnabled();

		if (loggerEnabled) {
			_logger.debug("updateConfiguration() called...");
		}

		if (pairs == null) {
			return;
		}

		try {
			for (int i = 0; i < pairs.length; i++) {
				String paramName = (String) pairs[i].getFirst();
				String paramValue = (String) pairs[i].getSecond();

				if (paramName.equals(HEARTBEAT_INTERVAL)) {
					if (loggerEnabled) {
						_logger
								.debug("updateConfiguration(): Setting HEARTBEAT_INTERVAL property to: "
										+ paramValue);
					}
					_heartBeatInterval = Integer.parseInt(paramValue);
				} else if (paramName.equals(NUM_OF_RETRIES)) {
					if (loggerEnabled) {
						_logger
								.debug("updateConfiguration(): Setting NUM_OF_RETRIES property to: "
										+ paramValue);
					}
					_retryCount = Integer.parseInt(paramValue);
				} else if (paramName.equals(OPERATION_TIMEOUT)) {
					if (loggerEnabled) {
						_logger
								.debug("updateConfiguration(): Setting OPERATION_TIMEOUT property to: "
										+ paramValue);
					}
				}
			}
		} catch (Exception e) {
			String msg = "Error occurred while updating component configuration: "
					+ e.getMessage();
			_logger.error(msg, e);
			throw new UnableToUpdateConfigException(msg);
		}
	}

	/**
	 * Callback method used to notify this object when it's role in the cluster
	 * has changed.
	 */
	public void roleChanged(String clusterId, PartitionInfo pInfo) {
		// TODO Auto-generated method stub
	}

	/**
	 * Returns a string representation of this object.
	 */
	public String toString() {
		StringBuffer buffer = new StringBuffer();

		Object[] params = { new Integer(_heartBeatInterval),
				new Integer(_retryCount), new Integer(_operationTimeout) };
		buffer.append(_strings.getString("MediaServerManagerImpl.toString",
				params)
				+ "\r\n");

		if (disabled) {
			buffer.append("\r\n"
					+ _strings.getString("MediaServerManagerImpl.disabled")
					+ "\r\n");
		}

		if (mediaServerList == null || mediaServerList.isEmpty()) {
			buffer.append(_strings
					.getString("MediaServerManagerImpl.noMediaServers"));
		} else {
			buffer.append(mediaServerList.toString());

		}

		return buffer.toString();
	}

	private class MediaServerList

	{
		private class MediaServerNode

		{
			MediaServerImpl data;
			MediaServerNode next;

			public MediaServerNode(MediaServerImpl data, MediaServerNode next)

			{
				this.data = data;
				this.next = next;
			}

			public String toString()

			{
				return this.data.toString();
			}

		}

		MediaServerNode rear;
		MediaServerNode current;

		public MediaServerList()

		{
			rear = null;
		}

		public synchronized String toString()

		{
			if (rear == null)
				return "Media Servers List ( )";
			MediaServerNode temp = rear.next;
			String retval = "Media Servers List ( ";

			do {
				retval = (retval + temp.data.toString() + " ");
				temp = temp.next;
			} while (temp != rear.next);

			retval += ")";
			return retval;
		}

		public synchronized boolean isEmpty()

		{
			return (rear == null);
		}

		public void clear()

		{
			rear = null;
		}

		public synchronized void add(MediaServer mediaServer)

		{
			MediaServerNode temp = new MediaServerNode(
					(MediaServerImpl) mediaServer, null);
			if (rear == null)

			{
				temp.next = temp;
				rear = temp;
				current = rear;
			}

			else

			{
				temp.next = rear.next;
				rear.next = temp;
			}

		}

		public  MediaServer getFirstMediaServer() {

			synchronized (rear) {
			
			if (this.rear != null)
				return this.rear.data;
			else
				return null;
			}
		}

		public synchronized Iterator<MediaServer> findAll() {
			Map<String, MediaServer> map = new HashMap<String, MediaServer>();

			MediaServer ms = null;
			MediaServerNode temp = rear;
			try {
				do {

					ms = temp.next.data;
					map.put(ms.getId(), ms);
					temp = temp.next;

					 if (_logger.isDebugEnabled()) {	
						 _logger.debug(" The MediaServers Found is: " + ms);
					 }

				} while (mediaServerList.getFirstMediaServer() != ms);

				 if (_logger.isDebugEnabled()) {	
					 _logger.debug(" The all the MediaServers are " + map);
				}
			} catch (Exception e) {
				_logger.error("findAll..media servers"+e);
			}

			return map.values().iterator();

		}

		public synchronized MediaServer find(String id)

		{
			if(rear==null){
				return null;
			}
        synchronized (rear) {
	

			if (isEmpty())
				return null;
			MediaServerNode temp = rear.next;

			do {
				if (temp.data.getId().equals(id)) {

					return temp.data;

				}
				temp = temp.next;
			} while (temp != rear.next);
			
       }

			return null;
		}

		public synchronized void remove(String id) {
			MediaServerNode curr = this.rear;
        synchronized (rear) {
	

			do {
				if (curr.next.data.getId().equals(id)) {
					MediaServerNode temp = curr.next;
					curr.next = temp.next;
				}
				curr = curr.next;

				if (curr.next == this.rear && curr.data.getId().equals(id)) {
					this.rear.next = null;
					curr.next = null;
				}

			} while (curr != this.rear);
			
        }
		}

		public synchronized MediaServerNode getNext() {
			this.current = this.current.next;
			return this.current;
		}

		public synchronized MediaServer getNextActiveMediaServer() {

			MediaServerNode nextMs = (MediaServerNode) mediaServerList
					.getNext();
			MediaServerNode tmp = nextMs;
			int state = MediaServer.STATE_DOWN;

			do {

				state = tmp.data.getState();

				if (state == MediaServer.STATE_ACTIVE) {

					 if (_logger.isDebugEnabled()) {
						 _logger.debug(" Returning media server as " + tmp);
					}

					return tmp.data;
				}

				tmp = tmp.next;

			} while (tmp != nextMs);

			 if (_logger.isDebugEnabled()) {
				 _logger.debug(" No Active media server Found :( ");
			 }

			return null;

		}
	}
	public MediaServer getMediaServer(String id){
		MediaServerImpl server = mediaServerList != null ? (MediaServerImpl) mediaServerList
				.find(id) : null;
		return server;
	}

	public static void main(String[] args)

	{
		 /*MediaServerImpl m1 =new MediaServerImpl();
		 m1.setId("snoshore");
		 m1.setName("snoshore");
		 m1.enableHeartbeat();
		 m1.setIsRemote(0);
		 m1.setDefaultState(ACTIVE);
		 m1.setState(MediaServer.STATE_ACTIVE);
				
		 MediaServerImpl m2 =new MediaServerImpl();
		 m2.setId("Convedia");
		 m2.setName("snoshore");
		 m2.enableHeartbeat();
		 m2.setIsRemote(0);
		 m2.setDefaultState(ACTIVE);
		 m2.setState(MediaServer.STATE_ACTIVE);
				
		 MediaServerImpl m3 =new MediaServerImpl();
		 m3.setId("dialogic");
		 m3.setName("snoshore");
		 m3.enableHeartbeat();
		 m3.setIsRemote(1);
		 m3.setDefaultState(ACTIVE);
		 m3.setState(MediaServer.STATE_ACTIVE);
		
		 MediaServerImpl m4 =new MediaServerImpl();
		 m4.setId("dialogic1");
		 m4.setName("snoshore");
		 m4.enableHeartbeat();
		 m4.setIsRemote(1);
		 m4.setDefaultState(ACTIVE);
		 m4.setState(MediaServer.STATE_ACTIVE);
				
		 MediaServerManagerImpl mgr =new MediaServerManagerImpl();
		 mgr.addMediaServer(m1);
		 mgr.addMediaServer(m2);
		 mgr.addMediaServer(m3);
		 mgr.addMediaServer(m4);
				
		// System.out.println(" MSMgr is "+mgr);
		 System.out.println( "First MS selected is " + mgr.selectByCapabilities(0, 0));
		 System.out.println( "second MS selected is " + mgr.selectByCapabilities(0, 1));
		 System.out.println( "Third MS selected is " + mgr.selectByCapabilities(0, 0));
		 System.out.println( "Fourth MS selected is " + mgr.selectByCapabilities(0, 1));
		 System.out.println("Local MS count is " + mgr.getActiveMSCount(0, 0));
		 System.out.println("Remote MS count is " + mgr.getActiveMSCount(0, 1));*/
		 
	}

	@Override
	public MediaServer selectByCapabilities(int capabilities, int isRemote) {
		// TODO Auto-generated method stub
		return null;
	}
}
