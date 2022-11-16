package com.baypackets.ase.sipconnector;

import org.apache.log4j.Logger;
public class AsePseudoClientTxnTimer
{
	 private static Logger m_logger = Logger.getLogger(AsePseudoClientTxnTimer.class.getName());

        AsePseudoClientTxnTimer(int type, AsePseudoSipClientTxnStateTable txnState)
        {
                m_iType = type;
                obj = new Object();
                asePseudoSipClientTxnStateTable = txnState;
        }
                /*
                * @see java.lang.Runnable#run()
                */
	public void initializeTime(int duration)
	{
		DURATION = duration;
		time  = System.currentTimeMillis()+duration;
	}
	public int getDuration()
	{
		return DURATION;
	}
		
        public void run()
        {
                switch(m_iType)
                {
                                case TIMER_2543:
                                       	asePseudoSipClientTxnStateTable.m_timer2543 = null;
                                        if (m_logger.isDebugEnabled()) m_logger.debug("AsePseudoClientTxnTimer():  TIMER 2543 expires.");
                                        asePseudoSipClientTxnStateTable._removeTxnFromMap();
                                        break;

                                case TIMER_B_F:
                                        asePseudoSipClientTxnStateTable.m_timerB_F = null;

                                       if (m_logger.isDebugEnabled()) m_logger.debug("AsePseudoClientTxnTimer():  TIMER B expires.");
                                        // Create a timeout response and send Use Pseudo Sil class...
                                        if(AsePseudoSipClientTxnStateTable.CALLING == asePseudoSipClientTxnStateTable.m_iState || AsePseudoSipClientTxnStateTable.PROCEEDING == asePseudoSipClientTxnStateTable.m_iState)
                                        {
                                                // Call timeout handler on transaction object
                                                //asePseudoSipClientTxnStateTable.m_associatedTxn.timeout();
						(asePseudoSipClientTxnStateTable.getAsePseudoSipClientTxn()).timeout();
                                        }
                                        break;

                                default:
                                        m_logger.error("AsePseudoClientTxnTimer(): Invalid timer type!");
                        }
                }

                public long getTime()
                {
                        //syncronized(obj)
                        //{
                                return time;
                        //}
                }
                public synchronized void cancel()
                {
                        //syncronized(obj)
                        //{
				if (m_logger.isDebugEnabled()) m_logger.debug("Cancelling the ClientTxnTimer "+m_iType+" Duration ===> "+DURATION);
                                cancel = true;
                        //}
                }

		public synchronized boolean isCancel()
		{
			return cancel;
		}

                int m_iType;
                Object obj ;
		boolean cancel = false;
                long time = 0;
		int DURATION =0;
                static final int TIMER_2543 = 1;
                static final int TIMER_B_F = 0;
                AsePseudoSipClientTxnStateTable asePseudoSipClientTxnStateTable = null;
        }


