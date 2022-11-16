package com.agnity.utility.cdrsftp.dbpull;

import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.agnity.utility.cdrsftp.dbpull.dao.CDRPullDao;
import com.agnity.utility.cdrsftp.dbpull.exceptions.CdrSftpFailedException;
import com.agnity.utility.cdrsftp.dbpull.exceptions.CdrWriteFailedException;
import com.agnity.utility.cdrsftp.dbpull.exceptions.DaoInitializationFailedException;
import com.agnity.utility.cdrsftp.dbpull.exceptions.DirectorySetFailedException;
import com.agnity.utility.cdrsftp.dbpull.exceptions.InvalidCdrLocationException;
import com.agnity.utility.cdrsftp.dbpull.exceptions.SftpNotConnectedException;
import com.agnity.utility.cdrsftp.dbpull.utils.CDRPullConfig;
import com.agnity.utility.cdrsftp.dbpull.utils.Constants;
import com.jcraft.jsch.JSchException;


public class CDRDbPull {

	private static Logger logger; 

	private CDRPullConfig cdrPullFromDbConfig;
	private CDRSftpConnection cdrSftpConnection;
	private CDRFileWriter fileWriter;
	private CDRPullDao dao;
	private long waitPeriod;
	
	private Map<Integer, Boolean> alarmStatusMap = new HashMap<Integer, Boolean>();

	static{
		PropertyConfigurator.configure("log4j.properties");
		logger = Logger.getLogger(CDRDbPull.class);
	}

	/**
	 * Main methods starts the flow
	 * steps:
	 * Read Configuration
	 * initialize location counters
	 * initialize archiveDirs 
	 * @param args
	 */
	public static void main(String[] args) {
		if(logger.isInfoEnabled())
			logger.info("Start CDR DB push main()");
		CDRDbPull dbPull=new CDRDbPull();

		//read configurations
		try{
			dbPull.readConfiguration();
		}catch(DaoInitializationFailedException e){
			if(logger.isDebugEnabled())
				logger.debug("Exception reading config file. Exit",e);
			dbPull.raiseAlarm(Constants.DB_NOT_ACCESIBLE, Constants.DB_NOT_ACCESIBLE_MESSAGE+" reading configuration "+e.getMessage());
			try {
				Thread.sleep(60000);
			} catch (InterruptedException e1) {
				logger.error("Error waiting before exit "+e1.getMessage());
			}
			System.exit(1);
		}catch(Exception e){
			if(logger.isDebugEnabled())
				logger.debug("Exception reading config file. Exit",e);
			dbPull.raiseAlarm(Constants.CONFIG_NOT_FOUND, Constants.CONFIG_NOT_FOUND_MESSAGE+" "+e.getMessage());
			try {
				Thread.sleep(60000);
			} catch (InterruptedException e1) {
				logger.error("Error waiting before exit "+e1.getMessage());
			}
			System.exit(1);
		}
		
		//force raise erro alrm to clear pending alarms
		//dbPull.raiseAlarm(Constants.CONFIG_NOT_FOUND, Constants.CONFIG_NOT_FOUND_MESSAGE);
		//dbPull.raiseAlarm(Constants.DB_NOT_ACCESIBLE, Constants.DB_NOT_ACCESIBLE_MESSAGE);
		//clear alarms i any
//		dbPull.raiseAlarm(Constants.CONFIG_NOW_ACCESIBLE, Constants.CONFIG_NOW_ACCESIBLE_MESSAGE);
//		dbPull.raiseAlarm(Constants.DB_IS_ACCESIBLE, Constants.DB_IS_ACCESIBLE_MESSAGE);
		
		//Raise an information alarm, which will also clear the already raised alarms if any
		//UAT-1144
		dbPull.raiseInformationalAlarm(Constants.CONFIG_NOW_ACCESIBLE, Constants.CONFIG_NOW_ACCESIBLE_MESSAGE);
		dbPull.raiseInformationalAlarm(Constants.DB_IS_ACCESIBLE, Constants.DB_IS_ACCESIBLE_MESSAGE);
		
		//initializes and start flow
		dbPull.triggerFlow();



		if(logger.isInfoEnabled())
			logger.info("End CDR DB pull main()");
	}


	private void triggerFlow() {
		if(logger.isDebugEnabled())
			logger.debug("Enter triggerFlow");

		waitPeriod=cdrPullFromDbConfig.getCdrPullWaitInterval()*1000;
		boolean exit =false;
		//trigger flow main loop
		while(true){
			exit = false;
			try{
				//initializes writer
				initializeFileWriter();
				//initailize DBConnection
				initializeDao();
				//initialize sftpConnection
				initializeSftpConnection();

				raiseAlarm(Constants.SFTP_CONNECTION_UP, Constants.SFTP_CONNECTION_UP_MESSAGE);
				raiseAlarm(Constants.DB_IS_ACCESIBLE, Constants.DB_IS_ACCESIBLE_MESSAGE);
				raiseAlarm(Constants.CONFIG_NOW_ACCESIBLE, Constants.CONFIG_NOW_ACCESIBLE_MESSAGE);
				
				//flow pull write and sftp
				startCdrPullFlow();


			} catch (JSchException e) {
				logger.error("JSchException in creating SFTP connection",e);
				raiseAlarm(Constants.SFTP_CONNECTION_DOWN, Constants.SFTP_CONNECTION_DOWN_MESSAGE+" "+cdrPullFromDbConfig.getRemoteIp()
								+" "+e.getMessage());
			} catch (SftpNotConnectedException e) {
				logger.error("SftpNotConnectedException in creating SFTP connection",e);
				raiseAlarm(Constants.SFTP_CONNECTION_DOWN, Constants.SFTP_CONNECTION_DOWN_MESSAGE+" "+cdrPullFromDbConfig.getRemoteIp()
								+" "+e.getMessage());
			} catch (DirectorySetFailedException e) {
				logger.error("DirectorySetFailedException in setting dirs on sftp connection",e);
				raiseAlarm(Constants.SFTP_CONNECTION_DOWN, Constants.SFTP_CONNECTION_DOWN_MESSAGE+" "+cdrPullFromDbConfig.getRemoteSftpDir()
								+" "+e.getMessage());
			} catch (DaoInitializationFailedException e) {
				logger.error("DaoInitializationFailedException in creating SFTP connectioninitializing DAO",e);
				raiseAlarm(Constants.DB_NOT_ACCESIBLE, Constants.DB_NOT_ACCESIBLE_MESSAGE+" "+e.getMessage());
			} catch (InvalidCdrLocationException e) {
				logger.error("InvalidCdrLocationException in setting cdr location, Correct the configuration and restart",e);
				raiseAlarm(Constants.CONFIG_NOT_FOUND, Constants.CONFIG_NOT_FOUND_MESSAGE+" "+e.getMessage());
				exit =true;
			} catch (CdrSftpFailedException e) {
				logger.error("CdrSftpFailedException on sftp of file",e);
				raiseAlarm(Constants.SFTP_CONNECTION_DOWN, Constants.SFTP_CONNECTION_DOWN_MESSAGE+" "+e.getMessage());
			} catch (Exception e) {
				logger.error("Unknown Exception in Utility",e);
				exit= true;
			}finally{
				if(cdrSftpConnection != null)
					cdrSftpConnection.destroy();
				
				if(dao!=null){
					try {
						dao.destroy();
					} catch (Exception e) {
						logger.warn("Exceptionin Dao destroy", e);
					}
				}
				
				if(exit)
					System.exit(1);

				//wait before retry
				try {
					Thread.sleep(waitPeriod);
				} catch (InterruptedException e) {
					//do nothing continue without sleep
				}
			}//@end finally
		}//@end while
	}//@end trigger flow


	private void initializeFileWriter() throws InvalidCdrLocationException {
		if(logger.isDebugEnabled())
			logger.debug("Enter initializeSftpConnection");
		//create sftp connection and set directories
		fileWriter = new CDRFileWriter();
		fileWriter.initialize(cdrPullFromDbConfig);
		//channel created setting directories
		if(logger.isDebugEnabled())
			logger.debug("Leave initializeSftpConnection() ");
	}


	private void initializeDao() throws DaoInitializationFailedException {
		if(logger.isDebugEnabled())
			logger.debug("Enter initializeDao");
		if(dao== null){
			throw new DaoInitializationFailedException("DAO not initialized Found null");
		}
		try {
			dao.initialize();
		} catch (Exception e) {
			throw new DaoInitializationFailedException("Dao initialize failed exception",e);
		}
		if(logger.isDebugEnabled())
			logger.debug("Leave initializeDao");
	}


	private void initializeSftpConnection() 
	throws JSchException, SftpNotConnectedException, DirectorySetFailedException {
		if(logger.isDebugEnabled())
			logger.debug("Enter initializeSftpConnection");
		//create sftp connection and set directories
		cdrSftpConnection = new CDRSftpConnection();
		cdrSftpConnection.initialize(cdrPullFromDbConfig);
		//channel created setting directories
		if(logger.isDebugEnabled())
			logger.debug("Leave initializeSftpConnection() ");

	}


	/**
	 * method reads configuration
	 * @throws DaoInitializationFailedException,Exception 
	 */
	private void readConfiguration() throws DaoInitializationFailedException,Exception {
		if(logger.isInfoEnabled())
			logger.info("Inside readConfiguration()");

		String[] paths={"/CdrPullConfig.xml"};
		ApplicationContext appContext=new ClassPathXmlApplicationContext(paths);

		cdrPullFromDbConfig =(CDRPullConfig)appContext.getBean("cdrPullFromDbConfig");
		try{
			dao =(CDRPullDao)appContext.getBean("cdrPullDaoImpl");
		}catch(Exception e){
			throw new DaoInitializationFailedException(e.getMessage(), e);
		}
		if(logger.isInfoEnabled())
			logger.info("Leave  readConfiguration()");
	}

	/**
	 * 
	 * @throws CdrSftpFailedException 
	 * @throws Exception 
	 */
	private void startCdrPullFlow() throws CdrSftpFailedException, SQLException, Exception {
		if(logger.isDebugEnabled())
			logger.debug("Inside startCdrPullFlow()");
		
		List<String> cdrs = null;
		boolean sleep= false;
		boolean execute= false;
		int adjustedMaxCdrs = cdrPullFromDbConfig.getMaxCdrs();
		if(adjustedMaxCdrs > cdrPullFromDbConfig.getCdrFileSizeAdjApplyCrit() && 
				cdrPullFromDbConfig.getCdrFileSizeAdjFactor() > 0 && cdrPullFromDbConfig.getCdrFileSizeAdjFactor() < 100){
			adjustedMaxCdrs = ( (adjustedMaxCdrs*100) / (100-cdrPullFromDbConfig.getCdrFileSizeAdjFactor()) );
		}
		Map<String,String> updateFields = new HashMap<String, String>();
		updateFields.put(cdrPullFromDbConfig.getStatusColumn(), Integer.toString(1));
		updateFields.put(cdrPullFromDbConfig.getSentFileColumn(), null);
		
		while(true){
			//check FIP here as FT can happen when script is running
			execute = checkNodeActiveStatus();
			
			int numberOfCdrsToFetch = 0;
			boolean isCloseOnSize = false;
			boolean continueProcess = false;
			
			if(!execute){
				if(logger.isDebugEnabled())
					logger.debug("Node not active so exit");
				//breaking of inner loop sending control to outer loop
				break;
			}
			sleep= false;
			try{
				//locking the table
				dao.lockTable(cdrPullFromDbConfig.getLockTable());
				raiseAlarm(Constants.DB_IS_ACCESIBLE, Constants.DB_IS_ACCESIBLE_MESSAGE);
				//fetching pending CDR count
				int availableCdrCnt = dao.getPendingCdrCnt();
				//fetch available attributes
				Map<String, String> cdrAttributes = dao.getAttributes();
				long currentTime = System.currentTimeMillis();
				//extract last sent time form attrinutes
				long lastSentTime = getLastSentTimeFromAttribute(cdrAttributes);
				int nextFileNameCtr = getNextFileCtr(cdrAttributes, cdrPullFromDbConfig.getCdrFileCntrIdentifier());
				String nextFileName = getNextFileName(nextFileNameCtr, cdrPullFromDbConfig);
				
				if(logger.isDebugEnabled()){
					logger.debug("got NextFileName::"+nextFileName);
					logger.debug("got Last sent time::"+lastSentTime);
					logger.debug("got NextFilecntr::"+nextFileNameCtr);
					logger.debug("got current time::"+currentTime);
				}
				if(cdrPullFromDbConfig.getMaxCdrs()>0 && availableCdrCnt >= adjustedMaxCdrs ){
					if(logger.isDebugEnabled())
						logger.debug("Max Size limit reached");
					numberOfCdrsToFetch = cdrPullFromDbConfig.getMaxCdrs();
					isCloseOnSize = true;
					continueProcess = true;
				}else if(cdrPullFromDbConfig.getRolloverTime()>0 &&
						(currentTime - lastSentTime) >= (cdrPullFromDbConfig.getRolloverTime()*1000) ){
					if(availableCdrCnt > 0){
						if(logger.isDebugEnabled())
							logger.debug("Timeout and CDRS available");
						numberOfCdrsToFetch = availableCdrCnt;
						if(numberOfCdrsToFetch > cdrPullFromDbConfig.getCdrFileSizeAdjApplyCrit() 
								&& cdrPullFromDbConfig.getCdrFileSizeAdjFactor() > 0 && cdrPullFromDbConfig.getCdrFileSizeAdjFactor() < 100){
							double factor =100-cdrPullFromDbConfig.getCdrFileSizeAdjFactor();							
							numberOfCdrsToFetch= (int) Math.ceil((numberOfCdrsToFetch*factor/100));
						}
						continueProcess = true;
					}else{
						if(logger.isDebugEnabled())
							logger.debug("Timeout and NO CDRs available, update TS and wait");
						dao.updateAttributes(Constants.TS_ATTR_NAME, Long.toString(currentTime), true);
						continueProcess = false;
					}
				}else{
					if(logger.isDebugEnabled())
						logger.debug("Just wait expired; NO timeout,NO size, rery after wait");
					dao.releaseConnection();
					continueProcess = false;
				}
				
				
				if(!continueProcess){
					if(logger.isDebugEnabled())
						logger.debug("Got next action as retry after sleep");
					//sleep and try again
					try {
						Thread.sleep(getMin(waitPeriod, ( (cdrPullFromDbConfig.getRolloverTime()*1000) -(currentTime - lastSentTime) ) ) );
					} catch (InterruptedException e) {
						if(logger.isDebugEnabled())
							logger.debug("sleep Failed",e);
					}
					continue;

				}//@End if files not found

				if(logger.isDebugEnabled()){
					logger.debug("Writing CDRs into file::"+nextFileName);
					logger.debug("Number of CDrs::"+numberOfCdrsToFetch);
				}
				updateFields.put(cdrPullFromDbConfig.getSentFileColumn(), nextFileName);
				cdrs = dao.fetchAndUpdateCdrsAndNextTs(numberOfCdrsToFetch, updateFields, false);
				if(cdrs == null  || cdrs.isEmpty()){
					if(logger.isDebugEnabled())
						logger.debug("cdrs list empty release conn and try agaian");
					dao.releaseConnection();
					continue;
				}//@End if cdrs not found

				if(logger.isDebugEnabled())
					logger.debug("CDR are not empty writing cdrs::"+cdrs);

				boolean status = fileWriter.writeCdrToFile(nextFileName, cdrs);
				if(status ==  false){
					//skip and continue
					if(logger.isDebugEnabled())
						logger.debug("cdrs write failed to file::"+nextFileName+"  will try again for this file");
					dao.releaseConnection();
					continue;
				}//@End status is false
				String destFile= getDestinationFileName(nextFileName);
				cdrSftpConnection.sftpFile(nextFileName, destFile);
				++nextFileNameCtr;
				//update dao
				if(!isCloseOnSize){
					dao.updateAttributes(Constants.TS_ATTR_NAME, Long.toString(currentTime), false);
				}
				
				dao.updateAttributes(cdrPullFromDbConfig.getCdrFileCntrIdentifier(), Integer.toString(nextFileNameCtr), false);
				//commiting changes to DB
				dao.commitConnAndCloseStatements();

			}catch(SQLException e){
				logger.error("SQLException in dao::"+e.getMessage(),e);
				sleep =true;
				//raise db failure alarms connection and retry
				raiseAlarm(Constants.DB_NOT_ACCESIBLE, Constants.DB_NOT_ACCESIBLE_MESSAGE+" "+e.getMessage());
			} catch (CdrWriteFailedException e) {
				logger.error("CdrWriteFailedException writing cdrs in file::"+e.getMessage(),e);
				sleep=true;
				//no neede to re throw
			} catch (CdrSftpFailedException e) {
				if(logger.isDebugEnabled())
					logger.debug("CdrSftpFailedException sftp cdrs::"+e.getMessage(),e);
				//close connection and retry
				throw e;
			} catch (Exception e) {
				if(logger.isDebugEnabled())
					logger.debug("Unknown Exception in DB operation::"+e.getMessage(),e);
				//unknown error exit
				throw e;
			}finally{
				
				if(logger.isDebugEnabled())
					logger.debug("For saftey always releasing lock in finally");
				
				try{
					dao.releaseConnection();
				}finally{
					if(logger.isDebugEnabled())
						logger.debug("Lock released");
				}
				
				if(sleep){
					try {
						Thread.sleep(waitPeriod);
					} catch (Exception e) {
						//
					}
				}
			}//@End try catch finally

		}//@End while
	}//@End startCdrPushFlow

	private long getMin(long waitPeriod2, long diff) {
		if(logger.isDebugEnabled()){
			logger.debug("waitPeriod2::"+waitPeriod2);
			logger.debug("diff::"+diff);
		}
		if(waitPeriod2<diff  || diff <=0)
			return waitPeriod2;
		else 
			return diff;
	}


	private String getNextFileName(int nextFileNameCtr,
			CDRPullConfig cdrPullFromDbConfig) {
		StringBuilder fileName = new StringBuilder();
		
		fileName.append(cdrPullFromDbConfig.getCdrFilePrefix());
		fileName.append("_");
		fileName.append(nextFileNameCtr);
		fileName.append(Constants.TMP_FILE_EXTENSION);
		
		return fileName.toString();
	}


	private int getNextFileCtr(Map<String, String> cdrAttributes,
			String cdrFileCntrIdentifier) throws SQLException, Exception {
		String cntr = cdrAttributes.get(cdrFileCntrIdentifier);
		
		if(cntr != null && !cntr.isEmpty() ){
			return Integer.parseInt(cntr);
		}else{
			dao.insertAttributes(cdrFileCntrIdentifier, Integer.toString(1), false);
			return 1;
		}
		
	}


	private long getLastSentTimeFromAttribute(Map<String, String> cdrAttributes) throws SQLException, Exception {
		String sentTime = cdrAttributes.get(Constants.TS_ATTR_NAME);
		
		if(sentTime != null && !sentTime.isEmpty() ){
			return Long.parseLong(sentTime);
		}else{
			dao.insertAttributes(Constants.TS_ATTR_NAME, Integer.toString(0), false);
			return 0;
		}
	}


	private boolean checkNodeActiveStatus() {

		String fip= cdrPullFromDbConfig.getFip();
		Object[] params={fip};
		String command=MessageFormat.format(Constants.COMMAND_ACTIVE_NODE_INSTANCE, params);
		Process process = null;
		boolean execute =false;
		try {
			process = new ProcessBuilder("bash","-c",command).start();
			InputStream is = process.getInputStream();
			process.waitFor();
			if(is.available()>0){
				if(logger.isDebugEnabled())
					logger.debug("fip attached;Active Node, execute");
				execute =true;
			}
			
		} catch (IOException e) {
			logger.error("IOEXCEPTIOn in checking AN status",e);
			
		}catch (InterruptedException e) {
			logger.error("InterruptedException in checking AN status",e);
			
		}finally{
			if(process!=null)
				process.destroy();
		}

		return execute;
	}


	private String getDestinationFileName(String fileName) {
		StringBuilder destFile= new StringBuilder();
		if(fileName.endsWith(Constants.TMP_FILE_EXTENSION)){
			int index = fileName.length()-Constants.TMP_FILE_EXTENSION.length();
			destFile.append(fileName.substring(0,index));
		}
		destFile.append(".");
		destFile.append(cdrPullFromDbConfig.getCdrFileExtension());
		return destFile.toString();
	}
	
	
	private void raiseInformationalAlarm(int code, String message){
	
		logger.error("raiseInformationalAlarm() ->id:::["+ code +"]  message::["+ message +"]");
		
		Process process=null;
		Object[] params={Integer.toString(code),message};
		String command=MessageFormat.format(Constants.RAISE_ALARM_COMMAND, params);
		
		if(logger.isDebugEnabled())
			logger.debug("raiseInformationalAlarm() ->Alarm command created::["+command+"]");
		
		try {
			process = new ProcessBuilder("bash","-c",command).start();
			process.waitFor();
		} catch (IOException e) {
			logger.error("IOException in raiseInformationalAlarm",e);
		}catch (InterruptedException e) {
			logger.error("InterruptedException in raiseInformationalAlarm",e);
		}finally{
			if(process!=null)
				process.destroy();
		}
		
		
		if(logger.isDebugEnabled())
			logger.debug("Leave raiseInformationalAlarm()");
	}
	
	private void raiseAlarm(int code, String message){
		if(logger.isDebugEnabled())
			logger.debug("Enter raiseAlarm() ->id:::["+ code +"]  message::["+ message +"]");
	
		boolean isAlarmReqd = checkAlarm(code);
		
		if(!isAlarmReqd){
			if(logger.isDebugEnabled())
				logger.debug("Enter raiseAlarm() ->id:::["+ code +"]  message::["+ message +"] alarm is not reuired");
			return;
		}
		
		logger.error("raiseAlarm() ->id:::["+ code +"]  message::["+ message +"]");
		
		Process process=null;
		Object[] params={Integer.toString(code),message};
		String command=MessageFormat.format(Constants.RAISE_ALARM_COMMAND, params);
		
		if(logger.isDebugEnabled())
			logger.debug("raiseAlarm() ->Alarm command created::["+command+"]");
		
		try {
			process = new ProcessBuilder("bash","-c",command).start();
			process.waitFor();
		} catch (IOException e) {
			logger.error("IOException in raiseAlarm",e);
		}catch (InterruptedException e) {
			logger.error("InterruptedException in raiseAlarm",e);
		}finally{
			if(process!=null)
				process.destroy();
		}
		
		
		if(logger.isDebugEnabled())
			logger.debug("Leave raiseAlarm()");
	}

	private boolean checkAlarm(int code) {
		if(logger.isDebugEnabled())
			logger.debug("Enter checkAlarm() ->id:::["+ code +"]");
		
		int checkCode= code;
		boolean retValue = false;
		
		if(code == Constants.DB_IS_ACCESIBLE){
			checkCode = Constants.DB_NOT_ACCESIBLE;
		}else if(code == Constants.CONFIG_NOW_ACCESIBLE){
			checkCode = Constants.CONFIG_NOT_FOUND;
		}else if(code == Constants.SFTP_CONNECTION_UP){
			checkCode = Constants.SFTP_CONNECTION_DOWN;
		}
		
		Boolean status = alarmStatusMap.get(checkCode);
		
		if(status == null){
			status = false;
		}

		
		if( status){
			switch(code){
				case Constants.SFTP_CONNECTION_UP :
				case Constants.DB_IS_ACCESIBLE :
				case Constants.CONFIG_NOW_ACCESIBLE :{
					retValue = true;
					alarmStatusMap.put(checkCode, false);
					break;
				}
				case Constants.SFTP_CONNECTION_DOWN :
				case Constants.DB_NOT_ACCESIBLE :
				case Constants.CONFIG_NOT_FOUND :{
					retValue = false;
					break;
				}
			}//end switch
		}else{
			switch(code){
				case Constants.SFTP_CONNECTION_UP :
				case Constants.DB_IS_ACCESIBLE :
				case Constants.CONFIG_NOW_ACCESIBLE :{
					retValue = false;
					break;
				}
				case Constants.SFTP_CONNECTION_DOWN :
				case Constants.DB_NOT_ACCESIBLE :
				case Constants.CONFIG_NOT_FOUND :{
					retValue = true;
					alarmStatusMap.put(checkCode, true);
					break;
				}
			}//end switch
		
		}
			
			
		if(logger.isDebugEnabled()){
			logger.debug("leave checkAlarm() ->id:::["+ code +"] with status:::["+retValue+"]");
		}
		
		return retValue;
	}

}
