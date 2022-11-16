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
*     Package:  com.genband.m5.maps.ide.sitemap.editpolicy
*
*     File:     SiteMapComponentEditPolicy.java
*
*     Desc:   	This is the edit policy to allow deletion of components from SiteMap.	
*     
*     Author    Date                Description
*    ---------------------------------------------------------
*     Genband   December 28, 2007   Initial Creation
*
**********************************************************************
**/
package com.genband.m5.maps.ide.sitemap.editpolicy ;

import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editpolicies.ComponentEditPolicy;
import org.eclipse.gef.requests.GroupRequest;

import com.genband.m5.maps.ide.sitemap.editpart.HeaderFooterEditPart;
import com.genband.m5.maps.ide.sitemap.editpart.ShapeTreeEditPart;
import com.genband.m5.maps.ide.sitemap.model.MainPage;
import com.genband.m5.maps.ide.sitemap.model.Page;
import com.genband.m5.maps.ide.sitemap.model.SiteMap;
import com.genband.m5.maps.ide.sitemap.model.commands.PageDeleteCommand;

/**
 * This edit policy enables the removal of instance from its container. 
 * @see HeaderFooterEditPart#createEditPolicies()
 * @see ShapeTreeEditPart#createEditPolicies()
 * @author Genband
 */
public class SiteMapComponentEditPolicy extends ComponentEditPolicy {

/* (non-Javadoc)
 * @see org.eclipse.gef.editpolicies.ComponentEditPolicy#createDeleteCommand(org.eclipse.gef.requests.GroupRequest)
 */
protected Command createDeleteCommand(GroupRequest deleteRequest) {
	Object parent = getHost().getParent().getModel();
	Object child = getHost().getModel();
	if (parent instanceof SiteMap && child instanceof Page) {
		return new PageDeleteCommand((SiteMap) parent, (MainPage) child);
	}
	//return super.createDeleteCommand(deleteRequest);
	
	System.out.println("SiteMapComponentEditPolicy: getHost()" + getHost());
	System.out.println("SiteMapComponentEditPolicy: gethost().getParent()" + getHost().getParent());
	return null;
}
}