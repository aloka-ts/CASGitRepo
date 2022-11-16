/**
 * 
 */
package com.baypackets.ase.ra.telnetssh.ls.library;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

import com.baypackets.ase.ra.telnetssh.LsRaAlarmManager;
import com.baypackets.ase.ra.telnetssh.configmanager.CommonLsConfig;
import com.baypackets.ase.ra.telnetssh.configmanager.LS;
import com.baypackets.ase.ra.telnetssh.event.LsResourceEvent;
import com.baypackets.ase.ra.telnetssh.ls.CommandStatus;
import com.baypackets.ase.ra.telnetssh.ls.LsManager;
import com.baypackets.ase.ra.telnetssh.ls.LsResult;
import com.baypackets.ase.ra.telnetssh.ls.LsSession;
import com.baypackets.ase.ra.telnetssh.utils.LsStatus;
import com.baypackets.ase.ra.telnetssh.utils.RaProperties;
import com.baypackets.ase.resource.ResourceException;
import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.UserInfo;

/**
 * The Class SshSession.
 * extends LSSession
 * manage ssh session with LS
 * uses Jsch Library
 *
 * @author saneja
 */
public class SshSession extends LsSession {

	/** The Constant logger. */
	private static final Logger logger=Logger.getLogger(SshSession.class);

	/** The sshsession ref. */
	private Session sshsessionRef;
	
	private SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy hh:mm aaa");


	/**
	 * Instantiates a new ssh session.
	 */
	public SshSession() {
		super();
	}

	/* (non-Javadoc)
	 * @see com.baypackets.ase.ra.telnetssh.ls.LsSession#executeCommand(java.lang.String)
	 */
	@Override
	public List<String> executeCommand(String command) {
		boolean isDebugEnabled=logger.isDebugEnabled();
		if(isDebugEnabled)
			logger.debug("Enter executeCommand for LsID:::"+ 
					(this.getLs()).getLsId() + "  Command::"+command);
		Channel channel=null;
		InputStream inputStream=null;
		InputStream errorStream=null;
		CommonLsConfig commonLsConfig=LsManager.getInstance().getCommonLsConfig();
		int attemptsAllowed=commonLsConfig.getReAttempt();
		int timeout=commonLsConfig.getNoResponseTimer();
		int currentAttempt=0;
		long executeStartTime=0;
		String result=null;
		//saneja@bug 7085[
		String delim=LsManager.getInstance().getRaProperties().getOutputDelim();
		String respSeperator=LsManager.getInstance().getRaProperties().getRespSeperator();
		int delimTimer=LsManager.getInstance().getRaProperties().getDelimRespTimer();
		boolean isFirstResp=true;
		boolean isFinalResp=false;
		List<String> responses=null;

		//]saneja@bug 7085

		//added to work with  both asynch behavior and synch behavior[
		boolean isReturn=false;
		synchronized (isAvailableForExecution) {
			if(isAvailableForExecution){
				isAvailableForExecution=false;
			}else{
				isReturn=true;
			}
		}
		//when used with aysnch synch will fail and ask for clearing of LS Queue
		if(isReturn)
			return null;
		//]added to work with asynch behavior

		while(currentAttempt < attemptsAllowed){
			currentAttempt++;
			try {
				channel=sshsessionRef.openChannel("exec");
				((ChannelExec)channel).setCommand(command);
				channel.setInputStream(null);
				inputStream=channel.getInputStream();
				((ChannelExec)channel).setErrStream(System.err);
				errorStream=((ChannelExec)channel).getErrStream();
				channel.connect();
				executeStartTime=System.currentTimeMillis();
				if(isDebugEnabled)
					logger.debug("Command sent at "+executeStartTime +" on lsid::"+(this.getLs()).getLsId() );
				byte[] tmp=new byte[1024];
				result = "";
				while( true ){
					while(inputStream.available()>0){
						int i=inputStream.read(tmp, 0, 1024);
						if(i<0)
							break;
						result+=new String(tmp, 0, i);
						//saneja@bug 7085[
						if(isFirstResp && result.contains(respSeperator)){
							if(isDebugEnabled)
								logger.debug("First Intermediate res Found::"+result);
							isFirstResp=false;
						}
						if(result.contains(delim)){
							if(isDebugEnabled)
								logger.debug("Final response recieved");
							result=result.substring(0, result.indexOf(delim));
							isFinalResp=true;
							break;
						}
						//saneja@bug 7085
					}

					//saneja@bug 7085[
					//check if exit inner loop on final resp
					if(isFinalResp){
						if(isDebugEnabled)
							logger.debug("Response recieved leaving");
						break;
					}
					//]saneja@bug 7085

					//check if exit inner loop on channel close
					if(channel.isClosed()){
						if(isDebugEnabled)
							logger.debug("Channel closed leaving");
						//saneja@bug 7085[
						result=null;
						//]saneja@bug 7085
						break;
					}

					//saneja@bug 7085[
					if(isFirstResp && ((System.currentTimeMillis() - executeStartTime)/1000) > timeout){
						if(isDebugEnabled)
							logger.debug("TimeOut happened");
						result=null;
						break;
					}

					if(((System.currentTimeMillis() - executeStartTime)/1000) > delimTimer){
						if(isDebugEnabled)
							logger.debug("Delimited response not recieved---> Timeout");
						result=null;
						break;
					}

					//]saneja@bug 7085
				}
				//check if result found settin result in response list
				if(result!=null && !(result.equals(""))){
					if(isDebugEnabled)
						logger.debug("Result matched from inputStream "+result +" on lsid::"+(this.getLs()).getLsId());
					//saneja@bug 7085[
					String[] respArray=result.split(respSeperator);
					responses=Arrays.asList(respArray);
					//]saneja@bug 7085
					break;
				}
				//				else{//check if command error on error stream
				//					while(errorStream.available()>0){
				//						int i=errorStream.read(tmp, 0, 1024);
				//						if(i<0)break;
				//						result+=new String(tmp, 0, i);
				//					}
				//					if(result!=null && !(result.equals(""))){
				//						if(isDebugEnabled)
				//							logger.debug("Result matched from errorStream "+result +" on lsid::"+(this.getLs()).getLsId());
				//						break;
				//					}else
				//						logger.error("Attempt:::" + currentAttempt + " Failed on lsid::"+(this.getLs()).getLsId() );
				//				}
			} catch (JSchException e) {
				result=null;
				responses=null;
				logger.error("Jsch Exception while executing command  on lsid::"+(this.getLs()).getLsId(),e);
			} catch (IOException e) {
				result=null;
				responses=null;
				logger.error("IO Exception while executing command  on lsid::"+(this.getLs()).getLsId(),e);
			}catch (Exception e) {
				result=null;
				responses=null;
				logger.error("Exception while executing command  on lsid::"+(this.getLs()).getLsId(),e);
			}finally{
				if(inputStream!=null){
					try {
						if(inputStream!=null)
							inputStream.close();
					} catch (IOException e) {
						logger.error("Inside finally->IO Exception while Closing input stream--Ignored");
					}
				}
				if(errorStream!=null){
					try {
						if(errorStream!=null)
							errorStream.close();
					} catch (IOException e) {
						logger.error("Inside finally->IO Exception while Closing Error stream--Ignored");
					}
				}
				if(channel!=null)
					channel.disconnect();
			}
		}
		//saneja@bug 7085[
		//		if(result!=null)
		//			result=result.trim();
		if(isDebugEnabled)
			logger.debug("Leaving executeCommand for LsID:::"+ 
					(this.getLs()).getLsId() + "  with Result"+responses);


		//added for working with both asynch and synch behavior together[
		markAvailable();
		//]for working on both synch and asynch

		return responses;
		//]saneja@bug 7085
	}

	/* (non-Javadoc)
	 * @see com.baypackets.ase.ra.telnetssh.ls.LsSession#startSession(com.baypackets.ase.ra.telnetssh.configmanager.LS)
	 */
	@Override
	public void startSession() {
		boolean isDebugEnabled=logger.isDebugEnabled();
		LS ls=this.getLs();
		if(ls==null){
			logger.error("ls is null in LS Session returning");
			return;
		}
		int lsId = ls.getLsId();
		if(isDebugEnabled)
			logger.debug("Enter SSH startSession for LsID:::"+ lsId);
		JSch jsch=new JSch();  
		String user=ls.getLsUser();
		String ip=ls.getLsIP();
		int port=ls.getLsPort();
		String pwd=ls.getLsPassword();
		//saneja@bug 7085 executiong suppression [
		RaProperties raProperties=LsManager.getInstance().getRaProperties();
		String suppressCommand=raProperties.getSuppressCommand();
		int connectTimeout=raProperties.getConnectTimeout()*1000;
		//CR UAT-1219 Changes
		long waitToSuppressCommand = raProperties.getWaitToSuppressCommand() * 1000;
		int suppressCommandTimeOut = raProperties.getSuppressCommandTimeout() * 1000;
		String suppressCommandDelim = raProperties.getSuppressCommandDelim();

		//]closed saneja@bug 7085
		try {
			sshsessionRef = jsch.getSession(user, ip, port);
			UserInfo ui=new SshUserInfo(pwd);
			sshsessionRef.setUserInfo(ui);
			sshsessionRef.setTimeout(connectTimeout);
			sshsessionRef.connect();
			if(isDebugEnabled)
				logger.debug("Connect sent on lsId::"+lsId);
			//CR UAT-1219 Changes
			if (isDebugEnabled)
				logger.debug("verifyLoginAndSuppress()   Sleeping before running the suppress Command for LsID:"
						+ (this.getLs()).getLsId());
			Thread.sleep(waitToSuppressCommand);
			if (isDebugEnabled)
				logger.debug("verifyLoginAndSuppress()   Writing suppress Command for LsID:"
						+ (this.getLs()).getLsId());
			//saneja @bugs 7085 send suppression command[
			//CR UAT -1219 Changes
			sshsessionRef.setTimeout(suppressCommandTimeOut);
			boolean success=suppress(suppressCommand);
			sshsessionRef.setTimeout(connectTimeout);
			if(success){
				sshsessionRef.sendKeepAliveMsg();
				this.setLsStatus(LsStatus.LS_UP);
				markAvailable();
			}else{
				int failedReLogins = ls.getFailedReLogins();
				ls.setFailedReLogins(++failedReLogins);
				logger.error("login failed LsId::::"+lsId+"Failed ReLogins are::::"+failedReLogins);
				throw (new Exception("SUPPRESS FAILURE"));
			}

			//]closed saneja @bugs 7085
			logger.error("LS connected lsiD"+lsId);
			System.out.println("LS_STATUS: " + dateFormat.format(Calendar.getInstance().getTime()) + " LS (Id " + lsId + " ,IP " + ip + "): CONNECTED ");
			if(this.isFailAlarmSent()){
				this.setFailAlarmSent(false);
				String alarmMsg="LsId:["+lsId+"]";
				LsRaAlarmManager.getInstance().raiseAlarm(LsRaAlarmManager.CONNECTION_RESTORED, lsId, alarmMsg);
			}
			try {
				LsManager.getInstance().getLsResourceAdaptor().deliverEvent(new LsResourceEvent("PEER_UP_EVENT LsId["+lsId+"]", LsResourceEvent.PEER_UP, null));
			} catch (Exception e1) {
				logger.error("Error Sending peer up event on connection success from start session lsId::"+lsId,e1);
			}
		} catch (JSchException e) {
			logger.error("JSchException creating SSH session with LsId::"+lsId,e);
			this.setLsStatus(LsStatus.LS_DOWN);
			if(sshsessionRef!=null)
				sshsessionRef.disconnect();
			try {
				LsManager.getInstance().getLsResourceAdaptor().deliverEvent(new LsResourceEvent("PEER_DOWN_EVENT LsId["+lsId+"]", LsResourceEvent.PEER_DOWN, null));
			} catch (Exception e1) {
				logger.error("Error Sending peer down event on connection fail from start session lsId::"+lsId,e1);
			}
			if(!this.isFailAlarmSent()){
				this.setFailAlarmSent(true);
				String alarmMsg="LsId:["+lsId+"]";
				System.out.println("LS_STATUS: " + dateFormat.format(Calendar.getInstance().getTime()) + " LS (Id " + lsId + " ,IP " + ip + "): DISCONNECTED ");
				LsRaAlarmManager.getInstance().raiseAlarm(LsRaAlarmManager.CONNECTION_FAILED, lsId, alarmMsg);
			}
			this.setLsDownTimeStamp(new Date());
			if (!ls.isUnderRecovery()){
				LsManager.getInstance().getSessionRecoveryTask().addLsSession(this);	
			}else{
				LsManager.getInstance().getSessionPermRecoveryTask().addLsSession(this);
			}
			
		} catch (Exception e) {
			logger.error("Exception creating SSH session with LsId::"+lsId,e);
			this.setLsStatus(LsStatus.LS_DOWN);
			if(sshsessionRef!=null)
				sshsessionRef.disconnect();
			try {
				LsManager.getInstance().getLsResourceAdaptor().deliverEvent(new LsResourceEvent("PEER_DOWN_EVENT LsId["+lsId+"]", LsResourceEvent.PEER_DOWN, null));
			} catch (Exception e1) {
				logger.error("Error Sending peer down event on connection fail from start session lsId::"+lsId,e1);
			}
			if(!this.isFailAlarmSent()){
				this.setFailAlarmSent(true);
				String alarmMsg="LsId:["+lsId+"]";
				System.out.println("LS_STATUS: " + dateFormat.format(Calendar.getInstance().getTime()) + " LS (Id " + lsId + " ,IP " + ip + "): DISCONNECTED ");
				LsRaAlarmManager.getInstance().raiseAlarm(LsRaAlarmManager.CONNECTION_FAILED, lsId, alarmMsg);
			}
			this.setLsDownTimeStamp(new Date());
			if (!ls.isUnderRecovery()){
				LsManager.getInstance().getSessionRecoveryTask().addLsSession(this);	
			}else{
				LsManager.getInstance().getSessionPermRecoveryTask().addLsSession(this);
			}
		}

		if(isDebugEnabled)
			logger.debug("Leave startSession for LsID:::"+ lsId);

	}

	/**
	 * Suppresses the LS messages recieved in between commands.
	 *
	 * @param suppressCommand the suppress command
	 * @return boolean status of suppress
	 */
	private boolean suppress(String suppressCommand) {
		boolean isDebugEnabled=logger.isDebugEnabled();
		if(isDebugEnabled)
			logger.debug("Enter suppress for LsID:::"+ 
					(this.getLs()).getLsId() + "  Command::"+suppressCommand);
		Channel channel=null;
		InputStream inputStream=null;
		InputStream errorStream=null;

		String result=null;
		try {
			channel=sshsessionRef.openChannel("exec");
			((ChannelExec)channel).setCommand(suppressCommand);
			channel.setInputStream(null);
			inputStream=channel.getInputStream();
			((ChannelExec)channel).setErrStream(System.err);
			errorStream=((ChannelExec)channel).getErrStream();
			channel.connect();
			if(isDebugEnabled)
				logger.debug("Command sent at "+System.currentTimeMillis() +" on lsid::"+(this.getLs()).getLsId() );
			byte[] tmp=new byte[1024];
			result = "";
			while(true  ){
				while(inputStream.available()>0){
					int i=inputStream.read(tmp, 0, 1024);
					if(i<0)break;
					result+=new String(tmp, 0, i);
				}
				if(channel.isClosed()){
					break;
				}
			}
		} catch (JSchException e) {
			result=null;
			logger.error("Jsch Exception while executing command  on lsid::"+(this.getLs()).getLsId(),e);
			return false;
		}catch (IOException e) {
			result=null;
			logger.error("IOException while executing command  on lsid::"+(this.getLs()).getLsId(),e);
			return false;
		}finally{
			if(inputStream!=null){
				try {
					inputStream.close();
				} catch (IOException e) {
					logger.error("Inside finally->IO Exception while Closing input stream--Ignored");
				}
			}
			if(errorStream!=null){
				try {
					errorStream.close();
				} catch (IOException e) {
					logger.error("Inside finally->IO Exception while Closing Error stream--Ignored");
				}
			}
			if(channel!=null)
				channel.disconnect();
		}

		return true;
	}

	/* (non-Javadoc)
	 * @see com.baypackets.ase.ra.telnetssh.ls.LsSession#stopSession()
	 */
	@Override
	public void stopSession() {
		boolean isDebugEnabled=logger.isDebugEnabled();
		if(isDebugEnabled)
			logger.debug("Enter stopSession for LsID:::"+ (this.getLs()).getLsId());
		LS ls=this.getLs();
		if(sshsessionRef!=null)
			sshsessionRef.disconnect();
		this.setLsStatus(LsStatus.LS_DOWN);
		System.out.println("LS_STATUS: " + dateFormat.format(Calendar.getInstance().getTime()) + " LS (Id " + this.getLs().getLsId() + " ,IP " + this.getLs().getLsIP() + "): DISCONNECTED ");
		try {
			LsManager.getInstance().getLsResourceAdaptor().deliverEvent(new LsResourceEvent("PEER_DOWN_EVENT LsId["+ls.getLsId()+"]", LsResourceEvent.PEER_DOWN, null));
		} catch (ResourceException e1) {
			logger.error("Error Sending peer down event on connection stop from stop session lsId::"+ls.getLsId(),e1);
		}
		
		sshsessionRef=null;
		//System.gc();
		if(isDebugEnabled)
			logger.debug("Leave stopSession for LsID:::"+ ls.getLsId());

	}


	//inner class for ssh session
	/**
	 * The Class SshUserInfo.
	 * Inner class to provide 
	 * user password info for 
	 * ssh session
	 * 
	 */
	class SshUserInfo implements UserInfo{

		/* (non-Javadoc)
		 * @see com.jcraft.jsch.UserInfo#getPassword()
		 */
		public String getPassword(){ return passwd; }

		/* (non-Javadoc)
		 * @see com.jcraft.jsch.UserInfo#promptYesNo(java.lang.String)
		 */
		public boolean promptYesNo(String str){
			return true;
		}

		/**
		 * Instantiates a new ssh user info.
		 *
		 * @param pwd the pwd
		 */
		public SshUserInfo(String pwd){
			passwd=pwd;
		}

		/** The passwd. */
		String passwd;

		/* (non-Javadoc)
		 * @see com.jcraft.jsch.UserInfo#getPassphrase()
		 */
		public String getPassphrase(){ return null; }

		/* (non-Javadoc)
		 * @see com.jcraft.jsch.UserInfo#promptPassphrase(java.lang.String)
		 */
		public boolean promptPassphrase(String message){ return true; }

		/* (non-Javadoc)
		 * @see com.jcraft.jsch.UserInfo#promptPassword(java.lang.String)
		 */
		public boolean promptPassword(String message){
			return true;
		}

		/* (non-Javadoc)
		 * @see com.jcraft.jsch.UserInfo#showMessage(java.lang.String)
		 */
		public void showMessage(String message){
		}
	}


	/**
	 * sends command to ls
	 * check if ls is avilable for execvution sends command
	 * else returns
	 *  (non-Javadoc)
	 * @see com.baypackets.ase.ra.telnetssh.ls.LsSession#sendCommand(java.lang.String)
	 */
	@Override
	public CommandStatus sendCommand(String command) {
		boolean isDebugEnabled=logger.isDebugEnabled();
		if(isDebugEnabled)
			logger.debug("Enter sendCommand for LsID:::"+ 
					(this.getLs()).getLsId() + "  Command::"+command);
		CommandStatus status=CommandStatus.SEND_FAIL;
		InputStream inputStream=null;
		InputStream errorStream=null;
		synchronized (isAvailableForExecution) {
			if(isAvailableForExecution){
				try {
					if(channel!=null){
						channel.disconnect();
						channel=null;
					}
					channel=sshsessionRef.openChannel("exec");
					((ChannelExec)channel).setCommand(command);
					channel.setInputStream(null);
					inputStream=channel.getInputStream();
					((ChannelExec)channel).setErrStream(System.err);
					errorStream=((ChannelExec)channel).getErrStream();
					channel.connect();
					sentTime=System.currentTimeMillis();
					status=CommandStatus.SEND_SUCCESS;
					isAvailableForExecution=false;
					if(isDebugEnabled)
						logger.debug("Command sent at "+sentTime +" on lsid::"+(this.getLs()).getLsId() );
				} catch (JSchException e) {
					logger.error("Jsch Exception while executing command  on lsid::"+(this.getLs()).getLsId(),e);
					status=CommandStatus.SEND_ERROR;
					cleanup(inputStream,errorStream);
				} catch (IOException e) {
					logger.error("IO Exception while executing command  on lsid::"+(this.getLs()).getLsId(),e);
					status=CommandStatus.SEND_ERROR;
					cleanup(inputStream,errorStream);
				}catch (Exception e) {
					logger.error("Exception while executing command  on lsid::"+(this.getLs()).getLsId(),e);
					status=CommandStatus.SEND_ERROR;
					cleanup(inputStream,errorStream);
				}
			}
		}
		if(isDebugEnabled)
			logger.debug("Leaving sendCommand for LsID:::"+ 
					(this.getLs()).getLsId() + "  with status"+status);
		return status;
	}

	/**
	 * @param inputStream
	 * @param errorStream
	 */
	private void cleanup(InputStream inputStream, InputStream errorStream) {
		if(inputStream!=null){
			try {
				if(inputStream!=null)
					inputStream.close();
			} catch (IOException e) {
				logger.error("Inside finally->IO Exception while Closing input stream--Ignored");
			}
		}
		if(errorStream!=null){
			try {
				if(errorStream!=null)
					errorStream.close();
			} catch (IOException e) {
				logger.error("Inside finally->IO Exception while Closing Error stream--Ignored");
			}
		}
		if(channel!=null){
			channel.disconnect();
			channel=null;	
		}
	}



	/* (non-Javadoc)
	 * @see com.baypackets.ase.ra.telnetssh.ls.LsSession#readResponse()
	 */
	@Override
	public LsResult readResponse(boolean isRequest) {

		boolean isDebugEnabled=logger.isDebugEnabled();
		if(isDebugEnabled)
			logger.debug("Enter readResponse for LsID:::"+ 
					(this.getLs()).getLsId() );
		InputStream inputStream=null;
		InputStream errorStream=null;
		CommonLsConfig commonLsConfig=LsManager.getInstance().getCommonLsConfig();
		int timeout=commonLsConfig.getNoResponseTimer();
		long executeStartTime=0;
		String result=null;
		//saneja@bug 7085[
		String delim=LsManager.getInstance().getRaProperties().getOutputDelim();
		String respSeperator=LsManager.getInstance().getRaProperties().getRespSeperator();
		int delimTimer=LsManager.getInstance().getRaProperties().getDelimRespTimer();
		List<String> responses=null;

		//]saneja@bug 7085

		//added to return if called with avaialble LS[
		boolean isReturn=false;
		synchronized (isAvailableForExecution) {
			if(isAvailableForExecution){
				isReturn=true;
			}
		}
		//returns if ls is available
		if(isReturn)
			return null;
		//]
		try {
			inputStream=channel.getInputStream();
			errorStream=((ChannelExec)channel).getErrStream();
			executeStartTime=System.currentTimeMillis();
			if(isDebugEnabled)
				logger.debug("Read start at "+executeStartTime +" on lsid::"+(this.getLs()).getLsId() );
			byte[] tmp=new byte[1024];
			result = "";
			while( true ){
				while(inputStream.available()>0){
					int i=inputStream.read(tmp, 0, 1024);
					if(i<0)
						break;
					result+=new String(tmp, 0, i);
					//saneja@bug 7085[
					//Commented this code as in SSH only the command's output comes without any prompt
//					if(isFirstResp && result.contains(respSeperator)){
//						if(isDebugEnabled)
//							logger.debug("First Intermediate res Found::"+result +" on lsid::"+(this.getLs()).getLsId());
//						isFirstResp=false;
//					}
//					if(result.contains(delim)){
//						if(isDebugEnabled)
//							logger.debug("Final response recieved  on lsid::"+(this.getLs()).getLsId());
//						result=result.substring(0, result.indexOf(delim));
//						isFinalResp=true;
//						break;
//					}
					//saneja@bug 7085
				}
				if(isDebugEnabled)
					logger.debug("Response received "+result +" on lsid::"+(this.getLs()).getLsId() );

				//saneja@bug 7085[
				//check if exit inner loop on final resp
//				if(isFinalResp){
//					if(isDebugEnabled)
//						logger.debug("Response recieved leaving  on lsid::"+(this.getLs()).getLsId());
//					break;
//				}
				//]saneja@bug 7085

				//check if exit inner loop on channel close
				if(channel.isClosed()){
					if(isDebugEnabled)
						logger.debug("Channel closed leaving");
					break;
				}

//				//saneja@bug 7085[
//				//check if first resp timer expired
//				if(isFirstResp && ((System.currentTimeMillis() - executeStartTime)/1000) > timeout){
//					if(isDebugEnabled)
//						logger.debug("TimeOut happened  on lsid::"+(this.getLs()).getLsId());
//					result=null;
//					break;
//				}

				//check if complete resp timer expired
				//Keeping this here as there could be cases where channel is not closed and stream is also
				//not available, so from safety standpoint preventing thread to get stuck.
				if(((System.currentTimeMillis() - executeStartTime)/1000) > delimTimer){
					if(isDebugEnabled)
						logger.debug("response not recieved---> Timeout on lsid::"+(this.getLs()).getLsId());
//					result=null;
					break;
				}

				//]saneja@bug 7085
			}
			//check if result found settin result in response list
			if(result!=null && !(result.equals(""))){
				if(isDebugEnabled)
					logger.debug("Result matched from inputStream "+result +" on lsid::"+(this.getLs()).getLsId());
				//saneja@bug 7085[
				responses = new ArrayList<String>();
				responses.add(result);
//				String[] respArray=result.split(respSeperator);
//				responses=Arrays.asList(respArray);
				//]saneja@bug 7085
			}
		} catch (IOException e) {
			result=null;
			responses=null;
			logger.error("IO Exception while reading resp on lsid::"+(this.getLs()).getLsId(),e);
		}catch (Exception e) {
			result=null;
			responses=null;
			logger.error("Exception while reading resp on lsid::"+(this.getLs()).getLsId(),e);
		}finally{
			if(inputStream!=null){
				try {
					if(inputStream!=null)
						inputStream.close();
				} catch (IOException e) {
					logger.error("Inside finally->IO Exception while Closing input stream--Ignored");
				}
			}
			if(errorStream!=null){
				try {
					if(errorStream!=null)
						errorStream.close();
				} catch (IOException e) {
					logger.error("Inside finally->IO Exception while Closing Error stream--Ignored");
				}
			}
			if(channel!=null)
				channel.disconnect();
			channel=null;
		}

		//saneja@bug 7085[
		//		if(result!=null)
		//			result=result.trim();
		boolean recover=  !(sshsessionRef.isConnected());

		if (recover || responses == null){
			String command = LsManager.getInstance().getRaProperties().getKeepAliveCommand();
			this.markAvailable();
			CommandStatus status = this.sendCommand(command);
			int lsId = this.getLs().getLsId();
			if(status.equals(CommandStatus.SEND_ERROR)){
				logger.error("SshSession(keepAliveCommand) On timeout()-->Error in send command " + command + " lsId::" + lsId);
			}else if(status.equals(CommandStatus.SEND_FAIL)){  //if fail due to busy LS
				logger.error("SshSession(keepAliveCommand)-->Command already sent, LS is not available for execution");
			}else if(status.equals(CommandStatus.SEND_SUCCESS)){
				logger.error("SshSession(keepAliveCommand)-->success");
				return(this.readResponseKeepAliveCommand());
			}
		}
		//]for working on both synch and asynch

		if(isDebugEnabled)
			logger.debug("Leaving readresponse for LsID:::"+ 
					(this.getLs()).getLsId() + "  with Result"+responses+
					"  and recoveryStatus:::"+recover);
		
		
		return (new LsResult(recover, responses));
		//]saneja@bug 7085

	}

	
	/* (non-Javadoc)
	 * @see com.baypackets.ase.ra.telnetssh.ls.LsSession#readResponse()
	 */
	@Override
	public LsResult readResponseKeepAliveCommand() {

		boolean isDebugEnabled=logger.isDebugEnabled();
		if(isDebugEnabled)
			logger.debug("Enter readResponse for LsID:::"+ 
					(this.getLs()).getLsId() );
		InputStream inputStream=null;
		InputStream errorStream=null;
		CommonLsConfig commonLsConfig=LsManager.getInstance().getCommonLsConfig();
		int timeout=commonLsConfig.getNoResponseTimer();
		long executeStartTime=0;
		String result=null;
		//saneja@bug 7085[
		String delim=LsManager.getInstance().getRaProperties().getOutputDelim();
		String respSeperator=LsManager.getInstance().getRaProperties().getRespSeperator();
		int delimTimer=LsManager.getInstance().getRaProperties().getDelimRespTimer();
		List<String> responses=null;

		//]saneja@bug 7085

		//added to return if called with avaialble LS[
		boolean isReturn=false;
		synchronized (isAvailableForExecution) {
			if(isAvailableForExecution){
				isReturn=true;
			}
		}
		//returns if ls is available
		if(isReturn)
			return null;
		//]
		try {
			inputStream=channel.getInputStream();
			errorStream=((ChannelExec)channel).getErrStream();
			executeStartTime=System.currentTimeMillis();
			if(isDebugEnabled)
				logger.debug("Read start at "+executeStartTime +" on lsid::"+(this.getLs()).getLsId() );
			byte[] tmp=new byte[1024];
			result = "";
			while( true ){
				while(inputStream.available()>0){
					int i=inputStream.read(tmp, 0, 1024);
					if(i<0)
						break;
					result+=new String(tmp, 0, i);
					//saneja@bug 7085[
//					if(isFirstResp && result.contains(respSeperator)){
//						if(isDebugEnabled)
//							logger.debug("First Intermediate res Found::"+result +" on lsid::"+(this.getLs()).getLsId());
//						isFirstResp=false;
//					}
//					if(result.contains(delim)){
//						if(isDebugEnabled)
//							logger.debug("Final response recieved  on lsid::"+(this.getLs()).getLsId());
//						result=result.substring(0, result.indexOf(delim));
//						isFinalResp=true;
//						break;
//					}
					//saneja@bug 7085
				}
				if(isDebugEnabled)
					logger.debug("Response recieved leaving  on lsid::"+(this.getLs()).getLsId());
				//saneja@bug 7085[
				//check if exit inner loop on final resp
//				if(isFinalResp){
//					if(isDebugEnabled)
//						logger.debug("Response recieved leaving  on lsid::"+(this.getLs()).getLsId());
//					break;
//				}
				//]saneja@bug 7085

				//check if exit inner loop on channel close
				if(channel.isClosed()){
					if(isDebugEnabled)
						logger.debug("Channel closed leaving");
					//saneja@bug 7085[
//					result=null;
					//]saneja@bug 7085
					break;
				}

				//saneja@bug 7085[
				//check if first resp timer expired
//				if(isFirstResp && ((System.currentTimeMillis() - executeStartTime)/1000) > timeout){
//					if(isDebugEnabled)
//						logger.debug("TimeOut happened  on lsid::"+(this.getLs()).getLsId());
//					result=null;
//					break;
//				}

				//check if complete resp timer expired
				if(((System.currentTimeMillis() - executeStartTime)/1000) > delimTimer){
					if(isDebugEnabled)
						logger.debug("Delimited response not recieved---> Timeout on lsid::"+(this.getLs()).getLsId());
//					result=null;
					break;
				}

				//]saneja@bug 7085
			}
			//check if result found settin result in response list
			if(result!=null && !(result.equals(""))){
				if(isDebugEnabled)
					logger.debug("Result matched from inputStream "+result +" on lsid::"+(this.getLs()).getLsId());
				//saneja@bug 7085[
				responses = new ArrayList<String>();
				responses.add(result);
//				String[] respArray=result.split(respSeperator);
//				responses=Arrays.asList(respArray);
				//]saneja@bug 7085
			}
		} catch (IOException e) {
			result=null;
			responses=null;
			logger.error("IO Exception while reading resp on lsid::"+(this.getLs()).getLsId(),e);
		}catch (Exception e) {
			result=null;
			responses=null;
			logger.error("Exception while reading resp on lsid::"+(this.getLs()).getLsId(),e);
		}finally{
			if(inputStream!=null){
				try {
					if(inputStream!=null)
						inputStream.close();
				} catch (IOException e) {
					logger.error("Inside finally->IO Exception while Closing input stream--Ignored");
				}
			}
			if(errorStream!=null){
				try {
					if(errorStream!=null)
						errorStream.close();
				} catch (IOException e) {
					logger.error("Inside finally->IO Exception while Closing Error stream--Ignored");
				}
			}
			if(channel!=null)
				channel.disconnect();
			channel=null;
		}

		//saneja@bug 7085[
		//		if(result!=null)
		//			result=result.trim();
		boolean recover=  !(sshsessionRef.isConnected());

		//]for working on both synch and asynch

		if(isDebugEnabled)
			logger.debug("Leaving readresponse for LsID:::"+ 
					(this.getLs()).getLsId() + "  with Result"+responses+
					"  and recoveryStatus:::"+recover);
		
		
		return (new LsResult(recover, responses));
		//]saneja@bug 7085

	}
	private long sentTime=0;
	private Boolean isAvailableForExecution=false;
	private Channel channel=null;


	/* (non-Javadoc)
	 * @see com.baypackets.ase.ra.telnetssh.ls.LsSession#makeAvailable()
	 */
	@Override
	public void markAvailable() {
		//added for working with both asynch and synch behavior together[
		synchronized (isAvailableForExecution) {
			isAvailableForExecution=true;
		}
}

}
