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

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;

import com.genband.m5.maps.ide.sitemap.util.Constants;

public class PageFigure extends BasicPageFigure {
	
	
	public PageFigure() {
		super();
		System.out.print("in constructor of PageFigure");
		}
	@Override
	public void paintFigure(Graphics graphics) {
		String pageIconName = null;
		pageIconName = "page16_" + getIconType() + ".bmp" ; 
		Image pageIcon = createImage(pageIconName);
			//get parent's(SiteMap's) bounds.So that we can adjust it accordingly.
		siteMapBounds = getParent().getBounds();
		
		Rectangle headerBounds = null;
		int iconWidth = 12;
		int iconHeight = 12;
		int headerYMargin = 0;
		int x = 0;
		int y = 0;
		
		int width = 2*Constants.PAGE_TAB_ICON_MARGIN + iconWidth + Constants.PAGE_TEXT_TAB_MARGIN;
		int height = 0;
		int fontHeight = graphics.getFontMetrics().getHeight();
		int fontWidth = graphics.getFontMetrics().getAverageCharWidth();
		int textAdjustment = 2* fontWidth;
		int selectedPageNo = -1;
		System.out.println("fontHeight = "+ fontHeight + "fontWidth = " + fontWidth);
		int lineWidth = siteMapBounds.height/150;
		List<IFigure> siblingFigures = getParent().getChildren() ;
		System.out.println("pageNo is : " + getPageNo() + " name is : " + getPageName());
		for (int i = 0; i< siblingFigures.size(); i++){
			if (siblingFigures.get(i) instanceof DisplayPageFigure){
				selectedPageNo = ((DisplayPageFigure)siblingFigures.get(i)).getPageNo();
			}
		}
		for (int i = 0; i< siblingFigures.size(); i++){
			if (siblingFigures.get(i) instanceof HeaderFigure){
				headerBounds = ((HeaderFigure)siblingFigures.get(i)).getBounds();
				headerYMargin = ((HeaderFigure)siblingFigures.get(i)).getYMargin();
			}
			if(siblingFigures.get(i) instanceof PageFigure && ((PageFigure)siblingFigures.get(i)).getPageNo()<getPageNo()){
				System.out.println("pageFigure: getPageNo()" + ((PageFigure)siblingFigures.get(i)).getPageNo());
				System.out.println("pageFigure: getPageNo()" + ((PageFigure)siblingFigures.get(i)).getPageName());
				System.out.println("x = "  +x);
				x = x + ((PageFigure)siblingFigures.get(i)).getBounds().width ;
				if ( selectedPageNo == ((PageFigure)siblingFigures.get(i)).getPageNo() ){
					x = x - 4 ;
				}
			}
		}
		System.out.println("x = " + x);
		y = headerBounds.y + headerBounds.height ;
		x = x + headerBounds.x + 2;
		System.out.println("final x = " + x);
		
		/* Here we are setting the width of tab proportional to the width of siteMap.
		 * But I have commented this line.If you want to adjust tab width according 
		 * to sitemap width and not according to text length then uncomment next line
		 
		 * and comment the block of code.
		 */
		//width = width + siteMapBounds.width/Constants.SITEMAP_PAGE_TAB_WIDTH_RATIO;
		
		/* Here we are setting tab's width according to the length of page name.
		 */
		if(getPageName().length()>6){
			width = width + getPageName().length()* fontWidth + textAdjustment;
		}else{
			width = width + 6 * fontWidth;
		}
		height = siteMapBounds.height/Constants.SITEMAP_PAGE_TAB_HEIGHT_RATIO;
		y = y + headerYMargin ;
		//x = x + (pageNo - 1) * width; 
		//width--;
		
		bounds = new Rectangle(x,y,width+1,height);
		setBounds(bounds);
		//graphics.setBackgroundColor(ColorConstants.black);
		//graphics.fillRectangle(bounds);
		
		if(Constants.NORMAL == state){
			y = y + 2 ;
			graphics.setForegroundColor(new Color(null,145,167,180));
			graphics.drawLine(x, y, x + width-1 , y);
			int startR = 255;
			int startG = 255;
			int startB = 255;
			int endR = 240;
			int endG = 240;
			int endB = 234;
			int currentR = 0;
			int currentG = 0;
			int currentB = 0;
			if ( 6 == Constants.DISPLAY_PAGE_SHADING ) {
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
			}
			
			double gradientR = (double)(startR - endR)/height;
			double gradientG = (double)(startG - endG)/height;
			double gradientB = (double)(startB - endB)/height;
			//int gradientG = startG - endG;
			//int gradientB = startB - endB;
			int adjustment = 0;
			
			height = height - 1 - 2 - adjustment;
			int i = 0 ;
			for ( i = 0 ; i < height ; i++ ){
				currentR = startR - (int)(gradientR*i);
				currentG = startG - (int)(gradientG*i);
				currentB = startB - (int)(gradientB*i);
				graphics.setForegroundColor(new Color(null,currentR,currentG,currentB));
				graphics.drawLine(x, y+i+1, x + width , y+i+1);
			}
			graphics.setForegroundColor(new Color(null,236,235,230));
			//graphics.drawLine(x, y+i+1, x + width , y+i+1);
			i++;
			graphics.setForegroundColor(new Color(null,145,155,156));
			//graphics.drawLine(x, y+i+1, x + width , y+i+1);
			//draw vertical boundaries
			graphics.setForegroundColor(new Color(null,145,167,180));
			graphics.drawLine(x, y, x, y + height+1+2+adjustment);
			graphics.drawLine(x+width-1, y, x+width-1, y + height+1+2+adjustment);
			graphics.drawImage(pageIcon, x + Constants.PAGE_TAB_ICON_MARGIN, y + height/2-iconHeight/2);
			graphics.setForegroundColor(ColorConstants.black);
			graphics.drawText(getPageName(), x+2*Constants.PAGE_TAB_ICON_MARGIN+iconWidth , y + height/2-fontHeight/2);
		}else if ( Constants.SELECTED == state || Constants.HOVER == state || Constants.CHILD_SELECTED == state ) {
			if ( Constants.HOVER == state ) {
				y = y+ 2 ;
			}
			if ( Constants.SELECTED == state || Constants.CHILD_SELECTED == state ) {
				width = width + 4 ;
				x = x - 2 ;
				bounds = new Rectangle(x,y,width+1,height);
				setBounds(bounds);
					
			}
			if(Constants.CHILD_SELECTED == state){
				//draw 2 shaded lines
				graphics.setForegroundColor(new Color(null,255,228,159));
				graphics.drawLine(x, y, x+width-1, y);
				graphics.setForegroundColor(new Color(null,255,238,193));
				graphics.drawLine(x, y+1, x+width-1, y+1);
			}
			else{
				//draw 2 orange shaded lines
				graphics.setForegroundColor(new Color(null,230,139,44));
				graphics.drawLine(x, y, x+width-1, y);
				graphics.setForegroundColor(new Color(null,255,199,60));
				graphics.drawLine(x, y+1, x+width-1, y+1);
			}
			//graphics.setBackgroundColor(new Color(null,255,255,225));
			graphics.setBackgroundColor(new Color(null,252,252,254));
			if ( 2 == Constants.DISPLAY_PAGE_SHADING ) {
				graphics.setBackgroundColor(new Color(null,230,230,230));
			}else if ( 1 == Constants.DISPLAY_PAGE_SHADING ) {
				graphics.setBackgroundColor(new Color(null,250,250,250));
			}else if ( 6 == Constants.DISPLAY_PAGE_SHADING ) {
				graphics.setBackgroundColor(new Color(null,250,250,225));
			}
			graphics.fillRectangle(x,y+2,width-1,height-2);
		
			/*if ( Constants.HOVER == state ){
				graphics.setForegroundColor(new Color(null,236,235,230));
				int i = height-3;
				graphics.drawLine(x, y+i+1, x + width , y+i+1);
				i++;
				graphics.setForegroundColor(new Color(null,145,155,156));
				graphics.drawLine(x, y+i+1, x + width , y+i+1);
			}*/
			//Vertical Lines
			graphics.setForegroundColor(new Color(null,145,155,156));
			graphics.drawLine(x,y,x,y + height);
			graphics.drawLine(x+width-1,y,x+width-1,y+height);
			//graphics.drawText(pageName, x+2, y + height/2-fontHeight/2);
			graphics.drawImage(pageIcon, x + Constants.PAGE_TAB_ICON_MARGIN, y + height/2-iconHeight/2 -2);
			graphics.setForegroundColor(ColorConstants.black);
			graphics.drawText(getPageName(), x+2*Constants.PAGE_TAB_ICON_MARGIN+iconWidth , y + height/2-fontHeight/2 -2);
		}
	}
}
