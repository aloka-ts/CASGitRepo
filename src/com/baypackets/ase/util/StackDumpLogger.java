//*****************************************************************************
// GENBAND, Inc. Confidential and Proprietary
//
// This work contains valuable confidential and proprietary
// information.
// Disclosure, use or reproduction without the written authorization of
// GENBAND, Inc. is prohibited. This unpublished work by GENBAND, Inc.
// is protected by laws of United States and other countries.
// If publication of work should occur the following notice shall
// apply:
//
//"Copyright 2007 GENBAND, Inc. All right reserved."
//*****************************************************************************


//*****************************************************************************
//
//      File:   StackDumpLogger.java
//
//      Desc:   This file contains definition of a utility class which has
//				method to dump stack trace of SAS JVM.
//
//      Author                          Date            Description
//      -----------------------------------------------------------------------
//      Neeraj Jain						30-Oct-07		Initial Creation
//
//*****************************************************************************

package com.baypackets.ase.util;

import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.apache.log4j.Logger;

import com.baypackets.bayprocessor.slee.common.BaseContext;
import com.baypackets.bayprocessor.slee.common.ConfigRepository;

/**
 * This class is a utility for other modules of SAS. It contains a method
 * which can be invoked by user to dump stack traces of all the threads in
 * in the JVM into a log file.
 *
 * @author Neeraj Jain
 */
public class StackDumpLogger {
	public static final Logger logger = Logger.getLogger(StackDumpLogger.class);

	/**
	 * This method prints stack-traces of all the threads present in the JVM
	 * into a log file. A new log file is created each time this method is
	 * invoked.
	 */
	public static void logStackTraces() {
		FileOutputStream fos = null;
		OutputStreamWriter osw = null;

		try {
			// Create a new file to log stack dump
			String logDir = BaseContext.getConfigRepository().getValue(
													Constants.OID_LOGS_DIR);
			String fn = new String(logDir +
				"/ASE_StackDump_sas_" +
				new SimpleDateFormat("MMM_dd_HH:mm:ss").format(new Date()) +
				".log");
			fos = new FileOutputStream(fn);
			osw = new OutputStreamWriter(fos);
			if (logger.isInfoEnabled())
			logger.info("Created file with name: " + fn);

			// Sort the threads by name
			Set<Map.Entry<Thread, StackTraceElement[]>> set = Thread.getAllStackTraces().entrySet();
			TreeMap<String, StackTraceElement[]> sortedMap = new TreeMap();
			for (Map.Entry<Thread, StackTraceElement[]> me : set) {
				sortedMap.put(me.getKey().getName(), me.getValue());
			}

			// Now print each thread's stack trace
			for (Map.Entry<String, StackTraceElement[]> me : sortedMap.entrySet()) {
				dumpStackTrace(osw, me.getKey(), me.getValue());
			}

			// Flush the buffers
			fos.flush();
			osw.flush();
		} catch(Throwable thr) {
			logger.error("Error in printing stack traces", thr);
			throw new IllegalStateException("Error in print stack trace");
		} finally {
			// Close all output streams created
			try {
				if (fos != null) {
					fos.close();
				}

				if (osw != null) {
					osw.close();
				}
			} catch(Exception exp) {
				logger.error("Error in closing streams", exp);
			}
		}
	}

	/**
	 * This is a private method used to print thread name and its stack-trace.
	 *
	 * @param p_fw File writer on which logs should be written
	 * @param p_tName Thread name
	 * @param p_ste Single stack-trace-element
	 */
	private static void dumpStackTrace(	Writer p_fw,
										String p_tName,
										StackTraceElement[] p_ste)
		throws IOException {

		p_fw.write(p_tName);
		p_fw.write(":\n");

		for (StackTraceElement ste : p_ste) {
			p_fw.write("    ");
			p_fw.write(ste.toString());
			p_fw.write("\n");
		}

		p_fw.write("\n");
	}
}
