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
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * Ignores (skips) a line in the LogWatcher editor view.
 */
public class IgnoreAction implements FilterAction
{
    public IgnoreAction()
    {
        super();
    }

    public void dispose()
    {
    }

    public void doViewerAction(LineStyleEvent event)
    {
    	return;
    }

    public String getDescription()
    {
        return "Don't show the line";
    }
    
    public String doWatcherAction(String line, boolean firstMatch)
    {
    	return null;	
    }
    
    public void toXML(Document doc, Node node)
    {
        Element action = doc.createElement("action");
        action.setAttribute("type", "ignore");
        node.appendChild(action);
    }
}
