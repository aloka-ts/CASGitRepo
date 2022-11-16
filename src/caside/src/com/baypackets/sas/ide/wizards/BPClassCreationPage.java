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
package com.baypackets.sas.ide.wizards;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.Signature;
import org.eclipse.jdt.ui.CodeGeneration;
import org.eclipse.jdt.ui.wizards.NewTypeWizardPage;
import org.eclipse.jdt.ui.wizards.NewTypeWizardPage.ImportsManager;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import com.baypackets.sas.ide.SasPlugin;
import com.genband.ase.alc.alcml.jaxb.xjc.CreateTimertype;

public abstract class BPClassCreationPage extends NewTypeWizardPage {

	public static final String LINE_DELIMITER = "\n";

	public BPClassCreationPage() {
		super(true, "New Java Class");
		setTitle("New Java Class");
		setDescription("Creates a New Java Class");
	}

	public void init(IStructuredSelection selection) {
		IJavaElement jelem = getInitialJavaElement(selection);
		initContainerPage(jelem);
		initTypePage(jelem);
		updateStatus();
	}

	protected void handleFieldChanged(String fieldName) {
		super.handleFieldChanged(fieldName);
		updateStatus();
	}

	private void updateStatus() {
		IStatus[] status = new IStatus[] { fContainerStatus, fPackageStatus,
				fTypeNameStatus, fModifierStatus, fSuperClassStatus,
				fSuperInterfacesStatus };

		// the mode severe status will be displayed and the OK button
		// enabled/disabled.
		updateStatus(status);
	}
	
	protected void createTypeMembers(IType type, ImportsManager imports, IProgressMonitor monitor) throws CoreException{
		
			type.createField(
					"private static Logger logger = Logger.getLogger("
							+ super.getTypeName() + ".class);", null,
					false, monitor);
			imports.addImport("org.apache.log4j.Logger");
		
	}

	protected void createMethod(IType type, ImportsManager imports,
			IProgressMonitor monitor, String methodName, String[] paramTypes,
			String[] paramNames, String[] exceptions, String returnType,
			String contents) {
		try {

			StringBuffer buffer = new StringBuffer();
			String RET_TYPE = Signature.createTypeSignature(returnType, true);
			String[] EXCEPTIONS = new String[exceptions.length];
			for (int i = 0; i < exceptions.length; i++) {
				EXCEPTIONS[i] = Signature.createTypeSignature(exceptions[i],
						true);
			}

			// Create comments for this method.
			String comment = !isAddComments() ? null : CodeGeneration
					.getMethodComment(type.getCompilationUnit(),
							type.getTypeQualifiedName('.'), methodName,
							paramNames, EXCEPTIONS, RET_TYPE, null,
							LINE_DELIMITER);
			if (comment != null) {
				buffer.append(comment);
				buffer.append(LINE_DELIMITER);
			}

			// Create the body of the method.
			buffer.append("public ");
			buffer.append(returnType);
			buffer.append(" ");
			buffer.append(methodName);
			buffer.append("( ");
			for (int i = 0; paramTypes != null && i < paramTypes.length; i++) {
				if (paramTypes[i] == null)
					continue;
				buffer.append(i == 0 ? "" : ", ");
				buffer.append(imports.addImport(paramTypes[i]));
				buffer.append(" ");
				buffer.append(paramNames[i]);
			}
			buffer.append(")");

			for (int i = 0; exceptions != null && i < exceptions.length; i++) {
				if (exceptions[i] == null)
					continue;
				buffer.append(i == 0 ? " throws " : ", ");
				buffer.append(imports.addImport(exceptions[i]));
			}

//			if (type.getField("logger") == null) {
//				type.createField(
//						"private static Logger logger = Logger.getLogger("
//								+ super.getTypeName() + ".class);", null,
//						false, monitor);
//				imports.addImport("org.apache.log4j.Logger");
//			}
			buffer.append(" {");
			buffer.append(LINE_DELIMITER);
			buffer.append("if(logger.isInfoEnabled()){")
					.append(LINE_DELIMITER)
					.append("logger.info(\"Entering " + methodName + " of "
							+ super.getTypeName() + "\");")
					.append(LINE_DELIMITER).append("}");
			final String content = contents != null ? contents : CodeGeneration
					.getMethodBodyContent(type.getCompilationUnit(),
							type.getTypeQualifiedName('.'), methodName, false,
							"", LINE_DELIMITER);
			if (content != null && content.length() != 0) {
				buffer.append(content);
			}
			buffer.append("}");

			type.createMethod(buffer.toString(), null, false, monitor);
		} catch (Exception e) {
			SasPlugin.getDefault().log(e.getMessage(), e);
		}
	}

	public void createControl(Composite parent) {
		initializeDialogUnits(parent);

		Composite composite = new Composite(parent, SWT.NONE);
		composite.setFont(parent.getFont());

		int nColumns = 4;

		GridLayout layout = new GridLayout();
		layout.numColumns = nColumns;
		composite.setLayout(layout);

		// pick & choose the wanted UI components
		createContainerControls(composite, nColumns);
		createPackageControls(composite, nColumns);
		// createEnclosingTypeControls(composite, nColumns);

		createSeparator(composite, nColumns);
		createTypeNameControls(composite, nColumns);
		createModifierControls(composite, nColumns);

		createSuperClassControls(composite, nColumns);
		createSuperInterfacesControls(composite, nColumns);

		createSeparator(composite, nColumns);
		this.createCustomControls(composite, nColumns);

		createSeparator(composite, nColumns);
		createCommentControls(composite, nColumns);
		enableCommentControl(true);

		setControl(composite);

		Dialog.applyDialogFont(composite);
	}

	protected abstract void createCustomControls(Composite parent, int nColumns);

	protected void createEmptySpace(Composite parent, int span) {
		Label space = new Label(parent, SWT.LEFT);
		GridData gd = new GridData();
		gd.horizontalAlignment = GridData.BEGINNING;
		gd.grabExcessHorizontalSpace = false;
		gd.horizontalSpan = span;
		gd.horizontalIndent = 0;
		gd.widthHint = 0;
		gd.heightHint = 0;
		space.setLayoutData(gd);
	}
	
	 protected abstract void AddFieldToDescriptor(IProgressMonitor monitor);
		

}
