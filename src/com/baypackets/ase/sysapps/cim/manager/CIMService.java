package com.baypackets.ase.sysapps.cim.manager;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import org.apache.log4j.Logger;


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
import com.baypackets.ase.sysapps.cim.validator.Validation;


@Path("/service")
public class CIMService {
	
	private static Logger logger=Logger.getLogger(CIMService.class);
	public static CIMManager cimManager;
	static{
		cimManager=CIMManager.getInstance();		
	}
	
	@POST
	@Path("/v1/createprofiles")
	@Produces("text/xml")
	@Consumes("text/xml")
	public CreateProfilesResponse createProfile(CreateProfilesRequest request){
		CreateProfilesResponse response=null;			
		Validation validation=new Validation();
		
			for(ServiceProfile sr:request.getServiceProfiles()){
				boolean checkUserNull=validation.validateUserName(sr);
				if(checkUserNull){
					break;
				}
				
				boolean checkStateNull=validation.validateUserState(sr);
				if(checkStateNull){
					break;
				}
			}				
			Errors errors=validation.getErrorList();
			if(errors.getError().size()>0){
				response=new CreateProfilesResponse();
				response.setErrors(errors);
				List<Profile> lsProfiles=new ArrayList<Profile>();
				for(ServiceProfile sr:request.getServiceProfiles()){
					Profile pr=new Profile();
					pr.setUserName(sr.getUserName());
					pr.setStatus("Could not create message profile");
				}
				response.setServiceProfiles(lsProfiles);
			}else{
					response=cimManager.createProfileList(request);			
			}
			
		return response;
	}
	
	@POST
	@Path("/v1/deleteprofiles")
	@Produces("text/xml")
	@Consumes("text/xml")
	public DeleteProfilesResponse deleteProfile(DeleteProfilesRequest request){
		DeleteProfilesResponse response=null;			
		Validation validation=new Validation();
		
			for(String sr:request.getUsername()){
				boolean checkUserNull=validation.validateAconyxUsername(sr);
				if(checkUserNull){
					break;
				}
				
				/*boolean checkStateNull=validation.validateUserState(sr);
				if(!checkStateNull){
					break;
				}*/
			}				
			Errors errors=validation.getErrorList();
			if(errors.getError().size()>0){
				response=new DeleteProfilesResponse();
				response.setErrors(errors);
				List<Profile> srProfile=new ArrayList<Profile>();
				for(String user:request.getUsername()){
					Profile sr=new Profile();
					sr.setStatus("Failure");
					sr.setUserName(user);
				}
				response.setServiceProfiles(srProfile);
			}else{
					response=cimManager.deleteProfileList(request);		
			}
			
		return response;
	}
	
	@POST
	@Path("/v1/fetchprofiles")
	@Produces("text/xml")
	@Consumes("text/xml")
	public FetchProfilesResponse fetchProfile(FetchProfilesRequest request){
		FetchProfilesResponse response=null;			
		Validation validation=new Validation();
		
			for(String sr:request.getUsername()){
			boolean checkUserNull=validation.validateAconyxUsername(sr);
			if(checkUserNull){
				break;
			}
			
			/*boolean checkStateNull=validation.validateUserState(sr);
			if(!checkStateNull){
				break;
			}*/
			}				
			Errors errors=validation.getErrorList();
			if(errors.getError().size()>0){
				response=new FetchProfilesResponse();
				response.setErrors(errors);
				List<ServiceProfile> srProfile=new ArrayList<ServiceProfile>();
				for(String user:request.getUsername()){
					ServiceProfile sr=new ServiceProfile();
					sr.setState("Failure");
					sr.setUserName(user);
				}
				response.setServiceProfiles(srProfile);
			}else{
					response=cimManager.fetchProfileList(request);			
			}
			
		return response;
	}
	
	@POST
	@Path("/v1/updateprofiles")
	@Produces("text/xml")
	@Consumes("text/xml")
	public UpdateProfilesResponse createProfile(UpdateProfilesRequest request){
		UpdateProfilesResponse response=null;			
		Validation validation=new Validation();
		
			for(ServiceProfile sr:request.getServiceProfiles()){
				boolean checkUserNull=validation.validateUserName(sr);
				if(checkUserNull){
					break;
				}
				
				boolean checkStateNull=validation.validateUserState(sr);
				if(checkStateNull){
					break;
				}
			}				
			Errors errors=validation.getErrorList();
			if(errors.getError().size()>0){
				response=new UpdateProfilesResponse();
				response.setErrors(errors);
				List<Profile> lsProfiles=new ArrayList<Profile>();
				for(ServiceProfile sr:request.getServiceProfiles()){
					Profile pr=new Profile();
					pr.setUserName(sr.getUserName());
					pr.setStatus("Could not update profile");
				}
				response.setServiceProfiles(lsProfiles);
			}else{
					response=cimManager.updateProfileList(request);		
			}
			
		return response;
	}
	
	@POST
	@Path("/v1/latestUserChat")
	@Produces("text/xml")
	@Consumes("text/xml")
	public LatestUserChatResponse getLatestUserChat(LatestUserChatRequest request){
		LatestUserChatResponse response=null;			
		Validation validation=new Validation();
		validation.validateAconyxUsername(request.getAconyxuser());

		Errors errors=validation.getErrorList();
		if(errors.getError().size()>0){
			response=new LatestUserChatResponse();
			response.setErrors(errors);

		}else{
			response=cimManager.getLatestUserChat(request);
		}

		return response;
	}
	
	@POST
	@Path("/v1/userChatHistory")
	@Produces("text/xml")
	@Consumes("text/xml")
	public ChatHistoryResponse getUserChatHistory(ChatHistoryRequest request){
		ChatHistoryResponse response=null;			
		Validation validation=new Validation();
		
			
			validation.validateAconyxUsername(request.getAconyxuser());
			
			Errors errors=validation.getErrorList();
			if(errors.getError().size()>0){
				response=new ChatHistoryResponse();
				response.setErrors(errors);

			}else{
					response=cimManager.getChatHistory(request);
			}
			
		return response;
	}
	
	@POST
	@Path("/v2/userChatHistory")
	@Produces("text/xml")
	@Consumes("text/xml")
	public ChatHistoryResponse getUserChatActivityLog(ChatHistoryRequest request){
		ChatHistoryResponse response=null;			
		Validation validation=new Validation();
		
			
			validation.validateAconyxUsername(request.getAconyxuser());
			
			Errors errors=validation.getErrorList();
			if(errors.getError().size()>0){
				response=new ChatHistoryResponse();
				response.setErrors(errors);

			}else{
					response=cimManager.getChatActivityLog(request);
			}
			
		return response;
	}
	
	@POST
	@Path("/v1/deleteChatHistory")
	@Produces("text/xml")
	@Consumes("text/xml")
	public DeleteHistoryResponse deleteUserChatHistory(DeleteHistoryRequest request){
		DeleteHistoryResponse response=null;			
		Validation validation=new Validation();
		
			
			validation.validateAconyxUsername(request.getUserName());
			
			Errors errors=validation.getErrorList();
			if(errors.getError().size()>0){
				response=new DeleteHistoryResponse();
				response.setErrors(errors);

			}else{
					response=cimManager.deleteChatHistory(request);
			}
			
		return response;
	}
}
