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
*     File:     SubPageXYLayoutEditPolicy.java
*
*     Desc:   	This is the edit policy for subPage.
*     			This basically defines which components are allowed 
*     			to be added to a subpage.			
*     
*     Author    Date                Description
*    ---------------------------------------------------------
*     Genband   December 28, 2007   Initial Creation
*
**********************************************************************
**/

package com.genband.m5.maps.ide.sitemap.editpolicy ;

import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editpolicies.XYLayoutEditPolicy;
import org.eclipse.gef.requests.ChangeBoundsRequest;
import org.eclipse.gef.requests.CreateRequest;

import com.genband.m5.maps.ide.sitemap.editpart.HeaderFooterEditPart;
import com.genband.m5.maps.ide.sitemap.model.Page;
import com.genband.m5.maps.ide.sitemap.model.PageShape;
import com.genband.m5.maps.ide.sitemap.model.PlaceHolder;
import com.genband.m5.maps.ide.sitemap.model.Portlet;
import com.genband.m5.maps.ide.sitemap.model.PortletShape;
import com.genband.m5.maps.ide.sitemap.model.Shape;
import com.genband.m5.maps.ide.sitemap.model.SiteMap;
import com.genband.m5.maps.ide.sitemap.model.commands.PortletCreateCommand;
import com.genband.m5.maps.ide.sitemap.model.commands.ShapeCreateCommand;
import com.genband.m5.maps.ide.sitemap.model.commands.ShapeSetConstraintCommand;

public class SubPageXYLayoutEditPolicy extends XYLayoutEditPolicy {
	
	/* (non-Javadoc)
	 * @see ConstrainedLayoutEditPolicy#createChangeConstraintCommand(ChangeBoundsRequest, EditPart, Object)
	 */
	protected Command createChangeConstraintCommand(ChangeBoundsRequest request,
			EditPart child, Object constraint) {
		return null;
	}
	
	/* (non-Javadoc)
	 * @see ConstrainedLayoutEditPolicy#createChangeConstraintCommand(EditPart, Object)
	 */
	protected Command createChangeConstraintCommand(EditPart child,
			Object constraint) {
		return null;
	}
	
	/* (non-Javadoc)
	 * @see LayoutEditPolicy#getCreateCommand(CreateRequest)
	 */
	protected Command getCreateCommand(CreateRequest request) {
		System.out.println("SubPageXYLayoutEditPolicy: getCreateCommand entered");
		return null;
	}
	
}
