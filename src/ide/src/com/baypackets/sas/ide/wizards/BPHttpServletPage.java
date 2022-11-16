//Author@Reeta Aggarwal

package com.baypackets.sas.ide.wizards;
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

public class BPHttpServletPage extends BPClassCreationPage{
	
public static final String LINE_DELIMITER = "\n";
	
	private static final String[] NULL_PARAMS = new String[0];
	private static final String VOID_TYPE = "void".intern();
	private static final String IO_EXCEPTION = "java.io.IOException".intern();
	private static final String SERVLET_EXCEPTION = "javax.servlet.ServletException".intern();
	private static final String SERVLET_REQUEST = "javax.servlet.ServletRequest".intern();
	private static final String SERVLET_RESPONSE = "javax.servlet.ServletResponse".intern();
	private static final String HTTP_SERVLET_REQUEST = "javax.servlet.http.HttpServletRequest".intern();
	private static final String HTTP_SERVLET_RESPONSE = "javax.servlet.http.HttpServletRequest".intern();
	
	private static final String[] EXCEPTION_TYPES = new String[] {SERVLET_EXCEPTION, IO_EXCEPTION};
	private static final String[] REQ_PARAM_TYPES = new String[] {HTTP_SERVLET_REQUEST};
	private static final String[] REQ_PARAM_NAMES = new String[] {"request".intern()};

	private static final String[] RESP_PARAM_TYPES = new String[] {HTTP_SERVLET_RESPONSE};
	private static final String[] RESP_PARAM_NAMES = new String[] {"response".intern()};

	private static final String[] SERV_PARAM_TYPES = new String[] {SERVLET_REQUEST, SERVLET_RESPONSE};
	private static final String[] SERV_PARAM_NAMES = new String[] {"request".intern(), "response".intern()};
	public String typeName=null;
	
	public BPHttpServletPage() {
		setTitle("New Http Servlet");
		setDescription("Creates a New Http Servlet Class");
	}
	
	//This method is added for setting servlet name to the AddHttpMappingAndInitParams page
	protected IStatus typeNameChanged(){
		typeName=this.getTypeName();
		IStatus status=super.typeNameChanged();
		if(nextPage!=null){	
		nextPage.setServletNameAndMapping(typeName);
		}
		return status;
	}
	
	protected void createTypeMembers(IType type, ImportsManager imports, IProgressMonitor monitor) throws CoreException {
		//Create life Cycle methods.
		if(this.bInit){
			super.createMethod(type, imports, monitor, "init", NULL_PARAMS, NULL_PARAMS, new String[]{SERVLET_EXCEPTION}, VOID_TYPE, null);
		}
		if(this.bDestroy){
			super.createMethod(type, imports, monitor, "destroy", NULL_PARAMS, NULL_PARAMS, NULL_PARAMS, VOID_TYPE, null);
		}

		//Create the Service method.
		if(this.bService){
			super.createMethod(type, imports, monitor, "service", SERV_PARAM_TYPES, SERV_PARAM_NAMES, EXCEPTION_TYPES, VOID_TYPE, null);
		}

		//Create Request methods.....
		if(this.bPost){
			super.createMethod(type, imports, monitor, "doPost", REQ_PARAM_TYPES, REQ_PARAM_NAMES, EXCEPTION_TYPES, VOID_TYPE, null);
		}
		if(this.bGet){
			super.createMethod(type, imports, monitor, "doGet", REQ_PARAM_TYPES, REQ_PARAM_NAMES, EXCEPTION_TYPES, VOID_TYPE, null);
		}
		if(this.bPut){
			super.createMethod(type, imports, monitor, "doPut", REQ_PARAM_TYPES, REQ_PARAM_NAMES, EXCEPTION_TYPES, VOID_TYPE, null);
		}
		if(this.bDelete){
			super.createMethod(type, imports, monitor, "doDelete", REQ_PARAM_TYPES, REQ_PARAM_NAMES, EXCEPTION_TYPES, VOID_TYPE, null);
		}
		
		
		//Call the super class's implementation....
		super.createInheritedMethods(type, this.bConstructors, this.bAbstractMethods, imports, new SubProgressMonitor(monitor, 1));
	
		if (monitor != null) {
			monitor.done();
		}	
	}

	protected void createCustomControls(Composite composite, int nColumns) {
		
		setSuperClass("javax.servlet.http.HttpServlet", false);
		
		this.createCommonMethodControls(composite, nColumns);
		this.createRequestMethodControls(composite, nColumns);
		
	}
	
	private void createCommonMethodControls(Composite parent, int nColumns){
		//Create the label.
		Label label = new Label(parent, SWT.LEFT | SWT.WRAP);
		label.setText("Which common method stubs would you like to create?");
		GridData gd1= new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		gd1.horizontalSpan= nColumns;
		label.setLayoutData(gd1);
		
		//Create an empty space.
		this.createEmptySpace(parent, 1);
		
		//Create the controls from the second column.
		Composite group = new Composite(parent, SWT.NONE);
		GridData gd2= new GridData();
		gd2.horizontalSpan= nColumns - 1;
		group.setLayoutData(gd2);
	
		GridLayout layout= new GridLayout();
		layout.makeColumnsEqualWidth= true;
		layout.numColumns= 3;
		layout.marginHeight= 0;
		layout.marginWidth= 0;
		group.setLayout(layout);
		
		btnConstructors = this.createButton(group, "Constructors from super class", bConstructors);
		btnAbstractMethods = this.createButton(group, "Inherited Abstract Methods", bAbstractMethods);
		btnInit = this.createButton(group, "init method", bInit);
		btnDestroy = this.createButton(group, "destroy method", bDestroy);
		btnService = this.createButton(group, "service method", bService);
	}
	
	private void createRequestMethodControls(Composite parent, int nColumns){
		//Create the label.
		Label label = new Label(parent, SWT.LEFT | SWT.WRAP);
		label.setText("Which request method stubs would you like to create?");
		GridData gd1= new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		gd1.horizontalSpan= nColumns;
		label.setLayoutData(gd1);
		
		//Create an empty space.
		this.createEmptySpace(parent, 1);
		
		//Create the controls from the second column.
		Composite group = new Composite(parent, SWT.NONE);
		GridData gd2= new GridData();
		gd2.horizontalSpan= nColumns - 1;
		group.setLayoutData(gd2);
	
		GridLayout layout= new GridLayout();
		layout.makeColumnsEqualWidth= true;
		layout.numColumns= 4;
		layout.marginHeight= 0;
		layout.marginWidth= 0;
		group.setLayout(layout);
		
		btnDoPost = this.createButton(group, "doPost method", bPost);
		btnDoGet = this.createButton(group, "doGet method", bGet);
		btnDoPut = this.createButton(group, "doPut method", bPut);
		btnDoDelete = this.createButton(group, "doDelete method", bDelete);
		
	}

	
	
	private Button createButton(Composite parent, String text, boolean select){
		Button btn = new Button(parent, SWT.CHECK | SWT.LEFT);
		btn.setLayoutData(new GridData());
		btn.setSelection(false);
		btn.setText(text);
		btn.setSelection(select);
		btn.addSelectionListener(this.selectionListener);
		return btn;
		
	}
	
	private SelectionListener selectionListener = new SelectionAdapter(){
    	public void widgetSelected(SelectionEvent event) {
    		checkSelection();
	    }
    };
    
    private void checkSelection(){
    	bConstructors = btnConstructors.getSelection();
		bAbstractMethods = btnAbstractMethods.getSelection();
		bInit = btnInit.getSelection();
		bDestroy = btnDestroy.getSelection();
		bService = btnService.getSelection();
		
		bPost = btnDoPost.getSelection();
		bGet = btnDoGet.getSelection();
		bPut = btnDoPut.getSelection();
		bDelete = btnDoDelete.getSelection();
		
    }
    

    public void setAddPametersPage(AddHttpMappingAndInitParams page){
    	nextPage=page;
     }

	private Button btnConstructors;
	private boolean bConstructors = true;
	private Button btnAbstractMethods;
	private boolean bAbstractMethods = true;
	
	private AddHttpMappingAndInitParams nextPage;
	
	
	private Button btnInit;
	private boolean bInit = false;
	private Button btnDestroy;
	private boolean bDestroy = false;
	private Button btnService;
	private boolean bService = false;
	
	private Button btnDoPost;
	private boolean bPost = false;
	private Button btnDoGet;
	private boolean bGet = false;
	private Button btnDoPut;
	private boolean bPut = false;
	private Button btnDoDelete;
	private boolean bDelete = false;
	

}
