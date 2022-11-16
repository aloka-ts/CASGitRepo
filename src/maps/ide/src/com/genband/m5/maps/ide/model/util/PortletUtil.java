package com.genband.m5.maps.ide.model.util;

import java.io.IOException;
import java.util.List;


import org.eclipse.core.resources.IFile;

import com.genband.m5.maps.ide.model.CPFScreen;

public class PortletUtil {

	/**
	 * Saves input data for screen as serialized object.
	 * @param input screen input data
	 * @param location where data is saved
	 * @throws IOException
	 */
	public void saveScreenMetaInfo (CPFScreen input, IFile location) throws IOException {
		//TODO
	}
	
	public CPFScreen loadResourceByName (IFile serializedData) throws IOException {
		return null; //TODO
	}
	
	/**
	 * The screen data is saved as project properties including type of resource
	 * such as portlet, web service
	 * @return list of portlet screens for the current project.
	 */
	public List<String> getPortletsList () {
		return null; //TODO
	}
	
	public List<CPFScreen> loadResources (IFile folderLocation) throws IOException {
		return null; //TODO
	}
}
