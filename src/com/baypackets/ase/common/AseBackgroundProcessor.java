package com.baypackets.ase.common;

import java.util.LinkedList;
import java.util.ListIterator;

import org.apache.log4j.Logger;

import com.baypackets.bayprocessor.agent.MComponent;
import com.baypackets.bayprocessor.agent.MComponentState;
import com.baypackets.bayprocessor.agent.OperationType;
import com.baypackets.bayprocessor.slee.common.Pair;
import com.baypackets.bayprocessor.slee.common.UnableToChangeStateException;
import com.baypackets.bayprocessor.slee.common.UnableToUpdateConfigException;


public class AseBackgroundProcessor implements MComponent, Runnable{

	private int rate = 1000;
	private boolean run_process = true;
	private LinkedList list = new LinkedList();
	private static AseBackgroundProcessor processor = new AseBackgroundProcessor();
	private static Logger logger = Logger.getLogger(AseBackgroundProcessor.class);
	
	public AseBackgroundProcessor()	{
	}
	

	public void changeState(MComponentState state) throws UnableToChangeStateException  {

		try  {
			if(state.getValue() == MComponentState.LOADED) {
				this.initialize();
			}
			if (state.getValue() == MComponentState.RUNNING){
		                this.start();
			}
			if(state.getValue() == MComponentState.STOPPED){
				this.stop();
			}
		} catch (Exception e)  {
			logger.error("changeState: ", e);
			throw new UnableToChangeStateException(e.getMessage());
		}
	}

	public void initialize()  {

	}

	public void start()  {
		if (logger.isDebugEnabled()) {

			logger.debug("Going to start Background Processor");
		}
		try {	
			new Thread(this, "BKGProcessor").start();
		} catch(Exception e)  {
			logger.error(e.getMessage(), e);
		}
	}
	
	public void stop()  {
		logger.error("Going to stop Background Processor");
		run_process = false;
	}	

	public void updateConfiguration(Pair[] configData, OperationType opType)
                throws UnableToUpdateConfigException {
                // No op
	}

	/**
	 * Register periodic callbacks delayed by give offset.
	 *
	 * @param offset Time in seconds after which callbacks should be started
	 * @param period Period in seconds for the callbacks
	 */
	public void registerBackgroundListener(BackgroundProcessListener listener, long offset, long period)	{
		if (logger.isDebugEnabled()) {

			logger.debug("Registering "+listener.toString()+" for Background Processor");
		}
		long currentTime = System.currentTimeMillis();
		if (logger.isDebugEnabled()) {

			logger.debug("Registering new Component");
		}
		Component component = new Component();
		component.setListener(listener);
		long t = currentTime + (offset*1000);
		component.setProcessTime(t);
		component.setDumpTime(period*1000);
		this.addValueInList(list,component,currentTime);
		this.printInfo(list);
	}
	
	public void registerBackgroundListener(BackgroundProcessListener listener, long period)	{
		if (logger.isDebugEnabled()) {

			logger.debug("Registering "+listener.toString()+" for Background Processor");
		}
		long currentTime = System.currentTimeMillis();
		if (logger.isDebugEnabled()) {

			logger.debug("Registering new Component");
		}
		Component component = new Component();
		component.setListener(listener);
		long t = currentTime + (period*1000);
		component.setProcessTime(t);
		component.setDumpTime(period*1000);
		this.addValueInList(list,component,currentTime);
		this.printInfo(list);
	}
	
	public void run() {
		try {
		
			Component comp = null;
			long processTime = 0;
			if (logger.isDebugEnabled()) {

				logger.debug("Starting Background Processor");
			}		
			while(run_process)	{
				
				try	{
					Thread.sleep(rate);
				} catch (InterruptedException e)	{
					continue;
				}
				
				synchronized(this)	{
					int size = list.size();
					if(size == 0) {
						logger.error("SIZE of List is 0: so returning");
						return;
					}
					
					for(int i=0; i<size ; i++) {
						comp = (Component)list.get(i);
						processTime = comp.getProcessTime();
	
						if((comp != null) && (processTime != 0)) {
												
							long currentTime = System.currentTimeMillis();
							if(currentTime >= processTime)	{
								comp.getListener().process(currentTime);
								list.remove(i);
								comp.setProcessTime(currentTime+comp.getDumpTime());
								this.addValueInList(list,comp,currentTime);
									
							} else {
								break;	
							}
						} else {
							if (logger.isDebugEnabled()) {

								logger.debug("Component is null or processtime is 0"); 
							}
						}
					}
				}
					
				
			}
	
		} catch(Throwable t ) {
			logger.error("Caught at Thread level",t);
		}
	}
		
		private synchronized void addValueInList(LinkedList list, Component comp, long currentTime)	{
			ListIterator iter = list.listIterator();
			while(iter.hasNext())	{
				Component tmp = (Component)iter.next();
				if(tmp.getProcessTime() >= comp.getProcessTime())	{
					iter.previous();
					break;
				}
			}
			iter.add(comp);
		}
		
		private synchronized void printInfo(LinkedList list) {
                        ListIterator iter = list.listIterator();
                        while(iter.hasNext())   {
                                Component tmp = (Component)iter.next();
						if (logger.isDebugEnabled()) {

	                                logger.debug("Name==="+tmp.getListener());
      	                          logger.debug("Process time ==="+tmp.getProcessTime());
						}
                        }
                }


}
