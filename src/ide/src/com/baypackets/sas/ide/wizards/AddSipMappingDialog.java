//Author@Reeta Aggarwal
package com.baypackets.sas.ide.wizards;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Combo;

public class AddSipMappingDialog extends Dialog {

	private static final String EQUAL = "equal".intern();

	private static final String CONTAINS = "contains".intern();

	private static final String EXISTS = "exists".intern();

	private static final String SUB_DOMAIN_OF = "subdomain-of".intern();

	private static final String EQUAL_IGNORE_CASE = "equalIgnoreCase".intern();

	private static final String CONTAINS_IGNORE_CASE = "containsIgnoreCase"
			.intern();

	private static final String[] VARIABLES = new String[] {
			"request.method".intern(), "request.uri".intern(),
			"request.uri.scheme".intern(), "request.uri.user".intern(),
			"request.uri.host".intern(), "request.uri.port".intern(),
			"request.uri.tel".intern(), "request.from".intern(),
			"request.from.uri".intern(), "request.from.uri.scheme".intern(),
			"request.from.uri.user".intern(), "request.from.uri.host".intern(),
			"request.from.uri.port".intern(),
			"request.from.display-name".intern(), "request.to".intern(),
			"request.to.uri".intern(), "request.to.uri.scheme".intern(),
			"request.to.uri.user".intern(), "request.to.uri.host".intern(),
			"request.to.uri.port".intern(), "request.to.display-name".intern() };

	private static final String[] CONDITIONS = new String[] { EQUAL,
			EQUAL_IGNORE_CASE, CONTAINS, CONTAINS_IGNORE_CASE, EXISTS,
			SUB_DOMAIN_OF };

	public AddSipMappingDialog(Shell shell) {
		super(shell);

	}

	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setText("Init Parameters");
	}

	public Control createDialogArea(Composite com) {

		this.com = com;
		GridLayout lay = new GridLayout();
		lay.numColumns = 2;
		com.setLayout(lay);

		new Label(com, SWT.LEFT | SWT.WRAP).setText("Condition :");
		GridData gri = new GridData(GridData.FILL_HORIZONTAL);
		condition = new Combo(com, SWT.DROP_DOWN | SWT.READ_ONLY);
		condition.setLayoutData(gri);
		for (int i = 0; i < CONDITIONS.length; i++) {
			condition.add(CONDITIONS[i]);
		}
		condition.select(0);

		new Label(com, SWT.LEFT | SWT.WRAP).setText("Variable :");

		GridData g = new GridData(GridData.FILL_HORIZONTAL);
		variable = new Combo(com, SWT.DROP_DOWN | SWT.READ_ONLY);
		variable.setLayoutData(g);
		for (int i = 0; i < VARIABLES.length; i++) {
			variable.add(VARIABLES[i]);
		}
		variable.select(0);

		new Label(com, SWT.LEFT | SWT.WRAP).setText("Value :");

		GridData g1 = new GridData(GridData.FILL_HORIZONTAL);
		value = new Text(com, SWT.SINGLE | SWT.BORDER);
		value.setLayoutData(g1);
		value.setTextLimit(50);

		Composite comp = (Composite) super.createDialogArea(com);
		return comp;

	}

	public void okPressed() {
		cond = condition.getText();
		var = variable.getText();
		val = value.getText();
		com.dispose();
		condition.dispose();
		value.dispose();
		this.close();
	}

	public String getcondition() {
		return cond;
	}

	public String getVariable() {
		return var;
	}

	public String getValue() {
		return val;
	}

	private Combo condition;

	private Combo variable;

	private Text value;

	Composite com;

	String cond = "";

	String var = "";

	String val = "";

}
