
package com.baypackets.ase.teststubs;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.security.Principal;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.Locale;

import javax.servlet.ServletOutputStream;
import javax.servlet.sip.Address;
import javax.servlet.sip.Parameterable;
import javax.servlet.sip.Proxy;
import javax.servlet.sip.ProxyBranch;
import javax.servlet.sip.Rel100Exception;
import javax.servlet.sip.ServletParseException;
import javax.servlet.sip.SipApplicationSession;
import javax.servlet.sip.SipServletRequest;
import javax.servlet.sip.SipServletResponse;
import javax.servlet.sip.SipSession;

import com.baypackets.ase.container.AseBaseRequest;
import com.baypackets.ase.container.AseBaseResponse;
import com.baypackets.ase.container.AseProtocolSession;
import com.baypackets.ase.spi.container.AbstractSasMessage;
import com.baypackets.ase.spi.container.SasProtocolSession;
import com.baypackets.ase.dispatcher.Destination;


public class DummySipServletResponse extends AbstractSasMessage
			implements AseBaseResponse, SipServletResponse {

  public DummySipServletResponse (
    int responseCode
  )
  {
    this.responseCode = responseCode; 
  }

  public DummySipServletResponse (
    int responseCode,
    String responseString
  )
  {
    this.responseCode = responseCode; 
    this.responseString = responseString; 
  }
	/* (non-Javadoc)
	 * @see javax.servlet.sip.SipServletResponse#getRequest()
	 */
	public SipServletRequest getRequest() {
		// TODO Auto-generated method stub
		return null;
	}

	public AseBaseRequest getBaseRequest() {
		return null;
	}

	/* (non-Javadoc)
	 * @see javax.servlet.sip.SipServletResponse#getStatus()
	 */
	public int getStatus() {
		// TODO Auto-generated method stub
		return 0;
	}

	/* (non-Javadoc)
	 * @see javax.servlet.sip.SipServletResponse#setStatus(int)
	 */
	public void setStatus(int arg0) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see javax.servlet.sip.SipServletResponse#setStatus(int, java.lang.String)
	 */
	public void setStatus(int arg0, String arg1) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see javax.servlet.sip.SipServletResponse#getReasonPhrase()
	 */
	public String getReasonPhrase() {
		// TODO Auto-generated method stub
		return responseString;
	}

	/* (non-Javadoc)
	 * @see javax.servlet.ServletResponse#getOutputStream()
	 */
	public ServletOutputStream getOutputStream() throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see javax.servlet.ServletResponse#getWriter()
	 */
	public PrintWriter getWriter() throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see javax.servlet.sip.SipServletResponse#getProxy()
	 */
	public Proxy getProxy() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see javax.servlet.sip.SipServletResponse#sendReliably()
	 */
	public void sendReliably() throws Rel100Exception {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see javax.servlet.sip.SipServletMessage#send()
	 */
	public void send() throws IOException {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see javax.servlet.sip.SipServletResponse#createAck()
	 */
	public SipServletRequest createAck() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see javax.servlet.sip.SipServletMessage#getCharacterEncoding()
	 */
	public String getCharacterEncoding() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see javax.servlet.sip.SipServletMessage#getContentType()
	 */
	public String getContentType() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see javax.servlet.sip.SipServletMessage#setCharacterEncoding(java.lang.String)
	 */
	public void setCharacterEncoding(String arg0) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see javax.servlet.sip.SipServletMessage#setContentLength(int)
	 */
	public void setContentLength(int arg0) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see javax.servlet.sip.SipServletMessage#setContentType(java.lang.String)
	 */
	public void setContentType(String arg0) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see javax.servlet.ServletResponse#setBufferSize(int)
	 */
	public void setBufferSize(int arg0) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see javax.servlet.ServletResponse#getBufferSize()
	 */
	public int getBufferSize() {
		// TODO Auto-generated method stub
		return 0;
	}

	/* (non-Javadoc)
	 * @see javax.servlet.ServletResponse#flushBuffer()
	 */
	public void flushBuffer() throws IOException {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see javax.servlet.ServletResponse#resetBuffer()
	 */
	public void resetBuffer() {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see javax.servlet.sip.SipServletMessage#isCommitted()
	 */
	public boolean isCommitted() {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see javax.servlet.ServletResponse#reset()
	 */
	public void reset() {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see javax.servlet.ServletResponse#setLocale(java.util.Locale)
	 */
	public void setLocale(Locale arg0) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see javax.servlet.ServletResponse#getLocale()
	 */
	public Locale getLocale() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see javax.servlet.sip.SipServletMessage#getFrom()
	 */
	public Address getFrom() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see javax.servlet.sip.SipServletMessage#getTo()
	 */
	public Address getTo() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see javax.servlet.sip.SipServletMessage#getMethod()
	 */
	public String getMethod() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see javax.servlet.sip.SipServletMessage#getProtocol()
	 */
	public String getProtocol() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see javax.servlet.sip.SipServletMessage#getHeader(java.lang.String)
	 */
	public String getHeader(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see javax.servlet.sip.SipServletMessage#getHeaders(java.lang.String)
	 */
	public ListIterator getHeaders(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see javax.servlet.sip.SipServletMessage#getHeaderNames()
	 */
	public Iterator getHeaderNames() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see javax.servlet.sip.SipServletMessage#setHeader(java.lang.String, java.lang.String)
	 */
	public void setHeader(String arg0, String arg1) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see javax.servlet.sip.SipServletMessage#addHeader(java.lang.String, java.lang.String)
	 */
	public void addHeader(String arg0, String arg1) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see javax.servlet.sip.SipServletMessage#removeHeader(java.lang.String)
	 */
	public void removeHeader(String arg0) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see javax.servlet.sip.SipServletMessage#getAddressHeader(java.lang.String)
	 */
	public Address getAddressHeader(String arg0) throws ServletParseException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see javax.servlet.sip.SipServletMessage#getAddressHeaders(java.lang.String)
	 */
	public ListIterator getAddressHeaders(String arg0)
		throws ServletParseException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see javax.servlet.sip.SipServletMessage#setAddressHeader(java.lang.String, javax.servlet.sip.Address)
	 */
	public void setAddressHeader(String arg0, Address arg1) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see javax.servlet.sip.SipServletMessage#addAddressHeader(java.lang.String, javax.servlet.sip.Address, boolean)
	 */
	public void addAddressHeader(String arg0, Address arg1, boolean arg2) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see javax.servlet.sip.SipServletMessage#getCallId()
	 */
	public String getCallId() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see javax.servlet.sip.SipServletMessage#getExpires()
	 */
	public int getExpires() {
		// TODO Auto-generated method stub
		return 0;
	}

	/* (non-Javadoc)
	 * @see javax.servlet.sip.SipServletMessage#setExpires(int)
	 */
	public void setExpires(int arg0) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see javax.servlet.sip.SipServletMessage#getContentLength()
	 */
	public int getContentLength() {
		// TODO Auto-generated method stub
		return 0;
	}

	/* (non-Javadoc)
	 * @see javax.servlet.sip.SipServletMessage#getRawContent()
	 */
	public byte[] getRawContent() throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see javax.servlet.sip.SipServletMessage#getContent()
	 */
	public Object getContent()
		throws IOException, UnsupportedEncodingException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see javax.servlet.sip.SipServletMessage#setContent(java.lang.Object, java.lang.String)
	 */
	public void setContent(Object arg0, String arg1)
		throws UnsupportedEncodingException {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see javax.servlet.sip.SipServletMessage#getAttribute(java.lang.String)
	 */
	public Object getAttribute(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see javax.servlet.sip.SipServletMessage#getAttributeNames()
	 */
	public Enumeration getAttributeNames() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see javax.servlet.sip.SipServletMessage#setAttribute(java.lang.String, java.lang.Object)
	 */
	public void setAttribute(String arg0, Object arg1) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see javax.servlet.sip.SipServletMessage#getSession()
	 */
	public SipSession getSession() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see javax.servlet.sip.SipServletMessage#getSession(boolean)
	 */
	public SipSession getSession(boolean arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see javax.servlet.sip.SipServletMessage#getApplicationSession()
	 */
	public SipApplicationSession getApplicationSession() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see javax.servlet.sip.SipServletMessage#getApplicationSession(boolean)
	 */
	public SipApplicationSession getApplicationSession(boolean arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see javax.servlet.sip.SipServletMessage#getAcceptLanguage()
	 */
	public Locale getAcceptLanguage() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see javax.servlet.sip.SipServletMessage#getAcceptLanguages()
	 */
	public Iterator getAcceptLanguages() {
		// TODO Auto-generated method stub
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
	 * @see javax.servlet.sip.SipServletMessage#isSecure()
	 */
	public boolean isSecure() {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see javax.servlet.sip.SipServletMessage#getRemoteUser()
	 */
	public String getRemoteUser() {
		// TODO Auto-generated method stub
		return null;
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
	 * @see javax.servlet.sip.SipServletMessage#getLocalAddr()
	 */
	public String getLocalAddr() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see javax.servlet.sip.SipServletMessage#getLocalPort()
	 */
	public int getLocalPort() {
		// TODO Auto-generated method stub
		return 0;
	}

	/* (non-Javadoc)
	 * @see javax.servlet.sip.SipServletMessage#getRemoteAddr()
	 */
	public String getRemoteAddr() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see javax.servlet.sip.SipServletMessage#getRemotePort()
	 */
	public int getRemotePort() {
		// TODO Auto-generated method stub
		return 0;
	}

	/* (non-Javadoc)
	 * @see javax.servlet.sip.SipServletMessage#getTransport()
	 */
	public String getTransport() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see com.baypackets.ase.container.AseBaseResponse#getProtocolSession()
	 */
	public SasProtocolSession getProtocolSession() {
		// TODO Auto-generated method stub
		return null;
	}

	public SasProtocolSession getProtocolSession(boolean create) {
		return null;
	}

	public void setPrevSession(AseProtocolSession session) {
	}

	public AseProtocolSession getPrevSession() {
		return null;
	}

  int responseCode;
  String responseString;

  public Destination m_destination;

  public void setDestination(Object destination)
  {
  	m_destination=(Destination)destination;
  }

  public Object getDestination()
  {
  	return m_destination;
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

		public SipServletRequest createPrack() throws Rel100Exception {
			// TODO Auto-generated method stub
			return null;
		}

		public Iterator<String> getChallengeRealms() {
			// TODO Auto-generated method stub
			return null;
		}

		public ProxyBranch getProxyBranch() {
			// TODO Auto-generated method stub
			return null;
		}

		public boolean isBranchResponse() {
			// TODO Auto-generated method stub
			return false;
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

		public void removeAttribute(String arg0) {
			// TODO Auto-generated method stub
			
		}

		public void setHeaderForm(HeaderForm arg0) {
			// TODO Auto-generated method stub
			
		}

		public void setParameterableHeader(String arg0, Parameterable arg1) {
			// TODO Auto-generated method stub
			
		}

}

