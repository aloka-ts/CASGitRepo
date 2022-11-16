package com.baypackets.sas.ide.logger.loader;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.Iterator;
import java.util.Vector;

import javax.management.MBeanServerConnection;
import javax.management.MBeanOperationInfo;
import javax.management.MBeanServerInvocationHandler;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;

import com.baypackets.ase.jmxmanagement.LogWatcherMBean;
import com.baypackets.sas.ide.SasPlugin;
import com.baypackets.sas.ide.logger.filters.Filter;
import com.baypackets.sas.ide.logger.util.BoundedList;
import com.baypackets.sas.ide.logger.util.LoggingUtil;
import com.baypackets.sas.ide.util.GetStatusSAS;
import com.baypackets.sas.ide.util.StatusASE;

/**
 * Watches a log file for any changes, and keeps a list of the most recent lines
 * to have been added to the file. Notifies LogFileListeners when a change to
 * the file being watched is detected.
 */
public class LogFilesListener extends Thread {

	private File m_file = null;
	private int m_interval = 1; // Seconds
	private final String SIP_DEBUG_LOG = "sipDebug.log";
	private final String CAS_LOG = "CAS.log";
	private final String FILE_TRUNCATED = "*** File truncated or not able to connect to server.Try Refresh !!!! ***";
	private final String SERVER_STOPPED = "*** Server is not running OR not able to connect.Try Refresh !!!! ***";
	private final String FILE_DELETED = "*** File does not exist or not able to connect to server.Try Refresh !!!! ***";
	private static String fileName = null;
	private static String readerNum = "";
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
		this.readerNum = loggingUtil.openReader(fileName, true,"0");

		SasPlugin.getDefault().log(
				"Inside run of Log watcher for ........." + fileName
						+ " Reader number is " + readerNum+ " numLines "+m_numLines);
		while (m_active) {
			// Keep checking for new lines in the file
			boolean updated = false;
//			boolean truncated = false;
//
//			// See if the file was truncated...
//			truncated = false;
//			long i = loggingUtil.getLogFileSizeUsingJMX(fileName);
//
//			if (i < size && i != 0) {
//				SasPlugin.getDefault().log(
//						"Setting truncated to true...File size is :" + size
//								+ " Value of i is " + i);
//				truncated = true;
//			}
//			size = loggingUtil.getLogFileSizeUsingJMX(fileName);
			try {
//				if (truncated || size < 0) {
//
//					m_list.add(this.FILE_TRUNCATED);
//					updated = true;
//					error = true;
//					if (!readerNum.equals("")){
//						SasPlugin.getDefault().log(
//								"File is truncated.........close reader..."
//										+ readerNum);
//						loggingUtil.closeReader(fileName, readerNum);
//					}
//
//					this.readerNum = loggingUtil.openReader(fileName, true);
//					SasPlugin.getDefault().log(
//							"File is truncated.........reader number is..."
//									+ readerNum);
//
//				} else 
					
				if (loggingUtil.isServerStopped()) {

					SasPlugin.getDefault().log(
							"The Server is not running........");
					m_list.add(this.SERVER_STOPPED);
					error = true;
					updated = true;
					m_active = false;
				} else if (!loggingUtil.isLogFileExist(fileName)) {
					SasPlugin.getDefault().log("The File is deleted.......");
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
								SasPlugin.getDefault().log(
										"The Line read is."
												+ line);
								updated = true;

								m_list.add(line);

								if (firstUpdate == false) { // make first update
															// to true when the
															// m_list is full
															// eith the
															// specaified no. of
															// lines
									if (m_list.isFull()) {
										SasPlugin.getDefault().log(
												"The List is  full now.."
														+ m_list.size());
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
					
					SasPlugin.getDefault().log(
							"out of while Line read ."
									+ line);
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