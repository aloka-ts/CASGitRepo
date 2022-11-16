/*
 * CABManager.java
 * @author Amit Baxi
 */
package com.baypackets.ase.sysapps.cab.manager;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;
import com.baypackets.ase.common.Registry;
import com.baypackets.ase.control.AseRoles;
import com.baypackets.ase.control.ClusterManager;
import com.baypackets.ase.control.PartitionInfo;
import com.baypackets.ase.control.RoleChangeListener;
import com.baypackets.ase.sysapps.cab.dao.CABDAO;
import com.baypackets.ase.sysapps.cab.dao.rdbms.CABDAOImpl;
import com.baypackets.ase.sysapps.cab.jaxb.AddToAddressBookGroupsRequest;
import com.baypackets.ase.sysapps.cab.jaxb.AddToAddressBookGroupsResponse;
import com.baypackets.ase.sysapps.cab.jaxb.AddressBookGroup;
import com.baypackets.ase.sysapps.cab.jaxb.AssociateContactViewsRequest;
import com.baypackets.ase.sysapps.cab.jaxb.AssociateContactViewsResponse;
import com.baypackets.ase.sysapps.cab.jaxb.ContactView;
import com.baypackets.ase.sysapps.cab.jaxb.CreateAddressBookGroupsRequest;
import com.baypackets.ase.sysapps.cab.jaxb.CreateAddressBookGroupsResponse;
import com.baypackets.ase.sysapps.cab.jaxb.CreateContactViewsRequest;
import com.baypackets.ase.sysapps.cab.jaxb.CreateContactViewsResponse;
import com.baypackets.ase.sysapps.cab.jaxb.CreatePCCRequest;
import com.baypackets.ase.sysapps.cab.jaxb.CreatePCCResponse;
import com.baypackets.ase.sysapps.cab.jaxb.DeleteAddressBookGroupsRequest;
import com.baypackets.ase.sysapps.cab.jaxb.DeleteAddressBookGroupsResponse;
import com.baypackets.ase.sysapps.cab.jaxb.DeleteContactViewsRequest;
import com.baypackets.ase.sysapps.cab.jaxb.DeleteContactViewsResponse;
import com.baypackets.ase.sysapps.cab.jaxb.DeletePCCRequest;
import com.baypackets.ase.sysapps.cab.jaxb.DeletePCCResponse;
import com.baypackets.ase.sysapps.cab.jaxb.Errors;
import com.baypackets.ase.sysapps.cab.jaxb.GetAddressBookGroupsRequest;
import com.baypackets.ase.sysapps.cab.jaxb.GetAddressBookGroupsResponse;
import com.baypackets.ase.sysapps.cab.jaxb.GetAllAddressBookGroupsRequest;
import com.baypackets.ase.sysapps.cab.jaxb.GetAllAddressBookGroupsResponse;
import com.baypackets.ase.sysapps.cab.jaxb.GetContactViewsRequest;
import com.baypackets.ase.sysapps.cab.jaxb.GetContactViewsResponse;
import com.baypackets.ase.sysapps.cab.jaxb.GetMemberDetailsRequest;
import com.baypackets.ase.sysapps.cab.jaxb.GetMemberDetailsResponse;
import com.baypackets.ase.sysapps.cab.jaxb.GetPCCRequest;
import com.baypackets.ase.sysapps.cab.jaxb.GetPCCResponse;
import com.baypackets.ase.sysapps.cab.jaxb.ModifyAddressBookGroup;
import com.baypackets.ase.sysapps.cab.jaxb.ModifyAddressBookGroupMembersRequest;
import com.baypackets.ase.sysapps.cab.jaxb.ModifyAddressBookGroupMembersResponse;
import com.baypackets.ase.sysapps.cab.jaxb.ModifyContactViewsRequest;
import com.baypackets.ase.sysapps.cab.jaxb.ModifyContactViewsResponse;
import com.baypackets.ase.sysapps.cab.jaxb.ModifyPCCRequest;
import com.baypackets.ase.sysapps.cab.jaxb.ModifyPCCResponse;
import com.baypackets.ase.sysapps.cab.jaxb.PersonalContactCard;
import com.baypackets.ase.sysapps.cab.jaxb.RemoveFromAddressBookGroupsRequest;
import com.baypackets.ase.sysapps.cab.jaxb.RemoveFromAddressBookGroupsResponse;
import com.baypackets.ase.sysapps.cab.jaxb.SearchUsersRequest;
import com.baypackets.ase.sysapps.cab.jaxb.SearchUsersResponse;
import com.baypackets.ase.sysapps.cab.maps.CABDBMaps;
import com.baypackets.ase.sysapps.cab.util.Configuration;
import com.baypackets.ase.sysapps.cab.util.Constants;
import com.baypackets.ase.sysapps.cab.util.ErrorCodes;

/**
 * CABManager class performs requested operation. 
 * It updates database by using methods of CABDAO interface and generate response of the RESTful request. 
 */
public class CABManager implements RoleChangeListener {
	public static String CAB_APP_STATE=Constants.STATE_INIT;
	private static final String KEY_SEPERATOR = "|";
	private static Logger logger=Logger.getLogger(CABManager.class);
	private static CABManager cabManager=new CABManager();
	public static CABDAO dao;
	private static CABDBMaps cabDBMaps;
	private CABManager(){}
	public static ConcurrentHashMap<String, Integer> FIELD_ID_MAP=new ConcurrentHashMap<String, Integer>();
	public static List<String> DEFAULT_CONTACT_VIEW_FIELDS=new LinkedList<String>();
	public static CABManager getInstance() { 
		return cabManager; 
	}
	static{
		Properties properties = new Properties();
		try {
			properties.load(new FileInputStream(System.getProperty(Constants.ASE_HOME) +File.separator+Constants.FILE_PROPERTIES));

		} catch(FileNotFoundException e){
			logger.error("FileNotFoundException occured while loading the properties file " + e);
		} catch (IOException e) {
			logger.error("IOException occured while loading the properties file" + e);
		}
		Configuration config=Configuration.getInstance();
		config.setParamValue(Constants.PROP_CAB_DATASOURCE_NAME, properties.getProperty(Constants.PROP_CAB_DATASOURCE_NAME));
		dao=new CABDAOImpl();
		try {
			//This method will load FIELD_ID map and DEFAULT_CONTACT_VIEW_FIELDS map from db...
			dao.loadPCCFieldMapFromDB();
			cabDBMaps=CABDBMaps.getInstance();
			ClusterManager clusterManager = (ClusterManager)Registry.lookup(com.baypackets.ase.util.Constants.NAME_CLUSTER_MGR);
	        clusterManager.registerRoleChangeListener(cabManager,com.baypackets.ase.util.Constants.RCL_SYSAPPS_PRIORITY);
		} catch (SQLException e) {
			logger.error("Exception in loading initial maps"+e.toString());
		} catch (Exception e) {
			logger.error("Exception in loading initial maps"+e.toString());
		}
	}
	public CreatePCCResponse createPCC(CreatePCCRequest request) {
		
		if(logger.isDebugEnabled())
			logger.debug("[CAB] CABManager: createPCC() entered");
		CreatePCCResponse response=new CreatePCCResponse();		
		List<PersonalContactCard> pccList=null;
		try{
			pccList=dao.createPCC(request.getPCCList());
		}
		catch (SQLException e) {
			logger.error("SQLException in createPCC()"+e.getMessage());
			Errors errors = new Errors();
			errors.addError(ErrorCodes.ERROR_021, ErrorCodes.ERROR_021_DESC+":SQLException");
			response.setErrors(errors);
		}
		catch (Exception e) {
			logger.error("Exception in createPCC()",e);
			Errors errors = new Errors();
			errors.addError(ErrorCodes.ERROR_021, ErrorCodes.ERROR_021_DESC+":Exception");
			response.setErrors(errors);
		}	
		if(pccList!=null)		
			response.setPCCList(pccList);
		if(logger.isDebugEnabled())
			logger.debug("[CAB] CABManager: createPCC() exiting...");
		return response;		
	}

	public ModifyPCCResponse modifyPCC(ModifyPCCRequest request) {
		if(logger.isDebugEnabled())
		logger.debug("[CAB] modifyPCC: modifyPCC() entered");
		ModifyPCCResponse response=new ModifyPCCResponse();		
		List<PersonalContactCard> pccList=null;
		try{
			pccList=dao.modifyPCC(request.getPCCList());
		}
		catch (SQLException e) {
			logger.error("SQLException in modifyPCC()"+e.getMessage());
			Errors errors = new Errors();
			errors.addError(ErrorCodes.ERROR_021, ErrorCodes.ERROR_021_DESC+":SQLException");
			response.setErrors(errors);
		}
		catch (Exception e) {
			logger.error("Exception in modifyPCC()",e);
			Errors errors = new Errors();
			errors.addError(ErrorCodes.ERROR_021, ErrorCodes.ERROR_021_DESC+":Exception");
			response.setErrors(errors);
		}	
		if(pccList!=null)		
			response.setPCCList(pccList);
		if(logger.isDebugEnabled())
			logger.debug("[CAB] modifyPCC: modifyPCC() exiting...");
		return response;				
	}

	public DeletePCCResponse deletePCC(DeletePCCRequest request) {
		if(logger.isDebugEnabled())
			logger.debug("[CAB] deletePCC: deletePCC() entered");
		DeletePCCResponse response=new DeletePCCResponse();		
		List<PersonalContactCard> pccList=null;
		try{
			pccList=dao.deletePCC(request.getAconyxUsernameList());
		}
		catch (SQLException e) {
			logger.error("SQLException in deletePCC()"+e.getMessage());
			Errors errors = new Errors();
			errors.addError(ErrorCodes.ERROR_021, ErrorCodes.ERROR_021_DESC+":SQLException");
			response.setErrors(errors);
		}
		catch (Exception e) {
			logger.error("Exception in deletePCC()",e);
			Errors errors = new Errors();
			errors.addError(ErrorCodes.ERROR_021, ErrorCodes.ERROR_021_DESC+":Exception");
			response.setErrors(errors);
		}	
		if(pccList!=null)		
			response.setPCCList(pccList);
		if(logger.isDebugEnabled())
			logger.debug("[CAB] deletePCC: deletePCC() exiting....");
		return response;	
	}

	public GetPCCResponse getPCC(GetPCCRequest request) {
		if(logger.isDebugEnabled())
			logger.debug("[CAB] getPCC: getPCC() entered");
		GetPCCResponse response=new GetPCCResponse();		
		List<PersonalContactCard> pccList=null;
		try{
			pccList=dao.getPCC(request.getAconyxUsernameList());
		}
		catch (SQLException e) {
			logger.error("SQLException in getPCC()"+e.getMessage());
			Errors errors = new Errors();
			errors.addError(ErrorCodes.ERROR_021, ErrorCodes.ERROR_021_DESC+":SQLException");
			response.setErrors(errors);
		}
		catch (Exception e) {
			logger.error("Exception in getPCC()",e);
			Errors errors = new Errors();
			errors.addError(ErrorCodes.ERROR_021, ErrorCodes.ERROR_021_DESC+":Exception");
			response.setErrors(errors);
		}	
		if(pccList!=null)		
			response.setPCCList(pccList);
		if(logger.isDebugEnabled())
			logger.debug("[CAB] getPCC: getPCC() exiting....");
		return response;	
	}	

	public CreateContactViewsResponse createContactViews(
			CreateContactViewsRequest request) {
		if(logger.isDebugEnabled())
			logger.debug("[CAB] CABManager: createContactViews() entered");
		CreateContactViewsResponse response=new CreateContactViewsResponse();		
		List<ContactView> contactViewList=null;
		List<ContactView> invalidContactViewList=new LinkedList<ContactView>();
		List<ContactView> inputList=request.getContactViewList();
		int size=inputList.size();
		ConcurrentHashMap<String, Long> contactViewMap=cabDBMaps.getConactViewMap();
		for(int i=0;i<size;i++){
			ContactView cview=inputList.get(i);
			String key=cview.getAconyxUsername()+KEY_SEPERATOR+cview.getName();
			if(contactViewMap.containsKey(key)){
				cview.setStatus(Constants.STATUS_ALREADY_CONFIGURED);
				invalidContactViewList.add(inputList.remove(i));
				i--;size--;
			}
		}
		try{
			contactViewList=dao.createContactViews(inputList);
		}
		catch (SQLException e) {
			logger.error("SQLException in createContactViews()"+e.getMessage());
			Errors errors = new Errors();
			errors.addError(ErrorCodes.ERROR_021, ErrorCodes.ERROR_021_DESC+":SQLException");
			response.setErrors(errors);
		}
		catch (Exception e) {
			logger.error("Exception in createContactViews()",e);
			Errors errors = new Errors();
			errors.addError(ErrorCodes.ERROR_021, ErrorCodes.ERROR_021_DESC+":Exception");
			response.setErrors(errors);
		}	
		if(contactViewList!=null){		
			contactViewList.addAll(invalidContactViewList);
			response.setContactViewList(contactViewList);
		}
		if(logger.isDebugEnabled())
			logger.debug("[CAB] CABManager: createContactViews() exiting...");
		return response;	
	}
	
	public ModifyContactViewsResponse modifyContactViews(
			ModifyContactViewsRequest request) {
		if(logger.isDebugEnabled())
			logger.debug("[CAB] CABManager: modifyContactViews() entered");
		ModifyContactViewsResponse response=new ModifyContactViewsResponse();		
		List<ContactView> contactViewList=null;
		List<ContactView> invalidContactViewList=new LinkedList<ContactView>();
		List<ContactView> inputList=request.getContactViewList();
		int size=inputList.size();
		ConcurrentHashMap<String, Long> contactViewMap=cabDBMaps.getConactViewMap();
		for(int i=0;i<size;i++){
			ContactView cview=inputList.get(i);
			String key=cview.getAconyxUsername()+KEY_SEPERATOR+cview.getName();
			if(!contactViewMap.containsKey(key)){
				cview.setStatus(Constants.STATUS_NOT_CONFIGURED);
				invalidContactViewList.add(inputList.remove(i));
				i--;size--;
			}
		}
		try{
			contactViewList=dao.modifyContactViews(inputList);
		}
		catch (SQLException e) {
			logger.error("SQLException in modifyContactViews()"+e.getMessage());
			Errors errors = new Errors();
			errors.addError(ErrorCodes.ERROR_021, ErrorCodes.ERROR_021_DESC+":SQLException");
			response.setErrors(errors);
		}
		catch (Exception e) {
			logger.error("Exception in modifyContactViews()",e);
			Errors errors = new Errors();
			errors.addError(ErrorCodes.ERROR_021, ErrorCodes.ERROR_021_DESC+":Exception");
			response.setErrors(errors);
		}	
		if(contactViewList!=null){		
			contactViewList.addAll(invalidContactViewList);
			response.setContactViewList(contactViewList);
			}
		if(logger.isDebugEnabled())
			logger.debug("[CAB] CABManager: modifyContactViews() exiting....");
		return response;	
	}
	
	public DeleteContactViewsResponse deleteContactViews(
			DeleteContactViewsRequest request) {
		if(logger.isDebugEnabled())
			logger.debug("[CAB] CABManager: deleteContactViews() entered");
		DeleteContactViewsResponse response=new DeleteContactViewsResponse();		
		List<ContactView> contactViewList=null;
		List<ContactView> invalidContactViewList=new LinkedList<ContactView>();
		List<ContactView> inputList=request.getContactViewList();
		int size=inputList.size();
		ConcurrentHashMap<String, Long> contactViewMap=cabDBMaps.getConactViewMap();
		for(int i=0;i<size;i++){
			ContactView cview=inputList.get(i);
			String key=cview.getAconyxUsername()+KEY_SEPERATOR+cview.getName();
			if(!contactViewMap.containsKey(key)){
				cview.setStatus(Constants.STATUS_NOT_CONFIGURED);
				invalidContactViewList.add(inputList.remove(i));
				i--;size--;
			}
		}
		try{
			contactViewList=dao.deleteContactViews(inputList);
		}
		catch (SQLException e) {
			logger.error("SQLException in deleteContactViews()"+e.getMessage());
			Errors errors = new Errors();
			errors.addError(ErrorCodes.ERROR_021, ErrorCodes.ERROR_021_DESC+":SQLException");
			response.setErrors(errors);
		}
		catch (Exception e) {
			logger.error("Exception in deleteContactViews()",e);
			Errors errors = new Errors();
			errors.addError(ErrorCodes.ERROR_021, ErrorCodes.ERROR_021_DESC+":Exception");
			response.setErrors(errors);
		}	
		if(contactViewList!=null){		
			contactViewList.addAll(invalidContactViewList);
			response.setContactViewList(contactViewList);
			}
		if(logger.isDebugEnabled())
			logger.debug("[CAB] CABManager: deleteContactViews() exiting....");
		return response;	
	}
	
	public GetContactViewsResponse getContactViews(
			GetContactViewsRequest request) {
		if(logger.isDebugEnabled())
			logger.debug("[CAB] CABManager: getContactViews() entered");
		GetContactViewsResponse response=new GetContactViewsResponse();		
		List<ContactView> contactViewList=null;
		try{
			contactViewList=dao.getContactViews(request.getAconyxUsernameList());
		}
		catch (SQLException e) {
			logger.error("SQLException in getContactViews()"+e.getMessage());
			Errors errors = new Errors();
			errors.addError(ErrorCodes.ERROR_021, ErrorCodes.ERROR_021_DESC+":SQLException");
			response.setErrors(errors);
		}
		catch (Exception e) {
			logger.error("Exception in getContactViews()",e);
			Errors errors = new Errors();
			errors.addError(ErrorCodes.ERROR_021, ErrorCodes.ERROR_021_DESC+":Exception");
			response.setErrors(errors);
		}	
		if(contactViewList!=null)		
			response.setContactViewList(contactViewList);
		if(logger.isDebugEnabled())
			logger.debug("[CAB] CABManager: getContactViews() exiting....");
		return response;	
	}

	public CreateAddressBookGroupsResponse createAddressBookGroups(
			CreateAddressBookGroupsRequest request) {
		if(logger.isDebugEnabled())
			logger.debug("[CAB] CABManager: createAddressBookGroups() entered");
		CreateAddressBookGroupsResponse response=new CreateAddressBookGroupsResponse();		
		List<AddressBookGroup> groupList=null;
		List<AddressBookGroup> invalidAddressBookGroupList=new LinkedList<AddressBookGroup>();
		List<AddressBookGroup> inputList=request.getGroupList();
		int size=inputList.size();
		ConcurrentHashMap<String, Long> addressGroupMap=cabDBMaps.getAddressGroupMap();
		ConcurrentHashMap<String, Long> conactViewMap=cabDBMaps.getConactViewMap();
		Errors errors = new Errors();
		for(int i=0;i<size;i++){
			AddressBookGroup group=inputList.get(i);
			String aconyxUsername=group.getAconyxUsername();
			String key=aconyxUsername+KEY_SEPERATOR+group.getName();
			if(addressGroupMap.containsKey(key)){
				group.setStatus(Constants.STATUS_ALREADY_CONFIGURED);
				invalidAddressBookGroupList.add(inputList.remove(i));
				i--;size--;
				continue;
			}			
			key=aconyxUsername+KEY_SEPERATOR+group.getContactViewName();
			if(!conactViewMap.containsKey(key)){				
				errors.addError(ErrorCodes.ERROR_014, ErrorCodes.ERROR_014_DESC+aconyxUsername);
				response.setErrors(errors);
				group.setStatus(Constants.STATUS_FAILED);
				invalidAddressBookGroupList.add(inputList.remove(i));
				i--;size--;
			}
		}
		try{
			groupList=dao.createAddressBookGroups(inputList);
		}
		catch (SQLException e) {
			logger.error("SQLException in createAddressBookGroups()"+e.getMessage());
			errors.addError(ErrorCodes.ERROR_021, ErrorCodes.ERROR_021_DESC+":SQLException");
			response.setErrors(errors);
		}
		catch (Exception e) {
			logger.error("Exception in createAddressBookGroups()",e);
			errors.addError(ErrorCodes.ERROR_021, ErrorCodes.ERROR_021_DESC+":Exception");
			response.setErrors(errors);
		}	
		if(groupList!=null)	{
				groupList.addAll(invalidAddressBookGroupList);
				response.setGroupList(groupList);
			}
		if(logger.isDebugEnabled())
			logger.debug("[CAB] CABManager: createAddressBookGroups() exiting....");
		return response;		
	
	}

	public DeleteAddressBookGroupsResponse deleteAddressBookGroups(
			DeleteAddressBookGroupsRequest request) {
		if(logger.isDebugEnabled())
			logger.debug("[CAB] CABManager: deleteContactViews() entered");
		DeleteAddressBookGroupsResponse response=new DeleteAddressBookGroupsResponse();		
		List<AddressBookGroup> groupList=null;
		List<AddressBookGroup> invalidAddressBookGroupList=new LinkedList<AddressBookGroup>();
		List<AddressBookGroup> inputList=request.getGroupList();
		int size=inputList.size();
		ConcurrentHashMap<String, Long> addressGroupMap=cabDBMaps.getAddressGroupMap();
		for(int i=0;i<size;i++){
			AddressBookGroup group=inputList.get(i);
			String key=group.getAconyxUsername()+KEY_SEPERATOR+group.getName();
			if(!addressGroupMap.containsKey(key)){
				group.setStatus(Constants.STATUS_NOT_CONFIGURED);
				invalidAddressBookGroupList.add(inputList.remove(i));
				i--;size--;
			}
		}
		try{
			groupList=dao.deleteAddressBookGroups(inputList);
		}
		catch (SQLException e) {
			logger.error("SQLException in deleteAddressBookGroups()"+e.getMessage());
			Errors errors = new Errors();
			errors.addError(ErrorCodes.ERROR_021, ErrorCodes.ERROR_021_DESC+":SQLException");
			response.setErrors(errors);
		}
		catch (Exception e) {
			logger.error("Exception in deleteAddressBookGroups()",e);
			Errors errors = new Errors();
			errors.addError(ErrorCodes.ERROR_021, ErrorCodes.ERROR_021_DESC+":Exception");
			response.setErrors(errors);
		}	
		if(groupList!=null){		
			groupList.addAll(invalidAddressBookGroupList);
			response.setGroupList(groupList);
			}
		if(logger.isDebugEnabled())
			logger.debug("[CAB] CABManager: deleteContactViews() exiting....");
		return response;	
	
	}

	public AddToAddressBookGroupsResponse addToAddressBookGroups(
			AddToAddressBookGroupsRequest request) {
		if(logger.isDebugEnabled())
			logger.debug("[CAB] CABManager: addToAddressBookGroups() entered");
		AddToAddressBookGroupsResponse response=new AddToAddressBookGroupsResponse();		
		List<AddressBookGroup> groupList=null;
		List<AddressBookGroup> invalidAddressBookGroupList=new LinkedList<AddressBookGroup>();
		List<AddressBookGroup> inputList=request.getGroupList();
		int size=inputList.size();
		ConcurrentHashMap<String, Long> addressGroupMap=cabDBMaps.getAddressGroupMap();
		for(int i=0;i<size;i++){
			AddressBookGroup group=inputList.get(i);
			String key=group.getAconyxUsername()+KEY_SEPERATOR+group.getName();
			if(!addressGroupMap.containsKey(key)){
				group.setStatus(Constants.STATUS_NOT_CONFIGURED);
				invalidAddressBookGroupList.add(inputList.remove(i));
				i--;size--;
			}
		}
		try{
			groupList=dao.addToAddressBookGroups(inputList);
		}
		catch (SQLException e) {
			logger.error("SQLException in addToAddressBookGroups()"+e.getMessage());
			Errors errors = new Errors();
			errors.addError(ErrorCodes.ERROR_021, ErrorCodes.ERROR_021_DESC+":SQLException");
			response.setErrors(errors);
		}
		catch (Exception e) {
			logger.error("Exception in addToAddressBookGroups()",e);
			Errors errors = new Errors();
			errors.addError(ErrorCodes.ERROR_021, ErrorCodes.ERROR_021_DESC+":Exception");
			response.setErrors(errors);
		}	
		if(groupList!=null){		
			groupList.addAll(invalidAddressBookGroupList);
			response.setGroupList(groupList);
			}

		if(logger.isDebugEnabled())
			logger.debug("[CAB] CABManager: addToAddressBookGroups() exiting....");
		return response;		
	}

	public RemoveFromAddressBookGroupsResponse removeFromAddressBookGroups(
			RemoveFromAddressBookGroupsRequest request) {

		if(logger.isDebugEnabled())
			logger.debug("[CAB] CABManager: removeFromAddressBookGroups() entered");
		RemoveFromAddressBookGroupsResponse response=new RemoveFromAddressBookGroupsResponse();		
		List<AddressBookGroup> groupList=null;
		List<AddressBookGroup> invalidAddressBookGroupList=new LinkedList<AddressBookGroup>();
		List<AddressBookGroup> inputList=request.getGroupList();
		int size=inputList.size();
		ConcurrentHashMap<String, Long> addressGroupMap=cabDBMaps.getAddressGroupMap();
		for(int i=0;i<size;i++){
			AddressBookGroup group=inputList.get(i);
			String key=group.getAconyxUsername()+KEY_SEPERATOR+group.getName();
			if(!addressGroupMap.containsKey(key)){
				group.setStatus(Constants.STATUS_NOT_CONFIGURED);
				invalidAddressBookGroupList.add(inputList.remove(i));
				i--;size--;
			}
		}
		try{
			groupList=dao.removeFromAddressBookGroups(inputList);
		}
		catch (SQLException e) {
			logger.error("SQLException in RemoveFromAddressBookGroupsResponse()"+e.getMessage());
			Errors errors = new Errors();
			errors.addError(ErrorCodes.ERROR_021, ErrorCodes.ERROR_021_DESC+":SQLException");
			response.setErrors(errors);
		}
		catch (Exception e) {
			logger.error("Exception in RemoveFromAddressBookGroupsResponse()",e);
			Errors errors = new Errors();
			errors.addError(ErrorCodes.ERROR_021, ErrorCodes.ERROR_021_DESC+":Exception");
			response.setErrors(errors);
		}	
		if(groupList!=null){		
			groupList.addAll(invalidAddressBookGroupList);
			response.setGroupList(groupList);
			}
		if(logger.isDebugEnabled())
			logger.debug("[CAB] CABManager: removeFromAddressBookGroups() exiting");
		return response;		
	}

	public AssociateContactViewsResponse associateContactViews(
			AssociateContactViewsRequest request) {
		if(logger.isDebugEnabled())
			logger.debug("[CAB] CABManager: associateContactViews() entered");
		AssociateContactViewsResponse response=new AssociateContactViewsResponse();		
		List<AddressBookGroup> groupList=null;
		List<AddressBookGroup> invalidAddressBookGroupList=new LinkedList<AddressBookGroup>();
		List<AddressBookGroup> inputList=request.getGroupList();
		int size=inputList.size();
		ConcurrentHashMap<String, Long> addressGroupMap=cabDBMaps.getAddressGroupMap();
		ConcurrentHashMap<String, Long> contactViewMap=cabDBMaps.getConactViewMap();
		Errors errors = new Errors();
		for(int i=0;i<size;i++){
			AddressBookGroup group=inputList.get(i);
			String aconyxUsername=group.getAconyxUsername();
			String key=aconyxUsername+KEY_SEPERATOR+group.getName();
			if(!addressGroupMap.containsKey(key)){
				group.setStatus(Constants.STATUS_NOT_CONFIGURED);
				invalidAddressBookGroupList.add(inputList.remove(i));
				i--;size--;
				continue;
			}
			key=aconyxUsername+KEY_SEPERATOR+group.getContactViewName();
			if(!contactViewMap.containsKey(key)){				
				errors.addError(ErrorCodes.ERROR_014, ErrorCodes.ERROR_014_DESC+aconyxUsername);
				group.setStatus(Constants.STATUS_FAILED);
				response.setErrors(errors);
				invalidAddressBookGroupList.add(inputList.remove(i));
				i--;size--;
			}
		}
		try{
			groupList=dao.associateContactViews(inputList);
		}
		catch (SQLException e) {
			logger.error("SQLException in associateContactViews()"+e.getMessage());
			errors.addError(ErrorCodes.ERROR_021, ErrorCodes.ERROR_021_DESC+":SQLException");
			response.setErrors(errors);
		}
		catch (Exception e) {
			logger.error("Exception in associateContactViews()",e);
			errors.addError(ErrorCodes.ERROR_021, ErrorCodes.ERROR_021_DESC+":Exception");
			response.setErrors(errors);
		}	
		if(groupList!=null){		
			groupList.addAll(invalidAddressBookGroupList);
			response.setGroupList(groupList);
			}
		if(logger.isDebugEnabled())
			logger.debug("[CAB] CABManager: associateContactViews() exiting....");
		return response;		
	}

	public GetAddressBookGroupsResponse getAddressBookGroups(
			GetAddressBookGroupsRequest request) {

		if(logger.isDebugEnabled())
			logger.debug("[CAB] CABManager: getAddressBookGroups() entered");
		GetAddressBookGroupsResponse response=new GetAddressBookGroupsResponse();		
		List<AddressBookGroup> groupList=null;
		List<AddressBookGroup> invalidAddressBookGroupList=new LinkedList<AddressBookGroup>();
		List<AddressBookGroup> inputList=request.getGroupList();
		int size=inputList.size();
		ConcurrentHashMap<String, Long> addressGroupMap=cabDBMaps.getAddressGroupMap();
		for(int i=0;i<size;i++){
			AddressBookGroup group=inputList.get(i);
			String key=group.getAconyxUsername()+KEY_SEPERATOR+group.getName();
			if(!addressGroupMap.containsKey(key)){
				group.setStatus(Constants.STATUS_NOT_CONFIGURED);
				invalidAddressBookGroupList.add(inputList.remove(i));
				i--;size--;
			}
		}
		try{
			groupList=dao.getAddressBookGroups(inputList);
		}
		catch (SQLException e) {
			logger.error("SQLException in getAddressBookGroups()"+e.getMessage());
			Errors errors = new Errors();
			errors.addError(ErrorCodes.ERROR_021, ErrorCodes.ERROR_021_DESC+":SQLException");
			response.setErrors(errors);
		}
		catch (Exception e) {
			logger.error("Exception in getAddressBookGroups()",e);
			Errors errors = new Errors();
			errors.addError(ErrorCodes.ERROR_021, ErrorCodes.ERROR_021_DESC+":Exception");
			response.setErrors(errors);
		}	
		if(groupList!=null){		
			groupList.addAll(invalidAddressBookGroupList);
			response.setGroupList(groupList);
			}
		if(logger.isDebugEnabled())
			logger.debug("[CAB] CABManager: getAddressBookGroups() exiting");
		return response;		
	}
	
	public GetAllAddressBookGroupsResponse getAllAddressBookGroups(
			GetAllAddressBookGroupsRequest request) {
		if(logger.isDebugEnabled())
			logger.debug("[CAB] CABManager: getAllAddressBookGroups() entered");
		GetAllAddressBookGroupsResponse response=new GetAllAddressBookGroupsResponse();		
		List<AddressBookGroup> groupList=new LinkedList<AddressBookGroup>();
		List<String> aconyxUsernameList=request.getAconyxUsernameList();
		List<String> resultList=null;
		try{
			resultList=dao.getAllAddressBookGroups(aconyxUsernameList,groupList);
		}
		catch (SQLException e) {
			logger.error("SQLException in getAddressBookGroups()"+e.getMessage());
			Errors errors = new Errors();
			errors.addError(ErrorCodes.ERROR_021, ErrorCodes.ERROR_021_DESC+":SQLException");
			response.setErrors(errors);
		}
		catch (Exception e) {
			logger.error("Exception in getAddressBookGroups()",e);
			Errors errors = new Errors();
			errors.addError(ErrorCodes.ERROR_021, ErrorCodes.ERROR_021_DESC+":Exception");
			response.setErrors(errors);
		}	
		if(groupList.size()!=0){		
			response.setGroupList(groupList);
			}
		if(request!=null && resultList.size()!=0){
			Errors errors = new Errors();
			for(String acxUser:resultList){
				errors.addError(ErrorCodes.ERROR_019, ErrorCodes.ERROR_019_DESC+acxUser);	
			}
			response.setErrors(errors);
		}

		if(logger.isDebugEnabled())
			logger.debug("[CAB] CABManager: getAllAddressBookGroups() exiting....");
		return response;		
	}

	public GetMemberDetailsResponse getMemberDetails(
			GetMemberDetailsRequest request) {
		if(logger.isDebugEnabled())
			logger.debug("[CAB] CABManager: getMemberDetails() entered");
		GetMemberDetailsResponse response=new GetMemberDetailsResponse();	
		String aconyxUsername=request.getAconyxUsername();
		response.setAconyxUsername(aconyxUsername);
		List <PersonalContactCard> pccList=null;
		try{
			pccList=dao.getMemberDetails(aconyxUsername,request.getMemberList());
		}
		catch (SQLException e) {
			logger.error("SQLException in getMemberDetails()"+e.getMessage());
			Errors errors = new Errors();
			errors.addError(ErrorCodes.ERROR_021, ErrorCodes.ERROR_021_DESC+":SQLException");
			response.setErrors(errors);
		}
		catch (Exception e) {
			logger.error("Exception in getMemberDetails()",e);
			Errors errors = new Errors();
			errors.addError(ErrorCodes.ERROR_021, ErrorCodes.ERROR_021_DESC+":Exception");
			response.setErrors(errors);
		}	
		if(pccList!=null){		
			response.setPCCList(pccList);
			}
		if(logger.isDebugEnabled())
			logger.debug("[CAB] CABManager: getMemberDetails() exiting....");
		return response;		
	}

	public SearchUsersResponse searchUsers(SearchUsersRequest request) {

		if(logger.isDebugEnabled())
			logger.debug("[CAB] CABManager: searchUsers() entered");
		SearchUsersResponse response=new SearchUsersResponse();	
		String aconyxUsername=request.getAconyxUsername();
		String searchBy=request.getSearchBy();
		String searchValue=request.getSearchValue();
		response.setAconyxUsername(aconyxUsername);
		List <PersonalContactCard> pccList=null;
		try{
			pccList=dao.searchUsers(aconyxUsername,searchBy,searchValue);
		}
		catch (SQLException e) {
			logger.error("SQLException in searchUsers()"+e.getMessage());
			Errors errors = new Errors();
			errors.addError(ErrorCodes.ERROR_021, ErrorCodes.ERROR_021_DESC+":SQLException");
			response.setErrors(errors);
		}
		catch (Exception e) {
			logger.error("Exception in searchUsers()",e);
			Errors errors = new Errors();
			errors.addError(ErrorCodes.ERROR_021, ErrorCodes.ERROR_021_DESC+":Exception");
			response.setErrors(errors);
		}	
		if(pccList!=null){
			response.setPCCList(pccList);
			}
		if(logger.isDebugEnabled())
			logger.debug("[CAB] CABManager: searchUsers() exiting....");
		return response;		
	
	}

	@Override
	public void roleChanged(String clusterId, PartitionInfo pInfo) {
		logger.error("Inside roleChanged() with role:"+AseRoles.getString(pInfo.getRole()));
		if(pInfo.getRole()==AseRoles.ACTIVE){
			long startTime=System.currentTimeMillis();
			logger.error("Reloading CAB Maps from DB.........");
				cabDBMaps.reloadMaps();
			logger.error("Loaded CAB Maps from DB in "+(System.currentTimeMillis()-startTime)+" ms");
		}
	}

	public ModifyAddressBookGroupMembersResponse modifyAddressBookGroupMembers(
			ModifyAddressBookGroupMembersRequest request) {
		if(logger.isDebugEnabled())
			logger.debug("[CAB] CABManager: modifyAddressBookGroupMembers() entered");
		       ModifyAddressBookGroupMembersResponse response = new ModifyAddressBookGroupMembersResponse();
		      List<ModifyAddressBookGroup>  groupList = null;
		      List <ModifyAddressBookGroup> invalidAddressBookGroupList = new LinkedList<ModifyAddressBookGroup>();
		    List <ModifyAddressBookGroup> inputList = request.getGroupList();
		     int size = inputList.size();
		      	ConcurrentHashMap<String, Long> addressGroupMap = cabDBMaps.getAddressGroupMap();
		     Errors errors = new Errors();
		    for (int i = 0; i < size; i++) {
		        ModifyAddressBookGroup group = (ModifyAddressBookGroup)inputList.get(i);
		        String aconyxUsername = group.getAconyxUsername();
		       String key = aconyxUsername + KEY_SEPERATOR + group.getName();
		       if (!addressGroupMap.containsKey(key)) {
		        group.setStatus(Constants.STATUS_NOT_CONFIGURED);
	         invalidAddressBookGroupList.add((ModifyAddressBookGroup)inputList.remove(i));
		          i--; size--;
	      }
		     }
	     try
		     {
		      groupList = dao.modifyAddressBookGroupMembers(inputList);
		     }
		     catch (SQLException e) {
		        logger.error("SQLException in createAddressBookGroups()" + e.getMessage());
		    	errors.addError(ErrorCodes.ERROR_021, ErrorCodes.ERROR_021_DESC+":SQLException");
		      response.setErrors(errors);
		      }
		      catch (Exception e) {
		       logger.error("Exception in createAddressBookGroups()", e);
		   	errors.addError(ErrorCodes.ERROR_021, ErrorCodes.ERROR_021_DESC+":Exception");
		       response.setErrors(errors);
		      }
		      if (groupList != null) {
		       groupList.addAll(invalidAddressBookGroupList);
		        response.setGroupList(groupList);
		     }
				if(logger.isDebugEnabled())
					logger.debug("[CAB] CABManager: modifyAddressBookGroupMembers() exiting....");
		    return response;
		  }
}
