package com.baypackets.ase.sysapps.pac.validator;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import javax.servlet.sip.ServletParseException;
import javax.servlet.sip.SipFactory;
import javax.servlet.sip.URI;

import org.apache.log4j.Logger;

import com.baypackets.ase.sysapps.pac.adaptors.SIPPACAdaptor;
import com.baypackets.ase.sysapps.pac.jaxb.AddAconyxUserRequest;
import com.baypackets.ase.sysapps.pac.jaxb.AggregatedPresenceRequest;
import com.baypackets.ase.sysapps.pac.jaxb.AssignUserChannelsRequest;
import com.baypackets.ase.sysapps.pac.jaxb.Channel;
import com.baypackets.ase.sysapps.pac.jaxb.ChannelPresence;
import com.baypackets.ase.sysapps.pac.jaxb.DeleteAconyxUserRequest;
import com.baypackets.ase.sysapps.pac.jaxb.DeleteAllAppChannelsRequest;
import com.baypackets.ase.sysapps.pac.jaxb.DeleteAllUserChannelsRequest;
import com.baypackets.ase.sysapps.pac.jaxb.DeleteUserChannelsRequest;
import com.baypackets.ase.sysapps.pac.jaxb.EnumeratedPresenceRequest;
import com.baypackets.ase.sysapps.pac.jaxb.Errors;
import com.baypackets.ase.sysapps.pac.jaxb.GetAconyxUserDataRequest;
import com.baypackets.ase.sysapps.pac.jaxb.GetAconyxUsernameRequest;
import com.baypackets.ase.sysapps.pac.jaxb.GetAllAppChannelsRequest;
import com.baypackets.ase.sysapps.pac.jaxb.GetAllUserChannelsRequest;
import com.baypackets.ase.sysapps.pac.jaxb.GetUserChannelRequest;
import com.baypackets.ase.sysapps.pac.jaxb.ModifyAconyxUserRequest;
import com.baypackets.ase.sysapps.pac.jaxb.ModifyUserChannelsRequest;
import com.baypackets.ase.sysapps.pac.jaxb.PresenceRequest;
import com.baypackets.ase.sysapps.pac.jaxb.UpdatePresenceRequest;
import com.baypackets.ase.sysapps.pac.jaxb.UserChannel;
import com.baypackets.ase.sysapps.pac.receiver.PACSIPServlet;
import com.baypackets.ase.sysapps.pac.util.Configuration;
import com.baypackets.ase.sysapps.pac.util.Constants;
import com.baypackets.ase.sysapps.pac.util.ErrorCodes;

public class Validation {	
	private static Logger logger = Logger.getLogger(Validation.class.getName());
	private static String aconyxUsernamePattern=Constants.PATTERN_ACONYX_USERNAME;

	static{
		try{
		Configuration config=Configuration.getInstance();
		String patternRegex=(String)config.getParamValue(Constants.PROP_PAC_PATTERN_ACONYX_USERNAME);
		// Compile Pattern to check syntax
		patternRegex=patternRegex.trim();
		Pattern.compile(patternRegex);
		aconyxUsernamePattern=patternRegex;
		}catch(PatternSyntaxException pEx){
			logger.error("[PAC] PatternSyntaxException in compiling regex property"+Constants.PROP_PAC_PATTERN_ACONYX_USERNAME+":"+pEx.toString());
		}catch(Exception e){
			logger.error("[PAC] Exception in loading property"+Constants.PROP_PAC_PATTERN_ACONYX_USERNAME+":"+e.toString());
		}
		if(logger.isInfoEnabled()){
			logger.info("[PAC] Expression for AconyxUserName Validation:"+aconyxUsernamePattern);
		}
	}
	
	private Errors errorList=new Errors();
	public Errors getErrorList() {
		return errorList;
	}	
	
	public void validateEncrypted(String encrypted) {
		encrypted=encrypted.trim();
		if(encrypted.equalsIgnoreCase(Constants.ENCRYPTED_YES)||encrypted.equalsIgnoreCase(Constants.ENCRYPTED_NO))
			;
		else{	
			errorList.addError(ErrorCodes.ERROR_007, ErrorCodes.ERROR_007_DESC);
		}		
	}
	
//	public void validateRole(String role) {
//		role=role.trim();
//		if(role.equalsIgnoreCase(Constants.ROLE_PAC_ADMIN)||role.equalsIgnoreCase(Constants.ROLE_PAC_USER))
//			;
//		else {
//			errorList.addError(ErrorCodes.ERROR_024, ErrorCodes.ERROR_024_DESC);
//		}		
//	}
	
	public void validateChannelURL(String channelURL,
			String channelName) {
			if(SIPPACAdaptor.SIP_CHANNEL.equals(channelName))
			{
				SipFactory fac = PACSIPServlet.getSipFactory();
				try {
					URI uri=fac.createURI(channelURL);
					if(uri.isSipURI())
						return;
				} catch (ServletParseException e) {
					logger.debug("[PAC] Exception while creating URI....");
				}
				errorList.addError(ErrorCodes.ERROR_013, ErrorCodes.ERROR_013_DESC);
			}
			else{
				try{
				URL url=new URL(channelURL);
				if(!(url.getProtocol().equals("http")||url.getProtocol().equals("https"))){
					errorList.addError(ErrorCodes.ERROR_013, ErrorCodes.ERROR_013_DESC);
					return;
				}
				url.toURI();
				} catch (MalformedURLException e) {  
					errorList.addError(ErrorCodes.ERROR_013, ErrorCodes.ERROR_013_DESC);
		        } catch (URISyntaxException e) {
		        	errorList.addError(ErrorCodes.ERROR_013, ErrorCodes.ERROR_013_DESC);
				} 
			}
				
	}

	
	@SuppressWarnings("unchecked")
	public void validateChannelName(String channelName) {
		//channelName=channelName.trim().toUpperCase();
		Configuration config=Configuration.getInstance();
		HashMap<String,Integer> map=(HashMap<String,Integer>)config.getParamValue(Constants.PROP_CHANNEL_ID_MAP);
		Set<String> channelNames=map.keySet();
		if(channelNames.contains(channelName))
			;
		else {
			errorList.addError(ErrorCodes.ERROR_011, ErrorCodes.ERROR_011_DESC);
		}
	}
	
	public boolean validateAssignUserChannelsRequest(AssignUserChannelsRequest request) {
		boolean checkNull=false;		
		if (request != null) {
			if (validateApplicationId(request.getApplicationId()))
				checkNull = true;
			if (validateAconyxUsername(request.getAconyxUsername()))
				checkNull = true;
			List<Channel> channels = request.getChannels();
			if (channels == null || channels.size() == 0) {
				errorList.addError(ErrorCodes.ERROR_022, ErrorCodes.ERROR_022_DESC);
				checkNull = true;
			} else {
				int size=channels.size();
				for (int i = 0; i < size; i++) {
					checkNull = this.validateUserChannel(channels.get(i), true);
				}
			}
		}else{
			checkNull=true;
		}
		return checkNull;
	}


	private boolean validateUserChannel(Channel channel,boolean isAssign) {			
		boolean checkNull=false;
		String channelUsername=channel.getChannelUsername();
		String password=channel.getPassowrd();
		String encrypted=channel.getEncrypted();
		String channelName=channel.getChannelName();					
		String channelURL=channel.getChannelURL();	
		if(channelUsername==null ||channelUsername.length()==0 ){
			errorList.addError(ErrorCodes.ERROR_008, ErrorCodes.ERROR_008_DESC);
			checkNull=true;
		}
		if(isAssign && (password==null ||password.length()==0) ){
			errorList.addError(ErrorCodes.ERROR_005, ErrorCodes.ERROR_005_DESC);
			checkNull=true;
		}
			if(channelName==null ||channelName.trim().length()==0 ){
				errorList.addError(ErrorCodes.ERROR_010, ErrorCodes.ERROR_010_DESC);
			checkNull=true;
		}
		if(password!=null && password.trim().length()!=0 && (encrypted==null ||encrypted.trim().length()==0) ){
			errorList.addError(ErrorCodes.ERROR_006, ErrorCodes.ERROR_006_DESC);
			checkNull=true;
		}
		if(isAssign && (channelURL==null || channelURL.length()==0)){
			errorList.addError(ErrorCodes.ERROR_012, ErrorCodes.ERROR_012_DESC);
			checkNull=true;
		}		
		if((password==null ||password.trim().length()==0)&& (channelURL==null||channelURL.trim().length()==0))	{
			errorList.addError(ErrorCodes.ERROR_023, ErrorCodes.ERROR_023_DESC);
			checkNull=true;
		}
		return checkNull;
	}


	public void validateChannelUsername(String channelUsername,
			String channelName) {
			if(SIPPACAdaptor.SIP_CHANNEL.equals(channelName))
			{
				SipFactory fac = PACSIPServlet.getSipFactory();
				try {
					URI uri=fac.createURI(channelUsername);
					if(uri.isSipURI())
						return;
				} catch (ServletParseException e) {
					logger.debug("[PAC] Exception while creating URI....");
				}
				errorList.addError(ErrorCodes.ERROR_026, ErrorCodes.ERROR_026_DESC);
			}
			else
				this.validateForSpecialChars("ChannelUsername", channelUsername);
	}


	public boolean validateModifyUserChannelsRequest(
			ModifyUserChannelsRequest request) {
		boolean checkNull = false;

		if (request != null) {
			if (validateApplicationId(request.getApplicationId()))
				checkNull = true;
			if (validateAconyxUsername(request.getAconyxUsername()))
				checkNull = true;
			List<Channel> channels = request.getChannels();
			if (channels == null || channels.size() == 0) {
				errorList.addError(ErrorCodes.ERROR_022, ErrorCodes.ERROR_022_DESC);
				checkNull = true;
			} else {
				int size=channels.size();
				for (int i = 0; i < size; i++) {
					checkNull = this.validateUserChannel(channels.get(i), false);
				}
			}
		} else {
			checkNull = true;
		}
		return checkNull;
	}


	public boolean validateDeleteUserChannelsRequest(
			DeleteUserChannelsRequest request) {
		boolean checkNull = false;
		if (request != null) {
			if(validateApplicationId(request.getApplicationId()))
				checkNull=true;
			if(validateAconyxUsername(request.getAconyxUsername()))
				checkNull=true;
			List<Channel> channelList = request.getChannels();
			if (channelList == null || channelList.size() == 0) {
				errorList.addError(ErrorCodes.ERROR_022, ErrorCodes.ERROR_022_DESC);
				checkNull = true;
			}
			else {
				int size=channelList.size();
				for (int i = 0; i < size ; i++) {
					Channel userChannel = channelList.get(i);
					String channelUsername = userChannel.getChannelUsername();
					String channelName = userChannel.getChannelName();
					if (channelUsername == null
							|| channelUsername.trim().length() == 0) {
						errorList.addError(ErrorCodes.ERROR_008, ErrorCodes.ERROR_008_DESC);
						checkNull = true;
					}
					if (channelName == null || channelName.trim().length() == 0) {
						errorList.addError(ErrorCodes.ERROR_010, ErrorCodes.ERROR_010_DESC);
						checkNull = true;
					}
				}
			}
		} else {
			checkNull = true;
		}
		return checkNull;
	}

	public boolean validatePresenceRequest(PresenceRequest request) {
		boolean checkNull=false;
		if(request!=null){
			if(validateApplicationId(request.getApplicationId()))
				checkNull=true;
			List<UserChannel> channels=request.getUserChannels();
			if(channels == null || channels.size() == 0){
				errorList.addError(ErrorCodes.ERROR_022, ErrorCodes.ERROR_022_DESC);
				checkNull=true;
			}else{
						int size=channels.size();
					for(int i=0; i< size; i++)
						checkNull=this.vaildateUserChannel(channels.get(i),false);
			}
		}else{
			checkNull=true;
		}
		return checkNull;
	}

	public boolean vaildateUserChannel(UserChannel channel,boolean isGetAconyxUser) {		
		boolean checkNull = false;
		if(!isGetAconyxUser && validateAconyxUsername(channel.getAconyxUsername()))
			checkNull=true;
		
		if(channel.getChannelUsername() == null || channel.getChannelUsername().trim().length() == 0 ){
			errorList.addError(ErrorCodes.ERROR_008, ErrorCodes.ERROR_008_DESC);
			checkNull=true;
		}
		String channelName=channel.getChannelName();
		if(channelName == null ||channelName.trim().length() == 0 ){
			errorList.addError(ErrorCodes.ERROR_010, ErrorCodes.ERROR_010_DESC);
			checkNull=true;
		}	
		
		return checkNull;
	}

	public boolean validateAggPresenceRequest(AggregatedPresenceRequest request) {
		boolean checkNull=false;

		if(request!=null){
			if(validateApplicationId(request.getApplicationId()))
				checkNull=true;
			if(validateAconyxUsername(request.getAconyxUsername()))
				checkNull=true;		}
		else{
			checkNull=true;
		}
		return checkNull;
	}

	public boolean validateEnumeratedPresenceRequest(EnumeratedPresenceRequest request) {
		boolean checkNull=false;

		if(request!=null){
			if(validateApplicationId(request.getApplicationId()))
				checkNull=true;
			List<String> aconyxUserList=request.getAconyxUsernames();
			if(aconyxUserList==null || aconyxUserList.size()==0){
				errorList.addError(ErrorCodes.ERROR_003, ErrorCodes.ERROR_003_DESC);
				checkNull=true;				
			}else{		
				int size=aconyxUserList.size();
				for(int i=0;i<size;i++){
					if(validateAconyxUsername(aconyxUserList.get(i)))
						checkNull=true;		
				}
			}
		}
		else{
			checkNull=true;
		}
		return checkNull;
	}
	
	public boolean validateAddAconyxUserRequest(AddAconyxUserRequest request) {
		if (request != null)
			return this.validateUserRequest(request.getAconyxUsername(),request.getPassword(), request.getEncrypted(), request.getRole(),true);
		else
			return false;		
	}


	public boolean validateModifyAconyxUserRequest(ModifyAconyxUserRequest request) {
		if (request != null)
			return this.validateUserRequest(request.getAconyxUsername(),request.getPassword(), request.getEncrypted(), request.getRole(),false);
		else
			return false;

	}


	private boolean validateUserRequest(String aconyxUserName, String password,
			String encrypted, String role, boolean isAdd) {
		boolean checkNull = false;
		if(validateAconyxUsername(aconyxUserName))
			checkNull=true;
		if (isAdd && (password == null || password.trim().length() == 0)) {
			errorList.addError(ErrorCodes.ERROR_005, ErrorCodes.ERROR_005_DESC);
			checkNull = true;
		}
		if (password != null && password.trim().length()!=0 && (encrypted == null || encrypted.length() == 0)) {
			errorList.addError(ErrorCodes.ERROR_006, ErrorCodes.ERROR_006_DESC);
			checkNull = true;
		}
	    if ((isAdd) && ((role == null) || (role.trim().length() == 0))) {
	        this.errorList.addError(ErrorCodes.ERROR_014, ErrorCodes.ERROR_014_DESC);
	        checkNull = true;
	      }

		if ((password == null || password.trim().length()==0) && (role == null || role.trim().length()==0)) {
			errorList.addError(ErrorCodes.ERROR_023, ErrorCodes.ERROR_023_DESC);
			checkNull = true;
		}
		return checkNull;
	}


	public boolean validateDeleteAconyxUserRequest(DeleteAconyxUserRequest request) {
		boolean checkNull = false;
		if (request != null) {
			String aconyxUserName = request.getAconyxUsername();
			if(validateAconyxUsername(aconyxUserName))
				checkNull=true;
		} else
			checkNull = false;
		return checkNull;
	}
	

	public boolean validateGetAconyxUserDataRequest(GetAconyxUserDataRequest request) {
		boolean checkNull = false;
		if (request != null) {
			String aconyxUserName = request.getAconyxUsername();
			if(validateAconyxUsername(aconyxUserName))
				checkNull=true;
		} else
			checkNull = false;
		return checkNull;
	}

	public boolean validateUpadatePresenceRequest(UpdatePresenceRequest request) {

		boolean checkNull=false;

		if(request!=null){

			if(validateApplicationId(request.getApplicationId()))
				checkNull=true;
			List<ChannelPresence> channelsPresenceList=request.getChannelPresence();
			if(channelsPresenceList == null || channelsPresenceList.size() == 0){
				errorList.addError(ErrorCodes.ERROR_022, ErrorCodes.ERROR_022_DESC);
				checkNull=true;
			}else{			
				for(int i=0; i<channelsPresenceList.size(); i++)
					checkNull=this.vaildateChannelPresence(channelsPresenceList.get(i));
			}
		}
		else{
			checkNull=true;
		}
		return checkNull;

	}
	
	private boolean vaildateChannelPresence(ChannelPresence channelPresence) {
		boolean checkNull = false;
		
		if(validateAconyxUsername(channelPresence.getAconyxUsername()))
			checkNull=true;
		
		if(channelPresence.getChannelUsername() == null || channelPresence.getChannelUsername().trim().length() == 0 ){
			errorList.addError(ErrorCodes.ERROR_008, ErrorCodes.ERROR_008_DESC);
			checkNull=true;
		}
		
		String channelName=channelPresence.getChannelName();
		if(channelName == null ||channelName.trim().length() == 0 )
		{
			errorList.addError(ErrorCodes.ERROR_010, ErrorCodes.ERROR_010_DESC);
			checkNull=true;
		}
		String status=channelPresence.getStatus();
		if(status == null ||status.trim().length() == 0 )
		{
			errorList.addError(ErrorCodes.ERROR_020, ErrorCodes.ERROR_020_DESC);
			checkNull=true;
		}
		return checkNull;
	}


	public void validateStatus(String status) {
		status=status.trim();
		if(Constants.PRESENCE_STATUS_AVAILABLE.equals(status) || Constants.PRESENCE_STATUS_BUSY.equals(status) || Constants.PRESENCE_STATUS_NOT_AVAILABLE.equals(status))
			;
		else 
		{	
			errorList.addError(ErrorCodes.ERROR_021, ErrorCodes.ERROR_021_DESC);
		}
		
	}


	public boolean validateDeleteAllAppChannelsRequest(
			DeleteAllAppChannelsRequest request) {
		boolean checkNull = false;
		if (request != null) {
			checkNull = validateApplicationId(request.getApplicationId());
		} else {
			checkNull = true;
		}
		return checkNull;
	}

	private boolean validateApplicationId(String applicationId){
		boolean checkNull=false;
		if(applicationId == null || applicationId.length() == 0){
			errorList.addError(ErrorCodes.ERROR_001, ErrorCodes.ERROR_001_DESC);
			checkNull=true;
		}
		if(this.validateForSpecialChars("ApplicationID", applicationId))
		checkNull=true;
		return checkNull;
	}

	private boolean validateAconyxUsername(String aconyxUserName){
		boolean checkNull=false;
		if(aconyxUserName == null || aconyxUserName.trim().length() == 0){
			errorList.addError(ErrorCodes.ERROR_003, ErrorCodes.ERROR_003_DESC);
			checkNull=true;
		}
		else {
			if(aconyxUserName.length()>Constants.MAX_ACONYX_USERNAME_LENGTH){
				errorList.addError(ErrorCodes.ERROR_031, ErrorCodes.ERROR_031_DESC+aconyxUserName);
				checkNull=true;
			}
		else{
			Pattern p = Pattern.compile(aconyxUsernamePattern);
			Matcher m = p.matcher(aconyxUserName);
			if (!m.matches()) {
				errorList.addError(ErrorCodes.ERROR_030, ErrorCodes.ERROR_030_DESC+aconyxUserName);
				checkNull=true;
			}}
		}
		return checkNull;
	}


	public boolean validateDeleteAllUserChannelsRequest(
			DeleteAllUserChannelsRequest request) {
		boolean checkNull = false;
		if (request != null) {
			if(validateApplicationId(request.getApplicationId()))
				checkNull=true;
			if(validateAconyxUsername(request.getAconyxUsername()))
				checkNull=true;
		} else {
			checkNull = true;
		}
		return checkNull;
	}


	public boolean validateGetAllUserChannelsRequest(GetAllUserChannelsRequest request) {
		boolean checkNull = false;
		if (request != null) {
			if(validateApplicationId(request.getApplicationId()))
				checkNull=true;
			if(validateAconyxUsername(request.getAconyxUsername()))
				checkNull=true;
		} else {
			checkNull = true;
		}
		return checkNull;
	}


	public boolean validateGetUserChannelRequest(GetUserChannelRequest request) {
		boolean checkNull=false;
		if(request!=null){
			if(validateApplicationId(request.getApplicationId()))
				checkNull=true;
			if(validateAconyxUsername(request.getAconyxUsername()))
				checkNull=true;
			if(request.getChannelUsername() == null || request.getChannelUsername().trim().length() == 0 ){
				errorList.addError(ErrorCodes.ERROR_008, ErrorCodes.ERROR_008_DESC);
				checkNull=true;
			}

			if(request.getChannelName() == null ||request.getChannelName().trim().length() == 0 ){
				errorList.addError(ErrorCodes.ERROR_010, ErrorCodes.ERROR_010_DESC);
				checkNull=true;
			}	
		}
		else{
			checkNull=true;
		}
		return checkNull;
	}


	public boolean validateGetAllAppChannelsRequest(
			GetAllAppChannelsRequest request) {
		boolean checkNull = false;
		if (request != null) {
			checkNull = validateApplicationId(request.getApplicationId());
		} else {
			checkNull = true;
		}
		return checkNull;
	}
	
	private boolean validateForSpecialChars(String attributeName,String attributeVal){
		boolean isInvalid=false;
		if(attributeName!=null && attributeVal!=null){
			Pattern p = Pattern.compile(Constants.PATTERN_SPECIAL_CHARS);
			Matcher m = p.matcher(attributeVal);
			if (!m.matches()) {
				errorList.addError(ErrorCodes.ERROR_028, ErrorCodes.ERROR_028_DESC+attributeName);
				isInvalid=true;
			}	
		}
		return isInvalid;
	}

	public boolean validateGetAconyxUsernameRequest(
			GetAconyxUsernameRequest request) {
		boolean checkNull=false;
		if(request!=null){
			if(validateApplicationId(request.getApplicationId()))
				checkNull=true;
			List<UserChannel> channels=request.getUserChannels();
			if(channels == null || channels.size() == 0){
				errorList.addError(ErrorCodes.ERROR_022, ErrorCodes.ERROR_022_DESC);
				checkNull=true;
			}else{
					int size=channels.size();
					for(int i=0; i<size; i++)
						checkNull=this.vaildateUserChannel(channels.get(i),true);
			}
		}else{
			checkNull=true;
		}
		return checkNull;	
	}
}
