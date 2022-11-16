package com.genband.m5.maps.mgmt;

import org.omg.PortableInterceptor.*;

import org.omg.IOP.*;

/**
 * Title: Description: Copyright: Copyright (c) 2002 Company:
 * 
 * @author
 * @version 1.0
 */

public class ClientForwardInterceptor extends org.omg.CORBA.LocalObject
		implements ClientRequestInterceptor {
	
	private static String m_userName;
	private static int m_iconsoleId;
	
	private static ServiceContext m_sc;

	public ClientForwardInterceptor() {
		System.out.println("######Inside Constructor");

	}

	public void setParams(String userName, int id) {

		System.out.println("Setting the params user id = " + userName
				+ " session id = " + id);
		m_userName = userName;
		m_iconsoleId = id;
		String contextString = m_userName + ":" + m_iconsoleId;
		System.out.println(" The context String is = " + contextString);

		m_sc = new ServiceContext(1, contextString.getBytes());
	}

	public String name() {

		return "ClientForwardInterceptor";

	}

	public void send_request(ClientRequestInfo ri) throws ForwardRequest {

		System.out.println(" The user id is  = " + m_userName
				+ "session id is =" + m_iconsoleId);

		if (null != m_sc)
			System.out.println(" Service Context is not null");
		else
			System.out.println(" Service Context is null");

		if (!(ri.target() instanceof RSIEms._ConsoleSessionStub)) {

			System.out.println("Not a instance of console session "
					+ ri.target().getClass().getName());

		} else {

			System.out.println(" MY Instance of console session "
					+ ri.target().getClass().getName());

			ri.add_request_service_context(m_sc, true);
		}

	}

	public void send_poll(ClientRequestInfo ri) {
	}

	public void receive_reply(ClientRequestInfo ri) {
	}

	public void receive_exception(ClientRequestInfo ri) throws ForwardRequest {
	}

	public void receive_other(ClientRequestInfo ri) throws ForwardRequest {
	}

	public void destroy() {
	}

}

