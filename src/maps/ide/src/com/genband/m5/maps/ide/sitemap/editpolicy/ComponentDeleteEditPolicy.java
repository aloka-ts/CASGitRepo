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
*     File:     ComponentDeleteEditPolicy.java
*
*     Desc:   	This is the edit policy to allow deletion of page.	
*     
*     Author    Date                Description
*    ---------------------------------------------------------
*     Genband   December 28, 2007   Initial Creation
*
**********************************************************************
**/

package com.genband.m5.maps.ide.sitemap.editpolicy;

import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editpolicies.ComponentEditPolicy;
import org.eclipse.gef.requests.GroupRequest;

import com.genband.m5.maps.ide.CPFPlugin;
import com.genband.m5.maps.ide.sitemap.editpart.HeaderFooterEditPart;
import com.genband.m5.maps.ide.sitemap.model.MainPage;
import com.genband.m5.maps.ide.sitemap.model.PlaceHolder;
import com.genband.m5.maps.ide.sitemap.model.Portlet;
import com.genband.m5.maps.ide.sitemap.model.Shape;
import com.genband.m5.maps.ide.sitemap.model.SiteMap;
import com.genband.m5.maps.ide.sitemap.model.SubPage;
import com.genband.m5.maps.ide.sitemap.model.commands.PageDeleteCommand;
import com.genband.m5.maps.ide.sitemap.model.commands.PortletDeleteCommand;
import com.genband.m5.maps.ide.sitemap.model.commands.ShapeDeleteCommand;
import com.genband.m5.maps.ide.sitemap.model.commands.SubPageDeleteCommand;

/**
 * This edit policy enables the removal of some component instance from its container. 
 * @see HeaderFooterEditPart#createEditPolicies()
 * @author Genband
 */
public class ComponentDeleteEditPolicy extends ComponentEditPolicy {

/** (non-Javadoc)
 * @see org.eclipse.gef.editpolicies.ComponentEditPolicy#createDeleteCommand(org.eclipse.gef.requests.GroupRequest)
 */
protected Command createDeleteCommand(GroupRequest deleteRequest) {
	CPFPlugin.getDefault().log("gethost()" + getHost());
	CPFPlugin.getDefault().log("gethost().getparent()" + getHost().getParent());
	//CPFPlugin.getDefault().log("");
	if ( null == getHost() || null == getHost().getParent()){
		return null;
	}
	Object parent = getHost().getParent().getModel();
	Object child = getHost().getModel();
	if (parent instanceof SiteMap && child instanceof Shape) {
		return new ShapeDeleteCommand((SiteMap) parent, (Shape) child);
	}
	if (parent instanceof SiteMap && child instanceof MainPage) {
		/*
		 * This is to restrict deletion of last page.
		 * If don't want to restrict just remove if else conditions
		 * and return the pageDeleteCommand as returned in else.
		 * Most probably everything will work fine then.
		 * I have done the desired checking if last page is deleted,
		 * where it was required.
		 */
			if(((SiteMap)parent).getNoOfPages() == 1){
			return null;
		}
		else{
			return new PageDeleteCommand((SiteMap) parent, (MainPage) child);
		}
	}if(parent instanceof MainPage && child instanceof SubPage){
		return new SubPageDeleteCommand((MainPage) parent, (SubPage) child);
	}
	if (parent instanceof PlaceHolder && child instanceof Portlet) {
		return new PortletDeleteCommand((PlaceHolder) parent, (Portlet) child);
	}
	
	return super.createDeleteCommand(deleteRequest);
}

}