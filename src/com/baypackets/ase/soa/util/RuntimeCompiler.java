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
//      File:   RuntimeCompiler.java
//
//      Desc:   This file defines a utility class for runtime compilation of java source files.
//
//      Author                          Date            Description
//      ----------------------------------------------------------------------
//      Rajendra Singh                  07/01/08        Initial Creation
//
//***********************************************************************************
                                                                                                                                        
package com.baypackets.ase.soa.util;



import java.io.*;
import java.util.*;
import javax.tools.*;
import org.apache.log4j.Logger;

public class RuntimeCompiler	{
	private static Logger logger = Logger.getLogger(RuntimeCompiler.class);
    public RuntimeCompiler()	{
		if(logger.isDebugEnabled())
		logger.debug("CLASS LOADER: " +this.getClass().getClassLoader());
    }

    public boolean compile(String srcDir,String outputDir,List<File>classPaths)	{
		if(logger.isDebugEnabled())	{
			logger.debug("SOURCE DIR: "+srcDir);
			logger.debug("OUTPUT DIR: "+outputDir);
			logger.debug("CLASSPATH: "+classPaths);
		}
        File file = new File(srcDir);
		List<File> files = null;
	
		try	{
			files = getFileListing(file);
        }catch(Exception exception)	{
            logger.error("Exception in Reading root source directory: "+exception);
        }

    	//print out all file names, and display the order of File.compareTo
    	/*for(File file1 : files ){
      		System.out.println(file1);
    	}
		*/
        JavaCompiler javacompiler = ToolProvider.getSystemJavaCompiler();
        StandardJavaFileManager standardjavafilemanager = javacompiler.getStandardFileManager(null, null, null);
		Iterable<? extends JavaFileObject> compilationUnits =
           standardjavafilemanager.getJavaFileObjectsFromFiles(files);
		try	{
			standardjavafilemanager.setLocation(StandardLocation.CLASS_PATH, classPaths);
			standardjavafilemanager.setLocation(StandardLocation.CLASS_OUTPUT,
							 Collections.singleton(new File(outputDir)));
		}catch(Exception e)	{
            logger.error("Exception in setting classpath: "+e);
		}
        JavaCompiler.CompilationTask compilationtask 
				= javacompiler.getTask(null, standardjavafilemanager, null, null, null, compilationUnits);
		boolean flag = false;
		try	{
        	flag = compilationtask.call().booleanValue();
		}catch(Exception exp)	{
			logger.error("Exception is: "+exp);
		}catch(Throwable th)	{
			logger.error("Throwable Exception is: "+th);
		}
        if(flag)
            logger.error("Compilation was successful");
        else
            logger.error("Compilation failed");

        try	{
            standardjavafilemanager.close();
        }
        catch(IOException ioexception)	{
            logger.error("I/O Exception occurred: "+ioexception);
        }
		return flag;
    }

	/**
 	* Recursively walk a directory tree and return a List of all
  	* Files found; the List is sorted using File.compareTo.
 	*
  	* @param aStartingDir is a valid directory, which can be read.
  	*/
	private List<File> getFileListing(File aStartingDir) throws FileNotFoundException {
    	validateDirectory(aStartingDir);
    	List<File> result = new ArrayList<File>();

    	File[] filesAndDirs = aStartingDir.listFiles();
    	List<File> filesDirs = Arrays.asList(filesAndDirs);
    	for(File file : filesDirs) {
       		if(file.isFile() && file.getName().endsWith(".java"))	{
      			result.add(file); //add files with .java estension only
			}
      		if ( ! file.isFile() ) {
        		//must be a directory
        		//recursive call!
        		List<File> deeperList = getFileListing(file);
        		result.addAll(deeperList);
      		}

    	}
		Collections.sort(result);
    	return result;
	}
	
	public boolean compileJavaSourceFile(String class_name, String src_code, String outputDir,List<File>classPaths)   {
        if(logger.isDebugEnabled()) {
            logger.debug("class_name: "+class_name);
            logger.debug(" src_code: "+src_code);
        }
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        DiagnosticCollector<JavaFileObject> diagnostics =
                new DiagnosticCollector<JavaFileObject>();
        JavaFileObject file = new JavaSourceFromString(class_name, src_code);
        Iterable<? extends JavaFileObject> compilationUnits = Arrays.asList(file);
        StandardJavaFileManager standardjavafilemanager = compiler.getStandardFileManager(null, null, null);
		try {
            standardjavafilemanager.setLocation(StandardLocation.CLASS_PATH, classPaths);
            standardjavafilemanager.setLocation(StandardLocation.CLASS_OUTPUT,
                             Collections.singleton(new File(outputDir)));
        }catch(Exception e) {
            logger.error("Exception in setting classpath: "+e);
        }
        JavaCompiler.CompilationTask compilationtask
                = compiler.getTask(null, standardjavafilemanager, diagnostics, null, null, compilationUnits);
        boolean flag = false;
        try {
            flag = compilationtask.call().booleanValue();
        }catch(Exception exp)   {
            logger.error("Exception is: "+exp);
        }catch(Throwable th)    {
            logger.error("Throwable Exception is: "+th);
        }
        if(logger.isDebugEnabled()) {
            for (Diagnostic diagnostic : diagnostics.getDiagnostics())  {
                logger.debug("CODE: " +diagnostic.getCode());
                logger.debug("KIND: " +diagnostic.getKind());
                logger.debug("Position: " +diagnostic.getPosition());
                logger.debug("StartPosition: " +diagnostic.getStartPosition());
                logger.debug("EndPosition: " +diagnostic.getEndPosition());
                logger.debug("SOURCE: " +diagnostic.getSource());
                logger.debug("MESSAGE: " +diagnostic.getMessage(null));
                                                                                                                          
            }
        }
        if(flag)
            logger.error("Compilation was successful");
        else
            logger.error("Compilation failed");
                                                                                                                          
        try {
            standardjavafilemanager.close();
        }
        catch(IOException ioexception)  {
            logger.error("I/O Exception occurred: "+ioexception);
        }
        return flag;

	}


	public boolean compileSource(String class_name, String src_code, ClassLoader cl,List<File>classPaths)	{
		if(logger.isDebugEnabled())	{
			logger.debug("class_name: "+class_name);
			logger.debug(" src_code: "+src_code);
			logger.debug("ClassLoader: "+cl);
		}
		JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
      	DiagnosticCollector<JavaFileObject> diagnostics =
                new DiagnosticCollector<JavaFileObject>();
		JavaFileObject file = new JavaSourceFromString(class_name, src_code);
        Iterable<? extends JavaFileObject> compilationUnits = Arrays.asList(file);
        StandardJavaFileManager standardjavafilemanager = compiler.getStandardFileManager(null, null, null);
		try	{
			standardjavafilemanager.setLocation(StandardLocation.CLASS_PATH, classPaths);
		}catch(Exception e)	{
			logger.error("Error in Setting classpath for runtime compiler");
		}
		MemoryOutputJavaFileManager fileManager = 
			new MemoryOutputJavaFileManager(standardjavafilemanager);
        JavaCompiler.CompilationTask task = compiler.getTask(
                null, fileManager, diagnostics, null, null, compilationUnits);
        boolean success = task.call();
		if(logger.isDebugEnabled()) {
        	for (Diagnostic diagnostic : diagnostics.getDiagnostics())	{
      			logger.debug("CODE: " +diagnostic.getCode());
      			logger.debug("KIND: " +diagnostic.getKind());
      			logger.debug("Position: " +diagnostic.getPosition());
      			logger.debug("StartPosition: " +diagnostic.getStartPosition());
      			logger.debug("EndPosition: " +diagnostic.getEndPosition());
      			logger.debug("SOURCE: " +diagnostic.getSource());
      			logger.debug("MESSAGE: " +diagnostic.getMessage(null));

    		}
			if(logger.isDebugEnabled())
        	logger.debug("Success: " + success);
		}
        if (success) {
			if(logger.isDebugEnabled()) {
				logger.debug("Compilation succeeded");
			}
			/*
            try {
			Object obj = (Class.forName(class_name,true,cl)).newInstance();
			if(logger.isDebugEnabled()) {
				logger.debug("Created Object successfully: " + obj);
			}
            } catch (Exception e) {
                logger.error("Exception in object creation: " + e);
            }
			*/ 
        }
		return success;
	}



/**
  * Directory is valid if it exists, does not represent a file, and can be read.
  */
  private void validateDirectory (File aDirectory) throws FileNotFoundException {
    if (aDirectory == null) {
      throw new IllegalArgumentException("Directory should not be null.");
    }
    if (!aDirectory.exists()) {
      throw new FileNotFoundException("Directory does not exist: " + aDirectory);
    }
    if (!aDirectory.isDirectory()) {
      throw new IllegalArgumentException("Is not a directory: " + aDirectory);
    }
    if (!aDirectory.canRead()) {
      throw new IllegalArgumentException("Directory cannot be read: " + aDirectory);
    }
  }

    public static void main(String args[])	{
        RuntimeCompiler runtimecompiler = new RuntimeCompiler();
        String srcdir = "/user/rajendra/soa/src";
        String outputDir = "/user/rajendra/soa/classes";
		List<File> classPaths = Arrays.asList(new File("."),new File("/user/rajendra/soa/src/classes.jar"));
        runtimecompiler.compile(srcdir,outputDir,classPaths);
    }

}
