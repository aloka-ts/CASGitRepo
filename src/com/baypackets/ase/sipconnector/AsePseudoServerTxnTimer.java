package com.baypackets.ase.sipconnector; 

import org.apache.log4j.Logger;

/*
* This timer takes care of
* Timeout of requests...
* if this timer fires then a 2xx response is retransmitted...
*/
public class AsePseudoServerTxnTimer
{
        private static Logger m_logger = Logger.getLogger(AsePseudoServerTxnTimer.class.getName());
	AsePseudoServerTxnTimer(int type, AsePseudoSipServerTxnStateTable txnTable)
        {
        	m_iType = type;
		serverTxnTable = txnTable;
        }


	public void initializeTime(int tim)
	{
		time = tim+System.currentTimeMillis();
		DURATION = tim;
	}

	public long getTime()
	{
		return time;
	}

	public int getDuration()
	{
		return DURATION;
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
		
        public void run()
        {
 	       switch(m_iType)
               {
        	       case RTX_2XX_TIMER:
                	       if(serverTxnTable.m_iRetransmitCount < AsePseudoSipServerTxnStateTable.MAX_RETRANSMITS )
                               {
                        	       if (m_logger.isDebugEnabled()) m_logger.debug("AsePseudoServerTxnTimer(): Retransmission of 2xx");

                                       // Create a timeout response and send Use Pseudo Sil class...
                                       serverTxnTable.m_associatedTxn.retransmit2xx();
                                       synchronized(this)
                                       {
                                	       if(serverTxnTable.m_timerG != null)
                                               {
                                        	        serverTxnTable._startTimer2xx();
                                               }
                                       }
                                }
                                else
                                {
                                        // check if ACK recvd...
                                        if(!serverTxnTable.m_bAckRecvd)
                                        {
                                                // ACK Not recvd...
                                              if (m_logger.isDebugEnabled())  m_logger.debug("2xx ACK timedout! Notify application...");
                                                serverTxnTable.getAsePseudoSipServerTxn().ackTimeout();
                                        }
                                }
                                break;

                      case TIMER_2543:
                      		if (m_logger.isDebugEnabled()) m_logger.debug("AsePseudoServerTxnTimer():  TIMER_2543 expires.");

                                serverTxnTable.m_timer2543 = null;
                               	serverTxnTable. _removeTxnFromMap();
                                break;

                     case RESPONSE_TIMER:
                               if (m_logger.isDebugEnabled()) m_logger.debug("AsePseudoServerTxnTimer():  RESPONSE_TIMER expires.");

                                serverTxnTable.m_timerResponse = null;
                               if (m_logger.isDebugEnabled()) m_logger.debug("Response timedout! Notify application...");
                                serverTxnTable.getAsePseudoSipServerTxn().txnTimeout();
                                serverTxnTable._removeTxnFromMap();
                                break;

                     default:
                                m_logger.error("Invalid timer type!");
		} //switch
	}
        int m_iType;
	long time = 0;
	boolean cancel =false;
	int DURATION = 0;
	AsePseudoSipServerTxnStateTable serverTxnTable = null;
        static final int RTX_2XX_TIMER = 1;
        static final int TIMER_2543 = 2;
        static final int RESPONSE_TIMER = 3;
}


