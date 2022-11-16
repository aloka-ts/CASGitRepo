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
package com.baypackets.sas.ide.logger.util;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Helper functions for dealing with XML documents
 */
public class XmlUtils
{
    public static Element createElementWithText(Document doc, String tagName, String text)
	{
	    Element elem = doc.createElement(tagName);
	    elem.appendChild(doc.createTextNode(text));
	    return elem;
	}
    
    public static Document createDocument() throws ParserConfigurationException
    {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
  
        DocumentBuilder builder = factory.newDocumentBuilder();
        return builder.newDocument();
    }
}