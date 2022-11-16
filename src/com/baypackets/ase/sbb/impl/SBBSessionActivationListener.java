/*
 * Created on Oct 30, 2004
 *
 */
package com.baypackets.ase.sbb.impl;

import java.io.Serializable;
import java.lang.reflect.Constructor;

import javax.servlet.sip.SipApplicationSession;
import javax.servlet.sip.SipSession;
import javax.servlet.sip.SipSessionActivationListener;
import javax.servlet.sip.SipSessionEvent;

import org.apache.log4j.Logger;

import com.baypackets.ase.sbb.SBB;
import com.baypackets.ase.sbb.SBBEventListener;
import com.baypackets.ase.sbb.util.Constants;


/**
 *
 * The SBB Servlet Implementation. When the application want to use the SBBs.
 */
public class SBBSessionActivationListener implements SipSessionActivationListener, Serializable {

	private static final long serialVersionUID = 811848884302981098L; 
	/** Logger element */
    private static Logger logger = Logger.getLogger(SBBSessionActivationListener.class.getName());


	public SBBSessionActivationListener() {
		super();
	}


	/* (non-Javadoc)
	 * @see javax.servlet.sip.SipSessionActivationListener#sessionDidActivate(javax.servlet.sip.SipSessionEvent)
	 */
	public void sessionDidActivate(SipSessionEvent activateEvent) {
		if(logger.isDebugEnabled()) 
			logger.debug("<SBB> Entering sessionDidActivate for activating SBB and its listener");
		
		SipSession session = activateEvent.getSession();
		if(logger.isDebugEnabled()) 
			logger.debug("<SBB> session activation for sesion = "+session.getId());
		String sbbName = (String)session.getAttribute(SBBOperationContext.ATTRIBUTE_SBB);	
		if(logger.isDebugEnabled()) 
			logger.debug("<SBB> Associated sbb is :: "+sbbName);

		SipApplicationSession appSession = session.getApplicationSession();
		// sets the transient application session into SBB
		
		SBBOperationContext ctx = (SBBOperationContext)appSession.getAttribute(sbbName);
		if(logger.isDebugEnabled()) 
			logger.debug("<SBB> Operation Constext = "+ctx);
		if(ctx == null)	{
			if(logger.isDebugEnabled()) 
				logger.debug("<SBB> OperationContext is NULL, so return without doing anything");
			return;
		}
		// sets the transient application session into SBB
		SBB sbb = ctx.getSBB();
		if(logger.isDebugEnabled()) 
			logger.debug("<SBB> SBB = "+sbb);
		sbb.setApplicationSession(appSession);
		
		try {
			String className = (String)appSession.getAttribute(sbbName + Constants.SBB_LISTENER_CLASS);
			if (className == null) {
				if (logger.isDebugEnabled()) {
					logger.debug("sessionDidActivate(): No event listener class registered with SBB.");
				}
			} else {
				if (logger.isDebugEnabled()) {
					logger.debug("sessionDidActivate(): Instantiating event listener class: " + className);
				}
				Constructor<?> constructor = null;
				Class<?> listenerClass = Class.forName(className);

				if (listenerClass == null) {
					throw new Exception("Unable to load listener class from class loader:" + className);
				}
				
				Constructor<?>[] constructors = listenerClass.getDeclaredConstructors();
				Class<?> clazz = listenerClass.getDeclaringClass();
				boolean isInner = false;
				Class<?> outerClass = null;
				for(Constructor<?> c : constructors){
					if(c.getParameterTypes().length == 0){
						constructor = c;
						break;
					}else if(clazz !=null && c.getParameterTypes().length == 1 ){
						Class<?>[] parameters = c.getParameterTypes();
						if(parameters[0].getCanonicalName().equals(clazz.getCanonicalName())){
							outerClass = parameters[0];
							constructor = c;
							isInner = true;
							break;
						}
					}
				}
				
				if(constructor!=null){
				
					SBBEventListener listener = null;
					if(!constructor.isAccessible()){
						constructor.setAccessible(true);
					}
					if(!isInner){
						listener = (SBBEventListener)constructor.newInstance();
						if (logger.isDebugEnabled()) {
							logger.debug("Created instance using constructor " + constructor.getName());
						}
					}else{
						Constructor<?> outerConstructor = null;
						Constructor<?>[] outerConstructors = outerClass.getDeclaredConstructors();
						for(Constructor<?> c : outerConstructors){
							if(c.getParameterTypes().length == 0){
								outerConstructor = c;
								break;
							}
						}
						if(outerConstructor ==null){
							throw new Exception("Unable to find Default Constructor for Outer Class " + outerClass.getCanonicalName() );
						}
						if(!outerConstructor.isAccessible()){
							outerConstructor.setAccessible(true);
						}
						listener = (SBBEventListener)constructor.newInstance(outerConstructor.newInstance());
						if (logger.isDebugEnabled()) {
							logger.debug("Created instance of inner class using constructor " + constructor.getName());
						}
					}
					if (logger.isDebugEnabled()) {
						logger.debug("Setting SBB Event Listener " + listener.toString());
					}
					sbb.setEventListener(listener);
				}else{
					throw new Exception("Unable to find Default Constructor for  " + className );
				}
				//SBBEventListener listener = (SBBEventListener)Class.forName(className).newInstance();
			}
		} catch (Exception e) {
			String msg = "Error occurred while instantiating event listener class during session activation: " + e.getMessage();
			logger.error(msg, e);
			throw new RuntimeException(msg);
		}
		
		if(logger.isDebugEnabled()) 
			logger.debug("<SBB> Leaving");
		ctx.activate(session); //[Bug 10212: It is now decided that the application will take the responsible for the cleanup]
	}

	/* (non-Javadoc)
	 * @see javax.servlet.sip.SipSessionActivationListener#sessionWillPassivate(javax.servlet.sip.SipSessionEvent)
	 */
	public void sessionWillPassivate(SipSessionEvent arg0) {
		// TODO: complete this method
	}
	
}
