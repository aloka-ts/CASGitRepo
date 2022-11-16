package com.baypackets.ase.startup;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Collection;

public class Bootstrap {
	
	public static final String ASE_HOME = System.getProperty("ase.home");

	public static void main(String[] args) throws Exception {
		Class clazz = Class.forName("com.baypackets.ase.startup.AseMain", true, getClassLoader());
		clazz.getMethod("main", new Class[] {String[].class}).invoke(null, new Object[] {args});
	}

	private static ClassLoader getClassLoader() throws Exception {
		return new AseClassLoader(getJars(), Bootstrap.class.getClassLoader());
	}

	private static URL[] getJars() throws Exception {
		File[] jarDirs = {new File(ASE_HOME, "otherjars"),
			new File(ASE_HOME, "bpjars"), 
			new File(ASE_HOME, "dsjars")};
			
		Collection jars = new ArrayList();

		for (int i = 0; i < jarDirs.length; i++) {
			File[] files = jarDirs[i].listFiles();

			for (int j = 0; j < files.length; j++) {
				if (files[j].getName().endsWith(".jar")) {
					jars.add(files[j].toURL());
				}
			}
		}
		
		return (URL[])jars.toArray(new URL[jars.size()]);
	}

}
