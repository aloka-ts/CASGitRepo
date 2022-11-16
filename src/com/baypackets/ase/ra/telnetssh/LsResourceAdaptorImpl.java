/**
 * 
 */
package com.baypackets.ase.ra.telnetssh;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import oracle.jdbc.dcn.RowChangeDescription.RowOperation;

import org.apache.log4j.Logger;

import com.baypackets.ase.container.exceptions.AseInvocationFailedException;
import com.baypackets.ase.ra.telnetssh.configmanager.CommonLsConfig;
import com.baypackets.ase.ra.telnetssh.configmanager.LS;
import com.baypackets.ase.ra.telnetssh.configmanager.LsConfigChangeData;
import com.baypackets.ase.ra.telnetssh.configmanager.db.LsRaDao;
import com.baypackets.ase.ra.telnetssh.configmanager.db.LsRaDaoImpl;
import com.baypackets.ase.ra.telnetssh.event.LsResourceEvent;
import com.baypackets.ase.ra.telnetssh.exception.LsResourceException;
import com.baypackets.ase.ra.telnetssh.ls.LsManager;
import com.baypackets.ase.ra.telnetssh.message.LsMessage;
import com.baypackets.ase.ra.telnetssh.message.LsRequest;
import com.baypackets.ase.ra.telnetssh.message.LsRequestImpl;
import com.baypackets.ase.ra.telnetssh.message.LsResponse;
import com.baypackets.ase.ra.telnetssh.qm.QueueManager;
import com.baypackets.ase.ra.telnetssh.qm.QueueManagerImpl;
import com.baypackets.ase.ra.telnetssh.session.LsResourceSession;
import com.baypackets.ase.ra.telnetssh.utils.Constants;
import com.baypackets.ase.ra.telnetssh.utils.RaProperties;
import com.baypackets.ase.resource.ResourceException;
import com.baypackets.ase.spi.container.SasMessage;
import com.baypackets.ase.spi.container.SasProtocolSession;
import com.baypackets.ase.spi.deployer.DeployableObject;
import com.baypackets.ase.spi.replication.ReplicationEvent;
import com.baypackets.ase.spi.resource.ResourceContext;
import com.baypackets.ase.util.AseStrings;

/**
 * The Class LsResourceAdaptorImpl.
 * Implementation class for LsResourceAdaptor Interface
 * Will act as controlling unit for RA
 * Methods of this class wll be called to start or stop 
 * the RA or interact with sub modules in RA
 * 
 *
 * @author saneja
 */
public class LsResourceAdaptorImpl implements LsResourceAdaptor {

	/** The Constant logger. */
	private static final Logger logger = Logger.getLogger(LsResourceAdaptorImpl.class);

	/** The Resourcecontext. */
	private ResourceContext context;

	/** The refernce to LsManager submodule. */
	private LsManager lsManager;

	/** The refernce to QueueManager. module */
	private QueueManager queueManager;

	/** The LsRaDao. */
	private LsRaDao lsRaDao;

	/** The ra properties. */
	private RaProperties raProperties;

	/** Map conatining mapping of LS corresponding to row id in DB. */
	private Map<String,LS> lsMap;

	/** The common ls configuration refence */
	private CommonLsConfig commonLsConfig;

	/** The role for RA/SAS active standby. */
	private short role = LsResourceAdaptor.ROLE_ACTIVE;

	/** The status of RA, IS RA up or down. */
	private boolean raUp = false;

	/** The can send message. */
	private boolean canSendMessage = false;

	/** The app list. MAintains list of deployed apps using the RA*/
	private ArrayList<DeployableObject> appList = new ArrayList<DeployableObject>();

	/** The ls resource adaptor. Self refgence to be used by sub modules to 
	 * acces LSResource Adaptor 
	 */
	private static LsResourceAdaptor lsResourceAdaptor;

	/** Flag to mark true when ra is active on machine */
	private boolean raActive=false;

	/**
	 * Instantiates a new ls resource adaptor impl.
	 */
	public LsResourceAdaptorImpl(){
		boolean isDebugEnabled=logger.isDebugEnabled();
		if(isDebugEnabled)
			logger.debug("creating lsAdaptor object");
		lsResourceAdaptor=this;
	}

	/**
	 * This  method returns the instance of LsResourceAdaptor.
	 *
	 * @return LsResourceAdaptor object.
	 * @throws Exception the exception
	 */
	public static LsResourceAdaptor getInstance() throws Exception{
		if(lsResourceAdaptor==null){
			logger.error("ERROR::::RA Object is null");
			throw new LsResourceException("ResourceAdaptorImpl Instance is null.");
		}
		return lsResourceAdaptor;
	}

	/** 
	 * init method is called whn RA is deployed.
	 * Flow:
	 * Sets context 
	 * Reads RA properties
	 * Initializes LsResourceFactory
	 * gets refences to sub modules -dao, quemanager and LsMAnager
	 * Sets required attributes of sub modules 
	 * 
	 * (non-Javadoc)
	 * @see com.baypackets.ase.spi.resource.ResourceAdaptor#init(com.baypackets.ase.spi.resource.ResourceContext)
	 */
	@Override
	public void init(ResourceContext context) throws ResourceException {
		boolean isDebugEnabled=logger.isDebugEnabled();
		boolean isInfoEnabled=logger.isInfoEnabled();
		if(isInfoEnabled)
			logger.info("Inside LsResourceAdaptor init() with context "+context);
		this.context = context;		
		//XXX Alternate approach
		//		String[] paths={"/telnetsshra.xml"};
		//		ApplicationContext appContext=new ClassPathXmlApplicationContext(paths,this.getClass());
		//		LsResourceAdaptorImpl.raProperties=((RaProperties) appContext.getBean("lsRaProperties"));

		try {
			Properties prop = new Properties();
			String fileName = System.getProperty("ase.home")+"/conf/telnetsshra.properties";
			InputStream is = new FileInputStream(fileName);
			prop.load(is);
			//reading properties
			int loadFactor=Integer.parseInt(prop.getProperty("telnetssh.dequethreadloadfactor"));
			//saneja @bug 10179[ vaues changed to dynamic
			
			//saneja@ bug 7085 reading configurables[
//			boolean commandLogEnabled=Boolean.parseBoolean(prop.getProperty("telnetssh.command.logging.enabled"));
//			String outputDelim=prop.getProperty("telnetssh.output.delim");
//			String suppressCommand=prop.getProperty("telnetssh.suppress.command");
//			int delimRespTimer=Integer.parseInt(prop.getProperty("telnetssh.output.delim.timer"));
//			String telnetPrompt=prop.getProperty("telnetssh.telnet.prompt");
//			String responseSep=prop.getProperty("telnetssh.output.seperator");
//			int connectTimeout=Integer.parseInt(prop.getProperty("telnetssh.connect.timeout"));
//			if(outputDelim==null || suppressCommand==null || telnetPrompt==null || 
//					delimRespTimer<=0 || loadFactor <=0 || connectTimeout<=0){
//				throw new LsResourceException("InitialIzation Failed-->INVALID values for properties");
//			}
			//]closed saneja@bug 7085
			String telnetPrompt=prop.getProperty("telnetssh.telnet.prompt");
			int connectTimeout=Integer.parseInt(prop.getProperty("telnetssh.connect.timeout"));
			int keepAliveTimer=Integer.parseInt(prop.getProperty("keep.alive.timer"));
			String keepAliveCommand = prop.getProperty("keep.alive.command");
			int keepAliveFailedAttempts=Integer.parseInt(prop.getProperty("keep.alive.failed.attempts"));
			
			
			//CR UAT-1219 Changes
			long waitToSuppressCommand = Long.parseLong(prop.getProperty("telnetssh.login.wait"));
			int suppressCommandTimeout = Integer.parseInt(prop.getProperty("suppress.command.response.timeout"));
			String suppressCommandDelim = prop.getProperty("suppress.command.response.delim");
			int lsLoginAttempts = Integer.parseInt(prop.getProperty("ls.max.login.attempts"));
			int longRecoveryPeriod = Integer.parseInt(prop.getProperty("telnetssh.telnet.long.recovery.period"));
			int lsQueueLoggingPeriod=Integer.parseInt(prop.getProperty("telnetssh.ls.quque.loging.period"));
			boolean isLocalEnvironment=Boolean.valueOf(prop.getProperty("telnetssh.local.environment"));
			if(telnetPrompt==null ||loadFactor <=0 || connectTimeout<=0){
				throw new LsResourceException("InitialIzation Failed-->INVALID values for properties");
			}
			
			//]closed saneja @bug 10179
			
			if(isInfoEnabled)
				logger.info("Reading properties Load Factor::"+loadFactor);
			raProperties=new RaProperties();
			raProperties.setDeQueueThreadLoadFactor(loadFactor);
			//saneja @bug 10179 [ values changed to dynamic
			
			//saneja@ bug 7085 setting configurables in props bean[
//			raProperties.setCommandLogEnabled(commandLogEnabled);
//			raProperties.setDelimRespTimer(delimRespTimer);
//			raProperties.setOutputDelim(outputDelim);
//			raProperties.setSuppressCommand(suppressCommand);
//			raProperties.setTelnetPrompt(telnetPrompt);
//			raProperties.setRespSeperator(responseSep);
//			raProperties.setConnectTimeout(connectTimeout);
			//]closed saneja@bug 7085
			raProperties.setTelnetPrompt(telnetPrompt);
			raProperties.setConnectTimeout(connectTimeout);
			//]closed saneja @bug 10179
			raProperties.setKeepAliveCommand(keepAliveCommand);
			raProperties.setKeepAliveFailedAttempts(keepAliveFailedAttempts);
			raProperties.setKeepAliveTimer(keepAliveTimer);
			
			//CR UAT-1219 Changes
			raProperties.setWaitToSuppressCommand(waitToSuppressCommand);
			raProperties.setSuppressCommandTimeout(suppressCommandTimeout);
			raProperties.setSuppressCommandDelim(suppressCommandDelim);
			raProperties.setLsMaxLoginAttempts(lsLoginAttempts);
			raProperties.setLongRecoveryPeriod(longRecoveryPeriod);
			raProperties.setLsQueueLoggingPeriod(lsQueueLoggingPeriod);
			raProperties.setLocalEnvironment(isLocalEnvironment);
			is.close();

		} catch (FileNotFoundException e) {
			throw new LsResourceException("InitialIzation Failed-->Property File not Found",e);
		} catch (IOException e) {
			throw new LsResourceException("InitialIzation Failed-->Property File IO failed",e);
		}

		if(context==null){
			logger.error("InitialIzation Failed-RA created with null resource context");
			throw new LsResourceException("InitialIzation Failed-->Resource Context is null");
		}
		if(isDebugEnabled)
			logger.debug("init  class loader is "+this.getClass().getClassLoader());
		//Initializes the resource factory	
		((LsResourceFactoryImpl)context.getResourceFactory()).init(context);
		// Gets the role for current SAS standby/active
		this.role = context.getCurrentRole();
		if(isDebugEnabled)
			logger.debug("The system is " + (this.role == LsResourceAdaptor.ROLE_ACTIVE ? "active" : "standby"));
		// get references to sub modules
		lsRaDao =LsRaDaoImpl.getInstance(); 
		queueManager =QueueManagerImpl.getInstance();
		lsManager=LsManager.getInstance();
		//saneja@ bug 7085 setting raproperties in LsManager[
		lsManager.setRaProperties(raProperties);
		//]closed saneja@bug 7085
		if(isInfoEnabled)
			logger.info("Leaving LsResourceAdaptor init()");
	}

	/**
	 * on ra startup this method is called.
	 * load dao raed LS and ad common configuratoion from db.
	 * register with Database for change notification
	 * starts quemanager
	 * starts lsmanager
	 * Raises RA_UP event
	 * 
	 *  (non-Javadoc)
	 * @see com.baypackets.ase.spi.resource.ResourceAdaptor#start()
	 */
	@Override
	public void start() throws ResourceException {
		boolean isDebugEnabled=logger.isDebugEnabled();
		boolean isInfoEnabled=logger.isInfoEnabled();
		if(isInfoEnabled)
			logger.info("Inside LsResourceAdaptor start()");
		raActive=true;
		if (this.role != LsResourceAdaptor.ROLE_ACTIVE) {
			if(isInfoEnabled)
				logger.info("Standby...");
			return;
		}
		try{
			lsRaDao.load(this);
			lsMap =lsRaDao.getAllLs();
			commonLsConfig=lsRaDao.getCommonLsConfig();
			
			if(commonLsConfig==null){
				logger.error("Unable to start RA, Common Configurations not defined in DB");
				throw new LsResourceException("RA Start Failed: Common LS Configuration not found");
			}
			
			//saneja @bug 10179 [ updating RA properties with values for variable which are run time configurable
			boolean commandLogEnabled=commonLsConfig.isCommandLogEnabled();
			String suppressCommand=commonLsConfig.getSuppressCommand();
			String outputDelim=commonLsConfig.getOutputDelim();
			int delimRespTimer=commonLsConfig.getDelimRespTimer();
			String responseSep=commonLsConfig.getRespSeperator();
			if(outputDelim==null || suppressCommand==null || delimRespTimer<=0 || responseSep ==null){
				throw new LsResourceException("InitialIzation Failed-->INVALID values dynamically updatble properties");
			}
			//setting dynamically updatable values in ra properties
			//because ra properties object is always referred to read these values
			// and no new framework is required
			raProperties.setCommandLogEnabled(commandLogEnabled);
			raProperties.setSuppressCommand(suppressCommand);
			raProperties.setOutputDelim(outputDelim);
			raProperties.setDelimRespTimer(delimRespTimer);
			raProperties.setRespSeperator(responseSep);
			//]closed saneja @bug 10179
			
			if(isDebugEnabled)
				logger.debug("RA DAO initialized, DB configuration read");

			Collection<LS> lsCollection= lsMap.values();

			queueManager.load(lsCollection, raProperties.getDeQueueThreadLoadFactor(),raProperties.getLsQueueLoggingPeriod(), this);
			if(isDebugEnabled)
				logger.debug("Queue Manager initialized, Queue Created");
			lsManager.start(lsCollection, commonLsConfig,this);
			this.raUp=true;
			this.canSendMessage=true;
			//send RA up event to app
			if(isDebugEnabled)
				logger.debug("sending RA up event");
			LsResourceEvent resourceEvent = new LsResourceEvent("RA_UP_EVENT", 
					LsResourceEvent.RA_UP, null);
			try {
				deliverEvent(resourceEvent);
			} catch (ResourceException e) {
				logger.error("Exception in delivering LsResourceEvent",e);
				throw new LsResourceException(e);
			}
			if(isDebugEnabled)
				logger.debug("RA UP event sent");
			//Notifying waiting threads...
			synchronized (this) {
				notifyAll();				
			}
		}catch(Exception e){
			logger.error("Exception while starting RA", e);
			throw(new LsResourceException(e));
		}

		if(isInfoEnabled)
			logger.info("Leaving LsResourceAdaptor start():: RA succesfully started");
	}

	/**
	 * called when RA is stopped.
	 * Raises RA_DOWN event
	 * destroys DAo, Quemanager and LSmanagers
	 *  
	 * (non-Javadoc)
	 * @see com.baypackets.ase.spi.resource.ResourceAdaptor#stop()
	 */
	@Override
	public void stop() throws ResourceException {
		boolean isDebugEnabled=logger.isDebugEnabled();
		boolean isInfoEnabled=logger.isInfoEnabled();
		if(isInfoEnabled)
			logger.info("Inside LsResourceAdaptor stop()");
		raActive=false;
		try {
			if(this.raUp == true ) {
				if(isDebugEnabled)
					logger.debug(" stoping RA " );
				//send RA- down event to app
				if(isDebugEnabled)
					logger.debug("sending RA down event");
				LsResourceEvent resourceEvent = new LsResourceEvent("RA_DOWN_EVENT", 
						LsResourceEvent.RA_DOWN, null);
				try {
					deliverEvent(resourceEvent);
				} catch (ResourceException e) {
					logger.error("Exception in delivering LsResourceEvent",e);
					throw new LsResourceException(e);
				}
				if(isDebugEnabled)
					logger.debug("RA down event sent");
				this.canSendMessage=false;
				this.raUp=false;
				lsRaDao.destroy();
				queueManager.destroy();
				lsManager.stop();
			}
		} catch (Exception ex) {
			logger.error("Exception while stopping RA", ex);
			throw new LsResourceException(ex);
		}
		if(isInfoEnabled)
			logger.info("Leaving LsResourceAdaptor stop():: RA successfully stopped");
	}

	/* (non-Javadoc)
	 * @see com.baypackets.ase.spi.resource.ResourceAdaptor#configurationChanged(java.lang.String, java.lang.Object)
	 */
	@Override
	public void configurationChanged(String arg0, Object arg1)
	throws ResourceException {
		//logic not required

	}

	/* (non-Javadoc)
	 * @see com.baypackets.ase.spi.resource.ResourceAdaptor#roleChanged(java.lang.String, java.lang.String, short)
	 */
	@Override
	public void roleChanged(String clusterId, String subsystemId, short role) {
		boolean isInfoEnabled=logger.isInfoEnabled();
		if(isInfoEnabled)
			logger.info("Inside LsResourceAdaptor roleChanged() to "+role);

		short preRole = this.role;
		this.role = role;
		if(logger.isDebugEnabled())
			logger.debug("is RA active::["+raActive+"]");
		if (preRole != ROLE_ACTIVE && role == ROLE_ACTIVE && raActive) {
			try {
				this.start();
			} catch (Exception e) {
				logger.error("Exception in roleChanged(): " , e);
			}
		}
		if(isInfoEnabled)
			logger.info("Leaving LsResourceAdaptor roleChanged()");
	}

	/**
	 * enques message in LSQ
	 *  (non-Javadoc)
	 * @see com.baypackets.ase.spi.resource.ResourceAdaptor#sendMessage(com.baypackets.ase.spi.container.SasMessage)
	 */
	@Override
	public void sendMessage(SasMessage message) throws IOException {
		boolean isDebugEnabled=logger.isDebugEnabled();
		if(isDebugEnabled)
			logger.debug("Inside SendMessage");
		try {
			if (message instanceof LsRequest) {
				if(this.canSendMessage) {
					replicate((LsRequest)message);					
					queueManager.enQueueRequest((LsRequest)message);
				} else {
					if(isDebugEnabled)
						logger.debug("Peer is disconnected. cannot send request");
					LsResourceEvent resourceEvent = new LsResourceEvent(message, 
							LsResourceEvent.REQUEST_FAIL_EVENT, message.getApplicationSession());
					resourceEvent.setMessage((LsMessage) message);
					try {
						deliverEvent(resourceEvent);
					} catch (ResourceException e) {
						logger.error("Exception in delivering LsResourceEvent",e);
						throw new LsResourceException(e);
					}
				}
			} else{
				logger.error("Message dropped: not a LsRequest.");
			}
		}catch(Exception e){
			logger.error("sendMessage() failed ", e);	
		}

		if(isDebugEnabled)
			logger.debug("Leaving SendMessage");

	}

	/* (non-Javadoc)
	 * @see com.baypackets.ase.spi.container.SasMessageCallback#failed(com.baypackets.ase.spi.container.SasMessage, com.baypackets.ase.container.exceptions.AseInvocationFailedException)
	 */
	@Override
	public void failed(SasMessage arg0, AseInvocationFailedException arg1) {
		// logic not rquired

	}

	/* (non-Javadoc)
	 * @see com.baypackets.ase.spi.container.SasMessageCallback#processed(com.baypackets.ase.spi.container.SasMessage)
	 */
	@Override
	public void processed(SasMessage arg0) {
		// logic not rquired

	}

	/**
	 * sends response to the service
	 *  (non-Javadoc)
	 * @see com.baypackets.ase.ra.telnetssh.LsResourceAdaptor#deliverResponse(com.baypackets.ase.ra.telnetssh.message.TelnetSshResponse)
	 */
	@Override
	public void deliverResponse(LsResponse response)
	throws ResourceException {
		boolean isDebugEnabled=logger.isDebugEnabled();
		if(isDebugEnabled)
			logger.debug("Inside deliverResponse(): response "+response); 
		//setting Session state to Inactive
		((LsResourceSession)response.getSession()).setSessionState(LsResourceSession.LS_INACTIVE);
		((LsRequestImpl)response.getRequest()).setStatus(LsRequest.REQUEST_INACTIVE);
		
		if(response.getSession().getProtocolSessionState()==SasProtocolSession.INVALID){
			if(logger.isDebugEnabled()){
				logger.debug("LsResourceSession already invalidated by application so not delivering response to application for LsRequest:"+((LsRequestImpl)response.getRequest()).getRequestId());
			}	
			return;
		}
		
		if (context != null) {
			if(isDebugEnabled)
				logger.debug("deliverResponse(): call context.");
			context.deliverMessage(response, true);
		} else {
			logger.error("Unable to deliver Response, Resource Context is null");
		}
		replicate((LsRequest)response.getRequest());
		if(isDebugEnabled)
			logger.debug("Leaving deliverResponse() ");
	}

	/**
	 * sends event to service
	 *  (non-Javadoc)
	 * @see com.baypackets.ase.ra.telnetssh.LsResourceAdaptor#deliverEvent(com.baypackets.ase.ra.telnetssh.event.LsResourceEvent)
	 */
	@Override
	public void deliverEvent(LsResourceEvent event) throws ResourceException {
		boolean isDebugEnabled=logger.isDebugEnabled();
		if(isDebugEnabled)
			logger.debug("Inside deliverEvent(): LsResourceEvent::"+event.getType()); 

		boolean deliverUpward=true;

		if(event.getType().equals(LsResourceEvent.REQUEST_FAIL_EVENT)) {
			((LsResourceSession)(event.getMessage()).getSession()).setSessionState(LsResourceSession.LS_INACTIVE);
			((LsRequestImpl)event.getMessage()).setStatus(LsRequest.REQUEST_INACTIVE);
			replicate((LsRequest)event.getMessage());
		} else if(event.getType().equals(LsResourceEvent.QUEUE_FULL)){
			((LsResourceSession)(event.getMessage()).getSession()).setSessionState(LsResourceSession.LS_INACTIVE);
			((LsRequestImpl)event.getMessage()).setStatus(LsRequest.REQUEST_INACTIVE);
			replicate((LsRequest)event.getMessage());
		} else if(event.getType().equals(LsResourceEvent.PEER_DOWN)){
		} else if(event.getType().equals(LsResourceEvent.PEER_UP)){
		}else if(event.getType().equals(LsResourceEvent.RA_DOWN)){
			this.canSendMessage= false;
		}else if (event.getType().equals(LsResourceEvent.RA_UP)){
			this.canSendMessage = true;
		}

		if(deliverUpward == true){
			if (context != null) {
				if(isDebugEnabled)
					logger.debug("deliverEvent(): call context.");
				context.deliverEvent(event, true);
			} else {
				if(isDebugEnabled)
					logger.debug("deliverEvent(): Context is null Failed");
			}
		}
		if(isDebugEnabled)
			logger.debug("Leav	ing deliverEvent() ");

	}

	/**
	 * Method is called when change notification foirm DB is recieved
	 * Synchronized so that in case of multiple change notification one request is processed at time
	 * 
	 * new logic::
	 * Insert::->row inserted with active_scp_version=1
	 * Update: active_scp_version is changed to zero.
	 * 			new row is inserted for lsID with active_scp_version =1
	 * Delete active_scp_version is updated to zero.
	 * 
	 * Flow:
	 * 1 Iterate through Change Data set for each new entry
	 * 		1.1  check table name
	 *      1.2  If LS specific table
	 *      	1.2.1 Check Opertaion Type
	 *      	1.2.2 If Insert
	 *      		1.2.2.1 Fetch data from DB for specific LS
	 *      		1.2.2.2 Add LS in LsMAp
	 *      		1.2.2.3 Add LS in Q Manager
	 *      		1.2.2.4 Start session with LS
	 *      	1.2.3 If Update
	 *      		1.2.3.1 Fetch data from DB for specific LS
	 *      		1.2.3.2 Check Updated element..
	 *      		1.2.3.3 If LS ID is updated then update ID in Q MAnager and Ls MAnager. Also update LS in LsMAp
	 *      		1.2.3.4 If LS Q size or Q threshold ID is updated then update same in Q MAnager. Also update LS in LsMAp
	 *            	1.2.3.5 If LS IP or Port or username or password or connection type is update is updated then 
	 *            			recreate Ls connection with new confguration in LsMAnager. Also update LS in LsMAp
	 *         1.2.2 If Delete
	 *      		1.2.2.1 Fetch data from LsMAp for rowID
	 *      		1.2.2.2 remove LS Ques from Q manager
	 *      		1.2.2.3 stop LsSession in LsManager
	 *      		1.2.2.4 remove LS form LsMAp
	 *      1.2 If Common configuration is updated (delete and insert is not supported)
	 *      		1.2.2.1 Update values in local reference to comon configuration.
	 * End Flow     
	 * 
	 *  (non-Javadoc)
	 * @see com.baypackets.ase.ra.telnetssh.LsResourceAdaptor#lsConfigurationChanged(java.util.List)
	 */
	@Override
	public synchronized void lsConfigurationChanged(List<LsConfigChangeData> lsConfigChangeDataList) {
		boolean isDebugEnabled=logger.isDebugEnabled();
		boolean isInfoEnabled=logger.isInfoEnabled();
		if(isDebugEnabled)
			logger.debug("Inside lsResourceAdaptorImpl lsConfigurationChanged()");
		LsConfigChangeData lsConfigChangeData=null;
		RowOperation rowOperation=null;
		String tableName=null;
		String rowId=null;
		LS ls=null;
		LS oldLs=null;
		CommonLsConfig updatedConfig=null;
		//sorting list to process update before INSERT
		if(logger.isDebugEnabled())
			logger.debug("lsConfigurationChanged()-->sorting list::"+lsConfigChangeDataList);
		Collections.sort(lsConfigChangeDataList);
		if(logger.isDebugEnabled())
			logger.debug("lsConfigurationChanged()-->sorted list::"+lsConfigChangeDataList);

		//sort completed
		Iterator<LsConfigChangeData> lsConfigChangeDataIterator=lsConfigChangeDataList.iterator();
		//iterate on list of change data
		while(lsConfigChangeDataIterator.hasNext()){
			lsConfigChangeData=lsConfigChangeDataIterator.next();
			rowOperation = lsConfigChangeData.getRowOperation();
			tableName = lsConfigChangeData.getTableName();
			int index=tableName.indexOf(AseStrings.PERIOD);
			index++;
			tableName=tableName.substring(index);
			rowId = lsConfigChangeData.getRowId();
			//check if LS details table
			if(tableName.equals(Constants.allLsTable)){
				if(rowOperation == RowOperation.UPDATE){
					//check if update operation
					if(isDebugEnabled)
						logger.debug("Update on table::" + tableName + "  rowId::"+rowId);
					try {
						//fetch updated data
						ls=lsRaDao.getLsByRowId(rowId);
						oldLs = lsMap.get(rowId);
						if(oldLs==null){
							if(isDebugEnabled)
								logger.debug("Existing Ls for rowId "+ rowId +" not found for updation in RA map "+lsMap );
							throw new LsResourceException("Old LS Details not found in RA");
						}
						if(ls ==null){
							if(isDebugEnabled)
								logger.debug("On Update data not found in DB for rowId::"+rowId+"  Deleting LS");
							//this can be case of acctive_scp-version changed to 0 and ls marked for deletion so deleting LS
							try {
								ls=lsMap.get(rowId);
								if(ls==null){
									if(isDebugEnabled)
										logger.debug("Ls for rowId "+ rowId +" not found for deletion in map "+lsMap );
									throw new LsResourceException("Deleted LS Details not found in RA");
								}else {
									if(isDebugEnabled)
										logger.debug("Deleting Ls for rowId "+ rowId );
									queueManager.deleteQ(ls);
									lsManager.stopLsSession(ls);
									lsMap.remove(rowId);
								}
								if(isDebugEnabled)
									logger.debug("LS Deleted lsId::"+ls.getLsId());
							} catch (Exception e) {
								logger.error("Exception fetching Added LS on table::" + tableName + "  rowId::"+rowId,e);
							}
							
							throw new LsResourceException("Updated Ls details not found in "+Constants.allLsTable);
						}
						//if id is changed
						if(oldLs.getLsId()!=ls.getLsId()){
							if(isInfoEnabled)
								logger.info("update lsId   oldId::["+oldLs.getLsId()+"]  newId::["+ls.getLsId()+"]");
							boolean status=queueManager.updateLsId(oldLs.getLsId(), ls.getLsId());
							if(!status){
								logger.error("LsId update failed oldId::["+oldLs.getLsId()+"]  newId::["+ls.getLsId()+"]");
							}else{
								if(isDebugEnabled)
									logger.debug("in Qmanager Id updated");
								status=lsManager.updateLsId(oldLs.getLsId(), ls.getLsId());
								if(!status){
									logger.error("LsId update failed oldId::["+oldLs.getLsId()+"]  newId::["+ls.getLsId()+"]");
								}else{
									if(isDebugEnabled)
										logger.debug("in LsManager Id updated");
									oldLs.setLsId(ls.getLsId());
								}
							}
						}
						//if size is changed
						if(oldLs.getLsQSize()!=ls.getLsQSize()){
							if(isInfoEnabled)
								logger.info("update lsQsize  oldSize::["+oldLs.getLsQSize()+"]  newsize::["+ls.getLsQSize()+"]");
							boolean status=queueManager.updateQSize(ls);
							if(!status){
								logger.error("Qsize update failed oldSize::["+oldLs.getLsQSize()+"]  newsize::["+ls.getLsQSize()+"]");
							}else{
								oldLs.setLsQSize(ls.getLsQSize());
							}
						}
						//if threshold is changed
						if(oldLs.getLsQThreshold()!=ls.getLsQThreshold()){
							if(isInfoEnabled)
								logger.info("update lsQthreshold   oldThreshold::["+oldLs.getLsQThreshold()+"]  newThreshold::["+ls.getLsQThreshold()+"]");
							boolean status=queueManager.updateQThreshold(ls);
							if(!status){
								logger.error("QThreshold update failed oldThreshold::["+oldLs.getLsQThreshold()+"]  newThreshold::["+ls.getLsQThreshold()+"]");
							}else{
								oldLs.setLsQThreshold(ls.getLsQThreshold());
							}
						}
						////if connection specifiic parameters are changed
						if(oldLs.getLsIP().equals(ls.getLsIP()) || oldLs.getLsPort()!=ls.getLsPort() || 
								oldLs.getConnType().equals(ls.getConnType()) || oldLs.getLsUser().equals(ls.getLsUser()) || 
								oldLs.getLsPassword().equals(ls.getLsPassword())){
							if(isInfoEnabled)
								logger.info("update LsSesion Parameters   oldIP::["+oldLs.getLsIP()+"]  newIP::["+ls.getLsIP()+"]"+
										" oldPort::["+oldLs.getLsPort()+"]  newPort::["+ls.getLsPort()+"]"+
										" oldUser::["+oldLs.getLsUser()+"]  newUser::["+ls.getLsUser()+"]"+
										" oldPassword::["+oldLs.getLsPassword()+"]  newPassword::["+ls.getLsPassword()+"]"+
										" oldConnType::["+oldLs.getConnType()+"]  newConntype::["+ls.getConnType()+"]");
							lsManager.updateLsSession(ls);
							oldLs.setLsIP(ls.getLsIP());
							oldLs.setLsPort(ls.getLsPort());
							oldLs.setLsUser(ls.getLsUser());
							oldLs.setLsPassword(ls.getLsPassword());
							oldLs.setConnType(ls.getConnType());
						}
						if(isDebugEnabled)
							logger.debug("LS updated lsId::"+ls.getLsId());

					} catch (Exception e) {
						logger.error("Exception fetching updated LS on table::" + tableName + "  rowId::"+rowId+"  message::"+e.getLocalizedMessage());
						if(isDebugEnabled)
							logger.debug("Exception fetching updated LS on table::" + tableName + "  rowId::"+rowId,e);
					}
				}else if(rowOperation == RowOperation.INSERT){//check change type--Insert
					if(isDebugEnabled)
						logger.debug("Insert on table::" + tableName + "  rowId::"+rowId);
					try {
						//fetch added LS
						ls=lsRaDao.getLsByRowId(rowId);
						if(ls !=null ){
							//add LS create Q nd LS session
							if(isDebugEnabled)
									logger.debug("Accept Insert on table::" + tableName + "  rowId::"+rowId +" with ls present");
								lsMap.put(rowId, ls);
								queueManager.addQ(ls);
								lsManager.startLsSession(ls);
						}else{
							if(isDebugEnabled)
								logger.debug("ON INSERT data not found in DB for rowId::"+rowId);
							throw new LsResourceException("Insert data not found in "+Constants.allLsTable);
						}
						if(isDebugEnabled)
							logger.debug("LS Added lsId::"+ls.getLsId());
					} catch (Exception e) {
						logger.error("Exception fetching Added LS on table::" + tableName + "  rowId::"+rowId +"  message::"+e.getMessage());
						if(isDebugEnabled)
							logger.debug("Exception fetching Added LS on table::" + tableName + "  rowId::"+rowId,e);
					}
				}else if(rowOperation == RowOperation.DELETE){ //check if delete 
					if(isDebugEnabled)
						logger.debug("Delete on table::" + tableName + "  rowId::"+rowId);
					try {
//						ls=lsMap.get(rowId);
//						if(ls==null){
//							logger.error("Ls for rowId "+ rowId +" not found for deletion in map "+lsMap );
//							throw new LsResourceException("Deleted LS Details not found in RA");
//						}else {
//							queueManager.deleteQ(ls);
//							lsManager.stopLsSession(ls);
//							lsMap.remove(rowId);
//						}
//						if(isDebugEnabled)
//							logger.debug("LS Deleted lsId::"+ls.getLsId());
						if(logger.isDebugEnabled())
								logger.debug("UnSupported Operation "+ rowOperation.toString() + " on table::" + tableName + "  rowId::"+rowId);
						throw new UnsupportedOperationException("UnSupported Operation "+ rowOperation.toString() + " on table::" + tableName + "  rowId::"+rowId);
					} catch (Exception e) {
						logger.error("Exception handling delete on table::" + tableName + "  rowId::"+rowId,e);
					}
				}else{
					logger.error("UnSupported Operation "+ rowOperation.toString() + " on table::" + tableName + "  rowId::"+rowId);
				}
			}else if(tableName.equals(Constants.commonConfigTable)){//check if common config updated(delete and insert not suported)
				if(rowOperation == RowOperation.UPDATE){
					if(isDebugEnabled)
						logger.debug("Update on table::" + tableName + "  rowId::"+rowId);
					try {
						updatedConfig=lsRaDao.getCommonLsConfig();
						if(updatedConfig==null){
							logger.error("updated config not found for updation in DB");
						}else{
							commonLsConfig.setId(updatedConfig.getId());
							commonLsConfig.setNoResponseTimer(updatedConfig.getNoResponseTimer());
							commonLsConfig.setReAttempt(updatedConfig.getReAttempt());
							commonLsConfig.setRecoveryPeriod(updatedConfig.getRecoveryPeriod());
							
							//saneja @bug 10179 [
							commonLsConfig.setCommandLogEnabled(updatedConfig.isCommandLogEnabled());
							commonLsConfig.setSuppressCommand(updatedConfig.getSuppressCommand());
							commonLsConfig.setOutputDelim(updatedConfig.getOutputDelim());
							commonLsConfig.setDelimRespTimer(updatedConfig.getDelimRespTimer());
							commonLsConfig.setRespSeperator(updatedConfig.getRespSeperator());
							
							//setting dynamically updatable values in ra properties
							//because ra properties object is always referred to read these values
							// and no new framework is required
							raProperties.setCommandLogEnabled(commonLsConfig.isCommandLogEnabled());
							raProperties.setSuppressCommand(commonLsConfig.getSuppressCommand());
							raProperties.setOutputDelim(commonLsConfig.getOutputDelim());
							raProperties.setDelimRespTimer(commonLsConfig.getDelimRespTimer());
							raProperties.setRespSeperator(commonLsConfig.getRespSeperator());
							
							//updating any local copies mantained
							lsManager.updateCommonLsConfig();
							//updating any local configuration maintained
							
							//]closed saneja @bug 10179
							
							if(isDebugEnabled)
								logger.debug("LS updated CommonLSConfig Updated::"+commonLsConfig);
						}
					} catch (Exception e) {
						logger.error("Exception fetching LS config on table::" + tableName,e);
					}
				}else if(rowOperation == RowOperation.INSERT){
					logger.error("UnSupported Operation "+ rowOperation.toString() + " on table::" + tableName + "  rowId::"+rowId);
				}else if(rowOperation == RowOperation.DELETE){
					logger.error("UnSupported Operation "+ rowOperation.toString() + " on table::" + tableName + "  rowId::"+rowId);
				}else{
					logger.error("UnSupported Operation "+ rowOperation.toString() + " on table::" + tableName + "  rowId::"+rowId);
				}
			}else{
				logger.error("Unknown table name -->"+tableName );
			}
		}

		if(isInfoEnabled)
			logger.info("leaving Ls Configuration Changed");
	}

	/*
	 * (non-Javadoc)
	 * @see com.baypackets.ase.ra.telnetssh.LsResourceAdaptor#getResourceContext()
	 */
	@Override
	public ResourceContext getResourceContext() {
		return context;
	}

	/**
	 * Method to replicate session and requests.
	 *
	 * @param LsRequest
	 */
	private void replicate(LsRequest request) {
		if(logger.isDebugEnabled())
			logger.debug("replicate called");
		ReplicationEvent event = new ReplicationEvent(request.getSession() , "RESOURCE");
		((LsResourceSession)request.getSession()).sendReplicationEvent(event);
	}

	/*
	 * (non-Javadoc)
	 * @see com.baypackets.ase.spi.resource.ResourceAdaptor#unregisterApp(com.baypackets.ase.spi.deployer.DeployableObject)
	 */
	public void unregisterApp(DeployableObject ctx){
		if(logger.isDebugEnabled()) {
			logger.debug("Inside unregisterApp() with context "+ctx);
		}
		this.appList.remove(ctx);
	}

	/*
	 * (non-Javadoc)
	 * @see com.baypackets.ase.spi.resource.ResourceAdaptor#registerApp(com.baypackets.ase.spi.deployer.DeployableObject)
	 */
	public void registerApp(DeployableObject ctx){
		if(logger.isDebugEnabled()) {
			logger.debug("Inside registerApp() with context= "+ctx);
		}
		this.appList.add(ctx);
	}

	/*
	 * 
	 */
	public Iterator<DeployableObject> getAllRegisteredApps(){
		if(logger.isDebugEnabled()) {
			logger.debug("Inside getAllRegisteredApps()");
		}
		return this.appList.iterator();
	}

	/**
	 * @return the raUp
	 */
	public boolean isRaUp() {
		return raUp;
	}
}
