/*
 * @(#)MediaServerStatisticsDaoImpl	
 * This class is used to store the MediaServerStatisticsInformation 
 * in the Database.
 *
 */
package com.baypackets.ase.sbb.mediaserver.mediaserverstatistics;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import org.apache.log4j.Logger;

import com.baypackets.ase.util.Constants;



public class MediaServerStatisticsDaoImpl implements MediaServerStatisticsDao{


	private static Logger logger = Logger.getLogger(MediaServerStatisticsDao.class);
	private static MediaServerStatisticsDao mediaServerStatisticsDao;
	private DataSource dataSource=null;
	private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss"); 

	public static synchronized MediaServerStatisticsDao getInstance() throws Exception{
		if(mediaServerStatisticsDao==null)
			mediaServerStatisticsDao=new MediaServerStatisticsDaoImpl();
		return mediaServerStatisticsDao;
	}

	@Override
	protected Object clone() throws CloneNotSupportedException {
		throw new CloneNotSupportedException("Clone is not allowed.");
	}

	/**
	 * Constructor which is setting the DataSource.
	 */
	private MediaServerStatisticsDaoImpl() throws NamingException {
		//jndi lookup
		final String DATASOURCE_NAME = Constants.SAS_DATASOURCE_NAME;
		String PROVIDER_URL = "file:" +	System.getProperty("ase.home") + "/jndiprovider/fileserver/";
		String CONTEXT_FACTORY ="com.sun.jndi.fscontext.RefFSContextFactory";
		if(logger.isDebugEnabled())
			logger.debug("getConnection()-->PROVIDER_URL::["+PROVIDER_URL+"]");
		InitialContext ctx = null;
		Hashtable<String, String> env = null;
		DataSource ds = null;
		env = new Hashtable<String, String>();
		env.put(Context.INITIAL_CONTEXT_FACTORY, CONTEXT_FACTORY);
		env.put(Context.PROVIDER_URL, PROVIDER_URL);
		ctx = new InitialContext(env);

		if(logger.isDebugEnabled())
			logger.debug("getConnection()-->Got context::["+ctx+"]");
		ds = (DataSource) ctx.lookup(DATASOURCE_NAME);
		this.setDataSource(ds);
	}

	/**
	 * This method will get the data from msStatisticsMap and  call the procedure
	 * This method will be called according to the property(media.statistics.db.store.timer) defined 
	 * in ase.properties.
	 */
	public void sendMsStaticticsToDB(Map<String,MediaServerStatisticsInfo> msStatisticsMap){
		CallableStatement stmt = null;
		Connection con = null;
		try{
			//creating connection
			con = getDataSource().getConnection();
			con.setAutoCommit(false);
			stmt = con.prepareCall("BEGIN GMS_STATISTICS_INFO_PROC(:ANNOUNCEMENT_ID, :TYPE_ANN,:ATTEMPTS_ANN , :RETRIES_ANN, :DURATION_ANN , :LAST_UPDATED_ANN); end;");
			Collection<MediaServerStatisticsInfo> mediaServerStatisticsInfos = msStatisticsMap.values();

			Iterator<MediaServerStatisticsInfo> mediaServerStatisticsInfosIterator = mediaServerStatisticsInfos.iterator();

			while(mediaServerStatisticsInfosIterator.hasNext()){
				MediaServerStatisticsInfo  mediaServerStatisticsInfo =mediaServerStatisticsInfosIterator.next();
				//setting statement parameters
				stmt.setString(1, mediaServerStatisticsInfo.getAnnouncementID());
				stmt.setString(2, mediaServerStatisticsInfo.getAnnouncementType());
				stmt.setInt(3, mediaServerStatisticsInfo.getAttempts());
				stmt.setInt(4, mediaServerStatisticsInfo.getRetries());
				stmt.setFloat(5, mediaServerStatisticsInfo.getDuration());
				stmt.setString(6,dateFormat.format(new Date()));
				
				stmt.execute();
				msStatisticsMap.remove(mediaServerStatisticsInfo.getAnnouncementID()+"_"+mediaServerStatisticsInfo.getAnnouncementType());
			}
		} catch (SQLException sqlExecuteException) {
			logger.error("SQLException Occurred while execution sendMsStaticticsToDB: "+sqlExecuteException);
		} catch (Exception e) {
			logger.error("Exception Occurred in sendMsStaticticsToDB: "+ e.getMessage());
		} finally {
			if (stmt != null) {
				try {
					stmt.close();
				} catch (SQLException e) {
					logger.error("Unable to close stmt SQLException "+e.getMessage());
				}
			}
			if (con != null) {
				try {
					con.setAutoCommit(true);
					con.close();
				} catch (SQLException e) {
					logger.error("Unable to close connection SQLException "+e.getMessage());
				}
			}
		}
	}

	/**
	 * @param dataSource the dataSource to set
	 */
	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	/**
	 * @return the dataSource
	 */
	public DataSource getDataSource() {
		return dataSource;
	}



}