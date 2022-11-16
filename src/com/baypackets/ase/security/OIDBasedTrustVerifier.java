/*
 * Created on Mar 31, 2005
 *
 */
package com.baypackets.ase.security;

import java.util.ArrayList;
import java.util.StringTokenizer;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import com.baypackets.ase.common.Registry;
import com.baypackets.ase.util.Constants;
import com.baypackets.bayprocessor.agent.MComponent;
import com.baypackets.bayprocessor.agent.MComponentState;
import com.baypackets.bayprocessor.agent.OperationType;
import com.baypackets.bayprocessor.slee.common.ConfigRepository;
import com.baypackets.bayprocessor.slee.common.Pair;
import com.baypackets.bayprocessor.slee.common.UnableToChangeStateException;
import com.baypackets.bayprocessor.slee.common.UnableToUpdateConfigException;

/**
 * @author Ravi
 */
public class OIDBasedTrustVerifier implements TrustVerifier, MComponent {
	
	private static Logger logger = Logger.getLogger(OIDBasedTrustVerifier.class);
	private static String DELIM = ",";

	ArrayList nodes = new ArrayList();
	
	public OIDBasedTrustVerifier(){
	}

	/* (non-Javadoc)
	 * @see com.baypackets.ase.security.TrustVerifier#isTrusted(java.lang.String)
	 */
	public boolean isTrusted(String nodeName) {
		nodeName = (nodeName == null) ? "" : nodeName;
		return this.nodes.contains(nodeName);
	}
	
	/**
	  * Initializes this object's state using the parameters from the
	  * ConfigRepository class.
	  */
	 public void initialize(){
		if(logger.isInfoEnabled()){
			logger.info("Going to load the trusted nodes information from the OID :" + Constants.PROP_SIP_TRUSTED_NODES);
		}

		ConfigRepository configRep = null;
		configRep = (ConfigRepository)Registry.lookup(Constants.NAME_CONFIG_REPOSITORY);
    	
		String strNodes = configRep.getValue(Constants.PROP_SIP_TRUSTED_NODES);
		strNodes = (strNodes == null) ? "" : strNodes;

	 	this.loadTrustedNodes(strNodes);
	 }
	 
	 protected void loadTrustedNodes(String strNodes){
		if(logger.isInfoEnabled()){
			logger.info("Load trusted Nodes methods called with :" + strNodes);
		}
	 	
		this.nodes.clear();
		StringTokenizer tokenizer = new StringTokenizer(strNodes, DELIM);
		for(;tokenizer.hasMoreTokens();){
			String node = tokenizer.nextToken();
			this.nodes.add(node);
		}
		if(logger.isInfoEnabled()){
			logger.info("The trusted nodes are :" + this.nodes);
		}
	 }
	 
    /**
     * start the verifier
     * @throws Exception
     */
	public void start() throws Exception {
	}

	 /**
	  * Shuts down the verifier 
	  */
	 public void shutdown() throws Exception {
	 }

	/* (non-Javadoc)
	 * @see com.baypackets.bayprocessor.agent.MComponent#changeState(com.baypackets.bayprocessor.agent.MComponentState)
	 */
	public void changeState(MComponentState componentState)
		throws UnableToChangeStateException {
		try {
			 if(logger.isEnabledFor(Level.INFO)){
				 logger.info("Change state called on OID based trust verifier :::" + componentState.getValue());
			 }
			 if(componentState.getValue() == MComponentState.LOADED){
				 this.initialize();
			 } else if(componentState.getValue() == MComponentState.RUNNING){
				 this.start();
			 } else if(componentState.getValue() == MComponentState.STOPPED){
		 		this.shutdown();
			 }
		 } catch(Exception e){
			 throw new UnableToChangeStateException(e.getMessage());
		 }
	}

	/* (non-Javadoc)
	 * @see com.baypackets.bayprocessor.agent.MComponent#updateConfiguration(com.baypackets.bayprocessor.slee.common.Pair[], com.baypackets.bayprocessor.agent.OperationType)
	 */
	public void updateConfiguration(Pair[] configData, OperationType opType)
		throws UnableToUpdateConfigException {

			try {                    
			  if (OperationType.MODIFY != opType.getValue()) {
				  return;
			  }
    
			  // iterate through the given array of config parameters...
			  for (int i = 0; i < configData.length; i++) {
				  // extract the parameter name and value
				  String tmpParamName = (String)configData[i].getFirst();
				  String tmpParamValue = (String)(configData[i].getSecond());
				  tmpParamValue = (tmpParamValue == null) ? "" : tmpParamValue.trim();
		
				  // determine what action to take based on the parameter
				  if (tmpParamName.equals(Constants.PROP_SIP_TRUSTED_NODES)) {
				  	this.loadTrustedNodes(tmpParamValue);
				  }
			  }

		  } catch (Exception e) {
			  logger.error(e.toString(), e);
			  throw new UnableToUpdateConfigException(e.getMessage());
		  }
	}
}
