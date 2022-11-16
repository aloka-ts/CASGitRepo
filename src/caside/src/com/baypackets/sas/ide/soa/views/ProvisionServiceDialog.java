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
package com.baypackets.sas.ide.soa.views;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Event;
import com.baypackets.sas.ide.SasPlugin;

public class ProvisionServiceDialog extends Dialog{
	
	
	public Shell shell=null;
	
	public ProvisionServiceDialog (Shell parent) 
	{
		super (parent);
	}
	
	String buttonName="Provision";
	
	
	
	/**
	 * Override to set the title of the dialog.
	 */
	
	public Object open () 
	{
		Object result=null;	
		Shell parent = getParent();
		shell = new Shell(parent);
		
		shell.setSize(420,200);
		shell.open();
		Display display = parent.getDisplay();
		
		shell.setText("AGNITY Remote SOA Service Provisioning");			
			
			Label title = new Label(shell, SWT.NONE);
			title.setText("Service Name:");
			title.setSize(80,20);
			title.setLocation(20,30);
			
			final Text textServiceName = new Text(shell, SWT.BORDER);
			textServiceName.setText("");
			textServiceName.setSize(200,20);
			textServiceName.setLocation(195,30);
			textServiceName.setEnabled(true);
			textServiceName.setText(serviceName);
			textServiceName.addListener(SWT.Modify, new Listener(){
				public void handleEvent(Event e) {
					serviceName=textServiceName.getText();
					validateDialog();
				}
			});
			
			
			final Label serviceVersionLabel = new Label(shell, SWT.NONE);
			serviceVersionLabel.setText("Service Version:");
			serviceVersionLabel.setSize(170,20);
			serviceVersionLabel.setLocation(20,60);
			
			
			final Text serviceVersion = new Text(shell, SWT.BORDER);
			serviceVersion.setText("");
			serviceVersion.setSize(200,20);
			serviceVersion.setLocation(195,60);
			serviceVersion.setEnabled(true);
			serviceVersion.setText(serviceVer);
			serviceVersion.addListener(SWT.Modify, new Listener(){
				public void handleEvent(Event e) {
					serviceVer=serviceVersion.getText();
					validateDialog();
				}
			});
			
			
			
			final Label serviceNameLabel = new Label(shell, SWT.NONE);
			serviceNameLabel.setText("Service WSDL URL(file or http):");
			serviceNameLabel.setSize(170,20);
			serviceNameLabel.setLocation(20,90);
			
			
			final Text serviceLocation = new Text(shell, SWT.BORDER);
			serviceLocation.setText("");
			serviceLocation.setSize(200,20);
			serviceLocation.setLocation(195,90);
			serviceLocation.setEnabled(true);
			serviceLocation.setText(serviceWSDLLocation);
			serviceLocation.addListener(SWT.Modify, new Listener(){
				public void handleEvent(Event e) {
					serviceWSDLLocation=serviceLocation.getText();
					validateDialog();
				}
			});
			
			
			doOK = new Button(shell, SWT.PUSH);
			
			if(this.isupdate){
			  doOK.setText("Update");
			}else{
				doOK.setText("Provision");
			}
			doOK.setSize(74,25);
			doOK.setLocation(120,130);
			doOK.addListener(SWT.Selection, new Listener(){
				public void handleEvent(Event e) {
					IsOKPressed=true;
					SasPlugin.getDefault().log("doOK of Provisioin dialog Pressed");
					textServiceName.dispose();
					serviceNameLabel.dispose();
					serviceLocation.dispose();
					doOK.dispose();
					shell.close();
					shell.dispose();
				}
			});
			
			
			final Button doCancel = new Button(shell, SWT.PUSH);
			doCancel.setText("Cancel");
			doCancel.setSize(74,25);
			doCancel.setLocation(200,130);
			doCancel.addListener(SWT.Selection, new Listener(){
				public void handleEvent(Event e) {
					IsCancelled = true;
					textServiceName.dispose();
					serviceNameLabel.dispose();
					serviceLocation.dispose();
					doOK.dispose();
					doCancel.dispose();
					shell.close();
					shell.dispose();
				}
			});
			validateDialog();
			while (!shell.isDisposed()) {
				if (!display.readAndDispatch()) display.sleep();
			}
			return result;
			
			
		 }
	
	  private void validateDialog(){
	    	
	    	if(this.serviceName==null||this.serviceName.equals("")||
	    	   this.serviceWSDLLocation==null||this.serviceWSDLLocation.equals("")||
	    	   this.serviceVer==null||this.serviceVer.equals("")){
	    		doOK.setEnabled(false);
	    	}else{
	    		doOK.setEnabled(true);
	    	}
	    	
//	    	SasPlugin.getDefault().log("The ServiceName :"+this.serviceName +" The location is :"+ this.serviceWSDLLocation+" Service Version:"+serviceVer );
	    }

	 public boolean isCancelled()
	    {
	    	return this.IsCancelled;
	    }
	 
	 public boolean okPressed()
	    {
	    	return this.IsOKPressed;
	    }
	 
	 public String getServiceName()
	    {
	    	return this.serviceName;
	    }
	 
	 public String getServiceWSDLLocation()
	    {
	    	return this.serviceWSDLLocation;
	    }
	 
	 
	 public String getServiceVersion()
	    {
	    	return this.serviceVer;
	    } 
	 


	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}



	public void setServiceWSDLLocation(String serviceWSDLLocation) {
		this.serviceWSDLLocation = serviceWSDLLocation;
	}



	public void setServiceVer(String serviceVer) {
		this.serviceVer = serviceVer;
	}
	
	
	public void isupdate(){
		isupdate=true;
	}


	boolean IsCancelled = false;
	boolean IsOKPressed=false;
	String serviceName="";
	String serviceWSDLLocation="";
	String serviceVer="";
	Button doOK ;
	boolean isupdate=false;

}	
