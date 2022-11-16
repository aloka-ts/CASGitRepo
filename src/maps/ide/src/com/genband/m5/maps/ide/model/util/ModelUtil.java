/**********************************************************************
*	 GENBAND, Inc. Confidential and Proprietary
*
* This work contains valuable confidential and proprietary information.
* Disclosure, use or reproduction without the written authorization of
* GENBAND, Inc. is prohibited.  This unpublished work by GENBAND, Inc.
* is protected by the laws of the United States and other countries.
* If publication of the work should occur the following notice shall 
* apply:
* 
* "Copyright 2007 GENBAND, Inc.  All rights reserved."
************************************************************************/


/**********************************************************************
*
*     Project:  MAPS
*
*     Package:  com.genband.m5.maps.ide.model.util
*
*     File:     ModelUtil.java
*
*     Desc:   	Generic utility class modeled around javax.persistence.Entity
*     and exposes utility methods for wizard based tools to work upon.
*
*     Author 			Date					 Description
*    ---------------------------------------------------------
*	  GENBAND  		Jan 7th, 2008				Initial Creation
*
************************************************************************/

package com.genband.m5.maps.ide.model.util;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import javax.persistence.Entity;

import org.apache.log4j.Logger;

import com.genband.m5.maps.common.CPFConstants.RelationshipType;
import com.genband.m5.maps.ide.CPFPlugin;
import com.genband.m5.maps.ide.model.FormatData;
import com.genband.m5.maps.ide.model.ModelAttribute;
import com.genband.m5.maps.ide.model.ModelEntity;
import com.genband.m5.maps.ide.preferences.PreferenceConstants;

public class ModelUtil {

	private static CPFPlugin LOG = CPFPlugin.getDefault();
	//private static Logger LOG = Logger.getLogger("com.genband.m5.maps.ide.model");
	
	private Map<String, ModelEntity> entityMap;
	int jarCount = 0;	
	ClassLoader parentLoader = null;
	private static int counter;
	private URL[] ejbContentfolderPath=null;
	
	private final static ModelUtil singleton = new ModelUtil ();

	private boolean loaderGbJar = false;
	private static URLClassLoader loader=null;
	
    private File file = new File (CPFPlugin.fullPath("library/gb-common.jar"));
	
	
	protected ModelUtil () {
		
		try {
			LOG.info ("Initializing ModelUtil ...");		
			
			entityMap = new HashMap<String, ModelEntity>();
			
			ClassLoader parentLoader = Thread.currentThread().getContextClassLoader();			
			
			URLClassLoader loader = new URLClassLoader(new URL[] { new URL(
					"file:///" + file.getAbsolutePath()) }, parentLoader);
			
			Thread.currentThread().setContextClassLoader( loader );
			
		} catch (MalformedURLException e) {
			
			LOG.error("Got error initing classloader for ModelUtil", e);
		}
		

	}
	
	public static ModelUtil getInstance() {
	
		return singleton;
	}
	
	
	public void setEJBContentPath(URL[] urlPath){
		CPFPlugin.getDefault().log("ModelUtil.......setEJBContentPath. called");
		ejbContentfolderPath=urlPath;
	}

	/**
	 * 
	 * @return list of entities in the current project that have been processed till now
	 */
	public Map<String, ModelEntity> getEntityMap () {

		if (! loaderGbJar) {
			getListOfEntities(file);
		}
		
		return entityMap;
	}
	
	public List<ModelEntity> getEntityList () {
		
		List<ModelEntity> data = new ArrayList<ModelEntity>();
		
		for (ModelEntity m : entityMap.values()) {
			data.add(m);
		}
		
		return data;
	}
	
	public ModelEntity findEntity (String canonicalName) {
		
		return entityMap.get(canonicalName);
	}
	
	public void addEntity (ModelEntity m) {
		
		entityMap.put(m.getCanonicalTypeName(), m);
		
	}
	
	public ModelEntity createModelEntity (String modelType, URL[] classpath) throws Exception {

		
		Class modelClass;
		try {
			if(this.ejbContentfolderPath!=null){
			 classpath=this.ejbContentfolderPath;
			}
			LOG.info("(String, URL[]): " + modelType + ", " + classpath);
			ClassLoader cl = Thread.currentThread().getContextClassLoader();
			URLClassLoader ucl = new URLClassLoader (classpath, cl); //keep context class loader as parent
			modelClass = ucl.loadClass (modelType);
		
		} catch (Exception e) {
			LOG.error ("Got an exception creating a ModelEntity", e);
			throw e;
		}

		if (null == modelClass.getAnnotation(Entity.class))
			throw new IllegalArgumentException("Expected EJB3 Entity only!!");
		
		return createModelEntity(modelClass.newInstance());
	}	

	public ModelEntity createModelEntity (Object model) throws Exception {

		LOG.info("(Object): " + model);
		if (null == model.getClass().getAnnotation(Entity.class))
			throw new IllegalArgumentException("Expected EJB3 Entity only!!");
		
		ModelEntity m = entityMap.get(model.getClass().getCanonicalName());
		
		if (m == null) {
			LOG.info("Did not find in the map: " + model.getClass().getCanonicalName());
			m = new ModelEntity (model);			
			entityMap.put(m.getCanonicalTypeName(), m);
			LOG.info("Put in the map: " + m.getCanonicalTypeName());
			//update properties here, to make sure if entity seen before are in map
			LOG.info("Is model static: To check for - " + m.getName());
			m.updateIfStatic();
			LOG.info("Is model static: " + m.getName() + "? " + m.isStatic());
			m.updateInverserRelations();
			LOG.info("Updated props that cause recursion ...");
		}

		
		return m;
	}
	
	/**
	 * 
	 * @param dir the directory containing entity definition as jar files (could be in sub-dir)
	 * @return list of entities found null if there was error or nothing found
	 */		
	public List<ModelEntity> getEntities (File dir) {
		LOG.info ("ModelUtil.getEntities() Start .......");
		List<ModelEntity> result = getListOfEntities(dir);
		LOG.info ("Entities jar path is................" + dir.getAbsolutePath());
		
		if (result != null) {
			for (Iterator<ModelEntity> iterator = result.iterator(); iterator.hasNext();) {
				ModelEntity modelEntity = iterator.next();
				LOG.info (modelEntity.getName());
			}
			//this.entityList.addAll(result); //Implicitly, ModelEntity searches in entityMap cache
		}
		
		LOG.info ("ModelUtil.getEntities() End .......");
		return result;
	}
	
	/** 
	 * @param file A jar file containing java class files of EJB3 entity or an enclosing directory
	 * @return a list of ModelEntity for wizard to process
	 */
	private List<ModelEntity> getListOfEntities(File file) {
		LOG.info ("File as input is " + file.getName());
		
		List<ModelEntity> result = new ArrayList<ModelEntity>();
		File[] files = file.listFiles();
		try {
			if (files != null) { //if directory

				LOG. info ("Input file is a directory. Call the method recursively.");
				
				for (File f : files) {
					
					List<ModelEntity> tmp = this.getListOfEntities(f); 
					if (tmp != null)
						result.addAll(tmp);
				}
			} else { //processing file
				
				String fileName = file.getName();
				if (fileName.endsWith(".jar")) {

					LOG.info ("Class loader path is: " + file.getAbsolutePath());
					
					if (jarCount == 0) {
						CPFPlugin.getDefault().log("jar count is " + jarCount);
						ClassLoader plugin_loader = Thread.currentThread()
								.getContextClassLoader();
						parentLoader = plugin_loader;
						jarCount++;
					}

					URLClassLoader loader = new URLClassLoader(
							new URL[] { new URL("file:///"
									+ file.getAbsolutePath()) }, parentLoader);
					parentLoader = loader;
					JarFile jarFile = new JarFile(file);
					Enumeration e = jarFile.entries();
					
					while (e.hasMoreElements()) {
						
						JarEntry f = (JarEntry) e.nextElement();
						String name = f.getName();
						
						if (name.endsWith(".class")) {
							LOG.info ("Class file found " + name);
							String packageName = name.substring(0, name
									.lastIndexOf('/') + 1);
							packageName = packageName.replaceAll("/", ".");
							LOG.info ("packageName is " + packageName);
							String className = name.substring(name
									.lastIndexOf('/') + 1, name
									.indexOf(".class"));
							LOG.info ("className is " + className);
							String completeName = packageName + className;
							Class cls = loader.loadClass(completeName);
							if (cls.getAnnotation(javax.persistence.Entity.class) != null) {
								
								ModelEntity entity = createModelEntity(cls.newInstance());
								result.add(entity);
								LOG.info("Class " + className + " is an EJB3 entity");
							} else {
								LOG.info("Class " + className + " is not an EJB3 entity");
							}
						} else {
							LOG.info("Inner class definition or non-java files: " + name);
						}
					}
				} else {
					LOG.info(fileName + " is not java archive (jar)");
				}
			}
		} catch (Throwable e) {
			LOG.error("Got exception processing directory for EJB3 entity", e);
			return null;
		}
		return result;
	}
	
	public List<ModelEntity> getDependentEntities (ModelEntity e) {
		return e.getDependentEntities();
	}
	
	public List<ModelEntity> getRelatedEntities (ModelEntity e) {
		return e.getRelatedEntities();
	}
	
	public List<ModelAttribute> getAttributes (ModelEntity e) {
		return e.getAttribList();
	}
	
	/**
	 * 
	 * @return a unique operation id for the project.
	 */
	public synchronized int getNextOperationId() {

		loadCounter();
		++counter;
		saveCounter();
		return counter;

	}
	
	
	private void loadCounter() {

		counter = CPFPlugin.getDefault().getPreferenceStore().getInt(
				PreferenceConstants.OPERATION_ID);
		LOG.info ("Loading counter is..." + counter);

	}

    private void saveCounter() {

		LOG.info ("Saving counter is..." + counter);
		CPFPlugin.getDefault().getPreferenceStore().setValue(
				PreferenceConstants.OPERATION_ID, counter);
		CPFPlugin.getDefault().savePluginPreferences();

	}
	
	/**
	 * 
	 * @param m1
	 *            first entity
	 * @param m2
	 *            second entity
	 * @return true if relationship from m1 to m2 is as OneToMany or ManyToMany
	 *         false if relationship from m1 to m2 is as ManyToOne or OneToOne
	 * @throws {@link IllegalArgumentException}
	 *             if m1 and m2 are not related.
	 */
	public boolean isMappedToMany (ModelEntity m1, ModelEntity m2) throws IllegalArgumentException {
		
		List<ModelAttribute> attribs1 = m1.getAttribList();
		for (ModelAttribute a1 : attribs1) {
			if (a1.isFK()
					&& a1.getForeignEntity().equals(m2)
					&& (a1.getRelType().getMapping() == RelationshipType.OneToMany
							|| a1.getRelType().getMapping() == RelationshipType.ManyToMany))
							
							return true;
		}
		return false;
	}
	/**
	 * Finds out if ModelEntity m is the owner in relationship for attribute a
	 * @param m
	 * @param a
	 * @return
	 * @throws IllegalArgumentException if a is not attribute of m.
	 */
	public boolean isOwner (ModelEntity m, ModelAttribute a) throws IllegalArgumentException {
		
		if (a.isOwner() && a.getEntity().equals(m))
			return true;
		
		return false;
	}
	
	public RelationShipInfo findRelationShipInfo (ModelEntity baseEntity, ModelEntity targetEntity) {
		
		RelationShipInfo relationShipInfo = null;
		
		List<ModelAttribute> attribs1 = baseEntity.getAttribList();
		for (ModelAttribute a1 : attribs1) {
			if (a1.isFK()
					&& a1.getForeignEntity().equals(targetEntity)) {
				
				relationShipInfo = a1.getRelType();
				LOG.info("Got relationship: " + relationShipInfo);				

			}
		}
		return relationShipInfo;
	}
	
/*	public static Blob mapByteArray2Blob (byte[] blob) {
		return null;
	}
	
	public static Clob mapCharArray2Clob (char[] clob) {
		return null;
	}
	
	public static byte[] mapBlob2ByteArray(Blob blob) {
		return null;
	}
	
	public static char[] mapClob2CharArray(Clob clob) {
		return null;
	}
	*/
	public ModelAttribute getPrimaryKey (ModelEntity modelEntity) {		

		List<ModelAttribute> attribs = modelEntity.getAttribList();
		for (ModelAttribute a : attribs) {
				System.out.println(a.getName() + a.isPK());
			if (a.isPK())
				return a;
		}
		
		return null; //should not happen
	}
	/**
	 * <pre> processes format string for grouping and currency symbol
	 * e.g. input: ####.00 output: #,###.00
	 * e.g. input: ##.00 output: ##.00
	 * e.g input: ###0000.00## output: ###0,000.00##
	 * </pre>
	 * @param formatData the input pattern without taking care of grouping
	 * @return pattern string after formatting
	 */
	public String processPattern (FormatData formatData) {
		String pattern = formatData.getPattern();
		StringBuilder sb = new StringBuilder(pattern);
		
		if(formatData.isGrouping()) {
			
			int dotPos = pattern.indexOf('.');
			if(dotPos == -1) { //there is no dot
				if (sb.length() > 3)
					sb.insert(sb.length() - 3, ',');
			} else {
				if (dotPos > 3)
					sb.insert(dotPos - 3, ',');					
			}
			LOG.info("Process pattern string for grouping: " + sb);
		}		
		
		if (formatData.getCurrencyCode() != null) {		
			sb.insert(0, formatData.getCurrencySymbol() == null ? formatData.getCurrencyCode() : formatData.getCurrencySymbol());
			LOG.info("Process pattern string for currency: " + sb);
		}
		pattern = sb.toString();
		LOG.info("Returning pattern as: " + pattern);
		return pattern;
	}
	
	/**
	 * parses input pattern string as a simple date formatter
	 * @param pattern should be a SimpleDateFormat compatible string
	 * @return true if passed else false
	 */
	public boolean parseDateTimeFormat (String pattern) {
		boolean result = true; //assume passed
		
		if (pattern == null
				|| pattern.trim().length() == 0)
			result = false;
		
		try {
			new SimpleDateFormat(pattern);
		} catch (Exception e) {
			LOG.error("Got exception parsing date format: " + pattern, e);
			result = false;
		}
		
		return result;
	}
	
	
	public boolean parseNumericFormat(String pattern){
		boolean result=true;
		String numStr="1234567890.";
		for(int l=0; l < pattern.length(); l++)
		{
			String thisChar=pattern.substring(l,l+1);
			if(numStr.indexOf(thisChar)>=0) 
			{
				result=true;
				
			}else{
				result=false;
			}
		}
		return result;
	}
	
	public boolean parseIntegeralFormat(String pattern){
		boolean result=true;
		String numStr="1234567890";
		for(int l=0; l < pattern.length(); l++)
		{
			String thisChar=pattern.substring(l,l+1);
			if(numStr.indexOf(thisChar)>=0) 
			{
				result=true;
				
			}else{
				result=false;
			}
		}
		return result;
	}
}
