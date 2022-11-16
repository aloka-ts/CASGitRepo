package com.genband.m5.maps.ide.sitemap.editpolicy ;

import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editpolicies.XYLayoutEditPolicy;
import org.eclipse.gef.requests.ChangeBoundsRequest;
import org.eclipse.gef.requests.CreateRequest;

import com.genband.m5.maps.ide.sitemap.editpart.HeaderFooterEditPart;
import com.genband.m5.maps.ide.sitemap.model.PageShape;
import com.genband.m5.maps.ide.sitemap.model.Shape;
import com.genband.m5.maps.ide.sitemap.model.SiteMap;
import com.genband.m5.maps.ide.sitemap.model.commands.ShapeCreateCommand;
import com.genband.m5.maps.ide.sitemap.model.commands.ShapeSetConstraintCommand;

public class Shapes1XYLayoutEditPolicy extends XYLayoutEditPolicy {
	
	/* (non-Javadoc)
	 * @see ConstrainedLayoutEditPolicy#createChangeConstraintCommand(ChangeBoundsRequest, EditPart, Object)
	 */
	protected Command createChangeConstraintCommand(ChangeBoundsRequest request,
			EditPart child, Object constraint) {
		if (child instanceof HeaderFooterEditPart && constraint instanceof Rectangle) {
			// return a command that can move and/or resize a Shape
			return new ShapeSetConstraintCommand(
					(Shape) child.getModel(), request, (Rectangle) constraint);
		}
		return super.createChangeConstraintCommand(request, child, constraint);
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
		System.out.println("Shapes1XYLayoutEditPolicy: getCreateCommand entered");
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
		
		if (childClass == PageShape.class) {
			System.out.println("getHost(): " + getHost().getClass().getName());
			// return a command that can add a Shape to a SiteMap 
			System.out.println("request: getConstraintFor(request) : in if condition ");
			return new ShapeCreateCommand((Shape)request.getNewObject(), 
					(SiteMap)getHost().getModel(), new Rectangle(100,100,100,100));
		//	return new ShapeCreateCommand((Shape)request.getNewObject(), 
		//			(SiteMap)getHost().getModel(), getHostFigure().getBounds() );
		}
		return null;
	}
	
}
