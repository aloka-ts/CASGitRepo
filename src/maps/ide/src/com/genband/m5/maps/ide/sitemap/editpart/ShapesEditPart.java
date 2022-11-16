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
*     Project:  CPFSupport
*
*     Package:  com.genband.m5.maps.ide.sitemap.editPart
*
*     File:     ShapesEditPart.java
*
*     Desc:   	EditPart (Controller) for shapes.
*
*     Author    Date                Description
*    ---------------------------------------------------------
*     Genband   December 28, 2007   Initial Creation
*
**********************************************************************
**/

package com.genband.m5.maps.ide.sitemap.editpart;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;

import org.eclipse.draw2d.ChopboxAnchor;
import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.ConnectionAnchor;
import org.eclipse.draw2d.Ellipse;
import org.eclipse.draw2d.EllipseAnchor;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.LightweightSystem;
import org.eclipse.draw2d.RectangleFigure;
import org.eclipse.draw2d.geometry.Rectangle;

import org.eclipse.gef.ConnectionEditPart;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.GraphicalEditPart;
import org.eclipse.gef.NodeEditPart;
import org.eclipse.gef.Request;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editparts.AbstractGraphicalEditPart;
import org.eclipse.gef.editpolicies.GraphicalNodeEditPolicy;
import org.eclipse.gef.requests.CreateConnectionRequest;
import org.eclipse.gef.requests.ReconnectRequest;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
//import org.eclipse.gmf.runtime.diagram.ui.figures.ResizableCompartmentFigure;

import com.genband.m5.maps.ide.sitemap.editpolicy.ComponentDeleteEditPolicy;
import com.genband.m5.maps.ide.sitemap.editpolicy.ComponentSelectionEditPolicy;
import com.genband.m5.maps.ide.sitemap.figure.FooterFigure;
import com.genband.m5.maps.ide.sitemap.figure.HeaderFigure;
import com.genband.m5.maps.ide.sitemap.figure.PlaceHolderFigure;
import com.genband.m5.maps.ide.sitemap.figure.SiteMapFigure2;
import com.genband.m5.maps.ide.sitemap.figure.SiteMapFigure3;
import com.genband.m5.maps.ide.sitemap.figure.TrailFigure;
import com.genband.m5.maps.ide.sitemap.figure.TrailTabFigure;
import com.genband.m5.maps.ide.sitemap.model.Footer;
import com.genband.m5.maps.ide.sitemap.model.Header;
import com.genband.m5.maps.ide.sitemap.model.ModelElement;
import com.genband.m5.maps.ide.sitemap.model.PageShape;
import com.genband.m5.maps.ide.sitemap.model.PortletShape;
import com.genband.m5.maps.ide.sitemap.model.Shape;

/**
 * EditPart used for Shape instances (more specific for PortletShape and
 * PageShape instances).
 * This edit part must implement the PropertyChangeListener interface, 
 * so it can be notified of property changes in the corresponding model element.
 * 
 * 
 * @author Genband
 */
class ShapesEditPart extends AbstractGraphicalEditPart 
	implements PropertyChangeListener {
	

/**
 * Upon activation, attach to the model element as a property change listener.
 */
public void activate() {
	if (!isActive()) {
		super.activate();
		((ModelElement) getModel()).addPropertyChangeListener(this);
	}
}

/* (non-Javadoc)
 * @see org.eclipse.gef.editparts.AbstractEditPart#createEditPolicies()
 */
protected void createEditPolicies() {
	// allow removal of the associated model element
	installEditPolicy(EditPolicy.COMPONENT_ROLE, new ComponentDeleteEditPolicy());
	//installEditPolicy(EditPolicy.LAYOUT_ROLE,  new Shapes1XYLayoutEditPolicy());
	installEditPolicy(EditPolicy.SELECTION_FEEDBACK_ROLE,  new ComponentSelectionEditPolicy());

	}
	
/*(non-Javadoc)
 * @see org.eclipse.gef.editparts.AbstractGraphicalEditPart#createFigure()
 */
protected IFigure createFigure() {
	IFigure f = createFigureForModel();
	f.setOpaque(true); // non-transparent figure
	//f.setBackgroundColor(ColorConstants.green);
	return f;
	
}
/*public IFigure createFigure() {
	Shell shell = new Shell(Display.getCurrent());
	final TabFolder tabFolder = new TabFolder (shell, SWT.BORDER);
	LightweightSystem lws = new LightweightSystem(shell);
	for (int i=0; i<6; i++) {
	TabItem item = new TabItem (tabFolder, SWT.NULL);
	item.setText ("TabItem " + i);
	}
	MyTabFolder result = new MyTabFolder();
	result.setFolder(tabFolder);
	//ResizableCompartmentFigure figure = (ResizableCompartmentFigure) super.createFigure();
	IFigure figure = new Figure();
	//figure.setTitleVisibility(false);
	lws.setContents(result);
	figure.add(result);
	figure.setOpaque(true); // non-transparent figure
	return figure;
	}
*/

/**
 * Return an IFigure depending on the instance of the current model element.
 * This allows this EditPart to be used for both sublasses of Shape. 
 */
private IFigure createFigureForModel() {
	if (getModel() instanceof PortletShape) {
		//return new Ellipse();
		System.out.println("vandana : parent edit part: " + getParent());
		
		//return new SiteMapFigure3(new Rectangle(100,100,200,200));
		return new SiteMapFigure3();
		//return new PlaceHolderFigure();

	} else if (getModel() instanceof PageShape) {
		//return new RectangleFigure();
		//return new TabPaneFigure();
		//return new MyTabFolder1();
		//return new MyFigure1();
		//return new TrailFigure(new Rectangle(100,100,100,100));
		return new SiteMapFigure2(new Rectangle(0,0,0,0));
		
	} else {
		// if Shapes gets extended the conditions above must be updated
		throw new IllegalArgumentException();
	}
}

/**
 * Upon deactivation, detach from the model element as a property change listener.
 */
public void deactivate() {
	if (isActive()) {
		super.deactivate();
		((ModelElement) getModel()).removePropertyChangeListener(this);
	}
}

private Shape getCastedModel() {
	return (Shape) getModel();
}

/** (non-Javadoc)
 * @see java.beans.PropertyChangeListener#propertyChange(java.beans.PropertyChangeEvent)
 */
public void propertyChange(PropertyChangeEvent evt) {
	String prop = evt.getPropertyName();
	if (Shape.SIZE_PROP.equals(prop) || Shape.LOCATION_PROP.equals(prop)) {
		refreshVisuals();
	}
}

protected void refreshVisuals() {
	int x,y,lx,ly;
	x = 10 ;
	y = 10 ;
	lx = 600 ;
	ly = 500 ;
	int xmargin = 10 ;
	int ymargin = 20;
	int height = 30;
	System.out.println("in refreshVisuals");
	Rectangle bounds = new Rectangle(getCastedModel().getLocation(),
			getCastedModel().getSize());
	System.out.println("getparent in refreshVisuals: " + getParent());
	// notify parent container of changed position & location
	// if this line is removed, the XYLayoutManager used by the parent container 
	// (the Figure of the ShapesDiagramEditPart), will not know the bounds of this figure
	// and will not draw it correctly.
	((GraphicalEditPart) getParent()).setLayoutConstraint(this, getFigure(), bounds);
	
	
}
@Override
public void setSelected(int value) {
	// TODO Auto-generated method stub
//	super.setSelected(value);
}

public void performRequest(Request req) {
	// TODO Auto-generated method stub
	System.out.print("action perform");
	super.performRequest(req);
	System.out.println("ShapesEditPart : req: type : " + req.getType());
	System.out.println("ShapesEditPart : req: class : " + req.getClass().getName());
	System.out.println("ShapesEditPart : req: extendedData : " + req.getExtendedData());

}

}