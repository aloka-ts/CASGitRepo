/*******************************************************************************
 *   Copyright (c) 2014 Agnity, Inc. All rights reserved.
 *   
 *   This is proprietary source code of Agnity, Inc. 
 *   
 *   Agnity, Inc. retains all intellectual property rights associated 
 *   with this source code. Use is subject to license terms.
 *   
 *   This source code contains trade secrets owned by Agnity, Inc.
 *   Confidentiality of this computer program must be maintained at 
 *   all times, unless explicitly authorized by Agnity, Inc.
 *******************************************************************************/
/*
 * Created on 26 Jan., 2006
 */
package com.baypackets.sas.ide.soa.views;

import java.net.InetAddress;
import java.util.ArrayList;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import java.util.StringTokenizer;
import com.baypackets.sas.ide.SasPlugin;
import com.baypackets.sas.ide.util.BPProjectINFO;
import com.baypackets.sas.ide.util.BPSASServicesNature;
import com.baypackets.sas.ide.util.GetStatusSAS;
import com.baypackets.sas.ide.util.IdeUtils;
import com.baypackets.sas.ide.util.StatusASE;
import com.baypackets.sas.ide.servicemanagement.BuildSARAction;

public class DeployServiceDialog extends Dialog 
{
	Object result;	
	private GetStatusSAS getSASStatus = null;
	private String serviceName = null;
	private String serviceVersion = null;
	private String servicePriority = null;
	private String SASAddress = null;
	private String remoteAddress=null;
	private int port = 0;
	
	private String serviceNameOld = null;
	private String serviceVersionOld = null;
	private String servicePriorityOld = null;
	
	
	private String pathSAR = null;
	private String ProjectName = null;
	
	private boolean IsCancelled = false;
	private Shell shell = null;
	
	private Text textServiceName = null;
	private Text textServicePriority = null;
	private Text textServiceVersion =null;	
	private Text textAdressSAS = null;
	private Button embeddedSAS = null;
	private Button runningInstanceSAS = null;	
	private Label AddressSAS = null;
	private Label DeployHostOption = null;
	private boolean buildSARRequired = false;
		
	private Button doOK =null;	
	private Button doCancel = null;
	private boolean complete = false;
	
	private Label title = null;
	private Label serviceNameLabel =null;
	private Label serviceVersionLabel = null;
	private Label servicePriorityLabel = null;	
	
	private CCombo ProjectList = null;
	private BPProjectINFO projectInfo = null;
	
	
	public DeployServiceDialog (Shell parent, int style) 
	{
		super (parent, style);
	}
	
	public DeployServiceDialog (Shell parent) 
	{
		this (parent, 0); // your default style bits go here (not the Shell's style bits)
	}
	public Object open () 
	{

		Shell parent = getParent();
		Shell shell = new Shell(parent);
		
		shell.setSize(400,500);
		shell.open();
		Display display = parent.getDisplay();
		StatusASE statusSAS  = StatusASE.getInstance();
		
		
		try
		{
						
			shell.setText("AGNITY CAS Services Deployment");			
			
			title = new Label(shell, SWT.NONE);
			title.setText("Enter the CAS Services Deployment Parameters");
			title.setSize(300,20);
			title.setLocation(60,30);	
			
			
			Label ProjectNameLabel = new Label(shell, SWT.None);
			ProjectNameLabel.setText("Select the Project");			
			ProjectNameLabel.setSize(150,20);
			ProjectNameLabel.setLocation(60,80);			
			
			
			ProjectList = new  CCombo(shell, SWT.DROP_DOWN|SWT.READ_ONLY); 
			ProjectList.setSize(150,20);
			ProjectList.setLocation(220,80);
	        ProjectList.setEnabled(true); 
	      
	        ArrayList listProjects =IdeUtils.getSOAProjects();
	        
	        if(listProjects==null){
	        	  SasPlugin.getDefault().log("No SOA projects found by DeployServiceDialog.java");
	        	return null;
	        }
	        
	        ProjectName = (String)listProjects.get(0);
	        
	        
	        
	        for(int i=0;i<listProjects.size();i++)
	        {
	        	ProjectList.add((String)listProjects.get(i));
	        }
	        
	        ProjectList.select(0);
	        ProjectList.addSelectionListener(new projectNameListener());
			
			
	        
			serviceNameLabel = new Label(shell, SWT.NONE);
			serviceNameLabel.setText("Service Name");
			serviceNameLabel.setSize(150,20);
			serviceNameLabel.setLocation(60,120);	
			
			textServiceName = new Text(shell, SWT.BORDER);
			textServiceName.setText("");
			textServiceName.setSize(150,20);
			textServiceName.setLocation(220,120);
			textServiceName.setEnabled(false);
			
			
			serviceVersionLabel = new Label(shell, SWT.NONE);
			serviceVersionLabel.setText("Service Version");
			serviceVersionLabel.setSize(150,20);
			serviceVersionLabel.setLocation(60,160);
			
			textServiceVersion = new Text(shell, SWT.BORDER);
			textServiceVersion.setText("");
			textServiceVersion.setSize(150,20);
			textServiceVersion.setLocation(220,160);
			textServiceVersion.setEnabled(false);
			
			servicePriorityLabel = new Label(shell, SWT.NONE);
			servicePriorityLabel.setText("Service Priority");
			servicePriorityLabel.setSize(150,20);
			servicePriorityLabel.setLocation(60,200);
			textServicePriority = new Text(shell, SWT.BORDER);
			textServicePriority.setText("");
			textServicePriority.setSize(150,20);
			textServicePriority.setLocation(220,200);
			textServicePriority.setEnabled(false);
			
			//reeta adding it
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
			  embeddedSAS.addSelectionListener(new EmbeddedSASListener());
			
			
			runningInstanceSAS =new Button(shell, SWT.RADIO);
			runningInstanceSAS.setText("Different Running Instance of CAS");			
			runningInstanceSAS.setSize(250,20);
			runningInstanceSAS.setLocation(60,260);
			if(statusSAS.getAttach()!=0){
			    runningInstanceSAS.setSelection(true);
			}else{
				runningInstanceSAS.setSelection(false);
			}
			
			runningInstanceSAS.addSelectionListener(new RunningSASListener());
			
			DeployHostOption = new Label(shell, SWT.NONE);
			DeployHostOption.setText("Host Address");
			DeployHostOption.setSize(80,20);
			DeployHostOption.setLocation(60,300);
			
			this.port = SasPlugin.getPORT();
		    int portSAS = statusSAS.getPORT();
			if(portSAS!=0)
				this.port = portSAS;
			this.SASAddress = statusSAS.getAddress();
			
			remoteAddress=this.SASAddress+":"+this.port;
			textAdressSAS = new Text(shell, SWT.BORDER);
			textAdressSAS.setSize(150,20);
			textAdressSAS.setLocation(200,300);
			
			if(statusSAS.getAttach()!=0){
			    textAdressSAS.setText(remoteAddress);
			}else {
				textAdressSAS.setText("localhost");
			}
			textAdressSAS.setEnabled(false);
			
			//
			 fillServiceParameters();
			 

			
			doOK = new Button(shell, SWT.PUSH);
			doOK.setText("Deploy ");
			doOK.setSize(74,25);
			doOK.setLocation(220,360);
			doOK.addMouseListener(new DoOKListener());
			
			
			doCancel = new Button(shell, SWT.PUSH);
			doCancel.setText("Cancel");
			doCancel.setSize(74,25);
			doCancel.setLocation(296,360);
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

		
		
		
		
		
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) display.sleep();
		}
		return result;
	}
	

    private class DoOKListener implements MouseListener    
    {

    	public void mouseDown(MouseEvent e)
    	{
    		if(buildSARRequired)
    		{
    			String[] buttontxt = new String[]{"OK"};
    			MessageDialog messageBox = new MessageDialog(shell,"Service Deployment", null,"Build the SAR File before deployment" , MessageDialog.INFORMATION, buttontxt,0);
    			messageBox.open();
    			complete = true;
    			IsCancelled = true;
    			return;
    			
    		}
    		setPageComplete(validatePage());
    	}

    	public void mouseUp(MouseEvent e)
    	{
    		if(buildSARRequired)
    		{
    			String[] buttontxt = new String[]{"OK"};
    			MessageDialog messageBox = new MessageDialog(shell,"Service Deployment", null,"Build the SAR File before deployment" , MessageDialog.INFORMATION, buttontxt,0);
    			messageBox.open();
    			complete = true;
    			IsCancelled = true;
    			return;
    			
    		}
    		setPageComplete(validatePage());
    	}
    
    	public void mouseDoubleClick(MouseEvent e)
    	{
    		setPageComplete(validatePage());
    	
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
    	
    	
    	serviceName = textServiceName.getText().trim();
    	serviceVersion = textServiceVersion.getText().trim();
    	servicePriority = textServicePriority.getText().trim();
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
    		this.complete = true;
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
    	shell.dispose();
    	
    }
    
    
	private class projectNameListener implements SelectionListener
	{

	    public void widgetDefaultSelected(SelectionEvent e)
	    {
	  
	    	String selectedItem = ProjectList.getText().trim();
	    	if(selectedItem!=null)
	    	{
	    	
	    		ProjectName = selectedItem;
	    		fillServiceParameters();
	    	}
	    	
	    	//fillServiceParameters();
	    	
	    	
	    	
	    	
	    	
	    }

	    public void widgetSelected(SelectionEvent e)
	    {
	    	
	    	String selectedItem = ProjectList.getText().trim();
	    	
	    	if(selectedItem!=null)
	    	{
	    	
	    		ProjectName = selectedItem;
	    		fillServiceParameters();
	    	}
	    	
	    	
	  
	    	
	    }

	    projectNameListener()
    {
    }
    
	}
	
	private void fillServiceParameters()
	{
		projectInfo = BPProjectINFO.getInstance();
		buildSARRequired = false;
		
		if(!projectInfo.initialize(ProjectName))
		{
			
			
			buildSARRequired = true;
			textServiceName.setText("");
                	textServicePriority.setText("");
                	textServiceVersion.setText("");

			
			return;
			
		}
		
		serviceNameOld = projectInfo.getApplicationName(ProjectName);
		serviceVersionOld = projectInfo.getApplicationVersion(ProjectName);
		servicePriorityOld = projectInfo.getApplicationPriority(ProjectName);
		pathSAR = projectInfo.getApplicationPath(ProjectName);
		
		textServiceName.setText(serviceNameOld);
		textServicePriority.setText(servicePriorityOld);
		textServiceVersion.setText(serviceVersionOld);

		
	}
	
	
	 private class RunningSASListener implements SelectionListener
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

	    	RunningSASListener()
	    	{
	    	}
	    }
	    
		   
	    private class EmbeddedSASListener implements SelectionListener
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
	    	EmbeddedSASListener()
	    	{
	    	}
	    }
	    
	    
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
	    
	    
	    
	  
	
	public String  getServiceName()
	{
		return serviceName;
	}
	
	public String  getServiceVersion()
	{
		return serviceVersion;
	}
	
	public String  getServicePriority()
	{
		return servicePriority;
	}

	public String getServicePath()
	{
		return pathSAR;
	}
	
	public String getAddressOfSAS()
	{
		return this.SASAddress;
	}
	public int getPORT()
	{
		return port;
	}
  
 }
