package com.genband.apps.routing;

import java.io.IOException;
import java.io.Serializable;

import javax.servlet.sip.Rel100Exception;
import javax.servlet.sip.ServletTimer;
import javax.servlet.sip.SipServletMessage;
import javax.servlet.sip.SipServletRequest;
import javax.servlet.sip.SipServletResponse;
import javax.servlet.sip.SipSession;

import org.apache.log4j.Logger;

import com.baypackets.ase.sbb.ProcessMessageException;
import com.baypackets.ase.sbb.impl.SBBOperation;
import com.baypackets.ase.sbb.impl.SBBOperationContext;
import com.baypackets.ase.sbb.mediaserver.MediaServerInfoHandler;

/**
 * This class is used to invalidate the app session in case multiple app
 * sessions are created by an application .e.g. for ATT CPA flow in which there are 2 app sessions
 * A->ivr and B-IVR . so when applications calls <cleanup> on RouteCallAction ALC ,these two app sessions
 * need to be cleared but as per container logic one thread which donot owns an appsession can not invalidate it
 * as there is logic of AseIC and locking as per current trhead local , so we need to invalidate the other app
 * session in its thread only thats wy we created a timer in cleanup method od RouteCallAction so that that
 * wpplication gets invaidated in its own thread.so in this class on timerExpired we will just invalidate
 * the appsession of that timer. here this class impelments SBbOperation because SBBServlet is the only timer listenr 
 * which gives timerExpired callbacks to classes which implements SBBOperation only thats why it had to implement this interface
 * @author reeta
 *
 */
import com.esotericsoftware.kryo.DefaultSerializer;
import com.esotericsoftware.kryo.serializers.ExternalizableSerializer;

@DefaultSerializer(ExternalizableSerializer.class)
public class AppSessionCleanerTimerListener implements SBBOperation, Serializable {
	
	private static final Logger logger = Logger.getLogger(MediaServerInfoHandler.class);

	@Override
	public void handleRequest(SipServletRequest request) {
		// TODO Auto-generated method stub

	}

	@Override
	public void handleResponse(SipServletResponse response) {
		// TODO Auto-generated method stub

	}

	@Override
	public void sendRequest(SipServletRequest request) throws IOException {
		// TODO Auto-generated method stub

	}

	@Override
	public void sendResponse(SipServletResponse response, boolean reliable)
			throws Rel100Exception, IOException {
		// TODO Auto-generated method stub

	}

	@Override
	public void ackTimedout(SipSession session) {
		// TODO Auto-generated method stub

	}

	@Override
	public void prackTimedout(SipSession session) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean isCompleted() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isMatching(SipServletMessage message) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public SBBOperationContext getOperationContext() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setOperationContext(SBBOperationContext ctx) {
		// TODO Auto-generated method stub

	}

	@Override
	public void start() throws ProcessMessageException {
		// TODO Auto-generated method stub

	}

	@Override
	public void timerExpired(ServletTimer timer) {
		// TODO Auto-generated method stub	
		
		if(logger.isDebugEnabled()){
			logger.debug("timerExpired need to invalidate the appsession of this timer" +timer.getApplicationSession());
		}
		timer.getApplicationSession().invalidate();

	}

	@Override
	public void cancel() {
		// TODO Auto-generated method stub

	}

}
