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
package com.baypackets.sas.ide.editor;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.TypedListener;
import com.baypackets.sas.ide.SasPlugin;

public class BPFormControl {

	Control control;
	public BPFormControl(Control control) {
		this.control = control;
		addListeners();
	}
	
	private BPFormListener formListener = null;
	private boolean modified = false;
	private boolean selected = false;
	private boolean focusOn =false;
	
	private void addListeners(){
		
		//Add Focus listener
		FocusListener fl = new FocusListener(){
			public void focusGained(FocusEvent e) {
				focusOn = true;
				//selected = false; //commented by reeta
				//modified = false;//commented by reeta
				SasPlugin.getDefault().log("The focusGained()....");
			}

			public void focusLost(FocusEvent e) {
				if(focusOn && selected && formListener != null){
					SasPlugin.getDefault().log("The focusLost()....calling selection changed");
						formListener.selectionChanged();
				}
				
				if(focusOn && modified && formListener != null){
					SasPlugin.getDefault().log("The focusLost()....calling text changed");
						formListener.textChanged();
				}
				focusOn = false;
				selected = false;
				modified = false;
			}
		};
		control.addFocusListener(fl);
		
		//Add Selection listener
		SelectionListener sl = new SelectionAdapter(){
			public void widgetSelected(SelectionEvent e) {
				selected = true;
//				if(formListener != null){
					SasPlugin.getDefault().log("The selectionAdaptor()....widget selected selected is true");	
//						formListener.selectionChanged();
//				}
				
			}
		};
		
		
		control.addListener(SWT.Selection, new TypedListener(sl));
		
		//Add Modify listener
		ModifyListener ml = new ModifyListener(){
			public void modifyText(ModifyEvent e) {
				modified = true;
//				if(formListener != null){
//					SasPlugin.getDefault().log("The modifyText()....modifyText.. calling textChanged");	
//						formListener.textChanged();
//				}
//				SasPlugin.getDefault().log("The modifyText()....modified is true");
				
			}
		};
		control.addListener(SWT.Modify, new TypedListener(ml));
	}

	public BPFormListener getFormListener() {
		return formListener;
	}

	public void setFormListener(BPFormListener formListener) {
		this.formListener = formListener;
	}

	public Control getControl() {
		return control;
	}
}
