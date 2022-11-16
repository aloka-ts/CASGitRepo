package com.genband.m5.maps.ide.wizard;

import java.util.ArrayList;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.jface.wizard.WizardPage;

import com.genband.m5.maps.common.CPFConstants;
import com.genband.m5.maps.ide.CPFPlugin;
import com.genband.m5.maps.ide.model.CPFPortlet;
import com.genband.m5.maps.ide.model.CPFScreen;
import com.genband.m5.maps.ide.model.util.CPFPortalObjectPersister;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.File;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;

public class CPFScreenLinkJSPsPage extends WizardPage {

	private CPFScreenCreationWizard wizard;

	private Composite composite;

	String fulljspName = null;

	String jspPathSubString = null;

	public CPFScreenLinkJSPsPage(ISelection selection,
			CPFScreenCreationWizard wizard) {
		super("LinkJSPsPage");
		setTitle("New Provisioning Screen Creation");
		setDescription("Select a screen to link this new screen.Link a List type Screen to Create/Details type Screen "
				+ "and Link Create/Details type Screen to a List type Screen");
		this.wizard = wizard;
	}

	/**
	 * Creates the main window's contents
	 * 
	 * @param shell the main window
	 */
	public void createControl(Composite parent) {
		initializeDialogUnits(parent);

		composite = new Composite(parent, SWT.NONE);
		composite.setFont(parent.getFont());

		int nColumns = 4;

		GridLayout layout = new GridLayout();
		layout.numColumns = nColumns;
		composite.setLayout(layout);

		Group group = new Group(composite, GridData.FILL_HORIZONTAL);
		layout = new GridLayout();
		layout.numColumns = 4;
		group.setLayout(layout);

		GridData gridD = new GridData(GridData.FILL_HORIZONTAL);
		gridD.horizontalSpan = 4;
		gridD.verticalIndent = 15;
		group.setLayoutData(gridD);
		group.setText("Link this Screen With an Existing Screen With Same Base Entity:");

		//	

		Label lbl = new Label(group, SWT.NONE);
		lbl.setText("Select a Screen to Link this Screen:");
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.horizontalSpan = 4;
		gridData.verticalIndent = 10;
		lbl.setLayoutData(gridData);

		final Label lb = new Label(group, SWT.NONE);
		lb.setText("Screen:");
		gridData = new GridData(GridData.FILL_HORIZONTAL);
		screenName = new CCombo(group, SWT.BORDER|SWT.BORDER|SWT.READ_ONLY);
		gridData.horizontalSpan = 3;
		screenName.setLayoutData(gridData);
		String resourcesPath = getCPFPortalPersisterResourcePath();

		if (resourcesPath != null) {
			this.loadCPFScreensToLink(resourcesPath);
			if (this.screenLoaded.size() != 0) {
				for (int j = 0; j < screenLoaded.size(); j++) {
					CPFScreen screen = screenLoaded.get(j);
					fulljspName = screen.getJspName();
					if (fulljspName != null) {
						String[] substr = fulljspName.split("/");
						int i = fulljspName.indexOf(substr[substr.length - 1]);
						jspPathSubString = fulljspName.substring(0, i);
						CPFPlugin.getDefault().log(
								"The JSP Name is!!!!!!!"
										+ substr[substr.length - 1]
										+ "Sub String is.." + jspPathSubString);
						screenName.add(substr[substr.length - 1] + ".xhtml");
						
					}

				}
				screenName.add("");
				
//				   if(screenName.getItemCount()!=0)
//					 screenName.select(0);
			}
		}
		
		//enable all operations 
		createRoles=wizard.getFirstPage().getRolesforCreate();
		modifyRoles=wizard.getFirstPage().getRolesforModify();
		viewRoles=wizard.getFirstPage().getRolesforView();
		deleteRoles=wizard.getFirstPage().getRolesforDelete();
		listRoles=wizard.getFirstPage().getRolesforList();
		
		screenName.addListener(SWT.Selection, new Listener() {
			
			public void handleEvent(Event e) {
				
				loadRolesForScreen();
				
			}
		});

		

			lbl = new Label(group, SWT.NONE);
			lbl.setText("Select the actions Supported by this List Screen:");
			gridData = new GridData(GridData.FILL_HORIZONTAL);
			gridData.horizontalSpan = 4;
			gridData.verticalIndent = 10;
			lbl.setLayoutData(gridData);

			gridD = new GridData(GridData.FILL_HORIZONTAL);
			gridD.horizontalSpan = 4;
			create = new Button(group, SWT.CHECK);
			create.setLayoutData(gridD);
			create.setEnabled(false);
			create.setText(CPFConstants.OperationType.CREATE.name());
	//		operations.add(CPFConstants.OperationType.CREATE);
			create.setSelection(false);
			create.addListener(SWT.Selection, new Listener() {
				public void handleEvent(Event e) {
					
					if (create.getSelection()) {
						    if (operations
								.indexOf(CPFConstants.OperationType.CREATE) == -1)
							operations.add(CPFConstants.OperationType.CREATE);

						} else {
						    if (operations
								.indexOf(CPFConstants.OperationType.CREATE) != -1)
							operations
									.remove(CPFConstants.OperationType.CREATE);
					}
				}
			});

			gridD = new GridData(GridData.FILL_HORIZONTAL);
			gridD.horizontalSpan = 4;
			modify = new Button(group, SWT.CHECK);
			modify.setLayoutData(gridD);
			modify.setText(CPFConstants.OperationType.MODIFY.name());
			modify.setEnabled(false);
		//	operations.add(CPFConstants.OperationType.MODIFY);
			modify.setSelection(false);
			modify.addListener(SWT.Selection, new Listener() {
				public void handleEvent(Event e) {
					
					if (modify.getSelection()) {
						if (operations
								.indexOf(CPFConstants.OperationType.MODIFY) == -1)
							operations.add(CPFConstants.OperationType.MODIFY);

					} else {
						if (operations
								.indexOf(CPFConstants.OperationType.MODIFY) != -1)
							operations
									.remove(CPFConstants.OperationType.MODIFY);
					}
				}
			});

			gridD = new GridData(GridData.FILL_HORIZONTAL);
			gridD.horizontalSpan = 4;
			view = new Button(group,SWT.CHECK);
			view.setLayoutData(gridD);
			view.setText(CPFConstants.OperationType.VIEW.name());
			view.setEnabled(false);
	//		operations.add(CPFConstants.OperationType.VIEW);
			view.setSelection(false);
			view.addListener(SWT.Selection, new Listener() {
				public void handleEvent(Event e) {
					
					if (view.getSelection()) {
						if (operations.indexOf(CPFConstants.OperationType.VIEW) == -1)
							operations.add(CPFConstants.OperationType.VIEW);

					} else {
						if (operations
								.indexOf(CPFConstants.OperationType.VIEW) != -1)
							operations
									.remove(CPFConstants.OperationType.VIEW);
					}

				}
			});

			gridD = new GridData(GridData.FILL_HORIZONTAL);
			gridD.horizontalSpan = 4;
			delete = new Button(group, SWT.CHECK);
			delete.setLayoutData(gridD);
			delete.setText(CPFConstants.OperationType.DELETE.name());
			
			if(this.islistAndDelete){
//				deleteRoles=this.wizard.getFirstPage().getRolesforDelete();
				CPFPlugin.getDefault().log("The Delete roles are.."+deleteRoles);
				if(deleteRoles!=null&&deleteRoles.size()!=0){
			     delete.setSelection(true);
			     operations.add(CPFConstants.OperationType.DELETE);
				}else{
					delete.setEnabled(false);
				}
			}else{
			   delete.setEnabled(false);
			}
			delete.addListener(SWT.Selection, new Listener() {
				public void handleEvent(Event e) {
					
					if (delete.getSelection()) {
						if (operations
								.indexOf(CPFConstants.OperationType.DELETE) == -1)
							operations.add(CPFConstants.OperationType.DELETE);

				    } else {
						if (operations
								.indexOf(CPFConstants.OperationType.DELETE) != -1)
							operations
									.remove(CPFConstants.OperationType.DELETE);
					}
				}
			});

			gridD = new GridData(GridData.FILL_HORIZONTAL);
			gridD.horizontalSpan = 4;
			sort = new Button(group, SWT.CHECK);
			sort.setLayoutData(gridD);
			sort.setText(CPFConstants.OperationType.SORT.name());
			if(this.islistAndDelete){
			    sort.setSelection(true);
			    operations.add(CPFConstants.OperationType.SORT);
			}else{
				sort.setEnabled(false);
			}
			sort.addListener(SWT.Selection, new Listener() {
				public void handleEvent(Event e) {
					
					    if (sort.getSelection()) {
						    if (operations.indexOf(CPFConstants.OperationType.SORT) == -1)
							operations.add(CPFConstants.OperationType.SORT);

						} else {
	                         if (operations
								.indexOf(CPFConstants.OperationType.SORT) != -1)
							operations
									.remove(CPFConstants.OperationType.SORT);
					    }

				}
			});

			gridD = new GridData(GridData.FILL_HORIZONTAL);
			gridD.horizontalSpan = 4;
			search = new Button(group, SWT.CHECK);
			search.setLayoutData(gridD);
			search.setText(CPFConstants.OperationType.SEARCH.name());
			
			if(this.islistAndDelete){
			    search.setSelection(true);
			    operations.add(CPFConstants.OperationType.SEARCH);
			}else{
				search.setEnabled(false);
			}
			search.addListener(SWT.Selection, new Listener() {
				public void handleEvent(Event e) {
					if (search.getSelection()) {
						if (operations
								.indexOf(CPFConstants.OperationType.SEARCH) == -1)
							operations.add(CPFConstants.OperationType.SEARCH);

				    } else {
						if (operations
								.indexOf(CPFConstants.OperationType.SEARCH) != -1)
							operations
									.remove(CPFConstants.OperationType.SEARCH);
					}
				}
			});
		
//	    this.loadRolesForScreen();
        getWizard().getContainer().updateButtons();
		setControl(composite);

		Dialog.applyDialogFont(composite);
	}

	private String getCPFPortalPersisterResourcePath() {

		String resourcesPath = null;

		try {

			CPFScreenMainPage page1 = this.wizard.getFirstPage();
			CPFScreenCreateSecondPage page2 = this.wizard.getSecondPage();
			IFolder resFolder = ResourcesPlugin.getWorkspace().getRoot()
					.getProject(page1.getProjectName()).getFolder(".resources");
			String platformPath = Platform.getLocation().toOSString();
			IFolder baseEntFolder = null;
			String baseEntityName = page2.getBaseEntity().getName();

			if (baseEntityName != null) {

				IFolder portalFolder = resFolder.getFolder("portal");

				if (!portalFolder.exists()) {
					CPFPlugin.getDefault()
							.log("Portal folder donot Exist!!!!!");
					resourcesPath = null;
				} else if (portalFolder.exists()) {
					baseEntFolder = portalFolder.getFolder(baseEntityName);
					if (!baseEntFolder.exists()) {
						resourcesPath = null;
					} else if (baseEntFolder.exists()) {
						resourcesPath = platformPath
								+ baseEntFolder.getFullPath().toOSString();
					}
				}
			}

		} catch (Exception c) {
			CPFPlugin
					.getDefault()
					.log(
							"CPFScreenLinkJSPsPage: Exception thrown while getting reource path of entity persister ");

		}
		return resourcesPath;
	}

	private void loadCPFScreensToLink(String cpfPortalsPath) {
		CPFPlugin.getDefault().log(
				"The Loading Path for Linking is...." + cpfPortalsPath);
		CPFPortalObjectPersister persister = CPFPortalObjectPersister
				.getInstance();
		File file = new File(cpfPortalsPath);
		File[] serFiles = file.listFiles();
		CPFPlugin.getDefault().log("The Files  found are...." + serFiles);
		try{
		if (serFiles != null) {
			for (int i = 0; i < serFiles.length; i++) {
 
				if (serFiles[i].getName().endsWith(".ser")) {
					CPFPlugin.getDefault().log(
							"The Persister File found is...."
									+ serFiles[i].getName());
					CPFPortlet portal = persister.readObject(serFiles[i]);
			if(portal!=null){
					CPFPlugin.getDefault().log(
							"The Portal Object Found !!...." + portal);
					if (islistAndDelete) {
						CPFScreen screen = portal.getDetailsScreen();
						CPFPlugin.getDefault().log(
								"The Details Screen Found is..." + screen);
						if (screen != null
								&& screen.getInterfaceType().contains(
										CPFConstants.InterfaceType.PORTLET)) {
							CPFPlugin.getDefault().log(
									"ADDING Details Screen To Found List.!!!!!!!!!.");
							screenLoaded.add(screen);
							portalScreenMap.put(screen, portal);
							portalFileMap.put(portal, serFiles[i]);
						}
					} else {
						CPFScreen screen = portal.getListScreen();
						CPFPlugin.getDefault().log(
								"The List Screen Found is..." + screen);
						if (screen != null
								&& screen.getInterfaceType().contains(
										CPFConstants.InterfaceType.PORTLET)) {
							CPFPlugin.getDefault().log(
							"ADDING List Screen To Found List.!!!!!!!!!.");
							screenLoaded.add(screen);
							portalScreenMap.put(screen, portal);
							portalFileMap.put(portal, serFiles[i]);
						}
					}

				}
			}
			}
		}
		}catch(Exception e){
			CPFPlugin.getDefault().error("Exception is thrown by reading file from persistence store"+e);
		}

	}
	
	 public void loadRolesForScreen(){
		 
		    String screenNm = screenName.getText();
			
               if (screenNm != null && !screenNm.equals("")) {
				
				String jspNm = screenNm.substring(0, screenNm.indexOf("."));
				jspNm = jspPathSubString + jspNm;
				for (int j = 0; j < screenLoaded.size(); j++) {
					CPFScreen screen = screenLoaded.get(j);
					String jspName = screen.getJspName();
					CPFPlugin.getDefault().log(
							"The Screen Selected For LINKING is!!!!!!!!!!"
									+ jspNm + "And Screen Compared is|||"
									+ jspName);

					if (jspName != null && jspName.equals(jspNm)) {

						selectedScreen = screen;
						
						//updatye create/modify operations in case its create case and selected screen is List screen

						selectedPortal = portalScreenMap.get(screen);

						if (selectedPortal != null) {
							persisterFile = portalFileMap
									.get(selectedPortal);
						}
						CPFPlugin.getDefault().log(
								"The CPFScreen Object for Selected Jsp is!!!!!"
										+ selectedScreen);
						break;
					}
				}
				
				if(selectedScreen==null){
					return;
				}

				Map<CPFConstants.OperationType, List<String>> linkScrRoles=selectedScreen.getMappedRoles();
				Set<CPFConstants.OperationType> operation=linkScrRoles.keySet();
						
				if(!islistAndDelete){
				if(createRoles!=null&&createRoles.size()!=0){
				     create.setEnabled(true);	
			         create.setSelection(true);
			         if(!operations.contains(CPFConstants.OperationType.CREATE))
			         operations.add(CPFConstants.OperationType.CREATE);
				}
				
				if(modifyRoles!=null&&modifyRoles.size()!=0){
					 modify.setEnabled(true);	
				     modify.setSelection(true);
				     if(!operations.contains(CPFConstants.OperationType.MODIFY))
				     operations.add(CPFConstants.OperationType.MODIFY);
					}

				if(viewRoles!=null&&viewRoles.size()!=0){
					 view.setEnabled(true);	
				     view.setSelection(true);
				     if(!operations.contains(CPFConstants.OperationType.VIEW))
				     operations.add(CPFConstants.OperationType.VIEW);
					}
				
				
				if(operation!=null){
					 Iterator<CPFConstants.OperationType> itr=operation.iterator();
					  while(itr.hasNext()){
						CPFConstants.OperationType op=itr.next();
						 java.util.List<String> roles=linkScrRoles.get(op);
						 
						 if(roles!=null&&roles.size()!=0){
							   if(op.equals(CPFConstants.OperationType.LIST)){
								    sort.setEnabled(true);
									search.setEnabled(true);
									sort.setSelection(true);
									search.setSelection(true);
									  if(!operations.contains(CPFConstants.OperationType.SORT))
									operations.add(CPFConstants.OperationType.SORT);
									  
									  if(!operations.contains(CPFConstants.OperationType.SEARCH))
									operations.add(CPFConstants.OperationType.SEARCH);
							   }
							   
							   if(op.equals(CPFConstants.OperationType.DELETE)){
								   delete.setEnabled(true);
							        delete.setSelection(true);
							        
							        if(!operations.contains(CPFConstants.OperationType.DELETE))
							        operations.add(CPFConstants.OperationType.DELETE);
							   }

							}
					   }
					}
				
				
				}else{	
				
				if(operation!=null){
					 Iterator<CPFConstants.OperationType> itr=operation.iterator();
					  while(itr.hasNext()){
						CPFConstants.OperationType op=itr.next();
						 java.util.List<String> roles=linkScrRoles.get(op);
						 
						 if(roles!=null&&roles.size()!=0){
							   if(op.equals(CPFConstants.OperationType.CREATE)){
								
								   
								   create.setEnabled(true);	
							         create.setSelection(true);
							         if(!operations.contains(CPFConstants.OperationType.CREATE))
							         operations.add(CPFConstants.OperationType.CREATE);
							   }
							   
							   if(op.equals(CPFConstants.OperationType.MODIFY)){
								   modify.setEnabled(true);	
								     modify.setSelection(true);
								     if(!operations.contains(CPFConstants.OperationType.MODIFY))
								     operations.add(CPFConstants.OperationType.MODIFY);
							   }
							   
							   if(op.equals(CPFConstants.OperationType.VIEW)){
								   view.setEnabled(true);	
								     view.setSelection(true);
								     if(!operations.contains(CPFConstants.OperationType.VIEW))
								     operations.add(CPFConstants.OperationType.VIEW);  
							   }
							   

							}
					   }
					}
			}
				
				
				CPFPlugin.getDefault().log(
						"The Actions supported are....+"+operations);
				if(!islistAndDelete){
					selectedScreen.setActionsSupported(operations);
				}
				
			}else{
				
				if(islistAndDelete){
				
				 create.setEnabled(false);	
		         create.setSelection(false);
		         modify.setEnabled(false);	
		         modify.setSelection(false);
		         view.setEnabled(false);
		         view.setSelection(false);
		         
				}else{
					 create.setEnabled(false);	
			         create.setSelection(false);
			         modify.setEnabled(false);	
			         modify.setSelection(false);
			         view.setEnabled(false);
			         view.setSelection(false);
			         sort.setEnabled(false);
					 search.setEnabled(false);
					 sort.setSelection(false);
					 search.setSelection(false);
			         
				}
			}

		}

	public void isListAndDelete(boolean isList) {
		islistAndDelete = isList;
	}

	public boolean canFlipToNextPage() {
		return islistAndDelete;
	}

	public CPFScreen getSelectedScreen() {
		return selectedScreen;
	}

	public CPFPortlet getSelectedPortal() {
		return selectedPortal;
	}

	public File getPortalPersisterfile() {
		return persisterFile;
	}

	public java.util.List<CPFConstants.OperationType> getSupportedActions() {
		return operations;
	}

	java.util.List<CPFConstants.OperationType> operations = new ArrayList<CPFConstants.OperationType>();

	java.util.List<CPFScreen> screenLoaded = new ArrayList<CPFScreen>();

	Map<CPFScreen, CPFPortlet> portalScreenMap = new HashMap<CPFScreen, CPFPortlet>();

	Map<CPFPortlet, File> portalFileMap = new HashMap<CPFPortlet, File>();

	CPFScreen selectedScreen;

	boolean islistAndDelete = false;

	private CPFPortlet selectedPortal;

	private File persisterFile;
	private Button create;
	private Button modify;
	private Button view;
	private Button delete;
	private Button sort;
	private Button search;
	CCombo screenName;
	
	java.util.List<String> createRoles;
	java.util.List<String> modifyRoles;
	java.util.List<String> viewRoles;
	java.util.List<String> deleteRoles;
	java.util.List<String> listRoles;
}
