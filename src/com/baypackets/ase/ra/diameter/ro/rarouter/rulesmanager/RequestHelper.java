/*------------------------------------------
 * SIP Request helper class
 * Nasir
 * Version 1.0   08/19/04
 * BayPackets Inc.
 * Revisions:
 * BugID : Date : Info
 *
 * BPUsa06502_18 : 10/08/04 : This change is to
 * take into account the arbitrary uri params
 * that can be included in the request as per
 * section 6.6.1 of Sip Servlet Specification
 * We have added the ability to add upto 10
 * arbitrary parameters in a request and trigger
 * the application based on this. 
 *------------------------------------------*/

package com.baypackets.ase.ra.diameter.ro.rarouter.rulesmanager;

import org.apache.log4j.Logger;

import com.baypackets.ase.ra.diameter.ro.RoRequest;
import com.baypackets.ase.resource.ResourceException;

public class RequestHelper
{
	private static Logger logger = Logger.getLogger(RequestHelper.class);

	public static final String[] varNames = {

		"request.orig.host",
		"request.orig.realm",

		"request.dest.host",
		"request.dest.realm",

	};


	// these constants are closely linked with the position of 
	// the string array above. 
	public static final int request_orig_host 	= 0;
	public static final int request_orig_realm 	= 1;
	public static final int request_dest_host	= 2;
	public static final int request_dest_realm	= 3;


	public static String getRequestProperty (RoRequest req, int index)
	{
		try {
			// here the index is the index into the above static array to get the 
			// value from the SIP request object
			
			switch (index)
			{
			
			case request_orig_host:

				return req.getOriginHost();

			case request_orig_realm:
				return req.getOriginRealm();
				
			case request_dest_host:
				return req.getDestinationHost();
				
			case request_dest_realm:
				return req.getDestinationRealm();
				
			default:
				return null;
				
			}
		} catch (ResourceException e) {
			logger.error("ResourceException in getRequestProperty ",e);
			return null;
		}
	}
}
