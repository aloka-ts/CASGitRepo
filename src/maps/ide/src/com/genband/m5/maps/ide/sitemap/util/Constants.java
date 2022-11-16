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
*     Package:  com.genband.m5.maps.ide.sitemap.util
*
*     File:     Constants.java
*
*     Desc:   	Constants used to create graphical Editor for sitemap.
*     			if you want to change some look and feel(like colors,
*     			margin , width of different components, line spacing etc.) 
*     			then probably, it can help a little bit.
*     			
*
*     Author    Date                Description
*    ---------------------------------------------------------
*     Genband   December 28, 2007   Initial Creation
*
**********************************************************************
**/
package com.genband.m5.maps.ide.sitemap.util;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.swt.graphics.Color;

public class Constants {
	
	public static final int INVALID = -100;
	public static int NORMAL_ICON = 0;
	public static int WARNING_ICON = 1;
	public static int ERROR_ICON = 2;
	public static Color SELECTION_COLOR = new Color(null, 220,   0,   0) ;//red
	public static Color HOVER_COLOR = new Color(null, 255, 196, 0);//orange
	public static Color SHADED_COLOR = new Color(null, 64, 64, 64);//darkGray
	public static Color SELECTABLE_COLOR = new Color(null, 128, 128, 128);//gray
	public static Color NONSELECTABLE_COLOR = new Color(null, 192, 192, 192);//lightGray
	public static Color TEXT_COLOR = new Color(null,255,255,255);//white
	public static Color PORTLET_CONTENT_LINE_COLOR = new Color(null,64,64,64);//black
	public static Color SELECTED_TEXT_COLOR = new Color(null,255,255,255);//white
	
	//public static Color HEADER_FOOTER_FILL_COLOR = new Color(null, 127, 127, 255);//lightBlue
	public static Color HEADER_FOOTER_FILL_COLOR = new Color(null, 87, 132, 163);//sun type
	public static Color FONT_COLOR = new Color(null,255,255,255);//white
	
	public static int CANVAS_SITEMAP_X_MARGIN_RATIO = 60 ;
	public static int CANVAS_SITEMAP_Y_MARGIN_RATIO = 60 ;
	
	public static int SITEMAP_HEADER_FOOTER_X_MARGIN_RATIO = 40 ;
	public static int SITEMAP_HEADER_FOOTER_Y_MARGIN_RATIO = 55 ;
	public static int SITEMAP_HEADER_FOOTER_HEIGHT_RATIO = 17 ;
	
	public static int SITEMAP_PAGE_TAB_HEIGHT_RATIO = 20 ;
	public static int SITEMAP_PAGE_TAB_WIDTH_RATIO = 12 ;
	public static int PAGE_TAB_ICON_MARGIN = 6 ;
	public static int PAGE_TEXT_TAB_MARGIN = 6 ;
	public static int DISPLAYPAGE_SUBPAGE_TAB_HEIGHT_RATIO = 16 ;
	public static int DISPLAYPAGE_SUBPAGE_TAB_WIDTH_RATIO = 12 ;
	public static int SUBPAGE_TAB_ICON_MARGIN = 6 ;
	public static int SUBPAGE_TEXT_TAB_MARGIN = 6 ;
	public static int MAIN_PAGE = 1;
	public static int SUBPAGE = 2;
	public static int SUB_SUBPAGE = 3;
	

	public static int PAGE_PLACEHOLDER_X_MARGIN_RATIO = 50 ;
	public static int PAGE_PLACEHOLDER_Y_MARGIN_RATIO = 55 ;
	
	public static int PLACEHOLDER_PORTLET_X_MARGIN_RATIO = 50 ;
	public static int PLACEHOLDER_PORTLET_Y_MARGIN_RATIO = 55 ;
	public static int MAX_NO_OF_PORTLETS = 2 ;
	public static int PORTLET_ICON_MARGIN = 6 ;
	public static int PORTLET_TITLE_CONTENT_RATIO = 4;
	public static int PORTLET_HEIGHT_SHADOW_RATIO = 18;
	public static Color PORTLET_TITLE_FILL_COLOR = new Color(null, 87, 132, 163);//sun type
	public static Color PORTLET_TITLE_FILL_COLOR_HOVER = new Color(null, 87, 132, 200);//sun type
	public static Color PORTLET_TITLE_FILL_COLOR_SELECTED = new Color(null, 87, 132, 220);//sun type
	//public static int ICON_TEXT_TAB_MARGIN = 6 ;

	public static int NORMAL = 0;
	public static int SELECTED = 1;
	public static int HOVER = 2;
	public static int PARENT_SELECTED = 3;
	public static int CHILD_SELECTED = 4;
	

	public static String LAYOUT_GENERIC_2_COLUMN = "generic";
	public static String LAYOUT_GENERIC_2_COLUMN_PLACEHOLDER1 = "left";
	public static String LAYOUT_GENERIC_2_COLUMN_PLACEHOLDER2 = "center";
	public static double LAYOUT_GENERIC_2_COLUMN_PLACEHOLDER1_TO_PLACEHOLDER2_RATIO = .4;
	
	public static String LAYOUT_3_COLUMN = "3columns";
	public static String LAYOUT_3_COLUMN_PLACEHOLDER1 = "left";
	public static String LAYOUT_3_COLUMN_PLACEHOLDER2 = "center";
	public static String LAYOUT_3_COLUMN_PLACEHOLDER3 = "right";
	
	// FOR DISPLAY PAGE
	public static int BOTH_VISIBLE = 0;
	public static int PAGE_CONTENT_VISIBLE = 1;
	public static int PAGE_CHILD_VISIBLE = 2;
	
	public static int DISPLAYPAGE_PAGE_CHILD_GROUP_HEIGHT_RATIO = 17;
	public static int DISPLAYPAGE_PAGE_CONTENT_GROUP_HEIGHT_RATIO = 17;
	public static Color PAGE_CHILD_GROUP_FILL_COLOR = new Color(null, 87, 132, 163);//sun type
	public static Color PAGE_CONTENT_GROUP_FILL_COLOR = new Color(null, 87, 132, 163);//sun type
	public static int DISPLAYPAGE_PAGE_CONTENT_GROUP_X_MARGIN_RATIO = 40 ;
	public static int DISPLAYPAGE_PAGE_CONTENT_GROUP_Y_MARGIN_RATIO = 55 ;
	public static int DISPLAYPAGE_PAGE_CHILD_GROUP_X_MARGIN_RATIO = 40 ;
	public static int DISPLAYPAGE_PAGE_CHILD_GROUP_Y_MARGIN_RATIO = 55 ;
	
	public static int DISPLAYPAGE_SUBPAGE_X_MARGIN_RATIO = 40*2;
	public static int DISPLAYPAGE_SUBPAGE_UPPER_Y_MARGIN_RATIO = 65;
	public static int DISPLAYPAGE_SUBPAGE_LOWER_Y_MARGIN_RATIO = 30;
	
	public static double DISPLAYPAGE_DISPLAYSUBPAGE_HEIGHT_RATIO = 1/.72 ; 
	public static int ADD_NEW_ROLES_ADDED_TO_CHILDREN = 0; 
	public static int CHANGE_ICONS_OF_CHILDREN_ON_ADDITION_OF_NEW_ROLES = 1; 
	public static int ROLES_PROPAGATE_STRATEGY = ADD_NEW_ROLES_ADDED_TO_CHILDREN;
	public static int REMOVE_ROLES_FROM_CHILDREN_ALSO = 2; 
	public static int CHANGE_ICONS_OF_CHILDREN_ON_REMOVAL_OF_ROLES = 3; 
	public static int ROLES_PROPAGATE_STRATEGY_ON_REMOVAL_OF_ROLES = REMOVE_ROLES_FROM_CHILDREN_ALSO;
	
	/*
	 * 1 light gray //235
	 * 2 dark Gray //210
	 * 3 normal
	 * 4 light gray without gradient
	 * 5 dark without gradient
	 * 6 yellow
	 */
	public static int DISPLAY_PAGE_SHADING = 1;
}
