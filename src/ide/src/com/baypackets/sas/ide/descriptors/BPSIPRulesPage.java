package com.baypackets.sas.ide.descriptors;

import org.eclipse.jface.wizard.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;

public class BPSIPRulesPage extends WizardPage {
	
	public BPSIPRulesPage(String args)
	{
		super(args);
	}
	
	public void createControl(Composite parent)
	{
		Composite frame = new Composite(parent, 0);
        GridLayout gl = new GridLayout();
        gl.numColumns = 2;
        GridData gd = new GridData(768);
        gd.horizontalSpan = 2;
        frame.setLayout(gl);
        frame.setLayoutData(gd);
        
        createServletControl(frame);
        
        //createClassControl(frame);
        //createFeature(frame);
        setControl(frame);
        setPageComplete(true);

		
	}
	
	public void createServletControl(Composite frame)
	{
		Label sep = new Label(frame, 258);
        GridData gd = new GridData(768);
        gd.horizontalSpan = 2;
        sep.setLayoutData(gd);
        Label instructions = new Label(frame, 32);
        instructions.setText("Enter The Servlet Context Parameters and Servlet Init Parameters");
        gd = new GridData();
        gd.horizontalSpan = 2;
        instructions.setLayoutData(gd);
        
        
        
        
        Composite checkBox = new Composite(frame, SWT.DROP_DOWN);
        GridLayout gl = new GridLayout();
        gl.numColumns = 3;
        gd = new GridData(1808);
        gd.horizontalSpan = 2;
        checkBox.setLayout(gl);
        checkBox.setLayoutData(gd);
        Composite checkBox1 = new Composite(checkBox, SWT.DROP_DOWN);
        gl = new GridLayout();
        gl.numColumns = 1;
        gd = new GridData(1808);
        gd.horizontalSpan = 1;
        checkBox1.setLayout(gl);
        checkBox1.setLayoutData(gd);
        Composite checkBox2 = new Composite(checkBox, SWT.DROP_DOWN);
        gl = new GridLayout();
        gl.numColumns = 1;
        gd = new GridData(1808);
        gd.horizontalSpan = 1;
        checkBox2.setLayout(gl);
        checkBox2.setLayoutData(gd);
        Composite checkBox3 = new Composite(checkBox, SWT.DROP_DOWN);
        gl = new GridLayout();
        gl.numColumns = 1;
        gd = new GridData(1808);
        gd.horizontalSpan = 1;
        checkBox3.setLayout(gl);
        checkBox3.setLayoutData(gd);
        
        
        
        
        Label paramtype = new Label(checkBox1, SWT.BOLD);
        
        
        
        gd = new GridData(768);
        
        paramtype.setLayoutData(gd);
        paramtype.setVisible(true);
        paramtype.setText("Parameter Type");
        
	}
	

}
