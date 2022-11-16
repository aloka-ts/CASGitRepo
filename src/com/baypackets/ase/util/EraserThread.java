/*
 * TelnetServer.java
 *
 * Created on August 6, 2004, 9:45 AM
 */
package com.baypackets.ase.util;

import java.io.PrintWriter;

import org.apache.log4j.Logger;

/**
 * This thread erases the echoed characters as they are being entered, and
 * replaces them with blankspaces.
 * 
 * @author Puneet
 */

class EraserThread implements Runnable {
	/**
	 * In order to ensure visibility across threads, especially on multi-CPU machines, the stop field 
	 * should be marked volatile
	 */
	private volatile boolean start;
	private PrintWriter writer = null;
	private static Logger _logger = Logger.getLogger(EraserThread.class);
	
	/**
	 *@param The Printwriter associated with the telnet console
	 */
	public EraserThread(PrintWriter writer) {
		this.writer = writer;
		start=true;
	}

	/**
	 * Begin masking...display blank space ( )
	 */
	public void run() {
		//To ensure that masking can occur when the system is under heavy use, 
		//the calling thread priority is set to the max for the duration of the call. 
		//The original priority is restored upon return
		int priority = Thread.currentThread().getPriority();
	    	Thread.currentThread().setPriority(Thread.MAX_PRIORITY);
		try{
			while (start) {
				writer.print("\b ");
				writer.flush();
				try {
					Thread.sleep(1);
				} catch (InterruptedException ie) {
					_logger.error(ie.getMessage(),ie);
				}
			}			
		}finally{
			//Restore the original priority 
			Thread.currentThread().setPriority(priority);
		}
	}

	/**
	 * Instruct the thread to stop masking
	 */
	public void stopMasking() {
		this.start = false;
	}
	
   
}
