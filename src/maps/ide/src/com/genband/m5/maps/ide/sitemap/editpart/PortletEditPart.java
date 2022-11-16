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
*     File:     PortletEditPart.java
*
*     Desc:   	EditPart (Controller) for Portlet.
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
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Rectangle;

import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.GraphicalEditPart;
import org.eclipse.gef.Request;
import org.eclipse.gef.editparts.AbstractGraphicalEditPart;

import sun.security.action.GetBooleanAction;

import com.genband.m5.maps.ide.sitemap.editpolicy.ComponentDeleteEditPolicy;
import com.genband.m5.maps.ide.sitemap.editpolicy.ComponentSelectionEditPolicy;
import com.genband.m5.maps.ide.sitemap.editpolicy.PlaceHolderXYLayoutEditPolicy;
import com.genband.m5.maps.ide.sitemap.figure.BasicFigure;
import com.genband.m5.maps.ide.sitemap.figure.DisplayPageFigure;
import com.genband.m5.maps.ide.sitemap.figure.PlaceHolderFigure;
import com.genband.m5.maps.ide.sitemap.figure.PortletFigure;
import com.genband.m5.maps.ide.sitemap.figure.SiteMapFigure3;
import com.genband.m5.maps.ide.sitemap.model.ModelElement;
import com.genband.m5.maps.ide.sitemap.model.PlaceHolder;
import com.genband.m5.maps.ide.sitemap.model.Portlet;
import com.genband.m5.maps.ide.sitemap.model.Shape;
import com.genband.m5.maps.ide.sitemap.util.Constants;

/**
 * EditPart used for Portlet instances.
 * This edit part must implement the PropertyChangeListener interface, 
 * so it can be notified of property changes in the corresponding model element.
 * 
 * @author Genband
 */
public class PortletEditPart extends AbstractGraphicalEditPart 
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
	//installEditPolicy(EditPolicy.CONTAINER_ROLE, new PlaceHolderXYLayoutEditPolicy());
	installEditPolicy(EditPolicy.SELECTION_FEEDBACK_ROLE,  new ComponentSelectionEditPolicy());

}

/**(non-Javadoc)
 * @see org.eclipse.gef.editparts.AbstractGraphicalEditPart#createFigure()
 */
/*
 * Return an IFigure for placeholder. 
 */
protected IFigure createFigure() {
	PortletFigure figure = null;
	if (getModel() instanceof Portlet) {
		figure = new PortletFigure();
		Portlet model = (Portlet)getCastedModel();
		//figure.setPlaceHolderNo(model.getPlaceHolderNo());
		//figure.setLayout(model.getLayout());
		//return new SiteMapFigure3();
		figure.setPortletNo(getCastedModel().getPortletNo());
		figure.setIconType(getCastedModel().getIconType());
		figure.setPortletName(getCastedModel().getName());
		
		return figure;
	
		//figure.setBounds(new Rectangle(0,0,100,100));
		//((PlaceHolderFigure)figure).setPlaceHolderName(((PlaceHolder)getModel()).getName());
	}else {
		throw new IllegalArgumentException();
	}
	//figure.setOpaque(true); // non-transparent figure
	//return figure;
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

public Portlet getCastedModel() {
	return (Portlet) getModel();
}

public PortletFigure getCastedFigure() {
	return (PortletFigure) getFigure();
}

/** (non-Javadoc)
 * @see java.beans.PropertyChangeListener#propertyChange(java.beans.PropertyChangeEvent)
 */
public void propertyChange(PropertyChangeEvent evt) {
	String prop = evt.getPropertyName();
	if ( Portlet.PORTLET_NAME.equals(prop)) {
		((PortletFigure)getFigure()).setPortletName(getCastedModel().getName());
		((PortletFigure)getFigure()).setPortletNo(getCastedModel().getPortletNo());
		refreshVisuals();
	} else if (Portlet.PORTLET_ROLES.equals(prop)) {
		if(Constants.NORMAL_ICON == decideIconType(((ModelElement)(getParent().getModel())).getRoles(),getCastedModel().getRoles())){
			getCastedModel().setIconType(Constants.NORMAL_ICON);
			getCastedFigure().setIconType(Constants.NORMAL_ICON);
		}else if(Constants.WARNING_ICON == decideIconType(((ModelElement)(getParent().getModel())).getRoles(),getCastedModel().getRoles())){
			getCastedModel().setIconType(Constants.WARNING_ICON);
			getCastedFigure().setIconType(Constants.WARNING_ICON);
		}else if(Constants.ERROR_ICON == decideIconType(((ModelElement)(getParent().getModel())).getRoles(),getCastedModel().getRoles())){
			getCastedModel().setIconType(Constants.ERROR_ICON);
			getCastedFigure().setIconType(Constants.ERROR_ICON);
		}
		refresh();
		
	}
	///TODO Roles change??
	//refreshVisuals();
}

protected void refreshVisuals() {
	System.out.println("PlaceHolderEditPart: in refreshVisuals");
	Rectangle parentBounds = ((GraphicalEditPart)getParent()).getFigure().getBounds();
	Rectangle bounds = new Rectangle(100,100,400,400);
	
	System.out.println("sitemapbounds: " + parentBounds);
	//bounds = new Rectangle(0,0,siteMapBounds.width,siteMapBounds.height);
	System.out.println("bounds: " + bounds);
	//bounds = siteMapBounds;
	//bounds = new Rectangle(19,19,2,2);
		//}
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
	System.out.println("setSelected called");
	if(SELECTED == value || SELECTED_PRIMARY == value){
		((BasicFigure)getFigure()).setState(Constants.SELECTED);
		getFigure().repaint();
		
	}else{
		((BasicFigure)getFigure()).setState(Constants.NORMAL);
		getFigure().repaint();
	}
	System.out.println("setSelected exiting");
}

public void performRequest(Request req) {
	System.out.print("action perform");
	super.performRequest(req);
	System.out.println("PlaceHolderEditPart: req: type : " + req.getType());
	System.out.println("PlaceHolderEditPart : req: class : " + req.getClass().getName());
	System.out.println("PlaceHolderEditPart : req: extendedData : " + req.getExtendedData());
	if(req.getType().equals(REQ_DIRECT_EDIT)){
		System.out.println("PlaceHolderEditPart: REQ_DIRECT_EDIT entered " );
		BasicFigure figure = (BasicFigure) getFigure();
		figure.setState(Constants.SELECTED);
		figure.repaint();
		//Color fgColor = ColorConstants.red;
		
		//figure.paint(f.graphics);
		//f.setFGColor(fgColor);
		//f.paintFigure(new SWTGraphics(new GC(new Shell())));
		System.out.println("PlaceHolderEditPart: REQ_DIRECT_EDIT exit \n\n" );
		//fireSelectionChanged();
	}
	if(req.getType().equals(REQ_OPEN)){
		System.out.println("PlaceHolderEditPart: REQ_DIRECT_EDIT entered " );
		BasicFigure figure = (BasicFigure) getFigure();
		figure.setState(Constants.SELECTED);
		figure.repaint();
		//Color fgColor = ColorConstants.red;
		
		//figure.paint(f.graphics);
		//f.setFGColor(fgColor);
		//f.paintFigure(new SWTGraphics(new GC(new Shell())));
		System.out.println("PlaceHolderEditPart: REQ_DIRECT_EDIT exit \n\n" );
		//fireSelectionChanged();
	}
}
private int decideIconType(String parentRoles , String roles){
	 List parentRolesList = parseString(parentRoles);
	 List rolesList = parseString(roles);
	 boolean warning = false ;
	 //if(rolesList.containsAll(parentRolesList)){
	 if ( rolesList.containsAll(parentRolesList) && parentRolesList.containsAll(rolesList)) {
		 return Constants.NORMAL_ICON;	 
	 }else {
		 for ( int i = 0 ; i < rolesList.size() ; i++ ) { 
			 if ( parentRolesList.contains(rolesList.get(i)) ) {
				 warning = true;
			 }
		 }
		 if(warning){
			 return Constants.WARNING_ICON;
		 }else{
			 return Constants.ERROR_ICON;
		 }
	 }
	 /*if(parentRoles.equals(roles)){
		 return Constants.NORMAL_ICON;
	 }else {
		 return Constants.WARNING_ICON;
	 }*/
}
/**
 * Parses the single String representation of the list into 
 * list items.
 */
private List parseString(String stringList) {
	ArrayList v = new ArrayList();
	if (stringList != null) {
		StringTokenizer st = new StringTokenizer(stringList, ","); //$NON-NLS-1$

		while (st.hasMoreElements()) {
			v.add(st.nextElement());
		}
	}
	return v;
	//return (String[]) v.toArray(new String[v.size()]);
}


private String makeString(List stringArray) {
	String str = "" ;
	if (null == stringArray || stringArray.size() == 0 ) {
		return "";
	}
	for( int i = 0 ; i < stringArray.size()-1 ; i++ ){
		str = str.concat((String)stringArray.get(i));
		str = str.concat(",");
	}
	str = str.concat((String)stringArray.get(stringArray.size()-1));
	return str;
}

}