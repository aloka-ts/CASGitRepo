/*
 * CABService.java
 * @author Amit Baxi
 */
package com.baypackets.ase.sysapps.cab.receiver;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import org.apache.log4j.Logger;

import com.baypackets.ase.sysapps.cab.manager.CABManager;
import com.baypackets.ase.sysapps.cab.util.Constants;
import com.baypackets.ase.sysapps.cab.util.ErrorCodes;
import com.baypackets.ase.sysapps.cab.validator.Validator;
import com.baypackets.ase.sysapps.cab.validator.Validator.EnumOperation;
import com.baypackets.ase.sysapps.cab.jaxb.*;

/**
 * CABService class provides methods to be called on receiving a REST Request.
 */

@Path("/cab/service")
public class CABService {
	private static Logger logger=Logger.getLogger(CABService.class);
	public static CABManager cabManager;
	static{
		cabManager=CABManager.getInstance();		
	}
	
	@POST
	@Path("/v1/createpcc")
	@Produces("text/xml")
	@Consumes("text/xml")
	public CreatePCCResponse createPCC(CreatePCCRequest request){
		if(logger.isDebugEnabled())
			logger.debug("[CAB] CABService: createPCC() entered");
		CreatePCCResponse response;	
		if(CABManager.CAB_APP_STATE.equals(Constants.STATE_LOADING)){
			response=new CreatePCCResponse();
			response.setErrors(new Errors(ErrorCodes.ERROR_030,ErrorCodes.ERROR_030_DESC));
			return response;
		}
		Validator validator=new Validator();
		
			//check request for null or missing values
			boolean checkNull=validator.validateCreatePCCRequest(request);
			if(!checkNull){
				List<PersonalContactCard> pccList=request.getPCCList();
				int size=pccList.size();
				for (int i = 0; i < size; i++) {
					PersonalContactCard personalContactCard=pccList.get(i);
					if(personalContactCard!=null)
						validator.validatePCCFields(personalContactCard);									
					}
				}				
			Errors errors=validator.getErrorList();
			if(errors.getError().size()>0){
				response=new CreatePCCResponse();
				response.setErrors(errors);
			}else{
					response=cabManager.createPCC(request);			
			}
			if(logger.isDebugEnabled())
				logger.debug("[CAB] CABService: createPCC() exiting....");
		return response;
	}

	@POST
	@Path("/v1/modifypcc")
	@Produces("text/xml")
	@Consumes("text/xml")
	public ModifyPCCResponse modifyPCC(ModifyPCCRequest request){
		if(logger.isDebugEnabled())
			logger.debug("[CAB] modifyPCC: modifyPCC() entered");
		ModifyPCCResponse response;	
		
		Validator validator=new Validator();
		
			//check request for null or missing values
			boolean checkNull=validator.validateModifyPCCRequest(request);
			if(!checkNull){
				List<PersonalContactCard> pccList=request.getPCCList();
				int size=pccList.size();
				for (int i = 0; i < size; i++) {
					PersonalContactCard personalContactCard=pccList.get(i);
						validator.validatePCCFields(personalContactCard);									
					}
				}				
			Errors errors=validator.getErrorList();
			if(errors.getError().size()>0){
				response=new ModifyPCCResponse();
				response.setErrors(errors);
			}else{
					response=cabManager.modifyPCC(request);			
			}
			if(logger.isDebugEnabled())
				logger.debug("[CAB] modifyPCC: modifyPCC() exiting....");
		return response;
	}
	
	@POST
	@Path("/v1/deletepcc")
	@Produces("text/xml")
	@Consumes("text/xml")
	public DeletePCCResponse deletePCC(DeletePCCRequest request){
		if(logger.isDebugEnabled())
			logger.debug("[CAB] deletePCC: deletePCC() entered");
		DeletePCCResponse response;	
		if(CABManager.CAB_APP_STATE.equals(Constants.STATE_LOADING)){
			response=new DeletePCCResponse();
			response.setErrors(new Errors(ErrorCodes.ERROR_030,ErrorCodes.ERROR_030_DESC));
			return response;
		}
		Validator validator=new Validator();		
			//check request for null or missing values
			boolean checkNull=validator.validateDeletePCCRequest(request);
			if(!checkNull){
				
				}				
			Errors errors=validator.getErrorList();
			if(errors.getError().size()>0){
				response=new DeletePCCResponse();
				response.setErrors(errors);
			}else{
					response=cabManager.deletePCC(request);			
			}
			if(logger.isDebugEnabled())
				logger.debug("[CAB] deletePCC: deletePCC() exiting....");
		return response;
	}
	
	@POST
	@Path("/v1/getpcc")
	@Produces("text/xml")
	@Consumes("text/xml")
	public GetPCCResponse getPCC(GetPCCRequest request){
		if(logger.isDebugEnabled())
			logger.debug("[CAB] getPCC: getPCC() entered");
		GetPCCResponse response;	
		Validator validator=new Validator();
			//check request for null or missing values
			boolean checkNull=validator.validateGetPCCRequest(request);
			if(!checkNull){
				
				}				
			Errors errors=validator.getErrorList();
			if(errors.getError().size()>0){
				response=new GetPCCResponse();
				response.setErrors(errors);
			}else{
					response=cabManager.getPCC(request);			
			}
			if(logger.isDebugEnabled())
				logger.debug("[CAB] getPCC: getPCC() exiting....");
		return response;
	}
	
	@POST
	@Path("/v1/createcontactviews")
	@Produces("text/xml")
	@Consumes("text/xml")
	public CreateContactViewsResponse createContactViews(CreateContactViewsRequest request){
		if(logger.isDebugEnabled())
			logger.debug("[CAB] CABService: createContactViews() entered");
		CreateContactViewsResponse response;
		if(CABManager.CAB_APP_STATE.equals(Constants.STATE_LOADING)){
			response=new CreateContactViewsResponse();
			response.setErrors(new Errors(ErrorCodes.ERROR_030,ErrorCodes.ERROR_030_DESC));
			return response;
		}
		Validator validator=new Validator();
		if(request!=null)
		{	
			validator.validateContactViews(request.getContactViewList(),EnumOperation.CREATE);
		}
		Errors errors=validator.getErrorList();
		if(errors.getError().size()>0){
			response=new CreateContactViewsResponse();
			response.setErrors(errors);
		}else{
				response=cabManager.createContactViews(request);			
		}
		if(logger.isDebugEnabled())
			logger.debug("[CAB] CABService: createContactViews() exiting....");
		return response;
	}
	
	@POST
	@Path("/v1/modifycontactviews")
	@Produces("text/xml")
	@Consumes("text/xml")
	public ModifyContactViewsResponse modifyContactViews(ModifyContactViewsRequest request){
		if(logger.isDebugEnabled())
			logger.debug("[CAB] CABService: modifyContactViews() entered");
		ModifyContactViewsResponse response;
		if(CABManager.CAB_APP_STATE.equals(Constants.STATE_LOADING)){
			response=new ModifyContactViewsResponse();
			response.setErrors(new Errors(ErrorCodes.ERROR_030,ErrorCodes.ERROR_030_DESC));
			return response;
		}
		Validator validator=new Validator();
		if(request!=null){	
			validator.validateContactViews(request.getContactViewList(),EnumOperation.MODIFY);			
		}
		Errors errors=validator.getErrorList();
		if(errors.getError().size()>0){
			response=new ModifyContactViewsResponse();
			response.setErrors(errors);
		}else{
				response=cabManager.modifyContactViews(request);			
		}
		if(logger.isDebugEnabled())
			logger.debug("[CAB] CABService: modifyContactViews() exiting....");
		return response;
	}
	
	@POST
	@Path("/v1/deletecontactviews")
	@Produces("text/xml")
	@Consumes("text/xml")
	public DeleteContactViewsResponse deleteContactViews(DeleteContactViewsRequest request){
		if(logger.isDebugEnabled())
			logger.debug("[CAB] CABService: deleteContactViews() entered");
		DeleteContactViewsResponse response;
		if(CABManager.CAB_APP_STATE.equals(Constants.STATE_LOADING)){
			response=new DeleteContactViewsResponse();
			response.setErrors(new Errors(ErrorCodes.ERROR_030,ErrorCodes.ERROR_030_DESC));
			return response;
		}
		Validator validator=new Validator();
		if(request!=null){	
			validator.validateContactViews(request.getContactViewList(),EnumOperation.DELETE);			
		}
		Errors errors=validator.getErrorList();
		if(errors.getError().size()>0){
			response=new DeleteContactViewsResponse();
			response.setErrors(errors);
		}else{
			response=cabManager.deleteContactViews(request);			
		}
		if(logger.isDebugEnabled())
			logger.debug("[CAB] CABService: deleteContactViews() exiting");
		return response;
	}
	
	@POST
	@Path("/v1/getcontactviews")
	@Produces("text/xml")
	@Consumes("text/xml")
	public GetContactViewsResponse getContactViews(GetContactViewsRequest request){
		if(logger.isDebugEnabled())
			logger.debug("[CAB] CABService: getContactViews() entered");
		GetContactViewsResponse response;		
		Validator validator=new Validator();	
		validator.validateGetContactViewsRequest(request);			
		Errors errors=validator.getErrorList();
		if(errors.getError().size()>0){
			response=new GetContactViewsResponse();
			response.setErrors(errors);
		}else{
			response=cabManager.getContactViews(request);			
		}
		if(logger.isDebugEnabled())
			logger.debug("[CAB] CABService: getContactViews() exiting");
		return response;
	}
	@POST
	@Path("/v1/createaddressbookgroups")
	@Produces("text/xml")
	@Consumes("text/xml")
	public CreateAddressBookGroupsResponse createAddressBookGroups(CreateAddressBookGroupsRequest request){
		if(logger.isDebugEnabled())
			logger.debug("[CAB] CABService: createAddressBookGroups() entered");
		CreateAddressBookGroupsResponse response;
		if(CABManager.CAB_APP_STATE.equals(Constants.STATE_LOADING)){
			response=new CreateAddressBookGroupsResponse();
			response.setErrors(new Errors(ErrorCodes.ERROR_030,ErrorCodes.ERROR_030_DESC));
			return response;
		}
		Validator validator=new Validator();
		boolean checkNull=validator.validateCreateAddressBookGroupsRequest(request);
		if(!checkNull){
			List<AddressBookGroup> groupList=request.getGroupList();
			int size=groupList.size();
			for (int i = 0; i < size; i++) {
				AddressBookGroup group=groupList.get(i);
					validator.validateAddressBookGroup(group,true,true,false,false);									
				}
			}				
		Errors errors=validator.getErrorList();
		if(errors.getError().size()>0){
			response=new CreateAddressBookGroupsResponse();
			response.setErrors(errors);
		}else{
				response=cabManager.createAddressBookGroups(request);			
		}
		if(logger.isDebugEnabled())
			logger.debug("[CAB] CABService: createAddressBookGroups() exiting....");
	return response;
	}
	
	@POST
	@Path("/v1/deleteaddressbookgroups")
	@Produces("text/xml")
	@Consumes("text/xml")
	public DeleteAddressBookGroupsResponse deleteAddressBookGroups(DeleteAddressBookGroupsRequest request){
		if(logger.isDebugEnabled())
			logger.debug("[CAB] CABService: deleteAddressBookGroups() entered");
		DeleteAddressBookGroupsResponse response;
		if(CABManager.CAB_APP_STATE.equals(Constants.STATE_LOADING)){
			response=new DeleteAddressBookGroupsResponse();
			response.setErrors(new Errors(ErrorCodes.ERROR_030,ErrorCodes.ERROR_030_DESC));
			return response;
		}
		Validator validator=new Validator();
		boolean checkNull=validator.validateDeleteAddressBookGroupsRequest(request);
		if(!checkNull){
			List<AddressBookGroup> groupList=request.getGroupList();
			int size=groupList.size();
			for (int i = 0; i < size; i++) {
				AddressBookGroup group=groupList.get(i);
					validator.validateAddressBookGroup(group,true,false,false,true);									
				}
			}				
		Errors errors=validator.getErrorList();
		if(errors.getError().size()>0){
			response=new DeleteAddressBookGroupsResponse();
			response.setErrors(errors);
		}else{
				response=cabManager.deleteAddressBookGroups(request);			
		}
		if(logger.isDebugEnabled())
			logger.debug("[CAB] CABService: deleteAddressBookGroups() exiting....");
	return response;
	}
	
	@POST
	@Path("/v1/addtoaddressbookgroups")
	@Produces("text/xml")
	@Consumes("text/xml")
	public AddToAddressBookGroupsResponse addToAddressBookGroups(AddToAddressBookGroupsRequest request){
		if(logger.isDebugEnabled())
			logger.debug("[CAB] CABService: addToAddressBookGroups() entered");
		AddToAddressBookGroupsResponse response= null;
		if(CABManager.CAB_APP_STATE.equals(Constants.STATE_LOADING)){
			response=new AddToAddressBookGroupsResponse();
			response.setErrors(new Errors(ErrorCodes.ERROR_030,ErrorCodes.ERROR_030_DESC));
			return response;
		}
		Validator validator=new Validator();
		boolean checkNull=validator.validateAddToAddressBookGroupsRequest(request);
		if(!checkNull){
			List<AddressBookGroup> groupList=request.getGroupList();
			int size=groupList.size();
			for (int i = 0; i < size; i++) {
				AddressBookGroup group=groupList.get(i);
					validator.validateAddressBookGroup(group,false,false,true,false);									
				}
			}	
		Errors errors=validator.getErrorList();
		if(errors.getError().size()>0){
			response=new AddToAddressBookGroupsResponse();
			response.setErrors(errors);
		}else{
				response=cabManager.addToAddressBookGroups(request);			
		}
		if(logger.isDebugEnabled())
			logger.debug("[CAB] CABService: addToAddressBookGroups() exiting....");
		return response;
	}
	
	@POST
	@Path("/v1/removefromaddressbookgroups")
	@Produces("text/xml")
	@Consumes("text/xml")
	public RemoveFromAddressBookGroupsResponse removeFromAddressBookGroups(RemoveFromAddressBookGroupsRequest request){
		if(logger.isDebugEnabled())
			logger.debug("[CAB] CABService: removeFromAddressBookGroups() entered");
		RemoveFromAddressBookGroupsResponse response= null;
		if(CABManager.CAB_APP_STATE.equals(Constants.STATE_LOADING)){
			response=new RemoveFromAddressBookGroupsResponse();
			response.setErrors(new Errors(ErrorCodes.ERROR_030,ErrorCodes.ERROR_030_DESC));
			return response;
		}
		Validator validator=new Validator();
		boolean checkNull=validator.validateRemoveFromAddressBookGroupsRequest(request);
		if(!checkNull){
			List<AddressBookGroup> groupList=request.getGroupList();
			int size=groupList.size();
			for (int i = 0; i < size; i++) {
				AddressBookGroup group=groupList.get(i);
					validator.validateAddressBookGroup(group,false,false,true,false);									
				}
			}	
		Errors errors=validator.getErrorList();
		if(errors.getError().size()>0){
			response=new RemoveFromAddressBookGroupsResponse();
			response.setErrors(errors);
		}else{
				response=cabManager.removeFromAddressBookGroups(request);			
		}
		if(logger.isDebugEnabled())
			logger.debug("[CAB] CABService: removeFromAddressBookGroups() exiting....");
		return response;
	}
	
	@POST
	@Path("/v1/associatecontactviews")
	@Produces("text/xml")
	@Consumes("text/xml")
	public AssociateContactViewsResponse associateContactViews (AssociateContactViewsRequest request){
		if(logger.isDebugEnabled())
			logger.debug("[CAB] CABService: associateContactViews() entered");
		AssociateContactViewsResponse response= null;
		if(CABManager.CAB_APP_STATE.equals(Constants.STATE_LOADING)){
			response=new AssociateContactViewsResponse();
			response.setErrors(new Errors(ErrorCodes.ERROR_030,ErrorCodes.ERROR_030_DESC));
			return response;
		}
		Validator validator=new Validator();
		boolean checkNull=validator.validateAssociateContactViewsRequest(request);
		if(!checkNull){
			List<AddressBookGroup> groupList=request.getGroupList();
			int size=groupList.size();
			for (int i = 0; i < size; i++) {
				AddressBookGroup group=groupList.get(i);
					validator.validateAddressBookGroup(group,false,true,false,false);									
				}
			}	
		Errors errors=validator.getErrorList();
		if(errors.getError().size()>0){
			response=new AssociateContactViewsResponse();
			response.setErrors(errors);
		}else{
				response=cabManager.associateContactViews(request);			
		}
		if(logger.isDebugEnabled())
			logger.debug("[CAB] CABService: associateContactViews() exiting....");
		return response;
	}
	
	@POST
	@Path("/v1/getaddressbookgroups")
	@Produces("text/xml")
	@Consumes("text/xml")
	public GetAddressBookGroupsResponse getAddressBookGroups (GetAddressBookGroupsRequest request){
		if(logger.isDebugEnabled())
			logger.debug("[CAB] CABService: getAddressBookGroups() entered");
		GetAddressBookGroupsResponse response= null;
		if(CABManager.CAB_APP_STATE.equals(Constants.STATE_LOADING)){
			response=new GetAddressBookGroupsResponse();
			response.setErrors(new Errors(ErrorCodes.ERROR_030,ErrorCodes.ERROR_030_DESC));
			return response;
		}
		Validator validator=new Validator();
		boolean checkNull=validator.validateGetAddressBookGroupsRequest(request);
		if(!checkNull){
			List<AddressBookGroup> groupList=request.getGroupList();
			int size=groupList.size();
			for (int i = 0; i < size; i++) {
				AddressBookGroup group=groupList.get(i);
					validator.validateAddressBookGroup(group,false,false,false,false);									
				}
			}	
		Errors errors=validator.getErrorList();
		if(errors.getError().size()>0){
			response=new GetAddressBookGroupsResponse();
			response.setErrors(errors);
		}else{
				response=cabManager.getAddressBookGroups(request);			
		}
		if(logger.isDebugEnabled())
			logger.debug("[CAB] CABService: getAddressBookGroups() exiting....");
		return response;
	}
	
	@POST
	@Path("/v1/getalladdressbookgroups")
	@Produces("text/xml")
	@Consumes("text/xml")
	public GetAllAddressBookGroupsResponse getAllAddressBookGroups (GetAllAddressBookGroupsRequest request){
		if(logger.isDebugEnabled())
			logger.debug("[CAB] CABService: getAllAddressBookGroups() entered");
		GetAllAddressBookGroupsResponse response= null;
		Validator validator=new Validator();
		validator.validateGetAllAddressBookGroupsRequest(request);	
		Errors errors=validator.getErrorList();
		if(errors.getError().size()>0){
			response=new GetAllAddressBookGroupsResponse();
			response.setErrors(errors);
		}else{
				response=cabManager.getAllAddressBookGroups(request);			
		}
		if(logger.isDebugEnabled())
			logger.debug("[CAB] CABService: getAllAddressBookGroups() exiting....");
		return response;
	}
	
	@POST
	@Path("/v1/getmemberdetails")
	@Produces("text/xml")
	@Consumes("text/xml")
	public GetMemberDetailsResponse getMemberDetails(GetMemberDetailsRequest request){
		if(logger.isDebugEnabled())
			logger.debug("[CAB] CABService: getMemberDetails() entered");
		GetMemberDetailsResponse response=null;
		Validator validator=new Validator();
		boolean checkNull=validator.validateGetMemberDetailsRequest(request);
		if(!checkNull){
			validator.validateMemberList(request.getMemberList());
		}
		Errors errors=validator.getErrorList();
		if(errors.getError().size()>0){
			response=new GetMemberDetailsResponse();
			response.setErrors(errors);
		}else{
				response=cabManager.getMemberDetails(request);			
		}
		if(logger.isDebugEnabled())
			logger.debug("[CAB] CABService: getMemberDetails() exiting....");
		return response;
	}
	
	@POST
	@Path("/v1/searchusers")
	@Produces("text/xml")
	@Consumes("text/xml")
	public SearchUsersResponse searchUsers(SearchUsersRequest request){
		if(logger.isDebugEnabled())
			logger.debug("[CAB] CABService: searchUsers() entered");
		SearchUsersResponse response=null;
		Validator validator=new Validator();
		boolean checkNull=validator.validateSearchUserRequest(request);
		if(!checkNull){
			validator.validateSearchBy(request.getSearchBy());
		}
		Errors errors=validator.getErrorList();
		if(errors.getError().size()>0){
			response=new SearchUsersResponse();
			response.setErrors(errors);
		}else{
					response=cabManager.searchUsers(request);			
		}
		if(logger.isDebugEnabled())
			logger.debug("[CAB] CABService: searchUsers() exiting....");
		return response;
	}
	
				@POST
    @Path("/v1/modifymembers")
	  @Produces({"text/xml"})
    @Consumes({"text/xml"})
    public ModifyAddressBookGroupMembersResponse modifyAddressBookGroupMembers(ModifyAddressBookGroupMembersRequest request) {
					if(logger.isDebugEnabled())
						logger.debug("[CAB] CABService: modifyAddressBookGroupMembers() entered");
					ModifyAddressBookGroupMembersResponse response =null;			
					if (CABManager.CAB_APP_STATE.equals("loading")) {
						response = new ModifyAddressBookGroupMembersResponse();
						response.setErrors(new Errors("CAB_030", "CAB Service Temporary Unavailable for this operation."));
						return response;
					}
					Validator validator = new Validator();
					boolean checkNull = validator.validateModifyAddressBookGroupMembersRequest(request);
					if (!checkNull) {
						List<ModifyAddressBookGroup> groupList = request.getGroupList();
						int size = groupList.size();
						for (int i = 0; i < size; i++) {
							ModifyAddressBookGroup group = (ModifyAddressBookGroup)groupList.get(i);
							validator.validateModifyAddressBookGroup(group);
						}
					}
					Errors errors = validator.getErrorList();
					if (errors.getError().size() > 0) {
						response = new ModifyAddressBookGroupMembersResponse();
						response.setErrors(errors);
					} else {
						response = cabManager.modifyAddressBookGroupMembers(request);
					}
					if(logger.isDebugEnabled())
						logger.debug("[CAB] CABService: modifyAddressBookGroupMembers() exiting....");
					return response;
				}
 
}
