/**
 *	Filename:	MsSipDialog.java
 *	Created On:	23-Jan-2007
 */

package com.genband.mssim;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Map;
import java.util.HashMap;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.util.URIUtil;

import org.apache.log4j.Logger;
import com.dynamicsoft.DsLibs.DsUtil.*;
import com.dynamicsoft.DsLibs.DsSipObject.*;
import com.dynamicsoft.DsLibs.DsSipParser.*;
import com.dynamicsoft.DsLibs.DsSipDialog.*;
import com.dynamicsoft.DsLibs.DsSipLlApi.*;

import com.genband.threadpool.Work;

/**
 *
 *
 */
public class MsSipDialog
	implements DsSipInvitationInterface, DsSipInviteDialogInterface, DsSipReinvitationInterface {

	private static Logger logger = Logger.getLogger(MsSipDialog.class);

    /////////////////  private static data ////////////////////////////
	private static MsScheduler m_getReqSch;
	private static MsScheduler m_finalRespSch;
	private static MsScheduler m_iterTimeSch;
	private static int m_finalResp;

    // some random  values to use in our messages
    private final static DsByteString BODY_TYPE = new DsByteString("application/sdp");
    private final static byte[] BOGUS_BODY =
        ( "v=0\r\n" +
          "o=bell 628504043 608504006 IN IP4 128.25.25.25\r\n"  +
          "s=Mediaserver simulator......\r\n"  +
          "c=IN IP4 me.com\r\n" +
          "e=ivr@genband.com\r\n"  +
          "m=video 3456 RTP/AVP 17").getBytes();

    /////////////////  private data ////////////////////////////
    private DsSipInvitation m_invitation;
    private DsSipInviteDialog m_dialog;
	private String m_url;
	private int m_callState = MsSimulator.STATE_A;

    /**
     * Server Side.
     * @throws Exception for simplicity of this example
     */
    MsSipDialog(DsSipInvitation invitation) {
		m_invitation = invitation;
		try {
			m_invitation.setInterfaces(this, this, this);
		} catch(Exception exp) {
			logger.error("Setting intefaces", exp);
		}
    }

	public static void initialize() {
		m_getReqSch = MsScheduler.getScheduler(MsSimulator.getReqDelay());
		m_finalRespSch = MsScheduler.getScheduler(MsSimulator.getFinalRespDelay());
		m_iterTimeSch = MsScheduler.getScheduler(MsSimulator.getInteractionPeriod());
		m_finalResp = MsSimulator.getFinalRespCode();
	}

    // ///////////////////  DsSipInvitationInterface implementation ///////////////
    //

    public void cancelled(DsSipInvitation invitation, DsSipCancelMessage cancel) {
        logger.debug("Invitation cancelled");
        logger.debug("------------------- CANCEL ------------------" );
        logger.debug(cancel);
        logger.debug("--------------- end of CANCEL ---------------" );
    }

    public void proceeding(DsSipInvitation invitation, DsSipResponse response,  DsSipInviteDialog dialog) {
        logger.debug("Invitation proceeding (dialog)");
        logger.debug("------------------- provisional ------------------" );
        logger.debug(response);
        logger.debug("--------------- end of provisional ---------------" );
    }

    public void proceeding(DsSipInvitation invitation, DsSipResponse response) {
        logger.debug("Invitation proceeding");
        logger.debug("------------------- provisional ------------------" );
        logger.debug(response);
        logger.debug("--------------- end of provisional ---------------" );
    }

    public void accepted(DsSipInvitation invitation, DsSipInviteDialog dialog) {
        logger.debug("Invitation accepted - ack'ing");
        logger.debug("------------------- final ------------------" );
        logger.debug(dialog.getFinalResponse());
        logger.debug("--------------- end of final ---------------" );

        // In a real application, the ACK may contain SDP, but here,
        //    we ACK without SDP for simplicity of example.
        try {
            dialog.sendAck(null);
        } catch (Exception exp) {
			logger.error("Sending ACK", exp);
        }
    }

    public void rejected(DsSipInvitation invitation) {
        logger.debug("Invitation rejected");
        logger.debug(invitation.getFinalResponse());
    }

    public void timeOut(DsSipInvitation invitation) {
        logger.error("Error: Invitation timed out");
    }

    public void transportError(DsSipInvitation invitation) {
        logger.error("Error: Invitation transport error");
    }

    // ///////////////////  DsSipInviteDialogInterface implementation ///////////////
    //

    public void proceeding(DsSipInviteDialog dialog, DsSipResponse response) {
        logger.debug("Dialog proceeding");
        logger.debug(response);
    }

    public void accepted(DsSipInviteDialog dialog) {
        logger.debug("Dialog accepted");
        logger.debug(dialog.getFinalResponse());
    }

    public void rejected(DsSipInviteDialog dialog) {
        logger.debug("Dialog rejected");
        logger.debug(dialog.getFinalResponse());
    }

    public void ack(DsSipInviteDialog dialog) {
        logger.debug("Dialog ack'ed");
        logger.debug(dialog.getAck());
		MsSimulator.getInstance().incrementAckRxCount();
    }

    public void terminated(DsSipInviteDialog dialog) {
        logger.debug("Dialog terminated");
		MsSimulator.getInstance().incrementByeRxCount();
		MsSimulator.getInstance().incrementBye200TxCount();
    }

    public void terminate(DsSipInviteDialog dialog, DsSipReinvitation reinvitation) {
        logger.debug("Dialog Reinvitation failed");
    }

    public void request(DsSipInviteDialog dialog, DsSipServerTransaction serverTransaction) {
        logger.debug("Dialog request received");

		try {
			DsSipRequest request = serverTransaction.getRequest();
			DsSipResponse response = new DsSipResponse(200, request, null, null);

			DsSipDialogState state = dialog.getDialogState();
			synchronized(state) {
				if(state.update(request, false) == DsSipDialogState.STATUS_OK) {
					serverTransaction.sendResponse(response);
					MsSimulator.getInstance().incrementMethodStats( m_callState,
																	request.getMethod());
				} else {
					logger.error("Error: while receiving in-dialog request " +
																	request.getMethod());
					serverTransaction.sendResponse(500);
				}
			}
		} catch(Exception exp) {
			logger.error("In request()", exp);
		}
    }

    public void reinvited(DsSipInviteDialog dialog, DsSipReinvitation reinvitation) {
        logger.debug("Dialog Reinvitation received");
    }

    /**
     * Start the command line.
     */
    static void startCommandLine() {
		logger.debug("startCommandLine() called");
    }

    // ///////////////////  DsSipReinvitationInterface implementation ///////////////
    //
    //  These methods will not be called since we do not demonstrate re-invitations
    //    in this program.

    public void cancelled(DsSipReinvitation reinvite, DsSipCancelMessage cancel) {
		logger.debug("Reinvitation cancelled notification");
    }

    public void ack(DsSipReinvitation reinvite) {
		logger.debug("Reinvitation ack notification");
    }

    public void proceeding(DsSipReinvitation reinvite, DsSipResponse response) {
		logger.debug("Reinvitation proceeding notification");
    }

    public void accepted(DsSipReinvitation reinvite) {
		logger.debug("Reinvitation accepted notification");
    }

    public void rejected(DsSipReinvitation reinvite) {
		logger.debug("Reinvitation rejected notification");
    }

	// ////////////////////////// invitation implementation ///////////////////////
	//
	public void invitation() {
		logger.debug("invitation() called");

		MsSimulator.getInstance().incrementInviteRxCount();

		DsSipInviteMessage invReq = m_invitation.getInvite();

		// Send 180 response
		try {
			m_dialog = m_invitation.proceed(new DsSipResponse(180, invReq, null, null));
			MsSimulator.getInstance().increment180TxCount();

			m_callState = MsSimulator.STATE_B;

			// Extract http link from ReportAt header
			m_url = invReq.getHeader(new DsByteString("Report-At")).getValue().toString();
			m_url = URIUtil.decode(m_url);
			logger.info("HTTP URL decoded is " + m_url);
		} catch(Exception exp) {
			logger.error("Sending 180 response", exp);
			return;
		}

		// Schedule HTTP GET request sender
		m_getReqSch.submit(new HttpGetSender());
	}

	private class HttpGetSender implements Work {
		public void execute() {
			// Send request to http server
			HttpClient client = new HttpClient();
			GetMethod method = null;
			try {
				method = new GetMethod(m_url);
				method.getParams().setParameter(HttpMethodParams.RETRY_HANDLER,
						new DefaultHttpMethodRetryHandler(3, false));
				int respCode = client.executeMethod(method);
				MsSimulator.getInstance().incrementHttpGetTxCount();
				if(respCode != HttpStatus.SC_OK) {
					MsSimulator.getInstance().incrementHttpGetErrRxCount();
					logger.error("Error: HTTP GET failed: " + new String(method.getResponseBody()));
					m_dialog.reject(m_dialog.createResponse(487));
					MsSimulator.getInstance().incrementErrTxCount();
					return;
				} else {
					MsSimulator.getInstance().incrementHttpGet200RxCount();
					logger.info("HTTP GET is successful.");
				}
			} catch(Throwable thr) {
				logger.error("Sending HTTP GET", thr);
				return;
			} finally {
				m_callState = MsSimulator.STATE_C;
				method.releaseConnection();
			}

			// Schedule final response sender
			m_finalRespSch.submit(new FinalResponseSender());
		} // execute()
	}

	private class FinalResponseSender implements Work {
		public void execute() {
			try {
				if(m_finalResp < 300) {
					// Accept invitation (Send 200 OK to INVITE)
					DsSipResponse dsResp = new DsSipResponse(	m_finalResp,
																m_dialog.getInvite(),
																BOGUS_BODY,
																BODY_TYPE);
					m_dialog.accept(dsResp);
					MsSimulator.getInstance().incrementInvFinTxCount();

					m_callState = MsSimulator.STATE_D;

					// Schedule task
					m_iterTimeSch.submit(new ConfirmedDialog());
				} else {
					// Reject invitation
					m_dialog.reject(m_dialog.createResponse(m_finalResp));
					MsSimulator.getInstance().incrementInvFinTxCount();

					m_callState = MsSimulator.STATE_D;
				}
			} catch(Exception exp) {
				logger.error("Sending final response", exp);
			}
		} // execute()
	}

	// ////////////////////////// Work implementation ///////////////////////////////
	//

	private class ConfirmedDialog implements Work {
		public void execute() {
			// Send POST to http server
			HttpClient client = new HttpClient();
			PostMethod method = null;
			try {
				method = new PostMethod(m_url + "?post");
				method.getParams().setParameter(HttpMethodParams.RETRY_HANDLER,
						new DefaultHttpMethodRetryHandler(3, false));
				int respCode = client.executeMethod(method);
				MsSimulator.getInstance().incrementHttpPostTxCount();
				if(respCode != HttpStatus.SC_OK) {
					MsSimulator.getInstance().incrementHttpPostErrRxCount();
					logger.error("Error: HTTP POST failed: " + new String(method.getResponseBody()));
					m_dialog.reject(m_dialog.createResponse(487));
					MsSimulator.getInstance().incrementErrTxCount();
					return;
				} else {
					MsSimulator.getInstance().incrementHttpPost200RxCount();
					logger.info("HTTP POST is successful.");
				}
			} catch(Throwable thr) {
				logger.error("Sending HTTP POST", thr);
				return;
			} finally {
				m_callState = MsSimulator.STATE_E;
				method.releaseConnection();
			}
		} // execute()
	}
}
