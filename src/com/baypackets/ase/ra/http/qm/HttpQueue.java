package com.baypackets.ase.ra.http.qm;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import com.baypackets.ase.ra.http.message.HttpRequest;
//import com.baypackets.ase.ra.http.utils.QStatus;
import com.esotericsoftware.kryo.DefaultSerializer;
import com.esotericsoftware.kryo.serializers.ExternalizableSerializer;

@DefaultSerializer(ExternalizableSerializer.class)
public class HttpQueue{

	/** The q size. */
	private int qSize;

	/** The q threshold. */
	private int qThreshold;

	/** The q status. */
	//private QStatus qStatus;

	/** The ls q. */
	private BlockingQueue<HttpRequest> httpQ;
	
	public HttpQueue(){
		this.httpQ = new LinkedBlockingQueue<HttpRequest>();
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

	public BlockingQueue<HttpRequest> getHttpQ() {
		return httpQ;
	}

	public void setHttpQ(BlockingQueue<HttpRequest> httpQ) {
		this.httpQ = httpQ;
	}
	
	
	
}
