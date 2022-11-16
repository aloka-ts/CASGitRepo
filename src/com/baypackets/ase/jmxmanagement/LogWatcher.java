package com.baypackets.ase.jmxmanagement;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.beanutils.PropertyUtils;

import com.baypackets.ase.util.Constants;

import org.apache.log4j.Logger;

import com.baypackets.ase.util.Constants;
import com.baypackets.bayprocessor.slee.common.BaseContext;
import com.baypackets.bayprocessor.slee.common.ConfigRepository;

public class LogWatcher implements LogWatcherMBean {

	private static Logger logger = Logger.getLogger(LogWatcher.class);

	private ConfigRepository ase = null;

	//private String filePath = null;

	private static int readerNum = 0;

	LogWatcherCleaner cleaner = null;
	
	private static int MAX_BUFFER_SIZE = 5242880;
	
	
	private static int  AVG_CHAR_IN_A_LINE =100;

	public LogWatcher() {
		if(logger.isInfoEnabled() ){
		logger.info("LogWatcher constructor ");
		}

	}

	public void intialize() {
		if(logger.isInfoEnabled() ){
		logger.info("Initializing LogWatcher ");
		}
		//ase = new Properties();
		
		ase=BaseContext.getConfigRepository();
		
		//filePath = Constants.ASE_HOME.concat("/conf").concat("/ase.properties");
		
		containerLog = getCurrentSASLogFileLocation(this.CAS_LOG);
		containerSipLog = getCurrentSASLogFileLocation(this.SIP_DEBUG_LOG);

		HashMap<Integer, LineNumberReader> map1 = new HashMap<Integer, LineNumberReader>();
		readers = Collections.synchronizedMap(map1);

		HashMap<Integer, Date> map2 = new HashMap<Integer, Date>();
		readersTS = Collections.synchronizedMap(map2);
		
		cleaner = new LogWatcherCleaner(this,15);
		if(logger.isInfoEnabled() ){
		logger.info("Starting LogWatcherCleaner Thread!!!!!!!!!!!!!!!! ");
		}
		cleaner.start();
	}

	public synchronized String readContainerLogs(String readerN) {
		String logLine = null;
		try {
			
			int i = Integer.parseInt(readerN);
			LineNumberReader reader = readers.get(i);
						
			if (reader != null) {
				
				int ptr =reader.getLineNumber();
				
				int fileLength = new Long(containerLog.length()).intValue();
				int bufferlenth= fileLength-ptr;
				
				if(bufferlenth < 0){
					bufferlenth = fileLength;
				}
				
				if (bufferlenth >= this.MAX_BUFFER_SIZE){
					bufferlenth = this.MAX_BUFFER_SIZE;
				}
				char[] cbuf = new char[bufferlenth];				
				
				int charRead = reader.read(cbuf, 0, bufferlenth);
				
				
				
				// set the next pointer to the file 
				if(charRead != -1 && containerLog.exists()) {
				    logLine = String.copyValueOf(cbuf);
				    reader.setLineNumber(ptr +charRead);	
				}else {
					logLine = null ;
					reader.setLineNumber(ptr);
				}
				
//					logger.info("LogWatcher SAS.Log  BufferLenth " +bufferlenth + 
//							" Pointer is at ..." +ptr +" " +
//							" File Length is ...."+fileLength +
//							" Character read are...."+charRead + 
//							" Line returned is ..." +logLine);
				
				Date date = Calendar.getInstance().getTime();
				
				synchronized (readersTS){
					  readersTS.put(i, date);
					}
			}
			 return logLine ;
		} catch (FileNotFoundException e) {
			
			logger.error("SAS.Log  FileNotFound....", e);
			return logLine  = null ;
		} catch (IOException i) {
			logger.error("SAS.log IOException....", i);
			return logLine = null;
		}

		
	}

	public synchronized long getLogFileSize(String fileName) {

		long size = 0;
		if (fileName.equals(this.CAS_LOG)) {

			if (containerLog != null && containerLog.exists()) {

				if (containerLog.length() == 0) {
					size = -1;
				} else {
					size = containerLog.length();
				}

			}
		} else if (fileName.equals(this.SIP_DEBUG_LOG)) {

			if (containerSipLog != null && containerSipLog.exists()) {
				if (containerSipLog.length() == 0) {
					size = -1;
				} else {
					size = containerSipLog.length();
				}
			}
		}
//		logger
//				.info("LogWatcher:getLogFileSize(fileName) Log File Size Returned is......"
//						+  size + " For File " + fileName +"Int value is" +new Long(size).intValue());
		return size;
	}
	
	
	
	public synchronized boolean isFileExist(String fileName){
		boolean isFileExist=false;
		        if(fileName.equals(this.SIP_DEBUG_LOG)){
		        	isFileExist=containerSipLog.exists();
		        }else if (fileName.equals(this.CAS_LOG)) {
		        	isFileExist=containerLog.exists();
		        }
		return isFileExist;
	}

	public synchronized String readContainerSipLogs(String readerN) {
		String logLine = null;
		try {
			
			int i = Integer.parseInt(readerN);
			LineNumberReader reader = readers.get(i);
			
			if (reader != null) {
				
				int ptr =reader.getLineNumber();
				
				int fileLength = new Long(containerSipLog.length()).intValue();
				int bufferlenth= fileLength-ptr;
				
				if(bufferlenth < 0){
					bufferlenth = fileLength;
				}
				
				if (bufferlenth >= this.MAX_BUFFER_SIZE){
					bufferlenth = this.MAX_BUFFER_SIZE;
				}
				char[] cbuf = new char[bufferlenth];
								
				
				int charRead = reader.read(cbuf, 0, bufferlenth);
				
				// set the next pointer to the file 
				if(charRead != -1 && containerSipLog.exists()) {
				    logLine = new String(cbuf);
				    reader.setLineNumber(ptr +charRead);	
				}else {
					logLine = null ;
					reader.setLineNumber(ptr);
				}
				
				Date date = Calendar.getInstance().getTime();
				
				synchronized (readersTS){
				  readersTS.put(i, date);
				}
				
			}
			 return logLine ;
		} catch (FileNotFoundException e) {
			
			logger.error("SAS.Log  FileNotFound....", e);
			return logLine  = null ;
		} catch (IOException i) {
			logger.error("SAS.log IOException....", i);
			return logLine = null;
		}

	}

	public synchronized void changeLogLevel(String level) {
		if(logger.isInfoEnabled() ){
		logger.info("LogWatcher :setLogLevel()....................." + level);
		logger.info("LogWatcher :ase.properties is located ....................." );
		}
//		FileInputStream fin = null;
//		FileOutputStream out = null;
		try {
			
//			PropertyUpdater pc= new PropertyUpdater();
//			fin = new FileInputStream(filePath);
//			pc.load(fin);
			ase.setValue("1.1.1", level);
			
//			out = new FileOutputStream(filePath);
//			pc.store(out, "updated Log Level in property file");
			

		} catch (Exception e) {
			logger.error("ase.properties FileNotFound....", e);
		} 
//		catch (IOException i) {
//			logger.error("ase.properties IOException....", i);
//		} finally {
//			try {
//				if(fin!=null)
//				fin.close();
//				
//				if(out!=null)
//				out.close();
//			} catch (IOException i) {
//				logger.error("ase.properties close IOException....", i);
//			}
//		}

	}

	public synchronized void changeSipLogging(String level) {
		// TODO Auto-generated method stub
		if(logger.isInfoEnabled() ){
		logger.info("LogWatcher :setSipLogging()....................." + level);
		logger.info("LogWatcher :ase.properties is located ....................." );
		}
//		FileInputStream fin = null;
//		FileOutputStream out = null;
		try {
			
//			PropertyUpdater pc= new PropertyUpdater();
//			fin = new FileInputStream(filePath);
//			pc.load(fin);
			ase.setValue("30.1.9", level);
			
//			out = new FileOutputStream(filePath);
//			pc.store(out, "updated Sip Logging in property file");
			
		} catch (Exception e) {
			logger.error("ase.properties FileNotFound....", e);
		} 

	}

	private File getCurrentSASLogFileLocation(String fileName) {

		FileInputStream fin = null;
		String dateStamp = null;
		String curentPath = "";
		try {
//			fin = new FileInputStream(filePath);
//			ase.load(fin);
			String saslogLocation = ase.getValue("1.1.2");

			if (ase.getValue("1.1.7") != null) {
				this.CAS_LOG = ase.getValue("1.1.7");

				if (logger.isInfoEnabled()) {
					logger.info("LogWatcher:Platform log file name is "
							+ this.CAS_LOG);
				}

			}
            if(logger.isInfoEnabled() ){
	                         logger.info("LogWatcher:replace ase.home if its there in path  "+saslogLocation);
	                         }
	              
                        if (saslogLocation != null){
                            
                            saslogLocation=saslogLocation.trim();
                            if (saslogLocation.startsWith("$(ase.home)")) {
                                int asehomeindex = saslogLocation.indexOf("$(ase.home)");
                                String saslogLocationSbstr = saslogLocation
                                        .substring(asehomeindex + "$(ase.home)".length());
                                saslogLocation = Constants.ASE_HOME + saslogLocationSbstr;
                            }
                                
                        }
			
			String dt = "";
			String mon = "";
			int month = (Calendar.getInstance().get(Calendar.MONTH) + 1);
			int date = (Calendar.getInstance().get(Calendar.DATE));
			if (Integer.toString(date).length() == 1) {
				dt = "0" + Integer.toString(date);
			} else {
				dt = Integer.toString(date);
			}
			if (Integer.toString(month).length() == 1) {
				mon = "0" + Integer.toString(month);
			} else {
				mon = Integer.toString(month);
			}

			int year = (Calendar.getInstance().get(Calendar.YEAR));
			dateStamp = mon + "_" + dt + "_" + year;
			if (fileName.equals(this.SIP_DEBUG_LOG)) {
				curentPath = saslogLocation + "/" + dateStamp + "/"
						+ this.SIP_DEBUG_LOG;
			} else if (fileName.equals(this.CAS_LOG)) {
				curentPath = saslogLocation + "/" + dateStamp + "/" + CAS_LOG;
			}
			if(logger.isInfoEnabled() ){
			logger.info("LogWatcher:The current file Location for "+fileName +" is " + curentPath);
			}
		} catch (Exception e) {
			logger.error("ase.properties FileNotFound....", e);
		} 
		File file = new File(curentPath);
		return file;
	}

	  // duplicacy of code has been kept in if/else just for clarity
	public synchronized String openReader(String fileName,boolean isSkip ,String noOfLinesToShow) {

		String readerIndex = "";
		int noOfLines =0;
		LineNumberReader reader =null;
		 long logFileLength =0;
		try {
			if(logger.isInfoEnabled() ){
			logger.info("LogWatcher:openReader() for the file........"
					+ fileName);
			}
			
			if (fileName.equals(this.CAS_LOG) && containerLog.exists()) {
				
				reader = new LineNumberReader(new FileReader(
						containerLog));
				
				logFileLength =containerLog.length();
				
				
			} else if (fileName.equals(this.SIP_DEBUG_LOG)
					&& containerSipLog.exists()) {
				
				reader = new LineNumberReader(new FileReader(
						containerSipLog));
				logFileLength =containerSipLog.length();
				
			}
			
			// if skip the old logs then skip them 
			if(isSkip){
				
				int skip=new Long(logFileLength).intValue();
				
				if(!noOfLinesToShow.equals("")){
					noOfLines = Integer.parseInt(noOfLinesToShow);
				}
				
				if(noOfLines > 0 && skip >= AVG_CHAR_IN_A_LINE * noOfLines){
					skip =skip - AVG_CHAR_IN_A_LINE * noOfLines;
				}
				
				reader.skip(skip);
				reader.setLineNumber(skip);
				if(logger.isInfoEnabled() ){
				logger.info("LogWatcher:Skipped Character are."
						+ skip+" Where file length is..."+logFileLength +" Where No of lines to show are .."+ noOfLines +" Reader Index is : "+readerNum);
				}
			}else {
				reader.setLineNumber(0);
			}
			
			//update the maps 
			insertToReaderMap(this.readerNum, reader);
			
			Date date = Calendar.getInstance().getTime();
			insertToReaderTSMap(this.readerNum, date);
			
		
			readerIndex = "" + readerNum;
			this.readerNum++;

		} catch (IOException e) {
			logger.error("Reader close IOException....", e);
		}
		return readerIndex;
	}

	public synchronized void closeReader(String fileName, String readerN) {

		try {
			if(logger.isInfoEnabled() ){
			logger.info("LogWatcher:closeReader() for the file..........."
					+ fileName +"Index of Reader : "+readerN);
			}
			
			int readerNumber = Integer.parseInt(readerN);
			LineNumberReader reader = readers.get(readerNumber);

			if (reader != null) {
				reader.close();
				this.removeFromReaderMap(readerNumber);
				this.removeFromReaderTSMap(readerNumber);
			}
		} catch (IOException e) {
			logger.error("Reader close IOException....", e);
		}

	}

	public Map<Integer, Date> getReaderTSMap() {
		return readersTS;
	}

	public Map<Integer, LineNumberReader> getReaderMap() {
		return readers;
	}

	public synchronized void insertToReaderTSMap(int ReaderNumber, Date date) {
		
		synchronized (readersTS) {
			readersTS.put(ReaderNumber, date);
		}
		
	}

	public synchronized void removeFromReaderTSMap(int ReaderNumber) {
	
		synchronized (readersTS) {
		   readersTS.remove(ReaderNumber);
		}
	}

	public synchronized void insertToReaderMap(int ReaderNumber,
			LineNumberReader reader) {
		synchronized (readers) {
		   readers.put(ReaderNumber, reader);
		}
	}

	public synchronized void removeFromReaderMap(int ReaderNumber) {
		synchronized (readers) {
		  readers.remove(ReaderNumber);
		}
	}
	
	private LineNumberReader sasLogs_reader;

	private LineNumberReader sipLogs_reader;

	private File containerLog;

	private File containerSipLog;

	private final String SIP_DEBUG_LOG = "sipDebug.log";

	private String CAS_LOG = "CAS.log";

	int sleepInterval;

	int waitCycleBeforeAbort;

	private Map<Integer, LineNumberReader> readers = null;

	private Map<Integer, Date> readersTS = null;
	

}
