package com.genband.m5.maps.ide.wizard;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;

import com.genband.m5.maps.ide.CPFPlugin;
import com.genband.m5.maps.ide.model.CPFAttribute;
import com.genband.m5.maps.ide.model.CPFScreen;
import com.genband.m5.maps.ide.model.ModelAttribute;
import com.genband.m5.maps.ide.model.ModelEntity;
import com.genband.m5.maps.ide.model.RelationKey;

public class CPFScreenNestedJSPsNamesPage extends WizardPage {

	private static CPFPlugin LOG = CPFPlugin.getDefault();
	
	private CPFScreenCreationWizard wizard;

	private Composite composite;

	java.util.List<ModelAttribute> oneToManyRelatedEnt;


	Map<RelationKey, CPFScreen> nestedJspsCPFScreenMap = new HashMap<RelationKey, CPFScreen>();
	
	Map<String, CPFScreen> localMap = new HashMap<String, CPFScreen>();
	
	public CPFScreenNestedJSPsNamesPage(ISelection selection,
			CPFScreenCreationWizard wizard) {
		super("NestedJSPPage");
		setTitle("New Provisioning Screen Creation");
		setDescription("Define the XHTML Names for the Related Entities");
		this.wizard = wizard;
	}

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
		group.setText("Define the Nested XHTML Names:");

		// Create the table
		final Text[] jspName = new Text[oneToManyRelatedEnt.size()];

		for (int k = 0; k < oneToManyRelatedEnt.size(); k++) {
			
			final int i = k;
			String jspNm = oneToManyRelatedEnt.get(i).getName();
			final Label lb = new Label(group, SWT.NONE);
			lb.setText("XHTML Name For Entity " + jspNm + ":");
			GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
			jspName[i] = new Text(group, SWT.BORDER);
			gridData.horizontalSpan = 3;
			jspName[i].setLayoutData(gridData);
			
			java.util.List<CPFAttribute> attList = wizard
					.getThirdPage().getUpdatedAttributesList();

			java.util.List<CPFAttribute> entAttList = new ArrayList<CPFAttribute>();
			
			for (CPFAttribute cpf : attList) {
				//TODO check null 
				if (cpf.getRelationKey() != null) {
					LOG.info ("relation info: " + cpf.getRelationKey().getRelationShipInfo());
					if (cpf.getRelationKey().getRelationShipInfo().getPropertyName().equals (oneToManyRelatedEnt.get(i).getName())) {
						
						entAttList.add (cpf);
					}
				}
			}			

			if (! entAttList.isEmpty()) {
				
				final CPFScreen sc = new CPFScreen();
				sc.setBaseEntity(oneToManyRelatedEnt.get (i).getForeignEntity());
	
				sc.setSelectedAttributes(entAttList);
				int opId= wizard.getModelUtil().getNextOperationId();
				CPFPlugin.getDefault().log("Setting OPERATION ID Pool on Nested Screen!!! " + opId);
			    sc.setOperationIdPool(new Integer[]{opId});

				nestedJspsCPFScreenMap.put (entAttList.get (0).getRelationKey(), sc);
				
				localMap.put (jspNm, sc); //changed
			}
			
			jspName[i].addListener(SWT.Modify, new Listener() {
				public void handleEvent(Event e) {
					String lbtxt = lb.getText();
					String jspNm = jspName[i].getText();

					if(!jspNm.equals("")){
					    canFlip=true;
						CPFPlugin.getDefault().log("Can fliptoNext is...."+canFlip);
					}else{
						canFlip=false;
						CPFPlugin.getDefault().log("Can fliptoNext is...."+canFlip);
					}
					String entityName = lbtxt.substring(22);
					entityName=entityName.substring(0,entityName.indexOf(":"));

					IPath path = wizard.getFirstPage().getJSPfileLocation();
					String jspFile = ResourcesPlugin.getWorkspace().getRoot()
							.getFolder(path).getFile(jspNm).getFullPath()
							.toPortableString();
					CPFPlugin.getDefault().log(
							"The Jsp Name is!!!!!!!!!" + jspNm);
					CPFPlugin.getDefault().log(
							"The Jsp file  is!!!!!!!!!" + jspFile);
					CPFPlugin.getDefault().log(
							"Entity Name is!!!!!!!!!!" + entityName);

					localMap.get (entityName).setJspName(jspFile); //changed
					
					CPFPlugin.getDefault().log(
							"The JSPS Map is!!!!!!!!!!"
									+ nestedJspsCPFScreenMap);
					getWizard().getContainer().updateButtons();

				}
			});
		}

		setControl(composite);

		setPageComplete(false);
		Dialog.applyDialogFont(composite);
	}

	public void setOneToManyRelatedEntitiesNames(
			java.util.List<ModelAttribute> list) {
		oneToManyRelatedEnt = list;
	}

	public Map<RelationKey, CPFScreen> getNestedJspsNamesMap() {
		return nestedJspsCPFScreenMap;
	}
	
	boolean canFlip=false;
	
	public boolean canFlipToNextPage(){
		return canFlip;
	}

}
