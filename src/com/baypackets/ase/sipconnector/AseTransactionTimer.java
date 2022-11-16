/** Created on 24th May 2006
*/

package com.baypackets.ase.sipconnector;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import java.io.IOException;
import java.util.ArrayList;
import com.baypackets.ase.common.AseEvent;
import com.baypackets.ase.common.AseMessage;
import com.baypackets.ase.common.Registry;
import com.baypackets.ase.util.StackDumpLogger;
import com.baypackets.ase.util.Constants;
import com.baypackets.ase.util.AseThreadMonitor;
import com.baypackets.bayprocessor.slee.common.ConfigRepository;
import com.baypackets.bayprocessor.slee.common.ThreadAlreadyRegisteredException;
import com.baypackets.bayprocessor.slee.common.ThreadNotRegisteredException;
import com.baypackets.bayprocessor.slee.common.UnableToChangeStateException;
import com.baypackets.bayprocessor.slee.common.UnableToUpdateConfigException;
import com.baypackets.bayprocessor.slee.threadmonitor.MonitoredThread;
import com.baypackets.bayprocessor.slee.threadmonitor.MonitoredThreadState;
import com.baypackets.bayprocessor.slee.threadmonitor.ThreadMonitor;
import com.baypackets.bayprocessor.slee.threadmonitor.ThreadOwner;
import com.baypackets.bayprocessor.slee.internalservices.TraceService;
import com.baypackets.ase.container.AseEngine;
public class AseTransactionTimer extends MonitoredThread implements ThreadOwner, Runnable
{

	private ArrayList ClientTxnTimer = new  ArrayList();		//32 seconds Timer  task
	private ArrayList ClientTxnTimerAux = new  ArrayList();
	
	private ArrayList ServerTxnTimer32 = new  ArrayList();		//32 seconds Timer  task
	ArrayList ServerTxnTimerAux32 = new  ArrayList();

	private ArrayList ServerTxnTimer40 = new  ArrayList();		//40 seconds Timer  task
	private ArrayList ServerTxnTimerAux40 = new  ArrayList();

	private ArrayList ServerTxnTimer = new  ArrayList();		//Variable Timers
	private ArrayList ServerTxnTimerAux = new  ArrayList();

	private static Logger logger = Logger.getLogger(AseTransactionTimer.class);
        private static AseTransactionTimer aseTimer = new AseTransactionTimer();

	private Object obj1 = new Object();
	private Object obj2 = new Object();
	private Object obj3 = new Object();
	private Object obj4 = new Object();

	private boolean stopped = false;
	private ThreadMonitor threadMonitor;

        private AseTransactionTimer()
	{

		super ("AseTransactionTimer", AseThreadMonitor.getThreadTimeoutTime(),(TraceService)Registry.lookup(Constants.NAME_TRACE_SERVICE));
                this.threadMonitor = (ThreadMonitor)Registry.lookup(Constants.NAME_THREAD_MONITOR);
		start();
        }

        public static AseTransactionTimer getInstance()
        {
                return aseTimer;
        }
	
	public void run()
	{
		try
		{
			if (logger.isDebugEnabled()) logger.debug("AseTransactionTimer is running");
			AseEngine engine =null;
			try 
			{
				engine = (AseEngine)Registry.lookup(Constants.NAME_ENGINE);
		if (logger.isDebugEnabled())	logger.debug("Registering with ThreadMonitor...");
				this.setThreadState(MonitoredThreadState.Idle);
				this.threadMonitor.registerThread(this);
				if (logger.isDebugEnabled()) logger.debug("Successfully registered with ThreadMonitor.");
                	} 
			catch (ThreadAlreadyRegisteredException e) 
			{
                        	logger.error("Thread is already registered with ThreadMonitor.", e);
                	}
		
			while (!this.stopped)
                	{
                 		//if (stopped) return;

				try
				{
					Thread.sleep(1000);
					int size = ServerTxnTimer.size();
					long miliseconds = System.currentTimeMillis();

					try
					{
						for(int ii=0;ii<size;ii++)
						{
							AsePseudoServerTxnTimer timer = (AsePseudoServerTxnTimer)ServerTxnTimer.get(ii);	
	                                		if(timer.isCancel())
                                        		{
								if(logger.isInfoEnabled()) logger.info("Timer is cancelled AsePseudoServerTxnTimer");
                                                		ServerTxnTimer.remove(ii);
                                        		}
                                        		else
                                        		{
                                                		if(timer.getTime()>(miliseconds+100))
                                                		{
									continue;
                                                		}
                                                		else
                                                		{
									try
									{
                                                        			timer.run();
									}
									catch(Exception ee)
									{
										logger.error("NJADAUN ", ee);
									}
                                                        		ServerTxnTimer.remove(ii);
                                                		}
                                        		}

                                        		size = ServerTxnTimer.size();
						}

					}
					catch(Exception e)
					{
						logger.error("Exception in MAIN SERVERLIST NJADAUN ", e);
					}
				
					synchronized(obj1)
					{
						int sze = ServerTxnTimerAux.size();
						if(ServerTxnTimerAux !=null)
						ServerTxnTimer.addAll(ServerTxnTimerAux);
						ServerTxnTimerAux.removeAll(ServerTxnTimerAux);
						ServerTxnTimerAux = null;
						ServerTxnTimerAux = new ArrayList();
					}
				
			//////////////////////////////////////////////////////////////////

					//miliseconds = System.currentTimeMillis();

					int sizz = ServerTxnTimer32.size();
					try
					{
						for(int ii = 0;ii<sizz; ii++)
						{
					
                                			AsePseudoServerTxnTimer timer32 = (AsePseudoServerTxnTimer)ServerTxnTimer32.get(ii);

							if(timer32.isCancel())
							{
								if(logger.isInfoEnabled()) logger.info("Timer is cancelled AsePseudoServerTxnTimer 32 seconds");
								
								ServerTxnTimer32.remove(ii);
							}
							else
							{
								if(timer32.getTime()>(miliseconds+100))
								{
									break;
								}
								else
								{
									try
									{
										timer32.run();
									}
									catch(Exception ee)
									{
										logger.error("NJADAUN ",ee);
									}
									ServerTxnTimer32.remove(ii);
								}
							}
	
							sizz = ServerTxnTimer32.size();
						}
					}
					catch(Exception e)
					{
						logger.error("Exception in 32 Server List NJADAUN ", e);
					}
                                	synchronized(obj2)
                                	{
                                        	int sze = ServerTxnTimerAux32.size();
                                        	if(ServerTxnTimerAux32 !=null)
                                                ServerTxnTimer32.addAll(ServerTxnTimerAux32);
                                        	ServerTxnTimerAux32.removeAll(ServerTxnTimerAux);
                                        	ServerTxnTimerAux32 = null;
                                        	ServerTxnTimerAux32 = new ArrayList();
                                	}
//////////////////////////////////////////////////////////////////////////////

                                	//miliseconds = System.currentTimeMillis();

                                	sizz = ServerTxnTimer40.size();
					try
					{
                                		for(int i = 0;i<sizz; i++)
                                		{
                                        		AsePseudoServerTxnTimer timer40 = (AsePseudoServerTxnTimer)ServerTxnTimer40.get(i);

                                        		if(timer40.isCancel())
                                        		{
							if(logger.isInfoEnabled()) 	logger.info("Timer is cancelled AsePseudoServerTxnTimer 40");
                                                		ServerTxnTimer40.remove(i);
                                        		}
                                        		else
                                        		{
                                                		if(timer40.getTime()>(miliseconds+100))
                                                		{
                                                        		break;
                                                		}
                                                		else
                                                		{
									try
									{
                                                        			timer40.run();
									}
									catch(Exception ee)
									{
										logger.error("NJADAUN ", ee);
									}
                                                        		ServerTxnTimer40.remove(i);
                                                		}
                                        		}
                                        		sizz = ServerTxnTimer40.size();
						}
                                	}
					catch(Exception e)
					{
						logger.error("Exception in 40 Server List NJADAUN ", e);
					}

                                	synchronized(obj3)
                                	{
                                        	int sze = ServerTxnTimerAux40.size();
                                        	if(ServerTxnTimerAux40 !=null)
                                                ServerTxnTimer40.addAll(ServerTxnTimerAux40);
                                        	ServerTxnTimerAux40.removeAll(ServerTxnTimerAux40);
                                        	ServerTxnTimerAux40 = null;
                                        	ServerTxnTimerAux40 = new ArrayList();
                                	}

/////////////////////////////////////////////////////////////////////////

                                	//miliseconds = System.currentTimeMillis();
                                	sizz = ClientTxnTimer.size();
					try
					{
                                		for(int l = 0;l<sizz; l++)
                                		{
                                        		AsePseudoClientTxnTimer timer = (AsePseudoClientTxnTimer)ClientTxnTimer.get(l);
                                        		if(timer.isCancel())
                                        		{
								if(logger.isInfoEnabled()) logger.info("Timer is cancelled AsePseudoClientTxnTimer");
                                                		ClientTxnTimer.remove(l);
                                        		}
                                        		else
                                        		{
                                                		if(timer.getTime()>(miliseconds+110))
                                                		{
                                                        		break;
                                                		}
                                                		else
                                                		{
									try
									{
                                                        			timer.run();
									}
									catch(Exception ee)
									{
										logger.error("NJADAUN ", ee);
									}
                                                        		ClientTxnTimer.remove(l);
                                                		}
                                        		}

                                        		sizz = ClientTxnTimer.size();
						}
                                	}
					catch(Exception e)
					{
						logger.error("Exception in CLIENT LIST NJADAUN ", e);
					}

                                	synchronized(obj4)
                                	{
                                        	int sze = ClientTxnTimerAux.size();
                                        	if(ClientTxnTimerAux !=null)
                                                ClientTxnTimer.addAll(ClientTxnTimerAux);
                                        	ClientTxnTimerAux.removeAll(ClientTxnTimerAux);
                                        	ClientTxnTimerAux = null;
                                        	ClientTxnTimerAux = new ArrayList();
                                	}
					this.updateTimeStamp();
                                	this.setThreadState(MonitoredThreadState.Running);
                                	this.setThreadState(MonitoredThreadState.Idle);

				}
				catch(Exception e)
				{
					logger.error("EXception in TIMER NJADAUN ", e);
				}
			}
		}
		catch(Exception ee)
		{
			logger.error("EXception in TIMER NJADAUN ", ee);
		}
		finally 
		{
                       	try 
			{
                             if (logger.isDebugEnabled())   logger.debug("Unregistering with ThreadMonitor...");
                               	threadMonitor.unregisterThread(this);
                             if (logger.isDebugEnabled())   logger.debug("Successfully unregistered with ThreadMonitor.");
                       	} 
			catch (ThreadNotRegisteredException e) 
			{
                               	logger.error("Thread is not currently registered with ThreadMonitor.");
                       	}
		}

	}

	public void addTask(AsePseudoClientTxnTimer timer)
	{
		synchronized(obj4) 
		{
                    if (logger.isDebugEnabled())    logger.debug("  Adding AsePseudoClientTxnTimer "+ timer);
                	ClientTxnTimerAux.add(timer);
                }
	}
	public void addServerTask(AsePseudoServerTxnTimer timer)
	{
		synchronized(obj2) 
		{
			if(timer.getDuration()==32000)
			{
                        if (logger.isDebugEnabled())	logger.debug("  Adding AsePseudoServerTxnTimer "+ timer);
                		ServerTxnTimerAux32.add(timer);
			}
		}
		synchronized(obj3)
		{
			if(timer.getDuration()==40000)
			{
                       if (logger.isDebugEnabled())		logger.debug("  Adding AsePseudoServerTxnTimer "+ timer);
               			ServerTxnTimerAux40.add(timer);
			}
		}
		synchronized(obj1)
		{	if(timer.getDuration()<32000)
			{
                        if (logger.isDebugEnabled())	logger.debug("  Adding AsePseudoServerTxnTimer "+ timer);
                		ServerTxnTimerAux.add(timer);
			}
		}
	}

	public void stopTimer()
	{
		stopped = true;
	}

	public ThreadOwner getThreadOwner() 
	{
		return this;
	}

	public int threadExpired(MonitoredThread thread) 
	{
		logger.error(thread.getName() + " expired");
		StackDumpLogger.logStackTraces();
		return ThreadOwner.SYSTEM_RESTART;
        }

}

