/*

 * CABDAOImpl.java
 * @author Amit Baxi
 */
package com.baypackets.ase.sysapps.cab.dao.rdbms;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.Hashtable;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;
import org.apache.log4j.Logger;
import com.baypackets.ase.sysapps.cab.util.Constants;
import com.baypackets.ase.sysapps.cab.dao.CABDAO;
import com.baypackets.ase.sysapps.cab.jaxb.AddressBookGroup;
import com.baypackets.ase.sysapps.cab.jaxb.ContactView;
import com.baypackets.ase.sysapps.cab.jaxb.ModifyAddressBookGroup;
import com.baypackets.ase.sysapps.cab.jaxb.NonAconyxMember;
import com.baypackets.ase.sysapps.cab.jaxb.PersonalContactCard;
import com.baypackets.ase.sysapps.cab.manager.CABManager;
import com.baypackets.ase.sysapps.cab.maps.CABDBMaps;
import com.baypackets.ase.sysapps.cab.util.Configuration;
/**
 *This class provides implementation of CABDAO methods for database related operations for CAB application.
 */
public class CABDAOImpl implements CABDAO {

	private static Logger logger=Logger.getLogger(CABDAOImpl.class);
	private static final String KEY_SEPERATOR = "|";
	static InitialContext ctx;
	static DataSource  regDataSource;
	private static long contactViewID_Counter=-1;
	private static long addressBookGroupID_Counter=-1;
	
	static {
		
		String PROVIDER_URL = "file:" + System.getProperty(Constants.ASE_HOME)+ Constants.PATH_JNDI_FILESERVER;
		String CONTEXT_FACTORY = Constants.CONTEXT_FACTORY;

		if (logger.isDebugEnabled()) {
			logger.debug("[CAB] CABDaoImpl Getting Data source");
		}

		try {
			Hashtable<String, String> env = new Hashtable<String, String>();
			env.put(Context.INITIAL_CONTEXT_FACTORY, CONTEXT_FACTORY);
			env.put(Context.PROVIDER_URL, PROVIDER_URL);
			ctx = new InitialContext(env);
			String dataSourceName=Configuration.getInstance().getParamValue(Constants.PROP_CAB_DATASOURCE_NAME);
			dataSourceName=(dataSourceName!=null && ! dataSourceName.trim().isEmpty() )?dataSourceName:Constants.PATH_DATASOURCE;
			regDataSource=(DataSource)ctx.lookup(dataSourceName);

		} 
		catch (Exception e) {
			logger.error("[CAB] CABDaoImpl  Exception in datasource lookup", e);
		}
	}
	
	@Override
	public List<PersonalContactCard> createPCC(List<PersonalContactCard> pccList)
			throws SQLException, Exception {
		if(logger.isDebugEnabled())
			logger.debug("[CAB] CABDaoImpl: createPCC() entered");
		List<PersonalContactCard> finalPccList=new LinkedList<PersonalContactCard>();
		if(pccList!=null && pccList.size()!=0){
			Connection connection = null;
			Statement insertStmt = null;
			String prepareStatementQuery = "INSERT INTO cab_personal_contact_cards(ACONYX_USERNAME, FIRSTNAME, LASTNAME, ADDRESS, CITY, STATE, COUNTRY, CONTACT1, CONTACT2, COMPANY, DEPARTMENT,DESIGNATION, GENDER, DOB, EMAIL1,EMAIL2) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
			PreparedStatement preparedStatement=null;
			ResultSet res=null;
			try {
				connection = regDataSource.getConnection();
				if (connection == null) {
					logger.error("[CAB] CABDaoImpl:createPCC No Connection");
					throw new Exception("No DB Connection");
				}
				insertStmt = connection.createStatement();
				preparedStatement=connection.prepareStatement(prepareStatementQuery);
				int size=pccList.size();
				for (int i = 0; i < size;i++){
					PersonalContactCard pcc=pccList.get(i);
					String aconyxUsername=pcc.getAconyxUsername();					
					res=insertStmt.executeQuery("SELECT Count(*) FROM cab_personal_contact_cards WHERE ACONYX_USERNAME = '"+aconyxUsername+"'");
					int count=0;
					while(res.next()){
						count=res.getInt(1);
		 			}
					if(count==0){	
					
						
					preparedStatement.setString(1, aconyxUsername);
					preparedStatement.setString(2, pcc.getFirstName());
					preparedStatement.setString(3, pcc.getLastName());
					preparedStatement.setString(4, pcc.getAddress());
					preparedStatement.setString(5, pcc.getCity());
					preparedStatement.setString(6, pcc.getState());
					preparedStatement.setString(7, pcc.getCountry());
					preparedStatement.setString(8, pcc.getContact1());
					preparedStatement.setString(9, pcc.getContact2());
					preparedStatement.setString(10, pcc.getCompany());
					preparedStatement.setString(11, pcc.getDepartment());
					preparedStatement.setString(12, pcc.getDesignation());
					preparedStatement.setString(13, pcc.getGender());
					preparedStatement.setString(14, pcc.getDob());
					preparedStatement.setString(15, pcc.getEmail1());
					preparedStatement.setString(16, pcc.getEmail2());
					
					try {
							connection.setAutoCommit(false);
							preparedStatement.execute();
							long id=this.getNextContactViewID();
							String insertQuery = "INSERT INTO cab_contact_views(ID,ACONYX_USERNAME,NAME) VALUES ('"+id+"','"+ aconyxUsername	+ "', '"+Constants.DEFAULT_CONTACT_VIEW_NAME+"')";
							insertStmt.execute(insertQuery);
							for(String fieldName:CABManager.DEFAULT_CONTACT_VIEW_FIELDS)
								{
								int fieldId=CABManager.FIELD_ID_MAP.get(fieldName);
								insertQuery="INSERT INTO cab_contact_view_fields_map(ID,FIELD_ID) VALUES ('"+id+"','"+fieldId+ "')";
								insertStmt.execute(insertQuery);
							}
							long groupId=this.getNextAddressBookGroupID();
							insertQuery ="INSERT INTO cab_address_book_groups(ID,ACONYX_USERNAME,NAME,CONTACT_VIEW_ID) VALUES ('"+groupId+"','"+ aconyxUsername	+ "', '"+Constants.DEFAULT_ADDRESS_BOOK_GROUP_NAME+"','"+id+"')";
							insertStmt.execute(insertQuery);
							connection.commit();
						pcc.setStatus(Constants.STATUS_SUCCESS);
						String key=aconyxUsername+KEY_SEPERATOR+Constants.DEFAULT_CONTACT_VIEW_NAME;
						CABDBMaps.getInstance().getConactViewMap().put(key,id);
						key=aconyxUsername+KEY_SEPERATOR+Constants.DEFAULT_ADDRESS_BOOK_GROUP_NAME;
						CABDBMaps.getInstance().getAddressGroupMap().put(key,groupId);
						
					} catch (Exception e) {
						logger.error(e.toString(),e);
						pcc.setStatus(Constants.STATUS_FAILED);
					}
					
					}else 
						pcc.setStatus(Constants.STATUS_ALREADY_CONFIGURED);
					finalPccList.add(pcc);
				}
			} catch (SQLException e) {
				logger.error("[CAB] CABDaoImpl: createPcc() SQL Exception occured ",e);
				throw e;
			} catch (Exception e) {
				logger.error("[CAB] CABDaoImpl: createPcc() Exception occured ",e);
				throw e;
			} finally {
				if(res!=null)
					res.close();
				if(insertStmt!=null)
					insertStmt.close();
				if(preparedStatement!=null)
					preparedStatement.close();
				if(connection!=null)
					connection.close(); 
			}
		}else if(logger.isDebugEnabled()){
		     logger.debug("[CAB] CABDaoImpl: empty list exiting....");
		  }
		if(logger.isDebugEnabled())
			logger.debug("[CAB] CABDaoImpl: createPCC() exiting...");
		return finalPccList;
	}

	@Override
	public List<PersonalContactCard> modifyPCC(List<PersonalContactCard> pccList)
			throws SQLException, Exception {
		if(logger.isDebugEnabled())
		logger.debug("[CAB] CABDaoImpl: modifyPCC() entered");
		List<PersonalContactCard> finalPccList=new LinkedList<PersonalContactCard>();
		if(pccList!=null && pccList.size()!=0){
			Connection connection = null;
			Statement statement = null;
			PreparedStatement pStatement=null;
			String updateQuery = "Update cab_personal_contact_cards SET FIRSTNAME=?, LASTNAME=?, ADDRESS=?, CITY=?, STATE=?, COUNTRY=?, CONTACT1=?, CONTACT2=?, COMPANY=?, DEPARTMENT=?,DESIGNATION=?, GENDER=?, DOB=?, EMAIL1=?,EMAIL2=? WHERE ACONYX_USERNAME=?";
			ResultSet res=null;
			try {
				connection = regDataSource.getConnection();
				if (connection == null) {
					logger.error("[CAB] CABDaoImpl: modifyPCC() No Connection");
					throw new Exception("No DB Connection");
				}
				statement = connection.createStatement();
				pStatement=connection.prepareStatement(updateQuery);
				int size=pccList.size();
				for (int i = 0; i < size;i++){
					PersonalContactCard pcc=pccList.get(i);
					String aconyxUsername=pcc.getAconyxUsername();
					res=statement.executeQuery("SELECT Count(*) FROM cab_personal_contact_cards WHERE ACONYX_USERNAME = '"+aconyxUsername+"'");
					int count=0;
					while(res.next()){
						count=res.getInt(1);
		 			}
					if(count==1){
						pStatement.setString(1, pcc.getFirstName());
						pStatement.setString(2, pcc.getLastName());
						pStatement.setString(3, pcc.getAddress());
						pStatement.setString(4, pcc.getCity());
						pStatement.setString(5, pcc.getState());
						pStatement.setString(6, pcc.getCountry());
						pStatement.setString(7, pcc.getContact1());
						pStatement.setString(8, pcc.getContact2());
						pStatement.setString(9, pcc.getCompany());
						pStatement.setString(10, pcc.getDepartment());
						pStatement.setString(11, pcc.getDesignation());
						pStatement.setString(12, pcc.getGender());
						pStatement.setString(13, pcc.getDob());
						pStatement.setString(14, pcc.getEmail1());
						pStatement.setString(15, pcc.getEmail2());
						pStatement.setString(16, aconyxUsername);
						try {
							pStatement.execute();
							pcc.setStatus(Constants.STATUS_SUCCESS);
						} catch (Exception e) {
							logger.error(e.toString(),e);
							pcc.setStatus(Constants.STATUS_FAILED);
						}
		 			}
					else
						pcc.setStatus(Constants.STATUS_NOT_CONFIGURED);
					finalPccList.add(pcc);
				}
			} catch (SQLException e) {
				logger.error("[CAB] CABDaoImpl: modifyPCC() SQL Exception occured ",e);
				throw e;
			} catch (Exception e) {
				logger.error("[CAB] CABDaoImpl: modifyPCC() Exception occured ",e);
				throw e;
			} finally {
				if(res!=null)
					res.close();
				if(statement!=null)
					statement.close();
				if(pStatement!=null)
					pStatement.close();
				if(connection!=null)
					connection.close(); 
			}
		}else if(logger.isDebugEnabled()){
		     logger.debug("[CAB] CABDaoImpl: empty list exiting....");
		  }
		if(logger.isDebugEnabled())
			logger.debug("[CAB] CABDaoImpl: modifyPCC() exiting...");
		return finalPccList;
	}

	@Override
	public List<PersonalContactCard> deletePCC(List<String> aconyxUsernameList)
	throws SQLException, Exception {
		if(logger.isDebugEnabled())
			logger.debug("[CAB] CABDaoImpl: deletePCC() entered");
		List<PersonalContactCard> finalPccList=new LinkedList<PersonalContactCard>();
		if(aconyxUsernameList!=null && aconyxUsernameList.size()!=0){
			Connection connection = null;
			Statement statement = null;
			ResultSet res=null;
			try {
				connection = regDataSource.getConnection();
				if (connection == null) {
					logger.error("[CAB] CABDaoImpl: deletePCC() No Connection");
					throw new Exception("No DB Connection");
				}
				statement = connection.createStatement();
				int size=aconyxUsernameList.size();
				for (int i = 0; i < size;i++){
					PersonalContactCard pcc=new PersonalContactCard();
					String aconyxUsername=aconyxUsernameList.get(i);
					pcc.setAconyxUsername(aconyxUsername);
					res=statement.executeQuery("SELECT count(*) FROM cab_personal_contact_cards WHERE ACONYX_USERNAME = '"+aconyxUsername+"'");
					int count=0;
					while(res.next()){
						count=res.getInt(1);
					}
					boolean present=false;
					if(count!=0){
						present=true;						
						String deleteQuery = "DELETE FROM cab_personal_contact_cards WHERE ACONYX_USERNAME='"+aconyxUsername+"'";						
						try {
							statement.execute(deleteQuery);
							pcc.setStatus(Constants.STATUS_SUCCESS);
							CABDBMaps.getInstance().removeFromMaps(aconyxUsername);
						} catch (Exception e) {
							logger.error(e.toString(),e);
							pcc.setStatus(Constants.STATUS_FAILED);
						}					
					}
					if(!present) 
						pcc.setStatus(Constants.STATUS_NOT_CONFIGURED);
					finalPccList.add(pcc);
				}
			} catch (SQLException e) {
				logger.error("[CAB] CABDaoImpl: deletePCC() SQL Exception occured ",e);
				throw e;
			} catch (Exception e) {
				logger.error("[CAB] CABDaoImpl: deletePCC() Exception occured ",e);
				throw e;
			} finally {
				if(res!=null)
					res.close();
				if(statement!=null)
					statement.close();
				if(connection!=null)
					connection.close(); 
			}
		}
		else if(logger.isDebugEnabled()){
			logger.debug("[CAB] CABDaoImpl: empty list exiting....");
		}
		if(logger.isDebugEnabled())
			logger.debug("[CAB] CABDaoImpl: deletePCC() exiting");
		return finalPccList;
	}

	@Override
	public List<PersonalContactCard> getPCC(List<String> aconyxUsernameList)
	throws SQLException, Exception {
		if(logger.isDebugEnabled())
		logger.debug("[CAB] CABDaoImpl: getPCC() entered");
		List<PersonalContactCard> finalPccList=new LinkedList<PersonalContactCard>();
		if(aconyxUsernameList!=null && aconyxUsernameList.size()!=0){
			Connection connection = null;
			Statement statement = null;
			ResultSet res=null;
			try {
				connection = regDataSource.getConnection();
				if (connection == null) {
					logger.error("[CAB] CABDaoImpl: getPCC() No Connection");
					throw new Exception("No DB Connection");
				}
				statement = connection.createStatement();
				int size=aconyxUsernameList.size();
				for (int i = 0; i < size;i++){
					PersonalContactCard pcc=new PersonalContactCard();
					String aconyxUsername=aconyxUsernameList.get(i);
					pcc.setAconyxUsername(aconyxUsername);
					res=statement.executeQuery("SELECT * FROM cab_personal_contact_cards WHERE ACONYX_USERNAME = '"+aconyxUsername+"'");
					boolean present=false;
					while(res.next()){
						present=true;						
						pcc.setFirstName(res.getString("FIRSTNAME"));
						pcc.setLastName(res.getString("LASTNAME"));
						pcc.setAddress(res.getString("ADDRESS"));
						pcc.setCity(res.getString("CITY"));
						pcc.setState(res.getString("STATE"));
						pcc.setCountry(res.getString("COUNTRY"));
						pcc.setContact1(res.getString("CONTACT1"));
						pcc.setContact2(res.getString("CONTACT2"));
						pcc.setCompany(res.getString("COMPANY"));
						pcc.setDepartment(res.getString("DEPARTMENT"));
						pcc.setDesignation(res.getString("DESIGNATION"));
						pcc.setGender(res.getString("GENDER"));
						pcc.setDob(res.getString("DOB"));
						pcc.setEmail1(res.getString("EMAIL1"));
						pcc.setEmail2(res.getString("EMAIL2"));						
						pcc.setStatus(Constants.STATUS_SUCCESS);	
					}if(!present) 
						pcc.setStatus(Constants.STATUS_NOT_CONFIGURED);
					finalPccList.add(pcc);
				}
			} catch (SQLException e) {
				logger.error("[CAB] CABDaoImpl: getPCC() SQL Exception occured ",e);
				throw e;
			} catch (Exception e) {
				logger.error("[CAB] CABDaoImpl: getPCC() Exception occured ",e);
				throw e;
			} finally {
				if(res!=null)
					res.close();
				if(statement!=null)
					statement.close();
				if(connection!=null)
					connection.close(); 
			}
		}else if(logger.isDebugEnabled()){
		     logger.debug("[CAB] CABDaoImpl: empty list exiting....");
		  }
		
		if(logger.isDebugEnabled())
			logger.debug("[CAB] CABDaoImpl: getPCC() exiting....");
		return finalPccList;
	}

	@Override
	public void loadPCCFieldMapFromDB() throws SQLException, Exception{
		if(logger.isDebugEnabled())
		logger.debug("[CAB] CABDaoImpl: loadPCCMapFromDB() entered");
		Connection connection = null;
		Statement statement = null;
		ResultSet res=null;
		try {
			connection = regDataSource.getConnection();
			if (connection == null) {
				logger.error("[CAB] CABDaoImpl: loadPCCMapFromDB() No Connection");
				throw new Exception("No DB Connection");
			}
			statement = connection.createStatement();
			res=statement.executeQuery("SELECT FIELD_NAME,FIELD_ID,IS_PUBLIC FROM cab_pcc_fields_map");
			while(res.next()){
				String fieldName=res.getString("FIELD_NAME");
				int fieldId=res.getInt("FIELD_ID");
				int isPublic=res.getInt("IS_PUBLIC");
				CABManager.FIELD_ID_MAP.put(fieldName, fieldId);
				if(isPublic!=0){
					CABManager.DEFAULT_CONTACT_VIEW_FIELDS.add(fieldName);
				}
				logger.info("[Map Entry for cab_pcc_fields_map: "+fieldName+"<-->"+fieldId+"]");
			}	
		}catch (SQLException e) {
			logger.error("[CAB] CABDaoImpl: loadPCCMapFromDB() SQL Exception occured ",e);
			throw e;
		} catch (Exception e) {
			logger.error("[CAB] CABDaoImpl: loadPCCMapFromDB() Exception occured ",e);
			throw e;
		} finally {
			if(res!=null)
				res.close();
			if(statement!=null)
				statement.close();
			if(connection!=null)
				connection.close(); 
		}
		if(logger.isDebugEnabled())
			logger.debug("[CAB] CABDaoImpl: loadPCCMapFromDB() exiting....");
	}

	@Override
	public List<ContactView> createContactViews(
			List<ContactView> contactViewList) throws SQLException, Exception {
		if(logger.isDebugEnabled())
			logger.debug("[CAB] CABDaoImpl: createContactViews() entered");
		List<ContactView> finalContactViewList=new LinkedList<ContactView>();
		if(contactViewList!=null && contactViewList.size()!=0){
			Connection connection = null;
			Statement statement = null;
			try {
				connection = regDataSource.getConnection();
				if (connection == null) {
					logger.error("[CAB] CABDaoImpl: createContactViews() No Connection");
					throw new Exception("No DB Connection");
				}
				statement = connection.createStatement();
				int size=contactViewList.size();
				for (int i = 0; i < size;i++){
					ContactView contactView=contactViewList.get(i);
					String aconyxUsername=contactView.getAconyxUsername();
					String name=contactView.getName();
					long id=this.getNextContactViewID();
					String insertQuery = "INSERT INTO cab_contact_views(ID,ACONYX_USERNAME,NAME) VALUES ('"+id+"','"+ aconyxUsername	+ "', '"+ name+ "')";
						try {
							connection.setAutoCommit(false);
							statement.execute(insertQuery);
							for(String fieldName:contactView.getFieldList())
							{
								int fieldId=CABManager.FIELD_ID_MAP.get(fieldName);
								insertQuery="INSERT INTO cab_contact_view_fields_map(ID,FIELD_ID) VALUES ('"+id+"','"+fieldId+ "')";
								statement.execute(insertQuery);
							}
							connection.commit();
							String key=aconyxUsername+KEY_SEPERATOR+name;
							CABDBMaps.getInstance().getConactViewMap().put(key,id);
							contactView.setStatus(Constants.STATUS_SUCCESS);

						} catch (Exception e) {
							logger.error(e.toString(),e);
							contactView.setStatus(Constants.STATUS_FAILED);
						}	
					finalContactViewList.add(contactView);
				}
			} catch (SQLException e) {
				logger.error("[CAB] CABDaoImpl: createContactViews() SQL Exception occured ",e);
				throw e;
			} catch (Exception e) {
				logger.error("[CAB] CABDaoImpl: createContactViews() Exception occured ",e);
				throw e;
			} finally {
				if(statement!=null)
					statement.close();
				if(connection!=null)
					connection.close(); 
			}
		}else if(logger.isDebugEnabled()){
		     logger.debug("[CAB] CABDaoImpl: empty list exiting....");
		  }
		
		if(logger.isDebugEnabled())
			logger.debug("[CAB] CABDaoImpl: createContactViews() exiting....");
		return finalContactViewList;
	}

	@Override
	public List<ContactView> deleteContactViews(
			List<ContactView> contactViewList) throws SQLException, Exception {
		if(logger.isDebugEnabled())
		logger.debug("[CAB] CABDaoImpl: deleteContactViews() entered");
		List<ContactView> finalContactViewList=new LinkedList<ContactView>();
		if(contactViewList!=null && contactViewList.size()!=0){
			Connection connection = null;
			Statement statement = null;
			ResultSet resultSet=null;
			try {
				connection = regDataSource.getConnection();
				if (connection == null) {
					logger.error("[CAB] CABDaoImpl: deleteContactViews() No Connection");
					throw new Exception("No DB Connection");
				}
				statement = connection.createStatement();
				int size=contactViewList.size();
				ConcurrentHashMap<String, Long> contactViewIDMap=CABDBMaps.getInstance().getConactViewMap();
				for (int i = 0; i < size;i++){
					ContactView contactView=contactViewList.get(i);
					String aconyxUsername=contactView.getAconyxUsername();
					String name=contactView.getName();	
					String key=aconyxUsername+KEY_SEPERATOR+name;
					Long id=contactViewIDMap.get(key);
					String validationQuery="SELECT Count(*) FROM cab_address_book_groups WHERE CONTACT_VIEW_ID = '"+id+"'";
					try {
						resultSet=statement.executeQuery(validationQuery);
						int count=0;
						while(resultSet.next()){
							count=resultSet.getInt(1);
						}
						if(count==0){
							String deleteQuery = "DELETE FROM cab_contact_views WHERE ACONYX_USERNAME = '"+aconyxUsername+"' AND NAME='"+name+"'";
							statement.execute(deleteQuery);//cab_contact_view_fields_map entries will be deleted due to cascade delete.
							contactViewIDMap.remove(key);
							contactView.setStatus(Constants.STATUS_SUCCESS);
						}else{
							contactView.setStatus(Constants.STATUS_ALREADY_IN_USE);
						}
					} catch (Exception e) {
							logger.error(e.toString(),e);
							contactView.setStatus(Constants.STATUS_FAILED);
						}
					finalContactViewList.add(contactView);
				}	
			} catch (SQLException e) {
				logger.error("[CAB] CABDaoImpl: deleteContactViews() SQL Exception occured ",e);
				throw e;
			} catch (Exception e) {
				logger.error("[CAB] CABDaoImpl: deleteContactViews() Exception occured ",e);
				throw e;
			} finally {
				if(resultSet!=null)
					resultSet.close();
				if(statement!=null)
					statement.close();
				if(connection!=null)
					connection.close(); 
			}
		}else if(logger.isDebugEnabled()){
		     logger.debug("[CAB] CABDaoImpl: empty list exiting....");
		  }
		if(logger.isDebugEnabled())
			logger.debug("[CAB] CABDaoImpl: deleteContactViews() exiting....");
		return finalContactViewList;
	
	}

	@Override
	public List<ContactView> getContactViews(List<String> aconyxUsernameList)
			throws SQLException, Exception {
		if(logger.isDebugEnabled())
		logger.debug("[CAB] CABDaoImpl: getContactViews() entered");
		List<ContactView> finalContactViewList=new LinkedList<ContactView>();
		if(aconyxUsernameList!=null && aconyxUsernameList.size()!=0){
			Connection connection = null;
			Statement statement = null;
			Statement innerStatement=null;
			ResultSet resultSet=null;
			ResultSet innerResultSet=null;
			try {
				connection = regDataSource.getConnection();
				if (connection == null) {
					logger.error("[CAB] CABDaoImpl: getContactViews() No Connection");
					throw new Exception("No DB Connection");
				}
				statement = connection.createStatement();
				int size=aconyxUsernameList.size();
				for (int i = 0; i < size;i++){
					String aconyxUsername=aconyxUsernameList.get(i);
					resultSet=statement.executeQuery("SELECT ID,NAME FROM cab_contact_views WHERE ACONYX_USERNAME = '"+aconyxUsername+"'");
					boolean present=false;
					while(resultSet.next()){
						present=true;
						ContactView view=new ContactView();
						view.setAconyxUsername(aconyxUsername);
						int viewId=resultSet.getInt("ID");
						view.setName(resultSet.getString("NAME"));
						List <String> fieldNameList=new LinkedList<String>();
						if(innerResultSet==null)
						innerStatement=connection.createStatement();
						String innerQuery="select FIELD_NAME FROM cab_pcc_fields_map, cab_contact_view_fields_map where cab_contact_view_fields_map.ID='"+viewId+"' AND cab_contact_view_fields_map.FIELD_ID= cab_pcc_fields_map.FIELD_ID ";
						innerResultSet=innerStatement.executeQuery(innerQuery);
						while(innerResultSet.next()){
							String fieldName=innerResultSet.getString("FIELD_NAME");
							fieldNameList.add(fieldName);
						}
						view.setFieldList(fieldNameList);
						view.setStatus(Constants.STATUS_SUCCESS);
						finalContactViewList.add(view);
					}				
					if(!present){
						ContactView contactView=new ContactView(aconyxUsername, null, null, Constants.STATUS_NOT_CONFIGURED);
						finalContactViewList.add(contactView);
					} 						

				}
			} catch (SQLException e) {
				logger.error("[CAB] CABDaoImpl: getContactViews() SQL Exception occured ",e);
				throw e;
			} catch (Exception e) {
				logger.error("[CAB] CABDaoImpl: getContactViews() Exception occured ",e);
				throw e;
			} finally {
				if(innerResultSet!=null)
					innerResultSet.close();
				if(innerStatement!=null)
					innerStatement.close();
				if(resultSet!=null)
					resultSet.close();
				if(statement!=null)
					statement.close();
				if(connection!=null)
					connection.close(); 
			}
		}else if(logger.isDebugEnabled()){
		     logger.debug("[CAB] CABDaoImpl: empty list exiting....");
		  }
		if(logger.isDebugEnabled())
			logger.debug("[CAB] CABDaoImpl: getContactViews() exiting....");
		return finalContactViewList;
	}

	@Override
	public List<ContactView> modifyContactViews(
			List<ContactView> contactViewList) throws SQLException, Exception {
		if(logger.isDebugEnabled())
			logger.debug("[CAB] CABDaoImpl: modifyContactViews() entered");
		List<ContactView> finalContactViewList=new LinkedList<ContactView>();
		if(contactViewList!=null && contactViewList.size()!=0){
			Connection connection = null;
			Statement statement = null;
			try {
				connection = regDataSource.getConnection();
				if (connection == null) {
					logger.error("[CAB] CABDaoImpl: modifyContactViews() No Connection");
					throw new Exception("No DB Connection");
				}
				statement = connection.createStatement();
				int size=contactViewList.size();
				for (int i = 0; i < size;i++){
					ContactView contactView=contactViewList.get(i);
					String aconyxUsername=contactView.getAconyxUsername();
					String name=contactView.getName();
					String key=aconyxUsername+KEY_SEPERATOR+name;
						try {
							long id=CABDBMaps.getInstance().getConactViewMap().get(key);
							connection.setAutoCommit(false);
							String deleteQuery="DELETE FROM cab_contact_view_fields_map WHERE ID='"+id+"'";
							statement.execute(deleteQuery);
							for(String fieldName:contactView.getFieldList())
							{
								int fieldId=CABManager.FIELD_ID_MAP.get(fieldName);
								String insertQuery="INSERT INTO cab_contact_view_fields_map(ID,FIELD_ID) VALUES ('"+id+"','"+fieldId+ "')";
								statement.execute(insertQuery);
							}
							connection.commit();
							contactView.setStatus(Constants.STATUS_SUCCESS);

						} catch (Exception e) {
							logger.error(e.toString(),e);
							contactView.setStatus(Constants.STATUS_FAILED);
						}											
					finalContactViewList.add(contactView);
				}

			} catch (SQLException e) {
				logger.error("[CAB] CABDaoImpl: modifyContactViews() SQL Exception occured ",e);
				throw e;
			} catch (Exception e) {
				logger.error("[CAB] CABDaoImpl: modifyContactViews() Exception occured ",e);
				throw e;
			} finally {
				if(statement!=null)
					statement.close();
				if(connection!=null)
					connection.close(); 
			}
		}else if(logger.isDebugEnabled()){
		     logger.debug("[CAB] CABDaoImpl: empty list exiting....");
		  }
		if(logger.isDebugEnabled())
			logger.debug("[CAB] CABDaoImpl: modifyContactViews() exiting....");
		return finalContactViewList;
	
	}

	@Override
	public List<AddressBookGroup> createAddressBookGroups(
			List<AddressBookGroup> groupList) throws SQLException, Exception {
		if(logger.isDebugEnabled())
			logger.debug("[CAB] CABDaoImpl: createAddressBookGroups() entered");
		List<AddressBookGroup> finalgroupList=new LinkedList<AddressBookGroup>();
		if(groupList!=null && groupList.size()!=0){
			Connection connection = null;
			Statement statement = null;
			PreparedStatement pStatement=null;
		
			try {
				connection = regDataSource.getConnection();
				if (connection == null) {
					logger.error("[CAB] CABDaoImpl: createAddressBookGroups() No Connection");
					throw new Exception("No DB Connection");
				}
				statement = connection.createStatement();
				int size=groupList.size();
				for (int i = 0; i < size;i++){
					AddressBookGroup group=groupList.get(i);
					String aconyxUsername=group.getAconyxUsername();
					String name=group.getName();
					String contactViewName=group.getContactViewName();
					List<String> memberList=group.getMemberList();
					List<NonAconyxMember> nonAconyxMemberList=group.getNonAconyxMemberList();
					String key=aconyxUsername+KEY_SEPERATOR+contactViewName;
						long id=this.getNextAddressBookGroupID();
						try {
							CABDBMaps cabdbMaps=CABDBMaps.getInstance();
							long contactViewId=cabdbMaps.getConactViewMap().get(key);
							String insertQuery = "INSERT INTO cab_address_book_groups(ID,NAME,ACONYX_USERNAME,CONTACT_VIEW_ID) VALUES ('"+id+"','"+ name+ "','"+ aconyxUsername+ "', '"+ contactViewId+ "')";
							connection.setAutoCommit(false);
							statement.execute(insertQuery);
							if(memberList!=null){
								for(String member:memberList){
									insertQuery="INSERT INTO cab_address_book_group_members(ID,MEMBER) VALUES ('"+id+"','"+member+ "')";
									statement.execute(insertQuery);
								}
							}
							if(nonAconyxMemberList!=null){
								pStatement=connection.prepareStatement("INSERT INTO cab_non_aconyx_members(ID,NAME,CONTACT,SIP_ADDRESS) VALUES (?,?,?,?)");
								for(NonAconyxMember nonMember:nonAconyxMemberList){
									String mname=nonMember.getName();
									String mContact=nonMember.getContact();
									String mSipAddress=nonMember.getSIPAddress();
									pStatement.setString(1, id+"");
									pStatement.setString(2, mname);
									pStatement.setString(3, mContact);
									pStatement.setString(4, mSipAddress);
									pStatement.executeUpdate();
								}
							}
							connection.commit();
							cabdbMaps.getAddressGroupMap().put(aconyxUsername+KEY_SEPERATOR+name, id);
							group.setStatus(Constants.STATUS_SUCCESS);

						} catch (Exception e) {
							logger.error(e.toString(),e);
							group.setStatus(Constants.STATUS_FAILED);
						}						
					finalgroupList.add(group);
				}
			} catch (SQLException e) {
				logger.error("[CAB] CABDaoImpl: createAddressBookGroups() SQL Exception occured ",e);
				throw e;
			} catch (Exception e) {
				logger.error("[CAB] CABDaoImpl: createAddressBookGroups() Exception occured ",e);
				throw e;
			} finally {
				if(pStatement!=null)
					pStatement.close();
				if(statement!=null)
					statement.close();
				if(connection!=null)
					connection.close(); 
			}
		}else if(logger.isDebugEnabled()){
		     logger.debug("[CAB] CABDaoImpl: empty list exiting....");
		  }
		if(logger.isDebugEnabled())
			logger.debug("[CAB] CABDaoImpl: createAddressBookGroups() exiting....");
		return finalgroupList;
	
	}
	
	
	public static synchronized void loadDBData(ConcurrentHashMap<String, Long> contactViewIDMap,ConcurrentHashMap<String, Long> addressBookGroupIDMap) throws SQLException, Exception{
		if(logger.isDebugEnabled())
			logger.debug("[CAB] CABDaoImpl: loadDBData() entered");			
			CABManager.CAB_APP_STATE=Constants.STATE_LOADING;
			contactViewIDMap.clear();
			addressBookGroupIDMap.clear();
			Connection connection = null;
			Statement statement = null;
			ResultSet res=null;
			try {
				connection = regDataSource.getConnection();
				if (connection == null) {
					logger.error("[CAB] CABDaoImpl: getConactViewMap() No Connection");
					throw new Exception("No DB Connection");
				}
				statement = connection.createStatement();
				res=statement.executeQuery("SELECT ID,ACONYX_USERNAME,NAME FROM cab_contact_views");
				while(res.next()){
					Long id=res.getLong("ID");
					String aconyxUser=res.getString("ACONYX_USERNAME");
					String name=res.getString("NAME");
					contactViewIDMap.put(aconyxUser+KEY_SEPERATOR+name, id);
					if(logger.isDebugEnabled()){
						logger.debug("Inserting into contactViewIDMap:["+aconyxUser+KEY_SEPERATOR+name+"--->"+id+"]");
					}
				}
				res=statement.executeQuery("SELECT ID,ACONYX_USERNAME,NAME FROM cab_address_book_groups");
				while(res.next()){
					Long id=res.getLong("ID");
					String aconyxUser=res.getString("ACONYX_USERNAME");
					String name=res.getString("NAME");
					addressBookGroupIDMap.put(aconyxUser+KEY_SEPERATOR+name, id);
					if(logger.isDebugEnabled()){
						logger.debug("Inserting into addressBookGroupIDMap:["+aconyxUser+KEY_SEPERATOR+name+"--->"+id+"]");
					}
				}
				/////////////// Load max ID /////////////////////////////
				res=statement.executeQuery("SELECT MAX(ID) FROM cab_contact_views"); 
				res.next();
				contactViewID_Counter=res.getLong(1);
				
				res=statement.executeQuery("SELECT MAX(ID) FROM cab_address_book_groups"); 
				res.next();
				addressBookGroupID_Counter=res.getLong(1);
				////////////////////////////////////////////////////////////////////
				CABManager.CAB_APP_STATE=Constants.STATE_LOADED;
			}catch (SQLException e) {
				logger.error("[CAB] CABDaoImpl: getConactViewMap() SQL Exception occured ",e);
				throw e;
			} catch (Exception e) {
				logger.error("[CAB] CABDaoImpl: getConactViewMap() Exception occured ",e);
				throw e;
			} finally {
				if(res!=null)
					res.close();
				if(statement!=null)
					statement.close();
				if(connection!=null)
					connection.close(); 
			}
			if(logger.isDebugEnabled())
				logger.debug("[CAB] CABDaoImpl: loadDBData() exiting..");			
	}
	

	@Override
	public List<AddressBookGroup> deleteAddressBookGroups(
			List<AddressBookGroup> groupList) throws SQLException, Exception {
		if(logger.isDebugEnabled())
			logger.debug("[CAB] CABDaoImpl: deleteAddressBookGroups() entered");
		List<AddressBookGroup> finalAddressBookGroupList=new LinkedList<AddressBookGroup>();
		if(groupList!=null && groupList.size()!=0){
			Connection connection = null;
			Statement statement = null;
			try {
				connection = regDataSource.getConnection();
				if (connection == null) {
					logger.error("[CAB] CABDaoImpl: deleteAddressBookGroups() No Connection");
					throw new Exception("No DB Connection");
				}
				statement = connection.createStatement();
				int size=groupList.size();
				for (int i = 0; i < size;i++){
					AddressBookGroup group=groupList.get(i);
					String aconyxUsername=group.getAconyxUsername();
					String name=group.getName();	
					String key=aconyxUsername+KEY_SEPERATOR+name;
					String deleteQuery = "DELETE FROM cab_address_book_groups WHERE ACONYX_USERNAME = '"+aconyxUsername+"' AND NAME='"+name+"'";					
						try {
							statement.execute(deleteQuery);//cab_address_book_group_members entries will be deleted due to cascade delete.
							CABDBMaps.getInstance().getAddressGroupMap().remove(key);
							group.setStatus(Constants.STATUS_SUCCESS);
							
						} catch (Exception e) {
							logger.error(e.toString(),e);
							group.setStatus(Constants.STATUS_FAILED);
						}
					finalAddressBookGroupList.add(group);
				}
			} catch (SQLException e) {
				logger.error("[CAB] CABDaoImpl: deleteAddressBookGroups() SQL Exception occured ",e);
				throw e;
			} catch (Exception e) {
				logger.error("[CAB] CABDaoImpl: deleteAddressBookGroups() Exception occured ",e);
				throw e;
			} finally {
				if(statement!=null)
					statement.close();
				if(connection!=null)
					connection.close(); 
			}
		}else if(logger.isDebugEnabled()){
		     logger.debug("[CAB] CABDaoImpl: empty list exiting....");
		  }
		if(logger.isDebugEnabled())
			logger.debug("[CAB] CABDaoImpl: deleteAddressBookGroups() exiting....");
		return finalAddressBookGroupList;	
	}

	@Override
	public List<AddressBookGroup> addToAddressBookGroups(
			List<AddressBookGroup> groupList) throws SQLException, Exception {
		if(logger.isDebugEnabled())
			logger.debug("[CAB] CABDaoImpl: addToAddressBookGroups() entered");
		List<AddressBookGroup> finalgroupList=new LinkedList<AddressBookGroup>();
		if(groupList!=null && groupList.size()!=0){
			Connection connection = null;
			Statement statement = null;
			PreparedStatement pStatement=null;
			try {
				connection = regDataSource.getConnection();
				if (connection == null) {
					logger.error("[CAB] CABDaoImpl: addToAddressBookGroups() No Connection");
					throw new Exception("No DB Connection");
				}
				statement = connection.createStatement();
				int size=groupList.size();
				for (int i = 0; i < size;i++){
					AddressBookGroup group=groupList.get(i);
					String aconyxUsername=group.getAconyxUsername();
					String name=group.getName();
					List<String> memberList=group.getMemberList();
					List<NonAconyxMember> nonAconyxMemberList=group.getNonAconyxMemberList();
					String key=aconyxUsername+KEY_SEPERATOR+name;
						try {
							long id=CABDBMaps.getInstance().getAddressGroupMap().get(key);
							connection.setAutoCommit(false);
							if(memberList!=null)
								for(String member:memberList){
								String insertQuery="INSERT INTO cab_address_book_group_members(ID,MEMBER) VALUES ('"+id+"','"+member+ "')";
								statement.execute(insertQuery);
								}
							if(nonAconyxMemberList!=null){
								pStatement=connection.prepareStatement("INSERT INTO cab_non_aconyx_members(ID,NAME,CONTACT,SIP_ADDRESS) VALUES (?,?,?,?)");
								for(NonAconyxMember nonMember:nonAconyxMemberList){
									String mname=nonMember.getName();
									String mContact=nonMember.getContact();
									String mSipAddress=nonMember.getSIPAddress();
									pStatement.setString(1, id+"");
									pStatement.setString(2, mname);
									pStatement.setString(3, mContact);
									pStatement.setString(4, mSipAddress);
									pStatement.executeUpdate();
								}
							}
							connection.commit();
							group.setStatus(Constants.STATUS_SUCCESS);
						} catch (Exception e) {
							logger.error(e.toString(),e);
							group.setStatus(Constants.STATUS_FAILED);
						}						
					finalgroupList.add(group);
				}
			} catch (SQLException e) {
				logger.error("[CAB] CABDaoImpl: addToAddressBookGroups() SQL Exception occured ",e);
				throw e;
			} catch (Exception e) {
				logger.error("[CAB] CABDaoImpl: addToAddressBookGroups() Exception occured ",e);
				throw e;
			} finally {
				if(pStatement!=null)
					pStatement.close();
				if(statement!=null)
					statement.close();
				if(connection!=null)
					connection.close(); 
			}
		}else if(logger.isDebugEnabled()){
		     logger.debug("[CAB] CABDaoImpl: empty list exiting....");
		  }
		if(logger.isDebugEnabled())
			logger.debug("[CAB] CABDaoImpl: addToAddressBookGroups() exiting....");
		return finalgroupList;
	}

	@Override
	public List<AddressBookGroup> removeFromAddressBookGroups(
			List<AddressBookGroup> groupList) throws SQLException, Exception {
		if(logger.isDebugEnabled())
			logger.debug("[CAB] CABDaoImpl: removeFromAddressBookGroups() entered");
		List<AddressBookGroup> finalgroupList=new LinkedList<AddressBookGroup>();
		if(groupList!=null && groupList.size()!=0){
			Connection connection = null;
			Statement statement = null;
			try {
				connection = regDataSource.getConnection();
				if (connection == null) {
					logger.error("[CAB] CABDaoImpl: removeFromAddressBookGroups() No Connection");
					throw new Exception("No DB Connection");
				}
				statement = connection.createStatement();
				int size=groupList.size();
				for (int i = 0; i < size;i++){
					AddressBookGroup group=groupList.get(i);
					String aconyxUsername=group.getAconyxUsername();
					String name=group.getName();
					List<String> memberList=group.getMemberList();
					List<NonAconyxMember> nonAconyxMemberList=group.getNonAconyxMemberList();
					String key=aconyxUsername+KEY_SEPERATOR+name;
						try {
							long id=CABDBMaps.getInstance().getAddressGroupMap().get(key);
							connection.setAutoCommit(false);
							if(memberList!=null)
								for(String member:memberList){
								String deleteQuery="DELETE FROM cab_address_book_group_members WHERE ID='"+id+"'AND MEMBER='"+member+"'";								
								statement.execute(deleteQuery);
								}
							if(nonAconyxMemberList!=null){
								for(NonAconyxMember nMember:nonAconyxMemberList){
									String mName=nMember.getName().replace("'", "''");
									String deleteQuery="DELETE FROM cab_non_aconyx_members WHERE ID='"+id+"'AND NAME='"+mName+"'";								
									statement.execute(deleteQuery);
								}
							}
							connection.commit();
							group.setStatus(Constants.STATUS_SUCCESS);
						} catch (Exception e) {
							logger.error(e.toString(),e);
							group.setStatus(Constants.STATUS_FAILED);
						}						
					finalgroupList.add(group);
				}
			} catch (SQLException e) {
				logger.error("[CAB] CABDaoImpl: removeFromAddressBookGroups() SQL Exception occured ",e);
				throw e;
			} catch (Exception e) {
				logger.error("[CAB] CABDaoImpl: removeFromAddressBookGroups() Exception occured ",e);
				throw e;
			} finally {
				if(statement!=null)
					statement.close();
				if(connection!=null)
					connection.close(); 
			}
		}else if(logger.isDebugEnabled()){
		     logger.debug("[CAB] CABDaoImpl: empty list exiting....");
		  }
		if(logger.isDebugEnabled())
			logger.debug("[CAB] CABDaoImpl: removeFromAddressBookGroups() exiting....");
		return finalgroupList;
	}

	@Override
	public List<AddressBookGroup> associateContactViews(
			List<AddressBookGroup> groupList) throws SQLException, Exception {
		if(logger.isDebugEnabled())
			logger.debug("[CAB] CABDaoImpl: associateContactViews() entered");
		List<AddressBookGroup> finalgroupList=new LinkedList<AddressBookGroup>();
		if(groupList!=null && groupList.size()!=0){
			Connection connection = null;
			Statement statement = null;
			try {
				connection = regDataSource.getConnection();
				if (connection == null) {
					logger.error("[CAB] CABDaoImpl: associateContactViews() No Connection");
					throw new Exception("No DB Connection");
				}
				statement = connection.createStatement();
				int size=groupList.size();
				for (int i = 0; i < size;i++){
					AddressBookGroup group=groupList.get(i);
					String aconyxUsername=group.getAconyxUsername();
					String name=group.getName();
					String contactViewName=group.getContactViewName();
					try {
							String contactViewKey=aconyxUsername+KEY_SEPERATOR+contactViewName;
							long contactViewId=CABDBMaps.getInstance().getConactViewMap().get(contactViewKey);
							String updateQuery = "UPDATE cab_address_book_groups SET CONTACT_VIEW_ID='"+contactViewId+"' WHERE ACONYX_USERNAME='"+aconyxUsername+"'AND NAME='"+name+"'";
							int row=statement.executeUpdate(updateQuery);
							if(row>0)
								group.setStatus(Constants.STATUS_SUCCESS);
							else
								group.setStatus(Constants.STATUS_FAILED);							
						} catch (Exception e) {
							logger.error(e.toString(),e);
							group.setStatus(Constants.STATUS_FAILED);
						}						
					finalgroupList.add(group);
				}
			} catch (SQLException e) {
				logger.error("[CAB] CABDaoImpl: associateContactViews() SQL Exception occured ",e);
				throw e;
			} catch (Exception e) {
				logger.error("[CAB] CABDaoImpl: associateContactViews() Exception occured ",e);
				throw e;
			} finally {
				if(statement!=null)
					statement.close();
				if(connection!=null)
					connection.close(); 
			}
		}else if(logger.isDebugEnabled()){
		     logger.debug("[CAB] CABDaoImpl: empty list exiting....");
		  }
		if(logger.isDebugEnabled())
			logger.debug("[CAB] CABDaoImpl: associateContactViews() exiting....");
		return finalgroupList;
	}

	
	@Override
	public List<AddressBookGroup> getAddressBookGroups(
			List<AddressBookGroup> groupList) throws SQLException, Exception {
		if(logger.isDebugEnabled())
			logger.debug("[CAB] CABDaoImpl: getAddressBookGroups() entered");
		List<AddressBookGroup> finalAddressBookGroupList=new LinkedList<AddressBookGroup>();
		if(groupList!=null && groupList.size()!=0){
			Connection connection = null;
			Statement statement = null;
			ResultSet resultSet = null;
			try {
				connection = regDataSource.getConnection();
				if (connection == null) {
					logger.error("[CAB] CABDaoImpl: getAddressBookGroups() No Connection");
					throw new Exception("No DB Connection");
				}
				statement = connection.createStatement();
				int size=groupList.size();
				for (int i = 0; i < size;i++){
					AddressBookGroup group=groupList.get(i);
					String aconyxUsername=group.getAconyxUsername();
					String name=group.getName();
					List<PersonalContactCard> pccList=new LinkedList<PersonalContactCard>();
					List<NonAconyxMember> nonAconyxMemberList=new LinkedList<NonAconyxMember>();
					try{						
						String key=aconyxUsername+KEY_SEPERATOR+name;						
						long id=CABDBMaps.getInstance().getAddressGroupMap().get(key);
						String contactViewQuery="SELECT NAME FROM cab_contact_views WHERE ID IN (SELECT CONTACT_VIEW_ID	FROM cab_address_book_groups WHERE ID='"+id+"')";						
						String memberQuery="SElECT ACONYX_USERNAME,FIRSTNAME,LASTNAME FROM cab_personal_contact_cards WHERE ACONYX_USERNAME IN (SELECT MEMBER FROM cab_address_book_group_members WHERE ID='"+id+"')";
						String nonAconyxMemberQuery="SElECT NAME,CONTACT,SIP_ADDRESS FROM cab_non_aconyx_members WHERE ID='"+id+"'";
						resultSet=statement.executeQuery(contactViewQuery);
						resultSet.next();
						String contactViewName=resultSet.getString("NAME");
						group.setContactViewName(contactViewName);
						resultSet=statement.executeQuery(memberQuery);
						while(resultSet.next()){
							String member=resultSet.getString("ACONYX_USERNAME");
							String firstName=resultSet.getString("FIRSTNAME");
							String lastName=resultSet.getString("LASTNAME");
							PersonalContactCard pcc=new PersonalContactCard();
							pcc.setAconyxUsername(member);
							pcc.setFirstName(firstName);
							pcc.setLastName(lastName); 
							pccList.add(pcc);	
						}										
						if(pccList.size()!=0)
							group.setPCCList(pccList);
						
						resultSet=statement.executeQuery(nonAconyxMemberQuery);
						while(resultSet.next()){
							String mName=resultSet.getString("NAME");
							String mContact=resultSet.getString("CONTACT");
							String mSipAddress=resultSet.getString("SIP_ADDRESS");
							NonAconyxMember nonAconyxMember=new NonAconyxMember(mName,mContact,mSipAddress);
							nonAconyxMemberList.add(nonAconyxMember);	
						}
						
						if(nonAconyxMemberList.size()!=0)
							group.setNonAconyxMemberList(nonAconyxMemberList);
						
							group.setStatus(Constants.STATUS_SUCCESS);							
							finalAddressBookGroupList.add(group);
					}catch (Exception e) {
							logger.error(e.toString(),e);
							group.setStatus(Constants.STATUS_FAILED);
							finalAddressBookGroupList.add(group);
					}
				}
			} catch (SQLException e) {
				logger.error("[CAB] CABDaoImpl: getAddressBookGroups() SQL Exception occured ",e);
				throw e;
			} catch (Exception e) {
				logger.error("[CAB] CABDaoImpl: getAddressBookGroups() Exception occured ",e);
				throw e;
			} finally {
				if(resultSet!=null)
					resultSet.close();
				if(statement!=null)
					statement.close();
				if(connection!=null)
					connection.close(); 
			}
		} else if(logger.isDebugEnabled()){
				     logger.debug("[CAB] CABDaoImpl: empty list exiting....");
		  }
		if(logger.isDebugEnabled())
			logger.debug("[CAB] CABDaoImpl: getAddressBookGroups() exiting....");
		return finalAddressBookGroupList;
	}
	
	@Override	
	public List<String> getAllAddressBookGroups(List<String> aconyxUsernameList,List<AddressBookGroup> finalAddressBookGroupList) throws SQLException, Exception {
		if(logger.isDebugEnabled())
			logger.debug("[CAB] CABDaoImpl: getAllAddressBookGroups() entered");
		 List<String> emptyResultList=new LinkedList<String>();
		if(aconyxUsernameList!=null && aconyxUsernameList.size()!=0){
			Connection connection = null;
			Statement statement = null;
			Statement innerStatement = null;
			ResultSet resultSet = null;
			ResultSet innerResultSet = null;
			try {
				connection = regDataSource.getConnection();
				if (connection == null) {
					logger.error("[CAB] CABDaoImpl: getAllAddressBookGroups() No Connection");
					throw new Exception("No DB Connection");
				}
				statement = connection.createStatement();
				int size=aconyxUsernameList.size();
				for (int i = 0; i < size;i++){
					String aconyxUsername=aconyxUsernameList.get(i);
					try{
						String query="SELECT cab_address_book_groups.NAME AS NAME,cab_address_book_groups.ID,cab_contact_views.NAME AS CONTACT_VIEW_NAME FROM cab_address_book_groups,cab_contact_views WHERE cab_address_book_groups.ACONYX_USERNAME='"+aconyxUsername+"' AND cab_address_book_groups.CONTACT_VIEW_ID=cab_contact_views.ID";
						//String query1="SELECT D.NAME,D.ID,P.CONTACT_VIEW_NAME FROM (SELECT ID,NAME,CONTACT_VIEW_ID FROM cab_address_book_groups  WHERE cab_address_book_groups.ACONYX_USERNAME='akohli@agnity.com')D LEFT JOIN (SELECT NAME AS CONTACT_VIEW_NAME,ID FROM cab_contact_views where ACONYX_USERNAME='akohli@agnity.com') P on D.CONTACT_VIEW_ID= P.id ";
						resultSet=statement.executeQuery(query);
						boolean isExists=false;
						while(resultSet.next()){
							AddressBookGroup group=new AddressBookGroup();
							List <PersonalContactCard> pccList=new LinkedList<PersonalContactCard>();
							List<NonAconyxMember> nonAconyxMemberList=new LinkedList<NonAconyxMember>();
							isExists=true;
							group.setName(resultSet.getString("NAME"));
							group.setContactViewName(resultSet.getString("CONTACT_VIEW_NAME"));
							group.setAconyxUsername(aconyxUsername);
							long id=resultSet.getLong("ID");
							String memberQuery="SELECT ACONYX_USERNAME,FIRSTNAME,LASTNAME FROM cab_personal_contact_cards WHERE ACONYX_USERNAME IN (SELECT MEMBER FROM cab_address_book_group_members WHERE ID='"+id+"')";
							String nonAconyxMemberQuery="SELECT NAME,CONTACT,SIP_ADDRESS FROM cab_non_aconyx_members WHERE ID='"+id+"'";
							if(innerStatement==null)
								innerStatement=connection.createStatement();
							innerResultSet=innerStatement.executeQuery(memberQuery);
							while(innerResultSet.next()){
								String member=innerResultSet.getString("ACONYX_USERNAME");
								String firstName=innerResultSet.getString("FIRSTNAME");
								String lastName=innerResultSet.getString("LASTNAME");
								PersonalContactCard pcc=new PersonalContactCard();
								pcc.setAconyxUsername(member);
								pcc.setFirstName(firstName);
								pcc.setLastName(lastName); 
								pccList.add(pcc);	
							}										
							if(pccList.size()!=0)
								group.setPCCList(pccList);
							
							innerResultSet=innerStatement.executeQuery(nonAconyxMemberQuery);
							while(innerResultSet.next()){
								String mName=innerResultSet.getString("NAME");
								String mContact=innerResultSet.getString("CONTACT");
								String mSipAddress=innerResultSet.getString("SIP_ADDRESS");
								NonAconyxMember nonAconyxMember=new NonAconyxMember(mName,mContact,mSipAddress);
								nonAconyxMemberList.add(nonAconyxMember);	
							}
							
							if(nonAconyxMemberList.size()!=0)
								group.setNonAconyxMemberList(nonAconyxMemberList);
							finalAddressBookGroupList.add(group);
						}	
						if(!isExists)//Add aconyxUsername in empty resultList if no address book group exists for it
							emptyResultList.add(aconyxUsername);
					}catch (Exception e) {
							logger.error(e.toString(),e);
					}
				}
			} catch (SQLException e) {
				logger.error("[CAB] CABDaoImpl: getAllAddressBookGroups() SQL Exception occured ",e);
				throw e;
			} catch (Exception e) {
				logger.error("[CAB] CABDaoImpl: getAllAddressBookGroups() Exception occured ",e);
				throw e;
			} finally {
				if(innerResultSet!=null)
					innerResultSet.close();
				if(innerStatement!=null)
					innerStatement.close();
				if(resultSet!=null)
					resultSet.close();
				if(statement!=null)
					statement.close();
				if(connection!=null)
					connection.close(); 
			}
		} else{
			  if(logger.isDebugEnabled())
				     logger.debug("[CAB] CABDaoImpl: empty list exiting....");
		  }
		
		if(logger.isDebugEnabled())
			logger.debug("[CAB] CABDaoImpl: getAllAddressBookGroups() exiting");
		return emptyResultList;
	}

	@Override
	public List<PersonalContactCard> getMemberDetails(String aconyxUsername,
			List<String> memberList) throws SQLException, Exception {
		if(logger.isDebugEnabled())
			logger.debug("[CAB] CABDaoImpl: getMemberDetails() entered");
		List<PersonalContactCard> finalPccList=new LinkedList<PersonalContactCard>();
		if(memberList!=null && memberList.size()!=0){
			Connection connection = null;
			Statement statement = null;
			ResultSet res=null;
			try {
				connection = regDataSource.getConnection();
				if (connection == null) {
					logger.error("[CAB] CABDaoImpl: getMemberDetails() No Connection");
					throw new Exception("No DB Connection");
				}
				statement = connection.createStatement();
				int size=memberList.size();
				for (int i = 0; i < size;i++){
					PersonalContactCard pcc=new PersonalContactCard();
					String memberName=memberList.get(i);
					pcc.setAconyxUsername(memberName);
					//Select contact view Id of member for Aconyx User 
					String contactViewQuery="(SELECT CONTACT_VIEW_ID FROM cab_address_book_groups WHERE cab_address_book_groups.ACONYX_USERNAME='"+memberName+"' AND ID IN " +
							"(SELECT ID FROM cab_address_book_group_members WHERE MEMBER='"+aconyxUsername+"')) UNION (SELECT ID FROM cab_contact_views WHERE cab_contact_views.ACONYX_USERNAME='"+memberName+"' AND cab_contact_views.NAME='"+Constants.DEFAULT_CONTACT_VIEW_NAME+"')";
					res=statement.executeQuery(contactViewQuery);
					
					StringBuffer buffer=new StringBuffer();
					while (res.next()) {
						String contactViewId=res.getString(1);
						if(contactViewId!=null && !(contactViewId.equals("null")))
							buffer.append(contactViewId+",");	
					}
					String contactViewIds=buffer.toString();//Contact View Id comma separated
					if(contactViewIds.isEmpty()){
						finalPccList.add(pcc);
						continue;
					}
					contactViewIds=contactViewIds.substring(0, contactViewIds.length()-1);//remove last "," in string
					
					String fieldQuery="SELECT DISTINCT FIELD_NAME FROM cab_pcc_fields_map WHERE FIELD_ID IN" +
					"(SELECT FIELD_ID from cab_contact_view_fields_map where ID IN " +
					"("+contactViewIds+"))";
					
					res=statement.executeQuery(fieldQuery);
					List <String> fields=new LinkedList<String>();
					
					StringBuffer query=new StringBuffer();
					query.append("Select ");
					while(res.next()){
						String fieldName=res.getString("FIELD_NAME");
						fields.add(fieldName);
					}
					for(String fieldName:fields){
						query.append(fieldName+",");
					}
					
					int index=query.lastIndexOf(",");			
					String str_query=query.substring(0,index);
					str_query=str_query.concat(" FROM cab_personal_contact_cards WHERE ACONYX_USERNAME='"+memberName+"'");
				   res=statement.executeQuery(str_query);
					while(res.next()){	
						for(String field:fields){
							pcc.setPCCField(field,res.getString(field));
						}
					}
	
					finalPccList.add(pcc);
				}
			} catch (SQLException e) {
				logger.error("[CAB] CABDaoImpl: getMemberDetails() SQL Exception occured ",e);
				throw e;
			} catch (Exception e) {
				logger.error("[CAB] CABDaoImpl: getMemberDetails() Exception occured ",e);
				throw e;
			} finally {
				if(res!=null)
					res.close();
				if(statement!=null)
					statement.close();
				if(connection!=null)
					connection.close(); 
			}
		}
		 else{
			  if(logger.isDebugEnabled())
				     logger.debug("[CAB] CABDaoImpl: empty list exiting....");
		  }
		if(logger.isDebugEnabled())
			logger.debug("[CAB] CABDaoImpl: getMemberDetails() exiting....");
		return finalPccList;
	}
	
	private synchronized long getNextContactViewID() throws SQLException, Exception{
		return ++contactViewID_Counter;
	}
	
	private synchronized long getNextAddressBookGroupID() throws SQLException, Exception{
		return ++addressBookGroupID_Counter;
	}

	@Override
	public List<PersonalContactCard> searchUsers(String aconyxUsername,
			String searchBy, String searchValue) throws SQLException, Exception {
		if(logger.isDebugEnabled())
			logger.debug("[CAB] CABDaoImpl: searchUsers() entered");
		List<PersonalContactCard> finalPccList=new LinkedList<PersonalContactCard>();
		if(aconyxUsername!=null && searchBy!=null && searchValue!=null ){
			Connection connection = null;
			Statement statement = null;
			Statement innerStatement = null;
			ResultSet resultSet=null;
			ResultSet innerResultSet=null;
			try {
				connection = regDataSource.getConnection();
				if (connection == null) {
					logger.error("[CAB] CABDaoImpl: searchUsers() No Connection");
					throw new Exception("No DB Connection");
				}
				statement = connection.createStatement();
				innerStatement=connection.createStatement();
				String searchQuery="SELECT ACONYX_USERNAME,FIRSTNAME,LASTNAME,"+searchBy+" FROM cab_personal_contact_cards WHERE UPPER("+searchBy+") LIKE '%"+searchValue.toUpperCase()+"%'";
				resultSet=statement.executeQuery(searchQuery);
				while(resultSet.next()){
					String user=resultSet.getString("ACONYX_USERNAME");
					String firstName=resultSet.getString("FIRSTNAME");
					String lastName=resultSet.getString("LASTNAME");
					String val=resultSet.getString(searchBy);
					boolean present=false;
					if(user.equals(aconyxUsername)){
						present=true;
					}else{

						String contactViewQuery="(SELECT CONTACT_VIEW_ID FROM cab_address_book_group_members WHERE cab_address_book_groups.ACONYX_USERNAME='"+user+"' AND ID IN " +
						"(SELECT ID FROM cab_address_book_group_members WHERE MEMBER='"+aconyxUsername+"')) UNION (SELECT ID FROM cab_contact_views WHERE cab_contact_views.ACONYX_USERNAME='"+user+"' AND cab_contact_views.NAME='"+Constants.DEFAULT_CONTACT_VIEW_NAME+"')";
						
						innerResultSet=innerStatement.executeQuery(contactViewQuery);
						StringBuffer buffer=new StringBuffer();
						while (innerResultSet.next()) {
							String contactViewId=innerResultSet.getString(1);
							if(contactViewId!=null && !(contactViewId.equals("null")))
								buffer.append(contactViewId+",");	
						}
						String contactViewIds=buffer.toString();//Contact View Id comma separated
						if(contactViewIds.isEmpty()){
							present=false;
							continue;
						}
						contactViewIds=contactViewIds.substring(0, contactViewIds.length()-1);//remove last "," in string
						String fieldselectionQuery="SELECT DISTINCT FIELD_NAME FROM cab_pcc_fields_map WHERE FIELD_ID IN" +
						"(SELECT FIELD_ID from cab_contact_view_fields_map where ID IN "+"("+contactViewIds+"))";
						
						innerResultSet=innerStatement.executeQuery(fieldselectionQuery);
						while(innerResultSet.next()){
							String fieldName=innerResultSet.getString("FIELD_NAME");
							if(fieldName.equals(searchBy)){
								present=true;
								break;
							}
						}
					}
					if(present){
						PersonalContactCard pcc=new PersonalContactCard();
						pcc.setAconyxUsername(user);
						pcc.setFirstName(firstName);
						pcc.setLastName(lastName);
						pcc.setPCCField(searchBy, val);
						finalPccList.add(pcc);
					}
				}
			} catch (SQLException e) {
				logger.error("[CAB] CABDaoImpl: searchUsers() SQL Exception occured ",e);
				throw e;
			} catch (Exception e) {
				logger.error("[CAB] CABDaoImpl: searchUsers() Exception occured ",e);
				throw e;
			} finally {
				if(innerResultSet!=null)
					innerResultSet.close();
				if(innerStatement!=null)
					innerStatement.close();
				if(resultSet!=null)
					resultSet.close();
				if(statement!=null)
					statement.close();
				if(connection!=null)
					connection.close(); 
			}
		} else{
			  if(logger.isDebugEnabled())
				     logger.debug("[CAB] CABDaoImpl: due to null values exiting....");
		  }
		if(logger.isDebugEnabled())
			logger.debug("[CAB] CABDaoImpl: searchUsers() exiting....");
		return finalPccList;
	}

	@Override
	public List<ModifyAddressBookGroup> modifyAddressBookGroupMembers(
			List<ModifyAddressBookGroup> groupList) throws SQLException,
			Exception {
		if(logger.isDebugEnabled())
		     logger.debug("[CAB] CABDaoImpl: modifyAddressBookGroupMembers() entered");
		     List <ModifyAddressBookGroup>finalgroupList = new LinkedList<ModifyAddressBookGroup>();
		  if ((groupList != null) && (groupList.size() != 0)) {
		      Connection connection = null;
	       PreparedStatement pStatement = null;
	       try {
		         connection = regDataSource.getConnection();
		         if (connection == null) {
		          logger.error("[CAB] CABDaoImpl: modifyAddressBookGroupMembers() No Connection");
		           throw new Exception("No DB Connection");
		         }
		     int size = groupList.size();
		        pStatement = connection.prepareStatement("UPDATE cab_non_aconyx_members SET CONTACT=?,SIP_ADDRESS=? WHERE ID=? AND NAME=?");
		        for (int i = 0; i < size; i++) {
		           ModifyAddressBookGroup group = (ModifyAddressBookGroup)groupList.get(i);
		           String aconyxUsername = group.getAconyxUsername();
		          String name = group.getName();
		          List<NonAconyxMember> nonAconyxMemberList = group.getNonAconyxMemberList();
		         String key = aconyxUsername + KEY_SEPERATOR + name;
		           try {
		             long id = ((Long)CABDBMaps.getInstance().getAddressGroupMap().get(key)).longValue();
		              connection.setAutoCommit(false);
		              if (nonAconyxMemberList != null) {
		                for (NonAconyxMember nonMember : nonAconyxMemberList) {
		                 String mname = nonMember.getName();
		                 String mContact = nonMember.getContact();
		                 String mSipAddress = nonMember.getSIPAddress();
		                  pStatement.setString(3, id+"");
		                  pStatement.setString(4, mname);
		                  pStatement.setString(1, mContact);
		                  pStatement.setString(2, mSipAddress);
		                int count = pStatement.executeUpdate();
		                if (count == 1)
		             nonMember.setStatus(Constants.STATUS_SUCCESS);
		                 else
		                    nonMember.setStatus(Constants.STATUS_FAILED);
		                }
		              }
		            connection.commit();
		             group.setStatus(Constants.STATUS_SUCCESS);
		            } catch (Exception e) {
		              logger.error(e.toString(), e);
		              group.setStatus(Constants.STATUS_FAILED);
		            }
		           finalgroupList.add(group);
		         }
		        } catch (SQLException e) {
		         logger.error("[CAB] CABDaoImpl: modifyAddressBookGroupMembers() SQL Exception occured ", e);
		          throw e;
		       } catch (Exception e) {
		          logger.error("[CAB] CABDaoImpl: modifyAddressBookGroupMembers() Exception occured ", e);
		         throw e;
		       } finally {
		         if (pStatement != null)
		           pStatement.close();
		         if (connection != null)
		           connection.close();
		        }
		     }
		  else{
			  if(logger.isDebugEnabled())
				     logger.debug("[CAB] CABDaoImpl: empty list exiting....");
		  }
			if(logger.isDebugEnabled())
			     logger.debug("[CAB] CABDaoImpl: modifyAddressBookGroupMembers() exiting....");
		     return finalgroupList;
	}
}
