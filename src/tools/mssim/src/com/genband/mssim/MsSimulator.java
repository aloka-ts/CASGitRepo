/**
 *	Filename:	MsSimulator.java
 *	Created On:	22-Jan-2007
 */

package com.genband.mssim;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Map;
import java.util.Map.Entry;
import java.util.HashMap;
import java.util.Set;
import java.util.Iterator;
import java.util.ConcurrentModificationException;

import org.apache.log4j.Logger;

import com.dynamicsoft.DsLibs.DsSipDialog.DsSipInvitationManager;
import com.dynamicsoft.DsLibs.DsSipDialog.DsSipNewInvitationInterface;
import com.dynamicsoft.DsLibs.DsSipDialog.DsSipDialogDefaults;
import com.dynamicsoft.DsLibs.DsSipLlApi.DsSipTransportLayer;
import com.dynamicsoft.DsLibs.DsSipLlApi.DsSipTransactionManager;
import com.dynamicsoft.DsLibs.DsSipObject.DsByteString;
import com.dynamicsoft.DsLibs.DsSipObject.DsSipTransportType;
import com.dynamicsoft.DsLibs.DsSipObject.DsSipURL;
import com.dynamicsoft.DsLibs.DsSipObject.DsSipContactHeader;
import com.dynamicsoft.DsLibs.DsSipParser.DsSipParserException;
import com.dynamicsoft.DsLibs.DsSipParser.DsSipParserListenerException;

import com.genband.threadpool.ThreadPool;
import com.genband.threadpool.Work;
import com.genband.threadpool.WorkHandler;

/**
 * SIP INVITE   -------> Rx
 * #### STATE_A ####
 * SIP 180      <------- Tx
 * 
 * #### STATE_B ####
 *
 * pause    < g seconds >
 * 
 * HTTP GET     <------- Tx
 * HTTP 200     -------> Rx
 * HTTP ERR     -------> Rx'
 * 
 * #### STATE_C ####
 *
 * pause    < f seconds >
 * 
 * SIP 2xx      <------- Tx
 * SIP Err      <------- Tx'
 * SIP ACK(2xx) -------> Rx
 * 
 * #### STATE_D ####
 *
 * pause    < i seconds >
 * 
 * HTTP POST    <------- Tx
 * HTTP 200     -------> Rx
 * HTTP ERR     -------> Rx'
 * 
 * #### STATE_E ####
 *
 * SIP BYE      -------> Rx
 * SIP 200      <------- Tx
 * 
 */
public class MsSimulator implements WorkHandler {

	private static final Logger logger = Logger.getLogger(MsSimulator.class);

	private static int THREADPOOL_SIZE = 4;

	private static String OPT_ADDR = "-h"; // a.b.c.d or hostname
	private static String OPT_PORT = "-p"; // Integer number
	private static String OPT_TRANSPORT = "-t"; // TCP/UDP
	private static String OPT_INTERACTION_PERIOD = "-i"; // in seconds
	private static String OPT_GET_REQ_DELAY = "-g"; // in seconds
	private static String OPT_FINAL_RESP_DELAY = "-f"; // in seconds
	private static String OPT_FINAL_RESP_CODE = "-c"; // [200-699]

	public static final int STATE_A = 0;
	public static final int STATE_B = 1;
	public static final int STATE_C = 2;
	public static final int STATE_D = 3;
	public static final int STATE_E = 4;

	private static MethodStatsCollector[] m_methodStatsArray;

    /////////////////  private static data ////////////////////////////

    private static DsSipInvitationManager m_invManager;
	private static int m_interactionPeriod = 20; // seconds
	private static int m_getReqDelay = 0; // seconds
	private static int m_finalRespDelay = 0; // seconds

    private static int m_port;
    private static String m_host;
    private static DsSipTransportType m_transportType;

	private static int m_finalRespCode = 200;

	private static ThreadPool m_threadpool;

	private int m_invRxCount = 0;
	private int m_180TxCount = 0;
	private int m_finTxCount = 0;
	private int m_errTxCount = 0;
	private int m_ackRxCount = 0;
	private int m_byeRxCount = 0;
	private int m_bye200TxCount = 0;

	private int m_httpGetTxCount = 0;
	private int m_httpPostTxCount = 0;
	private int m_httpGet200RxCount = 0;
	private int m_httpPost200RxCount = 0;
	private int m_httpGetErrRxCount = 0;
	private int m_httpPostErrRxCount = 0;

	private static MsSimulator m_self;

    /////////////////  private data ////////////////////////////

	private MsSimulator() {
		m_methodStatsArray = new MethodStatsCollector[5];

		for(int i = 0; i < 5; ++i) {
			m_methodStatsArray[i] = new MethodStatsCollector();
		}
	}

	public static MsSimulator getInstance() {
		if(m_self == null) {
			m_self = new MsSimulator();
		}

		return m_self;
	}

	private static void clear() {
		char esc = 27;
		String clear = esc + "[2J";
		System.out.println(clear);
	}

	private void dumpStats() {
		clear();
		System.out.println("--------- MsSim Statistics ------------");
		System.out.println(" SIP INVITE   -------> " + m_invRxCount);
		dumpEntries(STATE_A);
		System.out.println(" SIP 180      <------- " + m_180TxCount);
		dumpEntries(STATE_B);
		if(m_getReqDelay > 0) {
			System.out.println("\n pause    <" + m_getReqDelay + " seconds>\n");
		} else {
			System.out.println("");
		}
		System.out.println(" HTTP GET     <------- " + m_httpGetTxCount);
		System.out.println(" HTTP 200     -------> " + m_httpGet200RxCount);
		System.out.println(" HTTP ERR     -------> " + m_httpGetErrRxCount);
		dumpEntries(STATE_C);
		if(m_finalRespDelay > 0) {
			System.out.println("\n pause    <" + m_finalRespDelay + " seconds>\n");
		} else {
			System.out.println("");
		}
		if(m_finalRespCode < 300) {
			System.out.println(" SIP " + m_finalRespCode + "      <------- " + m_finTxCount);
			System.out.println(" SIP Err      <------- " + m_errTxCount);
			System.out.println(" SIP ACK(2xx) -------> " + m_ackRxCount);
		} else {
			System.out.println(" SIP " + m_finalRespCode + "      <------- " + m_finTxCount);
		}

		dumpEntries(STATE_D);
		System.out.println("\n pause    <" + m_interactionPeriod + " seconds>\n");
		System.out.println(" HTTP POST    <------- " + m_httpPostTxCount);
		System.out.println(" HTTP 200     -------> " + m_httpPost200RxCount);
		System.out.println(" HTTP ERR     -------> " + m_httpPostErrRxCount);
		dumpEntries(STATE_E);
		System.out.println("");
		System.out.println(" SIP BYE      -------> " + m_byeRxCount);
		System.out.println(" SIP 200      <------- " + m_bye200TxCount);
		System.out.println("---- Press ^C to terminate program ----");
	}

	private void dumpEntries(int state) {
		try {
			Iterator iter = m_methodStatsArray[state].getEntries().iterator();

			while(iter.hasNext()) {
				Map.Entry entry = (Map.Entry)iter.next();
				CountInteger ci = (CountInteger)entry.getValue();
				System.out.println(" " + entry.getKey() + "     -------> " + ci.get());
				System.out.println(" SIP 200      <------- " + ci.get());
			}
		} catch(ConcurrentModificationException exp) {
			logger.error("Getting entries for state = " + state, exp);
		}

	}

	public synchronized void incrementMethodStats(int state, DsByteString method) {
		m_methodStatsArray[state].increment(method);
	}

	public synchronized void incrementInviteRxCount() {
		m_invRxCount++;
	}

	public synchronized void increment180TxCount() {
		m_180TxCount++;
	}

	public synchronized void incrementInvFinTxCount() {
		m_finTxCount++;
	}

	public synchronized void incrementErrTxCount() {
		m_errTxCount++;
	}

	public synchronized void incrementAckRxCount() {
		m_ackRxCount++;
	}

	public synchronized void incrementByeRxCount() {
		m_byeRxCount++;
	}

	public synchronized void incrementBye200TxCount() {
		m_bye200TxCount++;
	}

	public synchronized void incrementHttpGetTxCount() {
		m_httpGetTxCount++;
	}

	public synchronized void incrementHttpPostTxCount() {
		m_httpPostTxCount++;
	}

	public synchronized void incrementHttpGet200RxCount() {
		m_httpGet200RxCount++;
	}

	public synchronized void incrementHttpPost200RxCount() {
		m_httpPost200RxCount++;
	}

	public synchronized void incrementHttpGetErrRxCount() {
		m_httpGetErrRxCount++;
	}

	public synchronized void incrementHttpPostErrRxCount() {
		m_httpPostErrRxCount++;
	}

    /**
     * Demonstrates the use of the Dialog Layer API.
     *
     * @param args -p <PORT> -h <IP-ADDR> -t <TRANSPORT>
     *
     * @throws Exception for clarity just throw Exception
     */
    public final static void main(String args[]) throws Exception {
		if(args.length > 0) {
			if(args[0].equalsIgnoreCase("-help")) {
				System.out.println("Options:");
				System.out.println("    -h <LISTEN-IP-ADDR>               [default = localhost]");
				System.out.println("    -p <LISTEN-PORT>                  [default = 5060]");
				System.out.println("    -t <TRANSPORT>                    [default = UDP]");
				System.out.println("    -i <IVR-INTERACTION-PERIOD>       [default = 20]");
				System.out.println("    -g <GET-REQ-SEND-DELAY>           [default = 0]");
				System.out.println("    -f <INVITE-FINAL-RESP-SEND-DELAY> [default = 0]");
				System.out.println("    -c <INVITE-FINAL-RESP-CODE>       [default = 200]");
				System.out.println("    -help <This help menu>");

				return;
			}
		}
        // read configuration data
        config(args);

		try {
			m_threadpool = new ThreadPool(THREADPOOL_SIZE, true, MsSimulator.getInstance());
			m_threadpool.start();
		} catch(Exception exp) {
			logger.error("Creating threadpool", exp);
		}   

		MsSipDialog.initialize();

        // create a transport layer
        DsSipTransportLayer tl = new DsSipTransportLayer();

        // create a transaction manager
        DsSipTransactionManager tm = new DsSipTransactionManager(tl, null);

        // create a handler for new invitations
        DsSipNewInvitationInterface nii = new MsNewInvitationListener();

        // create an invitation manager
        m_invManager = DsSipInvitationManager.getInstance(tm, nii);

        // listen for SIP messages
        tl.listenPort(m_port, m_transportType.getAsInt(), InetAddress.getByName(m_host));

        // start the command line
        getInstance().startCommandLine();
    }

    /**
     * Obtain configuration data from the command line. This method demonstrates
     * the use of DsSipDialogDefaults.
     */
    static void config(String args[]) throws DsSipParserException, DsSipParserListenerException {
        System.out.println("*************************************************");
        System.out.println("************ Media Server Simulator *************");
        System.out.println("*************************************************");
        System.out.flush();

		m_transportType = DsSipTransportType.T_UDP;
		m_port = DsSipURL.DEFAULT_PORT;

		try {
        	m_host = InetAddress.getLocalHost().getHostAddress();
		} catch(UnknownHostException exp) {
			exp.printStackTrace();
			System.exit(1);
		}

		for(int idx = 0; (idx + 1) < args.length; idx += 2) {
			String opt = args[idx];
			String val = args[idx + 1];

			if(opt.equals(OPT_PORT)) {
        		try {
            		m_port = Integer.parseInt(val);
        		} catch( NumberFormatException e ) {
					e.printStackTrace();
        		}
			} else if(opt.equals(OPT_ADDR)) {
            	m_host = val;
			} else if(opt.equals(OPT_TRANSPORT)) {
				if(val.equalsIgnoreCase("udp")) {
					m_transportType = DsSipTransportType.T_UDP;
				} else if(val.equalsIgnoreCase("tcp")) {
					m_transportType = DsSipTransportType.T_TCP;
				} else {
					System.out.println("Error: Invalid transport '" + val + "'. Ignoring it...");
				}
			} else if(opt.equals(OPT_GET_REQ_DELAY)) {
        		try {
            		m_getReqDelay = Integer.parseInt(val);
        		} catch( NumberFormatException e ) {
					e.printStackTrace();
        		}
			} else if(opt.equals(OPT_FINAL_RESP_DELAY)) {
        		try {
            		m_finalRespDelay = Integer.parseInt(val);
        		} catch( NumberFormatException e ) {
					e.printStackTrace();
        		}
			} else if(opt.equals(OPT_INTERACTION_PERIOD)) {
        		try {
            		m_interactionPeriod = Integer.parseInt(val);
        		} catch( NumberFormatException e ) {
					e.printStackTrace();
        		}
			} else if(opt.equals(OPT_FINAL_RESP_CODE)) {
        		try {
            		m_finalRespCode = Integer.parseInt(val);
        		} catch( NumberFormatException e ) {
					e.printStackTrace();
        		}

				if( (m_finalRespCode < 200) || (m_finalRespCode >= 700) ) {
					System.out.println("Error: Invalid value for '" + opt + "'. Ignoring it...");
					m_finalRespCode = 200;
				}
			} else {
				System.out.println("Error: Invalid option '" + opt + "'. Ignoring it...");
			}
		}

        // Use DsSipDialogDefaults to configure default Contact header.
        //    Use of defaults is optional.  If header field values are not
        //    provided, defaults will be used.

        DsSipContactHeader contact = new DsSipContactHeader(("\"MS Simulator\" <sip:ivr@" +
                m_host + ":" + m_port + ";transport=" + m_transportType + ">").getBytes());
        DsSipDialogDefaults.setContact(contact);
    }

    /**
     * Start the command line.
     */
    private void startCommandLine() {
		try {
			while(true) {
				Thread.sleep(1000);
				dumpStats();
			}
		} catch(Throwable thr) {
			thr.printStackTrace();
		}
    }

	public static int getInteractionPeriod() {
		return m_interactionPeriod;
	}

	public static int getReqDelay() {
		return m_getReqDelay;
	}

	public static int getFinalRespDelay() {
		return m_finalRespDelay;
	}

	public static int getFinalRespCode() {
		return m_finalRespCode;
	}

	public static ThreadPool getThreadPool() {
		return m_threadpool;
	}

	public void execute(Object data) {
		((Work)data).execute();
	}

	private class MethodStatsCollector {
		Map m_countMap = new HashMap(17);

		public void increment(DsByteString method) {
			CountInteger ci = (CountInteger)m_countMap.get(method);	
			if(ci == null) {
				m_countMap.put(method, ci);
			}
			ci.increment();
			return;
		}

		public Set getEntries() {
			return m_countMap.entrySet();
		}
	}

	private class CountInteger {
		private int m_count = 0;

		public void increment() {
			++m_count;
		}

		public int get() {
			return m_count;
		}

		public String toString() {
			return "" + m_count;
		}
	}
}
