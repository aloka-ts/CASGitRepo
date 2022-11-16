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
*     File:     SubPageFigure.java
*
*     Desc:   	Figure for subpage.
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
import com.sun.org.apache.regexp.internal.recompile;

public class SubPageFigure extends BasicPageFigure {
	
	int parentPageNo = Constants.INVALID;
	
	public SubPageFigure() {
		super();
		System.out.print("in constructor of SubPageFigure");
		}
	
	@Override
	public void paintFigure(Graphics graphics) {
		String pageIconName = null;
		pageIconName = "subpage16_" + getIconType() + ".bmp" ; 
		Image pageIcon = createImage(pageIconName);
			//get parent's(displayPage'S) bounds.So that we can adjust it accordingly.
		Rectangle displayPageBounds = getParent().getBounds();
		if(getParent() instanceof PageFigure){
			setBounds(new Rectangle(0,0,0,0));
			return;
		}
		Rectangle pageChildGroupBounds = null;
		int iconWidth = 12;
		int iconHeight = 12;
		int x = 0;
		int y = 0;
		int xMargin  = 0 ;
		int yMargin  = 0 ;
		
		int width = 2*Constants.SUBPAGE_TAB_ICON_MARGIN + iconWidth + Constants.SUBPAGE_TEXT_TAB_MARGIN;
		int height = 0;
		int fontHeight = graphics.getFontMetrics().getHeight();
		int fontWidth = graphics.getFontMetrics().getAverageCharWidth();
		int textAdjustment = 2* fontWidth;
		int selectedSubPageNo = Constants.INVALID;
		System.out.println("fontHeight = "+ fontHeight + "fontWidth = " + fontWidth);
		int lineWidth = displayPageBounds.height/150;
		List<IFigure> siblingFigures = getParent().getChildren() ;
		System.out.println("pageNo is : " + getPageNo() + " name is : " + getPageName());
		//find which subpage is currently selected
		DisplaySubPageFigure displaySubPageFigure = null ;
		for (int i = 0; i< siblingFigures.size(); i++){
			if (siblingFigures.get(i) instanceof DisplaySubPageFigure){
				selectedSubPageNo = ((DisplaySubPageFigure)siblingFigures.get(i)).getPageNo();
				displaySubPageFigure = (DisplaySubPageFigure)siblingFigures.get(i);
			}
		}
		for (int i = 0; i< siblingFigures.size(); i++){
			if (siblingFigures.get(i) instanceof PageChildGroupFigure){
				pageChildGroupBounds = ((PageChildGroupFigure)siblingFigures.get(i)).getBounds();
				//headerYMargin = ((PageChildGroupFigure)siblingFigures.get(i)).getYMargin();
			}
			if(siblingFigures.get(i) instanceof SubPageFigure && ((SubPageFigure)siblingFigures.get(i)).getPageNo()<getPageNo()){
				//System.out.println("SubPageFigure: getPageNo()" + ((SubPageFigure)siblingFigures.get(i)).getPageNo());
				//System.out.println("SubPageFigure: getPageNo()" + ((SubPageFigure)siblingFigures.get(i)).getPageName());
				//System.out.println("x = "  +x);
				x = x + ((SubPageFigure)siblingFigures.get(i)).getBounds().width ;
				if ( (Constants.INVALID != selectedSubPageNo) 
						&& (selectedSubPageNo == ((SubPageFigure)siblingFigures.get(i)).getPageNo()) ){
					x = x - 4 ;
				}
			}
		}
		xMargin = displayPageBounds.width/Constants.DISPLAYPAGE_SUBPAGE_X_MARGIN_RATIO;
		yMargin = displayPageBounds.height/Constants.DISPLAYPAGE_SUBPAGE_UPPER_Y_MARGIN_RATIO;
		//System.out.println("x = " + x);
		//DisplayPageFigure displayPageFigure = (DisplayPageFigure) getParent();
		
		//TODO check it
		//if(null != pageChildGroupBounds){
			y = pageChildGroupBounds.y + pageChildGroupBounds.height + yMargin;
			x = x + pageChildGroupBounds.x +2 + xMargin;
			//}
		//y = yMargin +20;
		//x = 
		//System.out.println("final x = " + x);
		
		/* Here we are setting the width of tab proportional to the width of parent.
		 * But I have commented this line.If you want to adjust tab width according 
		 * to parent width and not according to text length then uncomment next line
		 
		 * and comment the block of code.
		 */
		//width = width + displayPageBounds.width/Constants.DISPLAYPAGE_SUBPAGE_TAB_WIDTH_RATIO;
		
		/* Here we are setting tab's width according to the length of page name.
		 */
		if(getPageName().length()>6){
			width = width + getPageName().length()* fontWidth + textAdjustment;
		}else{
			width = width + 6 * fontWidth;
		}
		height = displayPageBounds.height/Constants.DISPLAYPAGE_SUBPAGE_TAB_HEIGHT_RATIO;
		//y = y + headerYMargin ;
		//x = x + (pageNo - 1) * width; 
		//width--;
		
		bounds = new Rectangle(x,y,width+1,height);
		//privateBounds = new Rectangle(x,y,width+1,height);
		setBounds(bounds);
		//graphics.setBackgroundColor(ColorConstants.black);
		//graphics.fillRectangle(bounds);
		//if ( Constants.PAGE_CONTENT_VISIBLE == ((DisplayPageFigure)getParent()).getDisplayState() ) {
		//	Constants.DISPLAY_PAGE_SHADING = 2 ;
		//}
		if ( Constants.NORMAL == state || Constants.HOVER == state ) {
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
				if(Constants.HOVER == state){
					//draw 2 orange shaded lines
					graphics.setForegroundColor(new Color(null,230,139,44));
					graphics.drawLine(x, y, x+width-1, y);
					graphics.setForegroundColor(new Color(null,255,199,60));
					graphics.drawLine(x, y+1, x+width-1, y+1);
				}
			}
			i++;
			//draw vertical boundaries
			graphics.setForegroundColor(new Color(null,145,167,180));
			graphics.drawLine(x, y, x, y + height+2+adjustment);
			graphics.drawLine(x+width, y, x+width, y + height+2+adjustment);
			graphics.drawImage(pageIcon, x + Constants.SUBPAGE_TAB_ICON_MARGIN, y + height/2-iconHeight/2);
			graphics.setForegroundColor(ColorConstants.black);
			graphics.drawText(getPageName(), x+2*Constants.SUBPAGE_TAB_ICON_MARGIN+iconWidth , y + height/2-fontHeight/2);
		}else if ( Constants.SELECTED == state ) {
			if ( Constants.HOVER == state ) {
			//	y = y+ 2 ;
			}
			if ( Constants.SELECTED == state ) {
				width = width + 4 ;
				x = x - 2 ;
				bounds = new Rectangle(x,y,width+1,height);
				setBounds(bounds);
					
			
			//draw 2 orange shaded lines
			graphics.setForegroundColor(new Color(null,230,139,44));
			graphics.drawLine(x, y, x+width-1, y);
			graphics.setForegroundColor(new Color(null,255,199,60));
			graphics.drawLine(x, y+1, x+width-1, y+1);
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
			graphics.drawImage(pageIcon, x + Constants.SUBPAGE_TAB_ICON_MARGIN, y + height/2-iconHeight/2 -2);
			graphics.setForegroundColor(ColorConstants.black);
			graphics.drawText(getPageName(), x+2*Constants.SUBPAGE_TAB_ICON_MARGIN+iconWidth , y + height/2-fontHeight/2 -2);
			}
		}
		int i = height;
		if ( null == displaySubPageFigure ) {
			graphics.setForegroundColor(new Color(null,236,235,230));
			graphics.drawLine(x+1, y+i+1, x + width-1 , y+i+1);
			i++;
			graphics.setForegroundColor(new Color(null,145,155,156));
			graphics.drawLine(x, y+i+1, x + width-1 , y+i+1);
			//Constants.DISPLAY_PAGE_SHADING = 1 ;
		}
	}
	public int getParentPageNo() {
		return parentPageNo;
	}
	public void setParentPageNo(int parentPageNo) {
		this.parentPageNo = parentPageNo;
	}
	
}
