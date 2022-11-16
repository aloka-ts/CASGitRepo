/**
 * 
 */
package com.baypackets.ase.ra.telnetssh.qm;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

import org.apache.log4j.Logger;

import com.baypackets.ase.common.AseBackgroundProcessor;
import com.baypackets.ase.common.BackgroundProcessListener;
import com.baypackets.ase.common.Registry;
import com.baypackets.ase.ra.telnetssh.ls.LsManager;
import com.baypackets.ase.ra.telnetssh.message.LsRequest;
import com.baypackets.ase.ra.telnetssh.utils.QStatus;
import com.baypackets.ase.util.Constants;

/**
 * The Class DequeTask.
 * implements Runnable
 * This class will run dequeue thread on Queue
 *
 * @author saneja
 */
public class DequeTask implements Runnable,BackgroundProcessListener{

	/** The Constant logger. */
	private static final Logger logger=Logger.getLogger(DequeTask.class);

	/** queue reference. */
	private List<LsQueue> lsQueueList;	

	private boolean isWaitState=false;
	/**
	 * Instantiates a new work manager.
	 *
	 */
	public DequeTask(int lsQueueLoggingPeriod) {
		lsQueueList=new ArrayList<LsQueue>();
		if(logger.isDebugEnabled()){
			logger.debug("Inside DequeTask() with lsQueueLoggingPeriod="+lsQueueLoggingPeriod+" secs");
		}
		if(lsQueueLoggingPeriod>-1){
		AseBackgroundProcessor processor = (AseBackgroundProcessor)Registry.lookup(Constants.BKG_PROCESSOR);
		processor.registerBackgroundListener(this, lsQueueLoggingPeriod);
		}
	}
	
	/** boolean to store if Q is empty*/
	private boolean isQsEmpty;

	/**
	 * Sets the ls queue list.
	 *
	 * @param lsQueueList the lsQueueList to set
	 */
	public void setLsQueueList(List<LsQueue> lsQueueList) {
		this.lsQueueList = lsQueueList;
	}

	/**
	 * Gets the ls queue list.
	 *
	 * @return the lsQueueList
	 */
	public List<LsQueue> getLsQueueList() {
		return lsQueueList;
	}

	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		if(logger.isInfoEnabled()){
			logger.info("Enter dequeue thread");
		}
		isQsEmpty=false;
		deQueueRequest();

//		QueueManager qm= QueueManagerImpl.getInstance();
//		Set<DequeTask> dequeueThreadSet=((QueueManagerImpl)qm).getDequeThreadSet();
//		synchronized (dequeueThreadSet) {
//			if(dequeueThreadSet.contains(this)){
//				dequeueThreadSet.remove(this);
//				if(logger.isDebugEnabled())
//					logger.debug("Removed thread from dequeue thread set");
//			}
//		}

		if(logger.isInfoEnabled()){
			logger.info("Leave dequeue thread");
		}
	}

	/**
	 * De queue thread will call this method 
	 * to dequeue.request and execute on LS
	 */
	private void deQueueRequest(){
		boolean isDebugEnabled=logger.isDebugEnabled();
		boolean isInfoEnabled=logger.isInfoEnabled();
		if(isInfoEnabled){
			logger.info("Enter deQueueRequest");
		}
		int ctr=0;
		LsManager lsManager=LsManager.getInstance();
		LsQueue queue=null;
		Queue<LsRequest> lsQ=null;
		LsRequest request=null;
		int lsId=-1;
		//alarm identifiaction params
		
		int emptyIndex=-1;
		
		
		while(lsQueueList!=null && !(lsQueueList.isEmpty())){
			try{
				if(ctr>=lsQueueList.size())
					ctr=0;
				//bug 7158 @saneja selective logging[
				if(ctr==emptyIndex){
					if(isInfoEnabled){
						logger.info("ctr matches empty index;cntr is["+ctr+"]");
					}
					synchronized (this) {
						isQsEmpty=true;
						StringBuilder statsBuiler=new StringBuilder();
						statsBuiler.append("QUEUE STAT [");
						for(LsQueue lsQueue:lsQueueList){
							synchronized (lsQueue) {
								isQsEmpty=(lsQueue.getLsQ().isEmpty() || 
										(lsQueue.getLsQ().peek().getStatus() !=LsRequest.REQUEST_PENDING) ) && isQsEmpty;
							}	
							statsBuiler.append(lsQueue.getLsId());
							statsBuiler.append(":");
							statsBuiler.append(lsQueue.getLsQ().size());
							statsBuiler.append(",");
						}
						statsBuiler.append("]");
						if(logger.isDebugEnabled()){
							logger.debug(statsBuiler.toString());
						}	
						if(isQsEmpty){
							try { 
								logger.warn("Going into Blocked state all Qs are empty or busy");
								isWaitState=true;
								this.wait();
								isWaitState=false;
								logger.warn("Deque Task notified so resuming again.");
							} catch (InterruptedException e) {
								if(isInfoEnabled)
									logger.info("Interrupted Exception on wait call",e);
							}
						}
					}//end sychronized
				}
				//]closed bug 7158@saneja selective logging

				queue=lsQueueList.get(ctr);

				lsId=queue.getLsId();
				if(isDebugEnabled){
					logger.debug("Dequeue on lsId ::" +lsId);
				}	

				synchronized (queue) {
					if((queue.getqStatus()==QStatus.Q_ACTIVE) ){
						//					LsCheck happens insend method and false is returned if Ls is free
						lsQ=queue.getLsQ();
						request=lsQ.peek();
					}
				}
				if(request == null || request.getStatus()!=LsRequest.REQUEST_PENDING){
					if(isDebugEnabled){
						logger.debug("Empty Queue or request in pending state  on lsId::[" +lsId + "] request::["+request+"]");
					}//saneja@bug7158[
					if(emptyIndex==-1){
						emptyIndex=ctr;
					}
					//]saneja@bug7158 closed
					ctr++;
					continue;
				}
				//saneja @bug 7158 reset setting Qsempty and index [
				isQsEmpty=false;
				emptyIndex=-1;
				//]closed saneja@bug 7158

				//			if(request.getStatus()!=LsRequest.REQUEST_PENDING){
				//				if(isDebugEnabled)
				//					logger.debug("Request not in pending state::" +lsId);
				//				ctr++;
				//				continue;
				//			}
				if(isDebugEnabled){
					logger.debug("Request found lsId ::" +lsId);
				}
				//			((LsRequestImpl) request).setStatus(LsRequest.REQUEST_ACTIVE);
				//lsManager.executeRequest(request);
				boolean status=lsManager.sendRequest(request);
				if(status){
					// on succes send increment request Impl copunter
					//				
				}
				ctr++;}
			catch(Exception e){
					logger.error("Exception occured in DequeTask: "+e.getMessage(),e);
			}
		}
		if(isInfoEnabled){
			logger.info("Leave deQueueRequest");
		}
	}

	/**
	 * removes Q from thread.
	 *
	 * @param lsQueue the ls queue
	 */
	public void deleteLsQ(LsQueue lsQueue){
		boolean isDebugEnabled=logger.isDebugEnabled();
		boolean isInfoEnabled=logger.isInfoEnabled();
		if(lsQueue==null){
			logger.error("Null input on LsInteractionTask deleteLsQ()");
			return;
		}
		if(isInfoEnabled)
			logger.info("Enter LsInteractionTask deleteLsQ() with lsId:: "+lsQueue.getLsId());
		synchronized (this) {
			if(lsQueueList.contains(lsQueue)){
				lsQueueList.remove(lsQueue);
				if(isDebugEnabled)
					logger.debug("Removed LsQueue from dequeue thread Queue List");
			}
		}	
		
		if(isInfoEnabled)
			logger.info("Leave LsInteractionTask deleteLsQ()");
	}

	/**
	 * @param isQsEmpty the isQsEmpty to set
	 */
	public void setQsEmpty(boolean isQsEmpty) {
		this.isQsEmpty = isQsEmpty;
	}

	/**
	 * @return the isQsEmpty
	 */
	public boolean isQsEmpty() {
		return isQsEmpty;
	}

	// Added method for printing LS QUEUE STATS on BackGroundProcessor
	@Override
	public void process(long arg0) {

		try{
			if(lsQueueList!=null && !(lsQueueList.isEmpty())){
				StringBuilder statsBuiler=new StringBuilder();
				synchronized (this) {
					String state=(isWaitState)?"Waiting":"Running";
					statsBuiler.append("DequeTask:["+state+"]");
					statsBuiler.append(" LS QUEU: [");
					for(LsQueue lsQueue:lsQueueList){
						statsBuiler.append(lsQueue.getLsId());
						statsBuiler.append(":");
						statsBuiler.append(lsQueue.getLsQ().size());
						statsBuiler.append(",");
					}
				}
				statsBuiler.append("]");
				logger.error(statsBuiler.toString()); 
			}
		}catch(Exception e){
			logger.error("Exception occured in process():"+e.getMessage(),e);
		}
	}
	
	public void destroy(){
		//AseBackgroundProcessor processor = (AseBackgroundProcessor)Registry.lookup(Constants.BKG_PROCESSOR);
		//processor.unregisterBackgroundListener(this);
	}
}
