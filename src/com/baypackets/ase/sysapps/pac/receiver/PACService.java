/*
 * PACService.java
 * @author Amit Baxi
 */
package com.baypackets.ase.sysapps.pac.receiver;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import org.apache.log4j.Logger;

import com.baypackets.ase.sysapps.pac.jaxb.AggregatedPresenceRequest;
import com.baypackets.ase.sysapps.pac.jaxb.AggregatedPresenceResponse;
import com.baypackets.ase.sysapps.pac.jaxb.AssignUserChannelsRequest;
import com.baypackets.ase.sysapps.pac.jaxb.AssignUserChannelsResponse;
import com.baypackets.ase.sysapps.pac.jaxb.Channel;
import com.baypackets.ase.sysapps.pac.jaxb.ChannelPresence;
import com.baypackets.ase.sysapps.pac.jaxb.DeleteAllAppChannelsRequest;
import com.baypackets.ase.sysapps.pac.jaxb.DeleteAllAppChannelsResponse;
import com.baypackets.ase.sysapps.pac.jaxb.DeleteAllUserChannelsRequest;
import com.baypackets.ase.sysapps.pac.jaxb.DeleteAllUserChannelsResponse;
import com.baypackets.ase.sysapps.pac.jaxb.DeleteUserChannelsRequest;
import com.baypackets.ase.sysapps.pac.jaxb.DeleteUserChannelsResponse;
import com.baypackets.ase.sysapps.pac.jaxb.EnumeratedPresenceRequest;
import com.baypackets.ase.sysapps.pac.jaxb.EnumeratedPresenceResponse;
import com.baypackets.ase.sysapps.pac.jaxb.Errors;
import com.baypackets.ase.sysapps.pac.jaxb.GetAconyxUsernameRequest;
import com.baypackets.ase.sysapps.pac.jaxb.GetAconyxUsernameResponse;
import com.baypackets.ase.sysapps.pac.jaxb.GetAllAppChannelsRequest;
import com.baypackets.ase.sysapps.pac.jaxb.GetAllAppChannelsResponse;
import com.baypackets.ase.sysapps.pac.jaxb.GetAllUserChannelsRequest;
import com.baypackets.ase.sysapps.pac.jaxb.GetAllUserChannelsResponse;
import com.baypackets.ase.sysapps.pac.jaxb.GetUserChannelRequest;
import com.baypackets.ase.sysapps.pac.jaxb.GetUserChannelResponse;
import com.baypackets.ase.sysapps.pac.jaxb.ModifyUserChannelsRequest;
import com.baypackets.ase.sysapps.pac.jaxb.ModifyUserChannelsResponse;
import com.baypackets.ase.sysapps.pac.jaxb.PresenceRequest;
import com.baypackets.ase.sysapps.pac.jaxb.PresenceResponse;
import com.baypackets.ase.sysapps.pac.jaxb.SubscribeRequest;
import com.baypackets.ase.sysapps.pac.jaxb.SubscribeResponse;
import com.baypackets.ase.sysapps.pac.jaxb.UpdatePresenceRequest;
import com.baypackets.ase.sysapps.pac.jaxb.UpdatePresenceResponse;
import com.baypackets.ase.sysapps.pac.jaxb.UserChannel;
import com.baypackets.ase.sysapps.pac.manager.PACManager;
import com.baypackets.ase.sysapps.pac.util.Constants;
import com.baypackets.ase.sysapps.pac.util.ErrorCodes;
import com.baypackets.ase.sysapps.pac.validator.Validation;

@Path("/pac/service")
public class PACService implements PACReceiver{

	private static Logger logger=Logger.getLogger(PACService.class);
	public static PACManager pacManager;
	static{
		pacManager=PACManager.getInstance();		
	}
	
	@POST
	@Path("/v1/assignuserchannels")
	@Produces("text/xml")
	@Consumes("text/xml")
	public AssignUserChannelsResponse assignUserChannels(AssignUserChannelsRequest request){
		if(logger.isDebugEnabled())
			logger.debug("[PAC] PACService: assignUserChannels() entered");
		AssignUserChannelsResponse response;			
		if(PACManager.PAC_CACHE_STATE.equals(Constants.STATE_LOADING)){
			response=new AssignUserChannelsResponse();
			response.setAconyxUsername(request.getAconyxUsername());
			response.setApplicationId(request.getApplicationId());
			response.setErrors(new Errors(ErrorCodes.ERROR_027,ErrorCodes.ERROR_027_DESC));
			return response;
		}
		Validation validation=new Validation();
			//check request for null or missing values
			boolean checkNull=validation.validateAssignUserChannelsRequest(request);
			if(!checkNull){
				List<Channel> channelList=request.getChannels();
				for (int i = 0; i < channelList.size(); i++) {
						Channel userChannel=channelList.get(i);
						String channelUsername=userChannel.getChannelUsername();
						String encrypted=userChannel.getEncrypted();
						String channelName=userChannel.getChannelName();					
						String channelURL=userChannel.getChannelURL();				
						validation.validateEncrypted(encrypted);					
						if(channelURL!=null)
						validation.validateChannelURL(channelURL,channelName);					
						validation.validateChannelName(channelName);
						validation.validateChannelUsername(channelUsername,channelName);					
					}
				}				
			Errors errors=validation.getErrorList();
			if(errors.getError().size()>0){
				response=new AssignUserChannelsResponse();
				response.setApplicationId(request.getApplicationId());
				response.setAconyxUsername(request.getAconyxUsername());
				response.setErrors(errors);
			}else{
					response=pacManager.assignUserChannels(request);			
			}
			if(logger.isDebugEnabled())
				logger.debug("[PAC] PACService: assignUserChannels() exiting....");
		return response;
	}
	
	@POST
	@Path("/v1/moduserchannels")
	@Produces("text/xml")
	@Consumes("text/xml")
	public ModifyUserChannelsResponse modifyUserChannels(ModifyUserChannelsRequest request){				
		if(logger.isDebugEnabled())
			logger.debug("[PAC] PACService: modifyUserChannels() entered");
		ModifyUserChannelsResponse response;
		if(PACManager.PAC_CACHE_STATE.equals(Constants.STATE_LOADING)){
			response=new ModifyUserChannelsResponse();
			response.setAconyxUsername(request.getAconyxUsername());
			response.setApplicationId(request.getApplicationId());
			response.setErrors(new Errors(ErrorCodes.ERROR_027,ErrorCodes.ERROR_027_DESC));
			return response;
		}
		Validation validation=new Validation();
		boolean checkNull=validation.validateModifyUserChannelsRequest(request);
		if(!checkNull){
			List<Channel> channelList=request.getChannels();
			for (int i = 0; i < channelList.size(); i++) {
					Channel userChannel=channelList.get(i);
					String channelUsername=userChannel.getChannelUsername();
					String encrypted=userChannel.getEncrypted();
					String channelName=userChannel.getChannelName();					
					String channelURL=userChannel.getChannelURL();	
					if(encrypted!=null)
					validation.validateEncrypted(encrypted);					
					if(channelURL!=null)
					validation.validateChannelURL(channelURL,channelName);			
					validation.validateChannelName(channelName);
					validation.validateChannelUsername(channelUsername,channelName);				
				}
			}				
		Errors errors=validation.getErrorList();
		if(errors.getError().size()>0){
			response=new ModifyUserChannelsResponse();
			response.setApplicationId(request.getApplicationId());
			response.setAconyxUsername(request.getAconyxUsername());
			response.setErrors(errors);
		}else{			
			response=pacManager.modifyUserChannels(request);			
		}
		if(logger.isDebugEnabled())
			logger.debug("[PAC] PACService: modifyUserChannels() exiting....");
	return response;
	}
	
	@POST
	@Path("/v1/deluserchannels")
	@Produces("text/xml")
	@Consumes("text/xml")
	public DeleteUserChannelsResponse deleteUserChannels(DeleteUserChannelsRequest request){		
		DeleteUserChannelsResponse response;
		if(logger.isDebugEnabled())
			logger.debug("[PAC] PACService: deleteUserChannels() entered");
		if(PACManager.PAC_CACHE_STATE.equals(Constants.STATE_LOADING)){
			response=new DeleteUserChannelsResponse();
			response.setAconyxUsername(request.getAconyxUsername());
			response.setApplicationId(request.getApplicationId());
			response.setErrors(new Errors(ErrorCodes.ERROR_027,ErrorCodes.ERROR_027_DESC));
			return response;
		}	
		Validation validation=new Validation();
			boolean checkNull=validation.validateDeleteUserChannelsRequest(request);
			if(!checkNull){			
				List<Channel> channelList=request.getChannels();
				for (int i = 0; i < channelList.size(); i++) {
						Channel userChannel=channelList.get(i);
						String channelUsername=userChannel.getChannelUsername();
						String channelName=userChannel.getChannelName();									
						validation.validateChannelName(channelName);
						validation.validateChannelUsername(channelUsername,channelName);				
					}
				}				
			Errors errors=validation.getErrorList();
			if(errors.getError().size()>0){
				response=new DeleteUserChannelsResponse();
				response.setAconyxUsername(request.getAconyxUsername());
				response.setApplicationId(request.getApplicationId());
				response.setErrors(errors);
			}else{
					response=pacManager.deleteUserChannels(request);			
			}
			if(logger.isDebugEnabled())
				logger.debug("[PAC] PACService: deleteUserChannels() exiting....");
			return response;
	}
	
	@POST
	@Path("/v1/getuserchannel")
	@Produces("text/xml")
	@Consumes("text/xml")
	public GetUserChannelResponse getUserChannel(GetUserChannelRequest request){
		if(logger.isDebugEnabled())
			logger.debug("[PAC] PACService: getUserChannel() entered");
		GetUserChannelResponse response=new GetUserChannelResponse();
		if(PACManager.PAC_CACHE_STATE.equals(Constants.STATE_LOADING)){
			response=new GetUserChannelResponse();
			response.setAconyxUsername(request.getAconyxUsername());
			response.setApplicationId(request.getApplicationId());
			response.setErrors(new Errors(ErrorCodes.ERROR_027,ErrorCodes.ERROR_027_DESC));
		}else{
			Validation validation=new Validation();
			boolean checknull=validation.validateGetUserChannelRequest(request);
			if(!checknull){
				String channelUsername=request.getChannelUsername();
				String channelName=request.getChannelName();									
				validation.validateChannelName(channelName);
				validation.validateChannelUsername(channelUsername,channelName);
			}
			Errors errors=validation.getErrorList();
			if(errors.getError().size()>0){
				response=new GetUserChannelResponse();
				response.setAconyxUsername(request.getAconyxUsername());
				response.setApplicationId(request.getApplicationId());
				response.setErrors(errors);
			}else{
				response=pacManager.getUserChannel(request);
			}
		}if(logger.isDebugEnabled())
			logger.debug("[PAC] PACService: deleteUserChannels() exiting....");
		return response;
	}

	@POST
	@Path("/v1/getalluserchannels")
	@Produces("text/xml")
	@Consumes("text/xml")
	public GetAllUserChannelsResponse getAllUserChannels(GetAllUserChannelsRequest request){
		if(logger.isDebugEnabled())
			logger.debug("[PAC] PACService: getAllUserChannels() entered");
		GetAllUserChannelsResponse response;
		if(PACManager.PAC_CACHE_STATE.equals(Constants.STATE_LOADING)){
			response=new GetAllUserChannelsResponse();
			response.setAconyxUsername(request.getAconyxUsername());
			response.setApplicationId(request.getApplicationId());
			response.setErrors(new Errors(ErrorCodes.ERROR_027,ErrorCodes.ERROR_027_DESC));
		}else{
		Validation validation=new Validation();
		boolean checknull=validation.validateGetAllUserChannelsRequest(request);
		if(!checknull){
			//Validation of fields if required
		}
		Errors errors=validation.getErrorList();
		if (errors.getError().size() > 0) {
				response=new GetAllUserChannelsResponse();
				response.setAconyxUsername(request.getAconyxUsername());
				response.setApplicationId(request.getApplicationId());
				response.setErrors(errors);
			} else {
				response = pacManager.getAllUserChannels(request);
			}
		}
		if(logger.isDebugEnabled())
			logger.debug("[PAC] PACService: getAllUserChannels() exiting....");
		return response;
	}
	
	@POST
	@Path("/v1/getallappchannels")
	@Produces("text/xml")
	@Consumes("text/xml")
	public GetAllAppChannelsResponse getAllAppChannels(GetAllAppChannelsRequest request){
		if(logger.isDebugEnabled())
			logger.debug("[PAC] PACService: getAllAppChannels() entered");
		GetAllAppChannelsResponse response;
		if(PACManager.PAC_CACHE_STATE.equals(Constants.STATE_LOADING)){
			response=new GetAllAppChannelsResponse();
			response.setApplicationId(request.getApplicationId());
			response.setErrors(new Errors(ErrorCodes.ERROR_027,ErrorCodes.ERROR_027_DESC));
		}else{
			Validation validation=new Validation();
			boolean checknull=validation.validateGetAllAppChannelsRequest(request);
			if(!checknull){
				//Validation of fields if required
			}
			Errors errors=validation.getErrorList();
			if(errors.getError().size()>0){
				response=new GetAllAppChannelsResponse();
				response.setApplicationId(request.getApplicationId());
				response.setErrors(errors);
			}else{
				response=pacManager.getAllAppChannels(request);
			}
		}
		if(logger.isDebugEnabled())
				logger.debug("[PAC] PACService: getAllAppChannels() exiting....");
		return response;
	}
	
	@POST
	@Path("/v1/delallappchannels")
	@Produces("text/xml")
	@Consumes("text/xml")
	public DeleteAllAppChannelsResponse deleteAllAppChannels(DeleteAllAppChannelsRequest request) {
		if(logger.isDebugEnabled())
			logger.debug("[PAC] PACService: deleteAllAppChannels() entered");
		DeleteAllAppChannelsResponse response;
		if(PACManager.PAC_CACHE_STATE.equals(Constants.STATE_LOADING))
		{
			response = new DeleteAllAppChannelsResponse();
			response.setApplicationId(request.getApplicationId());
			response.setErrors(new Errors(ErrorCodes.ERROR_027,ErrorCodes.ERROR_027_DESC));
		}else{
			Validation validation = new Validation();
			boolean checkNull = validation.validateDeleteAllAppChannelsRequest(request);
			if (!checkNull) {
				// Validation of fields if required
			}
			Errors errors = validation.getErrorList();
			if (errors.getError().size() > 0) {
				response = new DeleteAllAppChannelsResponse();
				response.setApplicationId(request.getApplicationId());
				response.setErrors(errors);
			} else {
				response = pacManager.deleteAllAppChannels(request);
			}
		}if(logger.isDebugEnabled())
			logger.debug("[PAC] PACService: deleteAllAppChannels() exiting....");
		return response;
	}
	
	@POST
	@Path("/v1/delalluserchannels")
	@Produces("text/xml")
	@Consumes("text/xml")
	public DeleteAllUserChannelsResponse deleteAllUserChannels(DeleteAllUserChannelsRequest request){		
		if(logger.isDebugEnabled())
			logger.debug("[PAC] PACService: deleteAllUserChannels() entered");
		DeleteAllUserChannelsResponse response;
		if(PACManager.PAC_CACHE_STATE.equals(Constants.STATE_LOADING))
		{
			response = new DeleteAllUserChannelsResponse();
			response.setAconyxUsername(request.getAconyxUsername());
			response.setApplicationId(request.getApplicationId());
			response.setErrors(new Errors(ErrorCodes.ERROR_027,ErrorCodes.ERROR_027_DESC));
		}else{
			Validation validation=new Validation();
			boolean checkNull=validation.validateDeleteAllUserChannelsRequest(request);
			if (!checkNull) {

			}
			Errors errors=validation.getErrorList();
			if (errors.getError().size() > 0) {
				response = new DeleteAllUserChannelsResponse();
				response.setApplicationId(request.getApplicationId());
				response.setAconyxUsername(request.getAconyxUsername());
				response.setErrors(errors);
			} else {
				response = pacManager.deleteAllUserChannels(request);
			}
		}
		if(logger.isDebugEnabled())
			logger.debug("[PAC] PACService: deleteAllUserChannels() exiting....");
		return response;
	}
			
	@POST
	@Path("/v1/updatepresence")
	@Produces("text/xml")
	@Consumes("text/xml")
	public UpdatePresenceResponse updatePresence(UpdatePresenceRequest request){
		if(logger.isDebugEnabled())
			logger.debug("[PAC] PACService: updatePresence() entered");
		UpdatePresenceResponse response;
		if(PACManager.PAC_CACHE_STATE.equals(Constants.STATE_LOADING))
		{
			response = new UpdatePresenceResponse();
			response.setApplicationId(request.getApplicationId());
			response.setErrors(new Errors(ErrorCodes.ERROR_027,ErrorCodes.ERROR_027_DESC));
		}else{
			Validation validation=new Validation();
			boolean checkNull=validation.validateUpadatePresenceRequest(request);
			//If request contains all mandatory fields then validate request for proper formats 
			if(! checkNull){
				List<ChannelPresence> channelsPresenceList=request.getChannelPresence();
				for (int i = 0; i < channelsPresenceList.size(); i++) {
					ChannelPresence presence=channelsPresenceList.get(i);
					String channelUsername=presence.getChannelUsername();
					String channelName=presence.getChannelName();									
					String status=presence.getStatus();
					validation.validateChannelName(channelName);
					validation.validateChannelUsername(channelUsername,channelName);
					validation.validateStatus(status);
				}
			}
			//In case of request validation error set error codes and return response
			Errors errors=validation.getErrorList();
			if(errors.getError().size()>0){
				response=new UpdatePresenceResponse();
				response.setApplicationId(request.getApplicationId());
				response.setErrors(errors);
			}
			else{
				response=pacManager.updatePresence(request,true);
			}
		}
		if(logger.isDebugEnabled())
			logger.debug("[PAC] PACService: updatePresence() exiting....");
		return response;
	}
	
	@POST
	@Path("/v1/getaggregatedpresence")
	@Produces("text/xml")
	@Consumes("text/xml")
	public AggregatedPresenceResponse getAggregatedPresence(AggregatedPresenceRequest request){
		if(logger.isDebugEnabled())
			logger.debug("[PAC] PACService: getAggregatedPresence() entered");
		AggregatedPresenceResponse response;
		if(PACManager.PAC_CACHE_STATE.equals(Constants.STATE_LOADING))
		{
			response = new AggregatedPresenceResponse();
			response.setAconyxUsername(request.getAconyxUsername());
			response.setApplicationId(request.getApplicationId());
			response.setErrors(new Errors(ErrorCodes.ERROR_027,ErrorCodes.ERROR_027_DESC));
		}else{
			Validation validation=new Validation();
			validation.validateAggPresenceRequest(request);
			Errors errors=validation.getErrorList();
			if(errors.getError().size()>0){
				response=new AggregatedPresenceResponse();
				response.setApplicationId(request.getApplicationId());
				response.setAconyxUsername(request.getAconyxUsername());
				response.setErrors(errors);
			}
			else{
				response=pacManager.getAggregatedPresence(request);
			}
		}
		if(logger.isDebugEnabled())
			logger.debug("[PAC] PACService: getAggregatedPresence() exiting....");
		return response;
	}
	
	@POST
	@Path("/v1/getenumeratedpresence")
	@Produces("text/xml")
	@Consumes("text/xml")
	public EnumeratedPresenceResponse getEnumeratedPresence(EnumeratedPresenceRequest request){
		if(logger.isDebugEnabled())
			logger.debug("[PAC] PACService: getEnumeratedPresence() entered");
		EnumeratedPresenceResponse response;
		if(PACManager.PAC_CACHE_STATE.equals(Constants.STATE_LOADING))
		{
			response = new EnumeratedPresenceResponse();
			response.setApplicationId(request.getApplicationId());
			response.setErrors(new Errors(ErrorCodes.ERROR_027,ErrorCodes.ERROR_027_DESC));
		}else{
			Validation validation=new Validation();
			validation.validateEnumeratedPresenceRequest(request);
			Errors errors=validation.getErrorList();
			if(errors.getError().size()>0){
				response=new EnumeratedPresenceResponse();
				response.setApplicationId(request.getApplicationId());
				response.setErrors(errors);
			}
			else{
				response=pacManager.getEnumeratedPresence(request);
			}
		}
		if(logger.isDebugEnabled())
			logger.debug("[PAC] PACService: getEnumeratedPresence() exiting....");
		return response;
	}
	
	@POST
	@Path("/v1/getpresence")
	@Produces("text/xml")
	@Consumes("text/xml")
	public PresenceResponse getPresence(PresenceRequest request){
		if(logger.isDebugEnabled())
			logger.debug("[PAC] PACService: getPresence() entered");
		PresenceResponse response;
		if(PACManager.PAC_CACHE_STATE.equals(Constants.STATE_LOADING))
		{
			response = new PresenceResponse();
			response.setApplicationId(request.getApplicationId());
			response.setErrors(new Errors(ErrorCodes.ERROR_027,ErrorCodes.ERROR_027_DESC));
		}else{
			//Validation of request
			Validation validation=new Validation();

			boolean checkNull=validation.validatePresenceRequest(request);

			//If request contains all mandatory fields then validate request for proper formats 
			if(! checkNull){
				List<UserChannel> userChannelList=request.getUserChannels();
				for (int i = 0; i < userChannelList.size(); i++) {
					UserChannel userChannel=userChannelList.get(i);
					String channelUsername=userChannel.getChannelUsername();
					String channelName=userChannel.getChannelName();									
					validation.validateChannelName(channelName);
					validation.validateChannelUsername(channelUsername,channelName);				
				}
			}
			//In case of request validation error set error codes and return response
			Errors errors=validation.getErrorList();
			if(errors.getError().size()>0){
				response=new PresenceResponse();
				response.setApplicationId(request.getApplicationId());		
				response.setErrors(errors);
			}
			else{
				response=pacManager.getPresence(request);
			}
		}
		if(logger.isDebugEnabled())
			logger.debug("[PAC] PACService: getPresence() entered");
		return response;
	}
	
	@POST
	@Path("/v1/getaconyxusername")
	@Produces("text/xml")
	@Consumes("text/xml")
	public GetAconyxUsernameResponse getAconyxUsername(GetAconyxUsernameRequest request){
		
		if(logger.isDebugEnabled())
			logger.debug("[PAC] PACService: getAconyxUsername() entered");
		GetAconyxUsernameResponse response;
		if(PACManager.PAC_CACHE_STATE.equals(Constants.STATE_LOADING))
		{
			response = new GetAconyxUsernameResponse();
			response.setApplicationId(request.getApplicationId());
			response.setErrors(new Errors(ErrorCodes.ERROR_027,ErrorCodes.ERROR_027_DESC));
		}else{
			//Validation of request
			Validation validation=new Validation();

			boolean checkNull=validation.validateGetAconyxUsernameRequest(request);

			//If request contains all mandatory fields then validate request for proper formats 
			if(! checkNull){
				List<UserChannel> userChannelList=request.getUserChannels();
				for (int i = 0; i < userChannelList.size(); i++) {
					UserChannel userChannel=userChannelList.get(i);
					String channelUsername=userChannel.getChannelUsername();
					String channelName=userChannel.getChannelName();									
					validation.validateChannelName(channelName);
					validation.validateChannelUsername(channelUsername,channelName);				
				}
			}
			//In case of request validation error set error codes and return response
			Errors errors=validation.getErrorList();
			if(errors.getError().size()>0){
				response=new GetAconyxUsernameResponse();
				response.setApplicationId(request.getApplicationId());		
				response.setErrors(errors);
			}
			else{
				response=pacManager.getAconyxUsername(request);
			}
		}
		if(logger.isDebugEnabled())
			logger.debug("[PAC] PACService: getAconyxUsername() exiting....");
		return response;
	}
	
	@POST
	@Path("/v1/getsubscriberequest")
	@Produces("text/xml")
	@Consumes("text/xml")
	public SubscribeResponse getSubscribeRequest(SubscribeRequest request){
		if(logger.isDebugEnabled())
			logger.debug("[PAC] PACService: getSubscribeRequest() entered");
		
		SubscribeResponse response;
		if(PACManager.PAC_CACHE_STATE.equals(Constants.STATE_LOADING))
		{
			response = new SubscribeResponse();
			response.setErrors(new Errors(ErrorCodes.ERROR_027,ErrorCodes.ERROR_027_DESC));
		}else{
			//Validation of request
			Validation validation=new Validation();
			String channelUsername=request.getChannelUserName();
			String channelName=request.getChannelName();
			validation.validateChannelName(channelName);
			validation.validateChannelUsername(channelUsername,channelName);
			
			//In case of request validation error set error codes and return response
			Errors errors=validation.getErrorList();
			if(errors.getError().size()>0){
				response=new SubscribeResponse();
				response.setErrors(errors);
			}else{
				response=pacManager.generateSubscribeRequest(request);
			}
		}
		
		return response;
	}
	
}
