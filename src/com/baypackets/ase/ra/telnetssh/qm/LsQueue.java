/**
 * 
 */
package com.baypackets.ase.ra.telnetssh.qm;

import java.util.LinkedList;
import java.util.Queue;

import com.baypackets.ase.ra.telnetssh.message.LsRequest;
import com.baypackets.ase.ra.telnetssh.utils.QStatus;

/**
 * The Class LSQueue. 
 * defines the queue and associated properties
 *
 * @author saneja
 */
public class LsQueue {

	/** The ls id. */
	private int lsId;

	/** The q size. */
	private int qSize;

	/** The q threshold. */
	private int qThreshold;

	/** The q status. */
	private QStatus qStatus;

	/** The ls q. */
	private Queue<LsRequest> lsQ;
	
	/**
	 * Instantiates a new lS queue.
	 *
	 * @param lsId the ls id
	 * @param qSize the q size
	 * @param qThreshold the q threshold
	 * @param qStatus the q status
	 */
	public LsQueue(int lsId, int qSize, int qThreshold, QStatus qStatus) {
		this.lsId=lsId;
		this.qSize = qSize;
		this.qThreshold = qThreshold;
		this.qStatus = qStatus;
		this.lsQ=new LinkedList<LsRequest>();

	}

	/**
	 * Sets the ls id.
	 *
	 * @param lsId the lsId to set
	 */
	public void setLsId(int lsId) {
		this.lsId = lsId;
	}

	/**
	 * Gets the ls id.
	 *
	 * @return the lsId
	 */
	public int getLsId() {
		return lsId;
	}

	/**
	 * Gets the q size.
	 *
	 * @return the qSize
	 */
	public int getQSize() {
		return qSize;

	}

	/**
	 * Sets the q size.
	 *
	 * @param qSize the qSize to set
	 */
	public void setQSize(int qSize) {
		this.qSize = qSize;
	}

	/**
	 * Gets the q threshold.
	 *
	 * @return the qThreshold
	 */
	public int getQThreshold() {
		return qThreshold;
	}

	/**
	 * Sets the q threshold.
	 *
	 * @param qThreshold the qThreshold to set
	 */
	public void setQThreshold(int qThreshold) {
		this.qThreshold = qThreshold;
	}

	/**
	 * Gets the q status.
	 *
	 * @return the qStatus
	 */
	public QStatus getqStatus() {
		return qStatus;
	}

	/**
	 * Sets the q status.
	 *
	 * @param qStatus the qStatus to set
	 */
	public void setqStatus(QStatus qStatus) {
		this.qStatus = qStatus;
	}

	/**
	 * Gets the ls q.
	 *
	 * @return the lsQ
	 */
	public Queue<LsRequest> getLsQ() {
		return lsQ;
	}

	/**
	 * Sets the ls q.
	 *
	 * @param lsQ the lsQ to set
	 */
	public void setLsQ(Queue<LsRequest> lsQ) {
		this.lsQ = lsQ;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		LsQueue other = (LsQueue) obj;
		if(this.lsId == other.lsId)
			return true;
		else
			return false;

	}



}
