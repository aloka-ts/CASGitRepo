package com.baypackets.ase.teststubs;

import java.util.Iterator;
import java.util.Set;
import java.util.Map.Entry;

import javax.servlet.sip.Address;
import javax.servlet.sip.URI;

public class DummyAddress implements Address {

  public DummyAddress (URI uri, String displayName) {
    this.uri = uri;
    this.displayName = displayName;
  }
	/* (non-Javadoc)
	 * @see javax.servlet.sip.Address#getDisplayName()
	 */
	public String getDisplayName() {
		// TODO Auto-generated method stub
		return displayName;
	}

	/* (non-Javadoc)
	 * @see javax.servlet.sip.Address#setDisplayName(java.lang.String)
	 */
	public void setDisplayName(String arg0) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see javax.servlet.sip.Address#getURI()
	 */
	public URI getURI() {
		// TODO Auto-generated method stub
		return uri;
	}

	/* (non-Javadoc)
	 * @see javax.servlet.sip.Address#setURI(javax.servlet.sip.URI)
	 */
	public void setURI(URI arg0) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see javax.servlet.sip.Address#getParameter(java.lang.String)
	 */
	public String getParameter(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see javax.servlet.sip.Address#setParameter(java.lang.String, java.lang.String)
	 */
	public void setParameter(String arg0, String arg1) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see javax.servlet.sip.Address#removeParameter(java.lang.String)
	 */
	public void removeParameter(String arg0) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see javax.servlet.sip.Address#getParameterNames()
	 */
	public Iterator getParameterNames() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see javax.servlet.sip.Address#isWildcard()
	 */
	public boolean isWildcard() {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see javax.servlet.sip.Address#getQ()
	 */
	public float getQ() {
		// TODO Auto-generated method stub
		return 0;
	}

	/* (non-Javadoc)
	 * @see javax.servlet.sip.Address#setQ(float)
	 */
	public void setQ(float arg0) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see javax.servlet.sip.Address#getExpires()
	 */
	public int getExpires() {
		// TODO Auto-generated method stub
		return 0;
	}

	/* (non-Javadoc)
	 * @see javax.servlet.sip.Address#setExpires(int)
	 */
	public void setExpires(int arg0) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see java.lang.Object#clone()
	 */
	public Object clone(){
		// TODO Auto-generated method stub
		return null;
	}

  URI uri;
  String displayName;
public Set<Entry<String, String>> getParameters() {
	// TODO Auto-generated method stub
	return null;
}
public String getValue() {
	// TODO Auto-generated method stub
	return null;
}
public void setValue(String arg0) {
	// TODO Auto-generated method stub
	
}
}
