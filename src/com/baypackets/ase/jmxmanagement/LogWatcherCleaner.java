package com.baypackets.ase.jmxmanagement;

import org.apache.log4j.Logger;

import java.util.Collections;
import java.util.Map;
import java.util.Date;
import java.util.Set;
import java.util.Iterator;
import java.util.Calendar;
import java.io.IOException;
import java.io.LineNumberReader;
import java.util.ConcurrentModificationException;

public class LogWatcherCleaner extends Thread {

	private static Logger logger = Logger.getLogger(LogWatcherCleaner.class);

	private int m_interval = 15; // mins

	LogWatcher watcher = null;

	public Map<Integer, LineNumberReader> readers = null;

	Map<Integer, Date> readersTS = null;

	public LogWatcherCleaner(LogWatcher watcher ,int m_interval) {
		this.watcher = watcher;
		this.m_interval=m_interval;
	}

	public void run() {

		try {

			boolean active = true;
			if(logger.isInfoEnabled() ){
			logger.info("Inside run() of LogWatcherCleaner............");
			}

			while (active) {

				readers = Collections.synchronizedMap(watcher.getReaderMap());
				readersTS = Collections.synchronizedMap(watcher
						.getReaderTSMap());
               synchronized (readers){
            	   synchronized (readersTS) {
					
						if (!readers.isEmpty() && !readersTS.isEmpty()) {
							if(logger.isInfoEnabled() ){
							logger
									.info("The LogWatcherCleaner Reader Currently in Map are" +readers.size());
							}
							Set keyset = readers.keySet();
							Iterator itr = keyset.iterator();
							while (itr.hasNext()) {
								int i = (Integer) itr.next();
								if (readersTS.containsKey(i)) {
									Date date = readersTS.get(i);
									Date current = Calendar.getInstance()
											.getTime();
									if ((current.getTime() - date.getTime()) >= (m_interval * 60000)) {
										LineNumberReader reader = readers.get(i);
										if(logger.isInfoEnabled() ){
										logger
												.info("LogWatcherCleaner Removing Reader not used till last 5 mins*********** with index :"
														+ i + "Current Timr is..."+current.getTime() +" Reader Time Stamp is.."+date.getTime());
										}
										watcher.removeFromReaderMap(i);
										watcher.removeFromReaderTSMap(i);
										try {
											reader.close();
										} catch (IOException e) {
											logger
													.error(
															"The LogWatcherCleaner has thrown exception while closing the Reader",
															e);
										}
									}
								}
							}
						}else{
							if(logger.isInfoEnabled() ){
							logger
							.info("The LogWatcherCleaner No Readers in Map .........");
							}
						}
				if(logger.isInfoEnabled() ){
				logger.info("LogWatcherCleaner Going to sleep for *********** "+m_interval);
				}
				sleep(m_interval * 60000);
				if(logger.isInfoEnabled() ){
				logger.info("LogWatcherCleaner Woke up after *********** "+m_interval);
				}
               }
			}
			}
		} catch (InterruptedException e) {
			logger.error("The LogWatcherCleaner InterruptedException !!!!!! ",
					e);
			//			// Ignore. If we have been stopped, the while condition will
			//			// fail.
		} catch (ConcurrentModificationException e) {
			logger
					.error(
							"The LogWatcherCleaner ConcurrentModificationException!!!!!! ",
							e);
		}
	}

}
