/*
 * CDRContextWrapper.java
 * @author Amit Baxi
 */
package com.baypackets.ase.cdr;

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
 * This class is a wrapper class for CDRContext objects.It will create objects of 
 * CDRContextImpl based on value of property max cdr writers in ase.properties 
 */
public class CDRContextWrapper implements MComponent{

	private static Logger logger = Logger.getLogger(CDRContextWrapper.class);
	private CDRContextImpl [] _cdrContexts;
	private int maxCDRWriters =1;
	private boolean initialized=false;
	/**
	 * This method returns CDRContext object at index i in _cdrContexts array.
	 * @param i
	 * @return
	 */
	public CDRContext getCDRContext(int i){
		CDRContext context=null;
		if(_cdrContexts!=null)
			if(i<_cdrContexts.length)
				context=this._cdrContexts[i];
		if(logger.isDebugEnabled())
			logger.debug("Returning context:"+context);
		return context;
	}
	
	/**
	 * This method initializes this object's state using the configuration
	 * parameters specified in the ConfigRepository singleton.
	 *
	 * @see com.baypackets.bayprocessor.slee.common.ConfigRepository
	 */
	public synchronized void initialize() throws InitializationFailedException {            
            if (logger.isDebugEnabled()) {
                logger.debug("initialize(): Initializing component state from the ConfigRepository...");
            }
                        
            ConfigRepository config = (ConfigRepository)Registry.lookup(Constants.NAME_CONFIG_REPOSITORY); 
			try{
				this.setMaxCDRWriters((int)Integer.parseInt(config.getValue(Constants.MAX_CDR_WRITERS)));
			}catch (Exception e) {
				logger.error("Incorrect value for property "+Constants.MAX_CDR_WRITERS +" Initializing with 1.");
			}
			this._cdrContexts=new CDRContextImpl[this.maxCDRWriters];
			for(int i=0;i<this.maxCDRWriters;i++){
				_cdrContexts[i]=new CDRContextImpl(i);
			}
			initialized=true;
	}  
	
	
	
	@Override
	public void changeState(MComponentState state)
	throws UnableToChangeStateException {
		if (state.getValue() == MComponentState.LOADED && ! this.initialized) {
			try {
				this.initialize();
			} catch (InitializationFailedException e) {
				String msg = "Error occurred while invoking changeState() on CDRContextWrapperObject"+this;
				logger.error(msg, e);
				throw new UnableToChangeStateException(msg);
			}
		}
		if(this._cdrContexts!=null){
			for(int i=0;i<_cdrContexts.length;i++){
				_cdrContexts[i].changeState(state);
			}
		}
	}

	@Override
	public void updateConfiguration(Pair[] arg0, OperationType arg1)
			throws UnableToUpdateConfigException {
		if(this._cdrContexts!=null){
			for(int i=0;i<_cdrContexts.length;i++){
				_cdrContexts[i].updateConfiguration(arg0,arg1);
			}}
	}
	/**
	 * @param maxCDRWriters the maxCDRWriters to set
	 */
	public void setMaxCDRWriters(int maxCDRWriters) {
		this.maxCDRWriters = maxCDRWriters;
	}
	/**
	 * @return the maxCDRWriters
	 */
	public int getMaxCDRWriters() {
		return maxCDRWriters;
	}
	/**
	 * @param _cdrContexts the _cdrContexts to set
	 */
	public void setCDRContexts(CDRContextImpl [] _cdrContexts) {
		this._cdrContexts = _cdrContexts;
	}
	/**
	 * @return the _cdrContexts
	 */
	public CDRContextImpl [] getCDRContexts() {
		return _cdrContexts;
	}
	
	
}