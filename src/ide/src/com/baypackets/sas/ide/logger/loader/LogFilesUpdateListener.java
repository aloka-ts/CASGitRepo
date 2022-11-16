package com.baypackets.sas.ide.logger.loader;

import com.baypackets.sas.ide.logger.util.BoundedList;

/**
 * Listens to a LogFilesListener for an update to the file being watched.
 */
public interface LogFilesUpdateListener 
{
	/**
	 * Notification that an update has occurred in the file being watched.
	 * 
	 * @param list The most recent lines that have been updated in the file.
	 */
	public void update(BoundedList list);
}
