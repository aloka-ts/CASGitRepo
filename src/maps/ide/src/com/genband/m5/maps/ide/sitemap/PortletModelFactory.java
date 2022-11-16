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
*     File:     PortletModelFactory.java
*
*     Desc:   	Factory for creating instances of portlet from a palette
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

public class PortletModelFactory implements CreationFactory {

		private Class type;
		private String name;
		private String toolTip;
		private int icon_type;
		private CPFPortlet cpfPortlet;
		/**
		 * Creates a PortletModelFactory.
		 *
		 * @param aClass The class to be instantiated using this factory.
		 */
		public PortletModelFactory(Class aClass) {
			type = aClass;
		}
		public PortletModelFactory(Class aClass, String name) {
			type = aClass;
			this.name = name;
		}
		public PortletModelFactory(Class aClass, String name , String toolTip) {
			type = aClass;
			this.name = name;
			this.toolTip = toolTip;
		}
		public PortletModelFactory(Class aClass, String name , String toolTip, int icon_type) {
			type = aClass;
			this.name = name;
			this.toolTip = toolTip;
			this.icon_type = icon_type;
		}
		public PortletModelFactory(Class aClass, String name , String toolTip, int icon_type, CPFPortlet cpfPortlet) {
			type = aClass;
			this.name = name;
			this.toolTip = toolTip;
			this.icon_type = icon_type;
			this.cpfPortlet = cpfPortlet;
		}
		/**
		 * Create the new object.
		 *
		 * @return The newly created object.
		 */
		public Object getNewObject() {
			try {
				Portlet portlet = (Portlet) type.newInstance();
				portlet.setName(name);
				portlet.setToolTip(toolTip);
				portlet.setIconType(icon_type);
				portlet.setCpfPortlet(cpfPortlet);
				return portlet;
			} catch (Exception exc) {
				return null;
			}
		}

		/**
		 * Returns the type of object this factory creates.
		 *
		 * @return The type of object this factory creates.
		 */
		public Object getObjectType() {
			return type;
		}


		}

