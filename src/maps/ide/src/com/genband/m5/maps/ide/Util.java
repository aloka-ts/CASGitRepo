package com.genband.m5.maps.ide;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Platform;

public class Util {
public static String getProjectPath(String projectName) {
		
		String platformPath="";
		String projectPath="";
				
		if(null != Platform.getLocation()){
			platformPath = Platform.getLocation().toOSString();
		}
		if(null != ResourcesPlugin.getWorkspace() && ( null != ResourcesPlugin.getWorkspace().getRoot()) 
				&& (null!=ResourcesPlugin.getWorkspace().getRoot().getProject(projectName))){
			projectPath = ResourcesPlugin.getWorkspace().getRoot().getProject(projectName).getFullPath().toOSString();
		}
		CPFPlugin.getDefault().info("getProjectPath() : Project Path : "+platformPath+""+projectPath);
		CPFPlugin.getDefault().info("getProjectPath() : Exiting getProjectPath()");
		return platformPath.concat(projectPath);
				
	}
}
