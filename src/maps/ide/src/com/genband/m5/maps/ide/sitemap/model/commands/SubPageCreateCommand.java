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
*     File:     SubPageCreateCommand.java
*
*     Desc:   	Command to add a SubPage to MainPage.
*
*     Author    Date                Description
*    ---------------------------------------------------------
*     Genband   December 28, 2007   Initial Creation
*
**********************************************************************
**/

package com.genband.m5.maps.ide.sitemap.model.commands;

import java.util.List;

import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.commands.Command;

import com.genband.m5.maps.ide.sitemap.model.MainPage;
import com.genband.m5.maps.ide.sitemap.model.PlaceHolder;
import com.genband.m5.maps.ide.sitemap.model.SubPage;
import com.genband.m5.maps.ide.sitemap.util.Constants;

/**
 * A command to add a SubPage to a Page.
 * The command can be undone or redone.
 * @author Genband
 */
public class SubPageCreateCommand 
	extends Command 
{
	
/** The new subPage. */ 
private SubPage newSubPage;
/** Page to add to. */
private final MainPage parent;
/** The bounds of the new subPage. */
private Rectangle bounds;

/**
 * Create a command that will add a new SubPage to page.
 * @param newSubPage the new SubPage that is to be added
 * @param parent the Page that will hold the new SubPage.
 * @param bounds the bounds of the new subPage; the size can be (-1, -1) if not known
 * @throws IllegalArgumentException if any parameter is null, or the request
 * 						  does not provide a new SubPage instance
 */
public SubPageCreateCommand(SubPage newSubPage, MainPage parent, Rectangle bounds) {
	this.newSubPage = newSubPage;
	this.parent = parent;
	this.bounds = bounds;
	setLabel("SubPage creation");
}

/**
 * Can execute if all the necessary information has been provided. 
 * @see org.eclipse.gef.commands.Command#canExecute()
 */
public boolean canExecute() {
	return newSubPage != null && parent != null && bounds != null;
}

/* (non-Javadoc)
 * @see org.eclipse.gef.commands.Command#execute()
 */
public void execute() {
	/*Dimension size = bounds.getSize();
	if (size.width > 0 && size.height > 0)
		newSubPage.setSize(size);
	*/
	int noOfSubPages = parent.getNoOfSubPages() ;
	noOfSubPages ++ ;
	int newSubPageNo = noOfSubPages;
	newSubPage.setDummy(false);
	
	if ( true == isDisplaySubPageExists() ) {
		newSubPageNo = noOfSubPages - 1 ;
	}
	newSubPage.setName("New SubPage" + newSubPageNo);
	newSubPage.setPageNo(newSubPageNo);
	newSubPage.setParentPageNo(parent.getPageNo());
	System.out.println("yeahhh: subpageadded: parentpageno: " + newSubPage.getParentPageNo());
	newSubPage.setNoOfSubPages(0);
	newSubPage.setLayout(parent.getLayout());
	newSubPage.setTheme(parent.getTheme());
	newSubPage.setRoles(parent.getRoles());
	newSubPage.setIconType(Constants.NORMAL_ICON);
	PlaceHolder placeHolder1 = new PlaceHolder();
	PlaceHolder placeHolder2 = new PlaceHolder();
	if(newSubPage.getLayout().equals(Constants.LAYOUT_GENERIC_2_COLUMN)){
		placeHolder1.setName(Constants.LAYOUT_GENERIC_2_COLUMN_PLACEHOLDER1);
		placeHolder1.setLayout(newSubPage.getLayout());
		placeHolder1.setPlaceHolderNo(1);
		placeHolder1.setRoles(newSubPage.getRoles());
		newSubPage.addPlaceHolder(placeHolder1);
		
		placeHolder2.setName(Constants.LAYOUT_GENERIC_2_COLUMN_PLACEHOLDER2);
		placeHolder2.setLayout(newSubPage.getLayout());
		placeHolder2.setPlaceHolderNo(2);
		placeHolder2.setRoles(newSubPage.getRoles());
		newSubPage.addPlaceHolder(placeHolder2);
	}else if(newSubPage.getLayout().equals(Constants.LAYOUT_3_COLUMN)){
		placeHolder1.setName(Constants.LAYOUT_3_COLUMN_PLACEHOLDER1);
		placeHolder1.setLayout(newSubPage.getLayout());
		placeHolder1.setPlaceHolderNo(1);
		placeHolder1.setRoles(newSubPage.getRoles());
		newSubPage.addPlaceHolder(placeHolder1);
		
		placeHolder2.setName(Constants.LAYOUT_3_COLUMN_PLACEHOLDER2);
		placeHolder2.setLayout(newSubPage.getLayout());
		placeHolder2.setPlaceHolderNo(2);
		placeHolder2.setRoles(newSubPage.getRoles());
		newSubPage.addPlaceHolder(placeHolder2);
		
		PlaceHolder placeHolder3 = new PlaceHolder();
		placeHolder3.setName(Constants.LAYOUT_3_COLUMN_PLACEHOLDER3);
		placeHolder3.setLayout(newSubPage.getLayout());
		placeHolder3.setPlaceHolderNo(3);
		placeHolder3.setRoles(newSubPage.getRoles());
		newSubPage.addPlaceHolder(placeHolder3);
	}
	/*
	 * If a subpage is added to display Page then
	 * check whether a pageChildGroup is there or not.
	 * If not there then add 1 to displayPage
	 */
	/*if ( true == parent.isDummy() ) {
		List siblings = parent.getChildren();
		boolean pageChildGroupExists = false;
		for ( int i = 0 ; i < siblings.size() ; i++ ) {
			if ( siblings.get(i) instanceof PageChildGroup ) {
				pageChildGroupExists = true ;
			}
		}
		if ( false == pageChildGroupExists ) {
			PageChildGroup pageChildGroup = new PageChildGroup();
			parent.addPageInnerGroup(pageChildGroup);
		}
	}
	*/
	//parent.setNoOfSubPages(noOfSubPages);
	parent.addSubPage(newSubPage);
}

/* (non-Javadoc)
 * @see org.eclipse.gef.commands.Command#redo()
 */
public void redo() {
	System.out.println(" parent is : " + parent);
	//parent.addPage(newSubPage);
	//parent.setNoOfSubPages(parent.getNoOfSubPages()+1);
	parent.addSubPage(newSubPage);
	
	//PageShape p = new PageShape();
	//parent.addChild(p);
}

/* (non-Javadoc)
 * @see org.eclipse.gef.commands.Command#undo()
 */
public void undo() {
	SubPageDeleteCommand subPageDeleteCommand = new SubPageDeleteCommand(parent , newSubPage ) ;
	subPageDeleteCommand.execute();
	//parent.setNoOfSubPages(parent.getNoOfSubPages()-1);
	//parent.removeSubPage(newSubPage);

}
	private boolean isDisplaySubPageExists(){
		List siblings = parent.getChildren();
		for ( int i = 0 ; i < siblings.size() ; i++ ) {
			if(siblings.get(i) instanceof SubPage && true == ((SubPage)siblings.get(i)).isDummy()) {
				return true;
			}
		}
		return false;
	}
}