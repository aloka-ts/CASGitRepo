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
 *     Project:  CPFSupport
 *
 *     Package:  com.genband.m5.maps.ide.sitemap
 *
 *     File:     SiteMapCreationWizard.java
 *
 *     Desc:   	Creates a wizard to create a siteMap.
 *
 *	  Author 	 Date					Description
 *    ---------------------------------------------------------
 *	  Genband	 December 28, 2007		Initial Creation
 *
 **********************************************************************
 **/

package com.genband.m5.maps.ide.sitemap;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceStatus;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ProjectScope;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.IScopeContext;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.actions.WorkspaceModifyDelegatingOperation;
import org.eclipse.ui.actions.WorkspaceModifyOperation;
import org.eclipse.ui.dialogs.WizardNewFileCreationPage;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.internal.ide.IDEWorkbenchMessages;
import org.eclipse.ui.part.Page;
import org.w3c.dom.NodeList;
import com.genband.m5.maps.common.CPFConstants;
import com.genband.m5.maps.ide.CPFPlugin;
import com.genband.m5.maps.ide.builder.CPFNature;
import com.genband.m5.maps.ide.preferences.PreferenceConstants;
import com.genband.m5.maps.ide.sitemap.model.Footer;
import com.genband.m5.maps.ide.sitemap.model.Header;
import com.genband.m5.maps.ide.sitemap.model.MainPage;
import com.genband.m5.maps.ide.sitemap.model.PageChildGroup;
import com.genband.m5.maps.ide.sitemap.model.PageContentGroup;
import com.genband.m5.maps.ide.sitemap.model.SiteMap;
import com.genband.m5.maps.ide.sitemap.util.Constants;
import com.genband.m5.maps.ide.sitemap.util.ProjectUtil;
import com.genband.m5.maps.ide.sitemap.model.PlaceHolder;


/**
 * Create new sitemap Actually this file creates a wizard which is used to
 * create a new siteMap Using this wizard one can create a file having extension
 * .sitemap These files can be opened with the SiteMapEditor
 * 
 * @author Genband
 */
public class SiteMapCreationWizard extends Wizard implements INewWizard {

	private CreationPage siteMapCreationPage;
	private IStructuredSelection selection;
	private IWorkbench workbench;
	
	public SiteMapCreationWizard() {
		super();
		setNeedsProgressMonitor(true);
	}
	

	public void addPages() {
		// add pages to this wizard
		siteMapCreationPage = new CreationPage(workbench, selection);
		addPage(siteMapCreationPage);
	}

	public void init(IWorkbench workbench, IStructuredSelection selection) {
		// create pages for this wizard
		this.selection=selection;
		this.workbench=workbench;
		
	}

	/**
	 * This method will be invoked, when the "Finish" button is pressed.
	 */
	public boolean performFinish() {
		
		ProjectUtil.setProjectName(siteMapCreationPage.projectName);
		ProjectUtil.setRoles(java.util.Arrays.asList(siteMapCreationPage.returnRolesList ()));
		return siteMapCreationPage.finish();
	}

	/**
	 * This WizardPage is used to create a new SiteMap
	 */

	private class CreationPage extends org.eclipse.jface.wizard.WizardPage {
		private static final String DEFAULT_EXTENSION = "sitemap";
		private static final String SITEMAP_PERSISTER = "sitemap.xml";
		private final IWorkbench workbench;
		Composite composite = null;

		/**
		 * Create a new wizard page instance.
		 * 
		 * @param workbench
		 *            the current workbench
		 * @param selection
		 *            the current object selection
		 */
		CreationPage(IWorkbench workbench, IStructuredSelection selection) {
			super("SiteMapCreation");
			this.workbench = workbench;
			setTitle("Create a new " + " SiteMap");
			setDescription("Create a new " + DEFAULT_EXTENSION + " file");
			//setFileExtension(DEFAULT_EXTENSION);
		}

//		/** Return a new SiteMap instance. */
//		private Object createDefaultContent() {
//			SiteMap siteMap = new SiteMap();
//			Header header = new Header();
//			siteMap.addHeader(header);
//			Footer footer = new Footer();
//			siteMap.addFooter(footer);
//
//			return siteMap;
//		}

		/** Return a new SiteMap instance. */
		private Object createDefaultContent() {
			System.out.println("IT default content creation start");
			CPFPlugin.getDefault().log("lllllllllllayout is : " +siteMapCreationPage.sitemapPersister.getLayoutType());
			System.out.println("ITT roles are : " + siteMapCreationPage.makeString(siteMapCreationPage.sitemapPersister.viewRolesList));
			SiteMap siteMap = new SiteMap();
			siteMap.setName(getFileName());
			siteMap.setLayout(siteMapCreationPage.sitemapPersister.getLayoutType());
			//siteMap.setLayout("3 Column");
			siteMap.setTheme(siteMapCreationPage.sitemapPersister.getThemeType());
			
			List<String> listRoles = siteMapCreationPage.sitemapPersister.getSitemapRolesMap().get("View");
			System.out.println("ITTT roles are : " + makeString(listRoles));
			siteMap.setRoles(makeString(listRoles));
			Header header = new Header();
			Footer footer= new Footer();
			
			MainPage dummyPage = new MainPage();
			dummyPage.setDummy(true);
			dummyPage.setName("New Page" + 1);
			dummyPage.setPageNo(1);
			dummyPage.setNoOfSubPages(0);
			dummyPage.setLayout(siteMap.getLayout());
			dummyPage.setTheme(siteMap.getTheme());
			dummyPage.setRoles(siteMap.getRoles());
			dummyPage.setIconType(Constants.NORMAL);
			PageChildGroup pageChildGroup = new PageChildGroup();
			//dummyPage.addPageInnerGroup(pageChildGroup);
			PageContentGroup pageContentGroup = new PageContentGroup();
			dummyPage.addPageInnerGroup(pageContentGroup);
			//dummyPage.setType(Constants.MAIN_PAGE);
			//dummyPage.setIcon(Page.getPageIcon(Constants.MAIN_PAGE,Constants.NORMAL));
			//System.out.println("IT dummypage layout is : " + dummyPage.getLayout());
			//System.out.println("IT constants.layout3column is : " + Constants.LAYOUT_3_COLUMN);
			//System.out.println("IT constants.LayoutGeneric2Column is : " + Constants.LAYOUT_GENERIC_2_COLUMN);
			if(dummyPage.getLayout().equals(Constants.LAYOUT_GENERIC_2_COLUMN)){
				PlaceHolder placeHolder1 = new PlaceHolder();
				placeHolder1.setLayout(dummyPage.getLayout());
				placeHolder1.setPlaceHolderNo(1);
				placeHolder1.setRoles(dummyPage.getRoles());
				dummyPage.addPlaceHolder(placeHolder1);
				placeHolder1.setName(Constants.LAYOUT_GENERIC_2_COLUMN_PLACEHOLDER1);
				//dummyPage.addChild(placeHolder1);
				
				PlaceHolder placeHolder2 = new PlaceHolder();
				placeHolder2.setName(Constants.LAYOUT_GENERIC_2_COLUMN_PLACEHOLDER2);
				placeHolder2.setPlaceHolderNo(2);
				placeHolder2.setLayout(dummyPage.getLayout());
				placeHolder2.setRoles(dummyPage.getRoles());
				dummyPage.addPlaceHolder(placeHolder2);
				//dummyPage.addChild(placeHolder2);
				//PortletShape s = new PortletShape();
				//dummyPage.addChild(s);
			}
			if ( dummyPage.getLayout().equals(Constants.LAYOUT_3_COLUMN) ) {
				//System.out.println("layout 3 column deteced");
				PlaceHolder placeHolder1 = new PlaceHolder();
				placeHolder1.setName(Constants.LAYOUT_3_COLUMN_PLACEHOLDER1);
				placeHolder1.setPlaceHolderNo(1);
				placeHolder1.setLayout(dummyPage.getLayout());
				placeHolder1.setRoles(dummyPage.getRoles());
				dummyPage.addPlaceHolder(placeHolder1);
				
				PlaceHolder placeHolder2 = new PlaceHolder();
				placeHolder2.setName(Constants.LAYOUT_3_COLUMN_PLACEHOLDER2);
				placeHolder2.setPlaceHolderNo(2);
				placeHolder2.setLayout(dummyPage.getLayout());
				placeHolder2.setRoles(dummyPage.getRoles());
				dummyPage.addPlaceHolder(placeHolder2);			
				
				PlaceHolder placeHolder3 = new PlaceHolder();
				placeHolder3.setName(Constants.LAYOUT_3_COLUMN_PLACEHOLDER3);
				placeHolder3.setPlaceHolderNo(3);
				placeHolder3.setLayout(dummyPage.getLayout());
				placeHolder3.setRoles(dummyPage.getRoles());
				dummyPage.addPlaceHolder(placeHolder3);
			}
			
			MainPage page = new MainPage();
			page.setDummy(false);
			page.setPageNo(1);
			page.setNoOfSubPages(0);
			page.setName("New Page" + 1);
			page.setRoles(siteMap.getRoles());
			page.setLayout(siteMap.getLayout());
			page.setTheme(siteMap.getTheme());
			page.setIconType(Constants.NORMAL);
			//page.setType(Constants.MAIN_PAGE);
			//page.setIcon(Page.getPageIcon(Constants.MAIN_PAGE,Constants.NORMAL));
			if(page.getLayout().equals(Constants.LAYOUT_GENERIC_2_COLUMN)){
				PlaceHolder placeHolder1 = new PlaceHolder();
				placeHolder1.setLayout(page.getLayout());
				placeHolder1.setPlaceHolderNo(1);
				placeHolder1.setName(Constants.LAYOUT_GENERIC_2_COLUMN_PLACEHOLDER1);
				placeHolder1.setLayout(Constants.LAYOUT_GENERIC_2_COLUMN);
				placeHolder1.setRoles(page.getRoles());
				page.addPlaceHolder(placeHolder1);
				//page.addChild(placeHolder1);
				
				PlaceHolder placeHolder2 = new PlaceHolder();
				placeHolder2.setName(Constants.LAYOUT_GENERIC_2_COLUMN_PLACEHOLDER2);
				placeHolder2.setPlaceHolderNo(2);
				placeHolder2.setLayout(page.getLayout());
				placeHolder2.setLayout(Constants.LAYOUT_GENERIC_2_COLUMN);
				placeHolder2.setRoles(page.getRoles());
				page.addPlaceHolder(placeHolder2);
				//page.addChild(placeHolder2);
				//PortletShape s = new PortletShape();
				//page.addChild(s);
			}
			if ( page.getLayout().equals(Constants.LAYOUT_3_COLUMN) ) {
				PlaceHolder placeHolder1 = new PlaceHolder();
				placeHolder1.setName(Constants.LAYOUT_3_COLUMN_PLACEHOLDER1);
				placeHolder1.setPlaceHolderNo(1);
				placeHolder1.setLayout(Constants.LAYOUT_3_COLUMN);
				placeHolder1.setRoles(page.getRoles());
				page.addPlaceHolder(placeHolder1);
				
				PlaceHolder placeHolder2 = new PlaceHolder();
				placeHolder2.setName(Constants.LAYOUT_3_COLUMN_PLACEHOLDER2);
				placeHolder2.setPlaceHolderNo(2);
				placeHolder2.setLayout(Constants.LAYOUT_3_COLUMN);
				placeHolder2.setRoles(page.getRoles());
				page.addPlaceHolder(placeHolder2);			
				
				PlaceHolder placeHolder3 = new PlaceHolder();
				placeHolder3.setName(Constants.LAYOUT_3_COLUMN_PLACEHOLDER3);
				placeHolder3.setPlaceHolderNo(3);
				placeHolder3.setLayout(Constants.LAYOUT_3_COLUMN);
				placeHolder3.setRoles(page.getRoles());
				page.addPlaceHolder(placeHolder3);
			}
			
			
			siteMap.addHeader(header);
			siteMap.addFooter(footer);
			siteMap.addPage(dummyPage);
			siteMap.addPage(page);
			
			//PortletShape p = new PortletShape();
			//siteMap.addChild(p);
			//System.out.println();
			//System.out.println("IT default content creation exiting");
			return siteMap;
		}
		
		/*
		 * This function is called by performFinish() this does all the tasks
		 * that are to be accomplished when 'Finish' Button is clicked This
		 * function will open the newly created siteMap(file) and activate it
		 */
		boolean finish() {

			// create a new file
			saveSitemap();
			
			IFile newFile = createNewFile();

			// open newly created siteMap(file) in the editor
			IWorkbenchPage page = workbench.getActiveWorkbenchWindow()
					.getActivePage();
			if (newFile != null && page != null) {
				try {
					IDE.openEditor(page, newFile, true);
				} catch (PartInitException e) {
					e.printStackTrace();
					return false;
				}
			}
			return true;
		}
		
		
		private void saveSitemap() {
			CPFPlugin.getDefault().log("Saving Sitemap",IStatus.INFO);
			String path =Platform.getLocation().toOSString()+getProjectHandle().getFullPath().append(".resources").append("sitemap")
							.append(SITEMAP_PERSISTER).toOSString();
			try {
				File file = new File(path);
				FileReader r=new FileReader(file);
				org.w3c.dom.Document doc = sitemapPersister.createDocument(r);
				NodeList sitemapNodes = doc.getElementsByTagName("sitemaps");
				CPFPlugin.getDefault().log("The sitemaps root node is.."+sitemapNodes.item(0));
				
				if(sitemapNodes.item(0)!=null){
				sitemapPersister.toXML(doc, sitemapNodes.item(0));
				// Write to the file
				Source source = new DOMSource(doc);
				Result result = new StreamResult(file);
				Transformer xformer = TransformerFactory.newInstance()
						.newTransformer();
				xformer.transform(source, result);
				
//				SiteMapPersister persister=new SiteMapPersister();
//				persister.loadSitemap(this.projectName, fileName);
//				CPFPlugin.getDefault().log("The sitemap Roles map obtained.."+persister.getSitemapRolesMap());
				
			//String[] roles=new String[]{"NPA","SPA"};
//				List<String> roles=new ArrayList<String>();
//				roles.add("NPA");
//				roles.add("SPA");
//				List<PortletInfo> info=PortletUtil.getPortletsInfo(projectName,roles);
//				CPFPlugin.getDefault().log("The PPPPPPPPPPPPPPPPortlet info is..........."+info);
//				
//				if(info!=null){
//				for(int i=0;i<info.size();i++){
//					PortletInfo p=info.get(i);
//				CPFPlugin.getDefault().log("PortletInfo Name"+	p.getName()+" IconType "
//					+p.getIconType()+ " ToolTip "
//					+p.getToopTip());
//				}
//				}
//				
				
				}
			} catch (Exception e) {
				CPFPlugin.getDefault().log("Error saving sitemap", e,IStatus.ERROR);
			}
		}

		public void createControl(Composite parent) {
			// super.createControl(parent);
			initializeDialogUnits(parent);
			int nColumns = 4;
			composite = new Composite(parent, SWT.NONE);
			composite.setFont(parent.getFont());
			composite.getShell().setText("New Screen");
			composite.pack();
			GridLayout layout = new GridLayout();
			layout.numColumns = nColumns;
			composite.setLayout(layout);

			Group group = new Group(composite, GridData.FILL_HORIZONTAL);
			GridLayout layout1 = new GridLayout();
			layout1.numColumns = 4;
			group.setLayout(layout1);
			GridData gridD = new GridData(GridData.FILL_HORIZONTAL);
			gridD.horizontalSpan = 4;
			group.setLayoutData(gridD);
			group.setText("Select Portal Project for Sitemap:");
			new Label(group, SWT.LEFT | SWT.WRAP).setText("Portal Project Name:");
			gridD = new GridData(GridData.FILL_HORIZONTAL);
			final CCombo cpfProject = new CCombo(group, SWT.SINGLE | SWT.BORDER);
			// gridD.horizontalSpan = nColumns;
			gridD.grabExcessHorizontalSpace = true;
			gridD.horizontalSpan = 3;
			cpfProject.setLayoutData(gridD);

			IProject[] resources = ResourcesPlugin.getWorkspace().getRoot()
					.getProjects();
			try {
				for (int i = 0; i < resources.length; i++) {
					if (resources[i].isOpen()
							&& resources[i].hasNature(CPFNature.NATURE_ID)) {
						cpfProject.add(resources[i].getName());
						cpfProject.select(0);
						projectName = cpfProject.getItem(0);
						CPFPlugin.getDefault().log(
								"The PROJECT NAME IS!!!!!!!!!!!!!!"
										+ projectName);
					}
				}

				if (projectName == null)
					this
							.setErrorMessage("There is no CPF Project in the Workspace");
			} catch (CoreException c) {
				CPFPlugin.getDefault().log(
						"The Core exception was thrown while lisitng CPFprojects"
								+ c);
			}

			cpfProject.addListener(SWT.Selection, new Listener() {
				public void handleEvent(Event e) {
					projectName = cpfProject.getText();
					//ProjectUtil.setProjectName(projectName);
					CPFPlugin.getDefault().log(
							"The Project selected is....." + projectName);
					// Updating Roles Table as per the The new Project Selected
					if (table != null) {
						table.dispose();
						createRolesTable(tablegroup);
						createRoleItems();
						tablegroup.layout();
					}

				}
			});
			sitemapPersister=new SiteMapPersister();
			new Label(group, SWT.LEFT | SWT.WRAP).setText("Sitemap File Name");
			GridData grid = new GridData(GridData.FILL_HORIZONTAL);
			final Text tFileName = new Text(group, SWT.SINGLE | SWT.BORDER);
			grid.horizontalSpan = 3;
			grid.grabExcessHorizontalSpace = true;
			grid.widthHint = this.SIZING_TEXT_FIELD_WIDTH;
			tFileName.setLayoutData(grid);
			tFileName.setTextLimit(80);
			tFileName.addListener(SWT.Modify, new Listener() {
				public void handleEvent(Event e) {
					fileName = tFileName.getText();
					sitemapPersister.setFileName(fileName);
					setPageComplete(validatePage());
				}
			});

			

			if(this.isWindows()){
				swtstyle=SWT.NULL;
			}else{
				swtstyle=SWT.BORDER;
			}
			tablegroup = new Group(composite, GridData.FILL_HORIZONTAL);
			layout1 = new GridLayout();
			layout1.numColumns = 4;
			tablegroup.setLayout(layout1);
			gridD = new GridData(GridData.FILL_HORIZONTAL);
			gridD.horizontalSpan = 4;
			gridD.verticalIndent = 15;
			tablegroup.setLayoutData(gridD);
			tablegroup.setText("Set User Roles:");
			tablegroup.setRedraw(true);
			tablegroup.pack(true);
			this.createRolesTable(tablegroup);
			this.createRoleItems();

			GridData gd = new GridData(SWT.RIGHT);
			final Button advanced = new Button(composite, SWT.PUSH);
			advanced.setText("Advanced");
			advanced.setLayoutData(gd);
			advanced.setEnabled(true);
			advanced.addListener(SWT.Selection, new Listener() {

				AdvancedSiteMapPrefDialog dialog = null;

				public void handleEvent(Event e) {

					if(dialog==null){
						CPFPlugin.getDefault().log(
						"The Sitemap property dialog is null... ..");
					dialog = new AdvancedSiteMapPrefDialog(
							composite.getShell());
					Composite com = (Composite) dialog
							.createDialogArea(composite);
					dialog.create();
					}
					dialog.open();
					sitemapPersister.setThemeType(dialog.getThemeType());
					sitemapPersister.setLayoutType(dialog.getLayoutType());

					if (dialog.getReturnCode() == Window.OK) {
						// portletPreferences = dialog.getPortletPreferences();
					}

				}
			});

			getWizard().getContainer().updateButtons();
			setControl(composite);
			// setVisible(true);
			setPageComplete(false);
			Dialog.applyDialogFont(composite);
			
		}

		protected InputStream getInitialContents() {
			// SiteMap siteMapDiagram = (SiteMap) createDefaultContent();
			ByteArrayInputStream bais = null;
			try {
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				ObjectOutputStream oos = new ObjectOutputStream(baos);
				oos.writeObject(createDefaultContent()); // argument must be
				// Serializable
				oos.flush();
				oos.close();
				bais = new ByteArrayInputStream(baos.toByteArray());
			} catch (IOException ioe) {
				ioe.printStackTrace();
			}
			return bais;
		}

		public String getFileName() {
			return fileName;
		}

		public void createRolesTable(Group group) {
			CPFPlugin.getDefault().log(
					"CREATE roles tableeeeeeeeeeee ............." + group);
			table = new Table(group, SWT.SINGLE | SWT.BORDER
					| SWT.FULL_SELECTION | SWT.HIDE_SELECTION | SWT.V_SCROLL);
			GridData gridD = new GridData(GridData.FILL_HORIZONTAL);
			gridD.horizontalSpan = 4;
			gridD.heightHint = 100;
			table.setLayoutData(gridD);
			table.setHeaderVisible(true);
			table.setLinesVisible(true);

			// Create 2 columns
			for (int i = 0; i < 2; i++) {
				TableColumn column = new TableColumn(table, swtstyle);
				if (i == 0)
					column.setText("Role Name");
				if (i == 1)
					column.setText(VIEW);
//				if (i == 2)
//					column.setText(VIEW_RECURSIVELY);
				// if (i == 3)
				// column.setText("Modify");
				column.setWidth(180);
				// column.pack();
			}

		}

		public void createRoleItems() {
			roles = getRolesList();
			if (roles != null) {
				for (int j = 0; j < roles.length; j++) {
					// Create the rows
					final TableItem item = new TableItem(table, SWT.BORDER);
					CPFPlugin.getDefault().log("Adding Item ......" + item,
							IStatus.INFO);

					CPFPlugin.getDefault().log(
							"item.getParent Display ............."
									+ item.getParent().getDisplay());

					// Create an editor object to use for text editing

					CPFPlugin.getDefault().log("Adding Role ......" + roles[j]);
					final TableEditor editor = new TableEditor(table);
					editor.grabHorizontal = true;
					roleLabel = new Label(table, swtstyle);

					roleLabel.setText(roles[j]);
					roleLabel.setToolTipText(roles[j]);
					item.setText(0, roles[j]);
					editor.setEditor(roleLabel, item, 0);

					// Create an editor object to use for text editing
					final TableEditor editor3 = new TableEditor(table);
					editor3.grabHorizontal = true;
					CPFPlugin.getDefault().log(
							"Adding editor ............." + editor3);
					viewAllow = new Button(table, swtstyle | SWT.CHECK);
					viewAllow.addListener(SWT.Selection, new Listener() {
						public void handleEvent(Event e) {

							Button bt = (Button) editor3.getEditor();
							TableItem item = editor3.getItem();
							String txt = item.getText(0);
							CPFPlugin.getDefault().log(
									"The table item selected is...." + item
											+ "The Role on item is..." + txt);
							if (bt.getSelection()) {
								CPFPlugin.getDefault().log(
										"Create Selected.........");
								if (viewRoles.indexOf(txt) == -1){
									CPFPlugin.getDefault().log(
											"Adding role to create............."
													+ txt);
								viewRoles.add(txt);
								}
							} else if (!bt.getSelection()) {
								CPFPlugin.getDefault().log(
										"Create UnSelected.........");
								if (viewRoles.indexOf(txt) != -1){
									CPFPlugin.getDefault().log(
											"Removing role from create............."
													+ txt);
								viewRoles.remove(txt);
								}
							}
							sitemapRolesMap.put(VIEW, viewRoles);
							sitemapPersister.setSitemapRolesMap(sitemapRolesMap);
							setPageComplete(validatePage());
							// wizard.getCPFScreen().setMappedRoles(screenRolesMap);
						}
					});

					editor3.setEditor(viewAllow, item, 1);
					CPFPlugin.getDefault().log(
							"editor.Display ............."
									+ editor.getEditor().getDisplay());
					CPFPlugin.getDefault().log(
							"editor.getItem Display ............."
									+ editor.getItem().getDisplay());

//					// Create an editor object to use for text editing
//					final TableEditor editor2 = new TableEditor(table);
//					editor2.grabHorizontal = true;
//					viewRecurAllow = new Button(table, SWT.BORDER | SWT.CHECK);
//					viewRecurAllow.addListener(SWT.Selection, new Listener() {
//
//						public void handleEvent(Event e) {
//							Button bt = (Button) editor2.getEditor();
//							String txt = editor2.getItem().getText(0);
//							CPFPlugin.getDefault().log(
//									"The table item selected is...."
//											+ editor2.getItem()
//											+ "The Role on item is..." + txt);
//							if (bt.getSelection()) {
//								CPFPlugin.getDefault().log(
//										"View Selected.........");
//								if (viewRecurRoles.indexOf(txt) == -1){
//									CPFPlugin.getDefault().log(
//											"Adding role to View............"
//													+ txt);
//								viewRecurRoles.add(txt);
//								}
//							} else if (!bt.getSelection()) {
//								CPFPlugin.getDefault().log(
//										"View UnSelected.........");
//								if (viewRecurRoles.indexOf(txt) != -1){
//									CPFPlugin.getDefault().log(
//											"Removing role from View............"
//													+ txt);
//								viewRecurRoles.remove(txt);
//								}
//							}
//							sitemapRolesMap.put(VIEW_RECURSIVELY,
//									viewRecurRoles);
//							sitemapPersister.setSitemapRolesMap(sitemapRolesMap);
//							setPageComplete(validatePage());
//							// wizard.getCPFScreen().setMappedRoles(screenRolesMap);
//						}
//					});
//					
//					editor2.setEditor(viewRecurAllow, item, 2);

				}
				table.setVisible(true);

			}
		}

		protected IFile createFileHandle() {
			if(fileName.endsWith(".sitemap")){
				fileName=fileName.substring(0 ,fileName.indexOf("."));
			}
			return getProjectHandle().getFile(
					new Path("WebContent").append("WEB-INF").append("sitemap")
							.append(fileName).addFileExtension(
									this.DEFAULT_EXTENSION));
		}

		public IProject getProjectHandle() {
			if (projectName != null)
				return ResourcesPlugin.getWorkspace().getRoot().getProject(
						projectName);
			else
				return null;
		}

		public IFile createNewFile() {
			IFile newFile = null;
			// create the new file and cache it if successful

			final IFile newFileHandle = createFileHandle();
			final InputStream initialContents = getInitialContents();
			IRunnableWithProgress runnable = new PerformFileCreation(newFileHandle ,initialContents);
			IRunnableWithProgress op = new WorkspaceModifyDelegatingOperation (runnable);

			try {
				getContainer().run(true, true, op);
			} catch (InterruptedException e) {
				return null;
			} catch (InvocationTargetException e) {
				if (e.getTargetException() instanceof CoreException) {
					ErrorDialog
							.openError(
									getContainer().getShell(), // Was
									// Utilities.getFocusShell()
									IDEWorkbenchMessages.WizardNewFileCreationPage_errorTitle,
									null, // no special message
									((CoreException) e.getTargetException())
											.getStatus());
				} else {
					// CoreExceptions are handled above, but unexpected runtime
					// exceptions and errors may still occur.
					CPFPlugin.getDefault().log(
							"createNewFile() InvocationTarget Excrption"); //$NON-NLS-1$
					MessageDialog.openError(getContainer().getShell(),
							"FileCreationFailed", e.getTargetException()
									.getMessage());
				}
				return null;
			}

			newFile = newFileHandle;

			return newFile;
		}
		
		
		class PerformFileCreation implements IRunnableWithProgress {
			 
			 private IFile newFileHandle;
		     private InputStream initialContents ;
			
		     public PerformFileCreation(IFile newFileHandle,InputStream initialContents){
				 this.newFileHandle = newFileHandle;
				  this.initialContents = initialContents;
			}
			  
			  public void run(IProgressMonitor monitor) throws InvocationTargetException,
				InterruptedException {
				try {
					
					monitor
							.beginTask(
									IDEWorkbenchMessages.WizardNewFileCreationPage_progress,
									2000);
					try {
						createFile(newFileHandle, initialContents,
								new SubProgressMonitor(monitor, 1000));
					} catch (CoreException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} finally {
					monitor.done();
				}
			} 
			  
			  protected void createFile(IFile fileHandle, InputStream contents,
					               IProgressMonitor monitor) throws CoreException {
					           if (contents == null) {
					              contents = new ByteArrayInputStream (new byte[0]);
					          }
					           try {
					              // Create a new file resource in the workspace
					                  IPath path = fileHandle.getFullPath();
					                  IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
					                  int numSegments = path.segmentCount();
					                  if (numSegments > 2
					                          && !root.getFolder(path.removeLastSegments(1)).exists()) {
					                      // If the direct parent of the path doesn't exist, try to
					  // create the
					  // necessary directories.
					  for (int i = numSegments - 2; i > 0; i--) {
					                          IFolder folder = root.getFolder(path
					                                   .removeLastSegments(i));
					                           if (!folder.exists()) {
					                               folder.create(false, true, monitor);
					                           }
					                       }
					                   }
					                  fileHandle.create(contents, false, monitor);
					              
					          } catch (CoreException e) {
					               // If the file already existed locally, just refresh to get contents
					   if (e.getStatus().getCode() == IResourceStatus.PATH_OCCUPIED) {
					                   fileHandle.refreshLocal(IResource.DEPTH_ZERO, null);
					               } else {
					                   throw e;
					               }
					           }
					  
					           if (monitor.isCanceled()) {
					               throw new OperationCanceledException();
					          }
					       }
		  }

		// Parses the string into seperate list items and adds them to the list.
		private String[] getRolesList() {
			String items = CPFPlugin.getDefault().getPreferenceStore()
					.getString(PreferenceConstants.P_ROLES);
			String[] itemList = null;
			if (getProjectHandle() != null) {
				IScopeContext projectScope = new ProjectScope(
						getProjectHandle());
				IEclipsePreferences projectNode = projectScope
						.getNode("com.genband.sas.maps");
				if (projectNode != null) {
					CPFPlugin.getDefault().log(
							"The project node in CPFPropertyPage to save properties is..... "
									+ projectNode);
					items = projectNode.get(PreferenceConstants.P_ROLES,
							CPFPlugin.getDefault().getPreferenceStore()
									.getString(PreferenceConstants.P_ROLES));
					// do something with the value.
				}
				CPFPlugin.getDefault().log("The Roles list is.." + items);
				String[] itemArray = parseString(items);
				itemList = new String[itemArray.length];
				if (itemArray != null) {
					for (int i = 0; i < itemArray.length; i++) {

						String role = itemArray[i];
						String roleName = null;
						if (role.indexOf("(") != -1) {
							roleName = role.substring(0, role.indexOf("("));
						} else {
							roleName = itemArray[i];
						}
						itemList[i] = roleName;
					}

				}
			}
			// set roles list to pass it to the attributes property dialog page
			// to the third page
			return itemList;
		}

		public String[] returnRolesList() {
			return roles;
		}

		/**
		 * Parses the single String representation of the list into an array of
		 * list items.
		 */
		private String[] parseString(String stringList) {
			ArrayList v = new ArrayList();
			if (stringList != null) {
				StringTokenizer st = new StringTokenizer(stringList, ";"); //$NON-NLS-1$

				while (st.hasMoreElements()) {
					v.add(st.nextElement());
				}
			}
			return (String[]) v.toArray(new String[v.size()]);
		}
		
		/*private String makeString(String[] stringArray) {
			String str = "" ;
			for( int i = 0 ; i < stringArray.length-1 ; i++ ){
				str = str.concat(stringArray[i]);
				str = str.concat(",");
			}
			str = str.concat(stringArray[stringArray.length-1]);
			return str;
		}*/

		private String makeString(List stringArray) {
			String str = "" ;
			if (null == stringArray || stringArray.size() == 0 ) {
				return "";
			}
			for( int i = 0 ; i < stringArray.size()-1 ; i++ ){
				str = str.concat((String)stringArray.get(i));
				str = str.concat(",");
			}
			str = str.concat((String)stringArray.get(stringArray.size()-1));
			return str;
		}


		
//		public void setVisible(boolean visible) {
//	//		composite.setVisible(true);
//			super.setVisible(true);
//
//		}
		
		public SiteMapPersister getSitemapPersister(){
			return sitemapPersister;
		}
		
		
		/**
		 * Return true, if the file name entered in this page is valid.
		 */
		private boolean validateFilename() {
			boolean value=true;
			CPFPlugin.getDefault().log("Validating............Filename is"+fileName);
			  setErrorMessage(null);
			 if (fileName==null||fileName.equals("")) {
				 setErrorMessage("The 'file' name can not be null"); 
			     value=false;
			 }else if (-1 != fileName.indexOf('.')&&!fileName.endsWith(".sitemap")) {
			     setErrorMessage("The 'file' name must have extension ."+ DEFAULT_EXTENSION);
			     value=false;
			 }else{
				 for(java.util.List list:sitemapRolesMap.values()){
			    	 if(list.isEmpty()){
			    		 value=false; 
			    		 setErrorMessage("None of the 'Role' is selected for CPF Sitemap"); 
			    	 }else{
			    		 value=true;
			    		 break;
			    	 }
			    }
			     if(sitemapRolesMap.isEmpty()){
			    	 setErrorMessage("None of the 'Role' is selected for CPF Sitemap"); 
			    	 value=false;
			     }
			 }
			 
			 
			 CPFPlugin.getDefault().log("Validating............value is"+value +" Sitemap roles map is .."+sitemapRolesMap);
			return value;
		}

		protected boolean validatePage() {
			return validateFilename();
		}
		
		
		 public boolean isWindows(){
			  if (System.getProperty("os.name").indexOf("Win") == 0)
		          return true;
			  else
				  return false;
			  }

		Label roleLabel = null;

		Button viewAllow = null;

		Button viewRecurAllow = null;

		protected String[] roles;
		String projectName = null;
		Table table = null;
		String fileName = null;
		Group tablegroup = null;

		java.util.List<String> viewRecurRoles = new java.util.ArrayList<String>();

		java.util.List<String> viewRoles = new java.util.ArrayList<String>();

		Map<String, java.util.List<String>> sitemapRolesMap = new HashMap<String, java.util.List<String>>();

		private static final int SIZING_TEXT_FIELD_WIDTH = 200;

		private static final String VIEW = "View";

		private static final String VIEW_RECURSIVELY = "View Recursively";
		
		private SiteMapPersister sitemapPersister=null;
		
		
		int swtstyle;
		
	}

}
