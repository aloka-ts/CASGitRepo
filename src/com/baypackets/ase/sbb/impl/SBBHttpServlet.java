package com.baypackets.ase.sbb.impl;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.sip.SipApplicationSession;
import org.apache.log4j.Logger;
import com.baypackets.ase.sbb.SBBEvent;
import com.baypackets.ase.sbb.WebEvent;
import com.baypackets.ase.util.AseStrings;

import javax.servlet.ServletContext;

public class SBBHttpServlet extends HttpServlet {

	/** Logger element */
	private static Logger logger = Logger.getLogger(SBBServlet.class.getName());

	public SBBHttpServlet() {
		super();
	}

	private static final int HTTP_GET = 1;
	private static final int HTTP_POST = 2;
	private static String OP_TYPE = "OP_TYPE";

	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		int opType = 0;

		if (request.getAttribute(OP_TYPE) != null)
			opType = 1;
		else
			opType = 2;

		if (logger.isDebugEnabled()) {
			if (opType == 1)
				logger.debug("<SBB> doGet() called  SBBHttpServlet..." + opType
						+ request.getRequestURL());
			else if (opType == 2)
				logger.debug("<SBB> doPost() called  SBBHttpServlet..."
						+ opType + request.getRequestURL());
		}
		String appid = request.getParameter("aai");

		if (logger.isDebugEnabled()) {
			logger.debug("<SBB>  SBBHttpServlet...AAI Param found is " + appid);
		}

		if (appid != null && appid.indexOf("aai=") != -1) {
			appid = appid.substring(appid.indexOf("aai=") + 4);
		}

		if (appid != null) {

			try {
				appid = URLDecoder.decode(appid, AseStrings.XML_ENCODING_UTF8);

			} catch (UnsupportedEncodingException e) {
				logger.error("could not decode Application Session ID", e);
				return;
			}

			if (logger.isDebugEnabled()) {
				logger
						.debug("<SBB> SBBHttpServlet  App id After Decoding is  is"
								+ appid);
			}

			SipApplicationSession appSession = (SipApplicationSession) getServletContext()
					.getAttribute(appid);

			String sbbName = (String) appSession
					.getAttribute(SBBOperationContext.ATTRIBUTE_SBB);
			SBBOperationContext operCtx = (SBBOperationContext) appSession
					.getAttribute(sbbName);

			ServletContext sCtx = null;

			if (operCtx instanceof SBBImpl) {

				SBBImpl sbb = (SBBImpl) operCtx;
				sCtx = sbb.getServletContext();
			} else {
				sCtx = getServletContext();
			}

			WebEvent event = new WebEvent(request, response, sCtx);

			switch (opType) {
			case HTTP_GET:

				event.setEventId(WebEvent.EVENT_HTTP_GET);

				break;
			case HTTP_POST: {
				event.setEventId(WebEvent.EVENT_HTTP_POST);
				event.loadData();
			}
				break;
			}
			operCtx.fireEvent(event);

		} else {

			if (logger.isDebugEnabled()) {
				logger
						.debug("<SBB> doPost() SBBHttpServlet...Donot know whom to invoke App id found to be null,");
			}

		}
	}

	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		request.setAttribute(OP_TYPE, "1");
		this.doPost(request, response);
	}

	public void doPut(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, java.io.IOException {

		fireEvent(req, resp, WebEvent.EVENT_HTTP_PUT);

	}

	public void fireEvent(HttpServletRequest req, HttpServletResponse resp,
			String eventID) {

		String appid = req.getParameter(AseStrings.PARAM_AAI);

		if (appid != null && appid.indexOf("aai=") != -1) {
			appid = appid.substring(appid.indexOf("aai=") + 4);
		}

		if (appid != null) {

			SipApplicationSession appSession = (SipApplicationSession) getServletContext()
					.getAttribute(appid);

			String sbbName = (String) appSession
					.getAttribute(SBBOperationContext.ATTRIBUTE_SBB);
			SBBOperationContext operCtx = (SBBOperationContext) appSession
					.getAttribute(sbbName);

			SBBEvent wEvent = new WebEvent(req, resp, getServletContext());

			wEvent.setEventId(eventID);
			operCtx.fireEvent(wEvent);

		} else {

		}

	}

}
