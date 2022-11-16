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
*     File:     DisplayPageFigure.java
*
*     Desc:   	Figure  which is basically the tab's content holding area.
*
*     Author    Date                Description
*    ---------------------------------------------------------
*     Genband   December 28, 2007   Initial Creation
*
**********************************************************************
**/

package com.genband.m5.maps.ide.sitemap.figure;

import java.util.List;

import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.graphics.Color;

import com.genband.m5.maps.ide.sitemap.util.Constants;

public class DisplayPageFigure extends BasicPageFigure {
	int pageChildGroupLastY = 0;
	int pageContentGroupLastY = 0;
	Rectangle pageChildGroupBounds = null;
	Rectangle pageContentGroupBounds = null;
	Rectangle displaySubPageBounds = null;
	boolean drawPageChildGroup = false;

	public DisplayPageFigure() {
		super();
		System.out.print("in constructor of DisplayPageFigure");
		setBounds(new Rectangle(9,9,100,100));
		}
		
	@Override
	public void paintFigure(Graphics graphics) {
	
		System.out.println("\n DisplayPageFigure: paintFigure() entered ");
		//get parent's(SiteMap's) bounds.So that we can adjust it accordingly.
		siteMapBounds = getParent().getBounds();
		pageChildGroupLastY = 0;
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
		System.out.println("pageNo is : " + getPageNo() + " name is : " + getPageName());
		int flag = 0;
		for (int i = 0; i< siblingFigures.size(); i++){
			if ( siblingFigures.get(i) instanceof HeaderFigure ) {
				headerBounds = ((HeaderFigure)siblingFigures.get(i)).getBounds();
				headerYMargin = ((HeaderFigure)siblingFigures.get(i)).getYMargin();
			}else if ( siblingFigures.get(i) instanceof FooterFigure ) {
				footerBounds = ((FooterFigure)siblingFigures.get(i)).getBounds();
			}else if ( siblingFigures.get(i) instanceof PageFigure 
					&&  ((PageFigure)siblingFigures.get(i)).getPageNo() == getPageNo()) {
				tabHeight =  ((PageFigure)siblingFigures.get(i)).getBounds().height ;
				tabWidth =  ((PageFigure)siblingFigures.get(i)).getBounds().width ;
				tabY =  ((PageFigure)siblingFigures.get(i)).getBounds().y ;
				tabX = ((PageFigure)siblingFigures.get(i)).getBounds().x;
				
				flag = 1;
			}
		}
		if ( 0 == flag ) {
			System.out.println("DisplayPageFigure: setting bounds to 10 and exiting");
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
			if ( 6 == Constants.DISPLAY_PAGE_SHADING ) {
				startB = 225;
				endB = endB - 30;
			}
			//dark gray
			if(2== Constants.DISPLAY_PAGE_SHADING){
				startR = 230;
				startG = 230;
				startB = 230;
				endR = 215;
				endG = 215;
				endB = 210;
					
			}
			
			//light gray
			if ( 1 == Constants.DISPLAY_PAGE_SHADING ) {
				startR = 250;
				startG = 250;
				startB = 250;
				endR = 235;
				endG = 235;
				endB = 230;
					
			}
			/*int startR = 244;
			int startG = 243;
			int startB = 238;
			int endR = 220;
			int endG = 220;
			int endB = 220;
			int currentR = 0;
			int currentG = 0;
			int currentB = 0;
			*/
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
			//graphics.setForegroundColor(new Color(null,230,230,230));
			if ( 2 == Constants.DISPLAY_PAGE_SHADING ) {
				graphics.setForegroundColor(new Color(null,230,230,230));
			} else if ( 1 == Constants.DISPLAY_PAGE_SHADING ) {
				graphics.setForegroundColor(new Color(null,250,250,250));
			} else if ( 6 == Constants.DISPLAY_PAGE_SHADING ) {
				graphics.setForegroundColor(new Color(null,252,252,254-30));
			}
			
			
			graphics.drawLine(tabX+1 , y,tabX+tabWidth-3,y);
			
			/*Rectangle pageContentGroupBounds = null;
			Rectangle subPageBounds = null;
			List child = getChildren();
			for ( i = 0 ; i< child.size() ; i++ ) {
				//write code here pagechildgroup
				if(child.get(i) instanceof PageChildGroupFigure){
					//pageChildGroupBounds = ((BasicFigure)child.get(i)).getBounds();
				}else if(child.get(i) instanceof PageContentGroupFigure){
					pageContentGroupBounds = ((BasicFigure)child.get(i)).getBounds();
				}else if(child.get(i) instanceof SubPageFigure){
					subPageBounds = ((BasicFigure)child.get(i)).getBounds();
				}
			}
			*/
			//////////Calculate SubPage bounds////////////
			
			//calculate pageChildGroupFigure bounds
			int pageChildGroupYMargin = height/Constants.DISPLAYPAGE_PAGE_CHILD_GROUP_Y_MARGIN_RATIO; //20
			int pageChildGroupY = y + pageChildGroupYMargin;
			int pageChildGroupHeight = height/Constants.DISPLAYPAGE_PAGE_CHILD_GROUP_HEIGHT_RATIO;
			int pageChildGroupXMargin = width/Constants.DISPLAYPAGE_PAGE_CHILD_GROUP_X_MARGIN_RATIO ; //10
			int pageChildGroupX = x + pageChildGroupXMargin;
			int pageChildGroupWidth = width - 2*pageChildGroupXMargin;
			//pageChildGroupWidth = 350;
			//pageChildGroupX = 30;
			pageChildGroupBounds = new Rectangle(pageChildGroupX,pageChildGroupY,pageChildGroupWidth,pageChildGroupHeight);
			//pageChildGroupBounds = new Rectangle(10,50,300,50);
			
			//calculate displaySubPageFigure bounds
			int displaySubPageHeight = (int)((double)height/Constants.DISPLAYPAGE_DISPLAYSUBPAGE_HEIGHT_RATIO);
			int subPageYMargin = height/Constants.DISPLAYPAGE_SUBPAGE_UPPER_Y_MARGIN_RATIO;
			int subPageXMargin = width/Constants.DISPLAYPAGE_SUBPAGE_X_MARGIN_RATIO;
			int subPageY = pageChildGroupBounds.y + pageChildGroupBounds.height + subPageYMargin;
			int subPageHeight = height/Constants.DISPLAYPAGE_SUBPAGE_TAB_HEIGHT_RATIO;
			int displaySubPageY = subPageY + subPageHeight +1;  
			int displaySubPageXMargin = width/Constants.DISPLAYPAGE_SUBPAGE_X_MARGIN_RATIO;
			int displaySubPageWidth = pageChildGroupBounds.width - 2*displaySubPageXMargin;
			int displaySubPageX = pageChildGroupBounds.x + subPageXMargin;
			displaySubPageBounds = new Rectangle(displaySubPageX,displaySubPageY,displaySubPageWidth,displaySubPageHeight);
			
			pageChildGroupLastY = pageChildGroupBounds.y ;
			//pageChildGroupLastY = subPageY + subPageHeight + subPageYMargin ;
			if ( true == isDrawPageChildGroup() || isPageChildGroupFigurePresent() ) {
				if ( Constants.PAGE_CHILD_VISIBLE == getDisplayState() ) {// || isDisplaySubPageFigurePresent() ){
					int tempyMargin = getBounds().height/Constants.DISPLAYPAGE_SUBPAGE_LOWER_Y_MARGIN_RATIO;
					pageChildGroupLastY = displaySubPageBounds.y + displaySubPageBounds.height; 
					pageChildGroupLastY = pageChildGroupLastY + (int)((double)tempyMargin*.6) ;
				
				}else if ( Constants.PAGE_CONTENT_VISIBLE == getDisplayState() ){ 
					pageChildGroupLastY = subPageY + subPageHeight + subPageYMargin ;
				}
			}
			//calculate PageContentGroup bounds
			int pageContentGroupXMargin = width/Constants.DISPLAYPAGE_PAGE_CONTENT_GROUP_X_MARGIN_RATIO ;
			int pageContentGroupYMargin = height/Constants.DISPLAYPAGE_PAGE_CONTENT_GROUP_Y_MARGIN_RATIO; //20
			
			int pageContentGroupX = x + pageContentGroupXMargin;
			int pageContentGroupWidth = width - 2*pageContentGroupXMargin;
			int pageContentGroupHeight = height/Constants.DISPLAYPAGE_PAGE_CONTENT_GROUP_HEIGHT_RATIO;
			int pageContentGroupY = pageChildGroupLastY + pageContentGroupYMargin;
			pageContentGroupBounds = new Rectangle(pageContentGroupX,pageContentGroupY,pageContentGroupWidth,pageContentGroupHeight);
			//pageContentGroupBounds = new Rectangle(10,120,300,100);
			
			//int subPageYMargin = height/Constants.DISPLAYPAGE_SUBPAGE_UPPER_Y_MARGIN_RATIO;
			//y = pageChildGroupBounds.y + pageChildGroupBounds.height + subPageYMargin;
			//////////////////////////////////////////////////////////////////////////
			
			if ( true == drawPageChildGroup || isPageChildGroupFigurePresent()) {
				int lineX = pageChildGroupBounds.x;
				int lineY = pageChildGroupBounds.y + pageChildGroupBounds.height - 2;
				//int yMargin = getBounds().height/Constants.DISPLAYPAGE_SUBPAGE_LOWER_Y_MARGIN_RATIO;
				//int lineLastX = lineX + pageChildGroupBounds.width ;
				//pageChildGroupWidth = pageChildGroupBounds.width ;
				
				/*graphics.setForegroundColor(ColorConstants.black);
				graphics.drawLine(lineX, lineY-2, lineX, pageChildGroupLastY);
				graphics.drawLine(lineLastX, lineY-2, lineLastX,pageChildGroupLastY);
				
				graphics.drawLine(lineX,pageChildGroupLastY , lineLastX, pageChildGroupLastY);
				graphics.setForegroundColor(ColorConstants.white);
				graphics.drawLine(lineX,pageChildGroupLastY-1 , lineLastX, pageChildGroupLastY-1);
				*/
				//draw 3 white(almost) lines(boundries)
				
				i = pageChildGroupLastY - lineY;
				
				graphics.setForegroundColor(new Color(null,252,252,254));
				graphics.drawLine(lineX, lineY+i+1, lineX + pageChildGroupWidth , lineY+i+1);//bottom horizontal
				graphics.drawLine(lineX+1, lineY+1, lineX+1 , lineY+i+1);// left vertical
				graphics.drawLine(lineX+pageChildGroupWidth-2, lineY+1, lineX+pageChildGroupWidth-2 , lineY+i+1);//right vertical
				
				//draw actual(Visible) boundarlineY of tab
				i++;
				graphics.setForegroundColor(new Color(null,145,155,156));
				graphics.drawLine(lineX, lineY+i+1, lineX + pageChildGroupWidth , lineY+i+1);//bottom horizontal
				graphics.drawLine(lineX, lineY, lineX , lineY+i+1);// left vertical
				graphics.drawLine(lineX+pageChildGroupWidth-1, lineY, lineX+pageChildGroupWidth-1 , lineY+i+1);//right vertical
				
				//draw shadow
				i++;
				graphics.setForegroundColor(new Color(null,208,206,191));
				graphics.drawLine(lineX, lineY+i+1, lineX + pageChildGroupWidth , lineY+i+1);//bottom horizontal
				graphics.drawLine(lineX+pageChildGroupWidth, lineY+1, lineX+pageChildGroupWidth , lineY+i+1);//right vertical
				
				//draw light shadow
				i++;
				graphics.setForegroundColor(new Color(null,227,224,208));
				graphics.drawLine(lineX, lineY+i+1, lineX + pageChildGroupWidth , lineY+i+1);//bottom horizontal
				graphics.drawLine(lineX+pageChildGroupWidth+1, lineY, lineX+pageChildGroupWidth+1 , lineY+i+1);//right vertical
				//2 dots of light shadow color at top right
				graphics.setForegroundColor(new Color(null,227,224,208));
				graphics.drawLine(lineX+pageChildGroupWidth+2 , lineY,lineX+pageChildGroupWidth+3,lineY);
				if ( Constants.PAGE_CONTENT_VISIBLE == getDisplayState() ){
					graphics.setBackgroundColor(new Color(null,245,244,234));//windows disable button color
					//graphics.setBackgroundColor(new Color(null,220,220,220));
					graphics.fillRectangle(lineX+1, lineY+1, pageChildGroupWidth-2, pageChildGroupLastY - lineY -1);
				}
			}
			
			//if ( null != pageContentGroupBounds ){
				int lineX = pageContentGroupBounds.x;
				int lineY = pageContentGroupBounds.y + pageContentGroupBounds.height - 2;
				int yMargin = getBounds().height/Constants.DISPLAYPAGE_SUBPAGE_LOWER_Y_MARGIN_RATIO;
				pageContentGroupLastY = 0 ;
				int lineLastX = lineX + pageContentGroupBounds.width ;
				/*if ( Constants.BOTH_VISIBLE == getDisplayState() ){
					pageContentGroupLastY = subPageBounds.y + subPageBounds.height + yMargin ;
				}else if ( Constants.PAGE_CHILD_VISIBLE == getDisplayState() ){
					//TODO
					Rectangle displaySubPageBounds = null;
					for(int j = 0 ; j< getChildren().size() ; j++ ){
						if(getChildren().get(j) instanceof DisplaySubPageFigure){
							displaySubPageBounds = ((BasicFigure)getChildren().get(j)).getBounds();
						}
					}
					pageContentGroupLastY = displaySubPageBounds.y + displaySubPageBounds.height + (int)((double)yMargin*.6) ;
				}else if ( Constants.PAGE_CONTENT_VISIBLE == getDisplayState() ){ 
					pageContentGroupLastY = subPageBounds.y + subPageBounds.height + yMargin ;	//same as BothVisible
				}*/
				
				pageContentGroupLastY = getBounds().y + getBounds().height - yMargin;
				/*graphics.setForegroundColor(ColorConstants.black);
				graphics.drawLine(lineX, lineY-2, lineX, pageContentGroupLastY);
				graphics.drawLine(lineLastX, lineY-2, lineLastX,pageContentGroupLastY);
				
				graphics.drawLine(lineX,pageContentGroupLastY , lineLastX, pageContentGroupLastY);
				graphics.setForegroundColor(ColorConstants.white);
				graphics.drawLine(lineX,pageContentGroupLastY-1 , lineLastX, pageContentGroupLastY-1);
				*/
				//draw 3 white(almost) lines(boundries)
				
				i = pageContentGroupLastY - lineY;
				
				graphics.setForegroundColor(new Color(null,252,252,254));
				graphics.drawLine(lineX, lineY+i+1, lineX + pageContentGroupWidth , lineY+i+1);//bottom horizontal
				graphics.drawLine(lineX+1, lineY+1, lineX+1 , lineY+i+1);// left vertical
				graphics.drawLine(lineX+pageContentGroupWidth-2, lineY+1, lineX+pageContentGroupWidth-2 , lineY+i+1);//right vertical
				
				//draw actual(Visible) boundarlineY of tab
				i++;
				graphics.setForegroundColor(new Color(null,145,155,156));
				graphics.drawLine(lineX, lineY+i+1, lineX + pageContentGroupWidth , lineY+i+1);//bottom horizontal
				graphics.drawLine(lineX, lineY, lineX , lineY+i+1);// left vertical
				graphics.drawLine(lineX+pageContentGroupWidth-1, lineY, lineX+pageContentGroupWidth-1 , lineY+i+1);//right vertical
				
				//draw shadow
				i++;
				graphics.setForegroundColor(new Color(null,208,206,191));
				graphics.drawLine(lineX, lineY+i+1, lineX + pageContentGroupWidth , lineY+i+1);//bottom horizontal
				graphics.drawLine(lineX+pageContentGroupWidth, lineY+1, lineX+pageContentGroupWidth , lineY+i+1);//right vertical
				
				//draw light shadow
				i++;
				graphics.setForegroundColor(new Color(null,227,224,208));
				graphics.drawLine(lineX, lineY+i+1, lineX + pageContentGroupWidth , lineY+i+1);//bottom horizontal
				graphics.drawLine(lineX+pageContentGroupWidth+1, lineY, lineX+pageContentGroupWidth+1 , lineY+i+1);//right vertical
				//2 dots of light shadow color at top right
				graphics.setForegroundColor(new Color(null,227,224,208));
				graphics.drawLine(lineX+pageContentGroupWidth+2 , lineY,lineX+pageContentGroupWidth+3,lineY);

			//}

			//graphics.fillRectangle(pageChildGroupBounds);
			//graphics.fillRectangle(lineX, lineY+pageChildGroupBounds.height, pageChildGroupBounds.width, pageChildGroupBounds.height);
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
			paintChildren(graphics);
			System.out.println("display state : " + displayState);
		System.out.println("\nDisplayPageFigure: paintFigure() exitiing ");
	}
	@Override
	protected void paintChildren(Graphics graphics) {
		System.out.println("in paintChildern of DisplayPageFigure");
		IFigure child;

		Rectangle clip = Rectangle.SINGLETON;
		for (int i = 0; i < getChildren().size(); i++) {
			child = (IFigure)getChildren().get(i);
			//System.out.println("child.intersects(graphics.getClip(clip)) : " + child.intersects(graphics.getClip(clip)));
			//System.out.println("graphics.getClip(clip)) : " + graphics.getClip(clip));
			//System.out.println("child.getBounds() : " + child.getBounds());
			child.setBounds(graphics.getClip(clip));
			//System.out.println("child.getBounds() : " + child.getBounds());
			if (child.isVisible() && child.intersects(graphics.getClip(clip))) {
				graphics.clipRect(child.getBounds());
				child.paint(graphics);
				graphics.restoreState();
			}
		}
	}

	public boolean isPageChildGroupFigurePresent(){
		for( int i = 0 ; i< getChildren().size() ; i++ ) {
			if(getChildren().get(i) instanceof PageChildGroupFigure ){
				return true;
			}
		}
		return false;
	}
	public boolean isDisplaySubPageFigurePresent(){
		for( int i = 0 ; i< getChildren().size() ; i++ ) {
			if(getChildren().get(i) instanceof DisplaySubPageFigure ){
				displayState = Constants.PAGE_CHILD_VISIBLE;
				return true;
			}
		}
		return false;
	}

	public int getPageChildGroupLastY() {
		return pageChildGroupLastY;
	}

	public void setPageChildGroupLastY(int pageChildGroupLastY) {
		this.pageChildGroupLastY = pageChildGroupLastY;
	}

	public int getPageContentGroupLastY() {
		return pageContentGroupLastY;
	}

	public void setPageContentGroupLastY(int pageContentGroupLastY) {
		this.pageContentGroupLastY = pageContentGroupLastY;
	}

	public Rectangle getPageChildGroupBounds() {
		return pageChildGroupBounds;
	}

	public void setPageChildGroupBounds(Rectangle pageChildGroupBounds) {
		this.pageChildGroupBounds = pageChildGroupBounds;
	}

	public Rectangle getDisplaySubPageBounds() {
		return displaySubPageBounds;
	}

	public void setDisplaySubPageBounds(Rectangle displaySubPageBounds) {
		this.displaySubPageBounds = displaySubPageBounds;
	}

	public Rectangle getPageContentGroupBounds() {
		return pageContentGroupBounds;
	}

	public void setPageContentGroupBounds(Rectangle pageContentGroupBounds) {
		this.pageContentGroupBounds = pageContentGroupBounds;
	}

	public boolean isDrawPageChildGroup() {
		return drawPageChildGroup;
	}

	public void setDrawPageChildGroup(boolean drawPageChildGroup) {
		this.drawPageChildGroup = drawPageChildGroup;
	}
	
}
