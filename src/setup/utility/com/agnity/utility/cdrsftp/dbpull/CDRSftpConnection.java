package com.agnity.utility.cdrsftp.dbpull;

import java.util.Vector;

import org.apache.log4j.Logger;

import com.agnity.utility.cdrsftp.dbpull.exceptions.CdrSftpFailedException;
import com.agnity.utility.cdrsftp.dbpull.exceptions.DirectorySetFailedException;
import com.agnity.utility.cdrsftp.dbpull.exceptions.SftpNotConnectedException;
import com.agnity.utility.cdrsftp.dbpull.utils.CDRPullConfig;
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
	 * @param cdrPullFromDbConfig
	 * @throws JSchException
	 * @throws SftpNotConnectedException
	 * @throws DirectorySetFailedException
	 */
	public void initialize(CDRPullConfig cdrPullFromDbConfig) 
		throws JSchException, SftpNotConnectedException, DirectorySetFailedException {
		if(logger.isDebugEnabled())
			logger.debug("Enter initialize()");
		String host=cdrPullFromDbConfig.getRemoteIp();
		String user=cdrPullFromDbConfig.getRemoteSftpUser();
		String password=cdrPullFromDbConfig.getRemoteSftpPassword();
		int port=cdrPullFromDbConfig.getRemoteSftpPort();
		sftpChannel = createSftpChannel(host,user,password,port);
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
		setDirectories(sftpChannel,cdrPullFromDbConfig.getLocalDirName(),cdrPullFromDbConfig.getRemoteSftpDir());
		if(logger.isDebugEnabled())
			logger.debug("Leave initialize()");
	}
		
	private ChannelSftp createSftpChannel(String host,String user,String password, int port) 
		throws JSchException, SftpNotConnectedException	{
		if(logger.isDebugEnabled())
			logger.debug("Inside createSftpChannel( host, user, password, port)");


		JSch jsch=new JSch();
		Session sftpSession=jsch.getSession(user, host, port);
		// username and password will be given via UserInfo interface.
		UserInfo ui=new SftpUserInfo(password);
		sftpSession.setUserInfo(ui);
		//connect session and check status
		sftpSession.connect();
		if(!sftpSession.isConnected()){
			throw new SftpNotConnectedException("Session not connected");
		}
		if(logger.isDebugEnabled())
			logger.debug("Inside createSftpChannel( host, user, password, port)->Session created");
		try {
			sftpSession.sendKeepAliveMsg();
		} catch (Exception e) {
			logger.error("Keep Alive on session Failed, IGNORED");
		}
		//create sftp channel and check status
		Channel channel=sftpSession.openChannel("sftp");
		channel.connect();
		if(!channel.isConnected()){
			throw new SftpNotConnectedException("Channel not connected");
		}
		ChannelSftp channelSftp=(ChannelSftp)channel;
		if(logger.isDebugEnabled())
			logger.debug("Leaving createSftpChannel( host, user, password, port)->SFTP chhanel created");
		
		if(logger.isDebugEnabled())
			logger.debug("Leave createSftpChannel( host, user, password, port) with channel");
		return channelSftp;

	}
	
	private void setDirectories(ChannelSftp channelSftp,String localDir,String remoteDir) 
	throws DirectorySetFailedException {
		if(logger.isDebugEnabled())
			logger.debug("Inside setDirectories()");
		try {
			channelSftp.lcd(localDir);
			if(logger.isDebugEnabled())
				logger.debug("Inside setDirectories()--> Local dir set to :["+localDir+"]");
			channelSftp.cd(remoteDir);
			if(logger.isDebugEnabled())
				logger.debug("Inside setDirectories()--> Remote dir set to :["+remoteDir+"]");
		} catch (SftpException e) {
			throw new DirectorySetFailedException("Initial directory set failed exception",e);
		}
		if(logger.isDebugEnabled())
			logger.debug("Leaving setDirectories()");
	}
	
	@SuppressWarnings("rawtypes")
	public void sftpFile(String localFileName, String remoteFileName) 
	throws CdrSftpFailedException {
		if(logger.isDebugEnabled())
			logger.debug("Inside sftpFile()->SFTP local File:["+ localFileName+"] destn Name::["+remoteFileName+"]");	
		String destnPath=".";
		try {
			sftpChannel.put(localFileName, destnPath, null, ChannelSftp.OVERWRITE);
		} catch (SftpException e) {
			throw new CdrSftpFailedException("SFTP failed recheck connection", e);
		} 
		if(logger.isDebugEnabled())
			logger.debug("Inside sftpFile()->Rename to destination Name");	
		try {
			sftpChannel.rename(localFileName, remoteFileName);
		} catch (SftpException e) {
			//rename failed check if destn file name exists
			try {
				Vector vv=sftpChannel.ls(remoteFileName);
				if(vv!=null && !vv.isEmpty()){
					if(logger.isDebugEnabled())
						logger.debug("Inside sftpFile()->Destination Already Exists");
					//dest file exists
					sftpChannel.rm(localFileName);
				}else{
					throw new CdrSftpFailedException("rename failed recheck connection ",e);
				}				
			} catch (SftpException e1) {
				throw new CdrSftpFailedException("rename and removal failed recheck connection ",e1);
			}
		}
		if(logger.isDebugEnabled())
			logger.debug("Leave sftpTmpFile()");

	}
	
	public void destroy(){
		if(sftpChannel!=null && sftpChannel.isConnected())
			sftpChannel.disconnect();
		if(session!=null && session.isConnected())
			session.disconnect();
	}

}
