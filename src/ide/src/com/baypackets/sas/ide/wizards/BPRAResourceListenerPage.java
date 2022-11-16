//Author@Reeta Aggarwal

package com.baypackets.sas.ide.wizards;
import java.util.ArrayList;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.jdt.core.IType;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.core.runtime.IStatus;

public class BPRAResourceListenerPage extends BPClassCreationPage{
	
public static final String LINE_DELIMITER = "\n";
	
	private static final String[] NULL_PARAMS = new String[0];
	private static final String VOID_TYPE = "void".intern();
	private static final String RESOURCE_EXCEPTION = "com.baypackets.ase.resource.ResourceException".intern();
	private static final String EVENT_HANDLER_IF = "com.baypackets.ase.resource.ResourceListener".intern();
	private static final String EVENT = "com.baypackets.ase.resource.ResourceEvent".intern();

	
	private static final String[] EXCEPTION_TYPES = new String[] {RESOURCE_EXCEPTION};

	private static final String[] HANDLE_EVENT_PARAM_TYPES = new String[] {EVENT};
	private static final String[] HANDLE_EVENT_NAMES = new String[] {"event".intern()};

	public String typeName=null;
	
	public BPRAResourceListenerPage() {
		setTitle("New Resource Listener");
		setDescription("Creates a New Resource Listener Class");
	}
	
	protected void createTypeMembers(IType type, ImportsManager imports, IProgressMonitor monitor) throws CoreException {
	
			super.createMethod(type, imports, monitor, "handleEvent", HANDLE_EVENT_PARAM_TYPES, HANDLE_EVENT_NAMES, EXCEPTION_TYPES, VOID_TYPE, null);
	
		if (monitor != null) {
			monitor.done();
		}	
	}

	protected void createCustomControls(Composite composite, int nColumns) {
		
		ArrayList interfaces = new ArrayList();
		interfaces.add(EVENT_HANDLER_IF);
		setSuperClass("java.lang.Object", true);
		this.setSuperInterfaces(interfaces, false);

//		this.createRequestMethodControls(composite, nColumns);
		
	}
	
//	
//	private void createRequestMethodControls(Composite parent, int nColumns){
//		//Create the label.
//		Label label = new Label(parent, SWT.LEFT | SWT.WRAP);
//		label.setText("Which request method stubs would you like to create?");
//		GridData gd1= new GridData(GridData.HORIZONTAL_ALIGN_FILL);
//		gd1.horizontalSpan= nColumns;
//		label.setLayoutData(gd1);
//		
//		//Create an empty space.
//		this.createEmptySpace(parent, 1);
//		
//		//Create the controls from the second column.
//		Composite group = new Composite(parent, SWT.NONE);
//		GridData gd2= new GridData();
//		gd2.horizontalSpan= nColumns - 1;
//		group.setLayoutData(gd2);
//	
//		GridLayout layout= new GridLayout();
//		layout.makeColumnsEqualWidth= true;
//		layout.numColumns= 4;
//		layout.marginHeight= 0;
//		layout.marginWidth= 0;
//		group.setLayout(layout);
//		
//		btnDoPost = this.createButton(group, "doPost method", bPost);
//		btnDoGet = this.createButton(group, "doGet method", bGet);
//		btnDoPut = this.createButton(group, "doPut method", bPut);
//		btnDoDelete = this.createButton(group, "doDelete method", bDelete);
//		
//	}
//
//	
//	
//	private Button createButton(Composite parent, String text, boolean select){
//		Button btn = new Button(parent, SWT.CHECK | SWT.LEFT);
//		btn.setLayoutData(new GridData());
//		btn.setSelection(false);
//		btn.setText(text);
//		btn.setSelection(select);
//		btn.addSelectionListener(this.selectionListener);
//		return btn;
//		
//	}
//	
//	private SelectionListener selectionListener = new SelectionAdapter(){
//    	public void widgetSelected(SelectionEvent event) {
//    		checkSelection();
//	    }
//    };
//    
//    private void checkSelection(){
//    	bConstructors = btnConstructors.getSelection();
//		bAbstractMethods = btnAbstractMethods.getSelection();
//		bInit = btnInit.getSelection();
//		bDestroy = btnDestroy.getSelection();
//		bService = btnService.getSelection();
//		
//    }
//    
//
//    public void setAddPametersPage(AddHttpMappingAndInitParams page){
//    	nextPage=page;
//     }
//
//	private Button btnConstructors;
//	private boolean bConstructors = true;
//	private Button btnAbstractMethods;
//	private boolean bAbstractMethods = true;
//	
//	private AddHttpMappingAndInitParams nextPage;
//	
//	
//	private Button btnInit;
//	private boolean bInit = false;
//	private Button btnDestroy;
//	private boolean bDestroy = false;
//	private Button btnService;
//	private boolean bService = false;
	
	

}
