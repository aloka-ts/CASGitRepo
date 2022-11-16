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
*     Package:  com.genband.m5.maps.ide.sitemap.model.commands
*
*     File:     PageCreateCommand.java
*
*     Desc:   	Command to add a Page to Site Map.
*
*     Author    Date                Description
*    ---------------------------------------------------------
*     Genband   December 28, 2007   Initial Creation
*
**********************************************************************
**/

package com.genband.m5.maps.ide.sitemap.model.commands;

import java.util.ArrayList ;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Rectangle;

import org.eclipse.gef.commands.Command;

import com.genband.m5.maps.common.CPFConstants;
import com.genband.m5.maps.ide.model.CPFPortlet;
import com.genband.m5.maps.ide.sitemap.model.Page;
import com.genband.m5.maps.ide.sitemap.model.PageShape;
import com.genband.m5.maps.ide.sitemap.model.PlaceHolder;
import com.genband.m5.maps.ide.sitemap.model.Portlet;
import com.genband.m5.maps.ide.sitemap.model.PortletShape;
import com.genband.m5.maps.ide.sitemap.model.Shape;
import com.genband.m5.maps.ide.sitemap.model.SiteMap;
import com.genband.m5.maps.ide.sitemap.util.Constants;


/**
 * A command to add a portlet to placeholder.
 * The command can be undone or redone.
 * @author Genband
 */
public class PortletCreateCommand 
	extends Command 
{
	
/** The new portlet. */ 
private Portlet newPortlet;
/** placeholder to add to. */
private final PlaceHolder parent;
/** The bounds of the new portlet. */
private Rectangle bounds;

/**
 * Create a command that will add a new Portlet to a placeholder.
 * @param newPortlet the new Portlet that is to be added
 * @param parent the placeholder that will hold the new element
 * @param bounds the bounds of the new shape; the size can be (-1, -1) if not known
 * @throws IllegalArgumentException if any parameter is null, or the request
 * 						  does not provide a new Portlet instance
 */
public PortletCreateCommand(Portlet newPortlet, PlaceHolder parent, Rectangle bounds) {
	this.newPortlet = newPortlet;
	this.parent = parent;
	this.bounds = bounds;
	setLabel("Portlet creation");
}

/**
 * Can execute if all the necessary information has been provided. 
 * @see org.eclipse.gef.commands.Command#canExecute()
 */
public boolean canExecute() {
	return newPortlet != null && parent != null && bounds != null;
}

/* (non-Javadoc)
 * @see org.eclipse.gef.commands.Command#execute()
 */
public void execute() {
	/*newPage.setLocation(bounds.getLocation());
	Dimension size = bounds.getSize();
	if (size.width > 0 && size.height > 0)
		newPage.setSize(size);
	int noOfPages = parent.getNoOfPages() ;
	noOfPages ++ ;
	newPage.setDummy(false);
	newPage.setName("New Page" + noOfPages);
	newPage.setPageNo(noOfPages);
	newPage.setNoOfSubPages(0);
	newPage.setLayout(parent.getLayout());
	newPage.setTheme(parent.getTheme());
	PlaceHolder placeHolder1 = new PlaceHolder();
	PlaceHolder placeHolder2 = new PlaceHolder();
	if(newPage.getLayout().equals(Constants.LAYOUT_GENERIC_2_COLUMN)){
		placeHolder1.setName("Left");
		newPage.addPlaceHolder(placeHolder1);
		
		placeHolder2.setName("Right");
		newPage.addPlaceHolder(placeHolder2);
	}else if(newPage.getLayout().equals(Constants.LAYOUT_3_COLUMN)){
		placeHolder1.setName("Left");
		newPage.addPlaceHolder(placeHolder1);
		
		placeHolder2.setName("Center");
		newPage.addPlaceHolder(placeHolder2);

		PlaceHolder placeHolder3 = new PlaceHolder();
		placeHolder3.setName("Right");
		newPage.addPlaceHolder(placeHolder3);
	}*/
	CPFPortlet cpfPortlet = newPortlet.getCpfPortlet();
	System.out.println("PortletCreateCommand : parent roles are : " + parent.getRoles());
	if(null != cpfPortlet && null != cpfPortlet.getListScreen()){
		Map<CPFConstants.OperationType, List<String>> mappedRoles = cpfPortlet.getListScreen().getMappedRoles();
		List<String> listRoles = mappedRoles
		.get(CPFConstants.OperationType.LIST);
		System.out.println("PortletCreateCommand : cpfportlet roles are : " +makeString(listRoles) );
		
		if ( null != listRoles ) {
			List parentRolesList = parseString(parent.getRoles());
			List newPortletRoles = new ArrayList();
			if ( null == parentRolesList || 0 == parentRolesList.size() ){
				newPortlet.setRoles("");
			}else{
				for(int i = 0 ; i < listRoles.size() ; i++ ) {
					if(parentRolesList.contains(listRoles.get(i))){
						newPortletRoles.add(listRoles.get(i));
					}
				}
				
				newPortlet.setRoles(makeString(newPortletRoles));
				if(null == newPortletRoles || 0 == newPortletRoles.size()){
					newPortlet.setIconType(Constants.ERROR_ICON);
				}else if(newPortletRoles.containsAll(parentRolesList)){
					newPortlet.setIconType(Constants.NORMAL_ICON);
				}else{
					newPortlet.setIconType(Constants.WARNING_ICON);
				}
			}
		}
	}
	System.out.println("PortletCreateCommand : new portlet's roles are : " + newPortlet.getRoles());
	newPortlet.setHelpEnabled(newPortlet.isHelpEnabledInCPFPortlet());
	newPortlet.setHelpScreen(newPortlet.getHelpScreenFromCPFPortlet());
	parent.addPortlet(newPortlet);
}

/* (non-Javadoc)
 * @see org.eclipse.gef.commands.Command#redo()
 */
public void redo() {
	System.out.println(" parent is : " + parent);
	//parent.addPage(newPage);
	parent.addPortlet(newPortlet);
	
	//PageShape p = new PageShape();
	//parent.addChild(p);
}

/* (non-Javadoc)
 * @see org.eclipse.gef.commands.Command#undo()
 */
public void undo() {
	parent.removePortlet(newPortlet);
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
	if ( null == stringArray || stringArray.size() == 0 ) {
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