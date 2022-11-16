/*******************************************************************************
 *   Copyright (c) 2014 Agnity, Inc. All rights reserved.
 *   
 *   This is proprietary source code of Agnity, Inc. 
 *   
 *   Agnity, Inc. retains all intellectual property rights associated 
 *   with this source code. Use is subject to license terms.
 *   
 *   This source code contains trade secrets owned by Agnity, Inc.
 *   Confidentiality of this computer program must be maintained at 
 *   all times, unless explicitly authorized by Agnity, Inc.
 *******************************************************************************/
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
