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
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Event;


public class RegisterServiceDialog extends Dialog{
	
	
	public Shell shell=null;
	
	public RegisterServiceDialog (Shell parent) 
	{
		super (parent);
	}
	
	
	
	/**
	 * Override to set the title of the dialog.
	 */
	
	public Object open () 
	{
		Object result=null;	
		Shell parent = getParent();
		shell = new Shell(parent);
		
		shell.setSize(400,200);
		shell.open();
		Display display = parent.getDisplay();
		
		shell.setText("AGNITY SOA Application Registration");			
			
			Label title = new Label(shell, SWT.NONE);
			title.setText("Application Name:");
			title.setSize(100,20);
			title.setLocation(20,30);
			
			final Text textApplicationName = new Text(shell, SWT.BORDER);
			textApplicationName.setText("");
			textApplicationName.setSize(250,20);
			textApplicationName.setLocation(125,30);
			textApplicationName.setEnabled(true);
			textApplicationName.addListener(SWT.Modify, new Listener(){
				public void handleEvent(Event e) {
					appName=textApplicationName.getText();
					validateDialog();
				}
			});
			
			final Label serviceNameLabel = new Label(shell, SWT.NONE);
			serviceNameLabel.setText("Service Name:");
			serviceNameLabel.setSize(100,20);
			serviceNameLabel.setLocation(20,60);
			
			final Combo serviceNameCombo= new Combo(shell, SWT.BORDER|SWT.READ_ONLY);
			serviceNameCombo.setText("");
			serviceNameCombo.setSize(250,20);
			serviceNameCombo.setLocation(125,60);
			serviceNameCombo.setEnabled(true);
			
			//Fill the combo box with the service names of the SOA services deployed on sAS
			fillServiceNameCombo(serviceNameCombo);
			
			serviceNameCombo.addListener(SWT.Selection, new Listener(){
				public void handleEvent(Event e) {
					serviceName=serviceNameCombo.getText();
					validateDialog();
				}
			});
			
			
			final Label appURL = new Label(shell, SWT.NONE);
			appURL.setText("Application URL:");
			appURL.setSize(100,20);
			appURL.setLocation(20,95);
			
			final Text appURLText = new Text(shell, SWT.BORDER|SWT.READ_ONLY);
			appURLText.setText("");
			appURLText.setSize(250,20);
			appURLText.setLocation(125,95);
			appURLText.setEnabled(true);
			appURLText.addListener(SWT.Modify, new Listener(){
				public void handleEvent(Event e) {
					applicationURL=appURLText.getText();
					validateDialog();
				}
			});
			
			
			doOK = new Button(shell, SWT.BORDER|SWT.PUSH);
			doOK.setText("Register");
			doOK.setSize(74,25);
			doOK.setLocation(125,130);
			doOK.addListener(SWT.Selection, new Listener(){
				public void handleEvent(Event e) {
					
				}
			});
			
			
			final Button doCancel = new Button(shell, SWT.BORDER|SWT.PUSH);
			doCancel.setText("Cancel");
			doCancel.setSize(74,25);
			doCancel.setLocation(205,130);
			doCancel.addListener(SWT.Selection, new Listener(){
				public void handleEvent(Event e) {
					IsCancelled = true;
					textApplicationName.dispose();
					serviceNameLabel.dispose();
					serviceNameCombo.dispose();
					doOK.dispose();
					doCancel.dispose();
					shell.close();
					shell.dispose();
				}
			});
			validateDialog();
			return result;
		 }
	
	
	private void fillServiceNameCombo(Combo combo){
		if(servicesNameList!=null){
			for(int i=0;i<servicesNameList.size();i++){
				combo.add(servicesNameList.get(i));
			}
			
		}
	}
	
	public void setServiceNameList(java.util.List servicesList){
		servicesNameList=servicesList;
	}
	
	
	
	private void validateDialog(){
    	
    	if(this.serviceName==null||this.serviceName.equals("")&&
    	   this.appName==null||this.appName.equals("")&&
    	   this.applicationURL==null||this.applicationURL.equals("")){
    		doOK.setEnabled(false);
    	}else{
    		doOK.setEnabled(true);
    	}
    }

	 public boolean isCancelled()
	    {
	    	return this.IsCancelled;
	    }
	 
	 public String getApplicationName()
	    {
	    	return this.appName;
	    }
	 
	 public String getServiceName()
	    {
	    	return this.serviceName;
	    }
	 
	 
	 public String getApplicationURL()
	    {
	    	return this.applicationURL;
	    }
	 

	boolean IsCancelled = false;
	String serviceName="";
	String appName="";
	String applicationURL="";
	Button doOK;
	java.util.List<String> servicesNameList;
}	
