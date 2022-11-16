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
*     File:     HeaderFigure.java
*
*     Desc:   	This a figure which is basically view for header.
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
import org.eclipse.swt.widgets.Shell;

import com.genband.m5.maps.ide.sitemap.util.Constants;

public class HeaderFigure extends BasicFigure {
	Rectangle siteMapBounds = new Rectangle(0,0,500,500);
	Color FGColor ;
	Color BGColor ;
	int yMargin ;
	public HeaderFigure() {
		FGColor = Constants.SELECTABLE_COLOR;
		state = Constants.NORMAL;
		bounds = new Rectangle(0,0,0,0);
		yMargin = 0;
	}
		
	public int getState() {
		return state;
	}
	public void setState(int state) {
		this.state = state;
	}
	@Override
	public void paintFigure(Graphics graphics) {
	
		//get parent's(SiteMap's) bounds.So that we can adjust it accordingly.
		siteMapBounds = getParent().getBounds();
		
		System.out.println("HeaderFigure: paintFigure() entered");
		System.out.println("getBounds " + getBounds());
		System.out.println("getparent().getBounds() " + getParent().getBounds());
		
		System.out.println("HeaderFigure: paintFigure() ");
		int fontHeight = 12;
		int lineWidth = siteMapBounds.height/150;
		//int lineWidth = 5;
		int xMargin = 5;
		int canvasSiteMapYMargin = siteMapBounds.width/Constants.CANVAS_SITEMAP_Y_MARGIN_RATIO;
		//if(siteMapBounds.width/Constants.SITEMAP_HEADER_FOOTER_X_MARGIN_RATIO>5){
			xMargin = siteMapBounds.width/Constants.SITEMAP_HEADER_FOOTER_X_MARGIN_RATIO ; //10
		//}
		//if (siteMapBounds.height/Constants.SITEMAP_HEADER_FOOTER_Y_MARGIN_RATIO>5){
			yMargin = siteMapBounds.height/Constants.SITEMAP_HEADER_FOOTER_Y_MARGIN_RATIO ; //20
		//}
		//xmargin = 20;
		//ymargin = 20;
		
		int width = siteMapBounds.width - 2*xMargin;
		int height = siteMapBounds.height/Constants.SITEMAP_HEADER_FOOTER_HEIGHT_RATIO;
		int x = siteMapBounds.x + xMargin;
		int y = siteMapBounds.y + canvasSiteMapYMargin + yMargin;
		bounds = new Rectangle(x,y,width,height);
		Rectangle rectBounds = new Rectangle(x , y ,width  , height - 2*lineWidth);	
		Rectangle fillBounds = new Rectangle(x+lineWidth,y+lineWidth,width - 2*lineWidth, height - 4*lineWidth);
		int textXMargin = getHeaderText().length()/2;
		int textX = width/2 - textXMargin ;
		int textY = y + (height - 2*lineWidth)/2  - fontHeight/2;
		
		setBounds(bounds);
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
		graphics.setBackgroundColor(Constants.HEADER_FOOTER_FILL_COLOR);
		graphics.fillRectangle(fillBounds);
		//paintChildren(graphics);
		graphics.setForegroundColor(Constants.FONT_COLOR);
		graphics.drawText(getHeaderText(), textX, textY );
		System.out.println("HeaderFigure: paintFigure() ");
		System.out.println("getBounds " + getBounds());
		System.out.println("getParent().getBounds ()" + getParent().getBounds());
		System.out.println("rectBounds " + rectBounds);
		System.out.println("xmargin " + xMargin);
		System.out.println("ymargin " + yMargin);
		System.out.println("x: " + x);
		System.out.println("y: " + y);
		System.out.println("width: " + width);
		System.out.println("height: " + height);
		System.out.println("HeaderFigure: paintFigure() exiting ");
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
	private String getHeaderText() {
		return "Header";
	}

	public void setFGColor(Color forgroundColor) {
		this.FGColor = forgroundColor;
	}

	public void setBGColor(Color backgroundColor) {
		this.BGColor = backgroundColor;
	}

	public int getYMargin() {
		return yMargin;
	}

	public void setYMargin(int margin) {
		yMargin = margin;
	}
}
