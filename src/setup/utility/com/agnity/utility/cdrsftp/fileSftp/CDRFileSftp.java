package com.agnity.utility.cdrsftp.fileSftp;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.text.MessageFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.comparator.LastModifiedFileComparator;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.agnity.utility.cdrsftp.dbpush.exceptions.CounterFileIoFailedException;
import com.agnity.utility.cdrsftp.fileSftp.exceptions.CdrSftpFailedException;
import com.agnity.utility.cdrsftp.fileSftp.exceptions.DirectorySetFailedException;
import com.agnity.utility.cdrsftp.fileSftp.exceptions.InvalidLocalDirException;
import com.agnity.utility.cdrsftp.fileSftp.exceptions.LocationNotFoundException;
import com.agnity.utility.cdrsftp.fileSftp.exceptions.SftpNotConnectedException;
import com.agnity.utility.cdrsftp.fileSftp.utils.CDRSftpConfig;
import com.agnity.utility.cdrsftp.fileSftp.utils.Constants;
import com.jcraft.jsch.JSchException;

public class CDRFileSftp {

	private static Logger			logger;

	private CDRLocation				cdrLocation;
	private long					waitPeriod;
	private CDRSftpConnection       cdrSftpConnection;
	private CDRSftpConfig           cdrSftpConfig;
	private Map<Integer, Boolean>	alarmStatusMap	= new HashMap<Integer, Boolean>();

	private static String remoteFilePrefix;
	private static String remoteFileExt;

	private boolean isCdrDateDirEnabled;

	private String cdrDateDirName = null;
	private static String dateFormat;
	private static SimpleDateFormat dateFormatter = null; 
	private static int noOfCDRsToProcess = 25;
	private final SimpleDateFormat cdrDateDirFormat = new SimpleDateFormat("MM_dd_yyyy");

	private String filePrefix;
	private String fileExt;
	private String origCDRDir;
	private String origCDRArchiveDir;
	private boolean checkCDRDateDir = true;
	private boolean changeArchiveDir = false;


	static {
		PropertyConfigurator.configure("log4j.properties");
		logger = Logger.getLogger(CDRFileSftp.class);
	}

	/**
	 * Main methods starts the flow
	 * steps:
	 * Read Configuration
	 * initialize cdr location and cdr archive Dir
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		if (logger.isInfoEnabled())
			logger.info("Start CDR Remote Sftp main()");
		CDRFileSftp cdrSftp = new CDRFileSftp();

		//read configurations
		cdrSftp.initialize();

		cdrSftp.raiseInformationalAlarm(Constants.CONFIG_NOW_ACCESIBLE, Constants.CONFIG_NOW_ACCESIBLE_MESSAGE);

		//initializes and start flow
		cdrSftp.triggerFlow();

		if (logger.isInfoEnabled())
			logger.info("End CDR File Sftp main()");
	}

	/**
	 * steps:
	 * Read Configuration
	 * initialize cdr location and cdr archive Dir
	 * initializes properties
	 */

	private void initialize(){
		try {
			readConfiguration();
		} catch (Exception e) {
			logger.error("Exception reading config file. Exit", e);
			raiseAlarm(Constants.CONFIG_NOT_FOUND, Constants.CONFIG_NOT_FOUND_MESSAGE + " "
					+ e.getMessage(),true);
			threadSleep(60000, "Error waiting before exit ");
			System.exit(1);
		}

		//initialize counters
		try {
			initializeLocations();
		} catch (FileNotFoundException e) {
			handleException(e);
		} catch (URISyntaxException e) {
			handleException(e);
		}catch (InvalidLocalDirException e) {
			handleException(e);
		}catch (LocationNotFoundException e) {
			handleException(e);
		}

		initializeProperties();

	}

	/**
	 * Steps:
	 * initialize Sftp Connection
	 * start CDR sftp flow 
	 */
	private void triggerFlow() {
		if(logger.isDebugEnabled())
			logger.debug("Enter triggerFlow");
		boolean exit = false;

		try {

			//initialize sftpConnection
			initializeSftpConnection();
			//raiseInformationalAlarm(Constants.SFTP_CONNECTION_UP, Constants.SFTP_CONNECTION_UP_MESSAGE+" "+cdrSftpConfig.getRemoteIp());
			raiseAlarm(Constants.SFTP_CONNECTION_UP, Constants.SFTP_CONNECTION_UP_MESSAGE, false);
			raiseAlarm(Constants.CONFIG_NOW_ACCESIBLE, Constants.CONFIG_NOW_ACCESIBLE_MESSAGE, true);
			//start flow
			startCdrSftpFlow();
		} catch (JSchException e) {
			logger.error("JSchException in creating SFTP connection",e);
			raiseAlarm(Constants.SFTP_CONNECTION_DOWN, Constants.SFTP_CONNECTION_DOWN_MESSAGE+" "+cdrSftpConfig.getRemoteIp()
					+" "+e.getMessage(), true);
		} catch (SftpNotConnectedException e) {
			logger.error("SftpNotConnectedException in creating SFTP connection",e);
			raiseAlarm(Constants.SFTP_CONNECTION_DOWN, Constants.SFTP_CONNECTION_DOWN_MESSAGE+" "+cdrSftpConfig.getRemoteIp()
					+" "+e.getMessage(), true);
		} catch (DirectorySetFailedException e) {
			logger.error("DirectorySetFailedException in setting dirs on sftp connection",e);
			raiseAlarm(Constants.SFTP_CONNECTION_DOWN, Constants.SFTP_CONNECTION_DOWN_MESSAGE+" "+cdrSftpConfig.getRemoteSftpDir()
					+" "+e.getMessage(), true);
		} catch (CdrSftpFailedException e) {
			logger.error("CdrSftpFailedException on sftp of file",e);
			raiseAlarm(Constants.SFTP_CONNECTION_DOWN, Constants.SFTP_CONNECTION_DOWN_MESSAGE+" "+e.getMessage(), true);
		} catch (Exception e) {
			logger.error("Unknown Exception in Utility",e);
			exit = true;
		}finally{
			if(cdrSftpConnection != null){
				cdrSftpConnection.destroy();
			}

			if(exit){
				System.exit(1);
			}

			//wait before retry
			threadSleep(waitPeriod, null);
		}//@
	}

	/**
	 * method reads configuration
	 */
	private void readConfiguration() {
		if (logger.isInfoEnabled())
			logger.info("Inside readConfiguration()");

		String[] paths = { "/CdrSftpConfig.xml" };
		ApplicationContext appContext = new ClassPathXmlApplicationContext(paths);
		cdrSftpConfig = (CDRSftpConfig) appContext.getBean("cdrSftpConfig");
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
	InvalidLocalDirException,LocationNotFoundException {

		if (logger.isDebugEnabled())
			logger.debug("Inside initializeLocations()");

		String cdrLoc = null;
		String cdrArchiveLoc = null;

		cdrLoc = cdrSftpConfig.getCdrLocalDir();
		cdrArchiveLoc = cdrSftpConfig.getCdrArchiveDir();
		if (cdrLoc != null && cdrArchiveLoc != null) {
			cdrLocation = new CDRLocation(cdrLoc,cdrArchiveLoc);
			cdrLocation.initialize(cdrSftpConfig);
			origCDRDir = cdrLocation.getCdrLocation();
			origCDRArchiveDir = cdrLocation.getCdrArchive();
			if (!origCDRArchiveDir.endsWith(Constants.SLASH)) {

				origCDRArchiveDir = origCDRArchiveDir+Constants.SLASH;
			}

		}

		if (cdrLocation == null) {
			logger.error("CDR Loc is null" + "CDR Loc:["
					+ cdrLocation + "]" );

			throw new LocationNotFoundException(
					"CDR Loc is null " + "CDR Loc:["
							+ cdrLocation + "]  ");
		}

		if (logger.isDebugEnabled())
			logger.debug("Leaving initializeLocations() with " + "CDR Loc:[" + cdrLocation
					+ "]  " );
	}


	private void initializeSftpConnection() 
			throws JSchException, SftpNotConnectedException, DirectorySetFailedException, Exception {
		if(logger.isDebugEnabled())
			logger.debug("Enter initializeSftpConnection");
		//create sftp connection and set directories
		cdrSftpConnection = new CDRSftpConnection();
		cdrSftpConnection.initialize(cdrSftpConfig);
		//channel created setting directories
		if(logger.isDebugEnabled())
			logger.debug("Leave initializeSftpConnection() ");

	}

	private void initializeProperties(){

		if (logger.isDebugEnabled())
			logger.debug("Inside initializeProperties()");

		waitPeriod = cdrSftpConfig.getCdrWaitInterval() * 1000;
		remoteFilePrefix = cdrSftpConfig.getCdrRemoteFilePrefix();
		remoteFileExt = cdrSftpConfig.getCdrRemoteFileExtension();
		filePrefix=cdrSftpConfig.getCdrFilePrefix();
		fileExt = cdrSftpConfig.getCdrFileExtension();
		dateFormat = cdrSftpConfig.getCdrRemoteFileDateFormat();
		if(dateFormat!=null && !dateFormat.isEmpty()){
			dateFormatter = new SimpleDateFormat(dateFormat);
		}
		noOfCDRsToProcess = cdrSftpConfig.getNumberOfCdrsToProcess();
		if(noOfCDRsToProcess <=0 || noOfCDRsToProcess > 100){
			noOfCDRsToProcess = 25;
			logger.debug("Value of No of CDRs to Process set as:: " + noOfCDRsToProcess);
		}

		isCdrDateDirEnabled = cdrSftpConfig.getCdrDateDirEnabled();
	}

	/**
	 * Flow:
	 * a) Checks if CDRs are available. If no, waits for a period and then checks for CDRs again
	 * b) If CDRs are available, then sftped to the Remote Server. 10 CDRs are picked at a time and sftped one by one
	 * c) Sftped CDRs are moved to the CDR Archive folder with the extension .PROCESSED
	 * 
	 * @throws JSchException,CdrSftpFailedException
	 * @throws DirectorySetFailedException 
	 */
	private void startCdrSftpFlow() throws CdrSftpFailedException, JSchException, DirectorySetFailedException, Exception{
		if (logger.isDebugEnabled())
			logger.debug("Inside startCdrSftpFlow()");

		boolean cdrSftpStatus =false;

		while (true) {
			try {
				cdrSftpStatus = sftpCDRsToRemoteServer(cdrLocation);
				if (!cdrSftpStatus) {
					if (logger.isDebugEnabled())
						logger.debug("Either CDR Location is empty. No CDRs to Sftp  OR sftp of CDRs failed");
					threadSleep(waitPeriod, "Exception in sleep");
				}

			} catch(Exception e){
				logger.error("CdrSftpFailed due to Exception", e);
				cdrSftpConnection.resetConnection();
				cdrSftpConnection.setDirectories(cdrLocation.getCdrLocation(), cdrSftpConfig.getRemoteSftpDir());
			}
		}//@End while
	}//@End startCdrSftpFlow


	/**
	 *This method is used to fetch the CDR files from the CDR Location.
	 *10 CDR files are fetched at a time
	 * @param CDR Location
	 */
	public List<String>  getFiles(String cdrLoc){
		List<String> listOfFiles = new ArrayList<String>();

		File dirFile = new File(cdrLoc);

		FilenameFilter fileFilter = new FilenameFilter() {
			public boolean accept(File dir, String name) {
				// for example : MNSAS2_0P19563.ACT
				return name.matches(filePrefix+"_.*\\."+fileExt);

			}
		};

		File listFiles [] = dirFile.listFiles(fileFilter);

		if(listFiles.length > noOfCDRsToProcess){

			/*			Arrays.sort(listFiles, new Comparator<File>() {

				@Override
				public int compare(File f1, File f2) {
					return Long.valueOf(f1.lastModified()).compareTo(f2.lastModified());
				}
			});*/

			Arrays.sort(listFiles,LastModifiedFileComparator.LASTMODIFIED_COMPARATOR);
		}

		for(int i = 0 ;i < listFiles.length ; i++){
			listOfFiles.add(listFiles[i].getName());
			// Process the number of files mentioned in properties file
			if(i == (noOfCDRsToProcess-1)){
				break;
			}
		}

		if (logger.isDebugEnabled()){
			logger.debug("Inside getFiles()-> :" + listOfFiles);
		}

		return listOfFiles;

	}

	/**
	 * This method returns the date directory from which CDR files are to fetched.
	 * It first lists all the directories in the CDR location on the basis of their 
	 * last modified date. Then the oldest created date directory is returned if it matches 
	 * the date format and CDR Files are present in it. 
	 * @param CDR Location
	 */
	private String getCdrDateDirectory(String cdrLoc) {
		if(logger.isDebugEnabled()) logger.debug("Get CDR Date Directory  ");
		File dirFile = new File(cdrLoc);
		File[] subDirs = dirFile.listFiles((FileFilter) DirectoryFileFilter.DIRECTORY);
		Arrays.sort(subDirs,LastModifiedFileComparator.LASTMODIFIED_COMPARATOR);
		for(final File subDir : subDirs){
			if(subDir.isDirectory() && matchesFormat(subDir.getName()) && cdrFilesPresent(subDir.toString())){
				cdrDateDirName  = subDir.getName();
				return subDir.toString();
			}
		}
		return null;
	}

	/**
	 * This method checks whether the directories inside the CDR location matches the date format 
	 * to ensure that only the date directories are processed.
	 * @param CDR Directory Name
	 */
	private boolean matchesFormat(String dirName) {
		try {
			Date date = cdrDateDirFormat.parse(dirName);
			return dirName.equals(cdrDateDirFormat.format(date));
		} catch (ParseException e) {
			logger.error(dirName + " Directory does not match the CDR Date Directory format. Will not be processed", e);
			return false;
		}

	}

	/**
	 * This method checks whether CDR Files are present in the directory or not to ensure 
	 * that the directory is processed only if CDRs are written in it.
	 * @param CDR Location
	 */
	private boolean cdrFilesPresent(String cdrLoc){

		File cdrDateDir = new File(cdrLoc);

		FilenameFilter fileFilter = new FilenameFilter() {
			public boolean accept(File dir, String name) {
				// for example : MNSAS2_0P19563.ACT
				return name.matches(filePrefix+"_.*\\."+fileExt);

			}
		};

		File listFiles [] = cdrDateDir.listFiles(fileFilter);

		if(listFiles.length >0 ){
			return true;
		}

		return false;
	}

	/**
	 * Sftp CDR files to a remote server
	 * @throws CdrSftpFailedException
	 * @throws DirectorySetFailedException 
	 */
	private boolean sftpCDRsToRemoteServer(CDRLocation cdrLocation) throws CdrSftpFailedException, DirectorySetFailedException{
		if (logger.isDebugEnabled())
			logger.debug("Inside sftpCDRsToRemoteServer()-> " + cdrSftpConfig.getRemoteIp());

		String cdrLoc = cdrLocation.getCdrLocation();

		if (!cdrLoc.endsWith(Constants.SLASH)) {

			cdrLoc = cdrLoc+Constants.SLASH;
		}

		if(isCdrDateDirEnabled){
			File cdrDateDir = new File(cdrLoc);
			if(!cdrDateDir.exists() || !cdrFilesPresent(cdrLoc) || checkCDRDateDir ){
				cdrLoc = getCdrDateDirectory(origCDRDir);
				if(cdrLoc == null){
					if (logger.isDebugEnabled())
						logger.debug("Inside sftpCDRsToRemoteServer()->No CDR files availble for Sftp in CDR Date Directory "
								+ "return with found::[ false ]");
					checkCDRDateDir = true;
					return false;
				}
				cdrLocation.setCdrLocation(cdrLoc);
				cdrSftpConnection.resetLocalDirectory(cdrLoc);
				cdrLoc = cdrLoc+Constants.SLASH;
				checkCDRDateDir = false;
				changeArchiveDir = true;
			}

		}

		if (logger.isDebugEnabled())
			logger.debug("Reading CDRs from directory  : " + cdrLoc);

		// getting 10 or less files list which need to be processed
		List<String> rawFileNameList =  getFiles(cdrLoc);

		boolean status = true;
		// if no file to processed, return from here
		if(rawFileNameList.size() == 0){
			status = false;
			if (logger.isDebugEnabled())
				logger.debug("Inside sftpCDRsToRemoteServer()->No files availble for Sftp :["
						+ rawFileNameList + " ] return with found::["
						+ status + "]");
			return status;
		}

		boolean isFileSftpSuccessfull = false;

		for(String rawFileName: rawFileNameList){
			try {
				String destFile = null;
				if(cdrSftpConfig.getRenameRemoteCdrFile()){
					destFile = getDestinationFileName(rawFileName);
				}
				isFileSftpSuccessfull = cdrSftpConnection.sftpFile(rawFileName, destFile);
				if(!isFileSftpSuccessfull)
					break;
			} catch (CdrSftpFailedException e) {
				logger.error("CdrSftpFailedException sftp cdrs::"+e.getMessage(),e);
				throw e;
			}
		}

		if (isFileSftpSuccessfull) {				
			moveACTFile(rawFileNameList, cdrLocation.getCdrArchive(),cdrLoc);

		} else {
			if (logger.isDebugEnabled())
				logger.debug("Inside sftpCDRsToRemoteServer->Sftp failed for files:["+
						rawFileNameList	+ " ]");
			//raiseAlarm(Constants.DB_NOT_ACCESIBLE, Constants.DB_NOT_ACCESIBLE_MESSAGE + " "
			//+ cdrLocation.getCdrLocation());
			status = false;
		} 

		if (logger.isDebugEnabled())
			logger.debug("Leave sftpCDRsToRemoteServer ->processing location::["
					+ cdrLocation.getCdrLocation() + "] with Status::[" + status + "]");

		return status;
	}

	/**
	 * Returns the renamed CDR file which should be sftped
	 * @param String original CDR filename
	 * @return String renamed CDR filename
	 */
	private String getDestinationFileName(String rawFileName) {
		String cdrSeq = rawFileName.substring(rawFileName.lastIndexOf(Constants.UNDERSCORE), 
				rawFileName.lastIndexOf(Constants.DOT));
		StringBuilder fileName = new StringBuilder();

		fileName.append(remoteFilePrefix);
		fileName.append(cdrSeq);
		if(dateFormatter!=null){
			Date dt = new Date();
			String suffix = dateFormatter.format(dt);
			fileName.append(Constants.UNDERSCORE);
			fileName.append(suffix);
		}
		fileName.append(Constants.DOT);
		fileName.append(remoteFileExt);

		return fileName.toString();
	}

	/**
	 * Moves the sftped CDR file to the Archive directory
	 */
	private void moveACTFile(List<String> rawFileNameList, String moveDir, String cdrLoc){

		if(!moveDir.endsWith(Constants.SLASH)){
			moveDir = moveDir+Constants.SLASH;
		}

		if(isCdrDateDirEnabled && cdrDateDirName!=null && changeArchiveDir ){
			logger.debug("Changing Archive Directory");
			File archiveDateDir = new File(origCDRArchiveDir+cdrDateDirName);
			if(!archiveDateDir.exists()){
				archiveDateDir.mkdir();
				logger.debug("Created Archive CDR Date Directory :: " + archiveDateDir.toString());
			}
			moveDir = archiveDateDir.toString()+Constants.SLASH;
			cdrLocation.setCdrArchive(moveDir);
			changeArchiveDir = false;
		}

		logger.debug("Moving ACT Files : rawCntrFileNameList : " + rawFileNameList + "  : moveDir :  "+moveDir );

		File originalFile = null;
		File reNamedFile = null;

		try {
			for( int i = 0 ; i < rawFileNameList.size() ; i++){
				originalFile = new File(cdrLoc + rawFileNameList.get(i));
				reNamedFile = new File(moveDir+rawFileNameList.get(i)+".PROCESSED");

				originalFile.renameTo(reNamedFile);
			}

			logger.debug("Inside moveACTFile:  Files moved to .PROCESSED "  );

			if(isCdrDateDirEnabled){
				File cdrDateDir = new File(cdrLoc);
				if(cdrDateDir.exists() && cdrDateDir.list().length == 0){
					logger.debug("Deleting Empty CDR Date Directory :: " + cdrLoc);
					cdrDateDir.delete();
				}
			}

		} catch (Exception e) {
			logger.error("Exception occured in moveACTFile : " + e.getMessage());
		}

	}

	/**
	 * Raises Critical Alarms in following cases:
	 * a) Sftp Connection goes down
	 * b) Sftp Connection comes up after going down
	 * c) Configuration is not accesible
	 * d) Configuration becomes accessible when earlier it was not accessible 
	 */
	public void raiseAlarm(int code, String message, boolean checkCode) {
		if (logger.isDebugEnabled())
			logger.debug("Enter raiseAlarm() ->id:::[" + code + "]  message::[" + message + "]");

		boolean isAlarmReqd;
		if(checkCode){
			isAlarmReqd = checkAlarm(code);
		}else{
			isAlarmReqd = true;
		}

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

	/**
	 * Raises Informational Alarm when Configuration file is successfully 
	 * read during initialization
	 */
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

		if (code == Constants.CONFIG_NOW_ACCESIBLE) {
			checkCode = Constants.CONFIG_NOT_FOUND;
		}else if(code == Constants.SFTP_CONNECTION_UP){
			checkCode = Constants.SFTP_CONNECTION_DOWN;
		}
		Boolean status = alarmStatusMap.get(checkCode);

		if (status == null) {
			status = false;
		}

		if (status) {
			switch (code) {
			case Constants.SFTP_CONNECTION_UP :
			case Constants.CONFIG_NOW_ACCESIBLE: {
				retValue = true;
				alarmStatusMap.put(checkCode, false);
				break;
			}
			case Constants.SFTP_CONNECTION_DOWN :
			case Constants.CONFIG_NOT_FOUND: {
				retValue = false;
				break;
			}
			}//end switch
		} else {
			switch (code) {
			case Constants.SFTP_CONNECTION_UP :
			case Constants.CONFIG_NOW_ACCESIBLE: {
				retValue = false;
				break;
			}
			case Constants.SFTP_CONNECTION_DOWN :
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


	private void threadSleep(long milliseconds, String errorMessage){

		try {
			Thread.sleep(milliseconds);
		} catch (InterruptedException e1) {
			logger.error(errorMessage + e1.getMessage());
		}

	}

	private void handleException(Exception e){
		logger.error("Following Exception at time of initialize. Exit", e);
		raiseAlarm(Constants.CONFIG_NOT_FOUND, Constants.CONFIG_NOT_FOUND_MESSAGE + " "
				+ e.getMessage(), true);
		threadSleep(60000," Erro waiting before exit ");
		System.exit(1);
	}

}
