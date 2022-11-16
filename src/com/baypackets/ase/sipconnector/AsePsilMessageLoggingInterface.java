/**
 * File: AsePsilMessageLoggingInterface.java
 * Created on: March 21, 2006
 * Author: Neeraj Jain
 */

package com.baypackets.ase.sipconnector;

interface AsePsilMessageLoggingInterface {
	public void logRequest(String request);
	public void logResponse(String response);
}
