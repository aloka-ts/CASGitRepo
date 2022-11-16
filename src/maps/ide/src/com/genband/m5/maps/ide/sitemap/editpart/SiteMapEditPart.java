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
*     File:     SiteMapEditPart.java
*
*     Desc:   	Edit Part(Controller) for SiteMap.
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
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import org.eclipse.core.runtime.Assert;
import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.ConnectionLayer;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.FlowLayout;
import org.eclipse.draw2d.FreeformLayer;
import org.eclipse.draw2d.FreeformLayout;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.MarginBorder;
import org.eclipse.draw2d.RectangleFigure;
import org.eclipse.draw2d.SWTGraphics;
import org.eclipse.draw2d.ShortestPathConnectionRouter;
import org.eclipse.draw2d.geometry.Rectangle;

import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.GraphicalEditPart;
import org.eclipse.gef.LayerConstants;
import org.eclipse.gef.Request;
import org.eclipse.gef.RootEditPart;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editparts.AbstractGraphicalEditPart;
import org.eclipse.gef.editpolicies.RootComponentEditPolicy;
import org.eclipse.gef.editpolicies.XYLayoutEditPolicy;
import org.eclipse.gef.requests.ChangeBoundsRequest;
import org.eclipse.gef.requests.CreateRequest;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.widgets.Shell;


import com.genband.m5.maps.ide.sitemap.editpolicy.SiteMapCreateXYLayoutEditPolicy;
import com.genband.m5.maps.ide.sitemap.figure.BasicPageFigure;
import com.genband.m5.maps.ide.sitemap.figure.DisplayPageFigure;
import com.genband.m5.maps.ide.sitemap.figure.PageFigure;
import com.genband.m5.maps.ide.sitemap.figure.SiteMapFigure;
import com.genband.m5.maps.ide.sitemap.figure.SiteMapFigure5;
import com.genband.m5.maps.ide.sitemap.figure.SiteMapFigureOriginalF;
import com.genband.m5.maps.ide.sitemap.model.MainPage;
import com.genband.m5.maps.ide.sitemap.model.ModelElement;
import com.genband.m5.maps.ide.sitemap.model.Page;
import com.genband.m5.maps.ide.sitemap.model.PageShape;
import com.genband.m5.maps.ide.sitemap.model.PortletShape;
import com.genband.m5.maps.ide.sitemap.model.Shape;
import com.genband.m5.maps.ide.sitemap.model.SiteMap;
import com.genband.m5.maps.ide.sitemap.model.commands.PageCreateCommand;
import com.genband.m5.maps.ide.sitemap.model.commands.ShapeCreateCommand;
import com.genband.m5.maps.ide.sitemap.model.commands.ShapeSetConstraintCommand;
import com.genband.m5.maps.ide.sitemap.util.Constants;
//import com.genband.m5.maps.ide.sitemap.figure.SiteMapFigureOriginalF;

/**
 * EditPart for the a SiteMap instance.
 * This edit part server as the main SiteMap container, the white area where
 * everything else is in. Also responsible for the container's layout (the
 * way the container rearanges its contents) and the container's capabilities
 * (edit policies).
 * This edit part must implement the PropertyChangeListener interface, 
 * so it can be notified of property changes in the corresponding model element.
 * 
 * @author Genband
 */


class SiteMapEditPart extends AbstractGraphicalEditPart 
	implements PropertyChangeListener  {
	
	private Figure figure;
	private Rectangle bounds;
/**
 * Upon activation, attach to the model element as a property change listener.
 */
public void activate() {
	if (!isActive()) {
		super.activate();
		((ModelElement) getModel()).addPropertyChangeListener(this);
	}
}
protected void addChild(EditPart child, int index) {
	Assert.isNotNull(child);
	if (index == -1)
		index = getChildren().size();
	if (children == null)
		children = new ArrayList(2);

	children.add(index, child);
	child.setParent(this);
	addChildVisual(child, index);
	child.addNotify();

	if (isActive())
		child.activate();
	fireChildAdded(child, index);
}

/* (non-Javadoc)
 * @see org.eclipse.gef.editparts.AbstractEditPart#createEditPolicies()
 */
protected void createEditPolicies() {
	// disallows the removal of this edit part from its parent
	//installEditPolicy(EditPolicy.COMPONENT_ROLE, new RootComponentEditPolicy());
	installEditPolicy(EditPolicy.COMPONENT_ROLE, new RootComponentEditPolicy());
	// handles constraint changes (e.g. moving and/or resizing) of model elements
	// and creation of new model elements
	installEditPolicy(EditPolicy.LAYOUT_ROLE,  new SiteMapCreateXYLayoutEditPolicy());
	//installEditPolicy(EditPolicy.SELECTION_FEEDBACK_ROLE,  new ShapesEditPolicy());
}

/* (non-Javadoc)
 * @see org.eclipse.gef.editparts.AbstractGraphicalEditPart#createFigure()
 */
/*@Override
public IFigure getContentPane() {
	//super.getContentPane();
	SiteMapFigureOriginalF siteMapFigure = (SiteMapFigureOriginalF)getFigure();
	
	return siteMapFigure.getRect();
}*/
@Override
public IFigure getFigure() {
	// TODO Auto-generated method stub
	return super.getFigure();
}
protected IFigure createFigure() {
	//Figure f = new FreeformLayer();
	//f.setBorder(new MarginBorder(3));
	//f.setLayoutManager(new FreeformLayout());
	bounds = new Rectangle(10,10,600,500);
	//figure = new SiteMapFigureOriginalF(bounds);
	//figure = new SiteMapFigure(bounds);
	figure = new SiteMapFigure();
	//f.add(new RectangleFigure());
	//SiteMapFigure f = (SiteMapFigure)figure;
	//f.setFGColor(ColorConstants.gray);
	//figure.setBounds(bounds);
	//figure.setSize(600,500);
	bounds = figure.getBounds();
	System.out.println("SiteMapEditPart : createFigure() figure Bounds ......" +  bounds);
	figure.setLayoutManager(new FreeformLayout());
	//((GraphicalEditPart) getParent()).setLayoutConstraint(this, getFigure(), bounds);
	System.out.println("\n\nggggetParent() " + getParent());
	System.out.println("before setLayoutConstraint this : " + this + " figure: " + figure + " bounds " + figure.getBounds());
	
	//((GraphicalEditPart) getParent()).setLayoutConstraint(this, figure, bounds);
	//setLayoutConstraint(this, figure, bounds);
	//figure.setConstraint(figure, bounds);
	System.out.println("f.getbounds() " + figure.getBounds());
	// Create the static router for the connection layer
	//ConnectionLayer connLayer = (ConnectionLayer)getLayer(LayerConstants.CONNECTION_LAYER);
	//connLayer.setConnectionRouter(new ShortestPathConnectionRouter(f));
	
	return figure;
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

private SiteMap getCastedModel() {
	return (SiteMap) getModel();
}

/* (non-Javadoc)
 * @see org.eclipse.gef.editparts.AbstractEditPart#getModelChildren()
 */
protected List getModelChildren() {
	return getCastedModel().getChildren(); // return a list of components
}

/* (non-Javadoc)
 * @see java.beans.PropertyChangeListener#propertyChange(PropertyChangeEvent)
 */
public void propertyChange(PropertyChangeEvent evt) {
	String prop = evt.getPropertyName();
	// these properties are fired when Components are added into or removed from 
	// the Sitemap instance and must cause a call of refreshChildren()
	// to update the sitemap's contents.
	List childParts = getChildren();
	if (SiteMap.CHILD_ADDED_PROP.equals(prop)
			|| SiteMap.CHILD_REMOVED_PROP.equals(prop)) {
		refreshChildren();
	}
	if (SiteMap.SITEMAP_ROLES_PROP.equals(prop)) {
		//TODO Update page roles and icons accordingly
		String siteMapPreviousRolesString = (String)evt.getOldValue();
		String siteMapRolesString = (String)evt.getNewValue();
		
		List siteMapPreviousRoles = parseString(siteMapPreviousRolesString);
		List siteMapRoles = parseString(siteMapRolesString);
		List newAddedRolesToSitemap = new ArrayList();
		List removedRolesFromSitemap = new ArrayList();
		for(int j = 0 ; j < siteMapPreviousRoles.size() ; j++ ) {
			if ( false == siteMapRoles.contains(siteMapPreviousRoles.get(j)) ) {
				removedRolesFromSitemap.add(siteMapPreviousRoles.get(j));
			}
		}
		for(int j = 0 ; j < siteMapRoles.size() ; j++ ) {
			if ( false == siteMapPreviousRoles.contains(siteMapRoles.get(j)) ) {
				newAddedRolesToSitemap.add(siteMapRoles.get(j));
			}
		}
		
		String pageRolesString = null;
		String newPageRolesString = null;
		
		for( int i = 0 ; i < getChildren().size() ; i++ ) {
			if ( getChildren().get(i) instanceof PageEditPart ) {
				pageRolesString = ((PageEditPart)getChildren().get(i)).getCastedModel().getRoles();
				List pageRoles = parseString(pageRolesString);
				
				if ( Constants.REMOVE_ROLES_FROM_CHILDREN_ALSO == Constants.ROLES_PROPAGATE_STRATEGY_ON_REMOVAL_OF_ROLES ) {
					for(int j = 0 ; j < removedRolesFromSitemap.size() ; j++){
						if ( pageRoles.contains(removedRolesFromSitemap.get(j)) ) {
							pageRoles.remove(removedRolesFromSitemap.get(j));
						}
					}
				}
				//for ( int j = 0 ; j < newAddedRolesToSitemap.size() ; j++ ) {
					
				//}
				if(newAddedRolesToSitemap.size()>0){
					//icon of page can be changed here or it will be changed in PageEditPart
		 			if ( Constants.ADD_NEW_ROLES_ADDED_TO_CHILDREN == Constants.ROLES_PROPAGATE_STRATEGY ) {
		 				for ( int k = 0 ; k < newAddedRolesToSitemap.size() ; k++ ){
			 				if ( false == pageRoles.contains(newAddedRolesToSitemap.get(k)) ) {
			 					pageRoles.add(newAddedRolesToSitemap.get(k));
			 				}
		 				}
		 			}
				}
				newPageRolesString = makeString(pageRoles);
				((PageEditPart)getChildren().get(i)).getCastedModel().setPropertyValue(Page.PAGE_ROLES, newPageRolesString);
				PropertyChangeEvent newEvent = new PropertyChangeEvent("pagee",Page.PAGE_ROLES,null,null);
				
				((PageEditPart)getChildren().get(i)).propertyChange(newEvent);
				
			}
		}
		
		refreshChildren();
	}
	if (SiteMap.HEADER_ADDED_PROP.equals(prop)
			|| SiteMap.HEADER_REMOVED_PROP.equals(prop)) {
		refreshChildren();
		
	}
	if (SiteMap.FOOTER_ADDED_PROP.equals(prop)
			|| SiteMap.FOOTER_REMOVED_PROP.equals(prop)) {
		refreshChildren();
	}
	if ( SiteMap.PAGE_ADDED_PROP.equals(prop) ) {
		//List children = getChildren();
		//handleChildChange(evt);
		refreshChildren();
		/*if(evt.getNewValue() instanceof MainPage && ((MainPage)evt.getNewValue()).isDummy()){
			for(int i = 0 ; i < getChildren().size() ; i++ ) {
				if(getChildren().get(i) instanceof PageEditPart && ((PageEditPart)getChildren().get(i)).getCastedModel().isDummy()){
					((PageEditPart)getChildren().get(i)).setSelected(Constants.SELECTED);
				}
			}
		}
		refreshChildren();
		*/
	}
	if ( SiteMap.PAGE_REMOVED_PROP.equals(prop) ) {
		System.out.println("In SiteMapEditPart:  PAGE_REMOVED_PROP noticed ");
		MainPage removedPage = (MainPage) evt.getNewValue();
		int pageNoOfRemovedPage = removedPage.getPageNo();
		System.out.println("Removed page no. is: " + pageNoOfRemovedPage);
		Page page ;
		refreshChildren();
		childParts = getChildren();
		BasicPageFigure pFigure = null ;
		//setting the page no. of remaining pages properly.
		for(int i = 0 ; i< childParts.size() ; i++){
			if(childParts.get(i) instanceof PageEditPart){
				page = ((PageEditPart)childParts.get(i)).getCastedModel();
				pFigure = ((PageEditPart)childParts.get(i)).getCastedFigure();
				if ( page.getPageNo() > pageNoOfRemovedPage ) {
				//if ( (false == page.isDummy()) && page.getPageNo() > pageNoOfRemovedPage ) {
					page.setPageNo(page.getPageNo()-1);
					pFigure.setPageNo(page.getPageNo());
				}
			}
		}
		
		int pageNoOfPageToBeSelected = Constants.INVALID;
		int noOfPagesBeforeDeletion = getCastedModel().getNoOfPages() +1 ;
		if(pageNoOfRemovedPage <  noOfPagesBeforeDeletion ){
			pageNoOfPageToBeSelected = pageNoOfRemovedPage ;
		}else if(pageNoOfRemovedPage == noOfPagesBeforeDeletion){
			if(1 == pageNoOfRemovedPage){
				pageNoOfPageToBeSelected = Constants.INVALID;
			}else{
				pageNoOfPageToBeSelected = pageNoOfRemovedPage -1;
			}
		}else{
			System.out.println("Something wrong with deletion of page.");
		}
		/*List children = getFigure().getChildren();
		for ( int i = 0 ; i < children.size() ; i++ ){
			if(children.get(i) instanceof PageFigure 
					&& pageNoOfPageToBeSelected == ((PageFigure)children.get(i)).getPageNo()){
				((PageFigure)children.get(i)).setState(Constants.SELECTED);
				
			}
		}*/
		System.out.println("page to be selected is : " + pageNoOfPageToBeSelected);
		for ( int i = 0 ; i < childParts.size() ; i++ ){
			if(childParts.get(i) instanceof PageEditPart 
					&&  (false == ((MainPage)((PageEditPart)childParts.get(i)).getModel()).isDummy())
					&& (pageNoOfPageToBeSelected == ((Page)((PageEditPart)childParts.get(i)).getModel()).getPageNo())){
				((PageEditPart)childParts.get(i)).setRemoved(true);
				((PageEditPart)childParts.get(i)).setRemovedPage(removedPage);
				((PageEditPart)childParts.get(i)).setSelected(SELECTED);
				((PageEditPart)childParts.get(i)).setRemoved(false);
				
			}else if ( childParts.get(i) instanceof PageEditPart 
					&&  ((MainPage)((PageEditPart)childParts.get(i)).getModel()).isDummy() ){
				((DisplayPageFigure)((PageEditPart)childParts.get(i)).getFigure()).setPageNo(pageNoOfPageToBeSelected);
				
			}
		}
		System.out.println("In SiteMapEditPart:  PAGE_REMOVED_PROP notification complete ");
		refreshChildren();
		
	}
	if(SiteMap.SITEMAP_LAYOUT_PROP.equals(prop)){
		System.out.println("layout changed");
		refreshChildren();
	}
	//getFigure().repaint();
}

public void performRequest(Request req) {
	System.out.print("action perform");
	super.performRequest(req);
	System.out.println("SiteMapEditPart: req: type : " + req.getType());
	System.out.println("SiteMapEditPart: req: class : " + req.getClass().getName());
	System.out.println("SiteMapEditPart: req: extendedData : " + req.getExtendedData());
	if(req.getType().equals(REQ_DIRECT_EDIT)){
		System.out.println("SiteMapEditPart: REQ_DIRECT_EDIT entered " );
		SiteMapFigure5 f= (SiteMapFigure5)figure;
		Color fgColor = ColorConstants.red;
		
		//figure.paint(f.graphics);
		//f.setFGColor(fgColor);
		//f.paintFigure(new SWTGraphics(new GC(new Shell())));
		System.out.println("SiteMapEditPart: REQ_DIRECT_EDIT exit \n\n" );
		//fireSelectionChanged();
	}
}
@Override
public void setSelected(int value) {
//	super.setSelected(value);
	if(SELECTED == value || SELECTED_PRIMARY == value){
		((SiteMapFigure)getFigure()).setState(Constants.SELECTED);
		getFigure().repaint();
	}else{
		((SiteMapFigure)getFigure()).setState(Constants.NORMAL);
		getFigure().repaint();
	}
}
/**
 * @see RootEditPart#setContents(EditPart)
 */
/*public void setContents(EditPart editpart) {
	if (contents == editpart)
		return;
	if (contents != null)
		removeChild(contents);
	contents = editpart;
	if (contents != null)
		addChild(contents, 0);
}
*/
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
