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
*     File:     PlaceHolderXYLayoutEditPolicy.java
*
*     Desc:   	This is the edit policy for PlaceHolder.
*     			This basically defines which components are allowed 
*     			to be added to a PlaceHolder.			
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

import com.genband.m5.maps.ide.sitemap.model.PlaceHolder;
import com.genband.m5.maps.ide.sitemap.model.Portlet;
import com.genband.m5.maps.ide.sitemap.model.PortletShape;
import com.genband.m5.maps.ide.sitemap.model.Shape;
import com.genband.m5.maps.ide.sitemap.model.SiteMap;
import com.genband.m5.maps.ide.sitemap.model.commands.PortletCreateCommand;
import com.genband.m5.maps.ide.sitemap.model.commands.ShapeCreateCommand;

public class PlaceHolderXYLayoutEditPolicy extends XYLayoutEditPolicy {
	
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
		System.out.println("PlaceHolderXYLayoutEditPolicy: getCreateCommand entered");
		Object childClass = request.getNewObjectType();
		
		if (childClass == PortletShape.class) {
			System.out.println("getHost(): " + getHost().getClass().getName());
			// return a command that can add a portlet to a Placeholder 
			System.out.println("request: getConstraintFor(request) : in if condition ");
			return new ShapeCreateCommand((Shape)request.getNewObject(), 
					(SiteMap)getHost().getModel(), new Rectangle(100,100,100,100));
		//	return new ShapeCreateCommand((Shape)request.getNewObject(), 
		//			(SiteMap)getHost().getModel(), getHostFigure().getBounds() );
		}if (childClass == Portlet.class) {
			System.out.println("getHost(): " + getHost().getClass().getName());
			// return a command that can add a portlet to a Placeholder 
			System.out.println("request: getConstraintFor(request) : in if condition ");
			return new PortletCreateCommand((Portlet)request.getNewObject(), 
					(PlaceHolder)getHost().getModel(), getHostFigure().getBounds());
		//	return new ShapeCreateCommand((Shape)request.getNewObject(), 
		//			(SiteMap)getHost().getModel(), getHostFigure().getBounds() );
		}
		return null;
	}
	
}
