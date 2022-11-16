package com.genband.m5.maps.ide.wizard;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWizard;
import org.eclipse.core.runtime.*;
import org.eclipse.jface.operation.*;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.core.resources.*;
import org.eclipse.core.runtime.CoreException;
import java.io.*;

import org.eclipse.ui.*;
import org.eclipse.ui.ide.IDE;

import com.genband.m5.maps.ide.builder.CPFPortletCSVWriter;
import com.genband.m5.maps.ide.model.template.ResourceBundle;
import com.genband.m5.maps.common.CPFConstants;
import com.genband.m5.maps.common.CPFConstants.RelationshipType;
import com.genband.m5.maps.ide.CPFPlugin;
import com.genband.m5.maps.ide.model.CPFAttribute;
import com.genband.m5.maps.ide.model.RelationKey;
import com.genband.m5.maps.ide.model.CPFPortlet;
import com.genband.m5.maps.ide.model.CPFScreen;
import com.genband.m5.maps.ide.model.CPFPortletPreference;
import com.genband.m5.maps.ide.model.ModelEntity;
import com.genband.m5.maps.ide.model.ModelAttribute;
import com.genband.m5.maps.ide.model.WebServiceInfo;
import com.genband.m5.maps.ide.model.util.*;

import java.util.HashMap;

public class CPFScreenCreationWizard extends Wizard implements INewWizard {

	private static CPFPlugin LOG = CPFPlugin.getDefault();
	
	private CPFScreenMainPage page1;
	
	//pages for list
    private CPFScreenCreateSecondPage page2;
    private CPFScreenCreateThirdPage page3;
    private CPFScreenWebServiceInfoPage webServicePage;
    public CPFScreenLinkJSPsPage linkPage;
    
    //pages for create
	private CPFScreenWebServiceInfoPage cwebServicePage;
	public CPFScreenCreateSecondPage cpage2;
	public CPFScreenCreateThirdPage cpage3;
	public CPFScreenLinkJSPsPage clinkPage;

	public CPFScreenNestedJSPsNamesPage nestedJSPsPage;

	

	private ISelection selection;

	private CPFScreenListFilterPage listPage;

	boolean canFinish = false;

	private ModelUtil modelUtil = ModelUtil.getInstance();

	private CPFScreen cpfScreen = new CPFScreen();
	private boolean isSoapInf = false;
	private boolean isPortletInf;
	

	/**
	 * Constructor for CPFScreen
	 */
	public CPFScreenCreationWizard() {
		super();
		setNeedsProgressMonitor(true);
	}

	/**
	 * Adding the page to the wizard.
	 */

	public void addPages() {
		page1 = new CPFScreenMainPage(selection, this);
		page1.setMessage("New Screen");
		addPage(page1);
	}

	/**
	 * This method is called when 'Finish' button is pressed in the wizard. We
	 * will create an operation and run it using wizard as execution context.
	 */
	public boolean performFinish() {
		IRunnableWithProgress op = new IRunnableWithProgress() {
			public void run(IProgressMonitor monitor)
					throws InvocationTargetException {
				
				if (!page1.isShowListTrue()){
					page2=cpage2;
					page3=cpage3;
					webServicePage=cwebServicePage;
					linkPage=clinkPage;
				}
				
				CPFPlugin.getDefault().log(
						"Performing Finish!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
				CPFPlugin.getDefault().log(
						"The view type is!!!!!!!!!!!!!!!!!!!!!!!!!!!!!"
								+ page1.getViewType());
				CPFPlugin.getDefault().log(
						"The Base entity is!!!!!!!!!!!!!!!!!!!!!!!!!!!"
								+ page2.getBaseEntity());
				CPFPlugin.getDefault().log(
						"The Selected other entities are!!!!!!!!!!!!!!"
								+ page2.getSelectedOtherEntities());
				CPFPlugin.getDefault().log(
						"The Roled map are!!!!!!!!!!!!!!!!!!!!!!!!!!!!"
								+ page1.getScreenRolesMap());
				CPFPlugin.getDefault().log(
						"The Interface type is!!!!!!!!!!!!!!!!!!!!!!!!"
								+ page1.getInterfaceTypeList());
				CPFPlugin.getDefault().log(
						"The Updated attributes list is!!!!!!!!!!!!!!!"
								+ page3.getUpdatedAttributesList());

				cpfScreen.setViewType(page1.getViewType());
				cpfScreen.setBaseEntity(page2.getBaseEntity());
				cpfScreen.setSelectedOtherEntities(page2
						.getSelectedOtherEntities());

				if (page1.isShowListTrue() && listPage != null) {
					cpfScreen.setExtraListPredicate(listPage.getFilterData());
				}
				cpfScreen.setMappedRoles(page1.getScreenRolesMap());
				cpfScreen.setInterfaceType(page1.getInterfaceTypeList());

				ModelEntity baseEntity = page2.getBaseEntity();
				List<CPFAttribute> cpfAttList = page3
						.getUpdatedAttributesList();
				List<ModelAttribute> modelEntityList = page2
						.getSelectedOtherEntities();

				java.util.List<CPFAttribute> baseEntityAtt = new java.util.ArrayList<CPFAttribute>();
				Map<RelationKey, List<CPFAttribute>> nestedAttributes = new HashMap<RelationKey, List<CPFAttribute>>();

				//we need to separate out attributes from base entity
				for (int i = 0; i < cpfAttList.size(); i++) {

					CPFAttribute att = cpfAttList.get(i);
					att.setPosition(i);
					

					//check against self-reference and reference to the same entity more than once

					if (att.isGroup()
						|| att.getRelationKey() == null) {
						LOG.info ("Adding to selected attrib list: " + att);
						baseEntityAtt.add (att);
					}
					else {

						if (nestedAttributes.get (att.getRelationKey()) == null) {
							List<CPFAttribute> otherEntityAtt = new java.util.ArrayList<CPFAttribute>();
							otherEntityAtt.add (att);
							LOG.info ("Adding to nested attrib map's new list: " + att);
							nestedAttributes.put(att.getRelationKey(), otherEntityAtt);
						}
						else {
							LOG.info ("Adding to nested attrib list: " + att);
							nestedAttributes.get (att.getRelationKey()).add (att);
						}
					}
				}				

				cpfScreen.setSelectedAttributes(baseEntityAtt);
				cpfScreen.setNestedAttributes(nestedAttributes);
				
				
				CPFPlugin.getDefault().log(
						"The Selected base entity attributes are !!!!!!!!!"
								+ baseEntityAtt.size() + " " + cpfScreen.getSelectedAttributes().size());
				CPFPlugin.getDefault().log(
						"The nested attributes list is!!!!!!!!!!!!!!!!!!!!!"
								+ nestedAttributes);

				IPath path = page1.getJSPfileLocation();
				String jspfileName = page1.getJSPfileName();

				if (jspfileName != null && path != null) {
					CPFPlugin.getDefault().log(
							"Create JSP file*******........." + path
									+ "jspfilename is....." + jspfileName);
					String jspFile = ResourcesPlugin.getWorkspace().getRoot()
							.getFolder(path).getFile(jspfileName).getFullPath()
							.toPortableString();
					CPFPlugin.getDefault().log(
							"Create JSP IFile for CPFScreen is*******........."
									+ jspFile);
					cpfScreen.setJspName(jspFile); //TODO it would be better to keep names relative to WebContent folder of project
				}

				// setting info  on CPFScreen on the basis of Interface type
				for (int i = 0; i < page1.getInterfaceTypeList().size(); i++) {
					CPFConstants.InterfaceType inf = (CPFConstants.InterfaceType) page1
							.getInterfaceTypeList().get(i);

					if (inf.equals(CPFConstants.InterfaceType.PORTLET)) {
						isPortletInf = true;

						if (page1.getPortletPrefernces() != null) {

							cpfScreen.setPreference(page1
									.getPortletPrefernces());
							CPFPlugin.getDefault().log(
									"The Portlet prefrences are!!!!!!!!!!!!!!!!!!!!!"
											+ page1.getPortletPrefernces());
						} else { //TODO it is better to keep default data in the right place i.e. page1 should have default data set

							CPFPortletPreference portletPrefer = new CPFPortletPreference();

							portletPrefer.setTitle(page1.getJSPfileName());
							portletPrefer.setPagination(10);

							java.util.List<CPFConstants.WindowMode> windowModesList = new ArrayList<CPFConstants.WindowMode>();
							windowModesList
									.add(CPFConstants.WindowMode.MINIMIZE);
							windowModesList
									.add(CPFConstants.WindowMode.MAXIMIZE);
							windowModesList.add(CPFConstants.WindowMode.NORMAL);

							portletPrefer.setWindowModes(windowModesList);
							portletPrefer
									.setDefaultWindowMode(CPFConstants.WindowMode.NORMAL);

							java.util.List<CPFConstants.PortletMode> portletModesList = new ArrayList<CPFConstants.PortletMode>();
							portletModesList.add(CPFConstants.PortletMode.VIEW);
							portletPrefer.setPortletModes(portletModesList);
							cpfScreen.setPreference(portletPrefer);
						}
					} else if (inf
							.equals(CPFConstants.InterfaceType.WEB_SERVICE)
							&& webServicePage != null) {
						isSoapInf = true;
						WebServiceInfo webInfo = webServicePage
								.getWebServiceInfo();
						cpfScreen.setWebServiceInfo(webInfo);
						CPFPlugin.getDefault()
								.log(
										"The WebService is supported........"
												+ webInfo);
					}
				}
				//

				// Setting info on CPF Screen in case of Nested jsps available for one to Many relationship b/w entities
				if (nestedJSPsPage != null) {
					
					cpfScreen.setNestedJspNames (nestedJSPsPage.getNestedJspsNamesMap());
					CPFPlugin.getDefault().log(
							"The Nested Jsps Map is!!!!!!!!!!!!!!!"
									+ nestedJSPsPage.getNestedJspsNamesMap());
				}
				
				int opId= modelUtil.getNextOperationId();
				CPFPlugin.getDefault().log("Setting OPERATION ID Pool on Screen!!! " + opId);
			    cpfScreen.setOperationIdPool(new Integer[]{opId});

				// Creating CPFPortlet and Persisting it
				try {

					CPFPortlet portal = new CPFPortlet();
					portal.setCurrentProject(page1.getProjectHandle().getName());

					if(!isPortletInf){   //in case user has gone to and frow from the wizard linkpage may be set to not null
						linkPage=null; //even in case of only web service so setting it to null in case there is not web interface
					}
					if (linkPage != null) {
						CPFPlugin
								.getDefault()
								.log(
										"Creating and Persisting CPF Portal!!!!!!!!!!!!!!!!!!!!!");

						List<CPFConstants.OperationType> actions = linkPage
								.getSupportedActions();
						CPFScreen linkScreen = linkPage.getSelectedScreen();
						CPFPortlet linkportal = linkPage.getSelectedPortal();
						File portalPersisterFile = linkPage
								.getPortalPersisterfile();
				

						String resourcesPath = getCPFPortalPersisterPathForWeb(null);
						CPFPlugin.getDefault().log(
								"The Persister Path is!!!!!!!!!!!!!!!"
										+ resourcesPath);

						CPFPlugin.getDefault().log(
								"The link screen is.........." + linkScreen);

						// handling Persistance of other screens on slected
						// CPFPortlet object for linking
						if (linkScreen != null) {
							handlePortalPersistance(linkportal,
									portalPersisterFile, resourcesPath);
						}

						if (cpfScreen.getViewType().equals(
								CPFConstants.ViewType.LIST)) {
							
							
							CPFPlugin
							.getDefault()
							.log(
							
							"Setting this screen as list Screen on Portal!!!!!!!!!!!!!!!!!!!!!");
							if(actions!=null){
							CPFPlugin
							.getDefault()
							.log(
									"Actions Supported are...."+actions.size());
							}
					        
							cpfScreen.setActionsSupported(actions);
					        portal.setListScreen(cpfScreen, true);
					      
							CPFPlugin
									.getDefault()
									.log(
											"Setting Details Link screen on this list Screen on Portal!!!!!!!!!!!!!!!!!!!!!");
							if (linkScreen != null) {
								
								if(linkportal.getListScreen()==null){
									 portal.setPortletId(linkportal.getPortletId());
								}else{
									int op = modelUtil.getNextOperationId();
									CPFPlugin.getDefault().log("NEXT OPERATION ID is !!! " + op);
									portal.setPortletId(op);
									
									String oldFileName="CreateMBean"+"_"+linkportal.getPortletId()+".java";
									
									IFile oldCreateFile=page1.getProjectHandle().getFile( new Path("/src/com/genband/m5/maps/mbeans/"+oldFileName));
									
									CPFPlugin.getDefault().log("Deleting old "+oldFileName + "File Handle is.."+oldCreateFile);
									
				   				if(oldCreateFile.exists()){
										oldCreateFile.delete(true, null);
										CPFPlugin.getDefault().log("Deleted old existing  " +oldCreateFile.getName());
									}else{
										CPFPlugin.getDefault().log("Old file "+oldFileName+" donot exists");
									}
									//updateCreateModifyViewSecurityData(op,linkScreen,null);
								}
								linkScreen.setPortletRef(portal);
								portal.setDetailsScreen(linkScreen, true);
							}else{
								int op = modelUtil.getNextOperationId();
								CPFPlugin.getDefault().log("NEXT OPERATION ID is !!! " + op);
								portal.setPortletId(op);
								
							}
						} else if (cpfScreen.getViewType().equals(
								CPFConstants.ViewType.DETAILS_VIEW)) {
							CPFPlugin
							.getDefault()
							.log(
									"Setting this screen as Details Screen on Portal!!!!!!!!!!!!!!!!!!!!!");
					        portal.setDetailsScreen(cpfScreen, true);
							CPFPlugin
									.getDefault()
									.log(
											"Setting List Link screen on Details Screen on Portal!!!!!!!!!!!!!!!!!!!!!");
							
							if (linkScreen != null) {
								    portal.setPortletId(linkportal.getPortletId());
								    linkScreen.setPortletRef(portal);
								    portal.setListScreen(linkScreen, true);
							}else{
								    int op = modelUtil.getNextOperationId();
									CPFPlugin.getDefault().log("NEXT OPERATION ID is !!! " + op);
									portal.setPortletId(op);
							}
						}

						//setting a reference to portlet from screen
						cpfScreen.setPortletRef(portal);						
						CPFPlugin
								.getDefault()
								.log(
										"WRITING OBJECT!!!! "
												+ portal
												+ " TO PERSISTER .........!!!!!!!!!!!!!!!At Location!!"
												+ resourcesPath);

						CPFPortalObjectPersister persister = CPFPortalObjectPersister
								.getInstance();
						persister.createPersisterFile(resourcesPath);

						persister.writeObject(portal);
						CPFPlugin
								.getDefault()
								.log(
										"The object has been written to persister.....!!!!!!!!!!!!!!!");

					} else{
						
						int op = modelUtil.getNextOperationId();
						 CPFPlugin.getDefault().log("NEXT OPERATION ID is !!! " + op);
						 portal.setPortletId(op);
						if (cpfScreen.getViewType().equals(
								CPFConstants.ViewType.LIST)) {
							CPFPlugin
									.getDefault()
									.log(
											"Setting this screen as list Screen on Portal!!!!!!!!!!!!!!!!!!!!!");
							cpfScreen.setActionsSupported(null);
							portal.setListScreen(cpfScreen, true);
						} else if (cpfScreen.getViewType().equals(
								CPFConstants.ViewType.DETAILS_VIEW)) {
							CPFPlugin
									.getDefault()
									.log(
											"Setting this screen as Details Screen on Portal!!!!!!!!!!!!!!!!!!!!!");
							portal.setDetailsScreen(cpfScreen, true);
						}
						
						 cpfScreen.setPortletRef(portal);
					}
					
					
					

					//Generating Code for this CPFScreen
					CPFPlugin.getDefault().info(
							"Generating Code!!!!!!!!!!!!!!!!!!!!!!!");

					if (isSoapInf) {
						LOG.info("Generating code for web service");							
						CodeGeneratorFactory.createWebServiceGenerator()
								.generateResource(portal);
						
						if(!isPortletInf){
							CPFPortletCSVWriter csvWriter= new CPFPortletCSVWriter();
							csvWriter.updatePortalSecurity(portal,page1.getProjectName());
						}
					} else {
						LOG.info("Not generating web service for now ...");
					}

					if (isPortletInf) {
						
						LOG.info("Generating code for jsf screen");
						CodeGeneratorFactory.createJsfGenerator()
								.generateResource(portal);
					} else {
						LOG.info("Not generating JSF screen for now ...");
					}
					
					//updateSecurityCSVFile(portal);

					CPFPlugin.getDefault().info(
							"Code has been Generated !!!!!!!!!!!!!!!!!!!!!!!");
					
					//update Resource Bundle Files for locales
					 updateResourceBundleFiles();
				} catch (Exception e) {
					LOG.error("Exception thrown while creating CPFPortlet ...",
							e);
				}
			}
		};

		try {
			LOG.info("Calling run ...");
			//getContainer().run (true, false, op);
			getContainer().run(false, true, op); //isForked, isCancellable, objRunnable
			LOG.info("run finished ...");

		} catch (InterruptedException e) {
			LOG.error("Got interrupt!", e);
			return false;
		} catch (InvocationTargetException e) {
			LOG.error("Got invocation exception ...", e);
			Throwable realException = e.getTargetException();
			MessageDialog.openError(getShell(), "Error", realException
					.getMessage());
			return false;
		}
		return true;
	}

	private void handlePortalPersistance(CPFPortlet portal, File persistFile,
			String filePath) {

		try {
			CPFPlugin
					.getDefault()
					.log(
							"HANDLING PERSISTANCE OF Screen that got UnLinked Because of Linking!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
			CPFPortalObjectPersister persister = CPFPortalObjectPersister
					.getInstance();
			
			CPFPortlet port = new CPFPortlet();
			port.setCurrentProject(page1.getProjectHandle().getName());

			if (persistFile != null) {
				CPFPlugin
						.getDefault()
						.log(
								"DELETING Persistance file of Link Screen!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
				persistFile.delete();
			}

			if (page1.getViewType().equals(CPFConstants.ViewType.LIST)) {

				CPFScreen listScreen = portal.getListScreen();
				
				if (listScreen != null) {
					port.setListScreen(listScreen, true);
					port.setPortletId(portal.getPortletId());
					listScreen.setPortletRef(port);
					
					persister.createPersisterFile(filePath);
					persister.writeObject(port);
					CPFPlugin
							.getDefault()
							.log(
									"PERSISTING!!!!!!! new ListScreen portal to the persister as selected portal listScreen was not null");
				}
				
			} else if (page1.getViewType().equals(
					CPFConstants.ViewType.DETAILS_VIEW)) {

				CPFScreen detailsScreen = portal.getDetailsScreen();

				if (detailsScreen != null) {
					port.setDetailsScreen(detailsScreen, true);
					port.setPortletId(this.getModelUtil().getNextOperationId());
					detailsScreen.setPortletRef(port);
					
					persister.createPersisterFile(filePath);
					persister.writeObject(port);
					CPFPlugin
					.getDefault()
					.log("Updated csv for unlinked details screen ..Portlet id." +port.getPortletId());
					
					CPFPlugin
					.getDefault()
					.log("Generating code for unlinked details screen.with new assigned portlet id..");
					if (isSoapInf) {							
						CodeGeneratorFactory.createWebServiceGenerator()
								.generateResource(port);
						
					}
					if (isPortletInf) {
						CodeGeneratorFactory.createJsfGenerator()
								.generateResource(port);
					}
					
					CPFPlugin
							.getDefault()
							.log(
									"PERSISTING!!!!!!! Writing new DetailsScreen portal to the persister as selected portal detailScreen was not null");
				}
			}
			

		} catch (Exception e) {
			CPFPlugin
					.getDefault()
					.log(
							"The exception thrown while handling Portal peristance in Screen creation wizard",
							e, IStatus.ERROR);
		}

	}

	private String getCPFPortalPersisterPathForWeb(IProgressMonitor monitor) {

		String resourcesPath = "";

		try {

			IFolder resFolder = ResourcesPlugin.getWorkspace().getRoot()
					.getProject(page1.getProjectName()).getFolder(".resources");
			String platformPath = Platform.getLocation().toOSString();
			IFolder baseEntFolder = null;

			if (cpfScreen.getBaseEntity().getName() != null) {

				IFolder portalFolder = resFolder.getFolder("portal");

				if (!portalFolder.exists())
					portalFolder.create(true, true, monitor);

				if (portalFolder.exists())
					baseEntFolder = portalFolder.getFolder(cpfScreen
							.getBaseEntity().getName());

				if (!baseEntFolder.exists())
					baseEntFolder.create(true, true, monitor);

				if (baseEntFolder.exists()) {
					resourcesPath = platformPath
							+ baseEntFolder.getFullPath().toOSString();
				}
			}

		} catch (CoreException c) {
			CPFPlugin
					.getDefault()
					.log(
							"CORE exception thrown file creating  folder in .resources ");

		}
		return resourcesPath;
	}

	private String getCPFPortalPersisterPathForSoap(IProgressMonitor monitor) {

		String resourcesPath = "";

		try {

			IFolder resFolder = ResourcesPlugin.getWorkspace().getRoot()
					.getProject(page1.getProjectName()).getFolder(".resources");
			String platformPath = Platform.getLocation().toOSString();
			IFolder baseEntFolder = null;

			if (cpfScreen.getBaseEntity().getName() != null) {

				IFolder portalFolder = resFolder.getFolder("webservice");

				if (!portalFolder.exists())
					portalFolder.create(true, true, monitor);

				if (portalFolder.exists())
					baseEntFolder = portalFolder.getFolder(cpfScreen
							.getBaseEntity().getName());

				if (!baseEntFolder.exists())
					baseEntFolder.create(true, true, monitor);

				if (baseEntFolder.exists()) {
					resourcesPath = platformPath
							+ baseEntFolder.getFullPath().toOSString();
				}
			}

		} catch (CoreException c) {
			CPFPlugin
					.getDefault()
					.log(
							"CORE exception thrown file creating  folder in .resources ");

		}
		return resourcesPath;
	}
	
	  private void updateResourceBundleFiles(){
		  CPFPlugin
			.getDefault()
			.log("Updating ResourceBundle files for Locales for the project for this screen");
		  IFolder bundleFolder = page1.getProjectHandle().getFolder("bundle");

          if (!bundleFolder.exists()) {
              	return;  
          }else{
        	  ResourceBundle bundleClas=new ResourceBundle();
        	  String attLabels=bundleClas.generate(cpfScreen);
        	  CPFPlugin
				.getDefault()
				.log("Resource bundle String from Template is..."+attLabels);
        	  String bundleFolderPath = Platform.getLocation().toOSString()+bundleFolder.getFullPath();
        	  CPFPlugin
				.getDefault()
				.log("Bundle Folder path is...."+bundleFolderPath);
        	  File bundleDir=new File(bundleFolderPath);
        	  File resFiles[]= bundleDir.listFiles();
        	  
        	  if(attLabels!=null&&!attLabels.equals("")){
	        	  for(int i=0;i<resFiles.length;i++){
	        		 if(resFiles[i].getName().endsWith(".properties")){
	        			 String filepath=bundleFolderPath+"/"+resFiles[i].getName();
	        			 try{
	        				 CPFPlugin
	        					.getDefault()
	        					.log("Resource File Path is...."+filepath);
	        				    BufferedWriter out = new BufferedWriter(new FileWriter(filepath, true));
	        			        out.write(attLabels);
	        			        out.write("\r\n");
	        			        out.close();
	        			 
	        			 }catch(FileNotFoundException e){
	        				 CPFPlugin
	        					.getDefault()
	        					.error("FileNotFoundException while appening to the resource bundle file"+filepath);
	        			 }catch(IOException e){
	        				 CPFPlugin
	     					.getDefault()
	     					.error("IOexception while appening to the resource bundle file"+filepath);
	     			 }
	        		  }
	        	  }
        	}
        	  
        	  
           }
	  }
	
	  private  BufferedWriter getCSVWriter(){
		  BufferedWriter in=null;
	  try{    

		   String path="";
		   String platformPath = Platform.getLocation().toOSString();
			IFile secFile = page1.getProjectHandle().getFile(new Path(".resources").append("security").append("security.csv"));
			if(secFile.exists()){
				path=platformPath+secFile.getFullPath().toOSString();
			}
		
		if(!path.equals("")){
           in = new BufferedWriter(new FileWriter(path,true));
         }
	  }catch (IOException e){
		  CPFPlugin
			.getDefault().error("IOException thrown while writing to csv file");
      }
	  
	  return in;
	 }


	public IWizardPage getNextPage(IWizardPage page) {

		java.util.List<CPFConstants.InterfaceType> infList = page1
				.getInterfaceTypeList();
		CPFPlugin.getDefault().log(
				"getNext page is called with..." + page.getName());
		if (page1.isShowListTrue()) {
			if (page.getName().equals("FirstPage")) {
				if (infList.indexOf(CPFConstants.InterfaceType.WEB_SERVICE) != -1) {
					CPFPlugin.getDefault().log(
							"The WebService is supported........");
					
			//		if(webServicePage==null){
						webServicePage = new CPFScreenWebServiceInfoPage(selection,
								this);
						addPage(webServicePage);
				//	}
				
					nextPage = webServicePage;
					
				} else {
					
			//		if(page2==null){
					page2 = new CPFScreenCreateSecondPage(selection, this);
					page2.setGroupBarCount(0);
					page2.isListAndDelete(true);
					addPage(page2);
			//	}
					
					nextPage = page2;
				}
				this.getContainer().updateButtons();
			} else if (page.getName().equals("WebServicePage")) {
				
			//	if(page2==null){
				page2 = new CPFScreenCreateSecondPage(selection, this);
				page2.setGroupBarCount(0);
				page2.isListAndDelete(true);
				addPage(page2);
			//	}
				
				nextPage = page2;
				this.getContainer().updateButtons();
			} else if (page.getName().equals("SecondPage")) {
				
				page3 = new CPFScreenCreateThirdPage(selection, this);
			//	addPage(page3);updated
				page3.isListAndDelete(true);
				page3.setRolesList(page1.returnRolesList());
				addPage(page3);
				nextPage = page3;
				
				if (infList.indexOf(CPFConstants.InterfaceType.WEB_SERVICE) != -1
						&&infList.indexOf(CPFConstants.InterfaceType.PORTLET)==-1) {
				     canFinish = true;  //Updated
				}else{
					canFinish = false;
				}
				this.getContainer().updateButtons();
			} else if (page.getName().equals("ThirdPage")) {

				CPFPlugin.getDefault().log(
						"The Base entity in next page is!!!!!!!!!!!!!!!!!!!!!!!!!!!"
								+ page2.getBaseEntity());
				CPFPlugin.getDefault().log(
						"The Selected other entities are in next page is!!!!!!!!!!!!!!"
								+ page2.getSelectedOtherEntities());

				ModelEntity baseEntity = page2.getBaseEntity();
				java.util.List<ModelAttribute> list = page2
						.getSelectedOtherEntities();
				java.util.List<ModelAttribute> oneToManyRelatedEnt = new ArrayList<ModelAttribute>();
				
				boolean showNestedJspPage=false;
				for(int i=0;i<page2.getSelectedAttributes().size();i++){
					CPFAttribute cpfAtt=page2.getSelectedAttributes().get(i);
					if(cpfAtt.getName().indexOf("<n>")!=-1){
						showNestedJspPage=true;
					}
				}

				for (int i = 0; i < list.size(); i++) {
					ModelAttribute mEntiy = list.get(i);
					boolean relation = mEntiy.getRelType().getMapping().equals(
							RelationshipType.OneToMany)
							|| mEntiy.getRelType().getMapping().equals(
									RelationshipType.ManyToMany);

					if (relation == true) {
						oneToManyRelatedEnt.add (mEntiy);
					}
				}
				CPFPlugin.getDefault().log(
						"The Nested Jsps page List is!!!!!!!!!!!!!!!!!!!!!!!!!!!" 
								+showNestedJspPage );
				if (infList.indexOf(CPFConstants.InterfaceType.PORTLET) != -1) {
				//	if (oneToManyRelatedEnt.size() != 0) {
					if(showNestedJspPage){
						nestedJSPsPage = new CPFScreenNestedJSPsNamesPage(
								selection, this);
						nestedJSPsPage
								.setOneToManyRelatedEntitiesNames(oneToManyRelatedEnt);
						addPage(nestedJSPsPage);
					
						nextPage = nestedJSPsPage;
						canFinish=false;
					} else {
//						if(linkPage==null){
						linkPage = new CPFScreenLinkJSPsPage(selection, this);
						linkPage.isListAndDelete(true);
						addPage(linkPage);
//						}
						
						nextPage = linkPage;
						canFinish = true;
						this.getContainer().updateButtons();
					}
					
				} else {
					if(listPage==null){
					listPage = new CPFScreenListFilterPage(selection, this);
					addPage(listPage);
					}
					
					nextPage = listPage;
					canFinish = true;
				}
				this.getContainer().updateButtons();
			} else if (page.getName().equals("NestedJSPPage")) {
//				if(linkPage==null){
				linkPage = new CPFScreenLinkJSPsPage(selection, this);
				linkPage.isListAndDelete(true);
				addPage(linkPage);
	//			}
				
				nextPage = linkPage;
				canFinish = true;
				// this.getContainer().updateButtons();
			} else if (page.getName().equals("LinkJSPsPage")) {
				if(listPage==null){
				listPage = new CPFScreenListFilterPage(selection, this);
				addPage(listPage);
				}
			
				nextPage = listPage;
				canFinish = true;
				// this.getContainer().updateButtons();

			}
		} else {

			if (page.getName().equals("FirstPage")) {
				if (infList.indexOf(CPFConstants.InterfaceType.WEB_SERVICE) != -1) {
					CPFPlugin.getDefault().log(
							"The WebService is supported........");
				//	if(cwebServicePage==null){
					cwebServicePage = new CPFScreenWebServiceInfoPage(selection,
							this);
					addPage(cwebServicePage);
				//	}
					
					nextPage = cwebServicePage;
				} else {
		//			if(cpage2==null){
					cpage2 = new CPFScreenCreateSecondPage(selection, this);
					cpage2.setGroupBarCount(1);
					cpage2.isListAndDelete(false);
					addPage(cpage2);
			//		}
					nextPage = cpage2;
				}
				this.getContainer().updateButtons();
			} else if (page.getName().equals("WebServicePage")) {
		//		if(cpage2==null){
				cpage2 = new CPFScreenCreateSecondPage(selection, this);
				cpage2.setGroupBarCount(0);
				cpage2.isListAndDelete(false);
				addPage(cpage2);
		//		}
				
				nextPage = cpage2;
				this.getContainer().updateButtons();
			} else if (page.getName().equals("SecondPage")) {
				CPFPlugin.getDefault().log(
						"The roles list on page1 is..."
								+ page1.returnRolesList());
				cpage3 = new CPFScreenCreateThirdPage(selection, this);
				cpage3.setRolesList(page1.returnRolesList());
				cpage3.isListAndDelete(false);
				addPage(cpage3);
				nextPage = cpage3;
				// canFinish=true;
				this.getContainer().updateButtons();
				if (infList.indexOf(CPFConstants.InterfaceType.PORTLET) == -1) {
					canFinish = true;
				}
				this.getContainer().updateButtons();

			} else if (page.getName().equals("ThirdPage")) {
//				if(clinkPage==null){
				clinkPage = new CPFScreenLinkJSPsPage(selection, this);
				clinkPage.isListAndDelete(false);
				addPage(clinkPage);
//				}
				
				nextPage = clinkPage;
				canFinish = true;
				this.getContainer().updateButtons();
			}
		}
		return nextPage;
	}
	
	
	

	public boolean canFinish() {
		return canFinish;
	}

	public CPFScreenMainPage getFirstPage() {
		return page1;
	}

	public CPFScreenCreateSecondPage getSecondPage() {
		
		if(page1.isShowListTrue()){
			return page2;
		}else{
			return cpage2;
		}
	}

	public CPFScreenCreateThirdPage getThirdPage() {
		if(page1.isShowListTrue()){
			return page3;
		}else{
			return cpage3;
		}
	}

	public ModelUtil getModelUtil() {
		return modelUtil;
	}

	public CPFScreen getCPFScreen() {
		return cpfScreen;
	}

	/**
	 * We will accept the selection in the workbench to see if we can initialize
	 * from it.
	 * 
	 * @see IWorkbenchWizard#init(IWorkbench, IStructuredSelection)
	 */
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		this.selection = selection;
	}
	
	IWizardPage nextPage = null;

}
