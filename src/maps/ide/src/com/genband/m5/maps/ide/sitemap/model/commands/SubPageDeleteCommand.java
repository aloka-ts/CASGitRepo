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
*     File:     SubPageDeleteCommand.java
*
*     Desc:   	Command to delete a SubPage from MainPage.
*
*     Author    Date                Description
*    ---------------------------------------------------------
*     Genband   December 28, 2007   Initial Creation
*
**********************************************************************
**/
package com.genband.m5.maps.ide.sitemap.model.commands;

import java.util.Iterator;
import java.util.List;

import org.eclipse.gef.commands.Command;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;

import com.genband.m5.maps.ide.CPFPlugin;
import com.genband.m5.maps.ide.sitemap.model.MainPage;
import com.genband.m5.maps.ide.sitemap.model.Page;
import com.genband.m5.maps.ide.sitemap.model.PageChildGroup;
import com.genband.m5.maps.ide.sitemap.model.Shape;
import com.genband.m5.maps.ide.sitemap.model.SiteMap;
import com.genband.m5.maps.ide.sitemap.model.SubPage;
import com.genband.m5.maps.ide.sitemap.util.Constants;


/**
 * A command to remove a page from its parent.
 * The command can be undone or redone.
 * @author Genband
 */
public class SubPageDeleteCommand extends Command {
/** SubPage to remove. */
private final SubPage subPage;

/** MainPage to remove from. */
private final MainPage parent;
/** True, if page was removed from its parent. */
private boolean wasRemoved;

private int pageRemoved = Constants.INVALID;
/**
 * Create a command that will remove the page from its parent.
 * @param parent the SiteMap containing the child
 * @param child    the Page to remove
 * @throws IllegalArgumentException if any parameter is null
 */
public SubPageDeleteCommand(MainPage parent, SubPage subPage) {
	if (parent == null || subPage == null) {
		throw new IllegalArgumentException();
	}
	setLabel("SubPage deletion");
	this.parent = parent;
	this.subPage = subPage;
}

/* (non-Javadoc)
 * @see org.eclipse.gef.commands.Command#canUndo()
 */
public boolean canUndo() {
	return wasRemoved;
}

/* (non-Javadoc)
 * @see org.eclipse.gef.commands.Command#execute()
 */
public void execute() {
	System.out.println("SubPage delete Command execution started");
	System.out.println("Subpage no of page to be deleted: "+ subPage.getPageNo());

	Shell shell = CPFPlugin.getDefault().getWorkbench()
	.getActiveWorkbenchWindow().getShell();
	
	boolean status = MessageDialog.openConfirm(shell, "Confim Delete", "Do you want to delete " + subPage.getName() + " SubPage?");
	
	if ( true == status ) {
		pageRemoved = subPage.getPageNo();
		parent.setSubPageRemoved(pageRemoved);
		//page.setLastpageDeleted(pageRemoved);

		//parent.setNoOfSubPages(parent.getNoOfSubPages()-1);
		wasRemoved = parent.removeSubPage(subPage);
		CPFPlugin.getDefault().log("SubPage delete Command execution completed");
	}else{
		CPFPlugin.getDefault().log("SubPage delete Command execution completed \n but subpage is not deleted because user didn't confirm it.");
	}

	
}

/* (non-Javadoc)
 * @see org.eclipse.gef.commands.Command#redo()
 */
public void redo() {
	// remove the page
	pageRemoved = subPage.getPageNo();
	parent.setSubPageRemoved(pageRemoved);
	//page.setLastpageDeleted(pageRemoved);
	//parent.setNoOfSubPages(parent.getNoOfSubPages()-1);
	wasRemoved = parent.removeSubPage(subPage);

}


/* (non-Javadoc)
 * @see org.eclipse.gef.commands.Command#undo()
 */
public void undo() {
	// add a page
	pageRemoved = Constants.INVALID;
	//parent.setNoOfSubPages(parent.getNoOfSubPages()+1);
	parent.addSubPage(subPage);
	
}
}