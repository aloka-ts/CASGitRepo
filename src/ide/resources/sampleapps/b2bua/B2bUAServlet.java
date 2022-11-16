package com.baypackets.sampleapps.b2bua;

import java.io.IOException;
import javax.servlet.sip.SipServlet;
import javax.servlet.sip.SipFactory;
import javax.servlet.sip.SipServletRequest;
import javax.servlet.sip.SipServletResponse;
import javax.servlet.sip.SipServletMessage;
import javax.servlet.sip.SipApplicationSession;
import javax.servlet.sip.TooManyHopsException;
import javax.servlet.sip.URI;
import javax.servlet.sip.Proxy;
import javax.servlet.ServletException;
import javax.servlet.ServletContext;


/**
 * This can be used as a base class to develop a Back to back user agent. You are free to 
 * customize and use this class a you see fit.
 * 
 */

public class B2bUAServlet extends SipServlet 
{
 

    /**
     * Header insert to prevent looping
     */
    private static final String REVISIT = "revisit";
    /**
     * Attribute to identify the upstream invite
     */
    private static final String UPSTREAMINVITE = "upstreamInvite";
    /**
     * Attribute to identify the down stream 200 OK
     */
    private static final String DOWNSTREAM200 = "downstream200";
    /**
     * Invite sip message
     */
    private static final String INVITE = "INVITE";
    /**
     * Bye sip message
     */
    private static final String BYE = "BYE";
    /**
     * Flag to indicate a message has been seen already used with the REVISIT header
     */
    private static final String YES = "yes";
    
    /**
     * Key for the progress state of a dialog
     */
    private static final String PROGRESSSTATE = "progressState";
    
    /**
     * Progress state, received 180 ringing message
     */
    private static final String GOT180 = "got180"; 
    
    /**
     * Progress state, received 200 OK message
     */
    private static final String GOT200 = "got200";   
    /**
     * Progress state, received Ack message
     */
    private static final String GOTACK = "gotACK";
    
    //  Keep a handle on the singleton SIP factory object.
    SipFactory m_SipFactory = null;


    /**
     * This method can be used to initialize the state of this SIP Servlet.
     * Initialise the sip factory reference
     */
    public void init() 
    {
        m_SipFactory = (SipFactory)getServletContext().getAttribute(SIP_FACTORY);
    }

    /**
     * Method for processing a SIP INVITE request.
     * @param a_Invite A SIP request object.
     * @throws ServletException Denotes a general error. 
     */
    public void doInvite(SipServletRequest a_Invite) throws ServletException 
    {
    	ServletContext servletContext = getServletContext();
        SipApplicationSession sipAppSession = a_Invite.getApplicationSession();
        //Check for revisit
        String revisitFlag = a_Invite.getHeader(REVISIT);
        if (revisitFlag == null) 
        {
            //Store upstream INVITE
            sipAppSession.setAttribute(UPSTREAMINVITE, a_Invite);
            //Create new downstream Invite
            SipServletRequest downstreamInvite = m_SipFactory.createRequest(sipAppSession, INVITE, 
                                                 a_Invite.getFrom(), a_Invite.getTo());
            try 
            {
                downstreamInvite.setContent(a_Invite.getContent(), a_Invite.getContentType());
            }
            catch (IOException e) 
            {
                throw new ServletException("doInvite. Error attempting to copy sdp to downstream Invite: " + e);
            }
            downstreamInvite.addHeader(REVISIT, YES);
            //Signify to container not  to kill upstream and downstream sessions - will need later
            a_Invite.getSession();
            downstreamInvite.getSession();
            try 
            {
                copyContent(a_Invite, downstreamInvite);
                downstreamInvite.send();
            }
            catch (IOException e) 
            {
                throw new ServletException("doInvite. Error attempting to send downstream Invite: " + e);
            }
            // TODO: Add your own code respond to the attempt to establish a call
        }
        else 
        {
            a_Invite.removeHeader(REVISIT);
            try 
            {
                /* CB added: proxy second-visit invite, but state no interest in subsequent response */
                URI uri = a_Invite.getRequestURI();
                Proxy proxy = a_Invite.getProxy();
                proxy.setSupervised(false);
                proxy.proxyTo(uri);
            }
            catch (TooManyHopsException e) 
            {
                throw new ServletException("doInvite. Error attempting to send revisiting downstream Invite: " + e);
            }
        }
    }


    /**
     * Method processing for a SIP 1xx informational response.
     * @param a_DownstreamResponse A SIP response object.
     * @throws ServletException Denotes a general error. 
     */
    public void doProvisionalResponse(SipServletResponse a_DownstreamResponse) throws ServletException 
    {
        int responseType = a_DownstreamResponse.getStatus();
        if (responseType == 180) 
        {
            SipApplicationSession sipAppSession = a_DownstreamResponse.getApplicationSession();
            sipAppSession.setAttribute(PROGRESSSTATE, GOT180);
            // create new upstream 180 from stored upstream INVITE
            SipServletRequest originalUpstreamInvite = (SipServletRequest)sipAppSession.getAttribute(UPSTREAMINVITE);
            SipServletResponse upstream180response = originalUpstreamInvite.createResponse(180);
            try 
            {
                copyContent(a_DownstreamResponse, upstream180response);
                upstream180response.send();
            }
            catch (IOException e) 
            {
                throw new ServletException("doProv. Error attempting to send upstream 180: " + e);
            }
        }
    }

    /**
     * Method processing for a SIP 2xx success response.
     * @param a_Response A SIP response object.
     * @throws ServletException Denotes a general error. 
     */

    public void doSuccessResponse(SipServletResponse a_Response) throws ServletException 
    {
        SipApplicationSession sipAppSession = a_Response.getApplicationSession(false);
        String responseMethod = a_Response.getMethod();
        if (responseMethod.equals(BYE)) 
        {
            sipAppSession.invalidate();
        }
        else 
        {
            sipAppSession.setAttribute(PROGRESSSTATE, GOT200);
            sipAppSession.setAttribute(DOWNSTREAM200, a_Response);
            SipServletRequest originalUpstreamInvite = (SipServletRequest)sipAppSession.getAttribute(UPSTREAMINVITE);
            SipServletResponse upstream200response = originalUpstreamInvite.createResponse(200);
            try 
            {
                upstream200response.setContent(a_Response.getContent(), a_Response.getContentType());
                upstream200response.send();
            }
            catch (IOException e) 
            {
                throw new ServletException("doSuccess. Error manipulating new upstream 200: " + e);
            }
            // TODO: add your own code to respond to call establishment, i.e. start billing
        }
    }

    /**
     * Method processing for a SIP ACK request.
     * @param a_UpstreamAck A SIP request object.
     * @throws ServletException Denotes a general error. 
     */
    public void doAck(SipServletRequest a_UpstreamAck) throws ServletException 
    {
        SipApplicationSession sipAppSession = a_UpstreamAck.getApplicationSession();
        sipAppSession.setAttribute(PROGRESSSTATE, GOTACK);
        SipServletResponse originalDownstream200 = (SipServletResponse)sipAppSession.getAttribute(DOWNSTREAM200);
        try 
        {
            SipServletRequest newAck = originalDownstream200.createAck();
            copyContent(a_UpstreamAck, newAck);
            newAck.send();
        }
        catch (Exception e) 
        {
            throw new ServletException("doAck. Error creating or sending new downstream ACK: " + e);
        }
    }

    /**
     * Copies the contents of msg1 to msg2.
     * @param a_Msg1 message copied from
     * @param a_Msg2 message copied to
     * @throws ServletException if a problem is encountered moving the content between the messages
     */
    private void copyContent(SipServletMessage a_Msg1, SipServletMessage a_Msg2) throws ServletException
    {
        try 
        {
            if (a_Msg1.getContentType() != null) 
            {
                a_Msg2.setContent(a_Msg1.getRawContent(), 
                    a_Msg1.getContentType());
            }
        } 
        catch (IOException ex) 
        {
            throw new ServletException("Error copying content across messages: " + ex);
        }
    }   

    /**
     * Method processing for a SIP BYE request.
     * @param request A SIP request object.
     * @throws ServletException Denotes a general error. 
     */
    public void doBye(SipServletRequest a_ByeRequest) throws ServletException 
    {
        try 
        {
            a_ByeRequest.createResponse(200).send();
        }
        catch (IOException e) 
        {
            throw new ServletException("doBye. Error creating or sending new 200 to BYE: " + e);
        }
        //Find out who sent the BYE so we can send a BYE to the other party
        SipApplicationSession sipAppSession = a_ByeRequest.getApplicationSession();
        SipServletRequest originalUpstreamInvite = (SipServletRequest)sipAppSession.getAttribute(UPSTREAMINVITE);
        String currentCallId = a_ByeRequest.getCallId();
        if (originalUpstreamInvite.getCallId().equals(currentCallId)) 
        {
            SipServletResponse originalDownstream200 = (SipServletResponse)sipAppSession.getAttribute(DOWNSTREAM200);

            try 
            {
                SipServletRequest newByeRequest = originalDownstream200.getSession().createRequest(BYE);
                copyContent(a_ByeRequest, newByeRequest);
                newByeRequest.send();
            }
            catch (IOException e) 
            {
                throw new ServletException("doBye. Error creating or sending new downstream BYE: " + e);
            }
        }
        else 
        {
            try 
            {
                SipServletRequest newByeRequest = originalUpstreamInvite.getSession().createRequest(BYE);
                copyContent(a_ByeRequest, newByeRequest);
                newByeRequest.send();
            }
            catch (IOException e) 
            {
                throw new ServletException("doBye. Error creating or sending new upstream BYE: " + e);
            }
        }
        // TODO: add your own code to respond to the bye, e.g. create a billing record
    }
}
