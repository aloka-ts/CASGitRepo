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
package com.baypackets.sas.ide.logger.filters;

import org.eclipse.swt.custom.LineStyleEvent;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

/**
 * A Filter specifies how to deal with a line that has been "matched" by one of the 
 * regular expressions attached to a Watcher.
 */
public interface FilterAction
{
	public void doViewerAction(LineStyleEvent event);
	
	public String getDescription();
	
	public void dispose();
	
	public String doWatcherAction(String line, boolean firstMatch);
	
	public void toXML(Document doc, Node node);
}
