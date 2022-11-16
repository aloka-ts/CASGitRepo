/*
 * PolicyManagerImpl.java
 *
 * Created on November 5, 2004, 10:54 AM
 */
package com.baypackets.ase.replication;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

import com.baypackets.ase.common.Registry;
import com.baypackets.ase.common.exceptions.StartupFailedException;
import com.baypackets.ase.spi.replication.ReplicationEvent;
import com.baypackets.ase.spi.util.CommandFailedException;
import com.baypackets.ase.spi.util.CommandHandler;
import com.baypackets.ase.util.Constants;
import com.baypackets.ase.util.StringManager;
import com.baypackets.ase.util.TelnetServer;
import com.baypackets.bayprocessor.agent.MComponent;
import com.baypackets.bayprocessor.agent.MComponentState;
import com.baypackets.bayprocessor.agent.OperationType;
import com.baypackets.bayprocessor.slee.common.ConfigRepository;
import com.baypackets.bayprocessor.slee.common.Pair;
import com.baypackets.bayprocessor.slee.common.UnableToChangeStateException;
import com.baypackets.bayprocessor.slee.common.UnableToUpdateConfigException;


/**
 * This class manages a repository of replication policies.
 *
 * @see com.baypackets.ase.replication.Policy
 */
public class PolicyManagerImpl implements PolicyManager, MComponent, CommandHandler {
    
    private static Logger _logger = Logger.getLogger(PolicyManagerImpl.class);
    private static StringManager _strings = StringManager.getInstance(PolicyManagerImpl.class.getPackage());
    
    private Map _appPolicyLookup = new HashMap();
    private Map _containerPolicyLookup = new HashMap();
    private short _lookupMode;
    private Collection _appNames;
    private int _replicationPerSec;
                
    
    /**
     * Adds a new replication policy to the repository.
     */
    public void addPolicy(Policy policy) {
        if (_logger.isDebugEnabled()) {
            _logger.debug("Adding the following replication policy to the PolicyManager:  " + policy.toString());
        }
        
        if (policy.getType() == Policy.CONTAINER_POLICY) {
            _containerPolicyLookup.put(policy.getReplicationEventId(), policy);
        } else if (policy.getType() == Policy.APPLICATION_POLICY) {
            Map policyMap = (Map)_appPolicyLookup.get(policy.getReplicationEventId());
            
            if (policyMap == null) {
                _appPolicyLookup.put(policy.getReplicationEventId(), policyMap = new HashMap());
            }
            
            policyMap.put(policy.getCreatorId(), policy);
        }
    }
    
    
    /**
     * Removes the specified policy from the repository.
     *
     * @param policyID  The primary key of the Policy to remove
     * @return the Policy object that was removed or null if none exists
     * for the given id
     */
    public Policy removePolicy(String policyID) {
        return null;
        // TO DO
    }
    
    
    /**
     * Removes all replication policies for the specified application.
     */
    public boolean removePoliciesForApp(String appName) {
        if (_logger.isDebugEnabled()) {
            _logger.debug("Removing replication policies for application: " + appName);
        }
        
        boolean removed = false;
        
        Iterator entries = _appPolicyLookup.entrySet().iterator();
        
        while (entries.hasNext()) {
            Map.Entry entry = (Map.Entry)entries.next();            
            Map policyMap = (Map)entry.getValue();            
            
            Policy policy = (Policy)policyMap.remove(appName);
            
            if (policy != null) {
                removed = true;
                
                if (_logger.isDebugEnabled()) {
                    _logger.debug("Successfully removed replication policy for event: " + policy.getReplicationEventId());
                }
                
                if (policyMap.isEmpty()) {
                    entries.remove();
                }                
            }            
        }
        
        return removed;
    }
    
    
    /**
     * Returns a value indicating the type of replication to be 
     * performed (if any) for the given event.  The possible return values 
     * are enumerated as public static constants of the Policy class.
     */
    public short query(ReplicationEvent event) {
        if (_logger.isDebugEnabled()) {
            _logger.debug("query() called...");
        }
        
        if (_lookupMode == PolicyManager.NEVER_REPLICATE) {
            if (_logger.isDebugEnabled()) {
                _logger.debug("query(): Policy lookup mode is NEVER_REPLICATE.  Returning NO_REPLICATION action indicator.");
            }
            return Policy.NO_REPLICATION;
        }
        if (_lookupMode == PolicyManager.ALWAYS_REPLICATE) {
            if (_logger.isDebugEnabled()) {
                _logger.debug("query(): Policy lookup mode is ALWAYS_REPLICATE.  Returning REPLICATE action indicator.");
            }
            return Policy.REPLICATE;
        }
        
        short replicationType = Policy.NO_REPLICATION;        
        
        if (_logger.isDebugEnabled()) {
            _logger.debug("query(): Looking up container policy for event: " + event.getEventId());
        }
	
        
        Policy policy = (Policy)_containerPolicyLookup.get(event.getEventId());
        
        if (policy != null) {
            if (_logger.isDebugEnabled()) {
                _logger.debug("query(): Got container policy for event: " + event.getEventId());
            }
            replicationType = policy.getReplicationType();
        } else if (_logger.isDebugEnabled()) {
            _logger.debug("query(): No container policy found for event: " + event.getEventId());
        }

        Collection appNames = event.getAppNames();        
        
        if (appNames == null || appNames.isEmpty()) {
            if (_logger.isDebugEnabled()) {
                _logger.debug("query(): No app names specified in given event.  Returning replication action type: " + replicationType);
            }
            return replicationType;
        }        
                
        if (_lookupMode == PolicyManager.CONTAINER_ONLY) {
            if (_logger.isDebugEnabled()) {
                _logger.debug("query(): Policy lookup mode is CONTAINER_ONLY.");
            }
            
            if (_appNames == null || _appNames.isEmpty()) {
                if (_logger.isDebugEnabled()) {
                    _logger.debug("query(): Returning replication action type: " + replicationType);
                }
                return replicationType;
            }
            
            appNames = new ArrayList(appNames);
            
            Iterator iterator = appNames.iterator();
            while (iterator.hasNext()) {
                if (!_appNames.contains(iterator.next())) {
                    iterator.remove();
                }
            }
            
            if (appNames.isEmpty()) {
                if (_logger.isDebugEnabled()) {
                    _logger.debug("query(): Returning replication action type: " + replicationType);
                }                
                return replicationType;
            }
        }
                    
        if (_appPolicyLookup != null) {
            Map policyMap = (Map)_appPolicyLookup.get(event.getEventId());
            
            if (policyMap != null && !policyMap.isEmpty()) {                
                Iterator iterator = appNames.iterator();
             
                while (iterator.hasNext()) {
                    String appName = (String)iterator.next();
                    
                    policy = (Policy)policyMap.get(appName);

                    if (policy == null) {
                        if (_logger.isDebugEnabled()) {
                            _logger.debug("No replication policy for event " + event.getEventId() + " found for application " + appName);
                        }
                        continue;
                    }
                    
                    if (_logger.isDebugEnabled()) {
                        _logger.debug("Found replication policy for event " + event.getEventId() + " for application " + appName);
                    }
                    
                    replicationType = policy.getReplicationType();                    
                    
                    if (replicationType == Policy.FULL_REPLICATION ||
                        replicationType == Policy.REPLICATE) {
                        break;
                    }                    
                }
            }       
        }

        if (_logger.isDebugEnabled()) {
            _logger.debug("query(): Returning replication action type: " + replicationType);
        }
        
        return replicationType;        
    }
 
    
    /**
     * Sets the policy lookup mode.  This has an effect on what the return 
     * value of the "query()" method will be.  The range of possible values
     * for the given "mode" parameter are enumerated by the PolicyManager's 
     * public static constants.
     */
    public void setLookupMode(short mode) {
        _lookupMode = mode;
    }
    
    
    /**
     * Returns the replication policy lookup mode.
     */
    public short getLookupMode() {
        return _lookupMode;
    }
    
    
    /**
     * Specifies the set of applications whose replication policies will be 
     * considered when the "query" method is invoked and the policy lookup
     * mode is set to CONTAINER_ONLY.
     */
    public void setAppNames(Collection appNames) {
        _appNames = appNames;
    }

    
    /**
     * Returns the names of the applications whose replication policies will
     * be considered.
     */
    public Collection getAppNames() {
        return _appNames;
    }
    
    
    /**
     * Returns the rate at which an active peer's data should be replicated 
     * to a peer that has just come up.
     */
    public int getReplicationPerSec() {
        return _replicationPerSec;
    }
    
    
    /**
     *
     */
    public void start() throws StartupFailedException {
        if (_logger.isDebugEnabled()) {
            _logger.debug("Starting PolicyManagerImpl class...");
        }
        
        try {
            init();
            loadPolicies();
        } catch (Exception e) {
            if (e instanceof StartupFailedException) {
                throw (StartupFailedException)e;
            }
            _logger.error(e.toString(), e);
            throw new StartupFailedException(e.toString());
        }
    }
    
    
    /**
     * Initializes this component's state from the EMS console.
     */
    public void init() throws Exception {
        if (_logger.isDebugEnabled()) {
            _logger.debug("init(): Registering ourselves with the TelnetServer...");
        }
        
        // Register commands with the TelnetServer class.
        TelnetServer server = (TelnetServer)Registry.lookup(Constants.NAME_TELNET_SERVER);
        server.registerHandler("reload-policies", this);
        server.registerHandler("set-policy-mode", this);
        server.registerHandler("dump-policies", this);
        
        if (_logger.isDebugEnabled()) {
            _logger.debug("init(): Getting initialization parameters from EMS...");
        }
        
        ConfigRepository repository = (ConfigRepository)Registry.lookup(Constants.NAME_CONFIG_REPOSITORY);
        String lookupMode = repository.getValue(Constants.OID_POLICY_LOOKUP_MODE);
        
        if (lookupMode != null) {
            if (_logger.isDebugEnabled()) {
                _logger.debug("init(): Setting policy lookup mode to: " + lookupMode);
            }
            _lookupMode = Short.parseShort(lookupMode);
        } else if (_logger.isDebugEnabled()) {
            _logger.debug("init(): Policy lookup mode is null.");
        }
    }
    
    
    /**
     * Loads the container's replication policies from an XML config file.
     */
    private void loadPolicies() throws Exception {
		String filepath = Constants.ASE_HOME + "/" + Constants.FILE_REPLICATION_CONFIG;
        if (_logger.isDebugEnabled()) {
            _logger.debug("Reading default container policies from file: " + filepath);
        }

        File configFile = new File(filepath);
        if (!configFile.exists()) {
            throw new StartupFailedException("Unable to load replication configuration file: " +filepath);
        }        
        
        _containerPolicyLookup.clear();
        
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();            
        Document doc = builder.parse(configFile);
            
        Collection policies = this.parseForPolicies((Element)doc.getElementsByTagName("replication-config").item(0), null);
               
        if (policies != null) {
            Iterator iterator = policies.iterator();
            while (iterator.hasNext()) {
                this.addPolicy((Policy)iterator.next());
            }
        }
        
        if (_logger.isDebugEnabled()) {
            _logger.debug(dumpPolicies());
        }
    }
    
    
    /**
     * Parses the given DOM object for replication policies.
     *
     * @param appName  The name of the application for which the parsed
     * policies belong.  If this is null, the policies are considered to
     * be the container's own policies.
     * @return  A Collection of Policy objects.
     * @see com.baypackets.ase.replication.Policy
     */
    public Collection parseForPolicies(Element elem, String appName) {
        if (_logger.isDebugEnabled()) {
            _logger.debug("parseForPolicies() called...");
        }
        
        short policyType = Policy.CONTAINER_POLICY;
        
        if (appName != null) {
            policyType = Policy.APPLICATION_POLICY;
        }
        
        NodeList nodes = elem.getElementsByTagName("replication-per-second");
        if (nodes != null && nodes.getLength() > 0) {
            _replicationPerSec = Integer.parseInt(getBodyContent((Element)nodes.item(0)));
        }
                        
        nodes = elem.getElementsByTagName("replication-policy");
              
        Collection policies = new ArrayList(nodes.getLength());
        
        for (int i = 0; i < nodes.getLength(); i++) {
            Element elem2 = (Element)nodes.item(i);
                                           
            Policy policy = new Policy();
            policy.setReplicationEventId(getBodyContent((Element)elem2.getElementsByTagName("event").item(0)));
            policy.setReplicationType(getReplicationType(getBodyContent((Element)elem2.getElementsByTagName("replication-type").item(0))));
            policy.setType(policyType);
            policy.setCreatorId(appName);
            policies.add(policy);
        }                        
        
        return policies;
    }
    
    
    /**
     * Prints a dump of the replication policies contained by this object.
     */
    private String dumpPolicies() {
        Collection policies = _containerPolicyLookup.values();
        
        StringBuffer buffer = new StringBuffer();
        
        // First print the container's policies...
        if (policies == null || policies.isEmpty()) {
            buffer.append("Policy manager has no replication policies.\r\n");
        } else {
            buffer.append("Dump of container's replication policies...\r\n");
            
            Iterator iterator = policies.iterator();
            
            while (iterator.hasNext()) {
                buffer.append("Policy:: " + iterator.next().toString() + "\r\n");
            }
        }
        
        // Now handle the applications' policies...
        policies = _appPolicyLookup.values();
        
        if (policies != null && !policies.isEmpty()) {                
            // Sort the application policies by app name.
            Map policyMap = new HashMap();
                
            Iterator iterator = policies.iterator();
                
            while (iterator.hasNext()) {
                Map map = (Map)iterator.next();
                    
                if (map != null && !map.isEmpty()) {
                    Iterator appNames = map.keySet().iterator();
                    
                    while (appNames.hasNext()) {
                        String appName = (String)appNames.next();
                        policies = (Collection)policyMap.get(appName);
                        
                        if (policies == null) {
                            policyMap.put(appName, policies = new ArrayList());
                        }
                        policies.add(map.get(appName));
                    }                    
                }
            }
            
            // Now iterate over the sorted policies and print them...
            Iterator appNames = policyMap.keySet().iterator();
            
            while (appNames.hasNext()) {
                String appName = (String)appNames.next();                
                buffer.append("\r\nReplication policies for application \"" + appName + "\"\r\n");
                
                iterator = ((Collection)policyMap.get(appName)).iterator();
                
                while (iterator.hasNext()) {
                    buffer.append("Policy:: ");
                    buffer.append(iterator.next().toString());
                    buffer.append("\r\n");
                }
            }
        }
                        
        return buffer.toString();
    }
    
    
    /**
     * Invoked by EMS to update the state of this managed component.
     */
    public void changeState(MComponentState state) throws UnableToChangeStateException {
        if (_logger.isDebugEnabled()) {
            _logger.debug("changeState() called...");
        }
        
        try {
            if (state.getValue() == MComponentState.RUNNING) {
                this.start();
            }
        } catch (Exception e) {
            _logger.error(e.toString(), e);
            throw new UnableToChangeStateException(e.toString());
        }
    }
        
    
    /**
     * Invoked by EMS to update this component's configuration.
     */
    public void updateConfiguration(Pair[] pairs, OperationType operationType) throws UnableToUpdateConfigException {
        if (_logger.isDebugEnabled()) {
            _logger.debug("updateConfiguration() called...");
        }
        
        try {
            if (pairs == null) {
                return;
            }
            
            for (int i = 0; i < pairs.length; i++) {
                Object paramName = pairs[i].getFirst();
                Object paramValue = pairs[i].getSecond();
                
                if (Constants.OID_POLICY_LOOKUP_MODE.equals(paramName)) {
                    if (paramValue != null) {
                        _lookupMode = Short.parseShort(paramValue.toString());
                    } 
                } else if (Constants.OID_RELOAD_POLICIES.equals(paramName)) {
                    loadPolicies();
                }
            }
        } catch (Exception e) {
            _logger.error(e.toString(), e);
            throw new UnableToUpdateConfigException(e.toString());
        }
    }
    
    
    /**
     * Returns the short value corresponding to the given replication type.
     */
    public short getReplicationType(String replicationType) {
        if ("NO_REPLICATION".equals(replicationType)) {
            return Policy.NO_REPLICATION;
        }
        if ("REPLICATE".equals(replicationType)) {
            return Policy.REPLICATE;
        }
        if ("FULL_REPLICATION".equals(replicationType)) {
            return Policy.FULL_REPLICATION;
        }
        throw new RuntimeException("Invalid replication type: " + replicationType); 
    }
    
    
    /**
     * Returns the body content of the given DOM Element as a String.
     */
    private String getBodyContent(Element elem) {
        NodeList nodes = elem.getChildNodes();
        for (int i = 0; i < nodes.getLength(); i++) {
            if (nodes.item(i) instanceof Text) {
                return nodes.item(i).getNodeValue().trim();
            }
        }
        return null;
    }
    
    
    /*************** Methods implemented from CommandHandler ***************/
    
    /**
     * Invoked by the TelnetServer class to handle the given command.
     */
    public String execute(String command, String[] args, java.io.InputStream in, java.io.OutputStream out) throws CommandFailedException {
        if ("reload-policies".equals(command)) {
            try {
                loadPolicies();
            } catch (Exception e) {
                _logger.error(e.toString(), e);
                return _strings.getString("PolicyManagerImpl.error", e.toString());
            }
        } else if ("dump-policies".equals(command)) {
            return dumpPolicies();
        } else if ("set-policy-mode".equals(command)) {
            if (args == null || args.length == 0) {
                return _strings.getString("PolicyManagerImpl.setPolicyModeUsage");
            }
            if (args[0].equals("NORMAL")) {
                _lookupMode = PolicyManager.NORMAL;
            } else if (args[0].equals("CONTAINER_ONLY")) {
                _lookupMode = PolicyManager.CONTAINER_ONLY;
            } else if (args[0].equals("NEVER_REPLICATE")) {
                _lookupMode = PolicyManager.NEVER_REPLICATE;
            } else if (args[0].equals("ALWAYS_REPLICATE")) {
                _lookupMode = PolicyManager.ALWAYS_REPLICATE;
            } else {
                return _strings.getString("PolicyManagerImpl.setPolicyModeUsage");
            }
        }
        return "";
    }
    
    
    /**
     * Returns a usage statement for the specified telnet command.
     */
    public String getUsage(String command) {
        if ("reload-policies".equals(command)) {
            return _strings.getString("PolicyManagerImpl.reloadPoliciesUsage");
        } else if ("set-policy-mode".equals(command)) {
            return _strings.getString("PolicyManagerImpl.setPolicyModeUsage");
        } else if ("dump-policies".equals(command)) {
            return _strings.getString("PolicyManagerImpl.dumpPoliciesUsage");
        }
        return "";
    }
        
}
