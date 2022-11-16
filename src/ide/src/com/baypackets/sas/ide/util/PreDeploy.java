package com.baypackets.sas.ide.util;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.StringTokenizer;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.ElementListSelectionDialog;

import com.baypackets.sas.ide.SasPlugin;

/**
 * This class collects all the information required to deploy an application
 * ApplicationName, ApplicationPriority, ApplicationVersion and Host of the SAS.
 * @author eclipse
 *
 */

public class PreDeploy  
{
	private CCombo ProjectList = null;
	private String ProjectName = null;
	private String serviceName = null;
	private String serviceVersion = null;
	private String servicePriority = null;
	private String SASAddress = null;
	private String remoteAddress=null;
	private boolean IsCancelled = true;
	private Shell shell = null;
	private Text textServiceName = null;
	private Text textServicePriority = null;
	private Text textServiceVersion =null;	
	private Text textAdressSAS = null;
	private Button embeddedSAS = null;
	private Button runningInstanceSAS = null;	
	private Button doOK =null;	
	private Button doCancel = null;
	private boolean complete = false;
	private Display display = null;
	private Label title = null;
	private Label serviceNameLabel =null;
	private Label serviceVersionLabel = null;
	private Label servicePriorityLabel = null;	
	private Label AddressSAS = null;
	private Label DeployHostOption = null;
	private int port = 0;
	public PreDeploy(String name, String version, String spri, Shell shell) 
	{
		this.serviceName = name;
		this.serviceVersion = version;
		this.servicePriority = spri;
		this.shell = shell;
		this.initialize();		
	}
	
	public PreDeploy(Shell shell) 
	{
		this.shell = shell;
		this.initialize();		
	}
	
	public String getName()
	{
		return serviceName;
	}
	public String getPriority()
	{
		return servicePriority;
	}
	
	public String getVersion()
	{
		return serviceVersion;
	}
	
	
	public String getAddressOfSAS()
	{
		return this.SASAddress;
	}
	
	public String getProjectName()
	{
		return this.ProjectName;
	}
	
	
	public void initialize()
	{
		try
		{
			//reeta modified this method
			StatusASE statusSAS  = StatusASE.getInstance();
			display = Display.getDefault();
			shell = new Shell(display);
			shell.setSize(400,500); 
			
			shell.open();			
			shell.setText("AGNITY CAS Services Deployment");			
			
			title = new Label(shell, SWT.NONE);
			title.setText("Enter the CAS Services Deployment Parameters");
			title.setSize(300,20);
			title.setLocation(60,30);	
			
			
			Label ProjectNameLabel = new Label(shell, SWT.None);
			ProjectNameLabel.setText("Select the Project");			
			ProjectNameLabel.setSize(150,20);
			ProjectNameLabel.setLocation(60,80);			
			
			
			ProjectList = new  CCombo(shell, SWT.DROP_DOWN|SWT.READ_ONLY|SWT.BORDER); 
			ProjectList.setSize(150,20);
			ProjectList.setLocation(220,80);
	        ProjectList.setEnabled(true);        
	        ArrayList listProjects = IdeUtils.getAllProjects();
	        
	        ProjectName = (String)listProjects.get(0);
	        
	        
	        
	        for(int i=0;i<listProjects.size();i++)
	        {
	        	ProjectList.add((String)listProjects.get(i));
//	        	   ProjectList.select(0);
	        }
	        
	     
	        ProjectList.addSelectionListener(new ProjectNameListener());
			serviceNameLabel = new Label(shell, SWT.NONE);
			serviceNameLabel.setText("Service Name");
			serviceNameLabel.setSize(150,20);//making 150 inplace of 100
			serviceNameLabel.setLocation(60,120);	
		
			textServiceName = new Text(shell, SWT.BORDER);
			textServiceName.setText("");
			textServiceName.setSize(150,20);
			textServiceName.setLocation(220,120);
			textServiceName.setEditable(false);
			
			serviceVersionLabel = new Label(shell, SWT.NONE);
			serviceVersionLabel.setText("Service Version");
			serviceVersionLabel.setSize(150,20);
			serviceVersionLabel.setLocation(60,160);

			textServiceVersion = new Text(shell, SWT.BORDER);
			textServiceVersion.setText("");
			textServiceVersion.setSize(150,20);
			textServiceVersion.setLocation(220,160);
			textServiceVersion.setEditable(false);
			
			servicePriorityLabel = new Label(shell, SWT.NONE);
			servicePriorityLabel.setText("Service Priority");
			servicePriorityLabel.setSize(150,20);
			servicePriorityLabel.setLocation(60,200);

			textServicePriority = new Text(shell, SWT.BORDER);
			textServicePriority.setText("");
			textServicePriority.setSize(150,20);
			textServicePriority.setLocation(220,200);
			textServicePriority.setEditable(false);

			
			AddressSAS = new Label(shell, SWT.NONE);			
			AddressSAS.setText("Deploy Service On");
			AddressSAS.setSize(120,20);
			AddressSAS.setLocation(60,200);
			
						
			embeddedSAS =new Button(shell, SWT.RADIO);
			embeddedSAS.setText("CAS Embedded within the IDE");			
			embeddedSAS.setSize(250,20);
			embeddedSAS.setLocation(60,230);
			if(statusSAS.getAttach()!=0){
				  embeddedSAS.setSelection(false);
				 }else{
			      embeddedSAS.setSelection(true);
		    }
			
			embeddedSAS.addSelectionListener(new DoEmbeddedSASListener());
			
			
			runningInstanceSAS =new Button(shell, SWT.RADIO);
			runningInstanceSAS.setText("Different Running Instance of CAS");			
			runningInstanceSAS.setSize(250,20);
			runningInstanceSAS.setLocation(60,260);
			if(statusSAS.getAttach()!=0){
				runningInstanceSAS.setSelection(true);
		    }else{
			    runningInstanceSAS.setSelection(false);
			}
			runningInstanceSAS.addSelectionListener(new DoRunningSASListener());
			
			DeployHostOption = new Label(shell, SWT.NONE);
			DeployHostOption.setText("Host Address");
			DeployHostOption.setSize(80,20);
			DeployHostOption.setLocation(60,300);
			
			textAdressSAS = new Text(shell, SWT.BORDER);
			this.port = statusSAS.getPORT();
			this.SASAddress = statusSAS.getAddress();
			remoteAddress=this.SASAddress+":"+this.port;
			if(statusSAS.getAttach()!=0){
			    textAdressSAS.setText(remoteAddress);
			}else {
				textAdressSAS.setText("localhost");
			}
			textAdressSAS.setSize(150,20);
			textAdressSAS.setLocation(200,300);
			
			textAdressSAS.setEnabled(false);
			
//				 fillServiceParameters();
			
			doOK = new Button(shell, SWT.PUSH);
			doOK.setText("Deploy");
			doOK.setSize(74,25);
			doOK.setLocation(200,360);
			doOK.addMouseListener(new DoOKListener());
			
			
			doCancel = new Button(shell, SWT.PUSH);
			doCancel.setText("Cancel");
			doCancel.setSize(74,25);
			doCancel.setLocation(276,360);
			doCancel.addMouseListener(new DoCancelListener());
				
			while(!shell.isDisposed())
			{
				if(!display.readAndDispatch())
				{
					display.sleep();
				}
				if(this.getPageComplete())
				{
					shell.close();
					shell.dispose();
				}
				
			}

			 
			    
		}
		catch(Exception e)
		{
			SasPlugin.getDefault().log(e.getMessage(), e);
		}
	}
	
	
	public String displayAndSelectAlcmlFolders(final String[] folders){
		
		String alcmlFolder=null;
		ElementListSelectionDialog  dialog = new ElementListSelectionDialog(shell,new LabelProvider());
		dialog.setTitle("Select Alcml Application");
		dialog.setElements(folders);
		
		int buttClicked = dialog.open();
		
		if(buttClicked == ElementListSelectionDialog.OK){
			Object[] results = dialog.getResult();
			alcmlFolder = (String)results[0];
			}
		
		
		return alcmlFolder;
	}
	
	
	public String getRemoteAlcmlFilePath(){
		
		InputDialog input = new InputDialog(shell, "Remote Dir", "Enter Remote Dir", "", null);		
		int result = input.open();
		if(result == InputDialog.OK)
		 return input.getValue();
		
		return null;
	}
	
    private class DoOKListener implements MouseListener    
    {

    	public void mouseDown(MouseEvent e)
    	{
    		setPageComplete(validatePage());
    		IsCancelled = false;

    	}

    	public void mouseUp(MouseEvent e)
    	{
    		setPageComplete(validatePage());
    		IsCancelled = false;
    	}
    
    	public void mouseDoubleClick(MouseEvent e)
    	{
    		setPageComplete(validatePage());
    		IsCancelled = false;
    	
    	}

    	DoOKListener()
    	{
    	}
    
    }
    
    private class DoCancelListener implements MouseListener    
    {

    	public void mouseDown(MouseEvent e)
    	{
    		setPageComplete(true);
    		IsCancelled = true;
    		
    		
    	}

    	public void mouseUp(MouseEvent e)
    	{
    		setPageComplete(true);
    		IsCancelled = true;
    		
    	}
    
    	public void mouseDoubleClick(MouseEvent e)
    	{
    		setPageComplete(true);
    		IsCancelled = true;
    	
    	}

    	DoCancelListener()
    	{
    	}
    
    }
    
    public void setPageComplete(boolean flag)
    {
    	this.complete = flag;
    	
    }
    
    public boolean isCancelled()
    {
    	return this.IsCancelled;
    }
    
    public boolean getPageComplete()
    {
    	return this.complete;
    }
    
    public boolean validatePage()
    {
    	if(textServiceVersion.getText().trim().length()==0)
    		return false;
    	if(textServicePriority.getText().trim().length()==0)
    		return false;
    	
    	if(textServiceName.getText().trim().length()==0)
    		return false;
    	
    	if(textAdressSAS.getText().trim().length()==0)
    		return false;
    	this.serviceName = textServiceName.getText().trim();
    	this.serviceVersion = textServiceVersion.getText().trim();
    	this.servicePriority = textServicePriority.getText().trim();
    	String addresss = textAdressSAS.getText().trim().toString();
	String address = null;
	if(addresss.indexOf(":")>0)
		address = takeInput(addresss);
	else
		address = addresss;

		String addrs = null;
		
		try
		{
			addrs = InetAddress.getByName(address).toString();
			
			if(addrs==null)
			{
				return false;
			}
			else
			{
				
				int index = addrs.lastIndexOf('/');
				
				SASAddress = addrs.substring(index+1);
			}
		}
		catch(Exception e)
		{
			return false;
			
		}
    	return true;
    }
    
    public void setDispose()
    {
    	
    	textServiceName.dispose();
    	textServicePriority.dispose();
    	
    	textServiceVersion.dispose();
    	
    	doOK.dispose();
    	title.dispose();
    	serviceNameLabel.dispose();
    	serviceVersionLabel.dispose();
    	servicePriorityLabel.dispose();
    	AddressSAS.dispose();
    	DeployHostOption.dispose();
    	
    	embeddedSAS.dispose();
    	runningInstanceSAS.dispose();
    	shell.dispose();
    	
    }
    
	   //reeta modified it
    private class DoRunningSASListener implements SelectionListener
    {

    	public void widgetDefaultSelected(SelectionEvent e)
    	{
    		if(runningInstanceSAS.getSelection())
    		{
    			textAdressSAS.setText(remoteAddress);
    			textAdressSAS.setEnabled(true);
    		   	embeddedSAS.setSelection(false);
    		}
    	
         }

    	public void widgetSelected(SelectionEvent e)
    	{
    		if(runningInstanceSAS.getSelection())
    		{
    			textAdressSAS.setText(remoteAddress);
    			textAdressSAS.setEnabled(true);
    		   	embeddedSAS.setSelection(false);    			
    		}
        
    	}

    	DoRunningSASListener()
    	{
    	}
    }
    
	   
    private class DoEmbeddedSASListener implements SelectionListener
    {
    	public void widgetDefaultSelected(SelectionEvent e)
    	{
    		if(embeddedSAS.getSelection())
    		{
    			textAdressSAS.setText("localhost");
    			textAdressSAS.setEnabled(false);
			    runningInstanceSAS.setSelection(false);
    		}
         }

    	public void widgetSelected(SelectionEvent e)
    	{
    		if(embeddedSAS.getSelection())
    		{
    			textAdressSAS.setText("localhost");
    			textAdressSAS.setEnabled(false);
			    runningInstanceSAS.setSelection(false);
    		}
        
    	}
    	DoEmbeddedSASListener()
    	{
    	}
    }

    //reeta added it
//    private ArrayList getAllProjects()
//	{
//		ArrayList listOfProjects = new ArrayList();
//		IProject [] projects =ResourcesPlugin.getWorkspace().getRoot().getProjects();		
//		ArrayList builtProject=BuildSARAction.getBuiltProjects();
//		if(builtProject.size()!=0){
//		   for(int k=builtProject.size()-1;k>=0;k--){
//			  listOfProjects.add(builtProject.get(k));
//		   }
//		}
//		for(int i=0;i<projects.length;i++)
//		{
//			try
//			{
//				if(projects[i].isOpen())
//				{
//					if(listOfProjects.indexOf(projects[i].getName())==-1)
//					    listOfProjects.add(projects[i].getName());
//				}
//			}
//			catch(Exception e)
//			{
//				SasPlugin.getDefault().log(e.getMessage(), e);
//			}
//		}
//		return listOfProjects;
//		
//	}
    
    
    
    
    //reeta added it
	private String takeInput(String inputAddressString)
	{
                StringTokenizer tokenizer = new StringTokenizer(inputAddressString, ":");
		String enterHost = null;
		
                while(tokenizer.hasMoreTokens())
                {
                        enterHost = tokenizer.nextToken();
                        port = Integer.parseInt(tokenizer.nextToken());

                        break;
                }
		return enterHost;

	}
	
	private class ProjectNameListener implements SelectionListener
	{

	    public void widgetDefaultSelected(SelectionEvent e)
	    {
	    	fillParameters();
	    }

	    public void widgetSelected(SelectionEvent e)
	    {
	    	fillParameters();
	    	
	    }
	    
	    private void fillParameters(){

	    	
	    	String selectedItem = ProjectList.getText().trim();
	    	
	    	if(selectedItem!=null)
	    	{
	    	
	    		ProjectName = selectedItem;
	    		if(isVtpProject(ProjectName)){
	    			fillServiceParameterForALcmlApp();
	    		} else {
	    		fillServiceParameters();
	    		}
	    	}
	   
	    }
	    
	    private boolean isVtpProject(String projectName){
	    	try {
				return ResourcesPlugin.getWorkspace().getRoot().getProject(projectName).getDescription().hasNature(BPSASServicesNature.VTP_NATURE_ID);
			} catch (CoreException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return false;
	    }
	    

	    ProjectNameListener()
    {
    }
    
	}
//	reeta added it
	private void fillServiceParameters()
	{
		textServiceName.setEditable(false);
		textServicePriority.setEditable(false);
		textServiceVersion.setEditable(false);
		BPProjectINFO projectInfo = BPProjectINFO.getInstance();
		if(!projectInfo.initialize(ProjectName))
		{
			textServiceName.setText("");
            textServicePriority.setText("");
            textServiceVersion.setText("");
            return;
			
		}
		
		this.serviceName = projectInfo.getApplicationName(ProjectName);
		this.serviceVersion = projectInfo.getApplicationVersion(ProjectName);
		this.servicePriority = projectInfo.getApplicationPriority(ProjectName);
		
		
		textServiceName.setText(serviceName);
		textServicePriority.setText(servicePriority);
		textServiceVersion.setText(serviceVersion);

		
	}
	
	//rohit added
	private void fillServiceParameterForALcmlApp(){
		
		
		
		textServiceName.setEditable(true);
		textServicePriority.setEditable(true);
		textServiceVersion.setEditable(true);
	}
	

	public int getPORT()
	{
		return port;
	}

	

}
