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
*     File:     SiteMapEditor.java
*
*     Desc:   	Editor for the SiteMap.
*
*     Author    Date                Description
*    ---------------------------------------------------------
*     Genband   December 28, 2007   Initial Creation
*
**********************************************************************
**/

package com.genband.m5.maps.ide.sitemap;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.EventObject;
import java.util.List;
import java.util.StringTokenizer;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.gef.ContextMenuProvider;
import org.eclipse.gef.DefaultEditDomain;
import org.eclipse.gef.EditPartViewer;
import org.eclipse.gef.GraphicalViewer;
import org.eclipse.gef.dnd.TemplateTransferDragSourceListener;
import org.eclipse.gef.dnd.TemplateTransferDropTargetListener;
import org.eclipse.gef.editparts.ScalableFreeformRootEditPart;
import org.eclipse.gef.palette.PaletteRoot;
import org.eclipse.gef.requests.CreationFactory;
import org.eclipse.gef.ui.actions.ActionRegistry;
import org.eclipse.gef.ui.palette.PaletteViewer;
import org.eclipse.gef.ui.palette.PaletteViewerProvider;
import org.eclipse.gef.ui.parts.ContentOutlinePage;
import org.eclipse.gef.ui.parts.GraphicalEditorWithFlyoutPalette;
import org.eclipse.gef.ui.parts.GraphicalViewerKeyHandler;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.util.TransferDropTargetListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.actions.WorkspaceModifyOperation;
import org.eclipse.ui.dialogs.SaveAsDialog;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.ui.part.IPageSite;

import com.genband.m5.maps.ide.MyObjectInputStream;
import com.genband.m5.maps.ide.CPFPlugin;
import com.genband.m5.maps.ide.model.CPFPortlet;
import com.genband.m5.maps.ide.model.CPFScreen;
import com.genband.m5.maps.ide.model.util.CPFPortalObjectPersister;
import com.genband.m5.maps.ide.sitemap.editpart.ShapesTreeEditPartFactory;
import com.genband.m5.maps.ide.sitemap.editpart.SiteMapEditPartFactory;
import com.genband.m5.maps.ide.sitemap.model.MainPage;
import com.genband.m5.maps.ide.sitemap.model.Page;
import com.genband.m5.maps.ide.sitemap.model.PlaceHolder;
import com.genband.m5.maps.ide.sitemap.model.Portlet;
import com.genband.m5.maps.ide.sitemap.model.SiteMap;
import com.genband.m5.maps.ide.sitemap.model.SubPage;
import com.genband.m5.maps.ide.sitemap.util.Constants;
import com.genband.m5.maps.ide.sitemap.util.ProjectUtil;
import com.genband.m5.maps.ide.sitemap.util.SiteMapUtil;

/**
 * A graphical editor with flyout palette that can edit .sitemap files.
 * Outline , property and palette views are also configured.
 * If palette view is not opened then editor will have a palette attached to it.
 * The binding between the .sitemap file extension and this editor is done in plugin.xml
 */
public class SiteMapEditor 
	extends GraphicalEditorWithFlyoutPalette 
{


/** This is the root of the editor's model. */
private SiteMap siteMap;
/** Palette component, holding new page and already created portlets */
private static PaletteRoot PALETTE_MODEL;




/** Create a new SiteMapEditor instance. This is called by the Workspace. */
public SiteMapEditor() {
}


/*
 * Configure the graphical viewer before it receives contents.
 * This is the place to choose an appropriate RootEditPart and EditPartFactory
 * for your editor. The RootEditPart determines the behavior of the editor's "work-area".
 * For example, GEF includes zoomable and scrollable root edit parts. The EditPartFactory
 * maps model elements to edit parts (controllers).
 * @see org.eclipse.gef.ui.parts.GraphicalEditor#configureGraphicalViewer()
 */
protected void configureGraphicalViewer() {
	//getGraphicalViewer().setControl(c1);
	

	super.configureGraphicalViewer( );
	
	
	
	/* final FigureCanvas figCanvas = (FigureCanvas) getGraphicalViewer().getControl();
	 LightweightSystem lws = figCanvas.getLightweightSystem();
	  //lws.setContents(new RectangleFigure());
	MyTabFolder1 myTabFolder1 = new MyTabFolder1();
	
	Rectangle rect = new Rectangle(10,10,40,40);
	myTabFolder1.setBounds(rect );
	lws.setContents(myTabFolder1);
	lws.setControl(figCanvas);
	lws.setContents(myTabFolder1);
	
	//Canvas canvas = new Canvas (new Shell(Display.getCurrent()), SWT.BORDER);
	//data = new FormData ();
	//data.left = new FormAttachment (20, 20);
	//canvas2.setLayoutData (data);
	//Label label0 = new Label (figCanvas, SWT.NONE);
	//label0.setText ("CPF_Project2");
	Button b1 = new Button(figCanvas.getShell(), SWT.PUSH);
	
	//lws.getUpdateManager().setGraphicsSource(new NativeGraphicsSource(figCanvas));
	Button b2 = new Button(figCanvas, SWT.PUSH);
	 */	
	
	//getGraphicalViewer().getControl().setBackground(ColorConstants.listForeground);
	//Canvas c = new Canvas(new Shell(),SWT.NONE);
	//Button b1 = new Button(c, SWT.PUSH);
	System.out.println("(Composite)getGraphicalViewer().getControl() " + (Composite)getGraphicalViewer().getControl());
	System.out.println("getGraphicalViewer().getControl() " + getGraphicalViewer().getControl());
	
	//label0 = new Label ((Composite)getGraphicalViewer().getControl(), SWT.NONE);
	//label0.setText ("CPF_Project2");
	//Button b1 = new Button ((Composite)getGraphicalViewer().getControl(), SWT.PUSH);
	//b1.setBackground(ColorConstants.blue);
	//label0.setForeground(new Color(display, 0, 0, 255));
	//FormData data = new FormData ();
	//data.left = new FormAttachment (7, 0);
	//data.top = new FormAttachment (8+18, 0);
	//label0.setLayoutData (data);
	
	//Control c1 = getGraphicalViewer().createControl(c);
	//FigureCanvas
	System.out.println("configureGraphicalViewer");
	GraphicalViewer viewer = getGraphicalViewer();
	
	viewer.setEditPartFactory(new SiteMapEditPartFactory());
	viewer.setRootEditPart(new ScalableFreeformRootEditPart());
	viewer.setKeyHandler(new GraphicalViewerKeyHandler(viewer));
	// configure the context menu provider
	ContextMenuProvider cmProvider =
			new SiteMapEditorContextMenuProvider(viewer, getActionRegistry());
	viewer.setContextMenu(cmProvider);
	getSite().registerContextMenu(cmProvider, viewer);
}

public void commandStackChanged(EventObject event) {
	firePropertyChange(IEditorPart.PROP_DIRTY);
	super.commandStackChanged(event);
}

private void createOutputStream(OutputStream os) throws IOException {
	ObjectOutputStream oos = new ObjectOutputStream(os);
	SiteMap siteMap = getModel();
	MainPage displayPage = getDisplayPage(siteMap);
	SubPage displaySubPage = getDisplaySubPage(displayPage);
	if ( null != displaySubPage ) {
		SubPage associatedSubpage = getDataSubPage(displayPage, displaySubPage.getPageNo());
		if ( null != associatedSubpage ) {
			copySubPageProperties(displaySubPage, associatedSubpage);
			cleanSubPage(associatedSubpage);
			copyPlaceHolders(displaySubPage, associatedSubpage);
		}
	}
	MainPage associatedPage = getDataPage(siteMap, displayPage.getPageNo());
	cleanPage(associatedPage);
	copyPageData(displayPage, associatedPage);
	
	oos.writeObject(siteMap);
	
	oos.close();
}

/* (non-Javadoc)
 * @see org.eclipse.gef.ui.parts.GraphicalEditorWithFlyoutPalette#createPaletteViewerProvider()
 */
protected PaletteViewerProvider createPaletteViewerProvider() {
	return new PaletteViewerProvider(getEditDomain()) {
		protected void configurePaletteViewer(PaletteViewer viewer) {
			super.configurePaletteViewer(viewer);
			// create a drag source listener for this palette viewer
			// together with an appropriate transfer drop target listener, this will enable
			// model element creation by dragging a CombinatedTemplateCreationEntries 
			// from the palette into the editor
			// @see SiteMapEditor#createTransferDropTargetListener()
			viewer.addDragSourceListener(new TemplateTransferDragSourceListener(viewer));
		}
	};
}

/**
 * Create a transfer drop target listener. When using a CombinedTemplateCreationEntry
 * tool in the palette, this will enable model element creation by dragging from the palette.
 * @see #createPaletteViewerProvider()
 */
private TransferDropTargetListener createTransferDropTargetListener() {
	return new TemplateTransferDropTargetListener(getGraphicalViewer()) {
		protected CreationFactory getFactory(Object template) {
			//return new SimpleFactory((Class) template);
			CreationFactory c =  new DataElementFactory(template);
			return c;
		}
	};
}

/* (non-Javadoc)
 * @see org.eclipse.ui.ISaveablePart#doSave(org.eclipse.core.runtime.IProgressMonitor)
 */
public void doSave(IProgressMonitor monitor) {
	ByteArrayOutputStream out = new ByteArrayOutputStream();
	try {
		createOutputStream(out);
		IFile file = ((IFileEditorInput) getEditorInput()).getFile();
		file.setContents(
			new ByteArrayInputStream(out.toByteArray()), 
			true,  // keep saving, even if IFile is out of sync with the Workspace
			false, // dont keep history
			monitor); // progress monitor
		getCommandStack().markSaveLocation();
	} catch (CoreException ce) { 
		ce.printStackTrace();
	} catch (IOException ioe) {
		ioe.printStackTrace();
	}
}

/* (non-Javadoc)
 * @see org.eclipse.ui.ISaveablePart#doSaveAs()
 */
public void doSaveAs() {
	// Show a SaveAs dialog
	Shell shell = getSite().getWorkbenchWindow().getShell();
	SaveAsDialog dialog = new SaveAsDialog(shell);
	dialog.setOriginalFile(((IFileEditorInput) getEditorInput()).getFile());
	dialog.open();
	
	IPath path = dialog.getResult();	
	if (path != null) {
		// try to save the editor's contents under a different file name
		final IFile file = ResourcesPlugin.getWorkspace().getRoot().getFile(path);
		try {
			new ProgressMonitorDialog(shell).run(
					false, // don't fork
					false, // not cancelable
					new WorkspaceModifyOperation() { // run this operation
						public void execute(final IProgressMonitor monitor) {
							try {
								ByteArrayOutputStream out = new ByteArrayOutputStream();
								createOutputStream(out);
								file.create(
									new ByteArrayInputStream(out.toByteArray()), // contents
									true, // keep saving, even if IFile is out of sync with the Workspace
									monitor); // progress monitor
							} catch (CoreException ce) {
								ce.printStackTrace();
							} catch (IOException ioe) {
								ioe.printStackTrace();
							} 
						}
					});
			// set input to the new file
			setInput(new FileEditorInput(file));
			getCommandStack().markSaveLocation();
		} catch (InterruptedException ie) {
  			// should not happen, since the monitor dialog is not cancelable
			ie.printStackTrace(); 
		} catch (InvocationTargetException ite) { 
			ite.printStackTrace(); 
		}
	}
}

/*public Object getAdapter(Class type) {
	if (type == IContentOutlinePage.class)
		return new SiteMapOutlinePage(new TreeViewer());
	return super.getAdapter(type);
}*/

SiteMap getModel() {
	return siteMap;
}

/* (non-Javadoc)
 * @see org.eclipse.gef.ui.parts.GraphicalEditorWithFlyoutPalette#getPaletteRoot()
 */
protected PaletteRoot getPaletteRoot() {
//	if (PALETTE_MODEL == null)
		PALETTE_MODEL = SiteMapEditorPaletteFactory.createPalette();
	return PALETTE_MODEL;
}

private void handleLoadException(Exception e) {
	System.err.println("** Load failed. Using default model. **");
	e.printStackTrace();
	siteMap = new SiteMap();
}

/**
 * Set up the editor's inital content (after creation).
 * @see org.eclipse.gef.ui.parts.GraphicalEditorWithFlyoutPalette#initializeGraphicalViewer()
 */
protected void initializeGraphicalViewer() {
	//super.initializeGraphicalViewer();
	System.out.println("initializeGraphicalViewer");
	GraphicalViewer viewer = getGraphicalViewer();
		viewer.setContents(getModel()); // set the contents of this editor
	
	// listen for dropped parts
	viewer.addDropTargetListener(createTransferDropTargetListener());
}

/* (non-Javadoc)
 * @see org.eclipse.ui.ISaveablePart#isSaveAsAllowed()
 */
public boolean isSaveAsAllowed() {
	return true;
}

protected void setInput(IEditorInput input) {
//	setEditDomain(new DefaultEditDomain(this));
//	CPFPlugin.getDefault().log("input is " +  input);
//	CPFPlugin.getDefault().log("input is " +  input.getName());
//	CPFPlugin.getDefault().log("input is " +  ((IFileEditorInput) input).getFile());
//	CPFPlugin.getDefault().log("input is " +  ((IFileEditorInput) input).getFile().getName());
//	CPFPlugin.getDefault().log("input is " +  ((IFileEditorInput) input).getFile().getFullPath());
//	CPFPlugin.getDefault().log("input is " +  ((IFileEditorInput) input).getFile().getProject());
//	CPFPlugin.getDefault().log("input is " +  ((IFileEditorInput) input).getFile().getProject().getName());

	ProjectUtil.setProjectName(((IFileEditorInput) input).getFile().getProject().getName());
	super.setInput(input);
	try {
		IFile file = ((IFileEditorInput) input).getFile();
		
		
		// Changes related to PR 49914 (ClassLoading issue) starts
		CPFPlugin.getDefault().log("SitemapEditor : file : " + file);
		List<String> externalJarsPath = SiteMapUtil.getExtrnalJars(((IFileEditorInput) input).getFile().getProject().getName());
		ClassLoader parentLoader = 	Thread.currentThread().getContextClassLoader();

		File gbFile = new File (CPFPlugin.fullPath("library/gb-common.jar"));
		ObjectInputStream in = null;
		URLClassLoader loader = new URLClassLoader(new URL[] { new URL(
				"file:///" + gbFile.getAbsolutePath()) }, parentLoader);

		
		URL urls[]= new URL[30];
		for ( int i = 0 ; i < externalJarsPath.size() ; i++ ) {
			File jarFile = new File (externalJarsPath.get(i));
			urls[i] = new URL("file:///" + jarFile.getAbsolutePath());
			
		}

		loader = new URLClassLoader(urls, parentLoader);
		try {
			in = new MyObjectInputStream(loader,file.getContents());
		} catch (CoreException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		
		//ObjectInputStream in = new ObjectInputStream(file.getContents());
		
		// Changes related to PR 49914 (ClassLoading issue) ends
		
		
		try {
			siteMap = (SiteMap) in.readObject();
		} catch (ClassNotFoundException e1) {
			// TODO Auto-generated catch block
			CPFPlugin.getDefault().log("ClassNotFoundException !!!");
			e1.printStackTrace();
		}
		//if ( null == ProjectUtil.getRoles() || ProjectUtil.getRoles().isEmpty()) {
			//List l = new ArrayList();
			//l.add("dummyRole1");
			//l.add("dummyRole2");
			//l.add("dummyRole3");
			//ProjectUtil.setProjectName("P1");
			System.out.println("IT roles set in setInput");
			System.out.println("roles of sitemap are : "+siteMap.getRoles());
			ProjectUtil.setRoles(parseString(siteMap.getRoles()));
		//}
		setEditDomain(new DefaultEditDomain(this));
		// update portlet 
		/*
		 * If some page or subPage has a portlet whose linking is changed
		 * then update its linked page info. here.
		 * Now a user can change linking of listing page even after dropping it on some page or subPage
		 * Only thing he/she has to do is ....he/she has to reopen the siteMap.
		 */
		List sitemapChildren = siteMap.getChildren();
		
		for ( int i = 0 ; i < sitemapChildren.size() ; i++ ) {
			if ( sitemapChildren.get(i) instanceof MainPage ) {
				MainPage mainPage =  (MainPage)sitemapChildren.get(i);
				List mainPageChildren = mainPage.getChildren();
				for( int j = 0 ; j < mainPageChildren.size() ; j++ ) {
					if(mainPageChildren.get(j) instanceof SubPage){
						//code for subPage update
				
						SubPage subPage =  (SubPage)mainPageChildren.get(j);
						List subPageChildren = subPage.getChildren();
						
						for( int k = 0 ; k < subPageChildren.size() ; k++ ) {
							
							 if ( subPageChildren.get(k) instanceof PlaceHolder ) {
								PlaceHolder placeHolder = (PlaceHolder)subPageChildren.get(k);
								List placeHolderChildren = placeHolder.getChildren();
								for ( int m = 0 ; m < placeHolderChildren.size() ; m++ ) {
									if ( placeHolderChildren.get(m) instanceof Portlet ) {
										Portlet portlet = (Portlet)placeHolderChildren.get(m);
										CPFPortlet updatedCPFPortlet = null;
										int portletId = portlet.getCpfPortlet().getPortletId();
										System.out.println("portletId = " + portletId + " in subPage");
										updatedCPFPortlet = getUpdatedCPFPortlet(ProjectUtil.getProjectName(), portletId);
										if ( null != updatedCPFPortlet ) {
											System.out.println("updating the portlet in subpage ");
											portlet.setCpfPortlet(updatedCPFPortlet);
											System.out.println("list screen is " + updatedCPFPortlet.getListScreen());
											System.out.println("list screen name is : " + updatedCPFPortlet.getListScreen().getJspName());
											System.out.println("details screen is :" + updatedCPFPortlet.getDetailsScreen());
											System.out.println("details screen name is :" + updatedCPFPortlet.getDetailsScreen().getJspName());
											System.out.println("portlet updated in subpage");
										} else {
											System.out.println("Error : Not able to find the portlet having portletId " + portletId);
										}
									}
								}
							}
						}
					} else if ( mainPageChildren.get(j) instanceof PlaceHolder ) {
						PlaceHolder placeHolder = (PlaceHolder)mainPageChildren.get(j);
						List placeHolderChildren = placeHolder.getChildren();
						for ( int k = 0 ; k < placeHolderChildren.size() ; k++ ) {
							if ( placeHolderChildren.get(k) instanceof Portlet ) {
								Portlet portlet = (Portlet)placeHolderChildren.get(k);
								CPFPortlet updatedCPFPortlet = null;
								int portletId = portlet.getCpfPortlet().getPortletId();
								System.out.println("portletId = " + portletId + " in main page");
								updatedCPFPortlet = getUpdatedCPFPortlet(ProjectUtil.getProjectName(), portletId);
								if ( null != updatedCPFPortlet ) {
									System.out.println("updating the portlet in main page");
									portlet.setCpfPortlet(updatedCPFPortlet);
									try {
										System.out.println("updating detailsscreen: " + updatedCPFPortlet.getDetailsScreen());
										portlet.getCpfPortlet().setDetailsScreen(updatedCPFPortlet.getDetailsScreen(), true);
										if( null != portlet.getDetailsScreen()){
											System.out.println("updating detailsscreen name: " + updatedCPFPortlet.getDetailsScreen().getJspName());
											portlet.getCpfPortlet().getDetailsScreen().setJspName(updatedCPFPortlet.getDetailsScreen().getJspName());
										}
									} catch (Exception e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
									System.out.println("list screen is " + updatedCPFPortlet.getListScreen());
									System.out.println("list screen name is : " + updatedCPFPortlet.getListScreen().getJspName());
									System.out.println("details screen is :" + updatedCPFPortlet.getDetailsScreen());
									if ( null != updatedCPFPortlet.getDetailsScreen() ) {
										System.out.println("details screen name is :" + updatedCPFPortlet.getDetailsScreen().getJspName());
									}
									System.out.println("portlet updated in mainpage ");
								} else {
									System.out.println("Error : Not able to find the portlet having portletId " + portletId);
								}
								
							}
						}
					}
				}
			}
		}
		
		System.out.println("\n\n\n siteMap.getChildren().size()  "+siteMap.getChildren().size());
		in.close();
		setPartName(file.getName());
		System.out.println(" file.getName()  " + file.getName());
		
	} catch (IOException e) { 
		handleLoadException(e); 
	} /*catch (CoreException e) { 
		handleLoadException(e); 
	} catch (ClassNotFoundException e) { 
		handleLoadException(e); 
	}*/
}

/**
 * Creates an outline for this editor.
 */
public class SiteMapOutlinePage extends ContentOutlinePage {	
	/**
	 * Create a new outline page for the SiteMap editor.
	 * @param viewer a viewer (TreeViewer instance) used for this outline page
	 * @throws IllegalArgumentException if editor is null
	 */
	public SiteMapOutlinePage(EditPartViewer viewer) {
		super(viewer);
	}

	public void createControl(Composite parent) {
		// create outline viewer page
		getViewer().createControl(parent);
		/*Button b1 = new Button(parent,SWT.PUSH);
		System.out.println("in create control");
		Label label0 = new Label (parent, SWT.NONE);
		label0.setText ("CPF_Project2");
		//label0.setForeground(new Color(display, 0, 0, 255));
		FormData data = new FormData ();
		data.left = new FormAttachment (7, 0);
		data.top = new FormAttachment (8+18, 0);
		label0.setLayoutData (data);
		getViewer().setControl(parent);
		*/
		// configure outline viewer
		getViewer().setEditDomain(getEditDomain());
		getViewer().setEditPartFactory(new ShapesTreeEditPartFactory());
		// configure & add context menu to viewer
		ContextMenuProvider cmProvider = new SiteMapEditorContextMenuProvider(
				getViewer(), getActionRegistry()); 
		getViewer().setContextMenu(cmProvider);
		getSite().registerContextMenu(
				"com.genband.m5.maps.ide.sitemap.outline.contextmenu",
				cmProvider, getSite().getSelectionProvider());		
		// hook outline viewer
		getSelectionSynchronizer().addViewer(getViewer());
		// initialize outline viewer with model
		getViewer().setContents(getModel());
		// show outline viewer
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.ui.part.IPage#dispose()
	 */
	public void dispose() {
		// unhook outline viewer
		getSelectionSynchronizer().removeViewer(getViewer());
		// dispose
		super.dispose();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.part.IPage#getControl()
	 */
	public Control getControl() {
		return getViewer().getControl();
	}
	
	/**
	 * @see org.eclipse.ui.part.IPageBookViewPage#init(org.eclipse.ui.part.IPageSite)
	 */
	public void init(IPageSite pageSite) {
		super.init(pageSite);
		ActionRegistry registry = getActionRegistry();
		IActionBars bars = pageSite.getActionBars();
		String id = ActionFactory.UNDO.getId();
		bars.setGlobalActionHandler(id, registry.getAction(id));
		id = ActionFactory.REDO.getId();
		bars.setGlobalActionHandler(id, registry.getAction(id));
		id = ActionFactory.DELETE.getId();
		bars.setGlobalActionHandler(id, registry.getAction(id));
	}
}
/**
 * Parses the single String representation of the list into 
 * list items.
 */
private List parseString(String stringList) {
	ArrayList v = new ArrayList();
	if (stringList != null) {
		StringTokenizer st = new StringTokenizer(stringList, ","); //$NON-NLS-1$

		while (st.hasMoreElements()) {
			v.add(st.nextElement());
		}
	}
	return v;
	//return (String[]) v.toArray(new String[v.size()]);
}
private void copyPageData(MainPage fromPage,MainPage toPage){
	 SubPage displaySubPage =  null ;
	 toPage.setPageNo(fromPage.getPageNo());
	 toPage.setLayout(fromPage.getLayout());
	 toPage.setName(fromPage.getName());
	 //toPage.setNoOfSubPages(fromPage.getNoOfSubPages());
	 toPage.setTheme(fromPage.getTheme());
	 toPage.setRoles(fromPage.getRoles());
	 toPage.setIconType(fromPage.getIconType());
	 toPage.setSelectedSubPageNo(fromPage.getSelectedSubPageNo());
	 toPage.setSelectedParentPageNo(fromPage.getSelectedParentPageNo());
	 //toPage.setDisplayState(fromPage.getDisplayState());
	 //TODO Copy selectedsubpageno and selectedParentPageNo
	 List fromPageChildren = fromPage.getChildren() ;
	 	
	 	//Find displaySubPage in fromPage (if it exists)
	 	for ( int i = 0 ; i < fromPageChildren.size() ; i++ ){
	 		if(fromPageChildren.get(i) instanceof SubPage 
	 				&& ((SubPage)fromPageChildren.get(i)).isDummy()){
	 			displaySubPage = (SubPage)fromPageChildren.get(i);
	 		}
	 	}	
	 	//Update frompage
	 	if ( null != displaySubPage ) {
			for ( int i = 0 ; i < fromPageChildren.size() ; i++ ) {
				if ( fromPageChildren.get(i) instanceof SubPage 
						&& ( false == ((SubPage)fromPageChildren.get(i)).isDummy() )
						&& displaySubPage.getPageNo() == ((SubPage)fromPageChildren.get(i)).getPageNo() ) {
					SubPage associatedSubPage = (SubPage)fromPageChildren.get(i);
					cleanSubPage(associatedSubPage);
					copyPlaceHolders(displaySubPage, associatedSubPage);
				}
			}
		}
	 	//copy data
	 	for ( int i = 0 ; i < fromPageChildren.size() ; i++ ){
			
			if(fromPageChildren.get(i) instanceof PlaceHolder){
				PlaceHolder fromPlaceHolder = (PlaceHolder)fromPageChildren.get(i);
				PlaceHolder newPlaceHolder = new PlaceHolder();
				//Copy placeholder info
				copyPlaceHolderProperties(fromPlaceHolder, newPlaceHolder);
				//newPlaceHolder.set
				System.out.println("frompage is : " + fromPage);
				System.out.println("topage is : " + toPage);
				
				System.out.println("check it: layout" + fromPlaceHolder.getLayout());
				System.out.println("" + fromPlaceHolder.getName());
				System.out.println("" + fromPlaceHolder.getPlaceHolderNo());
				//System.out.println("" + fromPlaceholder.get);
				
				toPage.addPlaceHolder(newPlaceHolder);
				
				for(int j = 0 ; j < fromPlaceHolder.getChildren().size() ; j++){
					if(fromPlaceHolder.getChildren().get(j) instanceof Portlet){
						Portlet fromPortlet = (Portlet)fromPlaceHolder.getChildren().get(j);
						Portlet newPortlet = createDuplicatePortlet(fromPortlet);
						/*Portlet newPortlet = new Portlet();
						newPortlet.setIconType(fromPortlet.getIconType());
						newPortlet.setName(fromPortlet.getName());
						newPortlet.setPortletNo(fromPortlet.getPortletNo());
						newPortlet.setRoles(fromPortlet.getRoles());
						*/
						newPlaceHolder.addPortlet(newPortlet);
					}
				}
			} else if ( fromPageChildren.get(i) instanceof SubPage ) {
				//code to copy subPages
				SubPage fromSubPage = (SubPage)fromPageChildren.get(i);
				if(false == fromSubPage.isDummy()){
					SubPage newSubPage = new SubPage();
					copySubPageProperties(fromSubPage,newSubPage);
					//toPage.setNoOfSubPages(toPage.getNoOfSubPages()+1);
					toPage.addSubPage(newSubPage);
					copyPlaceHolders(fromSubPage, newSubPage);
				}
			}
		}
		
		toPage.setDisplayState(fromPage.getDisplayState());
}

public void cleanPage(MainPage page){
	 List pageChildren = page.getChildren() ;
		int noOfChildren = pageChildren.size() ;
		System.out.println("cleaning page: ");
		System.out.println("page is dummy : " + page.isDummy());
		System.out.println("page children are: " + pageChildren.size());
		for(int i = 0 , j = 0 ; i < noOfChildren ; i++ ) {
			System.out.println("i = "+i +" j = " + j + "child is : " + pageChildren.get(j).getClass().getName());
			if ( pageChildren.get(j) instanceof PlaceHolder ) {
				page.removePlaceHolder((PlaceHolder)pageChildren.get(j));
				System.out.println("placeholder removed.....");
				continue;
			}
			else if ( pageChildren.get(j) instanceof SubPage && false == ((SubPage)pageChildren.get(j)).isDummy()) {
				//TODO write code to remove subpages if required.
				//page.setNoOfSubPages(page.getNoOfSubPages()-1);
				int previousSize = pageChildren.size();
				int previousDisplayState = page.getDisplayState();
				
				page.removeSubPage((SubPage)pageChildren.get(j));
				System.out.println("subpage removed...");
				int newSize = pageChildren.size();
				if(page.isDummy()){
					j = j - (previousSize - newSize) + 1 ;
				}
				int newDisplayState = page.getDisplayState();
				if ( Constants.PAGE_CONTENT_VISIBLE == previousDisplayState
						&& Constants.PAGE_CHILD_VISIBLE == newDisplayState ){
					j--;
				}
				continue;
			}else{
				//clean pagechildgroup and displaysubpage also
				j++;
			}
		}
		
		int noOfChildrenRemoved = 0;
		int l = 0;
		int k =0;
		/*for(int i = 0 , j = 0; i < noOfChildren ; i++ ) {
			j = noOfChildren-i-1;
			if ( pageChildren.get(j) instanceof PlaceHolder ) {
				page.removePlaceHolder((PlaceHolder)pageChildren.get(j));
				System.out.println("placeholder removed.....");
				k++;
			}
			else if ( pageChildren.get(j) instanceof SubPage ) {
				//TODO write code to remove subpages if required.
				//page.setNoOfSubPages(page.getNoOfSubPages()-1);
				page.removeSubPage((SubPage)pageChildren.get(j));
				l++;
			}else{
				//clean pagechildgroup and displaysubpage also
				//j++;
			}
			noOfChildrenRemoved=k+l;
		}*/
		
		System.out.println("cleaning page: exiting");
		
}
public void cleanSubPage(SubPage page){
	 List pageChildren = page.getChildren() ;
		int noOfChildren = pageChildren.size() ;
		System.out.println("page children are: " + pageChildren.size());
		for(int i = 0 , j = 0 ; i < noOfChildren ; i++ ) { 
			if ( pageChildren.get(j) instanceof PlaceHolder ) {
				page.removePlaceHolder((PlaceHolder)pageChildren.get(j));
				System.out.println("placeholder removed.....");
			}else {
				j++;
			}
		} 
}

public void removePlaceHoldersFromPage(Page page){
	 List pageChildren = page.getChildren() ;
		int noOfChildren = pageChildren.size() ;
		System.out.println("page children are: " + pageChildren.size());
		for(int i = 0 , j = 0 ; i < noOfChildren ; i++ ) { 
			if ( pageChildren.get(j) instanceof PlaceHolder ) {
				page.removePlaceHolder((PlaceHolder)pageChildren.get(j));
				System.out.println("placeholder removed.....");
			}else{
				j++ ;
			}
		} 
}
private void copyPlaceHolders(Page fromPage,Page toPage){
	 
	 List fromPageChildren = fromPage.getChildren() ;
		//System.out.println("fromPageChildren.size()  " + fromPageChildren.size());
	 for ( int i = 0 ; i < fromPageChildren.size() ; i++ ){
			
			if(fromPageChildren.get(i) instanceof PlaceHolder){
				//System.out.println(" placeholder: " + i  );
				PlaceHolder fromPlaceHolder = (PlaceHolder)fromPageChildren.get(i);
				PlaceHolder newPlaceHolder = new PlaceHolder();
				//Copy placeholder info
				copyPlaceHolderProperties(fromPlaceHolder, newPlaceHolder);
		
				toPage.addPlaceHolder(newPlaceHolder);
				
				for(int j = 0 ; j < fromPlaceHolder.getChildren().size() ; j++){
					if(fromPlaceHolder.getChildren().get(j) instanceof Portlet){
						Portlet fromPortlet = (Portlet)fromPlaceHolder.getChildren().get(j);
						Portlet newPortlet = createDuplicatePortlet(fromPortlet);
						/*Portlet newPortlet = new Portlet();
						newPortlet.setIconType(fromPortlet.getIconType());
						newPortlet.setName(fromPortlet.getName());
						newPortlet.setPortletNo(fromPortlet.getPortletNo());
						newPortlet.setRoles(fromPortlet.getRoles());
						*/
						newPlaceHolder.addPortlet(newPortlet);
					}
				}
			}
		}
		
}

public Portlet createDuplicatePortlet(Portlet portlet){
	 Portlet newPortlet = new Portlet();
	 newPortlet.setIconType(portlet.getIconType());
	 newPortlet.setName(portlet.getName());
	 newPortlet.setPortletNo(portlet.getPortletNo());
	 newPortlet.setRoles(portlet.getRoles());
	 newPortlet.setToolTip(portlet.getToolTip());
	 newPortlet.setCpfPortlet(portlet.getCpfPortlet());
	 newPortlet.setHelpEnabled(portlet.isHelpEnabled());
	 newPortlet.setHelpScreen(portlet.getHelpScreen());

	 return newPortlet;
}
private void copyPlaceHolderProperties(PlaceHolder fromPlaceHolder , PlaceHolder toPlaceHolder){
	 toPlaceHolder.setLayout(fromPlaceHolder.getLayout());
	 toPlaceHolder.setPlaceHolderNo(fromPlaceHolder.getPlaceHolderNo());
	 toPlaceHolder.setName(fromPlaceHolder.getName());
	 toPlaceHolder.setRoles(fromPlaceHolder.getRoles());
		
}

private void copySubPageProperties(SubPage fromSubPage , SubPage toSubPage){
	 //toSubPage.setDummy(fromSubPage.isDummy());
	 toSubPage.setIconType(fromSubPage.getIconType());
	 toSubPage.setLayout(fromSubPage.getLayout());
	 toSubPage.setName(fromSubPage.getName());
	 toSubPage.setNoOfSubPages(fromSubPage.getNoOfSubPages());
	 toSubPage.setPageNo(fromSubPage.getPageNo());
	 toSubPage.setParentPageNo(fromSubPage.getParentPageNo());
	 toSubPage.setRoles(fromSubPage.getRoles());
	 toSubPage.setTheme(fromSubPage.getTheme());
}
//helper function to get display page .
private MainPage getDisplayPage(SiteMap siteMap){
	MainPage displayPage = null;
	List siteMapChildren = siteMap.getChildren();
	for ( int i = 0 ; i < siteMapChildren.size() ; i++ ) {
		if (siteMapChildren.get(i) instanceof MainPage && ((MainPage)siteMapChildren.get(i)).isDummy()){
			displayPage = (MainPage)siteMapChildren.get(i);
		}
	}
	return displayPage;
}
//helper function to get data page having the given pageNo.
private MainPage getDataPage(SiteMap siteMap,int pageNo){
	MainPage dataPage = null;
	 List displayPageSiblings = siteMap.getChildren();
	 for ( int i = 0 ; i < displayPageSiblings.size() ; i++ ) {
			if (displayPageSiblings.get(i) instanceof MainPage 
					&&( false == ((MainPage)displayPageSiblings.get(i)).isDummy()) 
					&& pageNo == ((MainPage)displayPageSiblings.get(i)).getPageNo()){
				dataPage= (MainPage) displayPageSiblings.get(i);
			}
		}
		return dataPage;
}


//helper function to get display subPage .
private SubPage getDisplaySubPage(MainPage displayPage){
	SubPage displaySubPage = null;
	List displayPageChildren = displayPage.getChildren();
	for ( int i = 0 ; i < displayPageChildren.size() ; i++ ) {
		if (displayPageChildren.get(i) instanceof SubPage && ((SubPage)displayPageChildren.get(i)).isDummy()){
			displaySubPage = (SubPage)displayPageChildren.get(i);
		}
	}
	return displaySubPage;
}

//helper function to get data subPage having the given pageNo.
private SubPage getDataSubPage(MainPage displayPage,int pageNo){
	SubPage dataSubPage = null;
	 List displayPageChildren = displayPage.getChildren();
	 for ( int i = 0 ; i < displayPageChildren.size() ; i++ ) {
			if (displayPageChildren.get(i) instanceof SubPage 
					&&( false == ((SubPage)displayPageChildren.get(i)).isDummy()) 
					&& pageNo == ((SubPage)displayPageChildren.get(i)).getPageNo()){
				dataSubPage= (SubPage) displayPageChildren.get(i);
			}
		}
		return dataSubPage;
}
public static CPFPortlet getUpdatedCPFPortlet(String projectName , int portletId) {
	
	CPFPlugin.getDefault().log("SiteMapEditor  :getUpdatedCPFPortlet for the.........."+projectName);
	
	
	IFolder folder = getProjectHandle(projectName).getFolder(
			new Path(".resources").append("portal"));
	String path = Platform.getLocation().toOSString()
			+ folder.getFullPath().toOSString();

	File portalFolder = new File(path);
	if (portalFolder.exists()) {
		// got the entities folders
		File[] entitiesFolders = portalFolder.listFiles();
		// get the persisted files in these folders
		for (int i = 0; i < entitiesFolders.length; i++) {
			File[] dataPersisFiles = entitiesFolders[i].listFiles();
			for (int j = 0; j < dataPersisFiles.length; j++) {
				if (dataPersisFiles[j].getName().endsWith(".ser")) {
					CPFPortlet portlet = CPFPortalObjectPersister
							.getInstance().readObject(dataPersisFiles[j]);
					if (portlet != null) {
						if ( portletId == portlet.getPortletId() ) {
							return portlet;
						}
					}
				}
			}

		}
	}

	return null;
}

public static IProject getProjectHandle(String projectName) {
	if (projectName != null) {
		IProject proj = ResourcesPlugin.getWorkspace().getRoot().getProject(
				projectName);
		CPFPlugin.getDefault().info ("SiteMapEditor : getting proj handle: " + proj);
		return proj;
	}
	else
		return null;
}

}
