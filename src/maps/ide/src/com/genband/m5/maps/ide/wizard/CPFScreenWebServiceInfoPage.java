package com.genband.m5.maps.ide.wizard;

import java.util.Iterator;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.jface.wizard.WizardPage;

import com.genband.m5.maps.common.CPFConstants;
import com.genband.m5.maps.ide.CPFPlugin;
import com.genband.m5.maps.ide.model.WebServiceInfo;

public class CPFScreenWebServiceInfoPage extends WizardPage {

	private CPFScreenCreationWizard wizard;

	private Composite composite;

	public CPFScreenWebServiceInfoPage(ISelection selection,
			CPFScreenCreationWizard wizard) {
		super("WebServicePage");
		setTitle("New Provisioning Screen Creation");
		setDescription("Define the WebService Interface");
		this.wizard = wizard;
	}
	
	String serName="";
	String tns="http://maps.m5.genband.com/";

	/**
	 * Creates the main window's contents
	 * 
	 * @param shell
	 *            the main window
	 */
	public void createControl(Composite parent) {
		initializeDialogUnits(parent);
		composite = new Composite(parent, SWT.NONE);
		composite.setFont(parent.getFont());

		int nColumns = 4;

		GridLayout layout = new GridLayout();
		layout.numColumns = nColumns;
		composite.setLayout(layout);

		Group group = new Group(composite, GridData.FILL_HORIZONTAL);
		layout = new GridLayout();
		layout.numColumns = 4;
		group.setLayout(layout);

		GridData gridD = new GridData(GridData.FILL_HORIZONTAL);
		gridD.horizontalSpan = 4;
		gridD.verticalIndent = 15;
		group.setLayoutData(gridD);
		group.setText("Define the WebService Reference and TargetNameSpace:");

		// Create the table
		new Label(group, SWT.NONE).setText("Web Service Name:");
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		final Text serviceName = new Text(group, SWT.BORDER);
		gridData.horizontalSpan = 3;
		serviceName.setLayoutData(gridData);
		serviceName.addListener(SWT.Modify, new Listener() {
			public void handleEvent(Event e) {
				serName = serviceName.getText();
				CPFPlugin.getDefault().log(
						"The WebService Name is!!!!!!!!!" + serName);
				webInfo.setWebServiceName(serName);
				getWizard().getContainer().updateButtons();
			}
		});

		new Label(group, SWT.NONE).setText("TargetNameSpace:");
		gridData = new GridData(GridData.FILL_HORIZONTAL);
		final Text nameSpace = new Text(group, SWT.BORDER);
		gridData.horizontalSpan = 3;
		nameSpace.setLayoutData(gridData);
		nameSpace.setText(tns);
		webInfo.setTargetNamespace(nameSpace.getText());
		nameSpace.addListener(SWT.Modify, new Listener() {
			public void handleEvent(Event e) {
				tns = nameSpace.getText();
				CPFPlugin.getDefault().log(
						"The WebService NameSpace is!!!!!!!!!" + serName);
				webInfo.setTargetNamespace(tns);
				getWizard().getContainer().updateButtons();
			}
		});

		createWebServiceInfoTable(composite);

		setControl(composite);

		Dialog.applyDialogFont(composite);
	}

	private void createWebServiceInfoTable(Composite composite) {

		Group group = new Group(composite, GridData.FILL_HORIZONTAL);
		GridLayout layout = new GridLayout();
		layout.marginHeight = 10;
		layout.numColumns = 4;
		group.setLayout(layout);

		GridData gridD = new GridData(GridData.FILL_HORIZONTAL);
		gridD.horizontalSpan = 4;
		gridD.verticalIndent = 15;
		group.setLayoutData(gridD);
		group.setText("Define the WebService Methods:");
		// group.setRedraw(true);
		// group.pack(true);

		CPFPlugin.getDefault().log(
				"CREATE Methods tableeeeeeeeeeee ............." + group);
		Table table = new Table(group, SWT.SINGLE | SWT.BORDER
				| SWT.FULL_SELECTION | SWT.HIDE_SELECTION | SWT.V_SCROLL);
		gridD = new GridData(GridData.FILL_HORIZONTAL);
		gridD.horizontalSpan = 4;
		gridD.grabExcessHorizontalSpace = true;
		gridD.grabExcessVerticalSpace = true;
		gridD.widthHint = 60;
//		if (wizard.getFirstPage().getViewType().equals(
//				CPFConstants.ViewType.LIST)) {
//		gridD.heightHint = 50;
//		}else if (wizard.getFirstPage().getViewType().equals(
//				CPFConstants.ViewType.DETAILS_VIEW)){
//			gridD.heightHint = 75;
//		}
		table.setLayoutData(gridD);
		table.setHeaderVisible(true);
		table.setLinesVisible(true);

		// Create 2 columns
		for (int i = 0; i < 4; i++) {
			TableColumn column = new TableColumn(table, SWT.BORDER);
			// if (i == 0)
			// column.setText("");
			if (i == 0)
				column.setText("Operation Name");
			if (i == 1)
				column.setText("Method Name");
			if (i == 2)
				column.setText("In ParamName");
			if (i == 3)
				column.setText("Out ParamName");
			column.pack();//setWidth(100);
		}
		Map<CPFConstants.OperationType, java.util.List<String>> screenRolesMap =wizard.getFirstPage().getScreenRolesMap();
		Set<CPFConstants.OperationType> operation=screenRolesMap.keySet();
		operations=new java.util.ArrayList<String>();
		
		if(operation!=null){
		
			Iterator<CPFConstants.OperationType> itr=operation.iterator();
		while(itr.hasNext()){
			CPFConstants.OperationType op=itr.next();
			 java.util.List<String> roles=screenRolesMap.get(op);
			
			 if(roles!=null&&roles.size()!=0){
			    String opName=op.name();
			    operations.add(opName);

			CPFPlugin.getDefault()
					.log("Adding Operation.Name from Roles....." + opName);
			}
		}
		
		}
		
		if (operations.size()==1) {
			 gridD.heightHint = 25;
			}else if (operations.size()==2){
				gridD.heightHint = 50;
			}else if (operations.size()==3){
				gridD.heightHint = 75;
			}
//		for(int i=0;i<operations.size();i++){
//			operations.add(operations.get(i));
//		}
		
//		if (wizard.getFirstPage().getViewType().equals(
//				CPFConstants.ViewType.DETAILS_VIEW)) {
//			operations = new String[] {
//					CPFConstants.OperationType.CREATE.name(),
//					CPFConstants.OperationType.MODIFY.name(),
//					CPFConstants.OperationType.VIEW.name() };
//		} else {
//			operations = new String[] { CPFConstants.OperationType.LIST.name(),
//					CPFConstants.OperationType.DELETE.name() };
//		}
		for (int i = 0; i < operations.size(); i++) {
			final TableItem item = new TableItem(table, SWT.BORDER);

			// Create an editor object to use for text editing

			CPFPlugin.getDefault()
					.log("Adding Operation......" + operations.size());

			final TableEditor editor = new TableEditor(table);
			editor.grabHorizontal = true;
			CLabel opLabel = new CLabel(table, SWT.NONE | SWT.BORDER);

			opLabel.setText(operations.get(i));
			item.setText(0, operations.get(i));
			editor.setEditor(opLabel, item, 0);

			// Create an editor object to use for text editing
			final TableEditor editor2 = new TableEditor(table);
			editor2.grabHorizontal = true;
			Text methodName = new Text(table, SWT.BORDER);
			methodName.addListener(SWT.Modify, new Listener() {
				public void handleEvent(Event e) {

					Text mName = (Text) editor2.getEditor();
					TableItem item = editor2.getItem();
					String txt = item.getText(0);
					String methodNm = mName.getText();

					if (txt.equals(CPFConstants.OperationType.CREATE.name())) {
						webMethodsMap.put(CPFConstants.OperationType.CREATE,
								methodNm);
					} else if (txt.equals(CPFConstants.OperationType.MODIFY
							.name())) {
						webMethodsMap.put(CPFConstants.OperationType.MODIFY,
								methodNm);
					} else if (txt.equals(CPFConstants.OperationType.VIEW
							.name())) {
						webMethodsMap.put(CPFConstants.OperationType.VIEW,
								methodNm);
					} else if (txt.equals(CPFConstants.OperationType.LIST
							.name())) {
						webMethodsMap.put(CPFConstants.OperationType.LIST,
								methodNm);
					} else if (txt.equals(CPFConstants.OperationType.DELETE
							.name())) {
						webMethodsMap.put(CPFConstants.OperationType.DELETE,
								methodNm);
					}
					webInfo.setWebMethodsMap(webMethodsMap);
					CPFPlugin.getDefault().log(
							"The table item selected is...." + item
									+ "The Operation is..." + txt
									+ "MethodName is.." + methodNm);
					CPFPlugin.getDefault().log(
							"The WebMethods Map is!!!!!!!!!!" + webMethodsMap);
					getWizard().getContainer().updateButtons();
				}
			});
			editor2.setEditor(methodName, item, 1);

			final TableEditor editor1 = new TableEditor(table);
			editor1.grabHorizontal = true;
			Text inParam = new Text(table, SWT.BORDER);
			inParam.addListener(SWT.Modify, new Listener() {
				public void handleEvent(Event e) {
					String[] methodParams=null;
					Text mName = (Text) editor1.getEditor();
					TableItem item = editor1.getItem();
					String txt = item.getText(0);
					String methodNm = mName.getText();
					
					if(!methodNm.equals("")){
					  methodParams = new String[] { methodNm };
					}

					CPFPlugin.getDefault().log(
							"The table item selected is...." + item
									+ "The Operation is..." + txt
									+ "MethodName is.." + methodNm + "Params Array is.."+methodParams);

					if (txt.equals(CPFConstants.OperationType.CREATE.name())) {
						webParamsMap.put(CPFConstants.OperationType.CREATE,
								methodParams);
					} else if (txt.equals(CPFConstants.OperationType.MODIFY
							.name())) {
						webParamsMap.put(CPFConstants.OperationType.MODIFY,
								methodParams);
					} else if (txt.equals(CPFConstants.OperationType.VIEW
							.name())) {
						webParamsMap.put(CPFConstants.OperationType.VIEW,
								methodParams);
					} else if (txt.equals(CPFConstants.OperationType.LIST
							.name())) {
						webParamsMap.put(CPFConstants.OperationType.LIST,
								methodParams);
					} else if (txt.equals(CPFConstants.OperationType.DELETE
							.name())) {
						webParamsMap.put(CPFConstants.OperationType.DELETE,
								methodParams);
					}
					CPFPlugin.getDefault().log(
							"The WebParams Map is!!!!!!!!!!" + webParamsMap);
					webInfo.setWebParams(webParamsMap);
					getWizard().getContainer().updateButtons();
				}
			});
			editor1.setEditor(inParam, item, 2);

			if (operations.get(i).equals(CPFConstants.OperationType.VIEW.name())
					|| operations.get(i).equals(CPFConstants.OperationType.LIST
							.name())) {
				final TableEditor editor4 = new TableEditor(table);
				editor4.grabHorizontal = true;
				Text outParam = new Text(table, SWT.BORDER);
				outParam.addListener(SWT.Modify, new Listener() {
					public void handleEvent(Event e) {

						Text mName = (Text) editor4.getEditor();
						TableItem item = editor4.getItem();
						String txt = item.getText(0);
						String methodNm = mName.getText();
						CPFPlugin.getDefault().log(
								"The table item selected is...." + item
										+ "The Operation is..." + txt
										+ "MethodName is.." + methodNm);

						if (txt.equals(CPFConstants.OperationType.VIEW.name())) {
							webResultsMap.put(CPFConstants.OperationType.VIEW,
									methodNm);
						} else if (txt.equals(CPFConstants.OperationType.LIST
								.name())) {
							webResultsMap.put(CPFConstants.OperationType.LIST,
									methodNm);
						}

						CPFPlugin.getDefault().log(
								"The WebResults Map is!!!!!!!!!!"
										+ webResultsMap);
						webInfo.setWebResults(webResultsMap);
						getWizard().getContainer().updateButtons();
					}
				});
				editor4.setEditor(outParam, item, 3);

			}

		}

	}

	public WebServiceInfo getWebServiceInfo() {
		return webInfo;
	}

	public boolean canFlipToNextPage() {
		
		boolean canflip=false;
		
		if(!this.serName.equals("")&&
				!this.tns.equals("")){
		
			Iterator<String> itr =operations.iterator();
		while(itr.hasNext()){
			String opName=itr.next();
			CPFConstants.OperationType op=CPFConstants.OperationType.valueOf(opName);
			CPFPlugin.getDefault().log("CanfliptoNext first while loop "+ op.name() + "WebMthod Map " +webParamsMap.get(op) + "Web Method.." +webMethodsMap.get(op));
			if(  webMethodsMap.get(op) != null &&  ! webMethodsMap.get(op).isEmpty()
				&& webParamsMap.get(op) !=  null && webParamsMap.get(op).length!=0){
				canflip=true;
			}else{
				canflip=false;
				break;
			}
		}
		
		CPFPlugin.getDefault().log("CanfliptoNext is.The Web methods aand web param returned ..." +canflip);
		
	if(canflip==true) {
		itr =operations.iterator();
		while(itr.hasNext()){
			String opName=itr.next();
			CPFConstants.OperationType op=CPFConstants.OperationType.valueOf(opName);
		
			if(op.name().equals(CPFConstants.OperationType.LIST.name()) 
					|| op.name().equals(CPFConstants.OperationType.VIEW.name()))  {
			   CPFPlugin.getDefault().log("The operations containss "+ op.name() + " Web Result is.."+webResultsMap.get(op));
		
			 if ( webResultsMap.get(op) != null 
				&& ! webResultsMap.get(op).isEmpty()){
				 canflip=true;
			 } else{
				 canflip=false;
				 break;
			 }
			}
		 }
		}
	 // if(operations.contains(CPFConstants.OperationType.DELETE.name())){

	  
   }
	CPFPlugin.getDefault().log("CanfliptoNext is...."+canflip +" Web method map is.."+webMethodsMap + " Web Param mapis "+webParamsMap +" Web result map "+webResultsMap);  
		 return canflip;
	}

	Map<CPFConstants.OperationType, String> webMethodsMap = new HashMap<CPFConstants.OperationType, String>();

	Map<CPFConstants.OperationType, String[]> webParamsMap = new HashMap<CPFConstants.OperationType, String[]>();

	Map<CPFConstants.OperationType, String> webResultsMap = new HashMap<CPFConstants.OperationType, String>();

	

	WebServiceInfo webInfo = new WebServiceInfo();
	java.util.List<String> operations;
	
	
}
