package com.genband.m5.maps.mgmt;

import org.omg.PortableInterceptor.*;


/**
 * Title:
 * Description:
 * Copyright:    Copyright (c) 2002
 * Company:
 * @author
 * @version 1.0
 */

public class ClientInitializer extends org.omg.CORBA.LocalObject implements
		ORBInitializer {
	public ClientInitializer() {
	}

	public void post_init(ORBInitInfo info) {

		try {
			ClientRequestInterceptor ci = null;
			ci = new ClientForwardInterceptor();
			m_refCfi = (ClientForwardInterceptor) ci;
			info.add_client_request_interceptor(ci);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void pre_init(ORBInitInfo info) {
	}

	public static ClientForwardInterceptor m_refCfi;

}

