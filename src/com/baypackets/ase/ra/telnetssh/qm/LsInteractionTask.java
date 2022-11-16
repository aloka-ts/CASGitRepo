/**
 * 
 */
package com.baypackets.ase.ra.telnetssh.qm;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.Set;

import org.apache.log4j.Logger;

import com.baypackets.ase.ra.telnetssh.LsRaAlarmManager;
import com.baypackets.ase.ra.telnetssh.ls.LsManager;
import com.baypackets.ase.ra.telnetssh.message.LsRequest;
import com.baypackets.ase.ra.telnetssh.message.LsRequestImpl;
import com.baypackets.ase.ra.telnetssh.utils.QStatus;

/**
 * The Class LsInteractionTask.
 * implements Runnable
 * This class will run dequeue thread on Queue
 *
 * @author saneja
 */
public class LsInteractionTask implements Runnable {

	/** The Constant logger. */
	private static final Logger logger=Logger.getLogger(LsInteractionTask.class);

	/** queue reference. */
	private List<LsQueue> lsQueueList;	

	/**
	 * Instantiates a new work manager.
	 *
	 */
	public LsInteractionTask() {
		lsQueueList=new ArrayList<LsQueue>();
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
		if(logger.isInfoEnabled())
			logger.info("Enter dequeue thread");
		isQsEmpty=false;
		deQueueRequest();

		QueueManager qm= QueueManagerImpl.getInstance();
		Set<LsInteractionTask> dequeueThreadSet=((QueueManagerImpl)qm).getDequeThreadSet();
		synchronized (dequeueThreadSet) {
			if(dequeueThreadSet.contains(this)){
				dequeueThreadSet.remove(this);
				if(logger.isDebugEnabled())
					logger.debug("Removed thread from dequeue thread set");
			}
		}

		if(logger.isInfoEnabled())
			logger.info("Leave dequeue thread");
	}

	/**
	 * De queue thread will call this method 
	 * to dequeue.request and execute on LS
	 */
	private void deQueueRequest(){
		boolean isDebugEnabled=logger.isDebugEnabled();
		boolean isInfoEnabled=logger.isInfoEnabled();
		if(isInfoEnabled)
			logger.info("Enter deQueueRequest");
		int ctr=0;
		LsManager lsManager=LsManager.getInstance();
		LsQueue queue=null;
		Queue<LsRequest> lsQ=null;
		LsRequest request=null;
		int lsId=-1;
		//alarm identifiaction params
		int qOccupancy=0;
		int qSize=0;
		int qThreshold=0;
		int qThresholdVal=0;
		int qFilledPercentage=0;
		int emptyIndex=-1;
		
		
		while(lsQueueList!=null && !(lsQueueList.isEmpty())){
			if(ctr>=lsQueueList.size())
				ctr=0;
			//bug 7158 @saneja [
			if(ctr==emptyIndex){
				synchronized (this) {
					isQsEmpty=true;
					for(LsQueue lsQueue:lsQueueList){
						isQsEmpty=lsQueue.getLsQ().isEmpty() && isQsEmpty;
					}
					if(isQsEmpty){
						try {
							if(isDebugEnabled)
								logger.debug("Going into Blocked state all Qs are empty");
							this.wait();
						} catch (InterruptedException e) {
							if(isInfoEnabled)
								logger.info("Interrupted Exception on wait call",e);
						}
					}
				}//end sychronized
			}
			//]closed bug 7158@saneja
			try{
				queue=lsQueueList.get(ctr);
			}catch (IndexOutOfBoundsException ex){
				logger.error("Indexout of bound error, Ignore");
				ctr++;
				continue;
			}
			lsId=queue.getLsId();
			if(isDebugEnabled)
				logger.debug("Dequeue on lsId ::" +lsId);
			qSize=queue.getQSize();
			qThreshold=queue.getQThreshold();
			qThresholdVal=(int)((qSize*qThreshold)/100);
			synchronized (queue) {
				if((queue.getqStatus()==QStatus.Q_ACTIVE) ){
					lsQ=queue.getLsQ();
					request=lsQ.poll();
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
			if(request == null){
				if(isDebugEnabled)
					logger.debug("Empty Queue on lsId ::" +lsId);
				//saneja@bug7158[
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
			
			if(request.getStatus()!=LsRequest.REQUEST_PENDING){
				if(isDebugEnabled)
					logger.debug("Request not in pending state::" +lsId);
				ctr++;
				continue;
			}
			if(isDebugEnabled)
				logger.debug("Request found lsId ::" +lsId);
			((LsRequestImpl) request).setStatus(LsRequest.REQUEST_ACTIVE);
			lsManager.executeRequest(request);
			ctr++;
		}
		if(isInfoEnabled)
			logger.info("Leave deQueueRequest");
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
		if(lsQueueList.contains(lsQueue)){
			lsQueueList.remove(lsQueue);
			if(isDebugEnabled)
				logger.debug("Removed LsQueue from dequeue thread Queue List");
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

}
