package com.genband.m5.maps.ide.model.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.RandomAccessFile;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.ui.IFileEditorInput;

import com.genband.m5.maps.ide.MyObjectInputStream;
import com.genband.m5.maps.ide.CPFPlugin;
import com.genband.m5.maps.ide.model.CPFPortlet;
import com.genband.m5.maps.ide.model.CPFScreen;
import com.genband.m5.maps.ide.sitemap.util.SiteMapUtil;

public class CPFPortalObjectPersister {
	//TODO
	private static final String PORTAL_CONTROL_FILE = "FileSeqNumCtrl.dat";

	private RandomAccessFile controlFile;

	private String controlFileName = PORTAL_CONTROL_FILE;

	String filePath = "";

	File persisterFile;

	String filePrefix = "data_";

	String fileSufix = ".ser";

	int max = 0;
	private static CPFPortalObjectPersister persisterRef=null;
	
	
	 public static CPFPortalObjectPersister getInstance(){
		if(persisterRef==null){
			persisterRef=new CPFPortalObjectPersister();
			
		}else{
			return persisterRef;
		}
		return persisterRef;
	}
	
	  public CPFPortalObjectPersister() {
	    }
	
	   public void createPersisterFile(String filePath){
		
           try{
			//	  File file = new File(filePath, this.controlFileName);
			//      this.controlFile = new RandomAccessFile(file, "rwd");
			//     String fileName=this.generateFileName();

			//+"Control file is"+this.controlFile);
			//	        	  }catch(FileNotFoundException f){
			//	        		  CPFPlugin.getDefault().log( "The File not found Exception thrown by CPFScreenObjectPersister "+ f); 
			//	        	  }
		
		   String fileName =generateFileName(filePath);
			persisterFile = new File(filePath, fileName);
			CPFPlugin.getDefault().log(
					"The Persiser file Instance created is " + persisterFile);
           }catch(Exception io){
        	   CPFPlugin.getDefault().log(
   					"Exception thrown by persister while creating new file"+io); 
           }
	  }

	// add records to file
	public void writeObject(CPFPortlet portal) {
		try {

			ObjectOutputStream output = new ObjectOutputStream(
					new BufferedOutputStream(
							new FileOutputStream(persisterFile)));
			output.writeObject(portal);

			try // close file 
			{
				if (output != null)
					output.close();
			} // end try
			catch (IOException ioException) {
				CPFPlugin.getDefault().log(
						"Error closing file. " + ioException, IStatus.ERROR);

			} //    output.close();

		} catch (IOException e) {
			CPFPlugin.getDefault().error ("Error writing to file. ", e);
			return;
		} // end catch

	} // end method addRecords

	//	      add records to file
	public CPFPortlet readObject(File persisterFile) {
		CPFPortlet screen = null;
		try {
			// Changes related to PR 49914 (ClassLoading issue) starts
			Path abc=new Path(persisterFile.getAbsolutePath());
			CPFPlugin.getDefault().log("path is :  " + abc);
			CPFPlugin.getDefault().log(" IFile is " + ResourcesPlugin.getWorkspace().getRoot().getFile(abc));
			CPFPlugin.getDefault().log(" IFile location is " + ResourcesPlugin.getWorkspace().getRoot().getFile(abc).getLocation());
			
//			CPFPlugin.getDefault().log(" IFILE name is " + ResourcesPlugin.getWorkspace().getRoot().getFile(abc).getName());
//			CPFPlugin.getDefault().log(" IProject is " + ResourcesPlugin.getWorkspace().getRoot().getFile(abc).getProject());
//			CPFPlugin.getDefault().log(" workspace  is " + ResourcesPlugin.getWorkspace().getRoot().getFile(abc).getWorkspace());
//			CPFPlugin.getDefault().log(" Parent is " + ResourcesPlugin.getWorkspace().getRoot().getFile(abc).getParent());
//			CPFPlugin.getDefault().log(" Parent name is " + ResourcesPlugin.getWorkspace().getRoot().getFile(abc).getParent().getName());
//			CPFPlugin.getDefault().log(" Parent parent is " + ResourcesPlugin.getWorkspace().getRoot().getFile(abc).getParent().getParent());
//			CPFPlugin.getDefault().log(" Parent parent name is " + ResourcesPlugin.getWorkspace().getRoot().getFile(abc).getParent().getParent().getName());
//			CPFPlugin.getDefault().log(" Project name is " + ResourcesPlugin.getWorkspace().getRoot().getFile(abc).getProject().getName());
//			CPFPlugin.getDefault().log(" Project name from parent is " + ResourcesPlugin.getWorkspace().getRoot().getFile(abc).getParent().getProject().getName());
//			CPFPlugin.getDefault().log(" Project relativepath is " + ResourcesPlugin.getWorkspace().getRoot().getFile(abc).getParent().getProjectRelativePath());
			IFile file = ResourcesPlugin.getWorkspace().getRoot().getFile(abc);
			IProject pr=ResourcesPlugin.getWorkspace().getRoot().getFile(abc).getProject();
			CPFPlugin.getDefault().log("project name :  " + pr.getName());
			String projectName = file.getParent().getParent().getParent().getParent().getName();
			CPFPlugin.getDefault().log("correct project name :  " + projectName);
			//SiteMapUtil.getExtrnalJars(pr.getName());
			List<String> externalJarsPath = SiteMapUtil.getExtrnalJars(projectName);
			
			ClassLoader parentLoader = 	Thread.currentThread().getContextClassLoader();
			//ClassLoader parentLoader = 	CPFScreen.class.getClassLoader();
			File gbFile = new File (CPFPlugin.fullPath("library/gb-common.jar"));
			//URLClassLoader loader = null;
			URLClassLoader loader = new URLClassLoader(new URL[] { new URL(
					"file:///" + gbFile.getAbsolutePath()) }, parentLoader);
			//CPFPlugin.getDefault().log("externaljarCount =  " + externalJarsPath.size());
			URL urls[]= new URL[30];
			for ( int i = 0 ; i < externalJarsPath.size() ; i++ ) {
				File jarFile = new File (externalJarsPath.get(i));
				urls[i] = new URL("file:///" + jarFile.getAbsolutePath());
				
			}

			loader = new URLClassLoader(urls, parentLoader);
			
////////
			
			
			
			/*for ( int i = 0 ; i < externalJarsPath.size() ; i++ ) {
				File jarFile = new File (externalJarsPath.get(i));
				try {
					loader = new URLClassLoader(new URL[] { new URL(
							"file:///" + jarFile.getAbsolutePath()) }, parentLoader);
					input = new MyObjectInputStream(loader, new FileInputStream(persisterFile));
					CPFPlugin.getDefault().log("input is : " + input);
					try{
						screen = (CPFPortlet) input.readObject();
						CPFPlugin.getDefault().log(" reading successfull");
						break;
					}catch(Exception e) {
						CPFPlugin.getDefault().log(" reading unsuccessfull:((");
						continue;
					}
					
				} catch (MalformedURLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				
				//Thread.currentThread().setContextClassLoader( loader );
			}
			*/
			/*ClassLoader parentLoader = 	CPFScreen.class.getClassLoader();
			File file1 = new File ("c://jars//ad.jar");
			URLClassLoader loader = new URLClassLoader(new URL[] { new URL(
					"file:///" + file1.getAbsolutePath()) }, parentLoader);
			
			
			
			
			Thread.currentThread().setContextClassLoader( loader );
			//CPFPlugin.getDefault().log(" new loader url is : " + loader.getURLs()[0]);
			//CPFPlugin.getDefault().info("url is : " + ((URLClassLoader)Thread.currentThread().getContextClassLoader()).getURLs()[0]);
			
			CPFPlugin.getDefault().info("CPFPortalObjectPersister : going to read the file: ");
			
			
			//Class.forName("com.genband.m5.maps.common.ud.Project7", true, loader);
			ObjectInputStream input = new ObjectInputStream(
				new BufferedInputStream(new FileInputStream(persisterFile)));
			*/
			//ObjectInputStream input = new ObjectInputStream(
				//	new BufferedInputStream(new FileInputStream(persisterFile)));
			ObjectInputStream input = new MyObjectInputStream(loader, new FileInputStream(persisterFile));
			// Changes related to PR 49914 (ClassLoading issue) ends
			
			screen = (CPFPortlet) input.readObject();
			CPFPlugin.getDefault().info("CPFPortalObjectPersister : have tried to read the file: ");
			
			try // close file 
			{
				if (input != null)
					input.close();
			} // end try
			catch (IOException ioException) {
				CPFPlugin.getDefault().info("got exception while trying  to read the file: ");
					CPFPlugin.getDefault().log(
						"Error closing file. " + ioException, IStatus.ERROR);

			} //    output.close();

		} catch (IOException ioException) {
			CPFPlugin.getDefault().log(
					"Error Reading from file. " + ioException, IStatus.ERROR);
			return null;
		} // end catch
		catch (ClassNotFoundException ioException) {
			CPFPlugin.getDefault().log(
					"Class CPFScreen Not Found. " + ioException, IStatus.ERROR);
			return null;
		} // end catch 

		return screen;
	} // end method addRecords  
	
	
	private String generateFileName(String filePath) {
		File dir = new File(filePath);
		File[] serFiles = dir.listFiles();

		if (serFiles != null) {
			CPFPlugin.getDefault()
			.log("The PERSISTANCE Files Found in Store are................" + serFiles.length);
			for (int i = 0; i < serFiles.length; i++) {
				String filename = serFiles[i].getName();
				if (filename.endsWith(".ser")) {
					int j = filename.indexOf("_");
					int k = filename.indexOf(".");
					String ind = filename.substring(j+1, k);
					int index = Integer.parseInt(ind);
					if (index > max) {
						max = index;
					}

				}
			}
		}

	    String fileName = filePrefix + ++max + fileSufix;
	    CPFPlugin.getDefault()
		.log("The Index for the .SER file is.." + max);
	    return fileName;
	
	}
	
	
	public void  decreaseFileIndex(){
		if(max>0)
		 --max;
	 }
	

//	private String generateFileName() {
//
//		int rc = this.readRunningCount();
//		String filename = filePrefix + rc + fileSufix;
//		CPFPlugin.getDefault().log(
//				"Reading Running Count from Control file. " + rc);
//		this.writeRunningCount(++rc);
//		CPFPlugin.getDefault().log(
//				"Writing Running Count to the Control file. " + rc);
//		return filename;
//
//	}
//
//	private int readRunningCount() {
//		int rc = 0;
//		try {
//			this.controlFile.seek(0);
//			rc = (this.controlFile.length() >= 4) ? this.controlFile.readInt()
//					: 1;
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//		return rc;
//	}
//
//	/*
//	 * This method is used to write the new running count to the SeqCtrlFile.dat
//	 * which will be used as running count for the next file to be created.
//	 */
//	private void writeRunningCount(int rc) {
//		try {
//			this.controlFile.seek(0);
//			this.controlFile.setLength(0);
//			this.controlFile.writeInt(rc);
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//	}

}
