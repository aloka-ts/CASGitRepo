package com.agnity.utility.cdr;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.URISyntaxException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;
import com.jcraft.jsch.UserInfo;


public class CDRPush {

	private static Logger logger; 

	private static final String PRIMARY_LOC = "PRIMARY";
	private static final String SECONDARY_LOC = "SECONDARY";
	private static final String FILE_NAME_PRIMARY_LOC_APPENDER = "P";
	private static final String FILE_NAME_SECONDARY_LOC_APPENDER = "S";
	private static final String TMP_FILE_EXTENSION = ".TMP";
	private static final String TMP_FILE_CREATE_COMMAND="egrep -v {0} {1} > {2}";
	private static final String TMP_FILE_DELETE_COMMAND="rm - r {0}";
	private static final String COUNTER_FILE_NAME = "cdrPushFileSeqCntr.dat";

	private RandomAccessFile counterFile;
	CDRPushConfig cdrPushToDSIConfig;
	Session session=null;
	ChannelSftp sftpChannel=null;
	int index = -1;

	static{
		PropertyConfigurator.configure("log4j.properties");
		logger = Logger.getLogger(CDRPush.class);
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		if(logger.isInfoEnabled())
			logger.info("Start CDR push main()");
		CDRPush dsi=new CDRPush();
		dsi.startCdrPushFlow();

		if(logger.isInfoEnabled())
			logger.info("End CDR push main()");
	}

	/**
	 * Flow:
	 * Create session
	 * check status of session
	 * create channel and check its status
	 * 
	 * 
	 * 
	 * @param cdrPushToDSIConfig
	 */
	private void startCdrPushFlow() {
		//read config
		if(logger.isInfoEnabled())
			logger.info("Inside startCdrPushFlow()");

		if(logger.isDebugEnabled())
			logger.debug("startCdrPushFlow() Read Config from properties");

		String[] paths={"/CdrPushConfig.xml"};
		ApplicationContext appContext=new ClassPathXmlApplicationContext(paths);
		cdrPushToDSIConfig =(CDRPushConfig)appContext.getBean("cdrPushToDSIConfig");

		long waitPeriod=cdrPushToDSIConfig.getCdrPushWaitInterval()*1000;

		if(logger.isDebugEnabled())
			logger.debug("startCdrPushFlow() Properties read; read counter file");

		//reading counter file
		try{
			validateAndReadCounterFromFile();
		}catch (FileNotFoundException e){
			System.exit(1);
		}catch (URISyntaxException e){
			System.exit(1);
		} catch (InvalidLocalDirException e) {
			System.exit(1);
		} catch (CounterFileIoFailedException e) {
			System.exit(1);
		}

		if(logger.isDebugEnabled())
			logger.debug("startCdrPushFlow() counter succesfully read value= ["+ index +"]; entering while");

		//creating session and sftp loops
		while(true){
			try{
				if(logger.isDebugEnabled())
					logger.debug("startCdrPushFlow() Before sFTp connection to DSI");
				createSftpChannel();
				if(logger.isDebugEnabled())
					logger.debug("startCdrPushFlow() Setting local and remote  Directrories");
				setDirectories();
				if(logger.isDebugEnabled())
					logger.debug("startCdrPushFlow() SFTP check for tmp file");
				List<String> localTmpList=getTmpFileFromLocal();
				if((localTmpList!=null) && !(localTmpList.isEmpty())){
					if(logger.isDebugEnabled())
						logger.debug("startCdrPushFlow() SFTP existing TMP files");
					sftpInitialTmpFiles(localTmpList);
				}
				if(logger.isDebugEnabled())
					logger.debug("startCdrPushFlow() SFTP files as per counter");
				sftpCounterFile();
				logger.error("startCdrPushFlow() Unreachable Log. If reached sftp counter file exited");
			}catch (JSchException e){
				logger.error("startCdrPushFlow()  JSCH exception creating sftp session in createSftpChannel(); " +
						"Will retry automatically, Exception Trace::",e);
			}catch (SftpNotConnectedException e){ 
				logger.error("startCdrPushFlow()  SftpNotConnectedException creating sftp in createSftpChannel(); " +
						"Will retry automatically, Exception Trace::",e);
			} catch (LocalFileRemovalFaileException e) { //from sftpInitialTmpFiles sftpCounterFile->removeTmpFile
				logger.error("startCdrPushFlow()  LocalFileRemovalFaileException removing tmp file from local " +
						"Will Reconnect and retry automatically, Exception Trace::",e);
			} catch (SftpFailedException e) { //from sftpInitialTmpFiles sftpCounterFile ->sftpTmpFile
				logger.error("startCdrPushFlow()  SftpFailedException sftping/renaming tmp file to/at dsi " +
						"Will Reconnect and retry automatically, Exception Trace::",e);
			} catch (DirectorySetFailedException e) { //from setDirectories
				logger.error("startCdrPushFlow()  DirectorySetFailedException setiing directories " +
						"Will Reconnect and retry automatically; Check Directories are valid and restart " +
						"manually in case of configuration changes, Exception Trace::",e);
			} catch (CounterFileIoFailedException e) { //sftpCounterFile->writeIndex()
				logger.error("startCdrPushFlow()  CounterFileIoFailedException writing updated cntr to file" +
						"Will Reconnect and retry automatically, Exception Trace::",e);
			}finally{
				if(sftpChannel!=null && sftpChannel.isConnected())
					sftpChannel.disconnect();
				if(session!=null && session.isConnected())
					session.disconnect();
				//wait before retry
				try {
					Thread.sleep(waitPeriod);
				} catch (InterruptedException e) {
					//do nothing continue without sleep
				}
			}//end try catch finally
		}//end while
	}//end startCdrPushFlow



	private void sftpInitialTmpFiles(List<String> localTmpList) 
	throws LocalFileRemovalFaileException, SftpFailedException {
		String filePrefix=cdrPushToDSIConfig.getCdrPushLocalFilePrefix();
		String locType=cdrPushToDSIConfig.getCdrPushLocalLocType();
		StringBuilder fileName=new StringBuilder();
		fileName.append(filePrefix);
		if(locType.equalsIgnoreCase(PRIMARY_LOC)){
			fileName.append(FILE_NAME_PRIMARY_LOC_APPENDER);
		}else if(locType.equalsIgnoreCase(SECONDARY_LOC)){
			fileName.append(FILE_NAME_SECONDARY_LOC_APPENDER);
		}
		fileName.append(index);
		fileName.append(TMP_FILE_EXTENSION);
		String counterTmpFile=fileName.toString();
		if(logger.isDebugEnabled())
			logger.debug("Inside sftpInitialTmpFiles()->Before Iterating TmpFile list CounterTmpfIleNAme:["+counterTmpFile+"]");		
		Iterator<String> tmpFileListIterator=localTmpList.iterator();
		String tmpFile=null;
		String cdrLoc=cdrPushToDSIConfig.getCdrPushLocalDirName();
		StringBuilder tmpFileAbsolutePath=null;
		while(tmpFileListIterator.hasNext()){
			tmpFile=tmpFileListIterator.next();
			tmpFileListIterator.remove();
			//getting absolute tmp file path
			tmpFileAbsolutePath=new StringBuilder();
			tmpFileAbsolutePath.append(cdrLoc);
			if(!cdrLoc.endsWith("/")){
				tmpFileAbsolutePath.append("/");
			}
			tmpFileAbsolutePath.append(tmpFile);
			if(logger.isDebugEnabled())
				logger.debug("Inside sftpInitialTmpFiles()->Tmp file read from list:["+tmpFile+"]");	
			if(tmpFile.equals(counterTmpFile)){
				//remove file from local and remove
				if(logger.isDebugEnabled())
					logger.debug("Inside sftpInitialTmpFiles()->Tmp file matches counter file");	
				removeTmpFile(tmpFileAbsolutePath.toString());
				continue;
			}
			//sftp and rename
			if(logger.isDebugEnabled())
				logger.debug("Inside sftpInitialTmpFiles()->Before SFTP tmp file:["+tmpFile+"]");	
			sftpTmpFile(tmpFile);
			if(logger.isDebugEnabled())
				logger.debug("Inside sftpInitialTmpFiles()->bEFORE deleting tmp file from locaL:["+tmpFile+"]");	
			removeTmpFile(tmpFileAbsolutePath.toString());
		}
		if(logger.isDebugEnabled())
			logger.debug("Leaving sftpInitialTmpFiles()");		
	}

	private void sftpCounterFile() 
	throws LocalFileRemovalFaileException, SftpFailedException, 
	CounterFileIoFailedException {
		String filePrefix=cdrPushToDSIConfig.getCdrPushLocalFilePrefix();
		String locType=cdrPushToDSIConfig.getCdrPushLocalLocType();
		StringBuilder fileName=null;
		StringBuilder tmpFileName=null;
		long waitPeriod=cdrPushToDSIConfig.getCdrPushWaitInterval()*1000;
		String nonDsiIdentifier=cdrPushToDSIConfig.getCdrPushNonDSIIdentifier();
		String command= null;
		Process process=null;
		while(true){
			//making current counter filenaame
			fileName=new StringBuilder();
			tmpFileName=new StringBuilder();

			fileName.append(filePrefix);
			tmpFileName.append(filePrefix);

			if(locType.equalsIgnoreCase(PRIMARY_LOC)){

				fileName.append(FILE_NAME_PRIMARY_LOC_APPENDER);
				tmpFileName.append(FILE_NAME_PRIMARY_LOC_APPENDER);

			}else if(locType.equalsIgnoreCase(SECONDARY_LOC)){

				fileName.append(FILE_NAME_SECONDARY_LOC_APPENDER);
				tmpFileName.append(FILE_NAME_SECONDARY_LOC_APPENDER);

			}

			fileName.append(index);
			tmpFileName.append(index);

			fileName.append(".");
			fileName.append(cdrPushToDSIConfig.getCdrPushLocalFileExtension());
			tmpFileName.append(TMP_FILE_EXTENSION);

			String counterLocalFile=fileName.toString();
			String counterTmpFile=tmpFileName.toString();

			if(logger.isDebugEnabled())
				logger.debug("Inside sftpCounterFile()->Counter file name to check:["+ counterLocalFile +
						"]  Tmp file to be craeted:["+counterTmpFile+"]");	

			//check if file exists locally
			String cdrLoc=cdrPushToDSIConfig.getCdrPushLocalDirName();
			StringBuilder cntrFileAbsolutePath=new StringBuilder();
			StringBuilder tmpFileAbsolutePath=new StringBuilder();

			cntrFileAbsolutePath.append(cdrLoc);
			tmpFileAbsolutePath.append(cdrLoc);
			if(!cdrLoc.endsWith("/")){
				cntrFileAbsolutePath.append("/");
				tmpFileAbsolutePath.append("/");
			}
			cntrFileAbsolutePath.append(counterLocalFile);
			tmpFileAbsolutePath.append(counterTmpFile);
			//creating file on CDR dir
			File dir = new File(cdrLoc);
			File file=new File(dir,counterLocalFile);
			if(!file.exists()){
				if(logger.isDebugEnabled())
					logger.debug("Inside sftpCounterFile()->Counter file doesnt exist:["+ cntrFileAbsolutePath.toString()+" ]");	
				try {
					Thread.sleep(waitPeriod);
				} catch (InterruptedException e) {
					//do nothing continue without sleep
				}
				continue;
			}//end if file chk


			//reach here if file is found
			//creating tmp file
			Object[] params={nonDsiIdentifier, cntrFileAbsolutePath.toString(), tmpFileAbsolutePath.toString()};
			command=MessageFormat.format(TMP_FILE_CREATE_COMMAND, params);
			if(logger.isDebugEnabled())
				logger.debug("Inside sftpCounterFile()->Creating tmp file:["+ counterTmpFile+" ]  create command:["+command+"]");	
			try {
				process = new ProcessBuilder("bash","-c",command).start();
				process.waitFor();
			} catch (IOException e) {
				if(logger.isDebugEnabled())
					logger.error("IOEXCEPTIOn in creation of tmp file",e);
				if(process!=null)
					process.destroy();
				try {
					Thread.sleep(waitPeriod);
				} catch (InterruptedException e1) {
					//do nothing continue without sleep
				}
				continue;
			}// end try catch for creAting tmp file
			catch (InterruptedException e) {
				if(logger.isDebugEnabled())
					logger.error("InterruptedException in creation of tmp file",e);
				if(process!=null)
					process.destroy();
				try {
					Thread.sleep(waitPeriod);
				} catch (InterruptedException e1) {
					//do nothing continue without sleep
				}
				continue;
			}
			//INCREMENT INDEX and write to control file
			index++;
			writeIndex(index);
			if(logger.isDebugEnabled())
				logger.debug("Inside sftpCounterFile()->incremnted counter to:["+ index+" ]");

			if(logger.isDebugEnabled())
				logger.debug("Inside sftpInitialTmpFiles()->Before SFTP tmp file:["+counterTmpFile+"]");	
			sftpTmpFile(counterTmpFile);
			if(logger.isDebugEnabled())
				logger.debug("Inside sftpInitialTmpFiles()->bEFORE deleting tmp file from locaL:["+counterTmpFile+"]");	
			//remove local tmp file
			removeTmpFile(tmpFileAbsolutePath.toString());

		}//end while
	}

	private void removeTmpFile(String counterTmpFile) 
	throws LocalFileRemovalFaileException {
		Process process=null;
		Object[] params={counterTmpFile};
		String command=MessageFormat.format(TMP_FILE_DELETE_COMMAND, params);
		if(logger.isDebugEnabled())
			logger.debug("Inside removeTmpFile()->Deleting Tmp File:["+ counterTmpFile +
					"]  Delete command:["+command+"]");	
		try {
			process = new ProcessBuilder("bash","-c",command).start();
			process.waitFor();
		} catch (IOException e) {
			if(process!=null)
				process.destroy();
			throw new LocalFileRemovalFaileException("Unable to delete Local TmpFile",e);
		}// end try catch for deleting tmp file
		catch (InterruptedException e) {if(process!=null)
			process.destroy();
		throw new LocalFileRemovalFaileException("Unable to delete Local TmpFile",e);
		}

		if(logger.isDebugEnabled())
			logger.debug("Leaving  removeTmpFile()");	
	}

	@SuppressWarnings("rawtypes")
	private void sftpTmpFile(String tmpFile) 
	throws SftpFailedException {
		if(logger.isDebugEnabled())
			logger.debug("Inside sftpTmpFile()->SFTP Tmp File:["+ tmpFile+"]");	
		String destnPath=".";
		try {
			sftpChannel.put(tmpFile, destnPath, null, ChannelSftp.OVERWRITE);
		} catch (SftpException e) {
			throw new SftpFailedException("SFTP failed recheck connection", e);
		} 
		String newFileName=tmpFile.substring(0,(tmpFile.length()-3))+cdrPushToDSIConfig.getCdrPushLocalFileExtension();
		if(logger.isDebugEnabled())
			logger.debug("Inside sftpTmpFile()->Rename Tmp File new file name:["+ newFileName+"]");	
		try {
			sftpChannel.rename(tmpFile, newFileName);
		} catch (SftpException e) {
			//rename failed check if destn file name exists
			try {
				Vector vv=sftpChannel.ls(newFileName);
				if(vv!=null && !vv.isEmpty()){
					//dest file exists
					sftpChannel.rm(tmpFile);
				}else{
					throw new SftpFailedException("rename failed recheck connection ",e);
				}				
			} catch (SftpException e1) {
				throw new SftpFailedException("rename and removal failed recheck connection ",e1);
			}
		}
		if(logger.isDebugEnabled())
			logger.debug("Leave sftpTmpFile()");

	}

	private List<String> getTmpFileFromLocal() {
		if(logger.isDebugEnabled())
			logger.debug("Inside getTmpFileFromLocal()");
		List<String> localTmpList = new ArrayList<String>();
		String path=cdrPushToDSIConfig.getCdrPushLocalDirName();
		String filePrefix=cdrPushToDSIConfig.getCdrPushLocalFilePrefix();
		String locType=cdrPushToDSIConfig.getCdrPushLocalLocType();
		StringBuilder fileName=new StringBuilder();
		fileName.append(filePrefix);
		if(locType.equalsIgnoreCase(PRIMARY_LOC)){
			fileName.append(FILE_NAME_PRIMARY_LOC_APPENDER);
		}else if(locType.equalsIgnoreCase(SECONDARY_LOC)){
			fileName.append(FILE_NAME_SECONDARY_LOC_APPENDER);
		}
		String fileBegin=fileName.toString();
		String fileEnd=TMP_FILE_EXTENSION;
		if(logger.isDebugEnabled())
			logger.debug("Inside getTmpFileFromLocal() --> File begin patter:["+fileBegin+"]  file end pattern:["+fileEnd+"]");
		File file=new File(path);
		if(file.isDirectory()){
			String[] list=file.list();
			if(logger.isDebugEnabled())
				logger.debug("Inside getTmpFileFromLocal() --> File list size:["+list.length+"] ");
			for(int ii=0; ii<list.length; ii++){
				if(logger.isDebugEnabled())
					logger.debug("Inside getTmpFileFromLocal() --> checking if :["+list[ii]+"] is valid for current script");
				if(list[ii].endsWith(fileEnd) && 
						list[ii].startsWith(fileBegin)){
					localTmpList.add(list[ii]);
				}
			}
		}//end if
		if(logger.isDebugEnabled())
			logger.debug("Leaving getTmpFileFromLocal() --> Tmp file lsit:["+localTmpList+"] ");
		return localTmpList;
	}

	private void setDirectories() 
	throws DirectorySetFailedException {
		if(logger.isDebugEnabled())
			logger.debug("Inside setDirectories()");
		try {
			sftpChannel.lcd(cdrPushToDSIConfig.getCdrPushLocalDirName());
			if(logger.isDebugEnabled())
				logger.debug("Inside setDirectories()--> Local dir set to :["+cdrPushToDSIConfig.getCdrPushLocalDirName()+"]");
			sftpChannel.cd(cdrPushToDSIConfig.getDsiPushDir());
			if(logger.isDebugEnabled())
				logger.debug("Inside setDirectories()--> Remote dir set to :["+cdrPushToDSIConfig.getDsiPushDir()+"]");
		} catch (SftpException e) {
			throw new DirectorySetFailedException("Initial directory set failed exception",e);
		}
		if(logger.isDebugEnabled())
			logger.debug("Leaving setDirectories()");
	}

	private void createSftpChannel() 
	throws JSchException, SftpNotConnectedException {
		if(logger.isDebugEnabled())
			logger.debug("Inside createSftpChannel()");
		String host=cdrPushToDSIConfig.getDsiIP();
		String user=cdrPushToDSIConfig.getDsiUser();
		String password=cdrPushToDSIConfig.getDsiPassword();
		int port=cdrPushToDSIConfig.getDsiPort();

		JSch jsch=new JSch();
		session=jsch.getSession(user, host, port);
		// username and password will be given via UserInfo interface.
		UserInfo ui=new MyUserInfo(password);
		session.setUserInfo(ui);
		//connect session and check status
		session.connect();
		if(!session.isConnected()){
			throw new SftpNotConnectedException("Session not connected");
		}
		if(logger.isDebugEnabled())
			logger.debug("Inside createSftpChannel()->Session created");
		try {
			session.sendKeepAliveMsg();
		} catch (Exception e) {
			logger.error("Keep Alive on session Failed, IGNORED");
		}
		//create sftp channel and check status
		Channel channel=session.openChannel("sftp");
		channel.connect();
		if(!channel.isConnected()){
			throw new SftpNotConnectedException("Channel not connected");
		}
		sftpChannel=(ChannelSftp)channel;
		if(logger.isDebugEnabled())
			logger.debug("Inside createSftpChannel()->SFTP chhanel created");
		if(logger.isDebugEnabled())
			logger.debug("Leaving createSftpChannel()");
	}

	private void validateAndReadCounterFromFile() 
	throws FileNotFoundException, URISyntaxException, 
	InvalidLocalDirException, CounterFileIoFailedException {
		if(logger.isDebugEnabled())
			logger.debug("Inside validateAndReadCounterFromFile()");
		//getting primary location URL
		String cdrLoc=cdrPushToDSIConfig.getCdrPushLocalDirName();
		//creating file on CDR dir
		File dir = new File(cdrLoc);
		if(logger.isDebugEnabled())
			logger.debug("Inside validateAndReadCounterFromFile()-->Validating CDr location:["+cdrLoc+"]");
		if(!dir.isDirectory() || !(dir.exists())){
			throw new InvalidLocalDirException("cdr Location should be directory");
		}
		//creating/reading cntr file
		if(logger.isDebugEnabled())
			logger.debug("Inside validateAndReadCounterFromFile()-->reading counter file:["+COUNTER_FILE_NAME+"]");
		File file = new File(dir, COUNTER_FILE_NAME);
		this.counterFile = new RandomAccessFile(file, "rwd");
		index=readIndex();
		if(logger.isDebugEnabled())
			logger.debug("Leaving validateAndReadCounterFromFile()--., Index read:["+index+"]");
	}

	/**
	 * <pre>
	 * Reads the next index from the control file.
	 * If any IOException happens during this process, 
	 * calls the failureDetected() method.
	 * </pre>
	 * @return nextIndex from the file.
	 * @throws CounterFileIoFailedException 
	 */
	private int readIndex() throws CounterFileIoFailedException{
		int index = 0;
		try{
			this.counterFile.seek(0);
			index = (this.counterFile.length() >= 4) ? this.counterFile.readInt() : cdrPushToDSIConfig.getCdrStartIndex(); 
		}catch(IOException e){
			throw new CounterFileIoFailedException("Unable to read from Counter file",e);
		}
		//check if read index <start index
		if(index < cdrPushToDSIConfig.getCdrStartIndex())
			index=cdrPushToDSIConfig.getCdrStartIndex();	
		
		return index;
	}

	/**
	 * <pre>
	 * Writes the next index to the control file.
	 * If any IOException happens during this process, 
	 * calls the failureDetected() method.
	 * </pre>
	 * @param index Index to be written to the Control File.
	 * @throws CounterFileIoFailedException 
	 */
	private void writeIndex(int index) throws CounterFileIoFailedException{
		try{
			this.counterFile.seek(0);
			this.counterFile.setLength(0);
			this.counterFile.writeInt(index); 
		}catch(IOException e){
			throw new CounterFileIoFailedException("Unable to write to Counter file",e);
		}
	}




	private static class MyUserInfo implements UserInfo{
		public String getPassword(){ return passwd; }
		public boolean promptYesNo(String str){
			return true;
		}
		public MyUserInfo(String pwd){
			passwd=pwd;
		}
		String passwd;

		public String getPassphrase(){ return null; }
		public boolean promptPassphrase(String message){ return true; }
		public boolean promptPassword(String message){
			return true;
		}
		public void showMessage(String message){
		}
	}

}
