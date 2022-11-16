package com.baypackets.sas.ide.wizards;

import java.util.ArrayList;
import java.util.ResourceBundle;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.Signature;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.jdt.ui.wizards.NewTypeWizardPage;

import com.baypackets.sas.ide.util.IdeUtils;

/**
	* This class creates the SBB Listener 
*/

public class BPSbbListenerPage extends BPClassCreationPage 
{
	private static final String[] NULL_PARAMS = new String[0];
	private static final String INT_TYPE = "int".intern();
	private static final String HANDLE_EVENT = "handleEvent".intern();
	private static final String SBB = "com.baypackets.ase.sbb.SBB".intern();
	private static final String SBB_EVENT = "com.baypackets.ase.sbb.SBBEvent".intern();
	private static final String SBB_EVENT_LISTENER = "com.baypackets.ase.sbb.SBBEventListener".intern();
	private static final String MS_SBB = "com.baypackets.ase.sbb.MsSessionController".intern();
	
	private static final String[] PARAM_TYPES = new String[] {SBB, SBB_EVENT};
	private static final String[] PARAM_NAMES = new String[] {"sbb".intern(), "event".intern()};
	
	private static final ResourceBundle bundle = ResourceBundle.getBundle("com.baypackets.sas.ide.wizards.sbb_listener");
	//private static final ResourceBundle bundle = ResourceBundle.getBundle("sbb_listener");
	
	private static final String STR_DELIM = "$delim".intern();

	private static final String STR_IF_CLAUSE = bundle.getString("IF_CLAUSE").intern();
	private static final String STR_CONNECTED = bundle.getString("CONNECTED").intern();
	private static final String STR_CONNECT_PROGRESS = bundle.getString("CONNECT_PROGRESS").intern();
	private static final String STR_CONNECT_FAILED = bundle.getString("CONNECT_FAILED").intern();
	private static final String STR_HOLD_COMPLETED = bundle.getString("HOLD_COMPLETED").intern();
	private static final String STR_HOLD_FAILED = bundle.getString("HOLD_FAILED").intern();
	private static final String STR_RESYNC_COMPLETED = bundle.getString("RESYNC_COMPLETED").intern();
	private static final String STR_RESYNC_FAILED = bundle.getString("RESYNC_FAILED").intern();

	private static final String STR_UNHOLD_COMPLETED = bundle.getString("UNHOLD_COMPLETED").intern();
	private static final String STR_UNHOLD_FAILED = bundle.getString("UNHOLD_FAILED").intern();
	
	private static final String STR_DISCONNECTED = bundle.getString("DISCONNECTED").intern();
	private static final String STR_DISCONNECT_FAILED = bundle.getString("DISCONNECT_FAILED").intern();
	
	private static final String STR_EARLY_MEDIA = bundle.getString("EARLY_MEDIA").intern();
	private static final String STR_EARLY_MEDIA_CONNECT_PROGRESS = bundle.getString("EARLY_MEDIA_CONNECT_PROGRESS").intern();
	
	private static final String STR_SIG_IN_PROGRESS = bundle.getString("SIG_IN_PROGRESS").intern();
	private static final String STR_SIG_COMPLETED = bundle.getString("SIG_COMPLETED").intern();
	private static final String STR_SIG_FAILED = bundle.getString("SIG_FAILED").intern();
	
	
	private static final String STR_PLAY_COMPLETED = bundle.getString("PLAY_COMPLETED").intern();
	private static final String STR_PLAY_FAILED = bundle.getString("PLAY_FAILED").intern();
	private static final String STR_PLAY_COLLECT_COMPLETED = bundle.getString("PLAY_COLLECT_COMPLETED").intern();
	private static final String STR_PLAY_COLLECT_FAILED = bundle.getString("PLAY_COLLECT_FAILED").intern();
	private static final String STR_PLAY_RECORD_COMPLETED = bundle.getString("PLAY_RECORD_COMPLETED").intern();
	private static final String STR_PLAY_RECORD_FAILED = bundle.getString("PLAY_RECORD_FAILED").intern();
	private static final String STR_RECORD_COMPLETED = bundle.getString("RECORD_COMPLETED").intern();
	private static final String STR_RECORD_FAILED = bundle.getString("RECORD_FAILED").intern();;
	private static final String STR_STOP_RECORD_COMPLETED = bundle.getString("STOP_RECORD_COMPLETED").intern();
	private static final String STR_STOP_RECORD_FAILED = bundle.getString("STOP_RECORD_FAILED").intern();
		
	private String STR_PLAY_DIALOG_COMPLETED=bundle.getString("END_PLAY_DIALOG_COMPLETED").intern();
	private String STR_PLAY_DIALOG_FAILED=bundle.getString("END_PLAY_DIALOG_FAILED").intern();
	private String STR_PLAY_COLLECT_DIALOG_COMPLETED=bundle.getString("END_PLAY_COLLECT_DIALOG_COMPLETED").intern();
	private String STR_PLAY_COLLECT_DIALOG_FAILED=bundle.getString("END_PLAY_COLLECT_DIALOG_FAILED").intern();
	private String STR_RECORD_DIALOG_COMPLETED=bundle.getString("END_RECORD_DIALOG_COMPLETED").intern();
	private String STR_RECORD_DIALOG_FAILED=bundle.getString("END_RECORD_DIALOG_FAILED").intern();
	private String STR_PLAY_RECORD_DIALOG_COMPLETED=bundle.getString("END_PLAY_RECORD_DIALOG_COMPLETED").intern();
	private String STR_PLAY_RECORD_DIALOG_FAILED=bundle.getString("END_PLAY_RECORD_DIALOG_FAILED").intern();
	private String STR_AUDIT_COMPLETED=bundle.getString("AUDIT_COMPLETED").intern();
	private String STR_AUDIT_FAILED=bundle.getString("AUDIT_FAILED").intern();

	private static final String STR_RETURN_CONTINUE = bundle.getString("RETURN_CONTINUE").intern();
	
	public BPSbbListenerPage() 
	{
		setTitle("New SBB Event Listener");
		setDescription("Creates a New SBB Event Listener Class");
	}

	protected void createCustomControls(Composite parent, int nColumns) 
	{
		ArrayList interfaces = new ArrayList();
		interfaces.add(SBB_EVENT_LISTENER);
		setSuperClass("java.lang.Object", true);
		this.setSuperInterfaces(interfaces, false);
		
		this.createB2BControls(parent, nColumns);
		this.createMscmlControls(parent, nColumns);
		this.createMsmlControls(parent, nColumns);
	}
	
	protected String getHandleEventContents()
	{
		StringBuffer buffer = new StringBuffer();

		if(this.bConnected)
		{
			this.createIfClause(buffer, STR_CONNECTED);
			
			this.createIfClause(buffer, STR_CONNECT_PROGRESS);
		
			this.createIfClause(buffer, STR_CONNECT_FAILED);
		}
		if(this.bHoldCompleted)
		{
			this.createIfClause(buffer, STR_HOLD_COMPLETED);
		
			this.createIfClause(buffer, STR_HOLD_FAILED);
		}
		if(this.bResyncCompleted)
		{
			this.createIfClause(buffer, STR_RESYNC_COMPLETED);
		
			this.createIfClause(buffer, STR_RESYNC_FAILED);
		}
		if(this.bSigInProgress)
		{
			this.createIfClause(buffer, STR_SIG_IN_PROGRESS);
		
			this.createIfClause(buffer, STR_SIG_FAILED);
		
			this.createIfClause(buffer, STR_SIG_COMPLETED);
		}
		
		if(this.bdisconnect)
		{
			this.createIfClause(buffer, STR_DISCONNECTED);
		
			this.createIfClause(buffer, STR_DISCONNECT_FAILED);
		}
		if(this.bEarlyM)
		{
			this.createIfClause(buffer, STR_EARLY_MEDIA);
		
			this.createIfClause(buffer, STR_EARLY_MEDIA_CONNECT_PROGRESS);
		}
		
		if(this.bUnHold)
		{
			this.createIfClause(buffer, STR_UNHOLD_COMPLETED);
		
			this.createIfClause(buffer, STR_UNHOLD_FAILED);
		}
		
		
		if(this.bPlayComplete)
		{
			this.createIfClause(buffer, STR_PLAY_COMPLETED);
		
			this.createIfClause(buffer, STR_PLAY_FAILED);
		}
		if(this.bPlayCollectComplete)
		{
			this.createIfClause(buffer, STR_PLAY_COLLECT_COMPLETED);
			this.createIfClause(buffer, STR_PLAY_COLLECT_FAILED);
		}
		if(this.bRecordComplete)
		{
			this.createIfClause(buffer, STR_RECORD_COMPLETED);
			this.createIfClause(buffer, STR_RECORD_FAILED);
		}
		if(this.bPlayRecordComplete)
		{
			this.createIfClause(buffer, STR_PLAY_RECORD_COMPLETED);
		
		
			this.createIfClause(buffer, STR_PLAY_RECORD_FAILED);
		}
		
		if(this.bStopRecord)
		{
			this.createIfClause(buffer, STR_STOP_RECORD_COMPLETED);		
			this.createIfClause(buffer, STR_STOP_RECORD_FAILED);
			
		}
		
		/*
		 * Add MSML events
		 */
		
		if(bPlayDlgComplete){
            this.createIfClause(buffer, STR_PLAY_DIALOG_COMPLETED);
						
			this.createIfClause(buffer, STR_PLAY_DIALOG_FAILED);
		}
		if(bPlayCollectDlgComplete){
            this.createIfClause(buffer, STR_PLAY_COLLECT_DIALOG_COMPLETED);			
			
			this.createIfClause(buffer, STR_PLAY_COLLECT_DIALOG_FAILED);
		}
		if(bRecordDlgComplete){
            this.createIfClause(buffer, STR_RECORD_DIALOG_COMPLETED);			
			
			this.createIfClause(buffer, STR_RECORD_DIALOG_FAILED);
		}
		if(bPlayRecordDlgComplete){
            this.createIfClause(buffer, STR_PLAY_RECORD_DIALOG_COMPLETED);			
			
			this.createIfClause(buffer, STR_PLAY_RECORD_DIALOG_FAILED);
		}
		if(bAuditComplete){
			
            this.createIfClause(buffer, STR_AUDIT_COMPLETED);
						
			this.createIfClause(buffer, STR_AUDIT_FAILED);
		}
		
		buffer.append(LINE_DELIMITER);
		buffer.append(STR_RETURN_CONTINUE);
		buffer.append(LINE_DELIMITER);
		return buffer.toString();
		
	} 
	
	private void createIfClause(StringBuffer buffer, String event)
	{
		int length = buffer.length();
		buffer.append(STR_IF_CLAUSE);
		IdeUtils.replace(buffer,"$EVENT", event, length, true);
		IdeUtils.replace(buffer,STR_DELIM, LINE_DELIMITER, length, true);
	}
	
	protected void createTypeMembers(IType type, ImportsManager imports, IProgressMonitor monitor) throws CoreException 
	{
		
		//Call the super class's implementation....
		IMethod[] methods = super.createInheritedMethods(type, true, true, imports, new SubProgressMonitor(monitor, 1));
		
		//Search for the already created handleEvent(SBB, SBBEvent) method....
		IMethod handleEventMethod = null;
		for(int i=0; i<methods.length;i++)
		{
			if( methods[i].getElementName().equals(HANDLE_EVENT) &&
					methods[i].getNumberOfParameters() == 2 &&
					methods[i].getParameterTypes()[0].equals(Signature.createTypeSignature("SBB", false)) &&
					methods[i].getParameterTypes()[1].equals(Signature.createTypeSignature("SBBEvent", false))){
				handleEventMethod = methods[i];
				break;
			}
		}
		
		//Delete the handleEvent method that is already created ....
		if(handleEventMethod != null){
			handleEventMethod.delete(true, monitor);
		}
		
		//Create a new handleEvent method...
		String handleEventContents = this.getHandleEventContents();
		imports.addImport(SBB_EVENT_LISTENER);
		if(this.bPlayComplete  ||
				this.bPlayCollectComplete || this.bRecordComplete ||
				this.bPlayRecordComplete || this.bStopRecord||this.bPlayDlgComplete  ||
				this.bPlayCollectDlgComplete || this.bRecordDlgComplete ||
				this.bPlayRecordDlgComplete || this.bAuditComplete){
			imports.addImport(MS_SBB);
		}
		super.createMethod(type, imports, monitor, HANDLE_EVENT, PARAM_TYPES, PARAM_NAMES, NULL_PARAMS, INT_TYPE, handleEventContents);
		
		if (monitor != null) 
		{
			monitor.done();
		}	
	}

	private void createB2BControls(Composite parent, int nColumns)
	{
		//Create the label.
		Label label = new Label(parent, SWT.LEFT | SWT.WRAP);
		label.setText("Which B2B EVENT stubs would you like to create?");
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
		
		btnConnected = this.createButton(group, "CONNECT Events", bConnected);
		btnDisconnect = this.createButton(group, "DISCONNECT Events", bdisconnect);
		btnEarlyMedia = this.createButton(group, "Early MEDIA Events", bEarlyM);		
		btnHoldCompleted = this.createButton(group, "HOLD Events", bHoldCompleted);
		btnUnHold = this.createButton(group, "UNHOLD Events", bUnHold);	
		btnResyncCompleted = this.createButton(group, "RESYNC Events", bResyncCompleted);
		btnSigInProgress = this.createButton(group, "SIGNALLING Events", bSigInProgress);
	}
	
	
	private void createMscmlControls(Composite parent, int nColumns)
	{
		//Create the label.
		Label label = new Label(parent, SWT.LEFT | SWT.WRAP);
		label.setText("Which Media Server MSCML EVENT stubs would you like to create?");
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
		
		btnPlayComplete = this.createButton(group, "PLAY Events", bPlayComplete);
		btnPlayCollectComplete = this.createButton(group, "PLAY COLLECT Events", bPlayCollectComplete);
		btnRecordComplete = this.createButton(group, "RECORD Events", bRecordComplete);
		btnPlayRecordComplete = this.createButton(group, "PLAY RECORD Events", bPlayRecordComplete);
		btnStopRecord = this.createButton(group, "STOP RECORD Events", bStopRecord);
	}
	
	private void createMsmlControls(Composite parent, int nColumns)
	{
		//Create the label.
		Label label = new Label(parent, SWT.LEFT | SWT.WRAP);
		label.setText("Which Media Server MSML EVENT stubs would you like to create?");
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
		
		btnPlayDlgComplete = this.createButton(group, "PLAY DIALOG Events", bPlayDlgComplete);
		btnPlayCollectDlgComplete = this.createButton(group, "PLAY COLLECT DIALOG Events", bPlayCollectDlgComplete);
		btnRecordDlgComplete = this.createButton(group, "RECORD DIALOG Events", bRecordDlgComplete);
		btnPlayRecordDlgComplete = this.createButton(group, "PLAY RECORD DIALOG Events", bPlayRecordDlgComplete);
		btnAuditComplete= this.createButton(group, "AUDIT Events", bAuditComplete);
	}


	private Button createButton(Composite parent, String text, boolean select)
	{
		Button btn = new Button(parent, SWT.CHECK | SWT.LEFT);
		btn.setLayoutData(new GridData());
		btn.setSelection(false);
		btn.setText(text);
		btn.setSelection(select);
		btn.addSelectionListener(this.selectionListener);
		return btn;
	}
	
	private SelectionListener selectionListener = new SelectionAdapter()
	{
    		public void widgetSelected(SelectionEvent event) 
		{
    			checkSelection();
	    	}
    	};
    
	private void checkSelection()
	{
    	bConnected = btnConnected.getSelection();
		bHoldCompleted = btnHoldCompleted.getSelection();
		bResyncCompleted = btnResyncCompleted.getSelection();
		bSigInProgress = btnSigInProgress.getSelection();
		bdisconnect=btnDisconnect.getSelection();
		bEarlyM=btnEarlyMedia.getSelection();	
		bUnHold=btnUnHold.getSelection();
		
		
		bPlayComplete = btnPlayComplete.getSelection();
		bPlayCollectComplete = btnPlayCollectComplete.getSelection();
		bRecordComplete = btnRecordComplete.getSelection();
		bPlayRecordComplete = btnPlayRecordComplete.getSelection();
		bStopRecord = btnStopRecord.getSelection();
		
		/*
		 * Disable MSML buttons if any of MSCML is enabled as any one of these will be supported
		 */
		if(bPlayComplete||bPlayCollectComplete||bRecordComplete||bPlayRecordComplete||bStopRecord){
			
			btnPlayDlgComplete.setEnabled(false);
			btnPlayCollectDlgComplete.setEnabled(false);
			btnRecordDlgComplete.setEnabled(false);
			btnPlayRecordDlgComplete.setEnabled(false);
			btnAuditComplete.setEnabled(false);
		}else{
			btnPlayDlgComplete.setEnabled(true);
			btnPlayCollectDlgComplete.setEnabled(true);
			btnRecordDlgComplete.setEnabled(true);
			btnPlayRecordDlgComplete.setEnabled(true);
			btnAuditComplete.setEnabled(true);
		}
		
		
		bPlayDlgComplete = btnPlayDlgComplete.getSelection();
		bPlayCollectDlgComplete = btnPlayCollectDlgComplete.getSelection();
		bRecordDlgComplete = btnRecordDlgComplete.getSelection();
		bPlayRecordDlgComplete = btnPlayRecordDlgComplete.getSelection();
		bAuditComplete = btnAuditComplete.getSelection();
		
		/*
		 * Disable MSCML buttons if any of MSML is enabled as any one of these will be supported
		 */
		if(bPlayDlgComplete||bPlayCollectDlgComplete||bRecordDlgComplete||bPlayRecordDlgComplete||bAuditComplete){
			
			btnPlayComplete.setEnabled(false);
			btnPlayCollectComplete.setEnabled(false);
			btnRecordComplete.setEnabled(false);
			btnPlayRecordComplete.setEnabled(false);
			btnStopRecord.setEnabled(false);
		}else{
			btnPlayComplete.setEnabled(true);
			btnPlayCollectComplete.setEnabled(true);
			btnRecordComplete.setEnabled(true);
			btnPlayRecordComplete.setEnabled(true);
			btnStopRecord.setEnabled(true);
		}
	}
    

	
	private Button btnDisconnect ;
	private boolean bdisconnect=false;
	
	private Button btnEarlyMedia;
	private boolean bEarlyM=false;	
	
	private Button btnUnHold ;
	private boolean bUnHold=false;
		
	private Button btnConnected;
	private boolean bConnected = true;
	
	private Button btnHoldCompleted;
	private boolean bHoldCompleted = false;
	
	private Button btnResyncCompleted;
	private boolean bResyncCompleted = false;
	
	private Button btnSigInProgress;
	private boolean bSigInProgress = false; 
	
	
	//Media SBB MSCML Controls.....
	private Button btnPlayComplete;
	private boolean bPlayComplete = false;
	
    private Button btnPlayCollectComplete;
	private boolean bPlayCollectComplete = false;
	
	private Button btnRecordComplete;
	private boolean bRecordComplete = false;
	
	private Button btnPlayRecordComplete;
	private boolean bPlayRecordComplete = false;
	private Button btnStopRecord;
	
	private boolean bStopRecord = false;
	
	
	//Media SBB MSML Controls.....
	private Button btnPlayDlgComplete;
	private boolean bPlayDlgComplete=false;
	
	private Button btnPlayCollectDlgComplete;
	private boolean bPlayCollectDlgComplete=false;
	
	private Button btnRecordDlgComplete;
	private boolean bRecordDlgComplete=false;
	
	private Button btnPlayRecordDlgComplete;
	private boolean bPlayRecordDlgComplete=false;
		
	private Button btnAuditComplete;
	private boolean bAuditComplete=false;

}
