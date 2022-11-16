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
*     File:     PageEditPart.java
*
*     Desc:   	EditPart (Controller) for Page.
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
import com.genband.m5.maps.ide.sitemap.figure.BasicFigure;
import com.genband.m5.maps.ide.sitemap.figure.BasicPageFigure;
import com.genband.m5.maps.ide.sitemap.figure.DisplayPageFigure;
import com.genband.m5.maps.ide.sitemap.figure.DisplaySubPageFigure;
import com.genband.m5.maps.ide.sitemap.figure.PageFigure;
import com.genband.m5.maps.ide.sitemap.figure.SubPageFigure;
import com.genband.m5.maps.ide.sitemap.model.MainPage;
import com.genband.m5.maps.ide.sitemap.model.ModelElement;
import com.genband.m5.maps.ide.sitemap.model.Page;
import com.genband.m5.maps.ide.sitemap.model.PageChildGroup;
import com.genband.m5.maps.ide.sitemap.model.PlaceHolder;
import com.genband.m5.maps.ide.sitemap.model.Portlet;
import com.genband.m5.maps.ide.sitemap.model.Shape;
import com.genband.m5.maps.ide.sitemap.model.SiteMap;
import com.genband.m5.maps.ide.sitemap.model.SubPage;
import com.genband.m5.maps.ide.sitemap.util.Constants;

/**
 * EditPart used for Page instances.
 * This edit part must implement the PropertyChangeListener interface, 
 * so it can be notified of property changes in the corresponding model element.
 * 
 * @author Genband
 */
public class PageEditPart extends AbstractGraphicalEditPart 
	implements PropertyChangeListener {
	
boolean removed = false;
MainPage removedPage = null;
//int selectedSubPageNo = Constants.INVALID;
//int selectedParentPageNo = Constants.INVALID;
List childParts = getChildren();

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
protected List getModelChildren() {
	return getCastedModel().getChildren(); // return a list of components
}

/* (non-Javadoc)
 * @see org.eclipse.gef.editparts.AbstractEditPart#createEditPolicies()
 */
protected void createEditPolicies() {
	// allow removal of the associated model element
	installEditPolicy(EditPolicy.COMPONENT_ROLE, new ComponentDeleteEditPolicy());
	//installEditPolicy(EditPolicy.LAYOUT_ROLE,  new Shapes1XYLayoutEditPolicy());
	installEditPolicy(EditPolicy.CONTAINER_ROLE, new PageXYLayoutEditPolicy());
	installEditPolicy(EditPolicy.SELECTION_FEEDBACK_ROLE,  new ComponentSelectionEditPolicy());

	}

/**(non-Javadoc)
 * @see org.eclipse.gef.editparts.AbstractGraphicalEditPart#createFigure()
 */
/*
 * Return an IFigure for page. 
 * If the page is not dummy then this will return not actually the whole tab figure.
 * It will return figure which is basically the tab's upper portion(similar to 
 * button) only.
 * If page is dummy then it will return figure which is actually the tab's content
 * holder portion.This is the figure which will show contents and sub pages of current
 * page.
 */
protected IFigure createFigure() {
	BasicPageFigure figure = null;
	if ( getModel() instanceof MainPage	
			&& false == ((MainPage)getModel()).isDummy() ) {
		figure = new PageFigure();
	
	}else if ( getModel() instanceof MainPage	
			&& true == ((MainPage)getModel()).isDummy() ) {
		figure = new DisplayPageFigure();
	}else {
		System.out.println("error: " + getModel());
		throw new IllegalArgumentException();
	}
	figure.setIconType(getCastedModel().getIconType());
	figure.setPageName(getCastedModel().getName());
	figure.setPageNo(getCastedModel().getPageNo());
	figure.setLayoutManager(new FreeformLayout());
	figure.setOpaque(true); // non-transparent figure
	figure.setDisplayState(getCastedModel().getDisplayState());
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

public MainPage getCastedModel() {
	return (MainPage) getModel();
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
			PageEditPart displayPagePart = getDisplayPagePart();
			displayPagePart.getCastedModel().setName(getCastedModel().getName());
		}else if ( true == getCastedModel().isDummy() ) {
			PageEditPart associatedPagePart = getDataPagePart(getCastedModel().getPageNo());
			associatedPagePart.getCastedModel().setName(getCastedModel().getName());
			associatedPagePart.getCastedFigure().setPageName(getCastedModel().getName());
		}
		refreshVisuals();
	}	if ( Page.PAGE_THEME.equals(prop)) {
		
		if ( false == getCastedModel().isDummy() ) {
			PageEditPart displayPagePart = getDisplayPagePart();
			displayPagePart.getCastedModel().setTheme(getCastedModel().getTheme());
		}else if ( true == getCastedModel().isDummy() ) {
			PageEditPart associatedPagePart = getDataPagePart(getCastedModel().getPageNo());
			associatedPagePart.getCastedModel().setTheme(getCastedModel().getTheme());
		}
		refreshVisuals();
	} else if ( Page.PAGE_ROLES.equals(prop)) {
		
		//TODO roles
		/*
		 * change icon for page accordingly
		 * Also change the iconType of display page. We don't need it.
		 * But when we copy the data of displayPage we overwrite the data
		 * So we keep display page up to date.
		 * The other solution was not to copy the iconType from display page
		 * to data page. This was also a good solution but I opted for the previous
		 * solution just to make code more structured, understandable and maintainable.
		 * Otherwise, I know that I have *deliberately* not copied iconType from displayPage
		 * to data page.But the other person might think that I have forgotten to copy this
		 * info(iconType).Tomorrow I might also forget the same.
		 * This was the case when user clicks on the tab (data page) and changes the roles
		 * 
		 * TODO 2
		 * Now the second case is : User clicks on the displayPage and changes the roles
		 * TODO
		 * change the roles of displayPage , as well as the page of which it is displaying
		 * the content.
		 */
		//Case 1 : data page's roles have changed
		if(false == getCastedModel().isDummy()){
			PageEditPart displayPagePart = null;
			displayPagePart = getDisplayPagePart();
			//displayPagePart.getCastedModel().setRoles(getCastedModel().getRoles());
			displayPagePart.getCastedModel().setPropertyValue(Page.PAGE_ROLES, getCastedModel().getRoles());
			displayPagePart.propertyChange(evt);	
			if(Constants.NORMAL_ICON == decideIconType(((ModelElement)(getParent().getModel())).getRoles(),getCastedModel().getRoles())){
				getCastedModel().setIconType(Constants.NORMAL_ICON);
				getCastedFigure().setIconType(Constants.NORMAL_ICON);
				displayPagePart.getCastedModel().setIconType(Constants.NORMAL_ICON);
				displayPagePart.getCastedFigure().setIconType(Constants.NORMAL_ICON);
			}else if(Constants.WARNING_ICON == decideIconType(((ModelElement)(getParent().getModel())).getRoles(),getCastedModel().getRoles())){
				getCastedModel().setIconType(Constants.WARNING_ICON);
				getCastedFigure().setIconType(Constants.WARNING_ICON);
				displayPagePart.getCastedModel().setIconType(Constants.WARNING_ICON);
				displayPagePart.getCastedFigure().setIconType(Constants.WARNING_ICON);
			}else if(Constants.ERROR_ICON == decideIconType(((ModelElement)(getParent().getModel())).getRoles(),getCastedModel().getRoles())){
				getCastedModel().setIconType(Constants.ERROR_ICON);
				getCastedFigure().setIconType(Constants.ERROR_ICON);
				displayPagePart.getCastedModel().setIconType(Constants.ERROR_ICON);
				displayPagePart.getCastedFigure().setIconType(Constants.ERROR_ICON);
			}
			displayPagePart.getFigure().repaint();
			//Set roles of placeholders of displayPage according to the page.
			/*for ( int i = 0 ; i < displayPagePart.getModelChildren().size() ; i++ ) {
				if(displayPagePart.getModelChildren().get(i) instanceof PlaceHolder){
					((PlaceHolder)displayPagePart.getModelChildren().get(i)).setRoles(getCastedModel().getRoles());
				}
			}
		
			// set roles for subPages
			// Update subPage roles accordingly
			String pagePreviousRolesString = (String)evt.getOldValue();
			String pageRolesString = (String)evt.getNewValue();
			
			List pagePreviousRoles = parseString(pagePreviousRolesString);
			List pageRoles = parseString(pageRolesString);
			List newAddedRolesToPage = new ArrayList();
			List removedRolesFromPage = new ArrayList();
			for(int j = 0 ; j < pagePreviousRoles.size() ; j++ ) {
				if ( false == pageRoles.contains(pagePreviousRoles.get(j)) ) {
					removedRolesFromPage.add(pagePreviousRoles.get(j));
				}
			}
			for(int j = 0 ; j < pageRoles.size() ; j++ ) {
				if ( false == pagePreviousRoles.contains(pageRoles.get(j)) ) {
					newAddedRolesToPage.add(pageRoles.get(j));
				}
			}
			
			String subPageRolesString = null;
			String newSubPageRolesString = null;
			
			for( int i = 0 ; i < displayPagePart.getChildren().size() ; i++ ) {
				if ( displayPagePart.getChildren().get(i) instanceof SubPageEditPart ) {
					subPageRolesString = ((SubPageEditPart)displayPagePart.getChildren().get(i)).getCastedModel().getRoles();
					List subPageRoles = parseString(subPageRolesString);

					for(int j = 0 ; j < removedRolesFromPage.size() ; j++){
						if ( subPageRoles.contains(removedRolesFromPage.get(j)) ) {
							subPageRoles.remove(removedRolesFromPage.get(j));
						}
					}
					//for ( int j = 0 ; j < newAddedRolesToPage.size() ; j++ ) {
						
					//}
					if(newAddedRolesToPage.size()>0){
						//icon of subPage can be changed here or it will be changed in SubPageEditPart
			 			if ( Constants.ADD_NEW_ROLES_ADDED_TO_CHILDREN == Constants.ROLES_PROPAGATE_STRATEGY ) {
			 				for ( int k = 0 ; k < newAddedRolesToPage.size() ; k++ ){
				 				if ( false == subPageRoles.contains(newAddedRolesToPage.get(k)) ) {
				 					subPageRoles.add(newAddedRolesToPage.get(k));
				 				}
			 				}
			 			}
					}
					newSubPageRolesString = makeString(subPageRoles);
					((SubPageEditPart)displayPagePart.getChildren().get(i)).getCastedModel().setPropertyValue(Page.PAGE_ROLES, newSubPageRolesString);
				}
			}
			
			displayPagePart.refreshChildren();
		*/
			
		}
		//case 2 : roles of display page are changed
		else if (true == getCastedModel().isDummy()){
			PageEditPart associatedPagePart = getDataPagePart(getCastedModel().getPageNo());
			MainPage associatedPage = associatedPagePart.getCastedModel();
			associatedPage.setRoles(getCastedModel().getRoles());
			if(Constants.NORMAL_ICON == decideIconType(((ModelElement)(getParent().getModel())).getRoles(),getCastedModel().getRoles())){
				getCastedModel().setIconType(Constants.NORMAL_ICON);
				getCastedFigure().setIconType(Constants.NORMAL_ICON);
				associatedPagePart.getCastedModel().setIconType(Constants.NORMAL_ICON);
				associatedPagePart.getCastedFigure().setIconType(Constants.NORMAL_ICON);
			}else if(Constants.WARNING_ICON == decideIconType(((ModelElement)(getParent().getModel())).getRoles(),getCastedModel().getRoles())){
				getCastedModel().setIconType(Constants.WARNING_ICON);
				getCastedFigure().setIconType(Constants.WARNING_ICON);
				associatedPagePart.getCastedModel().setIconType(Constants.WARNING_ICON);
				associatedPagePart.getCastedFigure().setIconType(Constants.WARNING_ICON);
			}else if(Constants.ERROR_ICON == decideIconType(((ModelElement)(getParent().getModel())).getRoles(),getCastedModel().getRoles())){
				getCastedModel().setIconType(Constants.ERROR_ICON);
				getCastedFigure().setIconType(Constants.ERROR_ICON);
				associatedPagePart.getCastedModel().setIconType(Constants.ERROR_ICON);
				associatedPagePart.getCastedFigure().setIconType(Constants.ERROR_ICON);
			}
			associatedPagePart.getFigure().repaint();
		}
		//Set roles of placeholder according to the page.
		for ( int i = 0 ; i < getModelChildren().size() ; i++ ) {
			if(getModelChildren().get(i) instanceof PlaceHolder){
				((PlaceHolder)getModelChildren().get(i)).setRoles(getCastedModel().getRoles());
			}
		}

		// set roles for subPages
		// Update subPage roles accordingly
		String pagePreviousRolesString = (String)evt.getOldValue();
		String pageRolesString = (String)evt.getNewValue();
		
		List pagePreviousRoles = parseString(pagePreviousRolesString);
		List pageRoles = parseString(pageRolesString);
		List newAddedRolesToPage = new ArrayList();
		List removedRolesFromPage = new ArrayList();
		for(int j = 0 ; j < pagePreviousRoles.size() ; j++ ) {
			if ( false == pageRoles.contains(pagePreviousRoles.get(j)) ) {
				removedRolesFromPage.add(pagePreviousRoles.get(j));
			}
		}
		for(int j = 0 ; j < pageRoles.size() ; j++ ) {
			if ( false == pagePreviousRoles.contains(pageRoles.get(j)) ) {
				newAddedRolesToPage.add(pageRoles.get(j));
			}
		}
		
		String subPageRolesString = null;
		String newSubPageRolesString = null;
		
		for( int i = 0 ; i < getChildren().size() ; i++ ) {
			if ( getChildren().get(i) instanceof SubPageEditPart ) {
				subPageRolesString = ((SubPageEditPart)getChildren().get(i)).getCastedModel().getRoles();
				List subPageRoles = parseString(subPageRolesString);
				if ( Constants.REMOVE_ROLES_FROM_CHILDREN_ALSO == Constants.ROLES_PROPAGATE_STRATEGY_ON_REMOVAL_OF_ROLES ) {
					for(int j = 0 ; j < removedRolesFromPage.size() ; j++){
						if ( subPageRoles.contains(removedRolesFromPage.get(j)) ) {
							subPageRoles.remove(removedRolesFromPage.get(j));
						}
					}
				}
				//for ( int j = 0 ; j < newAddedRolesToPage.size() ; j++ ) {
					
				//}
				if(newAddedRolesToPage.size()>0){
					//icon of subPage can be changed here or it will be changed in SubPageEditPart
		 			if ( Constants.ADD_NEW_ROLES_ADDED_TO_CHILDREN == Constants.ROLES_PROPAGATE_STRATEGY ) {
		 				for ( int k = 0 ; k < newAddedRolesToPage.size() ; k++ ){
			 				if ( false == subPageRoles.contains(newAddedRolesToPage.get(k)) ) {
			 					subPageRoles.add(newAddedRolesToPage.get(k));
			 				}
		 				}
		 			}
				}
				newSubPageRolesString = makeString(subPageRoles);
				((SubPageEditPart)getChildren().get(i)).getCastedModel().setPropertyValue(Page.PAGE_ROLES, newSubPageRolesString);
				((SubPageEditPart)getChildren().get(i)).propertyChange(evt);
			}
		}
		
		refreshChildren();
		
		
		
		
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
			 * user can change the layout of data main page
			 * or he/she can change layout of display page.
			 * 
			 * 1)If the person clicked on the displayPage , then just modify the 
			 * placeholders and portlets of current page(display Page) accordingly.
			 * 
			 * 2)If that person clicked on tab and changed the main page's layout
			 * then we assume that the main page selected is up to date regarding
			 * placeholders and portlets.
			 * because when a user clicks on tab button we make it selected and 
			 * copy data of display page in main page.
			 * So I think the assumption is not wrong.If possible,then please think 
			 * about some test case where this assumption is not appropriate 
			 * and let me now so that we can modify the code accordingly. 
			 */
			
			/*
			 * Update the current page's placeholders.no matter whether it is a main 
			 * data page or displayPage.
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
		
		if(getModel() instanceof MainPage){
			if(((MainPage)getModel()).isDummy()){
				/* This is the case when user has clicked on displaypage and changed
				 * layout of displaypage.
				 */
				//Nothing more to do
			}
			else{
				/*
				 * This is the case when user has changed the properties of main data page.
				 * This main data page has already been updated. Now update the displaypage's
				 * placeholders. So that display could be updated.
				 */
				PageEditPart displayPagePart = getDisplayPagePart();
				MainPage displayPage = displayPagePart.getCastedModel();
				displayPage.setLayout(getCastedModel().getLayout());
				/*
				 * Here placeholders of displaypage are removed.
				 * This is done because of our assumption (mentioned above) that 
				 * the current page is in sync with the displaypage regarding placeholders
				 * and portlets.
				 */
				removePlaceHoldersFromPage(displayPage);
				copyPlaceHolders(getCastedModel(), displayPage);
				displayPagePart.refreshChildren();
			}
		}
		
		
		refreshChildren();
		//getFigure().repaint();
		//refreshVisuals();
	}
	/*else if (Page.CHILD_ADDED_PROP.equals(prop)
			|| Page.CHILD_REMOVED_PROP.equals(prop)) {
		refreshChildren();
		getFigure().repaint();
		
	}*/else if(Page.PLACEHOLDER_ADDED_PROP.equals(prop) || Page.PLACEHOLDER_REMOVED_PROP.equals(prop)){
		System.out.println("PageEditpart: Page.PLACEHOLDER_ADDED_PROP noticed");
		//handleChildChange(evt);
		refreshChildren();
	}else if(MainPage.SUBPAGE_ADDED_PROP.equals(prop) ){
		System.out.println("PageEditpart: Page.SUBPAGE_ADDED_PROP noticed");
		//handleChildChange(evt);
		/*
		 * check whether it is the 1st subpage added to displayPage.
		 * If this is the case then add pageChildGroup to displayPage.
		 */
		//if ( getCastedModel() instanceof MainPage && getCastedModel().isDummy() 
		/*if ( (false == ((SubPage)evt.getNewValue()).isDummy()) && getCastedModel() instanceof MainPage && getCastedModel().isDummy() 
					&& 1 == getCastedModel().getNoOfSubPages() ){
			PageChildGroup pageChildGroup = new PageChildGroup();
			getCastedModel().addPageInnerGroup(pageChildGroup);
		}*/
		refreshChildren();
	}else if ( MainPage.SUBPAGE_REMOVED_PROP.equals(prop) ) {
		System.out.println("PageEditpart: MainPage.SUBPAGE_REMOVED_PROP noticed");
		
		SubPage removedSubPage = (SubPage) evt.getNewValue();
		int pageNoOfRemovedSubPage = removedSubPage.getPageNo();
		System.out.println("Removed subPage no. is : " + pageNoOfRemovedSubPage);
		refreshChildren();
		childParts = getChildren();
		
		//setting the page no. of remaining pages properly.
		/*for(int i = 0 ; i< childParts.size() ; i++){
			if(childParts.get(i) instanceof PageEditPart){
				page = (Page) ((PageEditPart)childParts.get(i)).getModel();
				if ( page.getPageNo() > pageNoOfRemovedSubPage ) {
				//if ( (false == page.isDummy()) && page.getPageNo() > pageNoOfRemovedPage ) {
					page.setPageNo(page.getPageNo() -1 );
				}
			}
		}
		*/
		if ( false == removedSubPage.isDummy() ) {
			
			/*
			 * set the indices properly of the remaining SubPages
			 */
			List siblingsOfRemovedSubPagePart = getChildren();
			BasicPageFigure SPFigure;
			for ( int i = 0 ; i < siblingsOfRemovedSubPagePart.size() ; i++ ) {
				if ( siblingsOfRemovedSubPagePart.get(i) instanceof SubPageEditPart 
						&& (false == ((SubPageEditPart)siblingsOfRemovedSubPagePart.get(i)).getCastedModel().isDummy())) {
					SubPage subPage = ((SubPageEditPart)siblingsOfRemovedSubPagePart.get(i)).getCastedModel();
					SPFigure  = ((SubPageEditPart)siblingsOfRemovedSubPagePart.get(i)).getCastedFigure();
					if ( subPage.getPageNo() > removedSubPage.getPageNo() ) {
						subPage.setPageNo(subPage.getPageNo()-1);
						SPFigure.setPageNo(subPage.getPageNo());
					}
				}
			}
			
			
			boolean isLastSubPage = true;
			int pageNoOfSubPageToBeSelected = Constants.INVALID;
			int noOfSubPagesBeforeDeletion = getCastedModel().getNoOfSubPages() +1 ;
			List children = getChildren();
			SubPage displaySubPage = null ;
			for ( int i = 0 ; i< children.size() ; i++ ) {
				if ( children.get(i) instanceof SubPageEditPart 
						&& true == ((SubPageEditPart)children.get(i)).getCastedModel().isDummy()) {
					noOfSubPagesBeforeDeletion--;
					displaySubPage = ((SubPageEditPart)children.get(i)).getCastedModel();
				}
				if ( children.get(i) instanceof SubPageEditPart 
						&& false == ((SubPageEditPart)children.get(i)).getCastedModel().isDummy()) {
					isLastSubPage = false ; 
				}
			}
			/*
			 * Copying data of displaySubPage to the removed SubPage 
			 * This is just to support undo of SubPage removal.
			 */
			if ( true == isLastSubPage ) {
				if ( null != displaySubPage ) {
					copySubPageProperties(displaySubPage, removedSubPage);
					copyPlaceHolders(displaySubPage, removedSubPage) ;
				}else if ( null == displaySubPage ) {
					//nothing to be done.
					
				}
			}
			else {
				if ( pageNoOfRemovedSubPage < noOfSubPagesBeforeDeletion ) {
					pageNoOfSubPageToBeSelected = pageNoOfRemovedSubPage ;
				}else if ( pageNoOfRemovedSubPage == noOfSubPagesBeforeDeletion) {
					
					if(1 == pageNoOfRemovedSubPage){
						/*
						 * this case will never occur....because this is the case
						 * of last subPage(data) deletion.
						 * and we have already taken care of the last subPage deletion.
						 */
						pageNoOfSubPageToBeSelected = Constants.INVALID;
					} else {
						pageNoOfSubPageToBeSelected = pageNoOfRemovedSubPage -1;
					}
				}else{
					System.out.println("Something wrong with deletion of SubPage.");
				}
				
				//pageNoOfSubPageToBeSelected = 1;
				/*List children = getFigure().getChildren();
				for ( int i = 0 ; i < children.size() ; i++ ){
					if(children.get(i) instanceof PageFigure 
							&& pageNoOfPageToBeSelected == ((PageFigure)children.get(i)).getPageNo()){
						((PageFigure)children.get(i)).setState(Constants.SELECTED);
						
					}
				}*/
				System.out.println("subpage to be selected is : " + pageNoOfSubPageToBeSelected);
				for ( int i = 0 ; i < childParts.size() ; i++ ){
					if(childParts.get(i) instanceof SubPageEditPart 
							&&  (false == ((SubPage)((SubPageEditPart)childParts.get(i)).getModel()).isDummy())
							&& (pageNoOfSubPageToBeSelected == ((SubPage)((SubPageEditPart)childParts.get(i)).getModel()).getPageNo())){
						((SubPageEditPart)childParts.get(i)).setRemoved(true);
						((SubPageEditPart)childParts.get(i)).setRemovedSubPage(removedSubPage);
						((SubPageEditPart)childParts.get(i)).setSelected(SELECTED);
						((SubPageEditPart)childParts.get(i)).setRemoved(false);
						
					}else if ( childParts.get(i) instanceof SubPageEditPart 
							&&  ((SubPage)((SubPageEditPart)childParts.get(i)).getModel()).isDummy() ){
						((DisplaySubPageFigure)((SubPageEditPart)childParts.get(i)).getFigure()).setPageNo(pageNoOfSubPageToBeSelected);
						
					}
				}
			}
		}else if (removedSubPage.isDummy()){
		/*
		 * Right now nothing to do because displaySubPage 
		 * is removed only in 2 cases :
		 * 1) when display state is changed from PAGE_CHILD_VISIBLE to 
		 * PAGE_CONTETN_VISIBLE. In that case, we copy the data of 
		 * displaySubPage to the associated subPage. So nothing to do here.
		 * 2) When last subPage is removed. In that case no need to copy displaySubpage.
		 * Just delete it. So again nothing to be done.
		 */	
		}
		
	
		
		
		
		//handleChildChange(evt);
		/*
		 * check whether it is the 1st subpage added to displayPage.
		 * If this is the case then add pageChildGroup to displayPage.
		 */
		/*if ( getCastedModel() instanceof MainPage && getCastedModel().isDummy() 
				&& 0 == getCastedModel().getNoOfSubPages() ){
			for(int i = 0 ; i < getChildren().size() ; i++){
				if ( getChildren().get(i) instanceof PageInnerGroupEditPart ){
					if(((PageInnerGroupEditPart)getChildren().get(i)).getModel() instanceof PageChildGroup){
						getCastedModel().removePageInnerGroup((PageChildGroup)((PageInnerGroupEditPart)getChildren().get(i)).getCastedModel());
					}
				}
			}
			//PageChildGroup pageChildGroup = new PageChildGroup();
			//getCastedModel().addPageInnerGroup(pageChildGroup);
			
		}*/
		//getFigure().repaint();
		//refresh();
		refreshChildren();
	} else if (Page.PAGE_INNER_GROUP_ADDED_PROP.equals(prop)){
			
			if ( evt.getNewValue() instanceof PageChildGroup ) {
				if ( getCastedModel().isDummy() ) {
					((DisplayPageFigure)getFigure()).setDrawPageChildGroup(true);
				}
			}
	} else if (Page.PAGE_INNER_GROUP_REMOVED_PROP.equals(prop)){
			
			if ( evt.getNewValue() instanceof PageChildGroup ) {
				((DisplayPageFigure)getFigure()).setDrawPageChildGroup(false);
			}
	} else if ( MainPage.DISPLAY_STATE_PROP.equals(prop) ) {
			System.out.println("PageEditpart: MainPage.DISPLAY_STATE_PROP noticed");
		
		Object previousState = evt.getOldValue();
		Object newState = evt.getNewValue();
		if(previousState.equals(Constants.PAGE_CONTENT_VISIBLE) && newState.equals(Constants.PAGE_CHILD_VISIBLE)){
			System.out.println(" content to child ");
			
			((BasicPageFigure)getFigure()).setDisplayState(Constants.PAGE_CHILD_VISIBLE);	
			/* If it is a displayPage then,
			 * there is no DisplaySubPage to display subPage content.
			 * So add the display subPage. DisplaySubPage is nothing but
			 * a subPage only having dummy set to true
			 */
			if(getCastedModel().isDummy()){
				SubPage displaySubPage = new SubPage();
				displaySubPage.setDummy(true);
				displaySubPage.setPageNo(getCastedModel().getSelectedSubPageNo());
				displaySubPage.setParentPageNo(getCastedModel().getSelectedParentPageNo());
				SubPage subPageToBeDisplayed = null;
				SubPage firstSubPage = null;
				//find the subPage of which we want to display the data in displaySubPage.
				List modelChildren = getModelChildren();
				for ( int i = 0 ; i < modelChildren.size() ; i++ ) {
					if ( modelChildren.get(i) instanceof SubPage 
							&& (false == ((SubPage)modelChildren.get(i)).isDummy()) ) { 
						System.out.println("subpageNo: " + ((SubPage)modelChildren.get(i)).getPageNo());
						System.out.println("parentpageNo: " + ((SubPage)modelChildren.get(i)).getParentPageNo());
						if ((getCastedModel().getSelectedParentPageNo() == ((SubPage)modelChildren.get(i)).getParentPageNo())
								&& (getCastedModel().getSelectedSubPageNo() == ((SubPage)modelChildren.get(i)).getPageNo()) ) {
						//if ( selectedSubPageNo == ((SubPage)modelChildren.get(i)).getPageNo() ) {
								subPageToBeDisplayed = (SubPage) modelChildren.get(i);
							}
						if ( (1 == ((SubPage)modelChildren.get(i)).getPageNo())  
								&& (getCastedModel().getPageNo() == ((SubPage)modelChildren.get(i)).getParentPageNo()) ){
						//if ( (1 == ((SubPage)modelChildren.get(i)).getPageNo()) ) {  
							firstSubPage = (SubPage) modelChildren.get(i);
						}
					}/*else if ( modelChildren.get(i) instanceof SubPage 
							&& (true == ((SubPage)modelChildren.get(i)).isDummy()) ) {
						displaySubPage = (SubPage)modelChildren.get(i);
						displaySubPage.setPageNo(selectedSubPageNo);
						displaySubPage.setParentPageNo(selectedParentPageNo);
					}*/
				}
				//System.out.println(" firstSubPage: " + firstSubPage);
				//System.out.println(" noofsubpages: " + getCastedModel().getNoOfSubPages());
				if ( subPageToBeDisplayed == null ) {
					if ( getCastedModel().getNoOfSubPages() > 0 ){
						subPageToBeDisplayed = firstSubPage;
						if ( null != firstSubPage ) {
							displaySubPage.setPageNo(firstSubPage.getPageNo());
							displaySubPage.setParentPageNo(firstSubPage.getParentPageNo());
						}
					}
				}
				//System.out.println(" subpagetobedisplayed: " + subPageToBeDisplayed);
				if ( null != subPageToBeDisplayed ){
					//write code here to copy data etc.
					copySubPageProperties(subPageToBeDisplayed, displaySubPage);
					
					copyPlaceHolders(subPageToBeDisplayed, displaySubPage);
					//copyPageData(subPageToBeDisplayed, displaySubPage);
					/*PlaceHolder placeHolder = new PlaceHolder();
					placeHolder.setLayout(Constants.LAYOUT_GENERIC_2_COLUMN);
					placeHolder.setName(Constants.LAYOUT_GENERIC_2_COLUMN_PLACEHOLDER1);
					placeHolder.setPlaceHolderNo(1);
					placeHolder.setRoles("NP,SPA");
					displaySubPage.addPlaceHolder(placeHolder);
					*/
					getCastedModel().addSubPage(displaySubPage);
					
				}
			}
		}else if(previousState.equals(Constants.PAGE_CHILD_VISIBLE) && newState.equals(Constants.PAGE_CONTENT_VISIBLE)){
			
			((BasicPageFigure)getFigure()).setDisplayState(Constants.PAGE_CONTENT_VISIBLE);	
			if ( true == getCastedModel().isDummy() ) {
				SubPage displaySubPage = null ;
				SubPage previouslySelectedSubPage = null ;
				//selectedSubPageNo = Constants.INVALID;
				//selectedParentPageNo = Constants.INVALID;
				getCastedModel().setSelectedSubPageNo(Constants.INVALID);
				getCastedModel().setSelectedParentPageNo(Constants.INVALID);
				for(int i = 0 ; i < getModelChildren().size() ; i++ ){
					if(getModelChildren().get(i) instanceof SubPage && ((SubPage)getModelChildren().get(i)).isDummy()){
						//selectedSubPageNo = ((SubPage)getModelChildren().get(i)).getPageNo();
						//selectedParentPageNo = ((SubPage)getModelChildren().get(i)).getParentPageNo();
						getCastedModel().setSelectedSubPageNo(((SubPage)getModelChildren().get(i)).getPageNo());
						getCastedModel().setSelectedParentPageNo(((SubPage)getModelChildren().get(i)).getParentPageNo());
						displaySubPage = (SubPage)getModelChildren().get(i);
					}
				}
				if ( null != displaySubPage ) {
					for(int i = 0 ; i < getModelChildren().size() ; i++ ){
						if(getModelChildren().get(i) instanceof SubPage && false == ((SubPage)getModelChildren().get(i)).isDummy()
								&& getCastedModel().getSelectedSubPageNo() == ((SubPage)getModelChildren().get(i)).getPageNo()
								&& getCastedModel().getSelectedParentPageNo() == ((SubPage)getModelChildren().get(i)).getParentPageNo() ) {
							previouslySelectedSubPage = (SubPage)getModelChildren().get(i);
						}
					}
					if ( null != previouslySelectedSubPage ){
						cleanSubPage(previouslySelectedSubPage);
						copyPlaceHolders(displaySubPage, previouslySelectedSubPage);
					}
					getCastedModel().removeSubPage(displaySubPage);
				}
			}
		}
		
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
	System.out.println("PageEditPart: in refreshVisuals");
	//Rectangle bounds = new Rectangle(getCastedModel().getLocation(),getCastedModel().getSize());
	Rectangle bounds = new Rectangle(50,50,50,50);
	Rectangle siteMapBounds = ((GraphicalEditPart)getParent()).getFigure().getBounds();
	//bounds = new Rectangle (x + xmargin,y+ ymargin,lx-2*xmargin,height);	
		bounds = new Rectangle (siteMapBounds.x + xmargin , siteMapBounds.y + ymargin + 100 ,
				siteMapBounds.width - 2*xmargin , siteMapBounds.height/20 ) ;	
	System.out.println("PageEditPart: siteMapBounds: " + siteMapBounds);
	System.out.println("PageEditPart: bounds: " + bounds);
	//if ( siteMapBounds.height == 0 && siteMapBounds.width == 0 ){
		bounds = new Rectangle(20,20,0,0);
	//}
	// notify parent container of changed position & location
	// if this line is removed, the XYLayoutManager used by the parent container 
	// (the Figure of the SiteMapEditPart), will not know the bounds of this figure
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
		 * if the user has clicked on the displayPage which is nothing but
		 * the tab's content display portion
		 */
		if ( getCastedModel().isDummy() ) {
			PageEditPart associatedPagePart = getDataPagePart(getCastedModel().getPageNo());
			associatedPagePart.getCastedFigure().setState(Constants.SELECTED);
			associatedPagePart.getFigure().repaint();
		}
		/*if user has clicked (selected) on the tab (button)(Main data page)
		 * Here add code to copy data from dummy to page and vice-versa.
		 */
		else if ( false == getCastedModel().isDummy() ) {
			List children = ((SiteMapEditPart)getParent()).getFigure().getChildren();
			System.out.println("\nPageEditPart: setselected(): clicked on tab.");
			
			/*SiteMapEditPart siteMapEditPart = (SiteMapEditPart) getParent();
			List siblingParts = siteMapEditPart.getChildren();
			for ( int i = 0 ;i < siblingParts.size() ; i++ ) {
				if(siblingParts.get(i) instanceof PageEditPart && notdummy)
			}*/
			
			SiteMap siteMap = (SiteMap) getParent().getModel();
			List siblings = siteMap.getChildren();
			MainPage displayPage = null;
			PageEditPart displayPageEditPart = getDisplayPagePart();
			displayPage = displayPageEditPart.getCastedModel();
			System.out.println("sibling dummy page is: " + displayPage);
			MainPage previouslySelectedPage = null;
			if(true == removed ){
				previouslySelectedPage = removedPage;
			} else {
				previouslySelectedPage = getDataPagePart(displayPage.getPageNo()).getCastedModel();
				/*for ( int i = 0 ; i < siblings.size() ; i++ ){
					ModelElement sibling = (ModelElement) siblings.get(i);
					if(sibling instanceof Page && (false == ((MainPage)sibling).isDummy()) 
							&& displayPage.getPageNo() == ((Page)sibling).getPageNo()){
						previouslySelectedPage = (Page) sibling;
						break;
					}
				}*/
			}
			//TODO
			/*
			 * Copy data of displayPage back to the page of which it was
			 * actually showing the data.
			 */
			System.out.println("frompage is display page : " + displayPage);
			System.out.println("toPage is  previousPage: " + previouslySelectedPage);
			System.out.println("chck it: layout of display page is: " + displayPage.getLayout());
			cleanPage(previouslySelectedPage);
			copyPageData(displayPage, previouslySelectedPage);
			/*
			 * Copy data of new selected page in display page.
			 */
			//clean up the display page
			cleanPage(displayPage);
			/*List displayPageChildren = displayPage.getChildren() ;
			int noOfChildren = displayPageChildren.size() ;
			System.out.println("displaypage children are: " + displayPageChildren.size());
			for(int i = 0 , j = 0 ; i < noOfChildren ; i++ ) { 
				if ( displayPageChildren.get(j) instanceof PlaceHolder ) {
					displayPage.removePlaceHolder((PlaceHolder)displayPageChildren.get(j));
					System.out.println("placeholder removed.....");
				}else{
					j++ ;
				}
			}*/
			displayPage.setPageNo(getCastedModel().getPageNo());
			
			copyPageData((MainPage)getModel(), displayPage);
			
			Page newSelectedPage = (Page)getModel();
			//Copy newSelectedPage's properties in displayPage
			/*displayPage.setPageNo(newSelectedPage.getPageNo());
			displayPage.setLayout(newSelectedPage.getLayout());
			displayPage.setName(newSelectedPage.getName());
			displayPage.setNoOfSubPages(newSelectedPage.getNoOfSubPages());
			displayPage.setTheme(newSelectedPage.getTheme());
			*/
			//addPlaceHolders(displayPage);
			displayPageEditPart.getCastedFigure().setPageNo(getCastedModel().getPageNo());
			displayPageEditPart.getFigure().repaint();
			for ( int i = 0 ; i < children.size() ; i++ ) {
				/*if ( children.get(i) instanceof DisplayPageFigure ){
					//settings to get info. which page is being selected
					List siblingParts = getParent().getChildren();
					
					for ( int j = 0 ; j < siblingParts.size() ; j++ ) {
						if(((PageEditPart)siblingParts.get(i)).getFigure() instanceof DisplayPageFigure){
							((Page)((PageEditPart)siblingParts.get(i)).getModel()).setPageNo(((Page)getModel()).getPageNo());
						}
					}
					
						((DisplayPageFigure)children.get(i)).setPageNo(((Page)getModel()).getPageNo());
					
					System.out.println("\nPageEditPart: setselected(): repaint start");
					((DisplayPageFigure)children.get(i)).repaint();
					System.out.println("\nPageEditPart: setselected(): repaint complete");
				}else*/ 
				if ( children.get(i) instanceof PageFigure 
						&& false == (children.get(i).equals(getFigure()))){
					((PageFigure)children.get(i)).setState(Constants.NORMAL);
				}
			}
		}
		//set the figure selected whatever it is(pageFigure or DisplayPageFigure)
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

 private void copyPageData(MainPage fromPage,MainPage toPage){
	 SubPage displaySubPage =  null ;
	 toPage.setPageNo(fromPage.getPageNo());
	 toPage.setLayout(fromPage.getLayout());
	 toPage.setName(fromPage.getName());
	 //toPage.setNoOfSubPages(fromPage.getNoOfSubPages());
	 toPage.setTheme(fromPage.getTheme());
	 toPage.setRoles(fromPage.getRoles());
	 toPage.setIconType(fromPage.getIconType());
	 toPage.setSelectedSubPageNo(fromPage.getSelectedSubPageNo());
	 toPage.setSelectedParentPageNo(fromPage.getSelectedParentPageNo());
	 //toPage.setDisplayState(fromPage.getDisplayState());
	 //TODO Copy selectedsubpageno and selectedParentPageNo
	 List fromPageChildren = fromPage.getChildren() ;
	 	
	 	//Find displaySubPage in fromPage (if it exists)
	 	for ( int i = 0 ; i < fromPageChildren.size() ; i++ ){
	 		if(fromPageChildren.get(i) instanceof SubPage 
	 				&& ((SubPage)fromPageChildren.get(i)).isDummy()){
	 			displaySubPage = (SubPage)fromPageChildren.get(i);
	 		}
	 	}	
	 	//Update frompage
	 	if ( null != displaySubPage ) {
			for ( int i = 0 ; i < fromPageChildren.size() ; i++ ) {
				if ( fromPageChildren.get(i) instanceof SubPage 
						&& ( false == ((SubPage)fromPageChildren.get(i)).isDummy() )
						&& displaySubPage.getPageNo() == ((SubPage)fromPageChildren.get(i)).getPageNo() ) {
					SubPage associatedSubPage = (SubPage)fromPageChildren.get(i);
					cleanSubPage(associatedSubPage);
					copyPlaceHolders(displaySubPage, associatedSubPage);
				}
			}
		}
	 	//copy data
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
			} else if ( fromPageChildren.get(i) instanceof SubPage ) {
				//code to copy subPages
				SubPage fromSubPage = (SubPage)fromPageChildren.get(i);
				if(false == fromSubPage.isDummy()){
					SubPage newSubPage = new SubPage();
					copySubPageProperties(fromSubPage,newSubPage);
					//toPage.setNoOfSubPages(toPage.getNoOfSubPages()+1);
					toPage.addSubPage(newSubPage);
					copyPlaceHolders(fromSubPage, newSubPage);
				}
			}
		}
		
		toPage.setDisplayState(fromPage.getDisplayState());
 }
 
 public void cleanPage(MainPage page){
 	 List pageChildren = page.getChildren() ;
		int noOfChildren = pageChildren.size() ;
		System.out.println("cleaning page: ");
		System.out.println("page is dummy : " + page.isDummy());
		System.out.println("page children are: " + pageChildren.size());
		for(int i = 0 , j = 0 ; i < noOfChildren ; i++ ) {
			System.out.println("i = "+i +" j = " + j + "child is : " + pageChildren.get(j).getClass().getName());
			if ( pageChildren.get(j) instanceof PlaceHolder ) {
				page.removePlaceHolder((PlaceHolder)pageChildren.get(j));
				System.out.println("placeholder removed.....");
				continue;
			}
			else if ( pageChildren.get(j) instanceof SubPage && false == ((SubPage)pageChildren.get(j)).isDummy()) {
				//TODO write code to remove subpages if required.
				//page.setNoOfSubPages(page.getNoOfSubPages()-1);
				int previousSize = pageChildren.size();
				int previousDisplayState = page.getDisplayState();
				
				page.removeSubPage((SubPage)pageChildren.get(j));
				System.out.println("subpage removed...");
				int newSize = pageChildren.size();
				if(page.isDummy()){
					j = j - (previousSize - newSize) + 1 ;
				}
				int newDisplayState = page.getDisplayState();
				if ( Constants.PAGE_CONTENT_VISIBLE == previousDisplayState
						&& Constants.PAGE_CHILD_VISIBLE == newDisplayState ){
					j--;
				}
				continue;
			}else{
				//clean pagechildgroup and displaysubpage also
				j++;
			}
		}
		
		int noOfChildrenRemoved = 0;
		int l = 0;
		int k =0;
		/*for(int i = 0 , j = 0; i < noOfChildren ; i++ ) {
			j = noOfChildren-i-1;
			if ( pageChildren.get(j) instanceof PlaceHolder ) {
				page.removePlaceHolder((PlaceHolder)pageChildren.get(j));
				System.out.println("placeholder removed.....");
				k++;
			}
			else if ( pageChildren.get(j) instanceof SubPage ) {
				//TODO write code to remove subpages if required.
				//page.setNoOfSubPages(page.getNoOfSubPages()-1);
				page.removeSubPage((SubPage)pageChildren.get(j));
				l++;
			}else{
				//clean pagechildgroup and displaysubpage also
				//j++;
			}
			noOfChildrenRemoved=k+l;
		}*/
		
		System.out.println("cleaning page: exiting");
		
 }
 public void cleanSubPage(SubPage page){
	 List pageChildren = page.getChildren() ;
		int noOfChildren = pageChildren.size() ;
		System.out.println("page children are: " + pageChildren.size());
		for(int i = 0 , j = 0 ; i < noOfChildren ; i++ ) { 
			if ( pageChildren.get(j) instanceof PlaceHolder ) {
				page.removePlaceHolder((PlaceHolder)pageChildren.get(j));
				System.out.println("placeholder removed.....");
			}else {
				j++;
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
		//System.out.println("fromPageChildren.size()  " + fromPageChildren.size());
	 for ( int i = 0 ; i < fromPageChildren.size() ; i++ ){
			
			if(fromPageChildren.get(i) instanceof PlaceHolder){
				//System.out.println(" placeholder: " + i  );
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
 
 private void copySubPageProperties(SubPage fromSubPage , SubPage toSubPage){
	 //toSubPage.setDummy(fromSubPage.isDummy());
	 toSubPage.setIconType(fromSubPage.getIconType());
	 toSubPage.setLayout(fromSubPage.getLayout());
	 toSubPage.setName(fromSubPage.getName());
	 toSubPage.setNoOfSubPages(fromSubPage.getNoOfSubPages());
	 toSubPage.setPageNo(fromSubPage.getPageNo());
	 toSubPage.setParentPageNo(fromSubPage.getParentPageNo());
	 toSubPage.setRoles(fromSubPage.getRoles());
	 toSubPage.setTheme(fromSubPage.getTheme());
 }
//helper function to get display page edit part.
 private PageEditPart getDisplayPagePart(){
	 PageEditPart displayPagePart = null;
	 List displayPageSiblingParts = null;
	 if(getModel() instanceof MainPage){
			displayPageSiblingParts = getParent().getChildren();
		}else if (getModel() instanceof SubPage){
			displayPageSiblingParts = getParent().getParent().getChildren();
		}
		for ( int i = 0 ; i < displayPageSiblingParts.size() ; i++ ) {
			if (displayPageSiblingParts.get(i) instanceof PageEditPart 
					&&((MainPage)((PageEditPart)displayPageSiblingParts.get(i)).getModel()).isDummy() ){
				displayPagePart = (PageEditPart) displayPageSiblingParts.get(i);
			}
		}
		return displayPagePart;
 }
 //helper function to get edit part of page having the given pageNo.
 private PageEditPart getDataPagePart(int pageNo){
	 PageEditPart dataPagePart = null;
	 List displayPageSiblingParts = getParent().getChildren();
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
	 //if ( rolesList.containsAll(parentRolesList) ) {
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
public MainPage getRemovedPage() {
	return removedPage;
}
public void setRemovedPage(MainPage removedPage) {
	this.removedPage = removedPage;
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