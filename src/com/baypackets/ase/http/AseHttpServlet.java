package com.baypackets.ase.http;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.sip.SipApplicationSession;
import javax.servlet.sip.SipFactory;

import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.log4j.Logger;

import com.baypackets.ase.common.logging.AseHttpMsgLoggingInterface;
import com.baypackets.ase.common.logging.LoggingHandler;
import com.baypackets.ase.container.AseContext;
import com.baypackets.ase.tomcat.MergedServletContext;
import com.baypackets.ase.util.AseStrings;


public class AseHttpServlet extends HttpServlet {

	/** Logger element */
	private static Logger logger = Logger.getLogger(AseHttpServlet.class.getName());

	public AseHttpServlet() {
		super();
	}

	private static String OP_TYPE = "OP_TYPE";

	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		if (logger.isInfoEnabled()) {
			logger.info("Inside doPost()");
		}

		AseHttpEvent event = new AseHttpEvent();
		event.setHttpRequest(request);
		event.setHttpResponse(response);
		event.setsContext(getServletContext());
		
		if (request.getAttribute(OP_TYPE) != null) {
		
			event.setEventId(AseHttpEvent.EVENT_HTTP_GET);
		
		} else {
			event.setEventId(AseHttpEvent.EVENT_HTTP_POST);

			boolean isMultipart = ServletFileUpload.isMultipartContent(request);
			
			if (isMultipart) {
				event.loadData();
			}
		}
		if (logger.isInfoEnabled()) {
			logger.info("fireEvent");
		}

		fireEvent(event);

	}

	public void doGet(HttpServletRequest request, HttpServletResponse response)
	throws ServletException, IOException {
		
		if(logger.isInfoEnabled() ){
		logger.info("Inside doGet() ");
		}
		
		request.setAttribute(OP_TYPE, "1");
		this.doPost(request, response);
	}

	public void doPut(HttpServletRequest request, HttpServletResponse response)
	throws ServletException, java.io.IOException {

		AseHttpEvent event = new AseHttpEvent();
		event.setEventId(AseHttpEvent.EVENT_HTTP_PUT);
		//		String eventId = EVENT_HTTP_PUT;
		fireEvent(event);

	}

	public void fireEvent(AseHttpEvent event) {
		
		MergedServletContext mergedContext = (MergedServletContext) getServletContext();
		
		AseContext aseContext = mergedContext.getAseContext();
		
		String appid=mergedContext.getAseContext().getId();
		
		
		if (logger.isDebugEnabled()) {
            logger.debug("<AseHttpServlet>APPID from Merged context is " + appid);
		}
		
		HttpServletRequest request = event.getHttpRequest();
				
		String aai = request.getParameter(AseStrings.PARAM_AAI);

		if (aai != null && aai.indexOf("aai=") != -1) {
			appid = aai.substring(aai.indexOf("aai=") + 4);
		}
		

		if (appid != null) {
			if (logger.isDebugEnabled()) {
                logger.debug("<AseHttpServlet> doPost() Know whom to invoke App id found to be " + appid);
			}
            try {
                appid = URLDecoder.decode(appid, AseStrings.XML_ENCODING_UTF8);
	        } catch (UnsupportedEncodingException e) {
                logger.error("could not decode Application Session ID", e);
                return;
	        }

	        if (logger.isDebugEnabled()) {
	        	 logger.debug("AseHttpServlet  App id After Decoding is  is"
                                 + appid);
	        }

			SipApplicationSession appSession = (SipApplicationSession) getServletContext()
					.getAttribute(appid);
			
			
			if (appSession == null) {

				SipFactory factory = (SipFactory) getServletContext()
						.getAttribute("javax.servlet.sip.SipFactory");
				appSession = factory.createApplicationSession();

				if (logger.isDebugEnabled()) {
					logger.debug("<AseHttpServlet> doPost() created new App session "
							+ appSession);
				}
			} else {

				if (logger.isDebugEnabled()) {
					logger.debug("<AseHttpServlet> doPost() App Session found "
							+ appSession);
				}
			}
			event.setAppSession(appSession);
			
			List listeners = aseContext.getListeners();
			
			for (Object listener : listeners){
				if (listener instanceof AseHttpMethodNotificationListener){
					((AseHttpMethodNotificationListener) listener).handleHttpEvents(event);
				}
			}
		} else {
			if (logger.isDebugEnabled()) {
                logger.debug("<AseHttpServlet> doPost() Donot know whom to invoke App id found to be null");
			}
		}
	}

//	public static void setMessageLoggingInterface(AseHttpMsgLoggingInterface loggingInf) {
//		loggingInterface=loggingInf;
//		
//	}

//	public static AseHttpMsgLoggingInterface getMessageLoggingInterface() {
//		return LoggingHandler.getHttpLoggingInterface();
//	}
	
	
	//static AseHttpMsgLoggingInterface loggingInterface=null;

}
