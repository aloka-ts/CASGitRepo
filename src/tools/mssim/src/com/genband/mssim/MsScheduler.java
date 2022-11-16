
package com.genband.mssim;

import java.util.LinkedList;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

public class MsScheduler {
	private static Logger logger = Logger.getLogger(MsScheduler.class);

	private static Map m_schedulers = new HashMap(7);

	private LinkedList m_list;
	private int m_latency;
	private Object m_waitObject;

	private boolean m_isWaiting = false;

	private MsScheduler(int latency) {
		m_latency = latency;
		if(m_latency > 0) {
			m_list = new LinkedList();
			m_waitObject = new Object();
			(new SchedulerThread()).start();
		}
	}

	public static MsScheduler getScheduler(int delay) {
		Object key = new Integer(delay);
		MsScheduler sch = null;

		synchronized(m_schedulers) {
			sch = (MsScheduler)m_schedulers.get(key);
			if(sch == null) {
				sch = new MsScheduler(delay);
				m_schedulers.put(key, sch);
			}
		}

		return sch;
	}

	public void submit(Object obj) {
		logger.debug("In submit()");

		if(m_latency > 0) {
			synchronized(m_waitObject) {
				m_list.add(new Item(obj));
				if(m_isWaiting) {
					m_waitObject.notifyAll();
				}
			}
		} else {
			logger.debug("Submitting task to threadpool synchronously");
			MsSimulator.getThreadPool().submit(obj);
		}
	}

	private class SchedulerThread extends Thread {
		public SchedulerThread() {
			super("MsScheduler-" + m_latency + "s");
		}

		public void run() {
			try {
				while(true) {
					Item item = null;
					synchronized(m_waitObject) {
						if(m_list.isEmpty()) {
							// Wait for a new task to come
							try {
								m_isWaiting = true;
								m_waitObject.wait();
								m_isWaiting = false;
							} catch(InterruptedException exp) {
								logger.error("Waiting for new task", exp);
							}

							continue;
						} else {
							item = (Item)m_list.removeFirst();
						}
					}

					long waitTime = item.getExpiry() - System.currentTimeMillis();

					// Wait if more than 10 ms are remaining
					if(waitTime > 10) {
						if(logger.isInfoEnabled()) {
							logger.info("Waiting for " + waitTime + " ms");
						}
						try {
							synchronized(m_waitObject) {
								m_waitObject.wait(waitTime);
							}
						} catch(InterruptedException exp) {
							logger.error("Waiting for task expiry time", exp);
						}
					}

					logger.debug("Submitting task to threadpool");
					MsSimulator.getThreadPool().submit(item.getObject());
				}
			} catch(Throwable t) {
				logger.error("Caught throwable at thread level", t);
			}
		} // run
	}

	private class Item {
		private Object _obj;
		private long _expiry;

		public Item(Object obj) {
			_obj = obj;
			_expiry = System.currentTimeMillis() + m_latency*1000;
		}

		public Object getObject() {
			return _obj;
		}

		public long getExpiry() {
			return _expiry;
		}
	}
}
