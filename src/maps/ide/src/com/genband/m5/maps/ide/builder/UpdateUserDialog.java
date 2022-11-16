//Author@Reeta Aggarwal
package com.genband.m5.maps.ide.builder;

import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Event;

public class UpdateUserDialog   extends Dialog{
	private Shell shell = null;
	private Display display = null;
	
	public UpdateUserDialog(Shell shell) {
		super (shell);
	}

	private Listener listener = new Listener() {
		public void handleEvent(Event e) {
			nam = name.getText();
			if (nam.equals("")) {
				sendErrorMessage("Enter Valid DataBase User Name");
			} else {
				sendErrorMessage(null);
			}

		}
	};
	private boolean IsCancelled;
	private boolean IsOKPressed;

	private void sendErrorMessage(String message) {
	   this.sendErrorMessage(message);

	}

	public Object open() {

		Object result=null;	
		display = Display.getDefault();
		shell = new Shell(display);
		shell.setSize(400,180);
		shell.open();	
		shell.setText("Update DataBase User Name");	

		
		Label title = new Label(shell, SWT.NONE);
		title.setText("DataBase User Name:");
		title.setSize(140,20);
		title.setLocation(20,40);
		
		final Text textServiceName = new Text(shell, SWT.BORDER);
		textServiceName.setText("");
		textServiceName.setSize(160,20);
		textServiceName.setLocation(160,40); //180
		textServiceName.setEnabled(true);
		
		textServiceName.addListener(SWT.Modify, new Listener(){
			public void handleEvent(Event e) {
				nam=textServiceName.getText();
				
			}
		});
		
		doOK = new Button(shell, SWT.BORDER|SWT.PUSH);
		
		doOK.setText("OK");
		doOK.setSize(74,25);
		doOK.setLocation(120,100);
		doOK.addListener(SWT.Selection, new Listener(){
			public void handleEvent(Event e) {
				IsOKPressed =true;
				textServiceName.dispose();
				doOK.dispose();
				shell.close();
				shell.dispose();
			}
		});
		
		
		final Button doCancel = new Button(shell, SWT.BORDER|SWT.PUSH);
		doCancel.setText("Cancel");
		doCancel.setSize(74,25);
		doCancel.setLocation(200,100);
		doCancel.addListener(SWT.Selection, new Listener(){
			public void handleEvent(Event e) {
				IsCancelled=true;
				textServiceName.dispose();
				doOK.dispose();
				doCancel.dispose();
				shell.close();
				shell.dispose();
			}
		});

		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) display.sleep();
		}
		return result;
	}

	
	public String getUserName() {
		return nam;
	}

	 public boolean isCancelled()
	    {
	    	return this.IsCancelled;
	    }
	 
	 public boolean okPressed()
	    {
	    	return this.IsOKPressed;
	    }
	

	private Text name;
	private Button doOK =null;	
	
	Composite com;

	String nam = "";

	

}
