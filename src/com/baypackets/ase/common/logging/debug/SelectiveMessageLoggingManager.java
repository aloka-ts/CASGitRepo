package com.baypackets.ase.common.logging.debug;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import com.baypackets.ase.common.Registry;
import com.baypackets.ase.util.AseStrings;
import com.baypackets.ase.util.AseTimerService;
import com.baypackets.ase.util.Constants;
import com.baypackets.bayprocessor.slee.common.ConfigRepository;



public class SelectiveMessageLoggingManager {

	Timer timer = AseTimerService.instance().getTimer();
	private LoggingTimerTask timerTask = new LoggingTimerTask();
	private static SelectiveMessageLoggingManager  _instance = new SelectiveMessageLoggingManager();
	private SipDebugHandler handler = new SipDebugHandler();
	private  boolean isLoggingEnabled = false;
	private int timePeriod = 5; // value in minutes


	public static SelectiveMessageLoggingManager getInstance(){
		return _instance;
	}

	public void initialize(){

		ConfigRepository repository = (ConfigRepository)Registry.lookup(Constants.NAME_CONFIG_REPOSITORY);
		String strValue = null;
		strValue = repository.getValue(Constants.PROP_SELCTIVE_MSG_LOGGING).trim();

		if(strValue!=null){
			if((AseStrings.TRUE_CAPS).equalsIgnoreCase(strValue)){
				isLoggingEnabled = true;
			}
		}

		if(!isLoggingEnabled){
			return;
		}

		SipDebugCriteria.getInstance().setLoggingStatus(isLoggingEnabled);

		strValue = repository.getValue(Constants.PROP_SELCTIVE_MSG_LOGGING_TIME).trim();

		if(strValue!=null){
			timePeriod = Integer.parseInt(strValue);
			if(timePeriod <= 0){
				throw new IllegalArgumentException("Value of the logging time cannot be zero or negative");
			}
		}

		strValue = repository.getValue(Constants.PROP_SELECTIVE_LOGGING_LOG_FILE).trim();
		if(strValue!=null){
			try {
				SelectiveMessageLogger.getInstance().setLogFileLocation(strValue);
			} catch (IOException e) {
			}
		}

		timer.scheduleAtFixedRate(this.timerTask, 0 , timePeriod*60*1000);

	}


	class LoggingTimerTask extends TimerTask{

		@Override
		public void run(){
			if(isLoggingEnabled && handler.isFileModified()){
				try {
					handler.parse();
				} catch (Exception e) {

				}
			}
		}
	}
}