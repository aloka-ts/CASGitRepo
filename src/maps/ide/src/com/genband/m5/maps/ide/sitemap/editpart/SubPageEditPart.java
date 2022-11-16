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
*     File:     SubPageEditPart.java
*
*     Desc:   	EditPart (Controller) for SubPage.
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
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.FreeformLayout;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Rectangle;

import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.GraphicalEditPart;
import org.eclipse.gef.Request;
import org.eclipse.gef.editparts.AbstractGraphicalEditPart;

import sun.net.www.content.text.plain;

import com.genband.m5.maps.ide.sitemap.editpolicy.ComponentDeleteEditPolicy;
import com.genband.m5.maps.ide.sitemap.editpolicy.ComponentSelectionEditPolicy;
import com.genband.m5.maps.ide.sitemap.editpolicy.PageXYLayoutEditPolicy;
import com.genband.m5.maps.ide.sitemap.editpolicy.SubPageXYLayoutEditPolicy;
import com.genband.m5.maps.ide.sitemap.figure.BasicFigure;
import com.genband.m5.maps.ide.sitemap.figure.BasicPageFigure;
import com.genband.m5.maps.ide.sitemap.figure.DisplayPageFigure;
import com.genband.m5.maps.ide.sitemap.figure.DisplaySubPageFigure;
import com.genband.m5.maps.ide.sitemap.figure.PageFigure;
import com.genband.m5.maps.ide.sitemap.figure.SubPageFigure;
import com.genband.m5.maps.ide.sitemap.model.MainPage;
import com.genband.m5.maps.ide.sitemap.model.ModelElement;
import com.genband.m5.maps.ide.sitemap.model.Page;
import com.genband.m5.maps.ide.sitemap.model.PlaceHolder;
import com.genband.m5.maps.ide.sitemap.model.Portlet;
import com.genband.m5.maps.ide.sitemap.model.Shape;
import com.genband.m5.maps.ide.sitemap.model.SiteMap;
import com.genband.m5.maps.ide.sitemap.model.SubPage;
import com.genband.m5.maps.ide.sitemap.util.Constants;

/**
 * EditPart used for SubPage instances.
 * This edit part must implement the PropertyChangeListener interface, 
 * so it can be notified of property changes in the corresponding model element.
 * 
 * @author Genband
 */
public class SubPageEditPart extends AbstractGraphicalEditPart 
	implements PropertyChangeListener {
	
boolean removed = false;
SubPage removedSubPage = null;
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
 * @see org.eclipse.gef.editparts.AbstractEditPart#getModelChildren()
 */
public List getModelChildren() {
	return getCastedModel().getChildren(); // return a list of components
}

/* (non-Javadoc)
 * @see org.eclipse.gef.editparts.AbstractEditPart#createEditPolicies()
 */
protected void createEditPolicies() {
	// allow removal of the associated model element
	installEditPolicy(EditPolicy.COMPONENT_ROLE, new ComponentDeleteEditPolicy());
	//installEditPolicy(EditPolicy.LAYOUT_ROLE,  new Shapes1XYLayoutEditPolicy());
	installEditPolicy(EditPolicy.CONTAINER_ROLE, new SubPageXYLayoutEditPolicy());
	installEditPolicy(EditPolicy.SELECTION_FEEDBACK_ROLE,  new ComponentSelectionEditPolicy());

	}

/**(non-Javadoc)
 * @see org.eclipse.gef.editparts.AbstractGraphicalEditPart#createFigure()
 */
/*
 * Return an IFigure for subPage. 
 * If the subPage is not dummy then this will return not actually the whole tab figure.
 * It will return figure which is basically the tab's upper portion(similar to 
 * button) only.
 * If subPage is dummy then it will return figure which is actually the tab's content
 * holder portion.This is the figure which will show contents of current subPage.
 */
protected IFigure createFigure() {
	System.out.println("SubPageEditPart: create figure called");
	BasicPageFigure figure = null;
	if ( getModel() instanceof SubPage	
			&& false == getCastedModel().isDummy() ) {
		figure = new SubPageFigure();
		((SubPageFigure)figure).setParentPageNo(getCastedModel().getParentPageNo());
	}else if ( getModel() instanceof SubPage	
			&& true == getCastedModel().isDummy() ) {
		figure = new DisplaySubPageFigure();
		((DisplaySubPageFigure)figure).setParentPageNo(getCastedModel().getParentPageNo());
	}else {
		throw new IllegalArgumentException();
	}
	figure.setIconType(getCastedModel().getIconType());
	figure.setPageName(getCastedModel().getName());
	figure.setPageNo(getCastedModel().getPageNo());
	figure.setLayoutManager(new FreeformLayout());
	figure.setOpaque(true); // non-transparent figure
	//figure.paint(new Graphics());
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

public SubPage getCastedModel() {
	return (SubPage) getModel();
}

public BasicPageFigure getCastedFigure() {
	return (BasicPageFigure) getFigure();
}

/** (non-Javadoc)
 * @see java.beans.PropertyChangeListener#propertyChange(java.beans.PropertyChangeEvent)
 */
public void propertyChange(PropertyChangeEvent evt) {
	String prop = evt.getPropertyName();
	if ( Page.PAGE_NAME.equals(prop)) {
		getCastedFigure().setPageName(getCastedModel().getName());
		if ( false == getCastedModel().isDummy() ) {
			SubPageEditPart displayPagePart = getDisplaySubPagePart();
			displayPagePart.getCastedModel().setName(getCastedModel().getName());
		}else if ( true == getCastedModel().isDummy() ) {
			PageEditPart associatedPagePart = getDataPagePart(getCastedModel().getPageNo());
			associatedPagePart.getCastedModel().setName(getCastedModel().getName());
			associatedPagePart.getCastedFigure().setPageName(getCastedModel().getName());
		}
		refreshVisuals();
	}	if ( Page.PAGE_THEME.equals(prop)) {
		
		if ( false == getCastedModel().isDummy() ) {
			SubPageEditPart displayPagePart = getDisplaySubPagePart();
			displayPagePart.getCastedModel().setTheme(getCastedModel().getTheme());
		}else if ( true == getCastedModel().isDummy() ) {
			PageEditPart associatedPagePart = getDataPagePart(getCastedModel().getPageNo());
			associatedPagePart.getCastedModel().setTheme(getCastedModel().getTheme());
		}
		refreshVisuals();
	}else if ( Page.PAGE_ROLES.equals(prop)) {
		
		//TODO roles
		/*
		 * change icon for subPage accordingly
		 * Also change the iconType of display subPage. We don't need it.
		 * But when we copy the data of displaySubPage we overwrite the data
		 * So we keep display subPage up to date.
		 * The other solution was not to copy the iconType from display subPage
		 * to data subPage. This was also a good solution but I opted for the previous
		 * solution just to make code more structured, understandable and maintainable.
		 * Otherwise, I know that I have *deliberately* not copied iconType from displaySubPage
		 * to data subPage.But the other person might think that I have forgotten to copy this
		 * info(iconType).Tomorrow I might also forget the same.
		 * This was the case when user clicks on the tab (data subPage) and changes the roles
		 * 
		 * TODO 2
		 * Now the second case is : User clicks on the displaySubPage and changes the roles
		 * TODO
		 * change the roles of displaySubPage , as well as the subPage of which it is displaying
		 * the content.
		 */
		//Case 1 : data subPage's roles have changed
		if(false == getCastedModel().isDummy()){
			SubPageEditPart subPageDisplayPart = null;
			subPageDisplayPart = getDisplaySubPagePart();
			if ( null != subPageDisplayPart ){
				//subPageDisplayPart.getCastedModel().setRoles(getCastedModel().getRoles());
				subPageDisplayPart.getCastedModel().setPropertyValue(Page.PAGE_ROLES, getCastedModel().getRoles());
				subPageDisplayPart.propertyChange(evt);
			}
			if(Constants.NORMAL_ICON == decideIconType(((ModelElement)(getParent().getModel())).getRoles(),getCastedModel().getRoles())){
				getCastedModel().setIconType(Constants.NORMAL_ICON);
				getCastedFigure().setIconType(Constants.NORMAL_ICON);
				if ( null != subPageDisplayPart ){
					subPageDisplayPart.getCastedModel().setIconType(Constants.NORMAL_ICON);
					subPageDisplayPart.getCastedFigure().setIconType(Constants.NORMAL_ICON);
				}
			}else if(Constants.WARNING_ICON == decideIconType(((ModelElement)(getParent().getModel())).getRoles(),getCastedModel().getRoles())){
				getCastedModel().setIconType(Constants.WARNING_ICON);
				getCastedFigure().setIconType(Constants.WARNING_ICON);
				if ( null != subPageDisplayPart ){
					subPageDisplayPart.getCastedModel().setIconType(Constants.WARNING_ICON);
					subPageDisplayPart.getCastedFigure().setIconType(Constants.WARNING_ICON);
				}
			}else if(Constants.ERROR_ICON == decideIconType(((ModelElement)(getParent().getModel())).getRoles(),getCastedModel().getRoles())){
				getCastedModel().setIconType(Constants.ERROR_ICON);
				getCastedFigure().setIconType(Constants.ERROR_ICON);
				if ( null != subPageDisplayPart ){
					subPageDisplayPart.getCastedModel().setIconType(Constants.ERROR_ICON);
					subPageDisplayPart.getCastedFigure().setIconType(Constants.ERROR_ICON);
				}
			}
			
		}
		//case 2 : roles of display subPage are changed
		else if (true == getCastedModel().isDummy()){
			SubPageEditPart associatedSubPagePart = getDataSubPagePart(getCastedModel().getPageNo());
			//SubPage associatedSubPage = associatedSubPagePart.getCastedModel();
			associatedSubPagePart.getCastedModel().setRoles(getCastedModel().getRoles());
			if(Constants.NORMAL_ICON == decideIconType(((ModelElement)(getParent().getModel())).getRoles(),getCastedModel().getRoles())){
				getCastedModel().setIconType(Constants.NORMAL_ICON);
				getCastedFigure().setIconType(Constants.NORMAL_ICON);
				associatedSubPagePart.getCastedModel().setIconType(Constants.NORMAL_ICON);
				associatedSubPagePart.getCastedFigure().setIconType(Constants.NORMAL_ICON);
			}else if(Constants.WARNING_ICON == decideIconType(((ModelElement)(getParent().getModel())).getRoles(),getCastedModel().getRoles())){
				getCastedModel().setIconType(Constants.WARNING_ICON);
				getCastedFigure().setIconType(Constants.WARNING_ICON);
				associatedSubPagePart.getCastedModel().setIconType(Constants.WARNING_ICON);
				associatedSubPagePart.getCastedFigure().setIconType(Constants.WARNING_ICON);
			}else if(Constants.ERROR_ICON == decideIconType(((ModelElement)(getParent().getModel())).getRoles(),getCastedModel().getRoles())){
				getCastedModel().setIconType(Constants.ERROR_ICON);
				getCastedFigure().setIconType(Constants.ERROR_ICON);
				associatedSubPagePart.getCastedModel().setIconType(Constants.ERROR_ICON);
				associatedSubPagePart.getCastedFigure().setIconType(Constants.ERROR_ICON);
			}
			associatedSubPagePart.getFigure().repaint();
		}
		//Set roles of placeholder according to the page.
		for ( int i = 0 ; i < getModelChildren().size() ; i++ ) {
			if(getModelChildren().get(i) instanceof PlaceHolder){
				((PlaceHolder)getModelChildren().get(i)).setRoles(getCastedModel().getRoles());
			}
		}
		
		//Set roles for portlets...
		//TODO
	
		getFigure().repaint();
		refreshVisuals();
		
		}else if ( Page.PAGE_LAYOUT.equals(prop)) {
		if(getCastedModel().getLayout().equals(Constants.LAYOUT_GENERIC_2_COLUMN)){
			//TODO
		} else if(getCastedModel().getLayout().equals(Constants.LAYOUT_3_COLUMN)){
			//TODO
		}
		
		String previousLayout = (String)evt.getOldValue();
		String newLayout = (String)evt.getNewValue();
		
		
		if ( previousLayout.equals(Constants.LAYOUT_GENERIC_2_COLUMN) 
				&& newLayout.equals(Constants.LAYOUT_3_COLUMN) ) {
			//List modelChildren = getChildren();
			List modelChildren = getModelChildren();
			PlaceHolder placeHolder1 = null;
			PlaceHolder placeHolder2 = null;

			/*
			 * there can be 2 situations 
			 * user can change the layout of data subPage
			 * or he/she can change layout of display subPage.
			 * 
			 * 1)If the person clicked on the displaySubPage , then just modify the 
			 * placeholders and portlets of current subPage(display SubPage) accordingly.
			 * 
			 * 2)If that person clicked on tab and changed the data subpage's layout
			 * then we assume that the subPage selected is up to date regarding
			 * placeholders and portlets.
			 * because when a user clicks on tab button we make it selected and 
			 * copy data of display page in subpage(data subpage).
			 * So I think the assumption is not wrong.If possible,then please think 
			 * about some test case where this assumption is not appropriate 
			 * and let me now so that we can modify the code accordingly.
			 */
			
			/*
			 * Update the current subPage's placeholders.no matter whether it is a data 
			 * subpage or displaySubPage.
			 */
			for ( int i = 0 ; i < modelChildren.size() ; i++ ) {
				if ( modelChildren.get(i) instanceof PlaceHolder ) {
					PlaceHolder placeHolder = (PlaceHolder)modelChildren.get(i);
					placeHolder.setLayout(newLayout);
					//PlaceHolder newPlaceHolder = placeHolder;
					//copyPlaceHolder(placeHolder,newPlaceHolder);
					if ( 1 == placeHolder.getPlaceHolderNo() ) {
						placeHolder.setName(Constants.LAYOUT_3_COLUMN_PLACEHOLDER1);
						placeHolder1 = placeHolder;
					}else if ( 2 == placeHolder.getPlaceHolderNo() ) {
						placeHolder.setName(Constants.LAYOUT_3_COLUMN_PLACEHOLDER2);
						placeHolder2 = placeHolder;
					}
					//firePropertyChange(PlaceHolder.PLACEHOLDER_LAYOUT, 
					//		Constants.LAYOUT_GENERIC_2_COLUMN, Constants.LAYOUT_3_COLUMN);
				}
			}
			getCastedModel().removePlaceHolder(placeHolder1);
			getCastedModel().addPlaceHolder(placeHolder1);
			getCastedModel().removePlaceHolder(placeHolder2);
			getCastedModel().addPlaceHolder(placeHolder2);
			
			PlaceHolder newPlaceHolder = new PlaceHolder();
			newPlaceHolder.setLayout(Constants.LAYOUT_3_COLUMN);
			newPlaceHolder.setName(Constants.LAYOUT_3_COLUMN_PLACEHOLDER3);
			newPlaceHolder.setPlaceHolderNo(3);
			newPlaceHolder.setRoles(getCastedModel().getRoles());
			getCastedModel().addPlaceHolder(newPlaceHolder);
			
			
		}else if(previousLayout.equals(Constants.LAYOUT_3_COLUMN) 
				&& newLayout.equals(Constants.LAYOUT_GENERIC_2_COLUMN)){
			//TODO
			//here I am implementing basically flow layout
			List<Portlet> portlets = new ArrayList() ;
			List modelChildren = getModelChildren();
			
			for ( int i = 0 ; i < modelChildren.size() ; i++ ) {
				if ( modelChildren.get(i) instanceof PlaceHolder ) {
					PlaceHolder placeHolder = (PlaceHolder)modelChildren.get(i);
					for ( int j = 0 ; j < placeHolder.getChildren().size() ; j++ ) {
						if(placeHolder.getChildren().get(j) instanceof Portlet){
							Portlet newPortlet = createDuplicatePortlet((Portlet)placeHolder.getChildren().get(j));
							portlets.add(newPortlet);
						}
					}
					/*int childSize = placeHolder.getChildren().size();
					for ( int j = 0 ; j < childSize ; i++ ) {
						if ( placeHolder.getChildren().get(i) instanceof Portlet ) {
							placeHolder.removePortlet((Portlet)placeHolder.getChildren().get(i));
						}
					}*/
				}
			}
			//Remove the placeHolder
			int size = modelChildren.size();
			for ( int i = 0 , j = 0 ; i < size ; i++ ) {
				if ( modelChildren.get(j) instanceof PlaceHolder ) {
					getCastedModel().removePlaceHolder((PlaceHolder)modelChildren.get(j));
				}else{
					j++;
				}
			}
			//Now add 2 new placeholder
			PlaceHolder placeHolder1 = new PlaceHolder();
			placeHolder1.setLayout(Constants.LAYOUT_GENERIC_2_COLUMN);
			placeHolder1.setName(Constants.LAYOUT_GENERIC_2_COLUMN_PLACEHOLDER1);
			placeHolder1.setPlaceHolderNo(1);
			placeHolder1.setRoles(getCastedModel().getRoles());
			getCastedModel().addPlaceHolder(placeHolder1);
			
			PlaceHolder placeHolder2 = new PlaceHolder();
			placeHolder2.setLayout(Constants.LAYOUT_GENERIC_2_COLUMN);
			placeHolder2.setName(Constants.LAYOUT_GENERIC_2_COLUMN_PLACEHOLDER1);
			placeHolder2.setPlaceHolderNo(2);
			placeHolder2.setRoles(getCastedModel().getRoles());
			getCastedModel().addPlaceHolder(placeHolder2);
			
			for ( int i = 0 ; i < portlets.size() ; i++ ) {
				if(i < portlets.size()/2){
					placeHolder1.addPortlet(portlets.get(i));
				} else{
					placeHolder2.addPortlet(portlets.get(i));
				}
				
			}
		}
		
		if(getCastedModel().isDummy()){
			/* This is the case when user has clicked on displaySubpage and changed
			 * layout of displaySubpage.
			 */
			//Nothing more to do
		}
		else{
			/*
			 * This is the case when user has changed the properties of data subPage.
			 * This data subPage has already been updated. Now update the displaysubpage's
			 * placeHolders. So that display could be updated.
			 */
			SubPageEditPart displaySubPagePart = getDisplaySubPagePart();
			if ( null != displaySubPagePart ) {
				SubPage displaySubPage = displaySubPagePart.getCastedModel();
				displaySubPage.setLayout(getCastedModel().getLayout());
				/*
				 * Here placeholders of displaySubPage are removed.
				 * This is done because of our assumption (mentioned above) that 
				 * the current subPage is in sync with the displaySubPage regarding placeholders
				 * and portlets.
				 */
				removePlaceHoldersFromPage(displaySubPage);
				copyPlaceHolders(getCastedModel(), displaySubPage);
				displaySubPagePart.refreshChildren();
				
			}
		}
		
		
		
		refreshChildren();
		//getFigure().repaint();
		//refreshVisuals();
	}/*else if (Page.CHILD_ADDED_PROP.equals(prop)
			|| Page.CHILD_REMOVED_PROP.equals(prop)) {
		refreshChildren();
		getFigure().repaint();
		
	}*/else if(Page.PLACEHOLDER_ADDED_PROP.equals(prop) || Page.PLACEHOLDER_REMOVED_PROP.equals(prop)){
		System.out.println("SubPageEditpart: Page.PLACEHOLDER_ADDED_PROP noticed");
		//handleChildChange(evt);
		refreshChildren();
	}
	//refreshChildren();
	//refreshVisuals();
}

protected void refreshVisuals() {
	int xmargin = 10 ;
	int ymargin = 20;
	int height = 30;
	System.out.println("SubPageEditPart: in refreshVisuals");
	//Rectangle bounds = new Rectangle(getCastedModel().getLocation(),getCastedModel().getSize());
	Rectangle bounds = new Rectangle(50,50,50,50);
	Rectangle displayPageBounds = ((GraphicalEditPart)getParent()).getFigure().getBounds();
	//bounds = new Rectangle (x + xmargin,y+ ymargin,lx-2*xmargin,height);	
		bounds = new Rectangle (displayPageBounds.x + xmargin , displayPageBounds.y + ymargin + 100 ,
				displayPageBounds.width - 2*xmargin , displayPageBounds.height/20 ) ;	
	System.out.println("SubPageEditPart: displayPageBounds: " + displayPageBounds);
	System.out.println("SubPageEditPart: bounds: " + bounds);
	//if ( displayPageBounds.height == 0 && displayPageBounds.width == 0 ){
		bounds = new Rectangle(20,20,0,0);
	//}
	// notify parent container of changed position & location
	// if this line is removed, the XYLayoutManager used by the parent container 
	// (the Figure of the PageEditPart), will not know the bounds of this figure
	// and will not draw it correctly.
	System.out.println("getparent in refreshVisuals: " + getParent());
	((GraphicalEditPart) getParent()).setLayoutConstraint(this, getFigure(), bounds);	
}

@Override
public void setSelected(int value) {
//	super.setSelected(value);
	System.out.println("setSelected called");
	if(SELECTED == value || SELECTED_PRIMARY == value){
		
		/*
		 * if the user has clicked on the displaySubPage which is nothing but
		 * the tab's content display portion
		 */
		if(getCastedModel().isDummy()){
			SubPageEditPart associatedSubPageEditPart = getDataSubPagePart(getCastedModel().getPageNo());
			associatedSubPageEditPart.getCastedFigure().setState(Constants.SELECTED);
			associatedSubPageEditPart.getFigure().repaint();
			
			PageEditPart parentPageEditPart = getDataPagePart(getCastedModel().getParentPageNo());
			parentPageEditPart.getCastedFigure().setState(Constants.CHILD_SELECTED);
			parentPageEditPart.getFigure().repaint();
		}
		/*if user has clicked (selected) on the tab (button)( data subPage)
		 * Here copy data from displaySubPage to data subPage and vice-versa.
		 */
		else if (false == getCastedModel().isDummy() ) {
			/*getCastedModel().getPageNo();
			if(((PageEditPart)getParent()).getCastedModel().isDummy()){
				((PageEditPart)getParent()).getCastedFigure()
			}*/
			PageEditPart displayPagePart = (PageEditPart) getParent();
			MainPage displayPage = displayPagePart.getCastedModel();
			//In normal scenario then this condition must be true
			/*if(displayPage.isDummy()){
				displayPage.setDisplayState(Constants.PAGE_CHILD_VISIBLE);
				getParent().refresh();
				displayPagePart.getFigure().repaint();
				
			}*/
			
			List children = ((PageEditPart)getParent()).getFigure().getChildren();
			System.out.println("\nSubPageEditPart: setselected(): clicked on tab.");
			
			MainPage parentPage = (MainPage) getParent().getModel();
			List siblings = parentPage.getChildren();
			SubPageEditPart displaySubPagePart = getDisplaySubPagePart();
			if( null != displaySubPagePart){
			SubPage displaySubPage = displaySubPagePart.getCastedModel();
			/*for ( int i = 0 ; i < siblings.size() ; i++ ){
				ModelElement sibling = (ModelElement) siblings.get(i);
				if(sibling instanceof SubPage && ((SubPage)sibling).isDummy() ){
				displaySubPage = (SubPage) sibling;
					break;
				}
			}*/
			System.out.println("sibling dummy subpage is: " + displaySubPage);
			SubPage previouslySelectedSubPage = null;
			
			if ( true == removed ) {
				previouslySelectedSubPage = removedSubPage;
			} else {
				previouslySelectedSubPage = getDataSubPagePart(displaySubPage.getPageNo()).getCastedModel();
				/*for ( int i = 0 ; i < siblings.size() ; i++ ){
					ModelElement sibling = (ModelElement) siblings.get(i);
					if(sibling instanceof SubPage && (false == ((SubPage)sibling).isDummy()) 
							&& displaySubPage.getPageNo() == ((SubPage)sibling).getPageNo()){
						previouslySelectedSubPage = (SubPage) sibling;
						break;
					}
				}*/
			}
			//TODO
			/*
			 * Copy data of displaySubPage back to the subpage of which it was
			 * actually showing the data.
			 */
			System.out.println("frompage is display subpage : " + displaySubPage);
			System.out.println("toPage is  previousSubPage: " + previouslySelectedSubPage);
			System.out.println("chck it: layout of display subpage is: " + displaySubPage.getLayout());
			cleanPage(previouslySelectedSubPage);
			copyPlaceHolders(displaySubPage, previouslySelectedSubPage);
			/*
			 * Copy data of new selected page in display page.
			 */
			//clean up the display page
			cleanPage(displaySubPage);
			/*List displaySubPageChildren = displaySubPage.getChildren() ;
			int noOfChildren = displaySubPageChildren.size() ;
			System.out.println("displaySubPage children are: " + displaySubPageChildren.size());
			for(int i = 0 , j = 0 ; i < noOfChildren ; i++ ) { 
				if ( displaySubPageChildren.get(j) instanceof PlaceHolder ) {
					displaySubPage.removePlaceHolder((PlaceHolder)displaySubPageChildren.get(j));
					System.out.println("placeholder removed.....");
				}else{
					j++ ;
				}
			}*/
			displaySubPage.setPageNo(getCastedModel().getPageNo());
			displaySubPage.setParentPageNo(getCastedModel().getParentPageNo());
			System.out.println("frompage is Current page : " + this);
			System.out.println("toPage is dispaly Page: " + displaySubPage);
			copyPlaceHolders(getCastedModel(), displaySubPage);
			
			//Copy newSelectedPage's properties in displaySubPage
			/*displaySubPage.setPageNo(newSelectedPage.getPageNo());
			displaySubPage.setLayout(newSelectedPage.getLayout());
			displaySubPage.setName(newSelectedPage.getName());
			displaySubPage.setNoOfSubPages(newSelectedPage.getNoOfSubPages());
			displaySubPage.setTheme(newSelectedPage.getTheme());
			*/
			//addPlaceHolders(displaySubPage);
			displaySubPagePart.getCastedFigure().setPageNo(getCastedModel().getPageNo());
			//displaySubPagePart.getCastedFigure().setPa(getCastedModel().getParentPageNo());
			displaySubPagePart.getFigure().repaint();
			}
			/* Code to display the subPage data in displaySubPage if user clicks on subPage directly.
			 * Now no need to always double click the pageChildGroup or pageContentGroup to
			 *see the subPages' data.
			 */
			if(((PageEditPart)getParent()).getCastedModel().isDummy()){
				
				/*PageEditPart associatedParentPagePart = getDataPagePart(getCastedModel().getParentPageNo());
				MainPage associatedParentPage = associatedParentPagePart.getCastedModel();
				associatedParentPage.setSelectedSubPageNo(getCastedModel().getPageNo());
				associatedParentPage.setSelectedParentPageNo(getCastedModel().getParentPageNo());
				associatedParentPage.setDisplayState(Constants.PAGE_CHILD_VISIBLE);
				*/
				
				((PageEditPart)getParent()).getCastedModel().setSelectedSubPageNo(getCastedModel().getPageNo());
				((PageEditPart)getParent()).getCastedModel().setSelectedParentPageNo(getCastedModel().getParentPageNo());
				((PageEditPart)getParent()).getCastedModel().setDisplayState(Constants.PAGE_CHILD_VISIBLE);
			}
			for ( int i = 0 ; i < children.size() ; i++ ) {
				/*if ( children.get(i) instanceof DisplaySubPageFigure ){
					//settings to get info. which page is being selected
					List siblingParts = getParent().getChildren();
					
					for ( int j = 0 ; j < siblingParts.size() ; j++ ) {
						if(((SubPageEditPart)siblingParts.get(i)).getFigure() instanceof DisplayPageFigure){
							((Page)((SubPageEditPart)siblingParts.get(i)).getModel()).setPageNo(((Page)getModel()).getPageNo());
						}
					}
					
						((DisplayPageFigure)children.get(i)).setPageNo(((Page)getModel()).getPageNo());
					
					System.out.println("\n PageEditPart: setselected(): repaint start");
					((DisplayPageFigure)children.get(i)).repaint();
					System.out.println("\nPageEditPart: setselected(): repaint complete");
				}else 
				*/
				if ( children.get(i) instanceof SubPageFigure 
						&& false == (children.get(i).equals(getFigure()))){
					((SubPageFigure)children.get(i)).setState(Constants.NORMAL);
				}
			}
			PageEditPart parentPageEditPart = getDataPagePart(getCastedModel().getParentPageNo());
			parentPageEditPart.getCastedFigure().setState(Constants.CHILD_SELECTED);
			parentPageEditPart.getFigure().repaint();

		}
		//set the figure selected whatever it is(subPageFigure or DisplaySubPageFigure)
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
	System.out.println("PageEditPart: req: type : " + req.getType());
	System.out.println("PageEditPart : req: class : " + req.getClass().getName());
	System.out.println("PageEditPart : req: extendedData : " + req.getExtendedData());
	if(req.getType().equals(REQ_DIRECT_EDIT)){
		System.out.println("PageEditPart: REQ_DIRECT_EDIT entered " );
		BasicFigure figure = (BasicFigure) getFigure();
		figure.setState(Constants.SELECTED);
		figure.repaint();
		//Color fgColor = ColorConstants.red;
		
		//figure.paint(f.graphics);
		//f.setFGColor(fgColor);
		//f.paintFigure(new SWTGraphics(new GC(new Shell())));
		System.out.println("PageEditPart: REQ_DIRECT_EDIT exit \n\n" );
		//fireSelectionChanged();
	}
	if(req.getType().equals(REQ_OPEN)){
		System.out.println("PageEditPart: REQ_DIRECT_EDIT entered " );
		BasicFigure figure = (BasicFigure) getFigure();
		figure.setState(Constants.SELECTED);
		figure.repaint();
		//Color fgColor = ColorConstants.red;
		
		//figure.paint(f.graphics);
		//f.setFGColor(fgColor);
		//f.paintFigure(new SWTGraphics(new GC(new Shell())));
		System.out.println("PageEditPart: REQ_DIRECT_EDIT exit \n\n" );
		//fireSelectionChanged();
	}
}
 /*private void addPlaceHolders(Page displayPage){
	 Page page = getCastedModel();
		if(page.getLayout().equals(Constants.LAYOUT_GENERIC_2_COLUMN)){
			PlaceHolder placeHolder1 = new PlaceHolder();
			placeHolder1.setLayout(page.getLayout());
			placeHolder1.setPlaceHolderNo(1);
			displayPage.addPlaceHolder(placeHolder1);
			placeHolder1.setName(Constants.LAYOUT_GENERIC_2_COLUMN_PLACEHOLDER1);
			//page.addChild(placeHolder1);
			
			PlaceHolder placeHolder2 = new PlaceHolder();
			placeHolder2.setName(Constants.LAYOUT_GENERIC_2_COLUMN_PLACEHOLDER2);
			placeHolder2.setPlaceHolderNo(2);
			placeHolder2.setLayout(page.getLayout());
			displayPage.addPlaceHolder(placeHolder2);
			//page.addChild(placeHolder2);
			//PortletShape s = new PortletShape();
			//page.addChild(s);
		}
		if ( page.getLayout().equals(Constants.LAYOUT_3_COLUMN) ) {
		
			PlaceHolder placeHolder1 = new PlaceHolder();
			placeHolder1.setLayout(page.getLayout());
			placeHolder1.setPlaceHolderNo(1);
			placeHolder1.setName(Constants.LAYOUT_3_COLUMN_PLACEHOLDER1);
			displayPage.addPlaceHolder(placeHolder1);
			//page.addChild(placeHolder1);
			
			PlaceHolder placeHolder2 = new PlaceHolder();
			placeHolder2.setPlaceHolderNo(2);
			placeHolder2.setLayout(page.getLayout());
			placeHolder2.setName(Constants.LAYOUT_3_COLUMN_PLACEHOLDER2);
			displayPage.addPlaceHolder(placeHolder2);
			
			PlaceHolder placeHolder3 = new PlaceHolder();
			placeHolder3.setPlaceHolderNo(3);
			placeHolder3.setLayout(page.getLayout());
			placeHolder3.setName(Constants.LAYOUT_3_COLUMN_PLACEHOLDER3);
			displayPage.addPlaceHolder(placeHolder3);
			
		}
 }
 */

 private void copyPageData(Page fromPage,Page toPage){
	 toPage.setPageNo(fromPage.getPageNo());
	 toPage.setLayout(fromPage.getLayout());
	 toPage.setName(fromPage.getName());
	 toPage.setNoOfSubPages(fromPage.getNoOfSubPages());
	 toPage.setTheme(fromPage.getTheme());
	 toPage.setRoles(fromPage.getRoles());
	 toPage.setIconType(fromPage.getIconType());
	 List fromPageChildren = fromPage.getChildren() ;
		for ( int i = 0 ; i < fromPageChildren.size() ; i++ ){
			
			if(fromPageChildren.get(i) instanceof PlaceHolder){
				PlaceHolder fromPlaceHolder = (PlaceHolder)fromPageChildren.get(i);
				PlaceHolder newPlaceHolder = new PlaceHolder();
				//Copy placeholder info
				copyPlaceHolderProperties(fromPlaceHolder, newPlaceHolder);
				//newPlaceHolder.set
				System.out.println("frompage is : " + fromPage);
				System.out.println("topage is : " + toPage);
				
				System.out.println("check it: layout" + fromPlaceHolder.getLayout());
				System.out.println("" + fromPlaceHolder.getName());
				System.out.println("" + fromPlaceHolder.getPlaceHolderNo());
				//System.out.println("" + fromPlaceholder.get);
				
				toPage.addPlaceHolder(newPlaceHolder);
				
				for(int j = 0 ; j < fromPlaceHolder.getChildren().size() ; j++){
					if(fromPlaceHolder.getChildren().get(j) instanceof Portlet){
						Portlet fromPortlet = (Portlet)fromPlaceHolder.getChildren().get(j);
						Portlet newPortlet = createDuplicatePortlet(fromPortlet);
						/*Portlet newPortlet = new Portlet();
						newPortlet.setIconType(fromPortlet.getIconType());
						newPortlet.setName(fromPortlet.getName());
						newPortlet.setPortletNo(fromPortlet.getPortletNo());
						newPortlet.setRoles(fromPortlet.getRoles());
						*/
						newPlaceHolder.addPortlet(newPortlet);
					}
				}
			}
		}
		
 }
 
 public void cleanPage(Page page){
	 List pageChildren = page.getChildren() ;
		int noOfChildren = pageChildren.size() ;
		System.out.println("page children are: " + pageChildren.size());
		for(int i = 0 , j = 0 ; i < noOfChildren ; i++ ) { 
			if ( pageChildren.get(j) instanceof PlaceHolder ) {
				page.removePlaceHolder((PlaceHolder)pageChildren.get(j));
				System.out.println("placeholder removed.....");
			}else{
				//TODO write code to remove subpages if required.
				j++ ;
			}
		} 
 }

 public void removePlaceHoldersFromPage(Page page){
	 List pageChildren = page.getChildren() ;
		int noOfChildren = pageChildren.size() ;
		System.out.println("page children are: " + pageChildren.size());
		for(int i = 0 , j = 0 ; i < noOfChildren ; i++ ) { 
			if ( pageChildren.get(j) instanceof PlaceHolder ) {
				page.removePlaceHolder((PlaceHolder)pageChildren.get(j));
				System.out.println("placeholder removed.....");
			}else{
				j++ ;
			}
		} 
 }
 private void copyPlaceHolders(Page fromPage,Page toPage){
	 
	 List fromPageChildren = fromPage.getChildren() ;
		for ( int i = 0 ; i < fromPageChildren.size() ; i++ ){
			
			if(fromPageChildren.get(i) instanceof PlaceHolder){
				PlaceHolder fromPlaceHolder = (PlaceHolder)fromPageChildren.get(i);
				PlaceHolder newPlaceHolder = new PlaceHolder();
				//Copy placeholder info
				copyPlaceHolderProperties(fromPlaceHolder, newPlaceHolder);
		
				toPage.addPlaceHolder(newPlaceHolder);
				
				for(int j = 0 ; j < fromPlaceHolder.getChildren().size() ; j++){
					if(fromPlaceHolder.getChildren().get(j) instanceof Portlet){
						Portlet fromPortlet = (Portlet)fromPlaceHolder.getChildren().get(j);
						Portlet newPortlet = createDuplicatePortlet(fromPortlet);
						/*Portlet newPortlet = new Portlet();
						newPortlet.setIconType(fromPortlet.getIconType());
						newPortlet.setName(fromPortlet.getName());
						newPortlet.setPortletNo(fromPortlet.getPortletNo());
						newPortlet.setRoles(fromPortlet.getRoles());
						*/
						newPlaceHolder.addPortlet(newPortlet);
					}
				}
			}
		}
		
 }

 public Portlet createDuplicatePortlet(Portlet portlet){
	 Portlet newPortlet = new Portlet();
	 newPortlet.setIconType(portlet.getIconType());
	 newPortlet.setName(portlet.getName());
	 newPortlet.setPortletNo(portlet.getPortletNo());
	 newPortlet.setRoles(portlet.getRoles());
	 newPortlet.setToolTip(portlet.getToolTip());
	 newPortlet.setCpfPortlet(portlet.getCpfPortlet());
	 newPortlet.setHelpEnabled(portlet.isHelpEnabled());
	 newPortlet.setHelpScreen(portlet.getHelpScreen());

	 return newPortlet;
 }
 private void copyPlaceHolderProperties(PlaceHolder fromPlaceHolder , PlaceHolder toPlaceHolder){
	 toPlaceHolder.setLayout(fromPlaceHolder.getLayout());
	 toPlaceHolder.setPlaceHolderNo(fromPlaceHolder.getPlaceHolderNo());
	 toPlaceHolder.setName(fromPlaceHolder.getName());
	 toPlaceHolder.setRoles(fromPlaceHolder.getRoles());
		
 }
 
//helper function to get display subPage edit part.
 private SubPageEditPart getDisplaySubPagePart(){
	 SubPageEditPart displaySubPagePart = null;
	 List displaySubPageSiblingParts = null;
	 displaySubPageSiblingParts = getParent().getChildren();
	 for ( int i = 0 ; i < displaySubPageSiblingParts.size() ; i++ ) {
		 if (displaySubPageSiblingParts.get(i) instanceof SubPageEditPart 
				 &&((SubPageEditPart)displaySubPageSiblingParts.get(i)).getCastedModel().isDummy() ){
			 displaySubPagePart = (SubPageEditPart) displaySubPageSiblingParts.get(i);
		 }
	 }
	 return displaySubPagePart;
 }
 //helper function to get edit part of subPage having the passed pageNo.
 private SubPageEditPart getDataSubPagePart(int pageNo){
	 SubPageEditPart dataSubPagePart = null;
	 List displaySubPageSiblingParts = getParent().getChildren();
	 for ( int i = 0 ; i < displaySubPageSiblingParts.size() ; i++ ) {
			if (displaySubPageSiblingParts.get(i) instanceof SubPageEditPart 
					&&( false == ((SubPageEditPart)displaySubPageSiblingParts.get(i)).getCastedModel().isDummy()) 
					&& pageNo == ((SubPageEditPart)displaySubPageSiblingParts.get(i)).getCastedModel().getPageNo()){
				dataSubPagePart = (SubPageEditPart) displaySubPageSiblingParts.get(i);
			}
		}
		return dataSubPagePart;
 }
 //helper function to get edit part of MainPage(data page and not displaypage) having the given pageNo.
 private PageEditPart getDataPagePart(int pageNo){
	 PageEditPart dataPagePart = null;
	 List displayPageSiblingParts = getParent().getParent().getChildren();
	 for ( int i = 0 ; i < displayPageSiblingParts.size() ; i++ ) {
			if (displayPageSiblingParts.get(i) instanceof PageEditPart 
					&&( false == ((PageEditPart)displayPageSiblingParts.get(i)).getCastedModel().isDummy()) 
					&& pageNo == ((PageEditPart)displayPageSiblingParts.get(i)).getCastedModel().getPageNo()){
				dataPagePart = (PageEditPart) displayPageSiblingParts.get(i);
			}
		}
		return dataPagePart;
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
public boolean isRemoved() {
	return removed;
}
public void setRemoved(boolean removed) {
	this.removed = removed;
}
public SubPage getRemovedSubPage() {
	return removedSubPage;
}
public void setRemovedSubPage(SubPage removedSubPage) {
	this.removedSubPage = removedSubPage;
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