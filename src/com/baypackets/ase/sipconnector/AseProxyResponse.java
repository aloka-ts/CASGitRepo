/*
 * Created on Dec 7, 2004
 * 
 */
package com.baypackets.ase.sipconnector;

/**
 * @author BayPackets
 *
 */
public class AseProxyResponse {

	/**
	 * Not used 
	 */
	public AseProxyResponse() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * 
	 */
	public AseProxyResponse(AseSipServletResponse resp,
				AseProxyBranch branch) {
		m_branch = branch;
		m_response = resp;
	}

	
	/**
	 * @return
	 */
	public AseProxyBranch getBranch() {
		return m_branch;
	}

	/**
	 * @return
	 */
	public boolean isForwarded() {
		return m_isForwarded;
	}

	/**
	 * @return
	 */
	public boolean isSupervised() {
		return m_isSupervised;
	}

	/**
	 * @return
	 */
	public AseSipServletResponse getResponse() {
		return m_response;
	}

	/**
	 * @param branch
	 */
	public void setBranch(AseProxyBranch branch) {
		this.m_branch = branch;
	}

	/**
	 * @param b
	 */
	public void setForwarded(boolean b) {
		m_isForwarded = b;
	}

	/**
	 * @param b
	 */
	public void setSupervised(boolean b) {
		m_isSupervised = b;
	}

	/**
	 * @param response
	 */
	public void setResponse(AseSipServletResponse response) {
		this.m_response = response;
	}


	public static void main(String[] args) {
	}
	
	private boolean m_isForwarded;
	private boolean m_isSupervised;
	private AseProxyBranch m_branch;
	private AseSipServletResponse m_response;
	

}
