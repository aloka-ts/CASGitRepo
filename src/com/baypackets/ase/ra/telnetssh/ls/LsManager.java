/**
 * 
 */
package com.baypackets.ase.ra.telnetssh.ls;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;

import org.apache.log4j.Logger;

import com.baypackets.ase.ra.telnetssh.LsRaAlarmManager;
import com.baypackets.ase.ra.telnetssh.LsResourceAdaptor;
import com.baypackets.ase.ra.telnetssh.configmanager.CommonLsConfig;
import com.baypackets.ase.ra.telnetssh.configmanager.LS;
import com.baypackets.ase.ra.telnetssh.event.LsResourceEvent;
import com.baypackets.ase.ra.telnetssh.logger.CommandLogger;
import com.baypackets.ase.ra.telnetssh.ls.library.SessionCreationTask;
import com.baypackets.ase.ra.telnetssh.message.LsRequest;
import com.baypackets.ase.ra.telnetssh.message.LsRequestImpl;
import com.baypackets.ase.ra.telnetssh.message.LsResponse;
import com.baypackets.ase.ra.telnetssh.message.LsResponseImpl;
import com.baypackets.ase.ra.telnetssh.qm.LsQueue;
import com.baypackets.ase.ra.telnetssh.qm.QueueCleanTask;
import com.baypackets.ase.ra.telnetssh.qm.QueueManagerImpl;
import com.baypackets.ase.ra.telnetssh.utils.Constants;
import com.baypackets.ase.ra.telnetssh.utils.LsStatus;
import com.baypackets.ase.ra.telnetssh.utils.RaProperties;
import com.baypackets.ase.ra.telnetssh.workmanager.ThreadPool;
import com.baypackets.ase.ra.telnetssh.workmanager.ThreadPoolManager;
import com.baypackets.ase.resource.ResourceException;

/**
 * The Class LsManager.
 * Interface class to other modules 
 * trying to intercat wiuth LS
 *
 * @author saneja
 */
public class LsManager {

	/** The Constant logger. */
	private static final Logger logger=Logger.getLogger(LsManager.class);

	/** The ls manager. */
	private static LsManager lsManager;

	/** The ls id session map. */
	private Map<Integer,LsSession> lsIdSessionMap;

	/** The common ls config. */
	private CommonLsConfig commonLsConfig; 

	/** The command logger. */
	private CommandLogger commandLogger;

//	/** The es. */
//	private ExecutorService es;

	/** The ls resource adaptor. */
	private LsResourceAdaptor lsResourceAdaptor;

	/** The session recovery task. */
	private SessionRecoveryTask sessionRecoveryTask;
	
	/** The session recovery task. */
	private SessionPermRecoveryTask sessionPermRecoveryTask;

	/** The keep Alive task. */
	private KeepAliveTask keepAliveTask;

	/** SessionCreationTasks List */
	private List<Future<?>> taskList;

	/** The ra properties. */
	private RaProperties raProperties;
	
	/**reader Thread pool */
	private ThreadPoolManager readerThreadPool;
	
	/**reader Thread pool */
	private ThreadPoolManager sessionPool;
	
	/**keep alive thread pool */
	private ThreadPoolManager keepAliveThreadPool;
		



	/**
	 * Instantiates a new queue manager impl.
	 */
	private LsManager(){
		lsIdSessionMap=new ConcurrentHashMap<Integer, LsSession>();
	}

	/**
	 * Gets the single instance of QueueManagerImpl.
	 *
	 * @return single instance of QueueManagerImpl
	 */
	public static synchronized LsManager getInstance(){
		if(lsManager==null)
			lsManager=new LsManager();
		return lsManager;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#clone()
	 */
	@Override
	protected Object clone() throws CloneNotSupportedException {
		throw new CloneNotSupportedException("Clone is not allowed.");
	}
	//singleton complete

	/**
	 * Gets the ls id session map.
	 *
	 * @return the lsIdSessionMap
	 */
	public Map<Integer,LsSession> getLsIdSessionMap() {
		return lsIdSessionMap;
	}

	/**
	 * Gets the common ls config.
	 *
	 * @return the commonLsConfig
	 */
	public CommonLsConfig getCommonLsConfig() {
		return commonLsConfig;
	}

	/**
	 * Start method called on RA startup.
	 * sets the command logger
	 * Craetes thread pool for Session craetions
	 * starts ls session for each LS in seperate thread
	 * submit recovery task(session recovery task in thread pool
	 * Wait for Ls Session craetion tasks to complete
	 *
	 * @param lsCollection the ls collection
	 * @param commonLsConfig the common ls config
	 * @param lsResourceAdaptor the ls resource adaptor
	 */
	public void start(Collection<LS> lsCollection, CommonLsConfig commonLsConfig, 
			LsResourceAdaptor lsResourceAdaptor){
		boolean isInfoEnabled=logger.isInfoEnabled();
		boolean isDebugEnabled=logger.isDebugEnabled();
		if(isInfoEnabled)
			logger.info("Loading LSManager. Enter start()");
		this.lsResourceAdaptor=lsResourceAdaptor;
		this.commonLsConfig=commonLsConfig;
		commandLogger = CommandLogger.getInstance();
		//getting session thread pool
		sessionPool =new ThreadPool("ls-session-pool");
		sessionPool.startPool();
		
		//getting reader thread pool
		readerThreadPool=new ThreadPool("ls-reader-pool",1);//XXX add size
		readerThreadPool.startPool();
		
		//getting reader thread pool
		keepAliveThreadPool=new ThreadPool("ls-keepalive-pool",1);//XXX add size
		keepAliveThreadPool.startPool();
		
		
		//saneja@ bug 7085 setting configurable log[
		commandLogger.setLogEnabled(raProperties.isCommandLogEnabled());
		//]closed saneja@bug 7085
		taskList=new ArrayList<Future<?>>();
		if(isDebugEnabled)
			logger.debug("LsManager start()-->ThreadPool created");
		Iterator<LS> lsIterator=lsCollection.iterator();
		sessionRecoveryTask=new SessionRecoveryTask();
		sessionPermRecoveryTask = new SessionPermRecoveryTask();
		while(lsIterator.hasNext()){
			LS ls=lsIterator.next();
			if(isDebugEnabled)
				logger.debug("LsManager start()-->lsIterator onlsID::["+ls.getLsId() +"]");
			startLsSession(ls);
		}
		sessionRecoveryTask.setCommonLsConfig(this.commonLsConfig);
		sessionRecoveryTask.setSessionPermRecoveryTask(sessionPermRecoveryTask);
		sessionPool.submitTask(sessionRecoveryTask);
		sessionPool.submitTask(sessionPermRecoveryTask);
		
		keepAliveTask = new KeepAliveTask();
		keepAliveThreadPool.submitTask(keepAliveTask);
		
//Session creation can happpen in parallel no need to wait as failure will add tasks to recovery.
//		//waiting for session creation tasks to finish
//		Iterator<Future<?>> futureTaskIterator=taskList.iterator();
//		while(futureTaskIterator.hasNext()){
//			Future<?> futureTask=futureTaskIterator.next();
//			try {
//				futureTask.get();
//			} catch (InterruptedException e) {
//				logger.error("Wait on task failed with InterruptedException");
//			} catch (ExecutionException e) {
//				logger.error("Wait on task failed with ExecutionException",e);
//			}
//			futureTaskIterator.remove();
//		}
		taskList=null;
		if(isInfoEnabled)
			logger.info("LSManager loaded. leaving start()");

	}

	/**
	 * Stop called on RA stop.
	 */
	public void stop(){
		boolean isInfoEnabled=logger.isInfoEnabled();
		if(isInfoEnabled)
			logger.info("Destroying LSManager. Enter stop()");
		Set<Integer> lsIdSet =lsIdSessionMap.keySet();
		Iterator<Integer> lsIdIterator=lsIdSet.iterator();	
		LS ls=null;
		sessionRecoveryTask.destroyRecoverTask();
		sessionPermRecoveryTask.destroyRecoverTask();
		while(lsIdIterator.hasNext()){		
			Integer lsId=lsIdIterator.next();
			ls=lsIdSessionMap.get(lsId).getLs();
			stopLsSession(ls);
		}
		keepAliveTask.setDestroyKeepAlive(true);
		//stopping the pools
		readerThreadPool.stopPool();
		sessionPool.stopPool();
		keepAliveThreadPool.stopPool();
		//stopping the pools
		
		if(isInfoEnabled)
			logger.info("Destroy Complete. Leave stop()");
	}

	/**
	 * Start session with respective LS
	 * use Session Creation TAsk to create session.
	 *
	 * @param ls the ls
	 */
	public void startLsSession(LS ls){
		boolean isInfoEnabled=logger.isInfoEnabled();
		if(isInfoEnabled)
			logger.info("LsManager startLsSession(). lsId::"+ls.getLsId());
		SessionCreationTask sessionCreationTask=new SessionCreationTask(SessionCreationTask.Operation.START_LS);
		sessionCreationTask.setLs(ls);
		
		Future<?> futureTask=sessionPool.submitTask(sessionCreationTask);
		if(taskList!=null)
			taskList.add(futureTask);
		if(isInfoEnabled)
			logger.info("Leaving LsManager createLsSession");
	}

	/**
	 * Stop ls session.
	 *
	 * @param ls the ls
	 */
	public void stopLsSession(LS ls){
		boolean isInfoEnabled=logger.isInfoEnabled();
		if(isInfoEnabled)
			logger.info("LsManager stopLsSession(). lsId::"+ls.getLsId());
		LsSession lsSession=lsIdSessionMap.get(ls.getLsId());
		if(lsSession==null){
			logger.error("Inavlid update existing session not found   lsId::"+ls.getLsId());
			return;
		}
		synchronized(lsSession){
			if(lsSession.getLsStatus()==LsStatus.LS_UP)
				lsSession.stopSession();
			lsSession.setLs(null);	
		}
		lsIdSessionMap.remove(ls.getLsId());
		
		int size=getLsIdSessionMap().size();
		int loadFactor=getRaProperties().getDeQueueThreadLoadFactor();
		if(size % loadFactor ==0){
			getReaderThreadPool().decrementPoolSize(1);
		}
		if(isInfoEnabled)
			logger.info("Leaving LsManager createLsSession");
	}

	/**
	 * Execute request. Extracts command and 
	 * calls execute method of LsSession
	 *
	 * @param lsRequest the ls request
	 */
	public void executeRequest(LsRequest lsRequest){
		boolean isInfoEnabled=logger.isInfoEnabled();
		boolean isDebugEnabled=logger.isDebugEnabled();
		if(isInfoEnabled)
			logger.info("Enter LsManager executeRequest(). lsId::"+lsRequest.getLsId()+
					" command::" + lsRequest.getLsCommand());
			
		if(commandLogger.isLogEnabled()){		
			commandLogger.log("Execute--> on LsId::'"+ lsRequest.getLsId() +
				"'  RequestId::'"+lsRequest.getRequestId()+
				"'  Command::'"+lsRequest.getLsCommand()+"'");
		}
		
		int lsId = lsRequest.getLsId();
		QueueManagerImpl qm= (QueueManagerImpl) QueueManagerImpl.getInstance();
		LsQueue lsQueue=null;
		QueueCleanTask queueCleanTask=null;
		LsSession lsSession=lsIdSessionMap.get(Integer.valueOf(lsId));
		if(lsSession == null || lsSession.getLsStatus() == LsStatus.LS_DOWN){
			logger.error("LsManager executeRequest()-->Oops, Unable to find established session with LS  lsId::"+lsId);
			lsQueue=qm.getLsIdQueueMap().get(Integer.valueOf(lsId));
			queueCleanTask=new QueueCleanTask(lsQueue);
			if(isDebugEnabled)
				logger.debug("LsManager executeRequest()-->starting Q clean Task on LsId::"+ lsRequest.getLsId());
			sessionPool.submitTask(queueCleanTask);
			try {
				LsResourceEvent resourceEvent= new LsResourceEvent(lsRequest, LsResourceEvent.REQUEST_FAIL_EVENT, lsRequest.getApplicationSession());
				resourceEvent.setMessage(lsRequest);
				lsResourceAdaptor.deliverEvent(resourceEvent);
			} catch (ResourceException e) {
				logger.error("LsManager executeRequest()-->Error Sending request Fail event on null LsSession",e);
			}	
			return;
		}
		if(isDebugEnabled)
			logger.debug("LsManager executeRequest()-->sending command on LsId::"+ lsId);
		List<String> lsCommandResult=lsSession.executeCommand(lsRequest.getLsCommand());
		
		if(commandLogger.isLogEnabled()){
			commandLogger.log("Result--> on LsId::'"+ lsId + 
				"'  Command::'"+lsRequest.getLsCommand()+
				"'  Result is::'"+ lsCommandResult +"'" );
		}
		
		if(lsCommandResult==null || lsCommandResult.isEmpty()){
			logger.error("Unable to execute command on LS   lsId:::"+lsId);
			lsSession.setLsStatus(LsStatus.LS_DOWN);
			lsQueue=qm.getLsIdQueueMap().get(Integer.valueOf(lsId));
			queueCleanTask=new QueueCleanTask(lsQueue);
			if(isDebugEnabled)
				logger.debug("LsManager executeRequest()-->starting Q clean Task on LsId::"+ lsRequest.getLsId());
			sessionPool.submitTask(queueCleanTask);
			try {
				lsResourceAdaptor.deliverEvent(new LsResourceEvent("PEER_DOWN_EVENT LsId["+lsRequest.getLsId()+"]", LsResourceEvent.PEER_DOWN, null));
				LsResourceEvent resourceEvent= new LsResourceEvent(lsRequest, LsResourceEvent.REQUEST_FAIL_EVENT, lsRequest.getApplicationSession());
				resourceEvent.setMessage(lsRequest);
				lsResourceAdaptor.deliverEvent(resourceEvent);
			} catch (ResourceException e) {
				logger.error("Error Sending request Fail event/Peer_down on null lsCommandResult",e);
			}
			if(!lsSession.isFailAlarmSent()){
				lsSession.setFailAlarmSent(true);
				String alarmMsg="LsId:["+lsId+"]";
				LsRaAlarmManager.getInstance().raiseAlarm(LsRaAlarmManager.CONNECTION_FAILED, lsId, alarmMsg);
			}
			lsSession.setLsDownTimeStamp(new Date());
			sessionRecoveryTask.addLsSession(lsSession);
		}else {
			if(isDebugEnabled)
				logger.debug("LsManager executeRequest()-->Command result present on lsid::"+ lsRequest.getLsId());
			try {
				LsResponse lsResponse = (LsResponse) lsRequest.createResponse(Constants.EXECUTE);
				((LsResponseImpl) lsResponse).setResultCode(LsResponse.SUCCES_RESULT_CODE);
				((LsResponseImpl) lsResponse).setResultData(lsCommandResult);
				lsResourceAdaptor.deliverResponse(lsResponse);
			} catch (ResourceException e) {
				logger.error("Error creating Response  lsid::"+lsId,e);
			}
		}
		if(isInfoEnabled)
			logger.info("Leaving LsManager executeRequest()");
	}


	/**
	 * sends request and returns send status. 
	 * Extracts command and 
	 * calls send method of LsSession Flow:
	 * checks if LS is down return false 
	 *
	 * @param lsRequest the ls request
	 */
	public boolean sendRequest(LsRequest lsRequest){
		boolean isInfoEnabled=logger.isInfoEnabled();
		boolean isDebugEnabled=logger.isDebugEnabled();
		if(isInfoEnabled)
			logger.info("Enter LsManager sendRequest(). lsId::"+lsRequest.getLsId()+
					" command::" + lsRequest.getLsCommand());
		
		if (lsRequest.getStatus() != LsRequest.REQUEST_PENDING) {
			if (commandLogger.isLogEnabled()) {
				commandLogger.log("Send--> on LsId::'" + lsRequest.getLsId()
						+ "'  RequestId::'" + lsRequest.getRequestId()
						+ "'  Command::'" + lsRequest.getLsCommand()
						+ "'  status ::'" + lsRequest.getStatus() + "'");
			}
		}
		
		int lsId = lsRequest.getLsId();
		
		QueueManagerImpl qm= (QueueManagerImpl) QueueManagerImpl.getInstance();
		LsQueue lsQueue=null;
		QueueCleanTask queueCleanTask=null;
		LsSession lsSession=lsIdSessionMap.get(Integer.valueOf(lsId));
		//checking lssession status
		if(lsSession == null || lsSession.getLsStatus() == LsStatus.LS_DOWN){
			logger.error("LsManager sendRequest()-->Unable to find established session with LS  lsId::"+lsId);
			lsQueue=qm.getLsIdQueueMap().get(Integer.valueOf(lsId));
			queueCleanTask=new QueueCleanTask(lsQueue);
			if(isDebugEnabled)
				logger.debug("LsManager executeRequest()-->starting Q clean Task on LsId::"+ lsRequest.getLsId());
			sessionPool.submitTask(queueCleanTask);
			return false;
		}

		//sending command
		if(isDebugEnabled)
			logger.debug("LsManager sendRequest()-->sending command on LsId::"+ lsId);
		CommandStatus status=lsSession.sendCommand(lsRequest.getLsCommand());
		//check if error in sending command
		boolean returnStatus=false;
		if(status.equals(CommandStatus.SEND_ERROR)){
			logger.error("LsManager sendRequest()-->Error in send operation lsId::"+lsId);
			lsQueue=qm.getLsIdQueueMap().get(Integer.valueOf(lsId));
			queueCleanTask=new QueueCleanTask(lsQueue);
			if(isDebugEnabled)
				logger.debug("LsManager executeRequest()-->starting Q clean Task on LsId::"+ lsId);
			//This is added to mark ls session available after cleaning the queue.
			queueCleanTask.setLsSession(lsSession);
			sessionPool.submitTask(queueCleanTask);
			return false;
		}else if(status.equals(CommandStatus.SEND_FAIL)){  //if fail due to busy LS
			
			if (lsRequest.getStatus() != LsRequest.REQUEST_PENDING) {
				if (commandLogger.isLogEnabled()) {
					commandLogger
							.log("LsManager sendRequest()-->send failed busy LS try later");
				}
			}
			//UAT:1410 REQUEST_FAIL Event is being stopped as we should retry the request. Otherwise, the service
			//would clean the call.
//			try {
//				LsResourceEvent resourceEvent= new LsResourceEvent(lsRequest, LsResourceEvent.REQUEST_FAIL_EVENT, lsRequest.getApplicationSession());
//				resourceEvent.setMessage(lsRequest);
//				lsResourceAdaptor.deliverEvent(resourceEvent);
//			} catch (ResourceException e) {
//				logger.error("Exception sending Request_fail_event on clean",e);
//			}
			returnStatus= false;
		}else if(status.equals(CommandStatus.SEND_SUCCESS)){
			if(isDebugEnabled)
				logger.debug("LsManager sendRequest()-->Enque success");
			LsReaderTask readTask=new LsReaderTask();
			readTask.setLsRequest(lsRequest);
			readTask.setLsSession(lsSession);
			readerThreadPool.submitTask(readTask);
			((LsRequestImpl) lsRequest).setStatus(LsRequest.REQUEST_ACTIVE);
			((LsRequestImpl)lsRequest).incrementAttempt();
			returnStatus= true;
		}

		if(isDebugEnabled)
			logger.debug("LsManager sendRequest()-->send Status::["+returnStatus+"]");

		if(isInfoEnabled)
			logger.info("Leaving LsManager sendRequest() with status::"+returnStatus);

		return returnStatus;
	}


	/**
	 * processes commandresult recieved from LS. 
	 * creates response and sends to app
	 *
	 * @param lsRequest the ls request
	 * @return boolean true for normal handling false if session recovery started
	 */
	public boolean handleResult(LsRequest lsRequest, LsResult lsResult){

		boolean isInfoEnabled=logger.isInfoEnabled();
		boolean isDebugEnabled=logger.isDebugEnabled();
		if(isInfoEnabled)
			logger.info("Enter LsManager handleResult(lsRequest). lsId::"+lsRequest.getLsId()+
					" command::" + lsRequest.getLsCommand());
		List<String> lsCommandResult=lsResult.getResult();
		boolean status=false;
		boolean doRecovery=lsResult.isRecoverSession();
		
		if(commandLogger.isLogEnabled()){
			commandLogger.log("Result--> on LsId::'"+ lsRequest.getLsId() + 
				"'  RequestId::'"+lsRequest.getRequestId()+
				"'  Command::'"+lsRequest.getLsCommand()+
				"'  Result is::'"+ lsCommandResult +"'" );
		}
	
		CommonLsConfig commonLsConfig=getCommonLsConfig();
		int attemptsAllowed=commonLsConfig.getReAttempt();

		int lsId = lsRequest.getLsId();
		QueueManagerImpl qm= (QueueManagerImpl) QueueManagerImpl.getInstance();
		LsQueue lsQueue=null;
		QueueCleanTask queueCleanTask=null;
		LsSession lsSession=lsIdSessionMap.get(Integer.valueOf(lsId));
		//check if recovery flag
		if(doRecovery){
			logger.error("Ls Result contain recovery flag lsId:::"+lsRequest.getLsId());
			lsSession.setLsStatus(LsStatus.LS_DOWN);
			lsQueue=qm.getLsIdQueueMap().get(Integer.valueOf(lsId));
			queueCleanTask=new QueueCleanTask(lsQueue);
			if(isDebugEnabled)
				logger.debug("LsManager handleResult(lsRequest)-->starting Q clean Task on LsId::"+ lsRequest.getLsId());
			sessionPool.submitTask(queueCleanTask);
//			try {
//				lsResourceAdaptor.deliverEvent(new LsResourceEvent("PEER_DOWN_EVENT LsId["+lsRequest.getLsId()+"]", LsResourceEvent.PEER_DOWN, null));
//				LsResourceEvent resourceEvent= new LsResourceEvent(lsRequest, LsResourceEvent.REQUEST_FAIL_EVENT, lsRequest.getApplicationSession());
//				resourceEvent.setMessage(lsRequest);
//				lsResourceAdaptor.deliverEvent(resourceEvent);
//			} catch (ResourceException e) {
//				logger.error("Error Sending request Fail event/Peer_down on null lsCommandResult",e);
//			}
			//force disconnecting LsSession
			lsSession.stopSession();
			if(!lsSession.isFailAlarmSent()){
				lsSession.setFailAlarmSent(true);
				String alarmMsg="LsId:["+lsId+"]";
				LsRaAlarmManager.getInstance().raiseAlarm(LsRaAlarmManager.CONNECTION_FAILED, lsId, alarmMsg);
			}
			lsSession.setLsDownTimeStamp(new Date());
			sessionRecoveryTask.addLsSession(lsSession);
		}else if( lsCommandResult==null  ||  lsCommandResult.isEmpty() ){
			if(isDebugEnabled)
				logger.debug("Unable to get resp form LS lsId:::"+lsRequest.getLsId());
			//if attempts exceeded
			if(((LsRequestImpl)lsRequest).getAttempt() >= attemptsAllowed){
				logger.error("Null response Attempts completed for request on LS:::"+lsRequest.getLsId());
				lsSession.setLsStatus(LsStatus.LS_DOWN);
				lsQueue=qm.getLsIdQueueMap().get(Integer.valueOf(lsId));
				queueCleanTask=new QueueCleanTask(lsQueue);
				if(isDebugEnabled)
					logger.debug("LsManager handleResult(lsRequest) -->starting Q clean Task on LsId::"+ lsRequest.getLsId());
				sessionPool.submitTask(queueCleanTask);
//				try {
//					lsResourceAdaptor.deliverEvent(new LsResourceEvent("PEER_DOWN_EVENT LsId["+lsRequest.getLsId()+"]", LsResourceEvent.PEER_DOWN, null));
//					LsResourceEvent resourceEvent= new LsResourceEvent(lsRequest, LsResourceEvent.REQUEST_FAIL_EVENT, lsRequest.getApplicationSession());
//					resourceEvent.setMessage(lsRequest);
//					lsResourceAdaptor.deliverEvent(resourceEvent);
//				} catch (ResourceException e) {
//					logger.error("Error Sending request Fail event/Peer_down on null lsCommandResult",e);
//				}
				//force disconnecting LsSession
				lsSession.stopSession();
				
				if(!lsSession.isFailAlarmSent()){
					lsSession.setFailAlarmSent(true);
					String alarmMsg="LsId:["+lsId+"]";
					LsRaAlarmManager.getInstance().raiseAlarm(LsRaAlarmManager.CONNECTION_FAILED, lsId, alarmMsg);
				}
				lsSession.setLsDownTimeStamp(new Date());
				sessionRecoveryTask.addLsSession(lsSession);
			}else{
				if(isDebugEnabled)
					logger.debug("null resp attempts pending::: Ls "+lsRequest.getLsId());
				status=true;
				((LsRequestImpl) lsRequest).setStatus(LsRequest.REQUEST_PENDING);
				qm.notifyDequeueTask(lsRequest);
			}
		}else { //succes response
			if(isDebugEnabled)
				logger.debug("LsManager handleResult(lsRequest)-->Command result present on lsid::"+ lsRequest.getLsId());
			try {
				status=true;
				LsResponse lsResponse = (LsResponse) lsRequest.createResponse(Constants.EXECUTE);
				((LsResponseImpl) lsResponse).setResultCode(LsResponse.SUCCES_RESULT_CODE);
				((LsResponseImpl) lsResponse).setResultData(lsCommandResult);
				lsResourceAdaptor.deliverResponse(lsResponse);
				//POLLqueue
				qm.pollRequest(lsRequest);
				
			} catch (ResourceException e) {
				logger.error("Error creating Response  lsid::"+lsId,e);
			}
		}
		
		return status;

	}

	/**
	 * This is the overloaded method which is used to handle the keep alive command result
	 * processes commandresult recieved from LS. 
	 * creates response 
	 *
	 * @param lsResult the ls result
	 * @return boolean true for normal handling false if session recovery started
	 */
	public boolean handleResult(LsSession session, String command, LsResult lsResult){

		boolean isInfoEnabled=logger.isInfoEnabled();
		boolean isDebugEnabled=logger.isDebugEnabled();
		int lsId = session.getLs().getLsId();
		if(isInfoEnabled)
			logger.info("Enter LsManager handleResult(command). lsId::" + lsId +
					" command::" + command);
		List<String> lsCommandResult = lsResult.getResult();
		boolean status = false;
		boolean doRecovery = lsResult.isRecoverSession();
		if(commandLogger.isLogEnabled()){
			commandLogger.log("Result--> on LsId::'"+ lsId + 
				"'  Command::'"+ command +
				"'  Result is::'"+ lsCommandResult +"'" );
		}
		

		int attemptsAllowed = raProperties.getKeepAliveFailedAttempts();
		if(isDebugEnabled){
			logger.debug("LsManager handleResult(command)-->attemptsAllowed::"+ attemptsAllowed);
		}
		
		QueueManagerImpl qm= (QueueManagerImpl) QueueManagerImpl.getInstance();
		LsQueue lsQueue=null;
		QueueCleanTask queueCleanTask=null;
		LsSession lsSession=lsIdSessionMap.get(Integer.valueOf(lsId));
		//check if recovery flag
		if(doRecovery){
			logger.error("Ls Result contain recovery flag lsId:::" + lsId);
			lsSession.setLsStatus(LsStatus.LS_DOWN);
			lsQueue=qm.getLsIdQueueMap().get(Integer.valueOf(lsId));
			queueCleanTask=new QueueCleanTask(lsQueue);
			if(isDebugEnabled)
				logger.debug("LsManager handleResult(command)-->starting Q clean Task on LsId::"+ lsId);
			sessionPool.submitTask(queueCleanTask);
//			try {
//				lsResourceAdaptor.deliverEvent(new LsResourceEvent("PEER_DOWN_EVENT LsId["+lsRequest.getLsId()+"]", LsResourceEvent.PEER_DOWN, null));
//				LsResourceEvent resourceEvent= new LsResourceEvent(lsRequest, LsResourceEvent.REQUEST_FAIL_EVENT, lsRequest.getApplicationSession());
//				resourceEvent.setMessage(lsRequest);
//				lsResourceAdaptor.deliverEvent(resourceEvent);
//			} catch (ResourceException e) {
//				logger.error("Error Sending request Fail event/Peer_down on null lsCommandResult",e);
//			}
			//force disconnecting LsSession
			lsSession.stopSession();
			if(!lsSession.isFailAlarmSent()){
				lsSession.setFailAlarmSent(true);
				String alarmMsg="LsId:["+lsId+"]";
				LsRaAlarmManager.getInstance().raiseAlarm(LsRaAlarmManager.CONNECTION_FAILED, lsId, alarmMsg);
			}
			lsSession.setLsDownTimeStamp(new Date());
			sessionRecoveryTask.addLsSession(lsSession);
		}else if( lsCommandResult==null  ||  lsCommandResult.isEmpty() ){
			if(isDebugEnabled)
				logger.debug("Unable to get resp form LS lsId:::" + lsId);
			int keepAliveCmdCntr = lsSession.getKeepAliveCommandFailedCtr();
			keepAliveCmdCntr = keepAliveCmdCntr + 1;
			lsSession.setKeepAliveCommandFailedCtr(keepAliveCmdCntr);
			//if attempts exceeded
			if(keepAliveCmdCntr >= attemptsAllowed){
				logger.error("Null response Attempts for keep alive command completed for request on LS:::" + lsId);
				lsSession.setLsStatus(LsStatus.LS_DOWN);
				lsQueue=qm.getLsIdQueueMap().get(Integer.valueOf(lsId));
				queueCleanTask=new QueueCleanTask(lsQueue);
				if(isDebugEnabled)
					logger.debug("LsManager handleResult(command)-->starting Q clean Task on LsId::" + lsId);
				sessionPool.submitTask(queueCleanTask);
//				try {
//					lsResourceAdaptor.deliverEvent(new LsResourceEvent("PEER_DOWN_EVENT LsId["+lsRequest.getLsId()+"]", LsResourceEvent.PEER_DOWN, null));
//					LsResourceEvent resourceEvent= new LsResourceEvent(lsRequest, LsResourceEvent.REQUEST_FAIL_EVENT, lsRequest.getApplicationSession());
//					resourceEvent.setMessage(lsRequest);
//					lsResourceAdaptor.deliverEvent(resourceEvent);
//				} catch (ResourceException e) {
//					logger.error("Error Sending request Fail event/Peer_down on null lsCommandResult",e);
//				}
				//force disconnecting LsSession
				lsSession.stopSession();
				
				if(!lsSession.isFailAlarmSent()){
					lsSession.setFailAlarmSent(true);
					String alarmMsg="LsId:["+lsId+"]";
					LsRaAlarmManager.getInstance().raiseAlarm(LsRaAlarmManager.CONNECTION_FAILED, lsId, alarmMsg);
				}
				lsSession.setLsDownTimeStamp(new Date());
				sessionRecoveryTask.addLsSession(lsSession);
			}else{
				if(isDebugEnabled)
					logger.debug("Failed attempts pending:::" + lsId);
				status=true;
			}
		}else { //succes response
			if(isDebugEnabled)
				logger.debug("LsManager handleResult(command)-->Command result present on lsid::"+ lsId);
			status = true;
		}
		
		return status;

	}
	
	/**
	 * Updates the connection with LS based on new parameters
	 *
	 * @param ls the ls
	 */
	public void updateLsSession(LS ls){
		boolean isInfoEnabled=logger.isInfoEnabled();
		if(ls==null){
			logger.error("null input to update LsSession");
			return;
		}
		if(isInfoEnabled)
			logger.info("Enter LsManager updateLsSession(). lsId::"+ls.getLsId());

		//stopping the existing session
		LsSession lsSession=lsIdSessionMap.get(ls.getLsId());
		boolean failAlarmSent=false;
		if(lsSession==null){
			logger.error("Inavlid update existing session not found   lsId::"+ls.getLsId());
			return;
		}
		synchronized(lsSession){
			failAlarmSent=lsSession.isFailAlarmSent();
			if(lsSession.getLsStatus()==LsStatus.LS_UP)
				lsSession.stopSession();
			lsSession.setLs(null);	
		}
		lsIdSessionMap.remove(ls.getLsId());


		SessionCreationTask sessionCreationTask=new SessionCreationTask(SessionCreationTask.Operation.UPDATE_LS);
		sessionCreationTask.setLs(ls);
		sessionCreationTask.setFailAlarmSent(failAlarmSent);
		sessionPool.submitTask(sessionCreationTask);

		if(isInfoEnabled)
			logger.info("Leaving LsManager updateLsSession().");
	}

	/**
	 * Update ls id/number in LsManager maps
	 *
	 * @param oldLsId the old ls id
	 * @param newLsId the new ls id
	 * @return true, if successful
	 */
	public boolean updateLsId(int oldLsId, int newLsId) {
		boolean isDebugEnabled=logger.isDebugEnabled();
		boolean isInfoEnabled=logger.isInfoEnabled();
		if(isInfoEnabled)
			logger.info("upddateLsId()::Updating lsId oldId::"+oldLsId+"  newId::"+newLsId);
		boolean updateStatus=false;
		LsSession lsSession=lsIdSessionMap.get(Integer.valueOf(oldLsId));
		if(lsSession==null ){
			logger.error("Update failed::null Session::"+lsIdSessionMap);
			updateStatus=false;
			return updateStatus;
		}
		if(isDebugEnabled)
			logger.debug("update lsId key in lsIdSessionMap oldId::"+oldLsId+"  newId::"+newLsId);
		lsIdSessionMap.remove(Integer.valueOf(oldLsId));
		lsIdSessionMap.put(Integer.valueOf(newLsId),lsSession);
		updateStatus=true;
		if(isInfoEnabled)
			logger.info("upddateLsId()::return with status:;"+updateStatus);
		return updateStatus;
	}

	
	/**
	 * Update ls common configuraton stored locally
	 * no parameter since master copy of commonLsConfig is directly accesible 
	 * @return true, if successful
	 */
	public boolean updateCommonLsConfig() {
		boolean isInfoEnabled=logger.isInfoEnabled();
		if(isInfoEnabled)
			logger.info("updateCommonLsConfig()::Updating local copy of configuration");
		boolean updateStatus=false;
		//raproperties/commonLsConfig can be used interchangeably
		//raproperties use for this variable is not recommended for future use because these APis  cane be deprecated
		//is updated before this APi invocatio
		commandLogger.setLogEnabled(commonLsConfig.isCommandLogEnabled());
		updateStatus=true;
		if(isInfoEnabled)
			logger.info("updateCommonLsConfig()::return with status:;"+updateStatus);
		return updateStatus;
	}
	

	/**
	 * Gets the ls resource adaptor.
	 *
	 * @return the lsResourceAdaptor
	 */
	public LsResourceAdaptor getLsResourceAdaptor() {
		return lsResourceAdaptor;
	}

	/**
	 * Gets the session recovery task.
	 *
	 * @return the sessionRecoveryTask
	 */
	public SessionRecoveryTask getSessionRecoveryTask() {
		return sessionRecoveryTask;
	}

	/**
	 * @param raProperties the raProperties to set
	 */
	public void setRaProperties(RaProperties raProperties) {
		this.raProperties = raProperties;
	}

	/**
	 * @return the raProperties
	 */
	public RaProperties getRaProperties() {
		return raProperties;
	}
	
	public ThreadPoolManager getReaderThreadPool(){
		return readerThreadPool;
	}
	
	public ThreadPoolManager getSessionPool() {
		return sessionPool;
	}

	public ThreadPoolManager getKeepAliveThreadPool() {
		return keepAliveThreadPool;
	}
	
	public SessionPermRecoveryTask getSessionPermRecoveryTask() {
		return sessionPermRecoveryTask;
	}

}
