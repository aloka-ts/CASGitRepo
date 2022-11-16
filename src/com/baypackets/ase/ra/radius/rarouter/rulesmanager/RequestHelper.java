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

package com.baypackets.ase.ra.radius.rarouter.rulesmanager;

import java.net.InetSocketAddress;

import org.apache.log4j.Logger;
import com.baypackets.ase.ra.radius.RadiusRequest;
import com.baypackets.ase.resource.ResourceException;

public class RequestHelper
{
	private static Logger logger = Logger.getLogger(RequestHelper.class);

	public static final String[] varNames = {
		"request.remote.ip.address",
		"request.remote.port",
	};


	// these constants are closely linked with the position of 
	// the string array above. 
	public static final int request_remote_ip_address 	= 0;
	public static final int request_remote_port 	= 1;


	public static String getRequestProperty (RadiusRequest req, int index)
	{
	try {
			// here the index is the index into the above static array to get the 
			// value from the SIP request object
			
			switch (index)
			{
			case request_remote_ip_address:
				return req.getRemoteAddress().getAddress().getHostAddress().toString();
			case request_remote_port:
				return req.getRemoteAddress().getPort()+""; 		
			default:
				throw new ResourceException("unknown request property index:"+index);
			}
		} catch (ResourceException e) {
			logger.error("ResourceException in getRequestProperty ",e);
			return null;
		}
	}
}
