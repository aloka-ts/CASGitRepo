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
*     File:     FooterFigure.java
*
*     Desc:   	This a figure which is basically view for footer.
*
*     Author    Date                Description
*    ---------------------------------------------------------
*     Genband   December 28, 2007   Initial Creation
*
**********************************************************************
**/

package com.genband.m5.maps.ide.sitemap.figure;

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

public class FooterFigure extends BasicFigure {
	Rectangle siteMapBounds ;
	Color FGColor;
	Color BGColor;

	public FooterFigure() {
		System.out.print("in constructor of FooterFigure");
		FGColor = Constants.SELECTABLE_COLOR;
		state = Constants.NORMAL;
		bounds = new Rectangle(0,0,0,0);
	}
		
	@Override
	public void paintFigure(Graphics graphics) {
		//get parent's(SiteMap's) bounds.So that we can adjust it accordingly.
		siteMapBounds = getParent().getBounds();
		
		
		int fontHeight = 12;
		int lineWidth = siteMapBounds.height/150;
		//int lineWidth = 5;
		int xmargin = 5;
		//if(siteMapBounds.width/Constants.SITEMAP_HEADER_FOOTER_X_MARGIN_RATIO>5){
			xmargin = siteMapBounds.width/Constants.SITEMAP_HEADER_FOOTER_X_MARGIN_RATIO ; //10
		//}
		//int ymargin = 5;
		//if (siteMapBounds.height/Constants.SITEMAP_HEADER_FOOTER_Y_MARGIN_RATIO>5){
			//ymargin = siteMapBounds.height/Constants.SITEMAP_HEADER_FOOTER_Y_MARGIN_RATIO ; //20
		//}
		//xmargin = 20;
		//ymargin = 20;
		int canvasSiteMapYMargin = siteMapBounds.width/Constants.CANVAS_SITEMAP_Y_MARGIN_RATIO;
		int height = siteMapBounds.height/Constants.SITEMAP_HEADER_FOOTER_HEIGHT_RATIO;
		int width = siteMapBounds.width - 2*xmargin;
		int x = siteMapBounds.x + xmargin;
		int y = siteMapBounds.y + siteMapBounds.height - canvasSiteMapYMargin - height - lineWidth;// - 2*lineWidth;
		bounds = new Rectangle(x,y,width,height);
		Rectangle rectBounds = new Rectangle(x , y ,width , height - 2*lineWidth);	
		Rectangle fillBounds = new Rectangle(x+lineWidth,y+lineWidth,width - 2*lineWidth, height - 4*lineWidth);
		int textXMargin = getFooterText().length()/2;
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
		
		graphics.setLineWidth(lineWidth );
		graphics.drawRectangle(rectBounds);
		graphics.setBackgroundColor(Constants.HEADER_FOOTER_FILL_COLOR);
		graphics.fillRectangle(fillBounds);
		//paintChildren(graphics);
		graphics.setForegroundColor(Constants.FONT_COLOR);
		graphics.drawText(getFooterText(), textX, textY );

		
		
		//int lineWidth = siteMapBounds.height/100;
		/*int lineWidth = 2;
		int xmargin = siteMapBounds.width/Constants.SITEMAP_HEADER_FOOTER_X_MARGIN_RATIO ; //10
		int ymargin = siteMapBounds.height/Constants.SITEMAP_HEADER_FOOTER_Y_MARGIN_RATIO ; //20
		int x = siteMapBounds.x + xmargin;
		int y = siteMapBounds.y + siteMapBounds.height - ymargin;
		int width = siteMapBounds.width - 2*xmargin;
		int height = siteMapBounds.height/Constants.SITEMAP_HEADER_FOOTER_HEIGHT_RATIO;
		Rectangle bounds = new Rectangle(x , y ,width , height) ;	
		Rectangle fillBounds = new Rectangle(x+lineWidth,y+lineWidth,width - 2*lineWidth, height - 2*lineWidth);
		int textXMargin = getFooterText().length()/2;
		int textX = width/2 - textXMargin ;
		int textY = y + height/2;
		setBounds(bounds);
		graphics.setForegroundColor(FGColor);
		graphics.drawRectangle(x, y, width, height);
		graphics.setLineWidth(lineWidth );
		graphics.setBackgroundColor(Constants.HEADER_FOOTER_FILL_COLOR);
		graphics.fillRectangle(fillBounds);
		//paintChildren(graphics);
		graphics.setForegroundColor(Constants.FONT_COLOR);
		graphics.drawText(getFooterText(), textX, textY );
		*/

		/*System.out.println("paintFigure of FooterFigure entered ");
		//super.paintFigure(graphics);
		int lineWidth = 2;
		graphics.setLineWidth(lineWidth );
		System.out.print("FGColor = " + FGColor);
		
		graphics.setForegroundColor(FGColor);
		graphics.setBackgroundColor(ColorConstants.lightBlue);
		graphics.fillRectangle(getBounds());
		//paintChildren(graphics);
		graphics.setForegroundColor(ColorConstants.black);
		graphics.drawText(getFooter(), getBounds().x + getBounds().width/2 - 10, getBounds().y + 10 );
		System.out.println("paintFigure of FooterFigure exit \n\n\n");
		*/
		
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
	private String getFooterText() {
		return "Footer";
	}

	public void setFGColor(Color forgroundColor) {
		this.FGColor = forgroundColor;
	}

	public void setBGColor(Color backgroundColor) {
		this.BGColor = backgroundColor;
	}
	public int getState() {
		return state;
	}
	public void setState(int state) {
		this.state = state;
	}

}
