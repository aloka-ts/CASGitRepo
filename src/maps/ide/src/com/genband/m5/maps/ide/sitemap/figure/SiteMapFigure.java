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
*     File:     SiteMapFigure.java
*
*     Desc:   	This a figure which is basically view for Site Map.
*
*     Author    Date                Description
*    ---------------------------------------------------------
*     Genband   December 28, 2007   Initial Creation
*
**********************************************************************
**/

package com.genband.m5.maps.ide.sitemap.figure;


import java.util.List;

import javax.swing.text.StyleConstants.FontConstants;

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
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.widgets.Shell;

import com.genband.m5.maps.ide.sitemap.util.Constants;

public class SiteMapFigure extends FreeformLayer {
	Rectangle bounds ;
	Rectangle siteMapBounds ;
	Color FGColor;
	Color BGColor;
	int state;
	
	public SiteMapFigure() {
		System.out.println("Sitemap figure created");
		FGColor = Constants.SELECTABLE_COLOR;
		state = Constants.NORMAL;
		bounds = new Rectangle(0,0,0,0);
	}
	
	@Override
	public void paintFigure(Graphics graphics) {
		System.out.println("paintFigure of SitemapFigure entered ");
		//super.paintFigure(graphics);
		int lineWidth = 2;
		graphics.setLineWidth(lineWidth );
		System.out.print("FGColor = " + FGColor);
		
		graphics.setForegroundColor(FGColor);
		//graphics.setBackgroundColor(ColorConstants.red);
		//siteMapBounds = new Rectangle(getBounds().x + 3 , getBounds().y + 3 , getBounds().width - 9 , getBounds().height - 9);
		int xMargin = getBounds().width/Constants.CANVAS_SITEMAP_X_MARGIN_RATIO;
		int yMargin = getBounds().width/Constants.CANVAS_SITEMAP_Y_MARGIN_RATIO;
		
		siteMapBounds = new Rectangle(getBounds().x + xMargin,getBounds().y + yMargin, 
				getBounds().width - 2*xMargin, getBounds().height - 2*yMargin);
		if(Constants.NORMAL == state){
			graphics.setForegroundColor(Constants.SELECTABLE_COLOR);
		}else if(Constants.SELECTED == state){
			graphics.setForegroundColor(Constants.SELECTION_COLOR);
		}else if(Constants.HOVER == state){
			graphics.setForegroundColor(Constants.HOVER_COLOR);
		}
		
		graphics.drawRoundRectangle(siteMapBounds,4,4);
		graphics.setBackgroundColor(new Color(null,245,245,245));
		graphics.fillRoundRectangle(siteMapBounds,4,4);
		//paintChildren(graphics);
		//Font font;
		//graphics.setFont(FontConstants.);
		graphics.setForegroundColor(ColorConstants.titleGradient);
		
		//graphics.drawText(getFilename(), siteMapBounds.x + 10, siteMapBounds.y - 6 );
		graphics.setForegroundColor(FGColor);
		System.out.println("chidren size : " + getChildren().size());
		IFigure child;
		for (int i = 0; i < getChildren().size(); i++) {
			child = (IFigure)getChildren().get(i);
				child.repaint();
		}
		System.out.println("paintFigure of SitemapFigure exit \n\n\n");
		
	}
	
/*	protected void paintChildren(Graphics graphics) {
		IFigure child;
		List children = getChildren();
		Rectangle clip = Rectangle.SINGLETON;
		for (int i = 0; i < children.size(); i++) {
			child = (IFigure)children.get(i);
			if (child.isVisible() && child.intersects(graphics.getClip(clip))) {
				graphics.clipRect(child.getBounds());
				child.paint(graphics);
				//graphics.restoreState();
			}
		}
	}
*/
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
	private String getFilename() {
		return "NP Sitemap.sitemap";
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
