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
*     File:     PageFigure.java
*
*     Desc:   	Figure  which is basically nothing but a tab's upper portion
*     			(similar to button).
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
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Drawable;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.plugin.AbstractUIPlugin;

import com.genband.m5.maps.ide.CPFPlugin;
import com.genband.m5.maps.ide.sitemap.model.Header;
import com.genband.m5.maps.ide.sitemap.util.Constants;

public class PageContentPaneFigure extends BasicFigure {
	Rectangle siteMapBounds = new Rectangle(0,0,500,500);
	Color FGColor;
	Color BGColor;
	int pageNo = 1;
	int iconType = Constants.NORMAL;
	private String pageName = "New Page";
	public PageContentPaneFigure(Rectangle bounds) {
		System.out.print("in constructor of PageFigure");
		//super();
		FGColor = Constants.SELECTABLE_COLOR;
		//Rectangle bounds = getBounds();
		//this.bounds = new Rectangle(bounds);
		//siteMapBounds = new Rectangle(bounds.x + 3 , bounds.y + 3 , bounds.width - 9 , bounds.height - 9);
		//System.out.println("");
		//add(ellipse);
		//add(rectFigure);
	}
	public PageContentPaneFigure() {
		System.out.print("in constructor of PageFigure");
		//System.out.println("\tHeaderFigure: parent's bounds : " + getParent().getBounds());
		FGColor = Constants.SELECTABLE_COLOR;
		state = Constants.NORMAL;
		setBounds(new Rectangle(9,9,100,100));
		//Rectangle bounds = getBounds();
		//this.bounds = new Rectangle(bounds);
		//siteMapBounds = new Rectangle(bounds.x + 3 , bounds.y + 3 , bounds.width - 9 , bounds.height - 9);
		//System.out.println("");
		//add(ellipse);
		//add(rectFigure);
		}
		
	public int getState() {
		return state;
	}
	public void setState(int state) {
		this.state = state;
	}
	@Override
	public void paintFigure(Graphics graphics) {
	
		System.out.println("\nPageContentPaneFigure: paintFigure() entered ");
		//get parent's(SiteMap's) bounds.So that we can adjust it accordingly.
		siteMapBounds = getParent().getBounds();
		
		Rectangle headerBounds = null;
		Rectangle footerBounds = null;
		int headerYMargin = 0;
		int x = 0;
		int y = 0;
		
		int width = 0;
		int height = 0;
		int fontHeight = graphics.getFontMetrics().getHeight();
		int fontWidth = graphics.getFontMetrics().getAverageCharWidth();
		int textAdjustment = 2* fontWidth;
		int tabHeight = 0;
		int tabWidth = 0;
		int tabX = 0;
		int tabY = 0;
		int lineWidth = siteMapBounds.height/150;
		List<IFigure> siblingFigures = getParent().getChildren() ;
		System.out.println("pageNo is : " + pageNo + " name is : " + pageName);
		int flag = 0;
		for (int i = 0; i< siblingFigures.size(); i++){
			if ( siblingFigures.get(i) instanceof HeaderFigure ) {
				headerBounds = ((HeaderFigure)siblingFigures.get(i)).getBounds();
				headerYMargin = ((HeaderFigure)siblingFigures.get(i)).getYMargin();
			}else if ( siblingFigures.get(i) instanceof FooterFigure ) {
				footerBounds = ((FooterFigure)siblingFigures.get(i)).getBounds();
			}else if ( siblingFigures.get(i) instanceof PageFigure 
					&&  ((PageFigure)siblingFigures.get(i)).getPageNo() == pageNo) {
				tabHeight =  ((PageFigure)siblingFigures.get(i)).getBounds().height ;
				tabWidth =  ((PageFigure)siblingFigures.get(i)).getBounds().width ;
				tabY =  ((PageFigure)siblingFigures.get(i)).getBounds().y ;
				tabX = ((PageFigure)siblingFigures.get(i)).getBounds().x;
				
				flag = 1;
			}
		}
		if ( 0 == flag ) {
			System.out.println("PageContentPaneFigure: setting bounds to 10 and exiting");
			setBounds(new Rectangle(10,10,10,10));
			
			return;
		}
		
		x = headerBounds.x;
		width = headerBounds.width;
		y =  tabY +tabHeight;
		height = (footerBounds.y - headerYMargin) - y;
		
		bounds = new Rectangle(x,y,width,height);
		setBounds(bounds);
		width = width - 2 ;
		//graphics.setBackgroundColor(ColorConstants.black);
		//graphics.fillRectangle(bounds);
		
		//if(Constants.NORMAL == state){
			graphics.setForegroundColor(new Color(null,145,155,156));
			graphics.drawLine(x, y, x+width+1 , y);
			
			int startR = 252;
			int startG = 252;
			int startB = 254;
			int endR = 244;
			int endG = 243;
			int endB = 238;
			int currentR = 0;
			int currentG = 0;
			int currentB = 0;
			//startB = 225;
			//endB = endB - 30;
			double gradientR = (double)(startR - endR)/height;
			double gradientG = (double)(startG - endG)/height;
			double gradientB = (double)(startB - endB)/height;
			//int gradientG = startG - endG;
			//int gradientB = startB - endB;
			int adjustment = 2;
			height = height - 1 - 2 - adjustment;
			int i = 0 ;
			for ( i = 0 ; i < height ; i++ ){
				currentR = startR - (int)(gradientR*i);
				currentG = startG - (int)(gradientG*i);
				currentB = startB - (int)(gradientB*i);
				graphics.setForegroundColor(new Color(null,currentR,currentG,currentB));
				graphics.drawLine(x, y+i+1, x + width , y+i+1);
			}
			//draw 3 white(almost) lines(boundries)
			graphics.setForegroundColor(new Color(null,252,252,254));
			graphics.drawLine(x, y+i+1, x + width , y+i+1);//bottom horizontal
			graphics.drawLine(x+1, y+1, x+1 , y+i+1);// left vertical
			graphics.drawLine(x+width-2, y+1, x+width-2 , y+i+1);//right vertical
			
			//draw actual(Visible) boundary of tab
			i++;
			graphics.setForegroundColor(new Color(null,145,155,156));
			graphics.drawLine(x, y+i+1, x + width , y+i+1);//bottom horizontal
			graphics.drawLine(x, y, x , y+i+1);// left vertical
			graphics.drawLine(x+width-1, y, x+width-1 , y+i+1);//right vertical
			
			//draw shadow
			i++;
			graphics.setForegroundColor(new Color(null,208,206,191));
			graphics.drawLine(x, y+i+1, x + width , y+i+1);//bottom horizontal
			graphics.drawLine(x+width, y+1, x+width , y+i+1);//right vertical
			
			//draw light shadow
			i++;
			graphics.setForegroundColor(new Color(null,227,224,208));
			graphics.drawLine(x, y+i+1, x + width , y+i+1);//bottom horizontal
			graphics.drawLine(x+width+1, y, x+width+1 , y+i+1);//right vertical
			//2 dots of light shadow color at top right
			graphics.setForegroundColor(new Color(null,227,224,208));
			graphics.drawLine(x+width+2 , y,x+width+3,y);
			
			//draw white line on the tab selected
			graphics.setForegroundColor(new Color(null,252,252,254));
			graphics.drawLine(tabX+1 , y,tabX+tabWidth-3,y);
			
			
			//draw vertical boundaries
			//graphics.setForegroundColor(new Color(null,145,167,180));
			//graphics.drawLine(x, y, x, y + height+1+2+adjustment);
			//graphics.drawLine(x+width-1, y, x+width-1, y + height+1+2+adjustment);
			//graphics.drawText(pageName, x, y + height/2-fontHeight/2);
		/*}else if ( Constants.SELECTED == state || Constants.HOVER == state) {
			graphics.setForegroundColor(new Color(null,230,139,44));
			graphics.drawLine(x, y, x+width, y);
			graphics.setForegroundColor(new Color(null,255,199,60));
			graphics.drawLine(x, y+1, x+width, y+1);
			//graphics.setBackgroundColor(new Color(null,255,255,225));
			graphics.setBackgroundColor(ColorConstants.white);
			graphics.fillRectangle(x,y+2,width-1,height-2);
			if ( Constants.HOVER == state ){
			//	graphics.fillRectangle(x,y+2,width-1,height-2);
			//}else{
				graphics.setForegroundColor(new Color(null,236,235,230));
				int i = height-3;
				graphics.drawLine(x, y+i+1, x + width , y+i+1);
				i++;
				graphics.setForegroundColor(new Color(null,145,155,156));
				graphics.drawLine(x, y+i+1, x + width , y+i+1);
				
			}
			//Vertical Lines
			graphics.setForegroundColor(new Color(null,145,155,156));
			graphics.drawLine(x,y,x,y + height);
			graphics.drawLine(x+width-1,y,x+width-1,y+height);
			//graphics.drawText(pageName, x+2, y + height/2-fontHeight/2);
			graphics.drawText(pageName, x , y + height/2-fontHeight/2);
	
		}*/
		System.out.println("\nPageContentPaneFigure: paintFigure() exitiing ");
	}

	public int getPageNo(){
		return pageNo;
	}
	
	public String getPageName(){
		return pageName;
	}
	
	public void setFGColor(Color forgroundColor) {
		this.FGColor = forgroundColor;
	}

	public void setBGColor(Color backgroundColor) {
		this.BGColor = backgroundColor;
	}

	public void setPageName(String name) {
	pageName  = name ;	
	}
	
	public void setPageNo(int pageNo) {
		this.pageNo = pageNo;
	}
	
	protected static Image createImage(String name) {
		return CPFPlugin.getDefault().getImageRegistry().get(name);
	}
	public int getIconType() {
		return iconType;
	}
	public void setIconType(int iconType) {
		this.iconType = iconType;
	}

}
