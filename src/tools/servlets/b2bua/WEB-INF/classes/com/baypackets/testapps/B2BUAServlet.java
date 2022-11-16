package com.baypackets.testapps;

import java.io.IOException;
import java.util.Iterator;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.sip.Address;
import javax.servlet.sip.SipFactory;
import javax.servlet.sip.SipServlet;
import javax.servlet.sip.SipServletMessage;
import javax.servlet.sip.SipServletRequest;
import javax.servlet.sip.SipServletResponse;
import javax.servlet.sip.SipSession;
import javax.servlet.sip.SipURI;
import javax.servlet.sip.SipApplicationSession;
import javax.servlet.sip.TimerService;
import javax.servlet.sip.TimerListener;


/**
 * <p>Simple application illustrating how to write a back-to-back user
 * agent (B2BUA).</p>
 * 
 * <p>To build the application, three jar files are required: 
 * <code>sipservlet.jar,
 * servlet-2.4.jar</code> and <code>log4j-1.2.8.jar</code></p>
 */


public class B2BUAServlet extends SipServlet
{

  private static final String PEER_REQ = "PEER-REQUEST";
  private static final String INVITE = "INVITE";
  private static final String RESP_INV = "2XX-INVITE";
  private static final String ROLE = "ROLE";

  private SipFactory sipFactory;
  private static String calleeIP ;
  private static int calleePort ;

  public void init () throws ServletException
  {
    try
    {
      ServletContext sc = (ServletContext) getServletContext ();
      sipFactory = (SipFactory) sc.getAttribute (SIP_FACTORY);
      calleeIP = sc.getInitParameter ("callee_ip");
      calleePort = Integer.parseInt(sc.getInitParameter ("callee_port"));
    } catch (Exception e)
    {
      log ("Exception while initializing the Servlet: " + e);
    }
  }


  protected void doInvite (SipServletRequest req1)
    throws ServletException, IOException
  {
    log ("b2bua doInvite: " + req1.getRequestURI ());

    try
    {
      SipSession leg1 = req1.getSession ();
      SipSession leg2 = null;
      SipServletRequest req2 = null;

      if (req1.isInitial ())
	{
	  log ("Relaying initial INVITE");

	    //req2 = sipFactory.createRequest (req1.getApplicationSession (),
	    //				     "INVITE", req1.getFrom (),
	    //				     req1.getTo ());
	    req2 = sipFactory.createRequest (req1, true);
	  SipURI sipURI = (SipURI) req1.getRequestURI ().clone ();
	    log ("Setting Host part of outgoing request URI to: " + calleeIP);
	    sipURI.setHost (calleeIP);
	    sipURI.setPort (calleePort);
	    req2.setRequestURI (sipURI);
	    leg2 = req2.getSession ();

	  // Set roles
	    leg1.setAttribute (ROLE, "caller");
	    leg2.setAttribute (ROLE, "callee");
	}
      else
	{
	  log ("Relaying re-INVITE from " + getRole (req1));

	  leg2 =
	    getPeerSession (req1,
			(String) req1.getSession ().getAttribute (ROLE));
	  req2 = leg2.createRequest ("INVITE");
	}

      log ("\"relaying\" INVITE to " + req2.getRequestURI ());
      copyContent (req1, req2);

      // Store req1 as attr of req2 so we can "forward" response from callee
      req2.setAttribute (PEER_REQ, req1);

      // Store 2nd INVITE on incoming INVITEs SipSession. This allows
      // us to "forward" a subsequent CANCEL
      leg1.setAttribute (INVITE, req2);

      req2.send ();
    }
    catch (Exception e)
    {
      log ("Exception in doInvite: " + e);
    }
  }


/**
 * Receive ACK from one UA. Relay it to the other UA with the same content.
 *
 * @param	ack1	Sip Servlet Request
 * @throws	On error throws either a ServletException or IOException
 */
  protected void doAck (SipServletRequest ack1) throws ServletException,
    IOException
  {
    log ("b2bua doAck: " + getSummary (ack1));
    SipSession leg1 = ack1.getSession ();
    SipSession leg2 =
      getPeerSession (ack1, (String) ack1.getSession ().getAttribute (ROLE));
    SipServletResponse resp2 =
      (SipServletResponse) leg2.getAttribute (RESP_INV);
    SipServletRequest ack2 = resp2.createAck ();
      copyContent (ack1, ack2);

      leg1.removeAttribute (INVITE);
      leg2.removeAttribute (RESP_INV);
      ack1.removeAttribute (PEER_REQ);
      ack2.removeAttribute (PEER_REQ);
      ack2.send ();
  }


/**
 * Relay the incoming BYE to peer UA.
 *
 * @param	req1	Sip Servlet Request
 * @throws	On error throws either a ServletException or IOException
 */
  protected void doBye (SipServletRequest req)
    throws ServletException, IOException
  {
    log ("b2bua doBye: " + getSummary (req));

    SipSession leg = req.getSession ();
      log ("received from: " + leg.getAttribute (ROLE));

    String role = (String) leg.getAttribute (ROLE);

    SipSession leg2 = getPeerSession (req, role);

    SipServletRequest req2 = leg2.createRequest ("BYE");

    // store req1 so we can forward response later
    req2.setAttribute (PEER_REQ, req);

    req2.send ();
  }



/**
 * Relay the incoming CANCEL to peer UA. For simplicity it is assumed
 * that only the initial INVITE is cancelled.
 *
 * @param	req1	Sip Servlet Request
 * @throws	On error throws either a ServletException or IOException
 */
  protected void doCancel (SipServletRequest req1)
    throws ServletException, IOException
  {
    log ("b2bus doCancel: " + getSummary (req1));
    SipSession leg1 = req1.getSession ();
    SipSession leg2 =
      getPeerSession (req1, (String) req1.getSession ().getAttribute (ROLE));
    SipServletRequest req2 = (SipServletRequest) leg1.getAttribute (INVITE);
    SipServletRequest cancel = req2.createCancel ();
      cancel.send ();
  }


/**
 * Relay incoming 1xx responses other than 100.
 *
 * @param	resp	Sip Servlet Response
 * @throws	On error throws either a ServletException or IOException
 */
  protected void doProvisionalResponse (SipServletResponse resp)
    throws ServletException, IOException
  {
    log ("b2bua doProvisionalResponse: " + getSummary (resp));
    if (resp.getStatus () > 100)
      forward (resp);
    else if (resp.getStatus () == 100)
      resp.send ();
  }



/**
 * Relay final error responses.
 *
 * @param	resp	Sip Servlet Response
 * @throws	On error throws either a ServletException or IOException
 */
  protected void doErrorResponse (SipServletResponse resp)
    throws ServletException, IOException
  {
    log ("b2bua doErrorResponse: " + getSummary (resp));
    forward (resp);
  }



/**
 * Relay final success responses and invalidate the app session
 * if a BYE succeeded.
 *
 * @param	resp	Sip Servlet Response
 * @throws	On error throws either a ServletException or IOException
 */
  protected void doSuccessResponse (SipServletResponse resp)
    throws ServletException, IOException
  {
    log ("b2bua doSuccessResponse: " + getSummary (resp));
    if ("INVITE".equals (resp.getMethod ()))
      {
	// Store response object so we can forward ACK when we receive
	// it from the UAC. 
	resp.getSession ().setAttribute (RESP_INV, resp);
      }
    forward (resp);
    // terminate the app session on successful BYE and CANCEL
    if ("BYE".equals (resp.getMethod ()))
      {
	resp.getApplicationSession ().invalidate ();
      }
    else if ("CANCEL".equals (resp.getMethod ()))
      {
	resp.getApplicationSession ().invalidate ();
      }
  }



/**
 * Relays a response received on one dialog to the other dialog.
 *
 * @param	resp1	Sip Servlet Response
 * @throws	On error throws either a ServletException or IOException
 */
  private void forward (SipServletResponse resp1)
    throws ServletException, IOException
  {
    log ("\"forwarding\" " + getSummary (resp1));
    SipServletRequest req1 = resp1.getRequest ();
    SipServletRequest req2 = (SipServletRequest) req1.getAttribute (PEER_REQ);

    if (req2 == null)
      {
	throw new ServletException ("Failed to forward " +
				    getSummary (resp1) +
				    " - no outstanding request on peer leg");
      }

    int sc = resp1.getStatus ();
    SipServletResponse resp2 =
      req2.createResponse (sc, resp1.getReasonPhrase ());
    copyContent (resp1, resp2);
    resp2.send ();
  }



/**
 * Copies the contents of msg1 to msg2.
 *
 * @param	msg1	Sip Servlet Message
 * 			msg2	Sip Servlet Message
 */
  private void copyContent (SipServletMessage msg1, SipServletMessage msg2)
  {
    try
    {
      if (msg1.getContentType () != null)
	{
	  msg2.setContent (msg1.getRawContent (), msg1.getContentType ());
	}
    }
    catch (IOException e)
    {
      log ("Error: " + e);
    }
  }



/**
 * Returns one-line description of the specified request object.
 *
 * @param	resp	Sip Servlet Request.
 * @return	request method and from role as a String.
 */
  private String getSummary (SipServletRequest req)
  {
    return "" + req.getMethod () + " from " + getRole (req);
  }



/**
 * Returns one-line description of the specified response object.
 *
 * @param	resp	Sip Servlet Response.
 * @return	response status/method/from role as a String.
 */
  private String getSummary (SipServletResponse resp)
  {
    return "" + resp.getStatus () + "/" + resp.getMethod () +
      " from " + getRole (resp);
  }




/**
 * Returns role ("caller" or "callee") of the arguments SipSession.
 *
 * @param 	msg		Sip Servlet Message
 * @return	SipSession's ROLE attribute as a String if
 * <code>SipServletMessage.getSession().getAttribute(ROLE)</code>
 * is not null;
 * null if <code>SipServletMessage.getSession().getAttribute(ROLE)</code>
 * is null;
 */
  private String getRole (SipServletMessage msg)
  {
    SipSession leg = msg.getSession ();
    return leg == null ? null : (String) leg.getAttribute (ROLE);
  }




/**
 * If <code>aRole</code> equals callee, then Returns the SIP Session
 * of the caller.
 * <p>
 * If <code>aRole</code> equals caller, then Returns the SIP Session
 * of the callee.
 *<p>
 * Returns null, If <code>aRole</code> is null OR
 * the Iterator <code>req1.getApplicationSession().getSessions()</code>
 * is null.
 *<p>
 * @param	req1   	Sip Servlet Request.
 * @param   aRole	either callee/caller.
 * @return  a Sip Session attached with either callee/caller depending
 * on the aRole argument.
 */
  private SipSession getPeerSession (SipServletRequest req1, String aRole)
  {
    String peer_role = null;
    if (aRole == null || aRole.trim ().equals (""))
      return null;
    if (aRole.equals ("callee"))
      peer_role = "caller";
    else
      peer_role = "callee";

    Iterator it = req1.getApplicationSession ().getSessions ();
    if (it != null)
      {
	Object obj;
	while (it.hasNext ())
	  {
	    obj = (Object) it.next ();
	    if (obj instanceof SipSession)
	      {
		if (((SipSession) obj).getAttribute (ROLE).equals (peer_role))
		  {
		    return (SipSession) obj;
		  }
	      }
	  }
      }
    return null;
  }
}
