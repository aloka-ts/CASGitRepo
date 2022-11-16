package com.baypackets.ase.sbb.impl;

import javax.servlet.ServletContext;
import javax.servlet.sip.SipApplicationSession;

import com.baypackets.ase.spi.container.SasApplicationSession;

import org.apache.log4j.Logger;

import com.baypackets.ase.sbb.SBB;
import com.baypackets.ase.sbb.B2bSessionController;
import com.baypackets.ase.sbb.ConferenceController;
import com.baypackets.ase.sbb.ConferenceParticipant;
import com.baypackets.ase.sbb.conf.ConferenceParticipantImpl;
import com.baypackets.ase.sbb.conf.ConferenceControllerImpl;
import com.baypackets.ase.sbb.MsSessionController;
import com.baypackets.ase.sbb.TbctController;
import com.baypackets.ase.sbb.CallTransferController;
import com.baypackets.ase.sbb.SBBFactory;
import com.baypackets.ase.sbb.b2b.B2bSessionControllerImpl;
import com.baypackets.ase.sbb.mediaserver.MsSessionControllerImpl;
import com.baypackets.ase.sbb.tbct.TbctControllerImpl;
import com.baypackets.ase.sbb.calltransfer.CallTransferControllerImpl;
import com.baypackets.ase.sbb.GroupedMsSessionController;
import com.baypackets.ase.sbb.mediaserver.GroupedMsSessionControllerImpl;

public class SBBFactoryImpl extends SBBFactory {

	
	/** Logger element */
    private static Logger logger = Logger.getLogger(SBBFactoryImpl.class.getName());


	public SBB getSBB(String type, String name, SipApplicationSession appSession, ServletContext ctx) 
		throws IllegalArgumentException {
		
		//This is done to load the Media Server SBB at standby SAS during deserializing
		//by setting the appropriate SBB Class Loader. This is done to avoid adding the sbb classes
		//to the AseClassLoader
		name = name + "ASE_SBB";
		
		if (B2bSessionController.class.getName().equals(type)) {
			return this.getB2bSessionController(name, appSession, ctx);
		}
		if (MsSessionController.class.getName().equals(type)) {
			return this.getMsSessionController(name, appSession, ctx);
		}
		if (ConferenceController.class.getName().equals(type)) {
			return this.getConferenceController(name, appSession, ctx);
		}
		if (ConferenceParticipant.class.getName().equals(type)) {
            return this.getConferenceParticipant(name, appSession, ctx);
        }

		if (TbctController.class.getName().equals(type)) {
			return this.getTbctController(name, appSession, ctx);
		}

		if(CallTransferController.class.getName().equals(type)) {
			return this.getCallTransferController(name, appSession, ctx);
		}
				
		if (GroupedMsSessionController.class.getName().equals(type)) {
			return this.getGroupedMsSessionController(name, appSession, ctx);
		}
		
		throw new IllegalArgumentException("Unkown SBB type: " + type);
	}

	private B2bSessionController getB2bSessionController(String name,
					SipApplicationSession appSession,ServletContext servletCtx) 
					throws IllegalArgumentException {

		B2bSessionController b2bSessionCtrl = null;
		
		try {
			b2bSessionCtrl = (B2bSessionController)appSession.getAttribute(name);
			if (b2bSessionCtrl != null ) {
				/*
				 * Taking care FT case 
				 */
				if(b2bSessionCtrl.getApplicationSession()==null){
					b2bSessionCtrl.setApplicationSession(appSession);
            	}
            	
            	if(b2bSessionCtrl.getServletContext()==null){
            		b2bSessionCtrl.setServletContext(servletCtx);
            	}
				return b2bSessionCtrl;
			}
			b2bSessionCtrl = new B2bSessionControllerImpl();
			b2bSessionCtrl.setName(name);
			b2bSessionCtrl.setServletContext(servletCtx);
			// associate SBB with application session
			appSession.setAttribute(name,b2bSessionCtrl);
			b2bSessionCtrl.setApplicationSession(appSession);
		}
		catch(ClassCastException cce) {
			logger.error("SBB with specified name already exists",cce);
			throw new IllegalArgumentException("SBB with specified name already exists");
			
		}
		return b2bSessionCtrl;
	}
		
	private MsSessionController getMsSessionController(String name,
			SipApplicationSession appSession,ServletContext servletCtx) throws IllegalArgumentException {

		boolean loggerEnabled = logger.isDebugEnabled();

		if (loggerEnabled) {
			logger.debug("getMsSessionController() called...");
		}

		MsSessionController msSessionCtrl = null;
		
		try {
			msSessionCtrl = (MsSessionController)appSession.getAttribute(name);
			if (msSessionCtrl != null ) {
				/*
				 * Taking care FT case 
				 */
				if(msSessionCtrl.getApplicationSession()==null){
            		msSessionCtrl.setApplicationSession(appSession);
            	}
            	
            	if(msSessionCtrl.getServletContext()==null){
            		msSessionCtrl.setServletContext(servletCtx);
            	}
				return msSessionCtrl;
			}

			if (loggerEnabled) {
				logger.debug("getMsSessionController(): No MsSessionController currently associated with app session.  Binding new one...");
			}
			
			msSessionCtrl = new MsSessionControllerImpl();
			msSessionCtrl.setName(name);
			msSessionCtrl.setServletContext(servletCtx);
			// associate SBB with application session
			appSession.setAttribute(name,msSessionCtrl);
			msSessionCtrl.setApplicationSession(appSession);

			if (loggerEnabled) {
				logger.debug("getMsSessionController(): Successfully bound SBB to appSession with ID: " + ((SasApplicationSession)appSession).getAppSessionId());
			}
		}
		catch(ClassCastException cce) {
			logger.error("SBB with specified name already exists",cce);
			throw new IllegalArgumentException("SBB with specified name already exists");
			
		}
		return msSessionCtrl;
	}
	
	private GroupedMsSessionController getGroupedMsSessionController(String name,
			SipApplicationSession appSession,ServletContext servletCtx) throws IllegalArgumentException {

		boolean loggerEnabled = logger.isDebugEnabled();

		if (loggerEnabled) {
			logger.debug("getGroupedMsSessionController() called...");
		}

		GroupedMsSessionController msSessionCtrl = null;
		
		try {
			msSessionCtrl = (GroupedMsSessionController)appSession.getAttribute(name);
			if (msSessionCtrl != null ) {
				return msSessionCtrl;
			}

			if (loggerEnabled) {
				logger.debug("getGroupedMsSessionController(): No GroupedMsSessionController currently associated with app session.  Binding new one...");
			}
			
			msSessionCtrl = new GroupedMsSessionControllerImpl();
			msSessionCtrl.setName(name);
			msSessionCtrl.setServletContext(servletCtx);
			// associate SBB with application session
			appSession.setAttribute(name,msSessionCtrl);
			msSessionCtrl.setApplicationSession(appSession);

			if (loggerEnabled) {
				logger.debug("getGroupedMsSessionController(): Successfully bound SBB to appSession with ID: " + ((SasApplicationSession)appSession).getAppSessionId());
			}
		}
		catch(ClassCastException cce) {
			logger.error("SBB with specified name already exists",cce);
			throw new IllegalArgumentException("SBB with specified name already exists");
			
		}
		return msSessionCtrl;
	}

	private ConferenceController getConferenceController(String name,
			SipApplicationSession appSession,ServletContext servletCtx) throws IllegalArgumentException {

		 boolean loggerEnabled = logger.isDebugEnabled();

        if (loggerEnabled) {
            logger.debug("getConferenceController() called with name :: "+name +"...");
        }

        ConferenceController conferecneCtrl = null;

        try {
            conferecneCtrl = (ConferenceController)appSession.getAttribute(name);
            if (conferecneCtrl != null ) {
            	/*
				 * Taking care FT case 
				 */
            	if(conferecneCtrl.getApplicationSession()==null){
            		conferecneCtrl.setApplicationSession(appSession);
            	}
            	
            	if(conferecneCtrl.getServletContext()==null){
            		conferecneCtrl.setServletContext(servletCtx);
            	}
                return conferecneCtrl;
            }

            if (loggerEnabled) {
                logger.debug("getConferenceController(): No ConferenceController currently associated with app session.  Binding new one...");
            }
			conferecneCtrl = new ConferenceControllerImpl();
            conferecneCtrl.setName(name);
            conferecneCtrl.setServletContext(servletCtx);
            // associate SBB with application session
            appSession.setAttribute(name,conferecneCtrl);
            conferecneCtrl.setApplicationSession(appSession);
			
			if (loggerEnabled) {
			// test code remove this block
			logger.debug("ATTR ="+appSession.getAttribute(name));
			// test code ends

            
                logger.debug("getConferenceController(): Successfully bound SBB to appSession with ID: " + ((SasApplicationSession)appSession).getAppSessionId());
            }
        }
        catch(ClassCastException cce) {
            logger.error("SBB with specified name already exists",cce);
            throw new IllegalArgumentException("SBB with specified name already exists");
           
        }
        return conferecneCtrl;
	}



	private ConferenceParticipant getConferenceParticipant(String name,
            SipApplicationSession appSession,ServletContext servletCtx) throws IllegalArgumentException {

         boolean loggerEnabled = logger.isDebugEnabled();

        if (loggerEnabled) {
            logger.debug("getConferenceParticipant() called with name :: "+name +"...");
        }

        ConferenceParticipant conferecneParticipant = null;

        try {
            conferecneParticipant = (ConferenceParticipant)appSession.getAttribute(name);
            if (conferecneParticipant != null ) {
            	/*
				 * Taking care FT case 
				 */
            	if(conferecneParticipant.getApplicationSession()==null){
            		conferecneParticipant.setApplicationSession(appSession);
            	}
            	
            	if(conferecneParticipant.getServletContext()==null){
            		conferecneParticipant.setServletContext(servletCtx);
            	}
                return conferecneParticipant;
            }

            if (loggerEnabled) {
                logger.debug("getConferenceParticipant(): No ConferenceParticipant currently associated with app session.  Binding new one..."
);
            }
            conferecneParticipant = new ConferenceParticipantImpl();
            conferecneParticipant.setName(name);
            conferecneParticipant.setServletContext(servletCtx);
            // associate SBB with application session
            appSession.setAttribute(name,conferecneParticipant);
            conferecneParticipant.setApplicationSession(appSession);

            if (loggerEnabled) {
                logger.debug("getConferenceParticipant(): Successfully bound SBB to appSession with ID: " + ((SasApplicationSession)appSession).getAppSessionId());
            }
        }
        catch(ClassCastException cce) {
            logger.error("SBB with specified name already exists",cce);
            throw new IllegalArgumentException("SBB with specified name already exists");

        }
        return conferecneParticipant;
    }


	private TbctController getTbctController(	String name,
												SipApplicationSession appSession,
												ServletContext servletCtx) 
					throws IllegalArgumentException {
		if(logger.isDebugEnabled()) {
			logger.debug("getTbctController: enter with name = " + name);
		}

		TbctController tbctController = null;
		
		try {
			tbctController = (TbctController)appSession.getAttribute(name);
			if (tbctController != null ) {
				/*
				 * Taking care FT case 
				 */
            	if(tbctController.getApplicationSession()==null){
            		tbctController.setApplicationSession(appSession);
            	}
            	
            	if(tbctController.getServletContext()==null){
            		tbctController.setServletContext(servletCtx);
            	}
				if(logger.isDebugEnabled()) 
					logger.debug("getTbctController: exit");
				return tbctController;
			}

			// Create new SBB object
			tbctController = new TbctControllerImpl();
			tbctController.setName(name);
			tbctController.setServletContext(servletCtx);

			// associate SBB with application session
			appSession.setAttribute(name, tbctController);
			tbctController.setApplicationSession(appSession);
		} catch(ClassCastException cce) {
			logger.error("getTbctController: Another SBB type with specified name already exists", cce);
			throw new IllegalArgumentException("SBB with specified name already exists");
		}
		if(logger.isDebugEnabled()) 
			logger.debug("getTbctController: exit");
		return tbctController;
	}



        private CallTransferController getCallTransferController(String name,SipApplicationSession appSession, ServletContext servletCtx) throws IllegalArgumentException
	{
        
                if(logger.isDebugEnabled()) {
                        logger.debug("getCallTransferController: enter with name = " + name);
                }

                CallTransferController callTransfer = null;

                try {
                        callTransfer = (CallTransferController)appSession.getAttribute(name);
                        if (callTransfer != null ) {
                        	/*
            				 * Taking care FT case 
            				 */
                        	if(callTransfer.getApplicationSession()==null){
                        		callTransfer.setApplicationSession(appSession);
                        	}
                        	
                        	if(callTransfer.getServletContext()==null){
                        		callTransfer.setServletContext(servletCtx);
                        	}
							if(logger.isDebugEnabled()) 
								logger.debug("getCallTransferController: exit");
                            return callTransfer;
                        }

                        // Create new SBB object
                        callTransfer = new CallTransferControllerImpl();
                        callTransfer.setName(name);
                        callTransfer.setServletContext(servletCtx);

                        // associate SBB with application session
                        appSession.setAttribute(name, callTransfer);
                        callTransfer.setApplicationSession(appSession);
                } catch(ClassCastException cce) {
                        logger.error("getCallTransferController: Another SBB type with specified name already exists", cce);
                        throw new IllegalArgumentException("SBB with specified name already exists");
                }
				if(logger.isDebugEnabled()) 
					logger.debug("getCallTransferController: exit");
                return callTransfer;
        }



}
