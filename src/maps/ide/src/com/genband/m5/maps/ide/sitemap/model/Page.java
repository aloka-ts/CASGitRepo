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
*     Package:  com.genband.m5.maps.ide.sitemap.model
*
*     File:     Page.java
*
*     Desc:   	An abstract class for page(mainpage and subpage).
*     					MainPage and SubPage extend this class.
*
*     Author    Date                Description
*    ---------------------------------------------------------
*     Genband   December 28, 2007   Initial Creation
*
**********************************************************************
**/

package com.genband.m5.maps.ide.sitemap.model;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.ICellEditorValidator;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.PropertyDescriptor;
import org.eclipse.ui.views.properties.TextPropertyDescriptor;

import com.genband.m5.maps.ide.sitemap.util.Constants;

/**
 * Model class for Page.
 * @author Genband 
 */
public abstract class Page extends ModelElement {

/** 
 * A static array of property descriptors. 
 * There is one IPropertyDescriptor entry per editable property.
 * @see #getPropertyDescriptors()
 * @see #getPropertyValue(Object)
 * @see #setPropertyValue(Object, Object)
 */
private static IPropertyDescriptor[] descriptors;

/** ID for the name property value (used for by the corresponding property descriptor).  */
public static final String PAGE_NAME = "Page.Name";
/** ID for the layout property value (used for by the corresponding property descriptor).  */
public static final String PAGE_LAYOUT = "Page.Layout";
/** ID for the theme property value (used for by the corresponding property descriptor).  */
public static final String PAGE_THEME = "Page.Theme";
/** ID for the roles property value (used for by the corresponding property descriptor).  */
public static final String PAGE_ROLES = "Page.Roles";

/** Property ID to use when a placeHolder is added to this page. */
public static final String PLACEHOLDER_ADDED_PROP = "Page.PlaceHolderAdded";
/** Property ID to use when a placeHolder is removed from this page. */
public static final String PLACEHOLDER_REMOVED_PROP = "Page.PlaceHolderRemoved";
/** Property ID to use when a pageInnerGroup is added to this page. */
public static final String PAGE_INNER_GROUP_ADDED_PROP = "Page.PageInnerGroupAdded";
/** Property ID to use when a pageInnerGroup is removed from this page. */
public static final String PAGE_INNER_GROUP_REMOVED_PROP = "Page.PageInnerGroupRemoved";

/** A 16x16 pictogram of Page */
private static final Image PAGE_ICON = createImage("page16.bmp");

private static String[] rolesArray = {"role1","role2","role3","role4"};

/** No. of sub pages in this page*/
protected int noOfSubPages = 0;
/** Index of this Page */
private int pageNo = 1;

int iconType = Constants.NORMAL_ICON;
protected List child = new ArrayList();

/*
 * Initializes the property descriptors array.
 * @see #getPropertyDescriptors()
 * @see #getPropertyValue(Object)
 * @see #setPropertyValue(Object, Object)
 */
static {
	descriptors = new IPropertyDescriptor[] { 
			new TextPropertyDescriptor(PAGE_NAME, "Name"), // id and description pair
			new TextPropertyDescriptor(PAGE_ROLES, "Roles"), // id and description pair
			new TextPropertyDescriptor(PAGE_LAYOUT, "Layout"), // id and description pair
			new TextPropertyDescriptor(PAGE_THEME, "Theme"), // id and description pair
			//new ComboBoxPropertyDescriptor(PAGE_ROLES, "Specify roles",rolesArray), // id and description pair
	};
	// use a custom cell editor validator for properties
	for (int i = 0; i < descriptors.length; i++) {
		((PropertyDescriptor) descriptors[i]).setValidator(new ICellEditorValidator() {
			public String isValid(Object value) {
				return null;
			}
		});
	}
} // static

public Page(){
}

protected static Image createImage(String name) {
	return null;//CPFPlugin.getDefault().getImageRegistry().get(name);
}

/** Name of this Page */
private String name = "New Page";
/** Layout of this page*/
private String layout = "layout";
/** Theme of this page*/
private String theme = "theme";

public Image getIcon() {
	return PAGE_ICON;
}

/**
 * Returns an array of IPropertyDescriptors for this Page.
 * <p>The returned array is used to fill the property view, when the edit-part corresponding
 * to this model element is selected.</p>
 * @see #descriptors
 * @see #getPropertyValue(Object)
 * @see #setPropertyValue(Object, Object)
 */
public IPropertyDescriptor[] getPropertyDescriptors() {
	return descriptors;
}

/**
 * Return the property value for the given propertyId, or null.
 * <p>The property view uses the IDs from the IPropertyDescriptors array 
 * to obtain the value of the corresponding properties.</p>
 * @see #descriptors
 * @see #getPropertyDescriptors()
 */
public Object getPropertyValue(Object propertyId) {
	if (PAGE_NAME.equals(propertyId)) {
		return name;
	}else if (PAGE_ROLES.equals(propertyId)) {
		return roles;
	}else if (PAGE_LAYOUT.equals(propertyId)) {
		return layout;
	}else if (PAGE_THEME.equals(propertyId)) {
		return theme;
	}
	return super.getPropertyValue(propertyId);
}

public String getLayout() {
	return layout;
}

public void setLayout(String layout) {
	this.layout = layout;
}

public String getTheme() {
	return theme;
}

public void setTheme(String theme) {
	this.theme = theme;
}

/**
 * Set the property value for the given property id.
 * If no matching id is found, the call is forwarded to the superclass.
 * The property view uses the IDs from the IPropertyDescriptors array to set the values
 * of the corresponding properties.
 * @see #descriptors
 * @see #getPropertyDescriptors()
 */
public void setPropertyValue(Object propertyId, Object value) {
	if (PAGE_NAME.equals(propertyId)) {
		name = (String)value;
		firePropertyChange(PAGE_NAME, null, name);
	}else if (PAGE_ROLES.equals(propertyId)) {
		String previousRoles = (String)roles;
		roles = (String)value;
		firePropertyChange(PAGE_ROLES, previousRoles , roles);
	} else if (PAGE_LAYOUT.equals(propertyId)) {
		String previousLayout = layout;
		String newLayout = (String)value;
		if(value.equals(Constants.LAYOUT_3_COLUMN) || value.equals(Constants.LAYOUT_GENERIC_2_COLUMN)){
			layout = (String)value;
			firePropertyChange(PAGE_LAYOUT, previousLayout, newLayout);
		}

		
		/*if(previousLayout.equals(Constants.LAYOUT_GENERIC_2_COLUMN) 
				&& newLayout.equals(Constants.LAYOUT_3_COLUMN)){
			//List modelChildren = getChildren();
			for ( int i = 0 ; i < getChildren().size() ; i++ ) {
				if ( getChildren().get(i) instanceof PlaceHolder ) {
					PlaceHolder placeHolder = (PlaceHolder)getChildren().get(i);
					placeHolder.setLayout(newLayout);
					if ( 1 == placeHolder.getPlaceHolderNo() ) {
						placeHolder.setName(Constants.LAYOUT_3_COLUMN_PLACEHOLDER1);
					}else if ( 2 == placeHolder.getPlaceHolderNo() ) {
						placeHolder.setName(Constants.LAYOUT_3_COLUMN_PLACEHOLDER2);
					}
					//firePropertyChange(PlaceHolder.PLACEHOLDER_LAYOUT, 
					//		Constants.LAYOUT_GENERIC_2_COLUMN, Constants.LAYOUT_3_COLUMN);
				}
			}
			PlaceHolder newPlaceHolder = new PlaceHolder();
			newPlaceHolder.setLayout(Constants.LAYOUT_3_COLUMN);
			newPlaceHolder.setName(Constants.LAYOUT_3_COLUMN_PLACEHOLDER3);
			newPlaceHolder.setPlaceHolderNo(3);
			addPlaceHolder(newPlaceHolder);
			
		}else if(previousLayout.equals(Constants.LAYOUT_3_COLUMN) 
				&& newLayout.equals(Constants.LAYOUT_GENERIC_2_COLUMN)){
			//TODO
		}*/

	} else if (PAGE_THEME.equals(propertyId)) {
		theme = (String)value;
		firePropertyChange(PAGE_THEME, null, theme);
	}/*else if (PLACEHOLDER_ADDED_PROP.equals(propertyId)) {
		firePropertyChange(PLACEHOLDER_ADDED_PROP, null, "abc");
	}*/ else {
		super.setPropertyValue(propertyId, value);
	}
}

/** Return a List of child components in this page. The returned List should not be modified. */
public List getChildren() {
	return child;
}
		/** 
 * Add a placeHolder to this page.
 * @param s a non-null placeHolder instance
 * @return true, if the placeHolder was added, false otherwise
 */
public boolean addPlaceHolder(PlaceHolder placeHolder) {
	if (placeHolder != null && child.add(placeHolder)) {
		firePropertyChange(PLACEHOLDER_ADDED_PROP, null, placeHolder);
		//firePropertyChange(PAGE_NAME, null, placeHolder);
		return true;
	}
	return false;
}
/**
 * Remove a placeHolder from this page.
 * @param s a non-null placeHolder instance;
 * @return true, if the placeHolder was removed, false otherwise
 */
public boolean removePlaceHolder(PlaceHolder placeHolder) {
	if (placeHolder != null && child.remove(placeHolder)) {
		firePropertyChange(PLACEHOLDER_REMOVED_PROP, null, placeHolder);
		return true;
	}
	return false;
}

/** 
 * Add a pageInnerGroup to this page.
 * @param s a non-null pageInnerGroup instance
 * @return true, if the pageInnerGroup was added, false otherwise
 */
public boolean addPageInnerGroup(PageInnerGroup pageInnerGroup) {
	if (pageInnerGroup != null && child.add(pageInnerGroup)) {
		firePropertyChange(PAGE_INNER_GROUP_ADDED_PROP, null, pageInnerGroup);
		//firePropertyChange(PAGE_NAME, null, placeHolder);
		return true;
	}
	return false;
}
/**
 * Remove a pageInnerGroup from this page.
 * @param s a non-null pageInnerGroup instance;
 * @return true, if the pageInnerGroup was removed, false otherwise
 */
public boolean removePageInnerGroup(PageInnerGroup pageInnerGroup) {
	if (pageInnerGroup != null && child.remove(pageInnerGroup)) {
		firePropertyChange(PAGE_INNER_GROUP_REMOVED_PROP, null, pageInnerGroup);
		return true;
	}
	return false;
}

public String toString() {
	return "Page " + hashCode();
}

public String getName() {
	return name;
}

public void setName(String name) {
	this.name = name;
}

public int getPageNo() {
	return pageNo;
}

public void setPageNo(int pageNo) {
	this.pageNo = pageNo;
}

public int getNoOfSubPages() {
	return noOfSubPages;
}

public void setNoOfSubPages(int noOfSubPages) {
	this.noOfSubPages = noOfSubPages;
}


public int getIconType() {
	return iconType;
}

public void setIconType(int iconType) {
	this.iconType = iconType;
}

}