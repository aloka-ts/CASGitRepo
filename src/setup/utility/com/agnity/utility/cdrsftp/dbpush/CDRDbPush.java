package com.agnity.utility.cdrsftp.dbpush;

import java.util.List;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import com.agnity.utility.cdrsftp.dbpush.exceptions.CounterFileIoFailedException;
import com.agnity.utility.cdrsftp.dbpush.exceptions.InvalidLocalDirException;
import com.agnity.utility.cdrsftp.dbpush.exceptions.LocationNotFoundException;
import com.agnity.utility.cdrsftp.dbpush.utils.CDRPushConfig;
import com.agnity.utility.cdrsftp.dbpush.utils.Constants;

public class CDRDbPush {

	private static Logger			logger;

	private CDRPushConfig			cdrPushToDbConfig;
	private CDRLocation				primaryLoc;
	private CDRLocation				secondaryLoc;
	private long					waitPeriod;
	private Map<Integer, Boolean>	alarmStatusMap	= new HashMap<Integer, Boolean>();

	private SimpleDateFormat df = new SimpleDateFormat("yyyy_MM_dd_hh_mm_ss_sss");  
	
	static {
		PropertyConfigurator.configure("log4j.properties");
		logger = Logger.getLogger(CDRDbPush.class);
	}

	/**
	 * Main methods starts the flow
	 * steps:
	 * Read Configuration
	 * initialize location counters
	 * initialize archiveDirs
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		if (logger.isInfoEnabled())
			logger.info("Start CDR DB push main()");
		CDRDbPush dbPush = new CDRDbPush();

		//read configurations
		try {
			dbPush.readConfiguration();
		} catch (Exception e) {
			if (logger.isDebugEnabled())
				logger.debug("Exception reading config file. Exit", e);
			dbPush.raiseAlarm(Constants.CONFIG_NOT_FOUND, Constants.CONFIG_NOT_FOUND_MESSAGE + " "
							+ e.getMessage());
			try {
				Thread.sleep(60000);
			} catch (InterruptedException e1) {
				logger.error("Erro waiting before exit " + e1.getMessage());
			}
			System.exit(1);
		}

		//initialize counters
		try {
			dbPush.initializeLocations();
		} catch (FileNotFoundException e) {
			if (logger.isDebugEnabled())
				logger.debug("FileNotFoundException at time of initialize. Exit", e);
			dbPush.raiseAlarm(Constants.CONFIG_NOT_FOUND, Constants.CONFIG_NOT_FOUND_MESSAGE + " "
							+ e.getMessage());
			try {
				Thread.sleep(60000);
			} catch (InterruptedException e1) {
				logger.error("Erro waiting before exit " + e1.getMessage());
			}
			System.exit(1);
		} catch (URISyntaxException e) {
			if (logger.isDebugEnabled())
				logger.debug("URISyntaxException at time of initialize. Exit", e);
			dbPush.raiseAlarm(Constants.CONFIG_NOT_FOUND, Constants.CONFIG_NOT_FOUND_MESSAGE + " "
							+ e.getMessage());
			try {
				Thread.sleep(60000);
			} catch (InterruptedException e1) {
				logger.error("Erro waiting before exit " + e1.getMessage());
			}
			System.exit(1);
		} catch (InvalidLocalDirException e) {
			if (logger.isDebugEnabled())
				logger.debug("InvalidLocalDirException at time of initialize. Exit", e);
			dbPush.raiseAlarm(Constants.CONFIG_NOT_FOUND, Constants.CONFIG_NOT_FOUND_MESSAGE + " "
							+ e.getMessage());
			try {
				Thread.sleep(60000);
			} catch (InterruptedException e1) {
				logger.error("Erro waiting before exit " + e1.getMessage());
			}
			System.exit(1);
		} catch (CounterFileIoFailedException e) {
			if (logger.isDebugEnabled())
				logger.debug("CounterFileIoFailedException at time of initialize. Exit", e);
			dbPush.raiseAlarm(Constants.CONFIG_NOT_FOUND, Constants.CONFIG_NOT_FOUND_MESSAGE + " "
							+ e.getMessage());

			try {
				Thread.sleep(60000);
			} catch (InterruptedException e1) {
				logger.error("Erro waiting before exit " + e1.getMessage());
			}
			System.exit(1);
		} catch (LocationNotFoundException e) {
			if (logger.isDebugEnabled())
				logger.debug("LocationNotFoundException at time of initialize. Exit", e);
			dbPush.raiseAlarm(Constants.CONFIG_NOT_FOUND, Constants.CONFIG_NOT_FOUND_MESSAGE + " "
							+ e.getMessage());
			try {
				Thread.sleep(60000);
			} catch (InterruptedException e1) {
				logger.error("Error waiting before exit " + e1.getMessage());
			}
			System.exit(1);
		}

		//error alarm raised to force clearing alarm
		//dbPush.raiseAlarm(Constants.CONFIG_NOT_FOUND, Constants.CONFIG_NOT_FOUND_MESSAGE); // no need to raise this alarm in main
		//rasie clearing alram if configuration initailization is done
		//dbPush.raiseAlarm(Constants.CONFIG_NOW_ACCESIBLE, Constants.CONFIG_NOW_ACCESIBLE_MESSAGE); // this will be raise as informational alarm

		//Raise an information alarm, which will also clear the already raised alarms if any
		//UAT-1144
		dbPush.raiseInformationalAlarm(Constants.CONFIG_NOW_ACCESIBLE, Constants.CONFIG_NOW_ACCESIBLE_MESSAGE);
		dbPush.raiseInformationalAlarm(Constants.DB_IS_ACCESIBLE, Constants.DB_IS_ACCESIBLE_MESSAGE);
		
		//initialize archive DIRS and SQLLDR LOGDIR
		dbPush.initializeArchiveDirs(dbPush.primaryLoc, "primary");
		dbPush.initializeArchiveDirs(dbPush.secondaryLoc, "secondary");

		//start flow
		dbPush.startCdrPushFlow();

		if (logger.isInfoEnabled())
			logger.info("End CDR DB push main()");
	}

	/**
	 * creates and initializes archive dirs for locations.
	 * sets log file path and archive dir name in location
	 * 
	 * @param cdrLocation
	 * @param locationIdentifier
	 *            location typr directory
	 */
	private void initializeArchiveDirs(CDRLocation cdrLocation, String locationIdentifier) {
		if (logger.isDebugEnabled())
			logger.debug("Enter initializeArchiveDirs");
		String archiveDir = cdrPushToDbConfig.getCdrPushArchiveDirName();

		StringBuilder sqlLoadLogDir = new StringBuilder();
		StringBuilder cdrArchiveDir = new StringBuilder();

		sqlLoadLogDir.append(archiveDir);
		cdrArchiveDir.append(archiveDir);

		if (archiveDir == null || !(archiveDir.endsWith("/"))) {
			sqlLoadLogDir.append("/");
			cdrArchiveDir.append("/");

		}

		sqlLoadLogDir.append(locationIdentifier);
		cdrArchiveDir.append(locationIdentifier);

		sqlLoadLogDir.append("/");
		cdrArchiveDir.append("/");

		sqlLoadLogDir.append("sqlLoadLogs");
		cdrArchiveDir.append("loadedCdrArchive");

		File primarySqlLoadfile = new File(sqlLoadLogDir.toString());
		if (!(primarySqlLoadfile.exists())) {
			//create dirs
			primarySqlLoadfile.mkdirs();
		}

		File primaryCdrArchivefile = new File(cdrArchiveDir.toString());
		if (!(primaryCdrArchivefile.exists())) {
			//create dirs
			primaryCdrArchivefile.mkdirs();
		}

		cdrLocation.setLogFilePath(sqlLoadLogDir.toString());
		cdrLocation.setCdrArchive(cdrArchiveDir.toString());

		if (logger.isDebugEnabled())
			logger.debug("Leave initializeArchiveDirs");
	}

	/**
	 * method reads configuration
	 */
	private void readConfiguration() {
		if (logger.isInfoEnabled())
			logger.info("Inside readConfiguration()");

		String[] paths = { "/CdrPushConfig.xml" };
		ApplicationContext appContext = new ClassPathXmlApplicationContext(paths);
		cdrPushToDbConfig = (CDRPushConfig) appContext.getBean("cdrPushToDbConfig");
		if (logger.isInfoEnabled())
			logger.info("Leave  readConfiguration()");
	}

	/**
	 * @throws FileNotFoundException
	 * @throws URISyntaxException
	 * @throws InvalidLocalDirException
	 * @throws CounterFileIoFailedException
	 * @throws LocationNotFoundException
	 */
	private void initializeLocations() throws FileNotFoundException, URISyntaxException,
					InvalidLocalDirException, CounterFileIoFailedException,
					LocationNotFoundException {
		if (logger.isDebugEnabled())
			logger.debug("Inside initializeLocations()");

		String cdrLoc = null;

		//reading primary location counter
		cdrLoc = cdrPushToDbConfig.getCdrPushPrimaryDirName();
		if (cdrLoc != null) {
			primaryLoc = new CDRLocation(cdrLoc, Constants.FILE_NAME_PRIMARY_LOC_APPENDER);
			primaryLoc.initialize(cdrPushToDbConfig);
		}
		//reading secondary location counter
		cdrLoc = null;
		cdrLoc = cdrPushToDbConfig.getCdrPushSecondaryDirName();
		if (cdrLoc != null) {
			secondaryLoc = new CDRLocation(cdrLoc, Constants.FILE_NAME_SECONDARY_LOC_APPENDER);
			secondaryLoc.initialize(cdrPushToDbConfig);
		}

		if (primaryLoc == null || secondaryLoc == null) {
			logger.error("Either Primary Loc is null or secondar loc is null " + "primaryLoc:["
							+ primaryLoc + "]  " + "secondaryLoc:[" + secondaryLoc + "]");

			throw new LocationNotFoundException(
							"Either Primary Loc is null or secondar loc is null " + "primaryLoc:["
											+ primaryLoc + "]  " + "secondaryLoc:[" + secondaryLoc
											+ "]");
		}

		if (logger.isDebugEnabled())
			logger.debug("Leaving initializeLocations() with " + "primaryLoc:[" + primaryLoc
							+ "]  " + "secondaryLoc:[" + secondaryLoc + "]");
	}

	/**
	 * Flow:
	 * 
	 * @param cdrPushToDSIConfig
	 */
	private void startCdrPushFlow() {
		if (logger.isDebugEnabled())
			logger.debug("Inside startCdrPushFlow()");
		//initialize wait period
		waitPeriod = cdrPushToDbConfig.getCdrPushWaitInterval() * 1000;
		
		while (true) {
			try {
				//delete existing failover tmp files on primary
				if (logger.isDebugEnabled())
					logger.debug("startCdrPushFlow() Deleting failover files@primary");
				deleteTmpFilesForLocation(primaryLoc);

				//delete existing failover tmp files on secondary
				if (logger.isDebugEnabled())
					logger.debug("startCdrPushFlow() Deleting failover files@secondary");
				deleteTmpFilesForLocation(secondaryLoc);

				if (logger.isDebugEnabled())
					logger.debug("startCdrPushFlow() ->process locations..inside a loop");

				processLocations();

			} catch (Exception e) {
				logger.error("Exception in porocessing..restart after wait", e);
				try {
					Thread.sleep(waitPeriod);
				} catch (InterruptedException e1) {
					//do nothing continue without sleep
				}
			}//@End try catch finally
		}//@End while
	}//@End startCdrPushFlow

	private void processLocations() {
		if (logger.isDebugEnabled())
			logger.debug("Enter processLocations()->processing locations");
		boolean statusPrimary = true;
		boolean statusSecondary = true;
		while (true) {
			statusPrimary = dbPushLocation(primaryLoc);
			statusSecondary = dbPushLocation(secondaryLoc);

			if ((!(statusPrimary) && !(statusSecondary))) {
				if (logger.isDebugEnabled())
					logger.debug("Both primary and secondary locations are empty");

				try {
					Thread.sleep(waitPeriod);
				} catch (InterruptedException e) {
					if (logger.isDebugEnabled())
						logger.debug("Exception in sleep", e);
				}
			}
		}

	}

	
	public List<String>  getFiles(String cdrLoc){
		List<String> listOfFiles = new ArrayList<String>();

		final String filePrefix=cdrPushToDbConfig.getCdrPushFilePrefix();
		final String fileExt = cdrPushToDbConfig.getCdrPushFileExtension();

		File dirFile = new File(cdrLoc);

		FilenameFilter fileFilter = new FilenameFilter() {
			public boolean accept(File dir, String name) {
				// for example : MNSAS2_0P19563.ACT
				return name.matches(filePrefix+"_.*\\."+fileExt);

			}
		};

		File listFiles [] = dirFile.listFiles(fileFilter);
		
		if(listFiles.length > 10){
			
			Arrays.sort(listFiles, new Comparator<File>() {

				@Override
				public int compare(File f1, File f2) {
					return Long.valueOf(f1.lastModified()).compareTo(f2.lastModified());
				}
			});
		}

		for(int i = 0 ;i < listFiles.length ; i++){
			listOfFiles.add(listFiles[i].getName());
			// need only 10 files to processed
			if(i == 9){
				break;
			}
		}

		if (logger.isDebugEnabled()){
			logger.debug("Inside getFiles()-> :" + listOfFiles);
		}
		
		return listOfFiles;

	}

	public File prepareListWithDate( String cdrLoc, List<String> rawCntrFileNameList, String dateTimeStamp){
		
		File dateFile = new File(cdrLoc+dateTimeStamp+".DATE");
		logger.debug("Inside dbPush prepareListWithDate. rawCntrFileNameList " + rawCntrFileNameList.toString());
		logger.debug("Inside dbPush prepareListWithDate." + dateFile.getAbsolutePath());
		logger.debug("Inside dbPush prepareListWithDate.getAbsoluteFile. " + dateFile.getAbsoluteFile().toString());
		
		FileWriter fw = null;
		BufferedWriter bw = null;
		boolean file = false;
		try {
			
			file = dateFile.createNewFile();
			fw = new FileWriter(dateFile.getAbsoluteFile());
			bw = new BufferedWriter(fw);
			bw.write(rawCntrFileNameList.toString());
			
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			logger.error("Inside dbPush : Creating DATE file...." + e.getMessage());
			file = false;
		}finally{
			
			if(bw != null){
				try {
					bw.close();
				} catch (IOException e) {
					logger.error("Inside dbPush : BufferedWriter :finally..." + e.getMessage());
					file = false;
				}
			}
			
			if(fw != null ){
				try {
					fw.close();
				} catch (IOException e) {
					logger.error("Inside dbPush : FileWriter :finally..." + e.getMessage());
					file = false;
				}
			}
		}
		
		if(!file){
			dateFile = null;	
		}
		
		return dateFile;
	}

	/**
	 * @param cdrLocation
	 * @return
	 */
	private boolean dbPushLocation(CDRLocation cdrLocation) {
		if (logger.isDebugEnabled())
			logger.debug("Inside dbPushLocation()->processing location::["
							+ cdrLocation.getCdrLocation() + "]");
		
		String cdrLoc = cdrLocation.getCdrLocation();
		
		if (!cdrLoc.endsWith("/")) {
			
			cdrLoc = cdrLoc+"/";
		}
		
		// getting 10 or less files list which need to be processed
		List<String> rawCntrFileNameList =  getFiles(cdrLoc);
		
		logger.debug("Inside dbPushLocation()->  cdrLoc :  " +cdrLoc); 
		
		boolean status = false;
		// if no file to processed, return from here
		if(rawCntrFileNameList.size() == 0){
			status = false;
			if (logger.isDebugEnabled())
				logger.debug("Inside dbPushLocation()->No files availble to push :["
								+ rawCntrFileNameList + " ] return with found::["
								+ status + "]");
			status = false;
			return status;
		}
		
		//  timestamp for date file
		String dateTimeStamp = df.format(new Date());
		logger.debug("Inside dbPushLocation()-> dateTimeStamp :   " + dateTimeStamp );
		File dateFile = prepareListWithDate(cdrLoc,rawCntrFileNameList,dateTimeStamp);
		
		if(dateFile == null){
			logger.error("Inside dbPushLocation()-> error in creating listing file so returning....");
			return status;
		}
		
		//  time stamp will use as identifier for the file
		String rawLoadFileName = cdrLocation.getRawLoadFileName(cdrPushToDbConfig,dateTimeStamp);
		
		if (logger.isDebugEnabled())
		logger.debug("Inside dbPush : rawLoadFileName => : " +rawLoadFileName );
		
		String tmpFileNameForCntr = rawLoadFileName + Constants.TMP_FILE_EXTENSION;
		String logFileNameForCntr = rawLoadFileName + Constants.LOG_FILE_EXTENSION;
		String badFileNameForCntr = rawLoadFileName + Constants.BAD_FILE_EXTENSION;

		List<String> absoluteCounterFileNameList = new ArrayList<String>(rawCntrFileNameList.size());
		
		//		String archiveCdrLoc = cdrLocation.getCdrArchive();
		StringBuilder cntrFileAbsolutePath1 = new StringBuilder();
		
		StringBuilder tmpFileAbsolutePath = new StringBuilder();
		StringBuilder logFileAbsolutePath = new StringBuilder();
		StringBuilder badFileAbsolutePath = new StringBuilder();
		StringBuilder totalCdrsFileAbsolutePath = new StringBuilder();

		logFileAbsolutePath.append(cdrLocation.getLogFilePath());
		logFileAbsolutePath.append("/");
		logFileAbsolutePath.append(logFileNameForCntr);

		badFileAbsolutePath.append(cdrLocation.getLogFilePath());
		badFileAbsolutePath.append("/");
		badFileAbsolutePath.append(badFileNameForCntr);

		cntrFileAbsolutePath1.append(cdrLoc);
		totalCdrsFileAbsolutePath.append(cdrLoc);
		tmpFileAbsolutePath.append(cdrLoc);
		if (!cdrLoc.endsWith("/")) {
			cntrFileAbsolutePath1.append("/");
			tmpFileAbsolutePath.append("/");
			totalCdrsFileAbsolutePath.append("/");
		}
		String absoluteRawFileName=null;
		for(String rawFileName: rawCntrFileNameList){
			absoluteRawFileName = cntrFileAbsolutePath1.toString()+rawFileName;
			absoluteCounterFileNameList.add(absoluteRawFileName);
		}
		tmpFileAbsolutePath.append(tmpFileNameForCntr);
		totalCdrsFileAbsolutePath.append("totalCdrs" + Constants.TMP_FILE_EXTENSION);
		//creating file on CDR dir
		
		if (logger.isDebugEnabled())
			logger.debug("Inside dbPushLocation()->Counter file exists:["
							+ absoluteCounterFileNameList + " ]");
		status = true;
		//creating TmpFile
		boolean loadStatus = false;

		try {
			int totalCDRs = createTmpFile(absoluteCounterFileNameList, tmpFileAbsolutePath.toString(),
							tmpFileNameForCntr, totalCdrsFileAbsolutePath.toString());
			loadStatus = loadCdrFileInDb(tmpFileAbsolutePath.toString(),logFileAbsolutePath.toString(), badFileAbsolutePath.toString(), totalCDRs);
			
			if (loadStatus) {
				raiseAlarm(Constants.DB_IS_ACCESIBLE, Constants.DB_IS_ACCESIBLE_MESSAGE + " "	+ cdrLocation.getCdrLocation());
				if (logger.isDebugEnabled())
					logger.debug("Inside dbPushLocation()->tmpFileAbsolutePath:["
									+ tmpFileAbsolutePath.toString() + " ]");
				archiveFile(tmpFileAbsolutePath.toString(), cdrLocation.getCdrArchive());
				
				if (logger.isDebugEnabled())
					logger.debug("Inside dbPushLocatin absoluteCounterFileNameList : " + absoluteCounterFileNameList);
				//Move files
				moveACTFile(absoluteCounterFileNameList, rawCntrFileNameList, cdrLocation.getCdrArchive(),dateFile.toString());
				
			} else {
				if (logger.isDebugEnabled())
					logger.debug("Inside dbPushLocation()->load failed for files:["
									+ absoluteCounterFileNameList + " ]");
				raiseAlarm(Constants.DB_NOT_ACCESIBLE, Constants.DB_NOT_ACCESIBLE_MESSAGE + " "
								+ cdrLocation.getCdrLocation());
				status = false;
			}
		} catch (InterruptedException e) {
			if (logger.isDebugEnabled())
				logger.debug("dbPushLocation() ->InterruptedException files::["
								+ absoluteCounterFileNameList + " ]", e);
			status = false;
		} catch (IOException e) {
			if (logger.isDebugEnabled())
				logger.debug("dbPushLocation() ->InterruptedException files::["
								+ absoluteCounterFileNameList + " ]", e);
			status = false;
		} 

		if (logger.isDebugEnabled())
			logger.debug("Leave dbPushLocation()->processing location::["
							+ cdrLocation.getCdrLocation() + "] with Status::[" + status + "]");

		return status;
	}

	private void moveACTFile(List<String> absoluteCounterFileNameList,List<String> rawCntrFileNameList, String moveDir, String fulldateFile){
		
		logger.debug("Inside moveACTFile : absoluteCounterFileNameList : " + absoluteCounterFileNameList + "  : moveDir :  "+moveDir );
		logger.debug("Inside moveACTFile : rawCntrFileNameList : " + rawCntrFileNameList + "  : moveDir :  "+moveDir );
		
		if(!moveDir.endsWith("/")){
			moveDir = moveDir+"/";
		}
		
		int index = fulldateFile.lastIndexOf("/");
		String rawDateFile = fulldateFile.substring(index+1);
		
		File originalFile = null;
	    File reNamedFile = null;
	      
	    try {
			for( int i = 0 ; i < absoluteCounterFileNameList.size() ; i++){
				originalFile = new File(absoluteCounterFileNameList.get(i));
				reNamedFile = new File(moveDir+rawCntrFileNameList.get(i)+".PROCESSED");
				
				originalFile.renameTo(reNamedFile);
			}
			
			logger.debug("Inside moveACTFile : fulldateFile : "  +fulldateFile);
			logger.debug("Inside moveACTFile : rawDateFile " + rawDateFile  );
			
			originalFile = new File(fulldateFile);
			reNamedFile = new File(moveDir+rawDateFile+".PROCESSED");
			originalFile.renameTo(reNamedFile);
			
		logger.debug("Inside moveACTFile:  Files moved to .PROCESSED "  );
			
		} catch (Exception e) {
			logger.error("Exception occurec in moveACTFile : " + e.getMessage());
		}

	}
	
	/**
	 * @param fileName
	 * @param archiveDir
	 * @throws IOException
	 * @throws InterruptedException
	 */
	private void archiveFile(String fileName, String archiveDir) throws IOException,
					InterruptedException {
		if (logger.isDebugEnabled())
			logger.debug("Enter ArchiveFile() ->on file::[" + fileName + "]  archive dir::["
							+ archiveDir + "]");

		ProcessBuilder pb = null;
		Process process = null;
		Object[] params = { fileName, archiveDir };
		String command = MessageFormat.format(Constants.FILE_ARCHIVE_COMMAND, params);

		if (logger.isDebugEnabled())
			logger.debug("ArchiveFile() ->Archive command created::[" + command + "]");

		try {
			pb = new ProcessBuilder("bash", "-c", command);
			pb.redirectErrorStream(true);
			process = pb.start();
		    BufferedReader br = new BufferedReader(new InputStreamReader(process.getInputStream()));
		    String s;
		    if (logger.isDebugEnabled())
	    		logger.debug("Flushing the stream");
		    while ((s = br.readLine()) != null) {
		    	if (logger.isDebugEnabled())
		    		logger.debug(s);
			}
			process.waitFor();
		} catch (IOException e) {
			logger.error("IOException in archive of TMP files", e);
			throw e;
		} catch (InterruptedException e) {
			logger.error("InterruptedException in archive of TMP files", e);
			throw e; 
		} finally {
			if (process != null)
				process.destroy();
		}

		if (logger.isDebugEnabled())
			logger.debug("Leave ArchiveFile()");

	}

	/**
	 * @param fileName
	 * @return true if successful
	 * @throws IOException
	 * @throws InterruptedException
	 */
	private boolean loadCdrFileInDb(String fileName, String logFile, String badFile, int totalCDRs)
					throws IOException, InterruptedException {
		if (logger.isDebugEnabled())
			logger.debug("Enter loadCdrFileInDb()->CdrFile::[" + fileName + "]");
		boolean isSuccess = false;

		String user = cdrPushToDbConfig.getDbUser();
		String password = cdrPushToDbConfig.getDbPassword();
		String dbSrvc = cdrPushToDbConfig.getDbSrvc();
		String ctrlFile = cdrPushToDbConfig.getCtrlFileName();
		int rows = cdrPushToDbConfig.getCdrFileSize();

		Object[] params = { user, password, dbSrvc, ctrlFile, logFile, badFile, fileName,
						Integer.toString(rows + 10), Integer.toString(totalCDRs + 10)};
		String command = MessageFormat.format(Constants.TMP_FILE_DB_LOAD_COMMAND, params);

		ProcessBuilder pb = null;
		Process process = null;
		if (logger.isDebugEnabled())
			logger.debug("Inside loadCdrFileInDb()->Loading tmp file:[" + fileName
							+ " ]  create command:[" + command + "]");
		try {
			pb = new ProcessBuilder("bash", "-c", command);
			pb.redirectErrorStream(true);
			process = pb.start();
			if (logger.isDebugEnabled())
				     logger.debug("runLoader process started");
		    BufferedReader br = new BufferedReader(new InputStreamReader(process.getInputStream()));
		    String s;
		    if (logger.isDebugEnabled())
	    		logger.debug("Flushing the stream");
		    while ((s = br.readLine()) != null) {
		    	if (logger.isDebugEnabled())
		    		logger.debug(s);
			}
			process.waitFor();
			//isSuccess = checkError(process.getErrorStream());
			if (logger.isDebugEnabled())
				logger.debug("Leave loadCdrFileInDb()->Loading tmp file:[" + fileName
								+ " ]  with exit value:[" + process.exitValue() + "]");
			
			if (process.exitValue() == 0 || process.exitValue() == 2){
				isSuccess = true;	
			}
		} catch (IOException e) {
			logger.error("IOException in loading of tmp file", e);
			throw e;
		} catch (InterruptedException e) {
			logger.error("InterruptedException in loading of tmp file", e);
			throw e;
		} catch (IllegalThreadStateException e) {
			logger.error("IllegalThreadStateException in execution of sqlloader process", e);
			throw e;
		}finally {
			if (process != null)
				process.destroy();
		}//@End: try catch for loading tmp file

		if (logger.isDebugEnabled())
			logger.debug("Leave loadCdrFileInDb()->Loading tmp file:[" + fileName
							+ " ]  with success Status:[" + isSuccess + "]");

		return isSuccess;
	}

	/**
	 * @param errorStream
	 *            error stream on process to check errors
	 * @return
	 * @throws IOException
	 */
	private boolean checkError(InputStream errorStream) throws IOException {
		boolean isSuccess = true;
		if (errorStream.available() > 0) {
			isSuccess = false;
		}
		return isSuccess;
	}

	/**
	 * @param absoluteCounterFileNameList
	 * @param tmpFile
	 * @throws InterruptedException
	 * @throws IOException
	 */
	private int createTmpFile(List<String> absoluteCounterFileNameList, String absoluteTmpFile, String tmpFileName, String totalCdrsFile)
					throws InterruptedException, IOException {
		if (logger.isDebugEnabled())
			logger.debug("Enter createTmpFile()->CntrFile::[" + absoluteCounterFileNameList + "] Absolute Tmpfile::["
							+ absoluteTmpFile + "]  TmpFileName::[" + tmpFileName + "]");

		String ignoreCdrIdentifier = cdrPushToDbConfig.getCdrPushIgnoreCdrIdentifier();
		
		String OemCdrHeader = cdrPushToDbConfig.getCdrPushOemCdrHeader();
		
		StringBuilder inputFilesString = null;
		for(String absoluteCtrFileName: absoluteCounterFileNameList){
			if (logger.isDebugEnabled())
				logger.debug("createTmpFile()->Adding::[" + absoluteCtrFileName + "]");

			if(inputFilesString ==null){
				if (logger.isDebugEnabled())
					logger.debug("createTmpFile()->First Entry create object");
				inputFilesString= new StringBuilder();
				inputFilesString.append(absoluteCtrFileName);
			}else{
				if (logger.isDebugEnabled())
					logger.debug("createTmpFile()->Subsequent entry add after space");
				inputFilesString.append(" ");
				inputFilesString.append(absoluteCtrFileName);
			}
		}
		
		ignoreCdrIdentifier = "'" + ignoreCdrIdentifier + "|" + Constants.DEFAULT_CDR + "|" + OemCdrHeader + "'";
		Object[] totalCDRParams = { ignoreCdrIdentifier, inputFilesString, totalCdrsFile};
		Object[] params = { ignoreCdrIdentifier, inputFilesString, tmpFileName, absoluteTmpFile };
		String numberofCDRs = MessageFormat.format(Constants.TOTAL_CDRS, totalCDRParams);
		String command = MessageFormat.format(Constants.TMP_FILE_CREATE_COMMAND, params);
		Process noOfCdrsprocess = null;
		ProcessBuilder noOfCdrspb = null;
		Process process = null;
		ProcessBuilder pb = null;

		
		if (logger.isDebugEnabled())
			logger.debug("Inside createTmpFile()->Calculating number of CDRs[" + inputFilesString
							+ " ]  create command:[" + numberofCDRs + "]");
		
		
		int totalCdrs = 0;
		try {
			noOfCdrspb = new ProcessBuilder("bash", "-c", numberofCDRs);
			noOfCdrspb.redirectErrorStream(true);
			noOfCdrsprocess = noOfCdrspb.start();
		    BufferedReader noofCdrbr = new BufferedReader(new InputStreamReader(noOfCdrsprocess.getInputStream()));
		    String numberOfCdrs;
		    if (logger.isDebugEnabled())
	    		logger.debug("Flushing the stream");
		    while ((numberOfCdrs = noofCdrbr.readLine()) != null) {
		    	if (logger.isDebugEnabled())
		    		logger.debug( "Number of CDRs from the stream" + numberOfCdrs);
			}
		    noOfCdrsprocess.waitFor();
		    if (logger.isDebugEnabled())
	    		logger.debug("Reading total number of CDR file " + totalCdrsFile);
		    
		    BufferedReader nuOfCdrs = new BufferedReader(new FileReader(totalCdrsFile));
		    String sCurrentLine;
		    while ((sCurrentLine = nuOfCdrs.readLine()) != null) {
		    	totalCdrs = Integer.parseInt(sCurrentLine.trim());
		    	if (logger.isDebugEnabled())
		    		logger.debug("Total CDRs " + totalCdrs);
			}
		    
		    if (logger.isDebugEnabled())
				logger.debug("Inside createTmpFile()->Creating tmp file:[" + absoluteTmpFile
								+ " ]  create command:[" + command + "]");
			pb = new ProcessBuilder("bash", "-c", command);
			pb.redirectErrorStream(true);
			process = pb.start();
		    BufferedReader br = new BufferedReader(new InputStreamReader(process.getInputStream()));
		    String s;
		    if (logger.isDebugEnabled())
	    		logger.debug("Flushing the stream");
		    while ((s = br.readLine()) != null) {
		    	if (logger.isDebugEnabled())
		    		logger.debug(s);
			}
			process.waitFor();
			
		} catch (IOException e) {
			if (logger.isDebugEnabled())
				logger.error("IOException in creation of tmp file", e);
			throw e;
		} catch (InterruptedException e) {
			if (logger.isDebugEnabled())
				logger.error("InterruptedException in creation of tmp file", e);
			throw e;
		} finally {
			if (process != null)
				process.destroy();
		}//@End: try catch for creAting tmp file

		if (logger.isDebugEnabled())
			logger.debug("Leave createTmpFile()->" + totalCdrs);
		
		return totalCdrs;

	}

	/**
	 * cleans up tmp files from location
	 * 
	 * @param cdrLocation
	 */
	private void deleteTmpFilesForLocation(CDRLocation cdrLocation) {
		if (logger.isDebugEnabled())
			logger.debug("ENter deleteTmpFilesForLocation() ->on loc::["
							+ cdrLocation.getCdrLocation() + "]");
		String path = cdrLocation.getCdrLocation();
		String filePrefix = cdrPushToDbConfig.getCdrPushFilePrefix();
		StringBuilder fileName = new StringBuilder();
		fileName.append(path);
		if (!(path.endsWith("/")))
			fileName.append("/");

		fileName.append(filePrefix);
		fileName.append(cdrLocation.getFileNameAppender());
		fileName.append("*");
		fileName.append(Constants.TMP_FILE_EXTENSION);

		if (fileName.toString().equals("*")) {
			if (logger.isDebugEnabled())
				logger.debug("Tmp filename is * ignore delete step");
		}

		ProcessBuilder pb = null;
		Process process = null;
		Object[] params = { fileName.toString() };
		String command = MessageFormat.format(Constants.TMP_FILES_DELETE_COMMAND, params);

		if (logger.isDebugEnabled())
			logger.debug("deleteTmpFilesForLocation() ->delete command created::[" + command + "]");

		try {
			pb = new ProcessBuilder("bash", "-c", command);
			pb.redirectErrorStream(true);
			process = pb.start();
		    BufferedReader br = new BufferedReader(new InputStreamReader(process.getInputStream()));
		    String s;
		    if (logger.isDebugEnabled())
	    		logger.debug("Flushing the stream");
		    while ((s = br.readLine()) != null) {
		    	if (logger.isDebugEnabled())
		    		logger.debug(s);
			}
			process.waitFor();
		} catch (IOException e) {
			if (logger.isDebugEnabled())
				logger.error("IOException in cleanup of TMP files", e);
		} catch (InterruptedException e) {
			if (logger.isDebugEnabled())
				logger.error("InterruptedException in cleanup of TMP files", e);
		} finally {
			if (process != null)
				process.destroy();
		}

		if (logger.isDebugEnabled())
			logger.debug("Leave deleteTmpFilesForLocation() ->on loc::["
							+ cdrLocation.getCdrLocation() + "]");

	}

	private void raiseAlarm(int code, String message) {
		if (logger.isDebugEnabled())
			logger.debug("Enter raiseAlarm() ->id:::[" + code + "]  message::[" + message + "]");

		boolean isAlarmReqd = checkAlarm(code);

		if (!isAlarmReqd) {
			if (logger.isDebugEnabled())
				logger.debug("Enter raiseAlarm() ->id:::[" + code + "]  message::[" + message
								+ "] alarm is not reuired");
			return;
		}

		ProcessBuilder pb = null;
		Process process = null;
		Object[] params = { Integer.toString(code), message };
		String command = MessageFormat.format(Constants.RAISE_ALARM_COMMAND, params);

		if (logger.isDebugEnabled())
			logger.debug("raiseAlarm() ->Alarm command created::[" + command + "]");

		try {
			pb = new ProcessBuilder("bash", "-c", command);
			pb.redirectErrorStream(true);
			process = pb.start();
		    BufferedReader br = new BufferedReader(new InputStreamReader(process.getInputStream()));
		    String s;
		    if (logger.isDebugEnabled())
	    		logger.debug("Flushing the stream");
		    while ((s = br.readLine()) != null) {
		    	if (logger.isDebugEnabled())
		    		logger.debug(s);
			}

			process.waitFor();
		} catch (IOException e) {
			logger.error("IOException in raiseAlarm", e);
		} catch (InterruptedException e) {
			logger.error("InterruptedException in raiseAlarm", e);
		} finally {
			if (process != null)
				process.destroy();
		}

		if (logger.isDebugEnabled())
			logger.debug("Leave raiseAlarm()");
	}
	
	private void raiseInformationalAlarm(int code, String message) {

		logger.error("Enter raiseInformationalAlarm() ->id:::[" + code + "]  message::[" + message + "]");

		ProcessBuilder pb = null;
		Process process = null;
		Object[] params = { Integer.toString(code), message };
		String command = MessageFormat.format(Constants.RAISE_ALARM_COMMAND, params);

		if (logger.isDebugEnabled())
			logger.debug("raiseInformationalAlarm() ->Alarm command created::[" + command + "]");

		try {
			pb = new ProcessBuilder("bash", "-c", command);
			pb.redirectErrorStream(true);
			process = pb.start();
		    BufferedReader br = new BufferedReader(new InputStreamReader(process.getInputStream()));
		    String s;
		    if (logger.isDebugEnabled())
	    		logger.debug("Flushing the stream");
		    while ((s = br.readLine()) != null) {
		    	if (logger.isDebugEnabled())
		    		logger.debug(s);
			}

			process.waitFor();
		} catch (IOException e) {
			logger.error("IOException in raiseInformationalAlarm", e);
		} catch (InterruptedException e) {
			logger.error("InterruptedException in raiseInformationalAlarm", e);
		} finally {
			if (process != null)
				process.destroy();
		}

		if (logger.isDebugEnabled())
			logger.debug("Leave raiseInformationalAlarm()");
	}

	private boolean checkAlarm(int code) {
		if (logger.isDebugEnabled())
			logger.debug("Enter checkAlarm() ->id:::[" + code + "]");

		int checkCode = code;
		boolean retValue = false;

		if (code == Constants.DB_IS_ACCESIBLE) {
			checkCode = Constants.DB_NOT_ACCESIBLE;
		} else if (code == Constants.CONFIG_NOW_ACCESIBLE) {
			checkCode = Constants.CONFIG_NOT_FOUND;
		}
		Boolean status = alarmStatusMap.get(checkCode);

		if (status == null) {
			status = false;
		}

		if (status) {
			switch (code) {
				case Constants.DB_IS_ACCESIBLE:
				case Constants.CONFIG_NOW_ACCESIBLE: {
					retValue = true;
					alarmStatusMap.put(checkCode, false);
					break;
				}
				case Constants.DB_NOT_ACCESIBLE:
				case Constants.CONFIG_NOT_FOUND: {
					retValue = false;
					break;
				}
			}//end switch
		} else {
			switch (code) {
				case Constants.DB_IS_ACCESIBLE:
				case Constants.CONFIG_NOW_ACCESIBLE: {
					retValue = false;
					break;
				}
				case Constants.DB_NOT_ACCESIBLE:
				case Constants.CONFIG_NOT_FOUND: {
					retValue = true;
					alarmStatusMap.put(checkCode, true);
					break;
				}
			}//end switch

		}

		if (logger.isDebugEnabled()) {
			logger.debug("leave checkAlarm() ->id:::[" + code + "] with status:::[" + retValue
							+ "]");
		}

		return retValue;
	}

}
