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
*     Package:  com.genband.m5.maps.ide.builder
*
*     File:    CPFPortletCSVWriter.java
*
*     Desc:   	To create palette.
*
*   Author 		Reeta Aggarwal
*    Date									Description
*    ---------------------------------------------------------
*	  Genband        December 28, 2007   Initial Creation
*
**********************************************************************
**/
package com.genband.m5.maps.ide.builder;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;

import com.genband.m5.maps.common.CPFConstants;
import com.genband.m5.maps.common.CPFConstants.RelationshipType;
import com.genband.m5.maps.ide.CPFPlugin;
import com.genband.m5.maps.ide.model.CPFAttribute;
import com.genband.m5.maps.ide.model.CPFPortlet;
import com.genband.m5.maps.ide.model.CPFScreen;
import com.genband.m5.maps.ide.model.ModelAttribute;
import com.genband.m5.maps.ide.model.RelationKey;
import com.genband.m5.maps.ide.model.util.CPFPortalObjectPersister;
import com.genband.m5.maps.ide.sitemap.util.Constants;
import com.genband.m5.maps.ide.wizard.ExcelCSVPrinter;

public class CPFPortletCSVWriter {
	
	private String projectName="";
	private BufferedWriter in =null;
	private ExcelCSVPrinter printer = null;
	
	
	public  void  updatePortalSecurity(String projectName ) {
		
	try{
		this.projectName=projectName;
		
		CPFPlugin.getDefault().log("PortletUtil :getPortletInfo for the.........."+projectName);
		
		IFolder folder = getProjectHandle(projectName).getFolder(
				new Path(".resources").append("portal"));
		String path = Platform.getLocation().toOSString()
				+ folder.getFullPath().toOSString();
		
		File portalFolder = new File(path);
		
		if (portalFolder.exists()) {
			
			//getting the handle to the security file
			in = this.getCSVWriter(false);
			if (in != null) {
				printer = new ExcelCSVPrinter(in);
			} else {
				CPFPlugin.getDefault().error(
				"Could not create CSVWriter...");
				return;
			}
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
							updateSecurityCSVFile(portlet) ;
					}
				}

			}
		}
	 }
	} finally {
			if (printer != null) {
				try {
					printer.close();
				} catch (IOException e) {
					CPFPlugin.getDefault().error(
							"IOException thrown while closing CSV writer");
				}
			}

		}
	}
	
	public void  updatePortalSecurity(CPFPortlet portal ,String ProjectName){
		
		this.projectName=ProjectName;
		in = this.getCSVWriter(true);
		if (in != null) {
			printer = new ExcelCSVPrinter(in);
		} else {
			CPFPlugin.getDefault().error(
			"Could not create CSVWriter...");
			return;
		}
		
		this.updateSecurityCSVFile(portal);
		
	}
		public static IProject getProjectHandle(String projectName) {
		if (projectName != null) {
			IProject proj = ResourcesPlugin.getWorkspace().getRoot().getProject(
					projectName);
			return proj;
		}
		else
			return null;
	}
	
	
	 private void updateSecurityCSVFile(CPFPortlet portal) {
			try {
				CPFPlugin
				.getDefault().log("Update Security CSV file.......");
				
				    int portletId=portal.getPortletId();
		
					
					CPFScreen listScreen=portal.getListScreen();  //equal to cpfScreen
					CPFScreen detailsScreen=portal.getDetailsScreen();  //equal to cpfScreen
					
					if(listScreen!=null){
					this.updateSecurityForList(portletId,listScreen, printer);
					}
					
					if(detailsScreen!=null){
					this.updateCreateModifyViewSecurityData(portletId,detailsScreen, printer);
					}
			} catch (Exception e) {
						CPFPlugin.getDefault().error(
								"Exception thrown by updateSecurityCSVFile()");
			}
				

		}
		  
	 

	 private void updateSecurityForList(int portletId,CPFScreen listScreen,ExcelCSVPrinter printer){
		   
		    Integer opId[] = listScreen.getOperationIdPool();
		    String portletID = ""+portletId;
		    String operationID =""+opId[0];
		    
		    java.util.List<CPFAttribute> baseEntityAtt= listScreen.getSelectedAttributes();
		    Map<RelationKey ,java.util.List<CPFAttribute>> otherEntityAtt=listScreen.getNestedAttributes();
		 CPFPlugin
			.getDefault().log("Update Security CSV file for List/Delete......");
		 
			String baseEntityName = listScreen.getBaseEntity().getName();
		   
			Map<CPFConstants.OperationType ,List<String>> allRoles=listScreen.getMappedRoles();
			
			java.util.List<String> listRoles=allRoles.get(CPFConstants.OperationType.LIST);
			java.util.List<String> deleteRoles=allRoles.get(CPFConstants.OperationType.DELETE);
			StringBuilder listfields = new StringBuilder (1024);

			for (int i = 0; i < baseEntityAtt.size(); i++) {
				ModelAttribute mA = baseEntityAtt.get(i).getModelAttrib();
			
					if (mA == null) // will not happen in case of list as no group bar is there
						continue; //go to next attrib

										
					if (i != (baseEntityAtt.size() - 1))
						listfields = listfields.append (baseEntityName).append (".").append (
								mA.getName()).append (
								",");
					else
						listfields = listfields.append (baseEntityName).append (".").append (
								mA.getName());
					
			}
			CPFPlugin.getDefault().log("The Listfiled lenth is,......."+listfields.length() +"listfields is.. "+listfields.toString());
			
//			if (listfields.length () > 0 && listfields.charAt (listfields.length()) == ',') //remove dangling comma
//				listfields.deleteCharAt (listfields.length ());
//				
				Set<RelationKey> rK=otherEntityAtt.keySet();
				Iterator<RelationKey> rkIter=rK.iterator();
				
				while(rkIter.hasNext()){
					CPFPlugin.getDefault().log("Entering for nested Attribtues..");
					
					RelationKey rk=	rkIter.next();
					java.util.List<CPFAttribute>  cpfAttList=otherEntityAtt.get(rk);
					String propertyName =rk.getRelationShipInfo().getPropertyName();
					
					if(rk.getRelationShipInfo().getMapping().equals(RelationshipType.OneToOne)
							||rk.getRelationShipInfo().getMapping().equals(RelationshipType.ManyToOne)){
						
						listfields.append( ",");
						
						for(int j=0;j<cpfAttList.size();j++){
							ModelAttribute mA=cpfAttList.get(j).getModelAttrib();
							
							if (mA == null) //for group bar
								continue; //go to next attrib
							
							
							if (j != (cpfAttList.size() - 1))
								listfields.append (propertyName).append (".").append (
										mA.getName()).append (
										",");
							else
								listfields.append (propertyName).append (".").append (
										mA.getName());
							
						}

//						if (listfields.length () > 0 && listfields.charAt (listfields.length()) == ',') //remove dangling comma
//							listfields.deleteCharAt (listfields.length ());
				
						
					} else if(rk.getRelationShipInfo().getMapping().equals(RelationshipType.OneToMany)
							||rk.getRelationShipInfo().getMapping().equals(RelationshipType.ManyToMany)){
						
						CPFPlugin.getDefault().log("Entering for Many Nested Attribtues..");
						
						listScreen.getNestedJspNames().get(rk).setActionsSupported(listScreen.getActionsSupported());
						listScreen.getNestedJspNames().get(rk).setMappedRoles(listScreen.getMappedRoles());

                    	StringBuilder fields = new StringBuilder (1024);
                    	CPFPlugin.getDefault().log("nested screen is : " + listScreen.getNestedJspNames().get(rk));
                     	CPFPlugin.getDefault().log("nested screen is : " + listScreen.getNestedJspNames().get(rk).getOperationRoleMap(CPFConstants.OperationType.LIST));
                    	Map<Integer ,String[]> allRoles1 = listScreen.getNestedJspNames().get(rk)
							.getOperationRoleMap(CPFConstants.OperationType.LIST);

						String otherEntityName=null;
						CPFPlugin.getDefault().log("N-ary relation with base entity..Att List size is"+cpfAttList.size());

						for(int j=0;j<cpfAttList.size();j++){
							ModelAttribute mA=cpfAttList.get(j).getModelAttrib();

							if (mA == null) //for group bar
								continue; //go to next attrib

							otherEntityName=mA.getEntity().getName();

							if (j != (cpfAttList.size() - 1))
								fields.append (otherEntityName).append (".").append (
										mA.getName()).append (
										",");
							else
								fields.append (otherEntityName).append (".").append (
										mA.getName());
							
						}
						
//						if (fields.length () > 0 && fields.charAt (fields.length()) == ',') //remove dangling comma
//							fields.deleteCharAt (fields.length ());
				
						String fieldsStr = fields.toString ();
						if (allRoles1!=null) {
//							String actionType = '"' + CPFConstants.OperationType.LIST
//									.name() + '"';

							String actionType = CPFConstants.OperationType.LIST
							.name();
							Iterator<Integer> temp = allRoles1.keySet().iterator();
							for (;temp.hasNext();) {
								Integer x = temp.next();
								String[] roleNames =  allRoles1.get(x);
								for(int j=0; j<roleNames.length; j++) {
								String[] csvData = new String[] { portletID,
										x.toString(), roleNames[j], otherEntityName,
										actionType, fieldsStr };
								CPFPlugin.getDefault().log("Writing to CSV for n-ary in List.."+csvData);
								try {
									//Not writing data for nested since it shares the same operation id...
									printer.writeln(csvData);
									CPFPlugin.getDefault().log("Would not write CSV for n-ary in List..");
								} catch (IOException e) {
									// TODO Auto-generated catch block
									CPFPlugin.getDefault().error("IOException Writing to CSV file "+e);
								}
								}
							}
						} 
						
						
					}
				}
			String listfieldsStr =listfields.toString();
			
			CPFPlugin.getDefault().log("out of Nested Attribtues..");
			
			if (listRoles!=null) {
				String actionType = CPFConstants.OperationType.LIST
						.name();

				for (int i = 0; i < listRoles.size(); i++) {
					String roleName =listRoles.get(i);
					String[] csvData = new String[] { portletID,
							operationID, roleName, baseEntityName,
							actionType, listfieldsStr };
					CPFPlugin.getDefault().log("Writing to CSV for list.."+csvData +" Fields are.."+listfields);
					try {
						printer.writeln(csvData);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						CPFPlugin.getDefault().error("IOException Writing to CSV file" +e);
					}
				}
			} 
			
			if (deleteRoles!=null) {
				String actionType =CPFConstants.OperationType.DELETE
						.name();

				for (int i = 0; i < deleteRoles.size(); i++) {
					String roleName =deleteRoles.get(i);
					String[] csvData = new String[] { portletID,
							operationID, roleName, baseEntityName,
							actionType, "" };
					CPFPlugin.getDefault().log("Writing to CSV for delete.."+csvData );
					try {
						printer.writeln(csvData);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						CPFPlugin.getDefault().error("IOException Writing to CSV file" +e);
					}

				}
			}
		  
	  }
	 
	 
	private void  updateCreateModifyViewSecurityData(int portletId,CPFScreen detailsScreen,ExcelCSVPrinter printer){
		
		   Integer opId[] = detailsScreen.getOperationIdPool();
		 CPFPlugin
			.getDefault().log("Update Security CSV file for create/modify/view......");
		    String portletID = ""+portletId;
			String operationID =""+opId[0];
			
			String baseEntityName = detailsScreen.getBaseEntity().getName();
			
			java.util.List<CPFAttribute> baseEntityAtt = detailsScreen.getSelectedAttributes();
			Map<RelationKey ,java.util.List<CPFAttribute>> otherEntityAtt = detailsScreen.getNestedAttributes();
			
			Map<CPFConstants.OperationType ,List<String>> allRoles=detailsScreen.getMappedRoles();
		    
			java.util.List<String> createRoles = allRoles.get(CPFConstants.OperationType.CREATE);
			java.util.List<String> modifyRoles = allRoles.get(CPFConstants.OperationType.MODIFY);
			java.util.List<String> viewRoles = allRoles.get(CPFConstants.OperationType.VIEW);
			
			if (createRoles!=null) {
				String actionType =CPFConstants.OperationType.CREATE
						.name() ;

				for (int i = 0; i < createRoles.size(); i++) {
					String roleName =createRoles.get(i);
					String[] csvData = new String[] { portletID,
							operationID, roleName, baseEntityName,
							actionType, "" };
					CPFPlugin.getDefault().log("Writing to CSV for create.."+csvData);
					try {
						printer.writeln(csvData);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						CPFPlugin.getDefault().error("IOException Writing to CSV file");
					}

				}
			} if (modifyRoles!=null) {
				
				String listfields="";
				String actionType =CPFConstants.OperationType.MODIFY
						.name();
				
				for (int i = 0; i < baseEntityAtt.size(); i++) {
					ModelAttribute mA=baseEntityAtt.get(i).getModelAttrib();
					
					if (mA == null) //for group bar
						continue; //go to next attrib

					if(!mA.isFK()){
						if (i != (baseEntityAtt.size() - 1))
							listfields = listfields + mA.getName()+ ",";
						else
							listfields = listfields + mA.getName();
					}else{
						
					if(mA.getRelType().getMapping().equals(RelationshipType.OneToOne)||
							mA.getRelType().getMapping().equals(RelationshipType.ManyToOne)){
						if (i != (baseEntityAtt.size() - 1))
							listfields = listfields + mA.getName()+ ",";
						else
							listfields = listfields + mA.getName();
					 }else if(mA.getRelType().getMapping().equals(RelationshipType.OneToMany)||
								mA.getRelType().getMapping().equals(RelationshipType.ManyToMany)){
						 if (i != (baseEntityAtt.size() - 1))
								listfields = listfields + mA.getName()+"[n]"+",";
							else
								listfields = listfields + mA.getName()+"[n]"; 
						 
					 }
					}
				}
						
				Set<RelationKey> rK=otherEntityAtt.keySet();
				Iterator<RelationKey> rkIter=rK.iterator();
				
				while(rkIter.hasNext()){
					
					RelationKey rk=	rkIter.next();
					java.util.List<CPFAttribute>  cpfAttList=otherEntityAtt.get(rk);
					String propertyName =rk.getRelationShipInfo().getPropertyName();
					
					if(rk.getRelationShipInfo().getMapping().equals(RelationshipType.OneToOne)
							||rk.getRelationShipInfo().getMapping().equals(RelationshipType.ManyToOne)){
						
						listfields=listfields+",";
						
						for(int j=0;j<cpfAttList.size();j++){
							ModelAttribute mA=cpfAttList.get(j).getModelAttrib();
							
							if (mA == null) //for group bar
								continue; //go to next attrib

							if (j != (cpfAttList.size() - 1))
								listfields = listfields + propertyName + "."
										+ mA.getName()
										+ ",";
							else
								listfields = listfields + propertyName + "."
										+ mA.getName();
							
						}
						
					} else if(rk.getRelationShipInfo().getMapping().equals(RelationshipType.OneToMany)
							&&rk.getRelationShipInfo().getMapping().equals(RelationshipType.ManyToMany)){
                  listfields=listfields+",";
						
						for(int j=0;j<cpfAttList.size();j++){
							ModelAttribute mA=cpfAttList.get(j).getModelAttrib();
							
							if (mA == null) //for group bar
								continue; //go to next attrib

							if (j != (cpfAttList.size() - 1))
								listfields = listfields + propertyName+"[n]" + "."
										+ mA.getName()
										+ ",";
							else
								listfields = listfields + propertyName +"[n]"+ "."
										+ mA.getName();
							
						}
						
						
					}
				}
				listfields =listfields;
				for (int i = 0; i < modifyRoles.size(); i++) {
					String roleName = modifyRoles.get(i);
					String[] csvData = new String[] { portletID,
							operationID, roleName, baseEntityName,
							actionType, listfields };
					CPFPlugin.getDefault().log("Writing to CSV for modify.."+csvData+" Fields are.."+listfields);
					try {
						printer.writeln(csvData);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						CPFPlugin.getDefault().error("IOException Writing to CSV file" +e);
					}

				}
		 } if (viewRoles!=null) {
			 
			 String listfields="";
				String actionType = CPFConstants.OperationType.VIEW
						.name();
				
				for (int i = 0; i < baseEntityAtt.size(); i++) {
					ModelAttribute mA=baseEntityAtt.get(i).getModelAttrib();
						
					if (mA == null) //for group bar
						continue; //go to next attrib

					if(!mA.isFK()){
						if (i != (baseEntityAtt.size() - 1))
							listfields = listfields + mA.getName()+ ",";
						else
							listfields = listfields + mA.getName();
					}else{
						
					if(mA.getRelType().getMapping().equals(RelationshipType.OneToOne)||
							mA.getRelType().getMapping().equals(RelationshipType.ManyToOne)){
						if (i != (baseEntityAtt.size() - 1))
							listfields = listfields + mA.getName()+"."+baseEntityAtt.get(i).getForeignColumn().getName()+ ",";
						else
							listfields = listfields + mA.getName()+"."+baseEntityAtt.get(i).getForeignColumn().getName();
					 }else{
						 if (i != (baseEntityAtt.size() - 1))
								listfields = listfields + mA.getName()+"[n]."+baseEntityAtt.get(i).getForeignColumn().getName()+ ",";
							else
								listfields = listfields + mA.getName()+"[n]."+baseEntityAtt.get(i).getForeignColumn().getName(); 
						 
					 }
					}
				}
				
					Set<RelationKey> rK=otherEntityAtt.keySet();
					Iterator<RelationKey> rkIter=rK.iterator();
					
					while(rkIter.hasNext()){
						
						RelationKey rk=	rkIter.next();
						java.util.List<CPFAttribute>  cpfAttList=otherEntityAtt.get(rk);
						String propertyName =rk.getRelationShipInfo().getPropertyName();
						
						if(rk.getRelationShipInfo().getMapping().equals(RelationshipType.OneToOne)
								||rk.getRelationShipInfo().getMapping().equals(RelationshipType.ManyToOne)){
							
							listfields=listfields+",";
							
							for(int j=0;j<cpfAttList.size();j++){
								ModelAttribute mA=cpfAttList.get(j).getModelAttrib();
								
							if (mA == null) //for group bar
								continue; //go to next attrib

						   if(!mA.isFK()){
								if (j != (cpfAttList.size() - 1))
									listfields = listfields + propertyName + "."
											+ mA.getName()
											+ ",";
								else
									listfields = listfields + propertyName + "."
											+ mA.getName();
								
						   }else{
							   if (j != (cpfAttList.size() - 1))
									listfields = listfields + propertyName + "."
											+ mA.getName()+ "."+cpfAttList.get(j).getForeignColumn().getName()
											+ ",";
								else
									listfields = listfields + propertyName + "."
											+ mA.getName()+"."+cpfAttList.get(j).getForeignColumn().getName();
						   }
							}
							
						} else if(rk.getRelationShipInfo().getMapping().equals(RelationshipType.OneToMany)
								&&rk.getRelationShipInfo().getMapping().equals(RelationshipType.ManyToMany)){
                      listfields=listfields+",";
							
							for(int j=0;j<cpfAttList.size();j++){
								ModelAttribute mA=cpfAttList.get(j).getModelAttrib();
							
							if (mA == null) //for group bar
								continue; //go to next attrib

							if(!mA.isFK()){
								if (j != (cpfAttList.size() - 1))
									listfields = listfields + propertyName+"[n]" + "."
											+ mA.getName()
											+ ",";
								else
									listfields = listfields + propertyName +"[n]"+ "."
											+ mA.getName();
							}else{
								if (j != (cpfAttList.size() - 1))
									listfields = listfields + propertyName+"[n]" + "."
											+ mA.getName()+"."+cpfAttList.get(j).getForeignColumn().getName()
											+ ",";
								else
									listfields = listfields + propertyName +"[n]"+ "."
											+ mA.getName()+"."+cpfAttList.get(j).getForeignColumn().getName();
							}
								
							}
							
							
						}
					}
					listfields =listfields;
				for (int i = 0; i < viewRoles.size(); i++) {
					String roleName =viewRoles.get(i);
					String[] csvData = new String[] { portletID,
							operationID, roleName, baseEntityName,
							actionType, listfields };
					CPFPlugin.getDefault().log("Writing to CSV for view.."+csvData +" Fields are.."+listfields);
					try {
						printer.writeln(csvData);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						CPFPlugin.getDefault().error("IOException Writing to CSV file" +e);
					}

				}

		}
	 }
		
	//if only WS then donot delete the file
		  private  BufferedWriter  getCSVWriter(boolean isWS){
			  BufferedWriter in=null;
			  String path="";
		  try{    

			  
			   String platformPath = Platform.getLocation().toOSString();
			   
			 //Adding Security data
				IFolder securityFolder = getProjectHandle(this.projectName).getFolder(new Path(".resources").append("security"));

	                if (!securityFolder.exists()) {
	                	try {
							securityFolder.create(true, true, null);
						} catch (CoreException e) {
							// TODO Auto-generated catch block
						}
	                   }
	                
	               
	                path = CPFPlugin.fullPath(this.SECURITY_CSV_RESOURCE);
	                InputStream stream = new FileInputStream( path);
	                if(securityFolder.exists()){
	                	     IFile securityCSV = securityFolder.getFile(this.SECURITY_CSV); 
	                	 	try {
	                	     
	                	 	 if(securityCSV.exists()){
	                	    	 
	                	    	 if(!isWS){
	                	    		CPFPlugin.getDefault().log("Existing security.csv so deleting ....");
	                	    	    securityCSV.delete(true, null);
	                	    	    CPFPlugin.getDefault().log("Creatubg CSV file..." + path.toString());
	  	    	         		    securityCSV.create(stream, true, null);
	                	    	 }
	                	     } else{
	                	    	 securityCSV.create(stream, true, null);
	                	     }
	                		
	                		
							} catch (CoreException e) {
								// TODO Auto-generated catch block
							}
	                	
							
	                }
	            
				IFile secFile = securityFolder.getFile("security.csv");
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
		  private static final String SECURITY_CSV_RESOURCE="resources/security.csv";
			private static final String SECURITY_CSV="security.csv"; 
}
