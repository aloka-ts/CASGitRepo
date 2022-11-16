package com.genband.m5.maps.ide.model.util;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;

import com.genband.m5.maps.common.CPFConstants;
import com.genband.m5.maps.common.CPFConstants.OperationType;
import com.genband.m5.maps.ide.CPFPlugin;
import com.genband.m5.maps.ide.model.CPFAttribute;
import com.genband.m5.maps.ide.model.CPFPortlet;
import com.genband.m5.maps.ide.model.CPFResource;
import com.genband.m5.maps.ide.model.CPFScreen;
import com.genband.m5.maps.ide.model.ModelEntity;
import com.genband.m5.maps.ide.model.RelationKey;
import com.genband.m5.maps.ide.model.template.CreateMBean;
import com.genband.m5.maps.ide.model.template.DetailsXhtml;
import com.genband.m5.maps.ide.model.template.ListMBean;
import com.genband.m5.maps.ide.model.template.ListXhtml;

public class JsfCodeGenerator extends CodeGenerator {
	
	private CPFResource m_cpfResource;
	
	private ResourceInfo m_resourceInfo;
	
	private ModelUtil modelUtil = ModelUtil.getInstance();
	
	CPFPlugin LOG = CPFPlugin.getDefault();

	public JsfCodeGenerator () {
		LOG.info("JsfCodeGenerator constr...");	
	}
	
	@Override
	public IFile generateResource(CPFPortlet p_portlet) throws Exception {
		
		LOG.info("generateResource: " + p_portlet.getBaseEntity().getName());
		
		String path = null;     //Holds the absolute path of the generating files
		FileWriter file = null;	//To create files after writing result
		String result = null;	//HOlds the result string returned by JET intermediate Java code 
		
		CPFScreen listScreen = p_portlet.getListScreen();
		CPFScreen detailsScreen = p_portlet.getDetailsScreen();
		
		//Code generation starts here
		if(detailsScreen != null || listScreen != null) {
			String projectName = p_portlet.getCurrentProject();
			IFolder sourceFodler = ResourcesPlugin.getWorkspace().getRoot()
			.getProject(projectName).getFolder("src");

			//ensure packages are created...
			createPackage(sourceFodler);
		}
			
		//Genenrating Managed Bean and Xhtmls for Details Page Starts here...
		if(detailsScreen != null) {
			boolean list = false;	//Default list screen exists..
			try {
				if(listScreen == null) {
					list = true;
					List<CPFConstants.OperationType> actionsSupported = new ArrayList<OperationType>();
					actionsSupported.add(OperationType.CREATE);
					actionsSupported.add(OperationType.MODIFY);
					actionsSupported.add(OperationType.VIEW);
					detailsScreen.setActionsSupported(actionsSupported);
				} else {
					detailsScreen.setActionsSupported(listScreen.getActionsSupported());
				}
				m_cpfResource = new CPFResource(detailsScreen); 
				LOG.info("XHTML creation and size of base attributes is : " 
							+ detailsScreen.getSelectedAttributes().size());
				m_resourceInfo = new ResourceInfo();
				m_resourceInfo.setResourceName(detailsScreen.getBaseEntity().getName());
				m_cpfResource.setResourceInfo(m_resourceInfo);

				//Generating Xhtml....
				DetailsXhtml detailsXhtml = new DetailsXhtml();
				LOG.info("Calling generate on Details Xhtml..");

				result = detailsXhtml.generate(m_cpfResource);
				LOG.info("returned generate on Details Xhtml..");


				String fileName = detailsScreen.getJspName() + ".xhtml";
				path = Platform.getLocation().toOSString() + ResourcesPlugin.getWorkspace().getRoot()
				.getFile(new Path(fileName)).getFullPath().toOSString();	
				LOG.info("Path: " + path);

				try {
					file = new FileWriter (path);
					file.flush();
					file.write(result);
					//file.close();
					LOG.info("Xhtml has been created..");

				} catch (IOException e) {
					LOG.error("Got exception in file write", e);

				} finally {
					if (file != null) {
						file.close();
						file = null;
					}
				}

				LOG.info("After adding to screen size inside code geenrator is : " + detailsScreen.getSelectedAttributes().size());
				m_cpfResource = new CPFResource(detailsScreen);
				m_resourceInfo = new ResourceInfo();
				m_resourceInfo.setResourceName(detailsScreen.getBaseEntity().getName());
				m_cpfResource.setResourceInfo(m_resourceInfo);

				CreateMBean createMBean = new CreateMBean();
				LOG.info("Calling generate on Create ManangedBean..");

				result = createMBean.generate(m_cpfResource);
				LOG.info("returned generate on Create ManangedBean:..");


				String[] jsppaths = detailsScreen.getJspName().split("/");

				fileName = "CreateMBean_" + p_portlet.getPortletId() + ".java";

				path = Platform.getLocation().toOSString()
				+ ResourcesPlugin.getWorkspace().getRoot().getProject(
						jsppaths[1]).getFolder("src" + "/com/genband/m5/maps/mbeans").getFile(fileName)
						.getFullPath().toOSString();
				try {
					file = new FileWriter (path);
					file.flush();
					file.write(result);
					LOG.info("Managed Bean has been created..");

				} catch (IOException e) {
					LOG.error("Got exception in file write", e);
				} finally {
					if(file != null) {
						file.close();
						file = null;
					}
				}
			}finally {
					//Deleting added actionssupported.....
				if(list) {
					List<CPFConstants.OperationType> actionsSupported = detailsScreen.getActionsSupported();
					if(actionsSupported != null) {
						 detailsScreen.setActionsSupported(null);
					}
				}
				LOG.info("The totoal number of attributes left after code gen is : " + detailsScreen.getSelectedAttributes().size());
			}
		}
		//End of generating Managed Bean for Details Page........
			
		//Creating Managed Bean(s) and Xhtml(s) of List page for provisioning framework
		if(listScreen != null) {
			
			boolean priamryadded = false;
			try {
				// Adding basic primary Key to the selectedAttributes list....
				List<CPFAttribute> selectedAttributes = listScreen.getSelectedAttributes();
				CPFAttribute cpfAttribute = new CPFAttribute (modelUtil.getPrimaryKey(listScreen.getBaseEntity()));
				selectedAttributes.add(0, cpfAttribute);
				priamryadded = true;
				//End of Adding primayKey to the list
				
				if(listScreen.getActionsSupported().contains(OperationType.CREATE)) {
					LOG.info("Create is supported....");
				}
				
				if(listScreen.getActionsSupported().contains(CPFConstants.OperationType.SEARCH)) {
					LOG.info("Search is supported");
				}
				
				if(listScreen.getActionsSupported().contains(CPFConstants.OperationType.DELETE)) {
					LOG.info("Delete is supported");
				}
				
				m_cpfResource = new CPFResource(listScreen);
				m_cpfResource.setOperationId(0);		//Indicates that generating screen for Base entity
				
				//Generating Managed Bean and Xhtml for Base entity....
				m_resourceInfo = new ResourceInfo ();
				m_resourceInfo.setResourceName(p_portlet.getBaseEntity().getName());
				m_cpfResource.setResourceInfo(m_resourceInfo);

				ListMBean create = new ListMBean();
				LOG.info("Calling generate on List ManangedBean");
				result = create.generate(m_cpfResource);
				LOG.info("returned generate on List ManangedBean: ");

				String[] jsppaths = listScreen.getJspName().split("/");
				LOG.info("Project name is: " + jsppaths[1] + "Platform location is.."
						+ Platform.getLocation().toOSString());

				String fileName = "ListMBean_" + p_portlet.getPortletId() + ".java";

				path = Platform.getLocation().toOSString()
				+ ResourcesPlugin.getWorkspace().getRoot().getProject(
						jsppaths[1]).getFolder("src" + "/com/genband/m5/maps/mbeans").getFile(fileName)
						.getFullPath().toOSString();

				LOG.info("Path: " + path);

				try {
					file = new FileWriter (path);
					file.flush();
					file.write(result);
				} catch (IOException e) {
					LOG.error("Got exception in file write", e);
				} finally {
					if (file != null) {
						file.close();
						file = null;
					}
				}


				//Generating XHtml For Base Entity....

				m_cpfResource = new CPFResource(listScreen);

				m_resourceInfo = new ResourceInfo ();
				m_resourceInfo.setResourceName(p_portlet.getBaseEntity().getName());
				m_cpfResource.setResourceInfo(m_resourceInfo);

				ListXhtml createListXhtml = new ListXhtml();
				LOG.info("Calling generate on List Xhtml");
				result = createListXhtml.generate(m_cpfResource);
				LOG.info("returned generate on List Xhtml: ");

				// Removing added extra CPFattribue which is primary Key
				selectedAttributes = listScreen.getSelectedAttributes();
				selectedAttributes.remove(0);
				priamryadded = false;

				fileName = listScreen.getJspName() + ".xhtml";

				path = Platform.getLocation().toOSString()
				+ ResourcesPlugin.getWorkspace().getRoot().getFile(new Path(fileName))
				.getFullPath().toOSString();

				LOG.info("Path: " + path);

				try {
					file = new FileWriter (path);
					file.flush();
					file.write(result);
				} catch (IOException e) {
					LOG.error("Got exception in file write", e);
				} finally {
					if(file != null) {
						file.close();
						file = null;
					}
				}
			} finally {
				if (priamryadded) {
					listScreen.getSelectedAttributes().remove(0);
				}
			}
		}
		//End of Creating Java Bean of Web Service for Base Entity	
		
		//Generating Managed Beans and Xhtmls for One2Many relations 
		if(listScreen != null && 
			listScreen.getNestedJspNames() != null) {
			
			Set<RelationKey> itrNested = (Set<RelationKey>)listScreen.getNestedJspNames().keySet();
			//Set<RelationKey> itrNested = (Set<RelationKey>)listScreen.getNestedAttributes().keySet();
			Iterator<RelationKey> ite = itrNested.iterator();

			int i = 1;	
			List<CPFConstants.OperationType> op = new ArrayList<OperationType>();

			/*
			 * with out List there is no screen at all but still checking condition which is not necessary.. 
			 */
			if(listScreen.getActionsSupported().contains(OperationType.LIST)) {
				op.add(OperationType.LIST);
			}	
			if(listScreen.getActionsSupported().contains(OperationType.SEARCH)) {
				op.add(OperationType.SEARCH);
			}
			if(listScreen.getActionsSupported().contains(OperationType.SORT)) {
				op.add(OperationType.SORT);
			}

			while(ite.hasNext()) {
				RelationKey relationKey = ite.next();
				LOG.info("inside JSF : " + relationKey.getReferencedEntity().getName());
				boolean nestedprimary = false;	//Default not added
				CPFScreen nestedCpfScreen = listScreen.getNestedJspNames().get(relationKey); 
				LOG.info("JCG Attb Vis counter nested relation " + nestedCpfScreen.getJspName());
				
				if (nestedCpfScreen == null) {
					continue; //TODO; wizard sending null nestedJSPPAge
				}
				try{
					nestedCpfScreen.setPortletRef(listScreen.getPortletRef());
					ModelEntity m = relationKey.getReferencedEntity();
					nestedCpfScreen.setBaseEntity(m);

					nestedCpfScreen.setActionsSupported(op);
					nestedCpfScreen.setPreference(listScreen.getPreference());
					nestedCpfScreen.setMappedRoles(listScreen.getMappedRoles());
					nestedCpfScreen.setViewType(CPFConstants.ViewType.LIST);

					m_cpfResource = new CPFResource(nestedCpfScreen);
					m_cpfResource.setOperationId(i);	//Indicates that the screen is generating for related entity
					//Generating Managed Bean and Xhtml for Base entity....
					m_resourceInfo = new ResourceInfo ();
					m_resourceInfo.setResourceName(m.getName());	//Setting resource name for which we are geenrating screen and managed bean
					m_cpfResource.setClassName(relationKey.getRelationShipInfo().getPropertyName());
					m_cpfResource.setResourceInfo(m_resourceInfo);  

					//Adding primary Key to the list...
					List<CPFAttribute> selectedAttributes = nestedCpfScreen.getSelectedAttributes();
					CPFAttribute pkcpfAttribute = new CPFAttribute (modelUtil.getPrimaryKey(nestedCpfScreen.getBaseEntity()));
					selectedAttributes.add(0, pkcpfAttribute);
					nestedprimary = true;
					//End of Adding primayKey to the list

					ListMBean create = new ListMBean();
					LOG.info("Calling nested generate on List ManangedBean");
					result = create.generate(m_cpfResource);
					LOG.info("returned generate on nested List ManangedBean: ");

					String[] jsppaths = nestedCpfScreen.getJspName().split("/");
					LOG.info("Project name is: " + jsppaths[1] + "Platform location is.."
							+ Platform.getLocation().toOSString());

					String fileName = "ListMBean" + p_portlet.getPortletId() + "_nested" + relationKey.getRelationShipInfo().getPropertyName() + ".java";

					path = Platform.getLocation().toOSString()
					+ ResourcesPlugin.getWorkspace().getRoot().getProject(
							jsppaths[1]).getFolder("src" + "/com/genband/m5/maps/mbeans").getFile(fileName)
							.getFullPath().toOSString();
					LOG.info("Path: " + path);

					try {
						file = new FileWriter (path);
						file.flush();
						file.write(result);
					} catch (IOException e) {
						LOG.error("Got exception in file write", e);
					} finally {
						if(file != null) {
							file.close();
							file = null;
						}
					}


					//Generating XHtml....

					m_cpfResource = new CPFResource(nestedCpfScreen);

					m_resourceInfo = new ResourceInfo ();
					m_resourceInfo.setResourceName(nestedCpfScreen.getBaseEntity().getName());
					m_cpfResource.setResourceInfo(m_resourceInfo);
					m_cpfResource.setClassName(relationKey.getRelationShipInfo().getPropertyName());
					m_cpfResource.setOperationId(i);

					ListXhtml createListXhtml = new ListXhtml();
					LOG.info("Calling generate on List Xhtml");
					result = createListXhtml.generate(m_cpfResource);
					LOG.info("returned generate on List Xhtml: ");

					/*
					 * Removing added primary key from the list
					 */
					selectedAttributes = nestedCpfScreen.getSelectedAttributes();
					selectedAttributes.remove(0);
					nestedprimary = false;

					fileName = nestedCpfScreen.getJspName() + ".xhtml";

					path = Platform.getLocation().toOSString()
					+ ResourcesPlugin.getWorkspace().getRoot().getFile(new Path(fileName))
					.getFullPath().toOSString();

					LOG.info("Path: " + path);

					try {
						file = new FileWriter (path);
						file.flush();
						file.write(result);
					} catch (IOException e) {
						LOG.error("Got exception in file write", e);
					} finally {
						if(file != null) {
							file.close();
							file = null;
						}
					}
					i++;
				} finally {
					if(nestedprimary) {
						nestedCpfScreen.getSelectedAttributes().remove(0);
					}
					nestedCpfScreen.setPortletRef(null);
					nestedCpfScreen.setActionsSupported(null);
					nestedCpfScreen.setPreference(null);
					nestedCpfScreen.setMappedRoles(null);
				}
			}
		}
		//End of generating Managed Beans and Xhtmls for One2Many relations
		return null;
	}

	@Override
	public void generateResource(CPFScreen screenCapture, CPFListener listener,
			Object handback) throws Exception {
		LOG.info("generateResource called with listener: " + screenCapture);

	}


	@Override
	public  void createPackage(IFolder src ) {
		IFolder com = src.getFolder("com");

		try {
			if(!com.exists())
				com.create(true, true, null);
		} catch (CoreException e) {
			LOG.info("Got exception while creating package structure.." + e);
		}

		//Creating genband folder...
		IFolder genband = com.getFolder("genband");
		try {   
			if(!genband.exists())
				genband.create(true, true, null);
		} catch (CoreException e) {
			LOG.info("Got exception while creating package structure.." + e);
		}       

		//Creating m5 folder..
		IFolder m5 = genband.getFolder("m5");
		try {
			if(!m5.exists())
				m5.create(true, true, null);
		} catch (CoreException e) {
			LOG.info("Got exception while creating package structure.." + e);
		}               
		//Creating maps folder...
		IFolder maps = m5.getFolder("maps");
		try {
			if(!maps.exists())
				maps.create(true, true, null);
		} catch (CoreException e) {
			LOG.info("Got exception while creating package structure.." + e);
		}

		//Creating mbeans Folder....
		IFolder mbeans = maps.getFolder("mbeans");
		try {
			if(!mbeans.exists())
				mbeans.create(true, true, null);
		} catch (CoreException e) {
			LOG.info("Got exception while creating package structure.." + e);
		}
	}
}



