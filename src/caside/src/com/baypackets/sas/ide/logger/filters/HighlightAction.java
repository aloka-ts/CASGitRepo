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
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.graphics.Color;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import com.baypackets.sas.ide.logger.util.XmlUtils;

/**
 * Highlights a line in the LogWatcher editor view. 
 */
public class HighlightAction implements FilterAction
{
	private Color m_color = null;

	public HighlightAction(Color c)
	{
		m_color = c;
	}
	
	public void doViewerAction(LineStyleEvent event)
	{
		StyleRange range = new StyleRange(event.lineOffset, event.lineText.length(), m_color, null);
		event.styles = new StyleRange[1];
		event.styles[0] = range;
	}

	public String getDescription()
	{
		return "Highlight line";
	}

	public Color getColor()
	{
		return m_color;
	}

	public void setColor(Color color)
	{
		m_color = color;
	}

	public void dispose()
	{
		m_color.dispose();
	}

	public String doWatcherAction(String line, boolean firstMatch)
	{
		return line;
	}

	public void toXML(Document doc, Node node)
    {
        Element action = doc.createElement("action");
        action.setAttribute("type", "highlight");
        action.appendChild(XmlUtils.createElementWithText(doc, "red", Integer.toString(getColor().getRGB().red)));
        action.appendChild(XmlUtils.createElementWithText(doc, "green", Integer.toString(getColor().getRGB().green)));
        action.appendChild(XmlUtils.createElementWithText(doc, "blue", Integer.toString(getColor().getRGB().blue)));
        node.appendChild(action);
    }
}
