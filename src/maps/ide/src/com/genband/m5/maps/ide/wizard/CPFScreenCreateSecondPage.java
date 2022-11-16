package com.genband.m5.maps.ide.wizard;

import java.io.File;
import java.net.URL;
import java.util.Iterator;

import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;

import com.genband.m5.maps.common.CPFConstants;
import com.genband.m5.maps.common.CPFConstants.RelationshipType;
import com.genband.m5.maps.ide.CPFPlugin;
import com.genband.m5.maps.ide.model.CPFAttribute;
import com.genband.m5.maps.ide.model.ModelAttribute;
import com.genband.m5.maps.ide.model.ModelEntity;
import com.genband.m5.maps.ide.model.RelationKey;

public class CPFScreenCreateSecondPage extends WizardPage {

	private List availableAttributesUI;

	private List selectedAttributesUI;

	private List availableEntitiesUI;

	private List selectedEntitiesUI;

	private ModelEntity baseEntity;

//	public java.util.List<CPFAttribute> selectedAttributes;
//
//	public java.util.List<CPFAttribute> availableAttributes;
//
//	public java.util.List<ModelAttribute> selectedEntities;
	
	public java.util.List<CPFAttribute> availableAttributes = new java.util.ArrayList<CPFAttribute>();
	public java.util.List<CPFAttribute> selectedAttributes = new java.util.ArrayList<CPFAttribute>();
	public java.util.List<ModelAttribute> selectedEntities = new java.util.ArrayList<ModelAttribute>();

	Composite composite = null;

	CCombo baseEntityUI;

	Text barName = null;

	String bar = null;

	java.util.List<ModelEntity> availableEntities; //all entities available on drop-down for the project

	java.util.Map<String, ModelAttribute> dependAndRelEntities = null; //all entities that are related to base
	
	boolean nestedAttSelected = false;

	boolean flipToNextPage = false;

	public static int GropBarCount = 1;

	boolean isListAndDelete = false;

	private CPFScreenCreationWizard wizard;

	private CPFPlugin LOG = CPFPlugin.getDefault();

	public CPFScreenCreateSecondPage(ISelection selection,
			CPFScreenCreationWizard wizard) {
		super("SecondPage");
		setTitle("New Provisioning Screen Creation");
		setDescription("Select the entities and their attributes from the lists for the selected base entity");
		this.wizard = wizard;
	}

	public void createControl(Composite parent) {

		CPFPlugin.getDefault().log("The createControl called on SecondPage.........");
		initializeDialogUnits(parent);
		Composite composite = new Composite(parent, SWT.NONE);
		composite.setFont(parent.getFont());
		int nColumns = 4;

		GridLayout layout = new GridLayout();
		layout.numColumns = nColumns;
		composite.setLayout(layout);
		composite.setSize(400, 400);
		composite.pack();

		Group groupM = new Group(composite, GridData.FILL_HORIZONTAL);
		GridLayout layout1 = new GridLayout();
		layout1.numColumns = nColumns;
		groupM.setLayout(layout1);
		GridData gridD = new GridData(GridData.FILL_HORIZONTAL);
		gridD.horizontalSpan = nColumns;
		groupM.setLayoutData(gridD);

		Group group = new Group(groupM, GridData.FILL_HORIZONTAL);
		layout1 = new GridLayout();
		layout1.numColumns = nColumns;
		group.setLayout(layout1);
		gridD = new GridData(GridData.FILL_HORIZONTAL);
		gridD.horizontalSpan = nColumns;
		group.setLayoutData(gridD);
		group.setText("Base Entity");

		new Label(group, SWT.LEFT | SWT.WRAP).setText("Select Base Entity:");
		gridD = new GridData(GridData.FILL_HORIZONTAL);
		baseEntityUI = new CCombo(group, SWT.BORDER
				| SWT.READ_ONLY);
		gridD.horizontalSpan = 3;
		gridD.grabExcessHorizontalSpace = true;
		baseEntityUI.setLayoutData(gridD);
		// Load the base entities using ModelUtil
		loadBaseEntities();
		baseEntityUI.select(0);
		baseEntityUI.addListener(SWT.Modify, new Listener() {
			public void handleEvent(Event e) {
				handleBaseEntitySelection();
			}
		});

		group = new Group(groupM, GridData.FILL_HORIZONTAL);
		layout1 = new GridLayout();
		layout1.numColumns = 5;
		group.setLayout(layout1);
		gridD = new GridData(GridData.FILL_HORIZONTAL);
		gridD.horizontalSpan = 5;
		group.setLayoutData(gridD);
		group.setText("Related Entities and Attributes");

		gridD = new GridData();
		new Label(group, SWT.LEFT | SWT.WRAP).setText("Select"+"\n"+ "Entities:");
		availableEntitiesUI = new List(group, SWT.MULTI | SWT.BORDER | SWT.V_SCROLL
				| SWT.H_SCROLL);
		gridD.horizontalIndent = 5;
		gridD.heightHint = 110;
		gridD.widthHint = 140;
		availableEntitiesUI.setLayoutData(gridD);
		availableEntitiesUI.setEnabled(true);
		availableEntitiesUI.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event e) {

			}
		});

		availableEntitiesUI.addListener(SWT.MouseDoubleClick, new Listener() {

			public void handleEvent(Event e) {
				LOG.info("MouseDoubleClick to add an entity");
				
				processAddEntityEvent ();

			}
		});

		GridData gridData2 = new GridData();
		Button add = new Button(group, SWT.PUSH); // Selecting one of the
													// entities
													// (related/dependent)
		add.setText(">>");
		add.setLayoutData(gridData2);
		add.addListener(SWT.Selection, new Listener() {

			public void handleEvent(Event e) {
				LOG.info("Got selection event to add an  entity");

				processAddEntityEvent ();
			}
		});

		GridData gd = new GridData();
		Button remove = new Button(group, SWT.PUSH);
		gd.horizontalSpan = 1;
		remove.setText("<<");
		remove.setLayoutData(gd);

		remove.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event e) {

				LOG.info("Remove button for entity got a selection event");

				processRemoveEntityEvent();
			}
		});

		GridData gr = new GridData();
		selectedEntitiesUI = new List(group, SWT.MULTI | SWT.BORDER | SWT.V_SCROLL
				| SWT.H_SCROLL);
		gr.heightHint = 110;
		gr.widthHint = 140;
		// gr.grabExcessVerticalSpace = true;
		selectedEntitiesUI.setLayoutData(gr);
		selectedEntitiesUI.setEnabled(true);

		selectedEntitiesUI.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event e) {
				if (selectedEntitiesUI.getSelection() != null) {

				}
			}
		});

		selectedEntitiesUI.addListener(SWT.MouseDoubleClick, new Listener() {
			public void handleEvent(Event e) {
				
				LOG.info("Mouse double click event for removing selected entity");
				processRemoveEntityEvent ();
			}
		});new Label(group, SWT.LEFT | SWT.WRAP).setText("Select"+"\n"+"Attributes:");
		gridD = new GridData();
		availableAttributesUI = new List(group, SWT.MULTI | SWT.BORDER | SWT.V_SCROLL
				| SWT.H_SCROLL);
		gridD.horizontalIndent = 5;
		gridD.heightHint = 110;
		gridD.widthHint = 140;
		availableAttributesUI.setLayoutData(gridD);
		availableAttributesUI.setEnabled(true);

		availableAttributesUI.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event e) {
			}
		});
		availableAttributesUI.addListener(SWT.MouseDoubleClick, new Listener() {
			public void handleEvent(Event e) {

				LOG.info("Mouse double click event to add attributes");
				processAddAttributeEvent ();
			}
		});

		gridData2 = new GridData();
		add = new Button(group, SWT.PUSH);
		add.setText(">>");
		add.setLayoutData(gridData2);

		add.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event e) {

				LOG.info("Selection event to add attributes");
				processAddAttributeEvent ();
			}
		});

		gd = new GridData();
		remove = new Button(group, SWT.PUSH);
		gd.horizontalSpan = 1;
		remove.setText("<<");
		remove.setLayoutData(gd);

		remove.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event e) {

				LOG.info("Selection event on remove button of attribute");
				processRemoveAttributeEvent ();
			}
		});

		gr = new GridData();
		selectedAttributesUI = new List(group, SWT.MULTI | SWT.BORDER | SWT.V_SCROLL
				| SWT.H_SCROLL);
		gr.heightHint = 110;
		gr.widthHint = 140;
		// gr.grabExcessVerticalSpace = true;
		selectedAttributesUI.setLayoutData(gr);
		selectedAttributesUI.setEnabled(true);

		selectedAttributesUI.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event e) {
				if (selectedAttributesUI.getSelection() != null) {

				}
			}
		});

		selectedAttributesUI.addListener(SWT.MouseDoubleClick, new Listener() {
			public void handleEvent(Event e) {

				LOG.info("Mouse Double click event on remove button of attribute");
				processRemoveAttributeEvent ();
			}
		});

		GridData grd = new GridData();
		grd.widthHint = 100;
		grd.horizontalIndent = 120;
		Button addBar = new Button(composite, SWT.PUSH);
		addBar.setText("Add Group Bar");
		addBar.setLayoutData(grd);

		if (!this.isListAndDelete
			&&this.wizard.getFirstPage().getInterfaceTypeList().contains(CPFConstants.InterfaceType.PORTLET)) {
			addBar.setEnabled(true);
		}else{
			addBar.setEnabled(false);
		}

		addBar.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				String gpBarName = "Group BAR" + GropBarCount++;
				CPFAttribute cpfAtt = new CPFAttribute(gpBarName);
				cpfAtt.setGroup(true);
				selectedAttributesUI.add(gpBarName);
				selectedAttributes.add(cpfAtt);
			}

		});

		GridData grid = new GridData();
		Button up = new Button(composite, SWT.PUSH);
		up.setText("Move up");
		up.setLayoutData(grid);
		up.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event e) {
				
               if (selectedAttributesUI.getSelection() != null) {
					
					int j = selectedAttributesUI.getSelectionIndex();
					if (j > 0) {
						String preItem = selectedAttributesUI.getItem(j - 1);
						String item = selectedAttributesUI.getItem(j);
						
						if(item.indexOf("<n>")!=-1){
							
							if(preItem.indexOf("<n>")!=-1){
							  selectedAttributesUI.remove(j - 1);
							  selectedAttributesUI.add(preItem, j);
							
							  // modifying
							  CPFAttribute preAtt = (CPFAttribute) selectedAttributes
									.get(j - 1);
							  selectedAttributes.remove(j - 1);
							  selectedAttributes.add(j, preAtt);
							}
						}else{
							selectedAttributesUI.remove(j - 1);
							  selectedAttributesUI.add(preItem, j);
							
							  // modifying
							  CPFAttribute preAtt = (CPFAttribute) selectedAttributes
									.get(j - 1);
							  selectedAttributes.remove(j - 1);
							  selectedAttributes.add(j, preAtt);
						}

						
					}

				}


			}
		});

		GridData g = new GridData();
		Button down = new Button(composite, SWT.PUSH);
		down.setText("Move down");
		down.setLayoutData(g);
		down.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event e) {
				if (selectedAttributesUI.getSelection() != null) {

					int j = selectedAttributesUI.getSelectionIndex();
					if (selectedAttributesUI.getItems().length > j + 1) {
						String preItem = selectedAttributesUI.getItem(j + 1);
						String item = selectedAttributesUI.getItem(j);
						if(item.indexOf("<n>")!=-1){
						  if(preItem.indexOf("<n>")!=-1){	
							selectedAttributesUI.remove(j + 1);
							selectedAttributesUI.add(preItem, j);
							// modifying
							CPFAttribute preAtt = (CPFAttribute) selectedAttributes
									.get(j + 1);
							selectedAttributes.remove(j + 1);
							selectedAttributes.add(j, preAtt);
							}
						}else{
							selectedAttributesUI.remove(j + 1);
							selectedAttributesUI.add(preItem, j);
							// modifying
							CPFAttribute preAtt = (CPFAttribute) selectedAttributes
									.get(j + 1);
							selectedAttributes.remove(j + 1);
							selectedAttributes.add(j, preAtt);
						}
					}

				}
			}
		});

		this.handleBaseEntitySelection();
		setControl(composite);
		Dialog.applyDialogFont(composite);
	}

	public void loadBaseEntities() {
		URL[] jarUrls=null;
		wizard.getModelUtil().getEntityMap().clear();
		CPFPlugin.getDefault().log("Map has been cleared!!!");
		try {
			String path = Platform.getLocation().toOSString()
					+ this.wizard.getFirstPage().getProjectHandle().getFolder(
							"EJBContent").getFullPath().toOSString();
			
			File file = new File(path);
			File[] files = file.listFiles();
			CPFPlugin.getDefault().log(
					"The file Obtained is...." + file
							+ " The list of files is.." + files);
		
			if (files != null) {
				jarUrls=new URL[files.length];
				CPFPlugin
						.getDefault()
						.log(
								"Input file is a directory. Call the method recursively.");
				for (int i = 0; i < files.length; i++) {
					String fileName = files[i].getName();
					if (fileName.endsWith(".jar")) {
					CPFPlugin.getDefault().log(
									"The Entity jar file issssss..." + fileName +" Absolute path is .."+files[i].getAbsolutePath());
							jarUrls[i]=new URL("file:///"+files[i].getAbsolutePath());
							

						wizard.getModelUtil().setEJBContentPath(jarUrls);
							CPFPlugin.getDefault().log(
									"The Entity jar file issssss..." + fileName +" Absolute path is .."+files[i].getAbsolutePath());
							

					}
				}
				
				for (int i = 0; i < files.length; i++) {
					String fileName = files[i].getName();
					CPFPlugin.getDefault().log(
							"The Entity jar file issssss..." + fileName);
					if (fileName.endsWith(".jar")) {
						wizard.getModelUtil().getEntities(files[i]); //ModelUtil loads the list in memory

					}
				}
			}

			LOG.info("Current content of entity map: "
					+ wizard.getModelUtil().getEntityMap());
			availableEntities = wizard.getModelUtil().getEntityList();

			for (int j = 0; j < availableEntities.size(); j++) {
				ModelEntity perEntity = (ModelEntity) availableEntities.get(j);
				if (!perEntity.isWeakEntity()) {
					baseEntityUI.add(perEntity.getName());
				}
			}

		} catch (Throwable t) {
			LOG.error("Got error while loading base entities", t);
		}
	}

	public java.util.List<CPFAttribute> getSelectedAttributes() {
		return selectedAttributes;
	}

	public java.util.List<ModelAttribute> getSelectedOtherEntities() {
		return selectedEntities;
	}

	public ModelEntity getBaseEntity() {
		return baseEntity;
	}

	public boolean canFlipToNextPage() {
		
		boolean canFlipToNext = false;

		if (baseEntity != null) {

			for (CPFAttribute cpf : selectedAttributes) {
				
				for(ModelAttribute basemA :baseEntity.getAttribList()){
				if(cpf.getModelAttrib()!=null&&cpf.getModelAttrib().equals(basemA)){		
                                                canFlipToNext = true;
						break;
					}
				}
			}
		}
		
		LOG.info ("Can Flip to third page........" + canFlipToNext + "Selected Att list is " + selectedAttributes.toString());
		return canFlipToNext;
	}

	public void setGroupBarCount(int a) {
		GropBarCount = a;
	}

	public void isListAndDelete(boolean isListAndDelete) {
		this.isListAndDelete = isListAndDelete;
	}
	
	
	public boolean ifOnetoManyRelatedAttSelected(){
		return nestedAttSelected;
	}
	
	private void processRemoveAttributeEvent () {
		String[] attrib = selectedAttributesUI.getSelection();
		
		if (attrib == null) {
			return;
		}
		
		for (int j = 0; j < attrib.length; j++) {
			LOG.info("MouseDoubleClick event to remove attribute.." + attrib[j]);
			
	//		selectedAttributesUI.remove (attrib[j]);
			
			for (Iterator<CPFAttribute> iterator = selectedAttributes.iterator(); iterator.hasNext();) {
				CPFAttribute cpf = iterator.next();
				if (cpf.getName().equals (attrib[j])) {
					LOG.info ("Got cpf attrib: " + cpf);
			//		iterator.remove ();
					
					if(cpf.isGroup()){
						selectedAttributesUI.remove (attrib[j]);
						iterator.remove ();
					}
					 if(! cpf.isGroup()&& cpf.getModelAttrib().isRequired() 
 							&&  (isListAndDelete || !wizard.getFirstPage().getRolesforView().isEmpty())){
						    selectedAttributesUI.remove (attrib[j]);
							iterator.remove ();
							availableAttributes.add (cpf);
							availableAttributesUI.add (attrib[j]);
                         }
					if (! cpf.isGroup() && !cpf.getModelAttrib().isRequired()) {
						selectedAttributesUI.remove (attrib[j]);
						iterator.remove ();
						availableAttributes.add (cpf);
						availableAttributesUI.add (attrib[j]);
					}
				}
				
			}
		}
		getWizard().getContainer().updateButtons();

	}
	
	private void processAddAttributeEvent () {
		String[] attrib = availableAttributesUI.getSelection();
		
		if (attrib == null) {
			return;
		}
		
		for (int j = 0; j < attrib.length; j++) {
			LOG.info("MouseDoubleClick event to add attribute.." + attrib[j]);
			
			selectedAttributesUI.add (attrib[j]);
			availableAttributesUI.remove (attrib[j]);
			
			for (Iterator<CPFAttribute> iterator = availableAttributes.iterator(); iterator.hasNext();) {
				CPFAttribute cpf = iterator.next();
				if (cpf.getName().equals (attrib[j])) {
					LOG.info ("Got cpf attrib: " + cpf);
					selectedAttributes.add (cpf);
					iterator.remove();
				}
				
			}
		}
		getWizard().getContainer().updateButtons();

	}
	private void processRemoveEntityEvent() {
		String[] entitySelection = selectedEntitiesUI.getSelection();

		if (entitySelection == null) {
			return;
		}

		for (int j = 0; j < entitySelection.length; j++) {

			
            ModelAttribute ent = dependAndRelEntities.get(entitySelection[j]);
			
			if(ent.isRequired() 
					&&  !isListAndDelete 
					&& ( !wizard.getFirstPage().getRolesforCreate().isEmpty() 
                		|| !wizard.getFirstPage().getRolesforModify().isEmpty())){
				return;
			}
			
			selectedEntitiesUI.remove(entitySelection[j]);
			availableEntitiesUI.add(entitySelection[j]);

			if (selectedEntities.contains(ent)) {
				
				LOG.info("Removing entity: " + ent.getName());
				selectedEntities.remove(ent);

				java.util.List<ModelAttribute> referredAttrList = ent.getForeignEntity().getAttribList();
				for (Iterator<CPFAttribute> iterator = availableAttributes.iterator(); iterator.hasNext();) {
					CPFAttribute attr = iterator.next();
					for (ModelAttribute referredAttr : referredAttrList) {
						String attName =getAttributeNameForUI (getBaseName(ent), referredAttr);
						if (attr.getName().equals(attName)) {
							availableAttributesUI.remove(attName);
							iterator.remove();
						}						
					}

				}
				
				
				for (Iterator<CPFAttribute> iterator = selectedAttributes.iterator(); iterator.hasNext();) {
					CPFAttribute attr = iterator.next();
					for (ModelAttribute referredAttr : referredAttrList) {
						String attName =getAttributeNameForUI (getBaseName(ent), referredAttr);
						if (attr.getName().equals(attName)) {
							selectedAttributesUI.remove(attName);
							iterator.remove();
						}						
					}

				}

				LOG.info("removed Entity........**************"
								+ ent.getName());
			}
		}

	}
	
	
	private void processAddRequiredEntitiesAttributes(ModelAttribute entityAtt){
		
			if (entityAtt != null) {

				LOG.log("Add Entity......." + entityAtt.getName());

				if (entityAtt.getForeignEntity().getAttribList() != null) {
					for (int k = 0; k < entityAtt.getForeignEntity()
							.getAttribList().size(); k++) {
						ModelAttribute mAtt = entityAtt.getForeignEntity()
								.getAttribList().get(k);

						String refName = getBaseName (entityAtt);

						// only for basic attributes for listing
						// of related/dependent ones
						if (!mAtt.isFK()
								|| (!isListAndDelete && !mAtt.getForeignEntity().isWeakEntity())
								   && !mAtt.getForeignEntity().equals(baseEntity)){

							String attName = getAttributeNameForUI(refName, mAtt);
					
							CPFAttribute cpf = new CPFAttribute();
							cpf.setModelAttrib(mAtt);
							cpf.setName(attName);
							RelationKey key = new RelationKey ();
							key.setParentEntity(entityAtt.getEntity());
							key.setReferencedEntity(entityAtt.getForeignEntity());
							key.setRelationShipInfo(entityAtt.getRelType());
							//added for InverseRelationShipInfo problem
							key.setInverseRelationShipInfo(entityAtt.getInverseRelType());
                            cpf.setRelationKey (key);
                           
                            if(mAtt.isRequired() 
        							&&  !isListAndDelete 
        							&& ( !wizard.getFirstPage().getRolesforCreate().isEmpty() 
                                		|| !wizard.getFirstPage().getRolesforModify().isEmpty())){
        							selectedAttributes.add(cpf);
                                	selectedAttributesUI.add(attName);
                                }else {
                                	availableAttributesUI.add(attName);
                				    availableAttributes.add(cpf);
                                }

						}
					}
				}
			}

	}
	
	private void processAddEntityEvent() {
		
		String[] availableEntitySelection = availableEntitiesUI.getSelection();

		LOG.info("Entities Selected are: " + availableEntitySelection);
		
		if (availableEntitySelection == null) {
			return;
		}

		ModelAttribute entityAtt = null;
		for (int j = 0; j < availableEntitySelection.length; j++) {

			selectedEntitiesUI.add(availableEntitySelection[j]);
			availableEntitiesUI.remove(availableEntitySelection[j]);

			if (dependAndRelEntities.get(availableEntitySelection[j]) != null) {
				entityAtt = dependAndRelEntities
						.get(availableEntitySelection[j]);
				LOG.log("Add Entity......." + entityAtt.getName());

				selectedEntities.add(entityAtt);

				if (entityAtt.getForeignEntity().getAttribList() != null) {
					for (int k = 0; k < entityAtt.getForeignEntity()
							.getAttribList().size(); k++) {
						ModelAttribute mAtt = entityAtt.getForeignEntity()
								.getAttribList().get(k);

						String refName = getBaseName (entityAtt);

						// only for basic attributes for listing
						// of related/dependent ones
						if (!mAtt.isFK()
								|| (!isListAndDelete && !mAtt.getForeignEntity().isWeakEntity())
								   && !mAtt.getForeignEntity().equals(baseEntity)){

							String attName = getAttributeNameForUI(refName, mAtt);
					
							CPFAttribute cpf = new CPFAttribute();
							cpf.setModelAttrib(mAtt);
							cpf.setName(attName);
							RelationKey key = new RelationKey ();
							key.setParentEntity(entityAtt.getEntity());
							key.setReferencedEntity(entityAtt.getForeignEntity());
							key.setRelationShipInfo(entityAtt.getRelType());
							//added for InverseRelationShipInfo problem
							key.setInverseRelationShipInfo(entityAtt.getInverseRelType());
                            cpf.setRelationKey (key);
                           
                            if(mAtt.isRequired() 
        							&&  !isListAndDelete 
        							&& ( !wizard.getFirstPage().getRolesforCreate().isEmpty() 
                                		|| !wizard.getFirstPage().getRolesforModify().isEmpty())){
        							selectedAttributes.add(cpf);
                                	selectedAttributesUI.add(attName);
                                }else {
                                	availableAttributesUI.add(attName);
                				    availableAttributes.add(cpf);
                                }

						}
					}
				}
			}

		}

	}
	
	private String getEntityNameForUI (ModelAttribute m) {
		return m.getRelType().getSimpleTypeInfo() + "[ " + m.getName() + " ]";
	}

	private String getAttributeNameForUI (String base, ModelAttribute m) {
		String name = "";
		if (m.isPK()) {
			if(m.isRequired() 
					&& !isListAndDelete
					&& (   !wizard.getFirstPage().getRolesforCreate().isEmpty()
					    || !wizard.getFirstPage().getRolesforModify().isEmpty())){
				
              name = (base + "."
						+ m.getName() + "( * )" + "( PK )");
				LOG
				.info("getAttributeNameForUI for related and required attribute: "
						+ name);
			} else {
				
				name = (base + "."
						+ m.getName() + "( PK )");
				LOG
				.info("getAttributeNameForUI for  related and optional attribute: "
						+ name);

			}
		} else {
			if(m.isRequired() 
					&& !isListAndDelete
					&& (   !wizard.getFirstPage().getRolesforCreate().isEmpty()
					    || !wizard.getFirstPage().getRolesforModify().isEmpty())){ 
				

				name = (base + "."
						+ m.getName() + "( * )");
				LOG
				.info("getAttributeNameForUI for related and required attribute: "
						+ name);
			} else {
				
				name = (base + "."
						+ m.getName());
				LOG
				.info("getAttributeNameForUI for related and optional attribute: "
						+ name);

			}
		}
		return name;
	}
	
	
    private void handleBaseEntitySelection(){
    	
    	LOG.info("base entity has been modified in selection");
		if (availableEntitiesUI != null) {
			availableEntitiesUI.removeAll(); // clear related entities box
		}

		if (baseEntity != null) {
			CPFPlugin.getDefault().log(
					"Removing old base entity......."
							+ baseEntity.getName());

			selectedEntities.clear(); // clean selected entity objects
			selectedEntitiesUI.removeAll(); // clear selected entities box

			availableAttributesUI.removeAll(); // clear attributes box
			selectedAttributesUI.removeAll(); // clear selected attributes box
			availableAttributes.clear(); // clean attribute objects
			selectedAttributes.clear(); // clean selected attribute
										// objects

		}
    	for (int i = 0; i < availableEntities.size(); i++) {
			ModelEntity perEntity = (ModelEntity) availableEntities.get(i);
			if (perEntity.getName().equals(baseEntityUI.getText())) {

				baseEntity = perEntity;
				LOG.log("The Base entity selected is...."
						+ baseEntity.getName());
			}
		}
		dependAndRelEntities = new java.util.HashMap<String, ModelAttribute>();
		
		for (int k = 0; k < baseEntity.getAttribList().size(); k++) {
			
			ModelAttribute mAtt = baseEntity.getAttribList().get(k);
			LOG.log("The Base Entity Attribute...." + k + " is "
					+ mAtt.getName());

			if (mAtt.isFK()) {
				
				LOG.info("Found a foreign key: " + mAtt.getName());

				if (mAtt.getForeignEntity().isWeakEntity()) {
					
					//Edited by reeta
					if(mAtt.isRequired() 
						&& !isListAndDelete
						&& (   !wizard.getFirstPage().getRolesforCreate().isEmpty()
						    || !wizard.getFirstPage().getRolesforModify().isEmpty())){
						//selectedEntities.add(mAtt);
						 selectedEntitiesUI.add(getEntityNameForUI (mAtt) + "(*)");
						 selectedEntities.add(mAtt);
						 dependAndRelEntities.put (getEntityNameForUI (mAtt)+ "(*)", mAtt);
						 // add required entity attributes to the attribute lists
						 processAddRequiredEntitiesAttributes(mAtt);
					} else {
						availableEntitiesUI.add(getEntityNameForUI (mAtt)); // always add a dependent
						dependAndRelEntities.put (getEntityNameForUI (mAtt), mAtt);
					}
				
				}
				else { //related not weak makes sense for list page only
					
					if (! wizard.getFirstPage().iscreateAndUpdate()) { // list page

						LOG.log("Adding related entity to the entitylist......."
										+ mAtt.getName());
						
						dependAndRelEntities.put (getEntityNameForUI (mAtt), mAtt);
						
						availableEntitiesUI.add(getEntityNameForUI (mAtt)); // only for list page
						
					}else{//..
						String attName = getAttributeNameForUI (baseEntity.getName(), mAtt);
						CPFAttribute cpf = new CPFAttribute ();
						cpf.setModelAttrib (mAtt);
						cpf.setName (attName);
						
						if(mAtt.isRequired() 
							&&  !isListAndDelete 
							&& ( !wizard.getFirstPage().getRolesforCreate().isEmpty() 
                        		|| !wizard.getFirstPage().getRolesforModify().isEmpty())){
							selectedAttributes.add(cpf);
                        	selectedAttributesUI.add(attName);
                        }else {
                        	availableAttributesUI.add(attName);
        				    availableAttributes.add(cpf);
                        }

						
					}
				}

			} else {
				
				LOG.info("Found a basic attribute: " + mAtt.getName());

				String attName = getAttributeNameForUI (baseEntity.getName(), mAtt);
				CPFAttribute cpf = new CPFAttribute ();
				cpf.setModelAttrib (mAtt);
				cpf.setName (attName);
				
				if(mAtt.isRequired() 
						&&  !isListAndDelete 
						&& ( !wizard.getFirstPage().getRolesforCreate().isEmpty() 
                    		|| !wizard.getFirstPage().getRolesforModify().isEmpty())){
						selectedAttributes.add(cpf);
                    	selectedAttributesUI.add(attName);
                    }else {
                    	availableAttributesUI.add(attName);
    				    availableAttributes.add(cpf);
                    }

			}
		}
		getWizard().getContainer().updateButtons();
    	
    }
	
	private String getBaseName (ModelAttribute m) {
		String refName = m.getName();

		if (m.getRelType().getMapping().equals(
				RelationshipType.OneToMany)
				|| m.getRelType().getMapping().equals(
						RelationshipType.ManyToMany))

		refName = refName + "<n>";
		
		return refName;
	}

}
