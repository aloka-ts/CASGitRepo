/*
 * Created on 26 Jan., 2006
 */
package com.baypackets.sas.ide.mgmt;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.baypackets.sas.ide.SasPlugin;
import com.baypackets.sas.ide.util.StatusASE;


public class AttachDialog extends Dialog {

	private String host;
	private int debugPort;
	private int jmxPort;
	private boolean cancelled;

	public AttachDialog (Shell parent){
		super (parent);
		this.init();
	}

	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setText("SAS Attachment");
	}



	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.OK_ID,
                IDialogConstants.OK_LABEL, true);
        createButton(parent, IDialogConstants.CANCEL_ID,
                IDialogConstants.CANCEL_LABEL, false);
	}


	protected Control createDialogArea(Composite parent) {
		Composite composite = (Composite) super.createDialogArea(parent);
		createControls(composite);
		applyDialogFont(composite);
        return composite;
	}


	protected void okPressed() {
		String strHost = txtHost.getText().trim();
		String strDPort = txtDebugPort.getText().trim();
		String strJPort = txtJmxPort.getText().trim();
		if(!strHost.equals("")){
			host = strHost;
		}
		try{
			debugPort = Integer.parseInt(strDPort);
		}catch(NumberFormatException nfe){}
	
		try{
			jmxPort = Integer.parseInt(strJPort);
		}catch(NumberFormatException nfe){}
		
		cancelled = false;
		
		super.okPressed();
	}

	protected void cancelPressed() {
		cancelled = true;
		super.cancelPressed();
	}

	protected void createControls(Composite parent){
		//Create a top level composite
		//Composite composite = new Composite(parent, SWT.NONE);
		Composite composite = parent;
		GridLayout layout = new GridLayout();
		layout.numColumns = 3;
		layout.makeColumnsEqualWidth = true;
		composite.setLayout(layout);
		
		GridData gd = null;
		
		//Create the Controls for the param name
		Label lbHost = new Label(composite, SWT.NONE);
		lbHost.setText("Host:");

		//Create txtHost
		txtHost = new Text(composite, SWT.BORDER);
		txtHost.setText(this.host);
		
		gd = new GridData(GridData.FILL_BOTH);
		gd.horizontalSpan = 2;
		txtHost.setLayoutData(gd);

		//Create the Controls for the debug port
		Label lbDebugPort = new Label(composite, SWT.NONE);
		lbDebugPort.setText("Debug Port:");

		//Create txt Debug port
		txtDebugPort = new Text(composite, SWT.BORDER);
		txtDebugPort.setText(""+this.debugPort);
	
		//Create an Empty Space
		new Label(composite, SWT.NONE);
		
		//Create the Controls for the debug port
		Label lbJmxPort = new Label(composite, SWT.NONE);
		lbJmxPort.setText("JMX Port:");

		//Create txt Debug port
		txtJmxPort = new Text(composite, SWT.BORDER);
		txtJmxPort.setText(""+this.jmxPort);
	}
	
	private Text txtHost;
	private Text txtDebugPort;
	private Text txtJmxPort;
		
	private void init(){
		this.host = StatusASE.getInstance().getAddress();
		this.debugPort = SasPlugin.getDebugPort();
		this.jmxPort = SasPlugin.getPORT();
		this.cancelled = false;
	}
	
	public int getDebugPort() {
		return debugPort;
	}

	public void setDebugPort(int debugPort) {
		this.debugPort = debugPort;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public int getJmxPort() {
		return jmxPort;
	}

	public void setJmxPort(int jmxPort) {
		this.jmxPort = jmxPort;
	}

	public boolean isCancelled() {
		return cancelled;
	}
}
