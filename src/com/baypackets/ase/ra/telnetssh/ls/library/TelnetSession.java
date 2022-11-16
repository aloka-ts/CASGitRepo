/*******************************************************************************
 *   Copyright (c) 2011 Agnity, Inc. All rights reserved.
 *   
 *   This is proprietary source code of Agnity, Inc. 
 *   
 *   Agnity, Inc. retains all intellectual property rights associated 
 *   with this source code. Use is subject to license terms.
 *   
 *   This source code contains trade secrets owned by Agnity, Inc.
 *   Confidentiality of this computer program must be maintained at 
 *   all times, unless explicitly authorized by Agnity, Inc.
 *******************************************************************************/
/**
 * 
 */
package com.baypackets.ase.ra.telnetssh.ls.library;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.net.SocketException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.net.telnet.TelnetClient;
import org.apache.commons.net.telnet.TelnetCommand;
import org.apache.log4j.Logger;

import com.baypackets.ase.ra.telnetssh.LsRaAlarmManager;
import com.baypackets.ase.ra.telnetssh.configmanager.CommonLsConfig;
import com.baypackets.ase.ra.telnetssh.configmanager.LS;
import com.baypackets.ase.ra.telnetssh.event.LsResourceEvent;
import com.baypackets.ase.ra.telnetssh.exception.TelnetLoginFailedException;
import com.baypackets.ase.ra.telnetssh.logger.CommandLogger;
import com.baypackets.ase.ra.telnetssh.ls.CommandStatus;
import com.baypackets.ase.ra.telnetssh.ls.LsManager;
import com.baypackets.ase.ra.telnetssh.ls.LsResult;
import com.baypackets.ase.ra.telnetssh.ls.LsSession;
import com.baypackets.ase.ra.telnetssh.utils.LsStatus;
import com.baypackets.ase.ra.telnetssh.utils.RaProperties;
import com.baypackets.ase.resource.ResourceException;

/**
 * The Class TelnetSession.
 * Extends LsSession
 * creates telnet session with LS 
 * using Apache libraries
 *
 * @author saneja
 */
public class TelnetSession extends LsSession {

	/** The Constant logger. */
	private static final Logger logger=Logger.getLogger(TelnetSession.class);

	/** The apache telnetCLient. */
	private TelnetClient telnet;

	/** The inputstream on telnet client. */
	private InputStream in;
	
	private BufferedInputStream bStream;

	/** The outputstream on telnetclient. */
	private PrintStream out;

	/** The Constant FAIL_MSG.used to identify failure scenario */
	private static final String FAIL_MSG="Login incorrect";

	/** The Constant ECHO_MSG. used to identify prompt */
	private static final String ECHO_MSG="LS_RA_PROMPT_IDENTIFICATION_STRING";
	
	private LsManager lsManager = LsManager.getInstance();
	
	private SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy hh:mm aaa");
	
	private boolean isLocalEnvironment=false;

	/* (non-Javadoc)
	 * @see com.baypackets.ase.ra.telnetssh.ls.LsSession#executeCommand(java.lang.String)
	 */
	@Override
	public List<String> executeCommand(String command) {
		boolean isDebugEnabled=logger.isDebugEnabled();
		if(isDebugEnabled)
			logger.debug("Enter executeCommand for LsID:::"+ 
					(this.getLs()).getLsId() + "  Command::"+command);
		CommonLsConfig commonLsConfig=LsManager.getInstance().getCommonLsConfig();
		int attemptsAllowed=commonLsConfig.getReAttempt();
		int timeout=commonLsConfig.getNoResponseTimer();
		int currentAttempt=0;
		List<String> result=null;

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
			//saneja@bug 7085
			//			String output=sendCommand(command,timeout);
			//			if(output==null){
			//				logger.error("Attempt:::" + currentAttempt + " Failed");
			//			}else{
			//				result=(output.substring(0, (output.length()-((getPrompt()).length()))-1)).trim();
			//				break;
			//			}

			List<String> output=runCommand(command,timeout);
			if(output!=null && !(output.isEmpty())){
				result=output;
				break;
			}else{
				if(isDebugEnabled)
					logger.debug("Attempt:::" + currentAttempt + " Failed on LsId::["+this.getLs().getLsId()+"]");

				//bug 7172 [reading till prompt
				String pattern=getPrompt();
				StringBuilder sb=new StringBuilder();
				char ch ;
				char lastChar = pattern.charAt(pattern.length()-1);
				long executeStartTime=System.currentTimeMillis();
				int availableBytes=0;
				while (true){
					try {
						if(((System.currentTimeMillis() - executeStartTime)/1000) > timeout){
							if(isDebugEnabled)
								logger.debug("Inside timeouut handle:read Prompt Timeout on LsId::["+this.getLs().getLsId()+"]");
							currentAttempt=attemptsAllowed;
							break;
						}

						//check if stream available
						if(availableBytes==0){
							availableBytes=bStream.available();
						}
						if(availableBytes>0){
							ch = (char) bStream.read();
							availableBytes--;
						}
						else{ 
							Thread.sleep(50);// sleep nano
							continue; 
						}
						sb.append(ch);
						if ((ch == lastChar) && sb.toString().trim().endsWith(pattern)){
							break;
						}//end if
					} catch (IOException e) {
						logger.error("IOEXCEPTION in readPrompt LsID::"+ this.getLs().getLsId(),e);
						currentAttempt=attemptsAllowed;
						break;
					} catch (InterruptedException e) {
						logger.error("InterruptedException in readPrompt LsID::"+ this.getLs().getLsId(),e);
						break;
					}
				}//end while

				//]bug 7172 closed
			}
			//]closed saneja@bug 7085 
		}
		if(isDebugEnabled)
			logger.debug("Leaving executeCommand for LsID:::"+ 
					(this.getLs()).getLsId() + "  with Result"+result);

		//added for working with both asynch and synch behavior together[
		markAvailable();
		//]for working on both synch and asynch

		return result;
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
			logger.debug("Enter Telnet startSession for LsID:::"+ lsId);
		String user=ls.getLsUser();
		String ip=ls.getLsIP();
		int port=ls.getLsPort();
		String pwd=ls.getLsPassword();
		String telnetPrompt=null;
		//saneja@bug 7085 executiong suppression [
		RaProperties raProperties=LsManager.getInstance().getRaProperties();
		telnetPrompt=raProperties.getTelnetPrompt();
		String suppressCommand=raProperties.getSuppressCommand();
		long connectTimeout=raProperties.getConnectTimeout()*1000;
		//CR UAT-1219 Changes
		long waitToSuppressCommand = raProperties.getWaitToSuppressCommand() * 1000;
		long suppressCommandTimeOut = raProperties.getSuppressCommandTimeout() * 1000;
		String suppressCommandDelim = raProperties.getSuppressCommandDelim();
		this.isLocalEnvironment = raProperties.isLocalEnvironment();
		long startTime=System.currentTimeMillis();
		//]closed saneja@bug 7085

		boolean success=false;
		telnet= new TelnetClient();
		try {
			//saneja@ bug 7085 prompt check[
			if(telnetPrompt==null){
				logger.error("Null prompt LsId::::"+lsId);
				throw (new TelnetLoginFailedException("NULL PROMPT"));
			}
			//]closed saneja@ bug 7085

			// Connect to the specified server
			//			telnet.setDefaultTimeout(connectTimeout);
			startTime=System.currentTimeMillis();
			telnet.connect(ip, port);
			// Get input and output stream references
			in = telnet.getInputStream();
			bStream = (BufferedInputStream)in;
			
			out = new PrintStream(telnet.getOutputStream());
			// Log the user on
			if(isDebugEnabled)
				logger.debug("Before readUntil login");
			String read=readUntil("login:",connectTimeout);
			if(read==null){
				logger.error("Login Failed @signup Timeout in readUntil() reading login: on LsID::"+ lsId);
				throw new TelnetLoginFailedException("Login Failed @signup Timeout in readUntil() reading login: on LsID::"+ lsId);
			}
			write(user);
			if(isDebugEnabled)
				logger.debug("Read String Login flushed userId on LsId::::"+lsId);
			read=readUntil("Password:",connectTimeout);
			if(read==null){
				logger.error("Login Failed @signup Timeout in readUntil() reading password: on LsID::"+ lsId);
				throw new TelnetLoginFailedException("Login Failed @signup Timeout in readUntil() reading password: on LsID::"+ lsId);
			}
			write(pwd);
			if(isDebugEnabled)
				logger.debug("Read String Password flushed password on  LsId::::"+lsId);
			//saneja@bug 7085 commented and added suprression

			//			telnetPrompt=readPrompt();
			//			if(telnetPrompt==null){
			//				logger.error("login failed Null prompt LsId::::"+ls.getLsId());
			//				throw (new TelnetLoginFailedException("NULL PROMPT"));
			//			}
			//			if(isDebugEnabled)
			//				logger.debug("Got telnet  LsId::::"+ls.getLsId()+"   prompt"+telnetPrompt);

			this.setPrompt(telnetPrompt);
			success = verifyLoginAndSuppress(suppressCommand,
					startTime, connectTimeout, waitToSuppressCommand,
					suppressCommandTimeOut, suppressCommandDelim);
			if(success){
				telnet.setKeepAlive(true);
				this.setLsStatus(LsStatus.LS_UP);
				markAvailable();
				
				if(isDebugEnabled)
					logger.debug(" login and suppress success send IP/BREAK to clear stream  LsId::::"+lsId);
				
				//Thread.sleep(30000); // only for tetsing needs to remove this TODO
				telnet.sendCommand((byte) TelnetCommand.IP);
				telnet.sendCommand((byte) TelnetCommand.BREAK);
			}else{
//				int failedReLogins = ls.getFailedReLogins();
//				ls.setFailedReLogins(++failedReLogins);
//				logger.error("login failed LsId::::"+lsId+"Failed ReLogins are::::"+failedReLogins);
				throw (new TelnetLoginFailedException("LOGIN AND SUPPRESS FAILED"));
			}
			//]closed saneja @bug 7085
			logger.error("LS connected lsiD"+lsId);
			
			// RESET flags once LS is conncted done for bug 26170
			ls.setFailedReLogins(0);
			ls.setUnderRecovery(false);
			
			System.out.println("LS_STATUS: " + dateFormat.format(Calendar.getInstance().getTime()) + " LS (Id " + lsId + " ,IP " + ip + "): CONNECTED ");
			if(this.isFailAlarmSent()){
				this.setFailAlarmSent(false);
				String alarmMsg="LsId:["+lsId+"]";
				LsRaAlarmManager.getInstance().raiseAlarm(LsRaAlarmManager.CONNECTION_RESTORED,lsId,alarmMsg);
			}
			try {
				LsManager.getInstance().getLsResourceAdaptor().deliverEvent(new LsResourceEvent("PEER_UP_EVENT LsId["+lsId+"]", LsResourceEvent.PEER_UP, null));
			} catch (Exception e1) {
				logger.error("Error Sending peer up event on connection success from start session lsId::"+lsId,e1);
			}
		} catch (SocketException e) {
			logger.error("SocketException creating Telnet session with LsId::"+lsId,e);
			this.setLsStatus(LsStatus.LS_DOWN);
			if(telnet!=null){
				try {
					telnet.disconnect();
				} catch (IOException e1) {
					logger.error("Error closing connection in exception Ignored  LsId::::"+lsId);
				}
			}
			try {
				LsManager.getInstance().getLsResourceAdaptor().deliverEvent(new LsResourceEvent("PEER_DOWN_EVENT LsId["+lsId+"]", LsResourceEvent.PEER_DOWN, null));
			} catch (Exception e1) {
				logger.error("Error Sending peer down event on connection fail from start session lsId::"+lsId,e1);
			}
			if(!this.isFailAlarmSent()){
				this.setFailAlarmSent(true);
				String alarmMsg="LsId:["+lsId+"]";
				System.out.println("LS_STATUS: " + dateFormat.format(Calendar.getInstance().getTime()) + " LS (Id " + lsId + " ,IP " + ip + "): DISCONNECTED ");
				LsRaAlarmManager.getInstance().raiseAlarm(LsRaAlarmManager.CONNECTION_FAILED,lsId,alarmMsg);
			}
			this.setLsDownTimeStamp(new Date());
			if (!ls.isUnderRecovery()){
				logger.error("Added to the normal Session Recovery::"+lsId);
				LsManager.getInstance().getSessionRecoveryTask().addLsSession(this);
			}else{
				logger.error("Added to the permanent Session Recovery::"+lsId);
				LsManager.getInstance().getSessionPermRecoveryTask().addLsSession(this);
			}
		} catch (IOException e) {
			logger.error("IOException creating Telnet session with LsId::"+lsId,e);
			this.setLsStatus(LsStatus.LS_DOWN);
			if(telnet!=null){
				try {
					telnet.disconnect();
				} catch (IOException e1) {
					logger.error("Error closing connection in exception Ignored  LsId::::"+lsId);
				}
			}
			try {
				LsManager.getInstance().getLsResourceAdaptor().deliverEvent(new LsResourceEvent("PEER_DOWN_EVENT LsId["+lsId+"]", LsResourceEvent.PEER_DOWN, null));
			} catch (Exception e1) {
				logger.error("Error Sending peer down event on connection fail from start session lsId::"+lsId,e1);
			}
			if(!this.isFailAlarmSent()){
				this.setFailAlarmSent(true);
				String alarmMsg="LsId:["+lsId+"]";
				System.out.println("LS_STATUS: " + dateFormat.format(Calendar.getInstance().getTime()) + " LS (Id " + lsId + " ,IP " + ip + "): DISCONNECTED ");
				LsRaAlarmManager.getInstance().raiseAlarm(LsRaAlarmManager.CONNECTION_FAILED,lsId,alarmMsg);
			}
			this.setLsDownTimeStamp(new Date());
			if (!ls.isUnderRecovery()){
				logger.error("Added to the normal Session Recovery::"+lsId);
				LsManager.getInstance().getSessionRecoveryTask().addLsSession(this);	
			}else{
				logger.error("Added to the permanent Session Recovery::"+lsId);
				LsManager.getInstance().getSessionPermRecoveryTask().addLsSession(this);
			}
		} catch (TelnetLoginFailedException e) {
			logger.error("TelnetLoginFailedException creating Telnet session with LsId::"+lsId,e);
			this.setLsStatus(LsStatus.LS_DOWN);
			if(telnet!=null){
				try {
					telnet.disconnect();
				} catch (IOException e1) {
					logger.error("Error closing connection in exception Ignored  LsId::::"+lsId);
				}
			}
			try {
				LsManager.getInstance().getLsResourceAdaptor().deliverEvent(new LsResourceEvent("PEER_DOWN_EVENT LsId["+lsId+"]", LsResourceEvent.PEER_DOWN, null));
			} catch (Exception e1) {
				logger.error("Error Sending peer down event on connection fail from start session lsId::"+lsId,e1);
			}
			if(!this.isFailAlarmSent()){
				this.setFailAlarmSent(true);
				String alarmMsg="LsId:["+lsId+"]";
				System.out.println("LS_STATUS: " + dateFormat.format(Calendar.getInstance().getTime()) + " LS (Id " + lsId + " ,IP " + ip + "): DISCONNECTED ");
				LsRaAlarmManager.getInstance().raiseAlarm(LsRaAlarmManager.CONNECTION_FAILED,lsId,alarmMsg);
			}
			this.setLsDownTimeStamp(new Date());
			if (!ls.isUnderRecovery()){
				logger.error("Added to the normal Session Recovery::"+lsId);
				LsManager.getInstance().getSessionRecoveryTask().addLsSession(this);	
			}else{
				logger.error("Added to the permanent Session Recovery::"+lsId);
				LsManager.getInstance().getSessionPermRecoveryTask().addLsSession(this);
			}
		}catch (Exception e) {
			logger.error("Exception creating Telnet session with LsId::"+lsId,e);
			this.setLsStatus(LsStatus.LS_DOWN);
			if(telnet!=null){
				try {
					telnet.disconnect();
				} catch (IOException e1) {
					logger.error("Error closing connection in exception Ignored  LsId::::"+lsId);
				}
			}
			try {
				LsManager.getInstance().getLsResourceAdaptor().deliverEvent(new LsResourceEvent("PEER_DOWN_EVENT LsId["+lsId+"]", LsResourceEvent.PEER_DOWN, null));
			} catch (Exception e1) {
				logger.error("Error Sending peer down event on connection fail from start session lsId::"+lsId);
			}
			if(!this.isFailAlarmSent()){
				this.setFailAlarmSent(true);
				String alarmMsg="LsId:["+lsId+"]";
				System.out.println("LS_STATUS: " + dateFormat.format(Calendar.getInstance().getTime()) + " LS (Id " + lsId + " ,IP " + ip + "): DISCONNECTED ");
				LsRaAlarmManager.getInstance().raiseAlarm(LsRaAlarmManager.CONNECTION_FAILED,lsId,alarmMsg);
			}
			this.setLsDownTimeStamp(new Date());
			if (!ls.isUnderRecovery()){
				logger.error("Added to the normal Session Recovery::"+lsId);
				LsManager.getInstance().getSessionRecoveryTask().addLsSession(this);	
			}else{
				logger.error("Added to the permanent Session Recovery::"+lsId);
				LsManager.getInstance().getSessionPermRecoveryTask().addLsSession(this);
			}
		}finally{
			
			if (!success) {
				int failedReLogins = ls.getFailedReLogins();
				ls.setFailedReLogins(++failedReLogins);
				logger.error("login failed LsId::::" + lsId
						+ "Failed ReLogins are::::" + failedReLogins);
			}
		}

		if(isDebugEnabled)
			logger.debug("Leave startSession for LsID:::"+ lsId);

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
		if(telnet!=null){
			try {
				telnet.disconnect();

			} catch (IOException e1) {
				logger.error("Error closing connection in exception Ignored  LsId::::"+ls.getLsId());
			}
		}
		this.setLsStatus(LsStatus.LS_DOWN);
		System.out.println("LS_STATUS: " + dateFormat.format(Calendar.getInstance().getTime()) + " LS (Id " + this.getLs().getLsId() + " ,IP " + this.getLs().getLsIP() + "): DISCONNECTED ");
		try {
			LsManager.getInstance().getLsResourceAdaptor().deliverEvent(new LsResourceEvent("PEER_DOWN_EVENT LsId["+ls.getLsId()+"]", LsResourceEvent.PEER_DOWN, null));
		} catch (ResourceException e1) {
			logger.error("Error Sending peer down event on connection stop from stopSession lsId::"+ls.getLsId(),e1);
		}

		telnet=null;
		bStream=null;
		in=null;
		out=null;
		//System.gc();

		if(isDebugEnabled)
			logger.debug("Leave stopSession for LsID:::"+ ls.getLsId());

	}

	/**
	 * Read until.
	 * Read until patterns while login
	 *
	 * @param pattern the pattern
	 * @param connectTimeout 
	 * @return the string
	 */
	private String readUntil(String pattern, long connectTimeout) {
		boolean isDebugEnabled=logger.isDebugEnabled();
		if(isDebugEnabled)
			logger.debug("readuntil() LsID:"+ (this.getLs()).getLsId()+
					"  Patter::"+pattern);
		int lsId=(this.getLs()).getLsId();
		char lastChar = pattern.charAt(pattern.length()-1);
		StringBuffer sb = new StringBuffer();
		char ch;
		long startTime=System.currentTimeMillis();
		try{
			int availableBytes=0;
			while (true ) {
				//chk for timeout
				if(System.currentTimeMillis()-startTime > connectTimeout){
					if(isDebugEnabled)
						logger.debug("readUntil()   LsID:"+ (this.getLs()).getLsId()+
						"  Connection tImeout");
					return null;
				}
				//read character if in stream available
				if(availableBytes==0){
					availableBytes=bStream.available();
				}
				
				if(availableBytes>0){
					ch = (char) bStream.read();
					availableBytes--;
				}else{ 
					Thread.sleep(50);// Nano sleep
					continue; 
				}
				sb.append(ch);
				if ((ch == lastChar) && sb.toString().trim().endsWith(pattern)) {
					if(isDebugEnabled)
						logger.debug("Succesfully read for LsID::"+ lsId+
								"   ReadString::::"+sb.toString());
					return sb.toString();
				}
			}
		}catch (IOException e) {
			logger.error("IOEXCEPTION in readuntil LsID::"+ lsId,e);
			return null;
		} catch (InterruptedException e) {
			logger.error("InterruptedException in readuntil LsID::"+ lsId,e);
			return null;
		}
	}

	/**
	 * Verify login and suppress.
	 * methods verifies succesful login and 
	 * executes the suppression command.
	 *
	 * @param suppressCommand the suppress command
	 * @param startTime 
	 * @param connectTimeout 
	 * @return boolean login status true for sucesss false for failure 
	 */
	private boolean verifyLoginAndSuppress(String suppressCommand,
			long startTime, long connectTimeout, long waitToSuppressCommand,
			long suppressCommandTimeOut, String suppressCommandDelim) {
		boolean isDebugEnabled=logger.isDebugEnabled();
		if(isDebugEnabled)
			logger.debug("verifyLoginAndSuppress()   LsID:"+ (this.getLs()).getLsId()+
					"  Command::"+suppressCommand+  "   connectTimeout:::"+connectTimeout
					+" waitToSuppressCommand:::" + waitToSuppressCommand + " suppressCommandTimeOut" + suppressCommandTimeOut
					+ " suppressCommandDelim:::" + suppressCommandDelim);
		int lsId=(this.getLs()).getLsId();
		String telnetPrompt=getPrompt();
		char successLastChar = telnetPrompt.charAt(telnetPrompt.length()-1);
		char failLastChar = FAIL_MSG.charAt(FAIL_MSG.length()-1);
		StringBuilder sb = new StringBuilder();

		try {
			//			write("echo "+ECHO_MSG);
			//check if login succesfull
			char ch;
			int availableBytes=0;
			while (true ) {
				//chk for timeout
				if(System.currentTimeMillis()-startTime > connectTimeout){
					if(isDebugEnabled)
						logger.debug("verifyLoginAndSuppress()   LsID:"+ (this.getLs()).getLsId()+
						"  Connection tImeout");
					return false;
				}
				if(availableBytes==0){
					availableBytes=bStream.available();
				}
				//read character if in stream available
				if(availableBytes>0){
					ch = (char) bStream.read();
					availableBytes--;
				}else{ 
					Thread.sleep(50);// Nano sleep
					continue; 
				}
				sb.append(ch);
				if ((ch == successLastChar) && sb.toString().trim().endsWith(telnetPrompt)) {
					if(isDebugEnabled)
						logger.debug("Login Successful LsID::"+ lsId);
					break;
				}
				if ((ch == failLastChar) && sb.toString().trim().endsWith(FAIL_MSG)) {
					logger.error("Login Failed invalid user or pwd  LsID::"+ lsId);
					return false;
				}
			}
			//CR UAT-1219 Changes
			if (isDebugEnabled)
				logger.debug("verifyLoginAndSuppress()   Sleeping before running the suppress Command for LsID:"
						+ (this.getLs()).getLsId());
			Thread.sleep(waitToSuppressCommand);
			if (isDebugEnabled)
				logger.debug("verifyLoginAndSuppress()   Writing suppress Command for LsID:"
						+ (this.getLs()).getLsId());
			write(suppressCommand);
			long executionStartTime = System.currentTimeMillis();
			String read = readUntilSuppressCmdDelimTimeout(getPrompt(),executionStartTime, suppressCommandDelim, suppressCommandTimeOut);
			if(read==null){
				logger.error("Login Failed @suppression Timeout in readUntil() reading prompt::["+getPrompt()+"] LsID::"+ lsId);
				return false;
			}
			return true;
		}
		catch (IOException e) {
			logger.error("IOEXCEPTION in read prompt LsID::"+ lsId,e);
			return false;
		} catch (InterruptedException e) {
			logger.error("InterruptedException in Sleep operation",e);
			return false;
		}
	}


	/**
	 * Read prompt
	 * To identify telnet prompt message.
	 * this method is not in use but present in case we need to identify telnet prompt
	 *
	 * @return the string
	 */
	private String readPrompt() {
		boolean isDebugEnabled=logger.isDebugEnabled();
		if(isDebugEnabled)
			logger.debug("ReadPrompt()   LsID:"+ (this.getLs()).getLsId());
		int lsId=(this.getLs()).getLsId();
		char successLastChar = ECHO_MSG.charAt(ECHO_MSG.length()-1);
		char failLastChar = FAIL_MSG.charAt(FAIL_MSG.length()-1);
		StringBuilder sb = new StringBuilder();
		String prompt=null;
		try {
			write("echo "+ECHO_MSG);
			char ch = (char) bStream.read(); 
			while (true ) {
				sb.append(ch);
				if ((ch == successLastChar) && sb.toString().trim().endsWith(ECHO_MSG)) {
					if(isDebugEnabled)
						logger.debug("Login Successful LsID::"+ lsId);
					break;
				}
				if ((ch == failLastChar) && sb.toString().trim().endsWith(FAIL_MSG)) {
					logger.error("Login Failed invalid user or pwd  LsID::"+ lsId);
					return null;
				}
				ch = (char) bStream.read();
			}
			//checking prompt after login
			ch = (char) bStream.read();
			while(true){
				sb.append(ch);
				if(ch=='\r' || ch=='\n'){
					ch=(char) bStream.read();
				}
				else
					break;
			}
			byte[] b=new byte[1024];
			int i=bStream.read(b);
			if(i>0)
				prompt=ch+new String(b);
			prompt=prompt.trim();
			return prompt;
		}
		catch (IOException e) {
			logger.error("IOEXCEPTION in read prompt LsID::"+ lsId,e);
			return null;
		}
	}

	/**
	 * Write message to outputstream.
	 *
	 * @param value the value
	 */
	private CommandStatus write(String value) {
		CommandStatus status=CommandStatus.SEND_FAIL;
		try {
			out.println(value);
			out.flush();
			status=CommandStatus.SEND_SUCCESS;
		}catch (Exception e) {
			status=CommandStatus.SEND_ERROR;
			logger.error("Exception sending command on LsId::"+(this.getLs()).getLsId(),e);
		}
		return status;
	}

	/**
	 * Sends command to LS and fetches result. 
	 * modified signature saneja@bug 7085
	 *
	 * @param command the command
	 * @param timeout the timeout
	 * @return the string
	 */
	private List<String> runCommand(String command, int timeout) {
		long executeStartTime=0;
		if(out==null || bStream== null){
			logger.error("null streams LsID:"+ (this.getLs()).getLsId());
			return null;
		}
		//saneja@bug7085[
		String delim=LsManager.getInstance().getRaProperties().getOutputDelim();
		String respSeperator=LsManager.getInstance().getRaProperties().getRespSeperator();
		int delimTimer=LsManager.getInstance().getRaProperties().getDelimRespTimer();
		//]closed saneja@bug 7085
		try {
			write(command);
			executeStartTime=System.currentTimeMillis();
			//saneja@bug7085 [
			//return readUntilTimeout(getPrompt(),executeStartTime,timeout);
			return readUntilDelimTimeout(getPrompt(),executeStartTime,timeout,respSeperator,delim, delimTimer);
			//] closed saneja@bug 7085
		}catch(Exception ex){
			logger.error("Unable to run command connection down  LsID:"+ (this.getLs()).getLsId(),ex);
			return null;
		}
	}

	/**
	 * Read until timeout.
	 * not in use kept for future requirments
	 *
	 * @param pattern the pattern
	 * @param executeStartTime the execute start time
	 * @param timeout the timeout
	 * @return the string
	 */
	private String readUntilTimeout(String pattern, long executeStartTime, int timeout) {
		boolean isDebugEnabled=logger.isDebugEnabled();
		if(isDebugEnabled)
			logger.debug("readuntil() LsID:"+ (this.getLs()).getLsId()+
					"  Patter::"+pattern);
		int lsId=(this.getLs()).getLsId();
		char lastChar = pattern.charAt(pattern.length()-1);
		StringBuilder sb = new StringBuilder();
		try {
			char ch = (char) bStream.read();
			while (true) {
				sb.append(ch);
				if ((ch == lastChar) && sb.toString().trim().endsWith(pattern)) {
					if(isDebugEnabled)
						logger.debug("Succesfully read for LsID::"+ lsId+
								"   ReadString::::"+sb.toString());
					return sb.toString();
				}
				ch = (char) bStream.read();

				if( (((System.currentTimeMillis() - executeStartTime)/1000) > timeout)){
					logger.error("Timeout happened");
					return null;
				}


			}
		}catch (IOException e) {
			logger.error("IOEXCEPTION in readuntilTimeout LsID::"+ lsId,e);
			return null;
		}
	}
	//CR UAT -1219 Changes
	/**
	 * Read until timeout or delimiter or timeout for delimiter
	 * @param pattern the pattern prompt in this case
	 * @param executeStartTime the execute start time
	 * @param timeout the timer for first response in case of multiple response
	 * @param delimTimer timer to wait for delimiter
	 * @param delim delimiter  to identify end of responses
	 * @return the string
	 */
	
	private String readUntilSuppressCmdDelimTimeout(String pattern, long executeStartTime, String delim, long delimTimer) {
		boolean isDebugEnabled=logger.isDebugEnabled();
		int lsId=(this.getLs()).getLsId();
		if(isDebugEnabled)
			logger.debug("readUntilSuppressCmdDelimTimeout() LsID:"+ lsId+
					"  Pattern::"+pattern +"  executeStartTime::"+executeStartTime+
					"  delim::"+delim+ "  delimTimer::"+delimTimer);
		
		char lastChar = pattern.charAt(pattern.length()-1);
		//		char delimLastChar=delim.charAt(delim.length()-1);
		//		char intermediateLastChar=respSeperator.charAt(respSeperator.length()-1);
		List<String> responses=null;;
		StringBuilder sb = new StringBuilder();
		boolean isFirstResp=true;
		try {
			char ch ;
			int availableBytes=0;
			while (true) {

				//check if final response timeout
				if((System.currentTimeMillis() - executeStartTime) > delimTimer){
					logger.error("readUntilSuppressCmdDelimTimeout()-->Delimited response not recieved---> Timeout LsID::"+ lsId);
					//bug7172[
					telnet.sendCommand((byte) TelnetCommand.IP);
					telnet.sendCommand((byte) TelnetCommand.BREAK);
					//]
					return null;
				}


				if(availableBytes==0){
					availableBytes=bStream.available();
				}
				
				if(availableBytes>0){
					ch = (char) bStream.read();
					availableBytes--;
				}else{
					Thread.sleep(50);// Nano sleep
					continue; 
				}

				sb.append(ch);
				String output = sb.toString();
				if (output.endsWith(delim)) {
					if(isDebugEnabled)
						logger.debug("readUntilSuppressCmdDelimTimeout()-->Succesfully read resp for LsID::"+ lsId+
								"   ReadString::::"+output);
					boolean patternMatched=false;
					executeStartTime = System.currentTimeMillis();
					//Reading upto prompt if not reached
					while (true){
						if((System.currentTimeMillis() - executeStartTime) > delimTimer){
							logger.error("readUntilSuppressCmdDelimTimeout()-->Pattern in response not recieved---> Timeout LsID::"+ lsId);
							//bug7172[
							telnet.sendCommand((byte) TelnetCommand.IP);
							telnet.sendCommand((byte) TelnetCommand.BREAK);
							//]
							return null;
						}
						int remainingBytes=bStream.available();
						
						byte byteArray[]=null;
						if(remainingBytes>0){
							byteArray = new byte[remainingBytes];	
							bStream.read(byteArray);
							for (byte b: byteArray){
								ch = (char) b;				
								sb.append(ch);	
								if (sb.toString().trim().endsWith(pattern)){//(ch == lastChar) && condition is removed
									patternMatched=true;
									if(logger.isDebugEnabled()){
										logger.debug("readUntilSuppressCmdDelimTimeout()--> pattern matched so breaking loop For LsID::"+(this.getLs()).getLsId()+" ReadString is:"+sb.toString());
									}
									break;
								}
							} 
						}
						
						if(patternMatched){
							break; //while(true break)
						}else{
							Thread.sleep(50);// Nano sleep
						}
					}//end while
					return sb.toString();
				}
			}
		}catch (IOException e) {
			logger.error("IOEXCEPTION in readUntilSuppressCmdDelimTimeout LsID::"+ lsId,e);
			return null;
		} catch (InterruptedException e) {
			logger.error("InterruptedException in readUntilSuppressCmdDelimTimeout LsID::"+ lsId,e);
			return null;
		}
	}
	
	/**
	 * Read until timeout or delimiter or timeout for delimiter
	 * added by saneja@bug 7085
	 *
	 * @param pattern the pattern prompt in this case
	 * @param executeStartTime the execute start time
	 * @param timeout the timer for first response in case of multiple response
	 * @param delimTimer timer to wait for delimiter
	 * @param delim delimiter  to identify end of responses
	 * @return the string
	 */
	private List<String> readUntilDelimTimeout(String pattern, long executeStartTime, int timeout, 
			String respSeperator,String delim, int delimTimer) {
		boolean isDebugEnabled=logger.isDebugEnabled();
		if(isDebugEnabled)
			logger.debug("readUntilDelimTimeout() LsID:"+ (this.getLs()).getLsId()+
					"  Pattern::"+pattern +"  executeStartTime::"+executeStartTime+
					"  timeout::"+timeout+"  respSeperator::"+respSeperator+ 
					"  delim::"+delim+ "  delimTimer::"+delimTimer);
		int lsId=(this.getLs()).getLsId();
		char lastChar = pattern.charAt(pattern.length()-1);
		//		char delimLastChar=delim.charAt(delim.length()-1);
		//		char intermediateLastChar=respSeperator.charAt(respSeperator.length()-1);
		
		if(isDebugEnabled)
			logger.debug("readUntilDelimTimeout()-->last character should be ::" +lastChar +" For LsID::"+(this.getLs()).getLsId());
		
		List<String> responses=null;;
		StringBuilder sb = new StringBuilder();
		boolean isFirstResp=true;
		try {
			char ch = (char) 0;
			while (true) {

				//check if first response timeout
				if(isFirstResp  &&  (((System.currentTimeMillis() - executeStartTime)/1000) > timeout)){
					logger.error("readUntilDelimTimeout()-->Timeout happened  LsID::"+ lsId);
					//bug7172[
					telnet.sendCommand((byte) TelnetCommand.IP);
					telnet.sendCommand((byte) TelnetCommand.BREAK);
					//]
					return null;
				}

				//check if final response timeout
				if(((System.currentTimeMillis() - executeStartTime)/1000) > delimTimer){
					logger.error("readUntilDelimTimeout()-->Delimited response not recieved---> Timeout LsID::"+ lsId);
					//bug7172[
					telnet.sendCommand((byte) TelnetCommand.IP);
					telnet.sendCommand((byte) TelnetCommand.BREAK);
					//]
					return null;
				}

				byte[] byteArray = null;
				
				int availableBytes=bStream.available();
				if(availableBytes>0){
					byteArray = new byte[availableBytes];	
					if(isDebugEnabled)
						logger.debug("readUntilDelimTimeout()-->Characters Read::"+bStream.available()+ " LsID::"+ lsId);
					bStream.read(byteArray);
				}else {
					Thread.sleep(50);// Nano sleep
					continue; 
				}
				
				for (byte b: byteArray){
					ch = (char) b;
					sb.append(ch);	
				}
				
//				if(bStream.available()>0 ){
//					ch = (char) bStream.read();
//				}else{
//					continue;
//				}
//
//				sb.append(ch);
				
				if(isFirstResp && sb.toString().contains(respSeperator)){
					if(isDebugEnabled)
						logger.debug("readUntilDelimTimeout()-->First Intermediate res Found::"+sb.toString()+ " LsID::"+ lsId);
					isFirstResp=false;
				}
				
				//check if final response
				if (sb.toString().contains(delim)) {
					if(isDebugEnabled)
						logger.debug("readUntilDelimTimeout()-->Succesfully read resp for LsID::"+ lsId+
								"   ReadString::::"+sb.toString());

					String output=sb.toString();
					output=output.substring(0, output.indexOf(delim));
					String[] respArray=output.split(respSeperator);
					responses=Arrays.asList(respArray);
					boolean patternMatched=false;
					//Reading upto prompt if not reached
					if (sb.toString().trim().endsWith(pattern)) { //(ch == lastChar)  this & condition is removed... if last char is space then it cases issue
						//do nothing
					}else{
						
						if(this.isLocalEnvironment){
							if(logger.isDebugEnabled()){
								logger.debug("readUntilDelimTimeout()--> character not matched, isLocalEnvironment is true so not sending IP,BREAK commands");
							}
						}else{
							logger.error("readUntilDelimTimeout()--> character not matched sending IP,BREAK commands For LsID::"+(this.getLs()).getLsId()+" ReadString :"+sb.toString()+" :read ");
							
							//bug 7172 ensure no delay after output
							telnet.sendCommand((byte) TelnetCommand.IP);
							telnet.sendCommand((byte) TelnetCommand.BREAK);

							//else read upto prompt
							while (true){
								
								//check if final response timeout
								if(((System.currentTimeMillis() - executeStartTime)/1000) > delimTimer){
									logger.error("readUntilDelimTimeout()-->Prompt not recieved---> Timeout LsID::"+ lsId);
									//bug7172[
									telnet.sendCommand((byte) TelnetCommand.IP);
									telnet.sendCommand((byte) TelnetCommand.BREAK);
									//]
									return null;
								}
								int remainingBytes=bStream.available();
								byteArray=null;
								if(remainingBytes>0){
									byteArray = new byte[remainingBytes];	
									bStream.read(byteArray);
									for (byte b: byteArray){
										ch = (char) b;
										sb.append(ch);	

										if (sb.toString().trim().endsWith(pattern)){ //(ch == lastChar)  this & condition is removed... if last char is space then it cases issue
											patternMatched=true;
											if(logger.isDebugEnabled()){
												logger.debug("readUntilDelimTimeout()--> pattern matched so breaking loop For LsID::"+(this.getLs()).getLsId()+" Readstring "+sb.toString()+" :read");
											}
											break;
										}
									} 
								}else{
									Thread.sleep(50);// Nano sleep
									continue;
								}

								if(patternMatched){
									break;
								}
							}//end while
						}//end esle
					}
					return responses;
				}

			  

			}
		}catch (IOException e) {
			logger.error("IOEXCEPTION in readUntilDelimTimeout LsID::"+ lsId,e);
			return null;
		} catch (InterruptedException e) {
			logger.error("InterruptedException in readUntilDelimTimeout LsID::"+ lsId,e);
			return null;
		}
	}



	/**
	 * Instantiates a new telnet session.
	 */
	public TelnetSession() {
		super();
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
		synchronized (isAvailableForExecution) {
			if(isAvailableForExecution){
				status=write(command);
				sentTime=System.currentTimeMillis();
				if(isDebugEnabled)
					logger.debug("Command sent at "+sentTime +" on lsid::"+(this.getLs()).getLsId() );
				isAvailableForExecution=false;
			}
		}
		if(isDebugEnabled)
			logger.debug("Leaving sendCommand for LsID:::"+ 
					(this.getLs()).getLsId() + "  with status"+status);
		return status;
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

		CommonLsConfig commonLsConfig=LsManager.getInstance().getCommonLsConfig();
		int timeout=commonLsConfig.getNoResponseTimer();
		long executeStartTime=0;
		//saneja@bug7085[
		String delim=LsManager.getInstance().getRaProperties().getOutputDelim();
		String respSeperator=LsManager.getInstance().getRaProperties().getRespSeperator();
		int delimTimer=LsManager.getInstance().getRaProperties().getDelimRespTimer();
		//]closed saneja@bug 7085

		List<String> result=null;

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

		if(out==null || bStream== null){
			logger.error("readResponse()  null streams LsID:"+ (this.getLs()).getLsId());
			return (new LsResult(true, null));
		}	
		boolean cleanFailed=false;
		executeStartTime=System.currentTimeMillis();
		//saneja@bug7085 [
		List<String> output=readUntilDelimTimeout(getPrompt(),executeStartTime,timeout,respSeperator,delim, delimTimer);
		if(output!=null && !(output.isEmpty())){
			result=output;
		}else{
			if(isDebugEnabled)
				logger.debug("readResponse()  Read attempt failed on LsId::["+this.getLs().getLsId()+"]");

			//bug 7172 [reading till prompt
			String pattern=getPrompt();
			StringBuilder sb=new StringBuilder();
			char ch ;
			char lastChar = pattern.charAt(pattern.length()-1);
			long cleanupStartTime=System.currentTimeMillis();
			int availabeBytes=0;
			while (true){
				try {
					if(((System.currentTimeMillis() - cleanupStartTime)/1000) > timeout){
						logger.error("readResponse() Inside timeout handle:read Prompt Timeout on LsId::["+this.getLs().getLsId()+"]");
						cleanFailed=true;
						break;
					}
					//check if stream available
					if(availabeBytes==0){
						availabeBytes=bStream.available();
					}
					if(availabeBytes>0){
						ch = (char) bStream.read();
						availabeBytes--;
					}
					else{
						Thread.sleep(50);// Nano sleep
						continue;
					}
					sb.append(ch);
					if ((ch == lastChar) && sb.toString().trim().endsWith(pattern)){
						break;
					}//end if
				} catch (IOException e) {
					logger.error("readResponse() IOEXCEPTION in readPrompt LsID::"+ this.getLs().getLsId(),e);
					cleanFailed= true;
					break;
				} catch (InterruptedException e) {
					logger.error("readResponse() InterruptedException in readPrompt LsID::"+ this.getLs().getLsId(),e);
					cleanFailed= true;
					break;
				}
			}//end while

			//]bug 7172 closed
		}
		//]closed saneja@bug 7085 

		//added for working with both asynch and synch behavior together[
		boolean recover = cleanFailed;
		
		//This is for the second part of the CR -1183
		
		if (cleanFailed || result == null){	
			
			String command = LsManager.getInstance().getRaProperties().getKeepAliveCommand();
			this.markAvailable();
			int lsId = this.getLs().getLsId();
			logger.error("Sending keep alive command as result null or cleanFailed for  lsId::" + lsId);
			if(CommandLogger.getInstance().isLogEnabled()){
				CommandLogger.getInstance().log("[TelnetSession] Keep Alive Command Send--> on LsId::'"+ lsId +
					"'  Command::'"+command+"'");
			}
			CommandStatus status = this.sendCommand(command);
			if(status.equals(CommandStatus.SEND_ERROR)){
				logger.error("TelnetSession(keepAliveCommand) On timeout()-->Error in send command " + command + " lsId::" + lsId);
			}else if(status.equals(CommandStatus.SEND_FAIL)){  //if fail due to busy LS
				logger.error("TelnetSession(keepAliveCommand)-->Command already sent, LS is not available for execution lsId::" + lsId);
			}else if(status.equals(CommandStatus.SEND_SUCCESS)){
				if(isDebugEnabled)
					logger.debug("TelnetSession(keepAliveCommand)-->success");
				LsResult lsResult=this.readResponseKeepAliveCommand();
				
				if(!lsResult.isRecoverSession() && isRequest){
					recover=false;
					if(isDebugEnabled){
						logger.debug("TelnetSession(keepAliveCommand)-->is success so will go for retrying processing lsRequest");
					}
				}else{
					return lsResult;
				}
			}
		}
		
		//]for working on both synch and asynch
		if(isDebugEnabled)
			logger.debug("Leaving readresponse() for LsID:::"+ 
					(this.getLs()).getLsId() + "  with Result"+result+
					"  and recoveryStatus:::"+recover);

		return (new LsResult(recover, result));


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

		CommonLsConfig commonLsConfig=LsManager.getInstance().getCommonLsConfig();
		int timeout=commonLsConfig.getNoResponseTimer();
		long executeStartTime=0;
		//saneja@bug7085[
		String delim=LsManager.getInstance().getRaProperties().getOutputDelim();
		String respSeperator=LsManager.getInstance().getRaProperties().getRespSeperator();
		int delimTimer=LsManager.getInstance().getRaProperties().getDelimRespTimer();
		//]closed saneja@bug 7085

		List<String> result=null;

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

		if(out==null || bStream== null){
			logger.error("readResponseKeepAliveCommand()  null streams LsID:"+ (this.getLs()).getLsId());
			return (new LsResult(true, null));
		}	
		boolean cleanFailed=false;
		executeStartTime=System.currentTimeMillis();
		//saneja@bug7085 [
		List<String> output=readUntilDelimTimeout(getPrompt(),executeStartTime,timeout,respSeperator,delim, delimTimer);
		if(output!=null && !(output.isEmpty())){
			result=output;
		}else{
			if(isDebugEnabled)
				logger.debug("readResponseKeepAliveCommand()  Read attempt failed on LsId::["+this.getLs().getLsId()+"]");

			//bug 7172 [reading till prompt
			String pattern=getPrompt();
			StringBuilder sb=new StringBuilder();
			char ch ;
			char lastChar = pattern.charAt(pattern.length()-1);
			long cleanupStartTime=System.currentTimeMillis();
			int availableBytes=0;
			while (true){
				try {
					if(((System.currentTimeMillis() - cleanupStartTime)/1000) > timeout){
						logger.error("readResponseKeepAliveCommand() Inside timeout handle:read Prompt Timeout on LsId::["+this.getLs().getLsId()+"]");
						cleanFailed=true;
						break;
					}
					//check if stream available
					if(availableBytes==0)
						availableBytes=bStream.available();
					if(availableBytes>0){
						ch = (char) bStream.read();	
						availableBytes--;
					}
					else {
						Thread.sleep(50);// Nano sleep
						continue;
					}
					sb.append(ch);
					if ((ch == lastChar) && sb.toString().trim().endsWith(pattern)){
						break;
					}//end if
				} catch (IOException e) {
					logger.error("readResponseKeepAliveCommand() IOEXCEPTION in readPrompt LsID::"+ this.getLs().getLsId(),e);
					cleanFailed= true;
					break;
				} catch (InterruptedException e) {
					logger.error("readResponseKeepAliveCommand() InterruptedException in readPrompt LsID::"+ this.getLs().getLsId(),e);
					cleanFailed= true;
					break;
				}
			}//end while

			//]bug 7172 closed
		}
		//]closed saneja@bug 7085 

		//added for working with both asynch and synch behavior together[
		boolean recover = cleanFailed;


		//]for working on both synch and asynch
		if(isDebugEnabled)
			logger.debug("Leaving readResponseKeepAliveCommand() for LsID:::"+ 
					(this.getLs()).getLsId() + "  with Result"+result+
					"  and recoveryStatus:::"+recover);

		return (new LsResult(recover, result));


	}

	private long sentTime=0;
	private Boolean isAvailableForExecution=false;

	/**
	 * Takse boolean as input if LS reis under recovery it should be made avialble from there
	 * boolean input is treated as current LsStatus
	 *  (non-Javadoc)
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
