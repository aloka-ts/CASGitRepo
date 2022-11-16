package com.genband.m5.maps.ide.sitemap.figure;

import org.eclipse.draw2d.Button;
import org.eclipse.draw2d.Ellipse;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.FlowLayout;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.RectangleFigure;
import org.eclipse.draw2d.geometry.Rectangle;

public class TrailTabFigure extends Figure{
	
	public TrailTabFigure() {
		Button button = new Button("Button...");
		setLayoutManager(new FlowLayout());
		//setBounds(new Rectangle(10,10,100,100));
		add(button);
		//add(new RectangleFigure());
		//Ellipse e = new Ellipse();
		//e.setLayoutManager(new FlowLayout());
		System.out.println("getBounds()" + getBounds());
		System.out.println("getLocation()" + getLocation());
		//add(e);
		
	}
	protected void paintFigure(Graphics graphics) {
		//super.paintFigure(graphics);
		graphics.drawRectangle(10,10,50,50);
		System.out.println("in paintFigure getBounds in TrailTabFigure " + getBounds());
		System.out.println("in paintFigure getClientArea in TrailTabFigure " + getClientArea());
		System.out.println("in paintFigure getLocation in TrailTabFigure " + getLocation());
		System.out.println("in paintFigure getMaximumSize in TrailTabFigure " + getMaximumSize());
		System.out.println("in paintFigure getMinimumSize in TrailTabFigure " + getMinimumSize());
		System.out.println("in paintFigure getSize in TrailTabFigure " + getSize());
		System.out.println("in paintFigure getPreferredSize in TrailTabFigure " + getPreferredSize());
		System.out.println("in paintFigure getToolTip in TrailTabFigure " + getToolTip());
		System.out.println("in paintFigure getChildren().size() in TrailTabFigure " + getChildren().size());
		//graphics.dra
		
		
		
		
	}
}
