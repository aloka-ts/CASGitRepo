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
*     File:     MyFigure.java
*
*     Desc:   	This a figure which is basically view for myfigure.
*
*     Author    Date                Description
*    ---------------------------------------------------------
*     Genband   December 28, 2007   Initial Creation
*
**********************************************************************
**/

package com.genband.m5.maps.ide.sitemap.figure;

import org.eclipse.draw2d.*;
import org.eclipse.draw2d.geometry.Rectangle;

public class MyFigure extends Figure{
	
	private ScrollPane visiblePane;

	private ScrollPane invisiblePane;

	public MyFigure() {

	Figure pane = new FreeformLayer();

	pane.setLayoutManager(new FreeformLayout());

	setLayoutManager(new FlowLayout());

	ScrollPane scrollpane1 = new ScrollPane();
	//scrollpane1.add(new MyTabFolder1());
	scrollpane1.setViewport(new FreeformViewport());

	scrollpane1.setView(pane);

	visiblePane = scrollpane1;

	ScrollPane scrollpane2 = new ScrollPane();

	scrollpane2.setViewport(new FreeformViewport());

	scrollpane2.setView(pane);

	invisiblePane = scrollpane2;

	setBackgroundColor(ColorConstants.buttonLightest);

	setOpaque(true);

	Clickable button = new Button("switch");

//	button.setBounds(new Rectangle(30, 250, 140, 35));

	button.addActionListener(new ActionListener() {

	public void actionPerformed(ActionEvent e) {

	switchPanel();

	}
	
	});

	add(visiblePane);

	add(button);

	}
	private void switchPanel() {

		remove(visiblePane);

		ScrollPane temp = visiblePane;

		visiblePane = invisiblePane;

		invisiblePane = temp;

		add(visiblePane);

	}
}
