package com.baypackets.sas.ide.alc;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL; //import java.util.ArrayList;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Properties;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.ProjectHelper;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
//import org.eclipse.debug.ui.console.IConsole;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.search.IJavaSearchConstants;
import org.eclipse.jdt.core.search.SearchPattern;
import org.eclipse.jdt.launching.IVMInstall;
import org.eclipse.jdt.launching.JavaRuntime;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IActionDelegate;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;

import com.baypackets.sas.ide.SasPlugin;
import com.baypackets.sas.ide.util.IdeUtils;

public class BuildAlcExtAction implements IObjectActionDelegate {

	private IWorkbenchWindow window;
	private ISelection selection;
	private static final String ALCMC_HOME = "resources/alc/alcml";
	private static final String BUILD_XML_FOR_USER_EXT = "resources/alc/build-user-extensions.xml";
	IWorkbenchPart activePart;
	boolean active = true;

	Shell shell = null;

	protected String processMessage = " Build In Progress ...............";// procress
																		// info
	protected String shellTitle = "Building User Defined ALC Extensions "; //
	protected int processBarStyle = SWT.SMOOTH; // process bar style
	Shell parentShell = null;
	private Image processImage = SasPlugin.getImageDescriptor(
			"/icons/buildSAR.gif").createImage();

	public BuildAlcExtAction() {
		super();
	}

	public void init(IWorkbenchWindow window) {
		this.window = window;
		SasPlugin.getDefault().log("The init() of BuildAlcExtAction ");
	}

	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
		activePart = targetPart;
	}

	public void run(IAction action) {
		parentShell = activePart.getSite().getShell();
		final Display display = parentShell.getDisplay();
		shell = new Shell(display);
		final GridLayout gridLayout = new GridLayout();
		gridLayout.verticalSpacing = 10;

		shell.setLayout(gridLayout);
		shell.setSize(400, 100);

		shell.setText(shellTitle);

		final Composite composite = new Composite(shell, SWT.NONE);
		composite.setLayoutData(new GridData(GridData.FILL, SWT.CENTER, true,
				false));
		composite.setLayout(new GridLayout());

		final CLabel message = new CLabel(composite, SWT.NONE);
		message.setImage(processImage);
		GridData gd=new GridData(GridData.FILL, SWT.CENTER | SWT.BOLD, true, false);
		gd.verticalIndent=10;
		gd.horizontalIndent=30;
		message.setLayoutData(gd);
		message.setText(processMessage);

		shell.open();
		shell.layout();
		this.executeBuildFile();
		if (success || getError()) {
			shell.close();
		}

	}

	/**
	 * @see IActionDelegate#selectionChanged(IAction, ISelection)
	 */
	public void selectionChanged(IAction action, ISelection selection) {
		this.selection = selection;

	}

	private IPath getASEHOMEVar() {

		IPath pathASEHOME = JavaCore.getClasspathVariable("ASE_HOME");

		if (pathASEHOME == null) {

			String envVariable = System.getenv("ASE_HOME");
			SasPlugin.getDefault().log(
					"The ASE_HOME path..env variable is " + envVariable);

			if (envVariable != null) {

				pathASEHOME = new Path(envVariable);

				try {
					SasPlugin.getDefault().log(
							"The ASE_HOME Classpath Variable as it was not set "
									+ pathASEHOME);
					JavaCore
							.setClasspathVariable("ASE_HOME", pathASEHOME, null);
				} catch (JavaModelException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return pathASEHOME;
	}

	public void executeBuildFile() {
		SasPlugin.getDefault()
				.log(
						"The run() of BuildAlcExtAction  the selection is "
								+ selection);
		SasPlugin.getDefault().log(
				"The run() of BuildAlcExtAction  the active part is "
						+ activePart);
		String tx[] = null;
		MessageDialog ms = null;
		Shell shell = parentShell;

		if (selection instanceof IStructuredSelection) {

			for (Iterator it = ((IStructuredSelection) selection).iterator(); it
					.hasNext();) {
				Object element = it.next();
				IProject project = null;

				if (element instanceof IProject) {

					SasPlugin.getDefault().log(
							"The run() of The slection is an Alc project ");

					IProject prj = (IProject) element;

					if (!this.findAlcInterfaceImplementors(prj)) {
						this.isError = true;
						tx = new String[] { "OK" };
						ms = new MessageDialog(
								shell,
								"Building User Defined ALC Extensions",
								null,
								"No .java file extending ALCServiceInterfaceImpl found !!!",
								MessageDialog.ERROR, tx, 0);
						ms.open();
						return;
					}

					try {
						prj.build(IncrementalProjectBuilder.FULL_BUILD, null);
					} catch (CoreException e2) {
						// TODO Auto-generated catch block
						this.isError = true;
						tx = new String[] { "OK" };
						ms = new MessageDialog(shell,
								"Building User Defined ALC Extensions", null,
								e2.getMessage(), MessageDialog.ERROR, tx, 0);
						ms.open();
						return;
					}
					IFile file = prj.getFile("build.properties");
					String binfolder = null;
					// finding the location of the output folder for the src
					// which is usually bin
					if (file.exists()) {

						File property = Platform.getLocation().append(
								file.getFullPath()).toFile();
						Properties buildProperties = new Properties();
						InputStream stream = null;
						try {
							stream = new FileInputStream(property);
							buildProperties.load(stream);
							binfolder = buildProperties.getProperty("output");

							if (binfolder != null && !binfolder.equals("")) {
								binfolder = Platform.getLocation().append(
										prj.getFullPath()).append(binfolder)
										.toOSString();
							}
							SasPlugin.getDefault().log(
									"The output property from the build.properties is "
											+ binfolder);
						} catch (FileNotFoundException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

					}

					URL buildxmlFile = SasPlugin.getDefault().getBundle()
							.getEntry(this.BUILD_XML_FOR_USER_EXT);
					String buildfilepath = null;
					if (buildxmlFile != null) {
						try {
							buildfilepath = new Path(Platform.resolve(buildxmlFile)
									.getPath()).toOSString();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							this.isError = true;
							tx = new String[] { "OK" };
							ms = new MessageDialog(shell,
									"Building User Defined ALC Extensions",
									null, e.getMessage(), MessageDialog.ERROR,
									tx, 0);
							ms.open();
							return;
						}
					} else {
						this.isError = true;
						tx = new String[] { "OK" };
						ms = new MessageDialog(shell,
								"Building User Defined ALC Extensions", null,
								"build.xml not found ", MessageDialog.ERROR,
								tx, 0);
						ms.open();
						return;
					}

					// IFile buildfFile = prj.getFile("build.xml");
					//
					// if (!buildfFile.exists()) {
					// tx= new String[]{"OK"};
					// ms = new MessageDialog(activePart.getSite().getShell(),
					// "Building User Defined ALC Extensions" ,null,"build.xml
					// not found ", MessageDialog.ERROR, tx,0);
					// ms.open();
					// return;
					//	
					// } else {

					String alcmlcPath = null;
					IPath projectPath = Platform.getLocation().append(
							prj.getFullPath().toString());
					// String buildfilepath =
					// Platform.getLocation().append(buildfFile.getFullPath()).toOSString();

					try {
						URL alcmlcURL = SasPlugin.getDefault().getBundle()
								.getEntry(this.ALCMC_HOME);
						if (alcmlcURL != null) {
							alcmlcPath = new Path(Platform.resolve(alcmlcURL).getPath()).toOSString();
							SasPlugin.getDefault().log(
									"The ALCML_HOME is..." + alcmlcPath);
						} else {
							this.isError = true;
							tx = new String[] { "OK" };
							ms = new MessageDialog(shell,
									"Building User Defined ALC Extensions",
									null, "ALCML_HOME is not resolved",
									MessageDialog.ERROR, tx, 0);
							ms.open();
							return;
						}
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}

					String srcdir = projectPath.append("src").toOSString();
					String xsdoutDir = projectPath.append("src").append(
							"alcmltemp").toOSString();

					SasPlugin.getDefault().log(
							"The Build file path is..." + buildfilepath
									+ "Project Name is " + prj.getName());

					File buildFile = new File(buildfilepath);

					Project p = new Project();
					p.setUserProperty("ant.file", buildfilepath);
					p.setProperty("implPath", srcdir);
					p.setProperty("xsdOutputDir", xsdoutDir);
					p.setProperty("ALCML_HOME", alcmlcPath);
					p.setBasedir(projectPath.toOSString());

					IVMInstall jre = JavaRuntime.getDefaultVMInstall();
					File jdkHome = jre.getInstallLocation();
					p.setProperty("JAVA_HOME", jdkHome.getAbsolutePath());

					URL lib = SasPlugin.getDefault().getBundle().getEntry(
							"library");

					if (lib != null) {
						String libPath;
						try {
							libPath = Platform.resolve(lib).getPath();
							p.setProperty("plugin_lib", libPath);
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

					} else {
						this.isError = true;
						tx = new String[] { "OK" };
						ms = new MessageDialog(shell,
								"Building User Defined ALC Extensions", null,
								"There is no library directory in plugin",
								MessageDialog.ERROR, tx, 0);
						ms.open();
						return;
					}

					if (binfolder != null && !binfolder.equals("")) {
						p.setProperty("binFolder", binfolder);
					} else {
						String binDir = projectPath.append("bin").toOSString();
						p.setProperty("binFolder", binDir);
					}

					IPath path = getASEHOMEVar();

					SasPlugin.getDefault().log("ASE_HOME  path is " + path);
					SasPlugin.getDefault().log(
							"ImplPath Property is.." + srcdir);
					SasPlugin.getDefault().log(
							"xsdOutputDir Property is.." + xsdoutDir);
					SasPlugin.getDefault().log(
							"binFolder Property is.."
									+ p.getProperty("binFolder"));

					if (path != null) {
						p.setProperty("ASE_HOME", path.toOSString());
					} else {
						this.isError = true;
						tx = new String[] { "OK" };
						ms = new MessageDialog(shell,
								"Building User Defined ALC Extensions", null,
								"ASE_HOME Variable not set !!!! ",
								MessageDialog.ERROR, tx, 0);
						ms.open();
						return;
					}
					p.setProperty("PROJECT_NAME", prj.getName());

//					 DefaultLogger consoleLogger = new DefaultLogger();
//					 consoleLogger.setErrorPrintStream(System.err);
//					 consoleLogger.setOutputPrintStream(System.out);
//					 consoleLogger.setMessageOutputLevel(Project.MSG_INFO);
//					 p.addBuildListener(consoleLogger);

					try {
						p.fireBuildStarted();
						p.init();

						ProjectHelper helper = ProjectHelper.getProjectHelper();
						p.addReference("ant.projectHelper", helper);
						helper.parse(p, buildFile);
						p.executeTarget(p.getDefaultTarget());
						p.executeTarget("echo");
						p.fireBuildFinished(null);
						this.success = true;
					} catch (BuildException e) {
						p.fireBuildFinished(e);
						this.isError = true;
						SasPlugin.getDefault().log(
								"The run() of BuildAlcExtAction  BuildException thrown is"
										+ e);
						tx = new String[] { "OK" };
						ms = new MessageDialog(shell,
								"Building User Defined ALC Extensions", null,
								"BuildException thrown : " + e.getMessage(),
								MessageDialog.ERROR, tx, 0);
						ms.open();
						return;
					}

				}

			}

		}
		if (this.success) {

			tx = new String[] { "OK" };
			ms = new MessageDialog(activePart.getSite().getShell(),
					"Building User Defined ALC Extensions", null,
					"User Defined ALC Extensions built Successfully !!!",
					MessageDialog.INFORMATION, tx, 0);
			ms.open();
		}
		SasPlugin.getDefault().log("Exiting run Successfully");

		// }

	}

	boolean getError() {
		return isError;
	}

	private boolean findAlcInterfaceImplementors(IProject project) {
		ArrayList services = new ArrayList();
		IdeUtils.getClassNames(project, SEARCH_PATTERN, services);
		if (services.isEmpty()) {
			// services =IdeUtils.getALCInterfaceImplementors(project);
			SasPlugin.getDefault().log(
					"The services interface loaded are.." + services.size());
			return false;
		} else {
			return true;
		}

	}

	private boolean isError = false;
	private boolean success = false;
	private static final String SUPER_CLASS_NAME = "com.genband.ase.alc.alcml.ALCServiceInterface.ALCServiceInterfaceImpl"
			.intern();
	private static SearchPattern SEARCH_PATTERN = SearchPattern.createPattern(
			SUPER_CLASS_NAME, IJavaSearchConstants.CLASS,
			IJavaSearchConstants.REFERENCES, SearchPattern.R_EXACT_MATCH);
	private static final String CONSOLE_NAME = "ALC_BUILD_CONSOLE";

}
