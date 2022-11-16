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
*     File:     SiteMapCreateXYLayoutEditPolicy.java
*
*     Desc:   	This is the edit policy for Site Map.
*     			It actually allows page etc.(different components) to
*     			be added to SiteMap.
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
import com.genband.m5.maps.ide.sitemap.model.MainPage;
import com.genband.m5.maps.ide.sitemap.model.Portlet;
import com.genband.m5.maps.ide.sitemap.model.PortletShape;
import com.genband.m5.maps.ide.sitemap.model.Shape;
import com.genband.m5.maps.ide.sitemap.model.SiteMap;
import com.genband.m5.maps.ide.sitemap.model.commands.PageCreateCommand;
import com.genband.m5.maps.ide.sitemap.model.commands.ShapeCreateCommand;

	/**
	 * EditPolicy for the Figure used by Site Map.
	 * Children of XYLayoutEditPolicy can be used in Figures with XYLayout.
	 * @author Genband
	 */
	public class SiteMapCreateXYLayoutEditPolicy extends XYLayoutEditPolicy {

		/* (non-Javadoc)
		 * @see ConstrainedLayoutEditPolicy#createChangeConstraintCommand(ChangeBoundsRequest, EditPart, Object)
		 */
		protected Command createChangeConstraintCommand(ChangeBoundsRequest request,
				EditPart child, Object constraint) {
			if (child instanceof HeaderFooterEditPart && constraint instanceof Rectangle) {
				// return a command that can move and/or resize a Shape
				//return new ShapeSetConstraintCommand(
				//		(Shape) child.getModel(), request, (Rectangle) constraint);
				return null;
			}
			//return super.createChangeConstraintCommand(request, child, constraint);
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
			System.out.println("SiteMapEditPart: getCreateCommand");
			System.out.println("request: getSize " +  request.getSize());
			System.out.println("request: getLocation " +  request.getLocation());
			System.out.println("request: getNewObject " +  request.getNewObject());
			System.out.println("request: type : " +  request.getType());
			System.out.println("request: getExtendedData: " + request.getExtendedData());
			//System.out.println("request: getConstraintFor(request) : " +  getConstraintFor(request));
			System.out.println("request: getConstraintFor(request) : ");
			Rectangle parentBounds = getHostFigure().getBounds();
			Rectangle bounds = new Rectangle (parentBounds.x+100,parentBounds.y+300,parentBounds.width/2,parentBounds.height/2);
			Object childClass = request.getNewObjectType();
			
			if (childClass == MainPage.class) {
					System.out.println("request: getConstraintFor(request) : in if condition ");
					PageCreateCommand command = new PageCreateCommand((MainPage)request.getNewObject(), 
							(SiteMap)getHost().getModel(), bounds );
					
					return command;
			}if (childClass == PortletShape.class) {
				System.out.println("request: getConstraintFor(request) : in if condition ");
				return new ShapeCreateCommand((Shape)request.getNewObject(), 
						(SiteMap)getHost().getModel(), new Rectangle(100,100,100,100));
			}if (childClass == Portlet.class) {
				System.out.println("getHost(): " + getHost().getClass().getName());
				// return a command that can add a portlet to a Placeholder 
				System.out.println("request: getConstraintFor(request) : in if condition ");
				//return new PortletCreateCommand((Portlet)request.getNewObject(), 
				//		(PlaceHolder)getHost().getModel(), new Rectangle(100,100,100,100));
			//	return new ShapeCreateCommand((Shape)request.getNewObject(), 
			//			(SiteMap)getHost().getModel(), getHostFigure().getBounds() );
			}
			
			return null;
		}
		
	}

