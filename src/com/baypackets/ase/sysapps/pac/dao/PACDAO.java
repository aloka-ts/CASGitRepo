/*
 * PACDAO.java
 * @author Amit Baxi
 */
package com.baypackets.ase.sysapps.pac.dao;
import java.sql.SQLException;
import java.util.List;

import com.baypackets.ase.sysapps.pac.dataobjects.ChannelDO;
import com.baypackets.ase.sysapps.pac.dataobjects.PresenceDO;
import com.baypackets.ase.sysapps.pac.dataobjects.UserDO;
import com.baypackets.ase.sysapps.pac.jaxb.Channel;
import com.baypackets.ase.sysapps.pac.jaxb.Errors;
/**
 * This is an interface for PAC related database operations.
 */
public interface PACDAO {
	public boolean loadPACMemoryMapFromDB() throws SQLException, Exception;
	public void setChannelIdMap()throws SQLException, Exception;
	public boolean addAconyxUser(UserDO userDO)throws SQLException, Exception;
	public boolean modifyAconyxUser(UserDO userDO)throws SQLException, Exception;
	public boolean deleteAconyxUser(UserDO userDO)throws SQLException, Exception;
	public UserDO getAconyxUserData(String aconyxUsername)throws SQLException, Exception;
	public List<UserDO> getAllAconyxUsersData()throws SQLException,Exception;
	public List<Channel> assignUserChannels(List<ChannelDO> channelDoList,Errors errors)throws SQLException, Exception;
	public List<Channel> modifyUserChannels(List<ChannelDO> channelDoList)throws SQLException, Exception;
	public List<Channel> deleteUserChannels(List<ChannelDO> channelDoList)throws SQLException, Exception;		
	public List<PresenceDO> updatePresence(List<PresenceDO> presenceList)throws SQLException, Exception;
	public void deleteAllAppChannels(String applicationId)throws SQLException,Exception;
	public void deleteAllUserChannels(String applicationId,String aconyxUsername)throws SQLException,Exception;
	public void checkAndRecoverSessions(String channelName)throws SQLException, Exception;
}
