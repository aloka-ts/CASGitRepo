package com.baypackets.ase.util;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.channels.FileChannel;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import com.baypackets.ase.common.Registry;
import com.baypackets.bayprocessor.slee.common.ConfigRepository;

public class AseRexecRollover {
	
	private File file;
	private String filePath;
	private int count = 0;
	private Logger logger = Logger.getLogger(AseRexecRollover.class);
	private static AseRexecRollover instance;
	private static String OS = System.getProperty("os.name").toLowerCase();
	private static ConfigRepository m_configRepository  = (ConfigRepository)Registry.lookup(com.baypackets.ase.util.Constants.NAME_CONFIG_REPOSITORY);
	//file-size in bytes 25MB,26214400
	//(set by ase.properties file)
	private long fileSizeLimit;
	private boolean repeatLog;
	private int maxNoOfFile;
	private String logDir;
	private boolean rexecLoggingEnabled;
	
	private AseRexecRollover(){
		
	}
	
	public static AseRexecRollover getAseRexecRollover(){
		if(instance==null){
			instance = new AseRexecRollover();
			instance.logDir = (String)m_configRepository.getValue(Constants.OID_LOGS_DIR); 
			instance.fileSizeLimit = Long.parseLong(m_configRepository.getValue(Constants.REXEC_MAX_FILE_SIZE));
			instance.maxNoOfFile = Integer.parseInt(m_configRepository.getValue(Constants.REXEC_MAX_NO_OF_FILE));
			instance.repeatLog = Boolean.parseBoolean(m_configRepository.getValue(Constants.REXEC_REPEAT_LOG));
			instance.rexecLoggingEnabled = Boolean.parseBoolean(m_configRepository.getValue(Constants.REXEC_LOGGING_ENABLED)); 
		}
		return instance;
	}
	

	private void setFile() throws FileNotFoundException{
		//The check is introduced to enable the logging of this file based on the configuration
		//This check is introduced because of a deadlock situation which is caused when SAS
		//log rollover and rexec rollover happened at the same time. SAS log rollover process
		//prints some thing on the System.out print stream, which first take the lock on 
		//logger and then try to acquire the lock on rexec print stream, while rexec rollover
		//first acquire the lock on rexec print stream and then on logger. Due to this functionality
		//deadlock situation has occurred.
		if (rexecLoggingEnabled){
			if(logger.isDebugEnabled()){
				logger.debug("log-dir:"+this.logDir);
				logger.debug("File Size Limit:"+this.fileSizeLimit);
				logger.debug("max no of rexec file:"+this.maxNoOfFile);
				logger.debug("repeat rexec log:"+this.repeatLog);
			}
		}
		if(OS.indexOf("win")>=0){
			DateFormat dateFormat = new SimpleDateFormat("MMM_dd_HH_mm_ss");
			Calendar cal = Calendar.getInstance();
		    String today=dateFormat.format(cal.getTime());
			this.filePath = this.logDir+"/Ase_aconyx_"+today+".rexec_out";	
		}else{
			if(StringUtils.isNotBlank(System.getProperty(Constants.REXEC_LOG_FILE_NAME))) {
				this.filePath= this.logDir+"/"+System.getProperty(Constants.REXEC_LOG_FILE_NAME);	
			}else {
				if (logger.isDebugEnabled()) {
					logger.debug("rexec file path doesnot exists for creating new rexec file");
				}
				DateFormat dateFormat = new SimpleDateFormat("MMM_dd_HH_mm_ss");
				Calendar cal = Calendar.getInstance();
			    String today=dateFormat.format(cal.getTime());
				this.filePath = this.logDir+"/Ase_aconyx_"+today+".rexec_out";	
			}
			
		}
		if (rexecLoggingEnabled) {
			if (logger.isDebugEnabled()) {
				logger.debug("rexec log file path:" + this.filePath);
			}
		}
	    file = new File(this.filePath);
	    if (rexecLoggingEnabled) {
	    	logger.error("Now SAS has takeover on rexec_out log file("+this.filePath+").");
	    }
    }
	
	public String getFilePath() throws FileNotFoundException{
		if(filePath==null){
			this.setFile();
		}
		return this.filePath;
	}
	
	public long getFileSizeLimit(){
		return this.fileSizeLimit;
	}
	
	public void setRepeatLog(boolean b){
		this.repeatLog = b;
	}
	
	public void setMaxNoOfRexecFile(int i){
		 this.maxNoOfFile = i;
	}
		
	private void copyFile() throws IOException{
		File in= file;
		String fileOut = this.filePath+"."+count;
		if(count==0){
			fileOut= this.filePath+"_Init";
		}
		File out= new File(fileOut);
		FileChannel inChannel = new FileInputStream(in).getChannel();
		FileChannel outChannel = new FileOutputStream(out).getChannel();
		try {
			inChannel.transferTo(0, inChannel.size(), outChannel);
			if (rexecLoggingEnabled) {
				if (logger.isDebugEnabled()) {
					logger.debug("rexec log file rollover in " + fileOut);
				}
			}
		} catch (IOException e) {
			if (rexecLoggingEnabled) {
				logger.error("error in copy rexec file.",e);
			}
		} finally {
			if (inChannel != null)
				inChannel.close();
			if (outChannel != null)
				outChannel.close();
		}
		count = count+1;
		if(count>this.maxNoOfFile && repeatLog){
			count=1;
		}
	}
	
	public void rolloverFile() {
		try {
			System.out.flush();
			System.out.close();
			System.err.flush();
			System.err.close();
			this.copyFile();
			AseRexecPrintStream aps = new AseRexecPrintStream(this.filePath);
			System.setOut(aps);
			System.setErr(aps);
			return;
		} catch (IOException e) {
			if (rexecLoggingEnabled) {
				logger.error("error in rollover rexec file.", e);
			}
		}
	}
	
}
