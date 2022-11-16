package com.agnity.simulator.handlers.impl;

import java.util.Iterator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.log4j.Logger;

import com.agnity.simulator.InapIsupSimServlet;
import com.agnity.simulator.callflowadaptor.element.Node;
import com.agnity.simulator.callflowadaptor.element.child.ProvCallElem;
import com.agnity.simulator.callflowadaptor.element.child.VarElem;
import com.agnity.simulator.callflowadaptor.element.type.StartNode;
import com.agnity.simulator.domainobjects.SimCallProcessingBuffer;
import com.agnity.simulator.domainobjects.Variable;
import com.agnity.simulator.handlers.AbstractHandler;
import com.agnity.simulator.handlers.Handler;
import com.agnity.simulator.tasks.TcapSessionTimeoutTask;
import com.agnity.simulator.utils.Constants;
import com.agnity.simulator.utils.Helper;

public class StartHandler extends AbstractHandler{

	private static Logger logger = Logger.getLogger(StartHandler.class);
	public static Handler handler;

	public static synchronized Handler getInstance(){
		if(handler == null){
			synchronized (StartHandler.class) {
				if(handler ==null){
					handler = new StartHandler();
				}
			}
		}
		return handler;
	}


	@Override
	protected boolean processNode(Node node, SimCallProcessingBuffer simCpb) {
		if(logger.isInfoEnabled())
			logger.info("Inside StartHandler processNode()");

		if(!(node.getType().equals(Constants.START))){
			logger.error("Invalid Handler for node type::["+ node.getType()+"]");
			return false;
		}			

		List<Node> subElements =node.getSubElements();
		Iterator<Node> subElemIterator = subElements.iterator();

		//if CPB is null create new one
		if(simCpb == null){
			simCpb =new SimCallProcessingBuffer();
		}
		//setting if reliable
		boolean isReliable= ((StartNode)node).isSupportsReliable();
		//simCpb.setSupportsReliable(isReliable);
		//adding variables to CPB
		int leg = node.getSipLeg();
		if(leg ==1 )
			simCpb.setSupportsReliableLeg1(isReliable);
		else if(leg ==2)
			simCpb.setSupportsReliableLeg2(isReliable);
		else
			simCpb.setSupportsReliable(isReliable);
		
		//setting acn and store dilaog portion enable if acn value is present in
		String acn = InapIsupSimServlet.getInstance().getConfigData().getApplicationContextName();
		if(acn!=null && acn.matches("[0-9a-fA-F]+") && ((acn.length())%2==0)){
			simCpb.setDialoguePortionPresent(true);
			simCpb.setAppContextName(Helper.hexStringToByteArray(acn));
		}
		
		while(subElemIterator.hasNext()){
			Node subElem = subElemIterator.next();
			VarElem varElem = null;
			Variable variable = new Variable();
			ProvCallElem provCallElem = null;

			if(subElem.getType().equals(Constants.VAR)){
				varElem =(VarElem) subElem;
				variable =new Variable();
				variable.setVarName(varElem.getVarName());
				variable.setVarValue(varElem.getValue());
				variable.setNov(varElem.getNov());

				simCpb.addVariable(variable);

			}
			//handling to make a system call(provisioning)
			if(subElem.getType().equals(Constants.PROVCALL)){
				provCallElem = (ProvCallElem) subElem;
				logger.debug("going to execute system command "+provCallElem.getCmmndName()+" i.e. "+provCallElem.getCommand());
				int outputVal = Helper.systemCall(provCallElem.getCommand());
				logger.debug("value returned from system call is "+outputVal);
				if(outputVal!=0)
					return false;
				try{
					long time = InapIsupSimServlet.getInstance().getConfigData().getPublishingTime();
					time = time * 1000;
					Thread.sleep(time);
					
				}catch(InterruptedException e)
				{
					logger.error("Interrupt Exception while thread was sleepin when publishing going "+e);
				}
			}

		}//@End while

		simCpb.setOriginatingAddress(InapIsupSimServlet.getInstance().getLocalAddrs().get(0));
		simCpb.setDestinationAddress(InapIsupSimServlet.getInstance().getRemoteAddr());
		simCpb.setCallStartTime(System.currentTimeMillis());
		//to supportflow wait
		InapIsupSimServlet.getInstance().setFlowInitialized(true);

		if(InapIsupSimServlet.getInstance().isActivityTestSupported()){
			if(logger.isInfoEnabled())
				logger.info("StartHandler processNode()Test suite starting tcap session timeout timers");
			//starting timer  or counter
			Timer timeoutTimer =null;
			synchronized (InapIsupSimServlet.class) {
				timeoutTimer= InapIsupSimServlet.getInstance().getTimeoutTimer();
				if(timeoutTimer ==null ){
					if(logger.isInfoEnabled())
						logger.info("StartHandler processNode() creating new timer");
					timeoutTimer = new Timer();
					InapIsupSimServlet.getInstance().setTimeoutTimer(timeoutTimer);
				}

			}
			
			long expectedDelay = Constants.DEFAULT_TCAP_SESSION_TIMEOUT;
			String configuredDelay=InapIsupSimServlet.getInstance().getConfigData().getTcapSessionTimeout();
			if(configuredDelay!=null){
				try{
					expectedDelay = Long.parseLong(configuredDelay);
				}catch (Throwable e) {
					if(logger.isDebugEnabled())
						logger.debug("StartHandler processNode()-->Invalid DEFAULT_TCAP_SESSION_TIMEOUT value configures " +
								"using default value(in seconds)" +	Constants.DEFAULT_TCAP_SESSION_TIMEOUT);
				}
			}
			expectedDelay*=1000;
			if(logger.isDebugEnabled())
				logger.debug("StartHandler processNode()-->got imeout chk val::"+expectedDelay);
			
			TimerTask task= new TcapSessionTimeoutTask(simCpb);

			try{
				timeoutTimer.schedule(task, expectedDelay,expectedDelay);
			}catch(Exception e){
				logger.error("Timer creation FAiled once...recreating::"+e.getMessage());
				timeoutTimer = new Timer();
				InapIsupSimServlet.getInstance().setTimeoutTimer(timeoutTimer);
				try{
					timeoutTimer.schedule(task, expectedDelay, expectedDelay);			
				}catch (Exception e1) {
					logger.error("Timer creation FAiled again::"+e.getMessage());
					InapIsupSimServlet.getInstance().setTimeoutTimer(null);
				}
			}
		}
		if(logger.isInfoEnabled())
			logger.info("Leaving StartHandler processNode() witha status as true");

		return true;

	}

	@Override
	protected boolean processRecievedMessage(Node node, SimCallProcessingBuffer simCpb,
			Object message) {
		if(logger.isInfoEnabled())
			logger.info("Inside StartHandler processRecievedMessage()");


		if(logger.isInfoEnabled())
			logger.info("Leaving StartHandler processRecievedMessage() with fasle as receive mode is not supported");
		return false;
	}

	@Override
	public boolean validateMessage(Node node, Object message, SimCallProcessingBuffer simCpb) {
		if(logger.isInfoEnabled())
			logger.info("Leaving StartHandler validateMessage() with fasle as receive mode is not supported");
		return false;
	}



}
