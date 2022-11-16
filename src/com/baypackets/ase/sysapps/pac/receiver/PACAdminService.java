/*
 * PACAdminService.java
 * @author Amit Baxi
 */
package com.baypackets.ase.sysapps.pac.receiver;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import org.apache.log4j.Logger;

import com.baypackets.ase.sysapps.pac.jaxb.*;
import com.baypackets.ase.sysapps.pac.manager.PACManager;
import com.baypackets.ase.sysapps.pac.util.Constants;
import com.baypackets.ase.sysapps.pac.util.ErrorCodes;
import com.baypackets.ase.sysapps.pac.validator.Validation;


@Path("/pac/admin/v1")
public class PACAdminService implements PACReceiver{
	private static Logger logger=Logger.getLogger(PACAdminService.class);
	public static PACManager pacManager;
	static{
		pacManager=PACManager.getInstance();		
	}
	
	@POST
	@Path("/addaconyxuser")
	@Produces("text/xml")
	@Consumes("text/xml")
	public AddAconyxUserResponse addAconyxUser(AddAconyxUserRequest request ){
		if(logger.isDebugEnabled())
			logger.debug("[PAC] PACAdminService: addAconyxUser() entered");
		Validation validation=new Validation();
		boolean checkNull=validation.validateAddAconyxUserRequest(request);
		if(!checkNull){
			String encrypted=request.getEncrypted();
		//	String role=request.getRole();
			validation.validateEncrypted(encrypted);
		//	if(role!=null)
		//	validation.validateRole(role);
		}
		AddAconyxUserResponse response=new AddAconyxUserResponse();		
		Errors errors=validation.getErrorList();
		if(errors.getError().size()>0){
			response.setErrors(errors);
		}
		else{
			
			response=pacManager.addAconyxUser(request);			
		}
		if(logger.isDebugEnabled())
			logger.debug("[PAC] PACAdminService: addAconyxUser() exiting....");
	return response;
	}
	
	@POST
	@Path("/modaconyxuser")
	@Produces("text/xml")
	@Consumes("text/xml")
	public ModifyAconyxUserResponse modifyAconyxUser(
			ModifyAconyxUserRequest request) {
		if(logger.isDebugEnabled())
			logger.debug("[PAC] PACAdminService: modifyAconyxUser() entered");
		Validation validation = new Validation();
		boolean checkNull = validation.validateModifyAconyxUserRequest(request);
		if (!checkNull) {
			String encrypted = request.getEncrypted();
			//String role = request.getRole();
			if (encrypted != null)
				validation.validateEncrypted(encrypted);
			//if (role != null)
				//validation.validateRole(role);
		}
		ModifyAconyxUserResponse response = new ModifyAconyxUserResponse();
		Errors errors = validation.getErrorList();
		if (errors.getError().size() > 0) {
			response.setErrors(errors);
		} else {

			response = pacManager.modifyAconyxUser(request);
		}
		if(logger.isDebugEnabled())
			logger.debug("[PAC] PACAdminService: modifyAconyxUser() exiting....");
		return response;
	}
	
	@POST
	@Path("/delaconyxuser")
	@Produces("text/xml")
	@Consumes("text/xml")
	public DeleteAconyxUserResponse deleteAconyxUser(
			DeleteAconyxUserRequest request) {
		if(logger.isDebugEnabled())
			logger.debug("[PAC] PACAdminService: deleteAconyxUser() entered");
		DeleteAconyxUserResponse response = new DeleteAconyxUserResponse();
		if(PACManager.PAC_CACHE_STATE.equals(Constants.STATE_LOADING)){
			response.setAconyxUsername(request.getAconyxUsername());
			response.setErrors(new Errors(ErrorCodes.ERROR_027,ErrorCodes.ERROR_027_DESC));
			return response;
		}
		Validation validation = new Validation();
		boolean checkNull = validation.validateDeleteAconyxUserRequest(request);
		if (!checkNull) {
			// Validate fields
		}		
		Errors errors = validation.getErrorList();
		if (errors.getError().size() > 0) {
			response.setErrors(errors);
		} else {
			response = pacManager.deleteAconyxUser(request);
		}
		if(logger.isDebugEnabled())
			logger.debug("[PAC] PACAdminService: deleteAconyxUser() exiting....");
		return response;
	}
	
	@POST
	@Path("/getaconyxuserdata")
	@Produces("text/xml")
	@Consumes("text/xml")
	public GetAconyxUserDataResponse getAconyxUserData(
			GetAconyxUserDataRequest request) {
		if(logger.isDebugEnabled())
			logger.debug("[PAC] PACAdminService: getAconyxUserData() entered");
		Validation validation = new Validation();
		boolean checkNull = validation.validateGetAconyxUserDataRequest(request);
		if (!checkNull) {
			// Validate fields
		}
		GetAconyxUserDataResponse response = new GetAconyxUserDataResponse();
		Errors errors = validation.getErrorList();
		if (errors.getError().size() > 0) {
			response.setErrors(errors);
		} else {

			response = pacManager.getAconyxUserData(request);
		}
		if(logger.isDebugEnabled())
			logger.debug("[PAC] PACAdminService: getAconyxUserData() exiting....");
		return response;
	}
	
	@GET
	@Path("/getallaconyxusersdata")
	@Produces("text/xml")
	public GetAllAconyxUsersDataResponse getAllAconyxUsersData(){
		if(logger.isDebugEnabled())
			logger.debug("[PAC] PACAdminService: getAllAconyxUsersData() entered");
		GetAllAconyxUsersDataResponse response = pacManager.getAllAconyxUsersData();
		if(logger.isDebugEnabled())
			logger.debug("[PAC] PACAdminService: getAllAconyxUsersData() exiting....");
		return response;
	}
	
}
