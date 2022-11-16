package com.baypackets.ase.latency;


import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;

import com.baypackets.ase.common.AseBackgroundProcessor;
import com.baypackets.ase.common.BackgroundProcessListener;
import com.baypackets.ase.common.Registry;
import com.baypackets.ase.latency.AseLatencyData.MethodSpecificLatencyData;
import com.baypackets.ase.spi.util.CommandFailedException;
import com.baypackets.ase.spi.util.CommandHandler;
import com.baypackets.ase.util.Constants;
import com.baypackets.ase.util.TelnetServer;
import com.baypackets.ase.util.TimedRollingFileAppender;
import com.baypackets.bayprocessor.slee.common.ConfigRepository;


public class AseLatencyLogger implements BackgroundProcessListener,CommandHandler{


	private static Logger _logger = Logger.getLogger(AseLatencyLogger.class);
	private static AseLatencyLogger _self;

	private static Logger _msgLogger;
	private boolean _isInitialized = false;

	private static final Object _syncObj = new Object();

	private static final String CLI_COMMAND = "latency-level";
	private static int LatencyLoggingLevel=0;
	private long loggingTime = 100;


	public static  ArrayList<String> MessagesToBeCaptured = new ArrayList<String>();

	/**
	 * Default constructor.
	 */
	private AseLatencyLogger() {        
		if (_logger.isDebugEnabled()) {
			_logger.debug("AseLatencyLogger(): Preparing the Latency Logger...");
		}            

		try {
			_msgLogger = Logger.getLogger(Constants.NAME_SIP_LATENCY_LOGGER );
			_msgLogger.removeAllAppenders();
			TimedRollingFileAppender appender = new TimedRollingFileAppender();
			appender.setName(Constants.NAME_SIP_LATENCY_FILE_APPENDER);
			appender.setThreshold(Level.OFF);
			appender.setMaxFileSize("10MB");
			appender.setLayout(new PatternLayout("%d [%t] %m%n"));
			appender.setMaxBackupIndex(1000);
			_msgLogger.addAppender(appender);
			_msgLogger.setAdditivity(false);
		} catch (Exception e) {
			String msg = "Error occured while preparing the Latency Logger: " + e.toString();
			_logger.error(msg, e);
		}
	}

	public static AseLatencyLogger getInstance() {
		if (_self == null) {
			synchronized(_syncObj) {
				if (_self == null) {
					_self = new AseLatencyLogger();
				}
			}
		}

		if (_logger.isDebugEnabled()) {
			_logger.debug("Returning " + _self);
		}

		return _self;
	}


	public void initialize() {
		if (_logger.isDebugEnabled()) {
			_logger.debug("Entering initialize()");
		}
		try {
			if (_isInitialized) {
				_logger.error("Latency Logger is already initialized... returning");
				return;
			}

			TelnetServer telnetServer = (TelnetServer)Registry.lookup(Constants.NAME_TELNET_SERVER);
			telnetServer.registerHandler(CLI_COMMAND, this, false);


			if (!this.loadConfig())  {
				_logger.error("Could not load config file... not initializing Latency Logger");
				return;
			}



			ConfigRepository repository = (ConfigRepository)Registry.lookup(Constants.NAME_CONFIG_REPOSITORY);
			String fileLocation = repository.getValue(Constants.PROP_LATENCY_LOG_FILE);
			if (fileLocation != null) {
				this.setLogFileLocation(fileLocation);
			}


			AseBackgroundProcessor processor = (AseBackgroundProcessor)Registry.lookup(Constants.BKG_PROCESSOR);

			processor.registerBackgroundListener(this, loggingTime);

			_isInitialized = true; // set this true here after registering with background processor


		} catch(Throwable thr) {
			_logger.error("initialize(): Caught throwable", thr);
		}
		if (_logger.isDebugEnabled()) {
			_logger.debug("Exiting initialize()");
		}
	}

	public void registerLatencyDataProvider(LatencyDataProvider ldp){
		synchronized (latencyDataProviders) {
			if( ! latencyDataProviders.contains(ldp))
				latencyDataProviders.add(ldp);
		}
	}

	private final ArrayList<LatencyDataProvider> latencyDataProviders = new ArrayList<LatencyDataProvider>(); 

	public static interface LatencyDataProvider{
		public AseLatencyData getLatencyData();
		public AseLatencyData getOutLatencyData();
	}


	/**
	 * This method is a unit test and shows how to use this class
	 * @param a
	 */
	public static void main(String[] a) {

		try {

			_logger.addAppender(new ConsoleAppender());
			_logger.setLevel(Level.ALL);
			if (_logger.isDebugEnabled()) {
				_logger.log(Level.DEBUG, "logger done");
			}
			final AseLatencyLogger aLL = getInstance();
			aLL._msgLogger.addAppender(new ConsoleAppender());

			aLL._msgLogger.log(Level.OFF,"***************************************************msg logger");

			Thread thread=new Thread() {
				public void run() {
					if (_logger.isDebugEnabled()) {
						_logger.debug("Start first thread ");
					}
					while(true) {//this loop simulates multiple worker threads calling logAseLatencyData
						int count=1;
						try {
							Thread.sleep(2000);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}

						AseLatencyData ld= AseLatencyData.createSample();

						AseLatencyData ld1= AseLatencyData.createSample();

						ld.add(ld1);// this is similar to adding data in threadlocal by a single worker thread

						aLL.putLatencyDataInQueue(ld);
						if (_logger.isDebugEnabled()) {
							_logger.debug("first thread logged data"+count++);
						}
					}
				}
			};
			thread.start();

			Thread thread1=new Thread() {
				public void run() {
					if (_logger.isDebugEnabled()) {
						_logger.debug("Start second thread ");
					}
					while(true) {
						try {
							Thread.sleep(6000);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}


						aLL.process(0);
						if (_logger.isDebugEnabled()) {
							_logger.debug("second thread processed data");
						}
					}
				}
			};
			thread1.start();

		}catch(Exception e) {
			System.out.println("Exception main"+e);
			e.printStackTrace();
		}
		finally {
			System.out.println("Done");
		}
	}

	public void setLatencyLoggingLevel(int inParam) {
		if (_logger.isDebugEnabled()) {
			_logger.debug("setting latency logging level = " +inParam);
		}
		if (inParam >=0 && inParam <3)
			LatencyLoggingLevel=inParam;
	}

	public void setLatencyLoggingTime(long inParam) {
		if (_logger.isDebugEnabled()) {
			_logger.debug("setting latency logging time = " +inParam);
		}
		if (inParam >0)
			loggingTime=inParam;
	}


	private boolean loadConfig() {
		if(_logger.isDebugEnabled())
			_logger.debug("Inside loadConfig()");

		try{

			String strVal;

			ConfigRepository repository = (ConfigRepository)Registry.lookup(Constants.NAME_CONFIG_REPOSITORY);
			strVal = repository.getValue(Constants.OID_LATENCY_LOGGING_LEVEL);
			if (strVal != null) {
				strVal = strVal.trim();
				setLatencyLoggingLevel(Integer.parseInt(strVal));
			}

			strVal = repository.getValue(Constants.OID_LATENCY_LOGGING_TIME);
			if (strVal != null) {
				setLatencyLoggingTime(Long.parseLong(strVal.trim()));
			}

			strVal = repository.getValue(Constants.PROP_LATENCY_MESSAGES);
			if (strVal != null) {
				strVal = strVal.trim();
				StringTokenizer st = new StringTokenizer(strVal, ",");
				while (st.hasMoreTokens()) 
					MessagesToBeCaptured.add(st.nextToken().trim());
			}

			cumulativeAseLatencyData = new AseLatencyData(); 
			cumulativeAseOutLatencyData = new AseLatencyData(); 

		}

		catch(Throwable thr) {
			_logger.error("loadConfig(): Caught error", thr);
			return false;
		}

		return true;
	}

	public int getLatencyLoggingLevel() {
		return LatencyLoggingLevel;
	}


	public void setLogFileLocation(String path) throws IOException {
		try {
			TimedRollingFileAppender appender = 
				(TimedRollingFileAppender)_msgLogger.getAppender(Constants.NAME_SIP_LATENCY_FILE_APPENDER);
			appender.setFile(path);
		} catch (Exception e2) {
			_logger.error("Error while setting file path"+e2.toString(), e2);
		}
	}

	@Override
	public void process(long currentTime) {
		if(LatencyLoggingLevel==0)
			return;

		if (_logger.isDebugEnabled()) {
			_logger.debug("AseLatencyLogger(): Enter process");
		}

		for(LatencyDataProvider ldp: latencyDataProviders){
			putLatencyDataInQueue( ldp.getLatencyData() );
		}

		for(LatencyDataProvider ldp: latencyDataProviders){
			putLatencyOutDataInQueue( ldp.getOutLatencyData() );
		}

		List<AseLatencyData> ls =null;

		List<AseLatencyData> los =null;

		synchronized (dataMutex) {

			ls = latencyQueue;
			latencyQueue = new ArrayList<AseLatencyData>();

			los = latencyOutQueue;
			latencyOutQueue = new ArrayList<AseLatencyData>();

		}//end synch dataMutex

		final AseLatencyData currPeriodData = new AseLatencyData();

		for(AseLatencyData ld: ls) {

			currPeriodData.add(ld);
		}//end for ld:ls


		final AseLatencyData currOutPeriodData = new AseLatencyData();

		for(AseLatencyData ld: los) {

			currOutPeriodData.add(ld);

		}//end of ld:los


		boolean allZero=true;
		for(MethodSpecificLatencyData msd:currPeriodData.methodSpecificData){
			if(msd.counter!=0){
				allZero=false;
				break;
			}
		}
		if(allZero)
			return;



		String latencyPrintString = "Current Period Data [in milliseconds]:\n"+currPeriodData.printLatencyData();
		_msgLogger.log(Level.OFF, latencyPrintString);

		if (_logger.isDebugEnabled()) {
			_logger.log(Level.DEBUG, latencyPrintString);
		}
		synchronized (cumulativeAseLatencyData) {
			cumulativeAseLatencyData.add(currPeriodData);	
			dumpCumulativeData();
		}

		if(LatencyLoggingLevel == 2){


			boolean allOutZero=true;
			for(MethodSpecificLatencyData msd:currOutPeriodData.methodSpecificData){
				if(msd.counter!=0){
					allOutZero=false;
					break;
				}
			}
			if(allOutZero)
				return;


			latencyPrintString = "Current Outgoing Period Data [in milliseconds]:\n"+currOutPeriodData.printOutLatencyData();

			_msgLogger.log(Level.OFF, latencyPrintString);

			if (_logger.isDebugEnabled()) {
				_logger.log(Level.DEBUG, latencyPrintString);
			}


			synchronized (cumulativeAseOutLatencyData) {
				cumulativeAseOutLatencyData.add(currOutPeriodData);	
				dumpCumulativeOutData();
			}
		}

		if (_logger.isDebugEnabled()) {
			_logger.debug("AseLatencyLogger(): End process");
		}
	}

	private final Object cumulativeDataMutex = new Object();

	public void dumpCumulativeData(){

		synchronized (cumulativeDataMutex) {

			String dump = "Cumulative Current Latency Data [in milliseconds]:\n"+cumulativeAseLatencyData.printLatencyData();

			_msgLogger.log(Level.OFF, dump);

			if (_logger.isDebugEnabled()) {
				_logger.log(Level.DEBUG, dump);
			}
		}
	}

	public void dumpCumulativeOutData(){

		synchronized (cumulativeDataMutex) {

			String dump = "Cumulative Outgoing Latency Data [in milliseconds]:\n"+cumulativeAseOutLatencyData.printOutLatencyData();

			_msgLogger.log(Level.OFF, dump);

			if (_logger.isDebugEnabled()) {
				_logger.log(Level.DEBUG, dump);
			}


		}
	}


	public void putLatencyDataInQueue (AseLatencyData ld) {
		if (_logger.isDebugEnabled()) {
			_logger.debug("AseLatencyLogger(): Enter putLatencyDataInQueue");
		}

		synchronized (dataMutex) {

			latencyQueue.add(ld);
		}

		if (_logger.isDebugEnabled()) {
			_logger.debug("AseLatencyLogger(): Exit putLatencyDataInQueue");
		}
	}

	public void putLatencyOutDataInQueue (AseLatencyData ld) {
		if (_logger.isDebugEnabled()) {
			_logger.debug("AseLatencyLogger(): Enter putLatencyOutDataInQueue");
		}

		synchronized (dataMutex) {

			latencyOutQueue.add(ld);
		}

		if (_logger.isDebugEnabled()) {
			_logger.debug("AseLatencyLogger(): Exit putLatencyOutDataInQueue");
		}
	}

	private final Object dataMutex = new Object();


	private List<AseLatencyData> latencyQueue = new ArrayList<AseLatencyData>();

	private List<AseLatencyData> latencyOutQueue = new ArrayList<AseLatencyData>();

	private AseLatencyData cumulativeAseLatencyData;// = new AseLatencyData();

	private AseLatencyData cumulativeAseOutLatencyData;// = new AseLatencyData()



	/*
	 * CommandHandler Execution
	 * 
	 */
	public String execute(String command, String[] args, InputStream in,
			OutputStream out) throws CommandFailedException {

		if(args.length==0) 
			return "Latency Logging Level : " + LatencyLoggingLevel;

		int arg=-1;

		if(!(args[0].equals("dump"))){
			try {
				arg = Integer.parseInt(args[0]);

				if(arg >= 0 && arg < 3){
					setLatencyLoggingLevel(arg);	
				}
				else
					return "Latency-Level should be either 0, 1 or 2";

			} catch(NumberFormatException nfe) {
				return "Invalid argument: [Not A Number] " + args[0];
			}
		}
		
		else if (args[0].equals("dump")) {
			arg =0;
			dumpCumulativeData();
			if(LatencyLoggingLevel==2)
				dumpCumulativeOutData();
		}

		if (arg == -1) {
			return "Unnecessary argument: " + args[1];
		}

		return "Command Executed Successfully";

	}


	@Override
	public String getUsage(String command) {
		StringBuffer sb = new StringBuffer("Usage: " + CLI_COMMAND );
		sb.append("Use Level 0,1,2 to change the Latency Logging Level \n ");
		sb.append("Use 'dump' option to restore the final cumulative data in the sipLatency file");
		sb.append("After giving the command latency-level type the level 0,1,2 or 'dump' for dumping");
		return sb.toString();
	}


}
