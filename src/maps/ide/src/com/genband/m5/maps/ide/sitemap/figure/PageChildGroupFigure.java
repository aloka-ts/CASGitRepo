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
*     File:     PageChildGroupFigure.java
*
*     Desc:   	This a figure which is basically view for PageChildGroup.
*
*     Author    Date                Description
*    ---------------------------------------------------------
*     Genband   December 28, 2007   Initial Creation
*
**********************************************************************
**/

package com.genband.m5.maps.ide.sitemap.figure;

import org.eclipse.draw2d.Border;
import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.Ellipse;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.FigureCanvas;
import org.eclipse.draw2d.FreeformLayer;
import org.eclipse.draw2d.FreeformLayout;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.MarginBorder;
import org.eclipse.draw2d.MouseListener;
import org.eclipse.draw2d.MouseMotionListener;
import org.eclipse.draw2d.RectangleFigure;
import org.eclipse.draw2d.SWTGraphics;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Drawable;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Shell;

import com.genband.m5.maps.ide.sitemap.util.Constants;

public class PageChildGroupFigure extends BasicFigure {
	Rectangle displayPageBounds = new Rectangle(0,0,500,500);
	Color FGColor ;
	Color BGColor ;
	public PageChildGroupFigure() {
		
		FGColor = Constants.SELECTABLE_COLOR;
		state = Constants.NORMAL;
		bounds = new Rectangle(0,0,0,0);
		if ( getParent() instanceof DisplayPageFigure ) {
			((DisplayPageFigure)getParent()).setDrawPageChildGroup(true);
		}
	}
		
	public int getState() {
		return state;
	}
	public void setState(int state) {
		this.state = state;
	}
	@Override
	public void paintFigure(Graphics graphics) {
	
		Image pageChildGroupIcon = createImage("pageChildGroup16.bmp");
		//get parent's(displayPage's) bounds.So that we can adjust it accordingly.
		displayPageBounds = getParent().getBounds();
		//Rectangle siteMapBounds = getParent().getParent().getBounds();
		System.out.println("PageChildGroupFigure: paintFigure() entered");
		System.out.println("getBounds " + getBounds());
		System.out.println("getparent().getBounds() " + getParent().getBounds());
		
		System.out.println("PageChildGroupFigure: paintFigure() ");
		
		int fontHeight = graphics.getFontMetrics().getHeight();
		int iconWidth = 16;
		int iconHeight = 16;
		/*
		 int lineWidth = displayPageBounds.height/150;
		//int lineWidth = 5;
		int xMargin = 5;
		int yMargin = 5;
		xMargin = displayPageBounds.width/Constants.DISPLAYPAGE_PAGE_CHILD_GROUP_X_MARGIN_RATIO ; //10
		yMargin = displayPageBounds.height/Constants.DISPLAYPAGE_PAGE_CHILD_GROUP_Y_MARGIN_RATIO; //20
		
		int width = displayPageBounds.width - 2*xMargin;
		int height = displayPageBounds.height/Constants.DISPLAYPAGE_PAGE_CHILD_GROUP_HEIGHT_RATIO;
		int x = displayPageBounds.x + xMargin;
		int y = displayPageBounds.y + yMargin;
		bounds = new Rectangle(x,y,width,height);
		*/
		bounds = ((DisplayPageFigure)getParent()).getPageChildGroupBounds();
		int x = bounds.x;
		int y = bounds.y;
		int width = bounds.width;
		int height = bounds.height;
		
		setBounds(bounds);
		
		
		int startR = 152;
		int startG = 180;
		int startB = 208;
		int endR = 168;
		int endG = 193;
		int endB = 221;
		int currentR = 0;
		int currentG = 0;
		int currentB = 0;
		/*if ( 6 == Constants.DISPLAY_PAGE_SHADING ) {
			startB = 225;
			endB = endB - 30;
		}else if(2 == Constants.DISPLAY_PAGE_SHADING){
			startR = 230;
			startG = 230;
			startB = 230;
			endR = 215;
			endG = 215;
			endB = 210;
			
		}else if( 1 == Constants.DISPLAY_PAGE_SHADING){
			startR = 250;
			startG = 250;
			startB = 250;
			endR = 235;
			endG = 235;
			endB = 230;
		}*/
		
		double gradientR = (double)(startR - endR)/height;
		double gradientG = (double)(startG - endG)/height;
		double gradientB = (double)(startB - endB)/height;
		//int gradientG = startG - endG;
		//int gradientB = startB - endB;
		int adjustment = 0;
		
		height = height - 1 - 2 - adjustment;
		int i = 0 ;
		for ( i = 1 ; i < height ; i++ ){
			currentR = startR - (int)(gradientR*i);
			currentG = startG - (int)(gradientG*i);
			currentB = startB - (int)(gradientB*i);
			graphics.setForegroundColor(new Color(null,currentR,currentG,currentB));
			graphics.drawLine(x, y+i+1, x + width , y+i+1);
		}
		i++;
		
		/*graphics.setForegroundColor(new Color(null,236,235,230));
		//graphics.drawLine(x, y+i+1, x + width , y+i+1);
		i++;
		graphics.setForegroundColor(new Color(null,145,155,156));
		//graphics.drawLine(x, y+i+1, x + width , y+i+1);
		*/
		//top lines
		graphics.setForegroundColor(ColorConstants.white);//white
		
		graphics.drawLine(x, y+1, x + width-1 , y+1);
		
		graphics.setForegroundColor(new Color(null,145,155,156));
		//top boundary line
		graphics.drawLine(x, y, x + width-1 , y);
		
		//draw vertical boundaries
		//side lines and bottom lines
		graphics.drawLine(x, y, x, y + height);
		graphics.drawLine(x+width-1, y, x+width-1, y + height);
		graphics.setForegroundColor(ColorConstants.white);
		graphics.drawLine(x+1, y+1, x+1, y + height);
		graphics.setForegroundColor(new Color(null,141,225,244));
		graphics.drawLine(x+width-2, y+2, x+width-2, y + height);
		
		//graphics.drawLine(x+width-1, y, x+width-1, y + height+1+2+adjustment);
		graphics.drawImage(pageChildGroupIcon, x + Constants.PAGE_TAB_ICON_MARGIN, y + height/2-iconHeight/2);
		graphics.setForegroundColor(ColorConstants.black);
		graphics.drawText(getPageChildGroupName(), x+2*Constants.PAGE_TAB_ICON_MARGIN+iconWidth , y + height/2-fontHeight/2);
		
		/*Rectangle rectBounds = new Rectangle(x , y ,width  , height - 2*lineWidth);	
		Rectangle fillBounds = new Rectangle(x+lineWidth,y+lineWidth,width - 2*lineWidth, height - 4*lineWidth);
		int textXMargin = getPageChildGroupText().length()/2;
		int textX = width/2 - textXMargin ;
		int textY = y + (height - 2*lineWidth)/2  - fontHeight/2;
		
		if(Constants.NORMAL == state ){
			graphics.setForegroundColor(Constants.SELECTABLE_COLOR);
		}else if(Constants.SELECTED == state){
			graphics.setForegroundColor(Constants.SELECTION_COLOR);
		}else if(Constants.HOVER == state){
			graphics.setForegroundColor(Constants.HOVER_COLOR);
		}
		
		
		//graphics.setForegroundColor(FGColor);
		
		graphics.setLineWidth(lineWidth );
		graphics.drawRectangle(rectBounds);
		graphics.setBackgroundColor(Constants.PAGE_CHILD_GROUP_FILL_COLOR);
		graphics.fillRectangle(fillBounds);
		//paintChildren(graphics);
		graphics.setForegroundColor(Constants.FONT_COLOR);
		graphics.drawText(getPageChildGroupText(), textX, textY );
		*/
		System.out.println("PageChildGroupFigure: paintFigure() exiting ");
	}
	/*@Override
	public void addMouseMotionListener(MouseMotionListener listener) {
		super.addMouseMotionListener(listener);
		System.out.println("in addMouseMotionListener");
	}
	@Override
	public void addMouseListener(MouseListener listener) {
		// TODO Auto-generated method stub
		super.addMouseListener(listener);
		System.out.println("in addMouseListener");
	}*/
	private String getPageChildGroupName() {
		return "SubPages";
	}

	public void setFGColor(Color forgroundColor) {
		this.FGColor = forgroundColor;
	}

	public void setBGColor(Color backgroundColor) {
		this.BGColor = backgroundColor;
	}
}
