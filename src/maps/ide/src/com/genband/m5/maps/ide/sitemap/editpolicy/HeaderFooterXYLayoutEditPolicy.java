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
*     File:     HeaderFooterXYLayoutEditPolicy.java
*
*     Desc:   	This is the edit policy for header and footer.
*     			It actually disallows page etc.(each component) to
*     			be added to header or footer.
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
import com.genband.m5.maps.ide.sitemap.model.PageShape;
import com.genband.m5.maps.ide.sitemap.model.PortletShape;
import com.genband.m5.maps.ide.sitemap.model.Shape;
import com.genband.m5.maps.ide.sitemap.model.SiteMap;
import com.genband.m5.maps.ide.sitemap.model.commands.ShapeCreateCommand;
import com.genband.m5.maps.ide.sitemap.model.commands.ShapeSetConstraintCommand;

public class HeaderFooterXYLayoutEditPolicy extends XYLayoutEditPolicy {
	
	/* (non-Javadoc)
	 * @see ConstrainedLayoutEditPolicy#createChangeConstraintCommand(ChangeBoundsRequest, EditPart, Object)
	 */
	protected Command createChangeConstraintCommand(ChangeBoundsRequest request,
			EditPart child, Object constraint) {
	/*	if (child instanceof HeaderFooterEditPart && constraint instanceof Rectangle) {
			// return a command that can move and/or resize a Shape
			return new ShapeSetConstraintCommand(
					(Shape) child.getModel(), request, (Rectangle) constraint);
		}
		return super.createChangeConstraintCommand(request, child, constraint);
	*/
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
		System.out.println("HeaderFooterXYLayoutEditPolicy: getCreateCommand entered");
		System.out.println("request: getSize " +  request.getSize());
		System.out.println("request: getLocation " +  request.getLocation());
		System.out.println("request: getNewObject " +  request.getNewObject());
		System.out.println("request: type : " +  request.getType());
		System.out.println("request: getConstraintFor(request) : ");
		System.out.println("request: getExtendedData().size: " + request.getExtendedData().size());
		System.out.println("request: getExtendedData: " + request.getExtendedData());
				
		//System.out.println("request: getConstraintFor(request) : " +  getConstraintFor(request));
		System.out.println("request: getConstraintFor(request) : ");
		
		Object childClass = request.getNewObjectType();
		//if (childClass == PortletShape.class || childClass == PageShape.class) {
		
		/*if (childClass == PageShape.class) {
			System.out.println("getHost(): " + getHost().getClass().getName());
			// return a command that can add a Shape to a SiteMap 
			System.out.println("request: getConstraintFor(request) : in if condition ");
			return new ShapeCreateCommand((Shape)request.getNewObject(), 
					(SiteMap)getHost().getModel(), new Rectangle(100,100,100,100));
		//	return new ShapeCreateCommand((Shape)request.getNewObject(), 
		//			(SiteMap)getHost().getModel(), getHostFigure().getBounds() );
		}*/
		return null;
	}
	
}
