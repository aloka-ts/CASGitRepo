package com.genband.m5.maps.ide.sitemap.util;

import java.util.List;

public class ProjectUtil {
	
	private static String projectName = null;
	private static List<String> roles;

	public static List<String> getRoles() {
		return roles ;
	}

	public static String getProjectName() {
		return projectName;
	}

	public static void setProjectName(String projectName) {
		ProjectUtil.projectName = projectName;
	}

	public static void setRoles(List<String> r) {
		roles = r;
	}
}
