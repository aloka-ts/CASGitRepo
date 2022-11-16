/*
 * Created on Feb 14, 2005
 * 
 */
package com.baypackets.ase.sipconnector;

import java.util.Timer;
import java.util.TimerTask;

import org.apache.log4j.Logger;


/**
 * @author BayPackets
 * This class does the SM checks and transitions 
 * for Client Transactions This class does not store states.
 */
public class AsePseudoSipClientTxnStateTable 
{

	/**
	* 
	*/
	public AsePseudoSipClientTxnStateTable(AsePseudoSipClientTxn txn) 
	{
		m_associatedTxn = txn;
	}
    
	/*public static void setSipTimer(Timer sipTimer) 
	{
		m_sipTimer = sipTimer;	//NJADAUN
	}*/

	public void setProxyServerMode(boolean proxy) 
	{
		m_proxy = proxy;
	}

	public boolean getProxyServerMode() 
	{
		return m_proxy;
	}

	public void start() 
	{
		_startTimerB_F();
	}

	/**
	* @param inp
	* @return
	*/
	public int getNextState(int inp) 
	{
       		_execute(inp);
        	return m_iState;
	}
    
	/**
	* @param type
	* Sm Type Invite/NonInvite
	*/
	public void setSmType(int type) 
	{
        	m_iSmType = type;
        	if(NON_INVITE_TXN == type)
		{
            		m_iState = TRYING;
	
        	}
		else 
		{
            		m_iState = CALLING;
        	}
	}
    
	/**
	* @return
	* Get Current State
	*/
	public int getState() 
	{
        	return m_iState;
	}
    
	/**
	* @param ackReq
	*/
	public void setAckReq(AseSipServletRequest ackReq) throws AsePseudoTxnException 
	{
		if(m_iState != COMPLETED && m_iState != TERMINATED) 
		{
			throw new AsePseudoTxnException("Invalid state for ACK");
		}

		m_refAckReq = ackReq;
	}

	/**
	* @param cancelReq
	*/
	public void setCancelReq(AseSipServletRequest cancelReq) 
	{
        	m_refCancelReq = cancelReq;
	}
    
	/**
	* @return
	*/
	public AseSipServletRequest getCancelRequest() 
	{
        	return m_refCancelReq;
	}
    
	public void setCancellable() 
	{
        	m_bCancelSet = true;
	}
    
	public boolean isCancellable() 
	{
        	return m_bCancelSet;
	}
    
	/*
	* Actual SM checks.....
	* inp would be getResponseClass from DsResponse
	*/
	private void _execute(int inp)
	{
        	switch(m_iState)
		{
            		case CALLING:
            			_calling(inp);
            			break;

            		case TRYING:
            			_trying(inp);
            			break;

            		case PROCEEDING:
            			_proceeding(inp);
            			break;

            		case COMPLETED:
            			_completed(inp);
            			break;

            		case TERMINATED:
            			_terminated(inp);
            			break;

			case ERROR:
				// Do nothing, error messages would have already been logged
				break;

            		default:
				m_logger.error("_execute(int): Going into ERROR state, inp = " + inp);
            			m_iState = ERROR;
            			_cancelTimerB_F(); 
		}
	}
    
	private void _calling(int inp)
	{
        	if (m_logger.isDebugEnabled()) 
		{
            		m_logger.debug("_calling inp == "+inp);
        	}

        	if(NON_INVITE_TXN == m_iSmType)
		{
            		m_iState = ERROR;
            		_cancelTimerB_F();
			_removeTxnFromMap();

			m_logger.error("Fatal error - _calling called for non-INVITE txn");
            		return;
        	}

        	if(1 == inp) 
		{
            		m_iState = PROCEEDING;
        	} 
		else if(inp == 2) 
		{
            		m_iState = TERMINATED;
            		_cancelTimerB_F();

			// If INVITE transaction, start 2543 compatibility timer
           		_startTimer2543();
        	} 
		else if(inp <= 6) 
		{
            		m_iState = TERMINATED;
            		_cancelTimerB_F(); // any final response cancelTimer...
			_removeTxnFromMap();
        	} 
		else 
		{
            		m_iState = ERROR;
            		_cancelTimerB_F();
			_removeTxnFromMap();
        	}

        	if (m_logger.isDebugEnabled()) 
		{
            		m_logger.debug("_calling m_iState == "+m_iState);
        	} 
	}
    
	private void _proceeding(int inp)
	{
        	if (m_logger.isDebugEnabled()) 
		{
            		m_logger.debug("_proceeding inp == "+inp);
        	}

        	if(1 == inp) 
		{
            		m_iState = PROCEEDING;
            		_cancelTimerB_F();
        	} 
		else if(inp == 2) 
		{
            		m_iState = TERMINATED;
            		_cancelTimerB_F();

        		if(INVITE_TXN == m_iSmType) 
			{
				// If INVITE transaction, start 2543 compatibility timer
           			_startTimer2543();
			} 
			else 
			{
				_removeTxnFromMap();
			}
		} 
		else if(inp <= 6) 
		{
            		m_iState = TERMINATED;
            		_cancelTimerB_F(); // any final response cancelTimer...
			_removeTxnFromMap();
        	} 
		else 
		{
            		m_iState = ERROR;
            		_cancelTimerB_F();
			_removeTxnFromMap();
        	}

        	if (m_logger.isDebugEnabled()) 
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
    
	private void _terminated(int inp) 
	{
        	if (m_logger.isDebugEnabled()) 
		{
            		m_logger.debug("_terminated inp == "+inp);
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
            		_cancelTimerB_F();
			_removeTxnFromMap();

			m_logger.error("Fatal error - _trying called for INVITE txn");
            		return;
		}

		if(1 == inp) 
		{
            		m_iState = PROCEEDING;
        	} 
		else if(inp <= 6) 
		{
            		m_iState = TERMINATED;
            		_cancelTimerB_F();            
			_removeTxnFromMap();
        	} 
		else 
		{
            		m_iState = ERROR;
            		_cancelTimerB_F();
			_removeTxnFromMap();
        	}

		if (m_logger.isDebugEnabled()) 
		{
            		m_logger.debug("_trying m_iState == "+m_iState);
        	} 
	}
    
	/**
	* This method starts the timer B/F after a request is sent through txn.
	*/
	private void _startTimerB_F() 
	{
		transactionTimer = AseTransactionTimer.getInstance();
		AsePseudoClientTxnTimer newTimer = new AsePseudoClientTxnTimer(AsePseudoClientTxnTimer.TIMER_B_F, this);
        	if (m_logger.isDebugEnabled()) m_logger.debug("_startTimerB_F():scheduling with timer interval = " + TIMEOUT_B_F);
        	//m_sipTimer.schedule(newTimer, TIMEOUT_B_F);
		newTimer.initializeTime(32000);
		
		transactionTimer.addTask(newTimer);
		//transactionTimer.getObject().notify();
		m_timerB_F = newTimer;
	}
    
	private void _startTimer2543() 
	{
		transactionTimer = AseTransactionTimer.getInstance();
		AsePseudoClientTxnTimer newTimer = new AsePseudoClientTxnTimer(AsePseudoClientTxnTimer.TIMER_2543, this);
        if (m_logger.isDebugEnabled())	m_logger.debug("_startTimer2543(): timer interval :" + TIMEOUT_2543);
        	//m_sipTimer.schedule(newTimer, TIMEOUT_2543);
		newTimer.initializeTime(32000);
		transactionTimer.addTask(newTimer);
		//transactionTimer.getObject().notify();
		m_timer2543 = newTimer;
	}
    
	private void _cancelTimerB_F() 
	{
        	if(null != m_timerB_F) 
		{
        		m_logger.debug("Cancelling Timer B_F");
            		m_timerB_F.cancel();
			m_timerB_F = null;
        	}
	}
    
	public void _removeTxnFromMap() 
	{
		m_associatedTxn.remove();
	}

	public synchronized AsePseudoSipClientTxn getAsePseudoSipClientTxn()
	{
		return m_associatedTxn;
	}

	private static Logger m_logger = Logger.getLogger(AsePseudoSipClientTxnStateTable.class.getName());

	public int m_iState;
    
	private int m_iSmType;
    
	private int m_iRetransmitCount = 0;
    
	public AsePseudoSipClientTxn m_associatedTxn = null;

	public AsePseudoClientTxnTimer m_timerB_F = null;
    
	public AsePseudoClientTxnTimer m_timer2543 = null;
    
	private AseSipServletRequest m_refAckReq = null;
    
	private AseSipServletRequest m_refCancelReq = null;
    
	private boolean m_bCancelSet = false;

	private boolean m_proxy = false;
    
	//private static Timer m_sipTimer;		//NJADAUN

	private static AseTransactionTimer transactionTimer ;	//Added by NJADAUN

	// TBDNeeraj this has to be moved to AseSipConstants class...    
	private final static int TIMEOUT_B_F = 64*AseSipConstants.T1;
	//private final static int TIMEOUT_2543 = AseSipConstants.T2;
	private final static int TIMEOUT_2543 = 32000;		//32 seconds added by NJADAUN
    
	public final static int CALLING	= 1;
	public final static int PROCEEDING	= 2;
	public final static int COMPLETED	= 3;
	public final static int TRYING		= 4;
	public final static int TERMINATED	= 5;
    
	// Next State returning error should be checked by client code 
	public final static int ERROR = -1;
	public final static int INVITE_TXN = 10;
	public final static int NON_INVITE_TXN = 20;
}

