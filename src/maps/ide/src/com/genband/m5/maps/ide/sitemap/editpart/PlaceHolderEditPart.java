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
*     File:     PlaceHolderEditPart.java
*
*     Desc:   	EditPart (Controller) for PlaceHolder.
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
import org.eclipse.draw2d.FreeformLayout;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.ToolbarLayout;
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
import com.genband.m5.maps.ide.sitemap.figure.DisplaySubPageFigure;
import com.genband.m5.maps.ide.sitemap.figure.PlaceHolderFigure;
import com.genband.m5.maps.ide.sitemap.figure.PortletFigure;
import com.genband.m5.maps.ide.sitemap.figure.SiteMapFigure3;
import com.genband.m5.maps.ide.sitemap.figure.SubPageFigure;
import com.genband.m5.maps.ide.sitemap.model.ModelElement;
import com.genband.m5.maps.ide.sitemap.model.Page;
import com.genband.m5.maps.ide.sitemap.model.PlaceHolder;
import com.genband.m5.maps.ide.sitemap.model.Portlet;
import com.genband.m5.maps.ide.sitemap.model.Shape;
import com.genband.m5.maps.ide.sitemap.util.Constants;

/**
 * EditPart used for PlaceHolder instances.
 * This edit part must implement the PropertyChangeListener interface, 
 * so it can be notified of property changes in the corresponding model element.
 * 
 * @author Genband
 */
public class PlaceHolderEditPart extends AbstractGraphicalEditPart 
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
	installEditPolicy(EditPolicy.CONTAINER_ROLE, new PlaceHolderXYLayoutEditPolicy());
	installEditPolicy(EditPolicy.SELECTION_FEEDBACK_ROLE,  new ComponentSelectionEditPolicy());

}

/**(non-Javadoc)
 * @see org.eclipse.gef.editparts.AbstractGraphicalEditPart#createFigure()
 */
/*
 * Return an IFigure for placeholder. 
 */
protected IFigure createFigure() {
	PlaceHolderFigure figure = null;
	if (getModel() instanceof PlaceHolder) {
		figure = new PlaceHolderFigure();
		PlaceHolder model = getCastedModel();
		figure.setPlaceHolderNo(model.getPlaceHolderNo());
		figure.setLayout(model.getLayout());
		System.out.println("IT placeholder figure placeholder no. : " + figure.getPlaceHolderNo());
		System.out.println("IT placeholder figure placeholder name : " + figure.getPlaceHolderName());
		System.out.println("IT placeholder figure layout : " + model.getLayout());
		//return new SiteMapFigure3();
		figure.setLayoutManager(new FreeformLayout());
		
		/*
		ToolbarLayout layout = new ToolbarLayout();
		layout.setVertical(true);
		layout.setStretchMinorAxis(true);
		figure.setLayoutManager(layout);
		*/
		return figure;
	
		//figure.setBounds(new Rectangle(0,0,100,100));
		//((PlaceHolderFigure)figure).setPlaceHolderName(((PlaceHolder)getModel()).getName());
	}else {
		throw new IllegalArgumentException();
	}
	//figure.setOpaque(true); // non-transparent figure
	//return figure;
}

/** (non-Javadoc)
* @see org.eclipse.gef.editparts.AbstractEditPart#getModelChildren()
*/
protected List getModelChildren() {
	return getCastedModel().getChildren(); // return a list of components
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

private PlaceHolder getCastedModel() {
	return (PlaceHolder) getModel();
}

/** (non-Javadoc)
 * @see java.beans.PropertyChangeListener#propertyChange(java.beans.PropertyChangeEvent)
 */
public void propertyChange(PropertyChangeEvent evt) {
	String prop = evt.getPropertyName();
	if ( PlaceHolder.PLACEHOLDER_NAME.equals(prop)) {
		//if ( getFigure() instanceof PlaceHolderFigure ) {
		//	((PlaceHolderFigure)getFigure()).setPlaceHolderName(((PlaceHolder)getModel()).getName());
		//}
		//refreshVisuals();
	}else if ( PlaceHolder.PLACEHOLDER_ROLES.equals(prop)) {
		//TODO Update portlet roles and icons accordingly
		String placeHolderPreviousRolesString = (String)evt.getOldValue();
		String placeHolderRolesString = (String)evt.getNewValue();
		
		List placeHolderPreviousRoles = parseString(placeHolderPreviousRolesString);
		List placeHolderRoles = parseString(placeHolderRolesString);
		List newAddedRolesToPlaceHolder = new ArrayList();
		List removedRolesFromPlaceHolder = new ArrayList();
		for(int j = 0 ; j < placeHolderPreviousRoles.size() ; j++ ) {
			if ( false == placeHolderRoles.contains(placeHolderPreviousRoles.get(j)) ) {
				removedRolesFromPlaceHolder.add(placeHolderPreviousRoles.get(j));
			}
		}
		for(int j = 0 ; j < placeHolderRoles.size() ; j++ ) {
			if ( false == placeHolderPreviousRoles.contains(placeHolderRoles.get(j)) ) {
				newAddedRolesToPlaceHolder.add(placeHolderRoles.get(j));
			}
		}
		
		String portletRolesString = null;
		String newPortletRolesString = null;
		
		for( int i = 0 ; i < getChildren().size() ; i++ ) {
			if ( getChildren().get(i) instanceof PortletEditPart ) {
				portletRolesString = ((PortletEditPart)getChildren().get(i)).getCastedModel().getRoles();
				List portletRoles = parseString(portletRolesString);

				if ( Constants.REMOVE_ROLES_FROM_CHILDREN_ALSO == Constants.ROLES_PROPAGATE_STRATEGY_ON_REMOVAL_OF_ROLES ) {
					for(int j = 0 ; j < removedRolesFromPlaceHolder.size() ; j++){
						if ( portletRoles.contains(removedRolesFromPlaceHolder.get(j)) ) {
							portletRoles.remove(removedRolesFromPlaceHolder.get(j));
						}
					}
				}
				//for ( int j = 0 ; j < newAddedRolesToSitemap.size() ; j++ ) {
					
				//}
				if(newAddedRolesToPlaceHolder.size()>0){
					//icon of page can be changed here or it will be changed in PageEditPart
		 			if ( Constants.ADD_NEW_ROLES_ADDED_TO_CHILDREN == Constants.ROLES_PROPAGATE_STRATEGY ) {
		 				for ( int k = 0 ; k < newAddedRolesToPlaceHolder.size() ; k++ ){
			 				if ( false == portletRoles.contains(newAddedRolesToPlaceHolder.get(k)) ) {
			 					portletRoles.add(newAddedRolesToPlaceHolder.get(k));
			 				}
		 				}
		 			}
				}
				newPortletRolesString = makeString(portletRoles);
				((PortletEditPart)getChildren().get(i)).getCastedModel().setPropertyValue(Portlet.PORTLET_ROLES, newPortletRolesString);
			}
		}
		

		
		refreshChildren();
	}else if ( PlaceHolder.PLACEHOLDER_LAYOUT.equals(prop)) {
		//if ( getFigure() instanceof PlaceHolderFigure ) {
		//	((PlaceHolderFigure)getFigure()).setPlaceHolderName(((PlaceHolder)getModel()).getName());
		//}
		PlaceHolderFigure figure = (PlaceHolderFigure)getFigure();
		PlaceHolder placeHolder = getCastedModel();
		figure.setLayout(placeHolder.getLayout());
		figure.setPlaceHolderNo(placeHolder.getPlaceHolderNo());
		figure.repaint();
		//refreshVisuals();
	}else if ( PlaceHolder.PORTLET_ADDED_PROP.equals(prop)) {
		
		refreshChildren();
	}else if ( PlaceHolder.PORTLET_REMOVED_PROP.equals(prop)) {
		Portlet removedPortlet = (Portlet)evt.getNewValue();
		List siblingsOfRemovedPortlet = getChildren();
		for( int i = 0 ; i < siblingsOfRemovedPortlet.size() ; i++ ) {
			if ( siblingsOfRemovedPortlet.get(i) instanceof PortletEditPart 
					&& ((PortletEditPart)siblingsOfRemovedPortlet.get(i)).getCastedModel().getPortletNo()>removedPortlet.getPortletNo()) {
				Portlet portlet = ((PortletEditPart)siblingsOfRemovedPortlet.get(i)).getCastedModel();
				portlet.setPortletNo(portlet.getPortletNo()-1);
				((PortletEditPart)siblingsOfRemovedPortlet.get(i)).getCastedFigure().setPortletNo(portlet.getPortletNo());
			}
		}
		refreshChildren();
	}
	//refreshVisuals();
}

protected void refreshVisuals() {
	System.out.println("PlaceHolderEditPart: in refreshVisuals");
	Rectangle parentBounds = ((GraphicalEditPart)getParent()).getFigure().getBounds();
	Rectangle bounds = new Rectangle(20,20,0,0);
	if(((GraphicalEditPart)getParent()).getFigure() instanceof DisplayPageFigure){
		bounds = new Rectangle(0,0,50,50);
	}if(((GraphicalEditPart)getParent()).getFigure() instanceof SubPageFigure){
		bounds = new Rectangle(0,0,0,0);
	}if(((GraphicalEditPart)getParent()).getFigure() instanceof DisplaySubPageFigure){
		bounds = new Rectangle(0,0,50,50);
	}
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
	if ( null == stringArray ||stringArray.size() == 0 ) {
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