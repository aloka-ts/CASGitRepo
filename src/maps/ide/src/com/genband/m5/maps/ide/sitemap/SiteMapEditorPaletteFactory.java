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
*     Package:  com.genband.m5.maps.ide.sitemap
*
*     File:     SiteMapEditorPaletteFactory.java
*
*     Desc:   	To create palette.
*
*   Author 				 Date									Description
*    ---------------------------------------------------------
*	  Genband        December 28, 2007   Initial Creation
*
**********************************************************************
**/

package com.genband.m5.maps.ide.sitemap;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.gef.palette.CombinedTemplateCreationEntry;
import org.eclipse.gef.palette.PaletteContainer;
import org.eclipse.gef.palette.PaletteDrawer;
import org.eclipse.gef.palette.PaletteRoot;
import org.eclipse.gef.requests.SimpleFactory;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.plugin.AbstractUIPlugin;

import com.genband.m5.maps.ide.CPFPlugin;
import com.genband.m5.maps.ide.model.CPFPortlet;
import com.genband.m5.maps.ide.sitemap.model.MainPage;
import com.genband.m5.maps.ide.sitemap.model.Portlet;
import com.genband.m5.maps.ide.sitemap.model.SubPage;
import com.genband.m5.maps.ide.sitemap.util.PortletInfo;
import com.genband.m5.maps.ide.sitemap.util.PortletUtil;
import com.genband.m5.maps.ide.sitemap.util.ProjectUtil;

/**
 * Utility class to create Palette.
 * @see #createPalette() 
 * @author Genband
 */
final class SiteMapEditorPaletteFactory {

/** Preference ID used to persist the palette location. */
private static final String PALETTE_DOCK_LOCATION = "SiteMapEditorPaletteFactory.Location";
/** Preference ID used to persist the palette size. */
private static final String PALETTE_SIZE = "SiteMapEditorPaletteFactory.Size";
/** Preference ID used to persist the flyout palette's state. */
private static final String PALETTE_STATE = "SiteMapEditorPaletteFactory.State";

/** Create the "Page" drawer. */
private static PaletteContainer createPageDrawer() {
	PaletteDrawer componentsDrawer = new PaletteDrawer("Page");

	/*CombinedTemplateCreationEntry component = new CombinedTemplateCreationEntry(
			"Page", 
			"Add a new Page", 
			PageShape.class,
			new SimpleFactory(PageShape.class), 
			ImageDescriptor.createFromFile(CPFPlugin.class, "icons/page16.bmp"), 
			ImageDescriptor.createFromFile(CPFPlugin.class, "icons/page24.bmp"));
	*/
	
	CombinedTemplateCreationEntry component = new CombinedTemplateCreationEntry(
			"Page", 
			"Add a new Page", 
			MainPage.class,
			new SimpleFactory(MainPage.class), 
			getImageDescriptor("/icons/page16.bmp"), 
			getImageDescriptor("/icons/page24.bmp"));
			componentsDrawer.add(component);

			component = new CombinedTemplateCreationEntry(
				"SubPage", 
				"Add a new SubPage", 
				SubPage.class,
				new SimpleFactory(SubPage.class), 
				getImageDescriptor("/icons/subpage16.bmp"), 
				getImageDescriptor("/icons/subpage24.bmp"));
				componentsDrawer.add(component);

			return componentsDrawer;
}

public static ImageDescriptor getImageDescriptor(String path) {
	return AbstractUIPlugin.imageDescriptorFromPlugin("com.genband.m5.maps", path);
}

/** Create the "Portlets" drawer. */
private static PaletteContainer createPortletsDrawer() {
	PaletteDrawer componentsDrawer = new PaletteDrawer("Portlets");

	CombinedTemplateCreationEntry component = null;
	/*TODO
	 * Roles should be passed appropriately
	 */
	List roles = ProjectUtil.getRoles();
	//roles = new ArrayList();
	//roles.add("SPA");
	CPFPlugin.getDefault().log("ProjectUtil.getProjectName()  " + ProjectUtil.getProjectName());
	CPFPlugin.getDefault().info ("Got roles count: " + roles.size());
	List<PortletInfo> portletList = PortletUtil.getPortletsInfo(ProjectUtil.getProjectName(), roles);
	for (int i = 0; i < portletList.size() ; i++){
		String portletSmallImagePath = "icons/portlet16";
		String portletLargeImagePath = "icons/portlet24";
		String extension  = "bmp";
		
		component = new CombinedTemplateCreationEntry(
				portletList.get(i).getName(), 
				portletList.get(i).getToopTip(), 
				getPortlet(portletList.get(i).getName(),portletList.get(i).getToopTip(),portletList.get(i).getIconType(),portletList.get(i).getCpfPortlet()),
				new PortletModelFactory(Portlet.class,portletList.get(i).getName(),//here we are sending the info. 
						//that which portlet is dropped. Now we can cutomize the figure of portlet corresponding to the dropped portlet
						portletList.get(i).getToopTip(),portletList.get(i).getIconType(),portletList.get(i).getCpfPortlet()), 
				getImageDescriptor(portletSmallImagePath+"_"+portletList.get(i).getIconType()+".bmp"), 
				getImageDescriptor(portletSmallImagePath+"_"+portletList.get(i).getIconType()+".bmp"));
				componentsDrawer.add(component);
	}
	return componentsDrawer;
}

private static Portlet getPortlet(String name,String toolTip , int iconType,CPFPortlet cpfPortlet){
	try {
		Portlet portlet = new Portlet();
		portlet.setName(name);
		portlet.setToolTip(toolTip);
		portlet.setIconType(iconType);
		portlet.setCpfPortlet(cpfPortlet);
		return portlet;
	} catch (Exception exc) {
		return null;
	}
}
/**
 * Creates the PaletteRoot and adds all palette elements.
 * Factory method to create a new palette for SiteMap editor.
 * @return a new PaletteRoot
 */
static PaletteRoot createPalette() {
	PaletteRoot palette = new PaletteRoot();
	//palette.add(createToolsGroup(palette));
	//palette.add(createSiteMapDrawer());
	palette.add(createPageDrawer());
	palette.add(createPortletsDrawer());
	return palette;
}

/** Utility class. */
private SiteMapEditorPaletteFactory() {
	// Utility class
}

/*private static PaletteContainer createSiteMapDrawer() {
PaletteDrawer componentsDrawer = new PaletteDrawer("Sitemap");

CombinedTemplateCreationEntry component = new CombinedTemplateCreationEntry(
		"Sitemap", 
		"Add a new Page", 
		SiteMap.class,
		new SimpleFactory(PageShape.class), 
		ImageDescriptor.createFromFile(CPFPlugin.class, "icons/sitemap16.gif"), 
		ImageDescriptor.createFromFile(CPFPlugin.class, "icons/page316.gif"));
componentsDrawer.add(component);

}
*/
/** Create the "Tools" group. */
/*private static PaletteContainer createToolsGroup(PaletteRoot palette) {
	PaletteGroup toolGroup = new PaletteGroup("Tools");

	// Add a selection tool to the group
	ToolEntry tool = new PanningSelectionToolEntry();
	toolGroup.add(tool);
	palette.setDefaultEntry(tool);
	
	// Add a marquee tool to the group
	toolGroup.add(new MarqueeToolEntry());

	// Add a (unnamed) separator to the group
	toolGroup.add(new PaletteSeparator());

	// Add (solid-line) connection tool 
	tool = new ConnectionCreationToolEntry(
			"Solid connection",
			"Create a solid-line connection",
			new CreationFactory() {
				public Object getNewObject() { return null; }
				// see ShapesEditPart#createEditPolicies() 
				// this is abused to transmit the desired line style 
				public Object getObjectType() { return Connection.SOLID_CONNECTION; }
			},
			ImageDescriptor.createFromFile(CPFPlugin.class, "icons/connection_s16.gif"),
			ImageDescriptor.createFromFile(CPFPlugin.class, "icons/connection_s24.gif"));
	toolGroup.add(tool);
	
	// Add (dashed-line) connection tool
	tool = new ConnectionCreationToolEntry(
			"Dashed connection",
			"Create a dashed-line connection",
			new CreationFactory() {
				public Object getNewObject() { return null; }
				// see ShapesEditPart#createEditPolicies()
				// this is abused to transmit the desired line style 
				public Object getObjectType() { return Connection.DASHED_CONNECTION; }
			},
			ImageDescriptor.createFromFile(CPFPlugin.class, "icons/connection_d16.gif"),
			ImageDescriptor.createFromFile(CPFPlugin.class, "icons/connection_d24.gif"));
	toolGroup.add(tool);

	return toolGroup;
}
*/

}