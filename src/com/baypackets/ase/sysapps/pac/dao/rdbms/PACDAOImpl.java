/*

 * PACDAOImpl.java
 * @author Amit Baxi
 */
package com.baypackets.ase.sysapps.pac.dao.rdbms;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;

import org.apache.log4j.Logger;

import com.mysql.jdbc.exceptions.jdbc4.CommunicationsException;
import com.baypackets.ase.sysapps.pac.adaptors.PACAdaptor;
import com.baypackets.ase.sysapps.pac.adaptors.PACAdaptorFactory;
import com.baypackets.ase.sysapps.pac.adaptors.SIPPACAdaptor;
import com.baypackets.ase.sysapps.pac.cache.PACMemoryMap;
import com.baypackets.ase.sysapps.pac.cache.UserChannelDataRow;
import com.baypackets.ase.sysapps.pac.dao.PACDAO;
import com.baypackets.ase.sysapps.pac.dataobjects.ChannelDO;
import com.baypackets.ase.sysapps.pac.dataobjects.ChannelMapDO;
import com.baypackets.ase.sysapps.pac.dataobjects.PresenceDO;
import com.baypackets.ase.sysapps.pac.dataobjects.UserDO;
import com.baypackets.ase.sysapps.pac.jaxb.Channel;
import com.baypackets.ase.sysapps.pac.jaxb.Errors;
import com.baypackets.ase.sysapps.pac.manager.PACManager;
import com.baypackets.ase.sysapps.pac.util.Configuration;
import com.baypackets.ase.sysapps.pac.util.Constants;
import com.baypackets.ase.sysapps.pac.util.ErrorCodes;
import com.baypackets.ase.sysapps.pac.util.PACSubscriptionWork;
import com.baypackets.ase.util.AseStrings;
 	
public class PACDAOImpl implements PACDAO {

	private static Logger logger=Logger.getLogger(PACDAOImpl.class);
	static InitialContext ctx;
	static DataSource  currentDataSource;
	static DataSource  primaryDataSource;
	static DataSource  secondaryDataSource;
	static int NUM_DATASOURCE=1;
	private static boolean subscribeForOnlyActiveUsers = true;
	static {
		
		String PROVIDER_URL = "file:" + System.getProperty(Constants.ASE_HOME)+ Constants.PATH_JNDI_FILESERVER;
		String CONTEXT_FACTORY = Constants.CONTEXT_FACTORY;

		if (logger.isDebugEnabled()) {
			logger.debug("[PAC] PACDaoImpl Getting Data source");
		}

		try {
			Hashtable<String, String> env = new Hashtable<String, String>();
			env.put(Context.INITIAL_CONTEXT_FACTORY, CONTEXT_FACTORY);
			env.put(Context.PROVIDER_URL, PROVIDER_URL);
			ctx = new InitialContext(env);
			
			String primaryDatasourceName=(String)Configuration.getInstance().getParamValue(Constants.PROP_PAC_DATASOURCE_NAME);
			primaryDatasourceName=(primaryDatasourceName!=null && ! primaryDatasourceName.trim().isEmpty() )?primaryDatasourceName:Constants.PATH_DATASOURCE_PRIMARY;
			
			String secondaryDatasourceName=(String)Configuration.getInstance().getParamValue(Constants.PROP_PAC_SECONDARY_DATASOURCE_NAME);
			secondaryDatasourceName=(secondaryDatasourceName!=null && ! secondaryDatasourceName.trim().isEmpty())?secondaryDatasourceName:Constants.PATH_DATASOURCE_SECONDARY;
			
			currentDataSource=(DataSource)ctx.lookup(primaryDatasourceName);
			primaryDataSource=currentDataSource;
			try{
				secondaryDataSource=(DataSource)ctx.lookup(secondaryDatasourceName);
				if(secondaryDataSource!=null){
					NUM_DATASOURCE=2;
				}
			}catch (Exception e) {
				logger.error("[PAC] PACDaoImpl Secondary datasource not specified in datasource.xml");
			}
			
			String subscribeForActiveUsersFlag=(String)Configuration.getInstance().getParamValue(Constants.PROP_PAC_SUBSCRIBE_FOR_ONLY_ACTIVE_USERS);
			if(subscribeForActiveUsersFlag!=null &&  subscribeForActiveUsersFlag.trim().length()!=0){
				subscribeForOnlyActiveUsers =AseStrings.FALSE_SMALL.equalsIgnoreCase(subscribeForActiveUsersFlag.trim())?false:true;
			}
			logger.debug("subscribeForOnlyActiveUsers Flag :: " + subscribeForOnlyActiveUsers);
		} 
		catch (Exception e) {
			logger.error("[PAC] PACDaoImpl Exception in datasource lookup", e);
		}
	}

	@Override
	public void setChannelIdMap() throws SQLException, Exception {
		if(logger.isDebugEnabled())
			logger.debug("[PAC] PACDAOImpl: getChannelIdMap() entered");
		
		Connection connection =null;
		Statement statement=null;
		ResultSet result=null;
		for(int num=0;num<NUM_DATASOURCE;num++){
			HashMap<String,Integer> channelIdMap=new HashMap<String, Integer>();
			HashMap<Integer,ChannelMapDO> idChannelMap=new HashMap<Integer,ChannelMapDO>();
			DataSource localDataSource=currentDataSource;
			try{
				connection=localDataSource.getConnection();
				if(connection==null)
				{
					logger.error("[PAC] PACDAOImpl:getChannelIdMap No Connection");
					return ;
				}		
				statement=connection.createStatement();
				String query="select * from channel_detail";
				result=statement.executeQuery(query);
				while(result.next())
				{
					String channelName=result.getString("channel_name");
					int channelId=result.getInt("channel_id");
					String priority=result.getString("priority");
					int mode=result.getInt("mode");
					ChannelMapDO channelMapDO=new ChannelMapDO(channelName,mode, priority); 
					if(logger.isDebugEnabled())
						logger.debug("Filling Channel Name-Id MAP: ChannelName:"+channelName+" ChannelId:"+channelId);
					channelIdMap.put(channelName, channelId);
					idChannelMap.put(channelId, channelMapDO);
				}
				Configuration.getInstance().setParamValue(Constants.PROP_CHANNEL_ID_MAP,channelIdMap );
				Configuration.getInstance().setParamValue(Constants.PROP_ID_CHANNEL_MAP,idChannelMap );
				result.close();            
				statement.close();
				connection.close();
				return;
			}catch (SQLException e) {
				String msg=e.getMessage()!=null?e.getMessage():"";
				if((Constants.MSG_CONNECTION_EXCEPTION.equals(msg) || e instanceof CommunicationsException) && (num<NUM_DATASOURCE-1))
				{
					if(logger.isDebugEnabled())
						logger.debug("Connections of primary datasource failed.Trying with secondary datasource");
					this.flipDataSource(localDataSource);
					continue;	
				}
				logger.error("[PAC] PACDAOImpl:getChannelIdMap SQL Exception occured ",e);
				throw e;
			}
			catch(Exception e)
			{
				logger.error("[PAC] PACDAOImpl:getChannelIdMap Exception occured ",e);
				throw e;
			}
			finally{
				if(result!=null)
					result.close();
				if(statement!=null)
					statement.close();
				if(connection!=null)
					connection.close();            

			}
		}	
		if(logger.isDebugEnabled())
			logger.debug("[PAC] PACDAOImpl: getChannelIdMap() exiting....");
	}
	
	@Override
	public List<Channel> assignUserChannels(List<ChannelDO> channelDoList,Errors errors)
			throws SQLException, Exception {
		if (logger.isDebugEnabled()) {
			logger.debug("[PAC] PACDAOImpl: assignUserChannels() entered");
		}
		List<Channel> finalchannelsList = new LinkedList<Channel>();
		if (channelDoList != null && channelDoList.size() != 0) {
			Connection connection = null;
			Statement insertStmt = null;	
	outer_loop:
			for(int num=0;num<NUM_DATASOURCE;num++){			
				
				DataSource localDataSource=currentDataSource;
				try{
					connection=localDataSource.getConnection();
					if (connection == null) {
						logger.error("[PAC] PACDAOImpl:assignUserChannels No Connection");
						throw new Exception("No DB Connection");
					}
					insertStmt = connection.createStatement();
					Iterator<ChannelDO> iterator = channelDoList.iterator();
					while(iterator.hasNext()){
						ChannelDO channelDo = iterator.next();
						String appId = channelDo.getApplicationId();
						String aconyxUserName = channelDo.getAconyxUserName();
						String channelUsername = channelDo.getChannelUsername();
						String password = channelDo.getPassword();
						String encrypted = channelDo.getEncrypted();
						String channelName = channelDo.getChannelName();
						int ChannelId = Configuration.getInstance().getChannelId(channelName);
						String channelURL = channelDo.getChannelURL();
						long lastUpdated = System.currentTimeMillis();
						Channel channel = new Channel();
						channel.setChannelName(channelName);
						channel.setChannelUsername(channelUsername);
						// For SIP presence
						channel.setChannelURL(channelURL);
						channel.setPassowrd(password);
						channel.setEncrypted(encrypted);
						// For SIP presence
						String insertQuery = "insert into user_presence_data(application_id, aconyx_username, channel_username, password, isencrypted, channel_id, channel_url, presence_status, operational_status, last_updated) values('"
							+ appId
							+ "', '"
							+ aconyxUserName.replaceAll("'", "''")
							+ "', '"
							+ channelUsername.replaceAll("'", "''")
							+ "', '"
							+ password.replaceAll("'", "''")
							+ "', '"
							+ encrypted
							+ "', '"
							+ ChannelId
							+ "', '"
							+ channelURL
							+ "', '"
							+ Constants.PRESENCE_STATUS_UNKNOWN
							+ "', '"
							+ Constants.OPERATIONAL_STATUS_ACTIVE
							+ "', '"
							+ lastUpdated + "')";
						try {
							insertStmt.execute(insertQuery);
							channel.setStatus(Constants.STATUS_SUCCESS);
							PACMemoryMap pacMemoryMap=PACMemoryMap.getInstance();
							pacMemoryMap.insertUpdateChannelUserData(appId,
									aconyxUserName, ChannelId,channelUsername,
									new UserChannelDataRow(channelUsername,
											ChannelId, password, encrypted,channelURL, "Unknown", null,
											Constants.OPERATIONAL_STATUS_ACTIVE,
											lastUpdated));

						} catch(SQLException e) {
							String msg=e.getMessage();
							if(((msg!=null && msg.contains(Constants.MSG_READ_ONLY_EXCEPTION))|| e instanceof CommunicationsException )&& (num<NUM_DATASOURCE-1))
							{
								if(logger.isDebugEnabled())
									logger.debug("Connections of primary datasource failed.Trying with secondary datasource");
								this.flipDataSource(localDataSource);
								insertStmt.close();
								connection.close();								
								continue outer_loop;
							}else{
								logger.error(e.toString(),e);
								errors.addError(ErrorCodes.ERROR_025, ErrorCodes.ERROR_025_DESC+e.getMessage());
								channel.setStatus(Constants.STATUS_FAILED);
							}
							
						}catch (Exception e) {
							logger.error(e.toString(),e);
							errors.addError(ErrorCodes.ERROR_025, ErrorCodes.ERROR_025_DESC+e.getMessage());
							channel.setStatus(Constants.STATUS_FAILED);
						}
						finalchannelsList.add(channel);
						iterator.remove();
					}
					insertStmt.close();
					connection.close();
					return finalchannelsList;
				} catch (SQLException e) {
					if(Constants.MSG_CONNECTION_EXCEPTION.equals(e.getMessage()) && (num<NUM_DATASOURCE-1))
					{
						if(logger.isDebugEnabled())
							logger.debug("Connections of primary datasource failed.Trying with secondary datasource");
						this.flipDataSource(localDataSource);
						continue;	
					}
					logger.error("[PAC] PACDAOImpl:assignUserChannels SQL Exception occured ",e);
					throw e;
				} catch (Exception e) {
					logger.error("[PAC] PACDAOImpl:assignUserChannels Exception occured ",e);
					throw e;
				} finally {
					if(insertStmt!=null)
						insertStmt.close();
					if(connection!=null)
						connection.close(); 
				}
			}
		}else if(logger.isDebugEnabled()){
		     logger.debug("[PAC] PACDAOImpl: empty list exiting....");
		  }
		if (logger.isDebugEnabled()) {
			logger.debug("[PAC] PACDAOImpl: assignUserChannels() exiting....");
		}
		return finalchannelsList;
	}
		
	@Override
	public List<Channel> modifyUserChannels(List<ChannelDO> channelDoList)
			throws SQLException, Exception {

		if (logger.isDebugEnabled()) {
			logger.debug("[PAC] PACDAOImpl: modifyUserChannels() entered");
		}
		 List<Channel> finalchannelsList = new LinkedList<Channel>();
		 
		if (channelDoList != null && channelDoList.size()!=0)  {
			Connection connection =null;
			Statement updateStmt=null;			
outer_loop:			
		for(int num=0;num<NUM_DATASOURCE;num++){
				DataSource localDataSource=currentDataSource;
			try{
				connection=localDataSource.getConnection();
				if(connection==null){
						logger.error("[PAC] PACDAOImpl: modifyUserChannels() No Connection");
						throw new Exception("No DB Connection");
					}		
				updateStmt=connection.createStatement();
				PACMemoryMap pacMemoryMap=PACMemoryMap.getInstance();
				Iterator <ChannelDO> iterator = channelDoList.iterator();
				while(iterator.hasNext())
				{					
					ChannelDO channelDo=iterator.next();
					String appId=channelDo.getApplicationId();
					String aconyxUserName=channelDo.getAconyxUserName();
					String channelUsername=channelDo.getChannelUsername();
					String channelName=channelDo.getChannelName();					
					int ChannelId=Configuration.getInstance().getChannelId(channelName);	
					Channel channel=new Channel();
					channel.setChannelName(channelName);					
					channel.setChannelUsername(channelUsername);					
					UserChannelDataRow userChannelDataRow=pacMemoryMap.getChannelUserData(appId, aconyxUserName, ChannelId, channelUsername);
					if(userChannelDataRow!=null){
						//Ignore blank and null password
						String requestPassword=channelDo.getPassword();
						String password=(requestPassword!=null && !(requestPassword.trim().isEmpty()))?requestPassword:userChannelDataRow.getPassword();
						String encrypted=(channelDo.getEncrypted()!=null)?channelDo.getEncrypted():userChannelDataRow.getEncrypted();									
						String channelURL=(channelDo.getChannelURL()!=null)?channelDo.getChannelURL():userChannelDataRow.getChannelURL();	
						long lastUpdated=System.currentTimeMillis();
					
						String updateQuery="update user_presence_data set password = '"+password.replaceAll("'","''")+"', isencrypted = '"+encrypted+"', channel_url = '"+channelURL.replaceAll("'","''")+"',last_updated = '"+lastUpdated+"' where application_id='"+appId+"' and aconyx_username='"+aconyxUserName.replaceAll("'", "''")+"' and channel_username='"+channelUsername.replaceAll("'", "''")+"' and channel_id='"+ChannelId+"'";
						if(logger.isDebugEnabled())
							logger.debug("[PAC] PACDAOImpl:"+updateQuery);
						try{
							updateStmt.execute(updateQuery);
							userChannelDataRow.setPassword(password);
							userChannelDataRow.setEncrypted(encrypted);
							userChannelDataRow.setChannelURL(channelURL);
							userChannelDataRow.setLastUpdated(lastUpdated);
							pacMemoryMap.insertUpdateChannelUserData(appId,
									aconyxUserName, ChannelId, channelUsername,
									userChannelDataRow);
							
							channel.setPassowrd(channelDo.getPassword());
							channel.setEncrypted(channelDo.getEncrypted());
							channel.setChannelURL(channelDo.getChannelURL());
							channel.setStatus(Constants.STATUS_SUCCESS);							
						
							}catch(SQLException e) {
								String msg=e.getMessage();
								if(((msg!=null && msg.contains(Constants.MSG_READ_ONLY_EXCEPTION))|| e instanceof CommunicationsException )&& (num<NUM_DATASOURCE-1))
								{
									if(logger.isDebugEnabled())
										logger.debug("Connections of primary datasource failed.Trying with secondary datasource");
									this.flipDataSource(localDataSource);
									updateStmt.close();
									connection.close();								
									continue outer_loop;
								}else{
									logger.error(e.toString());
								channel.setStatus(Constants.STATUS_FAILED);}
								
							}catch (Exception e) {
								logger.error(e.toString());
								channel.setStatus(Constants.STATUS_FAILED);
							}
					}else
						channel.setStatus(Constants.STATUS_FAILED);
					
					finalchannelsList.add(channel);
					iterator.remove();
				}	
				updateStmt.close();		
				connection.close();   
				return finalchannelsList;	
			}catch (SQLException e) {
				if(Constants.MSG_CONNECTION_EXCEPTION.equals(e.getMessage()) && (num<NUM_DATASOURCE-1))
				{
					if(logger.isDebugEnabled())
						logger.debug("Connections of primary datasource failed.Trying with secondary datasource");
					this.flipDataSource(localDataSource);
					continue;	
				}
				logger.error("[PAC] PACDAOImpl: modifyUserChannels() SQL Exception occured ",e);
				throw e;
			}
			catch(Exception e)
			{
				logger.error("[PAC] PACDAOImpl: modifyUserChannels() Exception occured ",e);
				throw e;
			}
			finally{
				if(updateStmt!=null)
					updateStmt.close();
				if(connection!=null)
					connection.close(); 
			}
		}
			}else if(logger.isDebugEnabled()){
			     logger.debug("[PAC] PACDAOImpl: empty list exiting....");
			  }
		if (logger.isDebugEnabled()) {
			logger.debug("[PAC] PACDAOImpl: modifyUserChannels() exiting");
		}
		return finalchannelsList;	
	}

	@Override
	public List<Channel> deleteUserChannels(List<ChannelDO> channelDoList)
			throws SQLException, Exception {
		if (logger.isDebugEnabled()) {
			logger.debug("[PAC] PACDAOImpl: deleteUserChannels() entered");
		}
		List<Channel> finalchannelsList = new LinkedList<Channel>();
		if (channelDoList != null && channelDoList.size()!=0) {			
			Connection connection =null;
			Statement statement=null;
outer_loop:
				for(int num=0;num<NUM_DATASOURCE;num++){
				DataSource localDataSource=currentDataSource;
				try{
					connection=localDataSource.getConnection();
				if (connection == null) {
					logger.error("[PAC] PACDAOImpl: deleteUserChannels() No Connection");
					throw new Exception("No DB Connection");
				}		
				statement=connection.createStatement();	
				PACMemoryMap pacMemoryMap=PACMemoryMap.getInstance();
				Iterator<ChannelDO> iterator=channelDoList.iterator();
				while(iterator.hasNext())	{
					ChannelDO channelDo=iterator.next();
					String appId=channelDo.getApplicationId();
					String aconyxUserName=channelDo.getAconyxUserName();
					String channelUsername=channelDo.getChannelUsername();
					String channelName=channelDo.getChannelName();					
					int ChannelId=Configuration.getInstance().getChannelId(channelName);	
					Channel channel=new Channel();
					channel.setChannelName(channelName);					
					channel.setChannelUsername(channelUsername);
					
					UserChannelDataRow userChannelDataRow=pacMemoryMap.getChannelUserData(appId, aconyxUserName, ChannelId, channelUsername);
					if(userChannelDataRow!=null){
					String deleteQuery="delete from  user_presence_data  where application_id='"+appId+"' and aconyx_username='"+aconyxUserName.replaceAll("'", "''")+"' and channel_username='"+channelUsername.replaceAll("'", "''")+"' and channel_id='"+ChannelId+"'";
						try {
							statement.execute(deleteQuery);
							pacMemoryMap.deleteChannelUserData(appId,aconyxUserName, ChannelId, channelUsername);
							channel.setStatus(Constants.STATUS_SUCCESS);
						} 
						catch(SQLException e) {
							String msg=e.getMessage();
							if(((msg!=null && msg.contains(Constants.MSG_READ_ONLY_EXCEPTION))|| e instanceof CommunicationsException )&& (num<NUM_DATASOURCE-1))
							{
								if(logger.isDebugEnabled())
									logger.debug("Connections of primary datasource failed.Trying with secondary datasource");
								this.flipDataSource(localDataSource);
								statement.close();
								connection.close();								
								continue outer_loop;
							}else{
								logger.error(e.toString());
								channel.setStatus(Constants.STATUS_FAILED);

							}
						}catch (Exception e) {
							logger.error(e.toString());
							channel.setStatus(Constants.STATUS_FAILED);
						}
					}
					else
						channel.setStatus(Constants.STATUS_FAILED);
					
					finalchannelsList.add(channel);
					iterator.remove();
				}	
				statement.close();		
				connection.close();
				return finalchannelsList;				
			} catch (SQLException e) {
					if(Constants.MSG_CONNECTION_EXCEPTION.equals(e.getMessage()) && (num<NUM_DATASOURCE-1))
					{
						if(logger.isDebugEnabled())
							logger.debug("Connections of primary datasource failed.Trying with secondary datasource");
						this.flipDataSource(localDataSource);
						continue;	
					}
				logger.error("[PAC] PACDAOImpl: deleteUserChannels() SQL Exception occured ",e);
				throw e;
			} catch (Exception e) {
				logger.error("[PAC] PACDAOImpl: deleteUserChannels() Exception occured ",e);
				throw e;
			} finally {
				if(statement!=null)
					statement.close();
				if(connection!=null)
					connection.close(); 
			}
		}
			}else if(logger.isDebugEnabled()){
			     logger.debug("[PAC] PACDAOImpl: empty list exiting....");
			  }
		if (logger.isDebugEnabled()) 
			logger.debug("[PAC] PACDAOImpl: deleteUserChannels() exiting....");
		return finalchannelsList;		
	}

	@Override
	public boolean addAconyxUser(UserDO userDO) throws SQLException, Exception {

		if(logger.isDebugEnabled())
			logger.debug("[PAC] PACDAOImpl: addAconyxUser() entered");
		boolean success=false;
		if (userDO != null) {
			Connection connection = null;
			Statement statement = null;
			ResultSet res=null;
			outer_loop:
				for(int num=0;num<NUM_DATASOURCE;num++){
					DataSource localDataSource=currentDataSource;
					try{
						connection=localDataSource.getConnection();
				if (connection == null) {
					logger.error("[PAC] PACDAOImpl: addAconyxUser() No Connection");
					throw new Exception("No DB Connection");
				}
				statement = connection.createStatement();
				String aconyxUserName=userDO.getAconyxUserName();
				String password=userDO.getPassword();
				if(password!=null && password.trim().length()==0)
					password=null; // Ignore Blank password
				String encrypted=userDO.getEncrypted();
				String role=userDO.getRole();
				res=statement.executeQuery("select count(*) from aconyx_user_info where aconyx_username = '"+aconyxUserName.replaceAll("'", "''")+"'");
				while(res.next()){
		 			int count=res.getInt(1);
		 			if(count>0){
		 				return success;
		 			}
				}
				if (password!=null && encrypted.equalsIgnoreCase(Constants.ENCRYPTED_YES)) {
					// Password decryption process
				}
				String insertQuery="insert into aconyx_user_info (aconyx_username, password, role) values ('"+aconyxUserName.replaceAll("'", "''")+"', '"+password.replaceAll("'", "''")+"', '"+role+"')";
				statement.execute(insertQuery);	
				success=true;
				res.close();
				statement.close();
				connection.close();
				return success;
			} catch (SQLException e) {
				String msg=e.getMessage()!=null?e.getMessage():"";
				if((Constants.MSG_CONNECTION_EXCEPTION.equals(msg) || msg.contains(Constants.MSG_READ_ONLY_EXCEPTION) || e instanceof CommunicationsException) && (num<NUM_DATASOURCE-1))
				{
					if(logger.isDebugEnabled())
						logger.debug("Connections of primary datasource failed.Trying with secondary datasource");
					this.flipDataSource(localDataSource);
					continue outer_loop;	
				}
				logger.error("[PAC] PACDAOImpl: addAconyxUser() SQLException occured ",e);
				throw e;
			} catch (Exception e) {
				logger.error("[PAC] PACDAOImpl: addAconyxUser() Exception occured ", e);
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
		}else if(logger.isDebugEnabled()){
		     logger.debug("[PAC] PACDAOImpl: null userDO exiting....");
		  }
		if(logger.isDebugEnabled())
			logger.debug("[PAC] PACDAOImpl: addAconyxUser() exiting....");
		return success;
	}

	@Override
	public UserDO getAconyxUserData(String aconyxUsername) throws SQLException,
	Exception {
		if(logger.isDebugEnabled())
			logger.debug("[PAC] PACDAOImpl: getAconyxUserData() entered");
		UserDO userDO=null;
		if (aconyxUsername != null) {
			Connection connection = null;
			Statement statement = null;
			ResultSet res=null;
			outer_loop:
				for(int num=0;num<NUM_DATASOURCE;num++){		
					DataSource localDataSource=currentDataSource;
					try{
						connection=localDataSource.getConnection();
						if (connection == null) {
							logger.error("[PAC] PACDAOImpl: getAconyxUserData() No Connection");
							throw new Exception("No DB Connection");
						}
						statement = connection.createStatement();
						res=statement.executeQuery("select password,role from aconyx_user_info where aconyx_username = '"+aconyxUsername.replaceAll("'", "''")+"'");
						while(res.next())
						{
							String password=res.getString("password");
							String role=res.getString("role");
							userDO=new UserDO(aconyxUsername, password, Constants.ENCRYPTED_NO, role);		 			
						}	
						res.close();
						statement.close();
						connection.close();
						return userDO;	
					}catch (CommunicationsException e){
						if(num<NUM_DATASOURCE-1){
							if(logger.isDebugEnabled())
								logger.debug("Connections of primary datasource failed.Trying with secondary datasource");
							this.flipDataSource(localDataSource);
							statement.close();
							connection.close();								
							continue outer_loop;
						}
						logger.error("[PAC] PACDAOImpl: getAconyxUserData() SQLException occured ",e);
						throw e;
					} catch (SQLException e) {
						if(Constants.MSG_CONNECTION_EXCEPTION.equals(e.getMessage()) && (num<NUM_DATASOURCE-1))
						{
							if(logger.isDebugEnabled())
								logger.debug("Connections of primary datasource failed.Trying with secondary datasource");
							this.flipDataSource(localDataSource);
							continue;	
						}
						logger.error("[PAC] PACDAOImpl: getAconyxUserData() SQLException occured ",e);
						throw e;
					} catch (Exception e) {
						logger.error("[PAC] PACDAOImpl: getAconyxUserData() Exception occured ", e);
						throw e;
					} finally {
						if(res!=null)
							res.close();
						if(statement!=null)
							statement.close();
						if(connection!=null)
							connection.close();
					}
				}}else if(logger.isDebugEnabled()){
				     logger.debug("[PAC] PACDAOImpl: null aconyx username exiting....");
				  }
		if(logger.isDebugEnabled())
			logger.debug("[PAC] PACDAOImpl: getAconyxUserData() entered");
		return userDO;	
	}
	
	@Override
	public List<UserDO> getAllAconyxUsersData()throws SQLException,Exception{		
		if(logger.isDebugEnabled())
			logger.debug("[PAC] PACDAOImpl: getAllAconyxUsersData() entered");
		List <UserDO> userDOList=new LinkedList<UserDO>();	
			Connection connection = null;
			Statement statement = null;
			ResultSet res=null;
			outer_loop:
				for(int num=0;num<NUM_DATASOURCE;num++){			
					DataSource localDataSource=currentDataSource;
					try{
						connection=localDataSource.getConnection();
				if (connection == null) {
					logger.error("[PAC] PACDAOImpl: getAllAconyxUsersData() No Connection");
					throw new Exception("No DB Connection");
				}
				statement = connection.createStatement();
				res=statement.executeQuery("select aconyx_username,password,role from aconyx_user_info");
				while(res.next()){
					String aconyxUsername=res.getString("aconyx_username");
					String password=res.getString("password");
					String role=res.getString("role");
					UserDO userDO=new UserDO(aconyxUsername, password, Constants.ENCRYPTED_NO, role);		
					userDOList.add(userDO);
				}	
				res.close();
				statement.close();
				connection.close();
					} catch (CommunicationsException e){
						if(num<NUM_DATASOURCE-1){
							if(logger.isDebugEnabled())
								logger.debug("Connections of primary datasource failed.Trying with secondary datasource");
							this.flipDataSource(localDataSource);							
							continue outer_loop;
						}
						logger.error("[PAC] PACDAOImpl: getAllAconyxUsersData() SQLException occured ",e);
						throw e;
					}
				catch (SQLException e) {
					if(Constants.MSG_CONNECTION_EXCEPTION.equals(e.getMessage()) && (num<NUM_DATASOURCE-1))
					{
						if(logger.isDebugEnabled())
							logger.debug("Connections of primary datasource failed.Trying with secondary datasource");
						this.flipDataSource(localDataSource);
						continue;	
					}
				logger.error("[PAC] PACDAOImpl: getAllAconyxUsersData() SQLException occured ",e);
				throw e;
			} catch (Exception e) {
				logger.error("[PAC] PACDAOImpl: getAllAconyxUsersData() Exception occured ", e);
				throw e;
			} finally {
				if(res!=null)
					res.close();
				if(statement!=null)
					statement.close();
				if(connection!=null)
					connection.close();
			}
				break; //All operations perfromed successfully with current datasource for break loop
			}
			if(logger.isDebugEnabled())
				logger.debug("[PAC] PACDAOImpl: getAllAconyxUsersData() exiting");
		return userDOList;	
	}
	
	@Override
	public boolean modifyAconyxUser(UserDO userDO) throws SQLException, Exception {
		if(logger.isDebugEnabled())
			logger.debug("[PAC] PACDAOImpl: modifyAconyxUser() entered");
		boolean success=false;
		if (userDO != null) {
			Connection connection = null;
			Statement statement = null;
			ResultSet res=null;
			outer_loop:
				for(int num=0;num<NUM_DATASOURCE;num++){
					DataSource localDataSource=currentDataSource;
					try{
						connection=localDataSource.getConnection();
				if (connection == null) {
					logger.error("[PAC] PACDAOImpl: modifyAconyxUser() No Connection");
					throw new Exception("No DB Connection");
				}
				statement = connection.createStatement();
				String aconyxUserName=userDO.getAconyxUserName();
				String password=userDO.getPassword();
				if(password!=null && password.trim().length()==0)
					password=null; // Ignore Blank password
				String encrypted=userDO.getEncrypted();
				String role=userDO.getRole();
				res=statement.executeQuery("select count(*) from aconyx_user_info where aconyx_username = '"+aconyxUserName.replaceAll("'", "''")+"'");
				while(res.next()){
		 			int count=res.getInt(1);
		 			if(count==0){
		 				return success;
		 			}
				}
				if (password!=null && encrypted.equalsIgnoreCase(Constants.ENCRYPTED_YES)) {
					// Password decryption process
				}
				String updateQuery=null;
				if(password!=null && role!=null){
					updateQuery="update aconyx_user_info set password = '"+password.replaceAll("'", "''")+"', role = '"+role.replaceAll("'", "''")+"' where aconyx_username = '"+aconyxUserName.replaceAll("'", "''")+"'";
				}else if(password!=null){
					updateQuery="update aconyx_user_info set password = '"+password.replaceAll("'", "''")+"' where aconyx_username = '"+aconyxUserName.replaceAll("'", "''")+"'";
				}else if(role!=null){
					updateQuery="update aconyx_user_info set role = '"+role.replaceAll("'", "''")+"' where aconyx_username = '"+aconyxUserName.replaceAll("'", "''")+"'";
				}
				statement.execute(updateQuery);	
				if(logger.isDebugEnabled())
					logger.debug("[PAC] PACDAOImpl:"+updateQuery);
				success=true;
				res.close();
				statement.close();
				connection.close();
				return success;
			} catch (SQLException e) {
				String msg=e.getMessage()!=null?e.getMessage():"";
				if((Constants.MSG_CONNECTION_EXCEPTION.equals(msg) || msg.contains(Constants.MSG_READ_ONLY_EXCEPTION) || e instanceof CommunicationsException) && (num<NUM_DATASOURCE-1))
				{
					if(logger.isDebugEnabled())
						logger.debug("Connections of primary datasource failed.Trying with secondary datasource");
					this.flipDataSource(localDataSource);
					continue outer_loop;	
				}
				logger.error("[PAC] PACDAOImpl: modifyAconyxUser() SQLException occured ",e);
				throw e;
			} catch (Exception e) {
				logger.error("[PAC] PACDAOImpl: modifyAconyxUser() Exception occured ", e);
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
		}else if(logger.isDebugEnabled()){
		     logger.debug("[PAC] PACDAOImpl: null userDO exiting....");
		  }
		if(logger.isDebugEnabled())
			logger.debug("[PAC] PACDAOImpl: modifyAconyxUser() exiting....");
		return success;
	}

	@Override
	public boolean deleteAconyxUser(UserDO userDO) throws SQLException, Exception {
		if(logger.isDebugEnabled())
			logger.debug("[PAC] PACDAOImpl: deleteAconyxUser() entered");
		boolean success=false;
		if (userDO != null) {
			Connection connection = null;
			Statement statement = null;
			ResultSet res=null;
for(int num=0;num<NUM_DATASOURCE;num++){	
				DataSource localDataSource=currentDataSource;
				try{
					connection=localDataSource.getConnection();
					if (connection == null) {
					logger.error("[PAC] PACDAOImpl: deleteAconyxUser() No Connection");
					throw new Exception("No DB Connection");
				}
				statement = connection.createStatement();				
				String aconyxUsername=userDO.getAconyxUserName();
				res=statement.executeQuery("select count(*) from aconyx_user_info where aconyx_username = '"+aconyxUsername.replaceAll("'", "''")+"'");
				while(res.next()){
		 			int count=res.getInt(1);
		 			if(count==0){
		 				return success;
		 			}
				}
				String deleteQuery="delete from aconyx_user_info where aconyx_username = '"+aconyxUsername+"'";				
				statement.execute(deleteQuery);	
				if(logger.isDebugEnabled())
					logger.debug("[PAC] PACDAOImpl: deleteAconyxUser(): deleted successfully:"+aconyxUsername);
				PACMemoryMap.getInstance().deleteAllAconyxUserChannels(aconyxUsername);
				success=true;
				res.close();
				statement.close();
				connection.close();
				return success;
			} catch (SQLException e) {
				String msg=e.getMessage()!=null?e.getMessage():"";
				if((Constants.MSG_CONNECTION_EXCEPTION.equals(msg) || msg.contains(Constants.MSG_READ_ONLY_EXCEPTION) || e instanceof CommunicationsException) && (num<NUM_DATASOURCE-1))
				{
					if(logger.isDebugEnabled())
						logger.debug("Connections of primary datasource failed.Trying with secondary datasource");
					this.flipDataSource(localDataSource);
					continue;	
				}
				logger.error("[PAC] PACDAOImpl: deleteAconyxUser() SQLException occured ",e);
				throw e;
			} catch (Exception e) {
				logger.error("[PAC] PACDAOImpl: deleteAconyxUser() Exception occured ", e);
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
			}else if(logger.isDebugEnabled()){
			     logger.debug("[PAC] PACDAOImpl: null userDO exiting....");
			  }
		if(logger.isDebugEnabled())
			logger.debug("[PAC] PACDAOImpl: deleteAconyxUser() exiting....");
		return success;
	}

	@Override
	public boolean loadPACMemoryMapFromDB() throws SQLException, Exception {
		if(logger.isDebugEnabled())
			logger.debug("[PAC] PACDAOImpl: loadPACMemoryMapFromDB() entered");
		boolean isSuccessfulyLoaded=false;
		Connection connection = null;
		Statement statement = null;
		ResultSet res=null;
		SIPPACAdaptor sipAdaptor=(SIPPACAdaptor) PACAdaptorFactory.getInstance().getPACAdaptor(SIPPACAdaptor.SIP_CHANNEL);
		for(int num=0;num<NUM_DATASOURCE;num++){		
			DataSource localDataSource=currentDataSource;
			try{
				connection=localDataSource.getConnection();
			if (connection == null) {
				logger.error("[PAC] PACDAOImpl: loadPACMemoryMapFromDB() No Connection");
				throw new Exception("No DB Connection");
			}
			statement = connection.createStatement();
			res=statement.executeQuery("select application_id,aconyx_username,channel_username,password,isencrypted,channel_id,channel_url" +
					",presence_status,custom_label,operational_status,last_updated from user_presence_data");
			PACMemoryMap pacMemoryMap=PACMemoryMap.getInstance();
			LinkedList<ChannelDO> channelDOList=new LinkedList<ChannelDO>();
			int rowsLoaded=0;
			while(res.next())
	 		{
				String applicationId = res.getString("application_id");
				String aconyxUsername = res.getString("aconyx_username");
				String channelUsername = res.getString("channel_username");
				String password = res.getString("password");
				String encrypted = res.getString("isencrypted");
				int channelId = res.getInt("channel_id");
				String channelURL = res.getString("channel_url");
				String status = res.getString("presence_status");
				String customLabel = res.getString("custom_label");
				int opreationalStatus = res.getInt("operational_status");
				long lastUpdated = res.getLong("last_updated");
				UserChannelDataRow uRow=new UserChannelDataRow(channelUsername, channelId, password, encrypted,channelURL, status, customLabel, opreationalStatus, lastUpdated);
				pacMemoryMap.insertUpdateChannelUserData(applicationId, aconyxUsername, channelId, channelUsername, uRow);
				boolean susbcribeForUser = subscribeForOnlyActiveUsers ? !status.equals(Constants.PRESENCE_STATUS_NOT_AVAILABLE) : true;
				if(PACManager.SUBSCRIBE_FOR_PRESENCE_ON_LOAD && susbcribeForUser){
					String channelName = Configuration.getInstance().getChannelMapDO(channelId).getChannelName();
					channelDOList.add(new ChannelDO(applicationId, aconyxUsername, channelUsername, password, encrypted, channelName, channelURL));
					rowsLoaded++;
				}
			}
			if(channelDOList.size()>0){
				PACSubscriptionWork work=new PACSubscriptionWork(channelDOList, PACSubscriptionWork.Operation.SUBSCRIBE);
				PACManager.pacExecutorService.submit(work);
			}
			logger.error("[PAC] PACDAOImpl: loadPACMemoryMapFromDB() Loaded "+rowsLoaded+" rows in PACMemoryMap from DB");
			res.close();
			statement.close();
			connection.close();
			isSuccessfulyLoaded=true;
		} catch (CommunicationsException e){
			if(num<NUM_DATASOURCE-1){
				if(logger.isDebugEnabled())
					logger.debug("Connections of primary datasource failed.Trying with secondary datasource");
				this.flipDataSource(localDataSource);						
				continue;
			}
			logger.error("[PAC] PACDAOImpl: loadPACMemoryMapFromDB() SQLException occured ",e);
			throw e;
		}catch (SQLException e) {
				if(Constants.MSG_CONNECTION_EXCEPTION.equals(e.getMessage()) && (num<NUM_DATASOURCE-1))
				{
					if(logger.isDebugEnabled())
						logger.debug("Connections of primary datasource failed.Trying with secondary datasource");
					this.flipDataSource(localDataSource);
					continue;	
				}
			logger.error("[PAC] PACDAOImpl: loadPACMemoryMapFromDB() SQLException occured ",e);
			throw e;
		} catch (Exception e) {
			logger.error("[PAC] PACDAOImpl: loadPACMemoryMapFromDB() Exception occured ", e);
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
		if(logger.isDebugEnabled())
			logger.debug("[PAC] PACDAOImpl: loadPACMemoryMapFromDB() exiting....:"+isSuccessfulyLoaded);
		return isSuccessfulyLoaded;
	}

	@Override
	public List<PresenceDO> updatePresence(List<PresenceDO> presenceList)throws SQLException, Exception {
		if (logger.isDebugEnabled()) {
			logger.debug("[PAC] updatePresence entered");
		}
		List <PresenceDO> resultList=new LinkedList<PresenceDO>();
		if (presenceList != null && presenceList.size()!=0) {
			Connection connection = null;
			Statement statement = null;
			outer_loop:
				for(int num=0;num<NUM_DATASOURCE;num++){
					DataSource localDataSource=currentDataSource;
					try{
						connection=localDataSource.getConnection();
						if (connection == null) {
							logger.error("[PAC] PACDAOImpl: updatePresence() No Connection");
							throw new Exception("No DB Connection");
						}
						statement = connection.createStatement();
						PACMemoryMap pacMemoryMap=PACMemoryMap.getInstance();
						Iterator<PresenceDO> iterator = presenceList.iterator();
						while(iterator.hasNext()){
							PresenceDO presenceDO=iterator.next();
							String applicationId=presenceDO.getApplicationId();
							String aconyxUsername=presenceDO.getAconyxUsername();
							String channelUsername=presenceDO.getChannelUsername();
							String channelName=presenceDO.getChannelName();
							String status=presenceDO.getStatus();
							String customLabel=presenceDO.getCustomLabel();
							String customLabelDB=(customLabel==null)?customLabel:customLabel.replaceAll("'","''");
							int channelId=Configuration.getInstance().getChannelId(channelName);
							long lastUpdated = System.currentTimeMillis();
							String updateQuery="update user_presence_data set presence_status = '"+status+"', custom_label = '"+customLabelDB+"',last_updated='"+lastUpdated+"' where application_id='"+applicationId+"' and aconyx_username = '"+aconyxUsername.replaceAll("'", "''")+"' and channel_username='"+channelUsername.replaceAll("'", "''")+"' and channel_id='"+channelId+"' ";
							if(logger.isDebugEnabled())
								logger.debug("[PAC] PACDAOImpl:"+updateQuery);
							boolean success=false;
							try{
								statement.execute(updateQuery);
								success=true;
							}catch(SQLException e) {
								String msg=e.getMessage();
								if(((msg!=null && msg.contains(Constants.MSG_READ_ONLY_EXCEPTION))|| e instanceof CommunicationsException )&& (num<NUM_DATASOURCE-1))
								{
									if(logger.isDebugEnabled())
										logger.debug("Connections of primary datasource failed.Trying with secondary datasource");
									this.flipDataSource(localDataSource);
									statement.close();
									connection.close();								
									continue outer_loop;
								}else
									logger.error("[PAC] PACDAOImpl: updatePresence() error occured ",e);
								}catch (Exception e) {
									logger.error("[PAC] PACDAOImpl: updatePresence() error occured ",e);
								}
								if(success){
									UserChannelDataRow row=pacMemoryMap.getChannelUserData(applicationId, aconyxUsername,channelId, channelUsername);
									row.setStatus(status);
									row.setCustomLabel(customLabel);
									row.setLastUpdated(lastUpdated);
									pacMemoryMap.insertUpdateChannelUserData(applicationId, aconyxUsername, channelId, channelUsername, row);
									presenceDO.setStatus(Constants.STATUS_SUCCESS);
								}
								else
									presenceDO.setStatus(Constants.STATUS_FAILED);	
								
								resultList.add(presenceDO);
								iterator.remove();
							}				
							statement.close();
							connection.close();
							return resultList;
						} catch (SQLException e) {							
							if(Constants.MSG_CONNECTION_EXCEPTION.equals(e.getMessage()) && (num<NUM_DATASOURCE-1))
							{
								if(logger.isDebugEnabled())
									logger.debug("Connections of primary datasource failed.Trying with secondary datasource");
								this.flipDataSource(localDataSource);
								continue;	
							}
							logger.error("[PAC] PACDAOImpl: updatePresence() SQLException occured ",e);
							throw e;
						} catch (Exception e) {
							logger.error("[PAC] PACDAOImpl: updatePresence() Exception occured ", e);
							throw e;
						} finally {
							if(statement!=null)
								statement.close();
							if(connection!=null)
								connection.close(); 
						}
					}
				}else if(logger.isDebugEnabled()){
				     logger.debug("[PAC] PACDAOImpl: empty list exiting....");
				  }
		if (logger.isDebugEnabled()) 
			logger.debug("[PAC] updatePresence exiting....");
		return resultList;
	}

	@Override
	public void deleteAllAppChannels(String applicationId) throws SQLException,Exception {
		if(logger.isDebugEnabled())
			logger.debug("[PAC] PACDAOImpl: deleteAllAppChannels() entered");
		if (applicationId != null) {
			Connection connection = null;
			Statement statement = null;
			for(int num=0;num<NUM_DATASOURCE;num++){					
				DataSource localDataSource=currentDataSource;
				try{
					connection=localDataSource.getConnection();
					if (connection == null) {
						logger.error("[PAC] PACDAOImpl: deleteAllAppChannels No Connection");
						throw new Exception("No DB Connection");
					}
					statement = connection.createStatement();
					String deleteQuery="delete from user_presence_data where application_id = '"+applicationId+"'";				
					statement.execute(deleteQuery);	
					PACMemoryMap.getInstance().remove(applicationId,true);
					if(logger.isDebugEnabled())
						logger.debug("[PAC] PACDAOImpl: deleteAllAppChannels: deleted successfully for:"+applicationId);
					statement.close();
					connection.close();
					return;
				} 
				catch (SQLException e) {					
					String msg=e.getMessage()!=null?e.getMessage():"";
					if((Constants.MSG_CONNECTION_EXCEPTION.equals(msg) || msg.contains(Constants.MSG_READ_ONLY_EXCEPTION) || e instanceof CommunicationsException) && (num<NUM_DATASOURCE-1))
					{
						if(logger.isDebugEnabled())
							logger.debug("Connections of primary datasource failed.Trying with secondary datasource");
						this.flipDataSource(localDataSource);
						continue;	
					}
					logger.error("[PAC] PACDAOImpl: deleteAllAppChannels() SQLException occured ",e);
					throw e;
				} catch (Exception e) {
					logger.error("[PAC] PACDAOImpl: deleteAllAppChannels() Exception occured ", e);
					throw e;
				} finally {
					if(statement!=null)
						statement.close();
					if(connection!=null)
						connection.close(); 
				}
			}
		}else if(logger.isDebugEnabled()){
		     logger.debug("[PAC] PACDAOImpl: null appId exiting....");
		  }	
		if(logger.isDebugEnabled())
			logger.debug("[PAC] PACDAOImpl: deleteAllAppChannels() exiting....");
	}

	@Override
	public void deleteAllUserChannels(String applicationId,
			String aconyxUsername) throws SQLException, Exception {
		if(logger.isDebugEnabled())
			logger.debug("[PAC] PACDAOImpl: deleteAllUserChannels() entered");
		if (applicationId != null && aconyxUsername!=null) {
			Connection connection = null;
			Statement statement = null;
			for(int num=0;num<NUM_DATASOURCE;num++){			

				DataSource localDataSource=currentDataSource;
				try{
					connection=localDataSource.getConnection();
					if (connection == null) {
						logger.error("[PAC] PACDAOImpl: deleteAllUserChannels No Connection");
						throw new Exception("No DB Connection");
					}
					statement = connection.createStatement();
					String deleteQuery="delete from user_presence_data where application_id = '"+applicationId+"' and aconyx_username='"+aconyxUsername.replaceAll("'", "''")+"'";				
					statement.execute(deleteQuery);	
					PACMemoryMap.getInstance().deleteAconyxUserChannels(applicationId,aconyxUsername);
					if(logger.isDebugEnabled())
						logger.debug("[PAC] PACDAOImpl: deleteAllUserChannels: deleted successfully for:"+applicationId);
					statement.close();
					connection.close();
					return;
				} catch (SQLException e) {					
					String msg=e.getMessage()!=null?e.getMessage():"";
					if((Constants.MSG_CONNECTION_EXCEPTION.equals(msg) || msg.contains(Constants.MSG_READ_ONLY_EXCEPTION) || e instanceof CommunicationsException) && (num<NUM_DATASOURCE-1))
					{
						if(logger.isDebugEnabled())
							logger.debug("Connections of primary datasource failed.Trying with secondary datasource");
						this.flipDataSource(localDataSource);
						continue;	
					}
					logger.error("[PAC] PACDAOImpl: deleteAllUserChannels SQLException occured ",e);
					throw e;
				} catch (Exception e) {
					logger.error("[PAC] PACDAOImpl: deleteAllUserChannels Exception occured ", e);
					throw e;
				} finally {
					if(statement!=null)
						statement.close();
					if(connection!=null)
						connection.close(); 
				}
			}
		}	else if(logger.isDebugEnabled()){
		     logger.debug("[PAC] PACDAOImpl: null values of appId or aconyxUser exiting...."+applicationId);
		  }	
		if(logger.isDebugEnabled())
			logger.debug("[PAC] PACDAOImpl: deleteAllUserChannels() exiting....");
	}

	
	private void flipDataSource(DataSource ds){
			currentDataSource=(ds==primaryDataSource)?secondaryDataSource:primaryDataSource;
	}

	
	@Override
	public void checkAndRecoverSessions(String channelName) throws SQLException, Exception {
		if(logger.isDebugEnabled())
			logger.debug("[PAC] PACDAOImpl: checkAndRecoverSessions() entered");
		Connection connection = null;
		Statement statement = null;
		ResultSet res=null;
		int channelId=Configuration.getInstance().getChannelId(channelName);
		if(channelId==-1){
			logger.error("Invalid Channel Id.....");
			return;
		}
		for(int num=0;num<NUM_DATASOURCE;num++){		
			DataSource localDataSource=currentDataSource;
			try{
				connection=localDataSource.getConnection();
				if (connection == null) {
					logger.error("[PAC] PACDAOImpl: checkAndRecoverSessions() No Connection");
					throw new Exception("No DB Connection");
				}
				statement = connection.createStatement();
				res=statement.executeQuery("select application_id,aconyx_username,channel_username,password,isencrypted,channel_id,channel_url" +
						",presence_status,custom_label,operational_status,last_updated from user_presence_data where channel_id='"+channelId+"'");
				LinkedList<ChannelDO> channelDOList=new LinkedList<ChannelDO>();
				PACAdaptor adaptor=PACAdaptorFactory.getInstance().getPACAdaptor(channelName);
				while(res.next())
				{
					String applicationId = res.getString("application_id");
					String aconyxUsername = res.getString("aconyx_username");
					String channelUsername = res.getString("channel_username");
					String password = res.getString("password");
					String encrypted = res.getString("isencrypted");
					String channelURL = res.getString("channel_url");
					String status = res.getString("presence_status");
					boolean susbcribeForUser = subscribeForOnlyActiveUsers ? !status.equals(Constants.PRESENCE_STATUS_NOT_AVAILABLE) : true;
					if(! adaptor.isChannelWorking(applicationId, aconyxUsername, channelUsername) && susbcribeForUser){
						ChannelDO channelDo=new ChannelDO(applicationId, aconyxUsername, channelUsername, password, encrypted, channelName, channelURL);
						channelDOList.add(channelDo);
					}
				}
				
				if(channelDOList.size()>0){
					logger.error("[PAC] PACDAOImpl: checkAndRecoverSessions() will create sesssions: "+channelDOList.size());
					PACSubscriptionWork work=new PACSubscriptionWork(channelDOList, PACSubscriptionWork.Operation.SUBSCRIBE);
					PACManager.pacExecutorService.submit(work);
				}
				
				res.close();
				statement.close();
				connection.close();
				if(logger.isDebugEnabled())
					logger.debug("[PAC] PACDAOImpl: checkAndRecoverSessions() exit....");
				return;
			} catch (CommunicationsException e){
				if(num<NUM_DATASOURCE-1){
					if(logger.isDebugEnabled())
						logger.debug("Connections of primary datasource failed.Trying with secondary datasource");
					this.flipDataSource(localDataSource);						
					continue;
				}
				logger.error("[PAC] PACDAOImpl: checkAndRecoverSessions() SQLException occured ",e);
				throw e;
			}catch (SQLException e) {
				if(Constants.MSG_CONNECTION_EXCEPTION.equals(e.getMessage()) && (num<NUM_DATASOURCE-1))
				{
					if(logger.isDebugEnabled())
						logger.debug("Connections of primary datasource failed.Trying with secondary datasource");
					this.flipDataSource(localDataSource);
					continue;	
				}
				logger.error("[PAC] PACDAOImpl: checkAndRecoverSessions() SQLException occured ",e);
				throw e;
			} catch (Exception e) {
				logger.error("[PAC] PACDAOImpl: checkAndRecoverSessions() Exception occured ", e);
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
		
		if(logger.isDebugEnabled())
			logger.debug("[PAC] PACDAOImpl: checkAndRecoverSessions() exiting....");
	}
}
