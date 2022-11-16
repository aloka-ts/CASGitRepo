/**
 * 
 */
package com.baypackets.ase.ra.telnetssh.qm;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import org.apache.log4j.Logger;

import com.baypackets.ase.ra.telnetssh.LsRaAlarmManager;
import com.baypackets.ase.ra.telnetssh.LsResourceAdaptor;
import com.baypackets.ase.ra.telnetssh.configmanager.LS;
import com.baypackets.ase.ra.telnetssh.event.LsResourceEvent;
import com.baypackets.ase.ra.telnetssh.ls.LsManager;
import com.baypackets.ase.ra.telnetssh.ls.LsSession;
import com.baypackets.ase.ra.telnetssh.message.LsRequest;
import com.baypackets.ase.ra.telnetssh.message.LsRequestImpl;
import com.baypackets.ase.ra.telnetssh.session.LsResourceSession;
import com.baypackets.ase.ra.telnetssh.utils.LsStatus;
import com.baypackets.ase.ra.telnetssh.utils.QStatus;
import com.baypackets.ase.ra.telnetssh.workmanager.ThreadPool;
import com.baypackets.ase.ra.telnetssh.workmanager.ThreadPoolManager;
import com.baypackets.ase.resource.ResourceException;

/**
 * The class QueueManagerImpl
 * implements QueueManager interface.
 *
 * @author saneja
 */
public class QueueManagerImpl implements QueueManager{

	/** The Constant logger. */
	private static final Logger logger=Logger.getLogger(QueueManagerImpl.class);

	/** The lsId and Lsqueue mapping. */
	private Map<Integer,LsQueue> lsIdQueueMap;
	
//	/** The ls id de queue thread map. LsId is key and deque thread as value */
//	private Map<Integer,LsInteractionTask> lsIdDeQueueThreadMap;
//	
//	/** The deque thread set. */
//	private Set<LsInteractionTask> dequeThreadSet;
	
	/** task responsible for deque of LS */
	private DequeTask dequeueTask;
	
	/** The ls resource adaptor. */
	private LsResourceAdaptor lsResourceAdaptor;

	/** The que manager instance for singleton. */
	private static QueueManager queManager;

	/** The thread pool for deque task. */
	private ThreadPoolManager threadPool;

	/** The de queue thread load factor. */
	private int deQueueThreadLoadFactor;

	//creating this class as singleton

	/**
	 * Instantiates a new queue manager impl.
	 */
	private QueueManagerImpl(){
		lsIdQueueMap=new HashMap<Integer, LsQueue>();
		
	}

	/**
	 * Gets the single instance of QueueManagerImpl.
	 *
	 * @return single instance of QueueManagerImpl
	 */
	public static synchronized QueueManager getInstance(){
		if(queManager==null)
			queManager=new QueueManagerImpl();
		return queManager;
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

//	/**
//	 * Gets the ls id de queue thread map.
//	 *
//	 * @return the lsIdDeQueueThreadMap
//	 */
//	public Map<Integer, LsInteractionTask> getLsIdDeQueueThreadMap() {
//		return lsIdDeQueueThreadMap;
//	}
	
	/**
	 * Sets the de queue thread load factor.
	 *
	 * @param deQueueThreadLoadFactor the deQueueThreadLoadFactor to set
	 */
	public void setDeQueueThreadLoadFactor(int deQueueThreadLoadFactor) {
		this.deQueueThreadLoadFactor = deQueueThreadLoadFactor;
	}

	/**
	 * Gets the de queue thread load factor.
	 *
	 * @return the deQueueThreadLoadFactor
	 */
	public int getDeQueueThreadLoadFactor() {
		return deQueueThreadLoadFactor;
	}

//	/**
//	 * Gets the deque thread set.
//	 *
//	 * @return the dequeThreadSet
//	 */
//	public Set<LsInteractionTask> getDequeThreadSet() {
//		return dequeThreadSet;
//	}

	/**
	 * Gets the ls id queue map.
	 *
	 * @return the lsIdQueueMap
	 */
	public Map<Integer, LsQueue> getLsIdQueueMap() {
		return lsIdQueueMap;
	}
	
	/**
	 * Loads the Q Manager called on RA start up
	 * Flow:
	 * Creates cacahe thread pool for Deque threads
	 * Reads LS list and craetes LS Q.
	 * 	Adds LS Q to deque thread.
	 * 	Creates new deque thread in  pool if existing threads load exceeds max value 
	 * 
	 *  (non-Javadoc)
	 * @see com.baypackets.ase.ra.telnetssh.qm.QueueManager#init(java.util.List)
	 */
	@Override
	public void load(Collection<LS> lsCollection, int deQueueThreadLoadFactor,int lsQueueLoggingPeriod,LsResourceAdaptor lsResourceAdaptor) {
		boolean isDebugEnabled=logger.isDebugEnabled();
		boolean isInfoEnabled=logger.isInfoEnabled();
		if(isInfoEnabled)
			logger.info("Inside QueueManager Init()->Initializing Queue Manager");	
		LsQueue lsQueue;
		dequeueTask=new DequeTask(lsQueueLoggingPeriod);
		Iterator<LS> lsIterator=lsCollection.iterator();
		setDeQueueThreadLoadFactor(deQueueThreadLoadFactor);
		this.lsResourceAdaptor=lsResourceAdaptor;
		threadPool=new ThreadPool("ls-queue-mgr-pool",1);
		threadPool.startPool();		
		while(lsIterator.hasNext()){
			LS ls=lsIterator.next();
			lsQueue=new LsQueue(ls.getLsId(), ls.getLsQSize(), ls.getLsQThreshold(), QStatus.Q_ACTIVE);
			lsIdQueueMap.put(Integer.valueOf(ls.getLsId()), lsQueue);
			dequeueTask.getLsQueueList().add(lsQueue);
			if(isDebugEnabled)
				logger.debug("Queue and worker thread created for lsID:"+ls.getLsId());
		}
		threadPool.submitTask(dequeueTask);
		if(isDebugEnabled)
			logger.debug("Queues created. QueueMap is:"+lsIdQueueMap );
		if(isInfoEnabled)
			logger.info("Leaving QueueManager Init()->Initialization Complete");
	}

	/**
	 * destrys Q manager cleans delets deue threads and cleans Qs
	 *  (non-Javadoc)
	 * @see com.baypackets.ase.ra.telnetssh.qm.QueueManager#destroy()
	 */
	@Override
	public void destroy() {
		boolean isDebugEnabled=logger.isDebugEnabled();
		boolean isInfoEnabled=logger.isInfoEnabled();
		if(isInfoEnabled)
			logger.info("Inside QueueManager destroy()->destroy Queue Manager");	
		LsQueue lsQueue;
		Set<Integer> lsIdSet =lsIdQueueMap.keySet();
		Iterator<Integer> lsIdIterator=lsIdSet.iterator();
		while(lsIdIterator.hasNext()){		
			Integer lsId=lsIdIterator.next();
			lsQueue=lsIdQueueMap.get(lsId);
			lsQueue.setqStatus(QStatus.Q_DELETED);
			dequeueTask.deleteLsQ(lsQueue);
			if(!(lsQueue.getLsQ().isEmpty())){
				cleanQ(lsQueue);
			}
			lsQueue.setLsQ(null);
			lsIdIterator.remove();
			if(isDebugEnabled)
				logger.debug("Queue and worker thread deleted for lsID:"+lsId);
		}
	    dequeueTask.destroy();
		if(isDebugEnabled)
			logger.debug("Queues destroyed. QueueMap is:"+lsIdQueueMap );
		//shutting down executor 
		threadPool.stopPool();
		if(isDebugEnabled)
			logger.debug("Leaving QueueManager destroy()->destroy complete");
	}

	
	/**
	 * Add new Q to Qmanager
	 *  (non-Javadoc)
	 * @see com.baypackets.ase.ra.telnetssh.qm.QueueManager#addQ(com.baypackets.ase.ra.telnetssh.configmanager.LS)
	 */
	@Override
	public boolean addQ(LS ls) {
		boolean isDebugEnabled=logger.isDebugEnabled();
		boolean isInfoEnabled=logger.isInfoEnabled();
		if(ls == null ){
			logger.error("Error: Oops!!! Null Input");
			return false;
		}
		if(isInfoEnabled)
			logger.info("Inside QueueManager addQ()->Adding new Queue for LS id: "+ls.getLsId());	
		LsQueue lsQueue;
//		boolean newThread=true;
		lsQueue=lsIdQueueMap.get(Integer.valueOf(ls.getLsId()));
		if(lsQueue!=null){
			logger.error("Error: Oops!!! LS Already exists for LS Id:"+ls.getLsId());
			return false;
		}
		lsQueue=new LsQueue(ls.getLsId(), ls.getLsQSize(), ls.getLsQThreshold(), QStatus.Q_ACTIVE);
		lsIdQueueMap.put(Integer.valueOf(ls.getLsId()), lsQueue);
		synchronized (dequeueTask) {
			dequeueTask.getLsQueueList().add(lsQueue);
		}
		if(isDebugEnabled)
			logger.debug("Queue and dequeue task created for lsID:"+ls.getLsId()+
					" QueueMap is-->"+lsIdQueueMap );
		if(isInfoEnabled)
			logger.info("Leaving QueueManager addQ()->Addition Complete");
		return true;
	}

	/**
	 * Deletes and cleans Q. remove Q from deque thread. 
	 * hard stop is not supported
	 *  (non-Javadoc)
	 * @see com.baypackets.ase.ra.telnetssh.qm.QueueManager#deleteQ(com.baypackets.ase.ra.telnetssh.configmanager.LSParam)
	 */
	@Override
	public boolean deleteQ(LS ls) {
		boolean isDebugEnabled=logger.isDebugEnabled();
		boolean isInfoEnabled=logger.isInfoEnabled();
		if(ls == null ){
			logger.error("Error: Oops!!! input is null");
			return false;
		}
		if(isInfoEnabled)
			logger.info("Inside QueueManager deleteQ()->Deleting Queue for LS id: "+ls.getLsId());	
		LsQueue lsQueue;
		
		lsQueue=lsIdQueueMap.get(Integer.valueOf(ls.getLsId()));
		if(lsQueue==null){
			logger.error("Error: Oops!!! Queue and thread does not exist for LS Id:"+ls.getLsId());
			return false;
		}
		lsQueue.setqStatus(QStatus.Q_DELETED);
		dequeueTask.deleteLsQ(lsQueue);
		if(!(lsQueue.getLsQ().isEmpty())){
			cleanQ(lsQueue);
		}
		lsQueue.setLsQ(null);
		lsIdQueueMap.remove(ls.getLsId());
		

		if(isDebugEnabled)
			logger.debug("Queue and worker thread deleted for lsID:"+ls.getLsId()+
					" QueueMap is-->"+lsIdQueueMap);
		if(isInfoEnabled)
			logger.info("Leaving QueueManager deleteQ()->Deletion Complete");
		return true;
	}

	/* (non-Javadoc)
	 * @see com.baypackets.ase.ra.telnetssh.qm.QueueManager#updateQSize(com.baypackets.ase.ra.telnetssh.configmanager.LS)
	 */
	@Override
	public boolean updateQSize(LS ls) {
		boolean isDebugEnabled=logger.isDebugEnabled();
		boolean isInfoEnabled=logger.isInfoEnabled();
		if(ls == null || ls.getLsQSize()<=0){
			logger.error("Error: Oops!!! Null input or new Qsize less than or equal to 0:"+ls);
			return false;
		}
		if(isInfoEnabled)
			logger.info("Inside QueueManager updateQSize()->updating Queue Size");	
		LsQueue lsQueue;
		lsQueue=lsIdQueueMap.get(Integer.valueOf(ls.getLsId()));
		if(lsQueue==null){
			logger.error("Error: Oops!!! Queue and thread does not exist for LS Id:"+ls.getLsId());
			return false;
		}
		lsQueue.setQSize(ls.getLsQSize());
		if(isDebugEnabled)
			logger.debug("Queue Size updated for lsID:"+ls.getLsId());
		if(isInfoEnabled)
			logger.info("Leaving QueueManager updateQsize()->updation Complete");
		return true;
	}

	/* (non-Javadoc)
	 * @see com.baypackets.ase.ra.telnetssh.qm.QueueManager#updateQThreshold(com.baypackets.ase.ra.telnetssh.configmanager.LS)
	 */
	@Override
	public boolean updateQThreshold(LS ls) {
		boolean isDebugEnabled=logger.isDebugEnabled();
		boolean isInfoEnabled=logger.isInfoEnabled();
		if(ls == null || ls.getLsQThreshold()<=0 || ls.getLsQThreshold()>100 ){
			logger.error("Error: Oops!!! null input or new Qthreshold less than or equal to 0 or " +
					"new Qthreshold greater than to 100 :"+ls);
			return false;
		}
		if(isInfoEnabled)
			logger.info("Inside QueueManager updateQThreshold()->updating Queue Threshold");	
		LsQueue lsQueue;
		lsQueue=lsIdQueueMap.get(Integer.valueOf(ls.getLsId()));
		if(lsQueue==null){
			logger.error("Error: Oops!!! Queue and thread does not exist for LS Id:"+ls.getLsId());
			return false;
		}
		lsQueue.setQThreshold(ls.getLsQThreshold());
		if(isDebugEnabled)
			logger.debug("Queue Threshold updated for lsID:"+ls.getLsId());
		if(isInfoEnabled)
			logger.info("Leaving QueueManager updateQThreshold()->updation Complete");
		return true;
	}

	/**
	 * enques request to Qmanager
	 * Flow:
	 * fetch LSid for request
	 * check if Q or LsConnection doesn't exist if yes return with status false
	 * check on if Q status deleted or LS down if yes return with status false
	 * Check if Q is not full add request
	 * check if q becomes full raise alarm
	 * Check if q occupancy has exceeded threshold raise alarm
	 * 
	 *  (non-Javadoc)
	 * @see com.baypackets.ase.ra.telnetssh.qm.QueueManager#enQueueRequeuet(com.baypackets.ase.ra.telnetssh.message.TelnetSshRequest)
	 */
	@Override
	public boolean enQueueRequest(LsRequest request) throws ResourceException {
		boolean isDebugEnabled=logger.isDebugEnabled();
		boolean isInfoEnabled=logger.isInfoEnabled();
		if(isInfoEnabled)
			logger.info("Inside QueueManager enQueueRequest()  request::"+request +" lsId::"+request.getLsId());	
		int lsId=request.getLsId();
		LsQueue lsQueue=lsIdQueueMap.get(Integer.valueOf(lsId));
		LsSession lsSession=LsManager.getInstance().getLsIdSessionMap().get(Integer.valueOf(lsId));
		Queue<LsRequest> lsQ=null;
		boolean requestAdded=false;
		int qOccupancy=0;
		int qSize=0;
		int qThreshold=0;
		int qThresholdVal=0;
		int qFilledPercentage=0;
		if(lsQueue == null || lsSession==null){
			logger.error("Null Queue or session   on lsID::["+lsId+"]  and QueueMap is" +lsIdQueueMap +
					" and Session MAp is::"+LsManager.getInstance().getLsIdSessionMap() +" appsession::"+request.getApplicationSession());
			LsResourceEvent resourceEvent= new LsResourceEvent(request, LsResourceEvent.REQUEST_FAIL_EVENT, request.getApplicationSession());
			resourceEvent.setMessage(request);
			lsResourceAdaptor.deliverEvent(resourceEvent);
			return false;
		}
		//to decide alarm
		qSize=lsQueue.getQSize();
		qThreshold=lsQueue.getQThreshold();
		qThresholdVal=(int)((qSize*qThreshold)/100);
		synchronized (lsQueue) {
			if( lsQueue.getqStatus()== QStatus.Q_DELETED ||
					lsSession.getLsStatus()== LsStatus.LS_DOWN){
				logger.error("Ls down or Queue deleted lsId::"+lsId);
				LsResourceEvent resourceEvent= new LsResourceEvent(request, LsResourceEvent.REQUEST_FAIL_EVENT, request.getApplicationSession());
				resourceEvent.setMessage(request);
				lsResourceAdaptor.deliverEvent(resourceEvent);
				return false;
			}
			lsQ=lsQueue.getLsQ();
			//alarm decision
			qOccupancy=lsQ.size();
			if(qOccupancy<qSize){
				lsQ.add(request);
				requestAdded=true;
				if(isDebugEnabled)
					logger.debug("request added to queue");
				if((qOccupancy+1) == qSize) {
					qFilledPercentage= (((qOccupancy+1)*100)/qSize);
					if(isDebugEnabled)
						logger.debug("Raising Q full alarm for lsId::"+lsId+
								" currentOccupancy::"+qFilledPercentage);
					String alarmMsg="LsId:["+lsId+"]  Queue Occupancy:["+qFilledPercentage+"]";
					LsRaAlarmManager.getInstance().raiseAlarm(LsRaAlarmManager.QUEUE_OVERFLOW,lsId,alarmMsg);
				}else if((qOccupancy < qThresholdVal) && ((qOccupancy+1) >= qThresholdVal)){
					qFilledPercentage= (((qOccupancy+1)*100)/qSize);
					if(isDebugEnabled)
						logger.debug("Raising nearing overflow alarm for lsId::"+lsId+
								" currentOccupancy::"+qFilledPercentage);
					String alarmMsg="LsId:["+lsId+"]  Queue Occupancy:["+qFilledPercentage+"]";
					LsRaAlarmManager.getInstance().raiseAlarm(LsRaAlarmManager.QUEUE_NEARING_OVERFLOW,lsId,alarmMsg);
				}
			}
		}
		if(!requestAdded){
			logger.error("unable to addrequest to queue, Queue is already full");
			LsResourceEvent resourceEvent= new LsResourceEvent(request, LsResourceEvent.QUEUE_FULL, request.getApplicationSession());
			resourceEvent.setMessage(request);
			lsResourceAdaptor.deliverEvent(resourceEvent);
			return false;
		}else{
			//saneja@bug 7158 notify waiting deque thread on Q[
			notifyDequeueTask(request);
		//]bug 7158 closed @saneja	
		}
		if(isInfoEnabled)
			logger.info("Leaving QueueManager enQueueRequest()");	
		return true;
	}

	/**
	 * removes request form the Q called in case of Cancel
	 *  (non-Javadoc)
	 * @see com.baypackets.ase.ra.telnetssh.qm.QueueManager#removeRequeuet(com.baypackets.ase.ra.telnetssh.message.TelnetSshRequest)
	 */
	@Override
	public boolean removeRequest(LsRequest request) {
		boolean isDebugEnabled=logger.isDebugEnabled();
		boolean isInfoEnabled=logger.isInfoEnabled();
		if(request==null){
			logger.error("Input request is null");
			return false;
		}
		if(isInfoEnabled)
			logger.info("Inside QueueManager removeRequest() lsId::"+request.getLsId()+
					" Command" + request.getLsCommand());	
		int lsId=request.getLsId();
		LsQueue lsQueue=lsIdQueueMap.get(Integer.valueOf(lsId));
		Queue<LsRequest> lsQ=null;
		boolean deleted=false;
		if(lsQueue==null){
			logger.error("LSQ for request not found");
			return false;
		}
		synchronized (lsQueue) {
			lsQ=lsQueue.getLsQ();
			if(lsQ!=null && request.getStatus()==LsRequest.REQUEST_PENDING && lsQ.contains(request)){
				lsQ.remove(request);
				deleted=true;
				((LsRequestImpl) request).setStatus(LsRequest.REQUEST_INACTIVE);
				((LsResourceSession) request.getSession()).setSessionState(LsResourceSession.LS_INACTIVE);
				if(isDebugEnabled)
					logger.debug("Removed LsRequest from LsQueue on cancel");
			}else{
				logger.error("Q null or Request not pending or Request not found-->lsQ::["+lsQ
						+"]  Request Status::["+ request.getStatus() +"] ");
			}
		}
		if(isInfoEnabled)
			logger.info("Leaving QueueManager removeRequest() delete status::"+ deleted);	
		return deleted;
	}

	/**
	 * Empties the requests in q with request fail event as call back
	 *  (non-Javadoc)
	 * @see com.baypackets.ase.ra.telnetssh.qm.QueueManager#cleanQ(int)
	 */
	@Override
	public void cleanQ(LsQueue lsQueue) {
		boolean isDebugEnabled=logger.isDebugEnabled();
		boolean isInfoEnabled=logger.isInfoEnabled();
		if(isInfoEnabled)
			logger.info("Inside QueueManager cleanQ() lsId::"+lsQueue.getLsId());
		Queue<LsRequest> lsQ=null;
		LsRequest[] lsRequestarray=null;
		synchronized (lsQueue) {
			lsQ=lsQueue.getLsQ();
			lsRequestarray=new LsRequest[lsQ.size()];
			lsRequestarray=lsQ.toArray(lsRequestarray);
			lsQ.clear();
		}
		if(isDebugEnabled)
			logger.debug("After Synchronized block with lsrequestArray::"+lsRequestarray);
		for(LsRequest lsRequest:lsRequestarray){
			try {
				LsResourceEvent resourceEvent= new LsResourceEvent(lsRequest, LsResourceEvent.REQUEST_FAIL_EVENT, lsRequest.getApplicationSession());
				resourceEvent.setMessage(lsRequest);
				lsResourceAdaptor.deliverEvent(resourceEvent);
			} catch (ResourceException e) {
				logger.error("Exception sending Request_fail_event on clean",e);
			}
		}
		if(isInfoEnabled)
			logger.info("Leaving QueueManager cleanQ()");		
	}

	/**
	 * updats LSid local refernces in LSQ and maps.
	 *  (non-Javadoc)
	 * @see com.baypackets.ase.ra.telnetssh.qm.QueueManager#upddateLsId(int, int)
	 */
	@Override
	public boolean updateLsId(int oldLsId, int newLsId) {
		boolean isDebugEnabled=logger.isDebugEnabled();
		boolean isInfoEnabled=logger.isInfoEnabled();
		if(isInfoEnabled)
			logger.info("upddateLsId()::Updating lsId oldId::"+oldLsId+"  newId::"+newLsId);
		boolean updateStatus=false;
		LsQueue lsQueue=lsIdQueueMap.get(Integer.valueOf(oldLsId));
		
		if(lsQueue==null || dequeueTask ==null){
			logger.error("Update failed::null Queue or dequeue task Queue::"+lsQueue+
					"  Task::"+dequeueTask +"   Qmap"+lsIdQueueMap);
			updateStatus=false;
			return updateStatus;
		}
		if(isDebugEnabled)
			logger.debug("update lsId in LsQueue oldId::"+oldLsId+"  newId::"+newLsId);
		lsQueue.setLsId(newLsId);
		if(isDebugEnabled)
			logger.debug("remove old lsId as key on qmap and task map oldId::"+oldLsId+"  newId::"+newLsId);
		lsIdQueueMap.remove(Integer.valueOf(oldLsId));
		
		if(isDebugEnabled)
			logger.debug("add new  lsId as key on qmap and task map oldId::"+oldLsId+"  newId::"+newLsId);
		lsIdQueueMap.put(Integer.valueOf(newLsId),lsQueue);
		
		updateStatus=true;
		
		if(isInfoEnabled)
			logger.info("upddateLsId()::return with status:;"+updateStatus);
		return updateStatus;
	}

	/* (non-Javadoc)
	 * @see com.baypackets.ase.ra.telnetssh.qm.QueueManager#pollRequest(com.baypackets.ase.ra.telnetssh.message.LsRequest)
	 */
	@Override
	public boolean pollRequest(LsRequest request) {
		boolean isDebugEnabled=logger.isDebugEnabled();
		boolean isInfoEnabled=logger.isInfoEnabled();
		if(request==null){
			logger.error("Input request is null");
			return false;
		}
		if(isInfoEnabled)
			logger.info("Inside QueueManager pollRequest() lsId::"+request.getLsId()+
					" Command" + request.getLsCommand());	
		int lsId=request.getLsId();
		LsQueue lsQueue=lsIdQueueMap.get(Integer.valueOf(lsId));
		Queue<LsRequest> lsQ=null;
		boolean pollStatus=false;
		if(lsQueue==null){
			logger.error("LSQ for request not found");
			return false;
		}
		
		
		int qOccupancy=0;
		int qThresholdVal=0;
		int qFilledPercentage=0;
		int qSize=0;
		int qThreshold=0;
		
		synchronized (lsQueue) {
			if((lsQueue.getqStatus()==QStatus.Q_ACTIVE) ){
				lsQ=lsQueue.getLsQ();
				//XXX
				lsQ.poll();
//				lsQ.remove(request);
				pollStatus=true;
				
				qSize=lsQueue.getQSize();
				qThreshold=lsQueue.getQThreshold();
				qThresholdVal=(int)((qSize*qThreshold)/100);
				//alarm identifiaction
				qOccupancy=lsQ.size();
				if((qOccupancy+1)==qSize){
					if(isDebugEnabled)
						logger.debug("Raising Q full Alarm");
					qFilledPercentage= (((qOccupancy)*100)/qSize);
					String alarmMsg="LsId:["+lsId+"]  Queue Occupancy:["+qFilledPercentage+"]";
					LsRaAlarmManager.getInstance().raiseAlarm(LsRaAlarmManager.CLEAR_QUEUE_OVERFLOW,lsId,alarmMsg);
				}else if((qOccupancy < qThresholdVal) && ((qOccupancy+1)>=qThresholdVal)){
					if(isDebugEnabled)
						logger.debug("Raising Q near overflow Alarm");
					qFilledPercentage= (((qOccupancy)*100)/qSize);
					String alarmMsg="LsId:["+lsId+"]  Queue Occupancy:["+qFilledPercentage+"]";
					LsRaAlarmManager.getInstance().raiseAlarm(LsRaAlarmManager.CLEAR_QUEUE_NEARING_OVERFLOW,lsId,alarmMsg);
				}
			}
		}
		
		notifyDequeueTask(request);
		
		return pollStatus;
	}

	/* (non-Javadoc)
	 * @see com.baypackets.ase.ra.telnetssh.qm.QueueManager#NotifyDequeue(com.baypackets.ase.ra.telnetssh.message.LsRequest)
	 */
	@Override
	public boolean notifyDequeueTask(LsRequest request) {
		//saneja@bug 7158 notify waiting deque thread on Q[
		synchronized (dequeueTask) {
			boolean isQsEmpty=dequeueTask.isQsEmpty();
			if(isQsEmpty){
				dequeueTask.setQsEmpty(false);
				dequeueTask.notifyAll();
			}
		}
	//]bug 7158 closed @saneja	
		return true;
	}

}
