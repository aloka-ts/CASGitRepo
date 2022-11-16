/*
 * Created on Feb 15, 2005
 * 
 */
package com.baypackets.ase.sipconnector;

import java.util.Timer;
import java.util.TimerTask;

import org.apache.log4j.Logger;

/**
 * @author BayPackets
 *
 */
public class AsePseudoSipServerTxnStateTable 
{

	/**
	* 
	*/
	public AsePseudoSipServerTxnStateTable(AsePseudoSipServerTxn txn) 
	{
		m_associatedTxn = txn;
		_startTimerResponse();
	}

	/*public static void setSipTimer(Timer sipTimer) 
	{
		m_sipTimer = sipTimer;
	}*/

	public void setProxyServerMode(boolean proxy) 
	{
		m_proxy = proxy;
	}

	public boolean getProxyServerMode() 
	{
		return m_proxy;
	}

	public int getNextState(int inp) 
	{
       		_execute(inp);
        	return m_iState;
	}
    
	public void setResponse(AseSipServletResponse resp) 
	{
        	m_refSipServletResponse = resp;
	}
    
	public AseSipServletResponse getResponse() 
	{
        	return m_refSipServletResponse;
	}
    
	public void setSmType(int type)
	{
        	m_iSmType = type;
        	if(NON_INVITE_TXN == type)
		{
            		m_iState = TRYING;
        	}
		else 
		{
            		m_iState = PROCEEDING;
        	}
	}
    
	public void setAckRecvd(boolean req) 
	{
        	m_bAckRecvd = req;

		if(m_bAckRecvd) 
		{
			if(!m_proxy) 
			{
        			// Stop Retrans of 2xx timer...
				synchronized(this) 
				{
					if(m_timerG != null) 
					{
        					if (m_logger.isDebugEnabled()) m_logger.debug("Cancelling 2xx retransmission Timer.");
        					m_timerG.cancel();
						m_timerG = null;
					}
				}
			}

           		m_iState = TERMINATED;
			_removeTxnFromMap();
		} 
		else 
		{
			m_logger.error("Why m_bAckRecvd is set to false?");
		}
	}
    
	public int getState() 
	{
        	return m_iState;
	}
    
	/*
	* Actual SM checks.....
	* inp would be getResponseClass from DsResponse
	*/
	private void _execute(int inp)
	{
        	if(m_logger.isDebugEnabled()) 
		{
			m_logger.debug("state is : " + m_iState);
			m_logger.debug("input is : " + inp);
		}

        	switch(m_iState)
		{
            		case PROCEEDING:
            			_proceeding(inp);
            			break;

            		case COMPLETED:
            			_completed(inp);
            			break;

            		case CONFIRMED:
            			_confirmed(inp);
            			break;

            		case TERMINATED:
            			_terminated(inp);
            			break;
            		case TRYING:
            			_trying(inp);
            			break;

            		default:
            			m_iState = ERROR; 
				m_logger.error("_execute(int): Going into ERROR state");
		}
	}
    
	private void _trying(int inp) 
	{
        	if (m_logger.isDebugEnabled()) 
		{
            		m_logger.debug("_trying inp == "+inp);
        	}

        	if(INVITE_TXN == m_iSmType)
		{
        		m_iState = ERROR;
			_removeTxnFromMap();

			m_logger.error("Fatal error: _tring in INVITE txn");
            		return;
        	}

        	if(1 == inp) 
		{
            		m_iState = PROCEEDING;
        	} 
		else if(inp <= 6) 
		{
			if(m_timerResponse != null) 
			{
				m_timerResponse.cancel();
				m_timerResponse = null;
			}
            		m_iState = TERMINATED;
			_removeTxnFromMap();
        	} 
		else 
		{
            		m_iState = ERROR;
			_removeTxnFromMap();
        	}

        	if (m_logger.isDebugEnabled()) 
		{
            		m_logger.debug("_trying m_iState == "+m_iState);
        	} 
	}
    
	private void _proceeding(int inp) 
	{
        	if(m_logger.isDebugEnabled()) 
		{
            		m_logger.debug("_proceeding inp == "+inp);
        	}

        	if(1 == inp) 
		{
			//
			// 1xx response
			//

            		m_iState = PROCEEDING;
        	} 
		else if(2 == inp) 
		{
			//
			// 2xx response
			//

			if(m_timerResponse != null) 
			{
				m_timerResponse.cancel();
				m_timerResponse = null;
			}

			if(m_iSmType == INVITE_TXN) 
			{
				if(!m_proxy) 
				{
					// start 2xx retransmission timer, if UAS
            				_startTimer2xx();
				}
			_startTimer2543();
			} 
			else 
			{
				_removeTxnFromMap();
			}

           		m_iState = TERMINATED;
		} 
		else if(inp <= 6) 
		{
			//
			// 3xx - 6xx response
			//

			if(m_timerResponse != null) 
			{
				m_timerResponse.cancel();
				m_timerResponse = null;
			}

			if(m_iSmType == INVITE_TXN) 
			{
            			m_iState = COMPLETED;
			} 
			else 
			{
           			m_iState = TERMINATED;
				_removeTxnFromMap();
			}
		} 
		else 
		{
			m_logger.error("_proceeding(int): INVITE txn, going into ERROR state");
            		m_iState = ERROR;
			_removeTxnFromMap();
        	}

        	if(m_logger.isDebugEnabled()) 
		{
            		m_logger.debug("_proceeding m_iState == "+m_iState);
        	}
	}
    
	private void _completed(int inp)
	{
        	if (m_logger.isDebugEnabled()) 
		{
            		m_logger.debug("_completed inp == "+inp);
        	} 
    	}
    
    
	private void _confirmed(int inp) 
	{
        	if (m_logger.isDebugEnabled()) 
		{
            		m_logger.debug("_confirmed inp == "+inp);
        	} 
    	}
    
    
	private void _terminated(int inp) 
	{
        	if (m_logger.isDebugEnabled()) 
		{
            		m_logger.debug("_terminated inp == "+inp);
        	} 
    	}
    
	public void _startTimer2xx() 
	{
		// Commenting following for now as 2xx retransmission
		// should ideally be done by UAS core. Doing the same here would
		// require modication into searching algo for transaction for
		// 2xx ACKs since branch id will be different.
		// DONE!

		// Create new timer task
        	m_timerG = new AsePseudoServerTxnTimer(AsePseudoServerTxnTimer.RTX_2XX_TIMER, this);

		// Compute timer value
		if(m_iRetransmitCount == 0) 
		{
           		m_iTimerVal = AseSipConstants.T1;
		} 
		else if(m_iTimerVal < AseSipConstants.T2) 
		{
            		m_iTimerVal *= 2;
		}

		if(m_iTimerVal > AseSipConstants.T2) 
		{
           		m_iTimerVal = AseSipConstants.T2;
		}

		m_iRetransmitCount++;
		if(m_logger.isDebugEnabled())
			m_logger.debug("2xx retx count [" + m_iRetransmitCount +"] timer interval [" + m_iTimerVal + " ms]");

		// Schedule timer
        	//m_sipTimer.schedule(m_timerG, m_iTimerVal);
		m_timerG.initializeTime(m_iTimerVal);
		transactionTimer = AseTransactionTimer.getInstance();
                transactionTimer.addServerTask(m_timerG);
		//transactionTimer.getObject().notify();

	}
    
	// This acts as 2543 compatibility timer
	private void _startTimer2543() 
	{
		transactionTimer = AseTransactionTimer.getInstance();
        	m_timer2543 = new AsePseudoServerTxnTimer(AsePseudoServerTxnTimer.TIMER_2543, this);
        	int timerVal = 64*AseSipConstants.T1;
		if(m_logger.isDebugEnabled())
        		m_logger.debug("_startTimer2543(): timer interval" + timerVal);
        	//m_sipTimer.schedule(m_timer2543, timerVal);
		m_timer2543.initializeTime(timerVal);
		transactionTimer.addServerTask(m_timer2543);
		//transactionTimer.getObject().notify();
		
    	}
    
	private void _startTimerResponse() 
	{
		transactionTimer = AseTransactionTimer.getInstance();
        	m_timerResponse = new AsePseudoServerTxnTimer(AsePseudoServerTxnTimer.RESPONSE_TIMER, this);
        	int timerVal = 160000; // 160 secs
		if(m_logger.isDebugEnabled())
        		m_logger.debug("_startTimerResponse(): timer interval" + timerVal);
        	//m_sipTimer.schedule(m_timerResponse, timerVal);
		m_timerResponse.initializeTime(timerVal);
                transactionTimer.addServerTask(m_timerResponse);
		//transactionTimer.getObject().notify();
    	}

	public void _removeTxnFromMap() 
	{
		m_associatedTxn.remove();
	}

	public synchronized AsePseudoSipServerTxn getAsePseudoSipServerTxn()
	{
		return m_associatedTxn;
	}

	private static Logger m_logger =Logger.getLogger(AsePseudoSipServerTxnStateTable.class.getName());

	private int m_iState;
    
	public volatile int m_iRetransmitCount = 0;
    
	private int m_iSmType;
    
	private boolean m_proxy = false;
    
	//static private Timer m_sipTimer;
    
	public AsePseudoSipServerTxn m_associatedTxn = null;

	public AsePseudoServerTxnTimer m_timerG = null;
	public AsePseudoServerTxnTimer m_timer2543;
	public AsePseudoServerTxnTimer m_timerResponse;
    
	private AseSipServletResponse m_refSipServletResponse;
	private static AseTransactionTimer transactionTimer ;
	public boolean m_bAckRecvd = false;
    
	public final static int MAX_RETRANSMITS = 7;
	public final static int TIMER_VAL = 32000; // 32 secs
    
	//
	// States start ..    
	//

	public final static int PROCEEDING	= 1;
	public final static int COMPLETED	= 2;
	public final static int CONFIRMED	= 3;
	public final static int TRYING		= 4;
	public final static int TERMINATED	= 5;

	//
	// States end ..     
    	//

	// Next State returning error should be checked by client code 
	public final static int ERROR = -1;
	public final static int INVITE_TXN = 10;
	public final static int NON_INVITE_TXN = 20;
    
	int m_iTimerVal = 0;
}
