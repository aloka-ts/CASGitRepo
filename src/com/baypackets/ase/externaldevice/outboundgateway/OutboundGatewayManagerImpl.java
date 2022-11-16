/*
 *
 */
package com.baypackets.ase.externaldevice.outboundgateway;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.apache.log4j.Logger;

import com.baypackets.ase.common.Registry;
import com.baypackets.ase.control.PartitionInfo;
import com.baypackets.ase.control.RoleChangeListener;
import com.baypackets.ase.sbb.OutboundGateway;
import com.baypackets.ase.sbb.OutboundGatewaySelector;
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
 * Enables provisioning of the outbound gateways in SAS by implmenting the
 * <code>OutboundGatewayManager</code> and <code>OutboundGatewaySelector</code> interfaces.
 *
 *<p>
 * The OutboundGatewayManagerImpl implements the selectXXX methods by
 * load balancing the requests by round robin among the ACTIVE outbound gateways.
 *
 *<p>
 * The Outbound Gateway Status will be considered active as soon as it is added to the list.
 * If the HeartBeat servlet reports the failure, then it will be marked INACTIVE.
 * The OutboundGatewayManager will raise an ALARM to EMS to notify the failure of the Outbound Gateway.
 * If a gateway is marked as suspect it will not be used if ACTIVE gateways are available
 *<p>
 *
 */
public class OutboundGatewayManagerImpl implements OutboundGatewayManager, OutboundGatewaySelector,
                           MComponent, RoleChangeListener, CommandHandler {

    public static final int ROUND_ROBIN = 1;
    public static final int FIRST_AVAILABLE = 2;

    private static short FIND = 0;

    private static Logger logger = Logger.getLogger(OutboundGatewayManagerImpl.class);
    private static StringManager strings = StringManager.getInstance(OutboundGatewayManagerImpl.class.getPackage());

    private final ReentrantReadWriteLock readWriteLock = new ReentrantReadWriteLock();
    
    private final Lock readLock  = readWriteLock.readLock();
    private final Lock writeLock = readWriteLock.writeLock();
    
    private List<OutboundGateway> gateways;
    private Map <String,OutboundGatewayGroup> gatewayGroupMap;
    private int heartBeatInterval = Constants.DEFAULT_GW_HEARTBEAT_INTERVAL;
    private int retryCount = Constants.DEFAULT_GW_NUM_OF_RETRIES;
    private AseAlarmService alarmService;

    private int counter = 0;

    private int selectionMode = ROUND_ROBIN;
    private boolean usePriority = false;

    private OutboundGateway obProxy = null;
    private boolean disabled = false;
    
    /**
     * Default constructor.
     */
    public OutboundGatewayManagerImpl() {
    super();
    }

    /**
     * Returns the specified OutboundGateway or NULL if none
     * is found for the given ID.
     */
    public OutboundGateway findById(String id) {
    	OutboundGateway gw = null;
    	readLock.lock();
    	try{
    		if (gateways != null) {
    			Iterator<OutboundGateway> i = gateways.iterator();
    			while (i.hasNext()) {
    				OutboundGateway tmp = i.next();
    				if (tmp.getId().equals(id)) {
    					gw = tmp;
    					break;
    				}
    			}
    		}
    	}finally{
    		readLock.unlock();
    	}
    	return gw;
    }


    /**
     * Returns an iteration over all provisioned outbound gateways.  If no
     * outbound gateways are currently provisioned with the platform, an
     * empty Iterator is returned.
     */
    public Iterator findAll() {
    	readLock.lock();
    	try{
    		return gateways != null ? gateways.iterator() : new ArrayList(0).iterator();
    	}finally{
    		readLock.unlock();
    	}
    }


    /**
     * This method is invoked to add a new outbound gateway to the list of
     * provisioned outbound gateways.
     */
    public void addOutboundGateway(OutboundGateway obgw) {
    	
    	if(obgw==null)
    		return;
    	if(logger.isDebugEnabled()){
    		logger.debug("Adding outbound gateway: "+obgw.getId()+" with group id:"+obgw.getGroupId());
    	}
    	writeLock.lock();
    	try{
    		String groupId=obgw.getGroupId();
    		if (gateways == null) {
    			gateways = new ArrayList<OutboundGateway>();
    			gatewayGroupMap =new HashMap <String,OutboundGatewayGroup> ();
    		}
    		gateways.add(obgw);
    		
    		if(gatewayGroupMap.containsKey(groupId)){
    			gatewayGroupMap.get(groupId).add(obgw);
        	}else{
        		List<OutboundGateway> newGroupList=new ArrayList<OutboundGateway>();
        		newGroupList.add(obgw);
        		gatewayGroupMap.put(groupId,new OutboundGatewayGroup(newGroupList));
        	}
    		
    	}finally{
    		writeLock.unlock();
    	}
    }


    /**
     * This method removes the specified outbound gateway from the list of
     * provisioned outbound gateways.
     */
    public void removeOutboundGateway(String id) {
    	// Not using Iterator here as .remove() may not be implemented
    	if(logger.isDebugEnabled()){
    		logger.debug("Removing outbound gateway: "+id);
    	}
    	if(id==null){
    		return;
    	}
    	writeLock.lock();
    	try{
    		if (gateways != null) {
    			for (int i = 0; i < gateways.size(); i++) {
    				OutboundGateway obgw = (OutboundGateway)gateways.get(i);
    				if (obgw.getId().equals(id)) {
    					gateways.remove(i);
    					String groupId=obgw.getGroupId();
    					OutboundGatewayGroup group=gatewayGroupMap.get(groupId);
    					if(group!=null){	
    						group.remove(id);
    						if(group.size()==0){
    							gatewayGroupMap.remove(groupId);
    						}
    					}
    					break;
    				}
    			}
    		}
    	}finally{
    		writeLock.unlock();
    	}
    }


    /**
     * Returns the configured heart beat interval for the outbound gateways.
     */
    public int getOutboundGatewayHeartBeatInterval() {
        return heartBeatInterval;
    }


    /**
     * Sets the interval (in seconds) for which to ping the provisioned
     * outbound gateways.
     */
    public void setOutboundGatewayHeartBeatInterval(int interval) {
        heartBeatInterval = interval;
    }

    /* Implemenation of the OutboundGatewaySelector Interface */
    /**
     * Return an indication if outbound gateway processing is needed
     *
     * @return true if outbound gateway processing is needed
     */
    public boolean processingActive() {
    	//[vgoel] bug 6977@SBTM
    	if(disabled)
    		return false;
    	
        return ((obProxy != null) || ((gateways != null) && (gateways.size() > 0)));
    }

    /**
     * Return a outbound gateway
     * If obProxy is set return that object
     * Otherwise, if no gateways are configure return null
     * Otherwise, call selectOutboundGateway to get active gateway
     * If no gateways active return empty OutboundGateway object
     */
    public OutboundGateway select() {
        if (obProxy != null) {
            return obProxy;
        } else {
            return selectOutboundGateway(FIND, null);
        }
    }

    @Override
    public OutboundGateway selectFromGroup(String groupId) {
    	if(logger.isDebugEnabled()){
    		logger.debug("Inside selectFromGroup() from group: "+groupId);
    	}
    	OutboundGateway gw=null;
    	if(groupId!=null){
    		readLock.lock();
    		try{
    			OutboundGatewayGroup obgwGroup=gatewayGroupMap.get(groupId);
    			if(obgwGroup!=null){
    				gw=obgwGroup.getNextGateway(usePriority,selectionMode,null);
    			}else{
    				logger.error("No group found by groupId:"+groupId);
    			}
    		}finally{
    			readLock.unlock();
    		}
    	}
    	return gw;
    }
    
    @Override
    public OutboundGateway selectFromGroupExcept(String groupId,String outboundgatewayId) {
    	OutboundGateway gw=null;
    	if(groupId!=null){
    		readLock.lock();
    		try{
    			OutboundGatewayGroup obgwGroup=gatewayGroupMap.get(groupId);
    			if(obgwGroup!=null){
    				gw=obgwGroup.getNextGateway(usePriority,selectionMode,outboundgatewayId);
    			}
    		}finally{
    			readLock.unlock();
    		}
    	}
    	return gw;
    }
    
    public int selectionMode() {
        return selectionMode;
    }


    /**
     * Helper method invoked by the "selectXXX" methods.
     * servers are filtered by FIND method and by priority (if enabled)
     * then ACTIVE servers will block SUSPECT server selection.
     * Note: a higher priority SUSPECT server will be selected before an ACTIVE
     *       one with lower priority (correct?)
     */
    private OutboundGateway selectOutboundGateway(short criteriaType, Object criteria) {
        if (gateways == null) {
            return null;
        }

        List<OutboundGateway> results = null;

        readLock.lock();
        
        try {
            Iterator<OutboundGateway> iterator = gateways.iterator();

            boolean returnSuspects = true;
            int currentPriority = java.lang.Integer.MAX_VALUE;
            results = new ArrayList<OutboundGateway>();
            while (iterator.hasNext()) {
                OutboundGateway server = (OutboundGateway)iterator.next();

                if (server.getState() == OutboundGateway.STATE_DOWN) {
                    continue;
                }

                if (usePriority) {
                    if (server.getPriority() > currentPriority) {
                        continue;
                    } else if (server.getPriority() < currentPriority) {
                        // If new higher priority server, reset list
                        results.clear();
                        returnSuspects = true;
                    }
                }

                if (server.getState() == OutboundGateway.STATE_ACTIVE) {
                    if (returnSuspects && (results != null)) {
                        results.clear();
                    }
                    returnSuspects = false;
                } else if (!returnSuspects) {
                    // if active servers already active then do not select
                    // suspect
                    continue;
                }

                results.add(server);
                currentPriority = server.getPriority();
            }
        }finally{
        	readLock.unlock();
        }

        OutboundGateway retVal = null;
        if (results.size() != 0) {
            if (selectionMode == ROUND_ROBIN) {
                // Apply "load balancing" logic...
                // This logic will vary if there are up/down servers
                int index = counter++ % results.size();
                retVal = (OutboundGateway)results.get(index);
            } else {
                retVal = (OutboundGateway)results.get(0);
            }
        }
        return retVal;
    }


    /**
     * This method marks the specified outbound gateway as being unavailable
     * and sends an error alarm to EMS.
     *
     * @param id  The ID of the outbound gateway that has gone down.
     */
    public void outboundGatewaySuspect(String id) {
        boolean loggerEnabled = logger.isDebugEnabled();

        OutboundGatewayImpl server = (OutboundGatewayImpl)findById(id);
        if (server != null) {
            if (server.getState() == OutboundGateway.STATE_ACTIVE) {
                server.setState(OutboundGateway.STATE_SUSPECT);

                if (loggerEnabled) {
                    logger.debug("outboundGatewaySuspect(): Received notification that outbound gateway with ID, " + id + " is suspect.");
                }
            }

        } else if (loggerEnabled) {
            logger.debug("outboundGatewaySuspect(): No outbound gateway found for the specified ID.");
        }
    }


    /**
     * This method marks the specified outbound gateway as being unavailable
     * and sends an error alarm to EMS.
     *
     * @param id  The ID of the outbound gateway that has gone down.
     */
    public void outboundGatewayDown(String id) {
        boolean loggerEnabled = logger.isDebugEnabled();

        OutboundGatewayImpl server = (OutboundGatewayImpl)findById(id);

        if (server != null) {

            if (server.getState() != OutboundGateway.STATE_DOWN) {
                if (loggerEnabled) {
                    logger.debug("outboundGatewayDown(): Received notification that outbound gateway with ID, " + id + " is down.");
                }
                server.setState(OutboundGateway.STATE_DOWN);

                if (loggerEnabled) {
                    logger.debug("outboundGatewayDown(): Notifying EMS that outbound gateway is down...");
                }

                try {
                    alarmService.sendAlarm(Constants.ALARM_OUTBOUND_GATEWAY_DOWN,
                               strings.getString("OutboundGatewayManagerImpl.outboundGatewayDown",
                            		   new String[] {server.getId(),server.getGroupId(),server.getHost().getHostAddress(),server.getPort()+AseStrings.BLANK_STRING}));
                } catch (Exception e) {
                    logger.error("Error occurred while notifying EMS of downed outbound gateway: " + e.getMessage(), e);
                }
            }
        } else if (loggerEnabled) {
            logger.debug("outboundGatewayDown(): No outbound gateway found for the specified ID.");
        }
    }


    /**
     * This method marks the specified outbound gateway as being available and
     * then sends a clearing alarm to EMS for the previously sent error
     * alarm.
     *
     * @param id  The ID of the outbound gateway that has come up.
     */
    public void outboundGatewayUp(String id) {
        boolean loggerEnabled = logger.isDebugEnabled();

        OutboundGatewayImpl server = (OutboundGatewayImpl)findById(id);

        if (server != null) {
            if (server.getState() != OutboundGateway.STATE_ACTIVE) {
                if (loggerEnabled) {
                    logger.debug("outboundGatewayUp(): Received notification that outbound gateway with ID, '" + id + "' is now up.");
                }

                int oldState = server.getState();

                server.setState(OutboundGateway.STATE_ACTIVE);

                if (oldState == OutboundGateway.STATE_DOWN) {
                    if (loggerEnabled) {
                       logger.debug("outboundGatewayUp(): Notifying EMS that outbound gateway is up...");
                    }

                    try {
                       alarmService.sendAlarm(Constants.ALARM_OUTBOUND_GATEWAY_UP,
                               strings.getString("OutboundGatewayManagerImpl.outboundGatewayUp",
                            		   new String[] {server.getId(),server.getGroupId(),server.getHost().getHostAddress(),server.getPort()+AseStrings.BLANK_STRING}));
                    } catch (Exception e) {
                       logger.error("Error occurred while notifying EMS of recovered outbound gateway: " + e.getMessage(), e);
                    }
                }
            }
        } else if (loggerEnabled) {
            logger.debug("outboundGatewayUp(): No outbound gateway found for the specified ID.");
        }
    }


    /**
     * Returns the configured number of times that a suspected outbound gateway
     * will be pinged before it is marked as unavailable.
     */
    public int getRetryCount() {
        return retryCount;
    }


    /**
     * Sets the number of times that a suspected outbound gateway will continue
     * to be pinged before that server is marked as unavailable.
     *
     * @throws IllegalArgumentException if the given value is negative.
     */
    public void setRetryCount(int count) throws IllegalArgumentException {
        if (count < 0) {
            throw new IllegalArgumentException("Retry count for outbound gateways cannot be negative.");
        }
        retryCount = count;
    }

    /**
     * This method is invoked by the EMS to update the state of this
     * component.  If the value of the given "state" parameter is LOADED,
     * the meta data on all provisioned outbound gateways will be read from
     * the backing store.
     */
    public void changeState(MComponentState state) throws UnableToChangeStateException {
        if (logger.isDebugEnabled()) {
            logger.debug("changeState() called.  Setting component state to: " + state);
        }

        try {
            if (state.getValue() == MComponentState.LOADED) {
                this.initialize();
            }
        } catch (Exception e) {
            String obgwg = "Error occurred while setting component state: " + e.getMessage();
            logger.error(obgwg, e);
            throw new UnableToChangeStateException(obgwg);
        }
    }


    /**
     * This method initializes this object's state using the parameters
     * specified in the ConfigRepository singleton.  It internally calls
     * the "initialize(Properties)" method.
     *
     * @see com.baypackets.slee.common.ConfigRepository
     * @see #initialize(Properties)
     */
    public void initialize() throws InitializationFailedException {
        if (logger.isDebugEnabled()) {
            logger.debug("initialize(): Initializing component state from ConfigRepository...");
        }

        ConfigRepository config = (ConfigRepository)Registry.lookup(Constants.NAME_CONFIG_REPOSITORY);

        String sysappEnable = (String)config.getValue(Constants.PROP_SYSAPP_ENABLE);

        if(sysappEnable == null || !sysappEnable.trim().contains("obgwapp")) {
            
            disabled = true;
        }

        Properties props = new Properties();

        if (config.getValue(Constants.OID_GW_HEARTBEAT_INTERVAL) != null) {
            props.setProperty(Constants.OID_GW_HEARTBEAT_INTERVAL,
                      config.getValue(Constants.OID_GW_HEARTBEAT_INTERVAL));
        }
        if (config.getValue(Constants.OID_GW_NUM_OF_RETRIES) != null) {
            props.setProperty(Constants.OID_GW_NUM_OF_RETRIES,
                      config.getValue(Constants.OID_GW_NUM_OF_RETRIES));
        }

        if (config.getValue(Constants.OID_GW_SELECTION_MODE) != null) {
            props.setProperty(Constants.OID_GW_SELECTION_MODE,
                      config.getValue(Constants.OID_GW_SELECTION_MODE));
        }

        if (config.getValue(Constants.OID_SIP_OUTBOUND_PROXY) != null) {
            props.setProperty(Constants.OID_SIP_OUTBOUND_PROXY,
                      config.getValue(Constants.OID_SIP_OUTBOUND_PROXY));
        }

        this.initialize(props);
    }


    /**
     * This method initializes this object's state using the parameters
     * specified in the given Properties object.
     *
     * @throws InitializationFailedException if an error occurs during
     * initialization.
     */
    public void initialize(Properties props) throws InitializationFailedException {
        boolean loggerEnabled = logger.isDebugEnabled();

        if (loggerEnabled) {
            logger.debug("initialize(): Obtaining info on all outbound gateways from the backing store...");
        }

        getGatewayInfo();

        String sInterval = props.getProperty(Constants.OID_GW_HEARTBEAT_INTERVAL);
        String sNumOfRetries = props.getProperty(Constants.OID_GW_NUM_OF_RETRIES);
        String selectionMode = props.getProperty(Constants.OID_GW_SELECTION_MODE);
        String outboundProxy = props.getProperty(Constants.OID_SIP_OUTBOUND_PROXY);

        setHeartBeatInterval(sInterval, false);

        setRetryCount(sNumOfRetries, false);

        setSelectionMode(selectionMode, false);

        setOutboundProxy(outboundProxy, false);

        String cmd = strings.getString("OutboundGatewayManagerImpl.cmd");
        if (loggerEnabled) {
            logger.debug("initialize(): Registering command, " + cmd + " with the TelnetServer...");
        }

        TelnetServer telnetServer = (TelnetServer)Registry.lookup(Constants.NAME_TELNET_SERVER);
        telnetServer.registerHandler(cmd, this);

        // Obtain handle to Alarm service...
        alarmService = (AseAlarmService)Registry.lookup(Constants.NAME_ALARM_SERVICE);

        if (loggerEnabled) {
            logger.debug("initialize(): Successfully initialized OutboundGatewayManager.");
            logger.debug(this.toString());
        }
        }

        private void getGatewayInfo() {
        OutboundGatewayDAO dao = OutboundGatewayDAOFactory.getInstance().getOutboundGatewayDAO();
        Collection outboundGateways = null;
        try {
            outboundGateways = dao.getAllDevices();
        } catch (Exception e) {
            logger.error("Exception during attempt to retrieve gateway info.", e);
        }

        List<OutboundGateway> tmpList = null;
        Map <String,OutboundGatewayGroup> tempMap=null;
        
        if (outboundGateways != null) {
            tmpList = new ArrayList<OutboundGateway>(outboundGateways.size());
            tempMap = new HashMap <String,OutboundGatewayGroup> ();
            Iterator<OutboundGateway> iterator=outboundGateways.iterator();
            while(iterator.hasNext()){
            	OutboundGateway gw=iterator.next();
            	tmpList.add(gw);
            	String groupId=gw.getGroupId();
            	if(tempMap.containsKey(groupId)){
            		tempMap.get(groupId).add(gw);
            	}else{
            		List<OutboundGateway> newGroupList=new ArrayList<OutboundGateway>();
            		newGroupList.add(gw);
            		tempMap.put(groupId,new OutboundGatewayGroup(newGroupList));
            	}
            }
        }
        
        writeLock.lock();
        gateways = tmpList;
        gatewayGroupMap = tempMap;	
        writeLock.unlock();
        
    }

    /**
     * Returns the usage statement for the "gw-info" telnet command.
     */
    public String getUsage(String cmd) {
        return strings.getString("OutboundGatewayManagerImpl.usage");
    }

    private void setHeartBeatInterval(String sInterval, boolean update) {
        // if update is set then do not change value on error.
        logger.debug("Setting heartbeat interval: " + sInterval);
        int interval;
        if (update) {
            interval = heartBeatInterval;
        } else {
            interval = Constants.DEFAULT_GW_HEARTBEAT_INTERVAL;;
        }
        try {
            int tmpInt = Integer.parseInt(sInterval);
            if (tmpInt < 0) {
                logger.error("Invalid heartbeat interval: " + sInterval);
            } else {
                interval = tmpInt;
            }
        } catch (Exception e) {
            logger.error("Invalid heartbeat interval: " + sInterval + " " + e, e);
        }
        heartBeatInterval = interval;
    }

    private void setRetryCount(String sNumOfRetries, boolean update) {
        // if update is set then do not change value on error.
	if(logger.isDebugEnabled() ) {
        logger.debug("Setting number of retries: " + sNumOfRetries);
		}
        int count;
        if (update) {
            count = retryCount;
        } else {
            count = Constants.DEFAULT_GW_NUM_OF_RETRIES;;
        }
        try {
            int tmpInt = Integer.parseInt(sNumOfRetries);
            if (tmpInt < 0) {
                logger.error("Invalid retry count: " + sNumOfRetries );
            } else {
                count = tmpInt;
            }
        } catch (Exception e) {
            logger.error("Invalid retry count: " + sNumOfRetries + " " + e, e);
        }
        retryCount = count;
    }


    private void setSelectionMode(String sMode, boolean update) {
        // if update is set then do not change value on error.
		if(logger.isDebugEnabled() ) {
        logger.debug("Setting selection mode : " + sMode);
	}
        int mode;
        boolean priority;
        if (update) {
            mode = selectionMode;
            priority = usePriority;
        } else {
            mode = ROUND_ROBIN;
            priority = false;
        }

        try {
            int intMode = Integer.parseInt(sMode);

            switch(intMode) {

            case Constants.GW_SELECTION_ROUND_ROBIN:
                mode = ROUND_ROBIN;
                priority = false;
            break;
            case Constants.GW_SELECTION_FIRST_AVAILABLE:
                mode = FIRST_AVAILABLE;
                priority = false;
            break;
            case Constants.GW_SELECTION_ROUND_ROBIN_PRI:
                priority = true;
            break;
            case Constants.GW_SELECTION_FIRST_AVAILABLE_PRI:
                mode = FIRST_AVAILABLE;
                priority = true;
            break;
            default:
                logger.error("Error setting selection mode " + sMode );
            break;

            }
        } catch (Exception e) {
            logger.error("Error setting selection mode " + sMode + " " + e, e);
        }

        selectionMode = mode;
        usePriority = priority;
    }

    private void setOutboundProxy(String outboundProxy, boolean update) {

        if ((outboundProxy == null) || (outboundProxy.trim().equals(""))) {
		if(logger.isDebugEnabled() ){

            logger.debug("Outbound Proxy is reset");
		}
            obProxy = null;
        } else {
		if(logger.isDebugEnabled() ){

            logger.debug("Setting Outbound Proxy : " + outboundProxy);
		}

            char separator = '|';

            String ipAddress;
            int port;
            int index = outboundProxy.indexOf(separator);
            if(index != -1) {

                ipAddress = outboundProxy.substring(0, index);

                ipAddress = ipAddress.trim();

                port = 5060;
                try {
                    port = Integer.parseInt(outboundProxy.substring(index+1));
                } catch (Exception e) {
                    logger.error("Error setting port for outbound proxy" + " " + e, e);
                }
            }
            else {
					if(logger.isDebugEnabled() ){
                logger.debug("No separator " + separator + " found in outbound proxy using default 5060");
			}
                ipAddress = outboundProxy.trim();
                port = 5060;
            }

            OutboundGatewayImpl obImpl = new OutboundGatewayImpl();
            obImpl.setName(outboundProxy);
            try {
                obImpl.setHost(InetAddress.getByName(ipAddress));
            } catch (Exception e) {
                logger.error("Error setting outbound proxy " + outboundProxy + " " + e,  e);
                return;
            }
            obImpl.setPort(port);

            obProxy = obImpl;
				if(logger.isDebugEnabled() ){
            logger.debug("Setting outBound proxy Server, IP : " + ipAddress + ", Port : " + port);
		}
        }
    }


    /**
     * Returns a string representation of this object.
     */
    public String toString() {
        StringBuffer buffer = null;

        buffer = new StringBuffer();

        String sMode;
        if (selectionMode == ROUND_ROBIN) {
            if (usePriority) {
                sMode = strings.getString("OutboundGatewayManagerImpl.modeRR_PRI");
            } else {
                sMode = strings.getString("OutboundGatewayManagerImpl.modeRR");
            }
        } else {
            if (usePriority) {
                sMode = strings.getString("OutboundGatewayManagerImpl.modeFA_PRI");
            } else {
                sMode = strings.getString("OutboundGatewayManagerImpl.modeFA");
            }
        }

        Object[] params = {Integer.toString(heartBeatInterval), Integer.toString(retryCount), sMode};
        buffer.append("\r\n" + strings.getString("OutboundGatewayManagerImpl.toString", params) + "\r\n");

        if (disabled) {
            buffer.append("\r\n" + strings.getString("OutboundGatewayManagerImpl.disabled") + "\r\n");
        }

        OutboundGateway tmp = obProxy;
        if (tmp != null) {
            Object[] p = {tmp.getHost().getHostAddress(), Integer.toString(tmp.getPort())};
            buffer.append("\r\n" + strings.getString("OutboundGatewayManagerImpl.outboundProxy", p) + "\r\n");
        }

        if (gateways == null || gateways.isEmpty()) {
            buffer.append("\r\n" + strings.getString("OutboundGatewayManagerImpl.noOutboundGateways") + "\r\n");
        } else {
            buffer.append("\r\n");
            readLock.lock();
            try{
            	Iterator<OutboundGateway> iterator = gateways.iterator();

            	while (iterator.hasNext()) {
            		buffer.append(iterator.next());
            		buffer.append("\n");
            	}
            }finally{
            	readLock.unlock();
            } 
            
            buffer.append("\r\n");
        }

        return buffer.toString();
    }

    /**
     * This method is invoked to display the current status of all
     * provisioned outbound gateways when the user enters the command,
     * "gw-info" from a telnet console.
     */
    public String execute(String cmd, String[] args, InputStream in, OutputStream out) throws CommandFailedException {
        if (logger.isDebugEnabled()) {
            logger.debug("execute() called...");
        }

        if (args.length > 0) {
            if (args[0].equals("-r")) {
                getGatewayInfo();
            } else {
                return getUsage(cmd);
            }
        }

        return this.toString();
    }


    /**
     * Called by the EMS to update this component's configuration.
     */
    public void updateConfiguration(Pair[] pairs, OperationType arg1) throws UnableToUpdateConfigException {
        boolean loggerEnabled = logger.isDebugEnabled();

        if (loggerEnabled) {
            logger.debug("updateConfiguration() called...");
        }

        if (pairs == null) {
            return;
        }

        try {
            for (int i = 0; i < pairs.length; i++) {
                String paramName = (String)pairs[i].getFirst();
                String paramValue = (String)pairs[i].getSecond();

                if (paramName.equals(Constants.OID_GW_HEARTBEAT_INTERVAL)) {
                    setHeartBeatInterval(paramValue, true);
                } else if (paramName.equals(Constants.OID_GW_NUM_OF_RETRIES)) {
                    setRetryCount(paramValue, true);
                } else if (paramName.equals(Constants.OID_GW_SELECTION_MODE)) {
                    setSelectionMode(paramValue, true);
                } else if (paramName.equals(Constants.OID_SIP_OUTBOUND_PROXY)) {
                    setOutboundProxy(paramValue, true);
                }
            }
        } catch (Exception e) {
            String obgwg = "Error occurred while updating component configuration: " + e.getMessage();
            logger.error(obgwg, e);
            throw new UnableToUpdateConfigException(obgwg);
        }
    }


    /**
     * Callback method used to notify this object when it's role in the
     * cluster has changed.
     */
    public void roleChanged(String clusterId,  PartitionInfo pInfo) {
    }

}

