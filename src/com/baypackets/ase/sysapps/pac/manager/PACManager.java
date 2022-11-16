/*


 * PACManager.java
 * @author Amit Baxi
 */
package com.baypackets.ase.sysapps.pac.manager;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.servlet.sip.SipApplicationSession;

import org.apache.log4j.Logger;

import com.baypackets.ase.sysapps.pac.adaptors.PACAdaptor;
import com.baypackets.ase.sysapps.pac.adaptors.PACAdaptorFactory;
import com.baypackets.ase.sysapps.pac.adaptors.SIPPACAdaptor;
import com.baypackets.ase.sysapps.pac.cache.PACMemoryMap;
import com.baypackets.ase.sysapps.pac.cache.UserChannelDataRow;
import com.baypackets.ase.sysapps.pac.dao.PACDAO;
import com.baypackets.ase.sysapps.pac.dao.rdbms.PACDAOImpl;
import com.baypackets.ase.sysapps.pac.dataobjects.ChannelDO;
import com.baypackets.ase.sysapps.pac.dataobjects.PresenceDO;
import com.baypackets.ase.sysapps.pac.dataobjects.UserDO;
import com.baypackets.ase.sysapps.pac.jaxb.AconyxUser;
import com.baypackets.ase.sysapps.pac.jaxb.AconyxUserPresence;
import com.baypackets.ase.sysapps.pac.jaxb.AddAconyxUserRequest;
import com.baypackets.ase.sysapps.pac.jaxb.AddAconyxUserResponse;
import com.baypackets.ase.sysapps.pac.jaxb.AggregatedPresenceRequest;
import com.baypackets.ase.sysapps.pac.jaxb.AggregatedPresenceResponse;
import com.baypackets.ase.sysapps.pac.jaxb.AssignUserChannelsRequest;
import com.baypackets.ase.sysapps.pac.jaxb.AssignUserChannelsResponse;
import com.baypackets.ase.sysapps.pac.jaxb.Channel;
import com.baypackets.ase.sysapps.pac.jaxb.ChannelPresence;
import com.baypackets.ase.sysapps.pac.jaxb.DeleteAconyxUserRequest;
import com.baypackets.ase.sysapps.pac.jaxb.DeleteAconyxUserResponse;
import com.baypackets.ase.sysapps.pac.jaxb.DeleteAllAppChannelsRequest;
import com.baypackets.ase.sysapps.pac.jaxb.DeleteAllAppChannelsResponse;
import com.baypackets.ase.sysapps.pac.jaxb.DeleteAllUserChannelsRequest;
import com.baypackets.ase.sysapps.pac.jaxb.DeleteAllUserChannelsResponse;
import com.baypackets.ase.sysapps.pac.jaxb.DeleteUserChannelsRequest;
import com.baypackets.ase.sysapps.pac.jaxb.DeleteUserChannelsResponse;
import com.baypackets.ase.sysapps.pac.jaxb.EnumeratedPresenceRequest;
import com.baypackets.ase.sysapps.pac.jaxb.EnumeratedPresenceResponse;
import com.baypackets.ase.sysapps.pac.jaxb.Errors;
import com.baypackets.ase.sysapps.pac.jaxb.GetAconyxUserDataRequest;
import com.baypackets.ase.sysapps.pac.jaxb.GetAconyxUserDataResponse;
import com.baypackets.ase.sysapps.pac.jaxb.GetAconyxUsernameRequest;
import com.baypackets.ase.sysapps.pac.jaxb.GetAconyxUsernameResponse;
import com.baypackets.ase.sysapps.pac.jaxb.GetAllAconyxUsersDataResponse;
import com.baypackets.ase.sysapps.pac.jaxb.GetAllAppChannelsRequest;
import com.baypackets.ase.sysapps.pac.jaxb.GetAllAppChannelsResponse;
import com.baypackets.ase.sysapps.pac.jaxb.GetAllUserChannelsRequest;
import com.baypackets.ase.sysapps.pac.jaxb.GetAllUserChannelsResponse;
import com.baypackets.ase.sysapps.pac.jaxb.GetUserChannelRequest;
import com.baypackets.ase.sysapps.pac.jaxb.GetUserChannelResponse;
import com.baypackets.ase.sysapps.pac.jaxb.ModifyAconyxUserRequest;
import com.baypackets.ase.sysapps.pac.jaxb.ModifyAconyxUserResponse;
import com.baypackets.ase.sysapps.pac.jaxb.ModifyUserChannelsRequest;
import com.baypackets.ase.sysapps.pac.jaxb.ModifyUserChannelsResponse;
import com.baypackets.ase.sysapps.pac.jaxb.PresenceRequest;
import com.baypackets.ase.sysapps.pac.jaxb.PresenceResponse;
import com.baypackets.ase.sysapps.pac.jaxb.SubscribeRequest;
import com.baypackets.ase.sysapps.pac.jaxb.SubscribeResponse;
import com.baypackets.ase.sysapps.pac.jaxb.UpdatePresenceRequest;
import com.baypackets.ase.sysapps.pac.jaxb.UpdatePresenceResponse;
import com.baypackets.ase.sysapps.pac.jaxb.UserChannel;
import com.baypackets.ase.sysapps.pac.util.Configuration;
import com.baypackets.ase.sysapps.pac.util.Constants;
import com.baypackets.ase.sysapps.pac.util.ErrorCodes;
import com.baypackets.ase.sysapps.pac.util.PACSubscriptionWork;

public class PACManager implements Serializable{
	private static Logger logger=Logger.getLogger(PACManager.class);
	private static PACManager pacManager=new PACManager();
	
	public static String PAC_CACHE_STATE=Constants.STATE_INIT;
	public static PACDAO dao;
	public static boolean SUBSCRIBE_FOR_PRESENCE_ON_LOAD=true;
	public static ExecutorService pacExecutorService=null;
	public static PACManager getInstance() { 
		return pacManager; 
	}
	private PACManager(){}
	static {
		Properties properties = new Properties();
		try {
			properties.load(new FileInputStream(System.getProperty(Constants.ASE_HOME) +File.separator+Constants.FILE_PROPERTIES));

		} catch(FileNotFoundException e){
			logger.error("FileNotFoundException occured while loading the properties file " + e);
		} catch (IOException e) {
			logger.error("IOException occured while loading the properties file" + e);
		}
		Configuration config=Configuration.getInstance();
		config.setParamValue(Constants.PROP_PAC_SIP_URI, properties.getProperty(Constants.PROP_PAC_SIP_URI));
		config.setParamValue(Constants.PROP_PAC_DATASOURCE_NAME,properties.getProperty(Constants.PROP_PAC_DATASOURCE_NAME));
		config.setParamValue(Constants.PROP_PAC_SECONDARY_DATASOURCE_NAME,properties.getProperty(Constants.PROP_PAC_SECONDARY_DATASOURCE_NAME));
		config.setParamValue(Constants.PROP_SIP_SUBSCRIPTION_EXPIRES, properties.getProperty(Constants.PROP_SIP_SUBSCRIPTION_EXPIRES));	
		config.setParamValue(Constants.PROP_PAC_APPSESSION_TIMER_RESTART_TIME, properties.getProperty(Constants.PROP_PAC_APPSESSION_TIMER_RESTART_TIME));
		config.setParamValue(Constants.PROP_PAC_REST_ADAPTOR_CHANNELS, properties.getProperty(Constants.PROP_PAC_REST_ADAPTOR_CHANNELS));
		config.setParamValue(Constants.PROP_PAC_MAX_SUBSCRIPTION_REQUESTS, properties.getProperty(Constants.PROP_PAC_MAX_SUBSCRIPTION_REQUESTS));
		config.setParamValue(Constants.PROP_PAC_SUBSCRIPTION_DELAY, properties.getProperty(Constants.PROP_PAC_SUBSCRIPTION_DELAY));
		config.setParamValue(Constants.PROP_PAC_SIP_SESSION_REPLICATION_ENABLED, properties.getProperty(Constants.PROP_PAC_SIP_SESSION_REPLICATION_ENABLED));
		config.setParamValue(Constants.PROP_PAC_SUBSCRIBE_FOR_ONLY_ACTIVE_USERS, properties.getProperty(Constants.PROP_PAC_SUBSCRIBE_FOR_ONLY_ACTIVE_USERS));
		String maxThreads=properties.getProperty(Constants.PROP_PAC_MAX_SUBSCRIPTION_THREADS);
		config.setParamValue(Constants.PROP_PAC_MAX_SUBSCRIPTION_THREADS,maxThreads);
		config.setParamValue(Constants.PROP_PAC_PATTERN_ACONYX_USERNAME,properties.getProperty(Constants.PROP_PAC_PATTERN_ACONYX_USERNAME));
		config.setParamValue(Constants.PROP_SIP_SUBSCRIPTION_EXPIRES_ADJUST_INTERVAL, properties.getProperty(Constants.PROP_SIP_SUBSCRIPTION_EXPIRES_ADJUST_INTERVAL));	
		if(maxThreads!=null && maxThreads.trim().length()!=0){
			int maxNumThreads=4;
			try{
				maxNumThreads=Integer.valueOf(maxThreads);
			}catch (Exception e) {
				logger.error("Incorrect value given for property"+Constants.PROP_PAC_MAX_SUBSCRIPTION_THREADS);
				maxNumThreads=4;
			}
			pacExecutorService=Executors.newFixedThreadPool(maxNumThreads);
		}
		dao = new PACDAOImpl();
		try{
			dao.setChannelIdMap();	
		}catch (SQLException e) {
			logger.error("Exception in loading initial maps"+e.toString());
		} catch (Exception e) {
			logger.error("Exception in loading initial maps"+e.toString());
		}

	}
	public AddAconyxUserResponse addAconyxUser(AddAconyxUserRequest request){
		if(logger.isDebugEnabled())
			logger.debug("[PAC] PACManager: addAconyxUser() entered");
		AddAconyxUserResponse response=new AddAconyxUserResponse();
		String aconyxUserName=request.getAconyxUsername();
		UserDO userDO=new UserDO(aconyxUserName, request.getPassword(), request.getEncrypted(), request.getRole());
		boolean success=false;
		try{
			success=dao.addAconyxUser(userDO);
		}			

		catch (SQLException e) {
			logger.error("SQLException in addAconyxUser()"+e.toString());
			Errors errors = new Errors();
			errors.addError(ErrorCodes.ERROR_025, ErrorCodes.ERROR_025_DESC);
			response.setErrors(errors);
			return response;
		}
		catch (Exception e) {
			logger.error("Exception in addAconyxUser()"+e.toString());
			Errors errors = new Errors();
			errors.addError(ErrorCodes.ERROR_025, ErrorCodes.ERROR_025_DESC);
			response.setErrors(errors);
			return response;
		}	
		if (!success) {
			Errors errors = new Errors();
			errors.addError(ErrorCodes.ERROR_029, ErrorCodes.ERROR_029_DESC);
			response.setErrors(errors);
		} else {
			response.setAconyxUsername(aconyxUserName);
			response.setRole(userDO.getRole());
		}
		if(logger.isDebugEnabled())
			logger.debug("[PAC] PACManager: addAconyxUser() exiting....");
		return response;
	}
	
	public ModifyAconyxUserResponse modifyAconyxUser(ModifyAconyxUserRequest request) {
		if(logger.isDebugEnabled())
			logger.debug("[PAC] PACManager: modifyAconyxUser() entered");
		ModifyAconyxUserResponse response=new ModifyAconyxUserResponse();
		String aconyxUserName=request.getAconyxUsername();
		UserDO userDO=new UserDO(aconyxUserName, request.getPassword(), request.getEncrypted(), request.getRole());
		boolean success=false;
		try{
			 success=dao.modifyAconyxUser(userDO);
			}			
		catch (SQLException e) {
			logger.error("SQLException in modifyAconyxUser()"+e.toString());
			Errors errors = new Errors();
			errors.addError(ErrorCodes.ERROR_025, ErrorCodes.ERROR_025_DESC);
			response.setErrors(errors);
			return response;
		}
		catch (Exception e) {
			logger.error("Exception in modifyAconyxUser()"+e.toString());
			Errors errors = new Errors();
			errors.addError(ErrorCodes.ERROR_025, ErrorCodes.ERROR_025_DESC);
			response.setErrors(errors);
			return response;
		}	
		if (!success) {
			Errors errors = new Errors();
			errors.addError(ErrorCodes.ERROR_004, ErrorCodes.ERROR_004_DESC);
			response.setErrors(errors);
		} else {
			response.setAconyxUsername(aconyxUserName);
			response.setRole(userDO.getRole());
		}
		if(logger.isDebugEnabled())
			logger.debug("[PAC] PACManager: modifyAconyxUser() exiting....");
		return response;
	}
	
	public DeleteAconyxUserResponse deleteAconyxUser(DeleteAconyxUserRequest request) {
		if(logger.isDebugEnabled())
			logger.debug("[PAC] PACManager: deleteAconyxUser() entered");
		DeleteAconyxUserResponse response=new DeleteAconyxUserResponse();
		String aconyxUserName=request.getAconyxUsername();
		UserDO userDO=new UserDO(aconyxUserName,null,null,null);
		boolean success=false;
		try{
			List <ChannelDO> channelDOList=PACMemoryMap.getInstance().getAllChannelDOForAconyxUser(aconyxUserName);
			this.endSubscriptionForChannel(channelDOList);
			 success=dao.deleteAconyxUser(userDO);
			}			
		catch (SQLException e) {
			logger.error("SQLException in deleteAconyxUser()"+e.toString());
			Errors errors = new Errors();
			errors.addError(ErrorCodes.ERROR_025, ErrorCodes.ERROR_025_DESC);
			response.setErrors(errors);
			return response;
		}
		catch (Exception e) {
			logger.error("Exception in deleteAconyxUser()"+e.toString());
			Errors errors = new Errors();
			errors.addError(ErrorCodes.ERROR_025, ErrorCodes.ERROR_025_DESC);
			response.setErrors(errors);
			return response;
		}	
		if (!success) {
			Errors errors = new Errors();
			errors.addError(ErrorCodes.ERROR_004, ErrorCodes.ERROR_004_DESC);
			response.setErrors(errors);
		} else {
			response.setAconyxUsername(aconyxUserName);
		}
		if(logger.isDebugEnabled())
			logger.debug("[PAC] PACManager: deleteAconyxUser() exiting....");
		return response;
	}
	
	public GetAconyxUserDataResponse getAconyxUserData(GetAconyxUserDataRequest request) {

		if(logger.isDebugEnabled())
			logger.debug("[PAC] PACManager: getAconyxUserData() entered");
		GetAconyxUserDataResponse response=new GetAconyxUserDataResponse();
		String aconyxUsername=request.getAconyxUsername();
		response.setAconyxUsername(aconyxUsername);
		UserDO userDO=null;
		try{
			 userDO=dao.getAconyxUserData(aconyxUsername);
			}			
		catch (SQLException e) {
			logger.error("SQLException in getAconyxUserData()"+e.toString());
			Errors errors = new Errors();
			errors.addError(ErrorCodes.ERROR_025, ErrorCodes.ERROR_025_DESC);
			response.setErrors(errors);
			return response;
		}
		catch (Exception e) {
			logger.error("Exception in getAconyxUserData()"+e.toString());
			Errors errors = new Errors();
			errors.addError(ErrorCodes.ERROR_025, ErrorCodes.ERROR_025_DESC);
			response.setErrors(errors);
			return response;
		}	
		if (userDO==null) {
			Errors errors = new Errors();
			errors.addError(ErrorCodes.ERROR_004, ErrorCodes.ERROR_004_DESC);
			response.setErrors(errors);
		} else {
			response.setPassword(userDO.getPassword());
			response.setEncrypted(userDO.getEncrypted());
			response.setRole(userDO.getRole());
		}
		if(logger.isDebugEnabled())
			logger.debug("[PAC] PACManager: getAconyxUserData() exiting....");
		return response;
	
	}
	
	public GetAllAconyxUsersDataResponse getAllAconyxUsersData() {
		if(logger.isDebugEnabled())
			logger.debug("[PAC] PACManager: getAllAconyxUsersData() entered");
		GetAllAconyxUsersDataResponse response=new GetAllAconyxUsersDataResponse();
		List <AconyxUser> aconyxUserList=new LinkedList<AconyxUser>();
		List <UserDO> userDOList=null;
		try{
			userDOList=dao.getAllAconyxUsersData();
			}			
		catch (SQLException e) {
			logger.error("SQLException in getAllAconyxUsersData()"+e.toString());
			Errors errors = new Errors();
			errors.addError(ErrorCodes.ERROR_025, ErrorCodes.ERROR_025_DESC);
			response.setErrors(errors);
			return response;
		}
		catch (Exception e) {
			logger.error("Exception in getAllAconyxUsersData()"+e.toString());
			Errors errors = new Errors();
			errors.addError(ErrorCodes.ERROR_025, ErrorCodes.ERROR_025_DESC);
			response.setErrors(errors);
			return response;
		}	
		if (userDOList==null || userDOList.size()==0) {
			Errors errors = new Errors();
			errors.addError(ErrorCodes.ERROR_004, ErrorCodes.ERROR_004_DESC);
			response.setErrors(errors);
		} else {
			for (int i = 0; i < userDOList.size(); i++) {
				UserDO user=userDOList.get(i);
				AconyxUser aconyxUser=new AconyxUser(user.getAconyxUserName(),user.getPassword(),user.getEncrypted(),user.getRole());
				aconyxUserList.add(aconyxUser);
			}
			response.setAconyxUsers(aconyxUserList);
		}
		if(logger.isDebugEnabled())
			logger.debug("[PAC] PACManager: getAllAconyxUsersData() exiting....");
		return response;	
	
	}
	
	public AssignUserChannelsResponse assignUserChannels(
			AssignUserChannelsRequest request) {
		if(logger.isDebugEnabled())
			logger.debug("[PAC] PACManager: assignUserChannels() entered");
		AssignUserChannelsResponse response=new AssignUserChannelsResponse();
		String applicationId=request.getApplicationId();
		String aconyxUserName=request.getAconyxUsername();
		response.setApplicationId(applicationId);
		response.setAconyxUsername(aconyxUserName);	
		
		List<Channel> channelsList = request.getChannels();
		List<Channel> invalidChannels = new LinkedList<Channel>();
		List <ChannelDO> channelDoList=new LinkedList<ChannelDO>(); 
		PACMemoryMap pacMemoryMap=PACMemoryMap.getInstance();
		for (int i = 0; i < channelsList.size(); i++) {
			Channel userChannel=channelsList.get(i);
			String channelUsername=userChannel.getChannelUsername();
			String password=userChannel.getPassowrd();
			String encrypted=userChannel.getEncrypted();
			String channelName=userChannel.getChannelName();					
			String channelURL=userChannel.getChannelURL();
			int channelId=Configuration.getInstance().getChannelId(channelName);
			boolean present=pacMemoryMap.containsChannelUserData(applicationId, aconyxUserName, channelId, channelUsername);
			if(present){
				userChannel.setStatus(Constants.STATUS_ALREADY_CONFIGURED);
				invalidChannels.add(userChannel);
			}
				else{	
			ChannelDO channelDo=new ChannelDO(applicationId, aconyxUserName, channelUsername, password, encrypted, channelName, channelURL);
			channelDoList.add(channelDo);
			}
		}
		List<Channel> channels=null;
		Errors errors = new Errors();
		try{
			
			channels=dao.assignUserChannels(channelDoList,errors);
		}
		catch (SQLException e) {
			logger.error("SQLException in assignUserChannels()"+e.getMessage());
			errors.addError(ErrorCodes.ERROR_025, ErrorCodes.ERROR_025_DESC);
			response.setErrors(errors);
		}
		catch (Exception e) {
			logger.error("Exception in assignUserChannels()",e);
			errors.addError(ErrorCodes.ERROR_025, ErrorCodes.ERROR_025_DESC);
			response.setErrors(errors);
		}	
		if(channels!=null)
		{	
			this.subscribeForChannelPresence(applicationId,aconyxUserName,channels);
			channels.addAll(invalidChannels);
			response.setChannels(channels);	
			if(errors.getError().size()>0)
				response.setErrors(errors);
		}
		if(logger.isDebugEnabled())
			logger.debug("[PAC] PACManager: assignUserChannels() exiting....");
		return response;		
	}

	public void subscribeForChannelPresence(String applicationId,
			String aconyxUserName, List<Channel> channels) {
		if(logger.isDebugEnabled())
			logger.debug("[PAC] PACManager: subscribeForChannelPresence() entered:"+aconyxUserName);
		for (int i = 0; i < channels.size(); i++) {
			Channel uchannel=channels.get(i);
			if(Constants.STATUS_SUCCESS.equals(uchannel.getStatus()))
			{
				String channelName=uchannel.getChannelName();
				PACAdaptor adaptor=PACAdaptorFactory.getInstance().getPACAdaptor(channelName);
				if(adaptor!=null)
				adaptor.subscribeForUserPresence(applicationId, aconyxUserName, uchannel);
			}	
			// No need to send password IP Address and Port in Response so setting them as null.
			uchannel.setChannelURL(null);
			uchannel.setEncrypted(null);
			uchannel.setPassowrd(null);
		}
		if(logger.isDebugEnabled())
			logger.debug("[PAC] PACManager: subscribeForChannelPresence() exiting....");
	}
	
	private void endSubscriptionForChannel(List<ChannelDO> channelDOList) {
		if(logger.isDebugEnabled())
			logger.debug("[PAC] PACManager: endSubscriptionForChannel() entered");
		if(channelDOList!=null){
			PACSubscriptionWork work=new PACSubscriptionWork(channelDOList, PACSubscriptionWork.Operation.UNSBSCRIBE);
			pacExecutorService.submit(work);
			}else{
				if(logger.isDebugEnabled())
					logger.debug("[PAC] PACManager: endSubscriptionForChannel() null list so exiting...");
			}
		if(logger.isDebugEnabled())
			logger.debug("[PAC] PACManager: endSubscriptionForChannel() exiting...");
	}
	
	private void endSubscriptionForChannel(String applicationId,
			String aconyxUserName, List<Channel> channels) {
		if(logger.isDebugEnabled())
			logger.debug("[PAC] PACManager: endSubscriptionForChannel() entered:"+aconyxUserName);
		
		for (int i = 0; i < channels.size(); i++) {
			Channel uchannel=channels.get(i);
			if(Constants.STATUS_SUCCESS.equals(uchannel.getStatus()))
			{
				String channelName=uchannel.getChannelName();
				PACAdaptor adaptor=PACAdaptorFactory.getInstance().getPACAdaptor(channelName);
				if(adaptor!=null)
				adaptor.endSubscriptionForChannel(applicationId, aconyxUserName, uchannel);
			}	
		}	
		if(logger.isDebugEnabled())
			logger.debug("[PAC] PACManager: endSubscriptionForChannel() exiting...");
	}
	
	public ModifyUserChannelsResponse modifyUserChannels(
			ModifyUserChannelsRequest request) {
		if(logger.isDebugEnabled())
			logger.debug("[PAC] PACManager: modifyUserChannels() entered");
		ModifyUserChannelsResponse response=new ModifyUserChannelsResponse();
		String applicationId=request.getApplicationId();
		String aconyxUserName=request.getAconyxUsername();
		response.setApplicationId(applicationId);
		response.setAconyxUsername(aconyxUserName);
		Errors errors=this.validateAppIDAconyxUser(applicationId, aconyxUserName);
		if (errors != null) {
			response.setErrors(errors);
			return response;
		}
		List<Channel> channelsList = request.getChannels();
		List <ChannelDO> channelDoList=new LinkedList<ChannelDO>(); 
		List <Channel> invalidChannelList=new LinkedList<Channel>();
		PACMemoryMap pacMemoryMap=PACMemoryMap.getInstance();
		for (int i = 0; i < channelsList.size(); i++) {
			Channel userChannel=channelsList.get(i);
			String channelUsername=userChannel.getChannelUsername();
			String channelName=userChannel.getChannelName();	
			int channelId=Configuration.getInstance().getChannelId(channelName);
			if(pacMemoryMap.containsChannelUserData(applicationId, aconyxUserName, channelId, channelUsername)){
				String password=userChannel.getPassowrd();
				String encrypted=userChannel.getEncrypted();						
				String channelURL=userChannel.getChannelURL();
			ChannelDO channelDo=new ChannelDO(applicationId, aconyxUserName, channelUsername, password, encrypted, channelName,channelURL);
			channelDoList.add(channelDo);
			}else{
				userChannel.setStatus(Constants.STATUS_NOT_CONFIGURED);
				invalidChannelList.add(userChannel);
			}
		}
		List<Channel> channels=null;
		try{
		channels=dao.modifyUserChannels(channelDoList);			
		}
		catch (SQLException e) {
			logger.error("SQLException in modifyUserChannels()"+e.getMessage(),e);
			errors = new Errors();
			errors.addError(ErrorCodes.ERROR_025, ErrorCodes.ERROR_025_DESC);
			response.setErrors(errors);
		}
		catch (Exception e) {
			logger.error("Exception in modifyUserChannels()"+e.getMessage(),e);
			 errors = new Errors();
			 errors.addError(ErrorCodes.ERROR_025, ErrorCodes.ERROR_025_DESC);
			 response.setErrors(errors);			
			}	
		if(channels!=null)
		{	
			this.subscribeForChannelPresence(applicationId,aconyxUserName,channels);
			channels.addAll(invalidChannelList);
			response.setChannels(channels);		
		}
		response.setChannels(channels);
		if(logger.isDebugEnabled())
			logger.debug("[PAC] PACManager: modifyUserChannels() exiting....");
		return response;
	
	}

	public DeleteUserChannelsResponse deleteUserChannels(
			DeleteUserChannelsRequest request) {
		if(logger.isDebugEnabled())
			logger.debug("[PAC] PACManager: deleteUserChannels() entered");
		DeleteUserChannelsResponse response=new DeleteUserChannelsResponse();
		String applicationId=request.getApplicationId();
		String aconyxUserName=request.getAconyxUsername();
		response.setApplicationId(applicationId);
		response.setAconyxUsername(aconyxUserName);
		Errors errors=this.validateAppIDAconyxUser(applicationId, aconyxUserName);
		if (errors != null) {
			response.setErrors(errors);
			return response;
		}
		
		List<Channel> channelsList = request.getChannels();
		List <Channel> invalidChannelList=new LinkedList<Channel>();
		List <ChannelDO> channelDoList=new LinkedList<ChannelDO>(); 
		PACMemoryMap pacMemoryMap=PACMemoryMap.getInstance();
		for (int i = 0; i < channelsList.size(); i++) {
			Channel userChannel=channelsList.get(i);
			String channelUsername=userChannel.getChannelUsername();
			String channelName=userChannel.getChannelName();
			int channelId=Configuration.getInstance().getChannelId(channelName);
			if(pacMemoryMap.containsChannelUserData(applicationId, aconyxUserName, channelId, channelUsername))	{
			ChannelDO channelDo=new ChannelDO(applicationId, aconyxUserName, channelUsername, "", "", channelName, "");
			channelDoList.add(channelDo);
			}else{
				userChannel.setStatus(Constants.STATUS_NOT_CONFIGURED);
				invalidChannelList.add(userChannel);
			}
		}
		List<Channel> channels=null;
		try{
		channels=dao.deleteUserChannels(channelDoList);			
		}
		catch (SQLException e) {
			logger.error("SQLException in deleteUserChannels()"+e.getMessage(),e);
			 errors = new Errors();
			 errors.addError(ErrorCodes.ERROR_025, ErrorCodes.ERROR_025_DESC);
			 response.setErrors(errors);
		}
		catch (Exception e) {			
			logger.error("Exception in deleteUserChannels()"+e.getMessage(),e);
			errors = new Errors();
			errors.addError(ErrorCodes.ERROR_025, ErrorCodes.ERROR_025_DESC);
			response.setErrors(errors);	
		}	
		if(channels!=null)
		{	
			this.endSubscriptionForChannel(applicationId,aconyxUserName,channels);
			channels.addAll(invalidChannelList);
			response.setChannels(channels);		
		}
		response.setChannels(channels);
		if(logger.isDebugEnabled())
			logger.debug("[PAC] PACManager: deleteUserChannels() exiting....");
		return response;
	}

	public GetUserChannelResponse getUserChannel(GetUserChannelRequest request) {
		if(logger.isDebugEnabled())
			logger.debug("[PAC] PACManager: getUserChannel() entered");
		String applicationId=request.getApplicationId();
		String aconyxUsername=request.getAconyxUsername();
		String channelUsername=request.getChannelUsername();
		String channelName=request.getChannelName();	
		int channelId=Configuration.getInstance().getChannelId(channelName);
		GetUserChannelResponse response = new GetUserChannelResponse();
		response.setApplicationId(applicationId);
		response.setAconyxUsername(aconyxUsername);
		Errors errors=this.validateAppIDAconyxUser(applicationId, aconyxUsername);
		if (errors != null) {
			response.setErrors(errors);
			}
		else{
			UserChannelDataRow userchannelData=PACMemoryMap.getInstance().getChannelUserData(applicationId, aconyxUsername, channelId, channelUsername);
			if (userchannelData != null) {
				 Channel channel=new Channel(null,channelUsername, userchannelData.getPassword(), userchannelData.getEncrypted(), channelName, userchannelData.getChannelURL(), null);
				 response.setChannel(channel);
			}else{				
				errors = new Errors();
				errors.addError(ErrorCodes.ERROR_017, ErrorCodes.ERROR_017_DESC);
				response.setErrors(errors);
				}
		}
		if(logger.isDebugEnabled())
			logger.debug("[PAC] PACManager: deleteUserChannels() exiting....");
		return response;
	}

	public GetAllUserChannelsResponse getAllUserChannels(
			GetAllUserChannelsRequest request) {
		if(logger.isDebugEnabled())
			logger.debug("[PAC] PACManager: getAllUserChannels() entered");
		GetAllUserChannelsResponse response = new GetAllUserChannelsResponse();
		String applicationId=request.getApplicationId();
		String aconyxUserName=request.getAconyxUsername();
		response.setApplicationId(applicationId);
		response.setAconyxUsername(aconyxUserName);
		Errors errors=this.validateAppIDAconyxUser(applicationId, aconyxUserName);
		if(errors!=null)
		{
			response.setErrors(errors);
			return response;
		}
		else
		{
			LinkedList<ChannelDO> channelDOList=PACMemoryMap.getInstance().getAllChannelDO(applicationId, aconyxUserName);
			List<Channel> channelsList=new LinkedList<Channel>();
			if(channelDOList!=null && channelDOList.size()!=0)
			{
				for(int i=0;i<channelDOList.size();i++)
				{
					ChannelDO channelDO=channelDOList.get(i);
					Channel channel=new Channel();
					channel.setChannelUsername(channelDO.getChannelUsername());
					channel.setChannelName(channelDO.getChannelName());
					channel.setEncrypted(channelDO.getEncrypted());
					channel.setPassowrd(channelDO.getPassword());
					channel.setChannelURL(channelDO.getChannelURL());
					channelsList.add(channel);
				}
				response.setChannels(channelsList);
			}
		}
		if(logger.isDebugEnabled())
			logger.debug("[PAC] PACManager: getAllUserChannels() exiting....");
		return response;			
	}
	
	public DeleteAllAppChannelsResponse deleteAllAppChannels(
			DeleteAllAppChannelsRequest request) {
		if(logger.isDebugEnabled())
			logger.debug("[PAC] PACManager: deleteAllAppChannels() entered");
		DeleteAllAppChannelsResponse response=new DeleteAllAppChannelsResponse();
		String applicationId = request.getApplicationId();
		response.setApplicationId(applicationId);
		PACMemoryMap pacMemoryMap=PACMemoryMap.getInstance();
		if (!pacMemoryMap.containsApplicationID(applicationId)) {
			Errors errors = new Errors();
			errors.addError(ErrorCodes.ERROR_002, ErrorCodes.ERROR_002_DESC);
			response.setErrors(errors);
			return response;
		}
		try{
			List <ChannelDO> channelDOList=pacMemoryMap.getAllChannelDO(applicationId);
			this.endSubscriptionForChannel(channelDOList);
			dao.deleteAllAppChannels(applicationId);
			}			
		catch (SQLException e) {
			logger.error("SQLException in deleteAllAppChannels()",e);
			Errors errors = new Errors();
			errors.addError(ErrorCodes.ERROR_025, ErrorCodes.ERROR_025_DESC);
			response.setErrors(errors);
		}
		catch (Exception e) {
			logger.error("Exception in deleteAllAppChannels()",e);
			Errors errors = new Errors();
			errors.addError(ErrorCodes.ERROR_025, ErrorCodes.ERROR_025_DESC);
			response.setErrors(errors);
		}	
		if(logger.isDebugEnabled())
			logger.debug("[PAC] PACManager: deleteAllAppChannels() exiting....");
		return response;
	}

	
	public DeleteAllUserChannelsResponse deleteAllUserChannels(
			DeleteAllUserChannelsRequest request) {
		if(logger.isDebugEnabled())
			logger.debug("[PAC] PACManager: deleteAllUserChannels() entered");
		DeleteAllUserChannelsResponse response=new DeleteAllUserChannelsResponse();
		String applicationId = request.getApplicationId();
		String aconyxUsername = request.getAconyxUsername();
		response.setApplicationId(applicationId);
		response.setAconyxUsername(aconyxUsername);
		Errors errors=this.validateAppIDAconyxUser(applicationId, aconyxUsername);
		if(errors!=null)
		{
			response.setErrors(errors);
			return response;
		}
		try{
			List <ChannelDO> channelDOList=PACMemoryMap.getInstance().getAllChannelDO(applicationId,aconyxUsername);
			this.endSubscriptionForChannel(channelDOList);
			dao.deleteAllUserChannels(applicationId,aconyxUsername);
			}			
		catch (SQLException e) {
			logger.error("SQLException in deleteAllUserChannels()",e);
			errors = new Errors();
			Errors.Error error = new Errors.Error();
			error.setErrorCode(ErrorCodes.ERROR_025);
			error.setDescription(ErrorCodes.ERROR_025_DESC);
			errors.getError().add(error);
			response.setErrors(errors);
		}
		catch (Exception e) {
			logger.error("Exception in deleteAllUserChannels()",e);
			errors = new Errors();
			errors.addError(ErrorCodes.ERROR_025, ErrorCodes.ERROR_025_DESC);
			response.setErrors(errors);
		}	
		if(logger.isDebugEnabled())
			logger.debug("[PAC] PACManager: deleteAllUserChannels() exiting....");
		return response;
	}

	public PresenceResponse getPresence(PresenceRequest request) {
		if(logger.isDebugEnabled())
			logger.debug("[PAC] PACManager: getPresence() entered");
		List<ChannelPresence> result = new LinkedList<ChannelPresence>();
		List<UserChannel> channels = request.getUserChannels();
		String applicationId=request.getApplicationId();
		PresenceResponse response = new PresenceResponse();
		response.setApplicationId(applicationId);
		PACMemoryMap pacMemoryMap=PACMemoryMap.getInstance();
		Configuration config=Configuration.getInstance();
		List <Channel> pullChannelList=new LinkedList<Channel>();
		Errors errors=null;
		if (!pacMemoryMap.containsApplicationID(applicationId)) {
			 errors = new Errors();
			errors.addError(ErrorCodes.ERROR_002, ErrorCodes.ERROR_002_DESC);
			response.setErrors(errors);
			return response;
			}
		for (int i=0; i<channels.size(); i++) {
			UserChannel userChannel = channels.get(i);
			String channelUsername=userChannel.getChannelUsername();
			String aconyxUserName=userChannel.getAconyxUsername();
			PresenceDO presenceDO=pacMemoryMap.getPresence(applicationId, aconyxUserName,userChannel.getChannelName(), channelUsername);
			ChannelPresence channelPres;
			if (presenceDO != null) {				 
				String channelName=presenceDO.getChannelName();
				int mode=config.getChannelMode(channelName);
				if(mode==Constants.MODE_PULL){
					Channel channel=new Channel();
					channel.setAconyxUsername(aconyxUserName);
					channel.setChannelUsername(channelUsername);
					channel.setChannelName(channelName);
					channel.setEncrypted(presenceDO.getEncrypted());
					channel.setPassowrd(presenceDO.getPassword());
					channel.setChannelURL(presenceDO.getChannelURL());
					pullChannelList.add(channel); 
					continue;
				}else	
				channelPres = new ChannelPresence(aconyxUserName,presenceDO.getChannelUsername(),userChannel.getChannelName(), presenceDO.getStatus(),presenceDO.getCustomLabel());				
			}else{				
				 channelPres = new ChannelPresence(aconyxUserName,userChannel.getChannelUsername(),userChannel.getChannelName(),Constants.STATUS_NOT_CONFIGURED,null);
				}
			result.add(channelPres);
		}
			if(!pullChannelList.isEmpty()){
				List <ChannelPresence> pullPresenceList=this.fetchPullChannelPresnce(pullChannelList);
				if(pullPresenceList!=null && !pullPresenceList.isEmpty())
					result.addAll(pullPresenceList);
				}
			 response.setChannelPresence(result);
			 if(logger.isDebugEnabled())
					logger.debug("[PAC] PACManager: getPresence() exiting....");
		return response;		
	}
	
	public EnumeratedPresenceResponse getEnumeratedPresence(EnumeratedPresenceRequest request){
		if(logger.isDebugEnabled())
			logger.debug("[PAC] PACManager: getEnumeratedPresence() entered");
		EnumeratedPresenceResponse response = new EnumeratedPresenceResponse();
		String applicationId=request.getApplicationId();
		List<String> aconyxUserNameList=request.getAconyxUsernames();
		response.setApplicationId(applicationId);
		List<AconyxUserPresence> aconyxUserPresenceList=new LinkedList<AconyxUserPresence>();
		HashMap <String,AconyxUserPresence> aconyxUserPresenceMap=new HashMap<String, AconyxUserPresence>();
		List <Channel> pullChannelList=new LinkedList<Channel>();
		Errors errors=null;
		PACMemoryMap pacMemoryMap=PACMemoryMap.getInstance();
		Configuration config=Configuration.getInstance();
		if (!pacMemoryMap.containsApplicationID(applicationId)) {
			errors = new Errors();
			errors.addError(ErrorCodes.ERROR_002, ErrorCodes.ERROR_002_DESC);
			response.setErrors(errors);
			return response;
		}		
		for(String aconyxUserName:aconyxUserNameList){
			AconyxUserPresence aconyxUserPresence=new AconyxUserPresence();
			aconyxUserPresence.setAconyxUsername(aconyxUserName);
			LinkedList<PresenceDO> presenceDOList=pacMemoryMap.getAllChannelsPresence(applicationId, aconyxUserName);
			
			if(presenceDOList!=null && presenceDOList.size()!=0){
				int presenceDosize=presenceDOList.size();
				List<ChannelPresence> channelPresenceList = new LinkedList<ChannelPresence>();
				for (int i = 0; i < presenceDosize; i++) {
					PresenceDO presenceDO = presenceDOList.get(i);
					String channelName=presenceDO.getChannelName();
					int mode=config.getChannelMode(channelName);
					if(mode==Constants.MODE_PULL){
						Channel channel=new Channel();
						channel.setAconyxUsername(aconyxUserName);
						channel.setChannelUsername(presenceDO.getChannelUsername());
						channel.setChannelName(channelName);
						channel.setEncrypted(presenceDO.getEncrypted());
						channel.setPassowrd(presenceDO.getPassword());
						channel.setChannelURL(presenceDO.getChannelURL());
						pullChannelList.add(channel); 
						continue;
					}
					channelPresenceList.add(new ChannelPresence(null,presenceDO.getChannelUsername(),presenceDO.getChannelName(), presenceDO.getStatus(),presenceDO.getCustomLabel()));
				}
				aconyxUserPresence.setStatus(Constants.STATUS_SUCCESS);
				aconyxUserPresence.setChannelPresenceList(channelPresenceList);

			}else{
				aconyxUserPresence.setStatus(Constants.STATUS_NOT_CONFIGURED);
			}
			aconyxUserPresenceMap.put(aconyxUserName, aconyxUserPresence);
			aconyxUserPresenceList.add(aconyxUserPresence);
		}
		if(!pullChannelList.isEmpty()){
			List <ChannelPresence> pullPresenceList=this.fetchPullChannelPresnce(pullChannelList);
			if(pullPresenceList!=null && !pullPresenceList.isEmpty()){
				for(ChannelPresence presence:pullPresenceList){
					String acxUser=presence.getAconyxUsername();
					AconyxUserPresence acxPresence=aconyxUserPresenceMap.get(acxUser);
					if(acxPresence!=null)
						acxPresence.getChannelPresenceList().add(presence);
				}
			}
		}
		if (aconyxUserPresenceList.size() != 0) {
			response.setAconyxUserPresence(aconyxUserPresenceList);
		}
		if(logger.isDebugEnabled())
			logger.debug("[PAC] PACManager: getEnumeratedPresence() exiting....");
		return response;			
	}
	
	public AggregatedPresenceResponse getAggregatedPresence(AggregatedPresenceRequest request) {
		if(logger.isDebugEnabled())
			logger.debug("[PAC] PACManager: getAggregatedPresence() entered");
		AggregatedPresenceResponse response = new AggregatedPresenceResponse();
		String applicationId=request.getApplicationId();
		String aconyxUserName=request.getAconyxUsername();
		response.setApplicationId(applicationId);
		response.setAconyxUsername(aconyxUserName);
		Configuration config=Configuration.getInstance();
		Errors errors=this.validateAppIDAconyxUser(applicationId, aconyxUserName);
		PACMemoryMap pacMemoryMap=PACMemoryMap.getInstance();
		List <Channel> pullChannelList=new LinkedList<Channel>();
		if(errors!=null){
			response.setErrors(errors);
			return response;
		}
		else
		{
			LinkedList<PresenceDO> presenceDOList=pacMemoryMap.getAllChannelsPresence(applicationId, aconyxUserName);
			List<UserChannel> availableList=new LinkedList<UserChannel>();
			List<UserChannel> busyList=new LinkedList<UserChannel>();
			if(presenceDOList!=null && presenceDOList.size()!=0)
			{
				for (int i = 0; i < presenceDOList.size(); i++) {
					PresenceDO presenceDO = presenceDOList.get(i);
					String channelName=presenceDO.getChannelName();
					int mode=config.getChannelMode(channelName);
					if(mode==Constants.MODE_PULL){
						Channel channel=new Channel();
						channel.setAconyxUsername(aconyxUserName);
						channel.setChannelUsername(presenceDO.getChannelUsername());
						channel.setChannelName(channelName);
						channel.setEncrypted(presenceDO.getEncrypted());
						channel.setPassowrd(presenceDO.getPassword());
						channel.setChannelURL(presenceDO.getChannelURL());
						pullChannelList.add(channel); 
						continue;
					}
					if (Constants.PRESENCE_STATUS_AVAILABLE.equals(presenceDO.getStatus()))
						availableList.add(new UserChannel(null,presenceDO.getChannelUsername(), presenceDO.getChannelName(),null));
					else if (Constants.PRESENCE_STATUS_BUSY.equals(presenceDO.getStatus()))
						busyList.add(new UserChannel(null,presenceDO.getChannelUsername(), presenceDO.getChannelName(),null));
				}
				
				if(!pullChannelList.isEmpty()){
					List <ChannelPresence> pullPresenceList=this.fetchPullChannelPresnce(pullChannelList);
					if(pullPresenceList!=null && !pullPresenceList.isEmpty())
						for(ChannelPresence presence:pullPresenceList){
							String status=presence.getStatus();
							if (Constants.PRESENCE_STATUS_AVAILABLE.equals(status))
									availableList.add(new UserChannel(null,presence.getChannelUsername(), presence.getChannelName(),null));
							else if	(Constants.PRESENCE_STATUS_BUSY.equals(status))
								busyList.add(new UserChannel(null,presence.getChannelUsername(), presence.getChannelName(),null));
						}
					}
				
				if (availableList.size() != 0) {
					response.setUserChannels(availableList);
					response.setStatus(Constants.PRESENCE_STATUS_AVAILABLE);
				} else if (busyList.size() != 0) {
					response.setUserChannels(busyList);
					response.setStatus(Constants.PRESENCE_STATUS_BUSY);
				} else
					response.setStatus(Constants.PRESENCE_STATUS_NOT_AVAILABLE);
			}
		}
		if(logger.isDebugEnabled())
			logger.debug("[PAC] PACManager: getAggregatedPresence() exiting....");
		return response;		
	}

	public UpdatePresenceResponse updatePresence(UpdatePresenceRequest request, boolean fromREST) {
		if(logger.isDebugEnabled())
			logger.debug("[PAC] PACManager: updatePresence() entered");
		List<ChannelPresence> channelPresenceList = request
				.getChannelPresence();
		List<ChannelPresence> result = new LinkedList<ChannelPresence>();
		List<PresenceDO> presenceDOlist = new LinkedList<PresenceDO>();
		String applicationId = request.getApplicationId();
		UpdatePresenceResponse response = new UpdatePresenceResponse();
		response.setApplicationId(applicationId);
		PACMemoryMap pacMemoryMap=PACMemoryMap.getInstance();
		Errors errors=null;
		if (!pacMemoryMap.containsApplicationID(applicationId)) {
			 errors = new Errors();
			errors.addError(ErrorCodes.ERROR_002, ErrorCodes.ERROR_002_DESC);
			response.setErrors(errors);
			return response;
			}
		
		for (int i = 0; i < channelPresenceList.size(); i++) {
			ChannelPresence presence = channelPresenceList.get(i);
			String channelUsername = presence.getChannelUsername();
			String channelName = presence.getChannelName();
			String aconyxUserName = presence.getAconyxUsername();
			PresenceDO presenceDO = pacMemoryMap.getPresence(applicationId,
					aconyxUserName, channelName, channelUsername);
			if (presenceDO != null) {
				////////Skip DB update logic goes here /////
				if(presence.getStatus().equals(presenceDO.getStatus())){
					String customLabelNew=presence.getCustomLabel();
					String customLabelOld=presenceDO.getCustomLabel();
					if((customLabelNew==null && customLabelOld==null)||
							(customLabelOld!=null && customLabelNew !=null && customLabelNew.equals(customLabelOld))){
						ChannelPresence chPresence = new ChannelPresence(aconyxUserName,channelUsername,
								channelName, Constants.STATUS_SUCCESS, null);
						result.add(chPresence);
							continue;
						}
				}
				///////////////// END  //////////////////////
				if(channelName.equals(SIPPACAdaptor.SIP_CHANNEL)){
					if(!fromREST)
						presenceDO.setStatus(presence.getStatus()); // else do not change status
					}
				else
					presenceDO.setStatus(presence.getStatus());
				
				if (presence.getCustomLabel() != null)
					presenceDO.setCustomLabel(presence.getCustomLabel());
				presenceDOlist.add(presenceDO);
			} else {
				presence.setStatus(Constants.STATUS_NOT_CONFIGURED);
				presence.setCustomLabel(null);// In request no need to send it
												// back.
				result.add(presence);
			}
		}

		try {
			presenceDOlist = dao.updatePresence(presenceDOlist);
		} catch (SQLException e) {
			logger.error("SQLException in updatePresence()",e);
			errors = new Errors();
			errors.addError(ErrorCodes.ERROR_025, ErrorCodes.ERROR_025_DESC);
			response.setErrors(errors);
		} catch (Exception e) {
			logger.error("Exception in updatePresence",e);
			errors = new Errors();
			errors.addError(ErrorCodes.ERROR_025, ErrorCodes.ERROR_025_DESC);
			response.setErrors(errors);
		}

		if (presenceDOlist != null && presenceDOlist.size() != 0) {
			for (int i = 0; i < presenceDOlist.size(); i++) {

				PresenceDO presenceDO = presenceDOlist.get(i);
				String aconyxUsername=presenceDO.getAconyxUsername();
				String channelUsername = presenceDO.getChannelUsername();
				String channelName = presenceDO.getChannelName();
				
				String status = presenceDO.getStatus();
				ChannelPresence presence = new ChannelPresence(aconyxUsername,channelUsername,
						channelName, status, null);
				result.add(presence);
			}
		}
		response.setChannelPresence(result);
		if(logger.isDebugEnabled())
			logger.debug("[PAC] PACManager: updatePresence() exiting....");
		return response;
	}
	
	public Errors validateAppIDAconyxUser(String applicationId,String aconyxUsername){
		if(logger.isDebugEnabled())
			logger.debug("[PAC] PACManager: validateAppIDAconyxUser() entered"+applicationId+" "+aconyxUsername);
		Errors errors=null;
		PACMemoryMap pacMemoryMap=PACMemoryMap.getInstance();
		if (!pacMemoryMap.containsApplicationID(applicationId)) {
			errors = new Errors();
			errors.addError(ErrorCodes.ERROR_002, ErrorCodes.ERROR_002_DESC);
		
		}
		else if (!pacMemoryMap.containsAconyxUsername(applicationId, aconyxUsername)) {
			errors = new Errors();
			errors.addError(ErrorCodes.ERROR_009, ErrorCodes.ERROR_009_DESC);		
		}
		if(logger.isDebugEnabled())
			logger.debug("[PAC] PACManager: validateAppIDAconyxUser() exiting....");
		return errors;
	}

	public GetAllAppChannelsResponse getAllAppChannels(
			GetAllAppChannelsRequest request) {
		if(logger.isDebugEnabled())
			logger.debug("[PAC] PACManager: getAllAppChannels() entered");
		GetAllAppChannelsResponse response=new GetAllAppChannelsResponse();
		String applicationId = request.getApplicationId();
		response.setApplicationId(applicationId);
		PACMemoryMap pacMemoryMap=PACMemoryMap.getInstance();
		if (!pacMemoryMap.containsApplicationID(applicationId)) {
			Errors errors = new Errors();
			errors.addError(ErrorCodes.ERROR_022, ErrorCodes.ERROR_022_DESC);
			response.setErrors(errors);
			}
		else
		{
			LinkedList<ChannelDO> channelDOList=pacMemoryMap.getAllChannelDO(applicationId);
			List<Channel> channelsList=new LinkedList<Channel>();
			if(channelDOList!=null && channelDOList.size()!=0)
			{
				for(int i=0;i<channelDOList.size();i++)
				{
					ChannelDO channelDO=channelDOList.get(i);
					Channel channel=new Channel();
					channel.setAconyxUsername(channelDO.getAconyxUserName());
					channel.setChannelUsername(channelDO.getChannelUsername());
					channel.setChannelName(channelDO.getChannelName());
					channel.setEncrypted(channelDO.getEncrypted());
					channel.setPassowrd(channelDO.getPassword());
					channel.setChannelURL(channelDO.getChannelURL());
					channelsList.add(channel);
				}
				response.setChannels(channelsList);
			}
		}
		if(logger.isDebugEnabled())
			logger.debug("[PAC] PACManager: getAllAppChannels() exiting....");
		return response;		
	}
	private List<ChannelPresence> fetchPullChannelPresnce(List <Channel> channelList){
		if(logger.isDebugEnabled())
			logger.debug("[PAC] PACManager: fetchPullChannelPresnce() entered");
		List<ChannelPresence> finalPresenceList=null;
		if(channelList!=null && channelList.size()!=0){
			finalPresenceList=new LinkedList<ChannelPresence>();
			HashMap<String, List <Channel>> hashMap=new HashMap<String, List<Channel>>();
			for(Channel channel:channelList){
				String channelURL=channel.getChannelURL();
				List <Channel> mapChannelList=hashMap.get(channelURL);
				if(mapChannelList!=null){
					mapChannelList.add(channel);
				}else{
					mapChannelList=new LinkedList<Channel>();
					mapChannelList.add(channel);
					hashMap.put(channelURL, mapChannelList);
				}
			}
			for(List<Channel> mapChannelList:hashMap.values()){
				String channelName=mapChannelList.get(0).getChannelName();
				PACAdaptor adaptor=PACAdaptorFactory.getInstance().getPACAdaptor(channelName);
				if(adaptor!=null){
					List<ChannelPresence> externalPresnceList=adaptor.fetchUserPresence("", "aconyx", "abc123", mapChannelList);
					if(externalPresnceList!=null)
						finalPresenceList.addAll(externalPresnceList);
				}else{
					logger.error("No Adaptor defined in pac.properties for the channel:"+channelName);
				}
			}

		}
		if(logger.isDebugEnabled())
			logger.debug("[PAC] PACManager: fetchPullChannelPresnce() exiting....");
		return finalPresenceList;
	}
	public GetAconyxUsernameResponse getAconyxUsername(
			GetAconyxUsernameRequest request) {
		if(logger.isDebugEnabled())
			logger.debug("[PAC] PACManager: getAconyxUsername() entered");
		List<UserChannel> userChannels = new LinkedList<UserChannel>();
		String applicationId=request.getApplicationId();
		GetAconyxUsernameResponse response = new GetAconyxUsernameResponse();
		response.setApplicationId(applicationId);
		PACMemoryMap pacMemoryMap=PACMemoryMap.getInstance();
		for(UserChannel channel:request.getUserChannels()){
			String channelUsername=channel.getChannelUsername();
			String channelName=channel.getChannelName();
			UserChannel userChannel=new UserChannel();
			userChannel.setChannelUsername(channelUsername);
			userChannel.setChannelName(channelName);
			int channelId = Configuration.getInstance().getChannelId(channelName);
			String aconyxUsername=pacMemoryMap.getAconyxUsername(applicationId, channelId, channelUsername);
			if(aconyxUsername!=null){
				userChannel.setAconyxUsername(aconyxUsername);
				userChannel.setStatus(Constants.STATUS_SUCCESS);
			}else{
				userChannel.setStatus(Constants.STATUS_NOT_CONFIGURED);
			}
			userChannels.add(userChannel);
		}		
		response.setUserChannels(userChannels);
		if(logger.isDebugEnabled())
			logger.debug("[PAC] PACManager: getAconyxUsername() exiting....");
		return response;
	}
	public SubscribeResponse generateSubscribeRequest(SubscribeRequest request) {

		if(logger.isDebugEnabled())
			logger.debug("[PAC] PACManager: generateSubscribeRequest() entered");

		String channelUsername=request.getChannelUserName();
		String channelName=request.getChannelName();
		int channelId = Configuration.getInstance().getChannelId(channelName);

		PACMemoryMap pacMemoryMap=PACMemoryMap.getInstance();
		UserChannelDataRow ucRow;
		SubscribeResponse response = new SubscribeResponse();
		Enumeration<String> applicationIDs= pacMemoryMap.keys();
		String aconyxUsername = null;
		String applicationID = null;
		Channel channel = new Channel();
		while(applicationIDs.hasMoreElements()){
			applicationID = applicationIDs.nextElement();
			aconyxUsername = pacMemoryMap.getAconyxUsername(applicationID, channelId, channelUsername);
			if(aconyxUsername!=null){
				ucRow = pacMemoryMap.getChannelUserData(applicationID, aconyxUsername, channelId, channelUsername);
				channel.setChannelName(SIPPACAdaptor.SIP_CHANNEL);
				channel.setChannelUsername(channelUsername);
				channel.setChannelURL(ucRow.getChannelURL());
				break;
			}
		}

		if(aconyxUsername == null){
			return null;
		}else{
			SipApplicationSession applicationSession = null;
			response.setApplicationId(applicationID);
			response.setAconyxUserName(aconyxUsername);
			PACAdaptor adaptor=PACAdaptorFactory.getInstance().getPACAdaptor(channelName);
			String key=applicationID + aconyxUsername + channelUsername;

			synchronized (channelUsername.intern()) {
				if(!adaptor.isChannelWorking(applicationID, aconyxUsername, channelUsername) ){
					if(logger.isDebugEnabled()){
						logger.debug("Used not subscribed .Subscribing for the user through REST call from Registrar " + channelUsername);
					}
					adaptor.subscribeForUserPresence(applicationID, aconyxUsername, channel);
				}else{
					if(logger.isDebugEnabled()){
						logger.debug("Used already subscribed .Not Subscribing for the user through REST call from Registrar " + channelUsername);
					}
				}
			}
		}

		return response;
	}
}
