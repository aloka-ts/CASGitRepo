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
*     Package:  com.genband.m5.maps.ide.sitemap.figure
*
*     File:     BasicPageFigure.java
*
*     Desc:   	Base class for page figures.
*     					Different page figures(main page,display 
*     					page and subpage)	extend this class. 
*
*     Author    Date                Description
*    ---------------------------------------------------------
*     Genband   December 28, 2007   Initial Creation
*
**********************************************************************
**/

package com.genband.m5.maps.ide.sitemap.figure;

import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.graphics.Color;

import com.genband.m5.maps.ide.sitemap.util.Constants;

public class BasicPageFigure extends BasicFigure {
	Rectangle siteMapBounds = new Rectangle(0,0,500,500);
	private Color FGColor;
	private Color BGColor;
	private int pageNo = 1;
	private int iconType = Constants.NORMAL;
	private String pageName = "New Page";
	/*
	 * this is to keep track of what it is displaying right now.
	 */
	int displayState = Constants.PAGE_CONTENT_VISIBLE;
	
	public BasicPageFigure(){
		super();
		//System.out.println("\tBasicPageFigure: parent's bounds : " + getParent().getBounds());
		setFGColor(Constants.SELECTABLE_COLOR);
		//setBounds(new Rectangle(9,9,100,100));
		//Rectangle bounds = getBounds();
		//this.bounds = new Rectangle(bounds);
		//siteMapBounds = new Rectangle(bounds.x + 3 , bounds.y + 3 , bounds.width - 9 , bounds.height - 9);
		//System.out.println("");
		//add(ellipse);
		//add(rectFigure);
		}
	
	public Color getFGColor() {
		return FGColor;
	}
	public void setFGColor(Color color) {
		FGColor = color;
	}
	public Color getBGColor() {
		return BGColor;
	}
	public void setBGColor(Color color) {
		BGColor = color;
	}
	public int getPageNo() {
		return pageNo;
	}
	public void setPageNo(int pageNo) {
		this.pageNo = pageNo;
	}
	public int getIconType() {
		return iconType;
	}
	public void setIconType(int iconType) {
		this.iconType = iconType;
	}
	public String getPageName() {
		return pageName;
	}
	public void setPageName(String pageName) {
		this.pageName = pageName;
	}

	public int getDisplayState() {
		return displayState;
	}

	public void setDisplayState(int displayState) {
		this.displayState = displayState ;
	}

}
