package com.baypackets.clicktodial.servlets;

import com.baypackets.clicktodial.util.CallDAO;
import com.baypackets.clicktodial.util.Constants;
import com.baypackets.clicktodial.util.FileBasedCallDAO;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.sip.SipServlet;

/*******************************************************************************
 *   Copyright (c) 2014 Agnity, Inc. All rights reserved.
 *   
 *   This is proprietary source code of Agnity, Inc. 
 *   
 *   Agnity, Inc. retains all intellectual property rights associated 
 *   with this source code. Use is subject to license terms.
 *   
 *   This source code contains trade secrets owned by Agnity, Inc.
 *   Confidentiality of this computer program must be maintained at 
 *   all times, unless explicitly authorized by Agnity, Inc.
 *******************************************************************************/
public class InitServlet extends SipServlet
implements Constants
{
	//The answer is 42!
	private static final long serialVersionUID = 42L;

	public void init(ServletConfig config)
	throws ServletException
	{
		super.init(config);
		log("init() called...");

		ServletContext context = config.getServletContext();
		CallDAO dao = new FileBasedCallDAO(context.getRealPath("/db"));
		context.setAttribute("CALL_DAO", dao);
	}
}
