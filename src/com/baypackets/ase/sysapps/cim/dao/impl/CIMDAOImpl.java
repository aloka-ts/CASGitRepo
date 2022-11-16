/*
 * CIMDAOImpl.java
 * @author Amit Baxi 
 */
package com.baypackets.ase.sysapps.cim.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;

import org.apache.log4j.Logger;

import com.baypackets.ase.sysapps.cim.util.LicenseDAO;
import com.baypackets.ase.sysapps.cim.dao.CIMDAO;
import com.baypackets.ase.sysapps.cim.jaxb.ChatHistory;
import com.baypackets.ase.sysapps.cim.jaxb.ChatHistoryResponse;
import com.baypackets.ase.sysapps.cim.jaxb.CreateProfilesRequest;
import com.baypackets.ase.sysapps.cim.jaxb.DeleteProfilesRequest;
import com.baypackets.ase.sysapps.cim.jaxb.FetchProfilesRequest;
import com.baypackets.ase.sysapps.cim.jaxb.LatestMessage;
import com.baypackets.ase.sysapps.cim.jaxb.LatestUserChatResponse;
import com.baypackets.ase.sysapps.cim.jaxb.Message;
import com.baypackets.ase.sysapps.cim.jaxb.Profile;
import com.baypackets.ase.sysapps.cim.jaxb.ServiceProfile;
import com.baypackets.ase.sysapps.cim.jaxb.UpdateProfilesRequest;
import com.baypackets.ase.sysapps.cim.util.CIMMessageDAO;
import com.baypackets.ase.sysapps.cim.util.Constants;
import com.baypackets.ase.sysapps.cim.util.ContactBinding;
import com.baypackets.ase.sysapps.cim.util.Configuration;

public class CIMDAOImpl implements CIMDAO{

	static InitialContext ctx;
	static DataSource  currentDataSource;
	private static CIMDAOImpl cimDaoImpl = new CIMDAOImpl();
	private static Logger logger=Logger.getLogger(CIMDAOImpl.class);
	private static long fetchLimit=Constants.CHAT_HISTORY_FETCH_LIMIT*86400*1000;// in milliseconds
	private CIMDAOImpl(){
	}
	
	public static CIMDAOImpl getInstance() { 
		return cimDaoImpl; 
	}
	
	static {

		String fetchLimitStr=Configuration.getInstance().getParamValue(Constants.PROP_CIM_CHAT_HISTORY_FETCH_LIMIT);
		if(fetchLimitStr!=null){
			fetchLimitStr=fetchLimitStr.trim();
			if (logger.isDebugEnabled()) {
				logger.debug("[CIM] fetch Limit is "+fetchLimitStr+" days");
			}			
			try{
				fetchLimit=Integer.valueOf(fetchLimitStr)*86400*1000;// in milliseconds
			}catch(Exception e){
				logger.error("Exception in reading property:"+Constants.PROP_CIM_CHAT_HISTORY_FETCH_LIMIT+". Using Default "+(fetchLimit/86400000)+" days");
			}			
		}else{
			logger.error("Property:"+Constants.PROP_CIM_CHAT_HISTORY_FETCH_LIMIT+" not defined, so using Default "+(fetchLimit/86400000)+" days");
		}

		String PROVIDER_URL = "file:" + System.getProperty(Constants.ASE_HOME)+ Constants.PATH_JNDI_FILESERVER;
		String CONTEXT_FACTORY = Constants.CONTEXT_FACTORY;

		if (logger.isDebugEnabled()) {
			logger.debug("[CIM] CIMServiceDAOImpl Getting Data source");
		}
		try {
			Hashtable<String, String> env = new Hashtable<String, String>();
			env.put(Context.INITIAL_CONTEXT_FACTORY, CONTEXT_FACTORY);
			env.put(Context.PROVIDER_URL, PROVIDER_URL);
			ctx = new InitialContext(env);
			String dataSourceName=Configuration.getInstance().getParamValue(Constants.PROP_CIM_DATASOURCE_NAME);
			dataSourceName=(dataSourceName!=null && ! dataSourceName.trim().isEmpty() )?dataSourceName:Constants.PATH_DATASOURCE_PRIMARY;
			currentDataSource=(DataSource)ctx.lookup(dataSourceName);

		} 
		catch (Exception e) {
			logger.error("[PAC] PACDaoImpl Exception in datasource lookup", e);
		}
	}

	/* 
	 * This method will retrieve contact bindings for the address of record
	 * @param  addressOfRecord Address of record (To address in request)
	 */
	@Override
	public List<ContactBinding> getBindingsFor(String addressOfRecord)
			throws SQLException, Exception {
		logger.debug("Inside getBindingsFor() method with aor:"+addressOfRecord);
		ArrayList<ContactBinding> bindingList=new ArrayList<ContactBinding>();
		if(addressOfRecord!=null){
			String aor=addressOfRecord.replaceAll("'", "''");
			Connection connection = null;
			 PreparedStatement preStatement=null; 
			ResultSet res=null;
			try{
			
				connection=currentDataSource.getConnection();
				preStatement=connection.prepareStatement("SELECT CONTACTURI,PRIORITY,PATH,DISPLAYNAME,UNKNOWNPARAM FROM reg_binding WHERE REGISTRATIONID IN(SELECT REGISTRATIONID FROM reg_registrations WHERE ADDRESSOFRECORD=?)");
			        preStatement.setString(1, aor);			   
			        res = preStatement.executeQuery();
			    while(res.next()){
			    	String contactUri=res.getString("CONTACTURI");
			    	float priority=res.getFloat("PRIORITY");
			    	String path=res.getString("PATH");
			    	String displayName=res.getString("DISPLAYNAME");
			    	String unknownParam=res.getString("UNKNOWNPARAM");
			       	ContactBinding binding =new ContactBinding(contactUri, displayName, priority, path, unknownParam);
			       	bindingList.add(binding);
			    }  
				
			}catch (SQLException e) {
				logger.error(" CIMDAOImpl: getBindingsFor() SQLException occured ",e);
				throw e;
			}catch (Exception e) {
				logger.error(" CIMDAOImpl: getBindingsFor() SQLException occured ",e);
				throw e;
			}finally{
				if(res!=null){
					res.close();
				}	if(preStatement!=null){
					preStatement.close();
				}if(connection!=null){
					connection.close();
				}
			}
		}
		return bindingList;
	}
	
	/**
	 * This method return license information for organization. Tables used ::
	 * tas_license
	 * 
	 * @param organizationId
	 *            organization id
	 * @return LicenseDAO object
	 */
	@Override
	public LicenseDAO getLicenseData(int organizationId) throws SQLException,
	Exception {
	    if (organizationId < 0) {
		logger.error("[CIM] getLicenseData() Invalid Organization Id");
		throw new IllegalArgumentException("Organization Id is not valid");
	    }
	    if (logger.isDebugEnabled()) {
		logger.debug("[CIM] getLicenseData() entered with Organization Id: "
			+ organizationId);
	    }
	    Connection conn = null;
	    PreparedStatement pstmt = null;
	    ResultSet rs = null;

	    LicenseDAO licenseData = null;
	    try {
		conn = currentDataSource.getConnection();
		if (conn != null) {

		    pstmt = conn.prepareStatement(Constants.LICENSE_QUERY);
		    pstmt.setInt(1, organizationId);
		    if (logger.isDebugEnabled()) {
			logger.debug("[CIM] getLicenseData() getLicenseData: "
				+ Constants.LICENSE_QUERY);
		    }
		    rs = pstmt.executeQuery();
		    if (rs.next()) {
			int maxSubscribers = rs.getInt(Constants.MAX_SUBSCRIBERS);
			int sms = rs.getInt(Constants.SMS);
			int maxHourlySmsOut = rs
				.getInt(Constants.MAX_HOURLY_SMS_OUT);
			int maxSessionApplication = rs
				.getInt(Constants.MAX_SESSION_APPLICATION);
			int maxSessionSubscriber = rs
				.getInt(Constants.MAX_SESSION_SUBSCRIBER);
			int voiceClients = rs.getInt(Constants.VOICE_CLIENTS);
			int videoClients = rs.getInt(Constants.VIDEO_CLIENTS);
			int fmfmStubs = rs.getInt(Constants.FMFM_STUBS);
			String expirationDate = rs
				.getString(Constants.EXPIRATION_DATE);
			int callLimit = rs.getInt(Constants.CALL_LIMIT);
			int enterpriseCallLimit = rs
				.getInt(Constants.ENTERPRISE_CALL_LIMIT);
			if (logger.isDebugEnabled()) {
			    logger.debug("[CIM] getLicenseData() maxSubscribers: "
				    + maxSubscribers + ", sms: " + sms
				    + ", maxHourlySmsOut: " + maxHourlySmsOut
				    + ", maxSessionApplication: "
				    + maxSessionApplication
				    + ", maxSessionSubscriber: "
				    + maxSessionSubscriber + ", voiceClients: "
				    + voiceClients + ", videoClients: "
				    + videoClients + ", fmfmStubs: " + fmfmStubs
				    + ", expirationDate: " + expirationDate
				    + ", callLimit: " + callLimit
				    + ", enterprisecallLimit: "
				    + enterpriseCallLimit);
			}
			licenseData = new LicenseDAO(organizationId,
				maxSubscribers, sms, maxHourlySmsOut,
				maxSessionApplication, maxSessionSubscriber,
				voiceClients, videoClients, fmfmStubs,
				expirationDate, callLimit, enterpriseCallLimit);
		    }

		}
	    } catch (Exception e) {
		logger.error("[CIM] getLicenseData() An exception occured: ", e);
	    } finally {
		if (rs != null) {
		    rs.close();
		}
		if (pstmt != null) {
		    pstmt.close();
		}
		if (conn != null) {
		    conn.close();
		}
	    }
	    if (logger.isDebugEnabled()) {
		logger.debug("[CENTREX] getLicenseData() Return :: licenseData: "
			+ licenseData);
		logger.debug("[CENTREX] getLicenseData() exit");
	    }
	    return licenseData;
	}

	@Override
	public int isAconyxUser(String addressOfRecord) throws SQLException,
	Exception {
	    if (logger.isDebugEnabled())
		logger.debug("[CIM] isAconyxUser() method entered with AOR: "
			+ addressOfRecord);

	    int epID = -1;

	    if (addressOfRecord != null) {
		String aor = addressOfRecord.replaceAll("'", "''");
		Connection connection = null;
		PreparedStatement preStatement = null;
		ResultSet res = null;
		try {
		    connection = currentDataSource.getConnection();
		    preStatement = connection
			    .prepareStatement(Constants.ENTERPRISE_QUERY);
		    preStatement.setString(1, aor);
		    res = preStatement.executeQuery();
		    while (res.next()) {
			epID = res.getInt(Constants.EP_ACC_NO);
			break;
		    }
		} catch (SQLException e) {
		    logger.error(
			    " CIMDAOImpl: getBindingsFor() SQLException occured ",
			    e);
		    throw e;
		} catch (Exception e) {
		    logger.error(
			    " CIMDAOImpl: getBindingsFor() SQLException occured ",
			    e);
		    throw e;
		} finally {
		    if (res != null) {
			res.close();
		    }
		    if (preStatement != null) {
			preStatement.close();
		    }
		    if (connection != null) {
			connection.close();
		    }
		}
	    }
	    if (logger.isDebugEnabled())
		logger.debug("[CIM] isAconyxUser(): Exiting with value " + epID);
	    return epID;
	}
	
	public List<Profile> createProfilesList(CreateProfilesRequest request) throws SQLException, Exception{
		
		logger.debug("Inside createProfilesList()");
		List<Profile> srProfile=new ArrayList<Profile>();
		
		List<String> userStatus=new ArrayList<String>();
		for(ServiceProfile sr:request.getServiceProfiles()){
			userStatus.add(sr.getUserName());
		}
			List<String> userPresent=getUserStatusList(userStatus);
			
			if(userPresent.size()>0){
			Connection connection = null;
			Statement preStatement=null; 
			ResultSet res=null;
			try{
				
				connection=currentDataSource.getConnection();
				preStatement=connection.createStatement();
			    
				StringBuffer sb=new StringBuffer("INSERT INTO cim_profile VALUES");
				
				for(int i=0;i<userPresent.size();i++){
					
					ServiceProfile sr=getServiceProfileFouUser(request.getServiceProfiles(), userPresent.get(i));
					if(sr!=null){
					sb.append("('");
					sb.append(sr.getUserName().replaceAll("'", "''"));
					sb.append("',");
					sb.append(sr.getState().equalsIgnoreCase("ENABLE")?1:0);
					sb.append(",");
					sb.append(sr.getSmsInState().equalsIgnoreCase("ENABLE")?1:0);
					sb.append(")");
					if(i!=userPresent.size()-1){
						sb.append(",");
					}
					}
					
				}
				
				String createQuery=sb.toString();
				
			    preStatement.execute(createQuery);
			    for(ServiceProfile sr:request.getServiceProfiles()){
			    	Profile pr=new Profile();
			    	pr.setUserName(sr.getUserName());
			    	if(userPresent.contains(sr.getUserName())){
			    		pr.setStatus(Constants.SUCCESS);
			    	}
			    	else{
			    		pr.setStatus(Constants.INVALIDUSER);
			    	}
			    	srProfile.add(pr);
			    }
				
			}catch (SQLException e) {
				logger.error(" CIMDAOImpl: createProfilesList() SQLException occured ",e);
				throw e;
			}catch (Exception e) {
				logger.error(" CIMDAOImpl: createProfilesList() SQLException occured ",e);
				throw e;
			}finally{
				if(res!=null){
					res.close();
				}	if(preStatement!=null){
					preStatement.close();
				}if(connection!=null){
					connection.close();
				}
			}
			}
		return srProfile;
	}
	
	public List<Profile> deleteProfileList(DeleteProfilesRequest request) throws SQLException, Exception{
		
		logger.debug("Inside deleteProfileList()");
		List<Profile> srProfile=new ArrayList<Profile>();
		
		
			List<String> userPresent=getUserStatusList(request.getUsername());
			
			if(userPresent.size()>0){
			Connection connection = null;
			Statement preStatement=null; 
			ResultSet res=null;
			try{
		
				connection=currentDataSource.getConnection();
				preStatement=connection.createStatement();
			    
				StringBuffer sb=new StringBuffer("DELETE FROM cim_profile WHERE ACONYX_USERNAME IN(");
				
				for(int i=0;i<userPresent.size();i++){
					
					
					sb.append("'");
					sb.append(userPresent.get(i).replaceAll("'", "''"));
					sb.append("'");
					if(i!=userPresent.size()-1){
						sb.append(",");
					}
					
					
				}
				
				String deleteQuery=sb.toString()+")";
				logger.debug("[CIM] Delete user profile query is"+deleteQuery);
			   preStatement.executeUpdate(deleteQuery);
			    for(String str:request.getUsername()){
			    	
			    	Profile sr=new Profile();
			    	sr.setUserName(str);
			    	if(userPresent.contains(str)){
			    		sr.setStatus(Constants.SUCCESS);
			    	}
			    	else{
			    		sr.setStatus(Constants.INVALIDUSER);
			    	}
			    	srProfile.add(sr);
			    }
				
			}catch (SQLException e) {
				logger.error(" CIMDAOImpl: createProfilesList() SQLException occured ",e);
				throw e;
			}catch (Exception e) {
				logger.error(" CIMDAOImpl: createProfilesList() SQLException occured ",e);
				throw e;
			}finally{
				if(res!=null){
					res.close();
				}	if(preStatement!=null){
					preStatement.close();
				}if(connection!=null){
					connection.close();
				}
			}
			}
		return srProfile;
	}
	
	public List<ServiceProfile> fetchProfileList(FetchProfilesRequest request) throws SQLException, Exception{
		
		logger.debug("Inside fetchProfileList()");
		List<ServiceProfile> srProfile=new ArrayList<ServiceProfile>();
		
		
		List<String> userPresent=getUserStatusList(request.getUsername());
		
		if(userPresent.size()>0){
		Connection connection = null;
		Statement preStatement=null; 
		ResultSet res=null;
		try{
			
		
			connection=currentDataSource.getConnection();
			preStatement=connection.createStatement();
		    
			StringBuffer sb=new StringBuffer("SELECT ACONYX_USERNAME,SMS_IN,SMS_OUT FROM cim_profile WHERE ACONYX_USERNAME IN(");
			
			for(int i=0;i<userPresent.size();i++){
				
				
				sb.append("'");
				sb.append(userPresent.get(i).replaceAll("'", "''"));
				sb.append("'");
				if(i!=userPresent.size()-1){
					sb.append(",");
				}
				
				
			}
			
			String fetchQuery=sb.toString()+")";
			logger.debug("[CIM] Fetch user profile query is"+fetchQuery);
		    res = preStatement.executeQuery(fetchQuery);
		    while(res.next()){
		    	ServiceProfile sr=new ServiceProfile();
		    	sr.setUserName(res.getString("ACONYX_USERNAME"));
		    	sr.setState(res.getInt("SMS_OUT")==1?Constants.ENABLE:Constants.DISABLE);
		    	sr.setSmsInState(res.getInt("SMS_IN")==1?Constants.ENABLE:Constants.DISABLE);
		    	srProfile.add(sr);
		    }
		    for(String str:request.getUsername()){
		    	
		    	if(getServiceProfileFouUser(srProfile, str)==null){
		    	ServiceProfile sr=new ServiceProfile();
		    	sr.setUserName(str);
		    	sr.setState(Constants.INVALIDUSER);
		    	srProfile.add(sr);
		    	}
		    }
			
		}catch (SQLException e) {
			logger.error(" CIMDAOImpl: createProfilesList() SQLException occured ",e);
			throw e;
		}catch (Exception e) {
			logger.error(" CIMDAOImpl: createProfilesList() SQLException occured ",e);
			throw e;
		}finally{
			if(res!=null){
				res.close();
			}	if(preStatement!=null){
				preStatement.close();
			}if(connection!=null){
				connection.close();
			}
		}
		}
	return srProfile;
	}
	
	public List<Profile> updateProfileList(UpdateProfilesRequest request) throws SQLException, Exception{
	
		logger.debug("Inside updateProfileList()");
		List<Profile> srProfile=new ArrayList<Profile>();
		
		
		for(ServiceProfile sr:request.getServiceProfiles()){
			Profile pr=new Profile();
			pr.setUserName(sr.getUserName());
			Connection connection = null;
			Statement preStatement=null; 
			ResultSet res=null;
			try{
			
				connection=currentDataSource.getConnection();
				preStatement=connection.createStatement();
			    int cimOutValue=sr.getState().equalsIgnoreCase(Constants.ENABLE)?1:0;
			    int cimInValue=sr.getSmsInState().equalsIgnoreCase(Constants.ENABLE)?1:0;
				String updateQuery="UPDATE cim_profile SET SMS_OUT="+cimOutValue+",SMS_IN="+cimInValue+ " WHERE ACONYX_USERNAME='"+sr.getUserName().replaceAll("'", "''")+"'";
				
			    int status=preStatement.executeUpdate(updateQuery);
			    if(status==1){
			    	pr.setStatus(Constants.SUCCESS);
			    }
			    else{
			    	pr.setStatus(Constants.NOTCONFIGURED);
			    }
				
			}catch (SQLException e) {
				logger.error(" CIMDAOImpl: updateProfileList() SQLException occured ",e);
				throw e;
			}catch (Exception e) {
				logger.error(" CIMDAOImpl: updateProfileList() SQLException occured ",e);
				throw e;
			}finally{
				if(res!=null){
					res.close();
				}	if(preStatement!=null){
					preStatement.close();
				}if(connection!=null){
					connection.close();
				}
			}
			srProfile.add(pr);
		}
		return srProfile;
	}
	
	public List<String> getUserStatusList(List<String> userName) throws SQLException, Exception{
		
		logger.debug("Inside getUserStatusList()");
		List<String> userNameList=new ArrayList<String>();
			
		if(userName.size()>0){
			StringBuffer sb=new StringBuffer();
			
			for(int i=0;i<userName.size();i++){
				
				sb.append("'");
				sb.append(userName.get(i).replaceAll("'", "''"));
				sb.append("'");
				if(i!=userName.size()-1){
					sb.append(",");
				}
				
			}
			Connection connection = null;
			Statement preStatement=null; 
			ResultSet res=null;
			try{
			
				connection=currentDataSource.getConnection();
				preStatement=connection.createStatement();
			    
				String userStatusQuery="select ACONYX_USERNAME from aconyx_user_info where ACONYX_USERNAME IN ("+sb.toString()+")";
				
				logger.debug("GetUser Status query is" +userStatusQuery);
				
			    res = preStatement.executeQuery(userStatusQuery);
			    while(res.next()){
			    	userNameList.add(res.getString("ACONYX_USERNAME"));
			       	
			    }   
				
			}catch (SQLException e) {
				logger.error(" CIMDAOImpl: getUserStatusList() SQLException occured ",e);
				throw e;
			}catch (Exception e) {
				logger.error(" CIMDAOImpl: getUserStatusList() SQLException occured ",e);
				throw e;
			}finally{
				if(res!=null){
					res.close();
				}	if(preStatement!=null){
					preStatement.close();
				}if(connection!=null){
					connection.close();
				}
			}
		}
		return userNameList;
	}
	
	public ServiceProfile getServiceProfileFouUser(List<ServiceProfile> srList,String userName){
		ServiceProfile sr=null;
		
		for(ServiceProfile s:srList){
			if(s.getUserName().equals(userName)){
				sr=s;
				break;
			}
		}
		
		return sr;
	}
	
	public boolean fetchUserSMSOUTStatus(String userName) throws SQLException, Exception{

		if(logger.isDebugEnabled())
			logger.debug("[CIM] fetchUserSMSOUTStatus() method entered with user: " + userName);

		boolean isSmsOutEnable = false;	

		if(userName != null) {
			Connection connection = null;
			Statement preStatement = null; 
			ResultSet res = null;
			try {
				connection = currentDataSource.getConnection();
				preStatement = connection.createStatement();

				String fetchQuery = "SELECT ACONYX_USERNAME,SMS_OUT FROM cim_profile WHERE ACONYX_USERNAME='"+userName.replaceAll("'", "''")+"'";
				if(logger.isDebugEnabled())
					logger.debug("[CIM] fetchUserSMSOUTStatus() query: " + fetchQuery);

				res = preStatement.executeQuery(fetchQuery);
				while(res.next()){
					if(res.getInt("SMS_OUT") == 1){
						isSmsOutEnable=true;
						break;
					}
				}
			} catch (SQLException e) {
				logger.error(" CIMDAOImpl: fetchUserSMSOUTStatus() SQLException occured: ",e);
				throw e;
			} catch (Exception e) {
				logger.error(" CIMDAOImpl: fetchUserSMSOUTStatus() Exception occured: ",e);
				throw e;
			} finally {
				if(res != null) {
					res.close();
				}	
				if(preStatement != null){
					preStatement.close();
				}
				if(connection != null){
					connection.close();
				}
			}
		}

		if(logger.isDebugEnabled())
			logger.debug("[CIM] fetchUserSMSOUTStatus(): Exiting with value " + isSmsOutEnable);

		return isSmsOutEnable;
	}
	
	public boolean fetchUserSMSINStatus(String userName) throws SQLException, Exception{

		if(logger.isDebugEnabled())
			logger.debug("[CIM] fetchUserSMSINStatus() method entered with user: " + userName);

		boolean isSmsInEnable = false;		

		if(userName != null) {
			Connection connection = null;
			Statement preStatement = null; 
			ResultSet res = null;
			try{			
				connection = currentDataSource.getConnection();
				preStatement = connection.createStatement();

				String fetchQuery = "SELECT ACONYX_USERNAME,SMS_IN FROM cim_profile WHERE ACONYX_USERNAME='"+userName.replaceAll("'", "''")+"'";
				logger.debug("[CIM] fetchUserSMSINStatus query: "+fetchQuery);

				res = preStatement.executeQuery(fetchQuery);
				while(res.next()){
					if(res.getInt("SMS_IN") == 1){
						isSmsInEnable=true;
						break;
					}
				}		
			} catch (SQLException e) {
				logger.error(" CIMDAOImpl: fetchUserSMSINStatus() SQLException occured ",e);
				throw e;
			} catch (Exception e) {
				logger.error(" CIMDAOImpl: fetchUserSMSINStatus() SQLException occured ",e);
				throw e;
			} finally{
				if(res!=null){
					res.close();
				}	
				if(preStatement!=null){
					preStatement.close();
				}
				if(connection!=null){
					connection.close();
				}
			}
		}
		if(logger.isDebugEnabled())
			logger.debug("[CIM] fetchUserSMSINStatus(): Exiting with value " + isSmsInEnable);

		return isSmsInEnable;
	}
	
	/* 
	 * This method will retrieve latest contact binding for the address of record
	 * @param  addressOfRecord Address of record (To address in request)
	 */
	@Override
	public ContactBinding getLatestBindingForUser(String userName) throws SQLException, Exception {
		if(logger.isDebugEnabled())
			logger.debug("[CIM] getLatestBindingForUser() method entered with AOR: " + userName);

		ContactBinding binding=null;

		if(userName!=null){
			Connection connection = null;
			Statement preStatement = null; 
			ResultSet res = null;
			try{
				connection = currentDataSource.getConnection();
				preStatement = connection.createStatement();
				String query = "SELECT b.CONTACTURI,b.PRIORITY,b.PATH,b.DISPLAYNAME,b.UNKNOWNPARAM FROM reg_binding b,reg_registrations reg WHERE reg.REGISTRATIONID=b.REGISTRATIONID AND reg.USERNAME='"+userName.replaceAll("'", "''")+"' order by reg.USERNAME,b.INSERTIONTIME desc";		   
				res = preStatement.executeQuery(query);
				while(res.next()){
					String contactUri = res.getString("CONTACTURI");
					float priority = res.getFloat("PRIORITY");
					String path = res.getString("PATH");
					String displayName = res.getString("DISPLAYNAME");
					String unknownParam = res.getString("UNKNOWNPARAM");
					binding = new ContactBinding(contactUri, displayName, priority, path, unknownParam);
					break;
				} 
			} catch (SQLException e) {
				logger.error(" CIMDAOImpl: getBindingsForUser() SQLException occured ",e);
				throw e;
			} catch (Exception e) {
				logger.error(" CIMDAOImpl: getBindingsForUser() SQLException occured ",e);
				throw e;
			} finally {
				if(res != null){
					res.close();
				}	
				if(preStatement != null){
					preStatement.close();
				}
				if(connection != null){
					connection.close();
				}
			}
		}
		if(logger.isDebugEnabled())
			logger.debug("[CIM] getLatestBindingForUser(): Exiting with value " + binding);
		return binding;
	}
	
	public void insertChatLog(String id, String user, String phoneNumber, String type, 
								String message, String network) throws SQLException, Exception {

		if(logger.isDebugEnabled())
			logger.debug("[CIM] insertChatLog() method entered with user: " + user + "phone number: " + phoneNumber + "MESSAGE: " + message);

		Connection connection = null;
		Statement preStatement = null; 
		ResultSet res = null;

		// obtain DB connection
		if(currentDataSource == null) {
			logger.error("[TAS] insertChatLog() DataSource is null.");
			return;
		} else {
			connection = currentDataSource.getConnection();
		}

		if (connection == null) {
			logger.error("[TAS] insertChatLog() DB connection is null.");
			return;
		}
		
		try{
			preStatement = connection.createStatement();

			StringBuffer sb = new StringBuffer("INSERT INTO tas_activity_logs VALUES");

			sb.append("('");
			sb.append(id);
			sb.append("','");
			sb.append(user.replaceAll("'", "''"));
			sb.append("','");
			sb.append(phoneNumber.replaceAll("'", "''"));
			sb.append("','");
			sb.append(System.currentTimeMillis());
			sb.append("','");
			sb.append(type);
			sb.append("','");
			sb.append(message.replaceAll("'", "''"));
			sb.append("','");
			sb.append(network);
			sb.append("')");

			String createQuery = sb.toString();
			if(logger.isDebugEnabled())
				logger.debug("[CIM] insertChatLog(): DB query:" + createQuery);
			preStatement.execute(createQuery);
		} catch (SQLException e) {
			logger.error(" CIMDAOImpl: insertChatLog() SQLException occured ",e);
			throw e;
		} catch (Exception e) {
			logger.error(" CIMDAOImpl: insertChatLog() SQLException occured ",e);
			throw e;
		} finally {
			if(res != null){
				res.close();
			}	
			if(preStatement != null){
				preStatement.close();
			}
			if(connection != null){
				connection.close();
			}
		}
		if(logger.isDebugEnabled())
			logger.debug("[CIM] insertChatLog(): Exiting..");
	}
	
	public boolean deleteUserChatHistory(String userName,List<String> buddyList)throws SQLException, Exception{
		boolean isDeleted=false;
//		logger.debug("Inside getBindingsForUser() method with aor:"+userName);
//		ContactBinding binding=null;
//		if(userName!=null){
//			Connection connection = null;
//			Statement preStatement=null; 
//			ResultSet res=null;
//			try{
//				connection=currentDataSource.getConnection();
//				preStatement=connection.createStatement();
//			       String query="SELECT b.CONTACTURI,b.PRIORITY,b.PATH,b.DISPLAYNAME,b.UNKNOWNPARAM FROM reg_binding b,reg_registrations reg WHERE reg.REGISTRATIONID=b.REGISTRATIONID AND reg.USERNAME='"+userName+"' order by reg.USERNAME,b.INSERTIONTIME desc";		   
//			        res = preStatement.executeQuery(query);
//			    while(res.next()){
//			    	String contactUri=res.getString("CONTACTURI");
//			    	float priority=res.getFloat("PRIORITY");
//			    	String path=res.getString("PATH");
//			    	String displayName=res.getString("DISPLAYNAME");
//			    	String unknownParam=res.getString("UNKNOWNPARAM");
//			       binding =new ContactBinding(contactUri, displayName, priority, path, unknownParam);
//			       break;
//			      
//			    }   
//			}catch (SQLException e) {
//				logger.error(" CIMDAOImpl: getBindingsForUser() SQLException occured ",e);
//				throw e;
//			}catch (Exception e) {
//				logger.error(" CIMDAOImpl: getBindingsForUser() SQLException occured ",e);
//				throw e;
//			}finally{
//				if(res!=null){
//					res.close();
//				}	if(preStatement!=null){
//					preStatement.close();
//				}if(connection!=null){
//					connection.close();
//				}
//			}
//		}
		return isDeleted;
	}

	
	private void updateChatStatus(String user, String phoneNumber) throws SQLException, Exception {

		if (logger.isDebugEnabled()) {
			logger.debug("[CIM] updateAcitityType() method entered with user: " + user + " phoneNumber: " + phoneNumber);
		}

		Connection conn = null;
		Statement stmt = null;
		
		try {
			if(currentDataSource == null) {
				logger.error("[CIM] updateChatStatus() DataSource is null.");
				return ;
			} else {                    
				conn = currentDataSource.getConnection();
			}

			if (conn == null) {
				logger.error("[CIM] updateChatStatus() DB connection is null.");
				return ;
			}

			stmt = conn.createStatement();
			String query1 = "UPDATE tas_activity_logs set activity_type='RECEIVED_MESSAGE' where user_id='"+user.replaceAll("'", "''")+
											"' AND phone_number ='"+phoneNumber.replaceAll("'", "''")+"' AND activity_type = 'WAITING_MESSAGE'";

			if(logger.isDebugEnabled()){
				logger.debug("[CIM] updateChatStatus Query: " + query1);
			}

			stmt.executeUpdate(query1);
			
			String query2 = "UPDATE tas_activity_logs set activity_type='DELIVERED_MESSAGE' where user_id='"+phoneNumber.replaceAll("'", "''")+
			"' AND phone_number ='"+user.replaceAll("'", "''")+"' AND activity_type = 'SENT_MESSAGE'";

			if(logger.isDebugEnabled()){
				logger.debug("[CIM] updateChatStatus Query: " + query2);
			}

			stmt.executeUpdate(query2);
			
		} catch (Exception e) {
			logger.error("[TAS] updateChatStatus() An exception occured: ",e);
		} finally {
			if(stmt!=null){
				stmt.close();
			}
			if(conn!=null){
				conn.close();
			}
		}

		if (logger.isDebugEnabled()) {
			logger.debug("[CIM] updateChatStatus() exited");
		}
	}

	public LatestUserChatResponse getLatestUserChat(String userName) throws SQLException, Exception {
		if(logger.isDebugEnabled())
			logger.debug("[CIM] getLatestUserChat() method entered for user: " + userName);
		String fetchTime = String.valueOf(System.currentTimeMillis()-fetchLimit);
		LatestUserChatResponse response =new LatestUserChatResponse();
		if(userName != null) {	
			response.setAconyxuser(userName);
			Connection connection = null;
			PreparedStatement preStatement = null;
			ResultSet resultSet=null;
			List<LatestMessage> latestMessages=new LinkedList<LatestMessage>();
			try{
				// obtain DB connection
				if(currentDataSource == null) {
					logger.error("[TAS] getLatestUserChat() DataSource is null.");
					return response;
				} else {
					connection = currentDataSource.getConnection();
				}

				if (connection == null) {
					logger.error("[TAS] getLatestUserChat() DB connection is null.");
					return response;
				}
				
				preStatement=connection.prepareStatement("select a.phone_number ,a.time_stamp,a.activity_type,a.message,a.network_info from tas_activity_logs a where a.user_id=? and activity_type IN ('SENT_MESSAGE', 'RECEIVED_MESSAGE', 'WAITING_MESSAGE', 'DELIVERED_MESSAGE') AND time_stamp =(select max(time_stamp) from tas_activity_logs b where b.user_id=? and b.phone_number=a.phone_number and time_stamp >= '" + fetchTime + "' and activity_type IN ('SENT_MESSAGE', 'RECEIVED_MESSAGE', 'WAITING_MESSAGE', 'DELIVERED_MESSAGE'))");
				preStatement.setString(1, userName);
				preStatement.setString(2, userName);
				resultSet=preStatement.executeQuery();
				
				while(resultSet.next()){
					LatestMessage message=new LatestMessage();
					message.setBuddy(resultSet.getString("phone_number"));
					message.setContent(resultSet.getString("message"));
					message.setTimestamp(resultSet.getString("time_stamp"));
					message.setDirection(resultSet.getString("activity_type"));
					message.setType(resultSet.getString("network_info"));
					latestMessages.add(message);
				}
				if(!latestMessages.isEmpty()){
					response.setLatestMessages(latestMessages);
				}
			}catch (SQLException e) {
				logger.error(" CIMDAOImpl: getLatestUserChat() SQLException occured ",e);
				throw e;
			} catch (Exception e) {
				logger.error(" CIMDAOImpl: getLatestUserChat() SQLException occured ",e);
				throw e;
			} finally {
				if(resultSet != null){
					resultSet.close();
				}					 	
				if(preStatement != null){
					preStatement.close();
				}
				if(connection != null){
					connection.close();
				}
			}
		}
		return response;
	}
	public ChatHistoryResponse getUserChatHistory(String userName, List<String> buddyList) throws SQLException, Exception {

		if(logger.isDebugEnabled())
			logger.debug("[CIM] getUserChatHistory() method entered for user: " + userName);

		String fetchTime = String.valueOf(System.currentTimeMillis()-fetchLimit);

		ChatHistoryResponse response = new ChatHistoryResponse();
		if(userName != null) {			
			List<ChatHistory> chatHistories = new ArrayList<ChatHistory>();			
			response.setAconyxuser(userName);

			if(buddyList != null && buddyList.size() > 0) {
				Connection connection = null;
				Statement preStatement = null; 
				ResultSet res = null;
				
				try {
					// obtain DB connection
					if(currentDataSource == null) {
						logger.error("[TAS] getUserChatHistory() DataSource is null.");
						return null;
					} else {
						connection = currentDataSource.getConnection();
					}

					if (connection == null) {
						logger.error("[TAS] getUserChatHistory() DB connection is null.");
						return null;
					}

					for(String buddy:buddyList) {
						ChatHistory chatHis = new ChatHistory();
						chatHis.setBuddy(buddy);

						try {
							preStatement = connection.createStatement();
							String query = "SELECT * from tas_activity_logs where user_id = '"+userName.replaceAll("'", "''")+"' AND phone_number ='"+buddy.replaceAll("'", "''")+
							"' AND activity_type IN ('SENT_MESSAGE', 'RECEIVED_MESSAGE', 'WAITING_MESSAGE', 'DELIVERED_MESSAGE')	AND time_stamp >= '" + fetchTime + "' order by time_stamp";		   
							res = preStatement.executeQuery(query);
							List<Message> messList = new ArrayList<Message>();
							while(res.next()) {
								Message message = new Message();
								message.setContent(res.getString("message"));
								message.setTimestamp(res.getString("time_stamp"));

								if(res.getString("activity_type").equals("SENT_MESSAGE")){
									message.setDirection("SEND");
								}
								else {
									message.setDirection("REC");
								}

								if(res.getString("network_info").equals("SIP")){
									message.setType("IM");
								} else {
									message.setType("GSM");
								}

								messList.add(message);
							} 
							chatHis.setMessages(messList);
						} catch (SQLException e) {
							logger.error(" CIMDAOImpl: getUserChatHistory() SQLException occured ",e);
							throw e;
						} catch (Exception e) {
							logger.error(" CIMDAOImpl: getUserChatHistory() SQLException occured ",e);
							throw e;
						} finally {
							if(res != null){
								res.close();
							}	
							if(preStatement != null){
								preStatement.close();
							}
							if(connection != null){
								connection.close();
							}
						}
						chatHistories.add(chatHis);

						// update status of waiting messages
						updateChatStatus(userName, buddy);
					}
				} catch(Exception e) {
					logger.error(" CIMDAOImpl: getUserChatActivityLog() SQLException occured ",e);
					throw e;
				} finally {
					if(connection != null){
						connection.close();
					}
				}
			}
			response.setChatHistories(chatHistories);
		}
		return response;
	}
	
	public ChatHistoryResponse getUserChatActivityLog(String userName, List<String> buddyList) throws SQLException, Exception {

		if(logger.isDebugEnabled())
			logger.debug("[CIM] getUserChatActivityLog() method entered for user: " + userName);

		String fetchTime = String.valueOf(System.currentTimeMillis()-fetchLimit);

		ChatHistoryResponse response = new ChatHistoryResponse();
		if(userName != null) {			
			List<ChatHistory> chatHistories = new ArrayList<ChatHistory>();			
			response.setAconyxuser(userName);
			
			if(buddyList != null && buddyList.size() > 0) {
				
				Connection connection = null;
				Statement preStatement = null; 
				ResultSet res = null;

				try {
					// obtain DB connection
					if(currentDataSource == null) {
						logger.error("[TAS] getUserChatActivityLog() DataSource is null.");
						return null;
					} else {
						connection = currentDataSource.getConnection();
					}

					if (connection == null) {
						logger.error("[TAS] getUserChatActivityLog() DB connection is null.");
						return null;
					}				

					for(String buddy:buddyList) {
						ChatHistory chatHis = new ChatHistory();
						chatHis.setBuddy(buddy);

						try {
							preStatement = connection.createStatement();
							String query = "SELECT * from tas_activity_logs where user_id = '"+userName.replaceAll("'", "''")+"' AND phone_number ='"+buddy.replaceAll("'", "''")+
							"' AND activity_type IN ('SENT_MESSAGE', 'DELIVERED_MESSAGE', 'RECEIVED_MESSAGE', 'WAITING_MESSAGE')	AND time_stamp >= '" + fetchTime + "' order by time_stamp";		   
							res = preStatement.executeQuery(query);
							List<Message> messList = new ArrayList<Message>();
							while(res.next()) {
								Message message = new Message();
								message.setContent(res.getString("message"));
								message.setTimestamp(res.getString("time_stamp"));
								message.setDirection(res.getString("activity_type"));
								message.setType(res.getString("network_info"));						
								messList.add(message);
							} 
							chatHis.setMessages(messList);
						} catch (SQLException e) {
							logger.error(" CIMDAOImpl: getUserChatActivityLog() SQLException occured ",e);
							throw e;
						} catch (Exception e) {
							logger.error(" CIMDAOImpl: getUserChatActivityLog() SQLException occured ",e);
							throw e;
						} finally {
							if(res != null){
								res.close();
							}	
							if(preStatement != null){
								preStatement.close();
							}
						}
						chatHistories.add(chatHis);

						// update status of waiting messages
						updateChatStatus(userName, buddy);

					}
				} catch(Exception e) {
					logger.error(" CIMDAOImpl: getUserChatActivityLog() SQLException occured ",e);
					throw e;
				} finally {
					if(connection != null){
						connection.close();
					}
				}
			}
			response.setChatHistories(chatHistories);
		}
		return response;
	}

	@Override
	public String fetchPhoneNumber(String userName) throws SQLException,
			Exception {
		if(logger.isDebugEnabled())
			logger.debug("[CIM] fetchPhoneNumber() method entered with userName: " + userName);

		String phoneNumber=null;

		if(userName!=null){
			Connection connection = null;
			PreparedStatement preStatement = null; 
			ResultSet res = null;
			try{
				connection = currentDataSource.getConnection();
				preStatement = connection.prepareStatement("select phone_home,phone_office,phone_mobile from contact_info where user_id=(select id from user_detail where username=?)");		   
				preStatement.setString(1, userName);
				res=preStatement.executeQuery();
				while(res.next()){
					phoneNumber = res.getString("phone_mobile");
					if(phoneNumber==null){
						phoneNumber = res.getString("phone_home");
						if(phoneNumber==null){
							phoneNumber = res.getString("phone_office");
						}
					}
				} 
			} catch (SQLException e) {
				logger.error(" CIMDAOImpl: fetchPhoneNumber() SQLException occured ",e);
				throw e;
			} catch (Exception e) {
				logger.error(" CIMDAOImpl: fetchPhoneNumber() SQLException occured ",e);
				throw e;
			} finally {
				if(res != null){
					res.close();
				}	
				if(preStatement != null){
					preStatement.close();
				}
				if(connection != null){
					connection.close();
				}
			}
		}
		if(logger.isDebugEnabled())
			logger.debug("[CIM] fetchPhoneNumber(): Exiting with value " + phoneNumber);
		return phoneNumber;
	}

	@Override
	public void insertChatLogs(List<CIMMessageDAO> list) throws SQLException,
			Exception {
		if(logger.isDebugEnabled())
			logger.debug("[CIM] insertChatLogs() method entered ");

		if(list==null || list.isEmpty()){
			if(logger.isDebugEnabled())
				logger.debug("[CIM] insertChatLogs(): exiting as list is empty.");
			return;
		}
		
		Connection connection = null;
		PreparedStatement preStatement = null; 
		
		// obtain DB connection
		if(currentDataSource == null) {
			logger.error("[TAS] insertChatLog() DataSource is null.");
			return;
		} else {
			connection = currentDataSource.getConnection();
		}

		if (connection == null) {
			logger.error("[TAS] insertChatLog() DB connection is null.");
			return;
		}
		
		try{
			

			String insertQuery = "INSERT INTO tas_activity_logs VALUES(?,?,?,?,?,?,?)";
			preStatement = connection.prepareStatement(insertQuery);
			
			for(CIMMessageDAO messageObject:list){
				preStatement.setString(1,messageObject.getId());
				preStatement.setString(2,messageObject.getUser());
				preStatement.setString(3,messageObject.getPhoneNumber());
				preStatement.setString(4,Long.toString(System.currentTimeMillis()));
				preStatement.setString(5,messageObject.getActivityType());
				preStatement.setString(6,messageObject.getMessage());
				preStatement.setString(7,messageObject.getNetwork());
				preStatement.execute();
			}
			
		} catch (SQLException e) {
			logger.error(" CIMDAOImpl: insertChatLog() SQLException occured ",e);
			throw e;
		} catch (Exception e) {
			logger.error(" CIMDAOImpl: insertChatLog() SQLException occured ",e);
			throw e;
		} finally { 	
			if(preStatement != null){
				preStatement.close();
			}
			if(connection != null){
				connection.close();
			}
		}
		if(logger.isDebugEnabled())
			logger.debug("[CIM] insertChatLogs(): Exiting..");
		
	}
	
}
