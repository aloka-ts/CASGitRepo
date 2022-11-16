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
*     File:     SiteMap.java
*
*     Desc:   	Model for the SiteMap.
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

import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.jface.viewers.ICellEditorValidator;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.PropertyDescriptor;
import org.eclipse.ui.views.properties.TextPropertyDescriptor;

import com.genband.m5.maps.ide.CPFPlugin;
import com.genband.m5.maps.ide.sitemap.util.Constants;

/**
 * A container for multiple Components.
 * This is the "root" of the model data structure.
 * @author Genband 
 */
public class SiteMap extends ModelElement {
	//Right Now this size and location is not used.
	private Point location = new Point(0, 0);
	/** Size of this SiteMap. */
	private Dimension size = new Dimension(400,400);
	private static final long serialVersionUID = 1;

	private List child = new ArrayList();
	private String name = "SiteMap";
	private String layout = "Generic 2 Column";
	private String theme = "Default";
	private int noOfPages = -1;
	private int pageRemoved = Constants.INVALID;
	
	/** A 16x16 pictogram of SiteMap */
//	createImage("sitemap/icons/sitemap16.bmp");
	//private static final Image SITEMAP_ICON = createImage("sitemap16.bmp");

	/** 
	 * A static array of property descriptors.
	 * There is one IPropertyDescriptor entry per editable property.
	 * @see #getPropertyDescriptors()
	 * @see #getPropertyValue(Object)
	 * @see #setPropertyValue(Object, Object)
	 */
private static IPropertyDescriptor[] descriptors;
/** Property ID to use when a child is added to this sitemap. */
public static final String CHILD_ADDED_PROP = "SiteMap.ChildAdded";
/** Property ID to use when a child is removed from this sitemap. */
public static final String CHILD_REMOVED_PROP = "SiteMap.ChildRemoved";
/** Property ID to use when a header is added to this sitemap. */
public static final String HEADER_ADDED_PROP = "SiteMap.HeaderAdded";
/** Property ID to use when a header is removed from this sitemap. */
public static final String HEADER_REMOVED_PROP = "SiteMap.HeaderRemoved";
/** Property ID to use when a footer is added to this sitemap. */
public static final String FOOTER_ADDED_PROP = "SiteMap.FooterAdded";
/** Property ID to use when a footer is removed from this sitemap. */
public static final String FOOTER_REMOVED_PROP = "SiteMap.FooterRemoved";
/** Property ID to use when a page is added to this sitemap. */
public static final String PAGE_ADDED_PROP = "SiteMap.PageAdded";
/** Property ID to use when a page is removed from this sitemap. */
public static final String PAGE_REMOVED_PROP = "SiteMap.PageRemoved";
/** Property ID to use for its name */
public static final String SITEMAP_NAME_PROP = "SiteMap.name";
/** Property ID to use for its roles */
public static final String SITEMAP_ROLES_PROP = "SiteMap.roles";
/** Property ID to use for its layout */
public static final String SITEMAP_LAYOUT_PROP = "SiteMap.layout";
/** Property ID to use for its theme */
public static final String SITEMAP_THEME_PROP = "SiteMap.theme";

/** Property ID to use when the location of this siteMap is modified. 
 * btw most probably, we will never modify its location*/
public static final String SITEMAP_LOCATION_PROP = "SiteMap.Location";
/** Property ID to use when the size of this shape is modified. 
 */
public static final String SITEMAP_SIZE_PROP = "SiteMap.Size";


static {
	descriptors = new IPropertyDescriptor[] { 
			new TextPropertyDescriptor(SITEMAP_NAME_PROP, "Name"), // id and description pair
			new TextPropertyDescriptor(SITEMAP_ROLES_PROP, "Roles"),
			new TextPropertyDescriptor(SITEMAP_LAYOUT_PROP, "Layout"),
			new TextPropertyDescriptor(SITEMAP_THEME_PROP, "Theme"),
	};
	// use a custom cell editor validator for all four array entries
	for (int i = 0; i < descriptors.length; i++) {
		((PropertyDescriptor) descriptors[i]).setValidator(new ICellEditorValidator() {
			public String isValid(Object value) {
				return null;
			}
			});
	}
} // static

/**
 * Returns an array of IPropertyDescriptors for this shape.
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
	if (SITEMAP_NAME_PROP.equals(propertyId)) {
		return name;
	}
	if (SITEMAP_ROLES_PROP.equals(propertyId)) {
		return roles;
	}
	if (SITEMAP_LAYOUT_PROP.equals(propertyId)) {
		return layout;
	}
	if (SITEMAP_THEME_PROP.equals(propertyId)) {
		return theme;
	}
	return super.getPropertyValue(propertyId);

}

public void setPropertyValue(Object propertyId, Object value) {
	if (SITEMAP_NAME_PROP.equals(propertyId)) {
		name = (String)value;
	}if (SITEMAP_ROLES_PROP.equals(propertyId)) {
		String previousRoles = roles;
		roles = (String)value;
		firePropertyChange(SITEMAP_ROLES_PROP, previousRoles , roles);
	}if (SITEMAP_LAYOUT_PROP.equals(propertyId)) {
		if(value.equals(Constants.LAYOUT_3_COLUMN) || value.equals(Constants.LAYOUT_GENERIC_2_COLUMN)){
			layout = (String)value;
			
		}
		
	}if (SITEMAP_THEME_PROP.equals(propertyId)) {
		theme = (String)value;
	}else {
		super.setPropertyValue(propertyId, value);
	}
}

/*public Image getIcon() {
	return SITEMAP_ICON;
}*/

/**
 * Return the Size of the SiteMap.
 * @return a non-null Dimension instance
 */
public Dimension getSize() {
	return size.getCopy();
}

/**
 * Set the Size of this siteMap.
 * Will not modify the size if newSize is null.
 * @param newSize a non-null Dimension instance or null
 */
public void setSize(Dimension newSize) {
	if (newSize != null) {
		size.setSize(newSize);
		firePropertyChange(SITEMAP_SIZE_PROP, null, size);
	}
}

/**
 * Return the Location of this SiteMap.
 * @return a non-null location instance
 */
public Point getLocation() {
	return location.getCopy();
}

/**
 * Set the Location of this siteMap.
 * @param newLocation a non-null Point instance
 * @throws IllegalArgumentException if the parameter is null
 */
public void setLocation(Point newLocation) {
	if (newLocation == null) {
		throw new IllegalArgumentException();
	}
	location.setLocation(newLocation);
	firePropertyChange(SITEMAP_LOCATION_PROP, null, location);
}


protected static Image createImage(String name) {
	return CPFPlugin.getDefault().getImageRegistry().get(name);
	
}

/** 
 * Add a shape to this diagram.
 * @param s a non-null shape instance
 * @return true, if the shape was added, false otherwise
 */
public boolean addChild(Shape s) {
	if (s != null && child.add(s)) {
		firePropertyChange(CHILD_ADDED_PROP, null, s);
		return true;
	}
	return false;
}

/** 
 * Add header to this SiteMap.
 * @param s a non-null header instance
 * @return true, if the header was added, false otherwise
 */
public boolean addHeader(Header header) {
	if (header != null && child.add(header)) {
		firePropertyChange(HEADER_ADDED_PROP, null, header);
		return true;
	}
	return false;
}
/** 
 * Add footer  to this SiteMap.
 * @param s a non-null footer instance
 * @return true, if the footer was added, false otherwise
 */
public boolean addFooter(Footer footer) {
	if (footer != null && child.add(footer)) {
		firePropertyChange(FOOTER_ADDED_PROP, null, footer);
		return true;
	}
	return false;
}

/** 
 * Add page  to this SiteMap.
 * @param s a non-null page instance
 * @return true, if the page was added, false otherwise
 */
public boolean addPage(MainPage page) {
	if (page != null ) {
		noOfPages++;
		//page.setName("New Page" + noOfPages);
		//page.setPageNo(noOfPages);
		//page.setNoOfSubPages(0);
		//page.setLayout(layout);
		//page.setTheme(theme);
		if(((MainPage)page).isDummy()){
			page.setName("New Page " + 1);
			page.setPageNo(1);
			child.add(page);
			return true;
		}else{
			child.add(page);
			firePropertyChange(PAGE_ADDED_PROP, null, page);
			return true;
		}
	}
	return false;
}

/** Return a List of child components in this SiteMap. The returned List should not be modified. */
public List getChildren() {
	return child;
}

/**
 * Remove a shape from this diagram.
 * @param s a non-null shape instance;
 * @return true, if the shape was removed, false otherwise
 */
public boolean removeChild(Shape s) {
	if (s != null && child.remove(s)) {
		firePropertyChange(CHILD_REMOVED_PROP, null, s);
		return true;
	}
	return false;
}
/**
 * Remove header from this SiteMap.
 * @param header a non-null header instance;
 * @return true, if the header was removed, false otherwise
 */
public boolean removeHeader(Header header) {
 	if (header != null && child.remove(header)) {
		firePropertyChange(HEADER_REMOVED_PROP, null, header);
		return true;
	}
	return false;
}
/**
 * Remove footer from this SiteMap.
 * @param footer a non-null footer instance;
 * @return true, if the footer was removed, false otherwise
 */
public boolean removeFooter(Footer footer) {
	if (footer != null && child.remove(footer)) {
		firePropertyChange(FOOTER_REMOVED_PROP, null, footer);
		return true;
	}
	return false;
}
/**
 * Remove page from this SiteMap.
 * @param page a non-null page instance;
 * @return true, if the page was removed, false otherwise
 */
public boolean removePage(MainPage page) {
	System.out.println("removePage called" );
	if (page != null ) {
		if(((MainPage)page).isDummy()){
			return false ;
		} else {
			System.out.println("page removing");
			child.remove(page);
			System.out.println("page removed");
			noOfPages--;
			System.out.println("Now no. of pages is : " + noOfPages);
			firePropertyChange(PAGE_REMOVED_PROP, null, page);
			System.out.println("removePage exiting" );
			return true;
		}
	}
	return false;
}

public int getNoOfPages() {
	return noOfPages;
}

public void setNoOfPages(int noOfPages) {
	noOfPages = noOfPages;
}

public int getPageRemoved() {
	return pageRemoved;
}

public void setPageRemoved(int pageRemoved) {
	this.pageRemoved = pageRemoved;
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

public String getName() {
	return name;
}

public void setName(String name) {
	this.name = name;
}

public String getRoles() {
	return roles;
}

public void setRoles(String roles) {
	this.roles = roles;
}
}