//Author@Reeta Aggarwal
package com.baypackets.sas.ide.wizards;

import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.jface.dialogs.*;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Event;

public class AddInitParamsDialog extends TitleAreaDialog {

	public AddInitParamsDialog(Shell shell) {
		super(shell);

	}

	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setText("Init Parameters");
	}

	private Listener listener = new Listener() {
		public void handleEvent(Event e) {
			nam = name.getText();
			val = value.getText();
			if (nam.equals("")) {
				sendErrorMessage("Enter a Valid Parameter Name");
			} else {
				sendErrorMessage(null);
			}

		}
	};

	private void sendErrorMessage(String message) {
		this.setErrorMessage(message);

	}

	public Control createDialogArea(Composite com) {

		this.com = com;
		GridLayout lay = new GridLayout();
		lay.numColumns = 2;
		com.setToolTipText("Init Parameters");
		com.setLayout(lay);

		new Label(com, SWT.LEFT | SWT.WRAP).setText("Param Name :");
		GridData gri = new GridData(GridData.FILL_HORIZONTAL);
		name = new Text(com, SWT.SINGLE | SWT.BORDER);
		name.setLayoutData(gri);
		name.setTextLimit(40);
		name.addListener(SWT.Modify, listener);

		new Label(com, SWT.LEFT | SWT.WRAP).setText("Param Value :");

		GridData g = new GridData(GridData.FILL_HORIZONTAL);
		value = new Text(com, SWT.SINGLE | SWT.BORDER);
		value.setLayoutData(g);
		value.setTextLimit(40);
		value.addListener(SWT.Modify, listener);
		new Label(com, SWT.LEFT | SWT.WRAP).setText("Description :");

		GridData g1 = new GridData(GridData.FILL_HORIZONTAL);
		desc = new Text(com, SWT.SINGLE | SWT.BORDER);
		desc.setLayoutData(g1);
		desc.setTextLimit(40);

		Composite comp = (Composite) super.createDialogArea(com);
		return comp;

	}

	public void okPressed() {
		nam = name.getText();
		val = value.getText();
		des = desc.getText();
		com.dispose();
		name.dispose();
		value.dispose();
		desc.dispose();
		this.close();
	}

	public String getParamName() {
		return nam;
	}

	public String getParamValue() {
		return val;
	}

	public String getInitParamDescription() {
		return des;
	}

	private Text name;

	private Text value;

	private Text desc;

	Composite com;

	String nam = "";

	String val = "";

	String des = "";

}
