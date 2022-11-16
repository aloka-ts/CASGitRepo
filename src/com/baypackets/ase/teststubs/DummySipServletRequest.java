package com.baypackets.ase.teststubs;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.Principal;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.Locale;
import java.util.Map;

import javax.security.auth.Subject;
import javax.servlet.AsyncContext;
import javax.servlet.DispatcherType;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletInputStream;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.sip.Address;
import javax.servlet.sip.AuthInfo;
import javax.servlet.sip.B2buaHelper;
import javax.servlet.sip.Parameterable;
import javax.servlet.sip.Proxy;
import javax.servlet.sip.ServletParseException;
import javax.servlet.sip.SipApplicationSession;
import javax.servlet.sip.SipServletRequest;
import javax.servlet.sip.SipServletResponse;
import javax.servlet.sip.SipSession;
import javax.servlet.sip.SipURI;
import javax.servlet.sip.TooManyHopsException;
import javax.servlet.sip.URI;
import javax.servlet.sip.ar.SipApplicationRoutingDirective;
import javax.servlet.sip.ar.SipApplicationRoutingRegion;

import org.apache.log4j.Logger;

import com.baypackets.ase.container.AseBaseRequest;
import com.baypackets.ase.container.AseIc;
import com.baypackets.ase.container.AseProtocolSession;
import com.baypackets.ase.dispatcher.Destination;
import com.baypackets.ase.dispatcher.Dispatcher;
import com.baypackets.ase.dispatcher.DispatcherImpl;
import com.baypackets.ase.spi.container.AbstractSasMessage;
import com.baypackets.ase.spi.container.SasProtocolSession;
import com.baypackets.ase.dispatcher.Destination;


public class DummySipServletRequest extends AbstractSasMessage
  implements AseBaseRequest,SipServletRequest 
{
  static Logger _logger =
    Logger.getLogger(DispatcherImpl.class);
  public  DummySipServletRequest (
    	URI requestURI,
	int  maxForwards,
	boolean initial,
	String serverHost,
	int serverPort,
	String remoteAddress,
	String remoteHost,
	int remotePort,
	String localAddress,
	int localPort,
	Address fromAddress,
	Address toAddress,
	String method,
	String callId,
	int expires,
	String remoteUser,
	
	Dispatcher dispatcher,
	DummySipSession sipSession
  ) 
  {
    this.requestURI = requestURI;
	this.maxForwards=maxForwards;
	this.initial=initial;
	this.serverHost=serverHost;
	this.serverPort=serverPort;
	this.remoteAddress=remoteAddress;
	this.remoteHost=remoteHost;
	this.remotePort=remotePort;
	this.localAddress=localAddress;
	this.localPort=localPort;
	this.fromAddress=fromAddress;
	this.toAddress=toAddress;
	this.method=method;
	this.callId=callId;
	this.expires=expires;
	this.remoteUser=remoteUser;
	
	this.dispatcher=dispatcher;
	this.sipSession=sipSession;
  }

	public void setUserPrincipal(java.security.Principal principal) {
	}

	public URI getRequestURI() {
		return requestURI;
	}

	public void setRequestURI(URI arg0) {
			if (_logger.isInfoEnabled()) 
          _logger.info ("setREquestURI called with "+arg0);
          this.requestURI = arg0;
	}


	public void pushRoute(SipURI arg0) {
		// TODO Auto-generated method stub

	}

	public int getMaxForwards() {
		return maxForwards;
	}

	public void setMaxForwards(int arg0) {
			if (_logger.isInfoEnabled()) 
          _logger.info ("setMaxForwards called with "+arg0);
          this.maxForwards = arg0;
	}

	public void send() throws IOException {
			if (_logger.isInfoEnabled()) 
           _logger.info ("********Request.SEND called********");
	}

	public boolean isInitial() {
			if (_logger.isInfoEnabled()) 
           _logger.info ("isInitial called");
		return initial;
	}

	public ServletInputStream getInputStream() throws IOException {
		// TODO Auto-generated method stub
		if (_logger.isInfoEnabled()) 
           _logger.info ("getInputStream called");
		return null;
	}

	public BufferedReader getReader() throws IOException {
		if (_logger.isInfoEnabled()) 
           _logger.info ("getReader called");
		// TODO Auto-generated method stub
		return null;
	}

	public Proxy getProxy() throws TooManyHopsException {
		if (_logger.isInfoEnabled()) 
           _logger.info ("getProxy called");
		// TODO Auto-generated method stub
		return null;
	}

	public Proxy getProxy(boolean arg0) throws TooManyHopsException {
			if (_logger.isInfoEnabled()) 
           _logger.info ("getProxy called");
		// TODO Auto-generated method stub
		return null;
	}

	public SipServletResponse createResponse(int arg0) {
			if (_logger.isInfoEnabled()) 
           _logger.info ("createResponse called");
		// TODO Auto-generated method stub
		return new DummySipServletResponse(arg0);
	}

	public SipServletResponse createResponse(int arg0, String arg1) {
			if (_logger.isInfoEnabled()) 
           _logger.info ("createResponse called");
		return new DummySipServletResponse(arg0, arg1); 
	}

	public SipServletRequest createCancel() throws IllegalStateException {
			if (_logger.isInfoEnabled()) 
           _logger.info ("create cancel called, returning this");
		return this;
	}

	public Object getAttribute(String arg0) {
			if (_logger.isInfoEnabled()) 
           _logger.info ("getattribute called with "+arg0);
		return null;
	}

	public Enumeration getAttributeNames() {
			if (_logger.isInfoEnabled()) 
           _logger.info ("getAttributeNames called");
		return null;
	}

	public String getCharacterEncoding() {
		// TODO Auto-generated method stub
			if (_logger.isInfoEnabled()) 
           _logger.info ("getCharacterEncoding called");
		return null;
	}

	public void setCharacterEncoding(String arg0)
		throws UnsupportedEncodingException {
		// TODO Auto-generated method stub
		if (_logger.isInfoEnabled()) 
           _logger.info ("setCharacterEncoding called");

	}

	public int getContentLength() {
		// TODO Auto-generated method stub
		return 0;
	}

	/* (non-Javadoc)
	 * @see javax.servlet.sip.SipServletMessage#getContentType()
	 */
	public String getContentType() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see javax.servlet.ServletRequest#getParameter(java.lang.String)
	 */
	public String getParameter(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see javax.servlet.ServletRequest#getParameterNames()
	 */
	public Enumeration getParameterNames() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see javax.servlet.ServletRequest#getParameterValues(java.lang.String)
	 */
	public String[] getParameterValues(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see javax.servlet.ServletRequest#getParameterMap()
	 */
	public Map getParameterMap() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see javax.servlet.sip.SipServletMessage#getProtocol()
	 */
	public String getProtocol() {
		// TODO Auto-generated method stub
		return sipSession.getProtocol();
	}

	/* (non-Javadoc)
	 * @see javax.servlet.ServletRequest#getScheme()
	 */
	public String getScheme() {
		// TODO Auto-generated method stub
		if (_logger.isInfoEnabled()) 
           _logger.info ("getScheme called");
		return new String("sip");
	}

	/* (non-Javadoc)
	 * @see javax.servlet.ServletRequest#getServerName()
	 */
	public String getServerName() {
		// TODO Auto-generated method stub
		return serverHost;
	}

	/* (non-Javadoc)
	 * @see javax.servlet.ServletRequest#getServerPort()
	 */
	public int getServerPort() {
		// TODO Auto-generated method stub
		return serverPort;
	}

	public String getRemoteAddr() {
		// TODO Auto-generated method stub
		return remoteAddress;
	}

	/* (non-Javadoc)
	 * @see javax.servlet.ServletRequest#getRemoteHost()
	 */
	public String getRemoteHost() {
		// TODO Auto-generated method stub
		return remoteHost;
	}

	/* (non-Javadoc)
	 * @see javax.servlet.sip.SipServletMessage#setAttribute(java.lang.String, java.lang.Object)
	 */
	public void setAttribute(String arg0, Object arg1) {
		if (_logger.isInfoEnabled()) 
           _logger.info ("setattribute called");
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see javax.servlet.ServletRequest#removeAttribute(java.lang.String)
	 */
	public void removeAttribute(String arg0) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see javax.servlet.ServletRequest#getLocale()
	 */
	public Locale getLocale() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see javax.servlet.ServletRequest#getLocales()
	 */
	public Enumeration getLocales() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see javax.servlet.sip.SipServletMessage#isSecure()
	 */
	public boolean isSecure() {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see javax.servlet.ServletRequest#getRequestDispatcher(java.lang.String)
	 */
	public RequestDispatcher getRequestDispatcher(String arg0) {
		// TODO Auto-generated method stub
			if (_logger.isInfoEnabled()) 
           _logger.info ("getRequestDispatcher called");
		return null;
	}

	/* (non-Javadoc)
	 * @see javax.servlet.ServletRequest#getRealPath(java.lang.String)
	 */
	public String getRealPath(String arg0) {
		// TODO Auto-generated method stub
			if (_logger.isInfoEnabled()) 
           _logger.info ("getRealPath called");
		return null;
	}

	/* (non-Javadoc)
	 * @see javax.servlet.sip.SipServletMessage#getRemotePort()
	 */
	public int getRemotePort() {
		// TODO Auto-generated method stub
		return remotePort;
	}

	/* (non-Javadoc)
	 * @see javax.servlet.ServletRequest#getLocalName()
	 */
	public String getLocalName() {
		// TODO Auto-generated method stub
			if (_logger.isInfoEnabled()) 
           _logger.info ("getLocalName called");
		return null;
	}

	/* (non-Javadoc)
	 * @see javax.servlet.sip.SipServletMessage#getLocalAddr()
	 */
	public String getLocalAddr() {
		// TODO Auto-generated method stub
		return localAddress;
	}

	/* (non-Javadoc)
	 * @see javax.servlet.sip.SipServletMessage#getLocalPort()
	 */
	public int getLocalPort() {
		// TODO Auto-generated method stub
		return localPort;
	}

	/* (non-Javadoc)
	 * @see javax.servlet.sip.SipServletMessage#getFrom()
	 */
	public Address getFrom() {
		// TODO Auto-generated method stub
		return fromAddress;
	}

	/* (non-Javadoc)
	 * @see javax.servlet.sip.SipServletMessage#getTo()
	 */
	public Address getTo() {
		// TODO Auto-generated method stub
		return toAddress;
	}

	/* (non-Javadoc)
	 * @see javax.servlet.sip.SipServletMessage#getMethod()
	 */
	public String getMethod() {
		// TODO Auto-generated method stub
		return method;
	}

	/* (non-Javadoc)
	 * @see javax.servlet.sip.SipServletMessage#getHeader(java.lang.String)
	 */
	public String getHeader(String arg0) {
		// TODO Auto-generated method stub
			if (_logger.isInfoEnabled()) 
           _logger.info ("getHeader called with "+arg0);
		return null;
	}

	/* (non-Javadoc)
	 * @see javax.servlet.sip.SipServletMessage#getHeaders(java.lang.String)
	 */
	public ListIterator getHeaders(String arg0) {
		// TODO Auto-generated method stub
			if (_logger.isInfoEnabled()) 
           _logger.info ("getHeaders called");
		return null;
	}

	/* (non-Javadoc)
	 * @see javax.servlet.sip.SipServletMessage#getHeaderNames()
	 */
	public Iterator getHeaderNames() {
		// TODO Auto-generated method stub
			if (_logger.isInfoEnabled()) 
           _logger.info ("getHeaderNames called");
		return null;
	}

	/* (non-Javadoc)
	 * @see javax.servlet.sip.SipServletMessage#setHeader(java.lang.String, java.lang.String)
	 */
	public void setHeader(String arg0, String arg1) {
		// TODO Auto-generated method stub
			if (_logger.isInfoEnabled()) 
           _logger.info ("setHeader called");

	}

	/* (non-Javadoc)
	 * @see javax.servlet.sip.SipServletMessage#addHeader(java.lang.String, java.lang.String)
	 */
	public void addHeader(String arg0, String arg1) {
			if (_logger.isInfoEnabled()) 
           _logger.info ("addHeader called");
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see javax.servlet.sip.SipServletMessage#removeHeader(java.lang.String)
	 */
	public void removeHeader(String arg0) {
		// TODO Auto-generated method stub
			if (_logger.isInfoEnabled()) 
           _logger.info ("removeHeader called");

	}

	/* (non-Javadoc)
	 * @see javax.servlet.sip.SipServletMessage#getAddressHeader(java.lang.String)
	 */
	public Address getAddressHeader(String arg0) throws ServletParseException {
		// TODO Auto-generated method stub
			if (_logger.isInfoEnabled()) 
           _logger.info ("getAddressHeader called");
		return null;
	}

	/* (non-Javadoc)
	 * @see javax.servlet.sip.SipServletMessage#getAddressHeaders(java.lang.String)
	 */
	public ListIterator getAddressHeaders(String arg0)
		throws ServletParseException {
			if (_logger.isInfoEnabled()) 
           _logger.info ("getAddressHeaders called");
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see javax.servlet.sip.SipServletMessage#setAddressHeader(java.lang.String, javax.servlet.sip.Address)
	 */
	public void setAddressHeader(String arg0, Address arg1) {
		// TODO Auto-generated method stub
			if (_logger.isInfoEnabled()) 
           _logger.info ("setAddressHeader called");

	}

	/* (non-Javadoc)
	 * @see javax.servlet.sip.SipServletMessage#addAddressHeader(java.lang.String, javax.servlet.sip.Address, boolean)
	 */
	public void addAddressHeader(String arg0, Address arg1, boolean arg2) {
		// TODO Auto-generated method stub
			if (_logger.isInfoEnabled()) 
           _logger.info ("addAddressHeader called");

	}

	/* (non-Javadoc)
	 * @see javax.servlet.sip.SipServletMessage#getCallId()
	 */
	public String getCallId() {
		// TODO Auto-generated method stub
		return callId;
	}

	/* (non-Javadoc)
	 * @see javax.servlet.sip.SipServletMessage#getExpires()
	 */
	public int getExpires() {
		// TODO Auto-generated method stub
		return expires;
	}

	/* (non-Javadoc)
	 * @see javax.servlet.sip.SipServletMessage#setExpires(int)
	 */
	public void setExpires(int arg0) {
		// TODO Auto-generated method stub
			if (_logger.isInfoEnabled()) 
           _logger.info ("setExpires called");

	}

	/* (non-Javadoc)
	 * @see javax.servlet.sip.SipServletMessage#getRawContent()
	 */
	public byte[] getRawContent() throws IOException {
		// TODO Auto-generated method stub
			if (_logger.isInfoEnabled()) 
           _logger.info ("getRawContent called");
		return null;
	}

	/* (non-Javadoc)
	 * @see javax.servlet.sip.SipServletMessage#getContent()
	 */
	public Object getContent()
		throws IOException, UnsupportedEncodingException {
		// TODO Auto-generated method stub
			if (_logger.isInfoEnabled()) 
           _logger.info ("getContent called");
		return null;
	}

	/* (non-Javadoc)
	 * @see javax.servlet.sip.SipServletMessage#setContent(java.lang.Object, java.lang.String)
	 */
	public void setContent(Object arg0, String arg1)
		throws UnsupportedEncodingException {
		// TODO Auto-generated method stub
			if (_logger.isInfoEnabled()) 
           _logger.info ("setContent called");

	}

	/* (non-Javadoc)
	 * @see javax.servlet.sip.SipServletMessage#setContentLength(int)
	 */
	public void setContentLength(int arg0) {
		// TODO Auto-generated method stub
			if (_logger.isInfoEnabled()) 
           _logger.info ("setContentLength called");

	}

	/* (non-Javadoc)
	 * @see javax.servlet.sip.SipServletMessage#setContentType(java.lang.String)
	 */
	public void setContentType(String arg0) {
		// TODO Auto-generated method stub
			if (_logger.isInfoEnabled()) 
           _logger.info ("setContentType called");

	}

	/* (non-Javadoc)
	 * @see javax.servlet.sip.SipServletMessage#getSession()
	 */
	public SipSession getSession() {
		// TODO Auto-generated method stub
			if (_logger.isInfoEnabled()) 
           _logger.info ("getSession called");
		return this.sipSession;
	}

	/* (non-Javadoc)
	 * @see javax.servlet.sip.SipServletMessage#getSession(boolean)
	 */
	public SipSession getSession(boolean arg0) {
		// TODO Auto-generated method stub
		if (_logger.isInfoEnabled()) 
           _logger.info ("getSession caled");
		return sipSession;
	}

	/* (non-Javadoc)
	 * @see javax.servlet.sip.SipServletMessage#getApplicationSession()
	 */
	public SipApplicationSession getApplicationSession() {
		// TODO Auto-generated method stub
		return sipSession.getApplicationSession();
	}

	/* (non-Javadoc)
	 * @see javax.servlet.sip.SipServletMessage#getApplicationSession(boolean)
	 */
	public SipApplicationSession getApplicationSession(boolean arg0) {
		// TODO Auto-generated method stub
		if (_logger.isInfoEnabled()) 
           _logger.info ("getApplicationSession(boolean arg0) called");
		return null;
	}

	/* (non-Javadoc)
	 * @see javax.servlet.sip.SipServletMessage#getAcceptLanguage()
	 */
	public Locale getAcceptLanguage() {
		// TODO Auto-generated method stub
		if (_logger.isInfoEnabled()) 
           _logger.info ("getAcceptLanguage called");
		return null;
	}

	/* (non-Javadoc)
	 * @see javax.servlet.sip.SipServletMessage#getAcceptLanguages()
	 */
	public Iterator getAcceptLanguages() {
		// TODO Auto-generated method stub
		if (_logger.isInfoEnabled()) 
           _logger.info ("getAcceptLanguages called");
		return null;
	}

	/* (non-Javadoc)
	 * @see javax.servlet.sip.SipServletMessage#setAcceptLanguage(java.util.Locale)
	 */
	public void setAcceptLanguage(Locale arg0) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see javax.servlet.sip.SipServletMessage#addAcceptLanguage(java.util.Locale)
	 */
	public void addAcceptLanguage(Locale arg0) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see javax.servlet.sip.SipServletMessage#setContentLanguage(java.util.Locale)
	 */
	public void setContentLanguage(Locale arg0) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see javax.servlet.sip.SipServletMessage#getContentLanguage()
	 */
	public Locale getContentLanguage() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see javax.servlet.sip.SipServletMessage#isCommitted()
	 */
	public boolean isCommitted() {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see javax.servlet.sip.SipServletMessage#getRemoteUser()
	 */
	public String getRemoteUser() {
		// TODO Auto-generated method stub
		return remoteUser;
	}

	/* (non-Javadoc)
	 * @see javax.servlet.sip.SipServletMessage#isUserInRole(java.lang.String)
	 */
	public boolean isUserInRole(String arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see javax.servlet.sip.SipServletMessage#getUserPrincipal()
	 */
	public Principal getUserPrincipal() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see javax.servlet.sip.SipServletMessage#getTransport()
	 */
	public String getTransport() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see com.baypackets.ase.container.AseBaseRequest#getDispatcher()
	 */
	public Dispatcher getDispatcher() {
		// TODO Auto-generated method stub
		return dispatcher;
	}

	/* (non-Javadoc)
	 * @see com.baypackets.ase.container.AseBaseRequest#getProtocolSession()
	 */
	public SasProtocolSession getProtocolSession() {
		// TODO Auto-generated method stub
		return sipSession;
	}
	
	public SasProtocolSession getProtocolSession(boolean create) {
		return sipSession;
	}

	public Object getDestination() {
		return null;
	}

	public void setDestination(Object dest) {
	}

	public void setChainedDownstream() {
	}

	public boolean chainedDownstream() {
		return false;
	}

	public boolean isLoopback() {
		return false;
	}

	public void setLoopback() {
	}

    // members
	URI  requestURI;
	int  maxForwards;
	boolean initial;
	String serverHost;
	int serverPort;
	String remoteAddress;
	String remoteHost;
	int remotePort;
	String localAddress;
	int localPort;
	Address fromAddress;
	Address toAddress;
	String method;
	String callId;
	int expires;
	String remoteUser;
	
	Dispatcher dispatcher;
	AseProtocolSession aseProtoSession;
	DummySipSession sipSession;
   
	public Subject getSubject() {
		// TODO Auto-generated method stub
		return null;
	}

	public void setSubject(Subject subject) {
		// TODO Auto-generated method stub

	}

	public AseIc getIc() {
		return null;
	}

	public void setPrevSession(AseProtocolSession session) {
	}

	public AseProtocolSession getPrevSession() {
		return null;
	}

	/**
         * Sets the priority Message Flag for this message.
         */
        public void setMessagePriority(boolean priority)        {
                priorityMsg = priority;
        }
                                                                                                                             
        /**
         * Returns the priority Message Flag for this message.
         */
        public boolean getMessagePriority()     {
                return priorityMsg;
        }

		public void addAuthHeader(SipServletResponse arg0, AuthInfo arg1) {
			// TODO Auto-generated method stub
			
		}

		public void addAuthHeader(SipServletResponse arg0, String arg1,
				String arg2) {
			// TODO Auto-generated method stub
			
		}

		public B2buaHelper getB2buaHelper() {
			// TODO Auto-generated method stub
			return null;
		}

		public Address getInitialPoppedRoute() {
			// TODO Auto-generated method stub
			return null;
		}

		public Address getPoppedRoute() {
			// TODO Auto-generated method stub
			return null;
		}

		public SipApplicationRoutingRegion getRegion() {
			// TODO Auto-generated method stub
			return null;
		}

		public SipApplicationRoutingDirective getRoutingDirective()
				throws IllegalStateException {
			// TODO Auto-generated method stub
			return null;
		}

		public URI getSubscriberURI() {
			// TODO Auto-generated method stub
			return null;
		}

		public void pushPath(Address arg0) {
			// TODO Auto-generated method stub
			
		}

		public void pushRoute(Address arg0) {
			// TODO Auto-generated method stub
			
		}

		public void setRoutingDirective(SipApplicationRoutingDirective arg0,
				SipServletRequest arg1) throws IllegalStateException {
			// TODO Auto-generated method stub
			
		}

		public void addParameterableHeader(String arg0, Parameterable arg1,
				boolean arg2) {
			// TODO Auto-generated method stub
			
		}

		public HeaderForm getHeaderForm() {
			// TODO Auto-generated method stub
			return null;
		}

		public String getInitialRemoteAddr() {
			// TODO Auto-generated method stub
			return null;
		}

		public int getInitialRemotePort() {
			// TODO Auto-generated method stub
			return 0;
		}

		public String getInitialTransport() {
			// TODO Auto-generated method stub
			return null;
		}

		public Parameterable getParameterableHeader(String arg0)
				throws ServletParseException {
			// TODO Auto-generated method stub
			return null;
		}

		public ListIterator<? extends Parameterable> getParameterableHeaders(
				String arg0) throws ServletParseException {
			// TODO Auto-generated method stub
			return null;
		}

		public void setHeaderForm(HeaderForm arg0) {
			// TODO Auto-generated method stub
			
		}

		public void setParameterableHeader(String arg0, Parameterable arg1) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public AsyncContext getAsyncContext() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public DispatcherType getDispatcherType() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public ServletContext getServletContext() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public boolean isAsyncStarted() {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public boolean isAsyncSupported() {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public AsyncContext startAsync() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public AsyncContext startAsync(ServletRequest arg0, ServletResponse arg1) {
			// TODO Auto-generated method stub
			return null;
		}



}
 

