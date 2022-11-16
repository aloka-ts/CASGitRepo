//***********************************************************************************
//GENBAND, Inc. Confidential and Proprietary
//
//This work contains valuable confidential and proprietary
//information.
//Disclosure, use or reproduction without the written authorization of
//GENBAND, Inc. is prohibited. This unpublished work by GENBAND, Inc.
//is protected by laws of United States and other countries.
//If publication of work should occur the following notice shall
//apply:
//
//"Copyright 2007 GENBAND, Inc. All right reserved."
//***********************************************************************************
                                                                                                                      
                                                                                                                      
//***********************************************************************************
//
//      File:   MemoryOutputJavaFileManager.java
//
//      Desc:   This is a utility file to manage runtime compilation output in memory
//
//      Author                          Date            Description
//      ----------------------------------------------------------------------
//      Rajendra Singh                  19/01/08        Initial Creation
//
//***********************************************************************************



package com.baypackets.ase.soa.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

import javax.tools.FileObject;
import javax.tools.ForwardingJavaFileManager;
import javax.tools.JavaFileManager;
import javax.tools.JavaFileObject;
import javax.tools.StandardLocation;
import javax.tools.JavaFileObject.Kind;

import com.baypackets.ase.util.AseStrings;

/**
 * A java file manager that stores output in memory, delegating all other
 * functions to another file manager.
 * 
 * @author prunge
 */
public class MemoryOutputJavaFileManager 
extends ForwardingJavaFileManager<JavaFileManager>
{
	/**
	 * Maps class names to file objects.
	 */
	public static Map<String, MemoryOutputJavaFileObject> outputMap =
							new HashMap<String, MemoryOutputJavaFileObject>();
	
	private final List<URL> classPathUrls;
	
	/**
	 * Constructs a <code>MemoryOutputJavaFileManager</code>.
	 *
	 * @param fileManager the underlying file manager to use.
	 */
	public MemoryOutputJavaFileManager(JavaFileManager fileManager)
	{
		super(fileManager);
		
		classPathUrls = new ArrayList<URL>();
	}
	
	/**
	 * Adds a URL that classes may be loaded from.  All classes from this
	 * URL will be added to the classpath.
	 * 
	 * @param url the URL to add.
	 * 
	 * @throws NullPointerException if <code>url</code> is null.
	 */
	public void addClassPathUrl(final URL url)
	{
		if (url == null)
			throw new NullPointerException("url == null");
		
		classPathUrls.add(url);
	}
	
	/**
	 * Returns the base URL of the specified class.
	 * <p>
	 * 
	 * For example, if <code>java.lang.String</code> exists at 
	 * http://base.net/parent/java/lang/String.class, the base URL
	 * is http://base.net/parent/.
	 * 
	 * @param clazz the class.
	 * 
	 * @return a base URL where the class is located.
	 * 
	 * @throws IllegalArgumentException if a URL cannot be obtained.
	 */
	public static URL baseUrlOfClass(final Class<?> clazz)
	{		
		try
		{
			String name = clazz.getName();
			URL url = clazz.getResource(AseStrings.SLASH + name.replace(AseStrings.CHAR_DOT,AseStrings.CHAR_SLASH) + ".class");
			int curPos = 0;
			do
			{
				curPos = name.indexOf(AseStrings.CHAR_DOT, curPos + 1);
				if (curPos >= 0)
					url = new URL(url, "..");
			}
			while (curPos >= 0);
			
			return(url);
		}
		catch (final MalformedURLException e)
		{
			throw new IllegalArgumentException("Invalid URL for class " + clazz.getName(), e);
		}
	}

	public JavaFileObject getJavaFileForOutput(Location location,
			 String className, Kind kind, FileObject sibling) 
	throws IOException
	{
		if (kind != Kind.CLASS)
			throw new IOException("Only class output supported, kind=" + kind);
		
		try
		{
			MemoryOutputJavaFileObject output = 
				new MemoryOutputJavaFileObject(new URI(className), kind);
			
			outputMap.put(className, output);
			
			return(output);
		}
		catch (final URISyntaxException e)
		{
			throw new IOException(e);
		}
	}
}
