/**
 * 
 */
package com.baypackets.ase.ra.telnetssh.configmanager.db;

import java.util.ArrayList;
import java.util.List;

import oracle.jdbc.dcn.DatabaseChangeEvent;
import oracle.jdbc.dcn.DatabaseChangeListener;
import oracle.jdbc.dcn.RowChangeDescription;
import oracle.jdbc.dcn.TableChangeDescription;

import org.apache.log4j.Logger;

import com.baypackets.ase.ra.telnetssh.LsResourceAdaptor;
import com.baypackets.ase.ra.telnetssh.configmanager.LsConfigChangeData;

/**
 * The Class DatabaseChangeListenerImpl.
 * Listener class implementing DatabaseChangeListener
 * to listen for changes in LS configuration on DB
 *
 * @author saneja
 */
public class DatabaseChangeListenerImpl implements DatabaseChangeListener {
	
	/** The Constant logger. */
	private static final Logger logger=Logger.getLogger(DatabaseChangeListenerImpl.class);
	
	/** The telnet ssh resource adaptor reference. */
	private LsResourceAdaptor telnetSshResourceAdaptor;

	/**
	 * Instantiates a new database change listener impl.
	 *
	 * @param telnetSshResourceAdaptor the telnet ssh resource adaptor
	 */
	public DatabaseChangeListenerImpl(
			LsResourceAdaptor telnetSshResourceAdaptor) {
		this.telnetSshResourceAdaptor=telnetSshResourceAdaptor;
	}

	/**
	 * listener method called when Db change is happened
	 * Fetches array of table in which modification happens
	 * For each table fetches array of modified rows
	 * For each row ID fetch operation type and Row specifu details
	 * and stores them in List
	 * Invokes RA contol-LsResourceAadptorImpl with changed Data List
	 * 
	 *  (non-Javadoc)
	 * @see oracle.jdbc.dcn.DatabaseChangeListener#onDatabaseChangeNotification(oracle.jdbc.dcn.DatabaseChangeEvent)
	 */
	@Override
	public void onDatabaseChangeNotification(DatabaseChangeEvent event) {
		boolean isInfoEnabled=logger.isInfoEnabled();
		boolean isDebugEnabled=logger.isDebugEnabled();
		List<LsConfigChangeData> lsConfigChangeDataList=new ArrayList<LsConfigChangeData>();
		if(isInfoEnabled)
			logger.info("Inside DatabaseChangeListenerImpl onDatabaseChangeNotification()->DB change notification recieved");
		//fetching array of table changed events
		TableChangeDescription[] arrayTableChangeDescription=event.getTableChangeDescription();
		if(isDebugEnabled)
			logger.debug("number of tbl chang events"+arrayTableChangeDescription.length); 
		//fetching rows changed for each table
		for(TableChangeDescription tableChangeDescription: arrayTableChangeDescription){
			RowChangeDescription[] arrayRowChangeDescription=tableChangeDescription.getRowChangeDescription();
			if(isDebugEnabled)
				logger.debug("number of row changes"+arrayRowChangeDescription.length); 
			for(RowChangeDescription rowChangeDescription: arrayRowChangeDescription){
				LsConfigChangeData lsConfigChangeData=new LsConfigChangeData();
				lsConfigChangeData.setRowId(rowChangeDescription.getRowid().stringValue());
				lsConfigChangeData.setRowOperation(rowChangeDescription.getRowOperation());
				lsConfigChangeData.setTableName(tableChangeDescription.getTableName());
				lsConfigChangeDataList.add(lsConfigChangeData);
			}
		}
		if(isDebugEnabled)
			logger.debug("ls config change list created, Total number of change events:"+lsConfigChangeDataList.size()); 
		//calling telnet sshRA config data changed
		telnetSshResourceAdaptor.lsConfigurationChanged(lsConfigChangeDataList);
	}

}
