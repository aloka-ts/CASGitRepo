package com.baypackets.ase.ra.enumserver.qm;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import com.baypackets.ase.ra.enumserver.message.EnumMessage;

//import com.baypackets.ase.ra.enum.utils.QStatus;

public class EnumQueue {

	/** The q size. */
	private int qSize;

	/** The q threshold. */
	private int qThreshold;

	/** The ls q. */
	private BlockingQueue<EnumMessage> enumQ;

	public EnumQueue() {
		this.enumQ = new LinkedBlockingQueue<EnumMessage>();
	}

	public int getqSize() {
		return qSize;
	}

	public void setqSize(int qSize) {
		this.qSize = qSize;
	}

	public int getqThreshold() {
		return qThreshold;
	}

	public void setqThreshold(int qThreshold) {
		this.qThreshold = qThreshold;
	}

	public BlockingQueue<EnumMessage> getEnumQ() {
		return enumQ;
	}

	public void setEnumQ(BlockingQueue<EnumMessage> enumQ) {
		this.enumQ = enumQ;
	}

}
