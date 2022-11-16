
package com.baypackets.ase.teststubs;
import java.util.Iterator;

import javax.servlet.sip.SipURI;

public class DummySipUri implements SipURI {

  public DummySipUri (
    String user,
    String host,
    int port,
    String mAddrParam,
    String methodParam,
    String scheme
  )
  {
    this.user=user;
    this.host=host;
    this.port=port;
    this.mAddrParam=mAddrParam;
    this.methodParam=methodParam;
    this.scheme=scheme;
  }
	/* (non-Javadoc)
	 * @see javax.servlet.sip.SipURI#getUser()
	 */
	public String getUser() {
		// TODO Auto-generated method stub
		return user;
	}

	/* (non-Javadoc)
	 * @see javax.servlet.sip.SipURI#setUser(java.lang.String)
	 */
	public void setUser(String arg0) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see javax.servlet.sip.SipURI#getUserPassword()
	 */
	public String getUserPassword() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see javax.servlet.sip.SipURI#setUserPassword(java.lang.String)
	 */
	public void setUserPassword(String arg0) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see javax.servlet.sip.SipURI#getHost()
	 */
	public String getHost() {
		// TODO Auto-generated method stub
		return host;
	}

	/* (non-Javadoc)
	 * @see javax.servlet.sip.SipURI#setHost(java.lang.String)
	 */
	public void setHost(String arg0) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see javax.servlet.sip.SipURI#getPort()
	 */
	public int getPort() {
		// TODO Auto-generated method stub
		return port;
	}

	/* (non-Javadoc)
	 * @see javax.servlet.sip.SipURI#setPort(int)
	 */
	public void setPort(int arg0) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see javax.servlet.sip.SipURI#isSecure()
	 */
	public boolean isSecure() {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see javax.servlet.sip.SipURI#setSecure(boolean)
	 */
	public void setSecure(boolean arg0) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see javax.servlet.sip.SipURI#getParameter(java.lang.String)
	 */
	public String getParameter(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see javax.servlet.sip.SipURI#setParameter(java.lang.String, java.lang.String)
	 */
	public void setParameter(String arg0, String arg1) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see javax.servlet.sip.SipURI#removeParameter(java.lang.String)
	 */
	public void removeParameter(String arg0) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see javax.servlet.sip.SipURI#getParameterNames()
	 */
	public Iterator getParameterNames() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see javax.servlet.sip.SipURI#getTransportParam()
	 */
	public String getTransportParam() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see javax.servlet.sip.SipURI#setTransportParam(java.lang.String)
	 */
	public void setTransportParam(String arg0) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see javax.servlet.sip.SipURI#getMAddrParam()
	 */
	public String getMAddrParam() {
		// TODO Auto-generated method stub
		return mAddrParam;
	}

	/* (non-Javadoc)
	 * @see javax.servlet.sip.SipURI#setMAddrParam(java.lang.String)
	 */
	public void setMAddrParam(String arg0) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see javax.servlet.sip.SipURI#getMethodParam()
	 */
	public String getMethodParam() {
		// TODO Auto-generated method stub
		return methodParam;
	}

	/* (non-Javadoc)
	 * @see javax.servlet.sip.SipURI#setMethodParam(java.lang.String)
	 */
	public void setMethodParam(String arg0) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see javax.servlet.sip.SipURI#getTTLParam()
	 */
	public int getTTLParam() {
		// TODO Auto-generated method stub
		return 0;
	}

	/* (non-Javadoc)
	 * @see javax.servlet.sip.SipURI#setTTLParam(int)
	 */
	public void setTTLParam(int arg0) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see javax.servlet.sip.SipURI#getUserParam()
	 */
	public String getUserParam() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see javax.servlet.sip.SipURI#setUserParam(java.lang.String)
	 */
	public void setUserParam(String arg0) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see javax.servlet.sip.SipURI#getLrParam()
	 */
	public boolean getLrParam() {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see javax.servlet.sip.SipURI#setLrParam(boolean)
	 */
	public void setLrParam(boolean arg0) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see javax.servlet.sip.SipURI#getHeader(java.lang.String)
	 */
	public String getHeader(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see javax.servlet.sip.SipURI#setHeader(java.lang.String, java.lang.String)
	 */
	public void setHeader(String arg0, String arg1) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see javax.servlet.sip.SipURI#getHeaderNames()
	 */
	public Iterator getHeaderNames() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see javax.servlet.sip.URI#getScheme()
	 */
	public String getScheme() {
		// TODO Auto-generated method stub
		return scheme;
	}

	/* (non-Javadoc)
	 * @see javax.servlet.sip.URI#isSipURI()
	 */
	public boolean isSipURI() {
		// TODO Auto-generated method stub
		return true;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#clone()
	 */
	public SipURI clone() {
		// TODO Auto-generated method stub
		return null;
	}
  String user;
  String host;
  int port;
  String mAddrParam;
  String methodParam;
  String scheme;
public void removeHeader(String arg0) {
	// TODO Auto-generated method stub
	
}

}
