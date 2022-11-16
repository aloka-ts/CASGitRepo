package com.agnity.utility.cdrsftp.fileSftp;

import java.util.Vector;

import org.apache.log4j.Logger;

import com.agnity.utility.cdrsftp.fileSftp.exceptions.CdrSftpFailedException;
import com.agnity.utility.cdrsftp.fileSftp.exceptions.DirectorySetFailedException;
import com.agnity.utility.cdrsftp.fileSftp.exceptions.SftpNotConnectedException;
import com.agnity.utility.cdrsftp.fileSftp.utils.CDRSftpConfig;
import com.agnity.utility.cdrsftp.fileSftp.utils.Constants;
import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;
import com.jcraft.jsch.UserInfo;

public class CDRSftpConnection {

	private static Logger logger = Logger.getLogger(CDRSftpConnection.class);

	private Session session;
	private ChannelSftp sftpChannel;
	private static int CONN_RETRIES = 3;
	private static long waitPeriod;
	private CDRFileSftp cdrFileSftp;

	private static String remoteHost;

	private static String remoteUser;

	private static String remotePassword;

	private static int remotePort;

	//user info in
	private static class SftpUserInfo implements UserInfo{
		public String getPassword(){ return passwd; }
		public boolean promptYesNo(String str){
			return true;
		}
		public SftpUserInfo(String pwd){
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

	/**
	 * Initializes sftp connection and set initial directories
	 * @param cdrFtpConfig
	 * @throws JSchException
	 * @throws SftpNotConnectedException
	 * @throws DirectorySetFailedException
	 */
	public void initialize(CDRSftpConfig cdrFtpConfig) 
			throws JSchException, SftpNotConnectedException, DirectorySetFailedException, Exception {
		if(logger.isDebugEnabled())
			logger.debug("Enter initialize()");
		cdrFileSftp = new CDRFileSftp();
		CONN_RETRIES = cdrFtpConfig.getRemoteSftpConnRetries();
		waitPeriod = cdrFtpConfig.getSftpConnWaitInterval()*1000;
		remoteHost=cdrFtpConfig.getRemoteIp();
		remoteUser=cdrFtpConfig.getRemoteSftpUser();
		remotePassword=cdrFtpConfig.getRemoteSftpPassword();
		remotePort=cdrFtpConfig.getRemoteSftpPort();

		sftpChannel = createSftpChannel();
		session = sftpChannel.getSession();
		try{
			session.sendKeepAliveMsg();
		}catch(Exception e){
			if(logger.isDebugEnabled())
				logger.debug("keep alive failed",e);
		}
		//channel created setting directories
		if(logger.isDebugEnabled())
			logger.debug("initialize() Setting local and remote  Directrories");
		setDirectories(cdrFtpConfig.getCdrLocalDir(),cdrFtpConfig.getRemoteSftpDir());
		if(logger.isDebugEnabled())
			logger.debug("Leave initialize()");
	}

	/**
	 * Creates Sftp Channel 
	 * @throws CdrSftpFailedException, JSchException
	 */
	private ChannelSftp createSftpChannel() 
			throws JSchException, SftpNotConnectedException, Exception	{
		if(logger.isDebugEnabled())
			logger.debug("Inside createSftpChannel()");


		JSch jsch=new JSch();
		Session sftpSession=jsch.getSession(remoteUser, remoteHost, remotePort);
		// username and password will be given via UserInfo interface.
		UserInfo ui=new SftpUserInfo(remotePassword);
		sftpSession.setUserInfo(ui);
		//connect session and check status
		connectSftpSession(sftpSession);

		if(logger.isDebugEnabled())
			logger.debug("Inside createSftpChannel->Session created");
		try {
			sftpSession.sendKeepAliveMsg();
		} catch (Exception e) {
			logger.error("Keep Alive on session Failed, IGNORED");
		}
		//create sftp channel and check status
		Channel channel=sftpSession.openChannel("sftp");
		connectSftpChannel(channel);

		ChannelSftp channelSftp=(ChannelSftp)channel;
		if(logger.isDebugEnabled())
			logger.debug("Leaving createSftpChannel()->SFTP channel created");

		return channelSftp;

	}

	public void setDirectories(String localDir,String remoteDir) 
			throws DirectorySetFailedException {
		if(logger.isDebugEnabled())
			logger.debug("Inside setDirectories()");
		try {
			sftpChannel.lcd(localDir);
			if(logger.isDebugEnabled())
				logger.debug("Inside setDirectories()--> Local dir set to :["+localDir+"]");
			sftpChannel.cd(remoteDir);
			if(logger.isDebugEnabled())
				logger.debug("Inside setDirectories()--> Remote dir set to :["+remoteDir+"]");
		} catch (SftpException e) {
			throw new DirectorySetFailedException("Initial directory set failed exception",e);
		}
		if(logger.isDebugEnabled())
			logger.debug("Leaving setDirectories()");
	}


	public void resetLocalDirectory(String localDir) throws DirectorySetFailedException{
		if(logger.isDebugEnabled())
			logger.debug("Inside resetLocalDirectory()");
		try{
			if(logger.isDebugEnabled())
				logger.debug("Inside resetLocalDirectory()--> Local dir reset to :["+localDir+"]");
			sftpChannel.lcd(localDir);
		}catch(SftpException e) {
			throw new DirectorySetFailedException("Initial directory set failed exception",e);
		}
	}

	/**
	 * Sftp files to a remote server 
	 */
	@SuppressWarnings("rawtypes")
	public boolean sftpFile(String localFileName, String remoteFileName) 
			throws CdrSftpFailedException {
		if(logger.isDebugEnabled())
			logger.debug("Inside sftpFile()->SFTP local File:["+ localFileName+"]" +"->SFTP Remote File:[" + remoteFileName+"]");	
		String destnPath=".";
		boolean status = false;
		try {
			sftpChannel.put(localFileName, destnPath, null, ChannelSftp.OVERWRITE);
			Vector vv=sftpChannel.ls(localFileName);
			if(vv!=null && !vv.isEmpty()){
				status=true;
			}else{
				throw new CdrSftpFailedException("Failed to sftp CDR File ");
			}	
		}catch (SftpException e) {
			throw new CdrSftpFailedException("SFTP failed recheck connection", e);
		} 

		if(remoteFileName!=null){
			if(logger.isDebugEnabled())
				logger.debug("Inside sftpFile()->Rename to destination Name");	
			status = false;
			try {
				sftpChannel.rename(localFileName, remoteFileName);
				status=true;
			}catch (SftpException e) {
				//rename failed check if destn file name exists
				try {
					Vector vv=sftpChannel.ls(remoteFileName);
					if(vv!=null && !vv.isEmpty()){
						if(logger.isDebugEnabled())
							logger.debug("Inside sftpFile()->Destination Already Exists");
						//dest file exists
						sftpChannel.rm(localFileName);
						status=true;
					}else{
						throw new CdrSftpFailedException("rename failed recheck connection ",e);
					}				
				} catch (SftpException e1) {
					throw new CdrSftpFailedException("rename and removal failed recheck connection ",e1);
				}
			}
		}

		if(logger.isDebugEnabled())
			logger.debug("Leave sftpTmpFile()");
		return status;

	}
	/**
	 * Reconnects Sftp Session according to the value set for connection retries.
	 * If retries value is -1, then keeps trying until session is successfully connected 
	 * and raises alarm when connection fails for the first time.
	 * Reconnection is attempted after a wait period which is configurable
	 * @throws JSchException, Exception
	 */
	private void connectSftpSession(Session sftpSession) throws JSchException, Exception{

		if(logger.isDebugEnabled())
			logger.debug("Inside connectSftpSession:");
		int retries=0;
		String errMsg = null;
		if(CONN_RETRIES!= -1){
			for(int i =0; i< CONN_RETRIES ; i++){
				try{
					retries++;
					sftpSession.connect();
					if(sftpSession.isConnected()){
						break;
					}else{
						threadSleep(waitPeriod);
						logger.error("Session not connected . Retyring");
					}
				}catch(Exception e){
					logger.error("Exception while connecting SftpSession" ,e);
					threadSleep(waitPeriod);
					errMsg = e.getMessage();
					if(retries == CONN_RETRIES){
						logger.error("Retries Completed Throwing Exception");
						throw e;
					}
				}

				if(retries==1){
					logger.error("Session not connected . Raising Alarm");
					cdrFileSftp.raiseAlarm(Constants.SFTP_CONNECTION_DOWN, Constants.SFTP_CONNECTION_DOWN_MESSAGE+" "+remoteHost
							+" "+errMsg, true);
				}
				
				logger.error("Retrying to connect Sftp Session");
			}
		}else{
			while(true){
				try{
					retries++;
					sftpSession.connect();
					if(sftpSession.isConnected()){
						break;
					}else{
						threadSleep(waitPeriod);
						logger.error("Session not connected Retrying");
					}
				}catch(Exception e){
					logger.error("Exception while connecting SftpSession" ,e);
					threadSleep(waitPeriod);
					errMsg = e.getMessage();
				}

				if(retries==1){
					logger.error("Session not connected . Raising Alarm");
					cdrFileSftp.raiseAlarm(Constants.SFTP_CONNECTION_DOWN, Constants.SFTP_CONNECTION_DOWN_MESSAGE+" "+remoteHost
							+" "+errMsg, true);
				}
				
				logger.error("Retrying to connect Sftp Session");
			}
		}
	}

	/**
	 * Reconnects Sftp Channel according to the value set for connection retries.
	 * If retries value is -1, then keeps trying until channel is successfully connected 
	 * and raises alarm when connection fails for the first time.
	 * Reconnection is attempted after a wait period which is configurable
	 * @throws JSchException, Exception
	 */
	private void connectSftpChannel(Channel channel) throws JSchException, Exception{

		if(logger.isDebugEnabled())
			logger.debug("Inside connectSftpChannel");
		int retries=0;
		String errMsg = null;
		if(CONN_RETRIES!= -1){
			for(int i =0; i< CONN_RETRIES ; i++){
				try{
					retries++;
					channel.connect();
					if(channel.isConnected()){
						cdrFileSftp.raiseAlarm(Constants.SFTP_CONNECTION_UP, Constants.SFTP_CONNECTION_UP_MESSAGE, true);
						break;
					}else{
						threadSleep(waitPeriod);
						logger.error("Channel not connected Retrying");
					}
				}catch(Exception e){
					logger.error("Exception while connecting SftpChannel" ,e);
					threadSleep(waitPeriod);
					errMsg = e.getMessage();
					if(retries == CONN_RETRIES){
						logger.error("Retries Completed Throwing Exception");
						throw e;
					}
				}

				if(retries==1){
					logger.error("Channel not connected . Raising Alarm");
					cdrFileSftp.raiseAlarm(Constants.SFTP_CONNECTION_DOWN, Constants.SFTP_CONNECTION_DOWN_MESSAGE+" "+remoteHost
							+" "+errMsg, true);
				}
				
				logger.error("Retrying to connect Sftp Channel");
			}
		}else{
			while(true){
				try{
					retries++;
					channel.connect();
					if(channel.isConnected()){
						cdrFileSftp.raiseAlarm(Constants.SFTP_CONNECTION_UP, Constants.SFTP_CONNECTION_UP_MESSAGE, true);
						break;
					}else{
						threadSleep(waitPeriod);
						logger.error("Channel not connected Retrying");
					}
				}catch(Exception e){
					logger.error("Exception while connecting SftpChannel" ,e);
					threadSleep(waitPeriod);
					errMsg = e.getMessage();
				}

				if(retries==1){
					logger.error("Channel not connected . Raising Alarm");
					cdrFileSftp.raiseAlarm(Constants.SFTP_CONNECTION_DOWN, Constants.SFTP_CONNECTION_DOWN_MESSAGE+" "+remoteHost
							+" "+errMsg, true);
				}
				
				logger.error("Retrying to connect Sftp Channel");
			}
		}
	}

	/**
	 * Resets the Connection 
	 * @throws JSchException
	 */
	public void resetConnection() throws JSchException,Exception {

		logger.error("Connection Broken Resetting Connection");
		this.destroy();
		sftpChannel = createSftpChannel();
		session = sftpChannel.getSession();
		try{
			session.sendKeepAliveMsg();
		}catch(Exception e){
			if(logger.isDebugEnabled())
				logger.debug("keep alive failed",e);
		}
	}


	public void destroy(){
		if(sftpChannel!=null && sftpChannel.isConnected()){
			sftpChannel.disconnect();
			logger.error("Disconnect Sftp Channel");
		}
		if(session!=null && session.isConnected()){
			session.disconnect();
			logger.error("Disconnect Sftp Session");
		}
	}

	private void threadSleep(long milliseconds){

		try {
			Thread.sleep(milliseconds);
		} catch (InterruptedException e1) {
			//do nothing
		}

	}

}
