/****
  Copyright (c) 2015 Agnity, Inc. All rights reserved.
  
  This is proprietary source code of Agnity, Inc. 
  
  Agnity, Inc. retains all intellectual property rights associated 
  with this source code. Use is subject to license terms.
  
  This source code contains trade secrets owned by Agnity, Inc.
  Confidentiality of this computer program must be maintained at 
  all times, unless explicitly authorized by Agnity, Inc.
****/

package com.baypackets.ase.tomcat;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;

import org.apache.catalina.connector.Request;
import org.apache.catalina.connector.Response;
import org.apache.catalina.valves.ValveBase;
import org.apache.log4j.Logger;

import com.baypackets.ase.measurement.AseMeasurementUtil;
import com.baypackets.ase.util.AseStrings;

/**
 * This class is an implementation of org.apache.catalina.Valve interface of Tomcat.
 * This valve will be added into embedded tomcat host pipeline, so that incoming HTTP
 * requests can be reject by container whenever required. To reject incoming requests 
 * isReject flag must be set when soft shutdown initiated.
 *
 * @author Amit Baxi
 *
 */
public class AseTomcatValve extends ValveBase{
	private static final Logger logger = Logger.getLogger(AseTomcatValve.class);
	private volatile boolean m_isReject;
	private AtomicInteger pendingHttpRequests = new AtomicInteger(0);
	
	public void setReject(boolean reject){
		logger.debug("AseTomcatValve : setReject(boolean) called : reject parameter set as true");
		m_isReject=reject;
	}
	
	@Override
	 /** 
	 * This method will be invoked by Tomcat container once this valve added into container pipeline.
	 * This valve will reject incoming request with error response once isReject is set as true otherwise
	 * it will pass request and response to next valve in pipeline for further processing.
	 * 
	 * @param request The servlet request to be processed
     * @param response The servlet response to be created
     *
     * @exception IOException if an input/output error occurs, or is thrown
     *  by a subsequently invoked Valve, Filter, or Servlet
     * @exception ServletException if a servlet error occurs, or is thrown
     *  by a subsequently invoked Valve, Filter, or Servlet
     */
	public void invoke(Request request, Response response) throws IOException,
			ServletException {
		AseMeasurementUtil.counterHttpRequestsIn.increment();
		if(AseStrings.POST.equals(request.getMethod())){
			AseMeasurementUtil.counterHttpPostRequestsIn.increment();
		}else{
			AseMeasurementUtil.counterHttpGetRequestsIn.increment();
		}
		pendingHttpRequests.incrementAndGet();
		if(m_isReject){
			logger.error("Rejecting incoming HTTP request as subsystem is in softshutdown state");
			response.addHeader("Reason", "Container Soft ShutDown initiated");
			response.sendError(HttpServletResponse.SC_SERVICE_UNAVAILABLE,"Container rejected request as soft shutdown initiated.");
			pendingHttpRequests.decrementAndGet();
			AseMeasurementUtil.counterHttpErrorResponsesOut.increment();
			return;
		}
		getNext().invoke(request, response);
		if(response!=null){
			int status=response.getStatus();
			if(200<=status && 300>status){
				AseMeasurementUtil.counterHttpSuccessResponsesOut.increment();
			}else{
				AseMeasurementUtil.counterHttpErrorResponsesOut.increment();
			}
		}
		pendingHttpRequests.decrementAndGet();
	}
	
	public int getPendingHttpRequests(){
		return pendingHttpRequests.get();
	}
	
}