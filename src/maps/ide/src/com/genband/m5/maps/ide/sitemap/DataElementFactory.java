/**********************************************************************
*	 GENBAND, Inc. Confidential and Proprietary
*
* This work contains valuable confidential and proprietary 
* information.
* Disclosure, use or reproduction without the written authorization of
* GENBAND, Inc. is prohibited.  This unpublished work by GENBAND, Inc.
* is protected by the laws of the United States and other countries.
* If publication of the work should occur the following notice shall 
* apply:
* 
* "Copyright 2007 GENBAND, Inc.  All rights reserved."
**********************************************************************
**/


/**********************************************************************
*
*     Project:  MAPS
*
*     Package:  com.genband.m5.maps.ide.sitemap
*
*     File:     DataElementFactory.java
*
*     Desc:   	Factory for creating instances of new objects from a palette
*
*     Author    Date                Description
*    ---------------------------------------------------------
*     Genband   December 28, 2007   Initial Creation
*
**********************************************************************
**/

package com.genband.m5.maps.ide.sitemap;

import org.eclipse.gef.requests.CreationFactory;

import com.genband.m5.maps.ide.model.CPFPortlet;
import com.genband.m5.maps.ide.sitemap.model.Portlet;
import com.genband.m5.maps.ide.sitemap.util.Constants;

/**
 * Factory for creating instances of new objects from a palette
 * @author Genband
 */
public class DataElementFactory implements CreationFactory
{

	private Object template;
	private int iconType = Constants.NORMAL;
	private String portletName = "PortletNme";
	private String toolTip = "tooltip";
	private CPFPortlet cpfPortlet;
	/**
	 * Creates a new FlowElementFactory with the given template object
	 * 
	 * @param o
	 *            the template
	 */
	public DataElementFactory(Object o)
	{
		template = o;
		if(o instanceof Portlet){
			Portlet p = (Portlet)o;
			portletName = p.getName();
			iconType = p.getIconType();
			toolTip = p.getToolTip();
			cpfPortlet = p.getCpfPortlet();
		}
	}

	/**
	 * @see org.eclipse.gef.requests.CreationFactory#getNewObject()
	 */
	@SuppressWarnings("unchecked")
	public Object getNewObject()
	{
		try
		{
			if ( template instanceof Portlet) {
				Portlet p = new Portlet();
				p.setName(portletName);
				p.setIconType(iconType);
				p.setToolTip(toolTip);
				p.setCpfPortlet(cpfPortlet);
				return p;
			}
			return ((Class) template).newInstance();
		}
		catch (Exception e)
		{
			return null;
		}
	}

	/**
	 * @see org.eclipse.gef.requests.CreationFactory#getObjectType()
	 */
	public Object getObjectType()
	{
		if(template instanceof Portlet){
			return Portlet.class;
		}
		return template;
	}

}