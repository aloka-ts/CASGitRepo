package com.genband.m5.maps.ide.model;

import java.io.Serializable;
import java.util.Map;

import org.eclipse.core.resources.IProject;

import com.genband.m5.maps.common.CPFConstants;

public class CPFPortlet implements Serializable {

	private static final long serialVersionUID = 1931387804298544357L;
	
	private int portletId;

	private ModelEntity baseEntity;

	private CPFScreen listScreen;

	private CPFScreen detailsScreen;
	
	private String currentProject;

	public ModelEntity getBaseEntity() {
		return baseEntity;
	}

	public void setBaseEntity(ModelEntity baseEntity) {
		this.baseEntity = baseEntity;
	}

	public int getPortletId() {
		return portletId;
	}

	public void setPortletId(int portletId) {
		this.portletId = portletId;
	}

	public CPFScreen getListScreen() {

		return listScreen;

	}

	public void setListScreen(CPFScreen listScreen, boolean force)
			throws Exception {

		if (force) {
			//CPFPlugin.getDefault().log("Inside CPFPortlet setListScreen() DetailsPage is.."+this.detailsScreen+"Base Entity is... "+this.baseEntity);
			//CPFPlugin.getDefault().log("Inside CPFPortlet setListScreen() ListPage is.."+listScreen+"Base Entity is... "+listScreen.getBaseEntity());
			if (this.detailsScreen == null && this.baseEntity == null) {
				this.listScreen = listScreen;
				//CPFPlugin.getDefault().log("Inside CPFPortlet The List screen set is.."+listScreen);
				this.baseEntity = listScreen.getBaseEntity();
			}else if (this.detailsScreen != null&&this.baseEntity!=null
					&& listScreen.getBaseEntity().getName().equals(this.baseEntity.getName())) {
				//CPFPlugin.getDefault().log("Inside CPFPortlet Setting ListScreen for this Details Page"+listScreen);
				this.listScreen = listScreen;
			} else if (detailsScreen != null && baseEntity == null) {
				throw new Exception(
						"CPFPortlet BaseEntity of the details page is null");
			} else if (!listScreen.getInterfaceType().contains(
					CPFConstants.InterfaceType.PORTLET))
             throw new Exception("CPFPortlet references portal screens only");
		}

		else {

			throw new Exception("this Operation is not allowed");

		}

	}

	public CPFScreen getDetailsScreen() {

		return detailsScreen;

	}

	public void setDetailsScreen(CPFScreen detailsScreen, boolean force)
			throws Exception {

		if (force) {
			//CPFPlugin.getDefault().log("Inside CPFPortlet setDetailsScreen() listPage is.."+this.listScreen+"Base Entity is... "+this.baseEntity);
			//CPFPlugin.getDefault().log("Inside CPFPortlet setDetailsScreen() DetailsPage is.."+detailsScreen+"Base Entity is... "+detailsScreen.getBaseEntity());
			if (this.listScreen == null && this.baseEntity == null) {
				this.detailsScreen = detailsScreen;
				this.baseEntity = detailsScreen.getBaseEntity();
				//CPFPlugin.getDefault().log("Inside CPFPortlet The Details Screen set is.."+detailsScreen +"Base Entity is... "+this.detailsScreen.getBaseEntity());
			} else if (this.listScreen != null&&this.baseEntity!=null
					&& detailsScreen.getBaseEntity().getName().equals(this.baseEntity.getName())) {
				this.detailsScreen = detailsScreen;
				//CPFPlugin.getDefault().log("Inside CPFPortlet Setting DetailsScreen for this List Page"+detailsScreen);
			} else if (listScreen != null && baseEntity == null) {

				throw new Exception(
						"CPFPortlet BaseEntity of the details page is null");
			} else if (!detailsScreen.getInterfaceType().contains(
					CPFConstants.InterfaceType.PORTLET)) {

				throw new Exception("CPFPortlet references portal screens only");
			}

		}

		else {

			throw new Exception("this Operation is not allowed");

		}

	}
	
	public String getCurrentProject() {
		return currentProject;
	}

	public void setCurrentProject(String currentProject) {
		this.currentProject = currentProject;
	}

	public Map<Integer, String[]> getOperationRoleMap (CPFConstants.OperationType o) {

		//TODO: maintains the map for the whole portlet
    	return null;
    }

}