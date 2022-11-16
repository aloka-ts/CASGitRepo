/*******************************************************************************
 *   Copyright (c) 2014 Agnity, Inc. All rights reserved.
 *   
 *   This is proprietary source code of Agnity, Inc. 
 *   
 *   Agnity, Inc. retains all intellectual property rights associated 
 *   with this source code. Use is subject to license terms.
 *   
 *   This source code contains trade secrets owned by Agnity, Inc.
 *   Confidentiality of this computer program must be maintained at 
 *   all times, unless explicitly authorized by Agnity, Inc.
 *******************************************************************************/
package com.baypackets.sas.ide.logger.loader;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Iterator;
import java.util.Vector;

import com.baypackets.sas.ide.SasPlugin;
import com.baypackets.sas.ide.logger.filters.Filter;
import com.baypackets.sas.ide.logger.util.BoundedList;
import com.baypackets.sas.ide.logger.util.LoggingUtil;
import com.baypackets.sas.ide.logger.views.SASDebugLoggerView;

/**
 * Watches a log file for any changes, and keeps a list of the most recent lines
 * to have been added to the file. Notifies LogFileListeners when a change to
 * the file being watched is detected.
 */
public class LogFilesListener extends Thread {


	private int m_interval = 1; // Seconds
	private final String SERVER_STOPPED = "*** Server is not running OR not able to connect.Try Refresh. ***";
	private final String FILE_DELETED = "*** File does not exist or not able to connect to server.Try Refresh. ***";
	private static String fileName = null;
	private  String readerNum = "";
	private boolean error = false;

	/**
	 * Number of lines to show at start. Zero indicates showing the entire file.
	 */
	private int m_numLines = 10;
	private boolean m_active = false;
	private Vector m_listeners = new Vector();
	private boolean m_console = false; // Debugging
	private Vector m_filters = new Vector();
	private BoundedList m_list = null;
	LoggingUtil loggingUtil = null;
	boolean firstUpdate = false;

	/**
	 * Create a LogFilesListener.
	 */
	public LogFilesListener(int interval, int numLines, String logsType)
			throws FileNotFoundException, IOException {
		this.fileName = logsType;
		m_interval = interval;
		m_numLines = numLines;
		loggingUtil = new LoggingUtil();
	//	this.readerNum = loggingUtil.openReader(fileName, true);
	}

	/**
	 * Halt the execution of the Watcher.
	 */
	public void halt() {
		m_active = false;
		interrupt();
	}

	/**
	 * Determines if the watcher should output each updated line to the console.
	 */
	public void setConsole(boolean b) {
		m_console = b;
	}

	public void addListener(LogFilesUpdateListener listener) {
		m_listeners.add(listener);
	}

	/**
	 * Runs the thread that watches for changes to the file.
	 */
	public void run() {
		m_active = true;
		m_list = new BoundedList(m_numLines);
		String line = null;
		//long size = 0;
		if(fileName.equals(SASDebugLoggerView.SIP_DEBUG_LOG)){
		     this.readerNum = loggingUtil.openReader(fileName, false,"0");
		}else{
		    this.readerNum = loggingUtil.openReader(fileName, true,"0");
		}

		SasPlugin.getDefault().log(
				"Inside run of Log watcher for ........." + fileName
						+ " Reader number is " + readerNum+ " numLines "+m_numLines);
		while (m_active) {
			// Keep checking for new lines in the file
			boolean updated = false;

			try { 
					
				if (loggingUtil.isServerStopped()) {

					SasPlugin.getDefault().log(
							"The Server is not running........");
					m_list.add(this.SERVER_STOPPED);
					error = true;
					updated = true;
					m_active = false;
				} else if (!loggingUtil.isLogFileExist(fileName)) {
					SasPlugin.getDefault().log("The File do not exist.......");
					m_list.add(this.FILE_DELETED);
					updated = true;
					m_active = false;
					error = true;
				} else {

					// Read through the lines of the files
					while ((line = loggingUtil.getLogsUsingJMX(this.readerNum,
							fileName)) != null) {
						if (line.length() > 0) {

							if (m_filters != null) {
								synchronized (m_filters) {
									// Apply each filter
									for (Iterator iter = m_filters.iterator(); iter
											.hasNext();) {
										Filter f = (Filter) iter.next();
										if (f.matches(line)) {
											line = f.handleWatcherMatch(line,
													firstUpdate);
										}
									}
								}
							}

							// Make sure the filter didn't set the line to
							// null...
							if (line != null) {
//								SasPlugin.getDefault().log(
//										"The Line read is."
//												+ line);
								updated = true;

								m_list.add(line);

								if (firstUpdate == false) { // make first update
															// to true when the
															// m_list is full
															// eith the
															// specaified no. of
															// lines
									if (m_list.isFull()) {
//										SasPlugin.getDefault().log(
//												"The List is  full now.."
//														+ m_list.size());
										firstUpdate = true;
									}
								}

								if (updated && firstUpdate) { // show the lines
																// to show in
																// the start
																// after first
																// update show
																// the every
																// updated line
									notifyListeners();
								}

								if (m_console) {
									// Dump the latest line to the console if
									// debugging
									System.out.println(line);
								}
							}
						}
					} // while closed
					
//					SasPlugin.getDefault().log(
//							"out of while Line read ."
//									+ line);
					// firstUpdate = true;
				} // else closed
				if (updated) {
					notifyListeners();
				}

				sleep(m_interval * 1000);
			} catch (Exception e) {
				SasPlugin.getDefault().log(
						"Got Exception in while actve " + e);
			}
		}
		try {
			SasPlugin.getDefault().log(
					"out of while loop.........close reader..."
							+ readerNum);
			if (readerNum!=null &&!readerNum.equals("")){
			loggingUtil.closeReader(fileName, this.readerNum);
			}
		} catch (Exception e) {
			SasPlugin.getDefault().log(
					"Error closing log file reader" + e.getCause());
		}
	}

	/**
	 * Notify the listeners that an update has been made to the log file beign
	 * watched.
	 */
	protected synchronized void notifyListeners() {
		for (Iterator i = m_listeners.iterator(); i.hasNext();) {
			LogFilesUpdateListener l = (LogFilesUpdateListener) i.next();
			l.update(m_list);
		}
	}

	public int getInterval() {
		return m_interval;
	}

	public void setInterval(int interval) {
		m_interval = interval;
	}

	public void clear() {
		m_list.clear();

		// notifyListeners();
	}

	/**
	 * Sets the number of lines to show. Showing whole file is indicated with 0.
	 * 
	 * @param numLines
	 *            The number of lines to show. Zero indicates whole file.
	 */
	public void setNumLines(int numLines) {
		if (numLines == 0) {
			// Transform to biggest possible int.
			// m_numLines = Integer.MAX_VALUE;
			m_numLines = 10;
		} else if (numLines >= 100) {
			m_numLines = 100;
		} else {
			m_numLines = numLines;
		}
		m_list.setMaxItems(m_numLines);
	}

	public boolean isError() {
		return error;
	}

	/**
	 * Gets the number of lines to show for this watcher
	 * 
	 * @return number of lines.
	 */
	public int getNumLines() {
		return m_numLines;
	}

	public void setFilters(Vector filters) {
		synchronized (m_filters) {
			m_filters = filters;
		}
	}

	public boolean isFirstUpdate() {
		return firstUpdate;
	}

	public void stopListener() {
		this.halt();
		if (!readerNum.equals(""))
			loggingUtil.closeReader(fileName, readerNum);
		m_listeners.clear();
		this.clear();
	}

}