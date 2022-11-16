package com.genband.m5.maps.ide.sitemap.figure;


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

public class TrailFigure extends Figure {
	Rectangle bounds ;
	Rectangle siteMapBounds ;
	Color FGColor;
	Color BGColor;
	RectangleFigure rectFigure;
	RectangleFigure rectFigure1;
	public TrailFigure(Rectangle bounds) {
	System.out.print("in constructor of SitemapFigure");
	//super();
	FGColor = ColorConstants.lightGray;
	rectFigure = new RectangleFigure();
	rectFigure1 = new RectangleFigure();
	//Ellipse ellipse = new Ellipse();
	//RectangleFigure rectFigure = new RectangleFigure();
	//Rectangle bounds = getBounds();
	this.bounds = new Rectangle(bounds);
	siteMapBounds = new Rectangle(bounds.x + 3 , bounds.y + 3 , bounds.width - 9 , bounds.height - 9);
	//ellipse.setBounds(siteMapBounds);
	//rectFigure.setBounds(siteMapBounds);
	//add(ellipse);
	//add(rectFigure);
	}
	
	@Override
	public void paintFigure(Graphics graphics) {
		System.out.println("paintFigure of SitemapFigure entered ");
		//super.paintFigure(graphics);
		
		//Rectangle b1 = new Rectangle(130,130,50,50);
		Rectangle b1 = new Rectangle(getBounds().x + 30,getBounds().y + 30,getBounds().width/2,getBounds().height/2);
		rectFigure.setBounds(b1);
		rectFigure.setLayoutManager(new FreeformLayout());
		//add(rectFigure);
		
		//Rectangle b2 = new Rectangle(180,180,10,10);
		Rectangle b2 = new Rectangle(getBounds().x + 80,getBounds().y + 80,20,20);
		rectFigure1.setBounds(b2);
		rectFigure1.setLayoutManager(new FreeformLayout());
		//add(rectFigure1);
		
		int lineWidth = 2;
		graphics.setLineWidth(lineWidth );
		System.out.print("FGColor = " + FGColor);
		this.setConstraint(rectFigure, siteMapBounds);
		graphics.setForegroundColor(FGColor);
		//graphics.setBackgroundColor(ColorConstants.red);
		graphics.drawRectangle(getBounds().x + 30,getBounds().y + 30,getBounds().width/2,getBounds().height/2);
		graphics.drawRectangle(getBounds().x + 80,getBounds().y + 80,20,20);
		//paintChildren(graphics);
		//Font font;
		//graphics.setFont(FontConstants.);
		graphics.setForegroundColor(ColorConstants.titleGradient);
		
		//graphics.drawText(getFilename(), siteMapBounds.x + 10, siteMapBounds.y - 6 );
		graphics.setForegroundColor(FGColor);
		
		System.out.println("paintFigure of SitemapFigure exit \n\n\n");
		
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
	private String getFilename() {
		return "NP Sitemap.sitemap";
	}

	public void setFGColor(Color forgroundColor) {
		this.FGColor = forgroundColor;
	}

	public void setBGColor(Color backgroundColor) {
		this.BGColor = backgroundColor;
	}

	public IFigure getRect() {
		// TODO Auto-generated method stub
		return rectFigure;
	}
}
