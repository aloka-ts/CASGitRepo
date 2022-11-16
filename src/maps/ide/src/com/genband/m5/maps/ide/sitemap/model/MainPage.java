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
*     File:     MainPage.java
*
*     Desc:   	Model class for MainPage.
*
*     Author    Date                Description
*    ---------------------------------------------------------
*     Genband   December 28, 2007   Initial Creation
*
**********************************************************************
**/
package com.genband.m5.maps.ide.sitemap.model;

import java.util.List;

import com.genband.m5.maps.ide.sitemap.util.Constants;

public class MainPage extends Page{
	/** flag to decide whether this is an page or a dummy page 
	 * created to show contents of current page*/
	boolean dummy = false ;
	/*
	 * this is for display page only.
	 * this is to keep track of what it is displaying right now.
	 */
	int displayState = Constants.PAGE_CONTENT_VISIBLE;
	private int selectedSubPageNo = Constants.INVALID;
	private int selectedParentPageNo = Constants.INVALID;
	private int subPageRemoved = Constants.INVALID;

	/** Property ID to use when a subpage is added to this page. */
	public static final String SUBPAGE_ADDED_PROP = "MainPage.SubPageAdded";
	/** Property ID to use when a subpage is removed from this page. */
	public static final String SUBPAGE_REMOVED_PROP = "MainPage.SubPageRemoved";

	/** Property ID to use when display state is changed */
	public static final String DISPLAY_STATE_PROP = "MainPage.displayStateChanged";

	/** 
	 * Add a subpage to this page.
	 * @param s a non-null subpage instance
	 * @return true, if the subpage was added, false otherwise
	 */
	public boolean addSubPage(SubPage subPage) {
		if ( null != subPage ) {
			noOfSubPages++;
			/*
			 * If a subPage is added to display Page then
			 * check whether a pageChildGroup is there or not.
			 * If not there then add 1 to displayPage
			 */
			if ( true == isDummy() && (false == subPage.isDummy()) ) {
				List siblings = getChildren();
				boolean pageChildGroupExists = false;
				for ( int i = 0 ; i < siblings.size() ; i++ ) {
					if ( siblings.get(i) instanceof PageChildGroup ) {
						pageChildGroupExists = true ;
					}
				}
				if ( false == pageChildGroupExists ) {
					PageChildGroup pageChildGroup = new PageChildGroup();
					addPageInnerGroup(pageChildGroup);
				}
			}
			if (child.add(subPage)) {
				firePropertyChange(SUBPAGE_ADDED_PROP, null, subPage);
				return true;
			}
		}
		return false;
	}


	/**
	 * Remove a subpage from this page.
	 * @param s a non-null subpage instance;
	 * @return true, if the subpage was removed, false otherwise
	 */
	public boolean removeSubPage(SubPage s) {
		noOfSubPages-- ;
		if (s != null && child.remove(s)) {
			/*
			 * If a subPage is removed from display Page then
			 * check whether it was the last subPage or not.
			 * If it was the last subPage then delete PageChildGroup. 
			 */
			//if ( true == isDummy() && false == s.isDummy()) {
			if ( true == isDummy() && false == s.isDummy() ) {
				List children = getChildren();
				boolean isLastSubPage = true;
				PageChildGroup pageChildGroup = null ;
				SubPage displaySubPage = null ;
				for ( int i = 0 ; i < children.size() ; i++ ) {
					if ( children.get(i) instanceof SubPage && false == ((SubPage)children.get(i)).isDummy() ) {
						isLastSubPage = false ;
					}else if ( children.get(i) instanceof SubPage && true == ((SubPage)children.get(i)).isDummy() ) {
						displaySubPage = (SubPage)children.get(i);
					}else if ( children.get(i) instanceof PageChildGroup ) {
						pageChildGroup = (PageChildGroup)children.get(i);
					}
				}
				//if last subPage is removed then remove pageChildGroup
				if ( true == isLastSubPage ) {
					if ( null != pageChildGroup ) {
						removePageInnerGroup(pageChildGroup);
						if ( null != displaySubPage ) {
							removeSubPage(displaySubPage);
						}
					}
					setDisplayState(Constants.PAGE_CONTENT_VISIBLE);
				}
			}
			firePropertyChange(SUBPAGE_REMOVED_PROP, null, s);
			return true;
		}
		return false;
	}

	public int getPageSubRemoved() {
		return subPageRemoved;
	}

	public void setSubPageRemoved(int subPageRemoved) {
		this.subPageRemoved = subPageRemoved;
	}

	
	public boolean isDummy() {
		return dummy;
	}


	public void setDummy(boolean dummy) {
		this.dummy = dummy;
	}

	public int getDisplayState() {
		return displayState;
	}

	public void setDisplayState(int displayState) {
		int previousDisplayState = getDisplayState();
		this.displayState = displayState;
		firePropertyChange(MainPage.DISPLAY_STATE_PROP, previousDisplayState, displayState);
	}
	public String toString() {
		return "MainPage " + hashCode();
	}


	public int getSelectedSubPageNo() {
		return selectedSubPageNo;
	}


	public void setSelectedSubPageNo(int selectedSubPageNo) {
		this.selectedSubPageNo = selectedSubPageNo;
	}


	public int getSelectedParentPageNo() {
		return selectedParentPageNo;
	}


	public void setSelectedParentPageNo(int selectedParentPageNo) {
		this.selectedParentPageNo = selectedParentPageNo;
	}


}
