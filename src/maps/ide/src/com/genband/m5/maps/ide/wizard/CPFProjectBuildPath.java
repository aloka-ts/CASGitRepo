package com.genband.m5.maps.ide.wizard;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
//import org.eclipse.wst.common.project.facet.core.;
//import org.eclipse.wst.common.componentcore.
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.core.resources.ProjectScope;
import org.eclipse.core.runtime.preferences.IScopeContext;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.jdt.internal.ui.preferences.NewJavaProjectPreferencePage;
import org.eclipse.jdt.internal.ui.util.BusyIndicatorRunnableContext;
import org.eclipse.jdt.internal.ui.wizards.IStatusChangeListener;
import org.eclipse.jdt.internal.ui.wizards.NewWizardMessages;
import org.eclipse.jdt.internal.ui.wizards.buildpaths.BuildPathsBlock;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import com.genband.m5.maps.ide.CPFPlugin;
import com.genband.m5.maps.ide.builder.CPFNature;

public class CPFProjectBuildPath implements IRunnableWithProgress {

	private CPFProjectWizard wizard = null;
	private static final String EJB3_JAR = "library/ejb3-persistence.jar";
	private static final String GB_COMMON_JAR = "library/gb-common.jar";
	private static final String  MY_FACES_API="/library/myfaces-api-1.2.0.jar";
	private static final String  MY_FACES_IMPL="/library/myfaces-impl-1.2.0.jar";
	private static final String PORTLET_API_LIB="/library/portlet-api-lib.jar";
	private static final String SERVLET_API="/library/servlet-api.jar";
	private static final String JAX_WS="/library/jboss-jaxws.jar";
	private static final String EL_API="/library/el-api.jar";
	private static final String CPF_EJB="/library/CPF_EJB.jar";
	private static final String JBOSS_CLIENT="/library/jbossall-client.jar";

	public CPFProjectBuildPath(CPFProjectWizard wizard) {

		IStatusChangeListener listener = new IStatusChangeListener() {
			public void statusChanged(IStatus status) {
			}
		};
		fBuildPathsBlock = new BuildPathsBlock(
				new BusyIndicatorRunnableContext(), listener, 0, false, null);
		this.wizard = wizard;
	}

	public void run(IProgressMonitor monitor) throws InvocationTargetException,
			InterruptedException {
		if (monitor == null) {
			monitor = new NullProgressMonitor();
		}
		monitor.beginTask(NewWizardMessages.NewJavaProjectWizardPage_op_desc,
				10);

		// create the project
		try {
		//	new SubProgressMonitor(monitor, 2)
			BuildPathsBlock.createProject(getProjectHandle(),getProjectHandle().getLocationURI(), monitor);
			initBuildPaths(monitor);
			BuildPathsBlock.addJavaNature(getProjectHandle(),
					new SubProgressMonitor(monitor, 2));
			fBuildPathsBlock.configureJavaProject(new SubProgressMonitor(
					monitor, 6));
			addCPFNature(getProjectHandle());
			addEarNature();

		} catch (CoreException e) {
			throw new InvocationTargetException(e);
		} catch (OperationCanceledException e) {
			throw new InterruptedException();
		} finally {
			monitor.done();
		}
	}

	public void initBuildPaths(IProgressMonitor monitor) {

		ArrayList list = new ArrayList();

		list.add(JavaCore.newSourceEntry(getProjectHandle().getFullPath()
				.append("/src")));

		CPFPlugin.getDefault().log(
				"The init build path add entity jar files..........");
		addEntityJarFilesToClasspath(monitor, list);
		
		
		IClasspathEntry cp = JavaCore.newLibraryEntry(new Path(CPFPlugin.fullPath(EJB3_JAR)), null, null);
		list.add(cp);

		cp = JavaCore.newLibraryEntry(new Path(CPFPlugin.fullPath(GB_COMMON_JAR)), null, null);
		list.add(cp);
		
		cp = JavaCore.newLibraryEntry(new Path(CPFPlugin.fullPath(this.MY_FACES_API)), null, null);
		list.add(cp);
		
		cp = JavaCore.newLibraryEntry(new Path(CPFPlugin.fullPath(this.MY_FACES_IMPL)), null, null);
		list.add(cp);
		
		cp = JavaCore.newLibraryEntry(new Path(CPFPlugin.fullPath(this.PORTLET_API_LIB)), null, null);
		list.add(cp);
		
		cp = JavaCore.newLibraryEntry(new Path(CPFPlugin.fullPath(this.JAX_WS)), null, null);
		list.add(cp);
		
		cp = JavaCore.newLibraryEntry(new Path(CPFPlugin.fullPath(this.SERVLET_API)), null, null);
		list.add(cp);
		
		cp = JavaCore.newLibraryEntry(new Path(CPFPlugin.fullPath(this.EL_API)), null, null);
		list.add(cp);
		
		cp = JavaCore.newLibraryEntry(new Path(CPFPlugin.fullPath(this.CPF_EJB)), null, null);
		list.add(cp);
		
		cp = JavaCore.newLibraryEntry(new Path(CPFPlugin.fullPath(this.JBOSS_CLIENT)), null, null);
		list.add(cp);
		
		// Add the default Jar files to the Classpath.

		IClasspathEntry[] cps = new IClasspathEntry[list.size()];
		cps = (IClasspathEntry[]) list.toArray(cps);
		
		
		
		this.setDefaultClassPath(cps, true);

		// Set the output location for this project.
		IPath outputPath = this.wizard.getFirstPage().getProjectHandle()
				.getFullPath().append("/bin");
		this.setDefaultOutputFolder(outputPath);
		this.initProject();
	}

	public void setDefaultOutputFolder(IPath path) {
		fOutputLocation = path;
	}

	public void setDefaultClassPath(IClasspathEntry[] entries,
			boolean appendDefaultJRE) {
		if (entries != null && appendDefaultJRE) {
			IClasspathEntry[] jreEntry = NewJavaProjectPreferencePage
					.getDefaultJRELibrary();
			IClasspathEntry[] newEntries = new IClasspathEntry[entries.length
					+ jreEntry.length];
			System.arraycopy(entries, 0, newEntries, 0, entries.length);
			System.arraycopy(jreEntry, 0, newEntries, entries.length,
					jreEntry.length);
			entries = newEntries;
		}
		fClasspathEntries = entries;
	}

	private void addEntityJarFilesToClasspath(IProgressMonitor monitor,
			ArrayList classpathEntriesList) {

		try {
			java.util.List jarFilesList = this.wizard.getFirstPage()
					.getJarFiles();

			IFolder resFolder = getProjectHandle().getFolder("EJBContent");

			if (!resFolder.exists())
				resFolder.create(true, true, monitor);

			if (jarFilesList != null) {
				for (int i = 0; i < jarFilesList.size(); i++) {

					String jarFile = (String) jarFilesList.get(i);

					IPath libPath = resFolder.getFullPath(); // getProjectHandle().getFullPath().append("/lib");

					CPFPlugin.getDefault().log(
							"Adding jar files " + jarFile + " to " + libPath);

					IPath path = new Path(jarFile);
					if (path.getFileExtension().equals("jar")) {

						CPFPlugin.getDefault().log(
								"File Extension is::::::::: "
										+ path.getFileExtension());
                        File jarF=new File(jarFile);
                        String filename=jarF.getName();
//						String paths[] = jarFile.split("/");
//						String filename = paths[paths.length - 1];

						IFile file = getProjectHandle().getFile(
								new Path("EJBContent").append(filename));

						CPFPlugin.getDefault().log(
								"The File is***********::::::::: " + file
										+ "original file is" + path.toFile());

						if (!file.exists()) {
							FileInputStream stream = new FileInputStream(path
									.toFile());
							file.create(stream, true, monitor);
							CPFPlugin.getDefault().log(
									"The full path of file is.."
											+ file.getFullPath());
							IClasspathEntry cp = JavaCore.newLibraryEntry(file
									.getFullPath(), null, null);
							classpathEntriesList.add(cp);
							CPFPlugin.getDefault().log(
									"Copied file " + file
											+ " to Lib directory *********"
											+ libPath);

						}
					}
				}
			}
		} catch (Exception e) {
			CPFPlugin.getDefault().log(e.getMessage(), e, -1);
		}
	}

	private void initProject() {
		fBuildPathsBlock.init(getNewJavaProject(), fOutputLocation,
				fClasspathEntries);
	}

	protected IProject getProjectHandle() {

		return this.wizard.getFirstPage().getProjectHandle();
	}

	/**
	 * Returns the project location path. Subclasses should override this method
	 * if they don't provide a main page or if they provide their own main page
	 * implementation.
	 * 
	 * @return the project location path
	 */
	protected IPath getLocationPath() {
		return this.wizard.getFirstPage().getLocationPath();
	}

	/**
	 * Returns the Java project handle by converting the result of
	 * <code>getProjectHandle()</code> into a Java project.
	 * 
	 * @return the Java project handle
	 * @see #getProjectHandle()
	 */
	public IJavaProject getNewJavaProject() {
		return JavaCore.create(getProjectHandle());
	}

	public IClasspathEntry[] getRawClassPath() {
		return fBuildPathsBlock.getRawClassPath();
	}

	public IPath getOutputLocation() {
		return fBuildPathsBlock.getOutputLocation();
	}

	/**
	 * Add CPF nature to this New project
	 * 
	 * @param project
	 *            to have sample nature added
	 */
	private void addCPFNature(IProject project) {
		try {
			CPFPlugin.getDefault().log(
					"Add Project nature!!!!!!!!!!!!" );
			IProjectDescription description = project.getDescription();
			String[] natures = description.getNatureIds();
			for (int i = 0; i < natures.length; ++i) {
				if (CPFNature.NATURE_ID.equals(natures[i])) {
					// Remove the nature
					String[] newNatures = new String[natures.length - 1];
					System.arraycopy(natures, 0, newNatures, 0, i);
					System.arraycopy(natures, i + 1, newNatures, i,
							natures.length - i - 1);
					description.setNatureIds(newNatures);
					project.setDescription(description, null);
					return;
				}
			}

			// Add the nature
			String[] newNatures = new String[natures.length + 1];
			System.arraycopy(natures, 0, newNatures, 0, natures.length);
			newNatures[natures.length] = CPFNature.NATURE_ID;
			description.setNatureIds(newNatures);
			CPFPlugin.getDefault().log(
					"Add Project nature!!!!!!!!!!!!" + newNatures);
			for (int j = 0; j < newNatures.length; j++) {
				CPFPlugin.getDefault().log(
						"Add Project nature!!!!!!!!!!!!" + newNatures[j]);
			}

			project.setDescription(description, null);
		} catch (CoreException e) {
		}
	}

	private IPath fOutputLocation;

	private IClasspathEntry[] fClasspathEntries;

	private BuildPathsBlock fBuildPathsBlock;

	
	public void addEarNature() 
	{
		try{
		CPFPlugin.getDefault().log(
				"Adding EAR Natue Project nature!!!!!!!!!!!!" );
		String path =Platform.getLocation().toOSString()+getProjectHandle().getFullPath().append(".project").toOSString();
		
		File file=new File(path);
		CPFPlugin.getDefault().log("The .project file is...."+file);	
		if(file.exists()){
			CPFPlugin.getDefault().log("The .project file exists"+file);	
			
		FileReader r=new FileReader(file);
		org.w3c.dom.Document doc = createDocument(r);
		addEarBuildCommand(doc);
		addEarNature(doc);
		
		Source source = new DOMSource(doc);
		Result result = new StreamResult(file);
		Transformer xformer = TransformerFactory.newInstance()
				.newTransformer();
		xformer.transform(source, result);
		}
		}catch(IOException e){
			CPFPlugin.getDefault().error("addEarNature has thrown IOexception");	
		}catch(Exception e){
			CPFPlugin.getDefault().error("addEarNature has thrown Exception");	
		}
	}
	
	
	/**
	 * Create a Document with content based on the content of the given Reader.
	 */
	protected org.w3c.dom.Document createDocument(Reader r) throws Exception
	{
		org.w3c.dom.Document document;
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder parser = factory.newDocumentBuilder();
			document = parser.parse(new InputSource(r));
			return document;
		}
		catch (Exception e) {
			throw e;
		}
	}
	
	
	public void addEarBuildCommand(org.w3c.dom.Document doc) throws Exception
	{
		Element docElement = doc.getDocumentElement();
		
		NodeList buildSpecNode = doc.getElementsByTagName("buildSpec");
		
		Node buildspec=buildSpecNode.item(0);
		
		Element buildCmdEle = doc.createElement("buildCommand");
		buildCmdEle.appendChild(doc.createTextNode("\n"));
		
		Element name = doc.createElement("name");
		name.appendChild(doc.createTextNode(EAR_FACET_BUILDER));
		buildCmdEle.appendChild(name);
		buildCmdEle.appendChild(doc.createTextNode("\n"));
		
		Element args = doc.createElement("arguments");
		args.appendChild(doc.createTextNode("\n"));
		buildCmdEle.appendChild(args);
		buildCmdEle.appendChild(doc.createTextNode("\n"));
		
		buildspec.appendChild(buildCmdEle);
		
		buildspec.appendChild(doc.createTextNode("\n"));
		
		
		buildCmdEle = doc.createElement("buildCommand");
		buildCmdEle.appendChild(doc.createTextNode("\n"));
		
		name = doc.createElement("name");
		name.appendChild(doc.createTextNode(EAR_VALIDATION_BUILDER));
		buildCmdEle.appendChild(name);
		buildCmdEle.appendChild(doc.createTextNode("\n"));
		
		args = doc.createElement("arguments");
		args.appendChild(doc.createTextNode("\n"));
		buildCmdEle.appendChild(args);
		buildCmdEle.appendChild(doc.createTextNode("\n"));
		
		buildspec.appendChild(buildCmdEle);
		
	}
	
	
	public void addEarNature(org.w3c.dom.Document doc) throws Exception
	{
		Element docElement = doc.getDocumentElement();
		
		NodeList naturesNodeList = doc.getElementsByTagName("natures");
		
		Node naturesNode=naturesNodeList.item(0);
		
		Element natureEle = doc.createElement("nature");
		natureEle.appendChild(doc.createTextNode(EAR_FACET_NATURE));
		naturesNode.appendChild(natureEle);
		naturesNode.appendChild(doc.createTextNode("\n"));
		
		natureEle = doc.createElement("nature");
		natureEle.appendChild(doc.createTextNode(EAR_VALIDATION_NATURE));
		naturesNode.appendChild(natureEle);
		naturesNode.appendChild(doc.createTextNode("\n"));
		
		
	}
	
	private static String EAR_FACET_BUILDER="org.eclipse.wst.common.project.facet.core.builder";
	private static String EAR_VALIDATION_BUILDER="org.eclipse.wst.validation.validationbuilder";
	private static String EAR_FACET_NATURE="org.eclipse.wst.common.project.facet.core.nature";
	private static String EAR_VALIDATION_NATURE="org.eclipse.wst.common.modulecore.ModuleCoreNature";
	
	
	
}
