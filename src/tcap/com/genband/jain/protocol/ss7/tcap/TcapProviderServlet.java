package com.genband.jain.protocol.ss7.tcap;

import jain.InvalidAddressException;
import jain.ListenerAlreadyRegisteredException;
import jain.ListenerNotRegisteredException;
import jain.MandatoryParameterNotSetException;
import jain.protocol.ss7.IdNotAvailableException;
import jain.protocol.ss7.SccpUserAddress;
import jain.protocol.ss7.sccp.StateReqEvent;
import jain.protocol.ss7.tcap.ComponentReqEvent;
import jain.protocol.ss7.tcap.DialogueReqEvent;
import jain.protocol.ss7.tcap.InvalidUserAddressException;
import jain.protocol.ss7.tcap.JainTcapListener;
import jain.protocol.ss7.tcap.JainTcapStack;
import jain.protocol.ss7.tcap.TcapUserAddress;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.TooManyListenersException;

import javax.servlet.ServletException;
import javax.servlet.sip.SipServlet;

import org.apache.log4j.Logger;

import com.genband.tcap.provider.TcapFactory;
import com.genband.tcap.provider.TcapProvider;
import com.genband.tcap.provider.TcapSession;

public class TcapProviderServlet extends SipServlet implements TcapProvider {

    private static Logger logger = Logger.getLogger(TcapProviderServlet.class);
	
    private TcapSessionReplicator replicator;
    private INGatewayManagerImpl ingwManager;
    private int dialogueCounter =0;
	
    public void init() throws ServletException {
		if (logger.isDebugEnabled()) {
			logger.debug("init called...");
        }
		super.init();
        
        replicator = new TcapSessionReplicator(this);
        replicator.init();
        
        ingwManager = new INGatewayManagerImpl();
        ingwManager.init();
        Iterator<INGateway> gws = ingwManager.getAllINGateways();
	if(!gws.hasNext()){
	    throw new ServletException("No IN Gateways Configured.");
	}

        TcapJndiObjectFactory.bind(this);
        
        //For testing... To be removed from here.
        for(;gws.hasNext();){
	    ingwManager.inGatewayUp(gws.next().getId());
        }
        //Test code ends...
    }
	
    public void destroy() {
	//For testing... To be removed
    	Iterator<INGateway> gws = ingwManager.getAllINGateways();
    	for(;gws.hasNext();){
	    ingwManager.inGatewayDown(gws.next().getId());
    	}
	//Test code ends...
		
	TcapJndiObjectFactory.unbind();
	ingwManager.close();
	replicator.cleanup();
	super.destroy();
    }

    public void addJainTcapListener(JainTcapListener listener, SccpUserAddress addr)
	throws TooManyListenersException,
	       ListenerAlreadyRegisteredException, InvalidAddressException {
		
	//TODO Add provider logic
		
	replicator.addListener(listener);
    }
	
    public void addJainTcapListener(JainTcapListener listener, TcapUserAddress arg1)
	throws TooManyListenersException,
	       ListenerAlreadyRegisteredException, InvalidUserAddressException {
		
	//TODO Add provider logic
	
	replicator.addListener(listener);
    }
    
    public void addJainTcapListener(JainTcapListener listener, List<SccpUserAddress> sccpUserAddress, List<String> serviceKey)
	throws TooManyListenersException,
	       ListenerAlreadyRegisteredException, InvalidAddressException,IOException {
    	
    	//TODO Add provider logic
    	replicator.addListener(listener);
    }

    public JainTcapStack getAttachedStack() {
	// TODO Auto-generated method stub
	return null;
    }
	
    public int getNewDialogueId() throws IdNotAvailableException {
	// TODO Change the Dialogue ID generation as required.
	return ++dialogueCounter;
    }
	
    public int getNewInvokeId(int arg0) throws IdNotAvailableException {
	// TODO Auto-generated method stub
	return 0;
    }
	
    public JainTcapStack getStack() {
	// TODO Auto-generated method stub
	return null;
    }
	
    public boolean isAttached() {
	// TODO Auto-generated method stub
	return false;
    }
	
    public void releaseDialogueId(int arg0) throws IdNotAvailableException {
	// TODO Auto-generated method stub
		
    }
	
 	public void setTCCorrelationId(int dialogueId,int correlationId){
    	// TODO Auto-generated method stub
	 }
	    
	    /**
	     * 
	     * @param dialogueId
	     * @return
	     */
	    public Integer getTCCorrelationId(int dialogueId){
	    	// TODO Auto-generated method stub
	    	return null;
	    }
	    
	    /**
	     * 
	     * @param dialogueId
	     * @return
	     */
	    public Integer removeTCCorrelationId(int dialogueId){
	    	// TODO Auto-generated method stub
	    	return null;
	    }
	
	
    public void releaseInvokeId(int arg0, int arg1)
	throws IdNotAvailableException {
	// TODO Auto-generated method stub
		
    }
	
    public void removeJainTcapListener(JainTcapListener listener)
	throws ListenerNotRegisteredException {
	//TODO Add provider logic.
	replicator.removeListener(listener);
    }
    
    public void removeJainTcapListener(JainTcapListener listener, List<String> serviceKey) 
    throws ListenerNotRegisteredException,IOException {
    	//TODO Add provider logic.
    	replicator.removeListener(listener);
    }
	
    public void sendComponentReqEvent(ComponentReqEvent event)
	throws MandatoryParameterNotSetException {
	// TODO Auto-generated method stub
		
	//TODO Verify whether the following is required or not.
	//The provider may want to call the following to replicate.
	replicator.replicate(event.getDialogueId(), null);
    }
	
    public void sendDialogueReqEvent(DialogueReqEvent event)
	throws MandatoryParameterNotSetException {
	// TODO Auto-generated method stub
    }
	
    public void sendStateReqEvent(StateReqEvent event)
	throws MandatoryParameterNotSetException {
    }

    public TcapFactory getTcapFactory(JainTcapListener listener) {
	return replicator.getTcapFactory(listener);
    }

    public TcapSession getTcapSession(int dialogueId) {
	//TODO identify the listener corresponding to the dialogueId
	JainTcapListener listener  = null;
	return replicator.getTcapSession(dialogueId, listener);
    }

	@Override
	public void tcapListenerActivated(JainTcapListener listener) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void tcapListenerDeActivated(JainTcapListener listener) {
		// TODO Auto-generated method stub
		
	}	
}
