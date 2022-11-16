package com.baypackets.ase.sysapps.cim.manager;

import java.sql.SQLException;
import java.util.List;

import org.apache.log4j.Logger;

import com.baypackets.ase.sysapps.cim.dao.CIMDAO;
import com.baypackets.ase.sysapps.cim.dao.impl.CIMDAOImpl;
import com.baypackets.ase.sysapps.cim.jaxb.ChatHistoryRequest;
import com.baypackets.ase.sysapps.cim.jaxb.ChatHistoryResponse;
import com.baypackets.ase.sysapps.cim.jaxb.CreateProfilesRequest;
import com.baypackets.ase.sysapps.cim.jaxb.CreateProfilesResponse;
import com.baypackets.ase.sysapps.cim.jaxb.DeleteHistoryRequest;
import com.baypackets.ase.sysapps.cim.jaxb.DeleteHistoryResponse;
import com.baypackets.ase.sysapps.cim.jaxb.DeleteProfilesRequest;
import com.baypackets.ase.sysapps.cim.jaxb.DeleteProfilesResponse;
import com.baypackets.ase.sysapps.cim.jaxb.Errors;
import com.baypackets.ase.sysapps.cim.jaxb.FetchProfilesRequest;
import com.baypackets.ase.sysapps.cim.jaxb.FetchProfilesResponse;
import com.baypackets.ase.sysapps.cim.jaxb.LatestUserChatRequest;
import com.baypackets.ase.sysapps.cim.jaxb.LatestUserChatResponse;
import com.baypackets.ase.sysapps.cim.jaxb.Profile;
import com.baypackets.ase.sysapps.cim.jaxb.ServiceProfile;
import com.baypackets.ase.sysapps.cim.jaxb.UpdateProfilesRequest;
import com.baypackets.ase.sysapps.cim.jaxb.UpdateProfilesResponse;
import com.baypackets.ase.sysapps.cim.util.ErrorCodes;

public class CIMManager {
	private static Logger logger=Logger.getLogger(CIMManager.class);
	private static CIMManager cimManager=new CIMManager();
	
	
	public static CIMDAO dao;
	public static CIMManager getInstance() { 
		return cimManager; 
	}
	private CIMManager(){}
	static {
		dao = CIMDAOImpl.getInstance();
	}
		
	public CreateProfilesResponse createProfileList(CreateProfilesRequest request){
		
		CreateProfilesResponse response=new CreateProfilesResponse();
		
		
		List <Profile> userProfiles=null;
		try{
			userProfiles=dao.createProfilesList(request);
			}			
		catch (SQLException e) {
			logger.error("SQLException in createProfileList()"+e.toString());
			Errors errors = new Errors();
			errors.addError(ErrorCodes.ERROR_007, ErrorCodes.ERROR_007_DESC);
			response.setErrors(errors);
			return response;
		}
		catch (Exception e) {
			logger.error("Exception in createProfileList()"+e.toString());
			Errors errors = new Errors();
			errors.addError(ErrorCodes.ERROR_007, ErrorCodes.ERROR_007_DESC);
			response.setErrors(errors);
			return response;
		}	
		
		
		response.setServiceProfiles(userProfiles);
		
		return response;
	}
	
	public DeleteProfilesResponse deleteProfileList(DeleteProfilesRequest request){
		
		DeleteProfilesResponse response=new DeleteProfilesResponse();
		
			
		List <Profile> userProfiles=null;
		try{
			userProfiles=dao.deleteProfileList(request);
			}			
		catch (SQLException e) {
			logger.error("SQLException in createProfileList()"+e.toString());
			Errors errors = new Errors();
			errors.addError(ErrorCodes.ERROR_007, ErrorCodes.ERROR_007_DESC);
			response.setErrors(errors);
			return response;
		}
		catch (Exception e) {
			logger.error("Exception in createProfileList()"+e.toString());
			Errors errors = new Errors();
			errors.addError(ErrorCodes.ERROR_007, ErrorCodes.ERROR_007_DESC);
			response.setErrors(errors);
			return response;
		}	
		
		
		response.setServiceProfiles(userProfiles);
		return response;
	}
	
	public UpdateProfilesResponse updateProfileList(UpdateProfilesRequest request){
		
		UpdateProfilesResponse response=new UpdateProfilesResponse();
		
		List <Profile> userProfiles=null;
		try{
			userProfiles=dao.updateProfileList(request);
			}			
		catch (SQLException e) {
			logger.error("SQLException in createProfileList()"+e.toString());
			Errors errors = new Errors();
			errors.addError(ErrorCodes.ERROR_007, ErrorCodes.ERROR_007_DESC);
			response.setErrors(errors);
			return response;
		}
		catch (Exception e) {
			logger.error("Exception in createProfileList()"+e.toString());
			Errors errors = new Errors();
			errors.addError(ErrorCodes.ERROR_007, ErrorCodes.ERROR_007_DESC);
			response.setErrors(errors);
			return response;
		}	
		
		
		response.setServiceProfiles(userProfiles);
		
		return response;
	}
	
	public FetchProfilesResponse fetchProfileList(FetchProfilesRequest request){
		
		FetchProfilesResponse response=new FetchProfilesResponse();
		
		List <ServiceProfile> userProfiles=null;
		try{
			userProfiles=dao.fetchProfileList(request);
			}			
		catch (SQLException e) {
			logger.error("SQLException in createProfileList()"+e.toString());
			Errors errors = new Errors();
			errors.addError(ErrorCodes.ERROR_007, ErrorCodes.ERROR_007_DESC);
			response.setErrors(errors);
			return response;
		}
		catch (Exception e) {
			logger.error("Exception in createProfileList()"+e.toString());
			Errors errors = new Errors();
			errors.addError(ErrorCodes.ERROR_007, ErrorCodes.ERROR_007_DESC);
			response.setErrors(errors);
			return response;
		}	
		
		
		response.setServiceProfiles(userProfiles);
		
		return response;
	}
	
	public LatestUserChatResponse getLatestUserChat(LatestUserChatRequest request){

		LatestUserChatResponse response=null;
		try{
			response=dao.getLatestUserChat(request.getAconyxuser());
		}			
		catch (SQLException e) {
			logger.error("SQLException in getChatHistory()"+e.toString());
			Errors errors = new Errors();
			errors.addError(ErrorCodes.ERROR_007, ErrorCodes.ERROR_007_DESC);
			response=new LatestUserChatResponse();
			response.setAconyxuser(request.getAconyxuser());
			response.setErrors(errors);
		}
		catch (Exception e) {
			logger.error("Exception in getChatHistory()"+e.toString());
			Errors errors = new Errors();
			errors.addError(ErrorCodes.ERROR_007, ErrorCodes.ERROR_007_DESC);
			response=new LatestUserChatResponse();
			response.setAconyxuser(request.getAconyxuser());
			response.setErrors(errors);
		}	
		return response;
	}

	public ChatHistoryResponse getChatHistory(ChatHistoryRequest request){
		
		ChatHistoryResponse response=new ChatHistoryResponse();
		
		

		try{
			response=dao.getUserChatHistory(request.getAconyxuser(), request.getBuddies());
			}			
		catch (SQLException e) {
			logger.error("SQLException in getChatHistory()"+e.toString());
			Errors errors = new Errors();
			errors.addError(ErrorCodes.ERROR_007, ErrorCodes.ERROR_007_DESC);
			response.setErrors(errors);
			return response;
		}
		catch (Exception e) {
			logger.error("Exception in getChatHistory()"+e.toString());
			Errors errors = new Errors();
			errors.addError(ErrorCodes.ERROR_007, ErrorCodes.ERROR_007_DESC);
			response.setErrors(errors);
			return response;
		}	
		
		
		
		
		return response;
	}

	
	
	public ChatHistoryResponse getChatActivityLog(ChatHistoryRequest request){
		
		ChatHistoryResponse response=new ChatHistoryResponse();

		try{
			response=dao.getUserChatActivityLog(request.getAconyxuser(), request.getBuddies());
		} catch (SQLException e) {
			logger.error("SQLException in getChatHistory()"+e.toString());
			Errors errors = new Errors();
			errors.addError(ErrorCodes.ERROR_007, ErrorCodes.ERROR_007_DESC);
			response.setErrors(errors);
			return response;
		} catch (Exception e) {
			logger.error("Exception in getChatHistory()"+e.toString());
			Errors errors = new Errors();
			errors.addError(ErrorCodes.ERROR_007, ErrorCodes.ERROR_007_DESC);
			response.setErrors(errors);
			return response;
		}	
		
		return response;
	}

	public DeleteHistoryResponse deleteChatHistory(DeleteHistoryRequest request){
	
		DeleteHistoryResponse response=new DeleteHistoryResponse();
	
	
	boolean isDeleted=false;
	try{
		isDeleted=dao.deleteUserChatHistory(request.getUserName(),request.getAconyxUsernameList());
		}			
	catch (SQLException e) {
		logger.error("SQLException in deleteChatHistory()"+e.toString());
		Errors errors = new Errors();
		errors.addError(ErrorCodes.ERROR_007, ErrorCodes.ERROR_007_DESC);
		response.setErrors(errors);
		
		return response;
	}
	catch (Exception e) {
		logger.error("Exception in deleteChatHistory()"+e.toString());
		Errors errors = new Errors();
		errors.addError(ErrorCodes.ERROR_007, ErrorCodes.ERROR_007_DESC);
		response.setErrors(errors);
		
		return response;
	}	
	
	if(isDeleted)
	response.setResult("SUCCESS");
	
	return response;
	}
}
