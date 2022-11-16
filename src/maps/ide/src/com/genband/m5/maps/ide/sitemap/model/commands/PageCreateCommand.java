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

package com.genband.m5.maps.ide.sitemap.model.commands ;

import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.commands.Command;

import com.genband.m5.maps.ide.sitemap.model.MainPage;
import com.genband.m5.maps.ide.sitemap.model.PlaceHolder;
import com.genband.m5.maps.ide.sitemap.model.SiteMap;
import com.genband.m5.maps.ide.sitemap.util.Constants;


/**
 * A command to add a Page to Site Map.
 * The command can be undone or redone.
 * @author Genband
 */
public class PageCreateCommand 
	extends Command 
{
	
/** The new page. */ 
private MainPage newPage;
/** Site Map to add to. */
private final SiteMap parent;
/** The bounds of the new page. */
private Rectangle bounds;

/**
 * Create a command that will add a new Page to a SiteMap.
 * @param newPage the new Page that is to be added
 * @param parent the SiteMap that will hold the new page
 * @param bounds the bounds of the new Page; the size can be (-1, -1) if not known
 * @throws IllegalArgumentException if any parameter is null, or the request
 * 						  does not provide a new Page instance
 */
public PageCreateCommand(MainPage newPage, SiteMap parent, Rectangle bounds) {
	this.newPage = newPage;
	this.parent = parent;
	this.bounds = bounds;
	setLabel("Page creation");
}

/**
 * Can execute if all the necessary information has been provided. 
 * @see org.eclipse.gef.commands.Command#canExecute()
 */
public boolean canExecute() {
	return newPage != null && parent != null && bounds != null;
}

/* (non-Javadoc)
 * @see org.eclipse.gef.commands.Command#execute()
 */
public void execute() {
	/*Dimension size = bounds.getSize();
	if (size.width > 0 && size.height > 0)
		newPage.setSize(size);
	*/
	int noOfPages = parent.getNoOfPages() ;
	noOfPages ++ ;
	if(newPage instanceof MainPage){
		newPage.setDummy(false);
	}
	newPage.setName("New Page" + noOfPages);
	newPage.setPageNo(noOfPages);
	newPage.setNoOfSubPages(0);
	newPage.setLayout(parent.getLayout());
	newPage.setTheme(parent.getTheme());
	newPage.setRoles(parent.getRoles());
	newPage.setIconType(Constants.NORMAL_ICON);
	PlaceHolder placeHolder1 = new PlaceHolder();
	PlaceHolder placeHolder2 = new PlaceHolder();
	if(newPage.getLayout().equals(Constants.LAYOUT_GENERIC_2_COLUMN)){
		placeHolder1.setName(Constants.LAYOUT_GENERIC_2_COLUMN_PLACEHOLDER1);
		placeHolder1.setLayout(newPage.getLayout());
		placeHolder1.setPlaceHolderNo(1);
		placeHolder1.setRoles(newPage.getRoles());
		newPage.addPlaceHolder(placeHolder1);
		
		placeHolder2.setName(Constants.LAYOUT_GENERIC_2_COLUMN_PLACEHOLDER2);
		placeHolder2.setLayout(newPage.getLayout());
		placeHolder2.setPlaceHolderNo(2);
		placeHolder2.setRoles(newPage.getRoles());
		newPage.addPlaceHolder(placeHolder2);
	}else if(newPage.getLayout().equals(Constants.LAYOUT_3_COLUMN)){
		placeHolder1.setName(Constants.LAYOUT_3_COLUMN_PLACEHOLDER1);
		placeHolder1.setLayout(newPage.getLayout());
		placeHolder1.setPlaceHolderNo(1);
		placeHolder1.setRoles(newPage.getRoles());
		newPage.addPlaceHolder(placeHolder1);
		
		placeHolder2.setName(Constants.LAYOUT_3_COLUMN_PLACEHOLDER2);
		placeHolder2.setLayout(newPage.getLayout());
		placeHolder2.setPlaceHolderNo(2);
		placeHolder2.setRoles(newPage.getRoles());
		newPage.addPlaceHolder(placeHolder2);

		PlaceHolder placeHolder3 = new PlaceHolder();
		placeHolder3.setName(Constants.LAYOUT_3_COLUMN_PLACEHOLDER3);
		placeHolder3.setLayout(newPage.getLayout());
		placeHolder3.setPlaceHolderNo(3);
		placeHolder3.setRoles(newPage.getRoles());
		newPage.addPlaceHolder(placeHolder3);
	}
	parent.addPage(newPage);
}

/* (non-Javadoc)
 * @see org.eclipse.gef.commands.Command#redo()
 */
public void redo() {
	System.out.println(" parent is : " + parent);
	//parent.addPage(newPage);
	parent.addPage(newPage);
	
	//PageShape p = new PageShape();
	//parent.addChild(p);
}

/* (non-Javadoc)
 * @see org.eclipse.gef.commands.Command#undo()
 */
public void undo() {
	parent.removePage(newPage);
}
	
}