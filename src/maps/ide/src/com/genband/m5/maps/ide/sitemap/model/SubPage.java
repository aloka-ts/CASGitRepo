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
*     File:     SubPage.java
*
*     Desc:   	Model class for SubPage.
*
*     Author    Date                Description
*    ---------------------------------------------------------
*     Genband   December 28, 2007   Initial Creation
*
**********************************************************************
**/
package com.genband.m5.maps.ide.sitemap.model;

import com.genband.m5.maps.ide.sitemap.util.Constants;

public class SubPage extends Page{
	public SubPage() {
		System.out.println("constructor of subpage");
		// TODO Auto-generated constructor stub
	}
	int parentPageNo = Constants.INVALID;
	boolean dummy = false;
	public boolean isDummy() {
		return dummy ;
	}
	
	public void setDummy(boolean dummy) {
		this.dummy = dummy;
	}

	public int getParentPageNo() {
		return parentPageNo;
	}

	public void setParentPageNo(int parentPageNo) {
		this.parentPageNo = parentPageNo;
	}
	public String toString() {
		return "SubPage " + hashCode();
	}

}
