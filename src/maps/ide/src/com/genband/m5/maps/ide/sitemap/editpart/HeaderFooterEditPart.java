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
*     Package:  com.genband.m5.maps.ide.sitemap.editpart
*
*     File:     HeaderFooterEditPart.java
*
*     Desc:   	EditPart (Controller) for header and footer.
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
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
//import org.eclipse.gmf.runtime.diagram.ui.figures.ResizableCompartmentFigure;

import com.genband.m5.maps.ide.sitemap.editpolicy.ComponentDeleteEditPolicy;
import com.genband.m5.maps.ide.sitemap.editpolicy.ComponentSelectionEditPolicy;
import com.genband.m5.maps.ide.sitemap.editpolicy.HeaderFooterXYLayoutEditPolicy;
import com.genband.m5.maps.ide.sitemap.figure.BasicFigure;
import com.genband.m5.maps.ide.sitemap.figure.FooterFigure;
import com.genband.m5.maps.ide.sitemap.figure.HeaderFigure;
import com.genband.m5.maps.ide.sitemap.figure.PageFigure;
import com.genband.m5.maps.ide.sitemap.figure.SiteMapFigure2;
import com.genband.m5.maps.ide.sitemap.figure.SiteMapFigure3;
import com.genband.m5.maps.ide.sitemap.figure.SiteMapFigure5;
import com.genband.m5.maps.ide.sitemap.figure.TrailFigure;
import com.genband.m5.maps.ide.sitemap.figure.TrailTabFigure;
import com.genband.m5.maps.ide.sitemap.model.Footer;
import com.genband.m5.maps.ide.sitemap.model.Header;
import com.genband.m5.maps.ide.sitemap.model.HeaderFooter;
import com.genband.m5.maps.ide.sitemap.model.ModelElement;
import com.genband.m5.maps.ide.sitemap.model.PageShape;
import com.genband.m5.maps.ide.sitemap.model.PortletShape;
import com.genband.m5.maps.ide.sitemap.model.Shape;
import com.genband.m5.maps.ide.sitemap.util.Constants;

/**
 * EditPart used for Header or Footer instances.
 * This edit part must implement the PropertyChangeListener interface, 
 * so it can be notified of property changes in the corresponding model element.
 * 
 * @author Genband
 */
public class HeaderFooterEditPart extends AbstractGraphicalEditPart 
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
	installEditPolicy(EditPolicy.CONTAINER_ROLE, new HeaderFooterXYLayoutEditPolicy());
	
	//installEditPolicy(EditPolicy.LAYOUT_ROLE,  new Shapes1XYLayoutEditPolicy());
	installEditPolicy(EditPolicy.SELECTION_FEEDBACK_ROLE,  new ComponentSelectionEditPolicy());

	}
	
/*(non-Javadoc)
 * @see org.eclipse.gef.editparts.AbstractGraphicalEditPart#createFigure()
 */
protected IFigure createFigure() {
	IFigure f = createFigureForModel();
	f.setOpaque(true); // non-transparent figure
	return f;
}

/**
 * Return an IFigure depending on the instance of the current model element.
 * This allows this EditPart to be used for both subclasses of HeaderFooter. 
 */
private IFigure createFigureForModel() {
	if (getModel() instanceof Header) {
		Figure figure = new HeaderFigure();
		return figure;
	} else if (getModel() instanceof Footer) {
		Figure figure = new FooterFigure();
		return figure;
	} else {
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

private HeaderFooter getCastedModel() {
	return (HeaderFooter) getModel();
}

/** (non-Javadoc)
 * @see java.beans.PropertyChangeListener#propertyChange(java.beans.PropertyChangeEvent)
 */
public void propertyChange(PropertyChangeEvent evt) {
	String prop = evt.getPropertyName();
	if (Shape.SIZE_PROP.equals(prop) || Shape.LOCATION_PROP.equals(prop)) {
		refreshVisuals();
	}
	refreshVisuals();
}

protected void refreshVisuals() {
	int xmargin = 10 ;
	int ymargin = 20;
	int height = 30;
	System.out.println("HeaderFooterEditPart: in refreshVisuals");
	//Rectangle bounds = new Rectangle(getCastedModel().getLocation(),getCastedModel().getSize());
	Rectangle bounds = new Rectangle(50,50,50,50);
	Rectangle siteMapBounds = ((GraphicalEditPart)getParent()).getFigure().getBounds();
	if(getCastedModel() instanceof Header){
		//bounds = new Rectangle (x + xmargin,y+ ymargin,lx-2*xmargin,height);	
		bounds = new Rectangle (siteMapBounds.x + xmargin , siteMapBounds.y + ymargin ,
				siteMapBounds.width - 2*xmargin , siteMapBounds.height/20 ) ;	
	}else if (getCastedModel() instanceof Footer){
		//bounds = new Rectangle (20,500-30-30 ,580,30);
		//bounds = new Rectangle(x + xmargin, y+ ly -ymargin - height, lx-2*xmargin, height) ;	
		bounds = new Rectangle(siteMapBounds.x + xmargin , siteMapBounds.y + siteMapBounds.height - 2*ymargin ,
				siteMapBounds.width-2*xmargin , siteMapBounds.height/20 ) ;	

	}
	System.out.println("HeaderFooterEditPart: siteMapBounds: " + siteMapBounds);
	System.out.println("HeaderFooterEditPart: bounds: " + bounds);
	if ( siteMapBounds.height == 0 && siteMapBounds.width == 0 ){
		bounds = new Rectangle(20,20,0,0);
	}
	// notify parent container of changed position & location
	// if this line is removed, the XYLayoutManager used by the parent container 
	// (the Figure of the ShapesDiagramEditPart), will not know the bounds of this figure
	// and will not draw it correctly.
	System.out.println("getparent in refreshVisuals: " + getParent());
	((GraphicalEditPart) getParent()).setLayoutConstraint(this, getFigure(), bounds);
	
	
}
@Override
public void setSelected(int value) {
//	super.setSelected(value);
	if(SELECTED == value || SELECTED_PRIMARY == value){
		((BasicFigure)getFigure()).setState(Constants.SELECTED);
		getFigure().repaint();
	}else{
		((BasicFigure)getFigure()).setState(Constants.NORMAL);
		getFigure().repaint();
	}
}

public void performRequest(Request req) {
	System.out.print("action perform");
	super.performRequest(req);
	System.out.println("HeaderFooterEditPart: req: type : " + req.getType());
	System.out.println("HeaderFooterEditPart : req: class : " + req.getClass().getName());
	System.out.println("HeaderFooterEditPart : req: extendedData : " + req.getExtendedData());
	if(req.getType().equals(REQ_DIRECT_EDIT)){
		System.out.println("HeaderFooterEditPart: REQ_DIRECT_EDIT entered " );
		if(getFigure() instanceof HeaderFigure){
			HeaderFigure figure = (HeaderFigure) getFigure();
			figure.setState(Constants.SELECTED);
			figure.repaint();
		}
		if(getFigure() instanceof FooterFigure){
			FooterFigure figure = (FooterFigure) getFigure();
			figure.setState(Constants.SELECTED);
			figure.repaint();
		}
		//Color fgColor = ColorConstants.red;
		
		//figure.paint(f.graphics);
		//f.setFGColor(fgColor);
		//f.paintFigure(new SWTGraphics(new GC(new Shell())));
		System.out.println("HeaderFooterEditPart: REQ_DIRECT_EDIT exit \n\n" );
		//fireSelectionChanged();
	}
	if(req.getType().equals(REQ_OPEN)){
		System.out.println("HeaderFooterEditPart: REQ_DIRECT_EDIT entered " );
		if(getFigure() instanceof HeaderFigure){
			HeaderFigure figure = (HeaderFigure) getFigure();
			figure.setState(Constants.SELECTED);
			figure.repaint();
		}
		if(getFigure() instanceof FooterFigure){
			FooterFigure figure = (FooterFigure) getFigure();
			figure.setState(Constants.SELECTED);
			figure.repaint();
		}
		//Color fgColor = ColorConstants.red;
		
		//figure.paint(f.graphics);
		//f.setFGColor(fgColor);
		//f.paintFigure(new SWTGraphics(new GC(new Shell())));
		System.out.println("HeaderFooterEditPart: REQ_DIRECT_EDIT exit \n\n" );
		//fireSelectionChanged();
	}
}

}